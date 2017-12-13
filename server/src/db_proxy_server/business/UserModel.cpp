/*================================================================
*     Copyright (c) 2015年 lanhu. All rights reserved.
*   
*   文件名称：UserModel.cpp
*   创 建 者：Zhang Yuanhao
*   邮    箱：bluefoxah@gmail.com
*   创建日期：2015年01月05日
*   描    述：
*
================================================================*/
#include "UserModel.h"
#include "../DBPool.h"
#include "../CachePool.h"
#include "Common.h"
#include "SyncCenter.h"


extern map<int, IM::Buddy::TravelToolInfo> travelToolMap;
extern map<int, IM::Buddy::ScenicInfo> scenicMap;
extern map<int, IM::Buddy::HotelInfo> hotelMap;

#define IN_MAP_CHECK(k, m) if (m.find(k) == m.end()) continue

int cmp(const pair<string, int>& x,const pair<string, int>& y)
{
	return x.second > y.second;
}

CUserModel* CUserModel::m_pInstance = NULL;

CUserModel::CUserModel()
{

}

CUserModel::~CUserModel()
{
    
}

CUserModel* CUserModel::getInstance()
{
    if(m_pInstance == NULL)
    {
        m_pInstance = new CUserModel();
    }
    return m_pInstance;
}

void CUserModel::getChangedId(uint32_t& nLastTime, list<uint32_t> &lsIds)
{
    CDBManager* pDBManager = CDBManager::getInstance();
    CDBConn* pDBConn = pDBManager->GetDBConn("teamtalk_slave");
    if (pDBConn)
    {
        string strSql ;
        if(nLastTime == 0)
        {
            strSql = "select id, updated from IMUser where status != 3";
        }
        else
        {
            strSql = "select id, updated from IMUser where updated>=" + int2string(nLastTime);
        }
        CResultSet* pResultSet = pDBConn->ExecuteQuery(strSql.c_str());
        if(pResultSet)
        {
            while (pResultSet->Next()) {
                uint32_t nId = pResultSet->GetInt("id");
                uint32_t nUpdated = pResultSet->GetInt("updated");
        	 if(nLastTime < nUpdated)
                {
                    nLastTime = nUpdated;
                }
                lsIds.push_back(nId);
  		}
            delete pResultSet;
        }
        else
        {
            log(" no result set for sql:%s", strSql.c_str());
        }
        pDBManager->RelDBConn(pDBConn);
    }
    else
    {
        log("no db connection for teamtalk_slave");
    }
}

void CUserModel::getUsers(list<uint32_t> lsIds, list<IM::BaseDefine::UserInfo> &lsUsers)
{
    if (lsIds.empty()) {
        log("list is empty");
        return;
    }
    CDBManager* pDBManager = CDBManager::getInstance();
    CDBConn* pDBConn = pDBManager->GetDBConn("teamtalk_slave");
    if (pDBConn)
    {
        string strClause;
        bool bFirst = true;
        for (auto it = lsIds.begin(); it!=lsIds.end(); ++it)
        {
            if(bFirst)
            {
                bFirst = false;
                strClause += int2string(*it);
            }
            else
            {
                strClause += ("," + int2string(*it));
            }
        }
        string  strSql = "select * from IMUser where id in (" + strClause + ")";
        CResultSet* pResultSet = pDBConn->ExecuteQuery(strSql.c_str());
        if(pResultSet)
        {
            while (pResultSet->Next())
            {
                IM::BaseDefine::UserInfo cUser;
                cUser.set_user_id(pResultSet->GetInt("id"));
                cUser.set_user_gender(pResultSet->GetInt("sex"));
                cUser.set_user_nick_name(pResultSet->GetString("nick"));
                cUser.set_user_domain(pResultSet->GetString("domain"));
                cUser.set_user_real_name(pResultSet->GetString("name"));
                cUser.set_user_tel(pResultSet->GetString("phone"));
                cUser.set_email(pResultSet->GetString("email"));
                cUser.set_avatar_url(pResultSet->GetString("avatar"));
		cUser.set_sign_info(pResultSet->GetString("sign_info"));
             
                cUser.set_department_id(pResultSet->GetInt("departId"));
  		 cUser.set_department_id(pResultSet->GetInt("departId"));
                cUser.set_status(pResultSet->GetInt("status"));
                lsUsers.push_back(cUser);
            }
            delete pResultSet;
        }
        else
        {
            log(" no result set for sql:%s", strSql.c_str());
        }
        pDBManager->RelDBConn(pDBConn);
    }
    else
    {
        log("no db connection for teamtalk_slave");
    }
}

bool CUserModel::getUser(uint32_t nUserId, DBUserInfo_t &cUser)
{
    bool bRet = false;
    CDBManager* pDBManager = CDBManager::getInstance();
    CDBConn* pDBConn = pDBManager->GetDBConn("teamtalk_slave");
    if (pDBConn)
    {
        string strSql = "select * from IMUser where id="+int2string(nUserId);
        CResultSet* pResultSet = pDBConn->ExecuteQuery(strSql.c_str());
        if(pResultSet)
        {
            while (pResultSet->Next())
            {
                cUser.nId = pResultSet->GetInt("id");
                cUser.nSex = pResultSet->GetInt("sex");
                cUser.strNick = pResultSet->GetString("nick");
                cUser.strDomain = pResultSet->GetString("domain");
                cUser.strName = pResultSet->GetString("name");
                cUser.strTel = pResultSet->GetString("phone");
                cUser.strEmail = pResultSet->GetString("email");
                cUser.strAvatar = pResultSet->GetString("avatar");
                cUser.sign_info = pResultSet->GetString("sign_info");
                cUser.nDeptId = pResultSet->GetInt("departId");
                cUser.nStatus = pResultSet->GetInt("status");
                bRet = true;
            }
            delete pResultSet;
        }
        else
        {
            log("no result set for sql:%s", strSql.c_str());
        }
        pDBManager->RelDBConn(pDBConn);
    }
    else
    {
        log("no db connection for teamtalk_slave");
    }
    return bRet;
}


bool CUserModel::updateUser(DBUserInfo_t &cUser)
{
    bool bRet = false;
    CDBManager* pDBManager = CDBManager::getInstance();
    CDBConn* pDBConn = pDBManager->GetDBConn("teamtalk_master");
    if (pDBConn)
    {
        uint32_t nNow = (uint32_t)time(NULL);
        string strSql = "update IMUser set `sex`=" + int2string(cUser.nSex)+ ", `nick`='" + cUser.strNick +"', `domain`='"+ cUser.strDomain + "', `name`='" + cUser.strName + "', `phone`='" + cUser.strTel + "', `email`='" + cUser.strEmail+ "', `avatar`='" + cUser.strAvatar + "', `sign_info`='" + cUser.sign_info +"', `departId`='" + int2string(cUser.nDeptId) + "', `status`=" + int2string(cUser.nStatus) + ", `updated`="+int2string(nNow) + " where id="+int2string(cUser.nId);
        bRet = pDBConn->ExecuteUpdate(strSql.c_str());
        if(!bRet)
        {
            log("updateUser: update failed:%s", strSql.c_str());
        }
        pDBManager->RelDBConn(pDBConn);
    }
    else
    {
        log("no db connection for teamtalk_master");
    }
    return bRet;
}

