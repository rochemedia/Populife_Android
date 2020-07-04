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
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.adapter.FoundDeviceAdapter;
import com.populstay.populife.app.MyApplication;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.base.BaseApplication;
import com.populstay.populife.constant.BleConstant;
import com.populstay.populife.enumtype.Operation;
import com.populstay.populife.permission.PermissionListener;
import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice;

import java.util.ArrayList;
import java.util.List;

import static com.populstay.populife.app.MyApplication.mTTLockAPI;

public class FoundDeviceActivity extends BaseActivity implements AdapterView.OnItemClickListener {

	private List<ExtendedBluetoothDevice> mLockList = new ArrayList<>();
	private ListView mListView;
	private FoundDeviceAdapter mAdapter;
	private AlertDialog DIALOG;
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			showNoResultDialog();
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
					BaseApplication.getHandler().postDelayed(mRunnable, 10000);
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
		mListView = findViewById(R.id.lv_found_device);
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
						BaseApplication.getHandler().postDelayed(mRunnable, 10000);
					}

					@Override
					public void onDenied(List<String> deniedPermissions) {
						toast(R.string.note_permission_scan_locks);
						finish();
					}
				});
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
	}

	private void stopScan() {
		mTTLockAPI.stopBTDeviceScan();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopScan();
		unregisterReceiver(mReceiver);
	}
}
