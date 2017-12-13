package com.zhizulx.tt.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.zhizulx.tt.DB.entity.DayRouteEntity;
import com.zhizulx.tt.DB.entity.SightEntity;
import com.zhizulx.tt.R;
import com.zhizulx.tt.imservice.manager.IMTravelManager;
import com.zhizulx.tt.imservice.service.IMService;
import com.zhizulx.tt.imservice.support.IMServiceConnector;
import com.zhizulx.tt.ui.adapter.SightAdapter;
import com.zhizulx.tt.ui.base.TTBaseFragment;
import com.zhizulx.tt.utils.MonitorActivityBehavior;
import com.zhizulx.tt.utils.MonitorClickListener;
import com.zhizulx.tt.utils.TravelUIHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 设置页面
 */
public class SelectSightFragment extends TTBaseFragment{
	private View curView = null;
    private MonitorActivityBehavior monitorActivityBehavior;
    private Intent intent;
    private IMTravelManager travelManager;
    private TextView total;
    private TextView literature;
    private TextView comfort;
    private TextView exploration;
    private TextView excite;
    private TextView encounter;
    private RecyclerView rvSight;
    private SightAdapter sightAdapter;
    private List<SightEntity> sightEntityList = new ArrayList<>();
    private List<SightEntity> tagSightEntityList = new ArrayList<>();
    String Tag = "全部";
    private PopupWindow mPopupWindow;
    private LinearLayout pop;
    private TextView notScreen;
    private TextView free;
    private LinearLayout lyPop;
    static final int ALL = 0;
    static final int FREE = 1;
    private int spinner_select = ALL;
    private TextView selectSightDropText;
    private List<Integer> origin = new ArrayList<>();

    private Map<Integer, String> selectFlag = new HashMap<>();
    private int totalHours = 1;
    private int day = 1;
    private List<DayRouteEntity> dayRouteEntityList = new ArrayList<>();

    private IMServiceConnector imServiceConnector = new IMServiceConnector(){
        @Override
        public void onIMServiceConnected() {
            logger.d("config#onIMServiceConnected");
            IMService imService = imServiceConnector.getIMService();
            if (imService != null) {
                travelManager = imService.getTravelManager();
                initSightList();
                totalHours = getTotalHours();
                day = travelManager.getRouteEntity().getDay();
                copyRoute(travelManager.getRouteEntity().getDayRouteEntityList(), dayRouteEntityList);
            }
        }

        @Override
        public void onServiceDisconnected() {
        }
    };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		imServiceConnector.connect(this.getActivity());
        intent = getActivity().getIntent();
		if (null != curView) {
			((ViewGroup) curView.getParent()).removeView(curView);
			return curView;
		}
		curView = inflater.inflate(R.layout.travel_fragment_select_sight, topContentView);

