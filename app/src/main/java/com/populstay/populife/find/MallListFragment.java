package com.populstay.populife.find;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.populstay.populife.R;
import com.populstay.populife.find.adapter.MallListAdapter;
import com.populstay.populife.find.entity.Product;
import com.populstay.populife.find.entity.ProductInfo;

import java.util.ArrayList;
import java.util.List;

public class MallListFragment extends FindFragment{

    public static FindFragment newInstance() {
        FindFragment fragment = new MallListFragment();
        return fragment;
    }

    private RecyclerView mDeviceListView;
    private MallListAdapter mDeviceListAdapter;
    private List<Product> mDeviceList;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_mall_list;
    }

    @Override
    protected void init(View view) {
        mDeviceListView = view.findViewById(R.id.home_device_list_recyclerview);
        mDeviceListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mDeviceList = new ArrayList<>();
        initData();
        mDeviceListAdapter = new MallListAdapter(mDeviceList, getContext());
        mDeviceListView.setAdapter(mDeviceListAdapter);
        mDeviceListAdapter.setOnItemClickListener(new MallListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                mDeviceListAdapter.selectItem(position);
                Product  selectProduct = mDeviceList.get(position);

                Intent intent= new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(selectProduct.getDetailUrl());
                intent.setData(content_url);
                startActivity(intent);
            }
        });
    }

    private void initData(){

        // 密码锁
        Product device = new Product(getString(R.string.lock_type_keybox), ProductInfo.IProductInfoType.PRODUCT_LOCK_TYPE_KEY_BOX);
        device.setDetailUrl("https://www.populife.co/#/keybox");
        mDeviceList.add(device);

        // 横闩锁
        device = new Product(getString(R.string.lock_type_deadbolt), ProductInfo.IProductInfoType.PRODUCT_LOCK_TYPE_DEADBOLT);
        device.setDetailUrl("https://www.populife.co/#/deadbolt");
        mDeviceList.add(device);

        // 网关
        device = new Product(getString(R.string.device_name_gateway),ProductInfo.IProductInfoType.PRODUCT_TYPE_GATEWAY);
        device.setDetailUrl("https://www.populife.co/#/gateway");
        mDeviceList.add(device);

        // PopuCare
        device = new Product(getString(R.string.product_type_popu_care_name),ProductInfo.IProductInfoType.PRODUCT_TYPE_POPU_CARE);
        device.setDetailUrl("https://www.populife.co");
        mDeviceList.add(device);
    }
}
