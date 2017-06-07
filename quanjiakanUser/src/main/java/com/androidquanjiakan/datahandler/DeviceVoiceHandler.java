package com.androidquanjiakan.datahandler;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.database.TableInfo;
import com.androidquanjiakan.database.TableInfo_ValueStub;
import com.androidquanjiakan.database.TableProvider;
import com.androidquanjiakan.entity.DeviceVoiceEntity;
import com.androidquanjiakan.util.CheckUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/16 0016.
 */
public class DeviceVoiceHandler {

    public static final int remove(String deviceID, String time) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        int number = 0;
        Cursor cursor = resolver.query(TableProvider.DEVICE_VOICE_INFO_URI, null, TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_ID + "=? and " + TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP1 + " =?",
                new String[]{BaseApplication.getInstances().getUser_id(), deviceID, time},
                TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            number = resolver.delete(TableProvider.DEVICE_VOICE_INFO_URI, TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_ID + "=? and " + TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP1 + " =?",
                    new String[]{BaseApplication.getInstances().getUser_id(), deviceID, time});
        }
        if (cursor != null) {
            cursor.close();
        }
        return number;
    }

    public static final int remove(String deviceID) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        int number = 0;
        Cursor cursor = resolver.query(TableProvider.DEVICE_VOICE_INFO_URI, null, TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_ID + " =?",
                new String[]{BaseApplication.getInstances().getUser_id(), deviceID},
                TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            number = resolver.delete(TableProvider.DEVICE_VOICE_INFO_URI, TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_ID + " =?",
                    new String[]{BaseApplication.getInstances().getUser_id(), deviceID});
        }
        if (cursor != null) {
            cursor.close();
        }
        return number;
    }

    public static final int removeAll() {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        int number = 0;
        Cursor cursor = resolver.query(TableProvider.DEVICE_VOICE_INFO_URI, null, TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID + "=? ",
                new String[]{BaseApplication.getInstances().getUser_id()},
                TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            number = resolver.delete(TableProvider.DEVICE_VOICE_INFO_URI, TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID + "=? ",
                    new String[]{BaseApplication.getInstances().getUser_id()});
        }
        if (cursor != null) {
            cursor.close();
        }
        return number;
    }

    public static final void insertValue(final DeviceVoiceEntity entity) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        String deviceID = entity.getDevice_id();
        String direction = entity.getDirection();
        String path = entity.getVoice_path();
        if (CheckUtil.isEmpty(deviceID)) {
            deviceID = "";
        }
        if (CheckUtil.isEmpty(direction)) {
            direction = "";
        }
        if (CheckUtil.isEmpty(path)) {
            path = "";
        }
        if (!TableInfo_ValueStub.DEVICE_VOICE_INFO_DIRECTION_RECEIVE.equals(direction) &&
                !TableInfo_ValueStub.DEVICE_VOICE_INFO_DIRECTION_SEND.equals(direction)) {
            try {
                throw new Exception("Error Direction Value");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        Cursor cursor = resolver.query(TableProvider.DEVICE_VOICE_INFO_URI, null, TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP1 + "=? and " +
                        TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_ID + "=?",
                new String[]{entity.getUserid(), entity.getTime(), deviceID}, TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID, entity.getUserid());
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_ID, deviceID);
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_DIRECTION, direction);
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_VOICE_PATH, path);
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP1, entity.getTime());

            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP2, (entity.getReadFlag() == null ? "" : entity.getReadFlag()));
            resolver.update(TableProvider.DEVICE_VOICE_INFO_URI, contentValues, TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP1 + "=? and " +
                            TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_ID + "=?",
                    new String[]{BaseApplication.getInstances().getUser_id(), entity.getTime(), deviceID});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID, entity.getUserid());
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_ID, deviceID);
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_DIRECTION, direction);
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_VOICE_PATH, path);
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP1, entity.getTime());
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP2, (entity.getReadFlag() == null ? "" : entity.getReadFlag()));
            resolver.insert(TableProvider.DEVICE_VOICE_INFO_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static final void insertValue(List<DeviceVoiceEntity> list) {
        for (DeviceVoiceEntity entity : list) {
            insertValue(entity);
        }
    }

    public static final List<DeviceVoiceEntity> getAllValue(String device_id) {
        List<DeviceVoiceEntity> list = new ArrayList<DeviceVoiceEntity>();
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        Cursor cursor = resolver.query(TableProvider.DEVICE_VOICE_INFO_URI, null, /*TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID + "=? and " +*/
                TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_ID + "=?",
                new String[]{/*BaseApplication.getInstances().getUser_id(),*/ device_id}, TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                DeviceVoiceEntity entity = new DeviceVoiceEntity();
                String id = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID));
                String direction = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_DIRECTION));
                String path = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_VOICE_PATH));
                String time = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP1));
                String flag = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP2));

                entity.setUserid(id);
                entity.setDevice_id(device_id);
                entity.setDirection(direction);
                entity.setVoice_path(path);
                entity.setTime(time);
                entity.setReadFlag((flag == null ? "" : flag));

                list.add(entity);
            }
            if (cursor != null) {
                cursor.close();
            }
            return list;
        } else {
            if (cursor != null) {
                cursor.close();
            }
            return list;
        }
    }

    public static final DeviceVoiceEntity getValue(String userid,String device_id, String times) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        DeviceVoiceEntity entity = new DeviceVoiceEntity();
        Cursor cursor = resolver.query(TableProvider.DEVICE_VOICE_INFO_URI, null, TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP1 + "=? and " +
                        TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_ID + "=?",
                new String[]{userid, times, device_id}, TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            if(cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID));
                String direction = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_DIRECTION));
                String path = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_VOICE_PATH));
                String time = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP1));
                String flag = cursor.getString(cursor.getColumnIndex(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP2));

                entity.setUserid(id);
                entity.setDevice_id(device_id);
                entity.setDirection(direction);
                entity.setVoice_path(path);
                entity.setTime(time);
                entity.setReadFlag((flag == null ? "" : flag));
            }
            if (cursor != null) {
                cursor.close();
            }
            return entity;
        } else {
            if (cursor != null) {
                cursor.close();
            }
            return null;
        }
    }

    public static final void updataValue(final DeviceVoiceEntity entity) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        String deviceID = entity.getDevice_id();
        String direction = entity.getDirection();
        String path = entity.getVoice_path();
        if (!TableInfo_ValueStub.DEVICE_VOICE_INFO_DIRECTION_RECEIVE.equals(direction) &&
                !TableInfo_ValueStub.DEVICE_VOICE_INFO_DIRECTION_SEND.equals(direction)) {
            try {
                throw new Exception("Error Direction Value");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        Cursor cursor = resolver.query(TableProvider.DEVICE_VOICE_INFO_URI, null, TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP1 + "=? and " +
                        TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_VOICE_PATH + "=? and " +
                        TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_ID + "=?",
                new String[]{entity.getUserid(), entity.getTime(),entity.getVoice_path() ,deviceID}, TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID, entity.getUserid());
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_ID, deviceID);
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_DIRECTION, direction);
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_VOICE_PATH, path);
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP1, entity.getTime());

            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP2, (entity.getReadFlag()));
            resolver.update(TableProvider.DEVICE_VOICE_INFO_URI, contentValues, TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                            TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP1 + "=? and " +
                            TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_VOICE_PATH + "=? and " +
                            TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_ID + "=?",
                    new String[]{entity.getUserid(), entity.getTime(),entity.getVoice_path() ,deviceID});
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID, entity.getUserid());
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_ID, deviceID);
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_DIRECTION, direction);
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_VOICE_PATH, path);
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP1, entity.getTime());
            contentValues.put(TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_BACKUP2, (entity.getReadFlag()));
            resolver.insert(TableProvider.DEVICE_VOICE_INFO_URI, contentValues);
        }
        if (cursor != null) {
            cursor.close();
        }
    }


    public static final int getCount() {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        Cursor cursor = resolver.query(TableProvider.DEVICE_VOICE_INFO_URI, null, null, null, TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor != null) {
                cursor.close();
            }
            return cursor.getCount();
        } else {
            if (cursor != null) {
                cursor.close();
            }
            return 0;
        }
    }

    public static final int getCount(String deviceid) {
        ContentResolver resolver = BaseApplication.getInstances().getContentResolver();
        Cursor cursor = resolver.query(TableProvider.DEVICE_VOICE_INFO_URI, null, TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_USER_ID + "=? and " +
                        TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_DEVICE_ID + "=?",
                new String[]{BaseApplication.getInstances().getUser_id(), deviceid}, TableInfo.DEVICE_VOICE_INFO_TABLE_COLUMN_PK + " asc");
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor != null) {
                cursor.close();
            }
            return cursor.getCount();
        } else {
            if (cursor != null) {
                cursor.close();
            }
            return 0;
        }
    }

}
