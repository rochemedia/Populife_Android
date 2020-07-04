package com.populstay.populife.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;

import com.meiqia.core.MQManager;
import com.meiqia.core.callback.OnInitCallback;
import com.meiqia.core.callback.OnRegisterDeviceTokenCallback;
import com.meiqia.meiqiasdk.util.MQConfig;
import com.mob.MobSDK;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.BuildConfig;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.populstay.populife.R;
import com.populstay.populife.constant.Constant;
import com.populstay.populife.util.device.DeviceUtil;
import com.populstay.populife.util.locale.LanguageUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.toast.ToastUtil;
import com.ttlock.bl.sdk.util.LogUtil;


/**
 * App 基类
 * Created by Jerry
 */

public class BaseApplication extends Application {

	@SuppressLint("HandlerLeak")
	private static final Handler HANDLER = new Handler();
	@SuppressLint("StaticFieldLeak")
	private static Context mContext;

	public static Handler getHandler() {
		return HANDLER;
	}

	/**
	 * 获取 Context
	 */
	public static Context getApplication() {
		return mContext;
	}

	@Override
	public void onCreate() {
		mContext = getApplicationContext();
		super.onCreate();

		// 初始化 开发/发布 模式
		initDebugMode(false);

		//  设置本地化语言
		languageWork();

		// 初始化分享
		MobSDK.init(this);

		// 初始化美洽（在线客服）
		initMeiqiaSDK();
	}

	private void initMeiqiaSDK() {
		MQConfig.init(this, Constant.MEI_QIA_APP_KEY, new OnInitCallback() {
			@Override
			public void onSuccess(String clientId) {
			}

			@Override
			public void onFailure(int code, String message) {
			}
		});

		customMeiqiaSDK();

		MQManager.getInstance(this).registerDeviceToken(DeviceUtil.getDeviceId(this), new OnRegisterDeviceTokenCallback() {
			@Override
			public void onSuccess() {
			}

			@Override
			public void onFailure(int i, String s) {
			}
		});
//		MQManager.getInstance(this).closeMeiqiaService();
	}

	/**
	 * （可选）配置美洽自定义信息
	 */
	private void customMeiqiaSDK() {

		MQConfig.ui.titleGravity = MQConfig.ui.MQTitleGravity.LEFT;
		MQConfig.ui.backArrowIconResId = R.drawable.ic_back;
		MQConfig.ui.titleBackgroundResId = R.color.colorPrimary;
		MQConfig.ui.titleTextColorResId = R.color.white;
//		MQConfig.ui.leftChatBubbleColorResId = R.color.test_green;
//		MQConfig.ui.leftChatTextColorResId = R.color.test_red;
//		MQConfig.ui.rightChatBubbleColorResId = R.color.test_red;
//		MQConfig.ui.rightChatTextColorResId = R.color.test_green;
//		MQConfig.ui.robotEvaluateTextColorResId = R.color.test_red;
//		MQConfig.ui.robotMenuItemTextColorResId = R.color.test_blue;
//		MQConfig.ui.robotMenuTipTextColorResId = R.color.test_blue;
	}

	/**
	 * 初始化 开发/发布 模式
	 *
	 * @param isDebug 是否开发（调试）模式
	 *                开发模式：true
	 *                发布模式：false
	 */
	private void initDebugMode(boolean isDebug) {
		// Logger 日志
		initLogger(isDebug);
		// TTLock SDK
		LogUtil.setDBG(isDebug);
		com.ttlock.gateway.sdk.util.LogUtil.setDBG(isDebug);
		// 美洽
		MQManager.setDebugMode(isDebug);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		languageWork();
	}

	private void languageWork() {
		int type = LanguageUtil.getLanguageType(this);
		LanguageUtil.setLocale(type);
	}

	/**
	 * 初始化 logger 日志工具
	 *
	 * @param isDebug 是否开发（调试）模式
	 *                开发模式：true
	 *                发布模式：false
	 */
	private void initLogger(boolean isDebug) {
		if (isDebug) {
			// 启动 logger
			FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
					.tag(PeachLogger.LOGGER_TAG)
					.build();
			Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
		} else {
			// 禁用 logger
			Logger.addLogAdapter(new AndroidLogAdapter() {
				@Override
				public boolean isLoggable(int priority, String tag) {
					return BuildConfig.DEBUG;
				}
			});
		}
	}

	public void myToast(int resId) {
		ToastUtil.showToast(resId);
	}

	public void myToast(final String msg) {
		ToastUtil.showToast(msg);
	}
}
