package com.populstay.populife.maintservice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.ui.widget.exedittext.ExEditText;

/**
 * 维修进度查询/查看保修状态
 */
public class MaintSearchActivity extends BaseActivity {

    private String fromType = FROM_TYPE_MAINTENANCE_PROGRESS;
    public static final String FROM_TYPE_TAG = "from_type_tag";
    // 维修进度查询
    public static final String FROM_TYPE_MAINTENANCE_PROGRESS = "from_type_maintenance_progress";
    // 查看保修状态
    public static final String FROM_TYPE_CHECK_WARRANTY_STATUS = "from_type_check_warranty_status";

    private TextView mPageTitle, mTvSearchName, mTvBrowseDeviceNo;
    private ExEditText mEditSearchName;
    private TextView mTvNextBtn;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maint_search);
        getIntentData();
        initTitleBar();
        initView();
    }

    private void getIntentData() {
        fromType = getIntent().getStringExtra(FROM_TYPE_TAG);
    }

    private void initTitleBar() {
        mPageTitle = findViewById(R.id.page_title);
        if (FROM_TYPE_MAINTENANCE_PROGRESS.equals(fromType)){
            mPageTitle.setText(R.string.maintenance_progress_enquiry);
        }else {
            mPageTitle.setText(R.string.check_warranty_status);
        }
        findViewById(R.id.page_action).setVisibility(View.GONE);
    }

    private void initView() {
        mTvSearchName = findViewById(R.id.tv_search_name);
        mTvBrowseDeviceNo = findViewById(R.id.tv_find_the_device_number_btn);
        if (FROM_TYPE_MAINTENANCE_PROGRESS.equals(fromType)){
            mTvSearchName.setText(getResources().getString(R.string.user_name_edit_hint) + "*");
            mTvBrowseDeviceNo.setVisibility(View.GONE);
        }else {
            mTvBrowseDeviceNo.setVisibility(View.VISIBLE);
            mTvSearchName.setText(R.string.label_please_input_equipment_number);
        }

        mEditSearchName = findViewById(R.id.edit_content);
        mEditSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTvNextBtn.setEnabled(!TextUtils.isEmpty(mEditSearchName.getTextStr()));
            }
        });

        mTvNextBtn = findViewById(R.id.tv_finish);
        mTvNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FROM_TYPE_MAINTENANCE_PROGRESS.equals(fromType)){
                    goToNewActivity(MaintRequestListActivity.class);
                }else {
                    goToNewActivity(MaintWarrantyStatusActivity.class);
                }
            }
        });
    }
}
