package com.populstay.populife.util.bluetooth;

import android.app.Activity;

import com.populstay.populife.app.MyApplication;
import com.populstay.populife.base.BaseApplication;

import static com.populstay.populife.app.MyApplication.mTTLockAPI;

/**
 * Created by Jerry
 */
public class BluetoothUtil {

	public static boolean isBleEnable() {
		return mTTLockAPI.isBLEEnabled(BaseApplication.getApplication());
	}

	public static void requestBleEnable(Activity activity) {
		MyApplication.mTTLockAPI.requestBleEnable(activity);
	}
}
