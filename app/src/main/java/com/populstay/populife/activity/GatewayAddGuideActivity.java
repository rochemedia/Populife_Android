package com.populstay.populife.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.permission.PermissionListener;
import com.populstay.populife.util.timer.BaseCountDownTimer;
import com.populstay.populife.util.timer.ITimerListener;

import java.util.List;

public class GatewayAddGuideActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

	private TextView mTvPageTitle, mTvShowCountDownTime, mTvNext;
	private BaseCountDownTimer mCountDownTimer;
	private CheckBox mCbConfirmActivateDevice;
	private CheckBox mCbConfirmGatewayReconnect;
	public static final int COUNT_DOWN_MILLIS = 50;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gateway_add_guide);
		initCountDownTimer();
		initView();
		setListener();
		initStatus();
	}

	private void initCountDownTimer() {
		mCountDownTimer = new BaseCountDownTimer(COUNT_DOWN_MILLIS, new ITimerListener() {
			@Override
			public void onTimerTick(long secondsLeft) {
				setCountDownTimeText((int) (secondsLeft + 1));
			}

			@Override
			public void onTimerFinish() {
				resetRefreshCountDownTimerUI();
				mCbConfirmActivateDevice.setChecked(false);
				mCbConfirmGatewayReconnect.setChecked(false);
			}
		});
	}

	private void initView() {
		mTvPageTitle = findViewById(R.id.page_title);
		mTvShowCountDownTime = findViewById(R.id.tv_show_count_down_time);
		mTvNext = findViewById(R.id.tv_next);
		mCbConfirmActivateDevice = findViewById(R.id.cb_confirm_activate_device);
		mCbConfirmGatewayReconnect = findViewById(R.id.cb_confirm_gateway_reconnect);

		initTitleBarRightBtn();
	}

	private void initTitleBarRightBtn() {
		/*TextView tvQuestion = findViewById(R.id.page_action);
		tvQuestion.setText("");
		tvQuestion.setCompoundDrawablesWithIntrinsicBounds(
				getResources().getDrawable(R.drawable.help_icon), null, null, null);

		tvQuestion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PDFActivity.actionStart(GatewayAddGuideActivity.this, getString(R.string.user_manual_gateway),
						"user_manual_gateway.pdf", true);
			}
		});*/

		TextView tvSupport = findViewById(R.id.page_action);
		tvSupport.setText("");
		tvSupport.setCompoundDrawablesWithIntrinsicBounds(
				getResources().getDrawable(R.drawable.support_icon), null, null, null);

		tvSupport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}

	private void setListener() {
		mTvNext.setOnClickListener(this);
		mCbConfirmActivateDevice.setOnCheckedChangeListener(this);
		mCbConfirmGatewayReconnect.setOnCheckedChangeListener(this);
	}

	private void initStatus() {
		mTvPageTitle.setText(R.string.add_gateway_title);
		mCountDownTimer.start();
		startRefreshCountDownTimerUI();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.tv_next:
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
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (mCbConfirmActivateDevice.isChecked() && mCbConfirmGatewayReconnect.isChecked()){
			mCountDownTimer.start();
			startRefreshCountDownTimerUI();
		}else {
			mCountDownTimer.cancel();
			resetRefreshCountDownTimerUI();
		}
	}

	public void startRefreshCountDownTimerUI(){
		mTvNext.setEnabled(true);
		setCountDownTimeText(COUNT_DOWN_MILLIS);
		mCbConfirmActivateDevice.setChecked(true);
		mCbConfirmGatewayReconnect.setChecked(true);
	}

	public void resetRefreshCountDownTimerUI(){
		mTvNext.setEnabled(false);
		setCountDownTimeText(0);
	}

	public void setCountDownTimeText(int time){
		String timeStr = time > 9 ? String.valueOf(time) : "0" + time;
		if(null != mTvShowCountDownTime){
			mTvShowCountDownTime.setText(timeStr);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (null != mCountDownTimer){
			mCountDownTimer.cancel();
			mCountDownTimer = null;
		}
	}

}