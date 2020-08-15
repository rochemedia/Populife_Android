//package com.populstay.populife.fragment;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.populstay.populife.R;
//import com.populstay.populife.activity.LockDetailActivity;
//import com.populstay.populife.adapter.LockListAdapter;
//import com.populstay.populife.base.BaseFragment;
//import com.populstay.populife.common.Urls;
//import com.populstay.populife.entity.Key;
//import com.populstay.populife.net.RestClient;
//import com.populstay.populife.net.callback.IError;
//import com.populstay.populife.net.callback.IFailure;
//import com.populstay.populife.net.callback.ISuccess;
//import com.populstay.populife.push.EventPushService;
//import com.populstay.populife.util.date.DateUtil;
//import com.populstay.populife.util.log.PeachLogger;
//import com.populstay.populife.util.storage.PeachPreference;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
///**
// * 锁列表界面
// * Created by Jerry
// */
//
//public class LockListFragment extends BaseFragment {
//
//	private LinearLayout mLlNoData;
//	private SwipeRefreshLayout mRefreshLayout;
//	private ListView mListView;
//	private LockListAdapter mAdapter;
//	private List<Key> mLockList = new ArrayList<>();
//	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			final String action = intent.getAction();
//			if (EventPushService.ACTION_KEY_STATUS_CHANGE.equals(action)) { // 钥匙状态发生变化
//				requestLockList();
//			}
//		}
//	};
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.fragment_lock_list, null);
//
//		initView(view);
//		initListener();
//		registerReceiver();
//		requestLockList();
//		return view;
//	}
//
//	private void registerReceiver() {
//		if (getActivity() != null) {
//			getActivity().registerReceiver(mReceiver, getIntentFilter());
//		}
//	}
//
//	private IntentFilter getIntentFilter() {
//		final IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(EventPushService.ACTION_KEY_STATUS_CHANGE);
//		return intentFilter;
//	}
//
//	private void initView(View view) {
//		mLlNoData = view.findViewById(R.id.layout_no_data);
//		mListView = view.findViewById(R.id.lv_lock_list);
//		mAdapter = new LockListAdapter(getActivity(), mLockList);
//		mListView.setAdapter(mAdapter);
//
//		mRefreshLayout = view.findViewById(R.id.refresh_layout);
//		mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
//		mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//			@Override
//			public void onRefresh() {
//				mRefreshLayout.post(new Runnable() {
//					@Override
//					public void run() {
//						mRefreshLayout.setRefreshing(true);
//						requestLockList();
//					}
//				});
//			}
//		});
//	}
//
//	private void initListener() {
//		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//				LockDetailActivity.actionStart(getActivity(), String.valueOf(mLockList.get(i).getKeyId()));
//			}
//		});
//	}
//
////	@Override
////	protected void onVisibilityChanged(boolean visible) {
////		super.onVisibilityChanged(visible);
////		if (visible) {
////			requestLockList();
////		}
////	}
//
//	/**
//	 * 进行数据刷新操作
//	 */
//	public void doRefresh() {
//		requestLockList();
//	}
//
//	/**
//	 * 获取用户锁列表信息
//	 */
//	public void requestLockList() {
//		//todo 数据分页
//		RestClient.builder()
//				.url(Urls.LOCK_LIST)
////				.loader(getActivity())
//				.params("userId", PeachPreference.readUserId())
//				.params("pageNo", 1)
//				.params("pageSize", 50)
//				.success(new ISuccess() {
//					@Override
//					public void onSuccess(String response) {
//						if (mRefreshLayout != null) {
//							mRefreshLayout.setRefreshing(false);
//						}
//						PeachLogger.d("LOCK_LIST", response);
//						JSONObject result = JSON.parseObject(response);
//						int code = result.getInteger("code");
//						if (code == 200) {
//							JSONArray lockList = result.getJSONArray("data");
//							if (lockList == null || lockList.size() <= 1) { //没有锁或只有1把锁，显示添加锁或锁详情界面
//								if (lockList == null) {
//									PeachPreference.setAccountLockNum(PeachPreference.readUserId(), 0);
//								} else {
//									PeachPreference.setAccountLockNum(PeachPreference.readUserId(), lockList.size());
//								}
//								MainLockFragment fragment = (MainLockFragment) getParentFragment();
//								if (fragment != null) {
//									fragment.showLockDetailFragment();
//								}
//							} else { //有多把锁，显示锁列表界面
//								mLockList.clear();
//								if (mLlNoData != null) {
//									mLlNoData.setVisibility(View.GONE);
//								}
//								int lockNum = lockList.size();
//								PeachPreference.setAccountLockNum(PeachPreference.readUserId(), lockNum);
//								for (int i = 0; i < lockNum; i++) {
//									JSONObject keyItem = lockList.getJSONObject(i);
//									Key key = new Key();
//									key.setLockAlias(keyItem.getString("lockAlias"));
//									key.setKeyId(keyItem.getInteger("keyId"));
//									key.setStartDate(keyItem.getLong("startDate") * 1000);//返回结果单位：秒
//									key.setEndDate(keyItem.getLong("endDate") * 1000);//返回结果单位：秒
//									key.setKeyStatus(keyItem.getString("keyStatus"));
//
//									//(int)有效类型（1限时，2永久，3单次，4循环）
//									key.setKeyType(keyItem.getInteger("type"));
//									//（int）剩余有效天数
//									key.setDayNum(keyItem.getInteger("dayNum"));
//
//									key.setUserType(keyItem.getString("userType"));
//									key.setKeyRight(keyItem.getInteger("keyRight"));
//									key.setElectricQuantity(keyItem.getInteger("electricQuantity"));
//
//									// 普通用户，限时钥匙，判断未到生效时间
//									if (!key.isAdmin() && key.getKeyType() == 1 && key.getStartDate() > DateUtil.getCurTimeMillis()) {
//										key.setKeyStatus("110400"); // 还未到生效时间，设置钥匙状态
//									}
//
//									mLockList.add(key);
//								}
//								mAdapter.notifyDataSetChanged();
//							}
//						}
//					}
//				})
//				.failure(new IFailure() {
//					@Override
//					public void onFailure() {
//						if (mRefreshLayout != null) {
//							mRefreshLayout.setRefreshing(false);
//						}
//					}
//				})
//				.error(new IError() {
//					@Override
//					public void onError(int code, String msg) {
//						if (mRefreshLayout != null) {
//							mRefreshLayout.setRefreshing(false);
//						}
//					}
//				})
//				.build()
//				.post();
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		if (getActivity() != null) {
//			getActivity().unregisterReceiver(mReceiver);
//		}
//	}
//}
