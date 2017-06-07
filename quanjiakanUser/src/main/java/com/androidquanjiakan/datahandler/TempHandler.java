package com.androidquanjiakan.datahandler;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.database.TableInfo;
import com.androidquanjiakan.database.TableProvider;
import com.androidquanjiakan.util.CheckUtil;

/**
 * Created by Administrator on 2016/6/16 0016.
 */
public class TempHandler {

    public static final int remove(String key) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        int number = 0;
        Cursor cursor = resolver.query(TableProvider.TEMP_URI, null, TableInfo.TEMP_TABLE_COLUMN_USER_ID + "=? and " + TableInfo.TEMP_TABLE_COLUMN_INFO_KEY + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), key}, TableInfo.TEMP_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            number = resolver.delete(TableProvider.TEMP_URI, TableInfo.TEMP_TABLE_COLUMN_USER_ID + "=? and " + TableInfo.TEMP_TABLE_COLUMN_INFO_KEY + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(),key});
        }
        if(cursor!=null){
            cursor.close();
        }
        return number;
    }

    public static final void insertValue(String key, String value) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        if (CheckUtil.isEmpty(key)) {
            key = "";
        }
        if (CheckUtil.isEmpty(value)) {
            value = "";
        }
        Cursor cursor = resolver.query(TableProvider.TEMP_URI, null, TableInfo.TEMP_TABLE_COLUMN_USER_ID + "=? and " + TableInfo.TEMP_TABLE_COLUMN_INFO_KEY + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), key}, TableInfo.TEMP_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.TEMP_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.TEMP_TABLE_COLUMN_INFO_KEY, key);
            contentValues.put(TableInfo.TEMP_TABLE_COLUMN_INFO_VALUE, value);
            resolver.update(TableProvider.TEMP_URI, contentValues, TableInfo.TEMP_TABLE_COLUMN_USER_ID + "=? and " + TableInfo.TEMP_TABLE_COLUMN_INFO_KEY + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(), key});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.TEMP_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.TEMP_TABLE_COLUMN_INFO_KEY, key);
            contentValues.put(TableInfo.TEMP_TABLE_COLUMN_INFO_VALUE, value);
            resolver.insert(TableProvider.TEMP_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static final String getValue(String key){
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        if (CheckUtil.isEmpty(key)) {
            key = "";
        }
        Cursor cursor = resolver.query(TableProvider.TEMP_URI, null, TableInfo.TEMP_TABLE_COLUMN_USER_ID + "=? and " + TableInfo.TEMP_TABLE_COLUMN_INFO_KEY + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), key}, TableInfo.TEMP_TABLE_COLUMN_PK + " asc");
        if(cursor!=null && cursor.getCount()>0){
            if(cursor.moveToNext()){
                String value = cursor.getString(cursor.getColumnIndex(TableInfo.TEMP_TABLE_COLUMN_INFO_VALUE));
                if(cursor!=null){
                    cursor.close();
                }
                return value;
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

}
