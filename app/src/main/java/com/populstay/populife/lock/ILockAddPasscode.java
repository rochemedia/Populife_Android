package com.populstay.populife.lock;

/**
 * 闭锁接口回调
 * Created by Jerry
 */
public interface ILockAddPasscode {

	void onSuccess();

	void onFail();

	void onTimeOut();
}
