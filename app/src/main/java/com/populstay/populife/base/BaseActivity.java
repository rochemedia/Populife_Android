package com.populstay.populife.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.activity.LoginVerifyActivity;
import com.populstay.populife.activity.SignActivity;
import com.populstay.populife.activity.SplashActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.eventbus.Event;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.permission.PermissionListener;
import com.populstay.populife.push.EventPushService;
import com.populstay.populife.sign.ISignListener;
import com.populstay.populife.sign.SignHandler;
import com.populstay.populife.ui.loader.PeachLoader;
import com.populstay.populife.util.activity.ActivityCollector;
import com.populstay.populife.util.bluetooth.BluetoothUtil;
import com.populstay.populife.util.device.DeviceUtil;
import com.populstay.populife.util.device.HideIMEUtil;
import com.populstay.populife.util.dialog.DialogUtil;
import com.populstay.populife.util.locale.LanguageUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.net.NetworkUtil;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.toast.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Jerry
 */
public class BaseActivity extends AppCompatActivity {

	private static final int REQUEST_CODE_PERMISSION = 30;
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (EventPushService.ACTION_NEW_DEVICE_LOGIN.equals(action)) { // 当前账号已经异地登录
				showNewDeviceLoginDialog();
			}
		}
	};
	private PermissionListener mPermissionListener = null;
//	private MessageReceiver mMessageReceiver;
	private boolean mRemoteLoginChecking;
	private LocationManager mLocationManager;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		registerNetStateChangeReceiver();
		// 将当前 activity 加入到活动收集器中
		ActivityCollector.addActivity(this);
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}
		registerReceiver(mReceiver, getIntentFilter());
