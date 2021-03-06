package com.dohyun.lineplus.notepad.data;

import android.content.Context;
import android.util.Log;

import com.dohyun.lineplus.notepad.database.DatabaseManager;
import com.dohyun.lineplus.notepad.model.Image;
import com.dohyun.lineplus.notepad.model.MemoItem;

import java.util.ArrayList;

public class DataManager {
    private static DataManager instance = null;
    private Context context = null;
    private ArrayList<MemoItem> memos = null;

    public static DataManager getInstance(Context context){
        if(instance == null){
            instance = new DataManager(context);
        }
        return instance;
    }

    private DataManager(Context context){
        this.context = context;
        memos = new ArrayList<>();
    }

    public ArrayList<MemoItem> getMemos() {
        return memos;
    }

    private void NotifyObservers(DataObserverNotice.TYPE type, boolean result, long lparam1){
        DataObserverNotice dataObserverNotice = new DataObserverNotice();
        dataObserverNotice.setType(type);
        dataObserverNotice.setResult(result);
        dataObserverNotice.setLparam1(lparam1);
        DataObserver.getInstance().notifyObservers(dataObserverNotice);
    }

    public void requestMemos(){
        if(requestMemosEx()) {
            NotifyObservers(DataObserverNotice.TYPE.SELECT, true, 0);
        }
        else{
            NotifyObservers(DataObserverNotice.TYPE.SELECT, false, 0);

        }
    }

    private boolean requestMemosEx(){
        memos = DatabaseManager.getInstance(context).selectMemo();
        if(memos != null){
            return true;
        }
        else {
            Log.e("DATA MANAGER", "Request memo is fail.");
            return false;
        }
    }

    public void requestMemosDelete(long[] idxs){
        int deletedCnt = DatabaseManager.getInstance(context).deleteMemoByIdx(idxs);
        if(deletedCnt > 0){
            requestMemosEx();
            NotifyObservers(DataObserverNotice.TYPE.DELETE, true, 0);
        }
        else{
            NotifyObservers(DataObserverNotice.TYPE.DELETE, false, 0);
            Log.e("DATA MANAGER", "Delete memo is fail.");
        }
    }

    public void requestMemoInsert(MemoItem memo){
        long insertedIdx = DatabaseManager.getInstance(context).insertMemo(memo);

        if(insertedIdx >= 0){
            requestMemosEx();
            NotifyObservers(DataObserverNotice.TYPE.INSERT, true, insertedIdx);
        }
        else{
            NotifyObservers(DataObserverNotice.TYPE.INSERT, false, 0);
            Log.e("DATA MANAGER", "Insert memo is fail.");
        }
    }

    public void requestMemoUpdate(MemoItem memo){
        int updatedCnt = DatabaseManager.getInstance(context).updateMemoById(memo);

        if(updatedCnt > 0){
            requestMemosEx();
            NotifyObservers(DataObserverNotice.TYPE.UPDATE, true, 0);
        }
        else{
            NotifyObservers(DataObserverNotice.TYPE.UPDATE, false, 0);
            Log.e("DATA MANAGER", "Update memo is fail.");
        }
    }

    public void requestImageInsert(Image image, byte[] bytes){
        if(image.getType().equals("DIR")){
            String fileName = InternalDataManager.getInstance(context).saveFile(bytes); //내부 저장소에 이미지 저장
            if(fileName != null && !fileName.isEmpty()){
                image.setDir(fileName);
            }
            else{
                Log.e("DATA MANAGER", "Failed to save file into internal directory");
                return;
            }
        }


        boolean createMemo = false; //메모를 새로 만드는 경우
        if(image.getMemoIdx() <= 0){
            MemoItem memoItem = new MemoItem();
            long insertedMemoIdx = DatabaseManager.getInstance(context).insertMemo(memoItem);
            if(insertedMemoIdx >= 0){
                image.setMemoIdx(insertedMemoIdx);
                createMemo = true;
            }
            else{
                Log.e("DATA MANAGER", "Insert memo is fail.");
                return;
            }
        }

        long insertedIdx = DatabaseManager.getInstance(context).insertImage(image);

        if(insertedIdx >= 0){
            if(createMemo){
                requestMemosEx();
                NotifyObservers(DataObserverNotice.TYPE.INSERT, true, image.getMemoIdx());
            }
            else{
                //관련 메모 날짜 업데이트
                int updatedMemoDateCnt = DatabaseManager.getInstance(context).updateMemoDateByIdx(image.getMemoIdx());
                if(updatedMemoDateCnt <= 0){
                    Log.e("DATA MANAGER", "Update memo date is fail.");
                }

                requestMemosEx();
                NotifyObservers(DataObserverNotice.TYPE.INSERT_IMAGE, true, insertedIdx);
            }
        }
        else{
            if(image.getType().equals("DIR")){
                InternalDataManager.getInstance(context).deleteFile(image.getDir()); //내부저장소에 저장한 데이터 삭제
            }
            NotifyObservers(DataObserverNotice.TYPE.INSERT, false, 0);
            Log.e("DATA MANAGER", "Insert image is fail.");
        }
    }

    public void requestImagesDelete(ArrayList<Image> deleteImages){
        long deleteIdxs[] = new long[deleteImages.size()];
        for(int i = 0; i < deleteIdxs.length; i++){
            deleteIdxs[i] = deleteImages.get(i).getIdx();
        }

        int deletedCnt = DatabaseManager.getInstance(context).deleteImageByIdx(deleteIdxs);
        if(deletedCnt > 0){
            //내부 저장소의 데이터 삭제
            for(Image image : deleteImages){
                if(image.getType().equals("DIR")){
                    InternalDataManager.getInstance(context).deleteFile(image.getDir());
                }
            }

            requestMemosEx();
            NotifyObservers(DataObserverNotice.TYPE.DELETE_IMAGE, true, 0);
        }
        else{
            NotifyObservers(DataObserverNotice.TYPE.DELETE_IMAGE, false, 0);
            Log.e("DATA MANAGER", "Delete image is fail.");
        }
    }
}
