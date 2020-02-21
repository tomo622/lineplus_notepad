package com.lineplus.notepad.view;

import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lineplus.notepad.R;
import com.lineplus.notepad.data.DataManager;
import com.lineplus.notepad.event.OnClickAddPhoto;
import com.lineplus.notepad.event.OnSingleClickListener;
import com.lineplus.notepad.model.Image;
import com.lineplus.notepad.model.MemoItem;

public class ViewPhoto {
    private MemoActivity parent;

    private ConstraintLayout constLayout_transparent;
    private ConstraintLayout constLayout_untransparent;
    private AppCompatImageButton imgBnt_add;


    public ViewPhoto(MemoActivity parent){
        this.parent = parent;
        init();
    }

    private void init(){
        //////////////////////////////////////////////////
        // 생성
        //////////////////////////////////////////////////
        constLayout_transparent = parent.findViewById(R.id.photo_constLayout_transparent);
        constLayout_untransparent = parent.findViewById(R.id.photo_constLayout_untransparent);
        imgBnt_add = parent.findViewById(R.id.photo_imgBnt_add);

        //////////////////////////////////////////////////
        // 바인딩
        //////////////////////////////////////////////////
        constLayout_transparent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(parent.getTgl_photo().isChecked()){
                    parent.getTgl_photo().setChecked(false);
                    return true;
                }
                else{
                    return false;
                }
            }
        });

        constLayout_untransparent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(parent.getTgl_photo().isChecked()){
                    return true;
                }
                else{
                    return false;
                }
            }
        });

        final OnClickAddPhoto onClickAddPhoto = new OnClickAddPhoto() {
            @Override
            public void onClick(TYPE type, String strParam1) {
                if(type == TYPE.TAKE_PICTURE){

                }
                else if(type == TYPE.FROM_ALBUM){

                }
                else if(type == TYPE.INPUT_URL){
                    String url = strParam1;
                    Image image = new Image();
                    image.setMemoIdx(getCurrentMemoIdx());
                    image.setType("URL");
                    image.setUrl(url);
                    DataManager.getInstance(parent).requestImageInsert(image);
                }
            }
        };

        imgBnt_add.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onClickEx(View view) {
                AddPhotoDialog addPhotoDialog = new AddPhotoDialog(parent, onClickAddPhoto);
                addPhotoDialog.show();
            }
        });
    }

    private long getCurrentMemoIdx(){
        MemoItem memoItem = parent.getMemoItem();
        if(memoItem == null || memoItem.getIdx() <= 0){
            return -1;
        }
        return memoItem.getIdx();
    }
}
