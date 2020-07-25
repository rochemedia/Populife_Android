package com.populstay.populife.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.app.MyApplication;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.Key;
import com.populstay.populife.enumtype.Operation;
import com.populstay.populife.lock.ILockGetBattery;
import com.populstay.populife.lock.ILockGetTime;
import com.populstay.populife.lock.ILockQueryKeypadVolume;
import com.populstay.populife.lock.ILockResetLock;
import com.populstay.populife.lock.ILockSearchAutoLockTime;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.ui.widget.HelpPopupWindow;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.device.DeviceUtil;
import com.populstay.populife.util.dialog.DialogUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.string.StringUtil;
import com.ttlock.bl.sdk.util.DigitUtil;

import static com.populstay.populife.app.MyApplication.CURRENT_KEY;
import static com.populstay.populife.app.MyApplication.mTTLockAPI;

public class LockSettingsActivity extends BaseActivity implements View.OnClickListener {

	public static final String KEY_RESULT_DATA = "key_result_data";
	private static final String KEY_LOCK_SETTINGS_LOCK_MAC = "key_lock_settings_lock_mac";
	private static final String KEY_LOCK_SETTINGS_KEY_TYPE = "key_lock_settings_key_type";
	private static final int REQUEST_CODE_NAME = 1;
	private static final int REQUEST_CODE_PASSCODE = 2;
	private static final int REQUEST_CODE_GROUP = 3;
	private static final int REQUEST_CODE_SPECIAL_VALUE = 4;

	private TextView mTvSerialNum, mTvMacId, mTvBattery, mTvValidity, mTvStartTime, mTvEndTime,
			mTvLockName, mTvLockGroup, mTvAdminPasscode, mTvDelete, mTvRemoteUnlockState;
	private ImageView mIvSyncBattery, mIvBattery, mIvMacDisplay,mIvSetLockTimeHelp;
	private LinearLayout mLlMacId, mLlValidity, mLlStartEndTime, mLlLockName, mLlLockGroup,
			mLlAdminPasscode, mLlLockTime, mLlAutoLocking, mLlLockUpgrade, mLlRemoteUnlock, mLlRecords, mLlKeypadVolume;
	private Switch mSwitch;
	private Space mSpace;
	private AlertDialog DIALOG;
	private EditText mEtDialogInput;
	private CheckBox mCbDeleteKeys;

	private Key mKey = CURRENT_KEY;
	private int mKeyType;//钥匙类型（1限时，2永久，3单次，4循环）
	private String mInputPwd;

	private int mPwdType;//0删除锁时输入密码，1显示 Mac/Id 时输入密码
	private boolean mIsDeleteCallbackCalled; // 删除锁时，onSuccess/onFail 是否被回调过
	private int mPwdWrongCount; // 输入账号密码错误次数

	private HelpPopupWindow mHelpPopupWindow;

	/**
	 * 启动当前 activity
	 *
	 * @param context 上下文
	 * @param lockMac 锁的 Mac 地址
	 */
	public static void actionStart(Context context, String lockMac, int keyType) {
		Intent intent = new Intent(context, LockSettingsActivity.class);
		intent.putExtra(KEY_LOCK_SETTINGS_LOCK_MAC, lockMac);
		intent.putExtra(KEY_LOCK_SETTINGS_KEY_TYPE, keyType);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_settings);

