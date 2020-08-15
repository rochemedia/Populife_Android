package com.populstay.populife.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.adapter.ExpiringEkeyListAdapter;
import com.populstay.populife.app.MyApplication;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.BluetoothKey;
import com.populstay.populife.entity.Key;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;

import java.util.ArrayList;
import java.util.List;

public class ExpiringKeyListActivity extends BaseActivity {

	private LinearLayout mLlNoData;
	private ListView mListView;
	private ExpiringEkeyListAdapter mAdapter;
	private SwipeRefreshLayout mRefreshLayout;

	private Key mKey = MyApplication.CURRENT_KEY;
	private List<BluetoothKey> mBluetoothKeyList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_listview_refresh);

		initView();
		requestExpiringKeys();
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.expiring_keys);
		findViewById(R.id.page_action).setVisibility(View.GONE);

		mLlNoData = findViewById(R.id.layout_no_data);
		mListView = findViewById(R.id.list_view);
		mAdapter = new ExpiringEkeyListAdapter(this, mBluetoothKeyList);
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
						requestExpiringKeys();
					}
				});
			}
		});
	}

	private void requestExpiringKeys() {
		RestClient.builder()
				.url(Urls.LOCK_EKEY_LIST_EXPIRING)
				.loader(ExpiringKeyListActivity.this)
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

						PeachLogger.d("LOCK_EKEY_LIST_EXPIRING", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							JSONArray list = result.getJSONArray("data");
							mBluetoothKeyList.clear();
							if (list != null && !list.isEmpty()) {
								mLlNoData.setVisibility(View.GONE);
								int size = list.size();
								for (int i = 0; i < size; i++) {
									JSONObject ekeyItem = list.getJSONObject(i);
									BluetoothKey bluetoothKey = new BluetoothKey();

									bluetoothKey.setAvatar(ekeyItem.getString("avatar"));
									bluetoothKey.setAlias(ekeyItem.getString("nickname"));
									bluetoothKey.setStartDate(ekeyItem.getLong("startDate") * 1000);
									bluetoothKey.setEndDate(ekeyItem.getLong("endDate") * 1000);
									bluetoothKey.setLockAlias(ekeyItem.getString("lockAlias"));
									bluetoothKey.setDayNum(ekeyItem.getInteger("dayNum"));
									bluetoothKey.setKeyStatus(ekeyItem.getString("keyStatus"));

									mBluetoothKeyList.add(bluetoothKey);
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
}
