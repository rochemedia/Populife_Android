package com.populstay.populife.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
 * 绑定手机号或者邮箱
 * Created by Jerry
 */
public class AccountBindActivity extends BaseActivity
		implements View.OnClickListener, ITimerListener {

	public static final String KEY_BIND_RESULT = "key_bind_result";
	public static final String KEY_BIND_TYPE = "key_bind_type";

	private LinearLayout mLlCountry;
	private CountryCodePicker mCountryCodePicker;
	private EditText mEtUserName, mEtCode;
	private TextView mTvGetCode, mTvActionBtn;

	private BaseCountDownTimer mTimer = null;
	private int mBindType = Constant.ACCOUNT_TYPE_EMAIL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_bind);

		findViewById(R.id.page_action).setVisibility(View.GONE);

		getIntentData();
		initView();
		initListener();
	}

	private void getIntentData() {
		mBindType = getIntent().getIntExtra(KEY_BIND_TYPE, Constant.ACCOUNT_TYPE_EMAIL);
	}

	private void initView() {
		mLlCountry = findViewById(R.id.ll_account_bind_country);
		mCountryCodePicker = findViewById(R.id.cpp_account_bind);
		mEtUserName = findViewById(R.id.et_account_bind_user_name);
		mEtCode = findViewById(R.id.et_account_bind_code);
		mTvGetCode = findViewById(R.id.tv_account_bind_get_code);
		mTvActionBtn = findViewById(R.id.tv_account_bind_btn);

		switch (mBindType) {
			case Constant.ACCOUNT_TYPE_EMAIL:
				((TextView) findViewById(R.id.page_title)).setText(R.string.bind_email);

				mLlCountry.setVisibility(View.GONE);
//				mEtUserName.setCompoundDrawablesWithIntrinsicBounds(
//						getResources().getDrawable(R.drawable.ic_login_email), null, null, null);
				mEtUserName.setHint(getString(R.string.enter_email));
				mEtUserName.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

				break;

			case Constant.ACCOUNT_TYPE_PHONE:
				((TextView) findViewById(R.id.page_title)).setText(R.string.bind_phone);

				mLlCountry.setVisibility(View.VISIBLE);
//				mEtUserName.setCompoundDrawablesWithIntrinsicBounds(
//						getResources().getDrawable(R.drawable.ic_login_phone), null, null, null);
				mEtUserName.setHint(getString(R.string.enter_phone_num));
				mEtUserName.setInputType(InputType.TYPE_CLASS_PHONE);

				break;

			default:
				break;
		}

		setCountryInfo();
	}

	/**
	 * 设置国家信息（国家简称 + 国家码）
	 */
	private void setCountryInfo() {

		requestRuntimePermissions(new String[]{Manifest.permission.READ_PHONE_STATE,
				Manifest.permission.ACCESS_COARSE_LOCATION}, new PermissionListener() {
			@Override
			public void onGranted() {
				// 获取当前国家的国家码
				String countryNameCode = LanguageUtil.getCountryNameCode(AccountBindActivity.this);
				mCountryCodePicker.setCountryForNameCode(countryNameCode);
			}

			@Override
			public void onDenied(List<String> deniedPermissions) {
				toast(R.string.note_permission);
			}
		});
	}

	private void initListener() {
		mTvGetCode.setOnClickListener(this);
		mTvActionBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.tv_account_bind_get_code:
				if (checkCodeForm())
					// 获取验证码
					getVerificationCode();
				break;

			case R.id.tv_account_bind_btn:
				if (checkForm())
					bindAccount();
				break;

			default:
				break;
		}
	}

	/**
	 * 绑定手机号/邮箱时，获取验证码
	 */
	private void getVerificationCode() {
		WeakHashMap<String, Object> params = new WeakHashMap<>();
		params.put("type", mBindType);
		params.put("username", mBindType == Constant.ACCOUNT_TYPE_PHONE ? mCountryCodePicker.getSelectedCountryCodeWithPlus()
				+ mEtUserName.getText().toString() : mEtUserName.getText().toString());
		if (mBindType == Constant.ACCOUNT_TYPE_PHONE) { // 绑定手机时，需传入国家编码（如：+86）
			params.put("country", mCountryCodePicker.getSelectedCountryCodeWithPlus());
		}
		RestClient.builder()
				.url(Urls.VERIFICATION_CODE_REGISTER_OR_BIND)
				.params(params)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("GET_VIRIFICATION_CODE_BIND_ACCOUNT", response);
						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						switch (code) {
							case 200:
								// 获取验证码成功，开始倒计时
								mTimer = new BaseCountDownTimer(60, AccountBindActivity.this);
								mTimer.start();
								if (mBindType == Constant.ACCOUNT_TYPE_PHONE) {
									toast(R.string.note_success_get_verification_code_phone);
								} else if (mBindType == Constant.ACCOUNT_TYPE_EMAIL) {
									toast(R.string.note_success_get_verification_code_email);
								}
								break;

							case 951:
								toast(getString(R.string.note_phone_has_been_used));
								break;

							case 952:
								toast(getString(R.string.note_email_has_been_used));
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
				.error(new IError() {
					@Override
					public void onError(int code, String msg) {
						toast(R.string.note_get_verification_code_fail);
					}
				})
				.build()
				.post();
	}

	/**
	 * 绑定手机号或邮箱
	 */
	private void bindAccount() {
		String url = "";
		String paramKey = "";
		String paramValue = "";
		if (mBindType == Constant.ACCOUNT_TYPE_EMAIL) {
			url = Urls.ACCOUNT_BIND_EMAIL;
			paramKey = "email";
			paramValue = mEtUserName.getText().toString();
		} else if (mBindType == Constant.ACCOUNT_TYPE_PHONE) {
			url = Urls.ACCOUNT_BIND_PHONE;
			paramKey = "phone";
			paramValue = mCountryCodePicker.getSelectedCountryCodeWithPlus() + mEtUserName.getText().toString();
		}
		final String bindResult = paramValue;
		RestClient.builder()
				.url(url)
				.loader(this)
				.params(paramKey, paramValue)
				.params("code", mEtCode.getText().toString())
				.params("userId", PeachPreference.readUserId())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("ACCOUNT_BIND", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						switch (code) {
							case 200:
								Intent intent = new Intent();
								intent.putExtra(KEY_BIND_RESULT, bindResult);
								setResult(RESULT_OK, intent);
								finish();
								break;

							case 951:
								if (mBindType == Constant.ACCOUNT_TYPE_EMAIL) {
									toast(getString(R.string.note_email_has_been_used));
								} else if (mBindType == Constant.ACCOUNT_TYPE_PHONE) {
									toast(getString(R.string.note_phone_has_been_used));
								}
								break;

							case 953:
								toast(R.string.note_verifiction_code_invalid);
								break;

							default:
								toast(getString(R.string.note_account_bind_fail));
								break;
						}


					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(getString(R.string.note_account_bind_fail));
					}
				})
				.build()
				.post();
	}

	/**
	 * 获取验证码时，检查手机号或邮箱
	 */
	private boolean checkCodeForm() {
		final String userName = mEtUserName.getText().toString();

		boolean isPass = true;

		switch (mBindType) {
			case Constant.ACCOUNT_TYPE_EMAIL:
				if (userName.isEmpty() || !StringUtil.isEmail(userName)) {
					toast(R.string.note_email_invalid);
					isPass = false;
				}
				break;

			case Constant.ACCOUNT_TYPE_PHONE:
				if (userName.isEmpty()) {
					toast(R.string.note_phone_invalid);
					isPass = false;
				}
				break;

			default:
				break;
		}
		return isPass;
	}

	/**
	 * 绑定时，检查手机号/邮箱和验证码
	 */
	private boolean checkForm() {
		final String userName = mEtUserName.getText().toString();
		final String code = mEtCode.getText().toString();

		boolean isPass = true;

		switch (mBindType) {
			case Constant.ACCOUNT_TYPE_EMAIL:
				if (userName.isEmpty() || !StringUtil.isEmail(userName)) {
					toast(R.string.note_email_invalid);
					isPass = false;
				} else if (code.isEmpty()) {
					toast(R.string.note_verifiction_code_invalid);
					isPass = false;
				}
				break;

			case Constant.ACCOUNT_TYPE_PHONE:
				if (userName.isEmpty()) {
					toast(R.string.note_phone_invalid);
					isPass = false;
				} else if (code.isEmpty()) {
					toast(R.string.note_verifiction_code_invalid);
					isPass = false;
				}
				break;

			default:
				break;
		}
		return isPass;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
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
}
