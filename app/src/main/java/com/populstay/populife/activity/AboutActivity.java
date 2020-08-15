package com.populstay.populife.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.populstay.populife.BuildConfig;
import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;

public class AboutActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		((TextView) findViewById(R.id.page_title)).setText(R.string.about);
		findViewById(R.id.page_action).setVisibility(View.GONE);

		((TextView) findViewById(R.id.tv_app_version)).setText(String.format(getString(R.string.version_no), BuildConfig.VERSION_NAME));
	}

	public void viewUserTermsPrivacyPolicy(View view) {
		goToNewActivity(PrivacyPolicyActivity.class);
	}
}
