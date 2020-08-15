package com.populstay.populife.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.app.MyApplication;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.Key;
import com.populstay.populife.entity.Passcode;
import com.populstay.populife.enumtype.Operation;
import com.populstay.populife.keypwdmanage.entity.KeyPwd;
import com.populstay.populife.lock.ILockDeletePasscode;
import com.populstay.populife.lock.ILockModifyPasscode;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.ui.loader.PeachLoader;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.dialog.DialogUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.string.StringUtil;
import com.ttlock.bl.sdk.entity.Error;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;

import static com.populstay.populife.app.MyApplication.mTTLockAPI;

public class PasscodeDetailActivity extends BaseActivity implements View.OnClickListener {

	private static final int REQUEST_CODE_MODIFY_PASSCODE_PERIOD = 1;

	private static final String KEY_PASSCODE = "key_passcode";

	private TextView mTvSend, mTvPasscode, mTvName, mTvValidPeriod, mTvStartTime,
			mTvEndTime, mTvSender, mTvSendingTime, mTvDelete;
	private LinearLayout mLlPasscode, mLlName, mLlValidPeriod, mLlRecord;
	private ImageView mIvPasscodeMore, mIvValidPeriodMore;
	private AlertDialog DIALOG;
	private EditText mEtDialogInput;

	private Key mKey = MyApplication.CURRENT_KEY;
	private KeyPwd mPasscode;
	private int mModifyPasscodeType; // 修改密码的类型（0 修改密码，1 修改密码名称）

	/**
	 * 启动当前 activity
	 *
	 * @param context  上下文
	 * @param passcode 键盘密码
	 */
	public static void actionStart(Context context, Passcode passcode) {
		Intent intent = new Intent(context, PasscodeDetailActivity.class);
		intent.putExtra(KEY_PASSCODE, passcode);
		context.startActivity(intent);
	}
	public static void actionStart(Context context, KeyPwd passcode) {
		Intent intent = new Intent(context, PasscodeDetailActivity.class);
		intent.putExtra(KEY_PASSCODE, passcode);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_passcode_detail);

