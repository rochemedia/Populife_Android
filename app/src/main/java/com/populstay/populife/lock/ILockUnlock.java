package com.populstay.populife.lock;

/**
 * 开锁接口回调
 * Created by Jerry
 */
public interface ILockUnlock {

	void onUnlockSuccess(int battery);

	void onUnlockFail();

	void onUnlockFinish();
}
