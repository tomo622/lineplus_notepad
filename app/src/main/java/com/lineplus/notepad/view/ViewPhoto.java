package com.lineplus.notepad.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lineplus.notepad.R;
import com.lineplus.notepad.data.DataManager;
import com.lineplus.notepad.data.InternalDataManager;
import com.lineplus.notepad.event.OnCheckImageSelect;
import com.lineplus.notepad.event.OnClickAddPhoto;
import com.lineplus.notepad.event.OnSingleClickListener;
import com.lineplus.notepad.model.Image;
import com.lineplus.notepad.view.adapter.ImageListAdapter;

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
        imageListAdapter = new ImageListAdapter(images, new OnCheckImageSelect() {
            @Override
            public void checkSelectedCount(int cnt) {
                if(cnt > 0){
                    btn_delete.setVisibility(View.VISIBLE);
                }
                else{
                    btn_delete.setVisibility(View.GONE);
                }
            }
        });

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
                    if(ContextCompat.checkSelfPermission(parent, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        getImageFromAlbumEx();
                    }
                    else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(parent, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        } else {
                            ActivityCompat.requestPermissions(parent, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, parent.PREMISSION_CODE_READ_EXTERNAL_STORAGE);
                        }
                    }
                }
                else if(type == TYPE.INPUT_URL){
                    String url = strParam1;
                    Image image = new Image();
                    image.setMemoIdx(parent.getCurrentMemoIdx());
                    image.setType("URL");
                    image.setUrl(url);
                    DataManager.getInstance(parent).requestImageInsert(image, null);
                }
            }
        };

        imgBnt_add.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onClickEx(View view) {
                AddPhotoDialog addPhotoDialog = new AddPhotoDialog(parent, onClickAddPhoto);
                addPhotoDialog.show();
                if(tgl_select.isChecked()){
                    tgl_select.setChecked(false);
                }
            }
        });

        tgl_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                imageListAdapter.setSelectionMode(b);
                if(b){
                    //btn_delete.setVisibility(View.VISIBLE);
                }
                else{
                    btn_delete.setVisibility(View.GONE);
                }
            }
        });

        btn_delete.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onClickEx(View view) {
                ArrayList<Image> deleteTargetImages = new ArrayList<>();
                for(Image image : imageListAdapter.getItems()){
                    if(image.isSelected()){
                        deleteTargetImages.add(image);
                    }
                }

                deleteImageEx(deleteTargetImages);
            }
        });

        //////////////////////////////////////////////////
        // 설정
        //////////////////////////////////////////////////
        btn_delete.setVisibility(View.GONE);

        recy_photoList.setAdapter(imageListAdapter);
        recy_photoList.setLayoutManager(new LinearLayoutManager(parent, RecyclerView.HORIZONTAL, false));
    }

    public void getImageFromAlbumEx(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        parent.startActivityForResult(intent, parent.INTENT_CODE_GET_IMAGE_FROM_ALBUM); //intent 결과는 MemoActivity로
    }

    private void deleteImageEx(final ArrayList<Image> deleteImages){
        if(deleteImages.size() <=0 ){
            return;
        }
        AlertDialog.Builder adb = new AlertDialog.Builder(parent);
        adb.setTitle("정말 삭제하시겠습니까?");
        adb.setPositiveButton("예", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                DataManager.getInstance(parent).requestImagesDelete(deleteImages);
                dialog.dismiss();
            }

        });
        adb.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        adb.show();
    }

    public void setData(){
        images.clear();
        images.addAll(parent.getMemoItem().getImages());
        imageListAdapter.notifyDataSetChanged();

        if(images.size() > 0){
            tgl_select.setVisibility(View.VISIBLE);
        }
        else {
            tgl_select.setVisibility(View.GONE);
        }

        tgl_select.setChecked(false);
    }
}
