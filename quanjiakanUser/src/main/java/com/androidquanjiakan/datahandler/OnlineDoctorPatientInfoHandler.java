package com.androidquanjiakan.datahandler;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.database.TableInfo;
import com.androidquanjiakan.database.TableInfo_ValueStub;
import com.androidquanjiakan.database.TableProvider;
import com.androidquanjiakan.entity.PatientInfoEntity;
import com.androidquanjiakan.util.CheckUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/16 0016.
 */
public class OnlineDoctorPatientInfoHandler {

    public static final int remove(String id,String name,String sex,String age) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        int number = 0;
        Cursor cursor = resolver.query(TableProvider.ONLINE_DOCTOR_PATIENT_INFO_URI, null, TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME + "=? and " +
                TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX + "=? and " +
                TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_PK + "=? and " +
                TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_AGE + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), name,sex,id,age},
                TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            number = resolver.delete(TableProvider.ONLINE_DOCTOR_PATIENT_INFO_URI, TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME + "=? and " +
                            TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX + "=? and " +
                            TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_PK + "=? and " +
                            TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_AGE + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(),name,sex,id,age});
        }
        if(cursor!=null){
            cursor.close();
        }
        return number;
    }

    public static final int removeAll() {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        int number = 0;
        Cursor cursor = resolver.query(TableProvider.ONLINE_DOCTOR_PATIENT_INFO_URI, null, TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID + "=? ",
                new String[]{BaseApplication.getInstances().getUser_id()},
                TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            number = resolver.delete(TableProvider.ONLINE_DOCTOR_PATIENT_INFO_URI, TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID + "=? ",
                    new String[]{BaseApplication.getInstances().getUser_id()});
        }
        if(cursor!=null){
            cursor.close();
        }
        return number;
    }

    public static final void insertValue(String name, String sex,String age) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        if (CheckUtil.isEmpty(name)) {
            name = "";
        }
        if (CheckUtil.isEmpty(sex)) {
            sex = TableInfo_ValueStub.ONLINE_DOCTOR_PATIENT_INFO_MALE;
        }
        if (CheckUtil.isEmpty(age)) {
            age = "";
        }
        Cursor cursor = resolver.query(TableProvider.ONLINE_DOCTOR_PATIENT_INFO_URI, null, TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME + "=? and " + TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX + "=? and " +
                        TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_AGE + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), name,sex,age},
                TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME, name);
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX, sex);
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_AGE, age);

            resolver.update(TableProvider.ONLINE_DOCTOR_PATIENT_INFO_URI, contentValues, TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME + "=? and " + TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX + "=? and " +
                            TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_AGE + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(), name,sex,age});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME, name);
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX, sex);
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_AGE, age);
            resolver.insert(TableProvider.ONLINE_DOCTOR_PATIENT_INFO_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static final void insertValue(PatientInfoEntity entity) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        String name = entity.getName();
        String sex = entity.getSex();
        String age = entity.getAge();
        if (CheckUtil.isEmpty(name)) {
            name = "";
        }
        if (CheckUtil.isEmpty(sex)) {
            sex = TableInfo_ValueStub.ONLINE_DOCTOR_PATIENT_INFO_MALE;
        }else if(TableInfo_ValueStub.ONLINE_DOCTOR_PATIENT_INFO_MALE.endsWith(sex) ||
                TableInfo_ValueStub.ONLINE_DOCTOR_PATIENT_INFO_FEMALE.endsWith(sex)){
        }else{
            sex = TableInfo_ValueStub.ONLINE_DOCTOR_PATIENT_INFO_FEMALE;
        }
        if (CheckUtil.isEmpty(age)) {
            age = "";
        }
        Cursor cursor = resolver.query(TableProvider.ONLINE_DOCTOR_PATIENT_INFO_URI, null, TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME + "=? and " + TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX + "=? and " +
                        TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_AGE + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), name,sex,age},
                TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME, name);
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX, sex);
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_AGE, age);

            resolver.update(TableProvider.ONLINE_DOCTOR_PATIENT_INFO_URI, contentValues, TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME + "=? and " + TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX + "=? and " +
                            TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_AGE + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(), name,sex,age});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME, name);
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX, sex);
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_AGE, age);
            resolver.insert(TableProvider.ONLINE_DOCTOR_PATIENT_INFO_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static final void insertValue(List<PatientInfoEntity> list) {
        for (PatientInfoEntity entity:list) {
            insertValue(entity);
        }
    }

    public static final void updateValue(String name, String sex,String age) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        if (CheckUtil.isEmpty(name)) {
            name = "";
        }
        if (CheckUtil.isEmpty(sex)) {
            sex = TableInfo_ValueStub.ONLINE_DOCTOR_PATIENT_INFO_MALE;
        }
        if (CheckUtil.isEmpty(age)) {
            age = "";
        }
        Cursor cursor = resolver.query(TableProvider.ONLINE_DOCTOR_PATIENT_INFO_URI, null, TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME + "=? and " +
                        TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX + "=? and " +
                        TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_AGE + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), name,sex,age},
                TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME, name);
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX, sex);
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_AGE, age);

            resolver.update(TableProvider.ONLINE_DOCTOR_PATIENT_INFO_URI, contentValues, TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME + "=? and " +
                            TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX + "=? and " +
                            TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_AGE + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(), name,sex,age});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME, name);
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX, sex);
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_AGE, age);
            resolver.insert(TableProvider.ONLINE_DOCTOR_PATIENT_INFO_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static final void updateValue(PatientInfoEntity entity) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        Cursor cursor = resolver.query(TableProvider.ONLINE_DOCTOR_PATIENT_INFO_URI, null,
//                TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID + "=? and " +
//                        TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME + "=? and " +
//                        TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX + "=? and " +
                        TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_PK + "=?",
                new String[]{/*BaseApplication.getInstances().getUser_id(),*/ entity.getId()},
                TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME, entity.getName());
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX, entity.getSex());
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_AGE, entity.getAge());

            resolver.update(TableProvider.ONLINE_DOCTOR_PATIENT_INFO_URI, contentValues,
//                    TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID + "=? and " +
//                            TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME + "=? and " +
//                            TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX + "=? and " +
                            TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_PK + "=?",
                    new String[]{/*BaseApplication.getInstances().getUser_id(),*/ entity.getId()});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME, entity.getName());
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX, entity.getSex());
            contentValues.put(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_AGE, entity.getAge());
            resolver.insert(TableProvider.ONLINE_DOCTOR_PATIENT_INFO_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static final PatientInfoEntity getValue(String name){
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        if (CheckUtil.isEmpty(name)) {
            name = "";
        }
        Cursor cursor = resolver.query(TableProvider.ONLINE_DOCTOR_PATIENT_INFO_URI, null, TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), name}, TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_PK + " asc");

        if(cursor!=null && cursor.getCount()>0){
            if(cursor.moveToNext()){
                PatientInfoEntity entity = new PatientInfoEntity();
                String id = cursor.getString(cursor.getColumnIndex(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_PK));
                String sex = cursor.getString(cursor.getColumnIndex(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX));
                String age = cursor.getString(cursor.getColumnIndex(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_AGE));

                entity.setId(id);
                entity.setSex(sex);
                entity.setName(name);
                entity.setAge(age);

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

    public static final List<PatientInfoEntity> getAllValue(){
        List<PatientInfoEntity> list = new ArrayList<PatientInfoEntity>();
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        Cursor cursor = resolver.query(TableProvider.ONLINE_DOCTOR_PATIENT_INFO_URI, null, TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_USER_ID + "=? ",
                new String[]{BaseApplication.getInstances().getUser_id()},
                TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_PK + " asc");
        if(cursor!=null && cursor.getCount()>0){
            while (cursor.moveToNext()){
                PatientInfoEntity entity = new PatientInfoEntity();
                String id = cursor.getString(cursor.getColumnIndex(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_PK));
                String name = cursor.getString(cursor.getColumnIndex(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_NAME));
                String sex = cursor.getString(cursor.getColumnIndex(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_SEX));
                String age = cursor.getString(cursor.getColumnIndex(TableInfo.ONLINE_DOCTOR_PATIENT_INFO_TABLE_COLUMN_AGE));
                entity.setId(id);
                entity.setName(name);
                entity.setSex(sex);
                entity.setAge(age);
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
