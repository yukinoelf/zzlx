package com.zhizulx.tt.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.zhizulx.tt.R;
import com.zhizulx.tt.imservice.manager.IMTravelManager;
import com.zhizulx.tt.imservice.service.IMService;
import com.zhizulx.tt.imservice.support.IMServiceConnector;
import com.zhizulx.tt.ui.base.TTBaseFragment;
import com.zhizulx.tt.utils.MonitorActivityBehavior;
import com.zhizulx.tt.utils.MonitorClickListener;
import com.zhizulx.tt.utils.TravelUIHelper;

import java.util.ArrayList;
import java.util.List;

public class SelectTagFragment extends TTBaseFragment {
    private View curView = null;
    private MonitorActivityBehavior monitorActivityBehavior;
    private IMService imService;
    private IMTravelManager travelManager;
    private ImageView high;
    private ImageView medium;
    private ImageView low;
    private List<String> highTags = new ArrayList<>();
    private List<String> mediumTags = new ArrayList<>();
    private List<String> lowTags = new ArrayList<>();
    private int selectEmotion = 0;

    private IMServiceConnector imServiceConnector = new IMServiceConnector(){
        @Override
        public void onIMServiceConnected() {
            logger.d("config#onIMServiceConnected");
            imService = imServiceConnector.getIMService();
            if (imService != null) {
                travelManager = imService.getTravelManager();
                initTravelInfo();
                trace("010100", "home page in");
            }
        }

        @Override
        public void onServiceDisconnected() {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        imServiceConnector.connect(getActivity());
        if (null != curView) {
            ((ViewGroup) curView.getParent()).removeView(curView);
            return curView;
        }
        curView = inflater.inflate(R.layout.travel_fragment_select_tag,
                topContentView);

        initTags();
        initRes();
        initBtn();
        return curView;
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

    private void initTags() {
        highTags.add(getString(R.string.route_comfort));
        highTags.add(getString(R.string.route_exploration));
        highTags.add(getString(R.string.route_excite));
        mediumTags.add(getString(R.string.route_exploration));
        mediumTags.add(getString(R.string.route_comfort));
        mediumTags.add(getString(R.string.route_encounter));
        lowTags.add(getString(R.string.route_comfort));
        lowTags.add(getString(R.string.route_excite));
        lowTags.add(getString(R.string.route_encounter));
    }

    private void initRes() {
        // 设置顶部标题栏
        hideTopBar();
        high = (ImageView)curView.findViewById(R.id.select_tag_high);
        medium = (ImageView)curView.findViewById(R.id.select_tag_medium);
        low = (ImageView)curView.findViewById(R.id.select_tag_low);
    }

    private void initBtn() {
        MonitorClickListener selectTagListener = new MonitorClickListener(getActivity()) {
            @Override
            public void onMonitorClick(View v) {
                if (travelManager.getdBInitFin() == false) {
                    Toast.makeText(getActivity(), "数据加载中...", Toast.LENGTH_SHORT).show();
                    return;
                }
                final List<String> tags = new ArrayList<>();
                switch (v.getId()) {
                    case R.id.select_tag_high:
                        selectEmotion = 1;
                        tags.addAll(highTags);
                        trace("010301", "select_tag_high");
                        break;
                    case R.id.select_tag_medium:
                        selectEmotion = 2;
                        tags.addAll(mediumTags);
                        trace("010302", "select_tag_medium");
                        break;
                    case R.id.select_tag_low:
                        selectEmotion = 3;
                        tags.addAll(lowTags);
                        trace("010303", "select_tag_low");
                        break;
                }
                travelManager.getConfigEntity().setTags(tags);
                TravelUIHelper.openSelectDesignWayActivity(getActivity(), selectEmotion);
            }
        };
        high.setOnClickListener(selectTagListener);
        medium.setOnClickListener(selectTagListener);
        low.setOnClickListener(selectTagListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        imServiceConnector.disconnect(getActivity());
    }

    @Override
    protected void initHandler() {
    }

    private void initTravelInfo() {
        travelManager.initDatePlace();
    }

    private void trace(String code, String msg) {
        if (travelManager != null) {
            String myMsg = "[SelectTagFragment] " + msg;
            travelManager.AppTrace(code, myMsg);
        }
    }
}
