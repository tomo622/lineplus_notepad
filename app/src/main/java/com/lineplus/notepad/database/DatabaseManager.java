package com.lineplus.notepad.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {
    public static DatabaseManager instance = null;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "LINE_PLUS_NOTEPAD.db";
    private static final String TABLE_NAME_MEMO = "MEMO";
    private static final String TABLE_NAME_IMAGE = "IMAGE";
    private static final String SQL_CREATE_MEMO_TABLE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME_MEMO +
            "(IDX INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "TITLE TEXT , " +
            "DATE DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "CONTENT TEXT);";
    private static final String SQL_CREATE_IMAGE_TABLE = "CREATE TABLE IF NOT EXISTS "+
            TABLE_NAME_IMAGE +
            "(IDX INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "MEMO_IDX INTEGER NOT NULL, " +
            "TYPE TEXT NOT NULL, " + //IMAGE 또는 URL
            "BLOB_DATA BLOB, " +
            "URL_DATA TEXT);";
    private static final String SQL_DELETE_MEMO_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME_MEMO;
    private static final String SQL_DELETE_IMAGE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME_IMAGE;

    private Context context = null;
    private SQLiteDatabase db = null;

    public static DatabaseManager getInstance(Context context){
        if(instance == null){
            instance = new DatabaseManager(context);
        }
        return instance;
    }

    private DatabaseManager(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        db.execSQL(SQL_CREATE_MEMO_TABLE);
        db.execSQL(SQL_CREATE_IMAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        db.execSQL(SQL_DELETE_MEMO_TABLE);
        db.execSQL(SQL_DELETE_IMAGE_TABLE);
        onCreate(db);
    }
}
