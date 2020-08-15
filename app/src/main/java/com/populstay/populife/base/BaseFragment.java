package com.populstay.populife.base;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.populstay.populife.R;
import com.populstay.populife.eventbus.Event;
import com.populstay.populife.permission.PermissionListener;
import com.populstay.populife.util.activity.ActivityCollector;
import com.populstay.populife.util.bluetooth.BluetoothUtil;
import com.populstay.populife.util.device.HideIMEUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.net.NetworkUtil;
import com.populstay.populife.util.toast.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * fragment 基类
 * 懒加载
 * Created by Jerry
 */
public abstract class BaseFragment extends Fragment {

	private static final int REQUEST_CODE_PERMISSION = 20;
	protected Activity mActivity;
	private PermissionListener mPermissionListener = null;
	private View mRootView;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup parent = (ViewGroup) mRootView.getParent();
		if (parent != null) {
			parent.removeView(mRootView);
		}
		parent = null;
		HideIMEUtil.wrap(this);
		return mRootView;
	}

	/**
	 * Toast 提醒
	 * 直接传入要提醒的硬编码字符串文字
	 *
	 * @param text 要提醒的硬编码字符串文字
	 */
	public void toast(String text) {
		ToastUtil.showToast(text);
	}

	/**
	 * Toast 提醒
	 * 传入在 strings.xml 中定义的字符串文字的 id 引用
	 *
	 * @param id 在 strings.xml 中定义的字符串文字的 id 引用
	 */
	public void toast(int id) {
		ToastUtil.showToast(id);
	}

	public void toastSuccess() {
		toast(R.string.operation_success);
	}

	public void toastFail() {
		toast(R.string.operation_fail);
	}

	/**
	 * 检测蓝牙是否开启，未开启则弹出提示消息
	 */
	public boolean isBleEnable() {
		boolean isEnable = true;

		if (!BluetoothUtil.isBleEnable()) {
			isEnable = false;
			toast(R.string.enable_bluetooth);
		}

		return isEnable;
	}

	/**
	 * 检测网络是否开启，未开启则弹出提示消息
	 */
	public boolean isNetEnable() {
		boolean isEnable = true;

		if (!NetworkUtil.isNetConnected()) {
			isEnable = false;
			toast(R.string.note_network_error);
		}

		return isEnable;
	}

	/**
	 * 检测 蓝牙 和 网络 是否同时开启，未开启则弹出提示消息
	 */
	public boolean isBleNetEnable() {
		boolean isEnable = true;

		if (!isBleEnable()) {
			isEnable = false;
		} else if (!isNetEnable()) {
			isEnable = false;
		}

		return isEnable;
	}

	/**
	 * 跳转至新的 activity 页面
	 *
	 * @param clazz 要启动的 activity 类名
	 */
	public void goToNewActivity(Class clazz) {
		Intent intent = new Intent(getActivity(), clazz);
		startActivity(intent);
	}

	/**
	 * 运行时权限处理
	 */
	public void requestRuntimePermissions(String[] permissions, PermissionListener permissionListener) {
		Activity topActivity = ActivityCollector.getTopActivity();
		if (null == topActivity) {
			return;
		}
		mPermissionListener = permissionListener;
		List<String> permissionList = new ArrayList<>();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			for (String permission : permissions) {
				if (ContextCompat.checkSelfPermission(topActivity, permission) != PackageManager.PERMISSION_GRANTED) {
					permissionList.add(permission);
				}
				if (!permissionList.isEmpty()) {
					ActivityCompat.requestPermissions(topActivity, permissionList.toArray(new String[permissionList.size()]), REQUEST_CODE_PERMISSION);
				} else {
					mPermissionListener.onGranted();
				}
			}
		} else {
			mPermissionListener.onGranted();
		}
	}

	/**
	 * 用户对运行时权限授权/拒绝后，进行后续的回调操作
	 *
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == REQUEST_CODE_PERMISSION) {
			if (grantResults.length > 0) {
				List<String> deniedPermissions = new ArrayList<>();
				for (int i = 0; i < grantResults.length; i++) {
					int grantResult = grantResults[i];
					String permission = permissions[i];
					if (grantResult != PackageManager.PERMISSION_GRANTED) {
						deniedPermissions.add(permission);
					}
				}
				if (deniedPermissions.isEmpty()) {
					mPermissionListener.onGranted();
				} else {
					mPermissionListener.onDenied(deniedPermissions);
				}
			}
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(Event event){
		if (null == event || event.type <=0){
			PeachLogger.d("Fragment onEvent--无效事件");
			return;
		}
		onEventSub(event);
	}

	public void onEventSub(Event event){

	}
}