		getIntentData();
		initView();
		initListener();
	}

	private void getIntentData() {
		Intent data = getIntent();
		String lockMac = data.getStringExtra(KEY_LOCK_SETTINGS_LOCK_MAC);
		mKeyType = data.getIntExtra(KEY_LOCK_SETTINGS_KEY_TYPE, 0);
//		mKey = DbService.getKeyByLockmac(lockMac);
//		Log.d("ttttttttttttt1111111111", "" + System.currentTimeMillis());
//		//todo 数据库读取
//		if (mKey == null) {
//			finish();
//		}
//		PeachLogger.d("mac", lockMac);
//		PeachLogger.d("key", mKey);
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.settings);
		findViewById(R.id.page_action).setVisibility(View.GONE);

		mTvSerialNum = findViewById(R.id.tv_lock_settings_serial_number);
		mTvMacId = findViewById(R.id.tv_lock_settings_mac_id);
		mIvSyncBattery = findViewById(R.id.iv_lock_settings_battery_sync);
		mIvBattery = findViewById(R.id.iv_lock_settings_battery);
		mIvMacDisplay = findViewById(R.id.iv_lock_settings_mac_display);
		mTvBattery = findViewById(R.id.tv_lock_settings_battery);
		mLlMacId = findViewById(R.id.ll_lock_settings_mac_id);
		mLlValidity = findViewById(R.id.ll_lock_settings_validity_period);
		mTvValidity = findViewById(R.id.tv_lock_settings_validity_period);
		mLlStartEndTime = findViewById(R.id.ll_lock_settings_time);
		mTvStartTime = findViewById(R.id.tv_lock_settings_start_time);
		mTvEndTime = findViewById(R.id.tv_lock_settings_end_time);
		mLlLockName = findViewById(R.id.ll_lock_settings_lock_name);
		mTvLockName = findViewById(R.id.tv_lock_settings_lock_name);
		mLlLockGroup = findViewById(R.id.ll_lock_settings_lock_group);
		mTvLockGroup = findViewById(R.id.tv_lock_settings_lock_group);
		mLlAdminPasscode = findViewById(R.id.ll_lock_settings_admin_passcode);
		mTvAdminPasscode = findViewById(R.id.tv_lock_settings_admin_passcode);
		mLlLockTime = findViewById(R.id.ll_lock_settings_lock_time);
		mLlAutoLocking = findViewById(R.id.ll_lock_settings_auto_locking);
		mLlLockUpgrade = findViewById(R.id.ll_lock_settings_lock_update);
		mLlRemoteUnlock = findViewById(R.id.ll_lock_settings_remote_unlock);
		mLlRecords = findViewById(R.id.ll_lock_settings_records);
		mTvRemoteUnlockState = findViewById(R.id.tv_lock_settings_remote_unlock_state);
		mLlKeypadVolume = findViewById(R.id.ll_lock_settings_keypad_volume);
		mSwitch = findViewById(R.id.switch_lock_settings_reminder);
		mTvDelete = findViewById(R.id.tv_lock_settings_delete);
		mSpace = findViewById(R.id.space_lock_settings_lock_time);
		mIvSetLockTimeHelp = findViewById(R.id.iv_lock_settings_lock_time_help);

		initUI();
