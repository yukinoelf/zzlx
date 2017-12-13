package com.zhizulx.tt.imservice.manager;

import android.util.Log;

import com.zhizulx.tt.DB.DBInterface;
import com.zhizulx.tt.DB.String2Entity;
import com.zhizulx.tt.DB.entity.CityEntity;
import com.zhizulx.tt.DB.entity.CollectRouteEntity;
import com.zhizulx.tt.DB.entity.ConfigEntity;
import com.zhizulx.tt.DB.entity.DayRouteEntity;
import com.zhizulx.tt.DB.entity.HotelEntity;
import com.zhizulx.tt.DB.entity.RouteEntity;
import com.zhizulx.tt.DB.entity.SightEntity;
import com.zhizulx.tt.DB.sp.SystemConfigSp;
import com.zhizulx.tt.R;
import com.zhizulx.tt.imservice.event.TravelEvent;
import com.zhizulx.tt.protobuf.IMBaseDefine;
import com.zhizulx.tt.protobuf.IMBuddy;
import com.zhizulx.tt.protobuf.helper.ProtoBuf2JavaBean;
import com.zhizulx.tt.utils.CsvUtil;
import com.zhizulx.tt.utils.FileUtil;
import com.zhizulx.tt.utils.Logger;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

import static com.zhizulx.tt.protobuf.helper.ProtoBuf2JavaBean.getHotelEntity;
import static com.zhizulx.tt.protobuf.helper.ProtoBuf2JavaBean.getSightEntity;

public class IMTravelManager extends IMManager {
    private Logger logger = Logger.getLogger(IMTravelManager.class);
	private static IMTravelManager inst = new IMTravelManager();
	public static IMTravelManager instance() {
			return inst;
	}

    private IMSocketManager imSocketManager = IMSocketManager.instance();
    private IMLoginManager loginMgr = IMLoginManager.instance();
    private DBInterface dbInterface = DBInterface.instance();
    private Boolean dBInitFin = false;

    /**key=> sessionKey*/
    private Map<String, String> cityCodeName = new HashMap<>();
    private Map<String, String> cityNameCode = new HashMap<>();

    private Map<Integer, HotelEntity> hotelEntityMap = new HashMap<>();
    private Map<Integer, SightEntity> sightEntityMap = new HashMap<>();
    private List<RouteEntity> routeEntityList = new ArrayList<>();
    private RouteEntity routeEntity = new RouteEntity();
    private ConfigEntity configEntity = new ConfigEntity();
    private List<CityEntity> cityEntityList = new ArrayList<>();
    private List<CollectRouteEntity> collectRouteEntityList = new ArrayList<>();
    private CollectRouteEntity collectRouteEntity = new CollectRouteEntity();
    public static final int GET_ROUTE_BY_TAG = 0;
    public static final int GET_ROUTE_BY_SENTENCE = 1;

    @Override
    public void doOnStart() {
        String[] name = ctx.getResources().getStringArray(R.array.city_name);
        String[] code = ctx.getResources().getStringArray(R.array.city_code);
        for (int index=0; index<name.length; index ++) {
            cityCodeName.put(code[index], name[index]);
            cityNameCode.put(name[index], code[index]);
        }
    }


    // 未读消息控制器，本地是不存状态的
    public void onNormalLoginOk(){
        onLocalLoginOk();
        onLocalNetOk();
    }

    public void onLocalNetOk(){
        //reqTravelList();
    }

    public void onLocalLoginOk(){
        logger.i("group#loadFromDb");

/*        List<HotelEntity> localHotelEntityList = dbInterface.loadAllHotel();
        for(HotelEntity hotelEntity:localHotelEntityList){
            this.hotelEntityList.add(hotelEntity);
        }*/
        new Thread() {
            @Override
            public void run() {
                if (dBInitFin == false) {
                    initCityEntity();
                    initSightHotel();
                    dBInitFin = true;
                }
            }
        }.start();
        triggerEvent(new TravelEvent(TravelEvent.Event.TRAVEL_LIST_OK));
    }

    @Override
    public void reset() {
    }

    private void initSightHotel() {
        List<SightEntity> sightEntityList = dbInterface.loadAllSight();
        for (SightEntity sightEntity : sightEntityList) {
            sightEntityMap.put(sightEntity.getPeerId(), sightEntity);
        }

        List<HotelEntity> hotelEntityList = dbInterface.loadAllHotel();
        for (HotelEntity hotelEntity : hotelEntityList) {
            hotelEntityMap.put(hotelEntity.getPeerId(), hotelEntity);
        }
    }

