package com.example.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "pass_manager";

    //название и поля таблицы с данными
    public static final String TABLE_DATA = "notes";
    public static final String DATA_ID = "_id";
    public static final String DATA_TEXT = "data";

    public static final String TABLE_USER = "user";
    public static final String USER_ID = "_id";
    public static final String USER_KEY = "bites";
    public static final String USER_HASH = "hash";


    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_DATA + "(" + DATA_ID + " integer primary key, " + DATA_TEXT + " BLOB)");
        db.execSQL("create table " + TABLE_USER + "(" + USER_ID + " integer primary key, " + USER_KEY + " BLOB, " + USER_HASH + " BLOB)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_DATA);
        db.execSQL("drop table if exists " + TABLE_USER);
        onCreate(db);
    }
}
