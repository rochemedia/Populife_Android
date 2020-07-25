package com.populstay.populife.util;

import android.content.Context;
import android.util.TypedValue;

public class DensityUtils {

    /**像素密度*/
    public static float getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }
    /**  dp 转成为 px     */
    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }
    /**  px 转成为 dp     */
    public static int px2dp(Context context, float pxValue) {
        return (int) (pxValue / getDisplayMetrics(context) + 0.5f);
    }
    /** sp转px */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources().getDisplayMetrics());
    }
    /** px转sp */
    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
    }
}
