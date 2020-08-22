package com.populstay.populife.maintservice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;

public class MaintServiceDetailsActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maint_service_details);
        initTitleBar();
    }

    private void initTitleBar() {
        ((TextView) findViewById(R.id.page_title)).setText(R.string.maintenance_progress_enquiry);
        findViewById(R.id.page_action).setVisibility(View.GONE);
    }
}
