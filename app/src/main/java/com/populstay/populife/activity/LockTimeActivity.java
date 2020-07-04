package com.populstay.populife.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.app.MyApplication;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.entity.Key;
import com.populstay.populife.enumtype.Operation;
import com.populstay.populife.lock.ILockSetTime;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.storage.PeachPreference;

import static com.populstay.populife.app.MyApplication.mTTLockAPI;

public class LockTimeActivity extends BaseActivity {

	private static final String KEY_LOCK_TIME = "key_lock_time";

	private TextView mTvTime, mTvCalibrate;

	private Key mKey = MyApplication.CURRENT_KEY;
	private long mLockTime;

	/**
	 * @param lockTime 锁时间
	 */
	public static void actionStart(Context context, long lockTime) {
		Intent intent = new Intent(context, LockTimeActivity.class);
		intent.putExtra(KEY_LOCK_TIME, lockTime);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_time);

		getIntentData();
		initView();
		initListener();
	}

	private void getIntentData() {
		Intent data = getIntent();
		mLockTime = data.getLongExtra(KEY_LOCK_TIME, DateUtil.getCurTimeMillis());
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.lock_time);
		findViewById(R.id.page_action).setVisibility(View.GONE);

		mTvTime = findViewById(R.id.tv_lock_time);
		mTvCalibrate = findViewById(R.id.tv_lock_time_calibrate);
		mTvTime.setText(DateUtil.getDateToString(mLockTime, "yyyy-MM-dd HH:mm:ss"));
	}

	private void initListener() {
		mTvCalibrate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isBleEnable())
					calibrateLockTime();
			}
		});
	}

	private void calibrateLockTime() {
		showLoading();
		mLockTime = DateUtil.getCurTimeMillis();
		setSetTimeCallback();

		if (mTTLockAPI.isConnected(mKey.getLockMac())) {
			mTTLockAPI.setLockTime(null, PeachPreference.getOpenid(),
					mKey.getLockVersion(), mKey.getLockKey(), mLockTime,
					mKey.getLockFlagPos(), mKey.getAesKeyStr(), mKey.getTimezoneRawOffset());
		} else {
			mTTLockAPI.connect(mKey.getLockMac());
		}
	}

	private void setSetTimeCallback() {
		MyApplication.bleSession.setOperation(Operation.SET_LOCK_TIME);

		MyApplication.bleSession.setILockSetTime(new ILockSetTime() {
			@Override
			public void onSetTimeSuccess() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						stopLoading();
						toast(R.string.calibrate_time_success);
						mTvTime.setText(DateUtil.getDateToString(mLockTime, "yyyy-MM-dd HH:mm:ss"));
					}
				});
			}

			@Override
			public void onSetTimeFail() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						stopLoading();
						toastFail();
					}
				});
			}
		});
	}
}
