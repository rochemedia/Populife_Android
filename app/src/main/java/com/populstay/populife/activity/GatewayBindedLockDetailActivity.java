package com.populstay.populife.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.dialog.DialogUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.ttlock.bl.sdk.util.DigitUtil;

public class GatewayBindedLockDetailActivity extends BaseActivity implements View.OnClickListener {

	private static final String KEY_GATEWAY_LOCK_ID = "key_gateway_lock_id";
	private static final String KEY_GATEWAY_LOCK_ALIAS = "key_gateway_lock_alias";
	private static final String KEY_GATEWAY_LOCK_SPECIAL_VALUE = "key_gateway_lock_special_value";

	private TextView mTvTimeTitle, mTvTime, mTvTimeRead, mTvTimeCalibrate, mTvUnlock, mTvFreeze, mTvUnfreeze;
	private int mLockId, mLockSpecialValue;
	private String mLockAlias;

	/**
	 * 启动当前 activity
	 */
	public static void actionStart(Context context, int lockId, String lockAlias, int specialValue) {
		Intent intent = new Intent(context, GatewayBindedLockDetailActivity.class);
		intent.putExtra(KEY_GATEWAY_LOCK_ID, lockId);
		intent.putExtra(KEY_GATEWAY_LOCK_ALIAS, lockAlias);
		intent.putExtra(KEY_GATEWAY_LOCK_SPECIAL_VALUE, specialValue);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gateway_binded_lock_detail);

