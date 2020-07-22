package com.populstay.populife.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.adapter.FoundDeviceAdapter;
import com.populstay.populife.app.MyApplication;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.base.BaseApplication;
import com.populstay.populife.common.Urls;
import com.populstay.populife.constant.BleConstant;
import com.populstay.populife.enumtype.Operation;
import com.populstay.populife.eventbus.Event;
import com.populstay.populife.home.entity.HomeDevice;
import com.populstay.populife.home.entity.HomeDeviceInfo;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.permission.PermissionListener;
import com.populstay.populife.ui.CustomProgress;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.string.StringUtil;
import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import static com.populstay.populife.app.MyApplication.mTTLockAPI;

public class FoundDeviceActivity extends BaseActivity implements AdapterView.OnItemClickListener {

	private List<ExtendedBluetoothDevice> mLockList = new ArrayList<>();
	private ListView mListView;
	private LinearLayout mLlFoundDeviceView;
	private SeekBar mSeekbarScanDevice;
	private FoundDeviceAdapter mAdapter;
	private AlertDialog DIALOG;
	public static final int SCAN_TIME_OUT_SECONDS = 10 * 1000;
	private int mCurrentScanProgress = 0;
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			Log.e("mhs","mRunnable--mCurrentScanProgress="+mCurrentScanProgress);
			if (mCurrentScanProgress >= SCAN_TIME_OUT_SECONDS){
				showNoResultDialog();
			}else {
				upDateSeekbarScanDevice();
			}
		}
	};
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (BleConstant.ACTION_BLE_DEVICE.equals(action)) {
				Bundle bundle = intent.getExtras();
				if (bundle != null) {
					ExtendedBluetoothDevice device = bundle.getParcelable(BleConstant.DEVICE);
					mAdapter.updateDevice(device);
					BaseApplication.getHandler().removeCallbacks(mRunnable);
					mListView.setVisibility(View.VISIBLE);
					mLlFoundDeviceView.setVisibility(View.GONE);
					if (DIALOG != null) {
						DIALOG.cancel();
					}
				}
			}
