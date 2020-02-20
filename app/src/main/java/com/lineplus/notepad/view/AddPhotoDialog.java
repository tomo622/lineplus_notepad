package com.lineplus.notepad.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;

import com.lineplus.notepad.R;

public class AddPhotoDialog extends Dialog {
    private Context context;

    public AddPhotoDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_photo);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int width = (int) (dm.widthPixels * 0.98); // Display 사이즈의 90%
        getWindow().getAttributes().width = width;

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
