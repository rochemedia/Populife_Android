package com.populstay.populife.lock;

import com.ttlock.bl.sdk.entity.Error;

/**
 * Created by Jerry
 */
public interface ILockIcCardSearch {

	void onSuccess(String icCardInfo);

	void onFail(Error error);
}
