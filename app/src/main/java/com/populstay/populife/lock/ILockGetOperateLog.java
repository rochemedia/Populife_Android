package com.populstay.populife.lock;

/**
 * 读取锁操作记录
 * Created by Jerry
 */
public interface ILockGetOperateLog {

	void onSuccess(String operateLog);

	void onFail();
}
