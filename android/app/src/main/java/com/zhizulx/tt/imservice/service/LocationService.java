package com.zhizulx.tt.imservice.service;

//import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearch.OnWeatherSearchListener;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.zhizulx.tt.DB.sp.SystemConfigSp;
import com.zhizulx.tt.R;
import com.zhizulx.tt.config.SysConstant;
import com.zhizulx.tt.imservice.event.LocationEvent;
import com.zhizulx.tt.utils.Logger;
import com.zhizulx.tt.utils.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.greenrobot.event.EventBus;


/**
 * Created by yuki on 2017/4/1.
 */

public class LocationService  extends Service implements AMapLocationListener, OnWeatherSearchListener {
    private Logger logger = Logger.getLogger(LocationService.class);
    private AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private double dLongitude = 0.0;
    private double dLatitude = 0.0;
    private String cityName;
    private LocationEvent locationEvent = new LocationEvent(LocationEvent.Event.FRESH_EVENT);

    private LocalWeatherLive weatherlive;
    private WeatherSearchQuery mquery;
    private WeatherSearch mweathersearch;
    private String weather = "晴";
    private LocationEvent weatherEvent = new LocationEvent(LocationEvent.Event.SEND_WEATHER);

    /**binder*/
    private LocationService.LocationServiceBinder binder = new LocationService.LocationServiceBinder();

    public class LocationServiceBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        logger.i("IMService onBind");
        return binder;
    }

    @Override
    public void onCreate() {
        logger.i("IMService onCreate");
        super.onCreate();
        EventBus.getDefault().register(this, SysConstant.SERVICE_EVENTBUS_PRIORITY);
        // make the service foreground, so stop "360 yi jian qingli"(a clean
        // tool) to stop our app
        // todo eric study wechat's mechanism, use a better solution
        //startForeground((int) System.currentTimeMillis(), new Notification());
        initLocation();
    }

    @Override
    public void onDestroy() {
        logger.i("IMService onDestroy");
        // todo 在onCreate中使用startForeground
        // 在这个地方是否执行 stopForeground呐
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    // 负责初始化 每个manager
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logger.i("IMService onStartCommand");
        //应用开启初始化 下面这几个怎么释放 todo
        return START_STICKY;
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        logger.d("imservice#onTaskRemoved");
        // super.onTaskRemoved(rootIntent);
        this.stopSelf();
    }

    private void initLocation() {
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        /*//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);*/

        //获取一次定位结果：
        // 该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        // 设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocation(true);

        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        // 启动定位
        mlocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                SystemConfigSp.instance().setStrConfig(SystemConfigSp.SysCfgDimension.LOCAL_CITY, amapLocation.getCity().replace("市", ""));
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                dLatitude = amapLocation.getLatitude();//获取纬度
                dLongitude = amapLocation.getLongitude();//获取经度
                cityName = amapLocation.getCity();
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                sendLocation();
                searchliveweather();
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    /**收到消息需要上层的activity判断 {MessageActicity onEvent(PriorityEvent event)}，这个地方是特殊分支*/
    public void onEvent(LocationEvent event){
        switch (event.getEvent()){
            case GET_EVENT:
                mlocationClient.startLocation();
                break;
            case GET_WEATHER:
                sendWeather();
                break;
        }
    }

    private void sendLocation() {
        locationEvent.setLongitude(dLongitude);
        locationEvent.setLatitude(dLatitude);
        locationEvent.setCityName(cityName);
        EventBus.getDefault().postSticky(locationEvent);
        Log.e("yukiLocal", "locationEvent"+dLongitude+dLatitude);
    }

    private void sendWeather() {
        weatherEvent.setWeather(weather);
        EventBus.getDefault().postSticky(weatherEvent);
    }

    /**-----------------get/set 的实体定义---------------------*/
    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int rCode) {
        if (rCode == 1000) {
            if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                weatherlive = weatherLiveResult.getLiveResult();
                weather = weatherlive.getWeather();
            }else {
                ToastUtil.show(this, R.string.no_result);
            }
        }else {
            ToastUtil.showerror(this, rCode);
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }

    private void searchliveweather() {
        mquery = new WeatherSearchQuery(cityName, WeatherSearchQuery.WEATHER_TYPE_LIVE);//检索参数为城市和天气类型，实时天气为1、天气预报为2
        mweathersearch=new WeatherSearch(this);
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索
    }
}
