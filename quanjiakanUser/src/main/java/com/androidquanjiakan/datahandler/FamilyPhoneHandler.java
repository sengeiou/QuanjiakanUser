package com.androidquanjiakan.datahandler;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.database.TableInfo;
import com.androidquanjiakan.database.TableInfo_ValueStub;
import com.androidquanjiakan.database.TableProvider;
import com.androidquanjiakan.entity.FamilyPhoneNumberEntity;
import com.androidquanjiakan.util.CheckUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/16 0016.
 */
public class FamilyPhoneHandler {

    public static final int remove(String phone, String name) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        int number = 0;
        Cursor cursor = resolver.query(TableProvider.FAMILY_PHONE_URI, null, TableInfo.FAMILY_PHONE_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE + "=? and " + TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_NAME + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), phone, name}, TableInfo.FAMILY_PHONE_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            number = resolver.delete(TableProvider.FAMILY_PHONE_URI, TableInfo.FAMILY_PHONE_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE + "=? and " + TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_NAME + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(), phone, name});
        }
        if(cursor!=null){
            cursor.close();
        }
        return number;
    }

    public static final void insertValue(String phone, String name,boolean isAdd) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        if (CheckUtil.isEmpty(phone)) {
            phone = "";
        }
        if (CheckUtil.isEmpty(name)) {
            name = "";
        }
        Cursor cursor = resolver.query(TableProvider.FAMILY_PHONE_URI, null, TableInfo.FAMILY_PHONE_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE + "=? and " + TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_NAME + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), phone, name}, TableInfo.FAMILY_PHONE_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE, phone);
            contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_NAME, name);
            if (isAdd){
                contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE,
                        TableInfo_ValueStub.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE_VALUE_ADDED);
            }else{
                contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE,
                        TableInfo_ValueStub.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE_VALUE_UNADD);
            }
            resolver.update(TableProvider.FAMILY_PHONE_URI, contentValues, TableInfo.FAMILY_PHONE_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE + "=? and " + TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_NAME + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(), phone, name});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE, phone);
            contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_NAME, name);
            if (isAdd){
                contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE,
                        TableInfo_ValueStub.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE_VALUE_ADDED);
            }else{
                contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE,
                        TableInfo_ValueStub.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE_VALUE_UNADD);
            }
            resolver.insert(TableProvider.FAMILY_PHONE_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static final void insertValue(FamilyPhoneNumberEntity entity) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        String phone = entity.getPhoneNumber();
        String name = entity.getName();
        boolean isAdd = entity.isAdd();
        if (CheckUtil.isEmpty(phone)) {
            phone = "";
        }
        if (CheckUtil.isEmpty(name)) {
            name = "";
        }
        Cursor cursor = resolver.query(TableProvider.FAMILY_PHONE_URI, null, TableInfo.FAMILY_PHONE_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE + "=? and " + TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_NAME + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), phone, name}, TableInfo.FAMILY_PHONE_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE, phone);
            contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_NAME, name);
            if (isAdd){
                contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE,
                        TableInfo_ValueStub.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE_VALUE_ADDED);
            }else{
                contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE,
                        TableInfo_ValueStub.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE_VALUE_UNADD);
            }
            resolver.update(TableProvider.FAMILY_PHONE_URI, contentValues, TableInfo.FAMILY_PHONE_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE + "=? and " + TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_NAME + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(), phone, name});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE, phone);
            contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_NAME, name);
            if (isAdd){
                contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE,
                        TableInfo_ValueStub.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE_VALUE_ADDED);
            }else{
                contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE,
                        TableInfo_ValueStub.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE_VALUE_UNADD);
            }
            resolver.insert(TableProvider.FAMILY_PHONE_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }
    public static final void insertValue(List<FamilyPhoneNumberEntity> entity) {
        for (FamilyPhoneNumberEntity temp:entity) {
            insertValue(temp);
        }
    }

    public static final void updateValue(String phone, String name,boolean isAdd) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        if (CheckUtil.isEmpty(phone)) {
            phone = "";
        }
        if (CheckUtil.isEmpty(name)) {
            name = "";
        }
        Cursor cursor = resolver.query(TableProvider.FAMILY_PHONE_URI, null, TableInfo.FAMILY_PHONE_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE + "=? and " + TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_NAME + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), phone, name}, TableInfo.FAMILY_PHONE_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE, phone);
            contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_NAME, name);
            if (isAdd){
                contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE,
                        TableInfo_ValueStub.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE_VALUE_ADDED);
            }else{
                contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE,
                        TableInfo_ValueStub.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE_VALUE_UNADD);
            }
            resolver.update(TableProvider.FAMILY_PHONE_URI, contentValues, TableInfo.FAMILY_PHONE_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE + "=? and " + TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_NAME + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(), phone, name});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE, phone);
            contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_NAME, name);
            if (isAdd){
                contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE,
                        TableInfo_ValueStub.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE_VALUE_ADDED);
            }else{
                contentValues.put(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE,
                        TableInfo_ValueStub.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE_VALUE_UNADD);
            }
            resolver.insert(TableProvider.FAMILY_PHONE_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static final boolean getStateValue(String phone, String name) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        if (CheckUtil.isEmpty(phone)) {
            phone = "";
        }
        if (CheckUtil.isEmpty(name)) {
            name = "";
        }
        Cursor cursor = resolver.query(TableProvider.FAMILY_PHONE_URI, null, TableInfo.FAMILY_PHONE_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE + "=? and " + TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_NAME + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), phone, name}, TableInfo.FAMILY_PHONE_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToNext()) {
                String value = cursor.getString(cursor.getColumnIndex(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE));
                if(cursor!=null){
                    cursor.close();
                }
                return TableInfo_ValueStub.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE_VALUE_ADDED.equals(value);
            } else {
                if(cursor!=null){
                    cursor.close();
                }
                return false;
            }
        } else {
            if(cursor!=null){
                cursor.close();
            }
            return false;
        }
    }

    public static final List<FamilyPhoneNumberEntity> getAllValue() {
        List<FamilyPhoneNumberEntity> list = new ArrayList<FamilyPhoneNumberEntity>();
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        Cursor cursor = resolver.query(TableProvider.FAMILY_PHONE_URI, null, null,null, TableInfo.FAMILY_PHONE_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                FamilyPhoneNumberEntity entity = new FamilyPhoneNumberEntity();
                String name = cursor.getString(cursor.getColumnIndex(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE));
                String value = cursor.getString(cursor.getColumnIndex(TableInfo.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE));
                entity.setName(name);
                entity.setPhoneNumber(phone);
                entity.setAdd(TableInfo_ValueStub.FAMILY_PHONE_TABLE_COLUMN_PHONE_ADD_STATE_VALUE_ADDED.equals(value));
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

    private static final String[] PHONES_PROJECTION = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.Contacts.Photo.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID};
    /**
     * 联系人显示名称
     **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    /**
     * 电话号码
     **/
    private static final int PHONES_NUMBER_INDEX = 1;

    public static List<FamilyPhoneNumberEntity> getPhoneContacts() {
        List<FamilyPhoneNumberEntity> list = new ArrayList<FamilyPhoneNumberEntity>();
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, null);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                //得到手机号码
                String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
                //当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;
                //得到联系人名称
                String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
                FamilyPhoneNumberEntity object = new FamilyPhoneNumberEntity();
                object.setPhoneNumber(phoneNumber);
                object.setName(contactName);
                list.add(object);
            }
            phoneCursor.close();
        }
        return list;
    }

}
