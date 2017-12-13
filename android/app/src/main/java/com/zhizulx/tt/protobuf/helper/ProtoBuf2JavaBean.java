package com.zhizulx.tt.protobuf.helper;

import android.util.Log;

import com.google.protobuf.ByteString;
import com.zhizulx.tt.DB.entity.CollectRouteEntity;
import com.zhizulx.tt.DB.entity.DayRouteEntity;
import com.zhizulx.tt.DB.entity.DepartmentEntity;
import com.zhizulx.tt.DB.entity.HotelEntity;
import com.zhizulx.tt.DB.entity.RouteEntity;
import com.zhizulx.tt.DB.entity.SightEntity;
import com.zhizulx.tt.DB.entity.TrafficEntity;
import com.zhizulx.tt.config.DBConstant;
import com.zhizulx.tt.DB.entity.GroupEntity;
import com.zhizulx.tt.DB.entity.MessageEntity;
import com.zhizulx.tt.DB.entity.SessionEntity;
import com.zhizulx.tt.DB.entity.UserEntity;
import com.zhizulx.tt.config.MessageConstant;
import com.zhizulx.tt.imservice.entity.AudioMessage;
import com.zhizulx.tt.imservice.entity.MsgAnalyzeEngine;
import com.zhizulx.tt.imservice.entity.UnreadEntity;
import com.zhizulx.tt.protobuf.IMBaseDefine;
import com.zhizulx.tt.protobuf.IMBuddy;
import com.zhizulx.tt.protobuf.IMGroup;
import com.zhizulx.tt.protobuf.IMMessage;
import com.zhizulx.tt.utils.CommonUtil;
import com.zhizulx.tt.utils.FileUtil;
import com.zhizulx.tt.utils.pinyin.PinYin;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : yingmu on 15-1-5.
 * @email : yingmu@mogujie.com.
 *
 */
public class ProtoBuf2JavaBean {

    public static DepartmentEntity getDepartEntity(IMBaseDefine.DepartInfo departInfo){
        DepartmentEntity departmentEntity = new DepartmentEntity();

        int timeNow = (int) (System.currentTimeMillis()/1000);

        departmentEntity.setDepartId(departInfo.getDeptId());
        departmentEntity.setDepartName(departInfo.getDeptName());
        departmentEntity.setPriority(departInfo.getPriority());
        departmentEntity.setStatus(getDepartStatus(departInfo.getDeptStatus()));

        departmentEntity.setCreated(timeNow);
        departmentEntity.setUpdated(timeNow);

        // 设定pinyin 相关
        PinYin.getPinYin(departInfo.getDeptName(), departmentEntity.getPinyinElement());

        return departmentEntity;
    }

    public static UserEntity getUserEntity(IMBaseDefine.UserInfo userInfo){
        UserEntity userEntity = new UserEntity();
        int timeNow = (int) (System.currentTimeMillis()/1000);

        userEntity.setStatus(userInfo.getStatus());
        userEntity.setAvatar(userInfo.getAvatarUrl());
        userEntity.setCreated(timeNow);
        userEntity.setDepartmentId(userInfo.getDepartmentId());
        userEntity.setEmail(userInfo.getEmail());
        userEntity.setGender(userInfo.getUserGender());
        userEntity.setMainName(userInfo.getUserNickName());
        userEntity.setPhone(userInfo.getUserTel());
        userEntity.setPinyinName(userInfo.getUserDomain());
        userEntity.setRealName(userInfo.getUserRealName());
        userEntity.setUpdated(timeNow);
        userEntity.setPeerId(userInfo.getUserId());

        PinYin.getPinYin(userEntity.getMainName(), userEntity.getPinyinElement());
        return userEntity;
    }

