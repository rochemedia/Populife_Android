package com.populstay.populife.keypwdmanage;

import android.content.Context;
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
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.base.BasePagerAdapter;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.Key;
import com.populstay.populife.entity.Passcode;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.string.StringUtil;

import java.util.ArrayList;
import java.util.List;

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

            }
        });
    }

    private void initView() {
        mLlCreateKeyPwd = findViewById(R.id.ll_create_key_pwd);
        mTabLayout = findViewById(R.id.tl_lock_send_passcode);
        mViewPager = findViewById(R.id.vp_lock_send_passcode);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    protected void initTab() {

        Resources res = getResources();
        String[] titles = new String[]{
                res.getString(R.string.key_pwd_status_not_activated),
                res.getString(R.string.key_pwd_status_available),
                res.getString(R.string.key_pwd_status_invalid)};

        mFragmentList.add(KeyPwdListFragment.newInstance(KeyPwdListFragment.TAB_VAL_NOT_ACTIVATED));//待激活
        mFragmentList.add(KeyPwdListFragment.newInstance(KeyPwdListFragment.TAB_VAL_AVAILABLE));//可使用
        mFragmentList.add(KeyPwdListFragment.newInstance(KeyPwdListFragment.TAB_VAL_INVALID));//已失效

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
     * 获取锁键盘密码列表数据
     */
    private void requestPasscodeList() {
        //todo 数据分页
        RestClient.builder()
                .url(Urls.LOCK_PASSCODE_LIST)
//				.loader(LockManagePasswordActivity.this)
                .params("userId", PeachPreference.readUserId())
                .params("lockId", mKey.getLockId())
                .params("pageNo", 1)
                .params("pageSize", 100)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                       /* if (mRefreshLayout != null) {
                            mRefreshLayout.setRefreshing(false);
                        }*/

                        PeachLogger.d("LOCK_PASSCODE_LIST", response);

                        JSONObject result = JSON.parseObject(response);
                        int code = result.getInteger("code");
                        if (code == 200) {
                            JSONArray dataArray = result.getJSONArray("data");
                            mPasscodeList.clear();
                            if (dataArray != null && !dataArray.isEmpty()) {
                                int size = dataArray.size();
                                for (int i = 0; i < size; i++) {
                                    JSONObject dataObj = dataArray.getJSONObject(i);
                                    Passcode passcode = new Passcode();

                                    passcode.setKeyboardPwdId(dataObj.getInteger("keyboardPwdId"));
                                    passcode.setKeyboardPwd(dataObj.getString("keyboardPwd"));
                                    String alias = dataObj.getString("alias");
                                    passcode.setAlias(StringUtil.isBlank(alias) ? "" : alias);
                                    passcode.setSendUser(dataObj.getString("sendUser"));
                                    passcode.setKeyboardPwdType(dataObj.getInteger("keyboardPwdType"));
                                    Long start = dataObj.getLong("startDate");
                                    if (start != null) {
                                        passcode.setStartDate(start);
                                    } else {
                                        passcode.setStartDate(0);
                                    }
                                    Long end = dataObj.getLong("endDate");
                                    if (end != null) {
                                        passcode.setEndDate(end);
                                    } else {
                                        passcode.setEndDate(0);
                                    }
                                    Long create = dataObj.getLong("createDate");
                                    if (create != null) {
                                        passcode.setCreateDate(create);
                                    } else {
                                        passcode.setCreateDate(0);
                                    }
                                    passcode.setStatus(dataObj.getInteger("status"));

                                    mPasscodeList.add(passcode);
                                }
                                mAdapter.notifyDataSetChanged();
                            } else {
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
                       /* if (mRefreshLayout != null) {
                            mRefreshLayout.setRefreshing(false);
                        }*/
                    }
                })
                .build()
                .post();
    }
}
