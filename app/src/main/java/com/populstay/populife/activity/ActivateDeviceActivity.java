package com.populstay.populife.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.home.entity.HomeDeviceInfo;
import com.populstay.populife.permission.PermissionListener;
import com.populstay.populife.ui.MQGlideImageLoader;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.timer.BaseCountDownTimer;
import com.populstay.populife.util.timer.ITimerListener;

import java.util.HashMap;
import java.util.List;

/**
 * 确认设备已经亮起来了
 */
public class ActivateDeviceActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String KEY_LOCK_TYPE = "KEY_LOCK_TYPE";
    private String lockType;
    private ImageView mIvAddDevicePic,mIvNewMsg;
    private TextView mTvPageTitle,mTvShowCountDownTime,mTvNext;
    private BaseCountDownTimer mCountDownTimer;
    private CheckBox mCbConfirmActivateDevice;
    public static final int COUNT_DOWN_MILLIS = 30;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_device);
        initCountDownTimer();
        getIntentData();
        initView();
        setListener();
        initStatus();
    }

    private void initCountDownTimer() {
        mCountDownTimer = new BaseCountDownTimer(COUNT_DOWN_MILLIS, new ITimerListener() {
            @Override
            public void onTimerTick(long secondsLeft) {
                setCountDownTimeText((int) (secondsLeft + 1));
            }

            @Override
            public void onTimerFinish() {
                resetRefreshCountDownTimerUI();
            }
        });
    }

    private void getIntentData() {
        lockType = getIntent().getStringExtra(KEY_LOCK_TYPE);
    }

    private void initView() {
        mIvAddDevicePic = findViewById(R.id.iv_add_device_pic);
        mTvPageTitle = findViewById(R.id.page_title);
        mTvShowCountDownTime = findViewById(R.id.tv_show_count_down_time);
        mTvNext = findViewById(R.id.tv_next);
        mCbConfirmActivateDevice = findViewById(R.id.cb_confirm_activate_device);
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
                PDFActivity.actionStart(ActivateDeviceActivity.this, getString(R.string.user_manual_gateway),
                        "user_manual_gateway.pdf", true);
            }
        });

        mIvNewMsg = findViewById(R.id.iv_main_lock_msg_new);
        findViewById(R.id.rl_main_lock_online_service).setOnClickListener(new View.OnClickListener() {
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
                                startActivity(new MQIntentBuilder(ActivateDeviceActivity.this).
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

    private void setListener() {
        mTvNext.setOnClickListener(this);
        mCbConfirmActivateDevice.setOnCheckedChangeListener(this);
    }

    private void initStatus() {
        if(HomeDeviceInfo.IDeviceName.NAME_LOCK_DEADBOLT.equals(lockType)){
            mIvAddDevicePic.setImageResource(R.drawable.add_deadbolt_icon);
            mTvPageTitle.setText(R.string.lock_add_deadbolt);
        }else if (HomeDeviceInfo.IDeviceName.NAME_LOCK_KEY_BOX.equals(lockType)){
            mIvAddDevicePic.setImageResource(R.drawable.add_keybox_icon);
            mTvPageTitle.setText(R.string.lock_add_key_box);
        }else {
            mTvPageTitle.setText(R.string.lock_add);
        }

        mCountDownTimer.start();
        startRefreshCountDownTimerUI();

    }

    public static void actionStart(Context context, String lockType) {
        Intent intent = new Intent(context, ActivateDeviceActivity.class);
        intent.putExtra(KEY_LOCK_TYPE, lockType);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_next:
                goToNewActivity(FoundDeviceActivity.class);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked){
            mCountDownTimer.start();
            startRefreshCountDownTimerUI();
        }else {
            mCountDownTimer.cancel();
            resetRefreshCountDownTimerUI();
        }
    }

    public void startRefreshCountDownTimerUI(){
        mTvNext.setEnabled(true);
        setCountDownTimeText(COUNT_DOWN_MILLIS);
        mCbConfirmActivateDevice.setChecked(true);
    }

    public void resetRefreshCountDownTimerUI(){
        mTvNext.setEnabled(false);
        setCountDownTimeText(0);
        mCbConfirmActivateDevice.setChecked(false);
    }

    public void setCountDownTimeText(int time){
        String timeStr = time > 9 ? String.valueOf(time) : "0" + time;
        if(null != mTvShowCountDownTime){
            mTvShowCountDownTime.setText(timeStr);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mCountDownTimer){
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }
}
