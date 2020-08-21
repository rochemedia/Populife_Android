package com.populstay.populife.maintservice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.reflect.TypeToken;
import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.maintservice.adapter.MaintRequestListAdapter;
import com.populstay.populife.maintservice.entity.MaintRequest;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.ui.recycler.CommonRecyclerView;
import com.populstay.populife.ui.recycler.SpacesItemDecoration;
import com.populstay.populife.util.CollectionUtil;
import com.populstay.populife.util.GsonUtil;
import com.populstay.populife.util.storage.PeachPreference;

import java.util.List;

public class MaintRequestListActivity extends BaseActivity {

    private CommonRecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;

    private MaintRequestListAdapter mMaintRequestListAdapter;
    private LinearLayout mLlNoData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maint_request);
        initTitleBar();
        initView();
        requestMaintRequestList();
    }

    private void initTitleBar() {
        ((TextView) findViewById(R.id.page_title)).setText(R.string.maintenance_progress_enquiry);
        findViewById(R.id.page_action).setVisibility(View.GONE);
    }

    private void initView() {
        mRefreshLayout = findViewById(R.id.refresh_layout);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestMaintRequestList();
            }
        });

        mRecyclerView = findViewById(R.id.mRecyclerView);
        //mRecyclerView.addItemDecoration(new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.dimens_dp_10)));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLlNoData = findViewById(R.id.layout_no_data);
    }

    private void showEmptyView(){
        if (null != mLlNoData){
            mLlNoData.setVisibility(View.VISIBLE);
        }
        if (null != mRecyclerView){
            mRecyclerView.setVisibility(View.GONE);
        }
    }
    private void showContentView(){
        if (null != mLlNoData){
            mLlNoData.setVisibility(View.GONE);
        }
        if (null != mRecyclerView){
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }



    private void setData(List<MaintRequest> data) {
        if (mMaintRequestListAdapter == null) {
            mMaintRequestListAdapter = new MaintRequestListAdapter(this, data);
            mRecyclerView.setAdapter(mMaintRequestListAdapter);

            mRecyclerView.setOnItemClickListener(new CommonRecyclerView.OnItemClickListener() {
                @Override
                public void onItemClick(int position, View itemView) {

                }
            });

        } else {
            mMaintRequestListAdapter.reset(data);
        }
    }

    private void requestMaintRequestList() {
        RestClient.builder()
                .url(Urls.REPAIR_APPLY_GET)
                .loader(this)
                .params("userId", PeachPreference.readUserId())
                .params("start", "0")
                .params("limit", "1000000")
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        if (mRefreshLayout != null) {
                            mRefreshLayout.setRefreshing(false);
                        }

                        JSONObject result = JSON.parseObject(response);
                        if (result.getBoolean("success")) {

                            List<MaintRequest> data = GsonUtil.fromJson(result.getJSONArray("data").toJSONString(),new TypeToken<List<MaintRequest>>() {});

                            if (!CollectionUtil.isEmpty(data)){
                                showContentView();
                                setData(data);
                            }else {
                                showEmptyView();
                            }
                        }else {
                            showEmptyView();
                        }
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        showEmptyView();
                        if (mRefreshLayout != null) {
                            mRefreshLayout.setRefreshing(false);
                        }
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        showEmptyView();
                        if (mRefreshLayout != null) {
                            mRefreshLayout.setRefreshing(false);
                        }
                    }
                })
                .build()
                .get();
    }
}
