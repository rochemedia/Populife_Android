package com.populstay.populife.home.entity;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.populstay.populife.R;

public class HomeDeviceInfo {

    public interface IDeviceName {
        // 网关
        String NAEM_GATEWAY = "G2_5db4ad";//
        // 横闩锁
        String NAME_LOCK_DEADBOLT = "PPL-DB_a67291";
        // 密码锁
        String NAME_LOCK_KEY_BOX = "1";
    }


    public static @StringRes int getTypeNameByName(String deviceName) {
        @StringRes
        int name = -1;
        switch (deviceName) {
            case IDeviceName.NAEM_GATEWAY:
                name = R.string.device_name_gateway;
                break;
            case IDeviceName.NAME_LOCK_DEADBOLT:
                name = R.string.lock_type_deadbolt;
                break;
            case IDeviceName.NAME_LOCK_KEY_BOX:
                name = R.string.lock_type_keybox;
                break;
            default:
                // 不存在的类型
                break;
        }
        return name;
    }

    public static @DrawableRes int getIconActiveByName(String deviceName) {
        @DrawableRes
        int iconActive = -1;
        switch (deviceName) {
            case IDeviceName.NAEM_GATEWAY:
                iconActive = R.drawable.gateway_active;
                break;
            case IDeviceName.NAME_LOCK_DEADBOLT:
                iconActive = R.drawable.deadbolt_active;
                break;
            case IDeviceName.NAME_LOCK_KEY_BOX:
                iconActive = R.drawable.keybox_active;
                break;
            default:
                // 不存在的类型
                break;
        }
        return iconActive;
    }

    public static @DrawableRes int getIconInactiveByName(String deviceName) {
        @DrawableRes
        int iconInactive = -1;
        switch (deviceName) {
            case IDeviceName.NAEM_GATEWAY:
                iconInactive = R.drawable.gateway_inactive;
                break;
            case IDeviceName.NAME_LOCK_DEADBOLT:
                iconInactive = R.drawable.deadbolt_inactive;
                break;
            case IDeviceName.NAME_LOCK_KEY_BOX:
                iconInactive = R.drawable.keybox_inactive;
                break;
            default:
                // 不存在的类型
                break;
        }
        return iconInactive;
    }








}
