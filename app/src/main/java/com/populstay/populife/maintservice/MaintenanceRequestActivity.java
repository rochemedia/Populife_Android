package com.populstay.populife.maintservice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.home.entity.HomeDevice;
import com.populstay.populife.home.entity.HomeDeviceInfo;
import com.populstay.populife.maintservice.adapter.ProductListAdapter;

import java.util.ArrayList;
import java.util.List;


public class MaintenanceRequestActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvServiceProcessGuideHint;
    private RecyclerView mProductListView;
    private ProductListAdapter mProductListAdapter;
    private List<HomeDevice> mProductList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_request);
        initTitleBar();
        initData();
        initView();
        setListener();
    }

    private void initTitleBar() {
        ((TextView) findViewById(R.id.page_title)).setText(R.string.maintenance_request);
        findViewById(R.id.page_action).setVisibility(View.GONE);
    }

    private void initView() {
        mTvServiceProcessGuideHint = findViewById(R.id.tv_after_sales_service_process_guide_hint);
        initProductListView();
    }

    private void initData() {
        mProductList = new ArrayList<>();

        // 横闩锁
        HomeDevice device = new HomeDevice();
        device.setName(HomeDeviceInfo.IDeviceName.NAME_LOCK_DEADBOLT);
        device.setModelNum(HomeDeviceInfo.IModelNum.NAME_LOCK_DEADBOLT);
        mProductList.add(device);

        // 密码锁
        device = new HomeDevice();
        device.setName(HomeDeviceInfo.IDeviceName.NAME_LOCK_KEY_BOX);
        device.setModelNum(HomeDeviceInfo.IModelNum.NAME_LOCK_KEY_BOX);
        mProductList.add(device);

        // 网关
        device = new HomeDevice();
        device.setName(HomeDeviceInfo.IDeviceName.NAEM_GATEWAY);
        device.setModelNum(HomeDeviceInfo.IModelNum.NAEM_GATEWAY);
        mProductList.add(device);

    }

    private void initProductListView() {
        mProductListView = findViewById(R.id.product_list_view);
        mProductListView.setLayoutManager(new GridLayoutManager(this,2));
        mProductListAdapter = new ProductListAdapter(mProductList,this);
        mProductListView.setAdapter(mProductListAdapter);
        mProductListAdapter.setOnItemClickListener(new ProductListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                mProductListAdapter.selectItem(position);
                HomeDevice device = mProductList.get(position);
            }
        });

    }

    private void setListener() {
        mTvServiceProcessGuideHint.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_after_sales_service_process_guide_hint:
                goToNewActivity(MaintAfterSaleProcessActivity.class);
                break;
        }
    }
}
