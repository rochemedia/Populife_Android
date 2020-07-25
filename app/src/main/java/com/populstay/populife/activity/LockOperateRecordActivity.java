package com.populstay.populife.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.adapter.LockOperateRecordAdapter;
import com.populstay.populife.app.MyApplication;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.Key;
import com.populstay.populife.entity.LockOperateRecord;
import com.populstay.populife.enumtype.Operation;
import com.populstay.populife.lock.ILockGetOperateLog;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.dialog.DialogUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.string.StringUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.populstay.populife.app.MyApplication.mTTLockAPI;

public class LockOperateRecordActivity extends BaseActivity implements View.OnClickListener,
		ExpandableListView.OnChildClickListener {

	private static final String KEY_LOCK_ID = "key_lock_id";

	private TextView mTvClear, mTvSearch,mIvSync;
	private LinearLayout mLlNoData;
	private ExpandableListView mExpandableListView;
	private LockOperateRecordAdapter mAdapter;
	private SwipeRefreshLayout mRefreshLayout;

	private Key mKey = MyApplication.CURRENT_KEY;
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
		Intent intent = new Intent(context, LockOperateRecordActivity.class);
		intent.putExtra(KEY_LOCK_ID, lockId);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_operate_record);

		getIntentData();
		initView();
		initListener();
		requestLockOperateRecords();
	}

	private void initListener() {
		mTvClear.setOnClickListener(this);
		mTvSearch.setOnClickListener(this);
		mIvSync.setOnClickListener(this);
		mExpandableListView.setOnChildClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.page_action_2:
				DialogUtil.showCommonDialog(LockOperateRecordActivity.this, null,
						getString(R.string.note_clear_records),
						getString(R.string.clear), getString(R.string.cancel), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								clearAllRecords();
							}
						}, null);
				break;

			case R.id.page_action:
				DialogUtil.showCommonDialog(LockOperateRecordActivity.this, getString(R.string.sync_operate_records),
						getString(R.string.note_sync_operate_records), getString(R.string.ok), getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// 读取锁操作记录
								if (isBleNetEnable())
									readLockOperateLog();
							}
						}, null);
				break;

			case R.id.tv_lock_operate_records_search:
				LockOperateRecordSearchActivity.actionStart(LockOperateRecordActivity.this, mLockId);
				break;

			default:
				break;
		}
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, final int childPosition, long id) {
		DialogUtil.showCommonDialog(LockOperateRecordActivity.this, null,
				getString(R.string.note_delete_record),
				getString(R.string.delete), getString(R.string.cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						deleteRecord(groupPosition, childPosition);
					}
				}, null);
		return true;
	}

	private void getIntentData() {
		mLockId = getIntent().getIntExtra(KEY_LOCK_ID, 0);
	}

	private void initView() {
		TextView tvTitle = findViewById(R.id.page_title);
		tvTitle.setText(R.string.lock_action_records);


		mTvClear = findViewById(R.id.page_action_2);
		mTvClear.setText(R.string.clear);
		setClearVisible(false);

		mTvSearch = findViewById(R.id.tv_lock_operate_records_search);
		mIvSync = findViewById(R.id.page_action);
		mIvSync.setText("");
		mIvSync.setCompoundDrawablesWithIntrinsicBounds(
				getResources().getDrawable(R.drawable.refresh_icon), null, null, null);
		mLlNoData = findViewById(R.id.layout_no_data);
		mExpandableListView = findViewById(R.id.eplv_lock_operate_records);
		mAdapter = new LockOperateRecordAdapter(LockOperateRecordActivity.this, mGroupList, mChildList);
		mExpandableListView.setAdapter(mAdapter);
		// 设置 expandableListview 默认可折叠
		mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				return false;
			}
		});

		mRefreshLayout = findViewById(R.id.refresh_layout);
		mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
		mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mRefreshLayout.post(new Runnable() {
					@Override
					public void run() {
						mRefreshLayout.setRefreshing(true);
						requestLockOperateRecords();
					}
				});
			}
		});

		// 普通用户，不显示“同步操作记录”按钮
		if (!mKey.isAdmin() && mKey.getKeyRight() != 1) {
			mIvSync.setVisibility(View.GONE);
		}
	}

	private void setClearVisible(boolean isVisible) {
		if (isVisible) {
			mTvClear.setVisibility(View.VISIBLE);
			mTvClear.setText(R.string.clear);
		} else {
			mTvClear.setText(null);
			mTvClear.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 读取锁操作记录
	 */
	private void readLockOperateLog() {
		showLoading();
		setReadOperateLogCallback();

		if (mTTLockAPI.isConnected(mKey.getLockMac())) {
			mTTLockAPI.getOperateLog(null, mKey.getLockVersion(),
					mKey.getAesKeyStr(), DateUtil.getTimeZoneOffset());
		} else {
			mTTLockAPI.connect(mKey.getLockMac());
		}
	}

	private void setReadOperateLogCallback() {
		MyApplication.bleSession.setOperation(Operation.GET_OPERATE_LOG);

		MyApplication.bleSession.setILockGetOperateLog(new ILockGetOperateLog() {
			@Override
			public void onSuccess(final String operateLog) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						stopLoading();
						PeachLogger.d(operateLog);
						uploadLockOperateLog(operateLog);
					}
				});
			}

			@Override
			public void onFail() {
				stopLoading();
				toastFail();
			}
		});
	}

	/**
	 * 上传锁密码操作记录
	 */
	private void uploadLockOperateLog(String operateLog) {
		RestClient.builder()
				.url(Urls.LOCK_OPERATE_LOG_KEYBOARD_ADD)
				.loader(this)
				.params("userId", PeachPreference.readUserId())
				.params("lockId", mKey.getLockId())
				.params("records", operateLog)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_OPERATE_LOG_KEYBOARD_ADD", response);
						requestLockOperateRecords();
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.operation_fail);
					}
				})
				.error(new IError() {
					@Override
					public void onError(int code, String msg) {
						toast(R.string.operation_fail);
					}
				})
				.build()
				.post();
	}

	/**
	 * 获取锁的操作记录
	 */
	private void requestLockOperateRecords() {
		//todo 数据分页
		RestClient.builder()
				.url(Urls.LOCK_OPERATE_RECORDS_GET)
				.loader(LockOperateRecordActivity.this)
				.params("lockId", mLockId)
				.params("userId", PeachPreference.readUserId())
				.params("start", 0)
				.params("limit", 100)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						if (mRefreshLayout != null) {
							mRefreshLayout.setRefreshing(false);
						}

						PeachLogger.d("LOCK_OPERATE_RECORDS_GET", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							JSONArray dataArray = result.getJSONArray("data");
							mGroupList.clear();
							mChildList.clear();
							if (dataArray != null && !dataArray.isEmpty()) {
								mLlNoData.setVisibility(View.GONE);
//								mTvClear.setVisibility(View.VISIBLE);
								setClearVisible(true);
								int groupSize = dataArray.size();
								for (int i = 0; i < groupSize; i++) {
									JSONObject recordData = dataArray.getJSONObject(i);
									//循环并得到key列表
									for (String key : recordData.keySet()) {
										// 获得 key
										// TODO: 2019-07-04 移到下边的 if 判断里，防止 records 为空
										mGroupList.add(key);
										// 获得 key 对应的 value
										JSONArray records = recordData.getJSONArray(key);
										List<LockOperateRecord> recordList = new ArrayList<>();
										if (records != null && !records.isEmpty()) {
											int childSize = records.size();
											for (int j = 0; j < childSize; j++) {
												JSONObject recordItem = records.getJSONObject(j);
												LockOperateRecord record = new LockOperateRecord();
												record.setId(recordItem.getString("id"));

												String name = recordItem.getString("nickname");
												if (StringUtil.isBlank(name) && recordItem.containsKey("password")) {
													name = recordItem.getString("password");
												}
												record.setNickname(name);

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
//								mTvClear.setVisibility(View.GONE);
								setClearVisible(false);
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
	 * 删除单条操作记录
	 *
	 * @param groupPosition item 所在组元素数据列表（日期）的 index
	 * @param childPosition 子元素数据列表（操作记录）中，item 的 index
	 */
	private void deleteRecord(final int groupPosition, final int childPosition) {
		final String key = mGroupList.get(groupPosition);
		List<LockOperateRecord> recordList = mChildList.get(key);
		LockOperateRecord record = recordList.get(childPosition);
		String recordId = record.getId();
		RestClient.builder()
				.url(Urls.LOCK_OPERATE_RECORDS_DELETE)
				.loader(LockOperateRecordActivity.this)
				.params("id", recordId)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_OPERATE_RECORDS_DELETE", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							mChildList.get(key).remove(childPosition);
							if (mChildList.get(key).isEmpty()) {
								mGroupList.remove(groupPosition);
							}
							mAdapter.notifyDataSetChanged();
							if (mGroupList.isEmpty()) {
//								mTvClear.setVisibility(View.GONE);
								setClearVisible(false);
								mLlNoData.setVisibility(View.VISIBLE);
							}
						} else {
							toast(R.string.note_delete_record_fail);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.note_delete_record_fail);
					}
				})
				.build()
				.post();
	}

	/**
	 * 清空所有操作记录
	 */
	private void clearAllRecords() {
		RestClient.builder()
				.url(Urls.LOCK_OPERATE_RECORDS_CLEAR)
				.loader(LockOperateRecordActivity.this)
				.params("lockId", mLockId)
				.params("userId", PeachPreference.readUserId())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_OPERATE_RECORDS_CLEAR", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							mGroupList.clear();
							mChildList.clear();
							mAdapter.notifyDataSetChanged();
//							mTvClear.setVisibility(View.GONE);
							setClearVisible(false);
							mLlNoData.setVisibility(View.VISIBLE);
						} else {
							toast(R.string.note_clear_records_fail);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.note_clear_records_fail);
					}
				})
				.build()
				.post();
	}
}