		getIntentData();
		initView();
		initListener();
	}

	private void getIntentData() {
		mPasscode = getIntent().getParcelableExtra(KEY_PASSCODE);
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.password_detail);
		mTvSend = findViewById(R.id.page_action);
		mTvSend.setText(R.string.share);
		mTvSend.setVisibility(View.GONE);

		mTvPasscode = findViewById(R.id.tv_passcode_detail_passcode);
		mTvName = findViewById(R.id.tv_passcode_detail_name);
		mTvValidPeriod = findViewById(R.id.tv_passcode_detail_valid_period);
		mTvStartTime = findViewById(R.id.tv_passcode_detail_start_time);
		mTvEndTime = findViewById(R.id.tv_passcode_detail_end_time);
		mTvSender = findViewById(R.id.tv_passcode_detail_sender);
		mTvSendingTime = findViewById(R.id.tv_passcode_detail_sending_time);
		mTvDelete = findViewById(R.id.tv_passcode_detail_delete);
		mLlPasscode = findViewById(R.id.ll_passcode_detail_passcode);
		mLlName = findViewById(R.id.ll_passcode_detail_name);
		mLlValidPeriod = findViewById(R.id.ll_passcode_detail_valid_period);
		mLlRecord = findViewById(R.id.ll_passcode_detail_records);
		mIvPasscodeMore = findViewById(R.id.iv_passcode_detail_passcode_more);
		mIvValidPeriodMore = findViewById(R.id.iv_passcode_detail_valid_period_more);

		refreshUI();
	}

	@SuppressLint("SetTextI18n")
	private void refreshUI() {
		mTvPasscode.setText(mPasscode.getKeyboardPwd());
		mTvName.setText(mPasscode.getAlias());
		mTvSender.setText(mPasscode.getSendUser());
		mTvSendingTime.setText(DateUtil.getDateToString(mPasscode.getCreateDate(), "yyyy-MM-dd HH:mm"));

		/**
		 * type	value
		 * One-time			1
		 * Permanent		2
		 * Period			3
		 * Clear			4
		 * Weekend Cyclic	5
		 * Daily Cyclic		6
		 * Workday Cyclic	7
		 * Monday Cyclic	8
		 * Tuesday Cyclic	9
		 * Wednesday Cyclic	10
		 * Thursday Cyclic	11
		 * Friday Cyclic	12
		 * Saturday Cyclic	13
		 * Sunday Cyclic	14
		 */
		int passcodeType = mPasscode.getKeyboardPwdType();
		switch (passcodeType) {
			case 1:
				mLlPasscode.setEnabled(false);
				mLlName.setEnabled(true);
				mLlValidPeriod.setEnabled(false);
				mIvPasscodeMore.setVisibility(View.GONE);
				mIvValidPeriodMore.setVisibility(View.GONE);
				mTvValidPeriod.setVisibility(View.GONE);
				mTvStartTime.setVisibility(View.VISIBLE);
				mTvEndTime.setVisibility(View.VISIBLE);
				mTvStartTime.setText(DateUtil.getDateToString(mPasscode.getCreateDate(), "yyyy-MM-dd HH:mm"));
				mTvEndTime.setText(DateUtil.getDateToString(mPasscode.getCreateDate() + 3600 * 1000 * 6, "yyyy-MM-dd HH:mm"));
				break;

			case 2:
				mLlPasscode.setEnabled(true);
				mLlName.setEnabled(true);
				mLlValidPeriod.setEnabled(true);
				mIvPasscodeMore.setVisibility(View.VISIBLE);
				mIvValidPeriodMore.setVisibility(View.VISIBLE);
				mTvValidPeriod.setVisibility(View.VISIBLE);
				mTvValidPeriod.setText(R.string.permanent);
				mTvStartTime.setVisibility(View.GONE);
				mTvEndTime.setVisibility(View.GONE);
				break;

			case 3:
				mLlPasscode.setEnabled(true);
				mLlName.setEnabled(true);
				mLlValidPeriod.setEnabled(true);
				mIvPasscodeMore.setVisibility(View.VISIBLE);
				mIvValidPeriodMore.setVisibility(View.VISIBLE);
				mTvValidPeriod.setVisibility(View.GONE);
				mTvStartTime.setVisibility(View.VISIBLE);
				mTvEndTime.setVisibility(View.VISIBLE);
				mTvStartTime.setText(DateUtil.getDateToString(mPasscode.getStartDate(), "yyyy-MM-dd HH:mm"));
				mTvEndTime.setText(DateUtil.getDateToString(mPasscode.getEndDate(), "yyyy-MM-dd HH:mm"));
				break;

			case 4:
				mLlPasscode.setEnabled(false);
				mLlName.setEnabled(true);
				mLlValidPeriod.setEnabled(false);
				mIvPasscodeMore.setVisibility(View.GONE);
				mIvValidPeriodMore.setVisibility(View.GONE);
				mTvValidPeriod.setVisibility(View.GONE);
				mTvStartTime.setVisibility(View.VISIBLE);
				mTvEndTime.setVisibility(View.VISIBLE);
				mTvStartTime.setText(DateUtil.getDateToString(mPasscode.getCreateDate(), "yyyy-MM-dd HH:mm"));
				mTvEndTime.setText(DateUtil.getDateToString(mPasscode.getCreateDate() + 3600 * 1000 * 24, "yyyy-MM-dd HH:mm"));
				mLlRecord.setVisibility(View.GONE);
				break;

			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
				mLlPasscode.setEnabled(false);
				mLlName.setEnabled(true);
				mLlValidPeriod.setEnabled(false);
				mIvPasscodeMore.setVisibility(View.GONE);
				mIvValidPeriodMore.setVisibility(View.GONE);
				mTvValidPeriod.setVisibility(View.VISIBLE);
				mTvStartTime.setVisibility(View.GONE);
				mTvEndTime.setVisibility(View.GONE);
				String cyclicMode = "";
				String cyclicTime = " " + DateUtil.getDateToString(mPasscode.getStartDate(), "HH:00") + "-"
						+ DateUtil.getDateToString(mPasscode.getEndDate(), "HH:00");
				switch (passcodeType) {
					case 5:
						cyclicMode = getString(R.string.weekend);
						break;

					case 6:
						cyclicMode = getString(R.string.daily);
						break;

					case 7:
						cyclicMode = getString(R.string.workday);
						break;

					case 8:
						cyclicMode = getString(R.string.monday);
						break;

					case 9:
						cyclicMode = getString(R.string.tuesday);
						break;

					case 10:
						cyclicMode = getString(R.string.wednesday);
						break;

					case 11:
						cyclicMode = getString(R.string.thursday);
						break;

					case 12:
						cyclicMode = getString(R.string.friday);
						break;

					case 13:
						cyclicMode = getString(R.string.saturday);
						break;

					case 14:
						cyclicMode = getString(R.string.sunday);
						break;

					default:
						break;
				}
				mTvValidPeriod.setText(cyclicMode + cyclicTime);
				break;

			default:
				break;
		}
	}

	private void initListener() {
		mTvSend.setOnClickListener(this);
		mLlPasscode.setOnClickListener(this);
		mLlName.setOnClickListener(this);
		mLlValidPeriod.setOnClickListener(this);
		mLlRecord.setOnClickListener(this);
		mTvDelete.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.page_action:
				showShare();
				break;

			case R.id.ll_passcode_detail_passcode:
				mModifyPasscodeType = 0;//修改密码
				showInputDialog();
				break;

			case R.id.ll_passcode_detail_name:
				mModifyPasscodeType = 1;//修改密码名称
				showInputDialog();
				break;

			case R.id.ll_passcode_detail_valid_period://修改有效期
				Intent intentNickname = new Intent(this, PasscodePeriodModifyActivity.class);
				intentNickname.putExtra(PasscodePeriodModifyActivity.KEY_PASSCODE_PWD, mPasscode.getKeyboardPwd());
				intentNickname.putExtra(PasscodePeriodModifyActivity.KEY_PASSCODE_ID, mPasscode.getId());
				intentNickname.putExtra(PasscodePeriodModifyActivity.KEY_PASSCODE_TYPE, mPasscode.getKeyboardPwdType());
				intentNickname.putExtra(PasscodePeriodModifyActivity.KEY_PASSCODE_START_TIME, mPasscode.getStartDate());
				intentNickname.putExtra(PasscodePeriodModifyActivity.KEY_PASSCODE_END_TIME, mPasscode.getEndDate());
				startActivityForResult(intentNickname, REQUEST_CODE_MODIFY_PASSCODE_PERIOD);
				break;

			case R.id.ll_passcode_detail_records:
				PasscodeRecordActivity.actionStart(PasscodeDetailActivity.this, mPasscode.getKeyboardPwd());
				break;

			case R.id.tv_passcode_detail_delete:
				DialogUtil.showCommonDialog(PasscodeDetailActivity.this, null,
					getString(R.string.note_confirm_delete), getString(R.string.delete),
					getString(R.string.cancel), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							//和锁通信，删除密码
							deletePasscode();
						}
					}, null);
				break;

			case R.id.btn_dialog_input_cancel:
				DIALOG.cancel();
				break;

			case R.id.btn_dialog_input_ok:
				String input = mEtDialogInput.getText().toString();
				if (mModifyPasscodeType == 0) {//修改密码
					if (StringUtil.isBlank(input)) {
						toast(R.string.enter_password);
					} else if (input.length() < 6) {
						toast(R.string.note_passcode_invalid);
					} else {
						if (isBleNetEnable()) {
							//和锁通信，修改密码
							modifyPasscode(input);
							DIALOG.cancel();
						}
					}
				} else if (mModifyPasscodeType == 1) {//修改密码名称
					if (!StringUtil.isBlank(input)) {
						modifyPasscodeAlias(input);
						DIALOG.cancel();
					} else {
						toast(R.string.enter_passcode_name);
					}
				}

				break;

			default:
				break;
		}
	}

	private void deletePasscode() {
		PeachLoader.showLoading(this);
		if (mTTLockAPI.isConnected(mKey.getLockMac())) {
			setDeletePasscodeCallback();
			mTTLockAPI.deleteOneKeyboardPassword(null, PeachPreference.getOpenid(),
					mKey.getLockVersion(), mKey.getAdminPwd(), mKey.getLockKey(), mKey.getLockFlagPos(),
					mPasscode.getKeyboardPwdType(), mPasscode.getKeyboardPwd(), mKey.getAesKeyStr());
		} else {
			MyApplication.bleSession.setLockmac(mKey.getLockMac());
			setDeletePasscodeCallback();
			mTTLockAPI.connect(mKey.getLockMac());
		}
	}

	private void setDeletePasscodeCallback() {
		MyApplication.bleSession.setOperation(Operation.DELETE_ONE_KEYBOARDPASSWORD);
		MyApplication.bleSession.setKeyboardPwdType(mPasscode.getKeyboardPwdType());
		MyApplication.bleSession.setKeyboardPwdOriginal(mPasscode.getKeyboardPwd());

		MyApplication.bleSession.setILockDeletePasscode(new ILockDeletePasscode() {
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						PeachLoader.stopLoading();
						requestDeletePasscode();
					}
				});
			}

			@Override
			public void onFail() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						PeachLoader.stopLoading();
						toast(R.string.operation_fail);
					}
				});
			}
		});
	}

	private void modifyPasscode(String newPwd) {
		PeachLoader.showLoading(this);
		if (mTTLockAPI.isConnected(mKey.getLockMac())) {
			setModifyPasscodeCallback(newPwd);
			mTTLockAPI.modifyKeyboardPassword(null, PeachPreference.getOpenid(),
					mKey.getLockVersion(), mKey.getAdminPwd(), mKey.getLockKey(), mKey.getLockFlagPos(),
					mPasscode.getKeyboardPwdType(), mPasscode.getKeyboardPwd(), newPwd, 0, 0,
					mKey.getAesKeyStr(), DateUtil.getTimeZoneOffset());
		} else {
			MyApplication.bleSession.setLockmac(mKey.getLockMac());
			setModifyPasscodeCallback(newPwd);
			mTTLockAPI.connect(mKey.getLockMac());
		}
	}

	private void setModifyPasscodeCallback(final String newPwd) {
		MyApplication.bleSession.setOperation(Operation.MODIFY_KEYBOARD_PASSWORD);
		MyApplication.bleSession.setKeyboardPwdType(mPasscode.getKeyboardPwdType());
		MyApplication.bleSession.setKeyboardPwdOriginal(mPasscode.getKeyboardPwd());
		MyApplication.bleSession.setKeyboardPwdNew(newPwd);
		MyApplication.bleSession.setStartDate(0);
		MyApplication.bleSession.setEndDate(0);

		MyApplication.bleSession.setILockModifyPasscode(new ILockModifyPasscode() {
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						PeachLoader.stopLoading();
						requestModifyPasscode(newPwd);
					}
				});
			}

			@Override
			public void onFail(final Error error) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						PeachLoader.stopLoading();
						if (error == Error.LOCK_PASSWORD_NOT_EXIST) {
							toast(R.string.note_unused_passcode_cannot_be_modified);
						} else {
							toast(R.string.operation_fail);
						}
					}
				});
			}
		});
	}

	/**
	 * 请求服务器，修改键盘密码
	 */
	private void requestModifyPasscode(final String newPwd) {
		RestClient.builder()
				.url(Urls.LOCK_PASSCODE_MODIFY)
				.loader(this)
				.params("userId", PeachPreference.readUserId())
				.params("lockId", mKey.getLockId())
				.params("keyboardPwdId", mPasscode.getId())
				.params("changeType", 1)
				.params("newKeyboardPwd", newPwd)
				.params("timeZone", DateUtil.getTimeZone())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_PASSCODE_MODIFY", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							mPasscode.setKeyboardPwd(newPwd);
							refreshUI();
							toast(R.string.operation_success);
						} else {
							toast(R.string.operation_fail);
						}
					}
				})
				.build()
				.post();
	}

	/**
	 * 修改密码别名
	 */
	private void modifyPasscodeAlias(final String passcodeAlias) {
		RestClient.builder()
				.url(Urls.LOCK_PASSCODE_ALIAS_MODIFY)
				.loader(this)
				.params("alias", passcodeAlias)
				.params("keyboardPwdId", mPasscode.getId())
				.params("userId", PeachPreference.readUserId())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_PASSCODE_ALIAS_MODIFY", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							toast(R.string.note_modify_name_success);
							mPasscode.setAlias(passcodeAlias);
							refreshUI();
						} else {
							toast(R.string.note_modify_name_fail);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.note_modify_name_fail);
					}
				})
				.error(new IError() {
					@Override
					public void onError(int code, String msg) {
						toast(R.string.note_modify_name_fail);
					}
				})
				.build()
				.post();
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
			if (mModifyPasscodeType == 0) {
				title.setText(R.string.modify_passcode);
				mEtDialogInput.setHint(R.string.passcode_format_6_9_digits);
				mEtDialogInput.setInputType(InputType.TYPE_CLASS_NUMBER);
				mEtDialogInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
				mEtDialogInput.setText(mPasscode.getKeyboardPwd());
				mEtDialogInput.setSelection(mPasscode.getKeyboardPwd().length());
			} else {
				title.setText(R.string.modify_passcode_name);
				mEtDialogInput.setHint(R.string.enter_passcode_name);
				mEtDialogInput.setInputType(InputType.TYPE_CLASS_TEXT);
				mEtDialogInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
				mEtDialogInput.setText(mPasscode.getAlias());
				mEtDialogInput.setSelection(mPasscode.getAlias().length());
			}
			cancel.setOnClickListener(this);
			ok.setOnClickListener(this);
		}
	}

	/**
	 * 请求服务器，删除键盘密码
	 */
	private void requestDeletePasscode() {
		RestClient.builder()
				.url(Urls.LOCK_PASSCODE_DELETE)
				.loader(PasscodeDetailActivity.this)
				.params("lockId", mKey.getLockId())
				.params("userId", PeachPreference.readUserId())
				.params("keyboardPwdId", mPasscode.getId())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_PASSCODE_DELETE", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							toast(R.string.passcode_delete_success);
							finish();
						} else {
							toast(R.string.passcode_delete_fail);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.passcode_delete_fail);
					}
				})
				.build()
				.post();
	}

	public void showShare() {
		OnekeyShare oks = new OnekeyShare();

		// 自定义分享平台
		oks.setCustomerLogo(BitmapFactory.decodeResource(getResources(), R.drawable.ic_share_zalo),
				"Zalo", new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						try {
							Intent vIt = new Intent(Intent.ACTION_SEND);
//							vIt.setPackage("com.facebook.orca");
							vIt.setType("text/plain");
							vIt.putExtra(Intent.EXTRA_TEXT, getShareContent());
							startActivity(vIt);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		//关闭sso授权
		oks.disableSSOWhenAuthorize();
		oks.setCallback(new PlatformActionListener() {
			@Override
			public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

			}

			@Override
			public void onError(Platform platform, int i, Throwable throwable) {

			}

			@Override
			public void onCancel(Platform platform, int i) {

			}
		});

		// title标题，微信、QQ和QQ空间等平台使用
		oks.setTitle(getString(R.string.app_name));
		// titleUrl QQ和QQ空间跳转链接
//		oks.setTitleUrl("http://sharesdk.cn");
//		oks.setAddress("13201812820");
		// text是分享文本，所有平台都需要这个字段
		oks.setText(getShareContent());
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//		oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url在微信、微博，Facebook等平台中使用
//		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网使用
//		oks.setComment("我是测试评论文本");
		// 启动分享GUI
		oks.show(this);
	}

	private String getShareContent() {
		String content = "";
		String type = "";
		switch (mPasscode.getKeyboardPwdType()) {
			case 1:
				type = getString(R.string.one_time);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) +
						DateUtil.getDateToString(mPasscode.getCreateDate(), "yyyy-MM-dd HH:mm") + getString(R.string.use_it_within_6_hours) + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.one_time) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 2:
				type = getString(R.string.permanent);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getCreateDate(), "yyyy-MM-dd HH:mm") + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.permanent) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_use_passcode_once_before) + DateUtil.getDateToString(DateUtil.getStringToDate(DateUtil.getDateToString(mPasscode.getCreateDate(), "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm") + 1000 * 3600 * 24, "yyyy-MM-dd HH:mm") + getString(R.string.no_key_bottom_right_dont_share_passcode);
				break;

			case 3:
				type = getString(R.string.period);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.period) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_use_passcode_once_before) + DateUtil.getDateToString(DateUtil.getStringToDate(mTvStartTime.getText().toString(), "yyyy-MM-dd HH:mm") + 1000 * 3600 * 24, "yyyy-MM-dd HH:mm") + getString(R.string.no_key_bottom_right_dont_share_passcode);
				break;

			case 4:
				type = getString(R.string.clear);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getCreateDate(), "yyyy-MM-dd HH:mm") + getString(R.string.use_it_within_24_hours) + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.clear) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 5:
				type = getString(R.string.weekend_cyclic);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.weekend_cyclic) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 6:
				type = getString(R.string.daily_cyclic);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.daily_cyclic) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 7:
				type = getString(R.string.workday_cyclic);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.workday_cyclic) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 8:
				type = getString(R.string.monday_cyclic);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.monday_cyclic) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 9:
				type = getString(R.string.tuesday_cyclic);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.tuesday_cyclic) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 10:
				type = getString(R.string.wednesday_cyclic);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.wednesday_cyclic) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 11:
				type = getString(R.string.thursday_cyclic);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.thursday_cyclic) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 12:
				type = getString(R.string.friday_cyclic);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.friday_cyclic) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 13:
				type = getString(R.string.saturday_cyclic);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.saturday_cyclic) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 14:
				type = getString(R.string.sunday_cyclic);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.sunday_cyclic) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			default:
				break;
		}

		return content;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_MODIFY_PASSCODE_PERIOD) {
			mPasscode.setKeyboardPwdType(3);
			long startTime = data.getLongExtra(PasscodePeriodModifyActivity.KEY_PASSCODE_START_TIME, mPasscode.getStartDate());
			long endTime = data.getLongExtra(PasscodePeriodModifyActivity.KEY_PASSCODE_END_TIME, mPasscode.getEndDate());
			mPasscode.setStartDate(startTime);
			mPasscode.setEndDate(endTime);
			refreshUI();
		}
	}
}
