package com.androidquanjiakan.datahandler;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.database.TableInfo;
import com.androidquanjiakan.database.TableProvider;
import com.androidquanjiakan.entity.SceneModelEntity;
import com.androidquanjiakan.util.CheckUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/16 0016.
 */
public class SceneModelHandler {

    public static final int remove(String deviceID) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        int number = 0;
        Cursor cursor = resolver.query(TableProvider.DEVICE_SCENE_MODEL_URI, null, TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_USER_ID + "=? and " +
                TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_ID + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), deviceID},
                TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            number = resolver.delete(TableProvider.DEVICE_SCENE_MODEL_URI, TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_USER_ID + "=? and " +
                    TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_ID + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(),deviceID});
        }
        if(cursor!=null){
            cursor.close();
        }
        return number;
    }

    public static final void insertValue(String deviceID, String model) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        if (CheckUtil.isEmpty(deviceID)) {
            deviceID = "";
        }
        if (CheckUtil.isEmpty(model)) {
            model = "";
        }
        Cursor cursor = resolver.query(TableProvider.DEVICE_SCENE_MODEL_URI, null, TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_ID + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), deviceID}, TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_ID, deviceID);
            contentValues.put(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_SCENE_MODEL, model);
            resolver.update(TableProvider.DEVICE_SCENE_MODEL_URI, contentValues, TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_ID + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(), deviceID});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_ID, deviceID);
            contentValues.put(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_SCENE_MODEL, model);
            resolver.insert(TableProvider.DEVICE_SCENE_MODEL_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static final void insertValue(SceneModelEntity entity) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        String deviceID = entity.getDeviceID();
        String model = entity.getModel();
        if (CheckUtil.isEmpty(deviceID)) {
            deviceID = "";
        }
        if (CheckUtil.isEmpty(model)) {
            model = "";
        }
        Cursor cursor = resolver.query(TableProvider.DEVICE_SCENE_MODEL_URI, null, TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_ID + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), deviceID}, TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_ID, deviceID);
            contentValues.put(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_SCENE_MODEL, model);
            resolver.update(TableProvider.DEVICE_SCENE_MODEL_URI, contentValues, TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_ID + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(), deviceID});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_ID, deviceID);
            contentValues.put(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_SCENE_MODEL, model);
            resolver.insert(TableProvider.DEVICE_SCENE_MODEL_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static final void insertValue(List<SceneModelEntity> list) {
        for (SceneModelEntity entity:list) {
            insertValue(entity);
        }
    }

    public static final void updateValue(String deviceID, String model) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        if (CheckUtil.isEmpty(deviceID)) {
            deviceID = "";
        }
        if (CheckUtil.isEmpty(model)) {
            model = "";
        }
        Cursor cursor = resolver.query(TableProvider.DEVICE_SCENE_MODEL_URI, null, TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_ID + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), deviceID}, TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_ID, deviceID);
            contentValues.put(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_SCENE_MODEL, model);
            resolver.update(TableProvider.DEVICE_SCENE_MODEL_URI, contentValues, TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_ID + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(), deviceID});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_ID, deviceID);
            contentValues.put(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_SCENE_MODEL, model);
            resolver.insert(TableProvider.DEVICE_SCENE_MODEL_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static final SceneModelEntity getValue(String deviceID){
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        if (CheckUtil.isEmpty(deviceID)) {
            deviceID = "";
        }
        Cursor cursor = resolver.query(TableProvider.DEVICE_SCENE_MODEL_URI, null, TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_ID + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), deviceID}, TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_PK + " asc");

        if(cursor!=null && cursor.getCount()>0){
            if(cursor.moveToNext()){
                SceneModelEntity entity = new SceneModelEntity();
                String name = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_SCENE_MODEL));
                entity.setDeviceID(deviceID);
                entity.setModel(name);
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
        }else{
            if(cursor!=null){
                cursor.close();
            }
            return null;
        }
    }

    public static final List<SceneModelEntity> getAllValue(){
        List<SceneModelEntity> list = new ArrayList<SceneModelEntity>();
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        Cursor cursor = resolver.query(TableProvider.DEVICE_SCENE_MODEL_URI, null, null,null, TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_PK + " asc");
        if(cursor!=null && cursor.getCount()>0){
            while (cursor.moveToNext()){
                SceneModelEntity entity = new SceneModelEntity();
                String deviceID = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_ID));
                String name = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_SCENE_MODEL_TABLE_COLUMN_DEVICE_SCENE_MODEL));
                entity.setDeviceID(deviceID);
                entity.setModel(name);
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

}
