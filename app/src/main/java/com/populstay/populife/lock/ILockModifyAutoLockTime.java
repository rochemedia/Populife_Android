package com.populstay.populife.lock;

/**
 * 修改锁的自动闭锁时间
 * Created by Jerry
 */
public interface ILockModifyAutoLockTime {

	void onModifyAutoLockTimeSuccess();

	void onModifyAutoLockTimeFail();
}
