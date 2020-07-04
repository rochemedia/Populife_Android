package com.populstay.populife.entity;

import android.net.Uri;

import com.populstay.populife.util.file.FileUtil;


/**
 * 照相机调用类
 */

public class LatteCamera {

    public static Uri createCropFile() {
        return Uri.parse
                (FileUtil.createFile("crop_image",
                        FileUtil.getFileNameByTime("IMG", "jpg")).getPath());
    }

}
 