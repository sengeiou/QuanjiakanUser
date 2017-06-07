package com.androidquanjiakan.database;

/**
 * Created by Administrator on 2016/6/16 0016.
 */
public class TableInfo {
    //text integer
    /**
     * ***************************************************************************************************************************************
     * 临时信息表
     */
    public static final String TEMP_TABLE = "temp";

    public static final String TEMP_TABLE_COLUMN_PK = "_id";
    public static final String TEMP_TABLE_COLUMN_USER_ID = "user_id";
    public static final String TEMP_TABLE_COLUMN_INFO_KEY = "info_key";
    public static final String TEMP_TABLE_COLUMN_INFO_VALUE = "info_value";

    public static final String TEMP_TABLE_COLUMN_BACKUP1 = "backup1";
    public static final String TEMP_TABLE_COLUMN_BACKUP2 = "backup2";
    public static final String TEMP_TABLE_COLUMN_BACKUP3 = "backup3";
    public static final String TEMP_TABLE_COLUMN_BACKUP4 = "backup4";
    public static final String TEMP_TABLE_COLUMN_BACKUP5 = "backup5";

    public static final String TEMP_TABLE_CREATE = "CREATE TABLE " + TEMP_TABLE + " ("
            + TEMP_TABLE_COLUMN_PK + " integer primary key autoincrement,"
            + TEMP_TABLE_COLUMN_USER_ID + " text,"
            + TEMP_TABLE_COLUMN_INFO_KEY + " text,"
            + TEMP_TABLE_COLUMN_INFO_VALUE + " text,"
            + TEMP_TABLE_COLUMN_BACKUP1 + " text,"
            + TEMP_TABLE_COLUMN_BACKUP2 + " text,"
            + TEMP_TABLE_COLUMN_BACKUP3 + " text,"
            + TEMP_TABLE_COLUMN_BACKUP4 + " text,"
            + TEMP_TABLE_COLUMN_BACKUP5 + " text );";

    public static final String TEMP_TABLE_DROP = "DROP TABLE IF EXISTS " + TEMP_TABLE;

    /**
     * ***************************************************************************************************************************************
     * 绑定设备信息表
     */
    public static final String BIND_DEVICE_INFO_TABLE = "bind_device_info";

    public static final String BIND_DEVICE_INFO_TABLE_COLUMN_PK = "_id";
    public static final String BIND_DEVICE_INFO_TABLE_COLUMN_USER_ID = "user_id";
    public static final String BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_ID = "device_id";
    public static final String BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_USER_NAME = "device_user_name";
    public static final String BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_USER_HEADIMG = "device_user_headimg";

    public static final String BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP1 = "backup1";
    public static final String BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP2 = "backup2";
    public static final String BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP3 = "backup3";
    public static final String BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP4 = "backup4";
    public static final String BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP5 = "backup5";

    public static final String BIND_DEVICE_INFO_TABLE_CREATE = "CREATE TABLE " + BIND_DEVICE_INFO_TABLE + " ("
            + BIND_DEVICE_INFO_TABLE_COLUMN_PK + " integer primary key autoincrement,"
            + BIND_DEVICE_INFO_TABLE_COLUMN_USER_ID + " text,"
            + BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_ID + " text,"
            + BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_USER_NAME + " text,"
            + BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_USER_HEADIMG + " text,"
            + BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP1 + " text,"
            + BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP2 + " text,"
            + BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP3 + " text,"
            + BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP4 + " text,"
            + BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP5 + " text );";

    public static final String BIND_DEVICE_INFO_TABLE_DROP = "DROP TABLE IF EXISTS " + BIND_DEVICE_INFO_TABLE;

    /**
     * ***************************************************************************************************************************************
     * 设备情景模式信息表
     */
    public static final String DEVICE_SCENE_MODEL_TABLE = "device_scene";

    public static final String DEVICE_SCENE_MODEL_TABLE_COLUMN_PK = "_id";
    public static final String DEVICE_SCENE_MODEL_TABLE_COLUMN_USER_ID = "user_id";
    public static final String DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_ID = "device_id";
    public static final String DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_SCENE_MODEL = "scene_model";

    public static final String DEVICE_SCENE_MODEL_TABLE_COLUMN_BACKUP1 = "backup1";
    public static final String DEVICE_SCENE_MODEL_TABLE_COLUMN_BACKUP2 = "backup2";
    public static final String DEVICE_SCENE_MODEL_TABLE_COLUMN_BACKUP3 = "backup3";
    public static final String DEVICE_SCENE_MODEL_TABLE_COLUMN_BACKUP4 = "backup4";
    public static final String DEVICE_SCENE_MODEL_TABLE_COLUMN_BACKUP5 = "backup5";

