package com.populstay.populife.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.populstay.populife.R;
import com.populstay.populife.app.MyApplication;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.entity.Key;
import com.populstay.populife.enumtype.Operation;
import com.populstay.populife.lock.ILockModifyAutoLockTime;
import com.populstay.populife.util.storage.PeachPreference;

import java.util.ArrayList;
import java.util.List;

import static com.populstay.populife.app.MyApplication.mTTLockAPI;

public class LockAutoLockingActivity extends BaseActivity implements View.OnClickListener {

	private static final String KEY_AUTO_LOCK_TIME = "key_auto_lock_time";

	private TextView mTvSave, mTvSeconds, mTvSetTime;
	private Switch mSwitch;
	private LinearLayout mLlTime;

	private Key mKey = MyApplication.CURRENT_KEY;
	private OptionsPickerView mOptionsPicker;
	private int mSeconds;

	/**
	 * @param autoLockTime 自动闭锁时间
	 */
	public static void actionStart(Context context, int autoLockTime) {
		Intent intent = new Intent(context, LockAutoLockingActivity.class);
		intent.putExtra(KEY_AUTO_LOCK_TIME, autoLockTime);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_auto_locking);

		getIntentData();
		initView();
		initListener();
	}

	private void getIntentData() {
		Intent data = getIntent();
		mSeconds = data.getIntExtra(KEY_AUTO_LOCK_TIME, 0);
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.automatic_locking);
		mTvSave = findViewById(R.id.page_action);
		mTvSave.setText(R.string.save);

		mTvSeconds = findViewById(R.id.tv_lock_auto_lock_time);
		mTvSetTime = findViewById(R.id.tv_lock_auto_lock_set_time);
		mSwitch = findViewById(R.id.switch_lock_auto_lock);
		mLlTime = findViewById(R.id.ll_lock_auto_lock_time);
		mLlTime.setVisibility(View.GONE);

		initUI();
		initCustomOptionPicker();
	}

	@SuppressLint("SetTextI18n")
	private void initUI() {
		if (mSeconds == 0) {
			mSeconds = 5;
			mSwitch.setChecked(false);
			mLlTime.setVisibility(View.GONE);
		} else {
			mSwitch.setChecked(true);
			mLlTime.setVisibility(View.VISIBLE);
		}
		mTvSeconds.setText(mSeconds + getString(R.string.seconds));
	}

	private void initCustomOptionPicker() {
		final List<String> timeList = new ArrayList<>();
		int i = 5;
		while (i <= 120) {
			timeList.add(i + "s");
			i++;
		}
		/**
		 * @description
		 *
		 * 注意事项：
		 * 自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针。
		 * 具体可参考demo 里面的两个自定义layout布局。
		 */
		mOptionsPicker = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
			@SuppressLint("SetTextI18n")
			@Override
			public void onOptionsSelect(int options1, int option2, int options3, View v) {
				//返回的分别是三个级别的选中位置
				String time = timeList.get(options1);
				mSeconds = Integer.valueOf(time.substring(0, time.length() - 1));
				mTvSeconds.setText(mSeconds + getString(R.string.seconds));
			}
		})
				.setLayoutRes(R.layout.pickerview_custom_auto_lock, new CustomListener() {
					@Override
					public void customLayout(View v) {
						final TextView tvSubmit = v.findViewById(R.id.tv_finish);
						TextView tvCancel = v.findViewById(R.id.iv_cancel);
						tvSubmit.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								mOptionsPicker.returnData();
								mOptionsPicker.dismiss();
							}
						});

						tvCancel.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								mOptionsPicker.dismiss();
							}
						});
					}
				})
				.build();

		mOptionsPicker.setPicker(timeList);//添加数据
	}

	private void initListener() {
		mSwitch.setOnClickListener(this);
		mTvSave.setOnClickListener(this);
		mTvSetTime.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.switch_lock_auto_lock:
				mLlTime.setVisibility(mSwitch.isChecked() ? View.VISIBLE : View.GONE);
				break;

			case R.id.tv_lock_auto_lock_set_time:
				mOptionsPicker.show();
				break;

			case R.id.page_action:
				if (isBleEnable())
					modifyAutoLockTime();
				break;

			default:
				break;
		}
	}

	private void modifyAutoLockTime() {
		showLoading();
		setModifyAutoLockTimeCallback();

		if (mTTLockAPI.isConnected(mKey.getLockMac())) {
			mTTLockAPI.modifyAutoLockTime(null, PeachPreference.getOpenid(),
					mKey.getLockVersion(), mKey.getAdminPwd(), mKey.getLockKey(),
					mKey.getLockFlagPos(), mSwitch.isChecked() ? mSeconds : 0, mKey.getAesKeyStr());
		} else {
			mTTLockAPI.connect(mKey.getLockMac());
		}
	}

	private void setModifyAutoLockTimeCallback() {
		MyApplication.bleSession.setOperation(Operation.MODIFY_AUTO_LOCK_TIME);
		MyApplication.bleSession.setAutoLockTime(mSwitch.isChecked() ? mSeconds : 0);

		MyApplication.bleSession.setILockModifyAutoLockTime(new ILockModifyAutoLockTime() {
			@Override
			public void onModifyAutoLockTimeSuccess() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						stopLoading();
						toast(R.string.modify_auto_lock_success);
						finish();
					}
				});
			}

			@Override
			public void onModifyAutoLockTimeFail() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						stopLoading();
						toast(R.string.modify_auto_lock_fail);
					}
				});
			}
		});
	}
}
