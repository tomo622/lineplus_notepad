package com.lineplus.notepad.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class GraphicFunc {

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
