package com.zhizulx.tt.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.zhizulx.tt.DB.entity.SightEntity;
import com.zhizulx.tt.R;
import com.zhizulx.tt.config.IntentConstant;
import com.zhizulx.tt.imservice.service.IMService;
import com.zhizulx.tt.imservice.support.IMServiceConnector;
import com.zhizulx.tt.ui.base.TTBaseFragment;

/**
 * 设置页面
 */
public class IntroduceHotelFragment extends TTBaseFragment{
	private View curView = null;
    private int sightID;
    private Intent intent;
    private ImageView back;
	private SightEntity sightEntity;
    private RatingBar star;
    private TextView focusNum;
    private WebView webView;

    private IMServiceConnector imServiceConnector = new IMServiceConnector(){
        @Override
        public void onIMServiceConnected() {
            logger.d("config#onIMServiceConnected");
            IMService imService = imServiceConnector.getIMService();
            if (imService != null) {

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
		curView = inflater.inflate(R.layout.travel_fragment_introduce_hotel, topContentView);
        intent = getActivity().getIntent();
        sightID = intent.getIntExtra(IntentConstant.KEY_PEERID, 0);
		sightEntity = new SightEntity();
		sightEntity.setName("鼓浪屿");
		sightEntity.setStar(9);
		initRes();
        initBtn();
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
                getActivity().finish();
			}
		});

        star = (RatingBar)curView.findViewById(R.id.introduce_sight_star);
        focusNum = (TextView)curView.findViewById(R.id.introduce_sight_focus_num);
        star.setRating((float)(sightEntity.getStar())/2);

        webView = (WebView)curView.findViewById(R.id.introduce_sight_web);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        webView.loadUrl("https://www.baidu.com/");
	}

	@Override
	protected void initHandler() {
	}

    private void initBtn() {
        Button baidu = (Button)curView.findViewById(R.id.info1);
        Button sina = (Button)curView.findViewById(R.id.info2);

        View.OnClickListener introduceSightListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.info1:
                        webView.loadUrl("https://www.baidu.com/");
                        break;
                    case R.id.info2:
                        webView.loadUrl("http://www.sina.com.cn/");
                        break;
                }
            }
        };

        baidu.setOnClickListener(introduceSightListener);
        sina.setOnClickListener(introduceSightListener);
    }
}
