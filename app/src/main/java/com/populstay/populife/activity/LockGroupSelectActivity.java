package com.populstay.populife.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.adapter.LockGroupSelectAdapter;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.LockGroup;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.string.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class LockGroupSelectActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

	public static final String KEY_LOCK_GROUP = "key_lock_group";
	public static final String KEY_LOCK_ID = "key_lock_id";
	private static final int REQUEST_CODE_GROUP_ADD = 1;

	private LinearLayout mLlNoData;
	private TextView mTvAdd;
	private ListView mListView;
	private LockGroupSelectAdapter mAdapter;
	private List<LockGroup> mGroupList = new ArrayList<>();

	private String mLockGroup;
	private int mLockId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_lock_group);

		getIntentData();
		initView();
		initListener();
		requestLockGroup();
	}

	private void getIntentData() {
		mLockGroup = getIntent().getStringExtra(KEY_LOCK_GROUP);
		mLockId = getIntent().getIntExtra(KEY_LOCK_ID, 0);
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.select_group);
		mTvAdd = findViewById(R.id.page_action);
		mTvAdd.setText("");
		mTvAdd.setCompoundDrawablesWithIntrinsicBounds(
				getResources().getDrawable(R.drawable.ic_add), null, null, null);

		mLlNoData = findViewById(R.id.layout_no_data);
		mListView = findViewById(R.id.lv_select_lock_group);
		mAdapter = new LockGroupSelectAdapter(LockGroupSelectActivity.this, mGroupList);
		mListView.setAdapter(mAdapter);
	}

	private void initListener() {
		mTvAdd.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
	}

	private void requestLockGroup() {
		RestClient.builder()
				.url(Urls.LOCK_GROUP_LIST)
				.loader(LockGroupSelectActivity.this)
				.params("userId", PeachPreference.readUserId())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_GROUP_LIST", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							JSONArray list = result.getJSONArray("data");
							mGroupList.clear();
							if (list != null && !list.isEmpty()) {
								mLlNoData.setVisibility(View.GONE);
								int size = list.size();
								for (int i = 0; i < size; i++) {
									JSONObject groupItem = list.getJSONObject(i);
									LockGroup lockGroup = new LockGroup();

									String id = groupItem.getString("id");
									if (!StringUtil.isBlank(id)) {
										lockGroup.setId(id);
										lockGroup.setName(groupItem.getString("name"));
										lockGroup.setCreateTime(groupItem.getLong("createDate"));
									} else {
										lockGroup.setName(getResources().getString(R.string.other_lowercase));
									}
									lockGroup.setSelected(mLockGroup.equals(lockGroup.getName()));
									lockGroup.setLockCount(groupItem.getInteger("lockCount"));
									mGroupList.add(lockGroup);
								}
								mAdapter.notifyDataSetChanged();
							} else {
								mLlNoData.setVisibility(View.VISIBLE);
							}
						}
					}
				})
				.build()
				.get();
	}

	private void bindLockGroup(final String groupId) {
		RestClient.builder()
				.url(Urls.LOCK_GROUP_BIND)
				.loader(LockGroupSelectActivity.this)
				.params("userId", PeachPreference.readUserId())
				.params("lockId", mLockId)
				.params("homeId", groupId)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_GROUP_BIND", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							for (LockGroup group : mGroupList) {
								if (groupId.equals(group.getId())) {
									group.setSelected(true);
									mLockGroup = group.getName();
								} else {
									group.setSelected(false);
								}
							}
							mAdapter.notifyDataSetChanged();
						}
					}
				})
				.build()
				.get();
	}

	@Override
	public void onClick(View view) {
		Intent intent = new Intent(LockGroupSelectActivity.this, LockGroupEditActivity.class);
		intent.putExtra(LockGroupEditActivity.KEY_GROUP_ACTION_TYPE, LockGroupEditActivity.VAL_TYPE_ADD);
		startActivityForResult(intent, REQUEST_CODE_GROUP_ADD);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
		LockGroup group = mGroupList.get(position);
		String groupId = group.getId();
		String groupName = group.getName();

		if (!mLockGroup.equals(groupName)) {
			bindLockGroup(groupId);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			requestLockGroup();
		}
	}

	@Override
	public void finishCurrentActivity(View view) {
		setResult();
		super.finishCurrentActivity(view);
	}

	@Override
	public void onBackPressed() {
		setResult();
		super.onBackPressed();
	}

	private void setResult() {
		Intent intent = new Intent();
		intent.putExtra(LockSettingsActivity.KEY_RESULT_DATA, mLockGroup);
		setResult(RESULT_OK, intent);
	}
}
