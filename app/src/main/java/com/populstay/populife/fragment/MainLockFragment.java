package com.populstay.populife.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.meiqia.core.MQManager;
import com.meiqia.core.bean.MQMessage;
import com.meiqia.core.callback.OnGetMessageListCallback;
import com.meiqia.meiqiasdk.imageloader.MQImage;
import com.meiqia.meiqiasdk.util.MQIntentBuilder;
import com.populstay.populife.R;
import com.populstay.populife.activity.LockAddSelectTypeActivity;
import com.populstay.populife.base.BaseVisibilityFragment;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.LockGroup;
import com.populstay.populife.eventbus.Event;
import com.populstay.populife.home.HomeListActivity;
import com.populstay.populife.home.entity.Home;
import com.populstay.populife.home.entity.HomeDevice;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.permission.PermissionListener;
import com.populstay.populife.ui.MQGlideImageLoader;
import com.populstay.populife.ui.NoScrollViewPager;
import com.populstay.populife.util.CollectionUtil;
import com.populstay.populife.util.GsonUtil;
import com.populstay.populife.util.dialog.DialogUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.string.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 底部导航栏“Lock”Fragment
 * Created by Jerry
 */

public class MainLockFragment extends BaseVisibilityFragment {

	private static final int TAB_LOCK_DETAIL = 0;
	private static final int TAB_LOCK_LIST = 1;

