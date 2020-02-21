package com.lineplus.notepad.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

import com.lineplus.notepad.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class GraphicFunc {

    public static void setImageByUrlToImageView(final Context context, final String url, final ImageView imageView){
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                imageView.setImageBitmap(GraphicFunc.bytesToBitmap(GraphicFunc.getNotFoundImage(context))); //실패 시 기본이미지 넣어준다.
                Log.e("GRAPHIC FUNC", "Picasso load url image fail.");
            }
        });
        builder.build().load(url).into(imageView);
    }

    public static byte[] getNotFoundImage(Context context){
        byte[] bytes = null;
        try {
            Drawable d = context.getResources().getDrawable( R.drawable.ic_not_found_image );
            Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
            bytes = bitmapToBytes(bitmap);
        } catch (Exception e) {
            Log.e("GRAPHIC FUNC", "Getting not found image is fail. + " + e.toString());
        }
        return bytes;
    }

    public static byte[] bitmapToBytes(Bitmap bmp){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static Bitmap bytesToBitmap(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
    }

    public static float dpToPx(Context context, float dp){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
    }

    public static float pxToDp(Context context, float px){
        float density = context.getResources().getDisplayMetrics().density;

        if(density == 1.0){
            density *= 4.0;
        }
        else if(density == 1.5){
            density *= (8/3);
        }
        else if(density == 2.0){
            density *= 2.0;
        }

        return px/density;
    }
}
