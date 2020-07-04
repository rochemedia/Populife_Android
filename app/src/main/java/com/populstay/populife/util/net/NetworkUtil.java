package com.populstay.populife.util.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.populstay.populife.base.BaseApplication;

public class NetworkUtil {

	public NetworkUtil() {
	}

	/**
	 * 获取 WiFi 的名称
	 */
	public static String getWifiSSid() {
		WifiManager wifiManager = (WifiManager) BaseApplication.getApplication().getApplicationContext()
				.getSystemService(Context.WIFI_SERVICE);
		String ssid = "";
		if (wifiManager != null) {
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			if (wifiInfo != null) {
				ssid = wifiInfo.getSSID();
				if (ssid.length() > 2 && ssid.charAt(0) == '"' && ssid.charAt(ssid.length() - 1) == '"') {
					ssid = ssid.substring(1, ssid.length() - 1);
				}
			}
		}

		return ssid;
	}

	/**
	 * 获取当前网络连接状态
	 */
	public static boolean isNetConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) BaseApplication.getApplication()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean flag = false;
		if (connectivityManager != null) {
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if (networkInfo != null) {
				flag = networkInfo.isConnected();
			}
		}

		return flag;
	}
}