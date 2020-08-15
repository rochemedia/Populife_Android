package com.populstay.populife.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.adapter.LockOperateRecordAdapter;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.LockOperateRecord;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LockOperateRecordSearchActivity extends BaseActivity {

	private static final String KEY_LOCK_ID = "key_lock_id";

	private TextView mTvSearch;
	private EditText mEtInput;
	private LinearLayout mLlNoData;
	private ExpandableListView mExpandableListView;
	private LockOperateRecordAdapter mAdapter;

	private TagFlowLayout mTagFlowLayout;
	private TagAdapter<String> mTagAdapter;
	private List<String> mKeywordsDisplay = new ArrayList<>();
	private List<String> mKeywordsSearch = new ArrayList<>();

	private List<String> mGroupList = new ArrayList<>(); // 组元素数据列表（日期）
	private Map<String, List<LockOperateRecord>> mChildList = new LinkedHashMap<>(); // 子元素数据列表（操作记录）

	private int mLockId;

	/**
	 * 启动当前 activity
	 *
	 * @param context 上下文
	 * @param lockId  锁id
	 */
	public static void actionStart(Context context, int lockId) {
		Intent intent = new Intent(context, LockOperateRecordSearchActivity.class);
		intent.putExtra(KEY_LOCK_ID, lockId);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_operate_record_search);

		getIntentData();
		initView();
		initListener();
	}


	private void getIntentData() {
		mLockId = getIntent().getIntExtra(KEY_LOCK_ID, 0);
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.search_records);
		mTvSearch = findViewById(R.id.page_action);
		mTvSearch.setText(R.string.search);

		mEtInput = findViewById(R.id.et_lock_operate_records_search);
		mLlNoData = findViewById(R.id.layout_no_data);
		mExpandableListView = findViewById(R.id.eplv_lock_operate_records_search);
		mAdapter = new LockOperateRecordAdapter(LockOperateRecordSearchActivity.this, mGroupList, mChildList);
		mExpandableListView.setAdapter(mAdapter);
		// 设置 expandableListview 默认不可折叠
		mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				return false;
			}
		});

		mTagFlowLayout = findViewById(R.id.tfl_lock_records_search_keyword);
		mKeywordsDisplay.add(getString(R.string.unlock_with_app));
		mKeywordsDisplay.add(getString(R.string.lock_with_app));
		mKeywordsDisplay.add(getString(R.string.unlock_with_keyboard));
		mKeywordsSearch.add(getString(R.string.search_keyword_unlock_with_app));
		mKeywordsSearch.add(getString(R.string.search_keyword_lock_with_app));
		mKeywordsSearch.add(getString(R.string.search_keyword_unlock_with_keyboard));
		mTagAdapter = new TagAdapter<String>(mKeywordsDisplay) {
			@Override
			public View getView(FlowLayout parent, int position, String s) {
				TextView tvKeyword = (TextView) getLayoutInflater().inflate(R.layout.item_search_keyword, null);
				tvKeyword.setText(s);
				return tvKeyword;
			}
		};
		mTagFlowLayout.setAdapter(mTagAdapter);
	}

	private void initListener() {
		mTvSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				requestLockOperateRecords(mEtInput.getText().toString());
			}
		});
		mTagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
			@Override
			public boolean onTagClick(View view, int position, FlowLayout parent) {
				requestLockOperateRecords(mKeywordsSearch.get(position));
				return true;
			}
		});
	}

	/**
	 * 获取锁的操作记录
	 */
	private void requestLockOperateRecords(String keyword) {
		//todo 数据分页
		RestClient.builder()
				.url(Urls.LOCK_OPERATE_RECORDS_GET)
				.loader(LockOperateRecordSearchActivity.this)
				.params("lockId", mLockId)
				.params("userId", PeachPreference.readUserId())
				.params("keyword", keyword)
				.params("start", 0)
				.params("limit", 100)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_OPERATE_RECORDS_GET", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							JSONArray dataArray = result.getJSONArray("data");
							mGroupList.clear();
							mChildList.clear();
							if (dataArray != null && !dataArray.isEmpty()) {
								mLlNoData.setVisibility(View.GONE);
								int groupSize = dataArray.size();
								for (int i = 0; i < groupSize; i++) {
									JSONObject recordData = dataArray.getJSONObject(i);
									//循环并得到key列表
									for (String key : recordData.keySet()) {
										// 获得key
										mGroupList.add(key);
										//获得key值对应的value
										JSONArray records = recordData.getJSONArray(key);
										List<LockOperateRecord> recordList = new ArrayList<>();
										if (records != null && !records.isEmpty()) {
											int childSize = records.size();
											for (int j = 0; j < childSize; j++) {
												JSONObject recordItem = records.getJSONObject(j);
												LockOperateRecord record = new LockOperateRecord();
												record.setId(recordItem.getString("id"));
												record.setNickname(recordItem.getString("nickname"));
												record.setAvatar(recordItem.getString("avatar"));
												record.setContent(recordItem.getString("content"));
												long date = recordItem.getLong("createDate");
												record.setCreateDate(DateUtil.getDateToString(date, "yyyy-MM-dd HH:mm:ss"));
												recordList.add(record);
											}
											mChildList.put(key, recordList);
										}

									}
								}
								for (int k = 0; k < mGroupList.size(); k++) {
									mExpandableListView.expandGroup(k);
								}
								mAdapter.notifyDataSetChanged();
							} else {
								mLlNoData.setVisibility(View.VISIBLE);
							}
						} else {
							mLlNoData.setVisibility(View.VISIBLE);
						}
					}
				})
				.build()
				.get();
	}
}
