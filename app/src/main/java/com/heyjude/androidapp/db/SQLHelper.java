package com.heyjude.androidapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "heyjude.com.heyjude.androidapp.db";
    private static final int DATABASE_VERSION = 3;

    public SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBMessages.CREATE_TABLE_MESSAGES);
        Log.e(">>>>>>>>>>", "Database MESSAGE Created");
        db.execSQL(DBMessages.CREATE_TABLE_VENDOR);
        Log.e(">>>>>>>>>>", "Database VENDOR Created");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
