package com.populstay.populife.activity;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.permission.PermissionListener;

import java.util.List;

public class GatewayAddGuideActivity extends BaseActivity implements View.OnClickListener {

	private TextView mTvQuestion, mTvNext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gateway_add_guide);

		initView();
		initListener();
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.gateway_add);
		mTvQuestion = findViewById(R.id.page_action);
		mTvQuestion.setText("");
		mTvQuestion.setCompoundDrawablesWithIntrinsicBounds(
				getResources().getDrawable(R.drawable.ic_question_mark), null, null, null);

		mTvNext = findViewById(R.id.tv_gateway_add_guide_next);
	}

	private void initListener() {
		mTvQuestion.setOnClickListener(this);
		mTvNext.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.page_action:
				PDFActivity.actionStart(GatewayAddGuideActivity.this, getString(R.string.user_manual_gateway),
						"user_manual_gateway.pdf", true);
				break;

			case R.id.tv_gateway_add_guide_next:
				if (isBleNetEnable()) {
					requestRuntimePermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
							new PermissionListener() {
								@Override
								public void onGranted() {
									//开启蓝牙扫描
									goToNewActivity(GatewayAddActivity.class);
								}

								@Override
								public void onDenied(List<String> deniedPermissions) {
									toast(R.string.note_permission_scan_locks);
								}
							});
				}
				break;

			default:
				break;
		}
	}
}