bool CUserModel::insertUser(DBUserInfo_t &cUser)
{
    bool bRet = false;
    CDBManager* pDBManager = CDBManager::getInstance();
    CDBConn* pDBConn = pDBManager->GetDBConn("teamtalk_master");
    if (pDBConn)
    {
        string strSql = "insert into IMUser(`id`,`sex`,`nick`,`domain`,`name`,`phone`,`email`,`avatar`,`sign_info`,`departId`,`status`,`created`,`updated`) values(?,?,?,?,?,?,?,?,?,?,?,?)";
        CPrepareStatement* stmt = new CPrepareStatement();
        if (stmt->Init(pDBConn->GetMysql(), strSql))
        {
            uint32_t nNow = (uint32_t) time(NULL);
            uint32_t index = 0;
            uint32_t nGender = cUser.nSex;
            uint32_t nStatus = cUser.nStatus;
            stmt->SetParam(index++, cUser.nId);
            stmt->SetParam(index++, nGender);
            stmt->SetParam(index++, cUser.strNick);
            stmt->SetParam(index++, cUser.strDomain);
            stmt->SetParam(index++, cUser.strName);
            stmt->SetParam(index++, cUser.strTel);
            stmt->SetParam(index++, cUser.strEmail);
            stmt->SetParam(index++, cUser.strAvatar);
            
            stmt->SetParam(index++, cUser.sign_info);
            stmt->SetParam(index++, cUser.nDeptId);
            stmt->SetParam(index++, nStatus);
            stmt->SetParam(index++, nNow);
            stmt->SetParam(index++, nNow);
            bRet = stmt->ExecuteUpdate();
            
            if (!bRet)
            {
                log("insert user failed: %s", strSql.c_str());
            }
        }
        delete stmt;
        pDBManager->RelDBConn(pDBConn);
    }
    else
    {
        log("no db connection for teamtalk_master");
    }
    return bRet;
}

void CUserModel::clearUserCounter(uint32_t nUserId, uint32_t nPeerId, IM::BaseDefine::SessionType nSessionType)
{
    if(IM::BaseDefine::SessionType_IsValid(nSessionType))
    {
        CacheManager* pCacheManager = CacheManager::getInstance();
        CacheConn* pCacheConn = pCacheManager->GetCacheConn("unread");
        if (pCacheConn)
        {
            // Clear P2P msg Counter
            if(nSessionType == IM::BaseDefine::SESSION_TYPE_SINGLE)
            {
                int nRet = pCacheConn->hdel("unread_" + int2string(nUserId), int2string(nPeerId));
                if(!nRet)
                {
                    log("hdel failed %d->%d", nPeerId, nUserId);
                }
            }
            // Clear Group msg Counter
            else if(nSessionType == IM::BaseDefine::SESSION_TYPE_GROUP)
            {
                string strGroupKey = int2string(nPeerId) + GROUP_TOTAL_MSG_COUNTER_REDIS_KEY_SUFFIX;
                map<string, string> mapGroupCount;
                bool bRet = pCacheConn->hgetAll(strGroupKey, mapGroupCount);
                if(bRet)
                {
                    string strUserKey = int2string(nUserId) + "_" + int2string(nPeerId) + GROUP_USER_MSG_COUNTER_REDIS_KEY_SUFFIX;
                    string strReply = pCacheConn->hmset(strUserKey, mapGroupCount);
                    if(strReply.empty()) {
                        log("hmset %s failed !", strUserKey.c_str());
                    }
                }
                else
                {
                    log("hgetall %s failed!", strGroupKey.c_str());
                }
                
            }
            pCacheManager->RelCacheConn(pCacheConn);
        }
        else
        {
            log("no cache connection for unread");
        }
    }
    else{
        log("invalid sessionType. userId=%u, fromId=%u, sessionType=%u", nUserId, nPeerId, nSessionType);
    }
}

void CUserModel::setCallReport(uint32_t nUserId, uint32_t nPeerId, IM::BaseDefine::ClientType nClientType)
{
    if(IM::BaseDefine::ClientType_IsValid(nClientType))
    {
        CDBManager* pDBManager = CDBManager::getInstance();
        CDBConn* pDBConn = pDBManager->GetDBConn("teamtalk_master");
        if(pDBConn)
        {
            string strSql = "insert into IMCallLog(`userId`, `peerId`, `clientType`,`created`,`updated`) values(?,?,?,?,?)";
            CPrepareStatement* stmt = new CPrepareStatement();
            if (stmt->Init(pDBConn->GetMysql(), strSql))
            {
                uint32_t nNow = (uint32_t) time(NULL);
                uint32_t index = 0;
                uint32_t nClient = (uint32_t) nClientType;
                stmt->SetParam(index++, nUserId);
                stmt->SetParam(index++, nPeerId);
                stmt->SetParam(index++, nClient);
                stmt->SetParam(index++, nNow);
                stmt->SetParam(index++, nNow);
                bool bRet = stmt->ExecuteUpdate();
                
                if (!bRet)
                {
                    log("insert report failed: %s", strSql.c_str());
                }
            }
            delete stmt;
            pDBManager->RelDBConn(pDBConn);
        }
        else
        {
            log("no db connection for teamtalk_master");
        }
        
    }
    else
    {
        log("invalid clienttype. userId=%u, peerId=%u, clientType=%u", nUserId, nPeerId, nClientType);
    }
}


bool CUserModel::updateUserSignInfo(uint32_t user_id, const string& sign_info) {
   
    if (sign_info.length() > 128) {
        log("updateUserSignInfo: sign_info.length()>128.\n");
        return false;
    }
    bool rv = false;
    CDBManager* db_manager = CDBManager::getInstance();
    CDBConn* db_conn = db_manager->GetDBConn("teamtalk_master");
    if (db_conn) {
        uint32_t now = (uint32_t)time(NULL);
        string str_sql = "update IMUser set `sign_info`='" + sign_info + "', `updated`=" + int2string(now) + " where id="+int2string(user_id);
        rv = db_conn->ExecuteUpdate(str_sql.c_str());
        if(!rv) {
            log("updateUserSignInfo: update failed:%s", str_sql.c_str());
        }else{
                CSyncCenter::getInstance()->updateTotalUpdate(now);
           
        }
        db_manager->RelDBConn(db_conn);
        } else {
            log("updateUserSignInfo: no db connection for teamtalk_master");
            }
    return rv;
    }

bool CUserModel::getUserSingInfo(uint32_t user_id, string* sign_info) {
    bool rv = false;
    CDBManager* db_manager = CDBManager::getInstance();
    CDBConn* db_conn = db_manager->GetDBConn("teamtalk_slave");
    if (db_conn) {
        string str_sql = "select sign_info from IMUser where id="+int2string(user_id);
        CResultSet* result_set = db_conn->ExecuteQuery(str_sql.c_str());
        if(result_set) {
            if (result_set->Next()) {
                *sign_info = result_set->GetString("sign_info");
                rv = true;
                }
            delete result_set;
            } else {
                        log("no result set for sql:%s", str_sql.c_str());
                   }
                db_manager->RelDBConn(db_conn);
        } else {
                    log("no db connection for teamtalk_slave");
               }
    return rv;
   }

bool CUserModel::updatePushShield(uint32_t user_id, uint32_t shield_status) {
    bool rv = false;
    
    CDBManager* db_manager = CDBManager::getInstance();
    CDBConn* db_conn = db_manager->GetDBConn("teamtalk_master");
    if (db_conn) {
        uint32_t now = (uint32_t)time(NULL);
        string str_sql = "update IMUser set `push_shield_status`="+ int2string(shield_status) + ", `updated`=" + int2string(now) + " where id="+int2string(user_id);
        rv = db_conn->ExecuteUpdate(str_sql.c_str());
        if(!rv) {
            log("updatePushShield: update failed:%s", str_sql.c_str());
        }
        db_manager->RelDBConn(db_conn);
    } else {
        log("updatePushShield: no db connection for teamtalk_master");
    }
    
    return rv;
}

bool CUserModel::getPushShield(uint32_t user_id, uint32_t* shield_status) {
    bool rv = false;
    
    CDBManager* db_manager = CDBManager::getInstance();
    CDBConn* db_conn = db_manager->GetDBConn("teamtalk_slave");
    if (db_conn) {
        string str_sql = "select push_shield_status from IMUser where id="+int2string(user_id);
        CResultSet* result_set = db_conn->ExecuteQuery(str_sql.c_str());
        if(result_set) {
            if (result_set->Next()) {
                *shield_status = result_set->GetInt("push_shield_status");
                rv = true;
            }
            delete result_set;
        } else {
            log("getPushShield: no result set for sql:%s", str_sql.c_str());
        }
        db_manager->RelDBConn(db_conn);
    } else {
        log("getPushShield: no db connection for teamtalk_slave");
    }
    
    return rv;
}

