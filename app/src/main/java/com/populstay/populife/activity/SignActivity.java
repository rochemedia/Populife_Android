package com.populstay.populife.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.constant.Constant;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.permission.PermissionListener;
import com.populstay.populife.sign.ISignListener;
import com.populstay.populife.sign.SignHandler;
import com.populstay.populife.util.activity.ActivityCollector;
import com.populstay.populife.util.device.DeviceUtil;
import com.populstay.populife.util.locale.LanguageUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.string.StringUtil;
import com.populstay.populife.util.timer.BaseCountDownTimer;
import com.populstay.populife.util.timer.ITimerListener;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.text.MessageFormat;
import java.util.List;
import java.util.WeakHashMap;

/**
 * 登录、注册、找回密码
 * Created by Jerry
 */
public class SignActivity extends BaseActivity implements View.OnClickListener, ISignListener, ITimerListener {

	public static final String VAL_ACCOUNT_SIGN_IN = "val_account_sign_in";
	public static final String VAL_ACCOUNT_SIGN_UP = "val_account_sign_up";
	public static final String VAL_ACCOUNT_RESET_PWD = "val_account_reset_pwd";
	public static final String KEY_ACCOUNT_SIGN_ACTION_TYPE = "key_account_sign_action_type";

	private RelativeLayout mRlBack;
	private TextView mTvPageTitle, mTvPageAction, mTvPhone, mTvEmail, mTvPhoneTab, mTvEmailTab,
			mTvGetCode, mTvActionBtn, mTvForgetPwd, mTvUserTerms;
	private LinearLayout mLlCountry, mLlVerification;
	private CountryCodePicker mCountryCodePicker;
	private EditText mEtUserName, mEtPwd, mEtCode;

	private BaseCountDownTimer mTimer = null;
	private ISignListener mISignListener = this;
	private String mAccountActionType = VAL_ACCOUNT_SIGN_IN;
	private int mSignType = Constant.ACCOUNT_TYPE_EMAIL;

	/**
	 * 启动当前 activity
	 *
	 * @param context           上下文
	 * @param accountActionType 账号操作类型
	 */
	public static void actionStart(Context context, String accountActionType) {
		Intent intent = new Intent(context, SignActivity.class);
		intent.putExtra(KEY_ACCOUNT_SIGN_ACTION_TYPE, accountActionType);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign);

		mAccountActionType = getIntent().getStringExtra(KEY_ACCOUNT_SIGN_ACTION_TYPE);

