package com.populstay.populife.lock;

/**
 * 锁操作回调接口
 * Created by Jerry
 */
public interface ILockResetKeyboardPwd {

	void onSuccess(String pwdInfo, long timestamp);

	void onFail();
}
