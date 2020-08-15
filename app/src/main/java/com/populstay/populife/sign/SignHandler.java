package com.populstay.populife.sign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.app.AccountManager;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;

/**
 * Created by Jerry
 */
public class SignHandler {

	/**
	 * 登录
	 */
	public static void onSignIn(String response, ISignListener signListener) {
		final JSONObject result = JSON.parseObject(response);
		String userId = result.getJSONObject("data").getString("userId");
		PeachPreference.saveUserId(userId);

		// 已经注册并登录成功了
		AccountManager.setSignState(true);
		signListener.onSignInSuccess();
	}

	/**
	 * 注册
	 */
	public static void onSignUp(String response, ISignListener signListener) {
		final JSONObject result = JSON.parseObject(response);
		String userId = result.getString("data");
		PeachLogger.d("userid", userId);
		PeachPreference.saveUserId(userId);

		// 已经注册并登录成功了
		AccountManager.setSignState(true);
		signListener.onSignUpSuccess();
	}

	/**
	 * 退出登录
	 */
	public static void onSignOut(ISignListener signListener) {
		AccountManager.setSignState(false);
		signListener.onSignOutSuccess();
		//todo 退出后，清空全部信息
	}
}