bool CUserModel::getTransportTool(uint32_t user_id, IM::Buddy::GetTransportToolReq& req, IM::Buddy::GetTransportToolRsp& rsp) {
    log("enter.");
    bool ret = false;
    CDBManager* pDBManager = CDBManager::getInstance();
    CDBConn* pDBConn = pDBManager->GetDBConn("teamtalk_master");
    if (pDBConn)
    {
        string type;
        int tools[] = {IM::Buddy::TRAIN, IM::Buddy::AIRPLANE, IM::Buddy::BUS};
        int tool_type = req.transport_config().tool_type();
        for (int i = 0; i < sizeof(tools) / sizeof(tools[0]); ++i) {
            if (tool_type & tools[i] == tools[i]) {
                type += int2string(IM::Buddy::TRAIN) + ",";
            }
        }
        type = type.substr(0, type.length() - 1);
        
        string  strSql = "select * from IMTravelTool where type in(" + type 
            + ") and ((placeFromCode='" + req.basic_info().place_from_code() 
            + "' and placeToCode='" + req.basic_info().place_to_code() 
            + "') or (placeFromCode='" + req.basic_info().place_to_code() 
            + "' and placeToCode='" + req.basic_info().place_back_code()
            + "')) and timeFrom>'" + req.transport_config().time_from() 
            + "' and timeTo<'" + req.transport_config().time_to() 
            + "' order by placeFromCode, placeToCode, type, timeFrom, price";
        log("sql:%s", strSql.c_str());
        
        CResultSet* pResultSet = pDBConn->ExecuteQuery(strSql.c_str());
        if(pResultSet)
        {
            while (pResultSet->Next())
            {
                IM::Buddy::TravelToolInfo* pTravelToolInfo = rsp.add_travel_tool_info();
                if (nullptr == pTravelToolInfo) { continue; }

                pTravelToolInfo->set_id(pResultSet->GetInt("id"));
                pTravelToolInfo->set_transport_tool_type(pResultSet->GetInt("type"));
                pTravelToolInfo->set_no(pResultSet->GetString("no"));
                pTravelToolInfo->set_place_from_code(pResultSet->GetString("place_from_code"));
                pTravelToolInfo->set_place_from(pResultSet->GetString("place_from"));
                pTravelToolInfo->set_place_to_code(pResultSet->GetString("place_to_code"));
                pTravelToolInfo->set_place_to(pResultSet->GetString("place_to"));
                pTravelToolInfo->set_time_from(pResultSet->GetString("time_start"));
                pTravelToolInfo->set_time_to(pResultSet->GetString("time_end"));
                pTravelToolInfo->set_class_(pResultSet->GetString("class"));
                pTravelToolInfo->set_price(pResultSet->GetInt("price"));
            }
            delete pResultSet;
        }
        else
        {
            log(" no result set for sql:%s", strSql.c_str());
        }
        pDBManager->RelDBConn(pDBConn);
        ret = true;
    }
    else
    {
        log("no db connection for teamtalk_master");
    }

    return ret;
}

bool CUserModel::getScenicHotel(uint32_t user_id, IM::Buddy::GetScenicHotelReq& req, IM::Buddy::GetScenicHotelRsp& rsp) {
    log("enter.");
    bool ret = false;
    CDBManager* pDBManager = CDBManager::getInstance();
    CDBConn* pDBConn = pDBManager->GetDBConn("teamtalk_master");
    if (pDBConn)
    {
        string  strSql = "select * from Sight where cityCode='" + req.city_code() + "' order by sightMustSee desc, sightScore desc";
        log("sql:%s", strSql.c_str());
        
        CResultSet* pResultSet = pDBConn->ExecuteQuery(strSql.c_str());
        if(pResultSet)
        {
            while (pResultSet->Next())
            {
                IM::Buddy::ScenicInfo* pScenicInfo = rsp.add_scenic_info();
                if (nullptr == pScenicInfo) { continue; }

                pScenicInfo->set_id(pResultSet->GetInt("id"));
                pScenicInfo->set_city_code(pResultSet->GetString("cityCode"));	
                pScenicInfo->set_sightname(pResultSet->GetString("sightName"));
                pScenicInfo->set_sightstatus(pResultSet->GetInt("sightStatus"));
                pScenicInfo->set_sightopentime(pResultSet->GetString("sightOpenTime"));
                pScenicInfo->set_sightaddress(pResultSet->GetString("sightAddress"));
                pScenicInfo->set_sightdiscription(pResultSet->GetString("sightDiscription"));
                pScenicInfo->set_sightdiscriptiondetail(pResultSet->GetString("sightDiscriptionDetail"));
                pScenicInfo->set_sightstarttime(pResultSet->GetString("sightStartTime"));
                pScenicInfo->set_sightendtime(pResultSet->GetString("sightEndTime"));
                pScenicInfo->set_sightlatitude(pResultSet->GetString("sightLatitude"));
                pScenicInfo->set_sightlongitude(pResultSet->GetString("sightLongitude"));
                pScenicInfo->set_sightmustsee(pResultSet->GetInt("sightMustSee"));
                pScenicInfo->set_sightpic(pResultSet->GetString("sightPic"));
                pScenicInfo->set_sightplaytime(pResultSet->GetInt("sightPlayTime"));
                pScenicInfo->set_sightprice(pResultSet->GetInt("sightPrice"));
                pScenicInfo->set_sightscore(pResultSet->GetInt("sightScore"));
                pScenicInfo->set_sighttag(pResultSet->GetString("sightTag"));
                pScenicInfo->set_sighturl(pResultSet->GetString("sightUrl"));
                pScenicInfo->set_literature(pResultSet->GetInt("literature"));
                pScenicInfo->set_comfort(pResultSet->GetInt("comfort"));
                pScenicInfo->set_encounter(pResultSet->GetInt("encounter"));
                pScenicInfo->set_excite(pResultSet->GetInt("excite"));
                pScenicInfo->set_exploration(pResultSet->GetInt("exploration"));
            }
            delete pResultSet;
        }
        else
        {
            log(" no result set for sql:%s", strSql.c_str());
        }

        strSql = "select * from Hotel where cityCode='" + req.city_code() + "' order by hotelScore desc";
        log("sql:%s", strSql.c_str());
        pResultSet = pDBConn->ExecuteQuery(strSql.c_str());
        if(pResultSet)
        {
            while (pResultSet->Next())
            {
                IM::Buddy::HotelInfo* pHotelInfo = rsp.add_hotel_info();
                if (nullptr == pHotelInfo) { continue; }

                pHotelInfo->set_id(pResultSet->GetInt("id"));
                pHotelInfo->set_city_code(pResultSet->GetString("cityCode"));
                pHotelInfo->set_hotellatitude(pResultSet->GetString("hotelLatitude"));
                pHotelInfo->set_hotellongitude(pResultSet->GetString("hotelLongitude"));
                pHotelInfo->set_hotelname(pResultSet->GetString("hotelName"));
                pHotelInfo->set_hotelpic(pResultSet->GetString("hotelPic"));
                pHotelInfo->set_hotelscore(pResultSet->GetInt("hotelScore"));
                pHotelInfo->set_hoteltag(pResultSet->GetString("hotelTag"));
                pHotelInfo->set_hotelurl(pResultSet->GetString("hotelUrl"));
                pHotelInfo->set_hotelprice(pResultSet->GetInt("hotelPrice"));
            }
            delete pResultSet;
        }
        else
        {
            log(" no result set for sql:%s", strSql.c_str());
        }

        
        pDBManager->RelDBConn(pDBConn);
        ret = true;
    }
    else
    {
        log("no db connection for teamtalk_master");
    }

    return ret;
}