    public static final String DEVICE_SCENE_MODEL_TABLE_CREATE = "CREATE TABLE " + DEVICE_SCENE_MODEL_TABLE + " ("
            + DEVICE_SCENE_MODEL_TABLE_COLUMN_PK + " integer primary key autoincrement,"
            + DEVICE_SCENE_MODEL_TABLE_COLUMN_USER_ID + " text,"
            + DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_ID + " text,"
            + DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_SCENE_MODEL + " text,"
            + DEVICE_SCENE_MODEL_TABLE_COLUMN_BACKUP1 + " text,"
            + DEVICE_SCENE_MODEL_TABLE_COLUMN_BACKUP2 + " text,"
            + DEVICE_SCENE_MODEL_TABLE_COLUMN_BACKUP3 + " text,"
            + DEVICE_SCENE_MODEL_TABLE_COLUMN_BACKUP4 + " text,"
            + DEVICE_SCENE_MODEL_TABLE_COLUMN_BACKUP5 + " text );";

    public static final String DEVICE_SCENE_MODEL_TABLE_DROP = "DROP TABLE IF EXISTS " + DEVICE_SCENE_MODEL_TABLE;


    /**
     * ***************************************************************************************************************************************
     * 情亲号码信息表
     */
    public static final String FAMILY_PHONE_TABLE = "family_phone_number";

    public static final String FAMILY_PHONE_TABLE_COLUMN_PK = "_id";
    public static final String FAMILY_PHONE_TABLE_COLUMN_USER_ID = "user_id";
    public static final String FAMILY_PHONE_TABLE_COLUMN_PHONE_NAME = "phone_name";
    public static final String FAMILY_PHONE_TABLE_COLUMN_PHONE = "phone_number";
    public static final String FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE = "phone_number_add_state";

    public static final String FAMILY_PHONE_TABLE_COLUMN_BACKUP1 = "backup1";
    public static final String FAMILY_PHONE_TABLE_COLUMN_BACKUP2 = "backup2";
    public static final String FAMILY_PHONE_TABLE_COLUMN_BACKUP3 = "backup3";
    public static final String FAMILY_PHONE_TABLE_COLUMN_BACKUP4 = "backup4";
    public static final String FAMILY_PHONE_TABLE_COLUMN_BACKUP5 = "backup5";

    public static final String FAMILY_PHONE_TABLE_CREATE = "CREATE TABLE " + FAMILY_PHONE_TABLE + " ("
            + FAMILY_PHONE_TABLE_COLUMN_PK + " integer primary key autoincrement,"
            + FAMILY_PHONE_TABLE_COLUMN_USER_ID + " text,"
            + FAMILY_PHONE_TABLE_COLUMN_PHONE_NAME + " text,"
            + FAMILY_PHONE_TABLE_COLUMN_PHONE + " text,"
            + FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE + " text,"
            + FAMILY_PHONE_TABLE_COLUMN_BACKUP1 + " text,"
            + FAMILY_PHONE_TABLE_COLUMN_BACKUP2 + " text,"
            + FAMILY_PHONE_TABLE_COLUMN_BACKUP3 + " text,"
            + FAMILY_PHONE_TABLE_COLUMN_BACKUP4 + " text,"
            + FAMILY_PHONE_TABLE_COLUMN_BACKUP5 + " text );";

    public static final String FAMILY_PHONE_TABLE_DROP = "DROP TABLE IF EXISTS " + FAMILY_PHONE_TABLE;

    /**
     * ***************************************************************************************************************************************
     * 默认收货地址表
     */
    public static final String DEFAULT_RECEIVE_ADDRESS_TABLE = "default_receive_address";

    public static final String DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PK = "_id";
    public static final String DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID = "user_id";
    public static final String DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_ADDRESSID = "address_id";
    public static final String DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_NAME = "name";
    public static final String DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE = "phone_number";
    public static final String DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_ADDRESS = "address";

    public static final String DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_BACKUP1 = "backup1";
    public static final String DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_BACKUP2 = "backup2";
    public static final String DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_BACKUP3 = "backup3";
    public static final String DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_BACKUP4 = "backup4";
    public static final String DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_BACKUP5 = "backup5";