//		getLockBattery();
	}

	@SuppressLint("SetTextI18n")
	private void initUI() {
		boolean isRemind = PeachPreference.isShowLockingReminder(mKey.getLockMac());
		mSwitch.setChecked(isRemind);

		mTvSerialNum.setText(mKey.getLockName());

		refreshBattery();

		//只有管理员才可以校正锁时间
		mLlLockTime.setVisibility(mKey.isAdmin() ? View.VISIBLE : View.GONE);

		//远程开锁开关入口
		boolean isSupportRemoteUnlock = DigitUtil.isSupportRemoteUnlock(mKey.getSpecialValue());
		mLlRemoteUnlock.setVisibility(mKey.isAdmin() ? View.VISIBLE : View.GONE);
		mTvRemoteUnlockState.setText(isSupportRemoteUnlock ? R.string.on : R.string.off);

		// 管理员、同时支持 APP 闭锁，则显示“自动闭锁”，否则隐藏
		mLlAutoLocking.setVisibility(mKey.isAdmin() && DigitUtil.isSupportManualLock(mKey.getSpecialValue()) ? View.VISIBLE : View.GONE);

		if (mKey.isAdmin()) {//管理员

			mTvValidity.setText(getString(R.string.permanent));
			mLlStartEndTime.setVisibility(View.GONE);
			mTvLockName.setText(mKey.getLockAlias());
			mTvLockGroup.setText(StringUtil.isBlank(mKey.getRemarks()) ? getString(R.string.other_lowercase) : mKey.getRemarks());
			mTvAdminPasscode.setText(mKey.getNoKeyPwd());
		} else {
			if (mKey.getKeyRight() == 1) {//授权用户

				mTvLockName.setText(mKey.getLockAlias());
				mTvLockGroup.setText(StringUtil.isBlank(mKey.getRemarks()) ? getString(R.string.other_lowercase) : mKey.getRemarks());

				mLlAdminPasscode.setVisibility(View.GONE);

				switch (mKeyType) {//钥匙类型（1限时，2永久，3单次，4循环）
					case 1:
						mLlValidity.setVisibility(View.GONE);
						mTvStartTime.setText(DateUtil.getDateToString(mKey.getStartDate(), "yyyy-MM-dd HH:mm"));
						mTvEndTime.setText(DateUtil.getDateToString(mKey.getEndDate(), "yyyy-MM-dd HH:mm"));

						break;

					case 2:
						mTvValidity.setText(getString(R.string.permanent));
						mLlStartEndTime.setVisibility(View.GONE);
						break;

					case 3:
						mTvValidity.setText(getString(R.string.one_time));
						mLlStartEndTime.setVisibility(View.GONE);
						break;

					case 4:
						break;

					default:
						break;
				}
			} else {//普通用户
				mLlLockName.setVisibility(View.GONE);
				mLlLockGroup.setVisibility(View.GONE);
				mLlAdminPasscode.setVisibility(View.GONE);
				mLlLockUpgrade.setVisibility(View.GONE);
				mSpace.setVisibility(View.GONE);

				switch (mKeyType) {//钥匙类型（1限时，2永久，3单次，4循环）
					case 1:
						mLlValidity.setVisibility(View.GONE);
						mTvStartTime.setText(DateUtil.getDateToString(mKey.getStartDate(), "yyyy-MM-dd HH:mm"));
						mTvEndTime.setText(DateUtil.getDateToString(mKey.getEndDate(), "yyyy-MM-dd HH:mm"));

						break;

					case 2:
						mTvValidity.setText(getString(R.string.permanent));
						mLlStartEndTime.setVisibility(View.GONE);
						break;

					case 3:
						mTvValidity.setText(getString(R.string.one_time));
						mLlStartEndTime.setVisibility(View.GONE);
						break;

					case 4:

						break;

					default:
						break;
				}
			}
		}
	}

	/**
	 * 刷新锁电量显示
	 */
	@SuppressLint("SetTextI18n")
	private void refreshBattery() {
		Resources res = getResources();
		int battery = CURRENT_KEY.getElectricQuantity();
		mTvBattery.setText(battery + res.getString(R.string.unit_percent));
		int batteryLevel = (battery - 1) / 20;
		int imgResInt = R.drawable.ic_battery_100;
		int txtColor = res.getColor(R.color.battery_high_green);
		switch (batteryLevel) {
			case 0:
				imgResInt = R.drawable.ic_battery_20;
				txtColor = res.getColor(R.color.battery_low_red);
				break;

			case 1:
				imgResInt = R.drawable.ic_battery_40;
				txtColor = res.getColor(R.color.battery_middle_orange);
				break;

			case 2:
				imgResInt = R.drawable.ic_battery_60;
				txtColor = res.getColor(R.color.battery_high_green);
				break;

			case 3:
				imgResInt = R.drawable.ic_battery_80;
				txtColor = res.getColor(R.color.battery_high_green);
				break;

			case 4:
				imgResInt = R.drawable.ic_battery_100;
				txtColor = res.getColor(R.color.battery_high_green);
				break;

			default:
				break;
		}
		mIvBattery.setImageResource(imgResInt);
		mTvBattery.setTextColor(txtColor);
	}

	private void showHelpPopupWindow(View anchor){
		if (null == mHelpPopupWindow){
			mHelpPopupWindow = new HelpPopupWindow(this, R.layout.help_popup_window_layout2, R.dimen.help_win_width, R.dimen.help_win_height_92dp);
		}
		mHelpPopupWindow.show(anchor, Gravity.LEFT);
	}

	private void hideHelpPopupWindow(){
		if (null != mHelpPopupWindow){
			mHelpPopupWindow.dismiss();
		}
	}

	private void initListener() {
		mLlMacId.setOnClickListener(this);
		mLlLockName.setOnClickListener(this);
		mLlLockGroup.setOnClickListener(this);
		mLlAdminPasscode.setOnClickListener(this);
		mLlLockTime.setOnClickListener(this);
		mLlAutoLocking.setOnClickListener(this);
		mLlLockUpgrade.setOnClickListener(this);
		mTvDelete.setOnClickListener(this);
		mSwitch.setOnClickListener(this);
		mLlRemoteUnlock.setOnClickListener(this);
		mLlRecords.setOnClickListener(this);
		mIvSyncBattery.setOnClickListener(this);
		mLlKeypadVolume.setOnClickListener(this);
		mIvSetLockTimeHelp.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {
			case R.id.ll_lock_settings_mac_id:
				showInputDialog();
				mPwdType = 1;
				break;

			case R.id.ll_lock_settings_lock_name:
				intent.setClass(LockSettingsActivity.this, ModifyLockNameActivity.class);
				intent.putExtra(ModifyLockNameActivity.KEY_LOCK_NAME, mTvLockName.getText().toString());
				intent.putExtra(ModifyLockNameActivity.KEY_LOCK_ID, mKey.getLockId());
				startActivityForResult(intent, REQUEST_CODE_NAME);
				break;

			case R.id.ll_lock_settings_lock_group:
				intent.setClass(LockSettingsActivity.this, LockGroupSelectActivity.class);
				intent.putExtra(LockGroupSelectActivity.KEY_LOCK_ID, mKey.getLockId());
				intent.putExtra(LockGroupSelectActivity.KEY_LOCK_GROUP, mTvLockGroup.getText().toString());//锁分组名称
				startActivityForResult(intent, REQUEST_CODE_GROUP);
				break;

			case R.id.ll_lock_settings_admin_passcode:
				intent.setClass(LockSettingsActivity.this, ModifyAdminPasscodeActivity.class);
				intent.putExtra(ModifyAdminPasscodeActivity.KEY_PASSCODE, mTvAdminPasscode.getText().toString());
				startActivityForResult(intent, REQUEST_CODE_PASSCODE);
				break;

			case R.id.ll_lock_settings_lock_time:
				if (isBleEnable())
					readLockTime();
				break;

			case R.id.ll_lock_settings_auto_locking:
				if (isBleEnable())
					searchAutoLockTime();
				break;

			case R.id.ll_lock_settings_lock_update:
				goToNewActivity(LockUpdateActivity.class);
				break;

			case R.id.tv_lock_settings_delete:
				if (mKey.isAdmin()) {//管理员
					showInputDialog();
					mPwdType = 0;
				} else {
					if (mKey.getKeyRight() == 1) {//授权用户
						showChooseDialog();
					} else {//普通用户
						Resources res = getResources();
						DialogUtil.showCommonDialog(LockSettingsActivity.this, null,
								res.getString(R.string.note_confirm_delete), res.getString(R.string.ok),
								res.getString(R.string.cancel), new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialogInterface, int i) {
										deleteEkey("N");
									}
								}, null);
					}
				}
				break;

			case R.id.btn_dialog_input_cancel:
			case R.id.btn_dialog_choose_cancel:
				DIALOG.cancel();
				break;

			case R.id.btn_dialog_input_ok:
				mInputPwd = mEtDialogInput.getText().toString();
				if (!StringUtil.isBlank(mInputPwd)) {
					verifyAccountPwd(mInputPwd);
					DIALOG.cancel();
				} else {
					toast(R.string.enter_account_passwprd);
				}
				break;

			case R.id.btn_dialog_choose_ok:
				String delType = mCbDeleteKeys.isChecked() ? "Y" : "N";
				deleteEkey(delType);
				DIALOG.cancel();
				break;

			case R.id.switch_lock_settings_reminder:
				PeachPreference.setShowLockingReminder(mKey.getLockMac(), mSwitch.isChecked());
				break;

			case R.id.ll_lock_settings_remote_unlock:
				intent.setClass(LockSettingsActivity.this, LockRemoteUnlockConfigActivity.class);
				intent.putExtra(LockRemoteUnlockConfigActivity.KEY_LOCK_SPECIAL_VALUE, mKey.getSpecialValue());
				startActivityForResult(intent, REQUEST_CODE_SPECIAL_VALUE);
				break;

			case R.id.ll_lock_settings_records:
				LockOperateRecordActivity.actionStart(LockSettingsActivity.this, mKey.getLockId());
				break;

			case R.id.iv_lock_settings_battery_sync:
				DialogUtil.showCommonDialog(LockSettingsActivity.this, getString(R.string.sync_battery),
						getString(R.string.note_sync_battery), getString(R.string.ok), getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// 读取锁电量
								if (isBleNetEnable())
									getLockBattery();
							}
						}, null);
				break;

			case R.id.ll_lock_settings_keypad_volume:
				queryKeypadVolume();
				break;
				// 校准锁时间帮助按钮
			case R.id.iv_lock_settings_lock_time_help:
				showHelpPopupWindow(view);
				break;

			default:
				break;
		}
	}

	private void queryKeypadVolume() {
		showLoading();
		setQueryKeypadVolumeCallback();

		if (mTTLockAPI.isConnected(mKey.getLockMac())) {
			mTTLockAPI.operateAudioSwitch(null, 1, 0,
					PeachPreference.getOpenid(), mKey.getLockVersion(), mKey.getAdminPwd(),
					mKey.getLockKey(), mKey.getLockFlagPos(), mKey.getAesKeyStr());
		} else {
			mTTLockAPI.connect(mKey.getLockMac());
		}
	}

	private void setQueryKeypadVolumeCallback() {
		MyApplication.bleSession.setOperation(Operation.QUERY_KEYPAD_VOLUME);

		MyApplication.bleSession.setILockQueryKeypadVolume(new ILockQueryKeypadVolume() {
			@Override
			public void onSuccess(final int keypadVolume) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						stopLoading();
						LockSoundActivity.actionStart(LockSettingsActivity.this, keypadVolume);
					}
				});
			}

			@Override
			public void onFail() {
				stopLoading();
				toastFail();
			}
		});
	}

	private void readLockTime() {
		showLoading();
		setGetTimeCallback();

		if (mTTLockAPI.isConnected(mKey.getLockMac())) {
			mTTLockAPI.getLockTime(null, mKey.getLockVersion(), mKey.getAesKeyStr(), mKey.getTimezoneRawOffset());
		} else {
			mTTLockAPI.connect(mKey.getLockMac());
		}
	}

	private void setGetTimeCallback() {
		MyApplication.bleSession.setOperation(Operation.GET_LOCK_TIME);

		MyApplication.bleSession.setILockGetTime(new ILockGetTime() {
			@Override
			public void onGetTimeSuccess(final long time) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						stopLoading();
						LockTimeActivity.actionStart(LockSettingsActivity.this, time);
					}
				});
			}

			@Override
			public void onGetTimeFail() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						stopLoading();
						toastFail();
					}
				});
			}
		});
	}

	private void searchAutoLockTime() {
		showLoading();
		setSearchAutoLockTimeCallback();

		if (mTTLockAPI.isConnected(mKey.getLockMac())) {
			mTTLockAPI.searchAutoLockTime(null, PeachPreference.getOpenid(),
					mKey.getLockVersion(), mKey.getAdminPwd(), mKey.getLockKey(),
					mKey.getLockFlagPos(), mKey.getAesKeyStr());
		} else {
			mTTLockAPI.connect(mKey.getLockMac());
		}
	}

	private void setSearchAutoLockTimeCallback() {
		MyApplication.bleSession.setOperation(Operation.SEARCH_AUTO_LOCK_TIME);

		MyApplication.bleSession.setILockSearchAutoLockTime(new ILockSearchAutoLockTime() {
			@Override
			public void onSearchAutoLockTimeSuccess(final int second) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						stopLoading();
						LockAutoLockingActivity.actionStart(LockSettingsActivity.this, second);
					}
				});
			}

			@Override
			public void onSearchAutoLockTimeFail() {
				runOnUiThread(new Runnable() {
					@SuppressLint("SetTextI18n")
					@Override
					public void run() {
						stopLoading();
						toastFail();
					}
				});
			}
		});
	}

	/**
	 * （授权/普通）用户删除自己的钥匙（锁设置页面）
	 */
	private void deleteEkey(String delType) {
		RestClient.builder()
				.url(Urls.LOCK_EKEY_DELETE)
				.loader(LockSettingsActivity.this)
				.params("keyId", mKey.getKeyId())
				.params("userId", PeachPreference.readUserId())
				.params("delType", delType)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_EKEY_DELETE", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							PeachPreference.setBoolean(PeachPreference.HAVE_NEW_MESSAGE, true);
							toast(R.string.ekey_delete_success);
//							Key key = DbService.getKeyByLockmac(mKey.getLockMac());
//							if (key != null) {
//								DbService.deleteKey(key);
//							}
							goToNewActivity(MainActivity.class);
						} else {
							toast(R.string.ekey_delete_fail);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.ekey_delete_fail);
					}
				})
				.build()
				.post();
	}

	private void showChooseDialog() {
		DIALOG = new AlertDialog.Builder(this).create();
		DIALOG.setCanceledOnTouchOutside(false);
		DIALOG.show();
		final Window window = DIALOG.getWindow();
		if (window != null) {
			window.setContentView(R.layout.dialog_choose);
			window.setGravity(Gravity.CENTER);
			window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			//设置属性
			final WindowManager.LayoutParams params = window.getAttributes();
			params.width = WindowManager.LayoutParams.MATCH_PARENT;
			params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			params.dimAmount = 0.5f;
			window.setAttributes(params);

			TextView title = window.findViewById(R.id.tv_dialog_choose_title);
			mCbDeleteKeys = window.findViewById(R.id.cb_dialog_choose_content);
			AppCompatButton cancel = window.findViewById(R.id.btn_dialog_choose_cancel);
			AppCompatButton ok = window.findViewById(R.id.btn_dialog_choose_ok);
			ok.setText(R.string.delete);

			title.setText(R.string.note_delete_autu_key);
			mCbDeleteKeys.setText(R.string.note_delete_sent_key);

			cancel.setOnClickListener(this);
			ok.setOnClickListener(this);
		}
	}

	private void verifyAccountPwd(String pwd) {
		RestClient.builder()
				.url(Urls.LOCK_USER_CHECK)
				.loader(LockSettingsActivity.this)
				.params("password", pwd)
				.params("userId", PeachPreference.readUserId())
				.params("lockId", mKey.getLockId())
				.success(new ISuccess() {
					@SuppressLint("SetTextI18n")
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("ACCOUNT_PWD_VERIFY", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							boolean isPwdValid = result.getBoolean("success");
							if (isPwdValid) {
								mPwdWrongCount = 0;
								if (mPwdType == 0) {
									Resources res = getResources();
									DialogUtil.showCommonDialog(LockSettingsActivity.this, null,
											res.getString(R.string.note_confirm_delete), res.getString(R.string.ok),
											res.getString(R.string.cancel), new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialogInterface, int i) {
													adminResetLock();
												}
											}, null);
								} else {
									mIvMacDisplay.setVisibility(View.GONE);
									mTvMacId.setText(mKey.getLockMac() + "/" + mKey.getKeyId());
									mLlMacId.setEnabled(false);
								}
							} else {
								showPwdWrongDialog();
							}
						} else {
							showPwdWrongDialog();
						}
					}
				})
				.build()
				.post();
	}

	private void showPwdWrongDialog() {
//		if (++mPwdWrongCount >= 3) {
//			DialogUtil.showCommonDialog(LockSettingsActivity.this, null,
//					getString(R.string.note_pwd_wrong_times), getString(R.string.reset_pwd),
//					getString(R.string.cancel), new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							goToNewActivity(ResetPwdActivity.class);
//						}
//					}, null);
//		} else {
			toast(R.string.note_pwd_invalid);
//		}
	}

	private void showInputDialog() {
		DIALOG = new AlertDialog.Builder(this).create();
		DIALOG.setCanceledOnTouchOutside(false);
		DIALOG.show();
		final Window window = DIALOG.getWindow();
		if (window != null) {
			window.setContentView(R.layout.dialog_input);
			window.setGravity(Gravity.CENTER);
//			window.setWindowAnimations(R.style.anim_panel_up_from_bottom);
			window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			//设置属性
			final WindowManager.LayoutParams params = window.getAttributes();
			params.width = WindowManager.LayoutParams.MATCH_PARENT;
			params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			params.dimAmount = 0.5f;
			window.setAttributes(params);

			TextView title = window.findViewById(R.id.tv_dialog_input_title);
			mEtDialogInput = window.findViewById(R.id.et_dialog_input_content);
			AppCompatButton cancel = window.findViewById(R.id.btn_dialog_input_cancel);
			AppCompatButton ok = window.findViewById(R.id.btn_dialog_input_ok);

			title.setText(R.string.enter_account_passwprd);
			mEtDialogInput.setHint(R.string.enter_pwd);
			mEtDialogInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			mEtDialogInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

			cancel.setOnClickListener(this);
			ok.setOnClickListener(this);
		}
	}

	private void adminResetLock() {
		if (isBleNetEnable()) {
			showLoading();
			setDeleteLockCallback();

			if (mTTLockAPI.isConnected(mKey.getLockMac())) {
				mTTLockAPI.resetLock(null, PeachPreference.getOpenid(), mKey.getLockVersion(),
						mKey.getAdminPwd(), mKey.getLockKey(), mKey.getLockFlagPos(), mKey.getAesKeyStr());
			} else {//connect the lock
				mTTLockAPI.connect(mKey.getLockMac());
			}
		}
	}

	private void setDeleteLockCallback() {
		MyApplication.bleSession.setOperation(Operation.RESET_LOCK);

		MyApplication.bleSession.setILockResetLock(new ILockResetLock() {
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mIsDeleteCallbackCalled = true;
						stopLoading();
						requestDeleteLock();
					}
				});
			}

			@Override
			public void onFail() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mIsDeleteCallbackCalled = true;
						stopLoading();
						toastFail();
					}
				});
			}

			@Override
			public void onFinish() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (!mIsDeleteCallbackCalled) {
							mIsDeleteCallbackCalled = true;
							stopLoading();
							toast(R.string.note_make_sure_lock_nearby);
						}
					}
				});
			}
		});
	}

	/**
	 * 请求服务器，删除锁
	 */
	private void requestDeleteLock() {
		RestClient.builder()
				.url(Urls.LOCK_ADMIN_DELETE)
				.loader(LockSettingsActivity.this)
				.params("userId", PeachPreference.readUserId())
				.params("lockId", mKey.getLockId())
				.params("password", mInputPwd)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_ADMIN_DELETE", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						boolean isSuccess = result.getBoolean("success");
						if (code == 200 && isSuccess) {
							PeachPreference.setBoolean(PeachPreference.HAVE_NEW_MESSAGE, true);
//							Key key = DbService.getKeyByLockmac(mKey.getLockMac());
//							if (key != null) {
//								DbService.deleteKey(key);
//							}
							int lockNum = PeachPreference.getAccountLockNum(PeachPreference.readUserId());
							PeachPreference.setAccountLockNum(PeachPreference.readUserId(), lockNum - 1);
							goToNewActivity(MainActivity.class);
						} else {
							toastFail();
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toastFail();
					}
				})
				.error(new IError() {
					@Override
					public void onError(int code, String msg) {
						toastFail();
					}
				})
				.build()
				.post();
	}

	/**
	 * 通过 SDK 读取锁电量
	 */
	private void getLockBattery() {
		showLoading();
		setGetBatteryCallback();

		if (mTTLockAPI.isConnected(mKey.getLockMac())) {
			mTTLockAPI.getElectricQuantity(null, mKey.getLockVersion(), mKey.getAesKeyStr());
		} else {
			mTTLockAPI.connect(mKey.getLockMac());
		}
	}

	private void setGetBatteryCallback() {
		MyApplication.bleSession.setOperation(Operation.GET_LOCK_BATTERY);
		MyApplication.bleSession.setLockmac(mKey.getLockMac());

		MyApplication.bleSession.setILockGetBattery(new ILockGetBattery() {
			@Override
			public void onGetBatterySuccess(final int battery) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						stopLoading();
						requestUploadLockBattery(battery);
					}
				});
			}

			@Override
			public void onGetBatteryFail() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						stopLoading();
						toastFail();
					}
				});
			}
		});
	}

	/**
	 * 请求服务器，上传锁电量
	 */
	private void requestUploadLockBattery(final int battery) {
		RestClient.builder()
				.url(Urls.LOCK_UPLOAD_BATTERY)
				.loader(this)
				.params("lockId", mKey.getLockId())
				.params("electricQuantity", String.valueOf(battery))
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_UPLOAD_BATTERY", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							CURRENT_KEY.setElectricQuantity(battery);
							refreshBattery();
							if (battery <= 20) {
								DialogUtil.showCommonDialog(LockSettingsActivity.this, null,
										getString(R.string.note_low_battery), getString(R.string.ok), null,
										null, null);
								DeviceUtil.vibrate(LockSettingsActivity.this, 500);
							} else
								toastSuccess();
						}
					}
				})
				.build()
				.post();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if (resultCode == RESULT_OK) {
			String dataResult = "";
			if (data != null) {
				dataResult = data.getStringExtra(KEY_RESULT_DATA);
			}
			switch (requestCode) {
				case REQUEST_CODE_NAME:
					mTvLockName.setText(dataResult);
					break;

				case REQUEST_CODE_PASSCODE:
					mTvAdminPasscode.setText(dataResult);
					break;

				case REQUEST_CODE_GROUP:
					mTvLockGroup.setText(dataResult);
					break;

				case REQUEST_CODE_SPECIAL_VALUE:
					int specialValue = data.getIntExtra(LockRemoteUnlockConfigActivity.KEY_LOCK_SPECIAL_VALUE, 0);
					mKey.setSpecialValue(specialValue);
					if (DigitUtil.isSupportRemoteUnlock(specialValue)) {
						mTvRemoteUnlockState.setText(R.string.on);
					} else {
						mTvRemoteUnlockState.setText(R.string.off);
					}
					break;

				default:
					break;
			}
		}
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		hideHelpPopupWindow();
	}
}
