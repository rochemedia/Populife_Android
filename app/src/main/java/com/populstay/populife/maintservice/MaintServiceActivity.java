package com.populstay.populife.maintservice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.ui.widget.extextview.ExTextView;
import com.populstay.populife.util.DensityUtils;

public class MaintServiceActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvMaintenanceRequestBtn;
    private LinearLayout mLlMaintenanceProgressEnquiry, mLlCheckWarrantyStatus, mLlBuyPopucareService;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maint_service);
        initView();
        setListener();
    }

    private void initView() {

        initTitleBar();

        mTvMaintenanceRequestBtn = findViewById(R.id.tv_maintenance_request_btn);
        mLlMaintenanceProgressEnquiry = findViewById(R.id.ll_maintenance_progress_enquiry);
        mLlCheckWarrantyStatus = findViewById(R.id.ll_check_warranty_status);
        mLlBuyPopucareService = findViewById(R.id.ll_buy_popucare_service);

        int iconPadding = DensityUtils.dp2px(this, 12);

        ExTextView tvPwdPermanent = mLlMaintenanceProgressEnquiry.findViewById(R.id.tv_item_name);
        tvPwdPermanent.setText(R.string.maintenance_progress_enquiry);
        tvPwdPermanent.setRawIcon(R.drawable.maintenance_progress_enquiry, iconPadding);

        ExTextView tvPwdPeriod = mLlCheckWarrantyStatus.findViewById(R.id.tv_item_name);
        tvPwdPeriod.setText(R.string.check_warranty_status);
        tvPwdPeriod.setRawIcon(R.drawable.check_warranty_status, iconPadding);

        ExTextView tvPwdOneTime = mLlBuyPopucareService.findViewById(R.id.tv_item_name);
        tvPwdOneTime.setText(R.string.buy_popucare_service);
        tvPwdOneTime.setRawIcon(R.drawable.popucare_icon, iconPadding);
    }

    private void initTitleBar() {
        ((TextView) findViewById(R.id.page_title)).setText(R.string.service_support_maintain);
        findViewById(R.id.page_action).setVisibility(View.GONE);
    }

    private void setListener() {
        mTvMaintenanceRequestBtn.setOnClickListener(this);
        mLlMaintenanceProgressEnquiry.setOnClickListener(this);
        mLlCheckWarrantyStatus.setOnClickListener(this);
        mLlBuyPopucareService.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_maintenance_request_btn:
                goToNewActivity(MaintenanceRequestActivity.class);
                break;
            case R.id.ll_maintenance_progress_enquiry:
                break;
            case R.id.ll_check_warranty_status:
                break;
            case R.id.ll_buy_popucare_service:
                break;
        }

    }
}
