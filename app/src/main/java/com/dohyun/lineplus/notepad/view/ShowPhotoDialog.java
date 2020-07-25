package com.dohyun.lineplus.notepad.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageButton;

import com.dohyun.lineplus.notepad.R;
import com.dohyun.lineplus.notepad.event.OnSingleClickListener;
import com.dohyun.lineplus.notepad.model.Image;
import com.dohyun.lineplus.notepad.util.GraphicFunc;

import java.util.ArrayList;

public class ShowPhotoDialog extends Dialog {
    private Context context;
    private ArrayList<Image> images;
    private int showIndex;

    private ImageView img_image;
    private Button btn_close;
    private AppCompatImageButton imgBtn_left;
    private AppCompatImageButton imgBtn_right;

    public ShowPhotoDialog(Context context, ArrayList<Image> images, int showIndex) {
        super(context);
        this.context = context;
        this.images = images;
        this.showIndex = showIndex;
    }

    //TODO: TESTCODE
    public ShowPhotoDialog(Context context, Image image){
        super(context);
        this.context = context;
        this.images = new ArrayList<>();
        images.add(image);
        this.showIndex = 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_show_photo);

        img_image = findViewById(R.id.show_img_image);
        btn_close = findViewById(R.id.show_btn_close);
        imgBtn_left = findViewById(R.id.show_imgBtn_left);
        imgBtn_right = findViewById(R.id.show_imgBtn_right);

        imgBtn_left.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onClickEx(View view) {
                showIndex--;
                if(showIndex < 0){
                    showIndex = 0;
                }
                else{
                    showeImage(showIndex);
                }
            }
        });

        imgBtn_right.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onClickEx(View view) {
                showIndex++;
                if(showIndex >= images.size()){
                    showIndex = images.size() - 1;
                }
                else{
                    showeImage(showIndex);
                }
            }
        });


        btn_close.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onClickEx(View view) {
                dismiss();
            }
        });

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int width = (int) (dm.widthPixels * 0.9); // Display 사이즈의 90%
        getWindow().getAttributes().width = width;
        int height = (int) (dm.heightPixels * 0.9); // Display 사이즈의 90%
        getWindow().getAttributes().height = height;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        showeImage(showIndex);

    }

    private void showeImage(int showIndex){
        if(showIndex == 0){
            imgBtn_left.setVisibility(View.GONE);
        }
        else{
            if(imgBtn_left.getVisibility() == View.GONE){
                imgBtn_left.setVisibility(View.VISIBLE);
            }
        }

        if(showIndex == images.size() - 1){
            imgBtn_right.setVisibility(View.GONE);
        }
        else{
            if(imgBtn_right.getVisibility() == View.GONE){
                imgBtn_right.setVisibility(View.VISIBLE);
            }
        }

        Image showImage = images.get(showIndex);
        if(showImage.getType().equals("DIR")){
            GraphicFunc.setImageByDirToImageView(context, showImage.getDir(), img_image);
        }
        else if(showImage.getType().equals("URL")){
            GraphicFunc.setImageByUrlToImageView(context, showImage.getUrl(), img_image);
        }
    }
}
