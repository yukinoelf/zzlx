/*================================================================
*     Copyright (c) 2015年 lanhu. All rights reserved.
*   
*   文件名称：UserModel.h
*   创 建 者：Zhang Yuanhao
*   邮    箱：bluefoxah@gmail.com
*   创建日期：2015年01月05日
*   描    述：
*
#pragma once
================================================================*/
#ifndef __USERMODEL_H__
#define __USERMODEL_H__

#include "IM.BaseDefine.pb.h"
#include "IM.Buddy.pb.h"
#include "ImPduBase.h"
#include "public_define.h"
class CUserModel
{
public:
    static CUserModel* getInstance();
    ~CUserModel();
    void getChangedId(uint32_t& nLastTime, list<uint32_t>& lsIds);
    void getUsers(list<uint32_t> lsIds, list<IM::BaseDefine::UserInfo>& lsUsers);
    bool getUser(uint32_t nUserId, DBUserInfo_t& cUser);

    bool updateUser(DBUserInfo_t& cUser);
    bool insertUser(DBUserInfo_t& cUser);
//    void getUserByNick(const list<string>& lsNicks, list<IM::BaseDefine::UserInfo>& lsUsers);
    void clearUserCounter(uint32_t nUserId, uint32_t nPeerId, IM::BaseDefine::SessionType nSessionType);
    void setCallReport(uint32_t nUserId, uint32_t nPeerId, IM::BaseDefine::ClientType nClientType);

    bool updateUserSignInfo(uint32_t user_id, const string& sign_info);
    bool getUserSingInfo(uint32_t user_id, string* sign_info);
    bool updatePushShield(uint32_t user_id, uint32_t shield_status);
    bool getPushShield(uint32_t user_id, uint32_t* shield_status);
    bool getTransportTool(uint32_t user_id, IM::Buddy::GetTransportToolReq& req, IM::Buddy::GetTransportToolRsp& rsp);
    bool getScenicHotel(uint32_t user_id, IM::Buddy::GetScenicHotelReq& req, IM::Buddy::GetScenicHotelRsp& rsp);
    uint32_t createTravelDetail(uint32_t user_id, IM::Buddy::CreateMyTravelReq* pb);
	bool getRoute(string strSql,int routeNum, IM::Buddy::NewQueryRadomRouteRsp* pb);
    bool queryRadomRoute(uint32_t user_id, IM::Buddy::NewQueryRadomRouteReq* req, IM::Buddy::NewQueryRadomRouteRsp* pb);
    bool updateRadomRoute(uint32_t user_id, IM::Buddy::NewUpdateRadomRouteReq* req, IM::Buddy::NewUpdateRadomRouteRsp* pb);
    bool newCreateTravel(uint32_t user_id, IM::Buddy::NewCreateMyTravelReq* req, IM::Buddy::NewCreateMyTravelRsp* pb);
    bool createCollectRoute(uint32_t user_id, IM::Buddy::NewCreateCollectRouteReq* req, IM::Buddy::NewCreateCollectRouteRsp* pb);
    bool deleteCollectRoute(uint32_t user_id, IM::Buddy::NewDelCollectRouteReq* req, IM::Buddy::NewDelCollectRouteRsp* pb);
    bool queryCollectRoute(uint32_t user_id, IM::Buddy::NewQueryCollectRouteReq* req, IM::Buddy::NewQueryCollectRouteRsp* pb);
    bool queryTravelDetail(uint32_t user_id, IM::Buddy::QueryMyTravelRsp& rsp);
    bool deleteTravelDetail(uint32_t user_id, const set<uint32_t>& db_idx_list);
    uint32_t updateTravelDetail(uint32_t user_id, IM::Buddy::UpdateMyTravelReq* pb);
    IM::BaseDefine::ResultType modifyInfo(uint32_t user_id, uint32_t modify_type, const string& modify_context);

private:
    CUserModel();
private:
    static CUserModel* m_pInstance;
};

#endif /*defined(__USERMODEL_H__) */