IM::BaseDefine::ResultType CUserModel::modifyInfo(uint32_t user_id, uint32_t modify_type, const string& modify_context){
	CDBManager* db_manager = CDBManager::getInstance();
	CDBConn* db_conn = db_manager->GetDBConn("teamtalk_master");
    log("CUserModel::modifyInfo %d", modify_type);
	if(db_conn){
		string str_sql;
		switch(modify_type){
			case IM::Buddy::NICK :
			{
				str_sql = "UPDATE IMUser SET nick = '" + modify_context + "' WHERE id = " + int2string(user_id);
				break;
			}
            case IM::Buddy::AVATAR :
			{
				str_sql = "UPDATE IMUser SET avatar = '" + modify_context + "' WHERE id = " + int2string(user_id);
				break;
			}
            case IM::Buddy::SEX :
			{
				str_sql = "UPDATE IMUser SET sex = '" + modify_context + "' WHERE id = " + int2string(user_id);
				break;
			}
            case IM::Buddy::ENCOUNTER :
			{
				str_sql = "UPDATE IMUser SET departId = '" + modify_context + "' WHERE id = " + int2string(user_id);
				break;
			}
            case IM::Buddy::HOMELAND :
			{
				str_sql = "UPDATE IMUser SET email = '" + modify_context + "' WHERE id = " + int2string(user_id);
				break;
			}
            case IM::Buddy::SIGN :
			{
				str_sql = "UPDATE IMUser SET sign_info = '" + modify_context + "' WHERE id = " + int2string(user_id);
				break;
            }
            case IM::Buddy::PHONE :
			{
				str_sql = "UPDATE IMUser SET phone = '" + modify_context + "' WHERE id = " + int2string(user_id);
				break;
			}
			default:
			{
				db_manager->RelDBConn(db_conn);
				return IM::BaseDefine::ResultType::REFUSE_REASON_UNDEFINE_REQUEST;
			}
		}
		bool rv = db_conn->ExecuteUpdate(str_sql.c_str());
		if(rv) {
			db_manager->RelDBConn(db_conn);
			return IM::BaseDefine::ResultType::REFUSE_REASON_NONE;
		}else{
			log("modifyInfo: no result set for sql:%s", str_sql.c_str());
		}
		db_manager->RelDBConn(db_conn);
	}else{
        log("modifyInfo: no db connection for teamtalk_slave");
	}
    return IM::BaseDefine::ResultType::REFUSE_REASON_DB_OP_FAILED;
}

namespace{
    string string_fmt(const string& str, const char* format, ...) {
        char szBuffer[20480] = {0};
        va_list args;
        va_start(args, format);
        vsnprintf(szBuffer, sizeof(szBuffer), format, args);
        va_end(args);
        return szBuffer;
    }
}



uint32_t CUserModel::createTravelDetail(uint32_t user_id, IM::Buddy::CreateMyTravelReq* pb) {
    log("enter.");
    bool bRet = false;
    uint32_t idx = 0;
    CDBManager* pDBManager = CDBManager::getInstance();
    CDBConn* pDBConn = pDBManager->GetDBConn("teamtalk_master");
    if (pDBConn)
    {
        IM::Buddy::CreateMyTravelReq* req = (IM::Buddy::CreateMyTravelReq*)pb;
        string places;
        int size = req->my_travel().travel_detail().play_detail().day_hotel_size();
    	for (int n = 0; n < size; ++n) {            
            const IM::Buddy::DayHotel& hotel = req->my_travel().travel_detail().play_detail().day_hotel(n);
            string tmp;
            tmp = string_fmt(tmp, "%d&%d&%s&%s", 1, hotel.hotel_info().id(), hotel.daytimefrom().c_str(), hotel.daytimeto().c_str());

            if (n == size - 1) {
                places = places + tmp;
            }
            else {
                places = places + tmp + "|";
            }
    	}

        size = req->my_travel().travel_detail().play_detail().day_scenic_size();
    	for (int n = 0; n < size; ++n) {            
            const IM::Buddy::DayScenic& scenic = req->my_travel().travel_detail().play_detail().day_scenic(n);
            string tmp;
            tmp = string_fmt(tmp, "%d&%d&%s&%s", 1, scenic.scenic_info().id(), scenic.daytimefrom().c_str(), scenic.daytimeto().c_str());

            if ((n == 0) && (!places.empty())) {
                places += "|";
            }
            
            if (n == size - 1) {
                places = places + tmp;
            }
            else {
                places = places + tmp + "|";
            }
    	}
        
        
        string strSql;
        strSql = string_fmt(strSql, "call insert_or_update_my_travel(%d, %d, %d, '%s', '%s', '%s', %d, '%s', '%s', %d, '%s', '%s', %d, %d, %d, %d, %d, '%s', '%s', %d, %d, '%s', @ret, @idx)", 
            req->user_id(),
            req->my_travel().db_idx(),
            req->my_travel().cost(),
            req->my_travel().basic_info().date_from().c_str(),
            req->my_travel().basic_info().date_to().c_str(),
            req->my_travel().basic_info().place_to_code().c_str(),
            req->my_travel().basic_info().person_num(),
            req->my_travel().basic_info().place_from_code().c_str(),
            req->my_travel().basic_info().place_back_code().c_str(),
            req->my_travel().transport_config().tool_type(),
            req->my_travel().transport_config().time_from().c_str(),
            req->my_travel().transport_config().time_to().c_str(),
            req->my_travel().transport_config().quality(),
            req->my_travel().transport_config().transit(),
            req->my_travel().travel_detail().transport_tool().from_info().id(),
            req->my_travel().travel_detail().transport_tool().back_info().id(),
            req->my_travel().travel_detail().play_detail().play_config().quality(),
            req->my_travel().travel_detail().play_detail().play_config().time_from().c_str(),
            req->my_travel().travel_detail().play_detail().play_config().time_to().c_str(),
            req->my_travel().travel_detail().play_detail().play_config().transport_tool_type(),
            req->my_travel().travel_detail().play_detail().play_config().position(),
            places.c_str()
            );
        log("sql:%s", strSql.c_str());
        CResultSet* pResultSet = pDBConn->ExecuteQuery(strSql.c_str());
        if (pResultSet)
        {
            while (pResultSet->Next())
            {
                int ret = pResultSet->GetInt("ret");
                if (0 == ret) {
                    idx = pResultSet->GetInt("newId");
                }
            }
            delete pResultSet;
        }
        else
        {
            log(" no result set for sql:%s", strSql.c_str());
        }
        
    }
    else
    {
        log("no db connection for teamtalk_master");
    }
    return idx;
}

bool CUserModel::getRoute(string strSql,int routeNum, IM::Buddy::NewQueryRadomRouteRsp* pb){
	
    log("entry");
	bool bRet = false;
    CDBManager* pDBManager = CDBManager::getInstance();
    CDBConn* pDBConn = pDBManager->GetDBConn("teamtalk_master");
    if (!pDBConn)
    {
        log("no db connection for teamtalk");
        return false;
    }

    CResultSet* pResultSet = NULL;

    log("sql = %s", strSql.c_str());
    int i = 0;


    pResultSet = pDBConn->ExecuteQuery(strSql.c_str());
    if (pResultSet)
    {
        IM::Buddy::Route *route = NULL;
        IM::Buddy::DayRoute* dayRoute = NULL;
        int lineId = 0;
        int day_num = 0;
        int i = 0;
        log("pResultSet");

        while (pResultSet->Next())
        {
            if (i > routeNum)
            {
                log("routeNum = %d", routeNum);
                break;
            }

            if (lineId == 0 || pResultSet->GetInt("lineId") != lineId)
            {
                route = pb->add_routes();
                i++;
            }

            lineId = pResultSet->GetInt("lineId");
            log("lineId = %d", lineId);
            route->set_id(pResultSet->GetInt("lineId"));
            route->set_day_count(pResultSet->GetInt("dayCount"));
            route->set_city_code(pResultSet->GetString("cityCode"));
            route->add_tag(pResultSet->GetString("quality"));
            route->set_start_transport_tool((::IM::Buddy::TransportToolType)pResultSet->GetInt("startTool"));
            route->set_end_transport_tool((::IM::Buddy::TransportToolType)pResultSet->GetInt("endTool"));
            route->set_start_time(pResultSet->GetString("startTime"));
            route->set_end_time(pResultSet->GetString("endTime"));
            dayRoute = route->add_day_routes();
            bRet = true;
            
            char *p;
            const char* sep = " ";
            p = strtok(pResultSet->GetString("routes"), sep);
            while(p){
                dayRoute->add_scenics(atoi(p));
                p = strtok(NULL, sep);
            }

            p = strtok(pResultSet->GetString("hotels"), sep);
            while(p){
                dayRoute->add_hotels(atoi(p));
                p = strtok(NULL, sep);
            }
        }
        delete pResultSet;
    }

    pDBManager->RelDBConn(pDBConn);
	return bRet;
}

