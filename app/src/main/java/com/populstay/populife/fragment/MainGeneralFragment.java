package com.populstay.populife.fragment;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BasePagerAdapter;
import com.populstay.populife.base.BaseVisibilityFragment;
import com.populstay.populife.find.MallListFragment;
import com.populstay.populife.find.NewsListFragment;
import com.populstay.populife.find.UserManualFragment;
import com.populstay.populife.find.VideoListFragment;

import java.util.ArrayList;
import java.util.List;

import cn.ittiger.player.PlayerManager;


/**
 * 底部导航栏“General” Fragment
 * Created by Jerry
 */

public class MainGeneralFragment extends BaseVisibilityFragment implements View.OnClickListener {

	private TabLayout mTabLayout;
	private ViewPager mViewPager;
	private List<Fragment> mFragmentList = new ArrayList<>();
	private BasePagerAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_general, null);

		initView(view);
		initListener();
		initTab();
		return view;
	}

	protected void initTab() {

		Resources res = getResources();
		String[] titles = new String[]{
				res.getString(R.string.user_manual), res.getString(R.string.operation_video),
				res.getString(R.string.shopping_mall), res.getString(R.string.news)};

		mFragmentList.add(UserManualFragment.newInstance());
		mFragmentList.add(VideoListFragment.newInstance());
		mFragmentList.add(MallListFragment.newInstance());
		mFragmentList.add(NewsListFragment.newInstance());


		mAdapter = new BasePagerAdapter(getActivity().getSupportFragmentManager(), mFragmentList, titles);
		mViewPager.setAdapter(mAdapter);
	}

	@Override
	protected void onVisibilityChanged(boolean visible) {
		super.onVisibilityChanged(visible);
		if (!visible){
			PlayerManager.getInstance().pause();
		}
		/*if (visible) {
			boolean hasNewMessage = PeachPreference.getBoolean(PeachPreference.HAVE_NEW_MESSAGE);
			if (hasNewMessage) {
				mIvHasNewMessage.setVisibility(View.VISIBLE);
			} else {
				mIvHasNewMessage.setVisibility(View.INVISIBLE);
			}

			getMeiQiaUnreadMsg();
		}*/
	}

	/**
	 * 获取美洽未读消息
	 *//*
	private void getMeiQiaUnreadMsg() {
		MQManager.getInstance(getActivity()).getUnreadMessages(new OnGetMessageListCallback() {
			@Override
			public void onSuccess(List<MQMessage> messageList) {
				PeachLogger.d(messageList);
				if (messageList != null && !messageList.isEmpty())
					mIvNewMsg.setVisibility(View.VISIBLE);
				else
					mIvNewMsg.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onFailure(int code, String message) {
			}
		});
	}*/

	private void initView(View view) {
		view.findViewById(R.id.page_back).setVisibility(View.GONE);
		((TextView) view.findViewById(R.id.page_title)).setText(R.string.nav_tab_general);
		view.findViewById(R.id.page_action).setVisibility(View.GONE);

		/*mLlMessage = view.findViewById(R.id.ll_general_notification);
		mLlService = view.findViewById(R.id.ll_general_service);
		mLlGateway = view.findViewById(R.id.ll_general_gateway);
		mLlSettings = view.findViewById(R.id.ll_general_settings);
		mLlLockGroup = view.findViewById(R.id.ll_settings_lock_group);
		mLlAbout = view.findViewById(R.id.ll_settings_about);
		mIvHasNewMessage = view.findViewById(R.id.iv_general_notification_new);
		mIvNewMsg = view.findViewById(R.id.iv_general_service_new);*/

		mTabLayout = view.findViewById(R.id.tl_lock_send_passcode);
		mViewPager = view.findViewById(R.id.vp_lock_send_passcode);

		mTabLayout.setupWithViewPager(mViewPager);
		/*mTabLayout.setCurrentTab(0);
		mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
			@Override
			public void onTabSelect(int position) {

			}

			@Override
			public void onTabReselect(int position) {

			}
		});*/


	}

	private void initListener() {
		/*mLlMessage.setOnClickListener(this);
		mLlService.setOnClickListener(this);
		mLlGateway.setOnClickListener(this);
		mLlSettings.setOnClickListener(this);
		mLlLockGroup.setOnClickListener(this);
		mLlAbout.setOnClickListener(this);*/
	}

	@Override
	public void onClick(View v) {

	}

	/*@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ll_general_notification:
				break;

			case R.id.ll_general_service:
				break;

			case R.id.ll_general_gateway:
				break;

			case R.id.ll_general_settings:
				break;

			case R.id.ll_settings_lock_group:
				break;

			case R.id.ll_settings_about:
				break;

			default:
				break;
		}
	}*/
}
