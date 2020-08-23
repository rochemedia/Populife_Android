package com.populstay.populife.maintservice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.maintservice.entity.MaintRequest;

public class MaintServiceDetailsActivity extends BaseActivity {

    public static final String MAINT_REQUEST_DATA_TAG = "maint_request_data_tag";
    private TextView tvApplyNo, tvCheckMaintenanceOrder;


    private MaintRequest mMaintRequest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maint_service_details);
        getIntentData();
        initTitleBar();
        initView();
        setListener();
    }

    private void getIntentData() {
        mMaintRequest = getIntent().getParcelableExtra(MAINT_REQUEST_DATA_TAG);
    }

    private void initTitleBar() {
        ((TextView) findViewById(R.id.page_title)).setText(R.string.maintenance_progress_enquiry);
        findViewById(R.id.page_action).setVisibility(View.GONE);
    }

    private void initView() {
        tvApplyNo = findViewById(R.id.tvApplyNo);
        tvCheckMaintenanceOrder = findViewById(R.id.tvCheckMaintenanceOrder);

        if (null != mMaintRequest){
            tvApplyNo.setText(String.format(getString(R.string.repair_no),mMaintRequest.getApplyNo()));
        }
    }

    private void setListener() {
        tvCheckMaintenanceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNewActivity(MaintWorkOrderActivity.class);
            }
        });
    }
}
