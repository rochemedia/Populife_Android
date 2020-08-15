package com.populstay.populife.activity;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meiqia.core.MQManager;
import com.meiqia.core.bean.MQMessage;
import com.meiqia.core.callback.OnGetMessageListCallback;
import com.meiqia.meiqiasdk.imageloader.MQImage;
import com.meiqia.meiqiasdk.util.MQIntentBuilder;
import com.populstay.populife.R;
import com.populstay.populife.adapter.DeviceListAdapter;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.home.entity.HomeDevice;
import com.populstay.populife.home.entity.HomeDeviceInfo;
import com.populstay.populife.permission.PermissionListener;
import com.populstay.populife.ui.MQGlideImageLoader;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LockAddSelectTypeActivity extends BaseActivity implements View.OnClickListener {

	private LinearLayout mLlDeadbolt, mLlKeybox, mLlMortise;

	private RecyclerView mDeviceListView;
	private DeviceListAdapter mDeviceListAdapter;
	private List<HomeDevice> mDeviceList;
	private ImageView mIvNewMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_add_select_type);
		initData();
		initView();
		initListener();
	}

	private void initData() {
		mDeviceList = new ArrayList<>();

		// 横闩锁
		HomeDevice device = new HomeDevice();
		device.setName(HomeDeviceInfo.IDeviceName.NAME_LOCK_DEADBOLT);
		mDeviceList.add(device);

		// 密码锁
		device = new HomeDevice();
		device.setName(HomeDeviceInfo.IDeviceName.NAME_LOCK_KEY_BOX);
		mDeviceList.add(device);

		// 网关
		device = new HomeDevice();
		device.setName(HomeDeviceInfo.IDeviceName.NAEM_GATEWAY);
		mDeviceList.add(device);

	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.add_device);

		mLlDeadbolt = findViewById(R.id.ll_lock_type_deadbolt);
		mLlKeybox = findViewById(R.id.ll_lock_type_keybox);
		mLlMortise = findViewById(R.id.ll_lock_type_mortise);


		mDeviceListView = findViewById(R.id.device_list_recyclerview);
//		mDeviceListView.setLayoutManager(new GridLayoutManager(this,2));
		mDeviceListView.setLayoutManager(new LinearLayoutManager(this));
		mDeviceListAdapter = new DeviceListAdapter(mDeviceList, this, DeviceListAdapter.SHOW_TYPE_CARD, DeviceListAdapter.USE_FROM_SELECT_DEVICE_TYPE_LIST);
		mDeviceListView.setAdapter(mDeviceListAdapter);
		mDeviceListAdapter.setOnItemClickListener(new DeviceListAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(View v, int position) {
				mDeviceListAdapter.selectItem(position);
				HomeDevice device = mDeviceList.get(position);
				switch (device.getName()){
					case HomeDeviceInfo.IDeviceName.NAME_LOCK_DEADBOLT:
					case HomeDeviceInfo.IDeviceName.NAME_LOCK_KEY_BOX:
						LockAddGuideActivity.actionStart(LockAddSelectTypeActivity.this, device.getName());
						//goToNewActivity(LockAddGuideKeyboxOpenActivity.class);
						break;
					case HomeDeviceInfo.IDeviceName.NAEM_GATEWAY:
						goToNewActivity(GatewayAddGuideActivity.class);
						break;
				}
			}
		});

		initTitleBarRightBtn();
	}

	private void initTitleBarRightBtn() {
		findViewById(R.id.page_action).setVisibility(View.GONE);

		mIvNewMsg = findViewById(R.id.iv_main_lock_msg_new);
		View tvSupport = findViewById(R.id.rl_main_lock_online_service);
		tvSupport.setVisibility(View.VISIBLE);
		tvSupport.setOnClickListener(new View.OnClickListener() {
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
								startActivity(new MQIntentBuilder(LockAddSelectTypeActivity.this).
										setCustomizedId(PeachPreference.readUserId())
										.setClientInfo(clientInfo)
										.updateClientInfo(clientInfo)
										.build());
							}

							@Override
							public void onDenied(List<String> deniedPermissions) {
								toast(R.string.note_permission_avatar);
							}
						});

			}
		});
	}

	/**
	 * 获取美洽未读消息
	 */
	private void getMeiQiaUnreadMsg() {
		MQManager.getInstance(this).getUnreadMessages(new OnGetMessageListCallback() {
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

	@Override
	protected void onResume() {
		super.onResume();
		getMeiQiaUnreadMsg();
	}

	private void initListener() {
		mLlDeadbolt.setOnClickListener(this);
		mLlKeybox.setOnClickListener(this);
		mLlMortise.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.ll_lock_type_deadbolt:
				if (isBleNetEnable()) {
					LockAddGuideActivity.actionStart(LockAddSelectTypeActivity.this, "");
				}
				break;

			case R.id.ll_lock_type_keybox:
				if (isBleNetEnable()) {
					goToNewActivity(LockAddGuideKeyboxOpenActivity.class);
				}
				break;

			case R.id.ll_lock_type_mortise:
				if (isBleNetEnable()) {
					goToNewActivity(FoundDeviceActivity.class);
				}
				break;

			default:
				break;
		}
	}
}
