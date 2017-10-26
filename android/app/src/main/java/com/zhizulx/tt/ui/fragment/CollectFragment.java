package com.zhizulx.tt.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhizulx.tt.R;
import com.zhizulx.tt.imservice.manager.IMTravelManager;
import com.zhizulx.tt.imservice.service.IMService;
import com.zhizulx.tt.imservice.support.IMServiceConnector;
import com.zhizulx.tt.ui.activity.SelectStartDateActivity;
import com.zhizulx.tt.ui.base.TTBaseFragment;

public class CollectFragment extends TTBaseFragment {
    private View curView = null;
    private IMTravelManager travelManager;
    private RelativeLayout rlCollectStartTime;
    private EditText startNo;
    private EditText endNo;
    private String startDate;
    private TextView collectStartTimeHint;
    private Button collect;

    private IMServiceConnector imServiceConnector = new IMServiceConnector(){
        @Override
        public void onIMServiceConnected() {
            logger.d("config#onIMServiceConnected");
            IMService imService = imServiceConnector.getIMService();
            if (imService != null) {
                travelManager = imService.getTravelManager();
                trace("050317", "collection in");
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
        if (null != curView) {
            ((ViewGroup) curView.getParent()).removeView(curView);
            return curView;
        }
        curView = inflater.inflate(R.layout.tt_fragment_collect,
                topContentView);

        initRes();
        initBtn();
        return curView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Activity.RESULT_FIRST_USER){
            switch (resultCode) {
                case 102:
                    startDate = data.getStringExtra("startDate");
                    if (startDate != null) {
                        collectStartTimeHint.setText(startDate);
                    }
                    break;
            }
        }
    }

    private void initRes() {
        // 设置顶部标题栏
        setTopTitle(getActivity().getString(R.string.collect_condition));
        setTopLeftButton(R.drawable.tt_top_back);
        topLeftContainerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getActivity().finish();
            }
        });
        rlCollectStartTime = (RelativeLayout)curView.findViewById(R.id.collect_start_time);
        startNo = (EditText)curView.findViewById(R.id.start_no);
        endNo = (EditText)curView.findViewById(R.id.end_no);
        collectStartTimeHint = (TextView)curView.findViewById(R.id.collect_start_time_hint);
        collect = (Button)curView.findViewById(R.id.collect);
    }

    private void initBtn() {
        View.OnClickListener collectListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.collect_start_time:
                        jump2DateSelect();
                        break;
                    case R.id.collect:
                        finishCollect();
                        break;
                }
            }
        };
        rlCollectStartTime.setOnClickListener(collectListener);
        collect.setOnClickListener(collectListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        imServiceConnector.disconnect(getActivity());
    }

    @Override
    protected void initHandler() {
    }

    private void jump2DateSelect() {
        Intent dateSelect = new Intent(getActivity(), SelectStartDateActivity.class);
        startActivityForResult(dateSelect, Activity.RESULT_FIRST_USER);
    }

    private void finishCollect() {
        if (startDate == null || startDate.isEmpty()) {
            Toast.makeText(getActivity(), "请输入出发日期", Toast.LENGTH_SHORT).show();
            return;
        }
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            getActivity().getIntent().putExtra("collectStatus", 1);
            getActivity().getIntent().putExtra("startDate", startDate);
            if (startNo.getText().toString().isEmpty()) {
                getActivity().getIntent().putExtra("startTrafficNo", "");
            } else {
                getActivity().getIntent().putExtra("startTrafficNo", startNo.getText().toString());
            }

            if (endNo.getText().toString().isEmpty()) {
                getActivity().getIntent().putExtra("endTrafficNo", "");
            } else {
                getActivity().getIntent().putExtra("endTrafficNo", endNo.getText().toString());
            }

            getActivity().setResult(101, getActivity().getIntent());
            getActivity().finish();
            return;
        }
    }

    private void trace(String code, String msg) {
        if (travelManager != null) {
            String myMsg = "[SelectTagFragment] " + msg;
            travelManager.AppTrace(code, myMsg);
        }
    }
}