		initView();
		initListener();
	}

	private void initView() {
		mRlBack = findViewById(R.id.page_back);
		mTvPageTitle = findViewById(R.id.page_title);
		mTvPageAction = findViewById(R.id.page_action);
		mTvPhone = findViewById(R.id.tv_sign_phone);
		mTvEmail = findViewById(R.id.tv_sign_email);
		mTvPhoneTab = findViewById(R.id.tv_sign_phone_tab);
		mTvEmailTab = findViewById(R.id.tv_sign_email_tab);
		mTvGetCode = findViewById(R.id.tv_sign_get_code);
		mTvActionBtn = findViewById(R.id.tv_sign_action_btn);
		mTvForgetPwd = findViewById(R.id.tv_forget_pwd);
		mTvUserTerms = findViewById(R.id.tv_sign_user_terms);
		mLlCountry = findViewById(R.id.ll_sign_country);
		mLlVerification = findViewById(R.id.ll_sign_verification_code);
		mCountryCodePicker = findViewById(R.id.cpp_login);
		mEtUserName = findViewById(R.id.et_sign_user_name);
		mEtPwd = findViewById(R.id.et_sign_pwd);
		mEtCode = findViewById(R.id.et_sign_verification_code);

		initUI(mAccountActionType);

		setCountryInfo();
	}

	/**
	 * 设置国家信息（国家简称 + 国家码）
	 */
	private void setCountryInfo() {

		requestRuntimePermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
				Manifest.permission.READ_PHONE_STATE}, new PermissionListener() {
			@Override
			public void onGranted() {
				// 获取当前国家的国家码
				String countryNameCode = LanguageUtil.getCountryNameCode(SignActivity.this);
				mCountryCodePicker.setCountryForNameCode(countryNameCode);
			}

			@Override
			public void onDenied(List<String> deniedPermissions) {
				toast(R.string.note_permission);
			}
		});
	}

	/**
	 * 初始化界面 UI
	 *
	 * @param accountActionType 账号操作类型
	 *                          VAL_ACCOUNT_SIGN_IN		登录
	 *                          VAL_ACCOUNT_SIGN_UP		注册
	 *                          VAL_ACCOUNT_RESET_PWD	重置密码
	 */
	private void initUI(String accountActionType) {
		switch (accountActionType) {
			case VAL_ACCOUNT_SIGN_IN:
				mRlBack.setVisibility(View.GONE);
				mTvPageTitle.setText(R.string.sign_in);
				mTvPageAction.setText(R.string.sign_up);
				mTvActionBtn.setText(R.string.sign_in);
				mLlVerification.setVisibility(View.GONE);
				mTvUserTerms.setVisibility(View.GONE);
				break;

			case VAL_ACCOUNT_SIGN_UP:
				mTvPageTitle.setText(R.string.sign_up);
				mTvPageAction.setVisibility(View.GONE);
				mTvActionBtn.setText(R.string.sign_up);
				mTvForgetPwd.setVisibility(View.GONE);
				break;

			case VAL_ACCOUNT_RESET_PWD:
				mTvPageTitle.setText(R.string.reset_pwd);
				mTvPageAction.setVisibility(View.GONE);
				mTvActionBtn.setText(R.string.reset_pwd);
				mTvForgetPwd.setVisibility(View.GONE);
				mTvUserTerms.setVisibility(View.GONE);
				break;

			default:
				break;
		}
	}

	private void initListener() {
		mTvPageAction.setOnClickListener(this);
		mTvPhone.setOnClickListener(this);
		mTvEmail.setOnClickListener(this);
		mTvGetCode.setOnClickListener(this);
		mTvActionBtn.setOnClickListener(this);
		mTvForgetPwd.setOnClickListener(this);
		mTvUserTerms.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.page_action:
				actionStart(SignActivity.this, VAL_ACCOUNT_SIGN_UP);
				break;

			case R.id.tv_sign_phone:
				changeSignType(Constant.ACCOUNT_TYPE_PHONE);
				break;

			case R.id.tv_sign_email:
				changeSignType(Constant.ACCOUNT_TYPE_EMAIL);
				break;

			case R.id.tv_sign_get_code:
				if (checkCodeForm()) {
					// 获取验证码
					getVerificationCode();
				}
				break;

			case R.id.tv_sign_action_btn:
				if (checkForm()) {
					switch (mAccountActionType) {
						case VAL_ACCOUNT_SIGN_IN:
							// 登录
							signIn();
							break;

						case VAL_ACCOUNT_SIGN_UP:
							// 注册
							signUp();
							break;

						case VAL_ACCOUNT_RESET_PWD:
							// 重置密码
							resetPwd();
							break;

						default:
							break;
					}
				}
				break;

			case R.id.tv_forget_pwd:
				actionStart(SignActivity.this, VAL_ACCOUNT_RESET_PWD);
				break;

			case R.id.tv_sign_user_terms:
				goToNewActivity(PrivacyPolicyActivity.class);
				break;

			default:
				break;
		}
	}

	/**
	 * 获取验证码
	 */
	private void getVerificationCode() {
		if (mAccountActionType.equals(VAL_ACCOUNT_SIGN_UP)) {
			// 注册时，获取验证码
			WeakHashMap<String, Object> params = new WeakHashMap<>();
			params.put("type", mSignType);
			params.put("username", mSignType == Constant.ACCOUNT_TYPE_PHONE ? mCountryCodePicker.getSelectedCountryCodeWithPlus()
					+ mEtUserName.getText().toString() : mEtUserName.getText().toString());
			if (mSignType == Constant.ACCOUNT_TYPE_PHONE) { // 使用手机注册时，需传入国家编码（如：+86）
				params.put("country", mCountryCodePicker.getSelectedCountryCodeWithPlus());
			}
			RestClient.builder()
					.url(Urls.VERIFICATION_CODE_REGISTER_OR_BIND)
					.params(params)
					.success(new ISuccess() {
						@Override
						public void onSuccess(String response) {
							PeachLogger.d("GET_VIRIFICATION_CODE_REGISTER", response);
							JSONObject result = JSON.parseObject(response);
							int code = result.getInteger("code");
							switch (code) {
								case 200:
									// 获取验证码成功，开始倒计时
									startTimer();


									if (mSignType == Constant.ACCOUNT_TYPE_PHONE) {
										toast(R.string.note_success_get_verification_code_phone);
									} else if (mSignType == Constant.ACCOUNT_TYPE_EMAIL) {
										toast(R.string.note_success_get_verification_code_email);
									}

									break;

								case 951:
									toast(R.string.note_phone_has_been_registered);
									break;

								case 952:
									toast(R.string.note_email_has_been_registered);
									break;

								default:
									toast(R.string.note_get_verification_code_fail);
									break;
							}
						}
					})
					.failure(new IFailure() {
						@Override
						public void onFailure() {
							toast(R.string.note_get_verification_code_fail);
						}
					})
					.build()
					.post();
		} else if (mAccountActionType.equals(VAL_ACCOUNT_RESET_PWD)) {
			//（忘记密码后）重置密码时，获取验证码
			WeakHashMap<String, Object> params = new WeakHashMap<>();
			params.put("username", mSignType == Constant.ACCOUNT_TYPE_PHONE ? mCountryCodePicker.getSelectedCountryCodeWithPlus()
					+ mEtUserName.getText().toString() : mEtUserName.getText().toString());
			if (mSignType == Constant.ACCOUNT_TYPE_PHONE) { // 使用手机找回密码时，需传入国家编码（如：+86）
				params.put("country", mCountryCodePicker.getSelectedCountryCodeWithPlus());
			}
			RestClient.builder()
					.url(Urls.VERIFICATION_CODE_RESETPWD_DELETEACCOUNT_NEWDEVICELOGIN)
					.params(params)
					.success(new ISuccess() {
						@Override
						public void onSuccess(String response) {
							PeachLogger.d("GET_VIRIFICATION_CODE_RESET_PWD", response);
							JSONObject result = JSON.parseObject(response);
							int code = result.getInteger("code");
							switch (code) {
								case 200:
									// 获取验证码成功，开始倒计时
									startTimer();

									if (mSignType == Constant.ACCOUNT_TYPE_PHONE) {
										toast(R.string.note_success_get_verification_code_phone);
									} else if (mSignType == Constant.ACCOUNT_TYPE_EMAIL) {
										toast(R.string.note_success_get_verification_code_email);
									}

									break;

								case 920:
									if (mSignType == Constant.ACCOUNT_TYPE_PHONE) {
										toast(R.string.note_phone_has_not_been_registered);
									} else if (mSignType == Constant.ACCOUNT_TYPE_EMAIL) {
										toast(R.string.note_email_has_not_been_registered);
									}
									break;

								default:
									toast(R.string.note_get_verification_code_fail);
									break;
							}
						}
					})
					.failure(new IFailure() {
						@Override
						public void onFailure() {
							toast(R.string.note_get_verification_code_fail);
						}
					})
					.build()
					.post();
		}
	}

	/**
	 * 开始倒计时
	 */
	private void startTimer() {
		mTimer = new BaseCountDownTimer(60, SignActivity.this);
		mTimer.start();
	}

	/**
	 * 停止倒计时
	 */
	private void stopTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	/**
	 * 登录
	 */
	private void signIn() {
		final String countryCode = mCountryCodePicker.getSelectedCountryCodeWithPlus();
		final String userName = mSignType == Constant.ACCOUNT_TYPE_PHONE ? countryCode + mEtUserName.getText().toString()
				: mEtUserName.getText().toString();
		final String loginPwd = mEtPwd.getText().toString();
		RestClient.builder()
				.url(Urls.SIGN_IN)
				.loader(SignActivity.this)
				.params("username", userName)
				.params("password", loginPwd)
				.params("deviceId", DeviceUtil.getDeviceId(SignActivity.this))
				.success(new ISuccess() {
					@Override
					public void onSuccess(final String response) {
						PeachLogger.d("SIGN_IN", response);
						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						switch (code) {
							case 200:
								JSONObject accountInfo = result.getJSONObject("data");
								//处理异地登录
								if ("test@populife.co".equals(userName) || "+8613201812820".equals(userName)) { // 测试账号，不检测异地登录，直接登录进入主页
									PeachPreference.putStr(PeachPreference.ACCOUNT_PWD, loginPwd);
									SignHandler.onSignIn(response, mISignListener);
								} else {
									if (accountInfo.containsKey("phone") || accountInfo.containsKey("email")) {// 新设备登录，跳转到验证码验证页面
										LoginVerifyActivity.actionStart(SignActivity.this, mSignType, countryCode, userName, response, loginPwd);
									} else {//没有异地登录（依旧在同一设备登录）
										PeachPreference.putStr(PeachPreference.ACCOUNT_PWD, loginPwd);
										SignHandler.onSignIn(response, mISignListener);
									}
								}
								break;

							case 910:
								if (mSignType == Constant.ACCOUNT_TYPE_PHONE) {
									toast(R.string.note_phone_or_pwd_incorrect);
								} else if (mSignType == Constant.ACCOUNT_TYPE_EMAIL) {
									toast(R.string.note_email_or_pwd_incorrect);
								}
								break;

							default:
								toast(R.string.note_sign_in_fail);
								break;
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.note_sign_in_fail);
					}
				})
				.build()
				.post();
	}

	/**
	 * 注册
	 */
	private void signUp() {
		RestClient.builder()
				.url(Urls.SIGN_UP)
				.loader(SignActivity.this)
				.params("username", mSignType == Constant.ACCOUNT_TYPE_PHONE ? mCountryCodePicker.getSelectedCountryCodeWithPlus()
						+ mEtUserName.getText().toString() : mEtUserName.getText().toString())
				.params("password", mEtPwd.getText().toString())
				.params("code", mEtCode.getText().toString())
				.params("type", mSignType)
				.params("deviceId", DeviceUtil.getDeviceId(SignActivity.this))
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("SIGN_UP", response);
						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						switch (code) {
							case 200:
								PeachPreference.putStr(PeachPreference.ACCOUNT_PWD, mEtPwd.getText().toString());
								SignHandler.onSignUp(response, mISignListener);
								break;

							case 951:
								toast(R.string.note_phone_has_been_registered);
								break;

							case 952:
								toast(R.string.note_email_has_been_registered);
								break;

							case 953:
								toast(R.string.note_verifiction_code_invalid);
								break;

							default:
								toast(R.string.note_register_fail);
								break;
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.note_register_fail);
					}
				})
				.build()
				.post();
	}

	/**
	 * 重置密码
	 */
	private void resetPwd() {
		RestClient.builder()
				.url(Urls.ACCOUNT_PWD_RESET)
				.loader(SignActivity.this)
				.params("username", mSignType == Constant.ACCOUNT_TYPE_PHONE ? mCountryCodePicker.getSelectedCountryCodeWithPlus()
						+ mEtUserName.getText().toString() : mEtUserName.getText().toString())
				.params("password", mEtPwd.getText().toString())
				.params("code", mEtCode.getText().toString())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("ACCOUNT_PWD_RESET", response);
						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							toast(R.string.note_reset_pwd_success);
							finish();
						} else if (code == 954 || code == 953) {
							toast(R.string.note_verifiction_code_invalid);
						} else {
							toast(R.string.note_reset_pwd_fail);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.note_reset_pwd_fail);
					}
				})
				.error(new IError() {
					@Override
					public void onError(int code, String msg) {
						toast(R.string.note_reset_pwd_fail);
					}
				})
				.build()
				.post();
	}

	/**
	 * 切换登录/注册方式
	 *
	 * @param signType 登录/注册方式
	 *                 Constant.ACCOUNT_TYPE_PHONE 手机登录/注册
	 *                 Constant.ACCOUNT_TYPE_EMAIL 邮箱登录/注册
	 */
	public void changeSignType(int signType) {
		if (mSignType != signType) {
			mSignType = signType;
			switch (signType) {
				case Constant.ACCOUNT_TYPE_PHONE:
					mTvPhoneTab.setBackgroundColor(getResources().getColor(R.color.white));
					mTvEmailTab.setBackgroundColor(getResources().getColor(R.color.transparent));

					mEtUserName.setHint(getString(R.string.enter_phone_num));
					mEtUserName.setInputType(InputType.TYPE_CLASS_NUMBER);

					mLlCountry.setVisibility(View.VISIBLE);
					break;

				case Constant.ACCOUNT_TYPE_EMAIL:
					mTvPhoneTab.setBackgroundColor(getResources().getColor(R.color.transparent));
					mTvEmailTab.setBackgroundColor(getResources().getColor(R.color.white));

					mEtUserName.setHint(getString(R.string.enter_email));
					mEtUserName.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

					mLlCountry.setVisibility(View.GONE);
					break;

				default:
					break;
			}
			mEtUserName.setText("");
			mEtPwd.setText("");
			mEtCode.setText("");

			stopTimer();
			mTvGetCode.setEnabled(true);
			mTvGetCode.setText(getString(R.string.get_code));
		}
	}

	/**
	 * 获取验证码时，检查手机号或邮箱
	 */
	private boolean checkCodeForm() {
		final String userName = mEtUserName.getText().toString().trim();

		boolean isPass = true;

		switch (mSignType) {
			case Constant.ACCOUNT_TYPE_PHONE:
				if (userName.isEmpty()) {
					toast(R.string.note_phone_invalid);
					isPass = false;
				}
				break;

			case Constant.ACCOUNT_TYPE_EMAIL:
				if (userName.isEmpty() || !StringUtil.isEmail(userName)) {
					toast(R.string.note_email_invalid);
					isPass = false;
				}
				break;

			default:
				break;
		}
		return isPass;
	}

	private boolean checkForm() {
		final String userName = mEtUserName.getText().toString().trim();
		final String pwd = mEtPwd.getText().toString().trim();
		final String code = mEtCode.getText().toString().trim();

		boolean isPass = true;

		switch (mAccountActionType) {
			case VAL_ACCOUNT_SIGN_IN:
				switch (mSignType) {
					case Constant.ACCOUNT_TYPE_PHONE:
						if (userName.isEmpty()) {
							toast(R.string.note_phone_invalid);
							isPass = false;
						} else if (pwd.isEmpty() || pwd.length() < 6) {
							toast(R.string.note_pwd_format);
							isPass = false;
						}
						break;

					case Constant.ACCOUNT_TYPE_EMAIL:
						if (userName.isEmpty() || !StringUtil.isEmail(userName)) {
							toast(R.string.note_email_invalid);
							isPass = false;
						} else if (pwd.isEmpty() || pwd.length() < 6) {
							toast(R.string.note_pwd_format);
							isPass = false;
						}
						break;

					default:
						break;
				}
				break;

			case VAL_ACCOUNT_SIGN_UP:
			case VAL_ACCOUNT_RESET_PWD:
				switch (mSignType) {
					case Constant.ACCOUNT_TYPE_PHONE:
						if (userName.isEmpty()) {
							toast(R.string.note_phone_invalid);
							isPass = false;
						} else if (pwd.isEmpty() || pwd.length() < 6) {
							toast(R.string.note_pwd_format);
							isPass = false;
						} else if (code.isEmpty()) {
							toast(R.string.note_verifiction_code_invalid);
							isPass = false;
						}
						break;

					case Constant.ACCOUNT_TYPE_EMAIL:
						if (userName.isEmpty() || !StringUtil.isEmail(userName)) {
							toast(R.string.note_email_invalid);
							isPass = false;
						} else if (pwd.isEmpty() || pwd.length() < 6) {
							toast(R.string.note_pwd_format);
							isPass = false;
						} else if (code.isEmpty()) {
							toast(R.string.note_verifiction_code_invalid);
							isPass = false;
						}
						break;

					default:
						break;
				}
				break;

			default:
				break;
		}
		return isPass;
	}

	@Override
	public void onSignInSuccess() {
		goToNewActivity(MainActivity.class);
		ActivityCollector.finishAll();
	}

	@Override
	public void onSignUpSuccess() {
		// 注册成功后，自动登录
		signIn();
	}

	@Override
	public void onSignOutSuccess() {
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopTimer();
	}

	@Override
	public void onTimerTick(final long secondsLeft) {
		mTvGetCode.setEnabled(false);
		mTvGetCode.setText(MessageFormat.format("{0} s", secondsLeft));
	}

	@Override
	public void onTimerFinish() {
		mTvGetCode.setEnabled(true);
		mTvGetCode.setText(getString(R.string.get_code));
	}

	@Override
	protected void queryLatestDeviceId() {

	}
}
