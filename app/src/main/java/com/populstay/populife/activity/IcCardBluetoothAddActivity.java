package com.populstay.populife.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.app.MyApplication;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.base.BaseApplication;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.Key;
import com.populstay.populife.enumtype.Operation;
import com.populstay.populife.lock.ILockIcCardAdd;
import com.populstay.populife.lock.ILockIcCardModifyPeriod;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;

import java.util.WeakHashMap;

import static com.populstay.populife.app.MyApplication.mTTLockAPI;

public class IcCardBluetoothAddActivity extends BaseActivity {

	public static final String VAL_IC_CARD_TYPE_PERMANENT = "val_ic_card_type_permanent";
	public static final String VAL_IC_CARD_TYPE_PERIOD = "val_ic_card_type_period";
	private static final String KEY_IC_CARD_ADD_TYPE = "key_ic_card_add_type";
	private static final String KEY_IC_CARD_ADD_NAME = "key_ic_card_add_name";
	private static final String KEY_IC_CARD_ADD_START_DATE = "key_ic_card_add_start_date";
	private static final String KEY_IC_CARD_ADD_END_DATE = "key_ic_card_add_end_date";

	private TextView mTvNote;

	private Key mKey = MyApplication.CURRENT_KEY;
	private String mCardName, mCardType, mStartDate, mEndDate;
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			lockAddIcCard();
			BaseApplication.getHandler().postDelayed(mRunnable, 5000);
		}
	};

	/**
	 * 启动当前 activity
	 */
	public static void actionStart(Context context, String cardType, String cardName, String startDate, String endDate) {
		Intent intent = new Intent(context, IcCardBluetoothAddActivity.class);
		intent.putExtra(KEY_IC_CARD_ADD_TYPE, cardType);
		intent.putExtra(KEY_IC_CARD_ADD_NAME, cardName);
		intent.putExtra(KEY_IC_CARD_ADD_START_DATE, startDate);
		intent.putExtra(KEY_IC_CARD_ADD_END_DATE, endDate);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ic_card_bluetooth_add);

		getIntentData();
		initView();

		showLoading();
		lockAddIcCard();
		BaseApplication.getHandler().postDelayed(mRunnable, 5000);
	}

	private void getIntentData() {
		Intent data = getIntent();
		mCardType = data.getStringExtra(KEY_IC_CARD_ADD_TYPE);
		mCardName = data.getStringExtra(KEY_IC_CARD_ADD_NAME);
		mStartDate = data.getStringExtra(KEY_IC_CARD_ADD_START_DATE);
		mEndDate = data.getStringExtra(KEY_IC_CARD_ADD_END_DATE);
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.ic_card_add);
		findViewById(R.id.page_action).setVisibility(View.GONE);

		mTvNote = findViewById(R.id.tv_ic_card_bluetooth_add_note);
	}

	private void lockAddIcCard() {
		mTvNote.setText(R.string.try_to_connect_the_lock);

		if (mTTLockAPI.isConnected(mKey.getLockMac())) {
			setAddIcCardCallback();
			mTTLockAPI.addICCard(null, PeachPreference.getOpenid(), mKey.getLockVersion(),
					mKey.getAdminPwd(), mKey.getLockKey(), mKey.getLockFlagPos(), mKey.getAesKeyStr());
		} else {
			setAddIcCardCallback();
			mTTLockAPI.connect(mKey.getLockMac());
		}
	}

	private void setAddIcCardCallback() {
		MyApplication.bleSession.setOperation(Operation.ADD_IC_CARD);
		MyApplication.bleSession.setLockmac(mKey.getLockMac());

		MyApplication.bleSession.setILockIcCardAdd(new ILockIcCardAdd() {
			@Override
			public void onEnterAddMode() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mTvNote.setText(R.string.lock_connected_to_add_ic_card);
					}
				});
			}

			@Override
			public void onSuccess(final long cardNumber) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						stopLoading();
						BaseApplication.getHandler().removeCallbacks(mRunnable);

						//限时 IC 卡，需要再调用一个修改期限的 SDK 方法
						if (VAL_IC_CARD_TYPE_PERIOD.equals(mCardType)) {
							lockModifyIcCardPeriod(cardNumber);
						} else {
							requestAddIcCard(String.valueOf(cardNumber));
						}
					}
				});
			}

			@Override
			public void onFail() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						stopLoading();
						toast(R.string.operation_fail);
						finish();
					}
				});
			}
		});
	}

	private void lockModifyIcCardPeriod(long cardNumber) {
		showLoading();

		if (mTTLockAPI.isConnected(mKey.getLockMac())) {
			setModifyIcCardPeriodCallback(cardNumber);
			mTTLockAPI.modifyICPeriod(null, PeachPreference.getOpenid(), mKey.getLockVersion(),
					mKey.getAdminPwd(), mKey.getLockKey(), mKey.getLockFlagPos(), cardNumber,
					DateUtil.getStringToDate(mStartDate, "yyyy-MM-dd HH:mm"),
					DateUtil.getStringToDate(mEndDate, "yyyy-MM-dd HH:mm"),
					mKey.getAesKeyStr(), DateUtil.getTimeZoneOffset());
		} else {
			setModifyIcCardPeriodCallback(cardNumber);
			mTTLockAPI.connect(mKey.getLockMac());
		}
	}

	private void setModifyIcCardPeriodCallback(final long cardNumber) {
		MyApplication.bleSession.setOperation(Operation.MODIFY_IC_CARD_PERIOD);
		MyApplication.bleSession.setLockmac(mKey.getLockMac());
		MyApplication.bleSession.setStartDate(DateUtil.getStringToDate(mStartDate, "yyyy-MM-dd HH:mm"));
		MyApplication.bleSession.setEndDate(DateUtil.getStringToDate(mEndDate, "yyyy-MM-dd HH:mm"));
		MyApplication.bleSession.setIcCardNumber(cardNumber);

		MyApplication.bleSession.setILockIcCardModifyPeriod(new ILockIcCardModifyPeriod() {
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						stopLoading();
						requestAddIcCard(String.valueOf(cardNumber));
					}
				});
			}

			@Override
			public void onFail() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						stopLoading();
						toast(R.string.operation_fail);
					}
				});
			}
		});
	}

	/**
	 * 请求服务器，添加 IC 卡
	 */
	private void requestAddIcCard(String cardNumber) {
		WeakHashMap<String, Object> params = new WeakHashMap<>();
		params.put("cardNumber", cardNumber);
		params.put("lockId", mKey.getLockId());
		params.put("remark", mCardName);
		if (VAL_IC_CARD_TYPE_PERIOD.equals(mCardType)) {
			params.put("timeZone", DateUtil.getTimeZone());
			params.put("startDate", mStartDate);
			params.put("endDate", mEndDate);
		}
		RestClient.builder()
				.url(Urls.IC_CARD_ADD)
				.params(params)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("IC_CARD_ADD", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							goToNewActivity(LockManageIcCardActivity.class);
							finish();
						} else if (code == 951) {
							toast(R.string.note_ic_card_has_been_added);
						} else {
							toast(R.string.operation_fail);
						}
					}
				})
				.build()
				.post();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BaseApplication.getHandler().removeCallbacks(mRunnable);
	}
}
