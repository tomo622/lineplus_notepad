package com.lineplus.notepad.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.util.Log;

import com.lineplus.notepad.model.Image;
import com.lineplus.notepad.model.MemoItem;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    public ArrayList<MemoItem> selectMemo(){
        ArrayList<MemoItem> memoItems = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME_MEMO, null, null, null, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();

            while(cursor.isAfterLast() == false){
                if(cursor.getColumnCount() != MEMO_COLUMN_CNT){
                    continue;
                }

                MemoItem memoItem = new MemoItem();
                memoItem.setIdx(cursor.getLong(0));
                memoItem.setTitle(cursor.getString(1));
                memoItem.setDate(cursor.getString(2));
                memoItem.setContent(cursor.getString(3));
                memoItem.setImages(selectImageByMemoIdx(memoItem.getIdx()));

                memoItems.add(memoItem);

                cursor.moveToNext();
            }
        }

        return memoItems;
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
                memoItem.setIdx(cursor.getLong(0));
                memoItem.setTitle(cursor.getString(1));
                memoItem.setDate(cursor.getString(2));
                memoItem.setContent(cursor.getString(3));
                memoItem.setImages(selectImageByMemoIdx(memoItem.getIdx()));
            }while(false);
        }

        return memoItem;
    }

    private ArrayList<Image> selectImageByMemoIdx(long memoIdx){
        ArrayList<Image> images = new ArrayList<>();

        String selection = "MEMO_IDX = ?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = Long.toString(memoIdx);
        Cursor cursor = db.query(TABLE_NAME_IMAGE, null, selection, selectionArgs, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();

            while(cursor.isAfterLast() == false){
                if(cursor.getColumnCount() != IMAGE_COLUMN_CNT){
                    continue;
                }

                Image image = new Image();
                image.setIdx(cursor.getLong(0));
                image.setMemoIdx(cursor.getLong(1));
                image.setType(cursor.getString(2));
                if(image.getType().equals("IMAGE")){
                    byte[] blobBytes = cursor.getBlob(3);
                    Bitmap bmp = BitmapFactory.decodeByteArray(blobBytes, 0 , blobBytes.length);
                    image.setBmp(bmp);
                }
                else if(image.getType().equals("URL")){
                    image.setBmp(null);
                    image.setUrl(cursor.getString(4));
                }
                else {
                    Log.e("DB MANAGER", "Image type is nothing.");
                    continue;
                }

                images.add(image);

                cursor.moveToNext();
            }
        }

        return images;
    }

    /**
     * @param memoItem
     * @return 추가된 row idx
     */
    public long insertMemo(MemoItem memoItem){
        if(memoItem == null){
            return -1;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("TITLE", memoItem.getTitle());
        contentValues.put("CONTENT", memoItem.getContent());
        return db.insert(TABLE_NAME_MEMO, null, contentValues);
    }

    public long insertImage(Image image){
        if(image == null){
            return -1;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("MEMO_IDX", image.getMemoIdx());
        contentValues.put("TYPE", image.getType());
        if(image.getType().equals("IMAGE")){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.getBmp().compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] blobBytes = baos.toByteArray();
            contentValues.put("BLOB_DATA", blobBytes);
        }
        else if(image.getType().equals("URL")){
            contentValues.put("URL_DATA", image.getUrl());
        }
        else{
            Log.e("DB MANAGER", "Image type is nothing.");
            return -1;
        }
        return db.insert(TABLE_NAME_IMAGE, null, contentValues);
    }


    /**
     * @param idxs
     * @return 삭제된 row 개수
     */
    public int deleteMemoByIdx(long[] idxs){
        if(idxs.length <= 0){
            return 0;
        }

        String[] selectionArgs = new String[idxs.length];
        String selection = "IDX = ?";
        selectionArgs[0] = Long.toString(idxs[0]);

        for(int i = 1; i < idxs.length; i++){
            selection += " OR IDX = ?";
            selectionArgs[i] = Long.toString(idxs[i]);
        }

        deleteImageByMemoIdx(idxs); //관련된 사진 모두 삭제

        return db.delete(TABLE_NAME_MEMO, selection, selectionArgs);
    }

    private int deleteImageByMemoIdx(long[] memoIdxs){
        if(memoIdxs.length <= 0){
            return 0;
        }

        String[] selectionArgs = new String[memoIdxs.length];
        String selection = "MEMO_IDX = ?";
        selectionArgs[0] = Long.toString(memoIdxs[0]);

        for(int i = 1; i < memoIdxs.length; i++){
            selection += " OR MEMO_IDX = ?";
            selectionArgs[i] = Long.toString(memoIdxs[i]);
        }

        return db.delete(TABLE_NAME_IMAGE, selection, selectionArgs);
    }

    public int deleteImageByIdx(long[] idxs){
        if(idxs.length <= 0){
            return 0;
        }

        String[] selectionArgs = new String[idxs.length];
        String selection = "IDX = ?";
        selectionArgs[0] = Long.toString(idxs[0]);

        for(int i = 1; i < idxs.length; i++){
            selection += " OR IDX = ?";
            selectionArgs[i] = Long.toString(idxs[i]);
        }

        return db.delete(TABLE_NAME_IMAGE, selection, selectionArgs);
    }

    /**
     * @param memoItem
     * @return 수정된 row 개수
     */
    public int updateMemoById(MemoItem memoItem){
        if(memoItem == null){
            return 0;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("TITLE", memoItem.getTitle());
        contentValues.put("CONTENT", memoItem.getContent());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        contentValues.put("DATE", sdf.format(date));

        String selection = "IDX = ?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = Long.toString(memoItem.getIdx());

        return db.update(TABLE_NAME_MEMO, contentValues, selection, selectionArgs);
    }
}