    public static SessionEntity getSessionEntity(IMBaseDefine.ContactSessionInfo sessionInfo){
        SessionEntity sessionEntity = new SessionEntity();

        int msgType = getJavaMsgType(sessionInfo.getLatestMsgType());
        sessionEntity.setLatestMsgType(msgType);
        sessionEntity.setPeerType(getJavaSessionType(sessionInfo.getSessionType()));
        sessionEntity.setPeerId(sessionInfo.getSessionId());
        sessionEntity.buildSessionKey();
        sessionEntity.setTalkId(sessionInfo.getLatestMsgFromUserId());
        sessionEntity.setLatestMsgId(sessionInfo.getLatestMsgId());
        sessionEntity.setCreated(sessionInfo.getUpdatedTime());

        String content  = sessionInfo.getLatestMsgData().toStringUtf8();
        String desMessage = new String(com.zhizulx.tt.Security.getInstance().DecryptMsg(content));
        // 判断具体的类型是什么
        if(msgType == DBConstant.MSG_TYPE_GROUP_TEXT ||
                msgType ==DBConstant.MSG_TYPE_SINGLE_TEXT){
            desMessage =  MsgAnalyzeEngine.analyzeMessageDisplay(desMessage);
        }

        sessionEntity.setLatestMsgData(desMessage);
        sessionEntity.setUpdated(sessionInfo.getUpdatedTime());

        return sessionEntity;
    }


    public static GroupEntity getGroupEntity(IMBaseDefine.GroupInfo groupInfo){
        GroupEntity groupEntity = new GroupEntity();
        int timeNow = (int) (System.currentTimeMillis()/1000);
        groupEntity.setUpdated(timeNow);
        groupEntity.setCreated(timeNow);
        groupEntity.setMainName(groupInfo.getGroupName());
        groupEntity.setAvatar(groupInfo.getGroupAvatar());
        groupEntity.setCreatorId(groupInfo.getGroupCreatorId());
        groupEntity.setPeerId(groupInfo.getGroupId());
        groupEntity.setGroupType(getJavaGroupType(groupInfo.getGroupType()));
        groupEntity.setStatus(groupInfo.getShieldStatus());
        groupEntity.setUserCnt(groupInfo.getGroupMemberListCount());
        groupEntity.setVersion(groupInfo.getVersion());
        groupEntity.setlistGroupMemberIds(groupInfo.getGroupMemberListList());

        // may be not good place
        PinYin.getPinYin(groupEntity.getMainName(), groupEntity.getPinyinElement());

        return groupEntity;
    }


    /**
     * 创建群时候的转化
     * @param groupCreateRsp
     * @return
     */
    public static GroupEntity getGroupEntity(IMGroup.IMGroupCreateRsp groupCreateRsp){
        GroupEntity groupEntity = new GroupEntity();
        int timeNow = (int) (System.currentTimeMillis()/1000);
        groupEntity.setMainName(groupCreateRsp.getGroupName());
        groupEntity.setlistGroupMemberIds(groupCreateRsp.getUserIdListList());
        groupEntity.setCreatorId(groupCreateRsp.getUserId());
        groupEntity.setPeerId(groupCreateRsp.getGroupId());

        groupEntity.setUpdated(timeNow);
        groupEntity.setCreated(timeNow);
        groupEntity.setAvatar("");
        groupEntity.setGroupType(DBConstant.GROUP_TYPE_TEMP);
        groupEntity.setStatus(DBConstant.GROUP_STATUS_ONLINE);
        groupEntity.setUserCnt(groupCreateRsp.getUserIdListCount());
        groupEntity.setVersion(1);

        PinYin.getPinYin(groupEntity.getMainName(), groupEntity.getPinyinElement());
        return groupEntity;
    }


