package com.populstay.populife.keypwdmanage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.activity.LockManageBluetoothKeyActivity;
import com.populstay.populife.activity.LockManagePasswordActivity;
import com.populstay.populife.activity.LockSendEkeyActivity;
import com.populstay.populife.activity.LockSendPasscodeActivity;
import com.populstay.populife.app.MyApplication;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.base.BasePagerAdapter;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.Key;
import com.populstay.populife.entity.Passcode;
import com.populstay.populife.enumtype.Operation;
import com.populstay.populife.eventbus.Event;
import com.populstay.populife.lock.ILockGetOperateLog;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.dialog.DialogUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.string.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.populstay.populife.app.MyApplication.mTTLockAPI;

public class KeyPwdManageActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mLlCreateKeyPwd;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private BasePagerAdapter mAdapter;
    private Key mKey;
    public List<Passcode> mPasscodeList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_pwd_manage);
        getIntentData();
        initTitleBar();
        initView();
        initTab();
        setListener();
    }

    private void getIntentData() {
        mKey = (Key) getIntent().getSerializableExtra("key");
    }

    private void initTitleBar(){
        TextView tvTitle = findViewById(R.id.page_title);
        tvTitle.setText(R.string.keys_and_pwd);
        TextView tvRefresh = findViewById(R.id.page_action);
        tvRefresh.setText("");
        tvRefresh.setCompoundDrawablesWithIntrinsicBounds(
                getResources().getDrawable(R.drawable.refresh_icon), null, null, null);

        tvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showCommonDialog(KeyPwdManageActivity.this,
                        getString(R.string.sync_password_status), getString(R.string.note_sync_password_status),
                        getString(R.string.ok), getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (isBleNetEnable())
                                    // 读取锁密码操作记录
                                    readLockOperateLog();
                            }
                        }, null);
            }
        });
    }

    private void initView() {
        mLlCreateKeyPwd = findViewById(R.id.ll_create_key_pwd);
        mTabLayout = findViewById(R.id.tl_lock_send_passcode);
        mViewPager = findViewById(R.id.vp_lock_send_passcode);
        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    protected void initTab() {

        Resources res = getResources();
        String[] titles = new String[]{
                res.getString(R.string.key_pwd_status_not_activated),
                res.getString(R.string.key_pwd_status_available),
                res.getString(R.string.key_pwd_status_invalid)};

        mFragmentList.add(KeyPwdListFragment.newInstance(KeyPwdConstant.IKeyPwdCategory.KEY_PWD_CATEGORY_NOT_ACTIVATED, mKey));//待激活
        mFragmentList.add(KeyPwdListFragment.newInstance(KeyPwdConstant.IKeyPwdCategory.KEY_PWD_CATEGORY_AVAILABLE, mKey));//可使用
        mFragmentList.add(KeyPwdListFragment.newInstance(KeyPwdConstant.IKeyPwdCategory.KEY_PWD_CATEGORY_INVALID, mKey));//已失效

        mAdapter = new BasePagerAdapter(getSupportFragmentManager(), mFragmentList, titles);
        mViewPager.setAdapter(mAdapter);
    }

    private void setListener() {
        mLlCreateKeyPwd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_create_key_pwd:
                KeyPwdTypeSelectActivity.actionStartForResult(this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode){
            if (KeyPwdTypeSelectActivity.KEY_PWD_TYPE_REQUEST_CODE == requestCode){
                String  keyPwdTypeSelected = data.getStringExtra(KeyPwdTypeSelectActivity.KEY_PWD_TYPE_SELECTED);
                createKeyPwd(keyPwdTypeSelected);
            }
        }
    }

    private void createKeyPwd(String keyPwdTypeSelected) {
        if (KeyPwdConstant.IKeyPwdType.KEY_PWD_TYPE_KEY_BT_KEY.equals(keyPwdTypeSelected)) {
            LockSendEkeyActivity.actionStart(KeyPwdManageActivity.this, mKey.getLockId(), mKey.isAdmin());

        }else  {

            ArrayList<String> passwordList = new ArrayList<>();
            passwordList.add(mKey.getNoKeyPwd());
            for (Passcode passcode : mPasscodeList) {
                passwordList.add(passcode.getKeyboardPwd());
            }

            LockSendPasscodeActivity.actionStart(KeyPwdManageActivity.this, mKey.getLockId(),
                    mKey.getKeyId(), mKey.getLockName(), mKey.getLockMac(), passwordList, keyPwdTypeSelected);
        }
    }

    public static void actionStart(Context context, Key key) {
        Intent intent = new Intent(context, KeyPwdManageActivity.class);
        intent.putExtra("key", key);
        context.startActivity(intent);
    }

    /**
     * 读取锁操作记录
     */
    private void readLockOperateLog() {
        showLoading();
        setReadOperateLogCallback();

        if (mTTLockAPI.isConnected(mKey.getLockMac())) {
            mTTLockAPI.getOperateLog(null, mKey.getLockVersion(),
                    mKey.getAesKeyStr(), DateUtil.getTimeZoneOffset());
        } else {
            mTTLockAPI.connect(mKey.getLockMac());
        }
    }

    private void setReadOperateLogCallback() {
        MyApplication.bleSession.setOperation(Operation.GET_OPERATE_LOG);

        MyApplication.bleSession.setILockGetOperateLog(new ILockGetOperateLog() {
            @Override
            public void onSuccess(final String operateLog) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopLoading();
                        PeachLogger.d(operateLog);
                        uploadLockOperateLog(operateLog);
                    }
                });
            }

            @Override
            public void onFail() {
                stopLoading();
                toastFail();
            }
        });
    }

    /**
     * 上传锁密码操作记录
     */
    private void uploadLockOperateLog(String operateLog) {
        RestClient.builder()
                .url(Urls.LOCK_OPERATE_LOG_KEYBOARD_ADD)
                .loader(this)
                .params("userId", PeachPreference.readUserId())
                .params("lockId", mKey.getLockId())
                .params("records", operateLog)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        EventBus.getDefault().post(new Event(Event.EventType.SYN_PWD_INFO_SUCCESS));
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        toastFail();
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        toastFail();
                    }
                })
                .build()
                .post();
    }
}
