package com.zhizulx.tt.ui.activity;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.zhizulx.tt.DB.entity.DayRouteEntity;
import com.zhizulx.tt.DB.entity.SightEntity;
import com.zhizulx.tt.DB.sp.SystemConfigSp;
import com.zhizulx.tt.R;
import com.zhizulx.tt.imservice.manager.IMTravelManager;
import com.zhizulx.tt.imservice.service.IMService;
import com.zhizulx.tt.imservice.support.IMServiceConnector;
import com.zhizulx.tt.utils.AMapUtil;
import com.zhizulx.tt.utils.ImageUtil;
import com.zhizulx.tt.utils.TravelUIHelper;

import java.util.ArrayList;
import java.util.List;

public class ShowSightsInMap extends FragmentActivity{
    private IMService imService;
    private RelativeLayout topBar;
    private AMap aMap;
    private MapView mapView;
    private int day = 0;
    private ImageView back;
    private TextView title;
    private IMTravelManager travelManager;
    private Dialog dialog;
    private List<SightEntity> sightEntityList = new ArrayList<>();
    //private List<Integer> mapDrawable = new ArrayList<>();
	private IMServiceConnector imServiceConnector = new IMServiceConnector(){
        @Override
        public void onIMServiceConnected() {
            imService = imServiceConnector.getIMService();
            if (imService == null) {
                return;
            }
            travelManager = imService.getTravelManager();
            initSightList();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initMap();
                }
            }, 500);
        }

        @Override
        public void onServiceDisconnected() {
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        day = getIntent().getIntExtra("day", 0);
		// 在这个地方加可能会有问题吧
		imServiceConnector.connect(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.travel_activity_show_sights_in_map);
        topBar = (RelativeLayout) findViewById(R.id.show_sight_in_map_top_bar);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) topBar.getLayoutParams();
        int topHeight = SystemConfigSp.instance().getIntConfig(SystemConfigSp.SysCfgDimension.TOP_BAR_HEIGHT);
        lp.setMargins(0, topHeight, 0, 0);
        mapView = (MapView) findViewById(R.id.show_sights_in_map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        back = (ImageView)findViewById(R.id.show_sight_in_map_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title = (TextView)findViewById(R.id.show_sights_in_map_title);
        title.setText("第"+(day+1)+"天的景点");
/*        mapDrawable.add(R.drawable.poi_marker_1);
        mapDrawable.add(R.drawable.poi_marker_2);
        mapDrawable.add(R.drawable.poi_marker_3);
        mapDrawable.add(R.drawable.poi_marker_4);
        mapDrawable.add(R.drawable.poi_marker_5);
        mapDrawable.add(R.drawable.poi_marker_6);
        mapDrawable.add(R.drawable.poi_marker_7);
        mapDrawable.add(R.drawable.poi_marker_8);
        mapDrawable.add(R.drawable.poi_marker_9);*/
        dialog = TravelUIHelper.showLoadingDialog(ShowSightsInMap.this);
        mHandler.postDelayed(runnable, 1500);
	}

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

	@Override
	protected void onDestroy() {
        mapView.onDestroy();
		imServiceConnector.disconnect(this);
        super.onDestroy();
	}

    private void setSightPoint(SightEntity sightEntity) {
        if (sightEntity == null) {
            Log.e("yuki", "ShowSightsInMap sightEntity null");
            return;
        }

        LatLonPoint latLonPoint = new LatLonPoint(sightEntity.getLatitude(), sightEntity.getLongitude());
        String sightName = String.valueOf(sightEntityList.indexOf(sightEntity)+1) + ") " + sightEntity.getName();
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(latLonPoint))
                .icon(BitmapDescriptorFactory.fromBitmap(getSightTitleBitmap(sightName))));
                //.icon(BitmapDescriptorFactory.fromResource(mapDrawable.get(sightEntityList.indexOf(sightEntity)))));
    }

    private void initSightList() {
        List<Integer> sightIdList = new ArrayList<>();
/*        for (DayRouteEntity dayRouteEntity : travelManager.getRouteEntity().getDayRouteEntityList()) {
            sightIdList.addAll(dayRouteEntity.getSightIDList());
        }
        for (int sightId : sightIdList) {
            SightEntity sightEntity = travelManager.getSightByID(sightId);
            if (sightEntity != null) {
                sightEntityList.add(sightEntity);
            }
        }*/
        sightIdList.addAll(travelManager.getRouteEntity().getDayRouteEntityList().get(day).getSightIDList());
        for (int sightId : sightIdList) {
            SightEntity sightEntity = travelManager.getSightByID(sightId);
            if (sightEntity != null) {
                sightEntityList.add(sightEntity);
            }
        }
    }

    private void initMap() {
        if (sightEntityList.size() > 0) {
/*                SightEntity sightEntity = sightEntityList.get(0);
                LatLng latLng = new LatLng(sightEntity.getLatitude(), sightEntity.getLongitude());
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));*/
            for (SightEntity sightEntityIndex : sightEntityList) {
                setSightPoint(sightEntityIndex);
            }

            List<LatLng> points = new ArrayList<LatLng>();
            for (SightEntity sightEntity : sightEntityList) {
                LatLng latLonPoint = new LatLng(sightEntity.getLatitude(), sightEntity.getLongitude());
                points.add(latLonPoint);
            }
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng p : points) {
                builder = builder.include(p);
            }
            LatLngBounds latlngBounds = builder.build();
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latlngBounds, 100));
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

    private Bitmap getSightTitleBitmap(String sightName) {
        View view = View.inflate(ShowSightsInMap.this,R.layout.travel_item_sight_in_map, null);
        TextView name = (TextView) view.findViewById(R.id.tv_sight_name);
        name.setText(sightName);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }
}
