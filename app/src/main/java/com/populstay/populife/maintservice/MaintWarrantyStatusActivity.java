package com.populstay.populife.maintservice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;

public class MaintWarrantyStatusActivity extends BaseActivity {

    private TextView mPageTitle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maint_warranty_status);
        initTitleBar();
    }

    private void initTitleBar() {
        mPageTitle = findViewById(R.id.page_title);
        mPageTitle.setText(R.string.check_warranty_status);
        findViewById(R.id.page_action).setVisibility(View.GONE);
    }
}
