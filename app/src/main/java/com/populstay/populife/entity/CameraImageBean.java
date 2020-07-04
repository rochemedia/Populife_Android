package com.populstay.populife.entity;

import android.net.Uri;

/**
 * 存储一些中间值
 */

public final class CameraImageBean {

	private static final CameraImageBean INSTANCE = new CameraImageBean();
	private Uri mPath = null;

	public static CameraImageBean getInstance() {
		return INSTANCE;
	}

	public Uri getPath() {
		return mPath;
	}

	public void setPath(Uri mPath) {
		this.mPath = mPath;
	}
}
