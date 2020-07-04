package com.populstay.populife.lock;

/**
 * Created by Jerry
 */
public interface ILockIcCardAdd {

	void onEnterAddMode();

	void onSuccess(long cardNumber);

	void onFail();
}
