package com.lineplus.notepad.util;

import android.annotation.SuppressLint;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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

    @SuppressLint("NewApi")
    public static void setImageByDirToImageView(final Context context, final String dir, final ImageView imageView){
        Picasso.Builder builder = new Picasso.Builder(context);
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                imageView.setImageBitmap(GraphicFunc.bytesToBitmap(GraphicFunc.getNotFoundImage(context))); //실패 시 기본이미지 넣어준다.
                Log.e("GRAPHIC FUNC", "Picasso load dir image fail.");
            }
        });

        String uri = context.getFilesDir().toString() + "/" +dir;
        File file = new File(uri);
        builder.build().load(file).into(imageView);
    }

    public static byte[] getNotFoundImage(Context context){
        byte[] bytes = null;
        try {
            Drawable d = context.getResources().getDrawable( R.drawable.ic_not_found_image );
            Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
            bytes = bitmapToBytes(bitmap);
        } catch (Exception e) {
            Log.e("GRAPHIC FUNC", "Getting not found image is failed. + " + e.toString());
        }
        return bytes;
    }

    public static byte[] bitmapToBytes(Bitmap bmp){
        byte[] bytes = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            bytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
        } catch (IOException e) {
            Log.e("GRAPHIC FUNC", "Converting bitmap to bytes is failed. : " + e.toString());
        }
        return bytes;
    }

    public static Bitmap bytesToBitmap(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
    }

    public static byte[] inputstreamToBytes(InputStream inputStream){
        byte[] bytes = null;
        try{
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            bytes = buffer.toByteArray();
        }
        catch (Exception e){
            Log.e("GRAPHIC FUNC", "Failed to converting input stream to byte array.");
        }
        return bytes;
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
