package com.androidquanjiakan.datahandler;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.database.TableInfo;
import com.androidquanjiakan.database.TableInfo_ValueStub;
import com.androidquanjiakan.database.TableProvider;
import com.androidquanjiakan.entity.DeliverAddressEntity;
import com.androidquanjiakan.util.CheckUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/16 0016.
 */
public class DefaultReceiveAddressHandler {

    public static final int remove(String phone, String name) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        int number = 0;
        Cursor cursor = resolver.query(TableProvider.DEFAULT_RECEIVE_ADDRESS_URI, null, TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE + "=? and " + TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_NAME + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), phone, name}, TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            number = resolver.delete(TableProvider.DEFAULT_RECEIVE_ADDRESS_URI, TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE + "=? and " + TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_NAME + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(), phone, name});
        }
        if(cursor!=null){
            cursor.close();
        }
        return number;
    }

    public static final int remove() {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        int number = 0;
        Cursor cursor = resolver.query(TableProvider.DEFAULT_RECEIVE_ADDRESS_URI, null, TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID + "=?",
                new String[]{BaseApplication.getInstances().getUser_id()}, TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            number = resolver.delete(TableProvider.DEFAULT_RECEIVE_ADDRESS_URI, TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id()});
        }
        if(cursor!=null){
            cursor.close();
        }
        return number;
    }

    public static final void insertValue(String id,String phone, String name,String address) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        if (CheckUtil.isEmpty(id)) {
            id = "";
        }
        if (CheckUtil.isEmpty(phone)) {
            phone = "";
        }
        if (CheckUtil.isEmpty(name)) {
            name = "";
        }
        if (CheckUtil.isEmpty(address)) {
            address = "";
        }
//        Cursor cursor = resolver.query(TableProvider.DEFAULT_RECEIVE_ADDRESS_URI, null, TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID + "=? and " +
//                        TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE + "=? and " + TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_NAME + "=?",
//                new String[]{BaseApplication.getInstances().getUser_id(), phone, name}, TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PK + " asc");
        Cursor cursor = resolver.query(TableProvider.DEFAULT_RECEIVE_ADDRESS_URI, null, TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID + "=? ",
                new String[]{BaseApplication.getInstances().getUser_id()}, TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_ADDRESSID, id);
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE, phone);
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_NAME, name);
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_ADDRESS,address);
            resolver.update(TableProvider.DEFAULT_RECEIVE_ADDRESS_URI, contentValues,TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID + "=? ",
                    new String[]{BaseApplication.getInstances().getUser_id()});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_ADDRESSID, id);
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE, phone);
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_NAME, name);
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_ADDRESS,address);
            resolver.insert(TableProvider.DEFAULT_RECEIVE_ADDRESS_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static final void insertValue(DeliverAddressEntity entity) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        String id = entity.getId();
        String phone = entity.getMobile();
        String name = entity.getName();
        String address = entity.getAddress();
        if (CheckUtil.isEmpty(id)) {
            id = "";
        }
        if (CheckUtil.isEmpty(phone)) {
            phone = "";
        }
        if (CheckUtil.isEmpty(name)) {
            name = "";
        }
        if (CheckUtil.isEmpty(address)) {
            address = "";
        }
//        Cursor cursor = resolver.query(TableProvider.DEFAULT_RECEIVE_ADDRESS_URI, null, TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID + "=? and " +
//                        TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE + "=? and " + TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_NAME + "=?",
//                new String[]{BaseApplication.getInstances().getUser_id(), phone, name}, TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PK + " asc");
        Cursor cursor = resolver.query(TableProvider.DEFAULT_RECEIVE_ADDRESS_URI, null, TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID + "=? ",
                new String[]{BaseApplication.getInstances().getUser_id()}, TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_ADDRESSID, id);
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE, phone);
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_NAME, name);
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_ADDRESS,address);
            resolver.update(TableProvider.DEFAULT_RECEIVE_ADDRESS_URI, contentValues,TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID + "=? ",
                    new String[]{BaseApplication.getInstances().getUser_id()});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_ADDRESSID, id);
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE, phone);
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_NAME, name);
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_ADDRESS,address);
            resolver.insert(TableProvider.DEFAULT_RECEIVE_ADDRESS_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }
    public static final void insertValue(List<DeliverAddressEntity> entity) {
        for (DeliverAddressEntity temp:entity) {
            insertValue(temp);
        }
    }

    public static final void updateValue(String phone, String name,String address) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        if (CheckUtil.isEmpty(phone)) {
            phone = "";
        }
        if (CheckUtil.isEmpty(name)) {
            name = "";
        }
        if (CheckUtil.isEmpty(address)) {
            address = "";
        }
        Cursor cursor = resolver.query(TableProvider.DEFAULT_RECEIVE_ADDRESS_URI, null, TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID + "=? ",
                new String[]{BaseApplication.getInstances().getUser_id()}, TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE, phone);
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_NAME, name);
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_ADDRESS,address);
            resolver.update(TableProvider.DEFAULT_RECEIVE_ADDRESS_URI, contentValues,TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID + "=? ",
                    new String[]{BaseApplication.getInstances().getUser_id()});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE, phone);
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_NAME, name);
            contentValues.put(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_ADDRESS,address);
            resolver.insert(TableProvider.DEFAULT_RECEIVE_ADDRESS_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static final List<DeliverAddressEntity> getAllValue() {
        List<DeliverAddressEntity> list = new ArrayList<DeliverAddressEntity>();
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        Cursor cursor = resolver.query(TableProvider.DEFAULT_RECEIVE_ADDRESS_URI, null, TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_USER_ID + "=? ",
                new String[]{BaseApplication.getInstances().getUser_id()}, TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                DeliverAddressEntity entity = new DeliverAddressEntity();
                String id = cursor.getString(cursor.getColumnIndex(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_ADDRESSID));
                String name = cursor.getString(cursor.getColumnIndex(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE));
                String value = cursor.getString(cursor.getColumnIndex(TableInfo.DEFAULT_RECEIVE_ADDRESS_TABLE_COLUMN_PHONE_ADDRESS));
                entity.setId(id);
                entity.setName(name);
                entity.setMobile(phone);
                entity.setAddress(value);
                entity.setStatus(TableInfo_ValueStub.DEFAULT_RECEIVE_ADDRESS);
                list.add(entity) ;
            }
            if(cursor!=null){
                cursor.close();
            }
            return list;
        } else {
            if(cursor!=null){
                cursor.close();
            }
            return list;
        }
    }

}
