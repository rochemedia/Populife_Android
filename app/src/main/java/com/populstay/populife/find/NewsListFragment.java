package com.populstay.populife.find;

import android.view.View;

import com.populstay.populife.R;

public class NewsListFragment extends FindFragment{

    public static FindFragment newInstance() {
        FindFragment fragment = new NewsListFragment();
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_news_list;
    }

    @Override
    protected void init(View view) {

    }
}
