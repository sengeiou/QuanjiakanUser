package com.androidquanjiakan.datahandler;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.database.TableInfo;
import com.androidquanjiakan.database.TableProvider;
import com.androidquanjiakan.entity.BindDeviceEntity;
import com.androidquanjiakan.util.CheckUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/16 0016.
 */
public class BindDeviceHandler {

    public static final int remove(String deviceID) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        int number = 0;
        Cursor cursor = resolver.query(TableProvider.BIND_DEVICE_INFO_URI, null, TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_ID + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), deviceID},
                TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            number = resolver.delete(TableProvider.BIND_DEVICE_INFO_URI, TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                    TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_ID + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(),deviceID});
        }
        if(cursor!=null){
            cursor.close();
        }
        return number;
    }

    public static final int removeAll() {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        int number = 0;
        Cursor cursor = resolver.query(TableProvider.BIND_DEVICE_INFO_URI, null, TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_USER_ID + "=? ",
                new String[]{BaseApplication.getInstances().getUser_id()},
                TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            number = resolver.delete(TableProvider.BIND_DEVICE_INFO_URI, TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_USER_ID + "=? ",
                    new String[]{BaseApplication.getInstances().getUser_id()});
        }
        if(cursor!=null){
            cursor.close();
        }
        return number;
    }

    public static final void insertValue(String deviceID, String userName,String headImage) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        if (CheckUtil.isEmpty(deviceID)) {
            deviceID = "";
        }
        if (CheckUtil.isEmpty(userName)) {
            userName = "";
        }
        if (CheckUtil.isEmpty(headImage)) {
            headImage = "";
        }
        int number=0;
        Uri tempUri;
        Cursor cursor = resolver.query(TableProvider.BIND_DEVICE_INFO_URI, null, TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_ID + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), deviceID}, TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_ID, deviceID);
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_USER_NAME, userName);
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_USER_HEADIMG, headImage);
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP1, "0");
            number = resolver.update(TableProvider.BIND_DEVICE_INFO_URI, contentValues, TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_ID + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(), deviceID});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_ID, deviceID);
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_USER_NAME, userName);
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_USER_HEADIMG, headImage);
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP1, "0");
            tempUri = resolver.insert(TableProvider.BIND_DEVICE_INFO_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static final void insertValue(final BindDeviceEntity entity) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        String deviceID = entity.getDeviceid();
        String userName = entity.getName();
        String headImage = entity.getIcon();
        if (CheckUtil.isEmpty(deviceID)) {
            deviceID = "";
        }
        if (CheckUtil.isEmpty(userName)) {
            userName = "";
        }
        if (CheckUtil.isEmpty(headImage)) {
            headImage = "";
        }
        Cursor cursor = resolver.query(TableProvider.BIND_DEVICE_INFO_URI, null, TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_ID + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), deviceID}, TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_ID, deviceID);
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_USER_NAME, userName);
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_USER_HEADIMG, headImage);
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP1, entity.getW_TYPE());
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP2, entity.getPhoneNumber());
            if(entity.getLocation()!=null && entity.getLocation().length()>0 && entity.getLocation().contains(",")){
                contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP3, entity.getLocation());
            }
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP4, entity.getAlarmTime()+"");
            resolver.update(TableProvider.BIND_DEVICE_INFO_URI, contentValues, TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_ID + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(), deviceID});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_ID, deviceID);
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_USER_NAME, userName);
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_USER_HEADIMG, headImage);
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP1, entity.getW_TYPE());
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP2, entity.getPhoneNumber());
            if(entity.getLocation()!=null && entity.getLocation().length()>0 && entity.getLocation().contains(",")){
                contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP3, entity.getLocation());
            }
            contentValues.put(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP4, entity.getAlarmTime()+"");
            resolver.insert(TableProvider.BIND_DEVICE_INFO_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static final void insertValue(List<BindDeviceEntity> list) {
        for (BindDeviceEntity entity:list) {
            insertValue(entity);
        }
    }


    //**



    public static final BindDeviceEntity getValue(String deviceID){
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        if (CheckUtil.isEmpty(deviceID)) {
            deviceID = "";
        }
        Cursor cursor = resolver.query(TableProvider.BIND_DEVICE_INFO_URI, null, TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_ID + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), deviceID}, TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_PK + " asc");

        if(cursor!=null && cursor.getCount()>0){
            if(cursor.moveToNext()){
                BindDeviceEntity entity = new BindDeviceEntity();
                String name = cursor.getString(cursor.getColumnIndex(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_USER_NAME));
                String img = cursor.getString(cursor.getColumnIndex(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_USER_HEADIMG));
                String w_type = cursor.getString(cursor.getColumnIndex(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP1));
                entity.setDeviceid(deviceID);
                entity.setName(name);
                entity.setIcon(img);
                entity.setW_TYPE(w_type);
                if(cursor!=null){
                    cursor.close();
                }
                return entity;
            }else{
                return null;
            }
        }else{
            if(cursor!=null){
                cursor.close();
            }
            return null;
        }
    }

    public static final List<BindDeviceEntity> getAllValue(){
        List<BindDeviceEntity> list = new ArrayList<BindDeviceEntity>();
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        Cursor cursor = resolver.query(TableProvider.BIND_DEVICE_INFO_URI, null,  TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_USER_ID + "=? ",
                new String[]{BaseApplication.getInstances().getUser_id()}, TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_PK + " asc");
        if(cursor!=null && cursor.getCount()>0){
            while (cursor.moveToNext()){
                BindDeviceEntity entity = new BindDeviceEntity();
                String deviceID = cursor.getString(cursor.getColumnIndex(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_ID));
                String name = cursor.getString(cursor.getColumnIndex(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_USER_NAME));
                String img = cursor.getString(cursor.getColumnIndex(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_DEVICE_USER_HEADIMG));
                String w_type = cursor.getString(cursor.getColumnIndex(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP1));
                String phone = cursor.getString(cursor.getColumnIndex(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP2));
                String location = cursor.getString(cursor.getColumnIndex(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP3));
                String alarmTime = cursor.getString(cursor.getColumnIndex(TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_BACKUP4));
                if(alarmTime==null || alarmTime.length()<1){
                    alarmTime = "0";
                }
                entity.setDeviceid(deviceID);
                entity.setName(name);
                entity.setIcon(img);
                entity.setW_TYPE(w_type);
                entity.setPhoneNumber(phone);
                entity.setLocation(location);
                entity.setAlarmTime(Long.parseLong(alarmTime));
                list.add(entity);
            }
            if(cursor!=null){
                cursor.close();
            }
            return list;
        }else{
            if(cursor!=null){
                cursor.close();
            }
            return list;
        }
    }


    public static final int getCount(){
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        Cursor cursor = resolver.query(TableProvider.BIND_DEVICE_INFO_URI, null, null,null, TableInfo.BIND_DEVICE_INFO_TABLE_COLUMN_PK + " asc");
        if(cursor!=null && cursor.getCount()>0){
            if(cursor!=null){
                cursor.close();
            }
            return cursor.getCount();
        }else{
            if(cursor!=null){
                cursor.close();
            }
            return 0;
        }
    }

}
