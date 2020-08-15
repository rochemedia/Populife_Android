package com.populstay.populife.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.base.BasePagerAdapter;
import com.populstay.populife.fragment.LockSendPasscodeFragment;
import com.populstay.populife.keypwdmanage.KeyPwdConstant;
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
	public static final String KEY_PWD_TYPE = "key_pwd_type";

	private TextView mTvSend,mPageTitle,mTvPwdTypeName;
	private ViewPager mViewPager;
	private List<Fragment> mFragmentList = new ArrayList<>();
	private BasePagerAdapter mAdapter;

	private int mLockId;
	private int mKeyId;
	private String mLockName;
	private String mLockMac;
	private ArrayList<String> mPasswordList = new ArrayList<>();
	private AlertDialog mPwdTypeDialog;
	private LinearLayout mLlShowPwdDialogBtn;

	/**
	 * 启动当前 activity
	 *
	 * @param context 上下文
	 * @param lockId  锁 id
	 */
	public static void actionStart(Context context, int lockId, int keyId, String lockName, String lockMac, ArrayList<String> passwordList,String keyType) {
		Intent intent = new Intent(context, LockSendPasscodeActivity.class);
		intent.putExtra(KEY_LOCK_ID, lockId);
		intent.putExtra(KEY_KEY_ID, keyId);
		intent.putExtra(KEY_LOCK_NAME, lockName);
		intent.putExtra(KEY_LOCK_MAC, lockMac);
		intent.putStringArrayListExtra(KEY_PASSWORD_LIST, passwordList);
		intent.putExtra(KEY_PWD_TYPE, keyType);
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
		initCurrentAccessTypeIndex(getIntent().getStringExtra(KEY_PWD_TYPE));

		initView();
		initListener();
		initTab();

		showTips();
	}

	private void initCurrentAccessTypeIndex(String keyPwdType){
		if (TextUtils.isEmpty(keyPwdType)){
			mCurrentAccessTypeIndex = 0;
			return;
		}
		for (int i = 0; i < mPwdAccessTypeArr.length; i++) {
			if (keyPwdType.equals(mPwdAccessTypeArr[i])){
				mCurrentAccessTypeIndex = i;
				break;
			}
		}
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
		mPageTitle = findViewById(R.id.page_title);
		mTvPwdTypeName = findViewById(R.id.tv_pwd_type_name);
		mTvSend = findViewById(R.id.page_action);
		mTvSend.setText(R.string.share);
		mTvSend.setVisibility(View.GONE);
		mLlShowPwdDialogBtn = findViewById(R.id.ll_show_pwd_dialog_btn);

		mViewPager = findViewById(R.id.vp_lock_send_passcode);
	}

	public void setPageTitle(int position){
		mPageTitle.setText(mPwdAccessTypeTitleArr[position]);
	}
	public void setPwdTypeName(int position){
		mTvPwdTypeName.setText(mPwdAccessTypeNameArr[position]);
	}

	public void setCurrentPage(int position){
		mViewPager.setCurrentItem(position, false);
		setPageTitle(position);
		setPwdTypeName(position);
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
		mLlShowPwdDialogBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showPwdTypeSelectDialog();
			}
		});
	}

	protected void initTab() {

		Resources res = getResources();
		mPwdAccessTypeTitleArr = new String[]{
				res.getString(R.string.create_pwd_permanent), res.getString(R.string.create_pwd_period),
				res.getString(R.string.create_pwd_one_time), res.getString(R.string.create_pwd_custom)};

		mPwdAccessTypeNameArr = new String[]{
				res.getString(R.string.key_pwd_permanent), res.getString(R.string.key_pwd_period),
				res.getString(R.string.key_pwd_one_time), res.getString(R.string.key_pwd_custom)};

		mFragmentList.add(LockSendPasscodeFragment.newInstance(LockSendPasscodeFragment.VAL_TAB_TYPE_PERMANENT,
				mLockId, mKeyId, mLockName, mLockMac, mPasswordList));//永久密码
		mFragmentList.add(LockSendPasscodeFragment.newInstance(LockSendPasscodeFragment.VAL_TAB_TYPE_PERIOD,
				mLockId, mKeyId, mLockName, mLockMac, mPasswordList));//限时密码
		mFragmentList.add(LockSendPasscodeFragment.newInstance(LockSendPasscodeFragment.VAL_TAB_TYPE_ONE_TIME,
				mLockId, mKeyId, mLockName, mLockMac, mPasswordList));//单次密码
		mFragmentList.add(LockSendPasscodeFragment.newInstance(LockSendPasscodeFragment.VAL_TAB_TYPE_CUSTOMIZE,
				mLockId, mKeyId, mLockName, mLockMac, mPasswordList));//自定义密码

		mAdapter = new BasePagerAdapter(getSupportFragmentManager(), mFragmentList, mPwdAccessTypeTitleArr);
		mViewPager.setAdapter(mAdapter);
		setCurrentPage(mCurrentAccessTypeIndex);
	}

	private String[] mPwdAccessTypeTitleArr;
	private String[] mPwdAccessTypeNameArr;

	private int[] mPwdAccessTypeItemArr = {R.id.rb_1, R.id.rb_2, R.id.rb_3, R.id.rb_4};
	private String[] mPwdAccessTypeArr = {KeyPwdConstant.IKeyPwdType.KEY_PWD_TYPE_PERMANENT,
			KeyPwdConstant.IKeyPwdType.KEY_PWD_TYPE_PERIOD,
			KeyPwdConstant.IKeyPwdType.KEY_PWD_TYPE_ONE_TIME,
			KeyPwdConstant.IKeyPwdType.KEY_PWD_TYPE_CUSTOM};
	private int mCurrentAccessTypeIndex = 0;
	private int mSelectAccessTypeIndex = 0;

	private void showPwdTypeSelectDialog() {
		mPwdTypeDialog = new AlertDialog.Builder(this).create();
		mPwdTypeDialog.show();
		final Window window = mPwdTypeDialog.getWindow();
		if (window != null) {
			window.setContentView(R.layout.dialog_select_pwd_type);
			window.setGravity(Gravity.CENTER);
			window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			//设置属性
			final WindowManager.LayoutParams params = window.getAttributes();
			params.width = WindowManager.LayoutParams.WRAP_CONTENT;
			params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			params.dimAmount = 0.5f;
			window.setAttributes(params);

			RadioGroup radioGroup = window.findViewById(R.id.radio_group);
			radioGroup.check(mPwdAccessTypeItemArr[mCurrentAccessTypeIndex]);
			radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					if (checkedId == mPwdAccessTypeItemArr[0]){
						//永久密码
						mSelectAccessTypeIndex = 0;
					}else if (checkedId == mPwdAccessTypeItemArr[1]){
						//限时密码
						mSelectAccessTypeIndex = 1;
					}else if (checkedId == mPwdAccessTypeItemArr[2]){
						//单次密码
						mSelectAccessTypeIndex = 2;
					}else if (checkedId == mPwdAccessTypeItemArr[3]){
						//自定义密码
						mSelectAccessTypeIndex = 3;
					}
				}
			});
			window.findViewById(R.id.tv_cancel_btn).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mPwdTypeDialog.dismiss();
				}
			});
			window.findViewById(R.id.tv_save_btn).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mCurrentAccessTypeIndex = mSelectAccessTypeIndex;
					setCurrentPage(mCurrentAccessTypeIndex);
					mPwdTypeDialog.dismiss();
				}
			});
		}
	}
}
