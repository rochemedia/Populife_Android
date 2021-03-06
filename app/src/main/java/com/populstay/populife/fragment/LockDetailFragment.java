package com.populstay.populife.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gcssloop.widget.ArcSeekBar;
import com.populstay.populife.R;
import com.populstay.populife.activity.LockAddSelectTypeActivity;
import com.populstay.populife.activity.LockManageBluetoothKeyActivity;
import com.populstay.populife.activity.LockManageIcCardActivity;
import com.populstay.populife.activity.LockManagePasswordActivity;
import com.populstay.populife.activity.LockSettingsActivity;
import com.populstay.populife.adapter.LockActionAdapter;
import com.populstay.populife.app.MyApplication;
import com.populstay.populife.base.BaseFragment;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.Key;
import com.populstay.populife.entity.LockAction;
import com.populstay.populife.enumtype.Operation;
import com.populstay.populife.lock.ILockGetBattery;
import com.populstay.populife.lock.ILockLock;
import com.populstay.populife.lock.ILockUnlock;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.push.EventPushService;
import com.populstay.populife.ui.MyGridView;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.device.DeviceUtil;
import com.populstay.populife.util.dialog.DialogUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.string.StringUtil;
import com.ttlock.bl.sdk.util.DigitUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import static com.populstay.populife.app.MyApplication.CURRENT_KEY;
import static com.populstay.populife.app.MyApplication.mTTLockAPI;

/**
 * 锁详情操作界面（开闭锁、key、passcode、records、settings）
 * Created by Jerry
 */
