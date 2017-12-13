package com.zhizulx.tt.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhizulx.tt.DB.entity.HotelEntity;
import com.zhizulx.tt.R;
import com.zhizulx.tt.imservice.manager.IMTravelManager;
import com.zhizulx.tt.imservice.service.IMService;
import com.zhizulx.tt.imservice.support.IMServiceConnector;
import com.zhizulx.tt.ui.adapter.HotelAdapter;
import com.zhizulx.tt.ui.base.TTBaseFragment;
import com.zhizulx.tt.utils.MonitorActivityBehavior;
import com.zhizulx.tt.utils.TravelUIHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 设置页面
 */
public class SelectHotelFragment extends TTBaseFragment{
	private View curView = null;
    private MonitorActivityBehavior monitorActivityBehavior;
    private IMService imService;
    private IMTravelManager travelManager;
    private RecyclerView rvHotel;
    private HotelAdapter hotelAdapter;
    private int selectID = 0;
    private int day = 0;
    private Intent intent;
    private List<HotelEntity> hotelEntityArrayList = new ArrayList<>();
    private List<Integer> hotelList;

    private IMServiceConnector imServiceConnector = new IMServiceConnector(){
        @Override
        public void onIMServiceConnected() {
            logger.d("config#onIMServiceConnected");
            imService = imServiceConnector.getIMService();
            if (imService != null) {
                travelManager = imService.getTravelManager();
                hotelList = travelManager.getRouteEntity().getDayRouteEntityList().get(day-1).getHotelIDList();
                initHotelList();
            }
        }

        @Override
        public void onServiceDisconnected() {
        }
    };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        intent = getActivity().getIntent();
        day = intent.getIntExtra("day", 1);
		imServiceConnector.connect(this.getActivity());
		if (null != curView) {
			((ViewGroup) curView.getParent()).removeView(curView);
			return curView;
		}
		curView = inflater.inflate(R.layout.travel_fragment_select_hotel, topContentView);

		initRes();
        //testCase();
        initHotel();
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
    }

	@Override
	public void onResume() {
		super.onResume();
        monitorActivityBehavior = new MonitorActivityBehavior(getActivity());
        monitorActivityBehavior.storeBehavior(monitorActivityBehavior.START);
	}

    @Override
    public void onPause() {
        super.onPause();
        monitorActivityBehavior.storeBehavior(monitorActivityBehavior.END);
    }

	/**
	 * @Description 初始化资源
	 */
	private void initRes() {
		// 设置标题栏
		setTopTitle(getString(R.string.select_hotel));
		setTopLeftButton(R.drawable.tt_top_back);
		topLeftContainerLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
                getActivity().finish();
			}
		});

        setTopRightButton(R.drawable.detail_disp_adjust_finish);
        topRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager().getBackStackEntryCount() == 0) {
                    intent.putExtra("result", selectID);
                    getActivity().setResult(100, intent);
                    getActivity().finish();
                    return;
                }
                getFragmentManager().popBackStack();
            }
        });

        rvHotel = (RecyclerView)curView.findViewById(R.id.rv_hotel);
	}

	@Override
	protected void initHandler() {
	}

    private void initHotelList() {
        hotelEntityArrayList.clear();
        for (Integer hotelID : hotelList) {
            HotelEntity hotelEntity = travelManager.getHotelByID(hotelID);
            if (hotelEntity != null) {
                hotelEntityArrayList.add(hotelEntity);
                hotelEntity.setSelect(0);
            }
        }
        selectID = hotelEntityArrayList.get(0).getPeerId();
        hotelEntityArrayList.get(0).setSelect(1);
        hotelAdapter.notifyDataSetChanged();
    }

/*    private void testCase() {
        String pre = UrlConstant.PIC_URL_PREFIX;

        HotelEntity qitian = new HotelEntity();
        qitian.setPeerId(1);
        qitian.setName("7天快捷酒店");
        qitian.setPic(pre+"qitiankuaijiejiudian.png");
        qitian.setStar(9);
        qitian.setUrl("http://m.ctrip.com/webapp/hotel/hoteldetail/890106.html");
        qitian.setTag("经济型");
        qitian.setPrice(654);
        qitian.setDistance(123);
        qitian.setStartTime("12:00");
        qitian.setEndTime("14:00");

        HotelEntity rihang = new HotelEntity();
        rihang.setPeerId(1);
        rihang.setName("厦门日航酒店");
        rihang.setPic(pre+"rihangjiudian.png");
        rihang.setStar(10);
        rihang.setUrl("http://m.ctrip.com/webapp/hotel/hoteldetail/890106.html");
        rihang.setTag("豪华型");
        rihang.setPrice(321);
        rihang.setDistance(456);
        rihang.setStartTime("12:00");
        rihang.setEndTime("14:00");

        hotelEntityArrayList.add(qitian);
        hotelEntityArrayList.add(rihang);
    }*/

    private void initHotel() {
        rvHotel.setHasFixedSize(true);
        LinearLayoutManager layoutManagerResult = new LinearLayoutManager(getActivity());
        layoutManagerResult.setOrientation(LinearLayoutManager.VERTICAL);
        rvHotel.setLayoutManager(layoutManagerResult);
        HotelAdapter.OnRecyclerViewListener hotelRVListener = new HotelAdapter.OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                TravelUIHelper.openIntroduceHotelActivity(getActivity(),
                        hotelEntityArrayList.get(position).getName(),
                        hotelEntityArrayList.get(position).getUrl());
            }

            @Override
            public void onSelectClick(int position, View v) {
                for (HotelEntity hotelEntity : hotelEntityArrayList) {
                    hotelEntity.setSelect(0);
                }
                hotelEntityArrayList.get(position).setSelect(1);
                selectID = hotelEntityArrayList.get(position).getPeerId();
                hotelAdapter.notifyDataSetChanged();
            }
        };
        hotelAdapter = new HotelAdapter(getActivity(), hotelEntityArrayList);
        hotelAdapter.setOnRecyclerViewListener(hotelRVListener);
        rvHotel.setAdapter(hotelAdapter);
    }

    public class ComparatorPrice implements Comparator {
        public int compare(Object arg0, Object arg1) {

            HotelEntity left = (HotelEntity)arg0;
            HotelEntity right = (HotelEntity)arg1;

            //首先比较出现次数，如果相同，则比较名字
            Integer num = left.getPrice();
            Integer num2 = right.getPrice();
            int flag = num.compareTo(num2);
            if(flag == 0){
                return (left.getName()).compareTo(right.getName());
            }else{
                return flag;
            }
        }
    }

}
