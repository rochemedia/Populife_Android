package com.populstay.populife.home.entity;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.populstay.populife.R;

public class HomeDeviceInfo {

    public interface IDeviceModel {
        // 网关
        String MODEL_GATEWAY = "G2";
        // 横闩锁
        String MODEL_LOCK_DEADBOLT = "SN138-PPL-DB_PV53";
        // 密码锁
        String MODEL_LOCK_KEY_BOX = "1";
    }


    public static @StringRes int getNameByModel(String deviceModel) {
        @StringRes
        int name = -1;
        switch (deviceModel) {
            case IDeviceModel.MODEL_GATEWAY:
                name = R.string.device_name_gateway;
                break;
            case IDeviceModel.MODEL_LOCK_DEADBOLT:
                name = R.string.lock_type_deadbolt;
                break;
            case IDeviceModel.MODEL_LOCK_KEY_BOX:
                name = R.string.lock_type_keybox;
                break;
            default:
                // 不存在的类型
                break;
        }
        return name;
    }

    public static @DrawableRes int getIconActiveByModel(String deviceModel) {
        @DrawableRes
        int iconActive = -1;
        switch (deviceModel) {
            case IDeviceModel.MODEL_GATEWAY:
                iconActive = R.drawable.gateway_active;
                break;
            case IDeviceModel.MODEL_LOCK_DEADBOLT:
                iconActive = R.drawable.deadbolt_active;
                break;
            case IDeviceModel.MODEL_LOCK_KEY_BOX:
                iconActive = R.drawable.keybox_active;
                break;
            default:
                // 不存在的类型
                break;
        }
        return iconActive;
    }

    public static @DrawableRes int getIconInactiveByModel(String deviceModel) {
        @DrawableRes
        int iconInactive = -1;
        switch (deviceModel) {
            case IDeviceModel.MODEL_GATEWAY:
                iconInactive = R.drawable.gateway_inactive;
                break;
            case IDeviceModel.MODEL_LOCK_DEADBOLT:
                iconInactive = R.drawable.deadbolt_inactive;
                break;
            case IDeviceModel.MODEL_LOCK_KEY_BOX:
                iconInactive = R.drawable.keybox_inactive;
                break;
            default:
                // 不存在的类型
                break;
        }
        return iconInactive;
    }








}
