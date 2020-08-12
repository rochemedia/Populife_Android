package com.populstay.populife.find;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.populstay.populife.base.BaseVisibilityFragment;


public abstract class FindFragment extends BaseVisibilityFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResId(), null);
        init(view);
        return view;
    }

    protected abstract int getLayoutResId();

    protected abstract void init(View view);

    @Override
    public void onResume() {
        super.onResume();
    }

}
