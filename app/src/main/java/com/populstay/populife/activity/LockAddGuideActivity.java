package com.populstay.populife.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.home.entity.HomeDeviceInfo;

public class LockAddGuideActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String KEY_LOCK_TYPE = "KEY_LOCK_TYPE";
    private String lockType;

    private TextView mTvQuestion, mTvNext, mTvPageTitle;
    private CheckBox mCbBtAndLbsOpen, mCbNetOpen, mCkBatteryInstall, mCbKeepOpenDoor, mCbConfirmTime;

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
        mTvQuestion = findViewById(R.id.page_action);
        mTvNext = findViewById(R.id.tv_lock_add_guide_next);

        mCbBtAndLbsOpen = findViewById(R.id.cb_bt_and_lbs_open);
        mCbNetOpen = findViewById(R.id.cb_net_open);
        mCkBatteryInstall = findViewById(R.id.ck_battery_install);
        mCbKeepOpenDoor = findViewById(R.id.cb_keep_open_door);
        mCbConfirmTime = findViewById(R.id.cb_confirm_time);
    }

    private void initListener() {
        mTvQuestion.setOnClickListener(this);
        mTvNext.setOnClickListener(this);
        mCbBtAndLbsOpen.setOnCheckedChangeListener(this);
        mCbNetOpen.setOnCheckedChangeListener(this);
        mCkBatteryInstall.setOnCheckedChangeListener(this);
        mCbKeepOpenDoor.setOnCheckedChangeListener(this);
        mCbConfirmTime.setOnCheckedChangeListener(this);
    }

    private void initStatus() {
        if(HomeDeviceInfo.IDeviceModel.MODEL_LOCK_DEADBOLT.equals(lockType)){
            mTvPageTitle.setText(R.string.lock_add_deadbolt);
        }else if (HomeDeviceInfo.IDeviceModel.MODEL_LOCK_KEY_BOX.equals(lockType)){
            mTvPageTitle.setText(R.string.lock_add_key_box);
        }else {
            mTvPageTitle.setText(R.string.lock_add);
        }

        mTvQuestion.setText("");
        mTvQuestion.setCompoundDrawablesWithIntrinsicBounds(
                getResources().getDrawable(R.drawable.ic_question_mark), null, null, null);

        mCbBtAndLbsOpen.setChecked(isBleEnable());
        mCbNetOpen.setChecked(isNetEnable());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.page_action:
                CommonQuestionDetailActivity.actionStart(LockAddGuideActivity.this, "0", "1");
                break;

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
}