//            else if(action.equals(BleConstant.ACTION_BLE_DISCONNECTED)) {
//                cancelProgressDialog();
//            }
		}
	};

	private void showNoResultDialog() {
		DIALOG = new AlertDialog.Builder(this).create();
		DIALOG.setCanceledOnTouchOutside(false);
		DIALOG.show();
		final Window window = DIALOG.getWindow();
		if (window != null) {
			window.setContentView(R.layout.dialog_input);
			window.setGravity(Gravity.CENTER);
			window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			//设置属性
			final WindowManager.LayoutParams params = window.getAttributes();
			params.width = WindowManager.LayoutParams.MATCH_PARENT;
			params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			params.dimAmount = 0.5f;
			window.setAttributes(params);

			((TextView) window.findViewById(R.id.tv_dialog_input_title)).setText(R.string.note_scan_device_no_result);
			window.findViewById(R.id.et_dialog_input_content).setVisibility(View.GONE);
			window.findViewById(R.id.btn_dialog_input_cancel).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			window.findViewById(R.id.btn_dialog_input_ok).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					DIALOG.cancel();
					initSeekbarScanDevice();
				}
			});
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_found_device);

		initView();
	}

	private void initView() {
		mLlFoundDeviceView = findViewById(R.id.ll_found_device_view);
		mListView = findViewById(R.id.lv_found_device);
		mSeekbarScanDevice = findViewById(R.id.seekbar_scan_device);
		mSeekbarScanDevice.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 返回true，禁止手动拖动进度值
				return true;
			}
		});
		mAdapter = new FoundDeviceAdapter(this, mLockList);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		registerReceiver(mReceiver, getIntentFilter());

		//It needs location permission to start bluetooth scan,or it can not scan device
		requestRuntimePermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
				new PermissionListener() {
					@Override
					public void onGranted() {
						//开启蓝牙扫描
						startScan();
					}

					@Override
					public void onDenied(List<String> deniedPermissions) {
						toast(R.string.note_permission_scan_locks);
						finish();
					}
				});
		TextView  tvPageTitle = findViewById(R.id.page_title);
		tvPageTitle.setText(getResources().getString(R.string.locks_nearby));
		initTitleBarRightBtn();
	}

	private void initTitleBarRightBtn() {
		TextView tvSupport = findViewById(R.id.page_action);
		tvSupport.setText("");
		tvSupport.setCompoundDrawablesWithIntrinsicBounds(
				getResources().getDrawable(R.drawable.support_icon), null, null, null);

		tvSupport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}

	private void upDateSeekbarScanDevice(){
		mCurrentScanProgress += 500;
		mSeekbarScanDevice.setProgress(mCurrentScanProgress);
		BaseApplication.getHandler().postDelayed(mRunnable, 500);
	}

	private void initSeekbarScanDevice(){
		mSeekbarScanDevice.setMax(SCAN_TIME_OUT_SECONDS);
		mCurrentScanProgress = 0;
		upDateSeekbarScanDevice();
	}

	private IntentFilter getIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BleConstant.ACTION_BLE_DEVICE);
		intentFilter.addAction(BleConstant.ACTION_BLE_DISCONNECTED);
		return intentFilter;
	}

	@Override
	@RequiresPermission(Manifest.permission.BLUETOOTH)
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (isBleNetEnable()) {
			MyApplication.bleSession.setOperation(Operation.ADD_ADMIN);
			mTTLockAPI.connect((ExtendedBluetoothDevice) mAdapter.getItem(position));

			showLoading();
		}
	}

	private void startScan() {
		mTTLockAPI.startBTDeviceScan();
		initSeekbarScanDevice();
	}

	private void stopScan() {
		mTTLockAPI.stopBTDeviceScan();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopScan();
		unregisterReceiver(mReceiver);
		if (null != DIALOG){
			DIALOG.dismiss();
		}
	}

	@Override
	public void onEventSub(Event event) {
		super.onEventSub(event);
		switch (event.type){
			case Event.EventType.LOCK_LOCAL_INITIALIZE_SUCCEED:
				if (null == event.obj){
					toast(R.string.note_lock_init_fail);
					return;
				}
				initializeLock((String) event.obj);
				break;
		}

	}

	private int mLockId, mBattery;
	private String mLockName;
	private HomeDevice mHomeDevice = new HomeDevice();

	/**
	 * 请求服务器，初始化锁
	 */
	private void initializeLock(final String lockDataJson) {
		final WeakHashMap<String, Object> requestParams = parseLockData(lockDataJson);
		PeachLogger.d("LOCK_INIT", requestParams.toString());
		final CustomProgress customProgress = CustomProgress.show(this,
				getString(R.string.note_lock_init_ing), false, null);
		RestClient.builder()
				.url(Urls.LOCK_INIT)
				.params(requestParams)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						customProgress.cancel();
						PeachLogger.d("LOCK_INIT", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							JSONObject data = result.getJSONObject("data");
							mLockId = data.getInteger("lockId");
							mBattery = (int) requestParams.get("electricQuantity");
							mHomeDevice.setDeviceId(String.valueOf(mLockId));
							mHomeDevice.setName(mLockName);
							PeachPreference.setBoolean(PeachPreference.HAVE_NEW_MESSAGE, true);
							toast(R.string.note_lock_init_success);
							AddDeviceSuccessActivity.actionStart(FoundDeviceActivity.this, HomeDeviceInfo.IDeviceModel.MODEL_LOCK_DEADBOLT, mHomeDevice);
						} else {
							toast(R.string.note_lock_init_fail);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						customProgress.cancel();
						toast(R.string.note_lock_init_fail);
					}
				})
				.error(new IError() {
					@Override
					public void onError(int code, String msg) {
						customProgress.cancel();
						toast(R.string.note_lock_init_fail);
					}
				})
				.build()
				.post();
	}

	/**
	 * 解析锁的数据
	 */
	private WeakHashMap<String, Object> parseLockData(String lockDataJson) {
		JSONObject lockInfo = JSON.parseObject(lockDataJson);
		final WeakHashMap<String, Object> params = new WeakHashMap<>();
		params.put("userId", PeachPreference.readUserId());
		String name = lockInfo.getString("lockName");
		PeachLogger.d("lockName", "lockName="+name);
		//todo
		params.put("name", name);
		//params.put("lockName", name);
		mLockName = name;
		params.put("mac", lockInfo.getString("lockMac"));
		params.put("key", lockInfo.getString("lockKey"));
		params.put("flagPos", lockInfo.getInteger("lockFlagPos"));
		params.put("aesKey", lockInfo.getString("aesKeyStr"));
		params.put("adminPwd", lockInfo.getString("adminPwd"));
		params.put("noKeyPwd", lockInfo.getString("noKeyPwd"));
		String deletePwd = lockInfo.getString("deletePwd");
		params.put("deletePwd", StringUtil.isBlank(deletePwd) ? "" : deletePwd);
		params.put("pwdInfo", lockInfo.getString("pwdInfo"));
		params.put("timestamp", lockInfo.getString("timestamp"));
		params.put("specialValue", lockInfo.getInteger("specialValue"));
		params.put("electricQuantity", lockInfo.getInteger("electricQuantity"));
		params.put("timezoneRawOffSet", String.valueOf(lockInfo.getInteger("timezoneRawOffset")));
		params.put("modelNum", lockInfo.getString("modelNum"));
		params.put("hardwareRevision", lockInfo.getString("hardwareRevision"));
		params.put("firmwareRevision", lockInfo.getString("firmwareRevision"));
		JSONObject lockVersion = lockInfo.getJSONObject("lockVersion");
		params.put("protocolType", lockVersion.getInteger("protocolType"));
		params.put("protocolVersion", lockVersion.getInteger("protocolVersion"));
		params.put("scene", lockVersion.getInteger("scene"));
		params.put("groupId", lockVersion.getInteger("groupId"));
		params.put("orgId", lockVersion.getInteger("orgId"));

		return params;
	}
}
