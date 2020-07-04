package com.populstay.populife.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meiqia.core.MQManager;
import com.meiqia.core.bean.MQMessage;
import com.meiqia.core.callback.OnGetMessageListCallback;
import com.meiqia.meiqiasdk.imageloader.MQImage;
import com.meiqia.meiqiasdk.util.MQIntentBuilder;
import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.permission.PermissionListener;
import com.populstay.populife.ui.MQGlideImageLoader;
import com.populstay.populife.util.locale.LanguageUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;

import java.util.HashMap;
import java.util.List;

public class CustomerServiceActivity extends BaseActivity implements View.OnClickListener {

	private LinearLayout mLlManualApp, mLlManualDeadbolt, mLlManualKeybox, mLlManualGateway, mLlQuestions,
			mLlFeedback, mLlSendEmail, mLlOnlineCommunication;
	private ImageView mIvNewMsg;

	@Override
	protected void onResume() {
		super.onResume();
		getMeiQiaUnreadMsg();
	}

	/**
	 * 获取美洽未读消息
	 */
	private void getMeiQiaUnreadMsg() {
		MQManager.getInstance(this).getUnreadMessages(new OnGetMessageListCallback() {
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer_service);

		initView();
		initListener();
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.help_center);
		findViewById(R.id.page_action).setVisibility(View.GONE);

		mLlManualApp = findViewById(R.id.ll_service_manual_app);
		mLlManualDeadbolt = findViewById(R.id.ll_service_manual_deadbolt);
		mLlManualKeybox = findViewById(R.id.ll_service_manual_keybox);
		mLlManualGateway = findViewById(R.id.ll_service_manual_gateway);
		mLlQuestions = findViewById(R.id.ll_service_questions);
		mLlFeedback = findViewById(R.id.ll_service_feedback);
		mLlSendEmail = findViewById(R.id.ll_service_send_email);
		mLlOnlineCommunication = findViewById(R.id.ll_service_online_communication);
		mIvNewMsg = findViewById(R.id.iv_service_online_new);
	}

	private void initListener() {
		mLlManualApp.setOnClickListener(this);
		mLlManualDeadbolt.setOnClickListener(this);
		mLlManualKeybox.setOnClickListener(this);
		mLlManualGateway.setOnClickListener(this);
		mLlQuestions.setOnClickListener(this);
		mLlFeedback.setOnClickListener(this);
		mLlSendEmail.setOnClickListener(this);
		mLlOnlineCommunication.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ll_service_manual_app:
				PDFActivity.actionStart(CustomerServiceActivity.this, getString(R.string.user_manual_app),
						"user_manual_app.pdf", true);
				break;

			case R.id.ll_service_manual_deadbolt:
				PDFActivity.actionStart(CustomerServiceActivity.this, getString(R.string.user_manual_deadbolt),
						"user_manual_deadbolt.pdf", true);
				break;

			case R.id.ll_service_manual_keybox:
				PDFActivity.actionStart(CustomerServiceActivity.this, getString(R.string.user_manual_keybox),
						LanguageUtil.isChinese(CustomerServiceActivity.this) ? "user_manual_keybox_cn.pdf" : "user_manual_keybox_en.pdf", true);
				break;

			case R.id.ll_service_manual_gateway:
				PDFActivity.actionStart(CustomerServiceActivity.this, getString(R.string.user_manual_gateway),
						"user_manual_gateway.pdf", true);
				break;

			case R.id.ll_service_questions:
				goToNewActivity(CommonQuestionActivity.class);
				break;

			case R.id.ll_service_feedback:
				goToNewActivity(FeedbackListActivity.class);
				break;

			case R.id.ll_service_send_email:
				sendEmail();
				break;

			case R.id.ll_service_online_communication:
				requestRuntimePermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						new PermissionListener() {
							@Override
							public void onGranted() {
								HashMap<String, String> clientInfo = new HashMap<>();
								clientInfo.put("userId", PeachPreference.readUserId());
								clientInfo.put("phoneNum", PeachPreference.getStr(PeachPreference.ACCOUNT_PHONE));
								clientInfo.put("email", PeachPreference.getStr(PeachPreference.ACCOUNT_EMAIL));
								MQImage.setImageLoader(new MQGlideImageLoader());
								startActivity(new MQIntentBuilder(CustomerServiceActivity.this).
										setCustomizedId(PeachPreference.readUserId())
										.setClientInfo(clientInfo)
										.updateClientInfo(clientInfo)
										.build());
							}

							@Override
							public void onDenied(List<String> deniedPermissions) {
								toast(R.string.note_permission);
							}
						});
				break;

			default:
				break;
		}
	}

	private void sendEmail() {
		// 创建Intent
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		//设置内容类型
		emailIntent.setType("message/rfc822");
		//设置额外信息
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.customer_service_email)});
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
		//启动Activity
		startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
	}
}
