package com.lineplus.notepad.view;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lineplus.notepad.R;
import com.lineplus.notepad.event.OnSingleClickListener;

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

        imgBnt_add.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onClickEx(View view) {
                AddPhotoDialog addPhotoDialog = new AddPhotoDialog(parent);
                addPhotoDialog.show();
            }
        });
    }
}