    public static final String DEFAULT_RECEIVE_ADDRESS_TABLE_CREATE = "CREATE TABLE " + DEFAULT_RECEIVE_ADDRESS_TABLE + " ("
            + DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PK + " integer primary key autoincrement,"
            + DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID + " text,"
            + DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_ADDRESSID + " text,"
            + DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_NAME + " text,"
            + DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE + " text,"
            + DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_ADDRESS + " text,"
            + DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_BACKUP1 + " text,"
            + DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_BACKUP2 + " text,"
            + DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_BACKUP3 + " text,"
            + DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_BACKUP4 + " text,"
            + DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_BACKUP5 + " text );";

    public static final String DEFAULT_RECEIVE_ADDRESS_TABLE_DROP = "DROP TABLE IF EXISTS " + DEFAULT_RECEIVE_ADDRESS_TABLE;

    /**
     * ***************************************************************************************************************************************
     * 病人档案表
     */
    public static final String ONLINE_DOCTOR_PATIENT_INFO_TABLE = "online_doctor_patient_info_address";

    public static final String ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_PK = "_id";
    public static final String ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID = "user_id";
    public static final String ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX = "sex";
    public static final String ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME = "name";
    public static final String ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_AGE = "phone_number";
    public static final String ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_PHONE_ADDRESS = "address";

    public static final String ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_BACKUP1 = "backup1";
    public static final String ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_BACKUP2 = "backup2";
    public static final String ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_BACKUP3 = "backup3";
    public static final String ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_BACKUP4 = "backup4";
    public static final String ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_BACKUP5 = "backup5";

    public static final String ONLINE_DOCTOR_PATIENT_INFO_TABLE_CREATE = "CREATE TABLE " + ONLINE_DOCTOR_PATIENT_INFO_TABLE + " ("
            + ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_PK + " integer primary key autoincrement,"
            + ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID + " text,"
            + ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX + " text,"
            + ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME + " text,"
            + ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_AGE + " text,"
            + ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_PHONE_ADDRESS + " text,"
            + ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_BACKUP1 + " text,"
            + ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_BACKUP2 + " text,"
            + ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_BACKUP3 + " text,"
            + ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_BACKUP4 + " text,"
            + ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_BACKUP5 + " text );";

    public static final String ONLINE_DOCTOR_PATIENT_INFO_TABLE_DROP = "DROP TABLE IF EXISTS " + ONLINE_DOCTOR_PATIENT_INFO_TABLE;

    /**
     * ***************************************************************************************************************************************
     * 病人问题提交表
     */
    public static final String PATIENT_PROBLEM_INFO_TABLE = "patient_problem_info";

    public static final String PATIENT_PROBLEM_INFO_TABLE_COLUMN_PK = "_id";
    public static final String PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID = "user_id";
    public static final String PATIENT_PROBLEM_INFO_TABLE_COLUMN_SEX = "sex";
    public static final String PATIENT_PROBLEM_INFO_TABLE_COLUMN_NAME = "name";
    public static final String PATIENT_PROBLEM_INFO_TABLE_COLUMN_AGE = "age";
    public static final String PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID = "problem_id";
    public static final String PATIENT_PROBLEM_INFO_TABLE_COLUMN_TARGET = "target_doctor_id";//目标医生ID，其实这个没有【本地统一】
    public static final String PATIENT_PROBLEM_INFO_TABLE_COLUMN_TITLE = "title";//问题
    public static final String PATIENT_PROBLEM_INFO_TABLE_COLUMN_DATE = "date";//时间
    public static final String PATIENT_PROBLEM_INFO_TABLE_COLUMN_CLINIC = "clinic";//科室?---其实也没有

    public static final String PATIENT_PROBLEM_INFO_TABLE_COLUMN_BACKUP1 = "backup1";
    public static final String PATIENT_PROBLEM_INFO_TABLE_COLUMN_BACKUP2 = "backup2";
    public static final String PATIENT_PROBLEM_INFO_TABLE_COLUMN_BACKUP3 = "backup3";
    public static final String PATIENT_PROBLEM_INFO_TABLE_COLUMN_BACKUP4 = "backup4";
    public static final String PATIENT_PROBLEM_INFO_TABLE_COLUMN_BACKUP5 = "backup5";

    public static final String PATIENT_PROBLEM_INFO_TABLE_CREATE = "CREATE TABLE " + PATIENT_PROBLEM_INFO_TABLE + " ("
            + PATIENT_PROBLEM_INFO_TABLE_COLUMN_PK + " integer primary key autoincrement,"
            + PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID + " text,"

