package com.zhizulx.tt.DB.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.zhizulx.tt.DB.entity.DepartmentEntity;
import com.zhizulx.tt.DB.entity.UserEntity;
import com.zhizulx.tt.DB.entity.GroupEntity;
import com.zhizulx.tt.DB.entity.MessageEntity;
import com.zhizulx.tt.DB.entity.SessionEntity;
import com.zhizulx.tt.DB.entity.SightEntity;
import com.zhizulx.tt.DB.entity.HotelEntity;
import com.zhizulx.tt.DB.entity.TrafficEntity;
import com.zhizulx.tt.DB.entity.DetailDispEntity;

import com.zhizulx.tt.DB.dao.DepartmentDao;
import com.zhizulx.tt.DB.dao.UserDao;
import com.zhizulx.tt.DB.dao.GroupDao;
import com.zhizulx.tt.DB.dao.MessageDao;
import com.zhizulx.tt.DB.dao.SessionDao;
import com.zhizulx.tt.DB.dao.SightDao;
import com.zhizulx.tt.DB.dao.HotelDao;
import com.zhizulx.tt.DB.dao.TrafficDao;
import com.zhizulx.tt.DB.dao.DetailDispDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig departmentDaoConfig;
    private final DaoConfig userDaoConfig;
    private final DaoConfig groupDaoConfig;
    private final DaoConfig messageDaoConfig;
    private final DaoConfig sessionDaoConfig;
    private final DaoConfig sightDaoConfig;
    private final DaoConfig hotelDaoConfig;
    private final DaoConfig trafficDaoConfig;
    private final DaoConfig detailDispDaoConfig;

    private final DepartmentDao departmentDao;
    private final UserDao userDao;
    private final GroupDao groupDao;
    private final MessageDao messageDao;
    private final SessionDao sessionDao;
    private final SightDao sightDao;
    private final HotelDao hotelDao;
    private final TrafficDao trafficDao;
    private final DetailDispDao detailDispDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        departmentDaoConfig = daoConfigMap.get(DepartmentDao.class).clone();
        departmentDaoConfig.initIdentityScope(type);

        userDaoConfig = daoConfigMap.get(UserDao.class).clone();
        userDaoConfig.initIdentityScope(type);

        groupDaoConfig = daoConfigMap.get(GroupDao.class).clone();
        groupDaoConfig.initIdentityScope(type);

        messageDaoConfig = daoConfigMap.get(MessageDao.class).clone();
        messageDaoConfig.initIdentityScope(type);

        sessionDaoConfig = daoConfigMap.get(SessionDao.class).clone();
        sessionDaoConfig.initIdentityScope(type);

        sightDaoConfig = daoConfigMap.get(SightDao.class).clone();
        sightDaoConfig.initIdentityScope(type);

        hotelDaoConfig = daoConfigMap.get(HotelDao.class).clone();
        hotelDaoConfig.initIdentityScope(type);

        trafficDaoConfig = daoConfigMap.get(TrafficDao.class).clone();
        trafficDaoConfig.initIdentityScope(type);

        detailDispDaoConfig = daoConfigMap.get(DetailDispDao.class).clone();
        detailDispDaoConfig.initIdentityScope(type);

        departmentDao = new DepartmentDao(departmentDaoConfig, this);
        userDao = new UserDao(userDaoConfig, this);
        groupDao = new GroupDao(groupDaoConfig, this);
        messageDao = new MessageDao(messageDaoConfig, this);
        sessionDao = new SessionDao(sessionDaoConfig, this);
        sightDao = new SightDao(sightDaoConfig, this);
        hotelDao = new HotelDao(hotelDaoConfig, this);
        trafficDao = new TrafficDao(trafficDaoConfig, this);
        detailDispDao = new DetailDispDao(detailDispDaoConfig, this);

        registerDao(DepartmentEntity.class, departmentDao);
        registerDao(UserEntity.class, userDao);
        registerDao(GroupEntity.class, groupDao);
        registerDao(MessageEntity.class, messageDao);
        registerDao(SessionEntity.class, sessionDao);
        registerDao(SightEntity.class, sightDao);
        registerDao(HotelEntity.class, hotelDao);
        registerDao(TrafficEntity.class, trafficDao);
        registerDao(DetailDispEntity.class, detailDispDao);
    }
    
    public void clear() {
        departmentDaoConfig.getIdentityScope().clear();
        userDaoConfig.getIdentityScope().clear();
        groupDaoConfig.getIdentityScope().clear();
        messageDaoConfig.getIdentityScope().clear();
        sessionDaoConfig.getIdentityScope().clear();
        sightDaoConfig.getIdentityScope().clear();
        hotelDaoConfig.getIdentityScope().clear();
        trafficDaoConfig.getIdentityScope().clear();
        detailDispDaoConfig.getIdentityScope().clear();
    }

    public DepartmentDao getDepartmentDao() {
        return departmentDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public GroupDao getGroupDao() {
        return groupDao;
    }

    public MessageDao getMessageDao() {
        return messageDao;
    }

    public SessionDao getSessionDao() {
        return sessionDao;
    }

    public SightDao getSightDao() {
        return sightDao;
    }

    public HotelDao getHotelDao() {
        return hotelDao;
    }

    public TrafficDao getTrafficDao() {
        return trafficDao;
    }

    public DetailDispDao getDetailDispDao() {
        return detailDispDao;
    }

}