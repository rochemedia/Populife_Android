package com.populstay.populife.app;


import com.populstay.populife.util.storage.PeachPreference;

/**
 * Created by Jerry
 */
public class AccountManager {

	// 保存用户登录状态，登录后调用
	public static void setSignState(boolean state) {
		PeachPreference.setBoolean(SignTag.SIGN_TAG.name(), state);
	}

	// 是否已经登录
	public static boolean isSignIn() {
		return PeachPreference.getBoolean(SignTag.SIGN_TAG.name());
	}

	// 检测账号的登录状态
	public static void checkAccount(IUserChecker checker) {
		if (isSignIn()) {
			checker.onSignIn();
		} else {
			checker.onNotSignIn();
		}
	}

	private enum SignTag {
		SIGN_TAG
	}
}
