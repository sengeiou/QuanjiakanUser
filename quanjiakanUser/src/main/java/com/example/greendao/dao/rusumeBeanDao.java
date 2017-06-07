package com.example.greendao.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.androidquanjiakan.entity.rusumeBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "RUSUME_BEAN".
*/
public class rusumeBeanDao extends AbstractDao<rusumeBean, Long> {

    public static final String TABLENAME = "RUSUME_BEAN";

    /**
     * Properties of entity rusumeBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Json = new Property(1, String.class, "json", false, "JSON");
        public final static Property Imei = new Property(2, String.class, "imei", false, "IMEI");
    }


    public rusumeBeanDao(DaoConfig config) {
        super(config);
    }
    
    public rusumeBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"RUSUME_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"JSON\" TEXT," + // 1: json
                "\"IMEI\" TEXT);"); // 2: imei
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"RUSUME_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, rusumeBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String json = entity.getJson();
        if (json != null) {
            stmt.bindString(2, json);
        }
 
        String imei = entity.getImei();
        if (imei != null) {
            stmt.bindString(3, imei);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, rusumeBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String json = entity.getJson();
        if (json != null) {
            stmt.bindString(2, json);
        }
 
        String imei = entity.getImei();
        if (imei != null) {
            stmt.bindString(3, imei);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public rusumeBean readEntity(Cursor cursor, int offset) {
        rusumeBean entity = new rusumeBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // json
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // imei
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, rusumeBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setJson(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setImei(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(rusumeBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(rusumeBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(rusumeBean entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
