package com.populstay.populife.util.storage;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.populstay.populife.base.BaseApplication;

/**
 * Created by Jerry
 */
public class PeachPreference {

	public static final String AGREE_USER_TERMS_PRIVACY_POLICY = "AGREE_USER_TERMS_PRIVACY_POLICY"; // 同意用户协议和隐私政策
	public static final String SHOW_APP_USER_MAUAL = "show_app_user_maual"; // 显示 APP 操作手册
	public static final String ACCOUNT_PWD = "account_pwd";
	public static final String ACCOUNT = "account";
	public static final String ACCOUNT_AVATAR = "account_avatar";
	public static final String ACCOUNT_PHONE = "account_phone";
	public static final String ACCOUNT_EMAIL = "account_email";
	public static final String ACCOUNT_NICKNAME = "account_nickname";
	public static final String OPEN_ID = "open_id"; // 科技侠的用户 id
	public static final String HAVE_NEW_MESSAGE = "have_new_message";
	public static final String NOTE_CREATE_CUSTOMIZE_PASSWORD = "NOTE_CREATE_CUSTOMIZE_PASSWORD"; // 提示创建自定义密码
	private static final String ACCOUNT_LOCK_NUM = "account_lock_num"; // 用户名下的 锁/钥匙 数量
	private static final String ACCOUNT_REGISTER_TYPE = "account_register_type";
	private static final String SHOW_LOCKING_REMINDER = "show_loking_dialog";
	private static final String PLAY_LOCKING_SOUND = "play_locking_sound";
	private static final String TOUCH_ID_LOGIN = "touch_id_login"; //指纹验证登录
	private static final String LAST_SELECT_HOME_ID = "last_select_home_id"; //最近一次使用的家庭组
	private static final String LAST_SELECT_HOME_NAME = "last_select_home_name"; //最近一次使用的家庭组
	private static final String SHARE_KEY_PRE_ID = "share_key_pre_id"; //密钥分享ID
	/**
	 * 提示:
	 * <p>
	 * Activity.getPreferences(int mode)
	 * 生成 Activity名.xml 用于Activity内部存储
	 * <p>
	 * PreferenceManager.getDefaultSharedPreferences(Context)
	 * 生成 包名_preferences.xml
	 * <p>
	 * Context.getSharedPreferences(String name,int mode)
	 * 生成 name.xml
	 */
	private static final SharedPreferences PREFERENCES =
			PreferenceManager.getDefaultSharedPreferences(BaseApplication.getApplication());
	private static final String USER_ID = "user_id"; // 系统后台的用户 id
	private static final String USER_LOCK_INFO = "user_lock_info"; // 用户拥有的锁及附属的相关信息

	private static SharedPreferences getAppPreference() {
		return PREFERENCES;
	}

	public static void putStr(String key, String value) {
		getAppPreference()
				.edit()
				.putString(key, value)
				.apply();
	}

	public static String getStr(String key) {
		return getAppPreference()
				.getString(key, "");
	}

	public static void setBoolean(String key, boolean flag) {
		getAppPreference()
				.edit()
				.putBoolean(key, flag)
				.apply();
	}

	public static boolean getBoolean(String key) {
		return getAppPreference()
				.getBoolean(key, false);
	}

	/**
	 * 保存用户的 id
	 *
	 * @param userId 用户的 id
	 */
	public static void saveUserId(String userId) {
		getAppPreference()
				.edit()
				.putString(USER_ID, userId)
				.apply();
	}

	/**
	 * 读取用户的 id
	 *
	 * @return 用户的 id
	 */
	public static String readUserId() {
		return getAppPreference()
				.getString(USER_ID, "");
	}

	/**
	 * 读取科技侠的用户 id
	 *
	 * @return 科技侠的用户 id
	 */
	public static int getOpenid() {
		String openId = getStr(OPEN_ID);
		if (TextUtils.isEmpty(openId))
			return 0;
		return Integer.valueOf(openId);
	}

	/**
	 * 保存用户拥有的锁及附属的相关信息
	 *
	 * @param userLockInfo 用户拥有的锁及附属的相关信息
	 */
	public static void saveUserLockInfo(String userLockInfo) {
		getAppPreference()
				.edit()
				.putString(USER_LOCK_INFO, userLockInfo)
				.apply();
	}

