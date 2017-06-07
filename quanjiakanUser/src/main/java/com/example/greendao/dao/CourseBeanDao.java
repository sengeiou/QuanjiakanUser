package com.example.greendao.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.androidquanjiakan.entity.CourseBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "COURSE_BEAN".
*/
public class CourseBeanDao extends AbstractDao<CourseBean, Long> {

    public static final String TABLENAME = "COURSE_BEAN";

    /**
     * Properties of entity CourseBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Imei = new Property(1, String.class, "imei", false, "IMEI");
        public final static Property Json = new Property(2, String.class, "json", false, "JSON");
    }


    public CourseBeanDao(DaoConfig config) {
        super(config);
    }
    
    public CourseBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"COURSE_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"IMEI\" TEXT," + // 1: imei
                "\"JSON\" TEXT);"); // 2: json
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"COURSE_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, CourseBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String imei = entity.getImei();
        if (imei != null) {
            stmt.bindString(2, imei);
        }
 
        String json = entity.getJson();
        if (json != null) {
            stmt.bindString(3, json);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, CourseBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String imei = entity.getImei();
        if (imei != null) {
            stmt.bindString(2, imei);
        }
 
        String json = entity.getJson();
        if (json != null) {
            stmt.bindString(3, json);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public CourseBean readEntity(Cursor cursor, int offset) {
        CourseBean entity = new CourseBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // imei
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // json
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, CourseBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setImei(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setJson(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(CourseBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(CourseBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(CourseBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}