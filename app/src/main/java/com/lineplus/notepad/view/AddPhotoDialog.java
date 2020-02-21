package com.lineplus.notepad.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.lineplus.notepad.R;
import com.lineplus.notepad.event.OnClickAddPhoto;
import com.lineplus.notepad.event.OnSingleClickListener;

public class AddPhotoDialog extends Dialog {
    private Context context;
    private OnClickAddPhoto onClickAddPhoto;

    private Button btn_takePicture;
    private Button btn_fromAlbum;
    private Button btn_saveUrl;
    private ToggleButton tgl_inputUrl;
    private ConstraintLayout constLayout_inputUrl;
    private EditText edit_url;

    public AddPhotoDialog(Context context, OnClickAddPhoto onClickAddPhoto) {
        super(context);
        this.context = context;
        this.onClickAddPhoto = onClickAddPhoto;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_photo);

        //////////////////////////////////////////////////
        // 생성
        //////////////////////////////////////////////////
        btn_takePicture = findViewById(R.id.addPhoto_btn_takePicture);
        btn_fromAlbum = findViewById(R.id.addPhoto_btn_fromAlbum);
        btn_saveUrl = findViewById(R.id.addPhoto_btn_saveUrl);
        tgl_inputUrl = findViewById(R.id.addPhoto_tgl_inputUrl);
        constLayout_inputUrl = findViewById(R.id.addPhoto_constLayout_inputUrl);
        edit_url = findViewById(R.id.addPhoto_edit_url);

        //////////////////////////////////////////////////
        // 바인딩
        //////////////////////////////////////////////////
        btn_takePicture.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onClickEx(View view) {
                onClickAddPhoto.onClick(OnClickAddPhoto.TYPE.TAKE_PICTURE, "");
                dismiss();
            }
        });

        btn_fromAlbum.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onClickEx(View view) {
                onClickAddPhoto.onClick(OnClickAddPhoto.TYPE.FROM_ALBUM, "");
                dismiss();
            }
        });

        btn_saveUrl.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onClickEx(View view) {
                onClickAddPhoto.onClick(OnClickAddPhoto.TYPE.INPUT_URL, "");
                dismiss();
            }
        });

        tgl_inputUrl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    constLayout_inputUrl.setVisibility(View.VISIBLE);
                }
                else{
                    constLayout_inputUrl.setVisibility(View.GONE);
                }
            }
        });

        //////////////////////////////////////////////////
        // 설정
        //////////////////////////////////////////////////
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int width = (int) (dm.widthPixels * 0.98); // Display 사이즈의 90%
        getWindow().getAttributes().width = width;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        constLayout_inputUrl.setVisibility(View.GONE);
    }
}
