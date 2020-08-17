package com.populstay.populife.home.entity;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.populstay.populife.R;

public class HomeDeviceInfo {

    public interface IDeviceName {
        // 网关
        String NAEM_GATEWAY = "G2_5db4ad";//
        // 横闩锁
        String NAME_LOCK_DEADBOLT = "PPL-DB_a67291";
        // 密码锁
        String NAME_LOCK_KEY_BOX = "KB";
    }

    public interface IModelNum {
        // 网关
        String NAEM_GATEWAY = "1";//
        // 横闩锁
        String NAME_LOCK_DEADBOLT = "2";
        // 密码锁
        String NAME_LOCK_KEY_BOX = "3";
    }


    public static @StringRes int getTypeNameByName(String deviceName) {
        @StringRes
        int name = -1;

        if (TextUtils.isEmpty(deviceName)){
            return name;
        }

        if (deviceName.contains(IDeviceName.NAEM_GATEWAY)){
            name = R.string.device_name_gateway;
        }else if (deviceName.contains(IDeviceName.NAME_LOCK_DEADBOLT)){
            name = R.string.lock_type_deadbolt;
        }else if (deviceName.contains(IDeviceName.NAME_LOCK_KEY_BOX)){
            name = R.string.lock_type_keybox;
        }else {
            // 不存在的类型
        }
        return name;
    }

    public static @DrawableRes int getIconActiveByName(String deviceName) {
        @DrawableRes
        int iconActive = -1;

        if (TextUtils.isEmpty(deviceName)){
            return iconActive;
        }

        if (deviceName.contains(IDeviceName.NAEM_GATEWAY)){
            iconActive = R.drawable.gateway_active;
        }else if (deviceName.contains(IDeviceName.NAME_LOCK_DEADBOLT)){
            iconActive = R.drawable.deadbolt_active;
        }else if (deviceName.contains(IDeviceName.NAME_LOCK_KEY_BOX)){
            iconActive = R.drawable.keybox_active;
        }else {
            // 不存在的类型
        }
        return iconActive;
    }

    public static @DrawableRes int getIconInactiveByName(String deviceName) {
        @DrawableRes
        int iconInactive = -1;

        if (TextUtils.isEmpty(deviceName)){
            return iconInactive;
        }

        if (deviceName.contains(IDeviceName.NAEM_GATEWAY)){
            iconInactive = R.drawable.gateway_inactive;
        }else if (deviceName.contains(IDeviceName.NAME_LOCK_DEADBOLT)){
            iconInactive = R.drawable.deadbolt_inactive;
        }else if (deviceName.contains(IDeviceName.NAME_LOCK_KEY_BOX)){
            iconInactive = R.drawable.keybox_inactive;
        }else {
            // 不存在的类型
        }
        return iconInactive;
    }

    public static String getModelNumByNameWhenProduct(String deviceName) {
        String modelNum = "";

        if (TextUtils.isEmpty(deviceName)){
            return modelNum;
        }

        if (deviceName.contains(IDeviceName.NAEM_GATEWAY)){
            modelNum = IModelNum.NAEM_GATEWAY;
        }else if (deviceName.contains(IDeviceName.NAME_LOCK_DEADBOLT)){
            modelNum = IModelNum.NAME_LOCK_DEADBOLT;
        }else if (deviceName.contains(IDeviceName.NAME_LOCK_KEY_BOX)){
            modelNum = IModelNum.NAME_LOCK_KEY_BOX;
        }else {
            // 不存在的类型
        }
        return modelNum;
    }

    public static @DrawableRes int getProductPictureByName(String deviceName) {
        @DrawableRes
        int productPicture = -1;

        if (TextUtils.isEmpty(deviceName)){
            return productPicture;
        }

        if (deviceName.contains(IDeviceName.NAEM_GATEWAY)){
            productPicture = R.drawable.product_gateway;
        }else if (deviceName.contains(IDeviceName.NAME_LOCK_DEADBOLT)){
            productPicture = R.drawable.product_deadbolt;
        }else if (deviceName.contains(IDeviceName.NAME_LOCK_KEY_BOX)){
            productPicture = R.drawable.product_keybox;
        }else {
            // 不存在的类型
        }
        return productPicture;
    }

}