            + PATIENT_PROBLEM_INFO_TABLE_COLUMN_SEX + " text,"
            + PATIENT_PROBLEM_INFO_TABLE_COLUMN_NAME + " text,"
            + PATIENT_PROBLEM_INFO_TABLE_COLUMN_AGE + " text,"
            + PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID + " text,"
            + PATIENT_PROBLEM_INFO_TABLE_COLUMN_TARGET + " text,"
            + PATIENT_PROBLEM_INFO_TABLE_COLUMN_TITLE + " text,"
            + PATIENT_PROBLEM_INFO_TABLE_COLUMN_DATE + " text,"
            + PATIENT_PROBLEM_INFO_TABLE_COLUMN_CLINIC + " text,"

            + PATIENT_PROBLEM_INFO_TABLE_COLUMN_BACKUP1 + " text,"
            + PATIENT_PROBLEM_INFO_TABLE_COLUMN_BACKUP2 + " text,"
            + PATIENT_PROBLEM_INFO_TABLE_COLUMN_BACKUP3 + " text,"
            + PATIENT_PROBLEM_INFO_TABLE_COLUMN_BACKUP4 + " text,"
            + PATIENT_PROBLEM_INFO_TABLE_COLUMN_BACKUP5 + " text );";

    public static final String PATIENT_PROBLEM_INFO_TABLE_DROP = "DROP TABLE IF EXISTS " + PATIENT_PROBLEM_INFO_TABLE;



    /**
     * ***************************************************************************************************************************************
     * 绑定设备语音信息表
     */
    public static final String DEVICE_VOICE_INFO_TABLE = "device_voice_info";

    public static final String DEVICE_VOICE_INFO_TABLE_COLUMN_PK = "_id";
    public static final String DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID = "user_id";
    public static final String DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_ID = "device_id";
    public static final String DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_DIRECTION = "direction";
    public static final String DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_VOICE_PATH = "voice_path";

    public static final String DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP1 = "backup1";
    public static final String DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP2 = "backup2";
    public static final String DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP3 = "backup3";
    public static final String DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP4 = "backup4";
    public static final String DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP5 = "backup5";

    public static final String DEVICE_VOICE_INFO_TABLE_CREATE = "CREATE TABLE " + DEVICE_VOICE_INFO_TABLE + " ("
            + DEVICE_VOICE_INFO_TABLE_COLUMN_PK + " integer primary key autoincrement,"
            + DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID + " text,"
            + DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_ID + " text,"
            + DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_DIRECTION + " text,"
            + DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_VOICE_PATH + " text,"
            + DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP1 + " text,"
            + DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP2 + " text,"
            + DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP3 + " text,"
            + DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP4 + " text,"
            + DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP5 + " text );";

    public static final String DEVICE_VOICE_INFO_TABLE_DROP = "DROP TABLE IF EXISTS " + DEVICE_VOICE_INFO_TABLE;

    /**
     * ***************************************************************************************************************************************
     * 设备定位信息表
     */
    public static final String DEVICE_RAIL_INFO_TABLE = "device_rail_info";

    public static final String DEVICE_RAIL_INFO_TABLE_COLUMN_PK = "_id";
    public static final String DEVICE_RAIL_INFO_TABLE_COLUMN_USER_ID = "user_id";
    public static final String DEVICE_RAIL_INFO_TABLE_COLUMN_DEVICE_ID = "device_id";
    public static final String DEVICE_RAIL_INFO_TABLE_COLUMN_POINT_NUMBER = "point_number";
    public static final String DEVICE_RAIL_INFO_TABLE_COLUMN_POINT_DATA = "point_data";

    public static final String DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP1 = "backup1";
    public static final String DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP2 = "backup2";
    public static final String DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP3 = "backup3";
    public static final String DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP4 = "backup4";
    public static final String DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP5 = "backup5";

    public static final String DEVICE_RAIL_INFO_TABLE_CREATE = "CREATE TABLE " + DEVICE_RAIL_INFO_TABLE + " ("
            + DEVICE_RAIL_INFO_TABLE_COLUMN_PK + " integer primary key autoincrement,"
            + DEVICE_RAIL_INFO_TABLE_COLUMN_USER_ID + " text,"
            + DEVICE_RAIL_INFO_TABLE_COLUMN_DEVICE_ID + " text,"
            + DEVICE_RAIL_INFO_TABLE_COLUMN_POINT_NUMBER + " text,"
            + DEVICE_RAIL_INFO_TABLE_COLUMN_POINT_DATA + " text,"
            + DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP1 + " text,"
            + DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP2 + " text,"
            + DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP3 + " text,"
            + DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP4 + " text,"
            + DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP5 + " text );";

    public static final String DEVICE_RAIL_INFO_TABLE_DROP = "DROP TABLE IF EXISTS " + DEVICE_RAIL_INFO_TABLE;


}
