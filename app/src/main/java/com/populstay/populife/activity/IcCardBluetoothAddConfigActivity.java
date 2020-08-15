package com.populstay.populife.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.string.StringUtil;

import java.util.Calendar;
import java.util.Date;

public class IcCardBluetoothAddConfigActivity extends BaseActivity implements View.OnClickListener {

	private EditText mEtName;
	private Switch mSwitch;
	private TextView mTvStartTime, mTvEndTime, mTvNext;
	private LinearLayout mLlTime;
	//时间选择器
	private TimePickerView mTimePicker;

	private Date mStartTime;
	private Date mEndTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ic_card_bluetooth_add_config);

		initView();
		initListener();
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.ic_card_add);
		findViewById(R.id.page_action).setVisibility(View.GONE);

		mEtName = findViewById(R.id.et_ic_card_bluetooth_config_name);
		mSwitch = findViewById(R.id.switch_ic_card_bluetooth_config);
		mTvStartTime = findViewById(R.id.tv_ic_card_bluetooth_config_start_time);
		mTvEndTime = findViewById(R.id.tv_ic_card_bluetooth_config_end_time);
		mTvNext = findViewById(R.id.tv_ic_card_bluetooth_config_next);
		mLlTime = findViewById(R.id.ll_ic_card_bluetooth_config_time);

		initTimePicker();
	}

	private void initTimePicker() {
		//获取当前时间
		mStartTime = mEndTime = new Date();

		mTvStartTime.setText(DateUtil.getDateToString(mStartTime, "yyyy-MM-dd HH:mm"));
		mTvEndTime.setText(DateUtil.getDateToString(mEndTime, "yyyy-MM-dd HH:mm"));

		Calendar selectedDate = Calendar.getInstance();
		selectedDate.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH),
				selectedDate.get(Calendar.DAY_OF_MONTH), selectedDate.get(Calendar.HOUR_OF_DAY),
				selectedDate.get(Calendar.MINUTE));

		mTimePicker = new TimePickerBuilder(this, new OnTimeSelectListener() {
			@Override
			public void onTimeSelect(Date date, View v) {
				((TextView) v).setText(DateUtil.getDateToString(date, "yyyy-MM-dd HH:mm"));
				switch (v.getId()) {
					case R.id.tv_ic_card_bluetooth_config_start_time:
						mStartTime = date;
						break;

					case R.id.tv_ic_card_bluetooth_config_end_time:
						mEndTime = date;
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
		mSwitch.setOnClickListener(this);
		mTvStartTime.setOnClickListener(this);
		mTvEndTime.setOnClickListener(this);
		mTvNext.setOnClickListener(this);
		mEtName.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				mTvNext.setEnabled(!StringUtil.isBlank(editable.toString().trim()));
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.switch_ic_card_bluetooth_config:
				mLlTime.setVisibility(mSwitch.isChecked() ? View.GONE : View.VISIBLE);
				break;

			case R.id.tv_ic_card_bluetooth_config_start_time:
			case R.id.tv_ic_card_bluetooth_config_end_time:
				mTimePicker.show(view);
				break;

			case R.id.tv_ic_card_bluetooth_config_next:
				String cardName = mEtName.getText().toString().trim();
				if (!mSwitch.isChecked() && !mStartTime.before(mEndTime)) {
					toast(R.string.note_time_start_greater_than_end);
				} else {
					if (mSwitch.isChecked()) {
						IcCardBluetoothAddActivity.actionStart(IcCardBluetoothAddConfigActivity.this,
								IcCardBluetoothAddActivity.VAL_IC_CARD_TYPE_PERMANENT, cardName,
								"", "");
					} else {
						IcCardBluetoothAddActivity.actionStart(IcCardBluetoothAddConfigActivity.this,
								IcCardBluetoothAddActivity.VAL_IC_CARD_TYPE_PERIOD, cardName,
								mTvStartTime.getText().toString(), mTvEndTime.getText().toString());
					}
				}
				break;

			default:
				break;
		}
	}
}
