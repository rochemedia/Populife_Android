package com.populstay.populife.me;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meiqia.meiqiasdk.imageloader.MQImage;
import com.meiqia.meiqiasdk.util.MQIntentBuilder;
import com.populstay.populife.R;
import com.populstay.populife.activity.CommonQuestionActivity;
import com.populstay.populife.activity.CustomerServiceActivity;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.maintservice.MaintServiceActivity;
import com.populstay.populife.permission.PermissionListener;
import com.populstay.populife.ui.MQGlideImageLoader;
import com.populstay.populife.util.storage.PeachPreference;

import java.util.HashMap;
import java.util.List;

public class ServiceSupportActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mLlServiceSupportHelp, mLlServiceSupportMaintain, mLlServiceSupportSendEmail, mLlServiceSupportCustomer;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_support);
        initView();
        setListener();
    }

    private void initView() {
        initTitleBar();
        mLlServiceSupportHelp = findViewById(R.id.ll_service_support_help);
        mLlServiceSupportMaintain = findViewById(R.id.ll_service_support_maintain);
        mLlServiceSupportSendEmail = findViewById(R.id.ll_service_support_send_email);
        mLlServiceSupportCustomer = findViewById(R.id.ll_service_support_customer);
    }

    private void initTitleBar() {
        ((TextView)findViewById(R.id.page_title)).setText(R.string.me_list_item_name_service);
        findViewById(R.id.page_action).setVisibility(View.GONE);
    }

    private void setListener() {
        mLlServiceSupportHelp.setOnClickListener(this);
        mLlServiceSupportMaintain.setOnClickListener(this);
        mLlServiceSupportSendEmail.setOnClickListener(this);
        mLlServiceSupportCustomer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_service_support_help:
                goToNewActivity(CommonQuestionActivity.class);
                break;
            case R.id.ll_service_support_maintain:
                goToNewActivity(MaintServiceActivity.class);
                break;
            case R.id.ll_service_support_send_email:
                sendEmail();
                break;
            case R.id.ll_service_support_customer:
                onlineCustomer();
                break;
        }
    }
    private void onlineCustomer(){
        requestRuntimePermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new PermissionListener() {
                    @Override
                    public void onGranted() {
                        HashMap<String, String> clientInfo = new HashMap<>();
                        clientInfo.put("userId", PeachPreference.readUserId());
                        clientInfo.put("phoneNum", PeachPreference.getStr(PeachPreference.ACCOUNT_PHONE));
                        clientInfo.put("email", PeachPreference.getStr(PeachPreference.ACCOUNT_EMAIL));
                        MQImage.setImageLoader(new MQGlideImageLoader());
                        startActivity(new MQIntentBuilder(ServiceSupportActivity.this).
                                setCustomizedId(PeachPreference.readUserId())
                                .setClientInfo(clientInfo)
                                .updateClientInfo(clientInfo)
                                .build());
                    }

                    @Override
                    public void onDenied(List<String> deniedPermissions) {
                        toast(R.string.note_permission);
                    }
                });
    }

    private void sendEmail() {
        // 创建Intent
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        //设置内容类型
        emailIntent.setType("message/rfc822");
        //设置额外信息
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.customer_service_email)});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
        //启动Activity
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
    }
}
