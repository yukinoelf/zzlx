package com.zhizulx.tt.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zhizulx.tt.DB.entity.SightEntity;
import com.zhizulx.tt.R;
import com.zhizulx.tt.config.IntentConstant;
import com.zhizulx.tt.imservice.service.IMService;
import com.zhizulx.tt.imservice.support.IMServiceConnector;
import com.zhizulx.tt.ui.base.TTBaseFragment;
import com.zhizulx.tt.ui.widget.adver.CircleFlowIndicator;
import com.zhizulx.tt.ui.widget.adver.ImagePagerAdapter;
import com.zhizulx.tt.ui.widget.adver.ViewFlow;
import com.zhizulx.tt.utils.AMapUtil;
import com.zhizulx.tt.utils.MapContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 设置页面
 */
public class IntroduceSightFragment extends TTBaseFragment{
	private View curView = null;
    private int sightID;
    private Intent intent;
    private ImageView back;
	private SightEntity sightEntity;
    private TextView name;
    private RatingBar star;
    private TextView introduction;
    private TextView introductionDetail;
    private TextView openTime;
    private TextView playTime;
    private TextView free;
    private TextView address;
    private AMap aMap;
    private MapView mapView;
    private ScrollView scrollView;
    private MapContainer mapContainer;

    private ViewFlow avatar;
    private CircleFlowIndicator introduceSightIndic;

    private IMServiceConnector imServiceConnector = new IMServiceConnector(){
        @Override
        public void onIMServiceConnected() {
            logger.d("config#onIMServiceConnected");
            IMService imService = imServiceConnector.getIMService();
            if (imService != null) {
                sightEntity = imService.getTravelManager().getSightByID(sightID);
                dispSight();
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
        sightID = intent.getIntExtra(IntentConstant.KEY_PEERID, 0);
		imServiceConnector.connect(this.getActivity());
		if (null != curView) {
			((ViewGroup) curView.getParent()).removeView(curView);
			return curView;
		}
		curView = inflater.inflate(R.layout.travel_fragment_introduce_sight, topContentView);

        mapView = (MapView) curView.findViewById(R.id.introduce_sight_map);
        scrollView = (ScrollView) curView.findViewById(R.id.introduce_sight_scrollview);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        mapContainer = (MapContainer) curView.findViewById(R.id.map_container);
        mapContainer.setScrollView(scrollView);

		initRes();
		return curView;
	}

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        imServiceConnector.disconnect(getActivity());
    }

	@Override
	public void onResume() {
		super.onResume();
        mapView.onResume();
	}

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

	/**
	 * @Description 初始化资源
	 */
	private void initRes() {
		// 设置标题栏
		hideTopBar();
        back = (ImageView)curView.findViewById(R.id.introduce_sight_back);
        back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
                getActivity().finish();
			}
		});
        name = (TextView)curView.findViewById(R.id.introduce_sight_name);
        avatar = (ViewFlow)curView.findViewById(R.id.introduce_sight_bk);
        introduceSightIndic = (CircleFlowIndicator)curView.findViewById(R.id.introduce_sight_indic);
        introduction = (TextView)curView.findViewById(R.id.introduce_sight_introduction);
        introductionDetail = (TextView)curView.findViewById(R.id.introduce_sight_introduction_detail);
        openTime = (TextView)curView.findViewById(R.id.introduce_sight_open);
        playTime = (TextView)curView.findViewById(R.id.introduce_sight_play_time);
        free = (TextView)curView.findViewById(R.id.introduce_sight_price);
        address = (TextView)curView.findViewById(R.id.introduce_sight_address);
        star = (RatingBar)curView.findViewById(R.id.introduce_sight_star);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
	}

	@Override
	protected void initHandler() {
	}

    private void dispSight() {
        name.setText(sightEntity.getName());
        //Glide.with(getActivity()).load(sightEntity.getPic()).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(avatar);
        String pic = sightEntity.getPic();
        List<String> picUrlList = Arrays.asList(pic.split(","));
        initBanner(picUrlList);
        introduceProcess(sightEntity.getIntroduction());
        introductionDetail.setText(sightEntity.getIntroductionDetail());
        openTime.setText(sightEntity.getOpenTime());
        playTime.setText(sightEntity.getPlayTime()+"小时");
        if (sightEntity.getPrice() == 0) {
            free.setText("否");
        } else {
            free.setText("是");
        }
        address.setText(sightEntity.getAddress());
        star.setRating((float)(sightEntity.getStar()));
        LatLng latLng = new LatLng(sightEntity.getLatitude(), sightEntity.getLongitude());
        LatLonPoint latLonPoint = new LatLonPoint(sightEntity.getLatitude(), sightEntity.getLongitude());
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(latLonPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.position_icon)));
    }

    private void introduceProcess(String sightIntroduction) {
        String s = sightIntroduction + "详细介绍";
        Spannable spannable = Spannable.Factory.getInstance().newSpannable(s);
        spannable.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (introductionDetail.getVisibility() == View.GONE) {
                    introductionDetail.setVisibility(View.VISIBLE);
                } else {
                    introductionDetail.setVisibility(View.GONE);
                }
            }
        }, s.length() - 4, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        introduction.setText(spannable);
        introduction.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initBanner(List<String> imageUrlList) {
        avatar.setAdapter(new ImagePagerAdapter(getActivity(), imageUrlList).setInfiniteLoop(true));
        avatar.setmSideBuffer(imageUrlList.size()); // 实际图片张数，
        // 我的ImageAdapter实际图片张数为3

        avatar.setFlowIndicator(introduceSightIndic);
        avatar.setTimeSpan(3000);
        avatar.setSelection(imageUrlList.size() * 1000); // 设置初始位置
        if (imageUrlList.size() > 1) {
            avatar.startAutoFlowTimer(); // 启动自动播放
        }
    }
}