bool CUserModel::queryRadomRoute(uint32_t user_id, IM::Buddy::NewQueryRadomRouteReq* req,  IM::Buddy::NewQueryRadomRouteRsp* pb) {
    log("enter queryRadomRoute.");
    bool bRet = false;

	if (req->tags_size() != 3)
	{
		log("tag num insufficient!");
		return false;
	}
	// guard against same tag and undefined tag
	map<string, int> temp_tag,used_tag;
	vector<pair<string,int>> final_tag;
	int route_num[3] = {3,2,2};
	temp_tag["舒适"] = 1;
	temp_tag["文艺"] = 2;
	temp_tag["刺激"] = 3;
	temp_tag["探险"] = 4;
	temp_tag["艳遇"] = 5;
	map<string, int>::iterator it;
	
	for (int i = 0; i < req->tags_size(); i++)
	{
		it = temp_tag.find(req->tags(i));
		if (it != temp_tag.end())
		{
			used_tag[it->first] = route_num[i];
			temp_tag.erase(it);
		}
		else
		{
			it = temp_tag.begin();
			used_tag[it->first] = route_num[i];
			temp_tag.erase(it);
		}
	}

    map<string, vector<string> > tag_type;
    tag_type["毕业"] = {"文艺", "舒适", "探险"};
    tag_type["辞职"] = {"舒适", "文艺", "刺激"};
    tag_type["散心"] = {"舒适", "文艺", "无"};
    tag_type["分手"] = {"舒适", "文艺", "刺激"};
    tag_type["一起"] = {"文艺", "刺激", "文艺"};
    tag_type["结婚"] = {"舒适", "文艺", "无"};
    tag_type["低落"] = {"舒适", "文艺", "刺激"};
    tag_type["美好的记忆"] = {"舒适", "文艺", "无"};
    tag_type["灵魂"] = {"文艺", "探险", "刺激"};
    tag_type["思念"] = {"文艺", "舒适", "无"};
    tag_type["心扉"] = {"文艺", "探险", "刺激"};
    tag_type["快乐"] = {"舒适", "文艺", "探险"};
    tag_type["梦想"] = {"文艺", "探险", "刺激"};
    tag_type["记忆"] = {"文艺", "舒适", "探险"};
    tag_type["刻骨铭心"] = {"文艺", "探险", "无"};
    tag_type["一个人"] = {"无", "无", "无"};
    tag_type["陌生"] = {"文艺", "探险", "无"};
    tag_type["放松"] = {"文艺", "舒适", "无"};
    tag_type["感伤"] = {"文艺", "舒适", "探险"};
    tag_type["伤感"] = {"文艺", "舒适", "探险"};
    tag_type["美"] = {"文艺", "舒适", "探险"};
    tag_type["青春"] = {"文艺", "舒适", "探险"};
    tag_type["人生"] = {"文艺", "舒适", "探险"};
    tag_type["慢慢"] = {"文艺", "舒适", "无"};
    tag_type["幸福"] = {"舒适", "文艺", "无"};
    tag_type["期待"] = {"文艺", "舒适", "无"};
    tag_type["生命"] = {"文艺", "舒适", "探险"};
    tag_type["钱"] = {"穷游", "文艺", "探险"};
    tag_type["惆怅"] = {"文艺", "舒适", "探险"};
    tag_type["失去"] = {"舒适", "探险", "刺激"};
    tag_type["未知"] = {"穷游", "探险", "文艺"};
    tag_type["冲动"] = {"文艺", "舒适", "探险"};
    tag_type["痛快"] = {"穷游", "探险", "无"};
    tag_type["痛痛快快"] = {"刺激", "探险", "无"};
    tag_type["不快乐"] = {"文艺", "舒适", "无"};
    tag_type["回忆"] = {"文艺", "舒适", "无"};
    tag_type["不好"] = {"舒适", "文艺", "无"};
    tag_type["忘掉"] = {"文艺", "舒适", "探险"};
    tag_type["烦恼"] = {"舒适", "探险", "刺激"};
    tag_type["逃避"] = {"文艺", "舒适", "探险"};
    tag_type["艳遇"] = {"艳遇", "文艺", "探险"};
    tag_type["计划"] = {"文艺", "舒适", "探险"};
    tag_type["哭"] = {"舒适", "文艺", "艳遇"};
    tag_type["单"] = {"文艺", "艳遇", "无"};
    tag_type["忧伤"] = {"文艺", "舒适", "无"};
    tag_type["留恋"] = {"文艺", "舒适", "无"};
    tag_type["流浪"] = {"探险", "文艺", "艳遇"};
    tag_type["沿途"] = {"文艺", "舒适", "无"};
    tag_type["远方"] = {"探险", "穷游", "无"};
    tag_type["遗忘"] = {"文艺", "舒适", "探险"};
    tag_type["温和"] = {"舒适", "文艺", "无"};
    tag_type["火车"] = {"探险", "文艺", "艳遇"};
    tag_type["舒适"] = {"舒适", "文艺", "无"};
    tag_type["烟"] = {"探险", "文艺", "艳遇"};
    tag_type["飘"] = {"探险", "文艺", "艳遇"};
    tag_type["婚姻"] = {"文艺", "舒适", "探险"};
    tag_type["释放"] = {"刺激", "探险", "舒适"};

    if (req->sentence().length() != 0)
    {//将句子中对应词语的tag按照 5 3 1积分制累加进入 used_tag
        for (auto iter : tag_type)
        {
            if (strstr(req->sentence().c_str(), iter.first.c_str()))
            {
				for(int i = 0; i < iter.second.size(); i ++)
				{
					it = used_tag.find((iter.second)[i]);
					if (it != used_tag.end())
					{
						used_tag[(iter.second)[i]] = it->second + route_num[i];
					}
					else
					{
						used_tag[(iter.second)[i]] = route_num[i];
					}
				}
            }
        }
    }

	for (it = used_tag.begin();it != used_tag.end(); it++)
	{
		final_tag.push_back(make_pair(it->first,it->second));
	}
	sort(final_tag.begin(),final_tag.end(),cmp);//对最后需要使用的tag降序排列
	//假如前三个有 "无" 这个无意义的标签，就将其删除下一个替换上来
	for (int i = 0; i < 3; i++)
	{
		if (final_tag[i].first.compare("无"))
		{
			final_tag.erase(final_tag.begin()+i);
			break;
		}
		
	}

	for (int i = 0; i < 3; i++)
	{
		string strSql = "select a.* from IMRoute a right join (select lineId from IMRoute where quality = '" + final_tag[i].first + "' group by lineId  order by rand() limit " + int2string(route_num[i]) + ") b  on b.lineId = a.lineId;";
		bRet =getRoute(strSql,route_num[i], pb);
	}

    log("exit.");
    return bRet;
}