	/**
	 * 读取用户拥有的锁及附属的相关信息
	 *
	 * @return 用户拥有的锁及附属的相关信息
	 */
	public static String readUserLockInfo() {
		return getAppPreference()
				.getString(USER_LOCK_INFO, "");
	}

	/**
	 * 设置是否播放开闭锁声音（针对单个锁设置）
	 *
	 * @param locMac 锁地址
	 * @param isPlay 是否播放
	 */
	public static void setPlayLockingSound(String locMac, boolean isPlay) {
		getAppPreference()
				.edit()
				.putBoolean(PLAY_LOCKING_SOUND + locMac, isPlay)
				.apply();
	}

	/**
	 * 是否播放开闭锁声音（默认播放）
	 */
	public static boolean isPlayLockingSound(String locMac) {
		return getAppPreference()
				.getBoolean(PLAY_LOCKING_SOUND + locMac, true);
	}

	/**
	 * 设置是否显示开闭锁 弹框消息+震动（针对单个锁设置）
	 *
	 * @param userId 用戶ID
	 * @param isShow 是否弹框
	 */
	public static void setShowLockingReminder(String userId, boolean isShow) {
		getAppPreference()
				.edit()
				.putBoolean(SHOW_LOCKING_REMINDER + userId, isShow)
				.apply();
	}

	/**
	 * 是否显示开闭锁 弹框消息+震动（默认播放）
	 */
	public static boolean isShowLockingReminder(String userId) {
		return getAppPreference()
				.getBoolean(SHOW_LOCKING_REMINDER + userId, true);
	}

	/**
	 * 设置用户名下的 锁/钥匙 数量
	 *
	 * @param userId  用户 id
	 * @param lockNum 锁/钥匙 数量
	 */
	public static void setAccountLockNum(String userId, int lockNum) {
		getAppPreference()
				.edit()
				.putInt(ACCOUNT_LOCK_NUM + userId, lockNum)
				.apply();
	}

	/**
	 * 获取用户名下的 锁/钥匙 数量
	 */
	public static int getAccountLockNum(String userId) {
		return getAppPreference()
				.getInt(ACCOUNT_LOCK_NUM + userId, 0);
	}

	/**
	 * 获取用户账号注册类型
	 */
	public static int getAccountRegisterType() {
		return getAppPreference()
				.getInt(ACCOUNT_REGISTER_TYPE + readUserId(), 1);
	}

	/**
	 * 保存用户账号注册类型
	 *
	 * @param registerType 账号注册类型
	 */
	public static void setAccountRegisterType(int registerType) {
		getAppPreference()
				.edit()
				.putInt(ACCOUNT_REGISTER_TYPE + readUserId(), registerType)
				.apply();
	}

	/**
	 * 使用指纹验证登录
	 */
	public static boolean isTouchIdLogin() {
		return getAppPreference()
				.getBoolean(TOUCH_ID_LOGIN + readUserId(), false);
	}

	/**
	 * 设置是否使用指纹验证登录
	 *
	 * @param useTouchId 是否使用 Touch ID
	 */
	public static void setTouchIdLogin(boolean useTouchId) {
		getAppPreference()
				.edit()
				.putBoolean(TOUCH_ID_LOGIN + readUserId(), useTouchId)
				.apply();
	}

	public static void setLastSelectHomeId(String homeId) {
		getAppPreference()
				.edit()
				.putString(LAST_SELECT_HOME_ID + readUserId(), homeId)
				.apply();
	}

	public static String getLastSelectHomeId() {
		return getAppPreference()
				.getString(LAST_SELECT_HOME_ID + readUserId(),"");
	}
	public static void setLastSelectHomeName(String homeName) {
		getAppPreference()
				.edit()
				.putString(LAST_SELECT_HOME_NAME + readUserId(), homeName)
				.apply();
	}

	public static String getLastSelectHomeName() {
		return getAppPreference()
				.getString(LAST_SELECT_HOME_NAME + readUserId(),"");
	}


	public static String getShareKeyPreId(){
		return getAppPreference()
				.getString(SHARE_KEY_PRE_ID,"");
	}
	public static void setShareKeyPreId(String sharePreId){
		getAppPreference()
				.edit()
				.putString(SHARE_KEY_PRE_ID, sharePreId)
				.apply();
	}

}
