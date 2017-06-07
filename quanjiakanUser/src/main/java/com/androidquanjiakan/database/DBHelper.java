package com.androidquanjiakan.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/6/16 0016.
 */
public class DBHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_FILE_NAME = "quanjiakan.db";

    public DBHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TableInfo.TEMP_TABLE_CREATE);
        db.execSQL(TableInfo.BIND_DEVICE_INFO_TABLE_CREATE);
        db.execSQL(TableInfo.DEVICE_SCENE_MODEL_TABLE_CREATE);
        db.execSQL(TableInfo.FAMILY_PHONE_TABLE_CREATE);
        db.execSQL(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_CREATE);
        db.execSQL(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_CREATE);

        db.execSQL(TableInfo.PATIENT_PROBLEM_INFO_TABLE_CREATE);

        db.execSQL(TableInfo.DEVICE_RAIL_INFO_TABLE_CREATE);

        db.execSQL(TableInfo.DEVICE_VOICE_INFO_TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion>oldVersion){
            if(oldVersion>=1 && newVersion==DATABASE_VERSION){
                db.execSQL(TableInfo.TEMP_TABLE_DROP);
                db.execSQL(TableInfo.TEMP_TABLE_CREATE);

                db.execSQL(TableInfo.BIND_DEVICE_INFO_TABLE_DROP);
                db.execSQL(TableInfo.BIND_DEVICE_INFO_TABLE_CREATE);

                db.execSQL(TableInfo.DEVICE_SCENE_MODEL_TABLE_DROP);
                db.execSQL(TableInfo.DEVICE_SCENE_MODEL_TABLE_CREATE);

                db.execSQL(TableInfo.FAMILY_PHONE_TABLE_DROP);
                db.execSQL(TableInfo.FAMILY_PHONE_TABLE_CREATE);

                db.execSQL(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_DROP);
                db.execSQL(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_CREATE);

                db.execSQL(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_DROP);
                db.execSQL(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_CREATE);

                db.execSQL(TableInfo.PATIENT_PROBLEM_INFO_TABLE_DROP);
                db.execSQL(TableInfo.PATIENT_PROBLEM_INFO_TABLE_CREATE);

                db.execSQL(TableInfo.DEVICE_VOICE_INFO_TABLE_DROP);
                db.execSQL(TableInfo.DEVICE_VOICE_INFO_TABLE_CREATE);

                db.execSQL(TableInfo.DEVICE_RAIL_INFO_TABLE_DROP);
                db.execSQL(TableInfo.DEVICE_RAIL_INFO_TABLE_CREATE);


            }
        }
    }
}
