package com.populstay.populife.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.base.BasePagerAdapter;
import com.populstay.populife.fragment.LockSendPasscodeFragment;
import com.populstay.populife.util.dialog.DialogUtil;
import com.populstay.populife.util.storage.PeachPreference;

import java.util.ArrayList;
import java.util.List;

public class LockSendPasscodeActivity extends BaseActivity {

	private static final String KEY_LOCK_ID = "key_lock_id";
	private static final String KEY_KEY_ID = "key_key_id";
	private static final String KEY_LOCK_NAME = "key_lock_name";
	private static final String KEY_LOCK_MAC = "key_lock_mac";
	private static final String KEY_PASSWORD_LIST = "key_password_list";

	private TextView mTvSend;
	private TabLayout mTabLayout;
	private ViewPager mViewPager;
	private List<Fragment> mFragmentList = new ArrayList<>();
	private BasePagerAdapter mAdapter;

	private int mLockId;
	private int mKeyId;
	private String mLockName;
	private String mLockMac;
	private ArrayList<String> mPasswordList = new ArrayList<>();

	/**
	 * 启动当前 activity
	 *
	 * @param context 上下文
	 * @param lockId  锁 id
	 */
	public static void actionStart(Context context, int lockId, int keyId, String lockName, String lockMac, ArrayList<String> passwordList) {
		Intent intent = new Intent(context, LockSendPasscodeActivity.class);
		intent.putExtra(KEY_LOCK_ID, lockId);
		intent.putExtra(KEY_KEY_ID, keyId);
		intent.putExtra(KEY_LOCK_NAME, lockName);
		intent.putExtra(KEY_LOCK_MAC, lockMac);
		intent.putStringArrayListExtra(KEY_PASSWORD_LIST, passwordList);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_send_passcode);

		mLockId = getIntent().getIntExtra(KEY_LOCK_ID, 0);
		mKeyId = getIntent().getIntExtra(KEY_KEY_ID, 0);
		mLockName = getIntent().getStringExtra(KEY_LOCK_NAME);
		mLockMac = getIntent().getStringExtra(KEY_LOCK_MAC);
		mPasswordList = getIntent().getStringArrayListExtra(KEY_PASSWORD_LIST);

		initView();
		initListener();
		initTab();

		showTips();
	}

	private void showTips() {
		if (!PeachPreference.getBoolean(PeachPreference.NOTE_CREATE_CUSTOMIZE_PASSWORD)) {
			DialogUtil.showCommonDialog(this, getString(R.string.note),
					getString(R.string.note_create_customize_password), getString(R.string.ok),
					null, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							PeachPreference.setBoolean(PeachPreference.NOTE_CREATE_CUSTOMIZE_PASSWORD, true);
						}
					}, null);
		}
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.create_password);
		mTvSend = findViewById(R.id.page_action);
		mTvSend.setText(R.string.share);

		mTabLayout = findViewById(R.id.tl_lock_send_passcode);
		mViewPager = findViewById(R.id.vp_lock_send_passcode);

		mTabLayout.setupWithViewPager(mViewPager);
	}


	private void initListener() {
		mTvSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				LockSendPasscodeFragment fragment = (LockSendPasscodeFragment) mFragmentList.get(mViewPager.getCurrentItem());
				if (fragment != null) {
					if (fragment.isPasscodeGenerated()) {
						// 分享
						fragment.showShare();
					} else {
						toast(R.string.note_create_password_first);
					}
				}
			}
		});
	}

	protected void initTab() {

		Resources res = getResources();
		String[] titles = new String[]{
				res.getString(R.string.customize), res.getString(R.string.permanent),
				res.getString(R.string.period), res.getString(R.string.one_time),
				res.getString(R.string.clear), res.getString(R.string.cyclic)};

		mFragmentList.add(LockSendPasscodeFragment.newInstance(LockSendPasscodeFragment.VAL_TAB_TYPE_CUSTOMIZE,
				mLockId, mKeyId, mLockName, mLockMac, mPasswordList));//自定义密码
		mFragmentList.add(LockSendPasscodeFragment.newInstance(LockSendPasscodeFragment.VAL_TAB_TYPE_PERMANENT,
				mLockId, mKeyId, mLockName, mLockMac, mPasswordList));//永久密码
		mFragmentList.add(LockSendPasscodeFragment.newInstance(LockSendPasscodeFragment.VAL_TAB_TYPE_PERIOD,
				mLockId, mKeyId, mLockName, mLockMac, mPasswordList));//限时密码
		mFragmentList.add(LockSendPasscodeFragment.newInstance(LockSendPasscodeFragment.VAL_TAB_TYPE_ONE_TIME,
				mLockId, mKeyId, mLockName, mLockMac, mPasswordList));//单次密码
		mFragmentList.add(LockSendPasscodeFragment.newInstance(LockSendPasscodeFragment.VAL_TAB_TYPE_CLEAR,
				mLockId, mKeyId, mLockName, mLockMac, mPasswordList));//清空密码
		mFragmentList.add(LockSendPasscodeFragment.newInstance(LockSendPasscodeFragment.VAL_TAB_TYPE_CYCLIC,
				mLockId, mKeyId, mLockName, mLockMac, mPasswordList));//循环密码

		mAdapter = new BasePagerAdapter(getSupportFragmentManager(), mFragmentList, titles);
		mViewPager.setAdapter(mAdapter);
	}
}
