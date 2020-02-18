package com.lineplus.notepad.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lineplus.notepad.model.MemoItem;

public class DatabaseManager {
    private static DatabaseManager instance = null;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "LINE_PLUS_NOTEPAD.db";
    private static final String TABLE_NAME_MEMO = "MEMO";
    private static final String TABLE_NAME_IMAGE = "IMAGE";
    private static final int MEMO_COLUMN_CNT = 4;
    private static final int IMAGE_COLUMN_CNT = 5;
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

    private Context context = null;
    private SQLiteDatabase db = null;

    public static DatabaseManager getInstance(Context context){
        if(instance == null){
            instance = new DatabaseManager(context);
        }
        return instance;
    }

    private DatabaseManager(Context context){
        this.context = context;

        db = context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE, null);
        db.execSQL(SQL_CREATE_MEMO_TABLE);
        db.execSQL(SQL_CREATE_IMAGE_TABLE);
    }

    public MemoItem selectMemoByIdx(long idx){
        String selection = "IDX = ?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = Long.toString(idx);
        Cursor cursor = db.query(TABLE_NAME_MEMO, null, selection, selectionArgs, null, null, null);

        MemoItem memoItem = new MemoItem();

        if(cursor != null){
            cursor.moveToFirst();
            do{
                if(cursor.getColumnCount() != MEMO_COLUMN_CNT){
                    continue;
                }
                memoItem.setIdx(Long.parseLong(cursor.getString(0)));
                memoItem.setTitle(cursor.getString(1));
                memoItem.setDate(cursor.getString(2));
                memoItem.setContent(cursor.getString(3));
            }while(false);
        }

        return memoItem;
    }

    /**
     * @param memoItem
     * @return 추가된 row idx
     */
    public long insertMemo(MemoItem memoItem){
        ContentValues contentValues = new ContentValues();
        contentValues.put("TITLE", memoItem.getTitle());
        contentValues.put("CONTENT", memoItem.getContent());
        return db.insert(TABLE_NAME_MEMO, null, contentValues);
    }

    /**
     * @param idx
     * @return 삭제된 row 개수
     */
    public int deleteMemoByIdx(long[] idx){
        String selection = "IDX = ?";
        String[] selectionArgs = new String[idx.length];
        for(int i = 0; i < idx.length; i++){
            selectionArgs[i] = Long.toString(idx[i]);
        }
        return db.delete(TABLE_NAME_MEMO, selection, selectionArgs);
    }
}
