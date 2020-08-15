package com.populstay.populife.util.locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.WebView;

import com.populstay.populife.activity.MainActivity;
import com.populstay.populife.activity.SignActivity;
import com.populstay.populife.base.BaseApplication;

import java.util.Locale;

public class LanguageUtil {

	private static final String I18N = "i18n";
	private static final String LOCALE_LANGUAGE = "locale_language";

	private static final String TAG = "LanguageUtil";

	public static boolean isChinese(Context context) {
		Locale locale = context.getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		String country = locale.getCountry();
		return language.equals("zh") && country.equals("CN");
	}

	/**
	 * 设置本地化语言
	 *
	 * @param type
	 */
	public static void setLocale(int type) {
		// 解决 webview 所在的 activity 语言没有切换问题
		new WebView(BaseApplication.getApplication()).destroy();
		// 切换语言
		Resources resources = BaseApplication.getApplication().getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		Configuration config = resources.getConfiguration();
		Locale locale = getLocaleByType(type);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			config.setLocale(locale);
		} else {
			config.locale = locale;
		}
		Log.d(TAG, "setLocale: " + config.locale.toString());
		resources.updateConfiguration(config, dm);
	}

	/**
	 * 根据type获取locale
	 *
	 * @param type
	 * @return
	 */
	public static Locale getLocaleByType(int type) {
		Locale locale;
		// 应用用户选择语言
		switch (type) {
			case 0: // 跟随系统
				// 由于API仅支持7.0，需要判断，否则程序会crash(解决7.0以上系统不能跟随系统语言问题)
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
					LocaleList localeList = LocaleList.getDefault();
					int spType = getLanguageType(BaseApplication.getApplication());
					// 如果app已选择不跟随系统语言，则取第二个数据为系统默认语言
					if (spType != 0 && localeList.size() > 1) {
						locale = localeList.get(1);
					} else {
						locale = localeList.get(0);
					}
				} else {
					locale = Locale.getDefault();
				}
				break;

			case 1: // 英语
				locale = Locale.ENGLISH;
				break;

			case 2: // 简体中文
				locale = Locale.SIMPLIFIED_CHINESE;
				break;

			case 3: // 日语
				locale = Locale.JAPAN;
				break;

			case 4: // 法语
				locale = Locale.FRANCE;
				break;

			case 5: // 德语
				locale = Locale.GERMAN;
				break;

			default: // 默认英语
				locale = Locale.ENGLISH;
				break;
		}
		return locale;
	}

	/**
	 * 根据sp数据设置本地化语言
	 *
	 * @param context
	 */
	public static void setLocale(Context context) {
		int type = getLanguageType(context);
		setLocale(type);
	}

	/**
	 * 判断是否是相同语言
	 *
	 * @param context
	 * @return
	 */
	public static boolean isSameLanguage(Context context) {
		int type = getLanguageType(context);
		return isSameLanguage(type);
	}

	/**
	 * 判断是否是相同语言
	 *
	 * @param type
	 * @return
	 */
	public static boolean isSameLanguage(int type) {
		Locale locale = getLocaleByType(type);
		Locale appLocale = BaseApplication.getApplication().getResources().getConfiguration().locale;
		boolean equals = appLocale.equals(locale);
		Log.d(TAG, "isSameLanguage: " + locale.toString() + " / " + appLocale.toString() + " / " + equals);
		return equals;
	}

	/**
	 * sp存储本地语言类型
	 *
	 * @param type
	 */
	public static void putLanguageType(int type) {
		SharedPreferences sp = BaseApplication.getApplication().getSharedPreferences(I18N, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = sp.edit();
		edit.putInt(LOCALE_LANGUAGE, type);
		edit.apply();
	}

	/**
	 * sp获取本地存储语言类型
	 *
	 * @param context
	 * @return
	 */
	public static int getLanguageType(Context context) {
		SharedPreferences sp = context.getSharedPreferences(I18N, Context.MODE_PRIVATE);
		int type = sp.getInt(LOCALE_LANGUAGE, 0);
		return type;
	}

	/**
	 * 跳转主页
	 *
	 * @param context
	 */
	public static void toRestartMainActvity(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		context.startActivity(intent);
		// 杀掉进程，如果是跨进程则杀掉当前进程
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(0);
	}

	public static void toRestartSignActvity(Context context) {
		Intent intent = new Intent(context, SignActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.putExtra(SignActivity.KEY_ACCOUNT_SIGN_ACTION_TYPE, SignActivity.VAL_ACCOUNT_SIGN_UP);
		context.startActivity(intent);
	}

	/**
	 * 获取国家名字码
	 *
	 * @param context
	 * @return
	 */
	public static String getCountryNameCode(Context context) {
		String countryCode = "";
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm != null) {
			countryCode = tm.getNetworkCountryIso().toUpperCase();
		}
		return countryCode;
	}
}
