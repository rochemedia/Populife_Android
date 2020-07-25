package com.populstay.populife.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.adapter.GatewayAddListAdapter;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.base.BaseApplication;
import com.populstay.populife.common.Urls;
import com.populstay.populife.home.entity.HomeDevice;
import com.populstay.populife.home.entity.HomeDeviceInfo;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.ui.CustomProgress;
import com.populstay.populife.ui.widget.exedittext.ExEditText;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.net.NetworkUtil;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.string.StringUtil;
import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice;
import com.ttlock.gateway.sdk.api.G2GatewayAPI;
import com.ttlock.gateway.sdk.callback.G2GatewayCallback;
import com.ttlock.gateway.sdk.callback.G2GatewayConnectCallback;
import com.ttlock.gateway.sdk.callback.ScanCallback;
import com.ttlock.gateway.sdk.model.ConfigureGatewayInfo;
import com.ttlock.gateway.sdk.model.DeviceInfo;
import com.ttlock.gateway.sdk.model.Error;
import com.ttlock.gateway.sdk.model.WiFi;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class GatewayAddActivity extends BaseActivity implements TextWatcher {

	private LinearLayout mLlConfig,mLlFoundDeviceView;
	private TextView mTvTitle, mTvOk;
	private ExEditText mEtWifiName, mEtWifiPwd;
	private EditText mEtGatewayName;
	private SeekBar mSeekbarScanDevice;

	private ListView mListView;
	private GatewayAddListAdapter mAdapter;
	private List<ExtendedBluetoothDevice> mDeviceList = new ArrayList<>();

	private G2GatewayAPI mGatewayAPI;
	private CustomProgress mCustomProgress;
	private ExtendedBluetoothDevice mSelectedDevice;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gateway_add);

		initView();
		initListener();
		initData();
	}

	private void initView() {
		findViewById(R.id.page_action).setVisibility(View.GONE);
		mTvTitle = findViewById(R.id.page_title);
		mTvTitle.setText(R.string.add_gateway_title);

		mLlConfig = findViewById(R.id.ll_gateway_add);

		mListView = findViewById(R.id.lv_gateway_add);
		mAdapter = new GatewayAddListAdapter(this, mDeviceList);
		mListView.setAdapter(mAdapter);

		mEtWifiName = findViewById(R.id.et_gateway_add_wifi_name);
		mTvOk = findViewById(R.id.tv_gateway_add_ok);
		mEtWifiPwd = findViewById(R.id.et_gateway_add_wifi_pwd);
		mEtGatewayName = findViewById(R.id.et_gateway_add_gateway_name);
		mEtWifiName.setText(NetworkUtil.getWifiSSid());

		mLlFoundDeviceView = findViewById(R.id.ll_found_device_view);
		mSeekbarScanDevice = findViewById(R.id.seekbar_scan_device);
		mSeekbarScanDevice.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// 返回true，禁止手动拖动进度值
				return true;
			}
		});

	}

	private void initListener() {
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				showLoading();
				mSelectedDevice = mDeviceList.get(position);
				mGatewayAPI.connectGateway(mSelectedDevice, new G2GatewayConnectCallback() {
					@Override
					public void onConnectGateway(ExtendedBluetoothDevice extendedBluetoothDevice) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								stopLoading();
								mListView.setVisibility(View.GONE);
								mLlConfig.setVisibility(View.VISIBLE);
								mEtGatewayName.setText(mSelectedDevice.getName());
							}
						});
					}

					@Override
					public void onDisconnectGateway(ExtendedBluetoothDevice extendedBluetoothDevice) {
						stopLoading();
					}
				});
			}
		});

		mEtWifiName.addTextChangedListener(this);
		mEtWifiPwd.addTextChangedListener(this);
		mEtGatewayName.addTextChangedListener(this);

		mTvOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mCustomProgress = CustomProgress.show(GatewayAddActivity.this,
						getString(R.string.configuring_gateway), false, null);
				mTvOk.setEnabled(false);
				getUserKeyId();
			}
		});
	}

	private void initData() {
		mGatewayAPI = new G2GatewayAPI(this, new G2GatewayCallback() {
			@Override
			public void onScanWiFiByGateway(List<WiFi> list, int i, Error error) {

			}

			@Override
			public void onInitializeGateway(Error error, DeviceInfo deviceInfo) {
				PeachLogger.d("onInitializeGateway deviceInfo=" + deviceInfo.toString());
				PeachLogger.d("onInitializeGateway mSelectedDevice=" + mSelectedDevice.toString());
				checkInitGatewaySuccess(mSelectedDevice.getAddress(),deviceInfo);
			}

			@Override
			public void onEnterDFU(Error error) {

			}
		});
		startScanGateway();
		initSeekbarScanDevice();
	}


	private void startScanGateway(){
		if (null == mGatewayAPI){
			return;
		}
		mGatewayAPI.startScanGateway(new ScanCallback() {
			@Override
			public void onScanResult(ExtendedBluetoothDevice extendedBluetoothDevice) {
				PeachLogger.d(extendedBluetoothDevice);
				if (mAdapter != null){
					mAdapter.updateDevice(extendedBluetoothDevice);
				}
				BaseApplication.getHandler().removeCallbacks(mRunnable);
				mListView.setVisibility(View.VISIBLE);
				mLlFoundDeviceView.setVisibility(View.GONE);
				if (DIALOG != null) {
					DIALOG.cancel();
				}
			}

			@Override
			public void onScanFailed(int i) {
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

			((TextView) window.findViewById(R.id.tv_dialog_input_title)).setText(R.string.search_timeout);
			window.findViewById(R.id.et_dialog_input_content).setVisibility(View.GONE);
			TextView tvContent = window.findViewById(R.id.tv_dialog_content);
			tvContent.setVisibility(View.VISIBLE);
			tvContent.setText(R.string.scan_gateway_fial_hint);
			Button leftButton = window.findViewById(R.id.btn_dialog_input_cancel);
			leftButton.setText(R.string.retry_scan);
			leftButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					DIALOG.cancel();
					startScanGateway();
					initSeekbarScanDevice();
				}
			});
			Button rightButton = window.findViewById(R.id.btn_dialog_input_ok);
			rightButton.setText(R.string.cancel);
			rightButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}
	}

	/**
	 * 通过用户id获取sciener用户主键id
	 */
	private void getUserKeyId() {
		RestClient.builder()
				.url(Urls.GATEWAY_GET_USER_KEY_ID)
				.params("userId", PeachPreference.readUserId())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("GATEWAY_GET_USER_KEY_ID", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							int userKeyId = result.getInteger("data");
							configGateway(userKeyId);
						} else if (code == 951) {
							toast(R.string.note_gateway_donot_exists);
							refreshBtnState();
						} else {
							toast(R.string.note_gateway_init_fail);
							refreshBtnState();
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.note_gateway_init_fail);
						refreshBtnState();
					}
				})
				.error(new IError() {
					@Override
					public void onError(int code, String msg) {
						refreshBtnState();
					}
				})
				.build()
				.get();
	}

	private void configGateway(int userKeyId) {
		String userPwd = PeachPreference.getStr(PeachPreference.ACCOUNT_PWD);
		String md51 = StringUtil.md5(userPwd);
		String md52 = StringUtil.md5(md51);
		String md53 = StringUtil.md5(md52);
		String md54 = StringUtil.md5(md53);

		ConfigureGatewayInfo configureGatewayInfo = new ConfigureGatewayInfo();
		configureGatewayInfo.uid = userKeyId;
		configureGatewayInfo.userPwd = md54;

		configureGatewayInfo.ssid = mEtWifiName.getText().toString().trim();
		configureGatewayInfo.wifiPwd = mEtWifiPwd.getText().toString().trim();
		configureGatewayInfo.plugName = mSelectedDevice.getAddress();

		mGatewayAPI.initializeGateway(configureGatewayInfo);
	}

	/**
	 * 使用SDK添加网关后调用该接口绑定网关名称
	 *
	 * @param gatewayMac The Mac which you will get when calling the SDK method to add a gateway
	 */
	private void addGateway(final String gatewayMac,final DeviceInfo deviceInfo,final int gatewayId) {

		RestClient.builder()
				.url(Urls.GATEWAY_ADD)
				.params("userId", PeachPreference.readUserId())
				.params("gatewayMac", gatewayMac)
				.params("gatewayId", gatewayId)
				//todo 网关版本
				.params("gatewayVersion", "1")
				//todo 网络名称
				.params("networkName", NetworkUtil.getWifiSSid())
				//硬件版本
				.params("hardwareRevision", deviceInfo.getHardwareRevision())
				//固件版本
				.params("firmwareRevision", deviceInfo.getFirmwareRevision())
				//初始化时间
				.params("initDate", System.currentTimeMillis())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("GATEWAY_ADD", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							HomeDevice device = new HomeDevice();
							device.setName(mEtGatewayName.getText().toString().trim());
							device.setDeviceId(String.valueOf(gatewayId));
							device.setModelNum(deviceInfo.getModelNum());
							AddDeviceSuccessActivity.actionStart(GatewayAddActivity.this, HomeDeviceInfo.IDeviceModel.MODEL_GATEWAY, device);
						} else {
							toast(R.string.note_gateway_init_fail);
							refreshBtnState();
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.note_gateway_init_fail);
						refreshBtnState();
					}
				})
				.error(new IError() {
					@Override
					public void onError(int code, String msg) {
						refreshBtnState();
					}
				})
				.build()
				.post();
	}

	/**
	 * 使用SDK添加网关后调用该接口，查询网关是否添加成功
	 *
	 * @param gatewayNetMac The Mac which you will get when calling the SDK method to add a gateway
	 *                      {
	 *     "success": true,
	 *     "code": 200,
	 *     "msg": "",
	 *     "data": {
	 *         "errcode": 0,
	 *         "errmsg": "表示失败或否",
	 *         "gatewayId": 382323
	 *     }
	 * }
	 */
	private void checkInitGatewaySuccess(String gatewayNetMac, final DeviceInfo deviceInfo) {
		RestClient.builder()
				.url(Urls.GATEWAY_INIT_CHECK_SUCCESS)
				.params("userId", PeachPreference.readUserId())
				.params("gatewayNetMac", gatewayNetMac)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("GATEWAY_INIT_CHECK_SUCCESS", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							JSONObject dataObj = result.getJSONObject("data");
							int errcode = dataObj.getInteger("errcode");
							int gatewayId = dataObj.getInteger("gatewayId");
							if (0 == errcode) {
								toast(getString(R.string.note_gateway_init_success));
								refreshBtnState();
								/*Intent intent = new Intent(GatewayAddActivity.this, GatewayListActivity.class);
								startActivity(intent);*/
								// todo

								addGateway(mSelectedDevice.getAddress(),deviceInfo,gatewayId);
							} else {
								toast(R.string.note_gateway_init_fail);
								refreshBtnState();
							}
						} else {
							toast(R.string.note_gateway_init_fail);
							refreshBtnState();
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.note_gateway_init_fail);
						refreshBtnState();
					}
				})
				.error(new IError() {
					@Override
					public void onError(int code, String msg) {
						refreshBtnState();
					}
				})
				.build()
				.get();
	}

	private void refreshBtnState() {
		mCustomProgress.cancel();
		mTvOk.setEnabled(true);
		mTvOk.setText(R.string.ok);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		mTvOk.setEnabled(checkForm());
	}

	private boolean checkForm() {
		String wifiName = mEtWifiName.getText().toString().trim();
		String wifiPwd = mEtWifiPwd.getText().toString().trim();
		String gatewayName = mEtGatewayName.getText().toString().trim();
		boolean isPass = true;
		if (StringUtil.isBlank(wifiName) || StringUtil.isBlank(wifiPwd) || StringUtil.isBlank(gatewayName)
				|| wifiPwd.length() < 8) {
			isPass = false;
		}
		return isPass;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mGatewayAPI.stopScanGateway();
	}
}