    /**
     * 继承该方法实现自身的事件驱动
     * @param event
     */
    public synchronized void triggerEvent(TravelEvent event) {
        EventBus.getDefault().post(event);
    }

    /**-------------------------------分割线----------------------------------*/
    private IMBuddy.QualityType getQuality(int i) {
        switch (i) {
            case 1:
                return IMBuddy.QualityType.QUALITY_LOW;
            case 2:
                return IMBuddy.QualityType.QUALITY_MID;
            case 3:
                return IMBuddy.QualityType.QUALITY_HIGH;
            default:
                return IMBuddy.QualityType.QUALITY_MID;
        }
    }

    private IMBuddy.PositionType getPosition(int i) {
        switch (i) {
            case 1:
                return IMBuddy.PositionType.CENTRAL;
            case 2:
                return IMBuddy.PositionType.SCENIC;
            case 3:
                return IMBuddy.PositionType.OTHER;
            default:
                return IMBuddy.PositionType.OTHER;
        }
    }

    /**----------------实体set/get-------------------------------*/
    public String getCityNameByCode(String code) {
        return cityCodeName.get(code);
    }

    public String getCityCodeByName(String name) {
        return cityNameCode.get(name);
    }

    public List<HotelEntity> getHotelList() {
        List<HotelEntity> hotelEntityList = new ArrayList<>(hotelEntityMap.values());
        return hotelEntityList;
    }

    public List<SightEntity> getSightList() {
        List<SightEntity> sightEntityList = new ArrayList<>(sightEntityMap.values());
        return sightEntityList;
    }

    public HotelEntity getHotelByID(int id) {
        return hotelEntityMap.get(id);
    }

    public SightEntity getSightByID(int id) {
        return sightEntityMap.get(id);
    }

    public RouteEntity getRouteEntity() {
        return routeEntity;
    }

    public void setRouteEntity(RouteEntity routeEntity) {
        this.routeEntity = routeEntity;
    }

    public List<RouteEntity> getRouteEntityList() {
        return routeEntityList;
    }

    public CollectRouteEntity getCollectRouteEntity() {
        return collectRouteEntity;
    }

    public void setCollectRouteEntity(CollectRouteEntity collectRouteEntity) {
        this.collectRouteEntity = collectRouteEntity;
    }

    public List<CollectRouteEntity> getCollectRouteEntityList() {
        return collectRouteEntityList;
    }

    public void setCollectRouteEntityList(List<CollectRouteEntity> collectRouteEntityList) {
        this.collectRouteEntityList.clear();
        this.collectRouteEntityList.addAll(collectRouteEntityList);
    }

    public ConfigEntity getConfigEntity() {
        return configEntity;
    }

    public void setConfigEntity(ConfigEntity configEntity) {
        this.configEntity = configEntity;
    }

    public Boolean getdBInitFin() {
        return dBInitFin;
    }

    public void reqGetRandomRoute(int queryType) {
        Log.e("yuki", "reqGetRandomRoute");
        if (queryType == GET_ROUTE_BY_TAG) {
            configEntity.setSentence("");
        }
        int loginId = IMLoginManager.instance().getLoginId();
        IMBuddy.NewQueryRadomRouteReq newQueryRadomRouteReq = IMBuddy.NewQueryRadomRouteReq.newBuilder()
                .setUserId(loginId)
                .addAllTags(configEntity.getTags())
                .setSentence(configEntity.getSentence()).build();

        int sid = IMBaseDefine.ServiceID.SID_BUDDY_LIST_VALUE;
        int cid = IMBaseDefine.BuddyListCmdID.CID_BUDDY_LIST_RADOM_ROUTE_QUERY_REQUEST_VALUE;
        imSocketManager.sendRequest(newQueryRadomRouteReq,sid,cid);
    }

