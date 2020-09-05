package com.populstay.populife.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.reflect.TypeToken;
import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.eventbus.Event;
import com.populstay.populife.home.adapter.HomeListAdapter;
import com.populstay.populife.home.entity.Home;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.util.GsonUtil;
import com.populstay.populife.util.storage.PeachPreference;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class HomeListActivity extends BaseActivity implements View.OnClickListener {
    private String mActionType = VAL_ACTION_TYPE_SWITCH_HOME;
    public static final String VAL_ACTION_TYPE_SWITCH_HOME = "val_action_type_switch_home";
    public static final String VAL_ACTION_TYPE_MANAGE_HOME = "val_action_type_manage_home";
    public static final String KEY_ACTION_TYPE = "key_action_type";


    private RecyclerView mHomeRecyclerView;
    private HomeListAdapter mHomeListAdapter;
    private List<Home> mHomeDatas = new ArrayList<>();
    private TextView mTvPageTitle, mTvHomeCount;
    private TextView mTvCreateSpaceBtn, mTvManageSpaceBtn;
    private String mCurrentHomeId = PeachPreference.getLastSelectHomeId();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_list);
        getIntentData();
        initView();
        setListener();
        requestLockGroup();
    }

    private void getIntentData() {
        mActionType = getIntent().getStringExtra(KEY_ACTION_TYPE);
    }

    private void initView() {
        mTvCreateSpaceBtn = findViewById(R.id.tv_create_space_btn);
        mTvManageSpaceBtn = findViewById(R.id.tv_manage_space_btn);
        mTvHomeCount = findViewById(R.id.tv_home_count);
        findViewById(R.id.page_action).setVisibility(View.GONE);
        findViewById(R.id.page_action).setVisibility(View.GONE);
        if (VAL_ACTION_TYPE_MANAGE_HOME.equals(mActionType)) {
            mTvHomeCount.setVisibility(View.GONE);
            findViewById(R.id.second_line_view).setVisibility(View.VISIBLE);
            mTvPageTitle = findViewById(R.id.page_title);
            mTvPageTitle.setText(R.string.manage_space);
            mTvCreateSpaceBtn.setVisibility(View.VISIBLE);

        } else if (VAL_ACTION_TYPE_SWITCH_HOME.equals(mActionType)) {
            findViewById(R.id.page_title).setVisibility(View.GONE);
            mTvPageTitle = findViewById(R.id.page_left_title);
            mTvPageTitle.setVisibility(View.VISIBLE);
            mTvPageTitle.setText(R.string.switch_space);
            mTvManageSpaceBtn.setVisibility(View.VISIBLE);
        }

        mHomeRecyclerView = findViewById(R.id.homeRecyclerView);
        mHomeRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mHomeListAdapter = new HomeListAdapter(mHomeDatas, this, VAL_ACTION_TYPE_SWITCH_HOME.equals(mActionType) ? HomeListAdapter.SHOW_TYPE_CARD : HomeListAdapter.SHOW_TYPE_NORMAL);
        mHomeRecyclerView.setAdapter(mHomeListAdapter);
    }

    private void setListener() {
        mTvCreateSpaceBtn.setOnClickListener(this);
        mTvManageSpaceBtn.setOnClickListener(this);
        mHomeListAdapter.setOnItemClickListener(new HomeListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (VAL_ACTION_TYPE_SWITCH_HOME.equals(mActionType)) {
                    mHomeListAdapter.selectItem(position);
                    Home home = mHomeDatas.get(position);
                    PeachPreference.setLastSelectHomeId(home.getId());
                    PeachPreference.setLastSelectHomeName(home.getName());
                    EventBus.getDefault().post(new Event(Event.EventType.CHANGE_HOME, home));
                    finish();
                } else {
                    HomeDetailsActivity.actionStart(HomeListActivity.this, mHomeDatas.get(position));
                }
            }
        });
    }

    private void setHomeCount(int homeCount){
        if (null != mTvHomeCount){
            mTvHomeCount.setText(String.format(getResources().getString(R.string.space_num_match),homeCount));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_create_space_btn:
                HomeCreateActivity.actionStart(this, HomeCreateActivity.VAL_HOME_CREATE_ACTION_TYPE_NEW_HOME, null);
                break;
            case R.id.tv_manage_space_btn:
                actionStart(this,VAL_ACTION_TYPE_MANAGE_HOME);
                break;
        }
    }

    private void requestLockGroup() {
        RestClient.builder()
                .url(Urls.LOCK_GROUP_LIST)
                .loader(this)
                .params("userId", PeachPreference.readUserId())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject result = JSON.parseObject(response);
                        int code = result.getInteger("code");
                        if (code == 200) {
                            List<Home> datas = GsonUtil.fromJson(result.getJSONArray("data").toJSONString(),new TypeToken<List<Home>>(){});
                            setHomeCount(datas.size());
                            mHomeDatas.clear();
                            mHomeDatas.addAll(datas);
                            int selectPosition = 0;
                            for (int i = 0, len = mHomeDatas.size(); i < len; i++) {
                                if (!TextUtils.isEmpty(mCurrentHomeId) && mCurrentHomeId.equals(mHomeDatas.get(i).getId())){
                                    selectPosition = i;
                                }
                            }
                            mHomeListAdapter.selectItem(selectPosition);
                        }
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                    }
                })
                .build()
                .get();
    }

    public static void actionStart(Context context, String actionType) {
        Intent intent = new Intent(context, HomeListActivity.class);
        intent.putExtra(KEY_ACTION_TYPE, actionType);
        context.startActivity(intent);
    }

    @Override
    public void onEventSub(Event event) {
        super.onEventSub(event);
        if (Event.EventType.ADD_SPACE == event.type
                || Event.EventType.DELETE_SPACE == event.type
                || Event.EventType.RENAME_SPACE == event.type) {
            requestLockGroup();
        }
    }
}
