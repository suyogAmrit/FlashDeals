package com.suyogindia.helpers;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Tanmay on 10/24/2016.
 */

public class AndroidUtils {
    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }
}
