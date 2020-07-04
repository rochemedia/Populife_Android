package com.populstay.populife.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.log.PeachLogger;

public class MessageDetailActivity extends BaseActivity {


	private static final String KEY_MESSAGE_ID = "key_message_id";

	private LinearLayout mLinearLayout;
	private TextView mTvDate, mTvContent;
	private String mMessageId;

	/**
	 * 启动当前 activity
	 *
	 * @param context   上下文
	 * @param messageId 消息id
	 */
	public static void actionStart(Context context, String messageId) {
		Intent intent = new Intent(context, MessageDetailActivity.class);
		intent.putExtra(KEY_MESSAGE_ID, messageId);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_detail);

		mMessageId = getIntent().getStringExtra(KEY_MESSAGE_ID);

		initView();
		requestMessageDetail();
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.message_detail);
		findViewById(R.id.page_action).setVisibility(View.GONE);

		mLinearLayout = findViewById(R.id.ll_message_detail);
		mLinearLayout.setVisibility(View.GONE);
		mTvDate = findViewById(R.id.tv_message_detail_date);
		mTvContent = findViewById(R.id.tv_message_detail_content);
	}

	private void requestMessageDetail() {
		RestClient.builder()
				.url(Urls.USER_MESSAGE_ITEM_DETAIL)
				.loader(this)
				.params("id", mMessageId)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("USER_MESSAGE_ITEM_DETAIL", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							mLinearLayout.setVisibility(View.VISIBLE);
							JSONObject messageDetail = result.getJSONObject("data");
							long time = messageDetail.getLong("createDate");
							String content = messageDetail.getString("content");
							mTvDate.setText(DateUtil.getDateToString( time,"yyyy-MM-dd HH:mm:ss"));
							mTvContent.setText(content);
						}
					}
				})
				.build()
				.get();
	}
}
