package com.populstay.populife.permission;

import java.util.List;

/**
 * 运行时权限处理的回调接口
 * Created by Jerry
 */
public interface PermissionListener {
	/**
	 * 用户已授权所有权限，后续回调逻辑
	 */
	void onGranted();

	/**
	 * 用户未授权所有权限，后续回调逻辑
	 *
	 * @param deniedPermissions 用户所拒绝的权限
	 */
	void onDenied(List<String> deniedPermissions);
}
