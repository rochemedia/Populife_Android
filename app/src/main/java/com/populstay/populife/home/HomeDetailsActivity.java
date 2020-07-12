package com.populstay.populife.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.eventbus.Event;
import com.populstay.populife.home.entity.Home;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.util.log.PeachLogger;

import org.greenrobot.eventbus.EventBus;

public class HomeDetailsActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout mLlSpaceName;
    private TextView mTvSpaceName, mTvSpaceDeviceNumName;
    private TextView mTvDeleteSpaceBtn;
    private Home mHome;
    public static final String KEY_TRANSFER_DATA = "key_transfer_data";
    private TextView mPageTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_details);
        getIntentData();
        initView();
        setListener();

    }

    private void getIntentData() {
        mHome = getIntent().getParcelableExtra(KEY_TRANSFER_DATA);
    }

    private void initView() {
        findViewById(R.id.page_action).setVisibility(View.GONE);
        mPageTitle = findViewById(R.id.page_title);
        mPageTitle.setText(R.string.manage_space);
        mLlSpaceName = findViewById(R.id.ll_space_name);
        mTvSpaceName = findViewById(R.id.tv_space_name);
        mTvSpaceDeviceNumName = findViewById(R.id.tv_home_device_num_name);
        mTvDeleteSpaceBtn = findViewById(R.id.tv_delete_space_btn);

        if (null != mHome){
            mTvSpaceDeviceNumName.setText(String.format(getResources().getString(R.string.device_num_match),mHome.getLockCount()));
        }
        setSpaceName();
    }

    public void setSpaceName(){
        if (null != mHome){
            mTvSpaceName.setText(mHome.getName());
        }
    }

    private void setListener() {
        mTvDeleteSpaceBtn.setOnClickListener(this);
        mLlSpaceName.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_space_name:
                changeSpaceName();
                break;
            case R.id.tv_delete_space_btn:
                deleteSpace();
                break;
        }
    }

    private void changeSpaceName(){
        HomeCreateActivity.actionStart(this, HomeCreateActivity.VAL_HOME_CREATE_ACTION_TYPE_RENAME_HOME, mHome);
    }

    private void deleteSpace(){
        deleteGroup();
    }

    private void deleteGroup() {
        RestClient.builder()
                .url(Urls.LOCK_GROUP_DELETE)
                .loader(HomeDetailsActivity.this)
                .params("id", mHome.getId())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        PeachLogger.d("LOCK_GROUP_DELETE", response);

                        JSONObject result = JSON.parseObject(response);
                        int code = result.getInteger("code");
                        if (code != 200) {
                            toast(R.string.note_delete_group_fail);
                        }else {
                            EventBus.getDefault().post(new Event(Event.EventType.DELETE_SPACE));
                            finish();
                        }
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        toast(R.string.note_delete_group_fail);
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        toast(R.string.note_delete_group_fail);
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        toast(R.string.note_delete_group_fail);
                    }
                })
                .build()
                .post();
    }

    public static void actionStart(Context context, Home home) {
        Intent intent = new Intent(context, HomeDetailsActivity.class);
        intent.putExtra(KEY_TRANSFER_DATA, home);
        context.startActivity(intent);
    }

    @Override
    public void onEventSub(Event event) {
        super.onEventSub(event);
        if (Event.EventType.RENAME_SPACE == event.type) {
            if (event.obj instanceof String){
                String spaceName = (String) event.obj;
                mHome.setName(spaceName);
                setSpaceName();
            }

        }
    }

}
