package com.androidquanjiakan.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 作者：Administrator on 2017/3/7 16:52
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class RunTimeDBHelper extends SQLiteOpenHelper{

    public RunTimeDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table runtime(id integer primary key autoincrement,autoconnection text,lossreport text,lightpanel text,watchbell text,tagetstep text )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+"runtime");
        onCreate(db);

    }
}
