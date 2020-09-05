package com.populstay.populife.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.meiqia.core.MQManager;
import com.meiqia.core.bean.MQMessage;
import com.meiqia.core.callback.OnGetMessageListCallback;
import com.meiqia.meiqiasdk.imageloader.MQImage;
import com.meiqia.meiqiasdk.util.MQIntentBuilder;
import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.base.BluetoothBaseActivity;
import com.populstay.populife.permission.PermissionListener;
import com.populstay.populife.ui.MQGlideImageLoader;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.timer.BaseCountDownTimer;
import com.populstay.populife.util.timer.ITimerListener;

import java.util.HashMap;
import java.util.List;

public class GatewayAddGuideActivity extends BluetoothBaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

	private TextView mTvPageTitle, mTvShowCountDownTime, mTvNext;
	private BaseCountDownTimer mCountDownTimer;
	private CheckBox mCbConfirmActivateDevice;
	private CheckBox mCbConfirmGatewayReconnect;
	public static final int COUNT_DOWN_MILLIS = 50;
	private ImageView mIvNewMsg;


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

		findViewById(R.id.page_action).setVisibility(View.GONE);
		mIvNewMsg = findViewById(R.id.iv_main_lock_msg_new);
		View tvSupport = findViewById(R.id.rl_main_lock_online_service);
		tvSupport.setVisibility(View.VISIBLE);
		tvSupport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				requestRuntimePermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						new PermissionListener() {
							@Override
							public void onGranted() {
								HashMap<String, String> clientInfo = new HashMap<>();
								clientInfo.put("userId", PeachPreference.readUserId());
								clientInfo.put("phoneNum", PeachPreference.getStr(PeachPreference.ACCOUNT_PHONE));
								clientInfo.put("email", PeachPreference.getStr(PeachPreference.ACCOUNT_EMAIL));
								MQImage.setImageLoader(new MQGlideImageLoader());
								startActivity(new MQIntentBuilder(GatewayAddGuideActivity.this).
										setCustomizedId(PeachPreference.readUserId())
										.setClientInfo(clientInfo)
										.updateClientInfo(clientInfo)
										.build());
							}

							@Override
							public void onDenied(List<String> deniedPermissions) {
								toast(R.string.note_permission_avatar);
							}
						});

			}
		});
	}

	/**
	 * 获取美洽未读消息
	 */
	private void getMeiQiaUnreadMsg() {
		MQManager.getInstance(this).getUnreadMessages(new OnGetMessageListCallback() {
			@Override
			public void onSuccess(List<MQMessage> messageList) {
				PeachLogger.d(messageList);
				if (messageList != null && !messageList.isEmpty())
					mIvNewMsg.setVisibility(View.VISIBLE);
				else
					mIvNewMsg.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onFailure(int code, String message) {
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		getMeiQiaUnreadMsg();
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
		setCountDownTimeText(COUNT_DOWN_MILLIS);
		mCbConfirmActivateDevice.setChecked(true);
		mCbConfirmGatewayReconnect.setChecked(true);
		setNextBtnEnable();
	}

	public void resetRefreshCountDownTimerUI(){
		setCountDownTimeText(0);
		setNextBtnEnable();
	}

	private void setNextBtnEnable(){
		boolean enable = false;
		enable = mCbConfirmActivateDevice.isChecked() && mCbConfirmGatewayReconnect.isChecked();
		enable = enable && isBleEnableNotHint() && isLbsEnableNotHint();
		mTvNext.setEnabled(enable);
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

	@Override
	public void onBluetoothStateChanged(boolean isOpen) {
		setNextBtnEnable();
	}

	@Override
	public void onLocationStateChanged(boolean isOpen) {
		setNextBtnEnable();
	}

}