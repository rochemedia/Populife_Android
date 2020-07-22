package com.populstay.populife.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.adapter.DeviceListAdapter;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.home.entity.HomeDevice;
import com.populstay.populife.home.entity.HomeDeviceInfo;

import java.util.ArrayList;
import java.util.List;

public class LockAddSelectTypeActivity extends BaseActivity implements View.OnClickListener {

	private LinearLayout mLlDeadbolt, mLlKeybox, mLlMortise;

	private RecyclerView mDeviceListView;
	private DeviceListAdapter mDeviceListAdapter;
	private List<HomeDevice> mDeviceList;

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
		device.setModelNum(HomeDeviceInfo.IDeviceModel.MODEL_LOCK_DEADBOLT);
		mDeviceList.add(device);

		// 密码锁
		device = new HomeDevice();
		device.setModelNum(HomeDeviceInfo.IDeviceModel.MODEL_LOCK_KEY_BOX);
		mDeviceList.add(device);

		// 网关
		device = new HomeDevice();
		device.setModelNum(HomeDeviceInfo.IDeviceModel.MODEL_GATEWAY);
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
				switch (device.getModelNum()){
					case HomeDeviceInfo.IDeviceModel.MODEL_LOCK_DEADBOLT:
					case HomeDeviceInfo.IDeviceModel.MODEL_LOCK_KEY_BOX:
						LockAddGuideActivity.actionStart(LockAddSelectTypeActivity.this, device.getModelNum());
						//goToNewActivity(LockAddGuideKeyboxOpenActivity.class);
						break;
					case HomeDeviceInfo.IDeviceModel.MODEL_GATEWAY:
						goToNewActivity(GatewayAddGuideActivity.class);
						break;
				}
			}
		});

		initTitleBarRightBtn();
	}

	private void initTitleBarRightBtn() {
		TextView tvSupport = findViewById(R.id.page_action);
		tvSupport.setText("");
		tvSupport.setCompoundDrawablesWithIntrinsicBounds(
				getResources().getDrawable(R.drawable.support_icon), null, null, null);

		tvSupport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
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
