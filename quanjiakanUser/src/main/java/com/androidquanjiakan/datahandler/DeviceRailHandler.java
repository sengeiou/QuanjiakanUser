package com.androidquanjiakan.datahandler;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.amap.api.maps2d.model.LatLng;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.database.TableInfo;
import com.androidquanjiakan.database.TableInfo_ValueStub;
import com.androidquanjiakan.database.TableProvider;
import com.androidquanjiakan.entity.BindDeviceEntity;
import com.androidquanjiakan.entity.DeviceRailEntity;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/16 0016.
 */
public class DeviceRailHandler {

    public static final int remove(String deviceID,String position) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        int number = 0;
        Cursor cursor = resolver.query(TableProvider.DEVICE_RAIL_INFO_URI, null, TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_DEVICE_ID + "=? and "+TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP2 +" =?",
                new String[]{BaseApplication.getInstances().getUser_id(), deviceID,position},
                TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            number = resolver.delete(TableProvider.DEVICE_RAIL_INFO_URI, TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_DEVICE_ID + "=? and "+TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP2 +" =?",
                    new String[]{BaseApplication.getInstances().getUser_id(),deviceID,position});
        }
        if(cursor!=null){
            cursor.close();
        }
        return number;
    }

    public static final int remove(String deviceID,String position,String time) {
        int number = 0;
        number = remove(deviceID,position);
        return number;
    }

    public static final int remove(String deviceID,String position,long time) {
        int number = 0;
        number = remove(deviceID,position);
        return number;
    }

    public static final int remove(String deviceID) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        int number = 0;
        Cursor cursor = resolver.query(TableProvider.DEVICE_RAIL_INFO_URI, null, TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_DEVICE_ID +" =?",
                new String[]{BaseApplication.getInstances().getUser_id(), deviceID},
                TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            number = resolver.delete(TableProvider.DEVICE_RAIL_INFO_URI, TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_DEVICE_ID +" =?",
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
        Cursor cursor = resolver.query(TableProvider.DEVICE_RAIL_INFO_URI, null, TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_USER_ID + "=? ",
                new String[]{BaseApplication.getInstances().getUser_id()},
                TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            number = resolver.delete(TableProvider.DEVICE_RAIL_INFO_URI, TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_USER_ID + "=? ",
                    new String[]{BaseApplication.getInstances().getUser_id()});
        }
        if(cursor!=null){
            cursor.close();
        }
        return number;
    }


    public static final void insertValue(final DeviceRailEntity entity,String position) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        String deviceID = entity.getDeviceid();
        String number = entity.getNumber();
        String path = entity.getPath();
        if(!TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST.equals(position) &&
                !TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND.equals(position) &&
                !TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD.equals(position)){
            try {
                throw new Exception("Rail data insert error,error position value!");
            } catch (Exception e) {
                LogUtil.e(e.getMessage());
            }
            return;
        }
        if (CheckUtil.isEmpty(deviceID)) {
            deviceID = "";
        }
        if (CheckUtil.isEmpty(number)) {
            number = "";
        }
        if (CheckUtil.isEmpty(path)) {
            path = "";
        }
        Cursor cursor = resolver.query(TableProvider.DEVICE_RAIL_INFO_URI, null, TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP2+ "=? and " +
                        TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_DEVICE_ID + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(),position, deviceID}, TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_DEVICE_ID, deviceID);
            contentValues.put(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_POINT_NUMBER, number);
            contentValues.put(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_POINT_DATA, path);
            contentValues.put(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP1, entity.getTime());
            contentValues.put(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP2, position);
            resolver.update(TableProvider.DEVICE_RAIL_INFO_URI, contentValues, TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP2 + "=? and " +
                            TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_DEVICE_ID + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(),position, deviceID});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_DEVICE_ID, deviceID);
            contentValues.put(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_POINT_NUMBER, number);
            contentValues.put(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_POINT_DATA, path);
            contentValues.put(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP1, entity.getTime());
            contentValues.put(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP2, position);
            resolver.insert(TableProvider.DEVICE_RAIL_INFO_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static final void insertValue(List<DeviceRailEntity> list,String position) {
        for (DeviceRailEntity entity:list) {
            insertValue(entity,position);
        }
    }

    public static final List<DeviceRailEntity> getValue(String device_id,String position){
        List<DeviceRailEntity> list = new ArrayList<DeviceRailEntity>();
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        Cursor cursor = resolver.query(TableProvider.DEVICE_RAIL_INFO_URI, null, TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_DEVICE_ID + "=? ",
                new String[]{BaseApplication.getInstances().getUser_id(), device_id}, TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_PK + " asc");
        if(cursor!=null && cursor.getCount()>0){
            while (cursor.moveToNext()){
                DeviceRailEntity entity = new DeviceRailEntity();
                String number = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_POINT_NUMBER));
                String path = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_POINT_DATA));
                String time = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP1));
                String pos = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP2));
                entity.setUserid(BaseApplication.getInstances().getUser_id());
                entity.setDeviceid(device_id);
                entity.setNumber(number);
                entity.setPath(path);
                entity.setTime(time);
                entity.setPosition(pos);
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

    public static final DeviceRailEntity getSpecificValue(String device_id,String position){
        DeviceRailEntity entity = new DeviceRailEntity();
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        Cursor cursor = resolver.query(TableProvider.DEVICE_RAIL_INFO_URI, null, TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_DEVICE_ID + "=? and "+TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP2 + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), device_id,position}, TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_PK + " asc");
        if(cursor!=null && cursor.getCount()>0){
            while (cursor.moveToNext()){
                String number = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_POINT_NUMBER));
                String path = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_POINT_DATA));
                String time = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP1));
                String pos = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP2));
                entity.setUserid(BaseApplication.getInstances().getUser_id());
                entity.setDeviceid(device_id);
                entity.setNumber(number);
                entity.setPath(path);
                entity.setTime(time);
                entity.setPosition(pos);
            }
            if(cursor!=null){
                cursor.close();
            }
            return entity;
        }else{
            if(cursor!=null){
                cursor.close();
            }
            return null;
        }
    }


    public static final List<DeviceRailEntity> getAllValue(String device_id){
        List<DeviceRailEntity> list = new ArrayList<DeviceRailEntity>();
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        Cursor cursor = resolver.query(TableProvider.DEVICE_RAIL_INFO_URI, null, TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_DEVICE_ID + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), device_id}, TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_PK + " asc");
        if(cursor!=null && cursor.getCount()>0){
            while (cursor.moveToNext()){
                DeviceRailEntity entity = new DeviceRailEntity();
                String number = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_POINT_NUMBER));
                String path = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_POINT_DATA));
                String time = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP1));
                String pos = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_BACKUP2));
                entity.setUserid(BaseApplication.getInstances().getUser_id());
                entity.setDeviceid(device_id);
                entity.setNumber(number);
                entity.setPath(path);
                entity.setTime(time);
                entity.setPosition(pos);
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
        Cursor cursor = resolver.query(TableProvider.DEVICE_RAIL_INFO_URI, null, null,null, TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_PK + " asc");
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

    public static final int getCount(String deviceid){
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        Cursor cursor = resolver.query(TableProvider.DEVICE_RAIL_INFO_URI, null, TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_DEVICE_ID + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), deviceid}, TableInfo.DEVICE_RAIL_INFO_TABLE_COLUMN_PK + " asc");
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
