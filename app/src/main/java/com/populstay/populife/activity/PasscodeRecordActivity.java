package com.populstay.populife.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.adapter.PasscodeRecordAdapter;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.LockOperateRecord;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.log.PeachLogger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PasscodeRecordActivity extends BaseActivity {

	private static final String KEY_PASSCODE_ALIAS = "key_alias";
	private static final String KEY_PASSCPDE = "key_id";

	private TextView mTvTitle;
	private LinearLayout mLlNoData;
	private ExpandableListView mExpandableListView;
	private PasscodeRecordAdapter mAdapter;

	private List<String> mGroupList = new ArrayList<>(); // 组元素数据列表（日期）
	private Map<String, List<LockOperateRecord>> mChildList = new LinkedHashMap<>(); // 子元素数据列表（操作记录）
	private String mPasscode;

	/**
	 * 启动当前 activity
	 */
	public static void actionStart(Context context, String passcode) {
		Intent intent = new Intent(context, PasscodeRecordActivity.class);
		intent.putExtra(KEY_PASSCPDE, passcode);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ekey_passcode_record);

		getIntentData();
		initView();
		requestPasscodeOperateRecords();
	}

	private void getIntentData() {
		Intent data = getIntent();
		mPasscode = data.getStringExtra(KEY_PASSCPDE);
	}

	private void initView() {
		mTvTitle = findViewById(R.id.page_title);
		mTvTitle.setText(mPasscode);
		findViewById(R.id.page_action).setVisibility(View.GONE);

		mLlNoData = findViewById(R.id.layout_no_data);
		mExpandableListView = findViewById(R.id.eplv_eky_passcode_records);
		mAdapter = new PasscodeRecordAdapter(this, mGroupList, mChildList);
		mExpandableListView.setAdapter(mAdapter);
		// 设置 expandableListview 默认可折叠
		mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				return false;
			}
		});
	}

	/**
	 * 获取键盘密码的操作记录
	 */
	private void requestPasscodeOperateRecords() {
		//todo 数据分页
		RestClient.builder()
				.url(Urls.LOCK_PASSCODE_RECORD)
				.loader(this)
				.params("password", mPasscode)
				.params("start", 0)
				.params("limit", 100)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_PASSCODE_RECORD", response);

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
							}
						}
					}
				})
				.build()
				.get();
	}

}
