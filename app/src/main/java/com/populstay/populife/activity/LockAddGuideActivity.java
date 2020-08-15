package com.populstay.populife.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.meiqia.core.MQManager;
import com.meiqia.core.bean.MQMessage;
import com.meiqia.core.callback.OnGetMessageListCallback;
import com.meiqia.meiqiasdk.imageloader.MQImage;
import com.meiqia.meiqiasdk.util.MQIntentBuilder;
import com.populstay.populife.R;
import com.populstay.populife.base.BluetoothBaseActivity;
import com.populstay.populife.home.entity.HomeDeviceInfo;
import com.populstay.populife.permission.PermissionListener;
import com.populstay.populife.ui.MQGlideImageLoader;
import com.populstay.populife.ui.widget.HelpPopupWindow;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;

import java.util.HashMap;
import java.util.List;

public class LockAddGuideActivity extends BluetoothBaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String KEY_LOCK_TYPE = "KEY_LOCK_TYPE";
    private String lockType;

    private TextView  mTvNext, mTvPageTitle;
    private CheckBox mCbBtAndLbsOpen, mCbNetOpen, mCkBatteryInstall, mCbKeepOpenDoor, mCbConfirmTime;

    private HelpPopupWindow mHelpPopupWindow;
    private ImageView mIvNewMsg;

    public static void actionStart(Context context, String lockType) {
        Intent intent = new Intent(context, LockAddGuideActivity.class);
        intent.putExtra(KEY_LOCK_TYPE, lockType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_add_guide);
        getIntentData();
        initView();
        initListener();
        initStatus();
    }

    private void getIntentData() {
        lockType = getIntent().getStringExtra(KEY_LOCK_TYPE);
    }

    private void initView() {
        mTvPageTitle = findViewById(R.id.page_title);
        mTvNext = findViewById(R.id.tv_lock_add_guide_next);

        mCbBtAndLbsOpen = findViewById(R.id.cb_bt_and_lbs_open);
        mCbNetOpen = findViewById(R.id.cb_net_open);
        mCkBatteryInstall = findViewById(R.id.ck_battery_install);
        mCbKeepOpenDoor = findViewById(R.id.cb_keep_open_door);
        mCbConfirmTime = findViewById(R.id.cb_confirm_time);
        initTitleBarRightBtn();
    }

    private void initTitleBarRightBtn() {
        TextView tvQuestion = findViewById(R.id.page_action);
        tvQuestion.setText("");
        tvQuestion.setCompoundDrawablesWithIntrinsicBounds(
                getResources().getDrawable(R.drawable.help_icon), null, null, null);

        tvQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CommonQuestionDetailActivity.actionStart(LockAddGuideActivity.this, "0", "1");
                showHelpPopupWindow(v);
            }
        });

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
                                startActivity(new MQIntentBuilder(LockAddGuideActivity.this).
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

    private void showHelpPopupWindow(View anchor){
        if (null == mHelpPopupWindow){
            mHelpPopupWindow = new HelpPopupWindow(this);
        }
        mHelpPopupWindow.show(anchor, Gravity.RIGHT);
    }

    private void initListener() {
        mTvNext.setOnClickListener(this);
        mCbBtAndLbsOpen.setOnCheckedChangeListener(this);
        mCbNetOpen.setOnCheckedChangeListener(this);
        mCkBatteryInstall.setOnCheckedChangeListener(this);
        mCbKeepOpenDoor.setOnCheckedChangeListener(this);
        mCbConfirmTime.setOnCheckedChangeListener(this);
    }

    private void initStatus() {
        if(HomeDeviceInfo.IDeviceName.NAME_LOCK_DEADBOLT.equals(lockType)){
            mTvPageTitle.setText(R.string.lock_add_deadbolt);
        }else if (HomeDeviceInfo.IDeviceName.NAME_LOCK_KEY_BOX.equals(lockType)){
            mTvPageTitle.setText(R.string.lock_add_key_box);
        }else {
            mTvPageTitle.setText(R.string.lock_add);
        }

        mCbBtAndLbsOpen.setChecked(isBleEnable());
        mCbNetOpen.setChecked(isNetEnable());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_lock_add_guide_next:
                if (isBleNetEnable()) {
                    ActivateDeviceActivity.actionStart(this,lockType);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    	boolean nextEnable = mCbBtAndLbsOpen.isChecked()
				&& mCbNetOpen.isChecked()
				&& mCkBatteryInstall.isChecked()
				&& mCbKeepOpenDoor.isChecked()
				&& mCbConfirmTime.isChecked();
		mTvNext.setEnabled(nextEnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mHelpPopupWindow){
            mHelpPopupWindow.dismiss();
        }
    }

    @Override
    public void onBluetoothStateChanged(boolean isOpen) {

    }

    @Override
    public void onLocationStateChanged(boolean isOpen) {

    }

    @Override
    public void onNetStateChange(boolean isNetEnable) {

    }
}
