package com.populstay.populife.lock;

/**
 * 读取锁键盘按键音的开启状态
 * Created by Jerry
 */
public interface ILockQueryKeypadVolume {

	void onSuccess(int keypadVoleme);

	void onFail();
}