	//当前用户名下锁拥有的 锁/钥匙 数量
	public static int mAccountLockNum = PeachPreference.getAccountLockNum(PeachPreference.readUserId());
	public static int mCurrentFragmentIndex = TAB_LOCK_DETAIL;
	private NoScrollViewPager mViewPager;
	private ImageView mIvAddLock, mIvNewMsg;
	private RelativeLayout mRlOnlineService;
	private TextView mTvSwitchHome;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_lock, null);

		initView(view);
		initListener();
		final String sharePreId = PeachPreference.getShareKeyPreId();
		if (TextUtils.isEmpty(sharePreId)){
			requestLockGroup();
		}else {

			DialogUtil.showCommonDialog(mActivity, null,
					getString(R.string.receive_share_key),
					getString(R.string.ok), getString(R.string.cancel), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							receiveKey(sharePreId);
							PeachPreference.setShareKeyPreId("");
						}
					}, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							PeachPreference.setShareKeyPreId("");
							requestLockGroup();
						}
					});

		}
		return view;
	}

	private void receiveKey(String preId) {
		RestClient.builder()
				.url(Urls.KEY_RECEIVE)
				.loader(getActivity())
				.params("userId", PeachPreference.readUserId())
				.params("preId", preId)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						/*if (mRefreshLayout != null) {
							mRefreshLayout.setRefreshing(false);
						}*/


						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							fromShareHomeId = result.getString("data");

						}
						// 不能领取自己分享的钥匙
						else if (code == 952){
							toast(R.string.cannot_receive_oneself_shared_keys);
						}
						//钥匙已经被领取
						else if (code == 951){
							toast(R.string.the_key_has_been_receive);
						}
						requestLockGroup();
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						requestLockGroup();
						/*if (mRefreshLayout != null) {
							mRefreshLayout.setRefreshing(false);
						}*/
					}
				})
				.error(new IError() {
					@Override
					public void onError(int code, String msg) {
						requestLockGroup();
						/*if (mRefreshLayout != null) {
							mRefreshLayout.setRefreshing(false);
						}*/
					}
				})
				.build()
				.post();
	}



	private String fromShareHomeId;
	private Home currentHome;
	private void requestLockGroup() {
		RestClient.builder()
				.url(Urls.LOCK_GROUP_LIST)
				.loader(getActivity())
				.params("userId", PeachPreference.readUserId())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						/*if (mRefreshLayout != null) {
							mRefreshLayout.setRefreshing(false);
						}*/

						PeachLogger.d("LOCK_GROUP_LIST", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							List<Home> datas = GsonUtil.fromJson(result.getJSONArray("data").toJSONString(),new TypeToken<List<Home>>(){});
							if (!CollectionUtil.isEmpty(datas)){
								currentHome = datas.get(0);
								String lastSelectHomeId = PeachPreference.getLastSelectHomeId();

								if (!TextUtils.isEmpty(fromShareHomeId)){
									lastSelectHomeId = fromShareHomeId;
									PeachPreference.setLastSelectHomeId("");
									PeachPreference.setLastSelectHomeName("");
								}

								for (Home home : datas){
									if (home.getId().equals(lastSelectHomeId)){
										currentHome = home;
										break;
									}
								}

								if (TextUtils.isEmpty(lastSelectHomeId)){
									PeachPreference.setLastSelectHomeId(currentHome.getId());
									PeachPreference.setLastSelectHomeName(currentHome.getName());
								}
								EventBus.getDefault().post(new Event(Event.EventType.GET_HOME_DATA_COMPLETE,currentHome));
							}else {
								HomeListActivity.actionStart(getActivity(), HomeListActivity.VAL_ACTION_TYPE_SWITCH_HOME);
							}
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						/*if (mRefreshLayout != null) {
							mRefreshLayout.setRefreshing(false);
						}*/
					}
				})
				.error(new IError() {
					@Override
					public void onError(int code, String msg) {
						/*if (mRefreshLayout != null) {
							mRefreshLayout.setRefreshing(false);
						}*/
					}
				})
				.build()
				.get();
	}

	@Override
	protected void onVisibilityChanged(boolean visible) {
		super.onVisibilityChanged(visible);
		if (visible) {
			// 刷新子 fragment 页面
			refreshChildFragment();
			getMeiQiaUnreadMsg();
			MQManager.getInstance(getActivity()).closeMeiqiaService();
		} else {
			MQManager.getInstance(getActivity()).openMeiqiaService();
		}
	}

	/**
	 * 获取美洽未读消息
	 */
	private void getMeiQiaUnreadMsg() {
		MQManager.getInstance(getActivity()).getUnreadMessages(new OnGetMessageListCallback() {
			@Override
			public void onSuccess(List<MQMessage> messageList) {
				PeachLogger.d(messageList);
				if (messageList != null && !messageList.isEmpty())
					mIvNewMsg.setVisibility(View.VISIBLE);
				else
					mIvNewMsg.setVisibility(View.INVISIBLE);
			}

			@Override
			public void onFailure(int code, String message) {
			}
		});
	}

	private void refreshChildFragment() {
		int curTabIndex = mViewPager.getCurrentItem();
		ViewPagerAdapter adapter = (ViewPagerAdapter) mViewPager.getAdapter();
		if (adapter != null) {
			if (curTabIndex == 0) {
				LockDetailFragment fragment = (LockDetailFragment) adapter.getItem(0);
				if (fragment != null) {
					fragment.doRefresh();
				}
			} else if (curTabIndex == 1) {
				/*LockListFragment fragment = (LockListFragment) adapter.getItem(1);
				if (fragment != null) {
					fragment.doRefresh();
				}*/
			}
		}
	}

	private void initView(View view) {
		mTvSwitchHome = view.findViewById(R.id.tv_switch_home);
		mIvAddLock = view.findViewById(R.id.iv_main_lock_add);
		mIvNewMsg = view.findViewById(R.id.iv_main_lock_msg_new);
		mRlOnlineService = view.findViewById(R.id.rl_main_lock_online_service);


		mViewPager = view.findViewById(R.id.nsv_main_lock);
		setupViewPager(mViewPager);
		if (mAccountLockNum <= 1) {//没有锁或只有1把锁，显示锁详情页面
			setCurrentTab(TAB_LOCK_DETAIL);
		} else {//拥有多把锁（>=2），显示锁列表页面
			setCurrentTab(TAB_LOCK_LIST);
		}
		showAddLockBtn(mAccountLockNum > 0);
	}

	private void showAddLockBtn(boolean isShow){
		if (null == mIvAddLock){
			return;
		}
		mIvAddLock.setVisibility(isShow ? View.VISIBLE : View.GONE);
	}

	private void initListener() {
		mIvAddLock.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				goToNewActivity(LockAddSelectTypeActivity.class);
			}
		});
		mRlOnlineService.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				requestRuntimePermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						new PermissionListener() {
							@Override
							public void onGranted() {
								HashMap<String, String> clientInfo = new HashMap<>();
								clientInfo.put("userId", PeachPreference.readUserId());
								clientInfo.put("phoneNum", PeachPreference.getStr(PeachPreference.ACCOUNT_PHONE));
								clientInfo.put("email", PeachPreference.getStr(PeachPreference.ACCOUNT_EMAIL));
								MQImage.setImageLoader(new MQGlideImageLoader());
								startActivity(new MQIntentBuilder(getActivity()).
										setCustomizedId(PeachPreference.readUserId())
										.setClientInfo(clientInfo)
										.updateClientInfo(clientInfo)
										.build());
							}

							@Override
							public void onDenied(List<String> deniedPermissions) {
								toast(R.string.note_permission);
							}
						});
			}
		});
		mTvSwitchHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				HomeListActivity.actionStart(getActivity(), HomeListActivity.VAL_ACTION_TYPE_SWITCH_HOME);
			}
		});
	}

	/**
	 * 切换 LockDetailFragment 和 LockListFragment 页面
	 *
	 * @param index 被切换的 index
	 */
	private void setCurrentTab(int index) {
		mCurrentFragmentIndex = index;
		mViewPager.setCurrentItem(index, false);
		refreshChildFragment();
	}

	private void setupViewPager(ViewPager viewPager) {
		ViewPagerAdapter localViewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
		localViewPagerAdapter.addFragment(LockDetailFragment.newInstance(LockDetailFragment.VAL_TAG_FRAGMENT, ""));
		//localViewPagerAdapter.addFragment(new LockListFragment());
		viewPager.setAdapter(localViewPagerAdapter);
	}

	public void showLockDetailFragment() {
		setCurrentTab(TAB_LOCK_DETAIL);
	}

	public void showLockListFragment() {
		setCurrentTab(TAB_LOCK_LIST);
	}

	public class ViewPagerAdapter extends FragmentPagerAdapter {
		private List<Fragment> mListFragment = new ArrayList<>();

		private ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		private void addFragment(Fragment paramFragment) {
			if (mListFragment == null)
				mListFragment = new ArrayList();
			mListFragment.add(paramFragment);
		}

		public int getCount() {
			if (mListFragment == null)
				return 0;
			return mListFragment.size();
		}

		public Fragment getItem(int paramInt) {
			if (mListFragment == null)
				return null;
			return mListFragment.get(paramInt);
		}
	}

	@Override
	public void onEventSub(Event event) {
		super.onEventSub(event);
		switch (event.type){
			case Event.EventType.GET_HOME_DATA_COMPLETE:
			case Event.EventType.CHANGE_HOME:
				String userAccount = PeachPreference.getStr(PeachPreference.ACCOUNT);
				String lastSelectHomeName = PeachPreference.getLastSelectHomeName();
				if (TextUtils.isEmpty(lastSelectHomeName)){
					if (TextUtils.isEmpty(userAccount)){
						mTvSwitchHome.setText(R.string.my_space);
					}else {
						mTvSwitchHome.setText(String.format(getString(R.string.so_space), userAccount));
					}
				}else {
					mTvSwitchHome.setText(lastSelectHomeName);
				}
				break;
		}

	}
}
