package com.zhizulx.tt.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhizulx.tt.DB.Serializable.MapRoute;
import com.zhizulx.tt.DB.entity.CollectRouteEntity;
import com.zhizulx.tt.DB.entity.DayRouteEntity;
import com.zhizulx.tt.DB.entity.DetailDispEntity;
import com.zhizulx.tt.DB.entity.HotelEntity;
import com.zhizulx.tt.DB.entity.RouteEntity;
import com.zhizulx.tt.DB.entity.SightEntity;
import com.zhizulx.tt.R;
import com.zhizulx.tt.imservice.event.LocationEvent;
import com.zhizulx.tt.imservice.event.TravelEvent;
import com.zhizulx.tt.imservice.manager.IMTravelManager;
import com.zhizulx.tt.imservice.service.IMService;
import com.zhizulx.tt.imservice.support.IMServiceConnector;
import com.zhizulx.tt.protobuf.IMBuddy;
import com.zhizulx.tt.ui.activity.CollectActivity;
import com.zhizulx.tt.ui.activity.SelectHotelActivity;
import com.zhizulx.tt.ui.activity.SelectSightActivity;
import com.zhizulx.tt.ui.adapter.DetailDispAdapter;
import com.zhizulx.tt.ui.adapter.DetailDispMenuAdapter;
import com.zhizulx.tt.ui.base.TTBaseFragment;
import com.zhizulx.tt.utils.MonitorActivityBehavior;
import com.zhizulx.tt.utils.MonitorClickListener;
import com.zhizulx.tt.utils.TravelUIHelper;
import com.zhizulx.tt.utils.WheelPicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * 设置页面
 */
public class DetailDispFragment extends TTBaseFragment{
	private View curView = null;
    private MonitorActivityBehavior monitorActivityBehavior;
    private IMTravelManager travelManager;
    private RecyclerView rvDetailDisp;
    private DetailDispAdapter detailDispAdapter;
    private List<DetailDispEntity> detailDispEntityList = new ArrayList<>();
    private RecyclerView rvMenu;
    private ImageView goHome;
    private DetailDispMenuAdapter detailDispMenuAdapter;
    private TextView detailDispMenuDisp;
    private LinearLayout lyDetailDispMenu;
    private List<Integer> day = new ArrayList<>();
    private boolean move = false;
    private LinearLayoutManager layoutManagerResult;
    private int mIndex = 0;
    private ImageView routeCollection;
    private TextView routeCost;
    private TextView routeStyle;
    private static final int NULL = 0;
    private static final int DAY = 1;
    private static final int SIGHT = 2;
    private static final int HOTEL = 3;
    private static final int TRAFFIC = 4;
    private LinearLayout lyTimeSelectWheel;
    private WheelPicker timeWheel;
    private ImageView selectTimeCancel;
    private ImageView selectTimeConfirm;
    private DetailDispEntity timeSelect;
    private int hotelEditPos = 0;
    private DetailDispEntity start;
    private DetailDispEntity end;
    private DetailDispEntity map;
    private String trafficTitle;
    private String trafficUrl;
    private String startCity;
    private String endCity;
    private Dialog dialog;
    private Dialog dialogUpdate;
    private LocationEvent locationEvent = new LocationEvent(LocationEvent.Event.FRESH_EVENT);
    private boolean locationStatus = false;
    private boolean sighthotelStatus = false;
    private static final int UPDATE_OPT_TIME_CHANGE = 1;
    private static final int UPDATE_OPT_SIGHT_CHANGE = 2;
    private List<Integer> changeSightIDList = new ArrayList<>();

