package com.zhizulx.tt.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhizulx.tt.DB.sp.SystemConfigSp;
import com.zhizulx.tt.R;
import com.zhizulx.tt.config.SysConstant;
import com.zhizulx.tt.imservice.event.LocationEvent;
import com.zhizulx.tt.imservice.event.TravelEvent;
import com.zhizulx.tt.imservice.manager.IMTravelManager;
import com.zhizulx.tt.imservice.service.IMService;
import com.zhizulx.tt.imservice.support.IMServiceConnector;
import com.zhizulx.tt.ui.base.TTBaseFragment;
import com.zhizulx.tt.utils.MonitorActivityBehavior;
import com.zhizulx.tt.utils.MonitorClickListener;
import com.zhizulx.tt.utils.ToastUtil;
import com.zhizulx.tt.utils.TravelUIHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.greenrobot.event.EventBus;

public class SelectDesignWayFragment extends TTBaseFragment {
    private View curView = null;
    private MonitorActivityBehavior monitorActivityBehavior;
    private LinearLayout introduct;
    private ImageView ivIntroductIcon;
    private LinearLayout custom;
    private IMTravelManager travelManager;
    private Dialog dialog;
    private TextView tvSelectDesignWayIntroduct;
    private static final int NO_WEATHER = 0;
    private static final int SUNNY = 1;
    private static final int OVERCAST = 2;
    private static final int RAINY = 3;
    private static final int SNOWY = 4;

    private static final int NO_EMOTION = 0;
    private static final int HIGH_EMOTION = 1;
    private static final int MEDIUM_EMOTION = 2;
    private static final int LOW_EMOTION = 3;
    private int emotion = NO_EMOTION;

