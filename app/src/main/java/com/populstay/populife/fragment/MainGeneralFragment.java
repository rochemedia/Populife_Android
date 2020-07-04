package com.populstay.populife.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meiqia.core.MQManager;
import com.meiqia.core.bean.MQMessage;
import com.meiqia.core.callback.OnGetMessageListCallback;
import com.populstay.populife.R;
import com.populstay.populife.activity.AboutActivity;
import com.populstay.populife.activity.CustomerServiceActivity;
import com.populstay.populife.activity.GatewayListActivity;
import com.populstay.populife.activity.LockGroupListActivity;
import com.populstay.populife.activity.MessageListActivity;
import com.populstay.populife.activity.SettingsActivity;
import com.populstay.populife.base.BaseVisibilityFragment;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;

import java.util.List;


/**
 * 底部导航栏“General” Fragment
 * Created by Jerry
 */

public class MainGeneralFragment extends BaseVisibilityFragment implements View.OnClickListener {

	private LinearLayout mLlMessage, mLlService, mLlGateway, mLlSettings, mLlLockGroup, mLlAbout;
	private ImageView mIvHasNewMessage,mIvNewMsg;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_general, null);

		initView(view);
		initListener();
		return view;
	}

	@Override
	protected void onVisibilityChanged(boolean visible) {
		super.onVisibilityChanged(visible);
		if (visible) {
			boolean hasNewMessage = PeachPreference.getBoolean(PeachPreference.HAVE_NEW_MESSAGE);
			if (hasNewMessage) {
				mIvHasNewMessage.setVisibility(View.VISIBLE);
			} else {
				mIvHasNewMessage.setVisibility(View.INVISIBLE);
			}

			getMeiQiaUnreadMsg();
		}
	}

	/**
	 * 获取美洽未读消息
	 */
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
	}

	private void initView(View view) {
		view.findViewById(R.id.page_back).setVisibility(View.GONE);
		((TextView) view.findViewById(R.id.page_title)).setText(R.string.nav_tab_general);
		view.findViewById(R.id.page_action).setVisibility(View.GONE);

		mLlMessage = view.findViewById(R.id.ll_general_notification);
		mLlService = view.findViewById(R.id.ll_general_service);
		mLlGateway = view.findViewById(R.id.ll_general_gateway);
		mLlSettings = view.findViewById(R.id.ll_general_settings);
		mLlLockGroup = view.findViewById(R.id.ll_settings_lock_group);
		mLlAbout = view.findViewById(R.id.ll_settings_about);
		mIvHasNewMessage = view.findViewById(R.id.iv_general_notification_new);
		mIvNewMsg = view.findViewById(R.id.iv_general_service_new);
	}

	private void initListener() {
		mLlMessage.setOnClickListener(this);
		mLlService.setOnClickListener(this);
		mLlGateway.setOnClickListener(this);
		mLlSettings.setOnClickListener(this);
		mLlLockGroup.setOnClickListener(this);
		mLlAbout.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ll_general_notification:
				PeachPreference.setBoolean(PeachPreference.HAVE_NEW_MESSAGE, false);
				goToNewActivity(MessageListActivity.class);
				break;

			case R.id.ll_general_service:
				goToNewActivity(CustomerServiceActivity.class);
				break;

			case R.id.ll_general_gateway:
				goToNewActivity(GatewayListActivity.class);
				break;

			case R.id.ll_general_settings:
				goToNewActivity(SettingsActivity.class);
				break;

			case R.id.ll_settings_lock_group:
				goToNewActivity(LockGroupListActivity.class);
				break;

			case R.id.ll_settings_about:
				goToNewActivity(AboutActivity.class);
				break;

			default:
				break;
		}
	}
}
