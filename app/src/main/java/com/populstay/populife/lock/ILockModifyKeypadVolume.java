package com.populstay.populife.lock;

/**
 * 设置锁键盘按键音的开启状态
 * Created by Jerry
 */
public interface ILockModifyKeypadVolume {

	void onSuccess(int state);

	void onFail();
}
