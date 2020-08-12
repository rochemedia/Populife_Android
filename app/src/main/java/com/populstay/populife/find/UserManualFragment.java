package com.populstay.populife.find;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.populstay.populife.R;
import com.populstay.populife.activity.PDFActivity;
import com.populstay.populife.find.adapter.UserManualListAdapter;
import com.populstay.populife.find.entity.UserManual;
import com.populstay.populife.find.entity.UserManualInfo;
import com.populstay.populife.util.locale.LanguageUtil;

import java.util.ArrayList;
import java.util.List;

public class UserManualFragment extends FindFragment{
    public static FindFragment newInstance() {
        FindFragment fragment = new UserManualFragment();
        return fragment;
    }

    private RecyclerView mDeviceListView;
    private UserManualListAdapter mDeviceListAdapter;
    private List<UserManual> mDeviceList;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_user_manual;
    }

    @Override
    protected void init(View view) {

        mDeviceListView = view.findViewById(R.id.home_device_list_recyclerview);
        mDeviceListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mDeviceList = new ArrayList<>();
        initData();
        mDeviceListAdapter = new UserManualListAdapter(mDeviceList, getContext());
        mDeviceListView.setAdapter(mDeviceListAdapter);
        mDeviceListAdapter.setOnItemClickListener(new UserManualListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                mDeviceListAdapter.selectItem(position);
                UserManual homeDevice = mDeviceList.get(position);
                if (UserManualInfo.IUserManualType.USER_MANUAL_TYPE_APP.equals(homeDevice.getType())){
                    PDFActivity.actionStart(getActivity(), getString(R.string.user_manual_app),
                            "user_manual_app.pdf", true);
                }else if (UserManualInfo.IUserManualType.USER_MANUAL_LOCK_TYPE_DEADBOLT.equals(homeDevice.getType())){
                    PDFActivity.actionStart(getActivity(), getString(R.string.user_manual_deadbolt),
                            "user_manual_deadbolt.pdf", true);
                }else if (UserManualInfo.IUserManualType.USER_MANUAL_LOCK_TYPE_KEY_BOX.equals(homeDevice.getType())){
                    PDFActivity.actionStart(getActivity(), getString(R.string.user_manual_keybox),
                            LanguageUtil.isChinese(getActivity()) ? "user_manual_keybox_cn.pdf" : "user_manual_keybox_en.pdf", true);
                }else if (UserManualInfo.IUserManualType.USER_MANUAL_TYPE_GATEWAY.equals(homeDevice.getType())){
                    PDFActivity.actionStart(getActivity(), getString(R.string.user_manual_gateway),
                            "user_manual_gateway.pdf", true);
                }
            }
        });
    }

    private void initData(){

        // App手册
        UserManual device = new UserManual(getString(R.string.user_manual_app), UserManualInfo.IUserManualType.USER_MANUAL_TYPE_APP);
        mDeviceList.add(device);

        // 横闩锁
        device = new UserManual(getString(R.string.user_manual_deadbolt), UserManualInfo.IUserManualType.USER_MANUAL_LOCK_TYPE_DEADBOLT);
        mDeviceList.add(device);

        // 密码锁
        device = new UserManual(getString(R.string.user_manual_keybox),UserManualInfo.IUserManualType.USER_MANUAL_LOCK_TYPE_KEY_BOX);
        mDeviceList.add(device);

        // 网关
        device = new UserManual(getString(R.string.user_manual_gateway),UserManualInfo.IUserManualType.USER_MANUAL_TYPE_GATEWAY);
        mDeviceList.add(device);

    }
}
