package com.populstay.populife.lock;

/**
 * 读取锁的电量
 * Created by Jerry
 */
public interface ILockGetBattery {

	void onGetBatterySuccess(int battery);

	void onGetBatteryFail();
}