    public void onRspGetRandomRoute(IMBuddy.NewQueryRadomRouteRsp newQueryRadomRouteRsp) throws ParseException {
        Log.e("yuki", "onRspGetRandomRoute");
        if (newQueryRadomRouteRsp.getResultCode() != 0) {
            Log.e("yuki", "onRepTravelList fail" + newQueryRadomRouteRsp.getResultCode());
            triggerEvent(new TravelEvent(TravelEvent.Event.QUERY_RANDOM_ROUTE_FAIL));
        } else {
            if (newQueryRadomRouteRsp.getRoutesList().size() == 0) {
                Log.e("yuki", "onRspGetRandomRoute no route");
            } else {
                if (configEntity.getSentence().equals("")) {
                    List<RouteEntity> allRoute = new ArrayList<>();
                    for (IMBuddy.Route route : newQueryRadomRouteRsp.getRoutesList()) {
                        allRoute.add(ProtoBuf2JavaBean.getRouteEntity(route));
                    }
                    routeEntityList.clear();
                    routeEntityList.addAll(delSameCity(allRoute));
                    triggerEvent(new TravelEvent(TravelEvent.Event.QUERY_RANDOM_ROUTE_TAG_OK));
                } else {
                    routeEntity = ProtoBuf2JavaBean.getRouteEntity(newQueryRadomRouteRsp.getRoutesList().get(0));
                    triggerEvent(new TravelEvent(TravelEvent.Event.QUERY_RANDOM_ROUTE_SENTENCE_OK));
                }
            }
        }
    }

    private List<RouteEntity> delSameCity(List<RouteEntity> allRoute) {
        Map<String, RouteEntity> routeEntityMap = new HashMap<>();
        for (RouteEntity routeEntity : allRoute) {
            routeEntityMap.put(routeEntity.getCityCode(), routeEntity);
        }
        return new ArrayList<RouteEntity>(routeEntityMap.values());
    }

    public void reqUpdateRandomRoute(List<Integer> sightIdList) {
        Log.e("yuki", "reqUpdateRandomRoute");
        for (int index:sightIdList) {
            Log.e("yuki", String.valueOf(index));
        }
        HashSet h = new HashSet(sightIdList);
        sightIdList.clear();
        sightIdList.addAll(h);

        int loginId = IMLoginManager.instance().getLoginId();
        String startTime = String.format("%02d:00", routeEntity.getStartTime());
        String endTime = String.format("%02d:00", routeEntity.getEndTime());
        IMBuddy.NewUpdateRadomRouteReq newUpdateRadomRouteReq = IMBuddy.NewUpdateRadomRouteReq.newBuilder()
                .setUserId(loginId)
                .setDayCount(routeEntity.getDay())
                .setCityCode(routeEntity.getCityCode())
                .setStartTransportTool(IMBuddy.TransportToolType.valueOf(routeEntity.getStartTrafficTool()))
                .setEndTransportTool(IMBuddy.TransportToolType.valueOf(routeEntity.getEndTrafficTool()))
                .setStartTime(startTime)
                .setEndTime(endTime)
                .addAllScenicIds(sightIdList)
                .setTag(routeEntity.getRouteType()).build();

        int sid = IMBaseDefine.ServiceID.SID_BUDDY_LIST_VALUE;
        int cid = IMBaseDefine.BuddyListCmdID.CID_BUDDY_LIST_RADOM_ROUTE_UPDATE_REQUEST_VALUE;
        imSocketManager.sendRequest(newUpdateRadomRouteReq,sid,cid);
    }

    public void onRspUpdateRandomRoute(IMBuddy.NewUpdateRadomRouteRsp newUpdateRadomRouteRsp) throws ParseException {
        Log.e("yuki", "onRspUpdateRandomRoute");
        if (newUpdateRadomRouteRsp.getResultCode() != 0) {
            logger.e("onRspUpdateRandomRoute fail %d", newUpdateRadomRouteRsp.getResultCode());
            triggerEvent(new TravelEvent(TravelEvent.Event.UPDATE_RANDOM_ROUTE_FAIL));
        } else {
            List<String> tags = new ArrayList<>();
            tags.addAll(routeEntity.getTags());
            routeEntity = ProtoBuf2JavaBean.getRouteEntity(newUpdateRadomRouteRsp.getRoute());
            routeEntity.setTags(tags);
            triggerEvent(new TravelEvent(TravelEvent.Event.UPDATE_RANDOM_ROUTE_OK));
        }
    }

    public void reqCreateRoute() {
        Log.e("yuki", "reqCreateRoute");
        int loginId = IMLoginManager.instance().getLoginId();
        IMBuddy.NewCreateMyTravelReq newUpdateRadomRouteReq = IMBuddy.NewCreateMyTravelReq.newBuilder()
                .setUserId(loginId)
                .setDayCount(configEntity.getDuration())
                .setCityCode(getCityCodeByName(configEntity.getDestination()))
                .addAllTags(tagProcess(configEntity.getRouteType()))
                .build();
        int sid = IMBaseDefine.ServiceID.SID_BUDDY_LIST_VALUE;
        int cid = IMBaseDefine.BuddyListCmdID.CID_BUDDY_LIST_NEW_TRAVEL_CREATE_REQUEST_VALUE;
        imSocketManager.sendRequest(newUpdateRadomRouteReq,sid,cid);
    }

