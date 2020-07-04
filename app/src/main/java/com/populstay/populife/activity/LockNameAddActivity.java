package com.populstay.populife.activity;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.populstay.populife.ui.CustomProgress;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.string.StringUtil;

import java.util.WeakHashMap;

public class LockNameAddActivity extends BaseActivity implements View.OnClickListener {

	public static final String KEY_LOCK_INIT_DATA = "key_lock_init_data";

	private LinearLayout mLlSuccessfully;
	private EditText mEtName;
	private TextView mTvName, mTvBattery, mTvOK, mTvComplete;
	private ImageView mIvUpload;

	private String mLockInitData;
	private int mLockId, mBattery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_name_add);

		getIntentData();
		initView();
		initListener();
		initializeLock(mLockInitData);
	}

	private void getIntentData() {
		mLockInitData = getIntent().getStringExtra(KEY_LOCK_INIT_DATA);
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.lock_add);
		findViewById(R.id.page_action).setVisibility(View.GONE);

		mLlSuccessfully = findViewById(R.id.ll_lock_add_successfully);
		mEtName = findViewById(R.id.et_lock_name_add);
		mTvName = findViewById(R.id.tv_lock_add_success_name);
		mTvBattery = findViewById(R.id.tv_lock_add_success_battery);
		mTvOK = findViewById(R.id.tv_lock_name_add_ok);
		mTvComplete = findViewById(R.id.tv_lock_name_add_complete);
		mIvUpload = findViewById(R.id.iv_lock_name_add_upload);
		mIvUpload.setVisibility(View.INVISIBLE);
	}

	private void initListener() {
		mTvOK.setOnClickListener(this);
		mTvComplete.setOnClickListener(this);
		mIvUpload.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.tv_lock_name_add_ok:
				String name = mEtName.getText().toString();
				if (mLockId == 0) {
					toast(R.string.note_upload_lock_data);
				} else if (StringUtil.isBlank(name)) {
					toast(R.string.enter_lock_name);
				} else {
					modifyLockName(name);
				}
				break;

			case R.id.tv_lock_name_add_complete:
				goToNewActivity(MainActivity.class);
				break;

			case R.id.iv_lock_name_add_upload:
				initializeLock(mLockInitData);
				break;

			default:
				break;
		}
	}

	/**
	 * 请求服务器，初始化锁
	 */
	private void initializeLock(final String lockDataJson) {
		final WeakHashMap<String, Object> requestParams = parseLockData(lockDataJson);
		final CustomProgress customProgress = CustomProgress.show(this,
				getString(R.string.note_lock_init_ing), false, null);
		RestClient.builder()
				.url(Urls.LOCK_INIT)
				.params(requestParams)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						customProgress.cancel();
						PeachLogger.d("LOCK_INIT", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							JSONObject data = result.getJSONObject("data");
							mLockId = data.getInteger("lockId");
							mBattery = (int) requestParams.get("electricQuantity");
							PeachPreference.setBoolean(PeachPreference.HAVE_NEW_MESSAGE, true);
							toast(R.string.note_lock_init_success);
							mIvUpload.setVisibility(View.INVISIBLE);
						} else {
							toast(R.string.note_lock_init_fail);
							mIvUpload.setVisibility(View.VISIBLE);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						customProgress.cancel();
						toast(R.string.note_lock_init_fail);
						mIvUpload.setVisibility(View.VISIBLE);
					}
				})
				.error(new IError() {
					@Override
					public void onError(int code, String msg) {
						customProgress.cancel();
						toast(R.string.note_lock_init_fail);
						mIvUpload.setVisibility(View.VISIBLE);
					}
				})
				.build()
				.post();
	}

	/**
	 * 解析锁的数据
	 */
	private WeakHashMap<String, Object> parseLockData(String lockDataJson) {
		JSONObject lockInfo = JSON.parseObject(lockDataJson);
		final WeakHashMap<String, Object> params = new WeakHashMap<>();
		params.put("userId", PeachPreference.readUserId());
		String name = lockInfo.getString("lockName");
		params.put("name", name);
		// 显示蓝牙锁名称
		mEtName.setText(name);
		mEtName.setSelection(name.length());
		params.put("alias", "");
		params.put("mac", lockInfo.getString("lockMac"));
		params.put("key", lockInfo.getString("lockKey"));
		params.put("flagPos", lockInfo.getInteger("lockFlagPos"));
		params.put("aesKey", lockInfo.getString("aesKeyStr"));
		params.put("adminPwd", lockInfo.getString("adminPwd"));
		params.put("noKeyPwd", lockInfo.getString("noKeyPwd"));
		String deletePwd = lockInfo.getString("deletePwd");
		params.put("deletePwd", StringUtil.isBlank(deletePwd) ? "" : deletePwd);
		params.put("pwdInfo", lockInfo.getString("pwdInfo"));
		params.put("timestamp", lockInfo.getString("timestamp"));
		params.put("specialValue", lockInfo.getInteger("specialValue"));
		params.put("electricQuantity", lockInfo.getInteger("electricQuantity"));
		params.put("timezoneRawOffSet", String.valueOf(lockInfo.getInteger("timezoneRawOffset")));
		params.put("modelNum", lockInfo.getString("modelNum"));
		params.put("hardwareRevision", lockInfo.getString("hardwareRevision"));
		params.put("firmwareRevision", lockInfo.getString("firmwareRevision"));
		JSONObject lockVersion = lockInfo.getJSONObject("lockVersion");
		params.put("protocolType", lockVersion.getInteger("protocolType"));
		params.put("protocolVersion", lockVersion.getInteger("protocolVersion"));
		params.put("scene", lockVersion.getInteger("scene"));
		params.put("groupId", lockVersion.getInteger("groupId"));
		params.put("orgId", lockVersion.getInteger("orgId"));

		return params;
	}

	/**
	 * 修改锁名称
	 *
	 * @param lockName 修改以后的锁名称
	 */
	private void modifyLockName(final String lockName) {
		RestClient.builder()
				.url(Urls.LOCK_NAME_MODIFY)
				.loader(this)
				.params("lockId", mLockId)
				.params("lockAlias", lockName)
				.success(new ISuccess() {
					@SuppressLint("SetTextI18n")
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_NAME_MODIFY", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							mTvName.setText(lockName);
							mTvBattery.setText(String.valueOf(mBattery) + getString(R.string.unit_percent));
							Resources res = getResources();
							int batteryLevel = (mBattery - 1) / 20;
							int txtColor = res.getColor(R.color.battery_high_green);
							switch (batteryLevel) {
								case 0:
									txtColor = res.getColor(R.color.battery_low_red);
									break;

								case 1:
									txtColor = res.getColor(R.color.battery_middle_orange);
									break;

								case 2:
									txtColor = res.getColor(R.color.battery_high_green);
									break;

								case 3:
									txtColor = res.getColor(R.color.battery_high_green);
									break;

								case 4:
									txtColor = res.getColor(R.color.battery_high_green);
									break;

								default:
									break;
							}
							mTvBattery.setTextColor(txtColor);
							mLlSuccessfully.setVisibility(View.VISIBLE);
						} else {
							toast(R.string.note_lock_name_add_fail);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.note_lock_name_add_fail);
					}
				})
				.build()
				.post();
	}

	@Override
	public void onBackPressed() {
		goToNewActivity(MainActivity.class);
	}

	@Override
	public void finishCurrentActivity(View view) {
		goToNewActivity(MainActivity.class);
	}
}
