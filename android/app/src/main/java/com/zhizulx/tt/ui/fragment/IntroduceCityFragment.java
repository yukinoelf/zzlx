package com.zhizulx.tt.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhizulx.tt.DB.entity.CityEntity;
import com.zhizulx.tt.R;
import com.zhizulx.tt.imservice.manager.IMTravelManager;
import com.zhizulx.tt.imservice.service.IMService;
import com.zhizulx.tt.imservice.support.IMServiceConnector;
import com.zhizulx.tt.ui.base.TTBaseFragment;
import com.zhizulx.tt.ui.widget.adver.CircleFlowIndicator;
import com.zhizulx.tt.ui.widget.adver.ImagePagerAdapter;
import com.zhizulx.tt.ui.widget.adver.ViewFlow;

import java.util.ArrayList;

/**
 * 设置页面
 */
public class IntroduceCityFragment extends TTBaseFragment{
	private View curView = null;
    private String cityCode;
    private Intent intent;
    private ImageView back;
    private Boolean selectFlag;

    private ViewFlow mViewFlow;
    private CircleFlowIndicator mFlowIndicator;
    private ArrayList<String> imageUrlList = new ArrayList<>();

    private TextView citySelect;
    private IMTravelManager travelManager;

    private TextView title;
    private TextView discription;

    private IMServiceConnector imServiceConnector = new IMServiceConnector(){
        @Override
        public void onIMServiceConnected() {
            logger.d("config#onIMServiceConnected");
            IMService imService = imServiceConnector.getIMService();
            if (imService != null) {
                travelManager = imService.getTravelManager();
                CityEntity cityEntity = travelManager.getCityEntitybyCityCode(cityCode);
                imageUrlList.addAll(cityEntity.getPicList());
                initBanner(imageUrlList);
                title.setText("关于"+cityEntity.getName());
                discription.setText(cityEntity.getDiscription());
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
		curView = inflater.inflate(R.layout.travel_fragment_introduce_city, topContentView);
        intent = getActivity().getIntent();
        cityCode = intent.getStringExtra("cityCode");
        selectFlag = intent.getBooleanExtra("selectFlag", false);
		initRes();
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
	}

	/**
	 * @Description 初始化资源
	 */
	private void initRes() {
		// 设置标题栏
		hideTopBar();
        back = (ImageView)curView.findViewById(R.id.introduce_city_back);
        back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
                backProcess();
			}
		});

        mViewFlow = (ViewFlow) curView.findViewById(R.id.viewflow);
        mFlowIndicator = (CircleFlowIndicator) curView.findViewById(R.id.viewflowindic);

        citySelect = (TextView) curView.findViewById(R.id.bn_city_select);
        dispCitySelect();
        citySelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFlag = selectFlag ? false : true;
                backProcess();
            }
        });

        title = (TextView) curView.findViewById(R.id.introduce_city_title);
        discription = (TextView) curView.findViewById(R.id.introduce_city_discription);
	}

	@Override
	protected void initHandler() {
	}

    private void backProcess() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            intent.putExtra("selectFlag", selectFlag);
            getActivity().setResult(100, intent);
            getActivity().finish();
            return;
        }
        getFragmentManager().popBackStack();
    }

    private void initBanner(ArrayList<String> imageUrlList) {
        mViewFlow.setAdapter(new ImagePagerAdapter(getActivity(), imageUrlList).setInfiniteLoop(true));
        mViewFlow.setmSideBuffer(imageUrlList.size()); // 实际图片张数，
        // 我的ImageAdapter实际图片张数为3

        mViewFlow.setFlowIndicator(mFlowIndicator);
        mViewFlow.setTimeSpan(3000);
        mViewFlow.setSelection(imageUrlList.size() * 1000); // 设置初始位置
        if (imageUrlList.size() > 1) {
            mViewFlow.startAutoFlowTimer(); // 启动自动播放
        }
    }

    private void dispCitySelect() {
        if (selectFlag) {
            citySelect.setVisibility(View.GONE);
        } else {
            citySelect.setVisibility(View.VISIBLE);
        }
    }

}