		initRes();
        initBtn();
        //testCase();
        initPopupWindow();
        initSight();
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
		setTopTitle(getString(R.string.select_sight));
		setTopLeftButton(R.drawable.tt_top_back);
		topLeftContainerLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
                //copyRoute(dayRouteEntityList, travelManager.getRouteEntity().getDayRouteEntityList());
                if (getFragmentManager().getBackStackEntryCount() == 0) {
                    intent.putExtra("result", false);
                    getActivity().setResult(102, intent);
                    getActivity().finish();
                    return;
                }
                getActivity().finish();
			}
		});
        setTopRightButton(R.drawable.detail_disp_adjust_finish);
        topRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int thread = day < 2 ? 2 : day;
                if (getSightNum() < thread) {
                    Toast.makeText(getActivity(), "真懒，多玩几个地方嘛！", Toast.LENGTH_SHORT).show();
                } else {
                    if (getFragmentManager().getBackStackEntryCount() == 0) {
                        if (checkUpdateRoute()) {
                            intent.putExtra("result", true);
                            intent.putIntegerArrayListExtra("sightID", getChangeSightIDList());
                        } else {
                            intent.putExtra("result", false);
                        }
                        getActivity().setResult(102, intent);
                        getActivity().finish();
                        return;
                    }
                }
            }
        });

        total = (TextView)curView.findViewById(R.id.select_total);
        literature = (TextView)curView.findViewById(R.id.select_literature);
        comfort = (TextView)curView.findViewById(R.id.select_comfort);
        exploration = (TextView)curView.findViewById(R.id.select_exploration);
        excite = (TextView)curView.findViewById(R.id.select_excite);
        encounter = (TextView)curView.findViewById(R.id.select_encounter);
        selectFlag.put(R.id.select_total, "全部");
        selectFlag.put(R.id.select_literature, "文艺");
        selectFlag.put(R.id.select_comfort, "舒适");
        selectFlag.put(R.id.select_exploration, "探险");
        selectFlag.put(R.id.select_excite, "刺激");
        selectFlag.put(R.id.select_encounter, "邂逅");

        pop = (LinearLayout)curView.findViewById(R.id.select_sight_drop);
        pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.showAsDropDown(curView.findViewById(R.id.select_sight_drop));
            }
        });

        rvSight = (RecyclerView)curView.findViewById(R.id.rv_sight);
        selectSightDropText = (TextView)curView.findViewById(R.id.select_sight_drop_text);
    }

	@Override
	protected void initHandler() {
	}

    private void initBtn() {
        MonitorClickListener listener = new MonitorClickListener(getActivity()) {
            @Override
            public void onMonitorClick(View v) {
                int id = v.getId();
                Tag = selectFlag.get(id);
                tagProcess();
                freeProcess();
                buttonDisp(id);
                sightAdapter.notifyDataSetChanged();
            }
        };
        total.setOnClickListener(listener);
        literature.setOnClickListener(listener);
        comfort.setOnClickListener(listener);
        exploration.setOnClickListener(listener);
        excite.setOnClickListener(listener);
        encounter.setOnClickListener(listener);
    }

    private void buttonDisp(int id) {
        total.setBackground(getResources().getDrawable(R.drawable.select_sight_tag_not_click));
        total.setTextColor(getResources().getColor(R.color.not_clicked));
        literature.setBackground(getResources().getDrawable(R.drawable.select_sight_tag_not_click));
        literature.setTextColor(getResources().getColor(R.color.not_clicked));
        comfort.setBackground(getResources().getDrawable(R.drawable.select_sight_tag_not_click));
        comfort.setTextColor(getResources().getColor(R.color.not_clicked));
        exploration.setBackground(getResources().getDrawable(R.drawable.select_sight_tag_not_click));
        exploration.setTextColor(getResources().getColor(R.color.not_clicked));
        excite.setBackground(getResources().getDrawable(R.drawable.select_sight_tag_not_click));
        excite.setTextColor(getResources().getColor(R.color.not_clicked));
        encounter.setBackground(getResources().getDrawable(R.drawable.select_sight_tag_not_click));
        encounter.setTextColor(getResources().getColor(R.color.not_clicked));

        switch (id) {
            case R.id.select_total:
                total.setBackground(getResources().getDrawable(R.drawable.select_sight_tag_click));
                total.setTextColor(getResources().getColor(R.color.clicked));
                break;
            case R.id.select_literature:
                literature.setBackground(getResources().getDrawable(R.drawable.select_sight_tag_click));
                literature.setTextColor(getResources().getColor(R.color.clicked));
                break;
            case R.id.select_comfort:
                comfort.setBackground(getResources().getDrawable(R.drawable.select_sight_tag_click));
                comfort.setTextColor(getResources().getColor(R.color.clicked));
                break;
            case R.id.select_exploration:
                exploration.setBackground(getResources().getDrawable(R.drawable.select_sight_tag_click));
                exploration.setTextColor(getResources().getColor(R.color.clicked));
                break;
            case R.id.select_excite:
                excite.setBackground(getResources().getDrawable(R.drawable.select_sight_tag_click));
                excite.setTextColor(getResources().getColor(R.color.clicked));
                break;
            case R.id.select_encounter:
                encounter.setBackground(getResources().getDrawable(R.drawable.select_sight_tag_click));
                encounter.setTextColor(getResources().getColor(R.color.clicked));
                break;
        }
    }

    private void initSightList() {
        List<SightEntity> sightEntityList = new ArrayList<>();
        String cityCode = travelManager.getRouteEntity().getCityCode();
        for (SightEntity sightEntity : travelManager.getSightList()) {
            if (sightEntity.getCityCode().equals(cityCode)) {
                sightEntityList.add(sightEntity);
                if (sightEntity.getSelect() == 1) {
                    origin.add(sightEntity.getPeerId());
                }
            }
        }
        sightSort(sightEntityList);
        tagSightEntityList.clear();
        tagSightEntityList.addAll(this.sightEntityList);
        sightAdapter.notifyDataSetChanged();
    }

    private void sightSort(List<SightEntity> sightEntityListIn) {
        sightEntityList.clear();
        SightIDSort sort = new SightIDSort();
        Collections.sort(sightEntityListIn, sort);
        //all route sights
        List<Integer> sightSelectIDList = new ArrayList<>();
        for (DayRouteEntity dayRouteEntity : travelManager.getRouteEntity().getDayRouteEntityList()) {
            sightSelectIDList.addAll(dayRouteEntity.getSightIDList());
        }

        for (int i : sightSelectIDList) {
            for (SightEntity sightEntity : sightEntityListIn) {
                if (sightEntity.getPeerId() == i) {
                    sightEntityList.add(sightEntity);
                    continue;
                }
            }
        }

        for (SightEntity sightEntity : sightEntityListIn) {
            if (sightEntityList.contains(sightEntity)) {
                continue;
            }
            sightEntityList.add(sightEntity);
        }
    }

    public class SightIDSort implements Comparator {
        @Override
        public int compare(Object arg0, Object arg1) {
            // TODO Auto-generated method stub
            SightEntity route0 = (SightEntity) arg0;
            SightEntity route1 = (SightEntity) arg1;
            int id0 = route0.getPeerId();
            int id1 = route1.getPeerId();
            return id0 > id1 ? 1 : -1; //按照时间的由小到大排列
        }
    }

    private void initSight() {
        rvSight.setHasFixedSize(true);
        LinearLayoutManager layoutManagerResult = new LinearLayoutManager(getActivity());
        layoutManagerResult.setOrientation(LinearLayoutManager.VERTICAL);
        rvSight.setLayoutManager(layoutManagerResult);
        SightAdapter.OnRecyclerViewListener sightRVListener = new SightAdapter.OnRecyclerViewListener() {
            @Override
            public void onItemClick(int position) {
                TravelUIHelper.openIntroduceSightActivity(getActivity(), tagSightEntityList.get(position).getPeerId());
            }

            @Override
            public void onSelectClick(int position, View v) {
                SightEntity sightEntity = tagSightEntityList.get(position);
                if (sightEntity.getSelect() == 1) {
                    sightEntity.setSelect(0);
                } else {
                    int hours = sightEntity.getPlayTime() + getCurrentHours();
                    if (hours > totalHours) {
                        Toast.makeText(getActivity(), "景点太多可是玩不完的呢！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (calTravelFullRate() < 50) {
                        Toast.makeText(getActivity(), "时间还有很多，不要浪费嘛！", Toast.LENGTH_SHORT).show();
                    } else if (calTravelFullRate() < 80) {
                        Toast.makeText(getActivity(), "诶呦，不错喔！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "这条路线可能会有点累的呢！", Toast.LENGTH_SHORT).show();
                    }
                    sightEntity.setSelect(1);
                }
                sightAdapter.notifyDataSetChanged();
            }
        };
        sightAdapter = new SightAdapter(getActivity(), tagSightEntityList);
        sightAdapter.setOnRecyclerViewListener(sightRVListener);
        rvSight.setAdapter(sightAdapter);
    }

    private void freeProcess() {
        if (spinner_select == ALL) {
            selectSightDropText.setText(getString(R.string.select_sight_recommend));
            return;
        }

        if (spinner_select == FREE) {
            selectSightDropText.setText(getString(R.string.select_sight_free));
            Iterator<SightEntity> iSightEntity = tagSightEntityList.iterator();
            while (iSightEntity.hasNext()) {
                if (iSightEntity.next().getPrice() != 0) {
                    iSightEntity.remove();
                }
            }
        }
    }

    private void tagProcess() {
        tagSightEntityList.clear();
        if (Tag.equals("全部")) {
            tagSightEntityList.addAll(sightEntityList);
        } else {
            for (SightEntity sightEntity : sightEntityList) {
                if (sightEntity.getTag().contains(Tag)) {
                    tagSightEntityList.add(sightEntity);
                }
            }
        }
    }

    private void initPopupWindow() {
        View popupView = curView.inflate(getActivity(), R.layout.select_sight_popup_window, null);
        notScreen = (TextView) popupView.findViewById(R.id.select_sight_pop_not_screen);
        free = (TextView) popupView.findViewById(R.id.select_sight_pop_free);
        lyPop = (LinearLayout) popupView.findViewById(R.id.ly_select_sight_pop);
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        //mPopupWindow.setAnimationStyle(R.style.anim_menu_bottombar);

        mPopupWindow.getContentView().setFocusableInTouchMode(true);
        mPopupWindow.getContentView().setFocusable(true);
        mPopupWindow.getContentView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (mPopupWindow != null && mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                    }
                    return true;
                }
                return false;
            }
        });

        MonitorClickListener popupListener = new MonitorClickListener(getActivity()) {
            @Override
            public void onMonitorClick(View v) {
                switch (v.getId()) {
                    case R.id.select_sight_pop_not_screen:
                        spinner_select = 0;
                        tagProcess();
                        freeProcess();
                        sightAdapter.notifyDataSetChanged();
                        mPopupWindow.dismiss();
                        break;

                    case R.id.select_sight_pop_free:
                        spinner_select = 1;
                        tagProcess();
                        freeProcess();
                        sightAdapter.notifyDataSetChanged();
                        mPopupWindow.dismiss();
                        break;

                    case R.id.ly_select_sight_pop:
                        mPopupWindow.dismiss();
                        break;
                }
            }
        };
        lyPop.setOnClickListener(popupListener);
        notScreen.setOnClickListener(popupListener);
        free.setOnClickListener(popupListener);
    }

    private Boolean checkUpdateRoute() {
        List<Integer> newSightIDList = new ArrayList<>();
        for (SightEntity sightEntity : sightEntityList) {
            if (sightEntity.getSelect() == 1) {
                newSightIDList.add(sightEntity.getPeerId());
            }
        }
        if (newSightIDList.equals(origin)) {
            getActivity().finish();
            return false;
        }
        return true;
    }

    private int getTotalHours() {
        int day = travelManager.getRouteEntity().getDay();
        int defaultStartHour = 9;
        int defaultEndHour = 18;
        int startHour = travelManager.getRouteEntity().getStartTime() < defaultStartHour ? defaultStartHour : travelManager.getRouteEntity().getStartTime();
        int endHour = travelManager.getRouteEntity().getEndTime() > defaultEndHour ? defaultEndHour : travelManager.getRouteEntity().getEndTime();
        int totalHours = 1;

        if (day < 2) {
            totalHours = endHour - startHour;
        } else {
            int dayFirst = defaultEndHour - startHour;
            int dayLast = endHour - defaultStartHour;
            int middle = (day - 2) * (defaultEndHour - defaultStartHour);
            totalHours = dayFirst + dayLast + middle;
        }
        return totalHours;
    }

    private int getCurrentHours() {
        int playHours = 0;
        for (SightEntity sightEntity : sightEntityList) {
            if (sightEntity.getSelect() == 1) {
                playHours += sightEntity.getPlayTime();
            }
        }
        return playHours;
    }

    private int calTravelFullRate() {
        int currentHours = getCurrentHours();
        if (currentHours > totalHours) {
            return 100;
        }
        return currentHours*100/totalHours;
    }

    private int getSightNum() {
        int num = 0;
        for (SightEntity sightEntity : sightEntityList) {
            if (sightEntity.getSelect() == 1) {
                num ++;
            }
        }
        return num;
    }

    private void copyRoute(List<DayRouteEntity> ori, List<DayRouteEntity> des) {
        for (DayRouteEntity dayRouteEntity : ori) {
            DayRouteEntity newDayRouteEntity = new DayRouteEntity();
            List<Integer> sightIDList = new ArrayList<>();
            List<Integer> hotelIDList = new ArrayList<>();
            sightIDList.addAll(dayRouteEntity.getSightIDList());
            hotelIDList.addAll(dayRouteEntity.getHotelIDList());
            newDayRouteEntity.setSightIDList(sightIDList);
            newDayRouteEntity.setHotelIDList(hotelIDList);
            des.add(newDayRouteEntity);
        }
    }

    private ArrayList<Integer> getChangeSightIDList() {
        ArrayList<Integer> sightEntityList = new ArrayList<>();
        for (SightEntity sightEntity : tagSightEntityList) {
            if (sightEntity.getSelect() == 0) {
                continue;
            }
            sightEntityList.add(sightEntity.getPeerId());
        }
        return sightEntityList;
    }
}
