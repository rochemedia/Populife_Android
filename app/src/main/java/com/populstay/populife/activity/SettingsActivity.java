package com.populstay.populife.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {

	private LinearLayout mLlLockUser, mLlLockGroup, mLlAbout;

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
		mLlLockGroup = findViewById(R.id.ll_settings_lock_group);
		mLlAbout = findViewById(R.id.ll_settings_about);
	}

	private void initListener() {
		mLlLockUser.setOnClickListener(this);
		mLlLockGroup.setOnClickListener(this);
		mLlAbout.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ll_settings_lock_user:
				goToNewActivity(LockUserListActivity.class);
				break;

			case R.id.ll_settings_lock_group:
				goToNewActivity(LockGroupListActivity.class);
				break;

			case R.id.ll_settings_about:
				goToNewActivity(AboutActivity.class);
				break;

			default:
				break;
		}
	}
}
