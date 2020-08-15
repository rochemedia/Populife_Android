package com.populstay.populife.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.populstay.populife.R;
import com.populstay.populife.app.MyApplication;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.Key;
import com.populstay.populife.enumtype.Operation;
import com.populstay.populife.lock.ILockModifyPasscode;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.ui.loader.PeachLoader;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.ttlock.bl.sdk.entity.Error;

import java.util.Calendar;
import java.util.Date;

import static com.populstay.populife.app.MyApplication.mTTLockAPI;

public class PasscodePeriodModifyActivity extends BaseActivity implements View.OnClickListener {

	public static final String KEY_PASSCODE_PWD = "key_passode";
	public static final String KEY_PASSCODE_ID = "key_passode_id";
	public static final String KEY_PASSCODE_TYPE = "key_passcode_type";
	public static final String KEY_PASSCODE_START_TIME = "key_passcode_start_time";
	public static final String KEY_PASSCODE_END_TIME = "key_passcode_end_time";

	private TextView mTvSave, mTvStartTime, mTvEndTime;

	private Key mKey = MyApplication.CURRENT_KEY;
	private TimePickerView mTimePicker;
	private String mPasscodePwd;
	private int mPasscodeId, mPasscodeType;
	private long mStartTime, mEndTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_passcode_period_modify);

		getIntentData();
		initView();
		initListener();
	}

	private void getIntentData() {
		Intent data = getIntent();
		mPasscodePwd = data.getStringExtra(KEY_PASSCODE_PWD);
		mPasscodeId = data.getIntExtra(KEY_PASSCODE_ID, 0);
		mPasscodeType = data.getIntExtra(KEY_PASSCODE_TYPE, 0);
		mStartTime = data.getLongExtra(KEY_PASSCODE_START_TIME, 0);
		mEndTime = data.getLongExtra(KEY_PASSCODE_END_TIME, 0);
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.modify_period);
		mTvSave = findViewById(R.id.page_action);
		mTvSave.setText(R.string.save);

		mTvStartTime = findViewById(R.id.tv_passcode_period_modify_start_time);
		mTvEndTime = findViewById(R.id.tv_passcode_period_modify_end_time);

		if (mPasscodeType == 2) {//永久密码
			long now = System.currentTimeMillis();
			mStartTime = now;
			mEndTime = now + 3600 * 1000;
		}
		mTvStartTime.setText(DateUtil.getDateToString(mStartTime, "yyyy-MM-dd HH:mm"));
		mTvEndTime.setText(DateUtil.getDateToString(mEndTime, "yyyy-MM-dd HH:mm"));
		initTimePicker();
	}

	private void initTimePicker() {
		Calendar selectedDate = Calendar.getInstance();
		selectedDate.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH),
				selectedDate.get(Calendar.DAY_OF_MONTH), selectedDate.get(Calendar.HOUR_OF_DAY),
				selectedDate.get(Calendar.MINUTE));

		mTimePicker = new TimePickerBuilder(this, new OnTimeSelectListener() {
			@Override
			public void onTimeSelect(Date date, View v) {
				String time = DateUtil.getDateToString(date, "yyyy-MM-dd HH:mm");
				((TextView) v).setText(time);
				switch (v.getId()) {
					case R.id.tv_passcode_period_modify_start_time:
						mStartTime = DateUtil.getStringToDate(time, "yyyy-MM-dd HH:mm");
						break;

					case R.id.tv_passcode_period_modify_end_time:
						mEndTime = DateUtil.getStringToDate(time, "yyyy-MM-dd HH:mm");
						break;

					default:
						break;
				}
			}
		})
				.setType(new boolean[]{true, true, true, true, true, false})
				.setLabel(getString(R.string.unit_year), getString(R.string.unit_month), getString(R.string.unit_day),
						getString(R.string.unit_hour), getString(R.string.unit_minute), getString(R.string.unit_second))
				.setSubmitText(getResources().getString(R.string.ok))
				.setCancelText(getResources().getString(R.string.cancel))
				.setDate(selectedDate)
				.setRangDate(selectedDate, null)
				.build();
	}

	private void initListener() {
		mTvSave.setOnClickListener(this);
		mTvStartTime.setOnClickListener(this);
		mTvEndTime.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.page_action:
				if (mStartTime < mEndTime) {
					if (isBleNetEnable()) {
						//和锁通信，修改密码期限
						modifyPasscodePeriod();
					}
				} else {
					toast(R.string.note_time_start_greater_than_end);
				}
				break;

			case R.id.tv_passcode_period_modify_start_time:
			case R.id.tv_passcode_period_modify_end_time:
				mTimePicker.show(view);
				break;

			default:
				break;
		}
	}

	private void modifyPasscodePeriod() {
		PeachLoader.showLoading(this);
		if (mTTLockAPI.isConnected(mKey.getLockMac())) {
			setModifyPasscodeCallback();
			mTTLockAPI.modifyKeyboardPassword(null, PeachPreference.getOpenid(),
					mKey.getLockVersion(), mKey.getAdminPwd(), mKey.getLockKey(), mKey.getLockFlagPos(),
					3, mPasscodePwd, "", mStartTime, mEndTime,
					mKey.getAesKeyStr(), DateUtil.getTimeZoneOffset());
		} else {
			MyApplication.bleSession.setLockmac(mKey.getLockMac());
			setModifyPasscodeCallback();
			mTTLockAPI.connect(mKey.getLockMac());
		}
	}

	private void setModifyPasscodeCallback() {
		MyApplication.bleSession.setOperation(Operation.MODIFY_KEYBOARD_PASSWORD);
		MyApplication.bleSession.setKeyboardPwdType(3);//永久密码（2）修改期限后，变成限时密码（3）
		MyApplication.bleSession.setKeyboardPwdOriginal(mPasscodePwd);
		MyApplication.bleSession.setKeyboardPwdNew("");
		MyApplication.bleSession.setStartDate(mStartTime);
		MyApplication.bleSession.setEndDate(mEndTime);

		MyApplication.bleSession.setILockModifyPasscode(new ILockModifyPasscode() {
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						PeachLoader.stopLoading();
						requestModifyPasscodePeriod();
					}
				});
			}

			@Override
			public void onFail(final Error error) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						PeachLoader.stopLoading();
						if (error == Error.LOCK_PASSWORD_NOT_EXIST) {
							toast(R.string.note_unused_passcode_cannot_be_modified);
						} else {
							toast(R.string.operation_fail);
						}
					}
				});
			}
		});
	}

	/**
	 * 请求服务器，修改密码有效期
	 */
	private void requestModifyPasscodePeriod() {
		RestClient.builder()
				.url(Urls.LOCK_PASSCODE_MODIFY)
				.loader(this)
				.params("userId", PeachPreference.readUserId())
				.params("lockId", mKey.getLockId())
				.params("keyboardPwdId", mPasscodeId)
				.params("changeType", 1)
				.params("startDate", mTvStartTime.getText().toString())
				.params("endDate", mTvEndTime.getText().toString())
				.params("timeZone", DateUtil.getTimeZone())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_PASSCODE_MODIFY", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");

						if (code == 200) {
							Intent intent = new Intent();
							intent.putExtra(KEY_PASSCODE_START_TIME, mStartTime);
							intent.putExtra(KEY_PASSCODE_END_TIME, mEndTime);
							setResult(RESULT_OK, intent);
							toast(R.string.note_modify_period_success);
							finish();
						} else {
							toast(R.string.note_modify_period_fail);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.note_modify_period_fail);
					}
				})
				.build()
				.post();
	}
}
