package com.populstay.populife.util.dimen;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.populstay.populife.base.BaseApplication;

/**
 * Created by Jerry
 */

public final class DimenUtil {

    public static int getScreenWidth() {
        final Resources resources = BaseApplication.getApplication().getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight() {
        final Resources resources = BaseApplication.getApplication().getResources();
        final DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.heightPixels;
    }
}
