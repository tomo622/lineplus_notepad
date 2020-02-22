package com.lineplus.notepad.view;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lineplus.notepad.R;
import com.lineplus.notepad.data.DataManager;
import com.lineplus.notepad.event.OnClickAddPhoto;
import com.lineplus.notepad.event.OnSingleClickListener;
import com.lineplus.notepad.model.Image;
import com.lineplus.notepad.model.MemoItem;
import com.lineplus.notepad.view.adapter.ImageListAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class

ViewPhoto {
    private MemoActivity parent;

    private ConstraintLayout constLayout_transparent;
    private ConstraintLayout constLayout_untransparent;
    private AppCompatImageButton imgBnt_add;
    private ToggleButton tgl_select;
    private Button btn_delete;
    private RecyclerView recy_photoList;

    private ArrayList<Image> images;
    private ImageListAdapter imageListAdapter;

    public ViewPhoto(MemoActivity parent){
        this.parent = parent;
        init();
    }

    public void visibilityChanged(boolean isShow) {
        if(isShow){

        }
        else{
            tgl_select.setChecked(false); //슬라이드가 내려가면 선택 모드 버튼 off
        }
    }

    private void init(){
        //////////////////////////////////////////////////
        // 생성
        //////////////////////////////////////////////////
        constLayout_transparent = parent.findViewById(R.id.photo_constLayout_transparent);
        constLayout_untransparent = parent.findViewById(R.id.photo_constLayout_untransparent);
        imgBnt_add = parent.findViewById(R.id.photo_imgBnt_add);
        tgl_select = parent.findViewById(R.id.photo_tgl_select);
        btn_delete = parent.findViewById(R.id.photo_btn_delete);
        recy_photoList = parent.findViewById(R.id.photo_recy_photoList);

        images = new ArrayList<>();
        imageListAdapter = new ImageListAdapter(images);

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

        tgl_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                imageListAdapter.setSelectionMode(b);
                if(b){
                    btn_delete.setVisibility(View.VISIBLE);
                }
                else{
                    btn_delete.setVisibility(View.GONE);
                }
            }
        });

        btn_delete.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onClickEx(View view) {

            }
        });

        //////////////////////////////////////////////////
        // 설정
        //////////////////////////////////////////////////
        btn_delete.setVisibility(View.GONE);

        recy_photoList.setAdapter(imageListAdapter);
        recy_photoList.setLayoutManager(new LinearLayoutManager(parent, RecyclerView.HORIZONTAL, false));
    }

    private long getCurrentMemoIdx(){
        MemoItem memoItem = parent.getMemoItem();
        if(memoItem == null || memoItem.getIdx() <= 0){
            return -1;
        }
        return memoItem.getIdx();
    }

    public void setData(){
        images.clear();
        images.addAll(parent.getMemoItem().getImages());
        imageListAdapter.notifyDataSetChanged();
    }
}