bool CUserModel::updateRadomRoute(uint32_t user_id, IM::Buddy::NewUpdateRadomRouteReq* req, IM::Buddy::NewUpdateRadomRouteRsp* pb) {
    log("enter.");
    bool bRet = true;

    CDBManager* pDBManager = CDBManager::getInstance();
    CDBConn* pDBConn = pDBManager->GetDBConn("teamtalk_master");
    CacheManager* pCacheManager = CacheManager::getInstance();
    CacheConn* pCacheConn = pCacheManager->GetCacheConn("pubsub");
    if (!pDBConn)
    {
        log("no db connection for teamtalk");
        return false;
    }

    if (!pCacheConn)
    {
        log("no cache connection for teamtalk");
        pDBManager->RelDBConn(pDBConn);
        return false;
    }
    
    string tmp;
    string scenicList;

    for (int i = 0; i < req->scenic_ids_size(); ++i)
    {
        scenicList += int2string(req->scenic_ids(i)) + " ";
    }
    

    tmp = string_fmt(tmp, "{'cmd':'create', 'userId':%d, 'cityCode':'%s', 'dayCount':%d, 'tags':'%s', 'startTool':%d, 'endTool':%d, 'startTime':'%s', 'endTime':'%s', 'scenicList':'%s'}", user_id, req->city_code().c_str(), req->day_count(), req->tag().c_str(), req->start_transport_tool(), req->end_transport_tool(), req->start_time().c_str(), req->end_time().c_str(), scenicList.c_str());
    long ret = pCacheConn->pub("route", tmp);
    if (-1 == ret)
    {
        log("failed to pCacheConn->pub");
        pDBManager->RelDBConn(pDBConn);
        pCacheManager->RelCacheConn(pCacheConn);
        return false;
    }

    CResultSet* pResultSet = NULL;
    string strSql = "select * from IMRoute where status=0 and userId=" + int2string(user_id) + " order by dayNum";
    log("strSql:%s", strSql.c_str());
    int i = 0;
    bool data_exist = false;

    while (1)
    {
        pResultSet = pDBConn->ExecuteQuery(strSql.c_str());
        if (pResultSet)
        {
            IM::Buddy::Route *route = NULL;
            IM::Buddy::DayRoute* dayRoute = NULL;

            while (pResultSet->Next())
            {
                data_exist = true;
                route = pb->mutable_route();
                route->set_id(pResultSet->GetInt("lineId"));
                route->set_day_count(pResultSet->GetInt("dayCount"));
                route->set_city_code(pResultSet->GetString("cityCode"));
                route->add_tag(pResultSet->GetString("quality"));
                route->set_start_transport_tool((::IM::Buddy::TransportToolType)pResultSet->GetInt("startTool"));
                route->set_end_transport_tool((::IM::Buddy::TransportToolType)pResultSet->GetInt("endTool"));
                route->set_start_time(pResultSet->GetString("startTime"));
                route->set_end_time(pResultSet->GetString("endTime"));
                dayRoute = route->add_day_routes();
                
                char *p;
                const char* sep = " ";
                p = strtok(pResultSet->GetString("routes"), sep);
                while(p){
                    dayRoute->add_scenics(atoi(p));
                    p = strtok(NULL, sep);
                }

                p = strtok(pResultSet->GetString("hotels"), sep);
                while(p){
                    dayRoute->add_hotels(atoi(p));
                    p = strtok(NULL, sep);
                }
            }

            delete pResultSet;
        }

        if (data_exist)
        {
            bRet = true;
            break;
        }

        log("no data find, try again. %d", i++);
        if (i >= 100)
        {
            bRet = false;
            break;
        }

        sleep(1);
    }

    tmp = string_fmt(tmp, "{'cmd':'finish', 'userId':%d}", user_id);
    pCacheConn->pub("route", tmp);

    pDBManager->RelDBConn(pDBConn);
    pCacheManager->RelCacheConn(pCacheConn);

    return bRet;
}

bool CUserModel::newCreateTravel(uint32_t user_id, IM::Buddy::NewCreateMyTravelReq* req, IM::Buddy::NewCreateMyTravelRsp* pb) {
    log("enter.");
    bool bRet = true;

    CDBManager* pDBManager = CDBManager::getInstance();
    CDBConn* pDBConn = pDBManager->GetDBConn("teamtalk_master");
    CacheManager* pCacheManager = CacheManager::getInstance();
    CacheConn* pCacheConn = pCacheManager->GetCacheConn("pubsub");
    if (!pDBConn)
    {
        log("no db connection for teamtalk");
        return false;
    }

    if (!pCacheConn)
    {
        log("no cache connection for teamtalk");
        pDBManager->RelDBConn(pDBConn);
        return false;
    }

    string tags;
    for (int i = 0; i < req->tags_size(); ++i)
    {
        tags += req->tags(i) + " ";
    }

    string tmp;
    tmp = string_fmt(tmp, "{'cmd':'create', 'userId':%d, 'cityCode':'%s', 'dayCount':%d, 'tags':'%s'}", user_id, req->city_code().c_str(), req->day_count(), tags.c_str());
    long ret = pCacheConn->pub("route", tmp);
    if (-1 == ret)
    {
        log("failed to pCacheConn->pub");
        pDBManager->RelDBConn(pDBConn);
        pCacheManager->RelCacheConn(pCacheConn);
        return false;
    }

    CResultSet* pResultSet = NULL;
    string strSql = "select * from IMRoute where status=0 and userId=" + int2string(user_id) + " order by dayNum";
    log("strSql:%s", strSql.c_str());
    int i = 0;
    bool data_exist = false;

    while (1)
    {
        
        pResultSet = pDBConn->ExecuteQuery(strSql.c_str());
        if (pResultSet)
        {
            IM::Buddy::Route *route = NULL;
            IM::Buddy::DayRoute* dayRoute = NULL;

            while (pResultSet->Next())
            {
                data_exist = true;
                route = pb->mutable_route();
                route->set_id(pResultSet->GetInt("lineId"));
                route->set_day_count(pResultSet->GetInt("dayCount"));
                route->set_city_code(pResultSet->GetString("cityCode"));
                route->add_tag(pResultSet->GetString("quality"));
                route->set_start_transport_tool((::IM::Buddy::TransportToolType)pResultSet->GetInt("startTool"));
                route->set_end_transport_tool((::IM::Buddy::TransportToolType)pResultSet->GetInt("endTool"));
                route->set_start_time(pResultSet->GetString("startTime"));
                route->set_end_time(pResultSet->GetString("endTime"));
                dayRoute = route->add_day_routes();
                
                char *p;
                const char* sep = " ";
                p = strtok(pResultSet->GetString("routes"), sep);
                while(p){
                    dayRoute->add_scenics(atoi(p));
                    p = strtok(NULL, sep);
                }

                p = strtok(pResultSet->GetString("hotels"), sep);
                while(p){
                    dayRoute->add_hotels(atoi(p));
                    p = strtok(NULL, sep);
                }
            }

            delete pResultSet;
        }

        if (data_exist)
        {
            bRet = true;
            break;
        }

        log("no data find, try again. %d", i++);
        if (i >= 100)
        {
            bRet = false;
            break;
        }

        sleep(1);
    }

    tmp = string_fmt(tmp, "{'cmd':'finish', 'userId':%d}", user_id);
    pCacheConn->pub("route", tmp);

    pDBManager->RelDBConn(pDBConn);
    pCacheManager->RelCacheConn(pCacheConn);

    log("exit.");
    return bRet;
}


