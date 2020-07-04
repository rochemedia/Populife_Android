package com.populstay.populife.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;

public class LockAddSelectTypeActivity extends BaseActivity implements View.OnClickListener {

	private LinearLayout mLlDeadbolt, mLlKeybox, mLlMortise;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_add_select_type);

		initView();
		initListener();
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.select_lock);
		findViewById(R.id.page_action).setVisibility(View.GONE);

		mLlDeadbolt = findViewById(R.id.ll_lock_type_deadbolt);
		mLlKeybox = findViewById(R.id.ll_lock_type_keybox);
		mLlMortise = findViewById(R.id.ll_lock_type_mortise);
	}

	private void initListener() {
		mLlDeadbolt.setOnClickListener(this);
		mLlKeybox.setOnClickListener(this);
		mLlMortise.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ll_lock_type_deadbolt:
				if (isBleNetEnable()) {
					LockAddGuideActivity.actionStart(LockAddSelectTypeActivity.this, 0);
				}
				break;

			case R.id.ll_lock_type_keybox:
				if (isBleNetEnable()) {
					goToNewActivity(LockAddGuideKeyboxOpenActivity.class);
				}
				break;

			case R.id.ll_lock_type_mortise:
				if (isBleNetEnable()) {
					goToNewActivity(FoundDeviceActivity.class);
				}
				break;

			default:
				break;
		}
	}
}
