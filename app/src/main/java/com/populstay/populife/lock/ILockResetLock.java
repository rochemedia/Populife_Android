package com.populstay.populife.lock;

/**
 * 删除/重置锁（管理员操作）
 * Created by Jerry
 */
public interface ILockResetLock {

	void onSuccess();

	void onFail();

	void onFinish();
}