uint32_t CUserModel::updateTravelDetail(uint32_t user_id, IM::Buddy::UpdateMyTravelReq* pb) {
    log("enter.");
    bool bRet = false;
    uint32_t idx = 0;
    CDBManager* pDBManager = CDBManager::getInstance();
    CDBConn* pDBConn = pDBManager->GetDBConn("teamtalk_master");
    if (pDBConn)
    {
        IM::Buddy::UpdateMyTravelReq* req = (IM::Buddy::UpdateMyTravelReq*)pb;
        string places;
        int size = req->my_travel().travel_detail().play_detail().day_hotel_size();
    	for (int n = 0; n < size; ++n) {            
            const IM::Buddy::DayHotel& hotel = req->my_travel().travel_detail().play_detail().day_hotel(n);
            string tmp;
            tmp = string_fmt(tmp, "%d&%d&%s&%s", 1, hotel.hotel_info().id(), hotel.daytimefrom().c_str(), hotel.daytimeto().c_str());

            if (n == size - 1) {
                places = places + tmp;
            }
            else {
                places = places + tmp + "|";
            }
    	}

        size = req->my_travel().travel_detail().play_detail().day_scenic_size();
    	for (int n = 0; n < size; ++n) {            
            const IM::Buddy::DayScenic& scenic = req->my_travel().travel_detail().play_detail().day_scenic(n);
            string tmp;
            tmp = string_fmt(tmp, "%d&%d&%s&%s", 1, scenic.scenic_info().id(), scenic.daytimefrom().c_str(), scenic.daytimeto().c_str());

            if ((n == 0) && (!places.empty())) {
                places += "|";
            }
            
            if (n == size - 1) {
                places = places + tmp;
            }
            else {
                places = places + tmp + "|";
            }
    	}
        
        
        string strSql;
        strSql = string_fmt(strSql, "call insert_or_update_my_travel(%d, %d, %d, '%s', '%s', '%s', %d, '%s', '%s', %d, '%s', '%s', %d, %d, %d, %d, %d, '%s', '%s', %d, %d, '%s', @ret, @idx)", 
            req->user_id(),
            req->my_travel().db_idx(),
            req->my_travel().cost(),
            req->my_travel().basic_info().date_from().c_str(),
            req->my_travel().basic_info().date_to().c_str(),
            req->my_travel().basic_info().place_to_code().c_str(),
            req->my_travel().basic_info().person_num(),
            req->my_travel().basic_info().place_from_code().c_str(),
            req->my_travel().basic_info().place_back_code().c_str(),
            req->my_travel().transport_config().tool_type(),
            req->my_travel().transport_config().time_from().c_str(),
            req->my_travel().transport_config().time_to().c_str(),
            req->my_travel().transport_config().quality(),
            req->my_travel().transport_config().transit(),
            req->my_travel().travel_detail().transport_tool().from_info().id(),
            req->my_travel().travel_detail().transport_tool().back_info().id(),
            req->my_travel().travel_detail().play_detail().play_config().quality(),
            req->my_travel().travel_detail().play_detail().play_config().time_from().c_str(),
            req->my_travel().travel_detail().play_detail().play_config().time_to().c_str(),
            req->my_travel().travel_detail().play_detail().play_config().transport_tool_type(),
            req->my_travel().travel_detail().play_detail().play_config().position(),
            places.c_str()
            );
        log("sql:%s", strSql.c_str());
        CResultSet* pResultSet = pDBConn->ExecuteQuery(strSql.c_str());
        if (pResultSet)
        {
            while (pResultSet->Next())
            {
                int ret = pResultSet->GetInt("ret");
                if (0 == ret) {
                    idx = pResultSet->GetInt("newId");
                }
            }
            delete pResultSet;
        }
        else
        {
            log(" no result set for sql:%s", strSql.c_str());
        }
        
    }
    else
    {
        log("no db connection for teamtalk_master");
    }
    return idx;
}


bool CUserModel::queryTravelDetail(uint32_t user_id, IM::Buddy::QueryMyTravelRsp& rsp) {
    bool ret = false;
    CDBManager* pDBManager = CDBManager::getInstance();
    CDBConn* pDBConn = pDBManager->GetDBConn("teamtalk_master");
    if (pDBConn)
    {
        string strIds;
        string strSql = "SELECT * FROM IMTravelBasicInfo where status=0 and userId=" + int2string(user_id) + " order by id desc";
        log("sql:%s", strSql.c_str());
        CResultSet* pResultSet = pDBConn->ExecuteQuery(strSql.c_str());
        if(pResultSet)
        {
            while (pResultSet->Next())
            {
                IM::Buddy::MyTravel* pMyTravel = rsp.add_my_travel();
                pMyTravel->set_db_idx(pResultSet->GetInt("id"));
                pMyTravel->set_cost(pResultSet->GetInt("cost"));
                pMyTravel->mutable_basic_info()->set_date_from(pResultSet->GetString("dateFrom"));
                pMyTravel->mutable_basic_info()->set_date_to(pResultSet->GetString("dateTo"));
                pMyTravel->mutable_basic_info()->set_place_to_code(pResultSet->GetString("placeToCode"));
                pMyTravel->mutable_basic_info()->set_person_num(pResultSet->GetInt("personNum"));
                pMyTravel->mutable_basic_info()->set_place_from_code(pResultSet->GetString("placeFromCode"));
                pMyTravel->mutable_basic_info()->set_place_back_code(pResultSet->GetString("placeBackCode"));
                pMyTravel->mutable_transport_config()->set_tool_type(pResultSet->GetInt("toolType"));
                pMyTravel->mutable_transport_config()->set_time_from(pResultSet->GetString("timeFrom"));
                pMyTravel->mutable_transport_config()->set_time_to(pResultSet->GetString("timeTo"));
                pMyTravel->mutable_transport_config()->set_quality((::IM::Buddy::QualityType)pResultSet->GetInt("qualityType"));
                pMyTravel->mutable_transport_config()->set_transit(pResultSet->GetInt("transit"));
                pMyTravel->mutable_travel_detail()->mutable_play_detail()->mutable_play_config()->set_quality((::IM::Buddy::QualityType)pResultSet->GetInt("playQualityType"));
                pMyTravel->mutable_travel_detail()->mutable_play_detail()->mutable_play_config()->set_time_from(pResultSet->GetString("playTimeFrom"));
                pMyTravel->mutable_travel_detail()->mutable_play_detail()->mutable_play_config()->set_time_to(pResultSet->GetString("playTimeTo"));
                pMyTravel->mutable_travel_detail()->mutable_play_detail()->mutable_play_config()->set_transport_tool_type(pResultSet->GetInt("playToolType"));
                pMyTravel->mutable_travel_detail()->mutable_play_detail()->mutable_play_config()->set_position((::IM::Buddy::PositionType)pResultSet->GetInt("positionType"));
                IN_MAP_CHECK(pResultSet->GetInt("transToolToId"), travelToolMap);
                pMyTravel->mutable_travel_detail()->mutable_transport_tool()->mutable_from_info()->CopyFrom(travelToolMap[pResultSet->GetInt("transToolToId")]);
                IN_MAP_CHECK(pResultSet->GetInt("transToolBackId"), travelToolMap);
                pMyTravel->mutable_travel_detail()->mutable_transport_tool()->mutable_back_info()->CopyFrom(travelToolMap[pResultSet->GetInt("transToolBackId")]);
            
                strIds += int2string(pMyTravel->db_idx()) + ",";
            }
            delete pResultSet;
        }
        else
        {
            log(" no result set for sql:%s", strSql.c_str());
        }

        strIds += "0";
        strSql = "SELECT * FROM IMPlayDetail where status=0 and travelBasicId in (" + strIds + ") order by id, dayTimeFrom";
        log("sql:%s", strSql.c_str());
        pResultSet = pDBConn->ExecuteQuery(strSql.c_str());
        if (pResultSet)
        {
            int lastId = 0;
            IM::Buddy::MyTravel* pMyTravel = NULL;
            while (pResultSet->Next())
            {
                int id = pResultSet->GetInt("travelBasicId");
                if (lastId != id) 
                {
                    int count = rsp.my_travel_size();
                    int i = 0;
                    for (; i < count; ++i)
                    {
                        pMyTravel = rsp.mutable_my_travel(i);
                        if (pMyTravel->db_idx() == id)
                        {
                            break;
                        }
                    }
                    if (i == count) {pMyTravel = NULL;}

                    if (pMyTravel != NULL) 
                    {
                        if (pResultSet->GetInt("type") == 1)
                        {
                            IM::Buddy::DayScenic* pDayScenic = pMyTravel->mutable_travel_detail()->mutable_play_detail()->add_day_scenic();
                            pDayScenic->set_daytimefrom(pResultSet->GetString("dayTimeFrom"));
                            pDayScenic->set_daytimeto(pResultSet->GetString("dayTimeTo"));
                            IN_MAP_CHECK(pResultSet->GetInt("itemId"), scenicMap);
                            pDayScenic->mutable_scenic_info()->CopyFrom(scenicMap[pResultSet->GetInt("itemId")]);
                        }
                        else if (pResultSet->GetInt("type") == 2)
                        {
                            IM::Buddy::DayHotel* pDayHotel = pMyTravel->mutable_travel_detail()->mutable_play_detail()->add_day_hotel();
                            pDayHotel->set_daytimefrom(pResultSet->GetString("dayTimeFrom"));
                            pDayHotel->set_daytimeto(pResultSet->GetString("dayTimeTo"));
                            IN_MAP_CHECK(pResultSet->GetInt("itemId"), hotelMap);
                            pDayHotel->mutable_hotel_info()->CopyFrom(hotelMap[pResultSet->GetInt("itemId")]);
                        }
                        else {}
                        
                    }
                    lastId = id;
                }
                else 
                {
                    if (pMyTravel != NULL) 
                    {
                        if (pResultSet->GetInt("type") == 1)
                        {
                            IM::Buddy::DayScenic* pDayScenic = pMyTravel->mutable_travel_detail()->mutable_play_detail()->add_day_scenic();
                            pDayScenic->set_daytimefrom(pResultSet->GetString("dayTimeFrom"));
                            pDayScenic->set_daytimeto(pResultSet->GetString("dayTimeTo"));
                            IN_MAP_CHECK(pResultSet->GetInt("itemId"), scenicMap);
                            pDayScenic->mutable_scenic_info()->CopyFrom(scenicMap[pResultSet->GetInt("itemId")]);
                        }
                        else if (pResultSet->GetInt("type") == 2)
                        {
                            IM::Buddy::DayHotel* pDayHotel = pMyTravel->mutable_travel_detail()->mutable_play_detail()->add_day_hotel();
                            pDayHotel->set_daytimefrom(pResultSet->GetString("dayTimeFrom"));
                            pDayHotel->set_daytimeto(pResultSet->GetString("dayTimeTo"));
                            IN_MAP_CHECK(pResultSet->GetInt("itemId"), hotelMap);
                            pDayHotel->mutable_hotel_info()->CopyFrom(hotelMap[pResultSet->GetInt("itemId")]);
                        }
                        else {}
                    }
                }
            }
            delete pResultSet;
        }
        else
        {
            log(" no result set for sql:%s", strSql.c_str());
        }



        
        pDBManager->RelDBConn(pDBConn);
        ret = true;
    }
    else
    {
        log("no db connection for teamtalk_master");
    }

    return ret;
}