    /**
     * 拆分消息在上层做掉 图文混排
     * 在这判断
    */
    public static MessageEntity getMessageEntity(IMBaseDefine.MsgInfo msgInfo) {
        MessageEntity messageEntity = null;
        IMBaseDefine.MsgType msgType = msgInfo.getMsgType();
        switch (msgType) {
            case MSG_TYPE_SINGLE_AUDIO:
            case MSG_TYPE_GROUP_AUDIO:
                try {
                    /**语音的解析不能转自 string再返回来*/
                    messageEntity = analyzeAudio(msgInfo);
                } catch (JSONException e) {
                    return null;
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
                break;

            case MSG_TYPE_GROUP_TEXT:
            case MSG_TYPE_SINGLE_TEXT:
                messageEntity = analyzeText(msgInfo);
                break;
            default:
                throw new RuntimeException("ProtoBuf2JavaBean#getMessageEntity wrong type!");
        }
        return messageEntity;
    }

    public static MessageEntity analyzeText(IMBaseDefine.MsgInfo msgInfo){
       return MsgAnalyzeEngine.analyzeMessage(msgInfo);
    }


    public static AudioMessage analyzeAudio(IMBaseDefine.MsgInfo msgInfo) throws JSONException, UnsupportedEncodingException {
        AudioMessage audioMessage = new AudioMessage();
        audioMessage.setFromId(msgInfo.getFromSessionId());
        audioMessage.setMsgId(msgInfo.getMsgId());
        audioMessage.setMsgType(getJavaMsgType(msgInfo.getMsgType()));
        audioMessage.setStatus(MessageConstant.MSG_SUCCESS);
        audioMessage.setReadStatus(MessageConstant.AUDIO_UNREAD);
        audioMessage.setDisplayType(DBConstant.SHOW_AUDIO_TYPE);
        audioMessage.setCreated(msgInfo.getCreateTime());
        audioMessage.setUpdated(msgInfo.getCreateTime());

        ByteString bytes = msgInfo.getMsgData();

        byte[] audioStream = bytes.toByteArray();
        if(audioStream.length < 4){
            audioMessage.setReadStatus(MessageConstant.AUDIO_READED);
            audioMessage.setAudioPath("");
            audioMessage.setAudiolength(0);
        }else {
            int msgLen = audioStream.length;
            byte[] playTimeByte = new byte[4];
            byte[] audioContent = new byte[msgLen - 4];

            System.arraycopy(audioStream, 0, playTimeByte, 0, 4);
            System.arraycopy(audioStream, 4, audioContent, 0, msgLen - 4);
            int playTime = CommonUtil.byteArray2int(playTimeByte);
            String audioSavePath = FileUtil.saveAudioResourceToFile(audioContent, audioMessage.getFromId());
            audioMessage.setAudiolength(playTime);
            audioMessage.setAudioPath(audioSavePath);
        }

        /**抽离出来 或者用gson*/
        JSONObject extraContent = new JSONObject();
        extraContent.put("audioPath",audioMessage.getAudioPath());
        extraContent.put("audiolength",audioMessage.getAudiolength());
        extraContent.put("readStatus",audioMessage.getReadStatus());
        String audioContent = extraContent.toString();
        audioMessage.setContent(audioContent);

        return audioMessage;
    }


    public static MessageEntity getMessageEntity(IMMessage.IMMsgData msgData){

        MessageEntity messageEntity = null;
        IMBaseDefine.MsgType msgType = msgData.getMsgType();
        IMBaseDefine.MsgInfo msgInfo = IMBaseDefine.MsgInfo.newBuilder()
                .setMsgData(msgData.getMsgData())
                .setMsgId(msgData.getMsgId())
                .setMsgType(msgType)
                .setCreateTime(msgData.getCreateTime())
                .setFromSessionId(msgData.getFromUserId())
                .build();

        switch (msgType) {
            case MSG_TYPE_SINGLE_AUDIO:
            case MSG_TYPE_GROUP_AUDIO:
                try {
                    messageEntity = analyzeAudio(msgInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            case MSG_TYPE_GROUP_TEXT:
            case MSG_TYPE_SINGLE_TEXT:
                messageEntity = analyzeText(msgInfo);
                break;
            default:
                throw new RuntimeException("ProtoBuf2JavaBean#getMessageEntity wrong type!");
        }
        if(messageEntity != null){
            messageEntity.setToId(msgData.getToSessionId());
        }

        /**
         消息的发送状态与 展示类型需要在上层做掉
         messageEntity.setStatus();
         messageEntity.setDisplayType();
         */
        return messageEntity;
    }

    public static UnreadEntity getUnreadEntity(IMBaseDefine.UnreadInfo pbInfo){
        UnreadEntity unreadEntity = new UnreadEntity();
        unreadEntity.setSessionType(getJavaSessionType(pbInfo.getSessionType()));
        unreadEntity.setLatestMsgData(pbInfo.getLatestMsgData().toString());
        unreadEntity.setPeerId(pbInfo.getSessionId());
        unreadEntity.setLaststMsgId(pbInfo.getLatestMsgId());
        unreadEntity.setUnReadCnt(pbInfo.getUnreadCnt());
        unreadEntity.buildSessionKey();
        return unreadEntity;
    }

    /**----enum 转化接口--*/
    public static int getJavaMsgType(IMBaseDefine.MsgType msgType){
        switch (msgType){
            case MSG_TYPE_GROUP_TEXT:
                return DBConstant.MSG_TYPE_GROUP_TEXT;
            case MSG_TYPE_GROUP_AUDIO:
                return DBConstant.MSG_TYPE_GROUP_AUDIO;
            case MSG_TYPE_SINGLE_AUDIO:
                return DBConstant.MSG_TYPE_SINGLE_AUDIO;
            case MSG_TYPE_SINGLE_TEXT:
                return DBConstant.MSG_TYPE_SINGLE_TEXT;
            default:
                throw new IllegalArgumentException("msgType is illegal,cause by #getProtoMsgType#" +msgType);
        }
    }

    public static int getJavaSessionType(IMBaseDefine.SessionType sessionType){
        switch (sessionType){
            case SESSION_TYPE_SINGLE:
                return DBConstant.SESSION_TYPE_SINGLE;
            case SESSION_TYPE_GROUP:
                return DBConstant.SESSION_TYPE_GROUP;
            default:
                throw new IllegalArgumentException("sessionType is illegal,cause by #getProtoSessionType#" +sessionType);
        }
    }

    public static int getJavaGroupType(IMBaseDefine.GroupType groupType){
        switch (groupType){
            case GROUP_TYPE_NORMAL:
                return DBConstant.GROUP_TYPE_NORMAL;
            case GROUP_TYPE_TMP:
                return DBConstant.GROUP_TYPE_TEMP;
            default:
                throw new IllegalArgumentException("sessionType is illegal,cause by #getProtoSessionType#" +groupType);
        }
    }

    public static int getGroupChangeType(IMBaseDefine.GroupModifyType modifyType){
        switch (modifyType){
            case GROUP_MODIFY_TYPE_ADD:
                return DBConstant.GROUP_MODIFY_TYPE_ADD;
            case GROUP_MODIFY_TYPE_DEL:
                return DBConstant.GROUP_MODIFY_TYPE_DEL;
            default:
                throw new IllegalArgumentException("GroupModifyType is illegal,cause by " +modifyType);
        }
    }

    public static int getDepartStatus(IMBaseDefine.DepartmentStatusType statusType){
        switch (statusType){
            case DEPT_STATUS_OK:
                return DBConstant.DEPT_STATUS_OK;
            case DEPT_STATUS_DELETE:
                return DBConstant.DEPT_STATUS_DELETE;
            default:
                throw new IllegalArgumentException("getDepartStatus is illegal,cause by " +statusType);
        }

    }

    public static TrafficEntity getTrafficEntity(IMBuddy.TravelToolInfo travelToolInfo){
        TrafficEntity trafficEntity = new TrafficEntity();
        trafficEntity.setType(travelToolInfo.getTransportToolType());
        trafficEntity.setStartStation(travelToolInfo.getPlaceFrom());
        trafficEntity.setEndStation(travelToolInfo.getPlaceTo());
        trafficEntity.setStartTime(travelToolInfo.getTimeFrom());
        trafficEntity.setEndTime(travelToolInfo.getTimeTo());
        trafficEntity.setNo(travelToolInfo.getNo());
        trafficEntity.setPrice(travelToolInfo.getPrice());
        trafficEntity.setSeatClass(travelToolInfo.getClass_());
        return trafficEntity;
    }

    public static RouteEntity getRouteEntity(IMBuddy.Route route) throws ParseException {
        RouteEntity routeEntity = new RouteEntity();
        routeEntity.setDbId(route.getId());
        routeEntity.setDay(route.getDayCount());
        routeEntity.setCityCode(route.getCityCode());
        routeEntity.setRouteType(route.getTagList().get(0));
        routeEntity.setTags(route.getTagList());
        routeEntity.setStartTrafficTool(route.getStartTransportTool().getNumber());
        routeEntity.setEndTrafficTool(route.getEndTransportTool().getNumber());
        routeEntity.setStartTime(getHour(route.getStartTime()));
        routeEntity.setEndTime(getHour(route.getEndTime()));
        routeEntity.setDayRouteEntityList(getDayRouteEntityList(route.getDayRoutesList()));
        return routeEntity;
    }

    private static int getHour(String hour) throws ParseException {
        SimpleDateFormat formatterString = new SimpleDateFormat("HH:mm");
        SimpleDateFormat formatterInt = new SimpleDateFormat("HH");
        Date date = null;
        date = formatterString.parse(hour);
        String singleHour = formatterInt.format(date);
        return Integer.valueOf(singleHour);
    }

    private static List<DayRouteEntity> getDayRouteEntityList(List<IMBuddy.DayRoute> dayRouteList) {
        List<DayRouteEntity> dayRouteEntityList = new ArrayList<>();
        for (IMBuddy.DayRoute dayRoute : dayRouteList) {
            DayRouteEntity dayRouteEntity = new DayRouteEntity();
            dayRouteEntity.setSightIDList(dayRoute.getScenicsList());
            dayRouteEntity.setHotelIDList(dayRoute.getHotelsList());
            dayRouteEntityList.add(dayRouteEntity);
        }
        return dayRouteEntityList;
    }

    public static CollectRouteEntity getCollectRouteEntity(IMBuddy.CollectionRoute collectionRoute) throws ParseException {
        CollectRouteEntity collectRouteEntity = new CollectRouteEntity();
        collectRouteEntity.setDbId(collectionRoute.getId());
        collectRouteEntity.setStartDate(collectionRoute.getStartDate());
        collectRouteEntity.setStartTrafficNo(collectionRoute.getStartTrafficNo());
        collectRouteEntity.setEndTrafficNo(collectionRoute.getEndTrafficNo());
        collectRouteEntity.setRouteEntity(getRouteEntity(collectionRoute.getRoute()));
        return collectRouteEntity;
    }

    public static SightEntity getSightEntity(IMBuddy.ScenicInfo scenicInfo) throws ParseException {
        SightEntity sightEntity = new SightEntity();
        sightEntity.setPeerId(scenicInfo.getId());
        sightEntity.setCityCode(scenicInfo.getCityCode());
        sightEntity.setName(scenicInfo.getSightName());
        sightEntity.setPic(scenicInfo.getSightPic());
        sightEntity.setStar(scenicInfo.getSightScore());
        sightEntity.setTag(scenicInfo.getSightTag());
        sightEntity.setMustGo(scenicInfo.getSightMustSee());
        sightEntity.setOpenTime(scenicInfo.getSightOpenTime());
        sightEntity.setPlayTime(scenicInfo.getSightPlayTime());
        sightEntity.setPrice(scenicInfo.getSightPrice());
        sightEntity.setLongitude(Double.valueOf(scenicInfo.getSightLongitude()));
        sightEntity.setLatitude(Double.valueOf(scenicInfo.getSightLatitude()));
        sightEntity.setAddress(scenicInfo.getSightAddress());
        sightEntity.setIntroduction(scenicInfo.getSightDiscription());
        sightEntity.setIntroductionDetail(scenicInfo.getSightDiscriptionDetail());
        sightEntity.setStartTime(scenicInfo.getSightStartTime());
        sightEntity.setEndTime(scenicInfo.getSightEndTime());
        return sightEntity;
    }

    public static HotelEntity getHotelEntity(IMBuddy.HotelInfo hotelInfo) throws ParseException {
        HotelEntity hotelEntity = new HotelEntity();
        hotelEntity.setPeerId(hotelInfo.getId());
        hotelEntity.setCityCode(hotelInfo.getCityCode());
        hotelEntity.setName(hotelInfo.getHotelName());
        hotelEntity.setPic(hotelInfo.getHotelPic());
        hotelEntity.setStar(hotelInfo.getHotelScore());
        hotelEntity.setTag(hotelInfo.getHotelTag());
        hotelEntity.setUrl(hotelInfo.getHotelUrl());
        hotelEntity.setPrice(hotelInfo.getHotelPrice());
        hotelEntity.setLongitude(Double.valueOf(hotelInfo.getHotelLongitude()));
        hotelEntity.setLatitude(Double.valueOf(hotelInfo.getHotelLatitude()));
        return hotelEntity;
    }
}
