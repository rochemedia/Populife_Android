package com.populstay.populife.home.entity;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;

import com.populstay.populife.R;

public class HomeDeviceInfo {

    // 这个不要随便动，需要跟IOS端统一的，用来区分设备类型
    public interface IDeviceName {
        // 网关(G2开头的，在添加设备时，转为Gateway)
        String NAEM_GATEWAY = "Gateway";

        // PPL-DB开头为横闩锁
        String NAME_LOCK_DEADBOLT = "PPL-DB";

        // PPL-KB或KEYBOX开头为密码锁
        String NAME_LOCK_KEY_BOX = "PPL-KB";
        String NAME_LOCK_KEY_BOX_2 = "KEYBOX";
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

        if (deviceName.startsWith(IDeviceName.NAEM_GATEWAY)){
            name = R.string.device_name_gateway;
        }else if (deviceName.startsWith(IDeviceName.NAME_LOCK_DEADBOLT)){
            name = R.string.lock_type_deadbolt;
        }else if (deviceName.startsWith(IDeviceName.NAME_LOCK_KEY_BOX) || deviceName.startsWith(IDeviceName.NAME_LOCK_KEY_BOX_2)){
            name = R.string.lock_type_keybox;
        }else {
            // 不存在的类型
            name = R.string.device_name_gateway;
        }
        return name;
    }

    public static @DrawableRes int getIconByName(String deviceName) {
        @DrawableRes
        int iconActive = -1;

        if (TextUtils.isEmpty(deviceName)){
            return iconActive;
        }

        if (deviceName.startsWith(IDeviceName.NAEM_GATEWAY)){
            iconActive = R.drawable.device_card_single_icon_gateway_selector;
        }else if (deviceName.startsWith(IDeviceName.NAME_LOCK_DEADBOLT)){
            iconActive = R.drawable.device_card_single_icon_deadbolt_selector;
        }else if (deviceName.startsWith(IDeviceName.NAME_LOCK_KEY_BOX) || deviceName.startsWith(IDeviceName.NAME_LOCK_KEY_BOX_2)){
            iconActive = R.drawable.device_card_single_icon_key_box_selector;
        }else {
            // 不存在的类型
            iconActive = R.drawable.device_card_single_icon_gateway_selector;
        }
        return iconActive;
    }

    public static @DrawableRes int getIconInactiveByName(String deviceName) {
        @DrawableRes
        int iconInactive = -1;

        if (TextUtils.isEmpty(deviceName)){
            return iconInactive;
        }

        if (deviceName.startsWith(IDeviceName.NAEM_GATEWAY)){
            iconInactive = R.drawable.device_card_single_icon_gateway_selector;
        }else if (deviceName.startsWith(IDeviceName.NAME_LOCK_DEADBOLT)){
            iconInactive = R.drawable.device_card_single_icon_deadbolt_selector;
        }else if (deviceName.startsWith(IDeviceName.NAME_LOCK_KEY_BOX) || deviceName.startsWith(IDeviceName.NAME_LOCK_KEY_BOX_2)){
            iconInactive = R.drawable.device_card_single_icon_key_box_selector;
        }else {
            // 不存在的类型
            iconInactive = R.drawable.device_card_single_icon_gateway_selector;
        }
        return iconInactive;
    }

    public static String getModelNumByNameWhenProduct(String deviceName) {
        String modelNum = "";

        if (TextUtils.isEmpty(deviceName)){
            return modelNum;
        }

        if (deviceName.startsWith(IDeviceName.NAEM_GATEWAY)){
            modelNum = IModelNum.NAEM_GATEWAY;
        }else if (deviceName.startsWith(IDeviceName.NAME_LOCK_DEADBOLT)){
            modelNum = IModelNum.NAME_LOCK_DEADBOLT;
        }else if (deviceName.startsWith(IDeviceName.NAME_LOCK_KEY_BOX) || deviceName.startsWith(IDeviceName.NAME_LOCK_KEY_BOX_2)){
            modelNum = IModelNum.NAME_LOCK_KEY_BOX;
        }else {
            // 不存在的类型
            modelNum = IModelNum.NAEM_GATEWAY;
        }
        return modelNum;
    }

    public static @DrawableRes int getProductPictureByName(String deviceName) {
        @DrawableRes
        int productPicture = -1;

        if (TextUtils.isEmpty(deviceName)){
            return productPicture;
        }

        if (deviceName.startsWith(IDeviceName.NAEM_GATEWAY)){
            productPicture = R.drawable.product_gateway;
        }else if (deviceName.startsWith(IDeviceName.NAME_LOCK_DEADBOLT)){
            productPicture = R.drawable.product_deadbolt;
        }else if (deviceName.startsWith(IDeviceName.NAME_LOCK_KEY_BOX) || deviceName.startsWith(IDeviceName.NAME_LOCK_KEY_BOX_2)){
            productPicture = R.drawable.product_keybox;
        }else {
            // 不存在的类型
            productPicture = R.drawable.product_gateway;
        }
        return productPicture;
    }

}
