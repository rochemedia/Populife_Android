package com.populstay.populife.keypwdmanage;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.reflect.TypeToken;
import com.populstay.populife.R;
import com.populstay.populife.base.BaseFragment;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.Key;
import com.populstay.populife.keypwdmanage.adapter.KeyPwdListAdapter;
import com.populstay.populife.keypwdmanage.entity.KeyPwd;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.ui.recycler.CommonRecyclerView;
import com.populstay.populife.ui.recycler.SpacesItemDecoration;
import com.populstay.populife.util.CollectionUtil;
import com.populstay.populife.util.GsonUtil;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.toast.ToastUtil;

import java.util.List;


public class KeyPwdListFragment extends BaseFragment{

    private View mRootView;
    private TextView mTvDesc,mTvEmptyHint;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CommonRecyclerView mRvKeyPwdList;
    private View mFooterView;
    private KeyPwdListAdapter mKeyPwdListAdapter;

    public static final String CATEGORY_KEY = "CATEGORY_KEY";
    public static final String DATA_KEY = "DATA_KEY";

    private int mCurrentCategory;
    private Key mKey;
    private int mCurrentPageNo = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_key_pwd_list, null);
        getArgumentsData();
        initView();
        initDescAndEmptyHint();
        refreshData();
        return mRootView;
    }

    private void getArgumentsData() {
        Bundle args = getArguments();
        mCurrentCategory = args.getInt(CATEGORY_KEY, KeyPwdConstant.IKeyPwdCategory.KEY_PWD_CATEGORY_NOT_ACTIVATED);
        mKey = (Key) args.getSerializable(DATA_KEY);
    }

    private void initView() {
        mTvDesc = mRootView.findViewById(R.id.tv_desc);
        mTvEmptyHint = mRootView.findViewById(R.id.tv_empty_hint);

        mSwipeRefreshLayout = mRootView.findViewById(R.id.refresh_layout);
        mRvKeyPwdList = mRootView.findViewById(R.id.rv_key_pwd_list);
        mRvKeyPwdList.addItemDecoration(new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.dimens_dp_10)));
        mRvKeyPwdList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showRefreshView();
                refreshData();
            }
        });
        mRvKeyPwdList.setOnLoadMoreListener(new CommonRecyclerView.LoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMoreData();
            }
        });
    }

    private void initDescAndEmptyHint() {

        if (KeyPwdConstant.IKeyPwdCategory.KEY_PWD_CATEGORY_NOT_ACTIVATED == mCurrentCategory) {
            mTvDesc.setText(R.string.key_pwd_status_not_activated_desc);
            mTvEmptyHint.setText(R.string.key_pwd_status_not_activated_empty);
        } else if (KeyPwdConstant.IKeyPwdCategory.KEY_PWD_CATEGORY_AVAILABLE == mCurrentCategory) {
            mTvDesc.setText(R.string.key_pwd_status_available_desc);
            mTvEmptyHint.setText(R.string.key_pwd_status_available_empty);
        } else if (KeyPwdConstant.IKeyPwdCategory.KEY_PWD_CATEGORY_INVALID == mCurrentCategory) {
            mTvDesc.setText(R.string.key_pwd_status_invalid_desc);
            mTvEmptyHint.setText(R.string.key_pwd_status_invalid_empty);
        }
    }

    public static KeyPwdListFragment newInstance(int category, Key key) {
        Bundle args = new Bundle();
        args.putInt(CATEGORY_KEY, category);
        args.putSerializable(DATA_KEY, key);
        KeyPwdListFragment fragment = new KeyPwdListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void setData(List<KeyPwd> data,  boolean isResetData) {
        if (mKeyPwdListAdapter == null) {
            mKeyPwdListAdapter = new KeyPwdListAdapter(mActivity, data);
            mKeyPwdListAdapter.enableFooterView();
            mFooterView = LayoutInflater.from(mActivity).inflate(R.layout.footer_layout, mRvKeyPwdList, false);
            mKeyPwdListAdapter.addFooterView(mFooterView);
            mRvKeyPwdList.setAdapter(mKeyPwdListAdapter);
        } else {
            if (isResetData){
                mKeyPwdListAdapter.reset(data);
            }else {
                mKeyPwdListAdapter.addAll(data);
            }
        }
    }

    private void showContentView(){
        if (null != mTvEmptyHint){
            mTvEmptyHint.setVisibility(View.GONE);
        }
        if (null != mRvKeyPwdList){
            mRvKeyPwdList.setVisibility(View.VISIBLE);
        }
        hideLoadMoreView();
    }

    private void showEmptyView(){
        if (!isRefreshData()){
            ToastUtil.showToast(R.string.no_more_data);
            return;
        }
        if (null != mTvEmptyHint){
            mTvEmptyHint.setVisibility(View.VISIBLE);
        }
        if (null != mRvKeyPwdList){
            mRvKeyPwdList.setVisibility(View.GONE);
        }
    }

    private void hideRefreshView() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showRefreshView() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    private void hideLoadMoreView() {
        if (null == mFooterView){
            return;
        }
        if(mFooterView.getVisibility() == View.VISIBLE) {
            mFooterView.setVisibility(View.GONE);
        }
    }

    private void showLoadMoreView() {
        if (null == mFooterView){
            return;
        }
        if(mFooterView.getVisibility() == View.GONE) {
            mFooterView.setVisibility(View.VISIBLE);
        }
    }

    private boolean isRefreshData(){
        return mCurrentPageNo == 1;
    }

    public void refreshData() {
        mCurrentPageNo = 1;
        requestEkeyList();
    }

    public void loadMoreData() {
        showLoadMoreView();
        requestEkeyList();
    }

    /**
     * 获取用户钥匙列表数据
     */
    private void requestEkeyList() {
        RestClient.builder()
                .url(Urls.KEY_KEYBOARD_LIST)
                .params("userId", PeachPreference.readUserId())
                .params("lockId", mKey.getLockId())
                .params("category", mCurrentCategory)
                .params("pageNo", mCurrentPageNo)
                .params("pageSize", 20)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        hideRefreshView();
                        hideLoadMoreView();

                        JSONObject result = JSON.parseObject(response);
                        if (null == result || !result.getBoolean("success")){
                            showEmptyView();
                        }else {
                            List<KeyPwd> mDatas = GsonUtil.fromJson(result.getJSONArray("data").toJSONString(), new TypeToken<List<KeyPwd>>() {});
                            if (CollectionUtil.isEmpty(mDatas)){
                                showEmptyView();
                            }else {
                                showContentView();
                                setData(mDatas,isRefreshData());
                                mCurrentPageNo++;
                            }
                        }
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        showEmptyView();
                        hideLoadMoreView();
                        hideRefreshView();
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        showEmptyView();
                        hideLoadMoreView();
                        hideRefreshView();
                    }
                })
                .build()
                .post();
    }
}
