package com.androidquanjiakan.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 作者：Administrator on 2017/2/28 10:15
 * <p>
 * 邮箱：liuzj@hi-board.com
 */
public class WatchContactsDBHelper extends SQLiteOpenHelper {

    /**
     *
     * @param context
     *            上下文
     * @param name
     *            数据库名
     * @param factory
     *            可选的数据库游标工厂类，当查询(query)被提交时，该对象会被调用来实例化一个游标。默认为null。
     * @param version
     *            数据库版本号
     */
    public WatchContactsDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {// 覆写onCreate方法，当数据库创建时就用SQL命令创建一个表
        // 创建一个contacts表，id主键，自动增长，字符类型的name和pass;
        db.execSQL("create table contacts(id integer primary key autoincrement,name varchar(200),icon varchar(200),num varchar(200),mana varchar(200),imag varchar(200) )");
        db.execSQL("create table resume(id integer primary key autoincrement,optime text,clotime text )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+"contacts");
        db.execSQL("DROP TABLE IF EXISTS "+"resume");
        onCreate(db);

    }


}
