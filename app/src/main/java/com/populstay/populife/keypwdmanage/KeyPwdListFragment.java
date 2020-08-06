package com.populstay.populife.keypwdmanage;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseFragment;
import com.populstay.populife.fragment.LockSendPasscodeFragment;


public class KeyPwdListFragment extends BaseFragment {

    private View mRootView;
    private TextView mTvDesc,mTvEmptyHint;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRvKeyPwdList;

    // 待激活
    public static final String TAB_VAL_NOT_ACTIVATED = "TAB_VAL_NOT_ACTIVATED";
    // 可使用
    public static final String TAB_VAL_AVAILABLE = "TAB_VAL_AVAILABLE";
    // 已失效
    public static final String TAB_VAL_INVALID = "TAB_VAL_INVALID";
    public static final String TAB_KEY = "TAB_KEY";

    private String mCurrentTab = TAB_VAL_AVAILABLE;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_key_pwd_list, null);
        getArgumentsData();
        initView();
        initDescAndEmptyHint();
        return mRootView;
    }

    private void getArgumentsData() {
        Bundle args = getArguments();
        mCurrentTab = args.getString(TAB_KEY, TAB_VAL_AVAILABLE);
    }

    private void initView() {
        mTvDesc = mRootView.findViewById(R.id.tv_desc);
        mTvEmptyHint = mRootView.findViewById(R.id.tv_empty_hint);
        mSwipeRefreshLayout = mRootView.findViewById(R.id.refresh_layout);
        mRvKeyPwdList = mRootView.findViewById(R.id.rv_key_pwd_list);
    }

    private void initDescAndEmptyHint() {

        if (TAB_VAL_NOT_ACTIVATED.equals(mCurrentTab)) {
            mTvDesc.setText(R.string.key_pwd_status_not_activated_desc);
            mTvEmptyHint.setText(R.string.key_pwd_status_not_activated_empty);
        } else if (TAB_VAL_AVAILABLE.equals(mCurrentTab)) {
            mTvDesc.setText(R.string.key_pwd_status_available_desc);
            mTvEmptyHint.setText(R.string.key_pwd_status_available_empty);
        } else if (TAB_VAL_INVALID.equals(mCurrentTab)) {
            mTvDesc.setText(R.string.key_pwd_status_invalid_desc);
            mTvEmptyHint.setText(R.string.key_pwd_status_invalid_empty);
        }
    }

    public static KeyPwdListFragment newInstance(String tabType) {
        Bundle args = new Bundle();
        args.putString(TAB_KEY, tabType);
        KeyPwdListFragment fragment = new KeyPwdListFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
