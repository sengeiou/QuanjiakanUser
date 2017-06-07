package com.androidquanjiakan.datahandler;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.database.TableInfo;
import com.androidquanjiakan.database.TableInfo_ValueStub;
import com.androidquanjiakan.database.TableProvider;
import com.androidquanjiakan.entity.PatientProblemInfoEntity;
import com.androidquanjiakan.util.CheckUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/16 0016.
 */
public class PatientProblemInfoHandler {

    public static final int remove(String id) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        int number = 0;
        Cursor cursor = resolver.query(TableProvider.PATIENT_PROBLEM_INFO_URI, null,
                TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(),id},
                TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            number = resolver.delete(TableProvider.PATIENT_PROBLEM_INFO_URI,
                            TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(),id});
        }
        if(cursor!=null){
            cursor.close();
        }
        return number;
    }

    public static final int removeAll() {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        int number = 0;
        Cursor cursor = resolver.query(TableProvider.PATIENT_PROBLEM_INFO_URI, null,
                TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID + "=? ",
                new String[]{BaseApplication.getInstances().getUser_id()},
                TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            number = resolver.delete(TableProvider.PATIENT_PROBLEM_INFO_URI,
                    TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID + "=? ",
                    new String[]{BaseApplication.getInstances().getUser_id()});
        }
        if(cursor!=null){
            cursor.close();
        }
        return number;
    }

    public static final void insertValue(String id,String name, String sex,String age,
                                         String target_id,String title,String date,String clinic) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        if (CheckUtil.isEmpty(id)) {
            id = "";
        }
        if (CheckUtil.isEmpty(name)) {
            name = "";
        }
        if (CheckUtil.isEmpty(sex)) {
            sex = TableInfo_ValueStub.PATIENT_PROBLEM_INFO_MALE;
        }
        if (CheckUtil.isEmpty(age)) {
            age = "";
        }


        if (CheckUtil.isEmpty(target_id)) {
            target_id = "";
        }
        if (CheckUtil.isEmpty(title)) {
            title = "";
        }
        if (CheckUtil.isEmpty(date)) {
            age = System.currentTimeMillis()+"";
        }
        if (CheckUtil.isEmpty(clinic)) {
            clinic = "";
        }
        Cursor cursor = resolver.query(TableProvider.PATIENT_PROBLEM_INFO_URI, null,
                        TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), id},
                TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_NAME, name);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_SEX, sex);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_AGE, age);

            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID, id);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_TARGET, target_id);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_TITLE, title);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_DATE, date);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_CLINIC, clinic);

            resolver.update(TableProvider.PATIENT_PROBLEM_INFO_URI, contentValues,
                    TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(), id});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_NAME, name);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_SEX, sex);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_AGE, age);

            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID, id);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_TARGET, target_id);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_TITLE, title);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_DATE, date);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_CLINIC, clinic);
            resolver.insert(TableProvider.PATIENT_PROBLEM_INFO_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static final void insertValue(PatientProblemInfoEntity entity) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        String name = entity.getName();
        String sex = entity.getSex();
        String age = entity.getAge();

        String id = entity.getId();
        String target_id = entity.getTarget_id();
        String title = entity.getTitle();
        String date = entity.getDate();
        String clinic = entity.getClinic();
        if (CheckUtil.isEmpty(id)) {
            id = "";
        }
        if (CheckUtil.isEmpty(name)) {
            name = "";
        }
        if (CheckUtil.isEmpty(sex)) {
            sex = TableInfo_ValueStub.PATIENT_PROBLEM_INFO_MALE;
        }
        if (CheckUtil.isEmpty(age)) {
            age = "";
        }


        if (CheckUtil.isEmpty(target_id)) {
            target_id = "";
        }
        if (CheckUtil.isEmpty(title)) {
            title = "";
        }
        if (CheckUtil.isEmpty(date)) {
            age = System.currentTimeMillis()+"";
        }
        if (CheckUtil.isEmpty(clinic)) {
            clinic = "";
        }
        Cursor cursor = resolver.query(TableProvider.PATIENT_PROBLEM_INFO_URI, null,
                TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), id},
                TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_NAME, name);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_SEX, sex);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_AGE, age);

            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID, id);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_TARGET, target_id);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_TITLE, title);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_DATE, date);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_CLINIC, clinic);

            resolver.update(TableProvider.PATIENT_PROBLEM_INFO_URI, contentValues,
                    TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(), id});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_NAME, name);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_SEX, sex);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_AGE, age);

            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID, id);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_TARGET, target_id);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_TITLE, title);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_DATE, date);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_CLINIC, clinic);
            resolver.insert(TableProvider.PATIENT_PROBLEM_INFO_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static final void insertValue(List<PatientProblemInfoEntity> list) {
        for (PatientProblemInfoEntity entity:list) {
            insertValue(entity);
        }
    }

    public static final void updateValue(String id,String name, String sex,String age,
                                         String target_id,String title,String date,String clinic) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        if (CheckUtil.isEmpty(id)) {
            id = "";
        }
        if (CheckUtil.isEmpty(name)) {
            name = "";
        }
        if (CheckUtil.isEmpty(sex)) {
            sex = TableInfo_ValueStub.PATIENT_PROBLEM_INFO_MALE;
        }
        if (CheckUtil.isEmpty(age)) {
            age = "";
        }


        if (CheckUtil.isEmpty(target_id)) {
            target_id = "";
        }
        if (CheckUtil.isEmpty(title)) {
            title = "";
        }
        if (CheckUtil.isEmpty(date)) {
            age = System.currentTimeMillis()+"";
        }
        if (CheckUtil.isEmpty(clinic)) {
            clinic = "";
        }
        Cursor cursor = resolver.query(TableProvider.PATIENT_PROBLEM_INFO_URI, null,
                TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), id},
                TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_NAME, name);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_SEX, sex);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_AGE, age);

            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID, id);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_TARGET, target_id);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_TITLE, title);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_DATE, date);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_CLINIC, clinic);

            resolver.update(TableProvider.PATIENT_PROBLEM_INFO_URI, contentValues,
                    TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(), id});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_NAME, name);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_SEX, sex);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_AGE, age);

            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID, id);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_TARGET, target_id);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_TITLE, title);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_DATE, date);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_CLINIC, clinic);
            resolver.insert(TableProvider.PATIENT_PROBLEM_INFO_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static final void updateValue(PatientProblemInfoEntity entity) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        String name = entity.getName();
        String sex = entity.getSex();
        String age = entity.getAge();

        String id = entity.getId();
        String target_id = entity.getTarget_id();
        String title = entity.getTitle();
        String date = entity.getDate();
        String clinic = entity.getClinic();
        if (CheckUtil.isEmpty(id)) {
            id = "";
        }
        if (CheckUtil.isEmpty(name)) {
            name = "";
        }
        if (CheckUtil.isEmpty(sex)) {
            sex = TableInfo_ValueStub.PATIENT_PROBLEM_INFO_MALE;
        }
        if (CheckUtil.isEmpty(age)) {
            age = "";
        }


        if (CheckUtil.isEmpty(target_id)) {
            target_id = "";
        }
        if (CheckUtil.isEmpty(title)) {
            title = "";
        }
        if (CheckUtil.isEmpty(date)) {
            age = System.currentTimeMillis()+"";
        }
        if (CheckUtil.isEmpty(clinic)) {
            clinic = "";
        }
        Cursor cursor = resolver.query(TableProvider.PATIENT_PROBLEM_INFO_URI, null,
                TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), id},
                TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_NAME, name);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_SEX, sex);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_AGE, age);

            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID, id);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_TARGET, target_id);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_TITLE, title);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_DATE, date);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_CLINIC, clinic);

            resolver.update(TableProvider.PATIENT_PROBLEM_INFO_URI, contentValues,
                    TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(), id});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID, BaseApplication.getInstances().getUser_id());
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_NAME, name);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_SEX, sex);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_AGE, age);

            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID, id);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_TARGET, target_id);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_TITLE, title);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_DATE, date);
            contentValues.put(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_CLINIC, clinic);
            resolver.insert(TableProvider.PATIENT_PROBLEM_INFO_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static final PatientProblemInfoEntity getValue(String id){
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        if (CheckUtil.isEmpty(id)) {
            id = "";
        }
        Cursor cursor = resolver.query(TableProvider.PATIENT_PROBLEM_INFO_URI, null,
                TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), id},
                TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PK + " asc");

        if(cursor!=null && cursor.getCount()>0){
            if(cursor.moveToNext()){
                PatientProblemInfoEntity entity = new PatientProblemInfoEntity();
                String sex = cursor.getString(cursor.getColumnIndex(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_SEX));
                String age = cursor.getString(cursor.getColumnIndex(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_AGE));
                String name = cursor.getString(cursor.getColumnIndex(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_NAME));
                String clinic = cursor.getString(cursor.getColumnIndex(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_CLINIC));

                String date = cursor.getString(cursor.getColumnIndex(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_DATE));
                String title = cursor.getString(cursor.getColumnIndex(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_TITLE));
                String targetid = cursor.getString(cursor.getColumnIndex(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_TARGET));

                entity.setUser_id(BaseApplication.getInstances().getUser_id());
                entity.setId(id);

                entity.setSex(sex);
                entity.setName(name);
                entity.setAge(age);

                entity.setClinic(clinic);
                entity.setDate(date);
                entity.setTitle(title);
                entity.setTarget_id(targetid);

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

    public static final List<PatientProblemInfoEntity> getAllValue(){
        List<PatientProblemInfoEntity> list = new ArrayList<PatientProblemInfoEntity>();
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        Cursor cursor = resolver.query(TableProvider.PATIENT_PROBLEM_INFO_URI, null,
                TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_USER_ID + "=? ",
                new String[]{BaseApplication.getInstances().getUser_id()},
                TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PK + " asc");
        if(cursor!=null && cursor.getCount()>0){
            while (cursor.moveToNext()){
                PatientProblemInfoEntity entity = new PatientProblemInfoEntity();
                String id = cursor.getString(cursor.getColumnIndex(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_PROBLEM_ID));
                String sex = cursor.getString(cursor.getColumnIndex(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_SEX));
                String age = cursor.getString(cursor.getColumnIndex(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_AGE));
                String name = cursor.getString(cursor.getColumnIndex(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_NAME));
                String clinic = cursor.getString(cursor.getColumnIndex(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_CLINIC));

                String date = cursor.getString(cursor.getColumnIndex(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_DATE));
                String title = cursor.getString(cursor.getColumnIndex(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_TITLE));
                String targetid = cursor.getString(cursor.getColumnIndex(TableInfo.PATIENT_PROBLEM_INFO_TABLE_COLUMN_TARGET));

                entity.setUser_id(BaseApplication.getInstances().getUser_id());
                entity.setId(id);

                entity.setSex(sex);
                entity.setName(name);
                entity.setAge(age);

                entity.setClinic(clinic);
                entity.setDate(date);
                entity.setTitle(title);
                entity.setTarget_id(targetid);

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
