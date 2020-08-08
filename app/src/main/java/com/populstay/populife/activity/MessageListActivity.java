package com.populstay.populife.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.adapter.MessageListAdapter;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.ContentInfo;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.dialog.DialogUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;

import java.util.ArrayList;
import java.util.List;

public class MessageListActivity extends BaseActivity implements View.OnClickListener,
		AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

	private LinearLayout mLlNoData;
	private TextView mTvClear;
	private SwipeRefreshLayout mRefreshLayout;
	private ListView mListView;
	private MessageListAdapter mAdapter;
	private List<ContentInfo> mMessageList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_listview_refresh);

		initView();
		initListener();
		requestMessageList();
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.notification);
		mTvClear = findViewById(R.id.page_action);
		mTvClear.setText(R.string.clear);
		mTvClear.setVisibility(View.GONE);

		mLlNoData = findViewById(R.id.layout_no_data);
		mListView = findViewById(R.id.list_view);
		mAdapter = new MessageListAdapter(MessageListActivity.this, mMessageList);
		mListView.setAdapter(mAdapter);

		mRefreshLayout = findViewById(R.id.refresh_layout);
		mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
		mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mRefreshLayout.post(new Runnable() {
					@Override
					public void run() {
						mRefreshLayout.setRefreshing(true);
						requestMessageList();
					}
				});
			}
		});
	}

	private void initListener() {
		mTvClear.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
	}

	@Override
	public void onClick(View view) {
		DialogUtil.showCommonDialog(MessageListActivity.this, null,
				getString(R.string.note_clear_notifications),
				getString(R.string.clear), getString(R.string.cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						clearAllMessages();
					}
				}, null);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//		MessageDetailActivity.actionStart(MessageListActivity.this, mMessageList.get(i).getId());
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int index, long l) {
		DialogUtil.showCommonDialog(MessageListActivity.this, null,
				getString(R.string.note_delete_notification),
				getString(R.string.delete), getString(R.string.cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						deleteMessage(index);
					}
				}, null);
		return true;
	}

	/**
	 * 获取用户消息列表数据
	 */
	private void requestMessageList() {
		//todo 数据分页
		RestClient.builder()
				.url(Urls.USER_MESSAGE_LIST)
				.loader(this)
				.params("userId", PeachPreference.readUserId())
				.params("start", 0)
				.params("limit", 100)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						if (mRefreshLayout != null) {
							mRefreshLayout.setRefreshing(false);
						}

						PeachLogger.d("USER_MESSAGE_LIST", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							JSONArray messageList = result.getJSONArray("data");
							mMessageList.clear();
							if (messageList != null && !messageList.isEmpty()) {
								mLlNoData.setVisibility(View.GONE);
								mTvClear.setVisibility(View.VISIBLE);
								int size = messageList.size();
								for (int i = 0; i < size; i++) {
									JSONObject messageItem = messageList.getJSONObject(i);
									ContentInfo message = new ContentInfo();

									message.setId(messageItem.getString("id"));
									message.setUserId(messageItem.getString("userId"));
									message.setTitle(messageItem.getString("title"));
									message.setContent(messageItem.getString("content"));
									/*String createTime = DateUtil.getDateToString(
											messageItem.getLong("createDate"), "yyyy-MM-dd HH:mm:ss");
									message.setCreateTime(createTime);*/
									message.setHasRead("Y".equals(messageItem.getString("hasRead")));

									mMessageList.add(message);
								}
								mAdapter.notifyDataSetChanged();
							} else {
								mLlNoData.setVisibility(View.VISIBLE);
								mTvClear.setVisibility(View.GONE);
							}
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						if (mRefreshLayout != null) {
							mRefreshLayout.setRefreshing(false);
						}
					}
				})
				.error(new IError() {
					@Override
					public void onError(int code, String msg) {
						if (mRefreshLayout != null) {
							mRefreshLayout.setRefreshing(false);
						}
					}
				})
				.build()
				.get();
	}

	/**
	 * 清空用户所有消息
	 */
	private void clearAllMessages() {
		RestClient.builder()
				.url(Urls.USER_MESSAGE_CLEAR_ALL)
				.loader(MessageListActivity.this)
				.params("userId", PeachPreference.readUserId())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("USER_MESSAGE_CLEAR_ALL", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							toast(R.string.note_clear_notifications_success);
							mMessageList.clear();
							mAdapter.notifyDataSetChanged();
							mTvClear.setVisibility(View.GONE);
							mLlNoData.setVisibility(View.VISIBLE);
						} else {
							toast(R.string.note_clear_notifications_fail);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.note_clear_notifications_fail);
					}
				})
				.error(new IError() {
					@Override
					public void onError(int code, String msg) {
						toast(R.string.note_clear_notifications_fail);
					}
				})
				.build()
				.post();
	}

	/**
	 * 删除单条消息
	 *
	 * @param index 消息列表中，消息的 index
	 */
	private void deleteMessage(final int index) {
		String messageId = mMessageList.get(index).getId();
		RestClient.builder()
				.url(Urls.USER_MESSAGE_ITEM_DELETE)
				.loader(MessageListActivity.this)
				.params("id", messageId)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("USER_MESSAGE_ITEM_DELETE", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							mMessageList.remove(index);
							mAdapter.notifyDataSetChanged();
							if (mMessageList.isEmpty()) {
								mLlNoData.setVisibility(View.VISIBLE);
							}
						} else {
							toast(R.string.note_delete_notification_fail);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.note_delete_notification_fail);
					}
				})
				.build()
				.post();
	}
}