bool CUserModel::deleteTravelDetail(uint32_t user_id, const set<uint32_t>& db_idx_list) {
    log("CUserModel::deleteTravelDetail enter.");
    if (0 == db_idx_list.size()) {
        return true;
    }
    
    bool bRet = true;
    CDBManager* pDBManager = CDBManager::getInstance();
    CDBConn* pDBConn = pDBManager->GetDBConn("teamtalk_master");
    if (pDBConn)
    {
        string strClause;
        bool bFirst = true;
        for(auto it = db_idx_list.begin(); it != db_idx_list.end(); ++it) {
            if (bFirst) {
                bFirst = false;
                strClause = int2string(*it);
            }
            else
            {
                strClause += ("," + int2string(*it));
            }
        }
        
        string strSql = "update IMTravelBasicInfo set status=1 where user_id=" + int2string(user_id) + " and id in (" + strClause + ")";
        log("sql:%s", strSql.c_str());
        bRet = pDBConn->ExecuteUpdate(strSql.c_str());
        pDBManager->RelDBConn(pDBConn);
    }
    else
    {
        log("no db connection for teamtalk_master");
        bRet = false;
    }
    
    return bRet;
}

bool CUserModel::createCollectRoute(uint32_t user_id, IM::Buddy::NewCreateCollectRouteReq* req, IM::Buddy::NewCreateCollectRouteRsp* pb) {
    log("enter.");
    bool bRet = false;
    int idx = 0;

    CDBManager* db_manager = CDBManager::getInstance();
    CDBConn* db_conn = db_manager->GetDBConn("teamtalk_master");
    if (db_conn) 
    {
        string str_sql;
        str_sql = string_fmt(str_sql, "insert into IMCollectRoute (userId,lineId,dateFrom,dateTo,startToolNo,endToolNo) values (%d, %d, '%s', '%s', '%s', '%s');", 
            req->user_id(),
            req->collect().route().id(),
            req->collect().start_date().c_str(),
            req->collect().end_date().c_str(),
            req->collect().start_traffic_no().c_str(),
            req->collect().end_traffic_no().c_str()
            );

        log("sql:%s", str_sql.c_str());

        CResultSet* pResultSet = db_conn->ExecuteQuery(str_sql.c_str());
        if (pResultSet)
        {
            while (pResultSet->Next())
            {
                int ret = pResultSet->GetInt("ret");
                if (0 == ret) {
                    idx = pResultSet->GetInt("newId");
                    pb->set_collect_id(idx);
                    bRet = true;
                    break;
                }
            }
            delete pResultSet;
        }

        db_manager->RelDBConn(db_conn);
    } 
    else 
    {
        log("no db connection for teamtalk_master");
    }

    return bRet;
}

bool CUserModel::deleteCollectRoute(uint32_t user_id, IM::Buddy::NewDelCollectRouteReq* req, IM::Buddy::NewDelCollectRouteRsp* pb) {
    log("enter.");
    bool bRet = false;

    CDBManager* pDBManager = CDBManager::getInstance();
    CDBConn* pDBConn = pDBManager->GetDBConn("teamtalk_master");
    if (pDBConn)
    {
        string strSql = "delete from IMCollectRoute where userId=" + int2string(user_id) + " and id="+int2string(req->collect_id(0));
        log("sql:%s", strSql.c_str());
        if(pDBConn->ExecuteUpdate(strSql.c_str()))
        {
            bRet = true;
        }
        pDBManager->RelDBConn(pDBConn);
    }
    else
    {
        log("no db connection for teamtalk_master");
    }

    return bRet;
}

bool CUserModel::queryCollectRoute(uint32_t user_id, IM::Buddy::NewQueryCollectRouteReq* req, IM::Buddy::NewQueryCollectRouteRsp* pb) {
    log("enter.");
    bool bRet = false;

    CDBManager* pDBManager = CDBManager::getInstance();
    CDBConn* pDBConn = pDBManager->GetDBConn("teamtalk_master");
    if (!pDBConn)
    {
        log("no db connection for teamtalk");
        return false;
    }

    CResultSet* pResultSet = NULL;
    //string strSql = "select * from IMRoute where quality='" + tag + "' order by dayNum";
    string strSql = "select C.id as seq, C.*, R.* from IMRoute as R, IMCollectRoute as C where R.lineId=C.lineId and C.userId=" + int2string(user_id) +" order by seq, R.lineId, R.dayNum";
    log("sql = %s", strSql.c_str());

    pResultSet = pDBConn->ExecuteQuery(strSql.c_str());
    if (pResultSet)
    {
        IM::Buddy::CollectionRoute* collect = NULL;
        IM::Buddy::Route *route = NULL;
        IM::Buddy::DayRoute* dayRoute = NULL;
        int id = 0;
        int lineId = 0;
        int day_num = 0;

        while (pResultSet->Next())
        {
            if (id == 0 || pResultSet->GetInt("seq") != id)
            {
                collect = pb->add_collections();
            }

            id = pResultSet->GetInt("seq");
            collect->set_id(pResultSet->GetInt("seq"));
            collect->set_start_date(pResultSet->GetString("dateFrom"));
            collect->set_end_date(pResultSet->GetString("dateTo"));
            collect->set_start_traffic_no(pResultSet->GetString("startTool"));
            collect->set_end_traffic_no(pResultSet->GetString("endToolNo"));
            route = collect->mutable_route();

            lineId = pResultSet->GetInt("lineId");
            route->set_id(pResultSet->GetInt("lineId"));
            route->set_day_count(pResultSet->GetInt("dayCount"));
            route->set_city_code(pResultSet->GetString("cityCode"));
            route->add_tag(pResultSet->GetString("quality"));
            route->set_start_transport_tool((::IM::Buddy::TransportToolType)pResultSet->GetInt("startTool"));
            route->set_end_transport_tool((::IM::Buddy::TransportToolType)pResultSet->GetInt("endTool"));
            route->set_start_time(pResultSet->GetString("startTime"));
            route->set_end_time(pResultSet->GetString("endTime"));
            dayRoute = route->add_day_routes();
            bRet = true;
            
            char *p;
            const char* sep = " ";
            p = strtok(pResultSet->GetString("routes"), sep);
            while(p){
                dayRoute->add_scenics(atoi(p));
                p = strtok(NULL, sep);
            }

            p = strtok(pResultSet->GetString("hotels"), sep);
            while(p){
                dayRoute->add_hotels(atoi(p));
                p = strtok(NULL, sep);
            }
        }

        delete pResultSet;
    }

    pDBManager->RelDBConn(pDBConn);
    log("exit.");

    return bRet;
}

