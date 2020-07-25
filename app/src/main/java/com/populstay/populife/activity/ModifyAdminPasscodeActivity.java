package com.populstay.populife.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.app.MyApplication;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.Key;
import com.populstay.populife.enumtype.Operation;
import com.populstay.populife.lock.ILockSetAdminKeyboardPwd;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.ui.widget.exedittext.ExEditText;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.string.StringUtil;

import static com.populstay.populife.app.MyApplication.mTTLockAPI;

public class ModifyAdminPasscodeActivity extends BaseActivity {

	public static final String KEY_PASSCODE = "key_content";

	private ExEditText mEtInput;
	private TextView mTvSave;

	private String mContent;
	private Key mKey = MyApplication.CURRENT_KEY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_admin_passcode);

		getIntentData();
		initView();
	}

	private void getIntentData() {
		mContent = getIntent().getStringExtra(KEY_PASSCODE);
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.admin_modify_passcode);
		findViewById(R.id.page_action).setVisibility(View.GONE);

		mTvSave = findViewById(R.id.tv_save);
		mEtInput = findViewById(R.id.et_modify_passcode);
		mEtInput.setText(mContent);
		mEtInput.setSelection(mContent.length());
		setEnableSave();

		mEtInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				setEnableSave();
			}
		});


		mTvSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String input = mEtInput.getText().toString();
				if (StringUtil.isBlank(input)) {
					toast(R.string.input_something);
				} else if (input.equals(mContent)) {
					toast(R.string.note_nothing_changed);
				} else if (!StringUtil.isNum(input) || input.length() < 6 || input.length() > 9) {
					toast(R.string.note_passcode_invalid);
				} else {
					if (isBleNetEnable()) {
						showLoading();
						if (mTTLockAPI.isConnected(mKey.getLockMac())) {
							setCallback(input);
							mTTLockAPI.setAdminKeyboardPassword(null, PeachPreference.getOpenid(),
									mKey.getLockVersion(), mKey.getAdminPwd(), mKey.getLockKey(),
									mKey.getLockFlagPos(), mKey.getAesKeyStr(), input);
						} else {//connect the lock
							setCallback(input);
							mTTLockAPI.connect(mKey.getLockMac());
						}
					}
				}
			}
		});
	}

	public void setEnableSave() {
		mTvSave.setEnabled(null != mEtInput && !TextUtils.isEmpty(mEtInput.getTextStr()));
	}

	private void setCallback(final String input) {
		MyApplication.bleSession.setOperation(Operation.SET_ADMIN_KEYBOARD_PASSWORD);
		MyApplication.bleSession.setPassword(input);
		MyApplication.bleSession.setLockmac(mKey.getLockMac());

		MyApplication.bleSession.setILockSetAdminKeyboardPwd(new ILockSetAdminKeyboardPwd() {
			@Override
			public void onSetPwdSuccess() {
				stopLoading();
				setAdminKeyboardPwd(input);
			}

			@Override
			public void onSetPwdFail() {
				stopLoading();
				toast(R.string.note_modify_admin_passcode_fail);
			}
		});
	}


	/**
	 * 设置锁的管理员密码
	 *
	 * @param keyboardPwd
	 */
	private void setAdminKeyboardPwd(final String keyboardPwd) {
		RestClient.builder()
				.url(Urls.LOCK_ADMIN_KEYBOARD_PWD_MODIFY)
				.params("password", keyboardPwd)
				.params("lockId", mKey.getLockId())
				.params("userId", PeachPreference.readUserId())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_ADMIN_KEYBOARD_PWD_MODIFY", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							Intent intent = new Intent();
							intent.putExtra(LockSettingsActivity.KEY_RESULT_DATA, keyboardPwd);
							setResult(RESULT_OK, intent);
							finish();
						} else {
							toast(R.string.note_modify_admin_passcode_fail);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.note_modify_admin_passcode_fail);
					}
				})
				.build()
				.post();
	}
}
