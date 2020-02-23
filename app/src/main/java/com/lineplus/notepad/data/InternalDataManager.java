package com.lineplus.notepad.data;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InternalDataManager {
    private static InternalDataManager instance = null;
    private Context context = null;

    public static InternalDataManager getInstance(Context context){
        if(instance == null){
            instance = new InternalDataManager(context);
        }
        return instance;
    }

    private InternalDataManager(Context context){
        this.context = context;
    }

    public String saveFile(byte[] bytes){
        String fileName = "";
        try {
            fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        } catch (IOException e) {
            Log.e("INTERNAL DATA MANAGER", "Failed to save file. : " + e.toString());
        }
        //TESTCODE
        File file = new File(context.getFilesDir().toString());
        File[] files = file.listFiles();

        return fileName;
    }

    public boolean deleteFile(String fileName){
        boolean deleteResult = false;
        try{
            String uri = context.getFilesDir().toString() + "/" + fileName;
            File file = new File(uri);
            deleteResult = file.delete();
        }
        catch (Exception e){
            Log.e("INTERNAL DATA MANAGER", "Failed to delete file. : " + e.toString());
        }
        return  deleteResult;
    }
}