public class LockDetailFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

	public static final String VAL_TAG_FRAGMENT = "val_tag_fragment";//显示在 MainLockFragment 中
	public static final String VAL_TAG_ACTIVITY = "val_tag_activity";//显示在 LockDetailActivity 中
	private static final String KEY_KEY_ID = "key_key_id";
	private static final String KEY_TAG = "key_tag";
	private Key mCurKEY;
	private ArcSeekBar mBarUnlocking, mBarUnlock, mBarLock;
	private SwipeRefreshLayout mRefreshLayout;
	private MyGridView mGridView;
	private LockActionAdapter mAdapter;
	private List<LockAction> mActions = new ArrayList<>();
	private TextView mTvLockName, mTvLockStatus;
	private ImageView mIvAddLock, mIvRemoteUnlock, mIvBattery, mIvUnlocking, mIvUnlock, mIvLock;
	private FrameLayout mFlLockInfo;
	private RelativeLayout mRlUnlocking;
	private LinearLayout mLlLockAdd, mLlUnlockLock;
	private int mOpenid;
	private String mTag;
	private String mKeyId;
	private int mKeyType;//钥匙类型（1限时，2永久，3单次，4循环）
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (EventPushService.ACTION_KEY_STATUS_CHANGE.equals(action)) { // 钥匙状态发生变化
				requestUserLockInfo(mKeyId);
			}
		}
	};
	private boolean mIsLockCalled;//闭锁时，onLockSuccess/onLockFail是否被调用过
	private boolean mIsUnlockCalled;//开锁时，onUnlockSuccess/onUnlockFail是否被调用过

	public static LockDetailFragment newInstance(String actionType, String keyId) {

		Bundle args = new Bundle();
		args.putString(KEY_TAG, actionType);
		args.putString(KEY_KEY_ID, keyId);

		LockDetailFragment fragment = new LockDetailFragment();
		fragment.setArguments(args);
		return fragment;
	}

	/**
	 * 开启开闭锁时的旋转动画
	 *
	 * @param operateType 操作类型
	 *                    1 大图标开锁（锁只支持 APP 开锁）
	 *                    2 小图标开锁（锁同时支持 APP 开锁、闭锁）
	 *                    3 小图标闭锁（锁同时支持 APP 开锁、闭锁）
	 */
	private void startLockingAnimation(int operateType) {
		enableLockingColorFiltr(false, false, 0);
		Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_locking);
		LinearInterpolator lir = new LinearInterpolator();
		anim.setInterpolator(lir);
		switch (operateType) {
			case 1:
				mBarUnlocking.setVisibility(View.VISIBLE);
				mBarUnlocking.startAnimation(anim);

				startScaleAnimation(mIvUnlocking);
				startScaleAnimation(mBarUnlocking);
				break;

			case 2:
				mBarUnlock.setVisibility(View.VISIBLE);
				mBarUnlock.startAnimation(anim);

				startScaleAnimation(mIvUnlock);
				startScaleAnimation(mBarUnlock);
				break;

			case 3:
				mBarLock.setVisibility(View.VISIBLE);
				mBarLock.startAnimation(anim);

				startScaleAnimation(mIvLock);
				startScaleAnimation(mBarLock);
				break;

			default:
				break;
		}
	}

	/**
	 * 开启锁图标的缩放动画
	 */
	private void startScaleAnimation(View view) {
		ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.85f);
		ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.85f);
		AnimatorSet set = new AnimatorSet();
		set.setDuration(300);
		set.playTogether(animatorX, animatorY);
		set.start();
	}

	/**
	 * 停止开闭锁时的旋转动画
	 *
	 * @param operateType 操作类型
	 *                    1 大图标开锁（锁只支持 APP 开锁）
	 *                    2 小图标开锁（锁同时支持 APP 开锁、闭锁）
	 *                    3 小图标闭锁（锁同时支持 APP 开锁、闭锁）
	 */
	private void stopLockingAnimation(int operateType) {
		enableLockingColorFiltr(true, false, 0);
		Animation amUnlocking = mBarUnlocking.getAnimation();
		Animation amUnlock = mBarUnlock.getAnimation();
		Animation amLock = mBarLock.getAnimation();
		if (amUnlocking != null)
			mBarUnlocking.clearAnimation();
		if (amUnlock != null)
			mBarUnlock.clearAnimation();
		if (amLock != null)
			mBarLock.clearAnimation();

		switch (operateType) {
			case 1:
				stopScaleAnimation(mIvUnlocking);
				stopScaleAnimation(mBarUnlocking);
				break;

			case 2:
				stopScaleAnimation(mIvUnlock);
				stopScaleAnimation(mBarUnlock);
				break;

			case 3:
				stopScaleAnimation(mIvLock);
				stopScaleAnimation(mBarLock);
				break;

			default:
				break;
		}

		mBarUnlocking.setVisibility(View.GONE);
		mBarUnlock.setVisibility(View.GONE);
		mBarLock.setVisibility(View.GONE);
	}

	/**
	 * 停止锁图标的缩放动画
	 */
	private void stopScaleAnimation(View view) {
		ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, "scaleX", 0.85f, 1.0f, 0.9f, 1.0f);
		ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "scaleY", 0.85f, 1.0f, 0.9f, 1.0f);
		AnimatorSet set = new AnimatorSet();
		set.setDuration(500);
		set.playTogether(animatorX, animatorY);
		set.start();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_lock_detail, null);

		getIntentData();
		initView(view);
		initListener();
		registerReceiver();
		requestUserLockInfo(mKeyId);
		return view;
	}

	private void registerReceiver() {
		if (getActivity() != null) {
			getActivity().registerReceiver(mReceiver, getIntentFilter());
		}
	}

	private IntentFilter getIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(EventPushService.ACTION_KEY_STATUS_CHANGE);
		return intentFilter;
	}

	private void getIntentData() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			mTag = bundle.getString(KEY_TAG);
			mKeyId = bundle.getString(KEY_KEY_ID);
		}
	}

	private void initView(View view) {
		mBarUnlocking = view.findViewById(R.id.arc_seek_bar_unlocking);
		mBarUnlock = view.findViewById(R.id.arc_seek_bar_unlock);
		mBarLock = view.findViewById(R.id.arc_seek_bar_lock);
		mTvLockName = view.findViewById(R.id.tv_lock_detail_name);
		mTvLockStatus = view.findViewById(R.id.tv_lock_detail_status);
		mIvAddLock = view.findViewById(R.id.iv_lock_detail_add);
		mIvRemoteUnlock = view.findViewById(R.id.iv_lock_detail_remote_unlock);
		mIvBattery = view.findViewById(R.id.iv_lock_detail_battery);
		mIvUnlocking = view.findViewById(R.id.iv_lock_detail_unlocking);
		mIvUnlock = view.findViewById(R.id.iv_lock_detail_unlock);
		mIvLock = view.findViewById(R.id.iv_lock_detail_lock);
		mFlLockInfo = view.findViewById(R.id.fl_lock_detail);
		mLlLockAdd = view.findViewById(R.id.ll_lock_detail_add);
		mRlUnlocking = view.findViewById(R.id.rl_lock_detail_unlocking);
		mLlUnlockLock = view.findViewById(R.id.ll_lock_detail_unlock_lock);

		mRefreshLayout = view.findViewById(R.id.refresh_layout);
		mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
		mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mRefreshLayout.post(new Runnable() {
					@Override
					public void run() {
						mRefreshLayout.setRefreshing(true);
						doRefresh();
					}
				});
			}
		});

		mGridView = view.findViewById(R.id.gv_lock_detail_action);
		mAdapter = new LockActionAdapter(getActivity(), mActions);
		mGridView.setAdapter(mAdapter);

		mCurKEY = new Key();
		mOpenid = PeachPreference.getOpenid();
	}

	private void initListener() {
		mGridView.setOnItemClickListener(this);
		mIvAddLock.setOnClickListener(this);
		mIvUnlocking.setOnClickListener(this);
		mIvUnlock.setOnClickListener(this);
		mIvLock.setOnClickListener(this);
		mIvRemoteUnlock.setOnClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
		LockAction action = mActions.get(position);
		boolean isEnable = action.isEnable();
		switch (action.getActionType()) {
			case EKEY_MANAGE:
				if (isEnable) {
					LockManageBluetoothKeyActivity.actionStart(getActivity(), mCurKEY.getLockId(), mCurKEY.isAdmin());
				}
				break;

			case PASSCODE_MANAGE:
				if (isEnable) {
					LockManagePasswordActivity.actionStart(getActivity(), mCurKEY.getLockId(), mCurKEY.getKeyId(),
							mCurKEY.getLockName(), mCurKEY.getLockMac());
				}
				break;

			case IC_CARDS:
				if (isEnable) {
					goToNewActivity(LockManageIcCardActivity.class);
				}
				break;

			case SETTINGS:
				LockSettingsActivity.actionStart(getActivity(), mCurKEY.getLockMac(), mKeyType);
				break;

			default:
				break;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.iv_lock_detail_add:
				goToNewActivity(LockAddSelectTypeActivity.class);
				break;

			case R.id.iv_lock_detail_unlocking:
				// 开锁
				unlock(1);
				break;

			case R.id.iv_lock_detail_unlock:
				// 开锁
				unlock(2);
				break;

			case R.id.iv_lock_detail_lock:
				// 闭锁
				lock();
				break;

			case R.id.iv_lock_detail_remote_unlock:
				//网关远程开锁
				Resources res = getResources();
				DialogUtil.showCommonDialog(getActivity(), null,
						res.getString(R.string.note_unlock_remotely),
						res.getString(R.string.unlock), res.getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								if (isNetEnable())
									remoteUnlock();
							}
						}, null);
				break;

			default:
				break;
		}
	}

	/**
	 * 远程开锁
	 */
	private void remoteUnlock() {
		RestClient.builder()
				.url(Urls.GATEWAY_REMOTE_UNLOCK)
				.loader(getActivity())
				.params("userId", PeachPreference.readUserId())
				.params("lockId", mCurKEY.getLockId())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("GATEWAY_REMOTE_UNLOCK", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							toast(R.string.operation_success);
						} else if (code == 951) {
							toast(R.string.note_gateway_donot_exists);
						} else {
							toast(R.string.operation_fail);
						}
					}
				})
				.build()
				.post();
	}

	/**
	 * 开锁
	 */
	private void unlock(int operateType) {
		if (isBleEnable()) {
			mIsUnlockCalled = false;
			startLockingAnimation(operateType);
			setUnlockCallback(operateType);

			if (mTTLockAPI.isConnected(mCurKEY.getLockMac())) {
				if (mCurKEY.isAdmin())
					mTTLockAPI.unlockByAdministrator(null, mOpenid,
							mCurKEY.getLockVersion(), mCurKEY.getAdminPwd(), mCurKEY.getLockKey(),
							mCurKEY.getLockFlagPos(), System.currentTimeMillis(), mCurKEY.getAesKeyStr(),
							mCurKEY.getTimezoneRawOffset());
				else
					mTTLockAPI.unlockByUser(null, mOpenid, mCurKEY.getLockVersion(),
							mCurKEY.getStartDate(), mCurKEY.getEndDate(), mCurKEY.getLockKey(),
							mCurKEY.getLockFlagPos(), mCurKEY.getAesKeyStr(), mCurKEY.getTimezoneRawOffset());
			} else {
				mTTLockAPI.connect(mCurKEY.getLockMac());
			}
		}
	}

	/**
	 * @param operateType 操作类型
	 *                    1 大图标开锁（锁只支持 APP 开锁）
	 *                    2 小图标开锁（锁同时支持 APP 开锁、闭锁）
	 *                    3 小图标闭锁（锁同时支持 APP 开锁、闭锁）
	 */
	private void setUnlockCallback(final int operateType) {
		MyApplication.bleSession.setOperation(Operation.CLICK_UNLOCK);
		MyApplication.bleSession.setLockmac(mCurKEY.getLockMac());
		MyApplication.bleSession.setAdmin(mCurKEY.isAdmin());

		MyApplication.bleSession.setILockUnlock(new ILockUnlock() {
			@Override
			public void onUnlockSuccess(final int battery) {
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mIsUnlockCalled = true;
							stopLockingAnimation(operateType);

							addLockOperateLog(1);//添加开锁记录
							if (mKeyType == 3) {//如果是一次性钥匙，开锁成功后手动删除
								deleteOneTimeEkey();
							}

							boolean isRemind = PeachPreference.isShowLockingReminder(mCurKEY.getLockMac());
							if (isRemind) {
								// 开锁成功提示
								toast(R.string.unlocked_successfully);
								DeviceUtil.vibrate(getActivity(), 500);
							}

							requestUploadLockBattery(battery);
						}
					});
				}
			}

			@Override
			public void onUnlockFail() {
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mIsUnlockCalled = true;
							stopLockingAnimation(operateType);
							toastFail();
						}
					});
				}
			}

			@Override
			public void onUnlockFinish() {
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (!mIsUnlockCalled) {
								mIsUnlockCalled = true;
								stopLockingAnimation(operateType);
								toast(R.string.note_make_sure_lock_nearby);
							}
						}
					});
				}
			}
		});
	}

	/**
	 * 删除一次性钥匙
	 */
	private void deleteOneTimeEkey() {
		RestClient.builder()
				.url(Urls.LOCK_EKEY_DELETE)
				.params("keyId", mCurKEY.getKeyId())
				.params("userId", PeachPreference.readUserId())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_EKEY_DELETE", response);

						if (VAL_TAG_FRAGMENT.equals(mTag)) {
							requestUserLockInfo(mKeyId);
						} else if (VAL_TAG_ACTIVITY.equals(mTag)) {
							if (getActivity() != null) {
								getActivity().finish();
							}
						}
					}
				})
				.build()
				.post();
	}

	/**
	 * 闭锁
	 */
	private void lock() {
		if (isBleEnable()) {
			mIsLockCalled = false;
			startLockingAnimation(3);
			setLockCallback();

			if (mTTLockAPI.isConnected(mCurKEY.getLockMac())) {
				mTTLockAPI.lock(null, mOpenid, mCurKEY.getLockVersion(), mCurKEY.getStartDate(),
						mCurKEY.getEndDate(), mCurKEY.getLockKey(), mCurKEY.getLockFlagPos(), System.currentTimeMillis(),
						mCurKEY.getAesKeyStr(), mCurKEY.getTimezoneRawOffset());
			} else {
				mTTLockAPI.connect(mCurKEY.getLockMac());
			}
		}
	}

	private void setLockCallback() {
		MyApplication.bleSession.setOperation(Operation.LOCK);
		MyApplication.bleSession.setLockmac(mCurKEY.getLockMac());

		MyApplication.bleSession.setILockLock(new ILockLock() {
			@Override
			public void onLockSuccess(final int battery) {
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mIsLockCalled = true;
							stopLockingAnimation(3);

							addLockOperateLog(2);//添加闭锁记录

							boolean isRemind = PeachPreference.isShowLockingReminder(mCurKEY.getLockMac());
							if (isRemind) {
								// 闭锁成功提示
								toast(R.string.locked_successfully);
								DeviceUtil.vibrate(getActivity(), 500);
							}

							requestUploadLockBattery(battery);
						}
					});
				}
			}

			@Override
			public void onLockFail() {
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mIsLockCalled = true;
							stopLockingAnimation(3);
							toastFail();
						}
					});
				}
			}

			@Override
			public void onLockFinish() {
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (!mIsLockCalled) {
								mIsLockCalled = true;
								stopLockingAnimation(3);
								toast(R.string.note_make_sure_lock_nearby);
							}
						}
					});
				}
			}
		});
	}

	/**
	 * 请求服务器，上传锁电量
	 */
	private void requestUploadLockBattery(final int battery) {
		RestClient.builder()
				.url(Urls.LOCK_UPLOAD_BATTERY)
				.params("lockId", mCurKEY.getLockId())
				.params("electricQuantity", String.valueOf(battery))
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_UPLOAD_BATTERY", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							CURRENT_KEY.setElectricQuantity(battery);
							mCurKEY.setElectricQuantity(battery);
							refreshBattery();
							if (battery <= 20) {
								DialogUtil.showCommonDialog(getActivity(), null,
										getString(R.string.note_low_battery), getString(R.string.ok), null,
										null, null);
							}
						}
					}
				})
				.build()
				.post();
	}

	/**
	 * 上传开闭锁操作记录
	 *
	 * @param eventType 事件
	 *                  1：开锁
	 *                  2：闭锁
	 *                  4：IC 卡开锁
	 */
	private void addLockOperateLog(int eventType) {
		RestClient.builder()
				.url(Urls.LOCK_OPERATE_APP_LOG_ADD)
				.params("userId", PeachPreference.readUserId())
				.params("lockId", mCurKEY.getLockId())
				.params("event", eventType)
				.params("keyId", mCurKEY.getKeyId())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_OPERATE_APP_LOG_ADD", response);
					}
				})
				.build()
				.post();
	}

	/**
	 * 进行数据刷新操作
	 */
	public void doRefresh() {
		requestUserLockInfo(mKeyId);
//		getLockBattery();
	}

	/**
	 * 通过 SDK 读取锁电量
	 */
	private void getLockBattery() {
		if (mCurKEY != null) {
			if (mTTLockAPI.isConnected(mCurKEY.getLockMac())) {
				setGetBatteryCallback();
				mTTLockAPI.getElectricQuantity(null, mCurKEY.getLockVersion(), mCurKEY.getAesKeyStr());
			} else {
				setGetBatteryCallback();
				mTTLockAPI.connect(mCurKEY.getLockMac());
			}
		}
	}

	private void setGetBatteryCallback() {
		MyApplication.bleSession.setOperation(Operation.GET_LOCK_BATTERY);
		MyApplication.bleSession.setLockmac(mCurKEY.getLockMac());

		MyApplication.bleSession.setILockGetBattery(new ILockGetBattery() {
			@Override
			public void onGetBatterySuccess(final int battery) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						requestUploadLockBattery(battery);
					}
				});
			}

			@Override
			public void onGetBatteryFail() {

			}
		});
	}

	/**
	 * 登录成功后调用，查询用户拥有的锁及附属的相关信息
	 */
	public void requestUserLockInfo(String keyId) {
		WeakHashMap<String, Object> params = new WeakHashMap<>();
		params.put("userId", PeachPreference.readUserId());
		if (!StringUtil.isBlank(keyId)) {
			params.put("keyId", keyId);
		}
		RestClient.builder()
				.url(Urls.USER_LOCK_INFO)
				.params(params)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						if (mRefreshLayout != null) {
							mRefreshLayout.setRefreshing(false);
						}
						PeachLogger.d("USER_LOCK_INFO", response);
						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 920) {//没有锁信息
							PeachPreference.setAccountLockNum(PeachPreference.readUserId(), 0);
							setLockInfoVisible(false);
						} else if (code == 200) {
							JSONObject lockInfo = result.getJSONObject("data");
							if (lockInfo != null && !lockInfo.isEmpty()) { //有锁信息，显示对应的锁UI或锁列表UI
								int lockNum = lockInfo.getInteger("lockNum");
								PeachPreference.setAccountLockNum(PeachPreference.readUserId(), lockNum);
								if (VAL_TAG_FRAGMENT.equals(mTag)) {
									if (lockNum <= 0) { //锁数量为0，显示添加锁UI
										setLockInfoVisible(false);
									} else if (lockNum == 1) { //锁数量为1，获取锁详细信息，并显示锁详情UI
										setLockInfoVisible(true);
										parseLockInfo(lockInfo);
									} else { //锁数量>=2，显示锁列表UI
										MainLockFragment fragment = (MainLockFragment) getParentFragment();
										if (fragment != null) {
											fragment.showLockListFragment();
										}
									}
								} else if (VAL_TAG_ACTIVITY.equals(mTag)) {
									if (lockNum <= 0) { //锁数量为0，关闭当前 activity
										if (getActivity() != null) {
											getActivity().finish();
										}
									} else { //锁数量为1，获取锁详细信息，并显示锁详情UI
										setLockInfoVisible(true);
										parseLockInfo(lockInfo);
									}
								}
							} else { //无锁信息，显示添加锁UI
								PeachPreference.setAccountLockNum(PeachPreference.readUserId(), 0);
								setLockInfoVisible(false);
							}
//							PeachPreference.saveUserLockInfo(String.valueOf(lockInfo));
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

	private void setLockInfoVisible(boolean hasLockInfo) {
		if (hasLockInfo) {
			if (mLlLockAdd != null)
				mLlLockAdd.setVisibility(View.GONE);
			if (mFlLockInfo != null)
				mFlLockInfo.setVisibility(View.VISIBLE);
		} else {
			if (mLlLockAdd != null)
				mLlLockAdd.setVisibility(View.VISIBLE);
			if (mFlLockInfo != null)
				mFlLockInfo.setVisibility(View.GONE);
		}
	}

	private void parseLockInfo(@NonNull JSONObject lockInfo) {
		mKeyType = lockInfo.getInteger("keyType");
		String userType = lockInfo.getString("userType");
		String keyStatus = lockInfo.getString("keyStatus");
		int lockId = lockInfo.getInteger("lockId");
		int keyId = lockInfo.getInteger("keyId");
		String lockVersion = String.valueOf(lockInfo.getJSONObject("lockVersion"));
		String lockName = lockInfo.getString("name");
		String lockAlias = lockInfo.getString("alias");
		String lockMac = lockInfo.getString("mac");
		int electricQuantity = lockInfo.getInteger("electricQuantity");
		int lockFlagPos = lockInfo.getInteger("flagPos");
		String adminPwd = "";
		if (lockInfo.containsKey("adminPwd"))
			adminPwd = lockInfo.getString("adminPwd");
		String lockKey = lockInfo.getString("key");
		String noKeyPwd = "";
		if (lockInfo.containsKey("noKeyPwd"))
			noKeyPwd = lockInfo.getString("noKeyPwd");
		String deletePwd = "";
		if (lockInfo.containsKey("deletePwd"))
			deletePwd = lockInfo.getString("deletePwd");
		String pwdInfo = lockInfo.getString("pwdInfo");
		long timestamp = lockInfo.getLong("timestamp");
		String aesKeyStr = lockInfo.getString("aesKey");
		long startDate = lockInfo.getLong("startDate") * 1000;
		long endDate = lockInfo.getLong("endDate") * 1000;
		int specialValue = lockInfo.getInteger("specialValue");
		int timezoneRawOffset = lockInfo.getInteger("timezoneRawOffSet");
		int keyRight = lockInfo.getInteger("keyRight");
//		int remoteEnable = lockInfo.getInteger("remoteEnable");
//		int keyboardPwdVersion=lockInfo.getInteger("keyboardPwdVersion");
		boolean isAllowRemoteUnlock = false;
		if (lockInfo.containsKey("allowRemoteUnlock"))
			isAllowRemoteUnlock = lockInfo.getBoolean("allowRemoteUnlock");
//		String remarks=lockInfo.getString();
		String modelNum = lockInfo.getString("modelNum");
		String hardwareRevision = lockInfo.getString("hardwareRevision");
		String firmwareRevision = lockInfo.getString("firmwareRevision");
		String group = lockInfo.getString("homeName");

		mCurKEY.setUserId(PeachPreference.readUserId());

		mCurKEY.setUserType(userType);
		mCurKEY.setKeyStatus(keyStatus);
		mCurKEY.setLockId(lockId);
		mCurKEY.setKeyId(keyId);
		mCurKEY.setLockVersion(lockVersion);
		mCurKEY.setLockName(lockName);
		mCurKEY.setLockAlias(lockAlias);
		mCurKEY.setLockMac(lockMac);
		mCurKEY.setElectricQuantity(electricQuantity);
		mCurKEY.setLockFlagPos(lockFlagPos);
		mCurKEY.setAdminPwd(adminPwd);
		mCurKEY.setLockKey(lockKey);
		mCurKEY.setNoKeyPwd(noKeyPwd);
		mCurKEY.setDeletePwd(deletePwd);
		mCurKEY.setPwdInfo(pwdInfo);
		mCurKEY.setTimestamp(timestamp);
		mCurKEY.setAesKeyStr(aesKeyStr);
		mCurKEY.setStartDate(startDate);
		mCurKEY.setEndDate(endDate);
		mCurKEY.setSpecialValue(specialValue);
		mCurKEY.setTimezoneRawOffset(timezoneRawOffset);
		mCurKEY.setKeyRight(keyRight);
//		mCurKEY.setRemoteEnable(remoteEnable);
		mCurKEY.setModelNum(modelNum);
		mCurKEY.setHardwareRevision(hardwareRevision);
		mCurKEY.setFirmwareRevision(firmwareRevision);
		mCurKEY.setRemarks(group);//锁分组
		mCurKEY.setAllowRemoteUnlock(isAllowRemoteUnlock);

		CURRENT_KEY = mCurKEY;

		refreshLockActionUI();
	}

	/**
	 * 初始化锁的操作按钮界面
	 */
	private void refreshLockActionUI() {
		refreshBattery();

		mTvLockName.setText(mCurKEY.getLockAlias());

		// 远程开锁图标（仅限管理员远程开锁）
		boolean isSupportRemoteUnlock = DigitUtil.isSupportRemoteUnlock(mCurKEY.getSpecialValue());
		mIvRemoteUnlock.setVisibility(mCurKEY.isAdmin() && isSupportRemoteUnlock ? View.VISIBLE : View.GONE);
//		if (isSupportRemoteUnlock) {
//			if (mCurKEY.isAdmin()) {
//				mIvRemoteUnlock.setVisibility(View.VISIBLE);
//			} else {
//				if (mCurKEY.getKeyRight() == 1 && mCurKEY.isAllowRemoteUnlock()//授权用户，且允许远程开锁
//						&& ("110401".equals(mCurKEY.getKeyStatus()) || "110402".equals(mCurKEY.getKeyStatus()))) {//钥匙正常使用或待接收
//					mIvRemoteUnlock.setVisibility(View.VISIBLE);
//				} else {
//					mIvRemoteUnlock.setVisibility(View.INVISIBLE);
//				}
//			}
//		} else {
//			mIvRemoteUnlock.setVisibility(View.INVISIBLE);
//		}

		mActions.clear();
		if (mCurKEY.isAdmin()) {//判断是否为管理员
			//管理员，显示全部按钮
			initAdminUI();
		} else {
			// 判断钥匙未到生效时间
			if (mCurKEY.getStartDate() > DateUtil.getCurTimeMillis()) {
				mCurKEY.setKeyStatus("110400"); // 还未到生效时间，设置钥匙状态
			}
			if (mCurKEY.getKeyRight() == 1) {//判断普通用户是否被授权
				//授权用户，显示全部按钮
				initAuthUserUI(mCurKEY.getKeyStatus());
			} else {//普通用户，只显示 设置 按钮
				initCommonUserUI(mCurKEY.getKeyStatus());
			}
		}

		if (mActions.size() == 4) {
			mGridView.setNumColumns(4);
		} else {
			mGridView.setNumColumns(3);
		}

		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 刷新锁电量显示
	 */
	private void refreshBattery() {
		int batteryLevel = (mCurKEY.getElectricQuantity() - 1) / 20;
		int imgResInt = R.drawable.ic_battery_100;
		switch (batteryLevel) {
			case 0:
				imgResInt = R.drawable.ic_battery_20;
				break;

			case 1:
				imgResInt = R.drawable.ic_battery_40;
				break;

			case 2:
				imgResInt = R.drawable.ic_battery_60;
				break;

			case 3:
				imgResInt = R.drawable.ic_battery_80;
				break;

			case 4:
				imgResInt = R.drawable.ic_battery_100;
				break;

			default:
				break;
		}
		mIvBattery.setImageResource(imgResInt);
	}

	private void initAdminUI() {
		mTvLockStatus.setVisibility(View.INVISIBLE);

		setUnlockLock();

		enableLockingColorFiltr(true, false, 0);

		mActions.add(new LockAction(LockAction.LockActionType.EKEY_MANAGE,
				R.drawable.ic_lock_action_ekey_manage_able, R.string.bluetooth_keys, true));
		mActions.add(new LockAction(LockAction.LockActionType.PASSCODE_MANAGE,
				R.drawable.ic_lock_action_password_manage_able, R.string.passcode_management, true));
		if (DigitUtil.isSupportIC(mCurKEY.getSpecialValue())) {//支持 IC 卡
			mActions.add(new LockAction(LockAction.LockActionType.IC_CARDS,
					R.drawable.ic_lock_action_ic_card_able, R.string.lock_action_ic_cards, true));
		}
		mActions.add(new LockAction(LockAction.LockActionType.SETTINGS,
				R.drawable.ic_lock_action_setting_able, R.string.lock_action_settings, true));
	}

	/**
	 * 判断是否支持 开锁 & 闭锁
	 */
	private void setUnlockLock() {
		if (DigitUtil.isSupportManualLock(mCurKEY.getSpecialValue())) { // 同时支持 APP 开锁、闭锁
			mRlUnlocking.setVisibility(View.GONE);
			mLlUnlockLock.setVisibility(View.VISIBLE);
		} else { // 只支持 APP 开锁
			mRlUnlocking.setVisibility(View.VISIBLE);
			mLlUnlockLock.setVisibility(View.GONE);
		}
	}

	/**
	 * 激活/禁用 开闭锁图标，同时设置图标颜色
	 *
	 * @param isEnable       是否激活开闭锁图标
	 * @param setColorFilter 是否设置图标颜色
	 * @param color          图标颜色
	 */
	private void enableLockingColorFiltr(boolean isEnable, boolean setColorFilter, int color) {
		mIvUnlocking.setEnabled(isEnable);
		mIvUnlock.setEnabled(isEnable);
		mIvLock.setEnabled(isEnable);
		if (setColorFilter) {
			mIvUnlocking.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
			mIvUnlock.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
			mIvLock.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
		} else {
			mIvUnlocking.setColorFilter(null);
			mIvUnlock.setColorFilter(null);
			mIvLock.setColorFilter(null);
		}
	}

	/**
	 * @param keyStatus //钥匙状态
	 *                  110401：正常使用
	 *                  110402：待接收
	 *                  110405：已冻结
	 *                  110408：已删除
	 *                  110410：已重置
	 *                  110500：过期
	 */
	private void initAuthUserUI(String keyStatus) {
		Resources res = getResources();
		int colorGray = res.getColor(R.color.text_gray_light);
		int colorGrayParent = res.getColor(R.color.gray_lock_disable);
		switch (keyStatus) {
			case "110400": // 还未到生效时间
				mTvLockStatus.setVisibility(View.VISIBLE);
				mTvLockStatus.setTextColor(colorGray);
				mTvLockStatus.setText(R.string.note_key_not_become_valid);

				setUnlockLock();

				enableLockingColorFiltr(false, true, colorGrayParent);

				mActions.add(new LockAction(LockAction.LockActionType.EKEY_MANAGE,
						R.drawable.ic_lock_action_ekey_manage_disable, R.string.bluetooth_keys, false));
				mActions.add(new LockAction(LockAction.LockActionType.PASSCODE_MANAGE,
						R.drawable.ic_lock_action_password_manage_disable, R.string.passcode_management, false));
				if (DigitUtil.isSupportIC(mCurKEY.getSpecialValue())) {//支持 IC 卡
					mActions.add(new LockAction(LockAction.LockActionType.IC_CARDS,
							R.drawable.ic_lock_action_ic_card_disable, R.string.lock_action_ic_cards, false));
				}
				mActions.add(new LockAction(LockAction.LockActionType.SETTINGS,
						R.drawable.ic_lock_action_setting_able, R.string.lock_action_settings, true));
				break;

			case "110401"://正常使用
			case "110402"://待接收
				mTvLockStatus.setVisibility(View.INVISIBLE);

				setUnlockLock();

				enableLockingColorFiltr(true, false, 0);

				mActions.add(new LockAction(LockAction.LockActionType.EKEY_MANAGE,
						R.drawable.ic_lock_action_ekey_manage_able, R.string.bluetooth_keys, true));
				mActions.add(new LockAction(LockAction.LockActionType.PASSCODE_MANAGE,
						R.drawable.ic_lock_action_password_manage_able, R.string.passcode_management, true));
				if (DigitUtil.isSupportIC(mCurKEY.getSpecialValue())) {//支持 IC 卡
					mActions.add(new LockAction(LockAction.LockActionType.IC_CARDS,
							R.drawable.ic_lock_action_ic_card_able, R.string.lock_action_ic_cards, true));
				}
				mActions.add(new LockAction(LockAction.LockActionType.SETTINGS,
						R.drawable.ic_lock_action_setting_able, R.string.lock_action_settings, true));
				break;

			case "110405"://已冻结
				mTvLockStatus.setVisibility(View.VISIBLE);
				mTvLockStatus.setTextColor(colorGray);
				mTvLockStatus.setText(R.string.note_key_frozen);

				setUnlockLock();

				enableLockingColorFiltr(false, true, colorGrayParent);

				mActions.add(new LockAction(LockAction.LockActionType.EKEY_MANAGE,
						R.drawable.ic_lock_action_ekey_manage_disable, R.string.bluetooth_keys, false));
				mActions.add(new LockAction(LockAction.LockActionType.PASSCODE_MANAGE,
						R.drawable.ic_lock_action_password_manage_disable, R.string.passcode_management, false));
				if (DigitUtil.isSupportIC(mCurKEY.getSpecialValue())) {//支持 IC 卡
					mActions.add(new LockAction(LockAction.LockActionType.IC_CARDS,
							R.drawable.ic_lock_action_ic_card_disable, R.string.lock_action_ic_cards, false));
				}
				mActions.add(new LockAction(LockAction.LockActionType.SETTINGS,
						R.drawable.ic_lock_action_setting_able, R.string.lock_action_settings, true));
				break;

			case "110408"://已删除
			case "110410"://已重置
				setLockInfoVisible(false);
				break;

			case "110500"://已过期
				mTvLockStatus.setVisibility(View.VISIBLE);
				mTvLockStatus.setTextColor(colorGray);
				mTvLockStatus.setText(R.string.note_key_expired);

				setUnlockLock();

				enableLockingColorFiltr(false, true, colorGrayParent);

				mActions.add(new LockAction(LockAction.LockActionType.EKEY_MANAGE,
						R.drawable.ic_lock_action_ekey_manage_disable, R.string.bluetooth_keys, false));
				mActions.add(new LockAction(LockAction.LockActionType.PASSCODE_MANAGE,
						R.drawable.ic_lock_action_password_manage_disable, R.string.passcode_management, false));
				if (DigitUtil.isSupportIC(mCurKEY.getSpecialValue())) {//支持 IC 卡
					mActions.add(new LockAction(LockAction.LockActionType.IC_CARDS,
							R.drawable.ic_lock_action_ic_card_disable, R.string.lock_action_ic_cards, false));
				}
				mActions.add(new LockAction(LockAction.LockActionType.SETTINGS,
						R.drawable.ic_lock_action_setting_able, R.string.lock_action_settings, true));
				break;

			default:
				break;
		}
	}

	/**
	 * @param keyStatus //钥匙状态
	 *                  110401：正常使用
	 *                  110402：待接收
	 *                  110405：已冻结
	 *                  110408：已删除
	 *                  110410：已重置
	 *                  110500：过期
	 */
	private void initCommonUserUI(String keyStatus) {
		mActions.add(new LockAction(LockAction.LockActionType.SETTINGS,
				R.drawable.ic_lock_action_setting_able, R.string.lock_action_settings, true));

		Resources res = getResources();
		int colorGray = res.getColor(R.color.text_gray_light);
		int colorGrayParent = res.getColor(R.color.gray_lock_disable);

		switch (keyStatus) {
			case "110400": // 还未到生效时间
				mTvLockStatus.setVisibility(View.VISIBLE);
				mTvLockStatus.setTextColor(colorGray);
				mTvLockStatus.setText(R.string.note_key_not_become_valid);

				setUnlockLock();

				enableLockingColorFiltr(false, true, colorGrayParent);
				break;

			case "110401"://正常使用
			case "110402"://待接收
				mTvLockStatus.setVisibility(View.INVISIBLE);

				setUnlockLock();

				enableLockingColorFiltr(true, false, 0);
				break;

			case "110405"://已冻结
				mTvLockStatus.setVisibility(View.VISIBLE);
				mTvLockStatus.setTextColor(colorGray);
				mTvLockStatus.setText(R.string.note_key_frozen);

				setUnlockLock();

				enableLockingColorFiltr(false, true, colorGrayParent);

				mIvUnlocking.setEnabled(false);
				mIvUnlock.setEnabled(false);
				mIvLock.setEnabled(false);
				break;

			case "110408"://已删除
			case "110410"://已重置
				setLockInfoVisible(false);
				break;

			case "110500"://已过期
				mTvLockStatus.setVisibility(View.VISIBLE);
				mTvLockStatus.setTextColor(colorGray);
				mTvLockStatus.setText(R.string.note_key_expired);

				setUnlockLock();

				enableLockingColorFiltr(false, true, colorGrayParent);
				break;

			default:
				break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (getActivity() != null) {
			getActivity().unregisterReceiver(mReceiver);
		}
	}
}
