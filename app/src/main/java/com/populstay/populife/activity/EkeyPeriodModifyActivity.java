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
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;

import java.util.Calendar;
import java.util.Date;

public class EkeyPeriodModifyActivity extends BaseActivity implements View.OnClickListener {

	public static final String KEY_KEY_ID = "key_key_id";
	public static final String KEY_KEY_TYPE = "key_key_type";
	public static final String KEY_START_TIME = "key_start_time";
	public static final String KEY_END_TIME = "key_end_time";

	private TextView mTvSave, mTvStartTime, mTvEndTime;

	private TimePickerView mTimePicker;
	private int mKeyId;
	private int mKeyType;//钥匙类型（1限时，2永久，3单次，4循环）
	private long mStartTime, mEndTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ekey_period_modify);

		getIntentData();
		initView();
		initListener();
	}

	private void getIntentData() {
		Intent data = getIntent();
		mKeyId = data.getIntExtra(KEY_KEY_ID, 1);
		mKeyType = data.getIntExtra(KEY_KEY_TYPE, 1);
		mStartTime = data.getLongExtra(KEY_START_TIME, 0);
		mEndTime = data.getLongExtra(KEY_END_TIME, 0);
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.modify_period);
		mTvSave = findViewById(R.id.page_action);
		mTvSave.setText(R.string.save);

		mTvStartTime = findViewById(R.id.tv_ekey_period_modify_start_time);
		mTvEndTime = findViewById(R.id.tv_ekey_period_modify_end_time);

		if (mKeyType == 2) {//永久钥匙
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
					case R.id.tv_ekey_period_modify_start_time:
						mStartTime = DateUtil.getStringToDate(time, "yyyy-MM-dd HH:mm");
						break;

					case R.id.tv_ekey_period_modify_end_time:
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
					modifyEkeyPeriod();
				} else {
					toast(R.string.note_time_start_greater_than_end);
				}
				break;

			case R.id.tv_ekey_period_modify_start_time:
			case R.id.tv_ekey_period_modify_end_time:
				mTimePicker.show(view);
				break;

			default:
				break;
		}
	}

	/**
	 * 修改钥匙有效期
	 */
	private void modifyEkeyPeriod() {
		RestClient.builder()
				.url(Urls.LOCK_EKEY_MODIFY_PERIOD)
				.loader(this)
				.params("userId", PeachPreference.readUserId())
				.params("keyId", mKeyId)
				.params("type", 1)
				.params("startDate", mTvStartTime.getText().toString())
				.params("endDate", mTvEndTime.getText().toString())
				.params("timeZone", DateUtil.getTimeZone())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_EKEY_MODIFY_PERIOD", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");

						if (code == 200) {
							Intent intent = new Intent();
							intent.putExtra(KEY_START_TIME, mStartTime);
							intent.putExtra(KEY_END_TIME, mEndTime);
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