    public void onRspCreateRoute(IMBuddy.NewCreateMyTravelRsp newCreateMyTravelRsp) throws ParseException {
        Log.e("yuki", "onRspCreateRoute");
        if (newCreateMyTravelRsp.getResultCode() != 0) {
            logger.e("onRepTravelList fail %d", newCreateMyTravelRsp.getResultCode());
            triggerEvent(new TravelEvent(TravelEvent.Event.CREATE_ROUTE_FAIL));
        } else {
            routeEntity = ProtoBuf2JavaBean.getRouteEntity(newCreateMyTravelRsp.getRoute());
            routeEntity.setRouteType(configEntity.getRouteType());
            routeEntity.setTags(configEntity.getTags());
            triggerEvent(new TravelEvent(TravelEvent.Event.CREATE_ROUTE_OK));
        }
    }

    public void initalRoute() {
        for(Map.Entry<Integer, SightEntity> entry : sightEntityMap.entrySet()) {
            entry.getValue().setSelect(0);
        }

        for(Map.Entry<Integer, HotelEntity> entry : hotelEntityMap.entrySet()) {
            entry.getValue().setSelect(0);
        }
    }

    private void initCityEntity() {
        cityEntityList.clear();
        List<List<String>> csvCity = new ArrayList<List<String>>();
        try {
            CsvUtil csvUtilCity = new CsvUtil(ctx, "city.csv");
            csvUtilCity.run();
            csvCity = csvUtilCity.getCsv();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (List<String> cityStringList : csvCity) {
            CityEntity cityEntity = String2Entity.getCityEntity(cityStringList);
            cityEntityList.add(cityEntity);
        }
    }

    public List<CityEntity> getCityEntityList() {
        return cityEntityList;
    }

    public CityEntity getCityEntitybyCityCode(String cityCode) {
        for (CityEntity cityEntity : cityEntityList) {
            if (cityEntity.getCityCode().equals(cityCode)) {
                return cityEntity;
            }
        }
        return null;
    }

    private List<String> tagProcess(String routeType) {
        List<String> tags = new ArrayList<>();
        if (routeType != null && !routeType.isEmpty()) {
            tags.add(routeType);
        }

        int oriSize = tags.size();
        for (int i = 0; i < 3 - oriSize; i ++) {
            tags.add(configEntity.getTags().get(i));
        }
        return tags;
    }

    public void reqCreateCollectRoute() {
        Log.e("yuki", "CreateCollectRoute");
        int loginId = IMLoginManager.instance().getLoginId();

        RouteEntity routeEntity = collectRouteEntity.getRouteEntity();
        String startTime = String.format("%02d:00", routeEntity.getStartTime());
        String endTime = String.format("%02d:00", routeEntity.getEndTime());

        List<IMBuddy.DayRoute> dayRouteList = new ArrayList<>();
        for (DayRouteEntity dayRouteEntity : routeEntity.getDayRouteEntityList()) {
            IMBuddy.DayRoute dayRoute = IMBuddy.DayRoute.newBuilder()
                    .addAllScenics(dayRouteEntity.getSightIDList())
                    .addAllHotels(dayRouteEntity.getHotelIDList()).build();
            dayRouteList.add(dayRoute);
        }

        IMBuddy.Route route = IMBuddy.Route.newBuilder()
                .setId(routeEntity.getDbId())
                .setDayCount(routeEntity.getDay())
                .setCityCode(routeEntity.getCityCode())
                .addAllTag(routeEntity.getTags())
                .setStartTransportTool(IMBuddy.TransportToolType.valueOf(routeEntity.getStartTrafficTool()))
                .setEndTransportTool(IMBuddy.TransportToolType.valueOf(routeEntity.getEndTrafficTool()))
                .setStartTime(startTime)
                .setEndTime(endTime)
                .addAllDayRoutes(dayRouteList).build();

        IMBuddy.CollectionRoute collectionRoute = IMBuddy.CollectionRoute.newBuilder()
                .setId(collectRouteEntity.getDbId())
                .setStartDate(collectRouteEntity.getStartDate())
                .setEndDate(collectRouteEntity.getEndDate())
                .setStartTrafficNo(collectRouteEntity.getStartTrafficNo())
                .setEndTrafficNo(collectRouteEntity.getEndTrafficNo())
                .setRoute(route).build();

        IMBuddy.NewCreateCollectRouteReq newCreateCollectRouteReq = IMBuddy.NewCreateCollectRouteReq.newBuilder()
                .setUserId(loginId)
                .setCollect(collectionRoute).build();
        int sid = IMBaseDefine.ServiceID.SID_BUDDY_LIST_VALUE;
        int cid = IMBaseDefine.BuddyListCmdID.CID_BUDDY_LIST_NEW_CREATE_COLLECT_ROUTE_REQUEST_VALUE;
        imSocketManager.sendRequest(newCreateCollectRouteReq,sid,cid);
    }

    public void onRspCreateCollectRoute(IMBuddy.NewCreateCollectRouteRsp newCreateCollectRouteRsp) throws ParseException {
        Log.e("yuki", "onRspCreateCollectRoute");
        if (newCreateCollectRouteRsp.getResultCode() != 0) {
            logger.e("onRspCreateCollectRoute fail %d", newCreateCollectRouteRsp.getResultCode());
            triggerEvent(new TravelEvent(TravelEvent.Event.CREATE_COLLECT_ROUTE_FAIL));
        } else {
            triggerEvent(new TravelEvent(TravelEvent.Event.CREATE_COLLECT_ROUTE_OK));
        }
    }

    public void reqGetCollectRoute() {
        Log.e("yuki", "reqGetCollectRoute");
        int loginId = IMLoginManager.instance().getLoginId();
        IMBuddy.NewQueryCollectRouteReq newQueryCollectRouteReq = IMBuddy.NewQueryCollectRouteReq.newBuilder()
                .setUserId(loginId).build();

        int sid = IMBaseDefine.ServiceID.SID_BUDDY_LIST_VALUE;
        int cid = IMBaseDefine.BuddyListCmdID.CID_BUDDY_LIST_NEW_QUERY_COLLECT_ROUTE_REQUEST_VALUE;
        imSocketManager.sendRequest(newQueryCollectRouteReq,sid,cid);
    }

    public void onRspGetCollectRoute(IMBuddy.NewQueryCollectRouteRsp newQueryCollectRouteRsp) throws ParseException {
        Log.e("yuki", "onRspGetCollectRoute");
        if (newQueryCollectRouteRsp.getResultCode() != 0) {
            Log.e("yuki", "onRepTravelList fail" + newQueryCollectRouteRsp.getResultCode());
            triggerEvent(new TravelEvent(TravelEvent.Event.QUERY_COLLECT_ROUTE_FAIL));
        } else {
            if (newQueryCollectRouteRsp.getCollectionsList().size() == 0) {
                Log.e("yuki", "onRspGetCollectRoute no route");
            } else {
                List<CollectRouteEntity> collectRouteEntityList = new ArrayList<>();
                for (IMBuddy.CollectionRoute collectionRoute : newQueryCollectRouteRsp.getCollectionsList()) {
                    collectRouteEntityList.add(ProtoBuf2JavaBean.getCollectRouteEntity(collectionRoute));
                }
                setCollectRouteEntityList(collectRouteEntityList);
                triggerEvent(new TravelEvent(TravelEvent.Event.QUERY_COLLECT_ROUTE_OK));
            }
        }
    }

    public void reqDelCollectRoute(List<Integer> delIdList) {
        Log.e("yuki", "reqDelCollectRoute");
        int loginId = IMLoginManager.instance().getLoginId();
        IMBuddy.NewDelCollectRouteReq newQueryCollectRouteReq = IMBuddy.NewDelCollectRouteReq.newBuilder()
                .setUserId(loginId)
                .addAllCollectId(delIdList).build();

        int sid = IMBaseDefine.ServiceID.SID_BUDDY_LIST_VALUE;
        int cid = IMBaseDefine.BuddyListCmdID.CID_BUDDY_LIST_NEW_DELETE_COLLECT_ROUTE_REQUEST_VALUE;
        imSocketManager.sendRequest(newQueryCollectRouteReq,sid,cid);
    }

    public void onRspDelCollectRoute(IMBuddy.NewDelCollectRouteRsp newDelCollectRouteRsp) throws ParseException {
        Log.e("yuki", "onRspDelCollectRoute");
        if (newDelCollectRouteRsp.getResultCode() != 0) {
            logger.e("onRspDelCollectRoute fail %d", newDelCollectRouteRsp.getResultCode());
            triggerEvent(new TravelEvent(TravelEvent.Event.DELETE_COLLECT_ROUTE_FAIL));
        } else {
            triggerEvent(new TravelEvent(TravelEvent.Event.DELETE_COLLECT_ROUTE_OK));
        }
    }

    public void reqSightHotel(String cityCode) {
        Log.e("yuki", "reqSightHotel");
        int loginId = IMLoginManager.instance().getLoginId();
        IMBuddy.GetScenicHotelReq getScenicHotelReq = IMBuddy.GetScenicHotelReq.newBuilder()
                .setUserId(loginId)
                .setCityCode(cityCode).build();

        int sid = IMBaseDefine.ServiceID.SID_BUDDY_LIST_VALUE;
        int cid = IMBaseDefine.BuddyListCmdID.CID_BUDDY_LIST_TRAVEL_GET_SCENIC_HOTEL_REQUEST_VALUE;
        imSocketManager.sendRequest(getScenicHotelReq,sid,cid);
    }

    public void onRspSightHotel(IMBuddy.GetScenicHotelRsp getScenicHotelRsp) throws ParseException {
        Log.e("yuki", "onRspSightHotel");
        if (getScenicHotelRsp.getResultCode() != 0) {
            logger.e("onRspSightHotel fail %d", getScenicHotelRsp.getResultCode());
            triggerEvent(new TravelEvent(TravelEvent.Event.QUERY_SIGHT_HOTEL_FAIL));
        } else {
            List<SightEntity> sightEntityList = new ArrayList<>();
            for (IMBuddy.ScenicInfo scenicInfo : getScenicHotelRsp.getScenicInfoList()) {
                sightEntityList.add(getSightEntity(scenicInfo));
            }
            addSightList(sightEntityList);

            List<HotelEntity> hotelEntityList = new ArrayList<>();
            for (IMBuddy.HotelInfo hotelInfo : getScenicHotelRsp.getHotelInfoList()) {
                hotelEntityList.add(getHotelEntity(hotelInfo));
            }
            addHotelList(hotelEntityList);
            triggerEvent(new TravelEvent(TravelEvent.Event.QUERY_SIGHT_HOTEL_OK));
        }
    }

    public Boolean hasCollected(int routeId) {
        for (CollectRouteEntity collectRouteEntity: collectRouteEntityList) {
            if (routeId == collectRouteEntity.getRouteEntity().getDbId()) {
                return true;
            }
        }
        return false;
    }

    public void initDatePlace() {
        Calendar cal = Calendar.getInstance();
        configEntity.setStartCity(SystemConfigSp.instance().getStrConfig(SystemConfigSp.SysCfgDimension.LOCAL_CITY));
        configEntity.setEndCity(SystemConfigSp.instance().getStrConfig(SystemConfigSp.SysCfgDimension.LOCAL_CITY));
        cal.add(Calendar.DATE, 2);
        configEntity.setStartDate(cal.getTime());
        cal.add(Calendar.DATE, 3);
        configEntity.setEndDate(cal.getTime());
    }

    private void addSightList(List<SightEntity> sightEntityList) {
        List<SightEntity> dbSightEntityList = new ArrayList<>();
        for (SightEntity sightEntity : sightEntityList) {
            if (sightEntityMap.containsKey(sightEntity.getPeerId())) {
                continue;
            }
            sightEntityMap.put(sightEntity.getPeerId(), sightEntity);
            dbSightEntityList.add(sightEntity);
        }
        dbInterface.batchInsertOrUpdateSight(dbSightEntityList);
    }

    private void addHotelList(List<HotelEntity> hotelEntityList) {
        List<HotelEntity> dbHotelEntityList = new ArrayList<>();
        for (HotelEntity hotelEntity : hotelEntityList) {
            if (hotelEntityMap.containsKey(hotelEntity.getPeerId())) {
                continue;
            }
            hotelEntityMap.put(hotelEntity.getPeerId(), hotelEntity);
            dbHotelEntityList.add(hotelEntity);
        }
        dbInterface.batchInsertOrUpdateHotel(dbHotelEntityList);
    }

    public void AppTrace(String code, String msg) {
        FileUtil.uploadLog(code, msg, String.valueOf(loginMgr.getLoginId()));
    }
}
