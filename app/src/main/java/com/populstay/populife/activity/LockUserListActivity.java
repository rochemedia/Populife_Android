package com.populstay.populife.activity;

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
import com.populstay.populife.adapter.LockUserListAdapter;
import com.populstay.populife.app.MyApplication;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.Key;
import com.populstay.populife.entity.LockUser;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;

import java.util.ArrayList;
import java.util.List;

public class LockUserListActivity extends BaseActivity implements View.OnClickListener,
		AdapterView.OnItemClickListener {

	private TextView mTvExpiring;
	private LinearLayout mLlNoData;
	private ListView mListView;
	private LockUserListAdapter mAdapter;
	private SwipeRefreshLayout mRefreshLayout;

	private Key mKey = MyApplication.CURRENT_KEY;
	private List<LockUser> mUserList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_listview_refresh);

		initView();
		initListener();
		requestLockUsers();
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.lock_users);
		mTvExpiring = findViewById(R.id.page_action);
		mTvExpiring.setText(R.string.expiring);

		mLlNoData = findViewById(R.id.layout_no_data);
		mListView = findViewById(R.id.list_view);
		mAdapter = new LockUserListAdapter(this, mUserList);
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
						requestLockUsers();
					}
				});
			}
		});
	}

	private void initListener() {
		mTvExpiring.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
	}

	private void requestLockUsers() {
		RestClient.builder()
				.url(Urls.LOCK_USER_LIST)
				.loader(LockUserListActivity.this)
				.params("userId", PeachPreference.readUserId())
				.params("lockId", mKey.getLockId())
				.params("pageNo", 1)
				.params("pageSize", 50)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						if (mRefreshLayout != null) {
							mRefreshLayout.setRefreshing(false);
						}

						PeachLogger.d("LOCK_USER_LIST", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							JSONArray list = result.getJSONArray("data");
							mUserList.clear();
							if (list != null && !list.isEmpty()) {
								mLlNoData.setVisibility(View.GONE);
								int size = list.size();
								for (int i = 0; i < size; i++) {
									JSONObject userItem = list.getJSONObject(i);
									LockUser lockUser = new LockUser();
									lockUser.setUserId(userItem.getString("userId"));
									lockUser.setAvatar(userItem.getString("avatar"));
									lockUser.setNickname(userItem.getString("nickname"));
									lockUser.setUserName(userItem.getString("userName"));
									lockUser.setAlias(userItem.getString("alias"));
									lockUser.setType(userItem.getInteger("type"));

									mUserList.add(lockUser);
								}
								mAdapter.notifyDataSetChanged();
							} else {
								mLlNoData.setVisibility(View.VISIBLE);
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

	@Override
	public void onClick(View view) {
		goToNewActivity(ExpiringKeyListActivity.class);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		LockUser lockUser = mUserList.get(i);
		LockUserDetailActivity.actionStart(LockUserListActivity.this, lockUser.getUserName(),
				lockUser.getAlias(), lockUser.getType());
	}
}
