package com.populstay.populife.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.reflect.TypeToken;
import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.home.entity.Home;
import com.populstay.populife.home.entity.HomeDevice;
import com.populstay.populife.home.entity.HomeDeviceInfo;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.ui.widget.exedittext.ExEditText;
import com.populstay.populife.util.CollectionUtil;
import com.populstay.populife.util.GsonUtil;
import com.populstay.populife.util.device.KeyboardUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;

import java.util.ArrayList;
import java.util.List;

public class AddDeviceSuccessActivity extends BaseActivity implements View.OnClickListener {

    private OptionsPickerView mPickerHome;
    private TextView mTvHomeName, mTvFinish;
    private List<Home> mHomeList;
    private ExEditText mEtDeviceName;

    private Home mHome;
    private String mDeviceType = HomeDeviceInfo.IDeviceModel.MODEL_LOCK_DEADBOLT;
    private HomeDevice mHomeDevice;
    public static final String KEY_DEVICE_TYPE = "key_device_type";
    public static final String KEY_DEVICE_DATA = "key_device_data";

    public static void actionStart(Context context, String deviceType, HomeDevice device) {
        Intent intent = new Intent(context, AddDeviceSuccessActivity.class);
        intent.putExtra(KEY_DEVICE_TYPE, deviceType);
        intent.putExtra(KEY_DEVICE_DATA, device);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device_success);
        getIntentData();
        initView();
        setListener();
        initPicker();
        requestLockGroup();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        mDeviceType = intent.getStringExtra(KEY_DEVICE_TYPE);
        mHomeDevice = intent.getParcelableExtra(KEY_DEVICE_DATA);
    }

    private void initView() {
        findViewById(R.id.page_action).setVisibility(View.GONE);
        TextView tvPageTitle = findViewById(R.id.page_title);
        tvPageTitle.setText(R.string.add_success);
        mTvHomeName = findViewById(R.id.tv_home_name);
        mEtDeviceName = findViewById(R.id.et_device_name);
        mTvFinish = findViewById(R.id.tv_finish);
        mEtDeviceName.setText(mHomeDevice.getName());
    }

    private void setListener() {
        mTvHomeName.setOnClickListener(this);
        mTvFinish.setOnClickListener(this);
        mEtDeviceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setEnableTvFinishBtn();
            }
        });
    }

    private void setEnableTvFinishBtn(){
        mTvFinish.setEnabled(!TextUtils.isEmpty(mEtDeviceName.getTextStr()) && !TextUtils.isEmpty(mTvHomeName.getText().toString().trim()));
    }

    private void initPicker() {
        mPickerHome = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                if (CollectionUtil.isEmpty(mHomeList)){
                    return;
                }
                Home currentHome = mHomeList.get(options1);
                mTvHomeName.setText(currentHome.getName());
                setEnableTvFinishBtn();
            }
        }).setLayoutRes(R.layout.pickerview_select_home_group, new CustomListener() {
            @Override
            public void customLayout(View v) {
                final TextView tvSubmit = v.findViewById(R.id.tv_finish);
                TextView tvCancel = v.findViewById(R.id.iv_cancel);
                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPickerHome.returnData();
                        mPickerHome.dismiss();
                    }
                });

                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPickerHome.dismiss();
                    }
                });
            }
        }).setLineSpacingMultiplier(2.5F)
                .isDialog(false)
                .build();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tv_home_name:
                mPickerHome.show();
                KeyboardUtil.hideSoftInput(this);
                break;
            case R.id.tv_finish:
                // 锁头
                if (!HomeDeviceInfo.IDeviceModel.MODEL_GATEWAY.equals(mDeviceType)){
                    modifyLockName();
                    bindHome();
                }
                break;
        }

    }

    private void requestLockGroup() {
        RestClient.builder()
                .url(Urls.LOCK_GROUP_LIST)
                .loader(this)
                .params("userId", PeachPreference.readUserId())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject result = JSON.parseObject(response);
                        int code = result.getInteger("code");
                        if (code == 200) {
                            mHomeList = GsonUtil.fromJson(result.getJSONArray("data").toJSONString(), new TypeToken<List<Home>>() {
                            });
                            if (CollectionUtil.isEmpty(mHomeList)){
                                return;
                            }
                            String currentHomeId = PeachPreference.getLastSelectHomeId();
                            List<String> mHomeNames = new ArrayList<>();
                            mHome = mHomeList.get(0);
                           int selectPosition = 0;
                            for (int i = 0, len = mHomeList.size(); i < len; i++) {
                                Home home = mHomeList.get(i);
                                mHomeNames.add(home.getName());
                                if (home.getId().equals(currentHomeId)){
                                    mHome = home;
                                    selectPosition = i;
                                }
                            }

                            mPickerHome.setSelectOptions(selectPosition);
                            mTvHomeName.setText(mHome.getName());
                            mPickerHome.setPicker(mHomeNames);
                            setEnableTvFinishBtn();
                        }
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                    }
                })
                .build()
                .get();
    }

    /**
     * 修改锁名称
     *
     */
    private void modifyLockName() {
        RestClient.builder()
                .url(Urls.LOCK_NAME_MODIFY)
                .loader(this)
                .params("lockId", mHomeDevice.getDeviceId())
                .params("lockAlias", mEtDeviceName.getTextStr())
                .success(new ISuccess() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(String response) {
                        PeachLogger.d("LOCK_NAME_MODIFY", response);

                        JSONObject result = JSON.parseObject(response);
                        int code = result.getInteger("code");
                        if (code == 200) {
                        } else {
                            toast(R.string.note_lock_name_add_fail);
                        }
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        toast(R.string.note_lock_name_add_fail);
                    }
                })
                .build()
                .post();
    }

    /**
     * 绑定家庭
     *
     */
    private void bindHome() {
        RestClient.builder()
                .url(Urls.LOCK_BIND_HOME)
                .loader(this)
                .params("lockId", mHomeDevice.getDeviceId())
                .params("homeId", mHome.getId())
                .success(new ISuccess() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onSuccess(String response) {
                        JSONObject result = JSON.parseObject(response);
                        int code = result.getInteger("code");
                        if (code == 200) {
                            goToNewActivity(MainActivity.class);
                        } else {
                            toast(R.string.note_lock_name_add_fail);
                        }
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        toast(R.string.note_lock_name_add_fail);
                    }
                })
                .build()
                .post();
    }
}
