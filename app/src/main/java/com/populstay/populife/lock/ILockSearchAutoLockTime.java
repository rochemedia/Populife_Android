package com.populstay.populife.lock;

/**
 * 读取锁的自动闭锁时间
 * Created by Jerry
 */
public interface ILockSearchAutoLockTime {

	void onSearchAutoLockTimeSuccess(int second);

	void onSearchAutoLockTimeFail();
}
