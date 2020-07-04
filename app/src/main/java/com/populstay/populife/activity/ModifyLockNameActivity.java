package com.populstay.populife.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.string.StringUtil;

public class ModifyLockNameActivity extends BaseActivity {

	public static final String KEY_LOCK_NAME = "key_lock_name";
	public static final String KEY_LOCK_ID = "key_lock_id";

	private AppCompatEditText mEtInput;
	private TextView mTvSave;

	private String mLockName;
	private int mLockId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_lock_name);

		getIntentData();
		initView();
	}

	private void getIntentData() {
		mLockName = getIntent().getStringExtra(KEY_LOCK_NAME);
		mLockId = getIntent().getIntExtra(KEY_LOCK_ID, 0);
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.modify_lock_name);
		mTvSave = findViewById(R.id.page_action);
		mTvSave.setText(R.string.save);

		mEtInput = findViewById(R.id.et_modify_lock_name);

		mEtInput.setText(mLockName);
		mEtInput.setSelection(mLockName.length());


		mTvSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String input = mEtInput.getText().toString();
				if (StringUtil.isBlank(input)) {
					toast(R.string.input_something);
				} else if (input.equals(mLockName)) {
					toast(R.string.note_nothing_changed);
				} else {
					modifyLockName(input);
				}
			}
		});
	}

	/**
	 * 修改锁名称
	 *
	 * @param lockName 修改以后的锁名称
	 */
	private void modifyLockName(final String lockName) {
		RestClient.builder()
				.url(Urls.LOCK_NAME_MODIFY)
				.loader(ModifyLockNameActivity.this)
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
							Intent intent = new Intent();
							intent.putExtra(LockSettingsActivity.KEY_RESULT_DATA, lockName);
							setResult(RESULT_OK, intent);
							finish();
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
}
