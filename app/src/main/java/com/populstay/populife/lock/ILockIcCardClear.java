package com.populstay.populife.lock;

import com.ttlock.bl.sdk.entity.Error;

/**
 * Created by Jerry
 */
public interface ILockIcCardClear {

	void onSuccess();

	void onFail(Error error);
}