		getIntentData();
		initView();
		initListener();
		readLockTime();
	}

	private void getIntentData() {
		Intent data = getIntent();
		mLockId = data.getIntExtra(KEY_GATEWAY_LOCK_ID, 0);
		mLockSpecialValue = data.getIntExtra(KEY_GATEWAY_LOCK_SPECIAL_VALUE, 0);
		mLockAlias = data.getStringExtra(KEY_GATEWAY_LOCK_ALIAS);
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(mLockAlias);
		findViewById(R.id.page_action).setVisibility(View.GONE);

		mTvTimeTitle = findViewById(R.id.tv_gateway_binded_lock_time_title);
		mTvTime = findViewById(R.id.tv_gateway_binded_lock_time);
		mTvTimeRead = findViewById(R.id.tv_gateway_binded_lock_time_read);
		mTvTimeCalibrate = findViewById(R.id.tv_gateway_binded_lock_time_calibrate);
		mTvUnlock = findViewById(R.id.tv_gateway_binded_lock_remote_unlock);
		mTvFreeze = findViewById(R.id.tv_gateway_binded_lock_freeze);
		mTvUnfreeze = findViewById(R.id.tv_gateway_binded_lock_unfreeze);

		checkLockFeature();
	}

	private void checkLockFeature() {
		if (DigitUtil.isSupportRemoteUnlock(mLockSpecialValue)) {
			mTvUnlock.setVisibility(View.VISIBLE);
		} else {
			mTvUnlock.setVisibility(View.GONE);
		}
	}

	private void initListener() {
		mTvTimeRead.setOnClickListener(this);
		mTvTimeCalibrate.setOnClickListener(this);
		mTvUnlock.setOnClickListener(this);
		mTvFreeze.setOnClickListener(this);
		mTvUnfreeze.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.tv_gateway_binded_lock_time_read:
				readLockTime();
				break;

			case R.id.tv_gateway_binded_lock_time_calibrate:
				calibrateLockTime();
				break;

			case R.id.tv_gateway_binded_lock_remote_unlock:
				Resources res = getResources();
				DialogUtil.showCommonDialog(GatewayBindedLockDetailActivity.this, null,
						res.getString(R.string.note_unlock_remotely),
						res.getString(R.string.unlock), res.getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								remoteUnlock();
							}
						}, null);
				break;

			case R.id.tv_gateway_binded_lock_freeze:
				freezeLock();
				break;

			case R.id.tv_gateway_binded_lock_unfreeze:
				unfreezeLock();
				break;


			default:
				break;
		}
	}

	/**
	 * 读取锁时间
	 */
	private void readLockTime() {
		mTvTimeTitle.setText(R.string.reading_lock_time);
		RestClient.builder()
				.url(Urls.GATEWAY_LOCK_TIME_READ)
				.loader(this)
				.params("lockId", mLockId)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("GATEWAY_LOCK_TIME_READ", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							JSONObject dateInfo = result.getJSONObject("data");
							if (dateInfo != null) {
								long lockTime = dateInfo.getLong("date");
								String lockDate = DateUtil.getDateToString(lockTime, "yyyy-MM-dd HH:mm:ss");
								mTvTimeTitle.setText(R.string.lock_time);
								mTvTime.setText(lockDate);
							}
						} else if (code == 920) {
							mTvTimeTitle.setText(R.string.read_lock_time_fail);
							toast(R.string.note_lock_not_found);
						} else {
							mTvTimeTitle.setText(R.string.read_lock_time_fail);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						mTvTimeTitle.setText(R.string.read_lock_time_fail);
					}
				})
				.error(new IError() {
					@Override
					public void onError(int code, String msg) {
						mTvTimeTitle.setText(R.string.read_lock_time_fail);
					}
				})
				.build()
				.post();
	}

	/**
	 * 校准锁时间
	 */
	private void calibrateLockTime() {
		mTvTimeTitle.setText(R.string.calibrating_lock_time);
		RestClient.builder()
				.url(Urls.GATEWAY_LOCK_TIME_CALIBRATE)
				.loader(this)
				.params("lockId", mLockId)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("GATEWAY_LOCK_TIME_CALIBRATE", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							mTvTimeTitle.setText(R.string.lock_time);
							toast(R.string.operation_success);
						} else if (code == 920) {
							mTvTimeTitle.setText(R.string.calibrate_lock_time_fail);
							toast(R.string.note_lock_not_found);
						} else {
							mTvTimeTitle.setText(R.string.calibrate_lock_time_fail);
							toast(R.string.operation_fail);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						mTvTimeTitle.setText(R.string.calibrate_lock_time_fail);
					}
				})
				.error(new IError() {
					@Override
					public void onError(int code, String msg) {
						mTvTimeTitle.setText(R.string.calibrate_lock_time_fail);
					}
				})
				.build()
				.post();
	}

	/**
	 * 远程开锁
	 */
	private void remoteUnlock() {
		RestClient.builder()
				.url(Urls.GATEWAY_REMOTE_UNLOCK)
				.loader(this)
				.params("userId", PeachPreference.readUserId())
				.params("lockId", mLockId)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("GATEWAY_REMOTE_UNLOCK", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							toast(R.string.operation_success);
						} else if (code == 951) {
							toast(R.string.note_gateway_donot_exists);
						} else {
							toast(R.string.operation_fail);
						}
					}
				})
				.build()
				.post();
	}

	/**
	 * 冻结锁
	 */
	private void freezeLock() {
		RestClient.builder()
				.url(Urls.GATEWAY_LOCK_FREEZE)
				.loader(this)
				.params("userId", PeachPreference.readUserId())
				.params("lockId", mLockId)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("GATEWAY_LOCK_FREEZE", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							toast(R.string.operation_success);
						} else {
							toast(R.string.operation_fail);
						}
					}
				})
				.build()
				.post();
	}

	/**
	 * 解冻锁
	 */
	private void unfreezeLock() {
		RestClient.builder()
				.url(Urls.GATEWAY_LOCK_UNFREEZE)
				.loader(this)
				.params("userId", PeachPreference.readUserId())
				.params("lockId", mLockId)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("GATEWAY_LOCK_UNFREEZE", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							toast(R.string.operation_success);
						} else {
							toast(R.string.operation_fail);
						}
					}
				})
				.build()
				.post();
	}
}
