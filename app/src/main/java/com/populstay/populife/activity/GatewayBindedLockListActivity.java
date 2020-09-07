package com.populstay.populife.activity;

import android.content.Context;
import android.content.Intent;
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
import com.populstay.populife.adapter.GatewayBindedLockListAdapter;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.Gateway;
import com.populstay.populife.entity.GatewayBindedLock;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;

import java.util.ArrayList;
import java.util.List;

public class GatewayBindedLockListActivity extends BaseActivity {

	private static final String KEY_GATEWAY = "key_gateway";

	private LinearLayout mLlNoData;
	private SwipeRefreshLayout mRefreshLayout;

	private ListView mListView;
	private GatewayBindedLockListAdapter mAdapter;
	private List<GatewayBindedLock> mLockList = new ArrayList<>();
	private Gateway mGateway;

	/**
	 * 启动当前 activity
	 */
	public static void actionStart(Context context, Gateway gateway) {
		Intent intent = new Intent(context, GatewayBindedLockListActivity.class);
		intent.putExtra(KEY_GATEWAY, gateway);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gateway_binded_lock_list);

		getIntentData();
		initView();
		initListener();
		requestGatewayBindedLockList();
	}

	private void initListener() {
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				GatewayBindedLock lock = mLockList.get(i);
				GatewayBindedLockDetailActivity.actionStart(GatewayBindedLockListActivity.this,
						lock.getLockId(), lock.getAlias(),lock.getLockName(),lock.getSpecialValue());
			}
		});
	}

	private void getIntentData() {
		Intent data = getIntent();
		mGateway = data.getParcelableExtra(KEY_GATEWAY);

		PeachLogger.d(mGateway);
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(mGateway.getName());
		findViewById(R.id.page_action).setVisibility(View.GONE);
//
		mLlNoData = findViewById(R.id.layout_no_data);
		mListView = findViewById(R.id.lv_gateway_binded_lock_list);
		mAdapter = new GatewayBindedLockListAdapter(this, mLockList);
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
						requestGatewayBindedLockList();
					}
				});
			}
		});
	}

	private void requestGatewayBindedLockList() {
		RestClient.builder()
				.url(Urls.GATEWAY_BINDED_LOCK_LIST)
				.params("userId", PeachPreference.readUserId())
				.params("gatewayId", mGateway.getGatewayId())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						if (mRefreshLayout != null) {
							mRefreshLayout.setRefreshing(false);
						}
						PeachLogger.d("GATEWAY_BINDED_LOCK_LIST", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							JSONArray dataArray = result.getJSONArray("data");
							mLockList.clear();
							if (dataArray != null && !dataArray.isEmpty()) {
								mLlNoData.setVisibility(View.GONE);
								int size = dataArray.size();
								for (int i = 0; i < size; i++) {
									JSONObject objItem = dataArray.getJSONObject(i);
									GatewayBindedLock lock = new GatewayBindedLock();
									lock.setLockId(objItem.getInteger("lockId"));
									lock.setLockMac(objItem.getString("lockMac"));
									lock.setLockName(objItem.getString("lockName"));
									lock.setSignal(objItem.getInteger("rssi"));
									lock.setUpdateDate(objItem.getLong("updateDate"));
									lock.setAlias(objItem.getString("alias"));
									lock.setSpecialValue(objItem.getInteger("specialValue"));

									mLockList.add(lock);
								}
								mAdapter.notifyDataSetChanged();
							} else {
								mLlNoData.setVisibility(View.VISIBLE);
							}
						} else {
							mLlNoData.setVisibility(View.VISIBLE);
							toast(R.string.note_no_such_gateway_exists);
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
}
