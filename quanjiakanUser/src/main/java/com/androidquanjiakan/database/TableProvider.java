package com.androidquanjiakan.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by Administrator on 2016/6/16 0016.
 */
public class TableProvider extends ContentProvider{

    private DBHelper dbHelper;
    private SQLiteDatabase database;

    public static final String AUTHORITY = "com.androidquanjiakan.database.TableProvider";

    public static final Uri TEMP_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TableInfo.TEMP_TABLE);
    public static final Uri BIND_DEVICE_INFO_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TableInfo.BIND_DEVICE_INFO_TABLE);
    public static final Uri DEVICE_SCENE_MODEL_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TableInfo.DEVICE_SCENE_MODEL_TABLE);
    public static final Uri FAMILY_PHONE_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TableInfo.FAMILY_PHONE_TABLE);
    public static final Uri DEFAULT_RECEIVE_ADDRESS_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE);
    public static final Uri ONLINE_DOCTOR_PATIENT_INFO_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE);

    public static final Uri PATIENT_PROBLEM_INFO_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TableInfo.PATIENT_PROBLEM_INFO_TABLE);

    public static final Uri DEVICE_RAIL_INFO_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TableInfo.DEVICE_RAIL_INFO_TABLE);

    public static final Uri DEVICE_VOICE_INFO_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TableInfo.DEVICE_VOICE_INFO_TABLE);


    public static final int TEMP = 1;
    public static final int TEMP_ID = 2;

    public static final int BIND_DEVICE_INFO = 3;
    public static final int BIND_DEVICE_INFO_ID = 4;

    public static final int DEVICE_SCENE_MODEL = 5;
    public static final int DEVICE_SCENE_MODEL_ID = 6;

    public static final int FAMILY_PHONE = 7;
    public static final int FAMILY_PHONE_ID = 8;

    public static final int DEFAULT_RECEIVE_ADDRESS = 9;
    public static final int DEFAULT_RECEIVE_ADDRESS_ID = 10;

    public static final int ONLINE_DOCTOR_PATIENT_INFO = 11;
    public static final int ONLINE_DOCTOR_PATIENT_INFO_ID = 12;

    public static final int PATIENT_PROBLEM_INFO = 13;
    public static final int PATIENT_PROBLEM_INFO_ID = 14;

    public static final int DEVICE_VOICE_INFO = 15;
    public static final int DEVICE_VOICE_INFO_ID = 16;

    public static final int DEVICE_RAIL_INFO = 17;
    public static final int DEVICE_RAIL_INFO_ID = 18;



    public static UriMatcher uriMatcher;

    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(AUTHORITY,TableInfo.TEMP_TABLE,TEMP);
        uriMatcher.addURI(AUTHORITY,TableInfo.TEMP_TABLE+"/#",TEMP_ID);

        uriMatcher.addURI(AUTHORITY,TableInfo.BIND_DEVICE_INFO_TABLE,BIND_DEVICE_INFO);
        uriMatcher.addURI(AUTHORITY,TableInfo.BIND_DEVICE_INFO_TABLE+"/#",BIND_DEVICE_INFO_ID);

        uriMatcher.addURI(AUTHORITY,TableInfo.DEVICE_SCENE_MODEL_TABLE,DEVICE_SCENE_MODEL);
        uriMatcher.addURI(AUTHORITY,TableInfo.DEVICE_SCENE_MODEL_TABLE+"/#",DEVICE_SCENE_MODEL_ID);

        uriMatcher.addURI(AUTHORITY,TableInfo.FAMILY_PHONE_TABLE,FAMILY_PHONE);
        uriMatcher.addURI(AUTHORITY,TableInfo.FAMILY_PHONE_TABLE+"/#",FAMILY_PHONE_ID);

        uriMatcher.addURI(AUTHORITY,TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE,DEFAULT_RECEIVE_ADDRESS);
        uriMatcher.addURI(AUTHORITY,TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE+"/#",DEFAULT_RECEIVE_ADDRESS_ID);

        uriMatcher.addURI(AUTHORITY,TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE,ONLINE_DOCTOR_PATIENT_INFO);
        uriMatcher.addURI(AUTHORITY,TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE+"/#",ONLINE_DOCTOR_PATIENT_INFO_ID);

        uriMatcher.addURI(AUTHORITY,TableInfo.PATIENT_PROBLEM_INFO_TABLE,PATIENT_PROBLEM_INFO);
        uriMatcher.addURI(AUTHORITY,TableInfo.PATIENT_PROBLEM_INFO_TABLE+"/#",PATIENT_PROBLEM_INFO_ID);
        //************************************************************************************************
        uriMatcher.addURI(AUTHORITY,TableInfo.DEVICE_VOICE_INFO_TABLE,DEVICE_VOICE_INFO);
        uriMatcher.addURI(AUTHORITY,TableInfo.DEVICE_VOICE_INFO_TABLE+"/#",DEVICE_VOICE_INFO_ID);

        uriMatcher.addURI(AUTHORITY,TableInfo.DEVICE_RAIL_INFO_TABLE,DEVICE_RAIL_INFO);
        uriMatcher.addURI(AUTHORITY,TableInfo.DEVICE_RAIL_INFO_TABLE+"/#",DEVICE_RAIL_INFO_ID);


    }

    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        database = dbHelper.getWritableDatabase();
        return (database==null) ? false:true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            case TEMP:
                return "vnd.android.cursor.dir/vnd.quanjiakan.android."+TableInfo.TEMP_TABLE;
            case TEMP_ID:
                return "vnd.android.cursor.item/vnd.quanjiakan.android."+TableInfo.TEMP_TABLE;

            case BIND_DEVICE_INFO:
                return "vnd.android.cursor.dir/vnd.quanjiakan.android."+TableInfo.BIND_DEVICE_INFO_TABLE;
            case BIND_DEVICE_INFO_ID:
                return "vnd.android.cursor.item/vnd.quanjiakan.android."+TableInfo.BIND_DEVICE_INFO_TABLE;

            case DEVICE_SCENE_MODEL:
                return "vnd.android.cursor.dir/vnd.quanjiakan.android."+TableInfo.DEVICE_SCENE_MODEL_TABLE;
            case DEVICE_SCENE_MODEL_ID:
                return "vnd.android.cursor.item/vnd.quanjiakan.android."+TableInfo.DEVICE_SCENE_MODEL_TABLE;

            case FAMILY_PHONE:
                return "vnd.android.cursor.dir/vnd.quanjiakan.android."+TableInfo.FAMILY_PHONE_TABLE;
            case FAMILY_PHONE_ID:
                return "vnd.android.cursor.item/vnd.quanjiakan.android."+TableInfo.FAMILY_PHONE_TABLE;

            case DEFAULT_RECEIVE_ADDRESS:
                return "vnd.android.cursor.dir/vnd.quanjiakan.android."+TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE;
            case DEFAULT_RECEIVE_ADDRESS_ID:
                return "vnd.android.cursor.item/vnd.quanjiakan.android."+TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE;

            case ONLINE_DOCTOR_PATIENT_INFO:
                return "vnd.android.cursor.dir/vnd.quanjiakan.android."+TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE;
            case ONLINE_DOCTOR_PATIENT_INFO_ID:
                return "vnd.android.cursor.item/vnd.quanjiakan.android."+TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE;

            case PATIENT_PROBLEM_INFO:
                return "vnd.android.cursor.dir/vnd.quanjiakan.android."+TableInfo.PATIENT_PROBLEM_INFO_TABLE;
            case PATIENT_PROBLEM_INFO_ID:
                return "vnd.android.cursor.item/vnd.quanjiakan.android."+TableInfo.PATIENT_PROBLEM_INFO_TABLE;

            case DEVICE_VOICE_INFO:
                return "vnd.android.cursor.dir/vnd.quanjiakan.android."+TableInfo.DEVICE_VOICE_INFO_TABLE;
            case DEVICE_VOICE_INFO_ID:
                return "vnd.android.cursor.item/vnd.quanjiakan.android."+TableInfo.DEVICE_VOICE_INFO_TABLE;


            case DEVICE_RAIL_INFO:
                return "vnd.android.cursor.dir/vnd.quanjiakan.android."+TableInfo.DEVICE_RAIL_INFO_TABLE;
            case DEVICE_RAIL_INFO_ID:
                return "vnd.android.cursor.item/vnd.quanjiakan.android."+TableInfo.DEVICE_RAIL_INFO_TABLE;


            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        if(uriMatcher.match(uri)!=TEMP && uriMatcher.match(uri)!=TEMP_ID &&
                uriMatcher.match(uri)!=BIND_DEVICE_INFO && uriMatcher.match(uri)!=BIND_DEVICE_INFO_ID &&
                uriMatcher.match(uri)!=DEVICE_SCENE_MODEL && uriMatcher.match(uri)!=DEVICE_SCENE_MODEL_ID &&
                uriMatcher.match(uri)!=DEFAULT_RECEIVE_ADDRESS && uriMatcher.match(uri)!=DEFAULT_RECEIVE_ADDRESS_ID &&

                uriMatcher.match(uri)!=ONLINE_DOCTOR_PATIENT_INFO && uriMatcher.match(uri)!=ONLINE_DOCTOR_PATIENT_INFO_ID &&

                uriMatcher.match(uri)!=PATIENT_PROBLEM_INFO && uriMatcher.match(uri)!=PATIENT_PROBLEM_INFO_ID &&

                uriMatcher.match(uri)!=DEVICE_VOICE_INFO && uriMatcher.match(uri)!=DEVICE_VOICE_INFO_ID &&

                uriMatcher.match(uri)!=DEVICE_RAIL_INFO && uriMatcher.match(uri)!=DEVICE_RAIL_INFO_ID &&

                uriMatcher.match(uri)!=FAMILY_PHONE && uriMatcher.match(uri)!=FAMILY_PHONE_ID ){
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        long rowId = 0;
        ContentValues values;
        if(contentValues!=null){
            values = new ContentValues(contentValues);
        }else{
            values = new ContentValues();
        }
        if (uriMatcher.match(uri) == TEMP) {
            if (values.containsKey(TableInfo.TEMP_TABLE_COLUMN_USER_ID) == false) {
                throw new IllegalAccessError("USER ID MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.TEMP_TABLE_COLUMN_INFO_KEY) == false) {
                throw new IllegalAccessError("INFO KEY MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.TEMP_TABLE_COLUMN_INFO_VALUE) == false) {
                throw new IllegalAccessError("INFO VALUE MUST BE INSERT!"+uri);
            }
            rowId = database.insert(TableInfo.TEMP_TABLE, null, values);
        }else if(uriMatcher.match(uri) == BIND_DEVICE_INFO){
            if (values.containsKey(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_USER_ID) == false) {
                throw new IllegalAccessError("USER ID MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_ID) == false) {
                throw new IllegalAccessError("DEVICE ID MUST BE INSERT!"+uri);
            }
            if(values.containsKey(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_USER_NAME) == false){
                values.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_USER_NAME,"");
            }
            if(values.containsKey(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_USER_HEADIMG) == false){
                values.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_USER_HEADIMG,"");
            }
            rowId = database.insert(TableInfo.BIND_DEVICE_INFO_TABLE, null, values);
        }else if(uriMatcher.match(uri) == DEVICE_SCENE_MODEL){
            if (values.containsKey(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_USER_ID) == false) {
                throw new IllegalAccessError("USER ID MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_ID) == false) {
                throw new IllegalAccessError("DEVICE ID MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_SCENE_MODEL) == false) {
                throw new IllegalAccessError("SCENE MODEL MUST BE INSERT!"+uri);
            }
            rowId = database.insert(TableInfo.DEVICE_SCENE_MODEL_TABLE, null, values);
        }else if(uriMatcher.match(uri) == FAMILY_PHONE){
            if (values.containsKey(TableInfo.FAMILY_PHONE_TABLE_COLUMN_USER_ID) == false) {
                throw new IllegalAccessError("USER ID MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_NAME) == false) {
                throw new IllegalAccessError("Phone Name MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE) == false) {
                throw new IllegalAccessError("PHONE MUST BE INSERT!"+uri);
            }
            if(values.containsKey(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE) == false){
                values.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE,TableInfo_ValueStub.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE_VALUE_UNADD);
            }
            rowId = database.insert(TableInfo.FAMILY_PHONE_TABLE, null, values);
        }else if(uriMatcher.match(uri) == DEFAULT_RECEIVE_ADDRESS){
            if (values.containsKey(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID) == false) {
                throw new IllegalAccessError("USER ID MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_ADDRESSID) == false) {
                throw new IllegalAccessError("Address ID MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_NAME) == false) {
                throw new IllegalAccessError("Name MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE) == false) {
                throw new IllegalAccessError("PHONE MUST BE INSERT!"+uri);
            }
            if(values.containsKey(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_ADDRESS) == false){
                throw new IllegalAccessError("ADDRESS MUST BE INSERT!"+uri);
            }
            rowId = database.insert(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE, null, values);
        }
        else if(uriMatcher.match(uri) == ONLINE_DOCTOR_PATIENT_INFO){
            if (values.containsKey(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID) == false) {
                throw new IllegalAccessError("USER ID MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX) == false) {
                throw new IllegalAccessError("SEX MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME) == false) {
                throw new IllegalAccessError("Name MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_AGE) == false) {
                throw new IllegalAccessError("PHONE MUST BE INSERT!"+uri);
            }
            rowId = database.insert(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE, null, values);
        }
        /**
         * 问题表
         */
        else if(uriMatcher.match(uri) == PATIENT_PROBLEM_INFO){
            if (values.containsKey(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_SEX) == false) {
                throw new IllegalAccessError("SEX MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_NAME) == false) {
                throw new IllegalAccessError("NAME MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_AGE) == false) {
                throw new IllegalAccessError("AGE MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID) == false) {
                throw new IllegalAccessError("PROBLEM_ID MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_TITLE) == false) {
                throw new IllegalAccessError("TITLE MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_DATE) == false) {
                throw new IllegalAccessError("DATE MUST BE INSERT!"+uri);
            }
            rowId = database.insert(TableInfo.PATIENT_PROBLEM_INFO_TABLE, null, values);
        }

        /**
         * 设备语音表
         */
        else if(uriMatcher.match(uri) == DEVICE_VOICE_INFO){
            if (values.containsKey(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID) == false) {
                throw new IllegalAccessError("USER_ID MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_ID) == false) {
                throw new IllegalAccessError("DEVICE_ID MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_DIRECTION) == false) {
                throw new IllegalAccessError("DIRECTION MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_VOICE_PATH) == false) {
                throw new IllegalAccessError("VOICE_PATH MUST BE INSERT!"+uri);
            }
            rowId = database.insert(TableInfo.DEVICE_VOICE_INFO_TABLE, null, values);
        }

        /**
         * 设备电子围栏信息表
         */
        else if(uriMatcher.match(uri) == DEVICE_RAIL_INFO){
            if (values.containsKey(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_USER_ID) == false) {
                throw new IllegalAccessError("SEX MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_DEVICE_ID) == false) {
                throw new IllegalAccessError("NAME MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_POINT_NUMBER) == false) {
                throw new IllegalAccessError("AGE MUST BE INSERT!"+uri);
            }
            if (values.containsKey(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_POINT_DATA) == false) {
                throw new IllegalAccessError("PROBLEM_ID MUST BE INSERT!"+uri);
            }
            rowId = database.insert(TableInfo.DEVICE_RAIL_INFO_TABLE, null, values);
        }

        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(TEMP_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] selectionArgs) {
        if(selectionArgs!=null&&selectionArgs.length>0){
            for(int i=0;i<selectionArgs.length;i++){
                if(selectionArgs[i] == null){
                    selectionArgs[i] = "";
                }
            }
        }
        int count = 0;
        String contactID;
        switch (uriMatcher.match(uri)){
            case TEMP:
                count = database.delete(TableInfo.TEMP_TABLE, where,selectionArgs);
                break;
            case TEMP_ID:
                contactID = uri.getPathSegments().get(1);
                count = database.delete(
                        TableInfo.TEMP_TABLE,
                        TableInfo.TEMP_TABLE_COLUMN_PK
                                + "="
                                + contactID
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ")" : ""), selectionArgs);
                break;



            case BIND_DEVICE_INFO:
                count = database.delete(TableInfo.BIND_DEVICE_INFO_TABLE, where,selectionArgs);
                break;
            case BIND_DEVICE_INFO_ID:
                contactID = uri.getPathSegments().get(1);
                count = database.delete(
                        TableInfo.BIND_DEVICE_INFO_TABLE,
                        TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_PK
                                + "="
                                + contactID
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ")" : ""), selectionArgs);
                break;

            case DEVICE_SCENE_MODEL:
                count = database.delete(TableInfo.DEVICE_SCENE_MODEL_TABLE, where,selectionArgs);
                break;
            case DEVICE_SCENE_MODEL_ID:
                contactID = uri.getPathSegments().get(1);
                count = database.delete(
                        TableInfo.DEVICE_SCENE_MODEL_TABLE,
                        TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_PK
                                + "="
                                + contactID
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ")" : ""), selectionArgs);
                break;

            case FAMILY_PHONE:
                count = database.delete(TableInfo.FAMILY_PHONE_TABLE, where,selectionArgs);
                break;
            case FAMILY_PHONE_ID:
                contactID = uri.getPathSegments().get(1);
                count = database.delete(
                        TableInfo.FAMILY_PHONE_TABLE,
                        TableInfo.FAMILY_PHONE_TABLE_COLUMN_PK
                                + "="
                                + contactID
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ")" : ""), selectionArgs);
                break;
            case DEFAULT_RECEIVE_ADDRESS:
                count = database.delete(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE, where,selectionArgs);
                break;
            case DEFAULT_RECEIVE_ADDRESS_ID:
                contactID = uri.getPathSegments().get(1);
                count = database.delete(
                        TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE,
                        TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PK
                                + "="
                                + contactID
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ")" : ""), selectionArgs);
                break;

            case ONLINE_DOCTOR_PATIENT_INFO:
                count = database.delete(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE, where,selectionArgs);
                break;
            case ONLINE_DOCTOR_PATIENT_INFO_ID:
                contactID = uri.getPathSegments().get(1);
                count = database.delete(
                        TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE,
                        TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_PK
                                + "="
                                + contactID
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ")" : ""), selectionArgs);
                break;

            case PATIENT_PROBLEM_INFO:
                count = database.delete(TableInfo.PATIENT_PROBLEM_INFO_TABLE, where,selectionArgs);
                break;
            case PATIENT_PROBLEM_INFO_ID:
                contactID = uri.getPathSegments().get(1);
                count = database.delete(
                        TableInfo.PATIENT_PROBLEM_INFO_TABLE,
                        TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PK
                                + "="
                                + contactID
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ")" : ""), selectionArgs);
                break;


            case DEVICE_VOICE_INFO:
                count = database.delete(TableInfo.DEVICE_VOICE_INFO_TABLE, where,selectionArgs);
                break;
            case DEVICE_VOICE_INFO_ID:
                contactID = uri.getPathSegments().get(1);
                count = database.delete(
                        TableInfo.DEVICE_VOICE_INFO_TABLE,
                        TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_PK
                                + "="
                                + contactID
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ")" : ""), selectionArgs);
                break;


            case DEVICE_RAIL_INFO:
                count = database.delete(TableInfo.DEVICE_RAIL_INFO_TABLE, where,selectionArgs);
                break;
            case DEVICE_RAIL_INFO_ID:
                contactID = uri.getPathSegments().get(1);
                count = database.delete(
                        TableInfo.DEVICE_RAIL_INFO_TABLE,
                        TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_PK
                                + "="
                                + contactID
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ")" : ""), selectionArgs);
                break;


            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where,String[] selectionArgs) {
        if(selectionArgs!=null&&selectionArgs.length>0){
            for(int i=0;i<selectionArgs.length;i++){
                if(selectionArgs[i] == null){
                    selectionArgs[i] = "";
                }
            }
        }
        int count;
        String contactID;
        switch (uriMatcher.match(uri)) {
            case TEMP:
                count = database.update(TableInfo.TEMP_TABLE, values, where,
                        selectionArgs);
                break;
            case TEMP_ID:
                contactID = uri.getPathSegments().get(1);
                count = database.update(TableInfo.TEMP_TABLE,
                        values,
                        TableInfo.TEMP_TABLE_COLUMN_PK
                                + "="
                                + contactID
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ")" : ""), selectionArgs);
                break;
            case BIND_DEVICE_INFO:
                count = database.update(TableInfo.BIND_DEVICE_INFO_TABLE, values, where,
                        selectionArgs);
                break;
            case BIND_DEVICE_INFO_ID:
                contactID = uri.getPathSegments().get(1);
                count = database.update(TableInfo.BIND_DEVICE_INFO_TABLE,
                        values,
                        TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_PK
                                + "="
                                + contactID
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ")" : ""), selectionArgs);
                break;
            case DEVICE_SCENE_MODEL:
                count = database.update(TableInfo.DEVICE_SCENE_MODEL_TABLE, values, where,
                        selectionArgs);
                break;
            case DEVICE_SCENE_MODEL_ID:
                contactID = uri.getPathSegments().get(1);
                count = database.update(TableInfo.DEVICE_SCENE_MODEL_TABLE,
                        values,
                        TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_PK
                                + "="
                                + contactID
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ")" : ""), selectionArgs);
                break;
            case FAMILY_PHONE:
                count = database.update(TableInfo.FAMILY_PHONE_TABLE, values, where,
                        selectionArgs);
                break;
            case FAMILY_PHONE_ID:
                contactID = uri.getPathSegments().get(1);
                count = database.update(TableInfo.FAMILY_PHONE_TABLE,
                        values,
                        TableInfo.FAMILY_PHONE_TABLE_COLUMN_PK
                                + "="
                                + contactID
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ")" : ""), selectionArgs);

            case DEFAULT_RECEIVE_ADDRESS:
                count = database.update(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE, values, where,
                        selectionArgs);
                break;
            case DEFAULT_RECEIVE_ADDRESS_ID:
                contactID = uri.getPathSegments().get(1);
                count = database.update(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE,
                        values,
                        TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PK
                                + "="
                                + contactID
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ")" : ""), selectionArgs);
                break;

            case ONLINE_DOCTOR_PATIENT_INFO:
                count = database.update(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE, values, where,
                        selectionArgs);
                break;
            case ONLINE_DOCTOR_PATIENT_INFO_ID:
                contactID = uri.getPathSegments().get(1);
                count = database.update(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE,
                        values,
                        TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_PK
                                + "="
                                + contactID
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ")" : ""), selectionArgs);
                break;

            //PATIENT_PROBLEM_INFO
            case PATIENT_PROBLEM_INFO:
                count = database.update(TableInfo.PATIENT_PROBLEM_INFO_TABLE, values, where,
                        selectionArgs);
                break;
            case PATIENT_PROBLEM_INFO_ID:
                contactID = uri.getPathSegments().get(1);
                count = database.update(TableInfo.PATIENT_PROBLEM_INFO_TABLE,
                        values,
                        TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PK
                                + "="
                                + contactID
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ")" : ""), selectionArgs);
                break;

            case DEVICE_VOICE_INFO:
                count = database.update(TableInfo.DEVICE_VOICE_INFO_TABLE, values, where,
                        selectionArgs);
                break;
            case DEVICE_VOICE_INFO_ID:
                contactID = uri.getPathSegments().get(1);
                count = database.update(TableInfo.DEVICE_VOICE_INFO_TABLE,
                        values,
                        TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_PK
                                + "="
                                + contactID
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ")" : ""), selectionArgs);
                break;

            case DEVICE_RAIL_INFO:
                count = database.update(TableInfo.DEVICE_RAIL_INFO_TABLE, values, where,
                        selectionArgs);
                break;
            case DEVICE_RAIL_INFO_ID:
                contactID = uri.getPathSegments().get(1);
                count = database.update(TableInfo.DEVICE_RAIL_INFO_TABLE,
                        values,
                        TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_PK
                                + "="
                                + contactID
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                + ")" : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder){
        if(selectionArgs!=null&&selectionArgs.length>0){
            for(int i=0;i<selectionArgs.length;i++){
                if(selectionArgs[i] == null){
                    selectionArgs[i] = "";
                }
            }
        }
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)){
            case TEMP:
            case TEMP_ID:
                qb.setTables(TableInfo.TEMP_TABLE);
                break;

            case BIND_DEVICE_INFO:
            case BIND_DEVICE_INFO_ID:
                qb.setTables(TableInfo.BIND_DEVICE_INFO_TABLE);
                break;

            case DEVICE_SCENE_MODEL:
            case DEVICE_SCENE_MODEL_ID:
                qb.setTables(TableInfo.DEVICE_SCENE_MODEL_TABLE);
                break;

            case FAMILY_PHONE:
            case FAMILY_PHONE_ID:
                qb.setTables(TableInfo.FAMILY_PHONE_TABLE);
                break;

            case DEFAULT_RECEIVE_ADDRESS:
            case DEFAULT_RECEIVE_ADDRESS_ID:
                qb.setTables(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE);
                break;

            case ONLINE_DOCTOR_PATIENT_INFO:
            case ONLINE_DOCTOR_PATIENT_INFO_ID:
                qb.setTables(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE);
                break;

            //PATIENT_PROBLEM_INFO
            case PATIENT_PROBLEM_INFO:
            case PATIENT_PROBLEM_INFO_ID:
                qb.setTables(TableInfo.PATIENT_PROBLEM_INFO_TABLE);
                break;

            case DEVICE_VOICE_INFO:
            case DEVICE_VOICE_INFO_ID:
                qb.setTables(TableInfo.DEVICE_VOICE_INFO_TABLE);
                break;

            case DEVICE_RAIL_INFO:
            case DEVICE_RAIL_INFO_ID:
                qb.setTables(TableInfo.DEVICE_RAIL_INFO_TABLE);
                break;


            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        switch (uriMatcher.match(uri)) {
            case TEMP_ID:
                qb.appendWhere(TableInfo.TEMP_TABLE_COLUMN_PK + "="
                        + uri.getPathSegments().get(1));
                break;
            case BIND_DEVICE_INFO_ID:
                qb.appendWhere(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_PK + "="
                        + uri.getPathSegments().get(1));
                break;
            case DEVICE_SCENE_MODEL_ID:
                qb.appendWhere(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_PK + "="
                        + uri.getPathSegments().get(1));

                break;
            case FAMILY_PHONE_ID:
                qb.appendWhere(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PK + "="
                        + uri.getPathSegments().get(1));
                break;
            case DEFAULT_RECEIVE_ADDRESS_ID:
                qb.appendWhere(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PK + "="
                        + uri.getPathSegments().get(1));
                break;
            case ONLINE_DOCTOR_PATIENT_INFO_ID:
                qb.appendWhere(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_PK + "="
                        + uri.getPathSegments().get(1));
                break;
            //PATIENT_PROBLEM_INFO
            case PATIENT_PROBLEM_INFO_ID:
                qb.appendWhere(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PK + "="
                        + uri.getPathSegments().get(1));
                break;

            case DEVICE_VOICE_INFO_ID:
                qb.appendWhere(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_PK + "="
                        + uri.getPathSegments().get(1));
                break;

            case DEVICE_RAIL_INFO_ID:
                qb.appendWhere(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_PK + "="
                        + uri.getPathSegments().get(1));
                break;

            default:
                break;
        }
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = "_id";
        } else {
            orderBy = sortOrder;
        }
        Cursor c = qb.query(database, projection, selection, selectionArgs,
                null, null, orderBy);
        if (c != null)
            c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

}
