package com.populstay.populife.find;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.populstay.populife.R;
import com.populstay.populife.find.adapter.VideoAdapter;

import cn.ittiger.player.PlayerManager;

public class VideoListFragment extends FindFragment{

    public static FindFragment newInstance() {
        FindFragment fragment = new VideoListFragment();
        return fragment;
    }

    RecyclerView mRecyclerView;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_video_list;
    }

    @Override
    protected void init(View view) {

        mRecyclerView = view.findViewById(R.id.home_device_list_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new VideoAdapter(getActivity()));

    }

    @Override
    protected void onVisibilityChanged(boolean visible) {
        super.onVisibilityChanged(visible);
        if (!visible){
            PlayerManager.getInstance().pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PlayerManager.getInstance().release();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PlayerManager.getInstance().stop();
    }
}
