package com.populstay.populife.find.entity;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.populstay.populife.R;

public class ProductInfo {

    public interface IProductInfoType {
        // 网关
        String PRODUCT_TYPE_GATEWAY = "G2_5db4ad";//
        // 横闩锁
        String PRODUCT_LOCK_TYPE_DEADBOLT = "PPL-DB_a67291";
        // 密码锁
        String PRODUCT_LOCK_TYPE_KEY_BOX = "1";
        // PopuCare
        String PRODUCT_TYPE_POPU_CARE = "0";
    }

    public static @StringRes
    int getNameByType(String type) {
        @StringRes
        int name = -1;
        switch (type) {
            case IProductInfoType.PRODUCT_TYPE_GATEWAY:
                name = R.string.device_name_gateway;
                break;
            case IProductInfoType.PRODUCT_LOCK_TYPE_DEADBOLT:
                name = R.string.lock_type_deadbolt;
                break;
            case IProductInfoType.PRODUCT_LOCK_TYPE_KEY_BOX:
                name = R.string.lock_type_keybox;
                break;
            case IProductInfoType.PRODUCT_TYPE_POPU_CARE:
                name = R.string.product_type_popu_care_name;
                break;
        }
        return name;
    }

    public static @StringRes
    int getDescByType(String type) {
        @StringRes
        int name = -1;
        switch (type) {
            case IProductInfoType.PRODUCT_TYPE_POPU_CARE:
                name = R.string.product_type_popu_care_desc;
                break;
        }
        return name;
    }

    public static
    float getPriceByType(String type) {
        float price = 0f;
        switch (type) {
            case IProductInfoType.PRODUCT_TYPE_GATEWAY:
                price = 39.9f;
                break;
            case IProductInfoType.PRODUCT_LOCK_TYPE_DEADBOLT:
                price = 129f;
                break;
            case IProductInfoType.PRODUCT_LOCK_TYPE_KEY_BOX:
                price = 99f;
                break;
            case IProductInfoType.PRODUCT_TYPE_POPU_CARE:
                price = 49.9f;
                break;
        }
        return price;
    }

    public static @DrawableRes int getPhotoByType(String type) {
        @DrawableRes
        int iconInactive = -1;
        switch (type) {
            case IProductInfoType.PRODUCT_TYPE_GATEWAY:
                iconInactive = R.drawable.product_gateway;
                break;
            case IProductInfoType.PRODUCT_LOCK_TYPE_DEADBOLT:
                iconInactive = R.drawable.product_deadbolt;
                break;
            case IProductInfoType.PRODUCT_LOCK_TYPE_KEY_BOX:
                iconInactive = R.drawable.product_keybox;
                break;
            case IProductInfoType.PRODUCT_TYPE_POPU_CARE:
                iconInactive = R.drawable.product_popu_care;
                break;
        }
        return iconInactive;
    }

    public static @DrawableRes int getIconInactiveByType(String type) {
        @DrawableRes
        int iconInactive = -1;
        switch (type) {
            case IProductInfoType.PRODUCT_TYPE_GATEWAY:
                iconInactive = R.drawable.gateway_inactive;
                break;
            case IProductInfoType.PRODUCT_LOCK_TYPE_DEADBOLT:
                iconInactive = R.drawable.deadbolt_inactive;
                break;
            case IProductInfoType.PRODUCT_LOCK_TYPE_KEY_BOX:
                iconInactive = R.drawable.keybox_inactive;
                break;
            case IProductInfoType.PRODUCT_TYPE_POPU_CARE:
                iconInactive = R.drawable.app_inactive;
                break;
        }
        return iconInactive;
    }

    public static @DrawableRes int getIconActiveByType(String type) {
        @DrawableRes
        int iconActive = -1;
        switch (type) {
            case IProductInfoType.PRODUCT_TYPE_GATEWAY:
                iconActive = R.drawable.gateway_inactive;
                break;
            case IProductInfoType.PRODUCT_LOCK_TYPE_DEADBOLT:
                iconActive = R.drawable.deadbolt_inactive;
                break;
            case IProductInfoType.PRODUCT_LOCK_TYPE_KEY_BOX:
                iconActive = R.drawable.keybox_inactive;
                break;
            case IProductInfoType.PRODUCT_TYPE_POPU_CARE:
                iconActive = R.drawable.app_inactive;
                break;
        }
        return iconActive;
    }
}
