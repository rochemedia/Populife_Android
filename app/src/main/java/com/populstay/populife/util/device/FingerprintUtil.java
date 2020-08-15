package com.populstay.populife.util.device;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;

/**
 * Created by Jerry
 */
public class FingerprintUtil {

	public static boolean isSupportFingerprint(Context context) {
		if (Build.VERSION.SDK_INT < 23) {
			// 系统版本过低，不支持指纹功能
			return false;
		} else {
			KeyguardManager keyguardManager = context.getSystemService(KeyguardManager.class);
			FingerprintManager fingerprintManager = context.getSystemService(FingerprintManager.class);
			if (fingerprintManager != null && keyguardManager != null) {
				if (!fingerprintManager.isHardwareDetected()) {
					// 手机不支持指纹功能
					return false;
				} else {
					if (!keyguardManager.isKeyguardSecure()) {
						// 手机还未设置锁屏，需要先设置锁屏并添加一个指纹
						return false;
					} else if (!fingerprintManager.hasEnrolledFingerprints()) {
						// 至少需要在系统设置中添加一个指纹
						return false;
					}
				}
			} else {
				return false;
			}
		}
		return true;
	}
}
