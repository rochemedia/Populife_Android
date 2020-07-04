package com.populstay.populife.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;

public class LockUserDetailActivity extends BaseActivity {

	private static final String KEY_USER_NAME = "key_user_name";
	private static final String KEY_KEY_ALIAS = "key_key_alias";
	private static final String KEY_KEY_TYPE = "key_key_type";

	private String mUserName, mKeyAlias;
	private int mKeyType;

	/**
	 * 启动当前 activity
	 */
	public static void actionStart(Context context, String userName, String keyAlias, int keyType) {
		Intent intent = new Intent(context, LockUserDetailActivity.class);
		intent.putExtra(KEY_USER_NAME, userName);
		intent.putExtra(KEY_KEY_ALIAS, keyAlias);
		intent.putExtra(KEY_KEY_TYPE, keyType);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_user_detail);

		getIntentData();
		initView();

	}

	private void getIntentData() {
		Intent data = getIntent();
		mUserName = data.getStringExtra(KEY_USER_NAME);
		mKeyAlias = data.getStringExtra(KEY_KEY_ALIAS);
		mKeyType = data.getIntExtra(KEY_KEY_TYPE, 0);
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(mUserName);
		findViewById(R.id.page_action).setVisibility(View.GONE);

		((TextView) findViewById(R.id.tv_lock_user_detail_key_alias)).setText(mKeyAlias);
		TextView keyType = findViewById(R.id.tv_lock_user_detail_key_type);
		Resources res = getResources();
		switch (mKeyType) {
			case 1:
				keyType.setText(res.getString(R.string.period));
				break;

			case 2:
				keyType.setText(res.getString(R.string.permanent));
				break;

			case 3:
				keyType.setText(res.getString(R.string.one_time));
				break;

			case 4:
				keyType.setText(res.getString(R.string.cyclic));
				break;

			default:
				break;
		}
	}
}
