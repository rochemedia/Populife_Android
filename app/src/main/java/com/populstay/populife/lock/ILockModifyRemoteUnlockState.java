package com.populstay.populife.lock;

import com.ttlock.bl.sdk.entity.Error;

/**
 * 修改远程开锁状态（开启或关闭远程开锁功能）
 * Created by Jerry
 */
public interface ILockModifyRemoteUnlockState {

	/**
	 * @param battery     锁电量
	 * @param operateType 操作类型（1 get获取、2 modify修改）
	 * @param state       远程开锁开关状态（1 on打开、0 off关闭）
	 * @param feature     设备特征值（specialValue）
	 */
	void onSuccess(int battery, int operateType, int state, int feature);

	void onFail(Error error);
}
