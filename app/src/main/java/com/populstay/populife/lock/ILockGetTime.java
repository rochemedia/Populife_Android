package com.populstay.populife.lock;

/**
 * 读取锁的时间
 * Created by Jerry
 */
public interface ILockGetTime {

	void onGetTimeSuccess(long time);

	void onGetTimeFail();
}
