package com.populstay.populife.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.app.MyApplication;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.base.BaseApplication;
import com.populstay.populife.fragment.MainGeneralFragment;
import com.populstay.populife.fragment.MainLockFragment;
import com.populstay.populife.fragment.MainMeFragment;
import com.populstay.populife.push.EventPushService;
import com.populstay.populife.ui.NoScrollViewPager;
import com.populstay.populife.util.storage.PeachPreference;

import java.util.ArrayList;
import java.util.List;

import cn.ittiger.player.PlayerManager;

public class MainActivity extends BaseActivity {

	private static final int TAB_LOCK = 0, TAB_GENERAL = 1, TAB_ME = 2;
	private static boolean mIsExit = false; // 退出 APP 判断标志
	@SuppressLint("HandlerLeak")
	private static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mIsExit = false;
		}
	};
	private int mCurrentTab = TAB_LOCK;
	private NoScrollViewPager mViewPager;
	private RadioGroup navigation;
	private AlertDialog DIALOG;
	private TextView mTvPrivacyPolicy;
	private AppCompatButton mBtnNotAgree, mBtnAgree;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//showAppUserManual();
		initView();
		initListener();
		init();

		/*if (!PeachPreference.getBoolean(PeachPreference.AGREE_USER_TERMS_PRIVACY_POLICY)) {
			showPrivacyPolicy();
		}*/
	}

	private void showPrivacyPolicy() {
		DIALOG = new AlertDialog.Builder(this).create();
		DIALOG.setCanceledOnTouchOutside(false);
		DIALOG.setCancelable(false);
		DIALOG.show();
		final Window window = DIALOG.getWindow();
		if (window != null) {
			window.setContentView(R.layout.dialog_privacy_policy);
			window.setGravity(Gravity.CENTER);
			window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			//设置属性
			final WindowManager.LayoutParams params = window.getAttributes();
			params.width = WindowManager.LayoutParams.MATCH_PARENT;
			params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			params.dimAmount = 0.5f;
			window.setAttributes(params);

			mTvPrivacyPolicy = window.findViewById(R.id.tv_dialog_privacy_policy);
			mBtnNotAgree = window.findViewById(R.id.btn_dialog_privacy_policy_not_agree);
			mBtnAgree = window.findViewById(R.id.btn_dialog_privacy_policy_agree);

			mTvPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					goToNewActivity(PrivacyPolicyActivity.class);
				}
			});
			mBtnNotAgree.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
					System.exit(0);
				}
			});
			mBtnAgree.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					PeachPreference.setBoolean(PeachPreference.AGREE_USER_TERMS_PRIVACY_POLICY, true);
					DIALOG.cancel();
				}
			});
		}
	}

	private void showAppUserManual() {
		boolean hasShownBefore = PeachPreference.getBoolean(PeachPreference.SHOW_APP_USER_MAUAL);
		if (!hasShownBefore) {
			BaseApplication.getHandler().postDelayed(new Runnable() {
				@Override
				public void run() {
					PDFActivity.actionStart(MainActivity.this, getString(R.string.user_manual_app),
							"user_manual_app.pdf", false);
					PeachPreference.setBoolean(PeachPreference.SHOW_APP_USER_MAUAL, true);
				}
			}, 1000);
		}
	}

	private void initView() {
		mViewPager = findViewById(R.id.nsv_main);
		mViewPager.setOffscreenPageLimit(3);
		setupViewPager(mViewPager);

		navigation = findViewById(R.id.navigation);
		setCurrentTab(mCurrentTab);
	}

	/**
	 * 切换底部导航栏 tab 状态
	 *
	 * @param clickedTab 被点击的 tab
	 */
	private void setCurrentTab(int clickedTab) {
		mCurrentTab = clickedTab;
		mViewPager.setCurrentItem(clickedTab, false);
	}

	private void setupViewPager(ViewPager viewPager) {
		ViewPagerAdapter localViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		localViewPagerAdapter.addFragment(new MainLockFragment());
		localViewPagerAdapter.addFragment(new MainGeneralFragment());
		localViewPagerAdapter.addFragment(new MainMeFragment());
		viewPager.setAdapter(localViewPagerAdapter);
	}

	private void initListener() {
		navigation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
				switch (checkedId) {
					case R.id.nav_main_lock:
						setCurrentTab(TAB_LOCK);
						break;

					case R.id.nav_main_general:
						setCurrentTab(TAB_GENERAL);
						break;

					case R.id.nav_main_me:
						setCurrentTab(TAB_ME);
						break;

					default:
						break;
				}
			}
		});
	}

	/**
	 * Initialization
	 */
	private void init() {
		//turn on bluetooth
//		MyApplication.mTTLockAPI.requestBleEnable(this);
		MyApplication.mTTLockAPI.startBleService(this);
		Intent pushServiceIntent = new Intent(this, EventPushService.class);
		startService(pushServiceIntent);
	}

	/**
	 * 退出 APP
	 */
	private void exit() {
		if (!mIsExit) {
			mIsExit = true;
			toast(R.string.click_again_to_exit);
			mHandler.sendEmptyMessageDelayed(-1, 2000);
		} else {
			finish();
			System.exit(0);
		}
	}

	@Override
	public void onBackPressed() {
		if (PlayerManager.getInstance().onBackPressed()){
			return;
		}
		exit();
	}

	class ViewPagerAdapter extends FragmentPagerAdapter {
		private List<Fragment> mListFragment = new ArrayList<>();

		private ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		private void addFragment(Fragment paramFragment) {
			if (mListFragment == null)
				mListFragment = new ArrayList();
			mListFragment.add(paramFragment);
		}

		public int getCount() {
			if (mListFragment == null)
				return 0;
			return mListFragment.size();
		}

		public Fragment getItem(int paramInt) {
			if (mListFragment == null)
				return null;
			return mListFragment.get(paramInt);
		}
	}
}
