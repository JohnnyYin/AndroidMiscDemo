package com.cvte.demo.mesh;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by Johnny on 15/8/22.
 */
public class Utils {

    public static float dp2px(Context context, int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
