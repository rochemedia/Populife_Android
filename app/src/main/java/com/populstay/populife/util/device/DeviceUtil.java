package com.populstay.populife.util.device;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.provider.Settings;

import java.util.UUID;

/**
 * 设备信息工具类
 * Created by Jerry
 */
public class DeviceUtil {

	public static String getDeviceId(Context context) {
		String deviceId = "";

		if (deviceId != null && !"".equals(deviceId)) {
			return deviceId;
		}

		if (deviceId == null || "".equals(deviceId)) {
			try {
				deviceId = getAndroidId(context);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (deviceId == null || "".equals(deviceId)) {
			if (deviceId == null || "".equals(deviceId)) {
				UUID uuid = UUID.randomUUID();
				deviceId = uuid.toString().replace("-", "");
			}
		}

		if (deviceId == null || "".equals(deviceId)) {
			try {
				deviceId = getLocalMac(context).replace(":", "");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return deviceId;
	}

//	// IMEI码
//	private static String getIMIEStatus(Context context) {
//		TelephonyManager tm = (TelephonyManager) context
//				.getSystemService(Context.TELEPHONY_SERVICE);
//		String deviceId = tm.getDeviceId();
//		return deviceId;
//	}

	// Mac地址
	private static String getLocalMac(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	// Android Id
	private static String getAndroidId(Context context) {
		String androidId = Settings.Secure.getString(
				context.getContentResolver(), Settings.Secure.ANDROID_ID);
		return androidId;
	}

//	/**
//	 * 获取设备唯一标识 如果IMEI为null则返回wifi mac
//	 *
//	 * @return
//	 */
//	public static String getDeviceId00(Context mContext) {
//		String deviceId = null;
//		TelephonyManager telephonyManager = (TelephonyManager) mContext
//				.getSystemService(Context.TELEPHONY_SERVICE);
//		deviceId = telephonyManager.getDeviceId();
//		if (deviceId != null)
//			return deviceId;
//		WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
//		deviceId = wm.getConnectionInfo().getMacAddress();
//		if (deviceId != null) {
//			deviceId.replace(":", "");
//		}
//		return deviceId;
//	}

	//震动milliseconds毫秒
	public static void vibrate(Activity activity, long milliseconds) {
		Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}

	//以pattern[]方式震动
	public static void vibrate(Activity activity, long[] pattern, int repeat) {
		Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(pattern, repeat);
	}

	//取消震动
	public static void virateCancle(Activity activity) {
		Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
		vib.cancel();
	}
}