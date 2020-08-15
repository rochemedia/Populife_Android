package com.populstay.populife.lock;

/**
 * 闭锁接口回调
 * Created by Jerry
 */
public interface ILockLock {

	void onLockSuccess(int battery);

	void onLockFail();

	void onLockFinish();
}
