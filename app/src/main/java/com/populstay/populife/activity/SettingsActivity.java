package com.populstay.populife.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.util.device.FingerprintUtil;
import com.populstay.populife.util.storage.PeachPreference;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {

	private LinearLayout mLlLockUser, mLlSwitchLanguage, mLlAbout, mLlTouchIdLogin;
	private Switch mSwitchTouchIdLogin, mSwitchReminder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		initView();
		initListener();
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.settings);
		findViewById(R.id.page_action).setVisibility(View.GONE);

		mLlLockUser = findViewById(R.id.ll_settings_lock_user);
		mLlSwitchLanguage = findViewById(R.id.ll_settings_switch_language);
		mLlAbout = findViewById(R.id.ll_settings_about);

		mLlTouchIdLogin = findViewById(R.id.ll_touch_id_login);

		mSwitchTouchIdLogin = findViewById(R.id.switch_touch_id_login);
		mLlTouchIdLogin.setVisibility(FingerprintUtil.isSupportFingerprint(this) ? View.VISIBLE : View.GONE);
		findViewById(R.id.ll_touch_id_login_line).setVisibility(FingerprintUtil.isSupportFingerprint(this) ? View.VISIBLE : View.GONE);
		mSwitchTouchIdLogin.setChecked(PeachPreference.isTouchIdLogin());

		mSwitchReminder = findViewById(R.id.switch_lock_settings_reminder);
		mSwitchReminder.setChecked(PeachPreference.isShowLockingReminder(PeachPreference.readUserId()));
	}

	private void initListener() {
		mLlLockUser.setOnClickListener(this);
		mLlSwitchLanguage.setOnClickListener(this);
		mLlAbout.setOnClickListener(this);
		mSwitchTouchIdLogin.setOnClickListener(this);
		mSwitchReminder.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.switch_touch_id_login:
				PeachPreference.setTouchIdLogin(mSwitchTouchIdLogin.isChecked());
				break;

			case R.id.switch_lock_settings_reminder:
				PeachPreference.setShowLockingReminder(PeachPreference.readUserId(), mSwitchReminder.isChecked());
				break;

			case R.id.ll_settings_switch_language:
				goToNewActivity(ChangeLanguageActivity.class);
				break;
			case R.id.ll_settings_about:
				goToNewActivity(AboutActivity.class);
				break;

			default:
				break;
		}
	}
}
