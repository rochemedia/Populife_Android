package com.populstay.populife.find.entity;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.populstay.populife.R;

public class UserManualInfo {

    public interface IUserManualType {
        // 网关
        String USER_MANUAL_TYPE_GATEWAY = "G2_5db4ad";//
        // 横闩锁
        String USER_MANUAL_LOCK_TYPE_DEADBOLT = "PPL-DB_a67291";
        // 密码锁
        String USER_MANUAL_LOCK_TYPE_KEY_BOX = "1";
        // App手册
        String USER_MANUAL_TYPE_APP = "0";
    }


    public static @StringRes int getNameByType(String type) {
        @StringRes
        int name = -1;
        switch (type) {
            case IUserManualType.USER_MANUAL_TYPE_GATEWAY:
                name = R.string.user_manual_gateway;
                break;
            case IUserManualType.USER_MANUAL_LOCK_TYPE_DEADBOLT:
                name = R.string.user_manual_deadbolt;
                break;
            case IUserManualType.USER_MANUAL_LOCK_TYPE_KEY_BOX:
                name = R.string.user_manual_keybox;
                break;
            case IUserManualType.USER_MANUAL_TYPE_APP:
                name = R.string.user_manual_app;
                break;
        }
        return name;
    }

    public static @DrawableRes int getIconActiveByType(String type) {
        @DrawableRes
        int iconActive = -1;
        switch (type) {
            case IUserManualType.USER_MANUAL_TYPE_GATEWAY:
                iconActive = R.drawable.gateway_active;
                break;
            case IUserManualType.USER_MANUAL_LOCK_TYPE_DEADBOLT:
                iconActive = R.drawable.deadbolt_active;
                break;
            case IUserManualType.USER_MANUAL_LOCK_TYPE_KEY_BOX:
                iconActive = R.drawable.keybox_active;
                break;
            case IUserManualType.USER_MANUAL_TYPE_APP:
                iconActive = R.drawable.app_active;
                break;
        }
        return iconActive;
    }

    public static @DrawableRes int getIconInactiveByType(String type) {
        @DrawableRes
        int iconInactive = -1;
        switch (type) {
            case IUserManualType.USER_MANUAL_TYPE_GATEWAY:
                iconInactive = R.drawable.gateway_inactive;
                break;
            case IUserManualType.USER_MANUAL_LOCK_TYPE_DEADBOLT:
                iconInactive = R.drawable.deadbolt_inactive;
                break;
            case IUserManualType.USER_MANUAL_LOCK_TYPE_KEY_BOX:
                iconInactive = R.drawable.keybox_inactive;
                break;
            case IUserManualType.USER_MANUAL_TYPE_APP:
                iconInactive = R.drawable.app_inactive;
                break;
        }
        return iconInactive;
    }
}