    private IMServiceConnector imServiceConnector = new IMServiceConnector(){
        @Override
        public void onIMServiceConnected() {
            logger.d("config#onIMServiceConnected");
            IMService imService = imServiceConnector.getIMService();
            if (imService != null) {
                travelManager = imService.getTravelManager();
                startCity = travelManager.getConfigEntity().getStartCity();
                endCity = travelManager.getConfigEntity().getEndCity();
                travelManager.reqSightHotel(travelManager.getRouteEntity().getCityCode());
                travelManager.initalRoute();
                EventBus.getDefault().postSticky(new LocationEvent(LocationEvent.Event.GET_EVENT));
                dialog = TravelUIHelper.showLoadingDialog(getActivity());
                trace("050100", "detail disp in");
            }
        }

        @Override
        public void onServiceDisconnected() {
        }
    };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        if(!EventBus.getDefault().isRegistered(DetailDispFragment.this)){
            EventBus.getDefault().register(DetailDispFragment.this);
        }
		imServiceConnector.connect(this.getActivity());
		if (null != curView) {
			((ViewGroup) curView.getParent()).removeView(curView);
			return curView;
		}
		curView = inflater.inflate(R.layout.travel_fragment_detail_disp, topContentView);
		initRes();
        initBtn();
        initDetailDisp();
		return curView;
	}

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        imServiceConnector.disconnect(getActivity());
        EventBus.getDefault().unregister(DetailDispFragment.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        monitorActivityBehavior = new MonitorActivityBehavior(getActivity());
        monitorActivityBehavior.storeBehavior(monitorActivityBehavior.START);
        freshRoute();
    }

    @Override
    public void onPause() {
        super.onPause();
        monitorActivityBehavior.storeBehavior(monitorActivityBehavior.END);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Activity.RESULT_FIRST_USER){
            switch (resultCode) {
                case 100:
                    int hotelID = data.getIntExtra("result", 0);
                    int day = data.getIntExtra("day", 1);
                    HotelEntity hotelEntity = travelManager.getHotelByID(hotelID);
                    DetailDispEntity detailDispEntity = detailDispEntityList.get(hotelEditPos);
                    detailDispEntity.setTitle(hotelEntity.getName());
                    detailDispEntity.setImage(hotelEntity.getPic());
                    resetDayHotel(day, hotelID);
                    routeCost.setText("约"+getTotalCost()+"元");
                    detailDispAdapter.notifyDataSetChanged();
                    break;
                case 101:
                    if(data.getIntExtra("collectStatus", 0) == 1) {
                        routeCollection.setBackgroundResource(R.drawable.collected);
                        routeCollection.setClickable(false);
                        String startDate = data.getStringExtra("startDate");
                        String startTrafficNo = data.getStringExtra("startTrafficNo");
                        String endTrafficNo = data.getStringExtra("endTrafficNo");
                        storeCollectRoute(startDate, startTrafficNo, endTrafficNo);
                        travelManager.reqCreateCollectRoute();
                        hideTopRightButton();
                        Toast toast = Toast.makeText(getActivity(), "收藏成功，\"我的收藏\"中查看！", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                    }
                    break;
                case 102:
                    if(data.getBooleanExtra("result", false)) {
                        changeSightIDList.clear();
                        changeSightIDList.addAll(data.getIntegerArrayListExtra("sightID"));
                        updateRoute(UPDATE_OPT_SIGHT_CHANGE);
                    }
                    break;
            }
        }
    }

    private void resetDayHotel(int day, int hotelID) {
        List<Integer> hotelIDList = travelManager.getRouteEntity().getDayRouteEntityList().get(day-1).getHotelIDList();
        hotelIDList.remove(hotelIDList.indexOf(hotelID));
        hotelIDList.add(0, hotelID);
    }

	/**
	 * @Description 初始化资源
	 */
	private void initRes() {
		// 设置标题栏
        setTopTitle(getString(R.string.detail_disp));
        setTopLeftButton(R.drawable.tt_top_back);
        topLeftContainerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getActivity().finish();
                return;
            }
        });
        setTopRightButton(R.drawable.detail_disp_adjust);
        topRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adjustSight();
            }
        });
        rvDetailDisp = (RecyclerView)curView.findViewById(R.id.rv_detail_disp);
        rvMenu = (RecyclerView)curView.findViewById(R.id.rv_detail_disp_menu);
        goHome = (ImageView)curView.findViewById(R.id.detail_disp_go_home);
        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TravelUIHelper.openHomePageActivity(getActivity());
                getActivity().finish();
            }
        });
        detailDispMenuDisp = (TextView)curView.findViewById(R.id.detail_disp_menu);
        lyDetailDispMenu = (LinearLayout)curView.findViewById(R.id.ly_detail_disp_menu);

        lyTimeSelectWheel = (LinearLayout)curView.findViewById(R.id.ly_time_select_wheel);
        timeWheel = (WheelPicker)curView.findViewById(R.id.time_select_wheel);
        selectTimeCancel = (ImageView)curView.findViewById(R.id.select_time_cancel);
        selectTimeConfirm = (ImageView)curView.findViewById(R.id.select_time_confirm);

        routeCollection = (ImageView)curView.findViewById(R.id.route_collection);
        routeCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent collect = new Intent(getActivity(), CollectActivity.class);
                startActivityForResult(collect, Activity.RESULT_FIRST_USER);
            }
        });
        routeCost = (TextView)curView.findViewById(R.id.detail_disp_price);
        routeStyle = (TextView)curView.findViewById(R.id.detail_disp_route_style);
	}

	@Override
	protected void initHandler() {
	}

    private void initBtn() {
        detailDispMenuDisp.setOnClickListener(new MonitorClickListener(getActivity()) {
            @Override
            public void onMonitorClick(View v) {
                if (lyDetailDispMenu.getVisibility() == View.GONE) {
                    lyDetailDispMenu.setVisibility(View.VISIBLE);
                } else {
                    lyDetailDispMenu.setVisibility(View.GONE);
                }
            }
        });

        MonitorClickListener selectTimeListener = new MonitorClickListener(getActivity()) {
            @Override
            public void onMonitorClick(View v) {
                switch (v.getId()) {
                    case R.id.select_time_confirm:
                        timeSelect.setTime(timeWheel.getTimeData().replace("时", "") + ":00");
                        int hour = Integer.valueOf(timeWheel.getTimeData().replace("时", ""));
                        if (timeSelect.getStatus() == 0) {
                            travelManager.getRouteEntity().setStartTime(hour);
                        } else {
                            travelManager.getRouteEntity().setEndTime(hour);
                        }
                        detailDispAdapter.notifyDataSetChanged();
                        updateRoute(UPDATE_OPT_TIME_CHANGE);
                        break;
                }
                lyTimeSelectWheel.setVisibility(View.GONE);
            }
        };
        selectTimeCancel.setOnClickListener(selectTimeListener);
        selectTimeConfirm.setOnClickListener(selectTimeListener);
    }

    private void initDetailDisp() {
        rvDetailDisp.setHasFixedSize(true);
        layoutManagerResult = new LinearLayoutManager(getActivity());
        layoutManagerResult.setOrientation(LinearLayoutManager.VERTICAL);
        rvDetailDisp.setLayoutManager(layoutManagerResult);

        DetailDispAdapter.OnRecyclerViewListener detailDispRVListener = new DetailDispAdapter.OnRecyclerViewListener() {
            @Override
            public void onDayClick(View v, int position) {
                int day = getDay(detailDispEntityList.get(position).getTitle());
                TravelUIHelper.openShowSightsInMapActivity(getActivity(), day);
            }

            @Override
            public void onSightClick(View v, int position) {
                switch (v.getId()) {
                    case R.id.sight_avatar:
                    case R.id.detail_disp_sight_info:
                        TravelUIHelper.openIntroduceSightActivity(getActivity(), detailDispEntityList.get(position).getDbID());
                        break;
                    case R.id.detail_disp_sight_map:
                        map = detailDispEntityList.get(position);
                        if (isSameCity()) {
                            TravelUIHelper.openMapRouteActivity(getActivity(), getMapRoute(map));
                        } else {
                            Toast.makeText(getActivity(), "不支持跨城路线", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }

            @Override
            public void onHotelClick(View v, int position) {
                switch (v.getId()) {
                    case R.id.hotel_avatar:
                        int dbID = detailDispEntityList.get(position).getDbID();
                        HotelEntity hotelEntity = travelManager.getHotelByID(dbID);
                        TravelUIHelper.openIntroduceHotelActivity(getActivity(), hotelEntity.getName(), hotelEntity.getUrl());
                        break;
                    case R.id.detail_disp_hotel_info:
                        hotelEditPos = position;
                        Intent hotelIntent = new Intent(getActivity(), SelectHotelActivity.class);
                        hotelIntent.putExtra("day", getDayByHotel());
                        startActivityForResult(hotelIntent, Activity.RESULT_FIRST_USER);
                        //TravelUIHelper.openIntroduceHotelActivity(getActivity(), "test", "https://m.ctrip.com/html5/flight/swift/domestic/BJS/SHA/2017-04-01");
                        //TravelUIHelper.openIntroduceHotelActivity(getActivity(), "test", "http://m.ctrip.com/webapp/train/v2/index?from=http%3A%2F%2Fm.ctrip.com%2Fhtml5%2F#!/list");
                        break;
                    case R.id.detail_disp_hotel_map:
                        map = detailDispEntityList.get(position);
                        if (isSameCity()) {
                            TravelUIHelper.openMapRouteActivity(getActivity(), getMapRoute(map));
                        } else {
                            Toast.makeText(getActivity(), "不支持跨城路线", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }

            @Override
            public void onTrafficClick(View v, final int position) {
                switch (v.getId()) {
                    case R.id.detail_disp_traffic_select_result:
                        getTrafficContent(detailDispEntityList.get(position).getStatus(), detailDispEntityList.get(position).getTitle());
                        TravelUIHelper.openTrafficListActivity(getActivity(), trafficTitle, trafficUrl);
                        break;
                    case R.id.detail_disp_traffic_plane:
                        detailDispEntityList.get(position).setTitle("飞机");
                        detailDispAdapter.notifyDataSetChanged();
                        break;
                    case R.id.detail_disp_traffic_train:
                        detailDispEntityList.get(position).setTitle("火车");
                        detailDispAdapter.notifyDataSetChanged();
                        break;
                    case R.id.detail_disp_traffic_time:
                        TravelUIHelper.showAlertDialog(getActivity(), "调整起止时间会导致路线重新规划，是否确认调整？", new TravelUIHelper.dialogCallback() {
                            @Override
                            public void callback() {
                                lyTimeSelectWheel.setVisibility(View.VISIBLE);
                                timeSelect = detailDispEntityList.get(position);
                                Date date=null;
                                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                                try {
                                    date=formatter.parse(timeSelect.getTime());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                timeWheel.setTimeData(date.getHours());
                            }
                        });
                        break;
                }
            }
        };

        detailDispAdapter = new DetailDispAdapter(getActivity(), detailDispEntityList);
        detailDispAdapter.setOnRecyclerViewListener(detailDispRVListener);
        rvDetailDisp.setAdapter(detailDispAdapter);
        rvDetailDisp.addOnScrollListener(new RecyclerViewListener());

        rvMenu.setHasFixedSize(true);
        LinearLayoutManager layoutManagerMenu = new LinearLayoutManager(getActivity());
        layoutManagerResult.setOrientation(LinearLayoutManager.VERTICAL);
        rvMenu.setLayoutManager(layoutManagerMenu);
        detailDispMenuAdapter = new DetailDispMenuAdapter(getActivity(), rvDayInit());
        DetailDispMenuAdapter.OnRecyclerViewListener dayListener = new DetailDispMenuAdapter.OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                roll2Day(day.get(position));
            }
        };
        detailDispMenuAdapter.setOnRecyclerViewListener(dayListener);
        rvMenu.setAdapter(detailDispMenuAdapter);
    }

    private int getDayByHotel() {
        int i = 0;
        int day = 0;
        for (DetailDispEntity detailDispEntity : detailDispEntityList) {
            if (detailDispEntity.getType() == DAY && i < hotelEditPos) {
                day ++;
            }
            i ++;
        }
        return day;
    }

    private int getDay(String strDay) {
        int day = 1;
        if (strDay != null) {
            day = Integer.parseInt(strDay);
        }
        day --;
        return day;
    }

    private List<Integer> rvDayInit() {
        day.clear();
        for (DetailDispEntity index : detailDispEntityList) {
            if (index.getType() == DAY) {
                day.add(Integer.valueOf(index.getTitle()));
            }
        }
        return day;
    }

    private void roll2Day(int targetDay) {
        int pos = 0;
        for (DetailDispEntity index : detailDispEntityList) {
            if (index.getType() == DAY && index.getTitle().equals(String.valueOf(targetDay))) {
                mIndex = pos;
                moveToPosition(pos);
                return;
            }
            pos ++;
        }
    }

    private void smoothMoveToPosition(int n) {
        int firstItem = layoutManagerResult.findFirstVisibleItemPosition();
        int lastItem = layoutManagerResult.findLastVisibleItemPosition();
        if (n <= firstItem ){
            rvDetailDisp.smoothScrollToPosition(n);
        }else if ( n <= lastItem ){
            int top = rvDetailDisp.getChildAt(n - firstItem).getTop();
            rvDetailDisp.smoothScrollBy(0, top);
        }else{
            rvDetailDisp.smoothScrollToPosition(n);
            move = true;
        }
    }

    private void moveToPosition(int n) {
        int firstItem = layoutManagerResult.findFirstVisibleItemPosition();
        int lastItem = layoutManagerResult.findLastVisibleItemPosition();
        if (n <= firstItem ){
            rvDetailDisp.scrollToPosition(n);
        }else if ( n <= lastItem ){
            int top = rvDetailDisp.getChildAt(n - firstItem).getTop();
            rvDetailDisp.scrollBy(0, top);
        }else{
            rvDetailDisp.scrollToPosition(n);
            move = true;
        }
    }

    class RecyclerViewListener extends RecyclerView.OnScrollListener{
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (move && newState == RecyclerView.SCROLL_STATE_IDLE){
                move = false;
                int n = mIndex - layoutManagerResult.findFirstVisibleItemPosition();
                if ( 0 <= n && n < rvDetailDisp.getChildCount()){
                    int top = rvDetailDisp.getChildAt(n).getTop();
                    rvDetailDisp.smoothScrollBy(0, top);
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (move){
                move = false;
                int n = mIndex - layoutManagerResult.findFirstVisibleItemPosition();
                if ( 0 <= n && n < rvDetailDisp.getChildCount()){
                    int top = rvDetailDisp.getChildAt(n).getTop();
                    rvDetailDisp.scrollBy(0, top);
                }
            }
        }
    }

    private void adjustSight() {
        Intent intent = new Intent(getActivity(), SelectSightActivity.class);
        startActivityForResult(intent, Activity.RESULT_FIRST_USER);
    }

    private void showRoute() {
        detailDispEntityList.clear();
        RouteEntity routeEntity = travelManager.getRouteEntity();
        if (travelManager.hasCollected(routeEntity.getDbId())) {
            routeCollection.setBackgroundResource(R.drawable.collected);
            routeCollection.setClickable(false);
            hideTopRightButton();
        }
        routeCost.setText("约"+String.valueOf(getTotalCost())+"元");
        String routeType = routeEntity.getRouteType();
        routeStyle.setText(routeType+"路线");
        start = new DetailDispEntity();
        start.setType(TRAFFIC);
        start.setTitle(getTrafficType(routeEntity.getStartTrafficTool()));
        start.setTime(String.format("%02d:00", routeEntity.getStartTime()));
        detailDispEntityList.add(start);

        int day = 0;
        for (DayRouteEntity dayRouteEntity : routeEntity.getDayRouteEntityList()) {
            day ++;
            detailDispEntityList.addAll(getOneDayRoute(day, dayRouteEntity));
        }

        end = new DetailDispEntity();
        end.setType(TRAFFIC);
        end.setTitle(getTrafficType(routeEntity.getEndTrafficTool()));
        end.setTime(String.format("%02d:00", routeEntity.getEndTime()));
        detailDispEntityList.add(end);

        DetailDispEntity blank = new DetailDispEntity();
        blank.setType(NULL);
        detailDispEntityList.add(blank);

        detailDispAdapter.notifyDataSetChanged();
    }

    private String getTrafficType(int trafficTool) {
        String trafficType = getString(R.string.plane);
        switch (trafficTool) {
            case IMBuddy.TransportToolType.AIRPLANE_VALUE:
                trafficType = getString(R.string.plane);
                break;
            case IMBuddy.TransportToolType.TAXI_VALUE:
                trafficType = getString(R.string.train);
                break;
        }
        return trafficType;
    }

    private List<DetailDispEntity> getOneDayRoute(int day, DayRouteEntity dayRouteEntity) {
        List<DetailDispEntity> detailDispEntityList = new ArrayList<>();
        if (travelManager.getdBInitFin() == false) {
            return detailDispEntityList;
        }

        DetailDispEntity detailDispEntity;

        detailDispEntity = new DetailDispEntity();
        detailDispEntity.setType(DAY);
        detailDispEntity.setTitle(String.valueOf(day));
        detailDispEntityList.add(detailDispEntity);

        for (int i : dayRouteEntity.getSightIDList()) {
            SightEntity sightEntity = travelManager.getSightByID(i);
            if (sightEntity == null) {
                Log.e("getOneDayRoute", "not find sightEntity " + i);
                continue;
            }
            sightEntity.setSelect(1);
            detailDispEntity = new DetailDispEntity();
            detailDispEntity.setDbID(sightEntity.getPeerId());
            detailDispEntity.setType(SIGHT);
            detailDispEntity.setTitle(sightEntity.getName());
            detailDispEntity.setImage(sightEntity.getPic());
            detailDispEntityList.add(detailDispEntity);
        }

        HotelEntity hotelEntity = travelManager.getHotelByID(dayRouteEntity.getHotelIDList().get(0));
        if (hotelEntity == null) {
            Log.e("getOneDayRoute", "not find hotelEntity");
            return detailDispEntityList;
        }
        hotelEntity.setSelect(1);
        detailDispEntity = new DetailDispEntity();
        detailDispEntity.setDbID(hotelEntity.getPeerId());
        detailDispEntity.setType(HOTEL);
        detailDispEntity.setTitle(hotelEntity.getName());
        detailDispEntity.setImage(hotelEntity.getPic());
        detailDispEntityList.add(detailDispEntity);

        return detailDispEntityList;
    }

    private void getTrafficContent(int id, String trafficType) {
        // https://m.flight.qunar.com/ncs/page/flightlist?depCity=重庆&arrCity=成都&goDate=2017-04-11&sort=&airLine=&from=
        // http://touch.train.qunar.com/trainList.html?startStation=杭州&endStation=西安&date=2017-04-11&searchType=stasta&bd_source=&filterTrainType=&filterTrainType=&filterTrainType=

        String planeFormat = "https://m.flight.qunar.com/ncs/page/flightlist?depCity=%s&arrCity=%s&goDate=%s&sort=&airLine=&from=";
        String trainFormat = "http://touch.train.qunar.com/trainList.html?startStation=%s&endStation=%s&date=%s&searchType=stasta&bd_source=&filterTrainType=&filterTrainType=&filterTrainType=";
        RouteEntity routeEntity = travelManager.getRouteEntity();
        String destination = travelManager.getCityNameByCode(routeEntity.getCityCode());
        String startCity = travelManager.getConfigEntity().getStartCity();
        String endCity = travelManager.getConfigEntity().getEndCity();
        java.text.SimpleDateFormat date2string = new SimpleDateFormat( "yyyy-MM-dd");
        String startDate = date2string.format(travelManager.getConfigEntity().getStartDate());
        String endDate = date2string.format(travelManager.getConfigEntity().getEndDate());
        if (id == start.getDbID()) {
            trafficTitle = startCity + "-" + destination;
            if (trafficType.equals(getString(R.string.plane))) {
                trafficUrl = String.format(planeFormat, startCity, destination, startDate);
            } else {
                trafficUrl = String.format(trainFormat, startCity, destination, startDate);
            }
        } else {
            trafficTitle = destination + "-" + endCity;
            if (trafficType.equals(getString(R.string.plane))) {
                trafficUrl = String.format(planeFormat, destination, endCity, endDate);
            } else {
                trafficUrl = String.format(trainFormat, destination, endCity, endDate);
            }
        }
    }

    private void storeCollectRoute(String startDate, String startTrafficNo, String endTrafficNo) {
        CollectRouteEntity collectRouteEntity = travelManager.getCollectRouteEntity();
        RouteEntity routeEntity = travelManager.getRouteEntity();
        collectRouteEntity.setDbId(0);
        collectRouteEntity.setStartDate(startDate);
        Date date = null;
        try {
            date = (new SimpleDateFormat("MM-dd")).parse(startDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, routeEntity.getDay());
        collectRouteEntity.setEndDate((new SimpleDateFormat("MM-dd")).format(cal.getTime()));
        collectRouteEntity.setStartTrafficNo(startTrafficNo);
        collectRouteEntity.setEndTrafficNo(endTrafficNo);
        collectRouteEntity.setRouteEntity(routeEntity);
    }

    private MapRoute getMapRoute(DetailDispEntity detailDispEntity) {
        MapRoute mapRoute = new MapRoute();
        if (detailDispEntity.getType() == HOTEL) {
            HotelEntity hotelEntity = travelManager.getHotelByID(detailDispEntity.getDbID());
            mapRoute.setEndLongitude(hotelEntity.getLongitude());
            mapRoute.setEndLatitude(hotelEntity.getLatitude());
        }

        if (detailDispEntity.getType() == SIGHT) {
            SightEntity sightEntity = travelManager.getSightByID(detailDispEntity.getDbID());
            mapRoute.setEndLongitude(sightEntity.getLongitude());
            mapRoute.setEndLatitude(sightEntity.getLatitude());
        }
        mapRoute.setCity(locationEvent.getCityName());
        mapRoute.setStartLongitude(locationEvent.getLongitude());
        mapRoute.setStartLatitude(locationEvent.getLatitude());
        return mapRoute;
    }

    public void onEvent(LocationEvent event){
        switch (event.getEvent()){
            case FRESH_EVENT:
                Log.e("yuki", "FRESH_EVENT");
                locationEvent.setCityName(event.getCityName());
                locationEvent.setLongitude(event.getLongitude());
                locationEvent.setLatitude(event.getLatitude());
                locationStatus = true;
                processStatus();
                break;
        }
    }

    public void onEvent(TravelEvent event) {
        switch (event.getEvent()){
            case QUERY_SIGHT_HOTEL_OK:
                Log.e("yuki", "QUERY_SIGHT_HOTEL_OK");
                sighthotelStatus = true;
                processStatus();
                break;
            case QUERY_SIGHT_HOTEL_FAIL:
                Log.e("yuki", "QUERY_SIGHT_HOTEL_FAIL");
                break;
        }
    }

    private Boolean isSameCity() {
        String des = travelManager.getCityNameByCode(travelManager.getRouteEntity().getCityCode());
        String loc = locationEvent.getCityName().replace("市", "");
        Log.e("yuki", loc + "  " +des);
        return loc.equals(des);
    }

    public void onEventMainThread(TravelEvent event){
        switch (event.getEvent()){
            case CREATE_COLLECT_ROUTE_OK:
                Toast.makeText(getActivity(), "收藏成功", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TravelUIHelper.openHomePageActivity(getActivity());
                        getActivity().finish();
                    }
                },1000);
                break;
            case CREATE_COLLECT_ROUTE_FAIL:
                Log.e("yuki", "CREATE_COLLECT_ROUTE_FAIL");
                break;
            case UPDATE_RANDOM_ROUTE_OK:
                mHandler.removeCallbacks(runnable);
                dialogUpdate.dismiss();
                freshRoute();
                break;
            case UPDATE_RANDOM_ROUTE_FAIL:
                Log.e("yuki", "UPDATE_RANDOM_ROUTE_FAIL");
                break;
        }
    }

    private Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            processStatus();
        }
    };

    private Handler mUpdateHandler = new Handler();
    private Runnable runnableUpdate = new Runnable() {
        @Override
        public void run() {
            if (dialogUpdate != null) {
                dialogUpdate.dismiss();
            }
        }
    };

    private void updateRoute(int opt) {
        List<Integer> sightIdList = new ArrayList<>();
        if (UPDATE_OPT_TIME_CHANGE == opt) {
            RouteEntity routeEntity = travelManager.getRouteEntity();
            for (DayRouteEntity dayRouteEntity : routeEntity.getDayRouteEntityList()) {
                sightIdList.addAll(dayRouteEntity.getSightIDList());
            }
        }

        if (UPDATE_OPT_SIGHT_CHANGE == opt) {
            sightIdList = changeSightIDList;
        }

        travelManager.reqUpdateRandomRoute(sightIdList);
        if (dialogUpdate == null || !dialogUpdate.isShowing()) {
            dialogUpdate = TravelUIHelper.showCalculateDialog(getActivity());
        }
        mUpdateHandler.removeCallbacks(runnableUpdate);
        mUpdateHandler.postDelayed(runnableUpdate, 10000);
    }

    private void freshRoute() {
        if (travelManager != null) {
            travelManager.initalRoute();
            showRoute();
            rvDayInit();
        }
    }

    private int getTotalCost() {
        List<Integer> sightIdList = new ArrayList<>();
        List<Integer> hotelIdList = new ArrayList<>();
        int cost = 0;
        RouteEntity routeEntity = travelManager.getRouteEntity();
        for (DayRouteEntity dayRouteEntity : routeEntity.getDayRouteEntityList()) {
            sightIdList.addAll(dayRouteEntity.getSightIDList());
            hotelIdList.add(dayRouteEntity.getHotelIDList().get(0));
        }
        for (int sight:sightIdList) {
            if (travelManager.getSightByID(sight) == null) {
                Log.e("yuki", "sightid"+sight);
                continue;
            }
            cost += travelManager.getSightByID(sight).getPrice();
        }

        for (int hotel:hotelIdList) {
            cost += travelManager.getHotelByID(hotel).getPrice();
        }
        return cost;
    }

    private void processStatus() {
        if (locationStatus && sighthotelStatus) {
            if (dialog != null) {
                dialog.dismiss();
                showRoute();
                rvDayInit();
            }
        }
    }

    private void trace(String code, String msg) {
        if (travelManager != null) {
            String myMsg = "[SelectTagFragment] " + msg;
            travelManager.AppTrace(code, myMsg);
        }
    }
}