    private IMServiceConnector imServiceConnector = new IMServiceConnector(){
        @Override
        public void onIMServiceConnected() {
            logger.d("config#onIMServiceConnected");
            IMService imService = imServiceConnector.getIMService();
            if (imService != null) {
                travelManager = imService.getTravelManager();
                if (SystemConfigSp.instance().getIntConfig(SystemConfigSp.SysCfgDimension.FIRST) == 1) {
                    EventBus.getDefault().postSticky(new LocationEvent(LocationEvent.Event.GET_WEATHER));
                } else {
                    SystemConfigSp.instance().setIntConfig(SystemConfigSp.SysCfgDimension.FIRST, 1);
                }

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
        EventBus.getDefault().register(this);
        if (null != curView) {
            ((ViewGroup) curView.getParent()).removeView(curView);
            return curView;
        }
        curView = inflater.inflate(R.layout.travel_fragment_select_design_way,
                topContentView);
        emotion = getActivity().getIntent().getIntExtra("emotion", NO_EMOTION);
        initRes();
        return curView;
    }

    private void initRes() {
        // 设置顶部标题栏
        setTopTitle(getString(R.string.select_design_way));
        setTopLeftButton(R.drawable.tt_top_back);
        topLeftContainerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        introduct = (LinearLayout) curView.findViewById(R.id.select_design_way_introduct);
        ivIntroductIcon = (ImageView) curView.findViewById(R.id.select_design_way_introduct_icon);
        switch (emotion) {
            case HIGH_EMOTION:
                ivIntroductIcon.setBackground(getResources().getDrawable(R.drawable.happy_cat));
                break;
            case MEDIUM_EMOTION:
                ivIntroductIcon.setBackground(getResources().getDrawable(R.drawable.bored_cat));
                break;
            case LOW_EMOTION:
                ivIntroductIcon.setBackground(getResources().getDrawable(R.drawable.depressed_cat));
                break;
        }
        custom = (LinearLayout)curView.findViewById(R.id.select_design_way_custom);
        MonitorClickListener designWayListener = new MonitorClickListener(getActivity()) {
            @Override
            public void onMonitorClick(View v) {
                switch (v.getId()) {
                    case R.id.select_design_way_introduct:
                        if (travelManager != null) {
                            travelManager.reqGetRandomRoute(travelManager.GET_ROUTE_BY_TAG);
                            dialog = TravelUIHelper.showCalculateDialog(getActivity());
                            mHandler.postDelayed(runnable, SysConstant.CALCULATE_OVERTIME);
                        }
                        break;
                    case R.id.select_design_way_custom:
                        TravelUIHelper.openCreateTravelActivity(getActivity());
                        break;
                }
            }
        };
        introduct.setOnClickListener(designWayListener);
        custom.setOnClickListener(designWayListener);
        tvSelectDesignWayIntroduct = (TextView)curView.findViewById(R.id.tv_select_design_way_introduct);
        if (SystemConfigSp.instance().getIntConfig(SystemConfigSp.SysCfgDimension.FIRST) == 1) {
            tvSelectDesignWayIntroduct.setText(getRandomEmotionContent(""));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        imServiceConnector.disconnect(getActivity());
        EventBus.getDefault().unregister(this);
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

    @Override
    protected void initHandler() {
    }

    public void onEventMainThread(TravelEvent event){
        switch (event.getEvent()){
            case QUERY_RANDOM_ROUTE_TAG_OK:
                mHandler.removeCallbacks(runnable);
                dialog.dismiss();
                TravelUIHelper.openSelectTravelRouteActivity(getActivity());
                break;
            case QUERY_RANDOM_ROUTE_FAIL:
                Log.e("yuki", "QUERY_RANDOM_ROUTE_FAIL");
                break;
        }
    }

    public void onEvent(LocationEvent event){
        switch (event.getEvent()){
            case SEND_WEATHER:
                Log.e("weather", event.getWeather());
                tvSelectDesignWayIntroduct.setText(getRandomEmotionContent(event.getWeather()));
                break;
        }
    }

    private Handler mHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    };

    private String getRandomEmotionContent(String weather) {
        String[] high = {};
        String[] medium = {};
        String[] low = {};
        String emoStr = "点我试试看";
        List<String> emoList = new ArrayList<>();
        int weatherKey = NO_WEATHER;
        weatherKey = getWeatherKey(weather);

        switch (weatherKey) {
            case NO_WEATHER:
                high = getActivity().getResources().getStringArray(R.array.no_weather_high);
                medium = getActivity().getResources().getStringArray(R.array.no_weather_medium);
                low = getActivity().getResources().getStringArray(R.array.no_weather_low);
                break;
            case SUNNY:
                high = getActivity().getResources().getStringArray(R.array.sunny_high);
                medium = getActivity().getResources().getStringArray(R.array.sunny_medium);
                low = getActivity().getResources().getStringArray(R.array.sunny_low);
                break;
            case OVERCAST:
                high = getActivity().getResources().getStringArray(R.array.overcast_high);
                medium = getActivity().getResources().getStringArray(R.array.overcast_medium);
                low = getActivity().getResources().getStringArray(R.array.overcast_low);
                break;
            case RAINY:
                high = getActivity().getResources().getStringArray(R.array.rainy_high);
                medium = getActivity().getResources().getStringArray(R.array.rainy_medium);
                low = getActivity().getResources().getStringArray(R.array.rainy_low);
                break;
            case SNOWY:
                high = getActivity().getResources().getStringArray(R.array.snowy_high);
                medium = getActivity().getResources().getStringArray(R.array.snowy_medium);
                low = getActivity().getResources().getStringArray(R.array.snowy_low);
                break;
        }

        switch (emotion) {
            case HIGH_EMOTION:
                emoList.addAll(Arrays.asList(high));
                break;
            case MEDIUM_EMOTION:
                emoList.addAll(Arrays.asList(medium));
                break;
            case LOW_EMOTION:
                emoList.addAll(Arrays.asList(low));
                break;
        }

        int max = emoList.size();
        if (max > 0) {
            Random random = new Random();
            int s = random.nextInt(max);
            emoStr = emoList.get(s);
        }
        return emoStr;
    }

    private int getWeatherKey(String weather) {
        int weatherKey = NO_WEATHER;
        if(weather.indexOf("晴")!=-1 || weather.indexOf("多云")!=-1) {
            weatherKey = SUNNY;
        }

        if(weather.indexOf("阴")!=-1 || weather.indexOf("雾")!=-1 || weather.indexOf("霾")!=-1 || weather.indexOf("尘")!=-1 || weather.indexOf("沙")!=-1) {
            weatherKey = OVERCAST;
        }

        if(weather.indexOf("雨")!=-1) {
            weatherKey = RAINY;
        }

        if(weather.indexOf("雪")!=-1) {
            weatherKey = SNOWY;
        }
        return weatherKey;
    }
}
