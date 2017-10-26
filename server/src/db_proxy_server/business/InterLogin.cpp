/*================================================================
*     Copyright (c) 2015年 lanhu. All rights reserved.
*   
*   文件名称：InterLogin.cpp
*   创 建 者：Zhang Yuanhao
*   邮    箱：bluefoxah@gmail.com
*   创建日期：2015年03月09日
*   描    述：
*
================================================================*/
#include "InterLogin.h"
#include "../DBPool.h"
#include "EncDec.h"
#include "IM.Buddy.pb.h"
#include<stdlib.h>
#include<stdio.h>

map<int, IM::Buddy::TravelToolInfo> travelToolMap;
map<int, IM::Buddy::ScenicInfo> scenicMap;
map<int, IM::Buddy::HotelInfo> hotelMap;

string get_salt()
{
	srandom(time(NULL));
	unsigned str_len = 4; //长度为4
	unsigned ran_num;
	char ran_char_array[62] = {'1','2','3','4','5','6','7','8','9','0',
		'a','b','c','d','e','f','g','h','i','j','k','l','m',
		'n','o','p','q','r','s','t','u','v','w','x','y','z',
		'A','B','C','D','E','F','G','H','I','J','K','L','M',
		'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	string ran_str;
	string ran_char;
	for (unsigned i = 0; i < str_len; i++)
	{
		ran_num = (unsigned)random()%62;//字符数组中一共62个字符
		ran_char = (char)ran_char_array[ran_num];
		ran_str = ran_str + ran_char;
	}
	return ran_str;
}

bool getTravelToolInfo() {
    bool ret = false;

    CDBManager* pDBManager = CDBManager::getInstance();
    CDBConn* pDBConn = pDBManager->GetDBConn("teamtalk_slave");
    if (pDBConn) {
        string  strSql = "select * from IMTravelTool";
        log("sql:%s", strSql.c_str());
        
        CResultSet* pResultSet = pDBConn->ExecuteQuery(strSql.c_str());
        if(pResultSet)
        {
            while (pResultSet->Next())
            {
                IM::Buddy::TravelToolInfo travelToolInfo;
                travelToolInfo.set_id(pResultSet->GetInt("id"));
                travelToolInfo.set_transport_tool_type(pResultSet->GetInt("type"));
                travelToolInfo.set_no(pResultSet->GetString("no"));
                travelToolInfo.set_place_from_code(pResultSet->GetString("placeFromCode"));
                travelToolInfo.set_place_from(pResultSet->GetString("placeFrom"));
                travelToolInfo.set_place_to_code(pResultSet->GetString("placeToCode"));
                travelToolInfo.set_place_to(pResultSet->GetString("placeTo"));
                travelToolInfo.set_time_from(pResultSet->GetString("timeFrom"));
                travelToolInfo.set_time_to(pResultSet->GetString("timeTo"));
                travelToolInfo.set_class_(pResultSet->GetString("class"));
                travelToolInfo.set_price(pResultSet->GetInt("price"));

                travelToolMap[travelToolInfo.id()] = travelToolInfo;
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
    return ret;
}

bool getScenicInfo() {
    bool ret = false;

    CDBManager* pDBManager = CDBManager::getInstance();
    CDBConn* pDBConn = pDBManager->GetDBConn("teamtalk_slave");
    if (pDBConn) {
        string  strSql = "select * from IMScenic";
        log("sql:%s", strSql.c_str());
        
        CResultSet* pResultSet = pDBConn->ExecuteQuery(strSql.c_str());
        if(pResultSet)
        {
            while (pResultSet->Next())
            {
                IM::Buddy::ScenicInfo scenicInfo;
				
                scenicInfo.set_id(pResultSet->GetInt("id"));
                scenicInfo.set_city_code(pResultSet->GetString("cityCode"));	
                scenicInfo.set_sightname(pResultSet->GetString("sightName"));
                scenicInfo.set_sightstatus(pResultSet->GetInt("sightStatus"));
                scenicInfo.set_sightopentime(pResultSet->GetString("sightOpenTime"));
                scenicInfo.set_sightaddress(pResultSet->GetString("sightAddress"));
                scenicInfo.set_sightdiscription(pResultSet->GetString("sightDiscription"));
                scenicInfo.set_sightstarttime(pResultSet->GetString("sightStartTime"));
                scenicInfo.set_sightendtime(pResultSet->GetString("sightEndTime"));
                scenicInfo.set_sightlatitude(pResultSet->GetString("sightLatitude"));
                scenicInfo.set_sightlongitude(pResultSet->GetString("sightLongitude"));
                scenicInfo.set_sightmustsee(pResultSet->GetInt("sightMustSee"));
                scenicInfo.set_sightpic(pResultSet->GetString("sightPic"));
                scenicInfo.set_sightplaytime(pResultSet->GetInt("sightPlayTime"));
                scenicInfo.set_sightprice(pResultSet->GetInt("sightPrice"));
                scenicInfo.set_sightscore(pResultSet->GetInt("sightScore"));
                scenicInfo.set_sighttag(pResultSet->GetString("sightTag"));
                scenicInfo.set_sighturl(pResultSet->GetString("sightUrl"));
                scenicInfo.set_literature(pResultSet->GetInt("literature"));
                scenicInfo.set_comfort(pResultSet->GetInt("comfort"));
                scenicInfo.set_encounter(pResultSet->GetInt("encounter"));
                scenicInfo.set_excite(pResultSet->GetInt("excite"));
                scenicInfo.set_exploration(pResultSet->GetInt("exploration"));

                scenicMap[scenicInfo.id()] = scenicInfo;
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
    return ret;
}

bool getHotelInfo() {
    bool ret = false;

    CDBManager* pDBManager = CDBManager::getInstance();
    CDBConn* pDBConn = pDBManager->GetDBConn("teamtalk_slave");
    if (pDBConn) {
        string  strSql = "select * from IMHotel";
        log("sql:%s", strSql.c_str());
        
        CResultSet* pResultSet = pDBConn->ExecuteQuery(strSql.c_str());
        if(pResultSet)
        {
            while (pResultSet->Next())
            {
                IM::Buddy::HotelInfo hotelInfo;
                hotelInfo.set_id(pResultSet->GetInt("id"));
                hotelInfo.set_city_code(pResultSet->GetString("cityCode"));
                hotelInfo.set_hotellatitude(pResultSet->GetString("hotelLatitude"));
                hotelInfo.set_hotellongitude(pResultSet->GetString("hotelLongitude"));
                hotelInfo.set_hotelname(pResultSet->GetString("hotelName"));
                hotelInfo.set_hotelpic(pResultSet->GetString("hotelPic"));
                hotelInfo.set_hotelscore(pResultSet->GetInt("hotelScore"));
                hotelInfo.set_hoteltag(pResultSet->GetString("hotelTag"));
                hotelInfo.set_hotelurl(pResultSet->GetString("hotelUrl"));
                hotelInfo.set_hotelprice(pResultSet->GetInt("hotelPrice"));

                hotelMap[hotelInfo.id()] = hotelInfo;
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
    return ret;
}

bool getAllInfo() {
    static bool alreadyGet = false;
    if (alreadyGet) {
        return true;
    }
    return true;
    //return getTravelToolInfo() && getScenicInfo() && getHotelInfo();
}



bool CInterLoginStrategy::doLogin(const std::string &strName, const std::string &strPass, IM::BaseDefine::UserInfo& user)
{
    bool bRet = getAllInfo();
    bool bFind = false;
    if ( ! bRet) return bRet;
    CDBManager* pDBManger = CDBManager::getInstance();
    CDBConn* pDBConn = pDBManger->GetDBConn("teamtalk_slave");
    if (pDBConn) {
        string strSql = "select * from IMUser where name='" + strName + "' and status=0";
        CResultSet* pResultSet = pDBConn->ExecuteQuery(strSql.c_str());
        if(pResultSet)
        {
        	log("regist find");
            string strResult, strSalt;
            uint32_t nId, nGender, nDeptId, nStatus;
            string strNick, strAvatar, strEmail, strRealName, strTel, strDomain,strSignInfo;
            while (pResultSet->Next()) {
                nId = pResultSet->GetInt("id");
                strResult = pResultSet->GetString("password");
                strSalt = pResultSet->GetString("salt");
                
                strNick = pResultSet->GetString("nick");
                nGender = pResultSet->GetInt("sex");
                strRealName = pResultSet->GetString("name");
                strDomain = pResultSet->GetString("domain");
                strTel = pResultSet->GetString("phone");
                strEmail = pResultSet->GetString("email");
                strAvatar = pResultSet->GetString("avatar");
                nDeptId = pResultSet->GetInt("departId");
                nStatus = pResultSet->GetInt("status");
                strSignInfo = pResultSet->GetString("sign_info");
                bFind=true;
            }

            string strInPass = strPass + strSalt;
            char szMd5[33];
            CMd5::MD5_Calculate(strInPass.c_str(), strInPass.length(), szMd5);
            string strOutPass(szMd5);
            if(strOutPass == strResult)
            {
                bRet = true;
                user.set_user_id(nId);
                user.set_user_nick_name(strNick);
                user.set_user_gender(nGender);
                user.set_user_real_name(strRealName);
                user.set_user_domain(strDomain);
                user.set_user_tel(strTel);
                user.set_email(strEmail);
                user.set_avatar_url(strAvatar);
                user.set_department_id(nDeptId);
                user.set_status(nStatus);
  	        	user.set_sign_info(strSignInfo);

            }
            delete  pResultSet;
        }
		if (bFind == false)
		{
			log("regist");
			string insertSql = "insert into IMUser ";
			string salt = get_salt();
			string pwdOri = strPass + salt;
			char pwdMd5[33];
            CMd5::MD5_Calculate(pwdOri.c_str(), pwdOri.length(), pwdMd5);
            string pwdLast(pwdMd5);
			log("regist name %s pwd %s md5 %s", strName.c_str(), pwdOri.c_str(), pwdLast.c_str());
			insertSql += "(name,password,salt,nick,sex,domain,phone,email,avatar,departId,status,sign_info,created,updated)";
			insertSql += "values('" + strName + "','" + pwdLast + "','" + salt + "','";
			insertSql += strName+"',0,0,'"+strName+"','email','avatar',1,0,'',1482030230,1482030230);";
        	CResultSet* insertResultSet = pDBConn->ExecuteQuery(insertSql.c_str());
      log("sql %s",insertSql.c_str());
			if(insertResultSet)
	        {
	        	log("regist true");
				bRet = true;
                user.set_user_id(pDBConn->GetInsertId());//为了不再去数据库里取，这里设定一个固定值
                user.set_user_nick_name(strName);
                user.set_user_gender(0);
                user.set_user_real_name(strName);
                user.set_user_domain("");
                user.set_user_tel(strName);
                user.set_email("email");
                user.set_avatar_url("avatar");
                user.set_department_id(1);
                user.set_status(0);
  	        	user.set_sign_info("");
	        }
			else
			{
				bRet = false;
				log("regist false");
			}
		}
        pDBManger->RelDBConn(pDBConn);
    }
    return bRet;
}
