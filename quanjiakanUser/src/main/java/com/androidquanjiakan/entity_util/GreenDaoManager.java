package com.androidquanjiakan.entity_util;

import android.database.sqlite.SQLiteDatabase;

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.example.greendao.dao.DaoMaster;
import com.example.greendao.dao.DaoSession;

/**
 * 作者：Administrator on 2017/3/22 10:12
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class GreenDaoManager {
    private static GreenDaoManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private DaoSession daoSession;
    private SQLiteDatabase db;
    private DaoMaster daoMaster;

    public GreenDaoManager(String imei) {
        openHelper = new DaoMaster.DevOpenHelper(BaseApplication.getInstances(), QuanjiakanSetting.getInstance().getUserId()+imei+"watch.db", null);
        db = openHelper.getWritableDatabase();
//        helper.onUpgrade(db,DaoMaster.SCHEMA_VERSION-1,DaoMaster.SCHEMA_VERSION);
        //获取数据库对象
        daoMaster = new DaoMaster(db);
        //获取Dao对象管理者
        daoSession = daoMaster.newSession();
    }


    public static GreenDaoManager getInstance(String imei) {
        if (mInstance == null) {
            synchronized (GreenDaoManager.class) {
                if (mInstance == null) {
                    mInstance = new GreenDaoManager(imei);
                }
            }
        }
        return mInstance;
    }


    public DaoSession getDaoInstant() {
        return daoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}
