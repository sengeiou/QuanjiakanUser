package com.example.greendao.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.androidquanjiakan.entity.BindingRequestBean;
import com.androidquanjiakan.entity.BindWatchEntity;
import com.androidquanjiakan.entity.BroadCastBean;
import com.androidquanjiakan.entity.CallsTrafficBean;
import com.androidquanjiakan.entity.CareRemindBean;
import com.androidquanjiakan.entity.ContactsBean;
import com.androidquanjiakan.entity.CourseBean;
import com.androidquanjiakan.entity.EfenceBean;
import com.androidquanjiakan.entity.FareandFlowDataBean;
import com.androidquanjiakan.entity.LABlocationBean;
import com.androidquanjiakan.entity.LocationBean;
import com.androidquanjiakan.entity.MessageRecordBean;
import com.androidquanjiakan.entity.RunTimeCategoryBean;
import com.androidquanjiakan.entity.rusumeBean;
import com.androidquanjiakan.entity.ScheduleBean;
import com.androidquanjiakan.entity.WatchCardBean;
import com.androidquanjiakan.entity.WatchMapDevice_DeviceInfo;
import com.androidquanjiakan.entity.WeatherBean;
import com.androidquanjiakan.entity.WIFIlocationBean;

import com.example.greendao.dao.BindingRequestBeanDao;
import com.example.greendao.dao.BindWatchEntityDao;
import com.example.greendao.dao.BroadCastBeanDao;
import com.example.greendao.dao.CallsTrafficBeanDao;
import com.example.greendao.dao.CareRemindBeanDao;
import com.example.greendao.dao.ContactsBeanDao;
import com.example.greendao.dao.CourseBeanDao;
import com.example.greendao.dao.EfenceBeanDao;
import com.example.greendao.dao.FareandFlowDataBeanDao;
import com.example.greendao.dao.LABlocationBeanDao;
import com.example.greendao.dao.LocationBeanDao;
import com.example.greendao.dao.MessageRecordBeanDao;
import com.example.greendao.dao.RunTimeCategoryBeanDao;
import com.example.greendao.dao.rusumeBeanDao;
import com.example.greendao.dao.ScheduleBeanDao;
import com.example.greendao.dao.WatchCardBeanDao;
import com.example.greendao.dao.WatchMapDevice_DeviceInfoDao;
import com.example.greendao.dao.WeatherBeanDao;
import com.example.greendao.dao.WIFIlocationBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig bindingRequestBeanDaoConfig;
    private final DaoConfig bindWatchEntityDaoConfig;
    private final DaoConfig broadCastBeanDaoConfig;
    private final DaoConfig callsTrafficBeanDaoConfig;
    private final DaoConfig careRemindBeanDaoConfig;
    private final DaoConfig contactsBeanDaoConfig;
    private final DaoConfig courseBeanDaoConfig;
    private final DaoConfig efenceBeanDaoConfig;
    private final DaoConfig fareandFlowDataBeanDaoConfig;
    private final DaoConfig lABlocationBeanDaoConfig;
    private final DaoConfig locationBeanDaoConfig;
    private final DaoConfig messageRecordBeanDaoConfig;
    private final DaoConfig runTimeCategoryBeanDaoConfig;
    private final DaoConfig rusumeBeanDaoConfig;
    private final DaoConfig scheduleBeanDaoConfig;
    private final DaoConfig watchCardBeanDaoConfig;
    private final DaoConfig watchMapDevice_DeviceInfoDaoConfig;
    private final DaoConfig weatherBeanDaoConfig;
    private final DaoConfig wIFIlocationBeanDaoConfig;

    private final BindingRequestBeanDao bindingRequestBeanDao;
    private final BindWatchEntityDao bindWatchEntityDao;
    private final BroadCastBeanDao broadCastBeanDao;
    private final CallsTrafficBeanDao callsTrafficBeanDao;
    private final CareRemindBeanDao careRemindBeanDao;
    private final ContactsBeanDao contactsBeanDao;
    private final CourseBeanDao courseBeanDao;
    private final EfenceBeanDao efenceBeanDao;
    private final FareandFlowDataBeanDao fareandFlowDataBeanDao;
    private final LABlocationBeanDao lABlocationBeanDao;
    private final LocationBeanDao locationBeanDao;
    private final MessageRecordBeanDao messageRecordBeanDao;
    private final RunTimeCategoryBeanDao runTimeCategoryBeanDao;
    private final rusumeBeanDao rusumeBeanDao;
    private final ScheduleBeanDao scheduleBeanDao;
    private final WatchCardBeanDao watchCardBeanDao;
    private final WatchMapDevice_DeviceInfoDao watchMapDevice_DeviceInfoDao;
    private final WeatherBeanDao weatherBeanDao;
    private final WIFIlocationBeanDao wIFIlocationBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        bindingRequestBeanDaoConfig = daoConfigMap.get(BindingRequestBeanDao.class).clone();
        bindingRequestBeanDaoConfig.initIdentityScope(type);

        bindWatchEntityDaoConfig = daoConfigMap.get(BindWatchEntityDao.class).clone();
        bindWatchEntityDaoConfig.initIdentityScope(type);

        broadCastBeanDaoConfig = daoConfigMap.get(BroadCastBeanDao.class).clone();
        broadCastBeanDaoConfig.initIdentityScope(type);

        callsTrafficBeanDaoConfig = daoConfigMap.get(CallsTrafficBeanDao.class).clone();
        callsTrafficBeanDaoConfig.initIdentityScope(type);

        careRemindBeanDaoConfig = daoConfigMap.get(CareRemindBeanDao.class).clone();
        careRemindBeanDaoConfig.initIdentityScope(type);

        contactsBeanDaoConfig = daoConfigMap.get(ContactsBeanDao.class).clone();
        contactsBeanDaoConfig.initIdentityScope(type);

        courseBeanDaoConfig = daoConfigMap.get(CourseBeanDao.class).clone();
        courseBeanDaoConfig.initIdentityScope(type);

        efenceBeanDaoConfig = daoConfigMap.get(EfenceBeanDao.class).clone();
        efenceBeanDaoConfig.initIdentityScope(type);

        fareandFlowDataBeanDaoConfig = daoConfigMap.get(FareandFlowDataBeanDao.class).clone();
        fareandFlowDataBeanDaoConfig.initIdentityScope(type);

        lABlocationBeanDaoConfig = daoConfigMap.get(LABlocationBeanDao.class).clone();
        lABlocationBeanDaoConfig.initIdentityScope(type);

        locationBeanDaoConfig = daoConfigMap.get(LocationBeanDao.class).clone();
        locationBeanDaoConfig.initIdentityScope(type);

        messageRecordBeanDaoConfig = daoConfigMap.get(MessageRecordBeanDao.class).clone();
        messageRecordBeanDaoConfig.initIdentityScope(type);

        runTimeCategoryBeanDaoConfig = daoConfigMap.get(RunTimeCategoryBeanDao.class).clone();
        runTimeCategoryBeanDaoConfig.initIdentityScope(type);

        rusumeBeanDaoConfig = daoConfigMap.get(rusumeBeanDao.class).clone();
        rusumeBeanDaoConfig.initIdentityScope(type);

        scheduleBeanDaoConfig = daoConfigMap.get(ScheduleBeanDao.class).clone();
        scheduleBeanDaoConfig.initIdentityScope(type);

        watchCardBeanDaoConfig = daoConfigMap.get(WatchCardBeanDao.class).clone();
        watchCardBeanDaoConfig.initIdentityScope(type);

        watchMapDevice_DeviceInfoDaoConfig = daoConfigMap.get(WatchMapDevice_DeviceInfoDao.class).clone();
        watchMapDevice_DeviceInfoDaoConfig.initIdentityScope(type);

        weatherBeanDaoConfig = daoConfigMap.get(WeatherBeanDao.class).clone();
        weatherBeanDaoConfig.initIdentityScope(type);

        wIFIlocationBeanDaoConfig = daoConfigMap.get(WIFIlocationBeanDao.class).clone();
        wIFIlocationBeanDaoConfig.initIdentityScope(type);

        bindingRequestBeanDao = new BindingRequestBeanDao(bindingRequestBeanDaoConfig, this);
        bindWatchEntityDao = new BindWatchEntityDao(bindWatchEntityDaoConfig, this);
        broadCastBeanDao = new BroadCastBeanDao(broadCastBeanDaoConfig, this);
        callsTrafficBeanDao = new CallsTrafficBeanDao(callsTrafficBeanDaoConfig, this);
        careRemindBeanDao = new CareRemindBeanDao(careRemindBeanDaoConfig, this);
        contactsBeanDao = new ContactsBeanDao(contactsBeanDaoConfig, this);
        courseBeanDao = new CourseBeanDao(courseBeanDaoConfig, this);
        efenceBeanDao = new EfenceBeanDao(efenceBeanDaoConfig, this);
        fareandFlowDataBeanDao = new FareandFlowDataBeanDao(fareandFlowDataBeanDaoConfig, this);
        lABlocationBeanDao = new LABlocationBeanDao(lABlocationBeanDaoConfig, this);
        locationBeanDao = new LocationBeanDao(locationBeanDaoConfig, this);
        messageRecordBeanDao = new MessageRecordBeanDao(messageRecordBeanDaoConfig, this);
        runTimeCategoryBeanDao = new RunTimeCategoryBeanDao(runTimeCategoryBeanDaoConfig, this);
        rusumeBeanDao = new rusumeBeanDao(rusumeBeanDaoConfig, this);
        scheduleBeanDao = new ScheduleBeanDao(scheduleBeanDaoConfig, this);
        watchCardBeanDao = new WatchCardBeanDao(watchCardBeanDaoConfig, this);
        watchMapDevice_DeviceInfoDao = new WatchMapDevice_DeviceInfoDao(watchMapDevice_DeviceInfoDaoConfig, this);
        weatherBeanDao = new WeatherBeanDao(weatherBeanDaoConfig, this);
        wIFIlocationBeanDao = new WIFIlocationBeanDao(wIFIlocationBeanDaoConfig, this);

        registerDao(BindingRequestBean.class, bindingRequestBeanDao);
        registerDao(BindWatchEntity.class, bindWatchEntityDao);
        registerDao(BroadCastBean.class, broadCastBeanDao);
        registerDao(CallsTrafficBean.class, callsTrafficBeanDao);
        registerDao(CareRemindBean.class, careRemindBeanDao);
        registerDao(ContactsBean.class, contactsBeanDao);
        registerDao(CourseBean.class, courseBeanDao);
        registerDao(EfenceBean.class, efenceBeanDao);
        registerDao(FareandFlowDataBean.class, fareandFlowDataBeanDao);
        registerDao(LABlocationBean.class, lABlocationBeanDao);
        registerDao(LocationBean.class, locationBeanDao);
        registerDao(MessageRecordBean.class, messageRecordBeanDao);
        registerDao(RunTimeCategoryBean.class, runTimeCategoryBeanDao);
        registerDao(rusumeBean.class, rusumeBeanDao);
        registerDao(ScheduleBean.class, scheduleBeanDao);
        registerDao(WatchCardBean.class, watchCardBeanDao);
        registerDao(WatchMapDevice_DeviceInfo.class, watchMapDevice_DeviceInfoDao);
        registerDao(WeatherBean.class, weatherBeanDao);
        registerDao(WIFIlocationBean.class, wIFIlocationBeanDao);
    }
    
    public void clear() {
        bindingRequestBeanDaoConfig.clearIdentityScope();
        bindWatchEntityDaoConfig.clearIdentityScope();
        broadCastBeanDaoConfig.clearIdentityScope();
        callsTrafficBeanDaoConfig.clearIdentityScope();
        careRemindBeanDaoConfig.clearIdentityScope();
        contactsBeanDaoConfig.clearIdentityScope();
        courseBeanDaoConfig.clearIdentityScope();
        efenceBeanDaoConfig.clearIdentityScope();
        fareandFlowDataBeanDaoConfig.clearIdentityScope();
        lABlocationBeanDaoConfig.clearIdentityScope();
        locationBeanDaoConfig.clearIdentityScope();
        messageRecordBeanDaoConfig.clearIdentityScope();
        runTimeCategoryBeanDaoConfig.clearIdentityScope();
        rusumeBeanDaoConfig.clearIdentityScope();
        scheduleBeanDaoConfig.clearIdentityScope();
        watchCardBeanDaoConfig.clearIdentityScope();
        watchMapDevice_DeviceInfoDaoConfig.clearIdentityScope();
        weatherBeanDaoConfig.clearIdentityScope();
        wIFIlocationBeanDaoConfig.clearIdentityScope();
    }

    public BindingRequestBeanDao getBindingRequestBeanDao() {
        return bindingRequestBeanDao;
    }

    public BindWatchEntityDao getBindWatchEntityDao() {
        return bindWatchEntityDao;
    }

    public BroadCastBeanDao getBroadCastBeanDao() {
        return broadCastBeanDao;
    }

    public CallsTrafficBeanDao getCallsTrafficBeanDao() {
        return callsTrafficBeanDao;
    }

    public CareRemindBeanDao getCareRemindBeanDao() {
        return careRemindBeanDao;
    }

    public ContactsBeanDao getContactsBeanDao() {
        return contactsBeanDao;
    }

    public CourseBeanDao getCourseBeanDao() {
        return courseBeanDao;
    }

    public EfenceBeanDao getEfenceBeanDao() {
        return efenceBeanDao;
    }

    public FareandFlowDataBeanDao getFareandFlowDataBeanDao() {
        return fareandFlowDataBeanDao;
    }

    public LABlocationBeanDao getLABlocationBeanDao() {
        return lABlocationBeanDao;
    }

    public LocationBeanDao getLocationBeanDao() {
        return locationBeanDao;
    }

    public MessageRecordBeanDao getMessageRecordBeanDao() {
        return messageRecordBeanDao;
    }

    public RunTimeCategoryBeanDao getRunTimeCategoryBeanDao() {
        return runTimeCategoryBeanDao;
    }

    public rusumeBeanDao getRusumeBeanDao() {
        return rusumeBeanDao;
    }

    public ScheduleBeanDao getScheduleBeanDao() {
        return scheduleBeanDao;
    }

    public WatchCardBeanDao getWatchCardBeanDao() {
        return watchCardBeanDao;
    }

    public WatchMapDevice_DeviceInfoDao getWatchMapDevice_DeviceInfoDao() {
        return watchMapDevice_DeviceInfoDao;
    }

    public WeatherBeanDao getWeatherBeanDao() {
        return weatherBeanDao;
    }

    public WIFIlocationBeanDao getWIFIlocationBeanDao() {
        return wIFIlocationBeanDao;
    }

}