//		// 注册美洽
//		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, intentFilter);

		//  设置本地化语言
		setLocale();
	}



	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setNavigationBarColor(getResources().getColor(R.color.white));
		}
	}

	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		HideIMEUtil.wrap(this);
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		HideIMEUtil.wrap(this);
	}

	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		super.setContentView(view, params);
		HideIMEUtil.wrap(this);
	}

	/**
	 * 设置Locale
	 */
	private void setLocale() {
		if (!LanguageUtil.isSameLanguage(this)) {
			LanguageUtil.setLocale(this);
			LanguageUtil.toRestartMainActvity(this);
		}
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		Context context = languageWork(newBase);
		super.attachBaseContext(context);

	}

	private Context languageWork(Context context) {
		// 8.0及以上使用createConfigurationContext设置configuration
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			return updateResources(context);
		} else {
			return context;
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	private Context updateResources(Context context) {
		Resources resources = context.getResources();
		int type = LanguageUtil.getLanguageType(context);
		Locale locale = LanguageUtil.getLocaleByType(type);
		if (locale == null) {
			return context;
		}
		Configuration configuration = resources.getConfiguration();
		configuration.setLocale(locale);
		configuration.setLocales(new LocaleList(locale));
		return context.createConfigurationContext(configuration);
	}

	private IntentFilter getIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(EventPushService.ACTION_NEW_DEVICE_LOGIN);
		return intentFilter;
	}

	public void showLoading() {

		PeachLoader.showLoading(this);
	}

	public void stopLoading() {
		PeachLoader.stopLoading();
	}

	/**
	 * 显示 toast 内容
	 *
	 * @param content 需要显示的内容
	 */
	public void toast(String content) {
		ToastUtil.showToast(content);
	}

	/**
	 * 显示 toast 内容
	 *
	 * @param resId 字符串内容的资源 id
	 */
	public void toast(int resId) {
		ToastUtil.showToast(resId);
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

	public boolean isBleEnableNotHint() {
		boolean isEnable = true;

		if (!BluetoothUtil.isBleEnable()) {
			isEnable = false;
		}
		return isEnable;
	}

	/**
	 * 定位服务是否可用
	 */
	public boolean isLbsEnableNotHint() {
		if (null == mLocationManager){
			mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		}
		return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
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

	public boolean isNetEnableNotHint() {
		boolean isEnable = true;

		if (!NetworkUtil.isNetConnected()) {
			isEnable = false;
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
	 * 跳转至新的 activity 页面（无需在 activity 之间传递数据）
	 *
	 * @param clazz 要启动的 activity 类名
	 */
	public void goToNewActivity(Class clazz) {
		Intent intent = new Intent(this, clazz);
		startActivity(intent);
	}

	/**
	 * 退出当前 activity
	 *
	 * @param view view 视图
	 */
	public void finishCurrentActivity(View view) {
		this.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		stopLoading();

		EventBus.getDefault().unregister(this);
		unregisterReceiver(mReceiver);
		unregisterNetStateChangeReceiver();

		// 当前 activity 销毁时,将其从活动收集器中移除
		ActivityCollector.removeActivity(this);
//		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
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

	@Override
	protected void onResume() {
		super.onResume();
		queryLatestDeviceId();
	}


	private boolean isNeedCheckRemoteLogin(){
		if (this instanceof SignActivity || this instanceof LoginVerifyActivity || this instanceof SplashActivity){
			return false;
		}

		long currentTime = System.currentTimeMillis();
		long lastCheckRemoteLoginTime = PeachPreference.getLastCheckRemoteLoginTime();
		if (currentTime - lastCheckRemoteLoginTime < 1000 * 30){
			return false;
		}

		return true;
	}

	/**
	 * 查询最新的设备id, 判断是否已经在异地登录
	 */
	protected void queryLatestDeviceId() {
		if (!isNeedCheckRemoteLogin()){
			return;
		}

		// 检查中
		if (mRemoteLoginChecking){
			return;
		}
		mRemoteLoginChecking = true;

		RestClient.builder()
				.url(Urls.QUERY_LATEST_DEVICE_ID)
				.params("userId", PeachPreference.readUserId())
				.success(new ISuccess() {
					@Override
							public void onSuccess(String response) {
						mRemoteLoginChecking = false;
						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							String latestDeviceId = result.getString("data");// 最新登录的设备 id
							String localDeviceId = DeviceUtil.getDeviceId(BaseActivity.this);
							if (latestDeviceId != null && !latestDeviceId.equals(localDeviceId)) {
								showNewDeviceLoginDialog();
							}
						}
					}
				}).failure(new IFailure() {
			@Override
			public void onFailure() {
				mRemoteLoginChecking = false;
			}
		}).build().get();
	}

	private void showNewDeviceLoginDialog() {
		DialogUtil.showCommonDialog(BaseActivity.this, null,
				getString(R.string.note_login_new_device),
				getString(R.string.ok), null, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						SignHandler.onSignOut(new ISignListener() {
							@Override
							public void onSignInSuccess() {
							}

							@Override
							public void onSignUpSuccess() {
							}

							@Override
							public void onSignOutSuccess() {
								SignActivity.actionStart(BaseActivity.this, SignActivity.VAL_ACCOUNT_SIGN_IN);
								ActivityCollector.finishAll();
							}
						});
					}
				}, null);
	}


	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(Event event){
		if (null == event || event.type <=0){
			PeachLogger.d("onEvent--无效事件");
			return;
		}
		onEventSub(event);
	}

	public void onEventSub(Event event){
		// 检查异地登录
		if (Event.EventType.FRAGMENT_RESUME_CHECK_REMOTE_LOGIN == event.type){
			queryLatestDeviceId();
		}
	}

	public void onNetStateChange(boolean isNetEnable){

	}

	public NetStateChangeReceiver mNetStateChangeReceiver;

	private void registerNetStateChangeReceiver(){
		if(mNetStateChangeReceiver == null){
			mNetStateChangeReceiver = new NetStateChangeReceiver(this);
		}
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(mNetStateChangeReceiver, intentFilter);
	}

	private void unregisterNetStateChangeReceiver(){
		if(mNetStateChangeReceiver != null){
			unregisterReceiver(mNetStateChangeReceiver);
			mNetStateChangeReceiver = null;
		}
	}

	static class NetStateChangeReceiver extends BroadcastReceiver {

		SoftReference<BaseActivity> softReference;
		public NetStateChangeReceiver(BaseActivity baseActivity){
			softReference = new SoftReference(baseActivity);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			if (null == softReference){
				return;
			}
			if (intent.getAction().matches("android.net.conn.CONNECTIVITY_CHANGE"))
			{
				boolean  isNetEnable = softReference.get().isNetEnable();
				PeachLogger.d("BroadcastReceiver-->onNetStateChange-->isNetEnable=" + isNetEnable);
				softReference.get().onNetStateChange(isNetEnable);
			}
		}
	}

}
