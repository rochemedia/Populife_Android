package com.populstay.populife.keypwdmanage;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
import com.populstay.populife.activity.EkeyDetailActivity;
import com.populstay.populife.activity.PasscodeDetailActivity;
import com.populstay.populife.app.MyApplication;
import com.populstay.populife.base.BaseFragment;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.Key;
import com.populstay.populife.enumtype.Operation;
import com.populstay.populife.eventbus.Event;
import com.populstay.populife.keypwdmanage.adapter.KeyPwdListAdapter;
import com.populstay.populife.keypwdmanage.entity.CreateBluetoothActionInfo;
import com.populstay.populife.keypwdmanage.entity.KeyPwd;
import com.populstay.populife.lock.ILockDeletePasscode;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.ui.loader.PeachLoader;
import com.populstay.populife.ui.recycler.CommonRecyclerView;
import com.populstay.populife.ui.recycler.SpacesItemDecoration;
import com.populstay.populife.util.CollectionUtil;
import com.populstay.populife.util.GsonUtil;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.dialog.DialogUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.toast.ToastUtil;

import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;

import static com.populstay.populife.app.MyApplication.mTTLockAPI;


public class KeyPwdListFragment extends BaseFragment {

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

            mKeyPwdListAdapter.setmDeleteBtnClickListener(new KeyPwdListAdapter.ListViewActionBtnClickListener() {
                @Override
                public void onClick(View view, final KeyPwd item) {
                    if (item.isBTKey()){
                        DialogUtil.showCommonDialog(mActivity, null,
                                mActivity.getResources().getString(R.string.note_delete_ekey), mActivity.getResources().getString(R.string.delete),
                                mActivity.getResources().getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        deleteEkey(item);
                                    }
                                }, null);
                    }else {
                        DialogUtil.showCommonDialog(mActivity, null,
                                getString(R.string.note_confirm_delete), getString(R.string.delete),
                                getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //和锁通信，删除密码
                                        deletePasscode(item);
                                    }
                                }, null);
                    }
                }
            });
            mKeyPwdListAdapter.setmEditBtnClickListener(new KeyPwdListAdapter.ListViewActionBtnClickListener() {
                @Override
                public void onClick(View view, KeyPwd item) {
                    if (item.isBTKey()){
                        EkeyDetailActivity.actionStart(mActivity, item.getId(),
                                item.getKeyRight(), item.getAlias(), item.getType(), item.getStartDate(),
                                item.getEndDate(), item.getRecUser(), item.getSendUser(), item.getSendDate(), item.getStatus());
                    }else {
                        PasscodeDetailActivity.actionStart(mActivity, item);
                    }
                }
            });
            mKeyPwdListAdapter.setmSendBtnClickListener(new KeyPwdListAdapter.ListViewActionBtnClickListener() {
                @Override
                public void onClick(View view, KeyPwd item) {
                    if (item.isBTKey()){

                    }else {
                        showShare(item);
                    }

                }
            });

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


    /**
     * 删除钥匙（钥匙详情页面）
     */
    private void deleteEkey(KeyPwd item) {
        RestClient.builder()
                .url(Urls.LOCK_EKEY_DELETE)
                .loader(mActivity)
                .params("keyId", item.getId())
                .params("userId", PeachPreference.readUserId())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject result = JSON.parseObject(response);
                        int code = result.getInteger("code");
                        if (code == 200) {
                            toast(R.string.ekey_delete_success);
                            refreshData();
                        } else {
                            toast(R.string.ekey_delete_fail);
                        }
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        toast(R.string.ekey_delete_fail);
                    }
                })
                .build()
                .post();
    }

    private void deletePasscode(KeyPwd item) {
        PeachLoader.showLoading(mActivity);
        if (mTTLockAPI.isConnected(mKey.getLockMac())) {
            setDeletePasscodeCallback(item);
            mTTLockAPI.deleteOneKeyboardPassword(null, PeachPreference.getOpenid(),
                    mKey.getLockVersion(), mKey.getAdminPwd(), mKey.getLockKey(), mKey.getLockFlagPos(),
                    item.getKeyboardPwdType(), item.getKeyboardPwd(), mKey.getAesKeyStr());
        } else {
            MyApplication.bleSession.setLockmac(mKey.getLockMac());
            setDeletePasscodeCallback(item);
            mTTLockAPI.connect(mKey.getLockMac());
        }
    }

    private void setDeletePasscodeCallback(final KeyPwd item) {
        MyApplication.bleSession.setOperation(Operation.DELETE_ONE_KEYBOARDPASSWORD);
        MyApplication.bleSession.setKeyboardPwdType(item.getKeyboardPwdType());
        MyApplication.bleSession.setKeyboardPwdOriginal(item.getKeyboardPwd());

        MyApplication.bleSession.setILockDeletePasscode(new ILockDeletePasscode() {
            @Override
            public void onSuccess() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PeachLoader.stopLoading();
                        requestDeletePasscode(item);
                    }
                });
            }

            @Override
            public void onFail() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PeachLoader.stopLoading();
                        toast(R.string.operation_fail);
                    }
                });
            }
        });
    }

    /**
     * 请求服务器，删除键盘密码
     */
    private void requestDeletePasscode(KeyPwd item) {
        RestClient.builder()
                .url(Urls.LOCK_PASSCODE_DELETE)
                .loader(mActivity)
                .params("lockId", mKey.getLockId())
                .params("userId", PeachPreference.readUserId())
                .params("keyboardPwdId", item.getId())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject result = JSON.parseObject(response);
                        int code = result.getInteger("code");
                        if (code == 200) {
                            toast(R.string.passcode_delete_success);
                            refreshData();
                        } else {
                            toast(R.string.passcode_delete_fail);
                        }
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        toast(R.string.passcode_delete_fail);
                    }
                })
                .build()
                .post();
    }

    public void showShare(final KeyPwd item) {
        OnekeyShare oks = new OnekeyShare();

        // 自定义分享平台
        oks.setCustomerLogo(BitmapFactory.decodeResource(getResources(), R.drawable.ic_share_zalo),
                "Zalo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent vIt = new Intent(Intent.ACTION_SEND);
//							vIt.setPackage("com.facebook.orca");
                            vIt.setType("text/plain");
                            vIt.putExtra(Intent.EXTRA_TEXT, getShareContent(item));
                            startActivity(vIt);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });

        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle(getString(R.string.app_name));
        // titleUrl QQ和QQ空间跳转链接
//		oks.setTitleUrl("http://sharesdk.cn");
//		oks.setAddress("13201812820");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(getShareContent(item));
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//		oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url在微信、微博，Facebook等平台中使用
//		oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网使用
//		oks.setComment("我是测试评论文本");
        // 启动分享GUI
        oks.show(mActivity);
    }

    private String getShareContent(KeyPwd mPasscode) {
        String content = "";
        String type = "";
        switch (mPasscode.getKeyboardPwdType()) {
            case 1:
                type = getString(R.string.one_time);
                content = getString(R.string.hello_here_is_your_passcode) + mPasscode.getKeyboardPwd() + "\n" +
                        getString(R.string.start_time) + getString(R.string.symbol_colon) +
                        DateUtil.getDateToString(mPasscode.getCreateDate(), "yyyy-MM-dd HH:mm") + getString(R.string.use_it_within_6_hours) + "\n" +
                        getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.one_time) + "\n" +
                        getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
                        "\n" +
                        getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
                        "\n" +
                        getString(R.string.note_no_key_bottom_right_dont_share_passcode);
                break;

            case 2:
                type = getString(R.string.permanent);
                content = getString(R.string.hello_here_is_your_passcode) + mPasscode.getKeyboardPwd() + "\n" +
                        getString(R.string.start_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getCreateDate(), "yyyy-MM-dd HH:mm") + "\n" +
                        getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.permanent) + "\n" +
                        getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
                        "\n" +
                        getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
                        "\n" +
                        getString(R.string.note_use_passcode_once_before) + DateUtil.getDateToString(DateUtil.getStringToDate(DateUtil.getDateToString(mPasscode.getCreateDate(), "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm") + 1000 * 3600 * 24, "yyyy-MM-dd HH:mm") + getString(R.string.no_key_bottom_right_dont_share_passcode);
                break;

            case 3:
                type = getString(R.string.period);
                content = getString(R.string.hello_here_is_your_passcode) + mPasscode.getKeyboardPwd() + "\n" +
                        getString(R.string.start_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getStartDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.end_time) + getString(R.string.symbol_colon) +  DateUtil.getDateToString(mPasscode.getEndDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.period) + "\n" +
                        getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
                        "\n" +
                        getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
                        "\n" +
                        getString(R.string.note_use_passcode_once_before) + DateUtil.getDateToString(mPasscode.getStartDate() + 1000 * 3600 * 24, "yyyy-MM-dd HH:mm") + getString(R.string.no_key_bottom_right_dont_share_passcode);
                break;

            case 4:
                type = getString(R.string.clear);
                content = getString(R.string.hello_here_is_your_passcode) + mPasscode.getKeyboardPwd() + "\n" +
                        getString(R.string.start_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getCreateDate(), "yyyy-MM-dd HH:mm") + getString(R.string.use_it_within_24_hours) + "\n" +
                        getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.clear) + "\n" +
                        getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
                        "\n" +
                        getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
                        "\n" +
                        getString(R.string.note_no_key_bottom_right_dont_share_passcode);
                break;

            case 5:
                type = getString(R.string.weekend_cyclic);
                content = getString(R.string.hello_here_is_your_passcode) + mPasscode.getKeyboardPwd() + "\n" +
                        getString(R.string.start_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getStartDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.end_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getEndDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.weekend_cyclic) + "\n" +
                        getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
                        "\n" +
                        getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
                        "\n" +
                        getString(R.string.note_no_key_bottom_right_dont_share_passcode);
                break;

            case 6:
                type = getString(R.string.daily_cyclic);
                content = getString(R.string.hello_here_is_your_passcode) + mPasscode.getKeyboardPwd() + "\n" +
                        getString(R.string.start_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getStartDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.end_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getEndDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.daily_cyclic) + "\n" +
                        getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
                        "\n" +
                        getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
                        "\n" +
                        getString(R.string.note_no_key_bottom_right_dont_share_passcode);
                break;

            case 7:
                type = getString(R.string.workday_cyclic);
                content = getString(R.string.hello_here_is_your_passcode) + mPasscode.getKeyboardPwd() + "\n" +
                        getString(R.string.start_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getStartDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.end_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getEndDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.workday_cyclic) + "\n" +
                        getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
                        "\n" +
                        getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
                        "\n" +
                        getString(R.string.note_no_key_bottom_right_dont_share_passcode);
                break;

            case 8:
                type = getString(R.string.monday_cyclic);
                content = getString(R.string.hello_here_is_your_passcode) + mPasscode.getKeyboardPwd() + "\n" +
                        getString(R.string.start_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getStartDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.end_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getEndDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.monday_cyclic) + "\n" +
                        getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
                        "\n" +
                        getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
                        "\n" +
                        getString(R.string.note_no_key_bottom_right_dont_share_passcode);
                break;

            case 9:
                type = getString(R.string.tuesday_cyclic);
                content = getString(R.string.hello_here_is_your_passcode) + mPasscode.getKeyboardPwd() + "\n" +
                        getString(R.string.start_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getStartDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.end_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getEndDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.tuesday_cyclic) + "\n" +
                        getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
                        "\n" +
                        getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
                        "\n" +
                        getString(R.string.note_no_key_bottom_right_dont_share_passcode);
                break;
                
            case 10:
                type = getString(R.string.wednesday_cyclic);
                content = getString(R.string.hello_here_is_your_passcode) + mPasscode.getKeyboardPwd() + "\n" +
                        getString(R.string.start_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getStartDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.end_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getEndDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.wednesday_cyclic) + "\n" +
                        getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
                        "\n" +
                        getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
                        "\n" +
                        getString(R.string.note_no_key_bottom_right_dont_share_passcode);
                break;

            case 11:
                type = getString(R.string.thursday_cyclic);
                content = getString(R.string.hello_here_is_your_passcode) + mPasscode.getKeyboardPwd() + "\n" +
                        getString(R.string.start_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getStartDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.end_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getEndDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.thursday_cyclic) + "\n" +
                        getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
                        "\n" +
                        getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
                        "\n" +
                        getString(R.string.note_no_key_bottom_right_dont_share_passcode);
                break;

            case 12:
                type = getString(R.string.friday_cyclic);
                content = getString(R.string.hello_here_is_your_passcode) + mPasscode.getKeyboardPwd() + "\n" +
                        getString(R.string.start_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getStartDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.end_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getEndDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.friday_cyclic) + "\n" +
                        getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
                        "\n" +
                        getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
                        "\n" +
                        getString(R.string.note_no_key_bottom_right_dont_share_passcode);
                break;

            case 13:
                type = getString(R.string.saturday_cyclic);
                content = getString(R.string.hello_here_is_your_passcode) + mPasscode.getKeyboardPwd() + "\n" +
                        getString(R.string.start_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getStartDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.end_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getEndDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.saturday_cyclic) + "\n" +
                        getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
                        "\n" +
                        getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
                        "\n" +
                        getString(R.string.note_no_key_bottom_right_dont_share_passcode);
                break;

            case 14:
                type = getString(R.string.sunday_cyclic);
                content = getString(R.string.hello_here_is_your_passcode) + mPasscode.getKeyboardPwd() + "\n" +
                        getString(R.string.start_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getStartDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.end_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mPasscode.getEndDate(),DateUtil.DATE_TIME_PATTERN_1) + "\n" +
                        getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.sunday_cyclic) + "\n" +
                        getString(R.string.lock_name) + getString(R.string.symbol_colon) + mKey.getLockName() + "\n" +
                        "\n" +
                        getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
                        "\n" +
                        getString(R.string.note_no_key_bottom_right_dont_share_passcode);
                break;

            default:
                break;
        }

        return content;
    }

    @Override
    public void onEventSub(Event event) {
        super.onEventSub(event);
        if (Event.EventType.CREATE_BT_KEY_SUCCESS == event.type){
            refreshData();
            CreateBluetoothActionInfo createBluetoothActionInfo = (CreateBluetoothActionInfo) event.obj;
            if (null != createBluetoothActionInfo && createBluetoothActionInfo.isShare()){
                showShareBTKey(createBluetoothActionInfo.getShareUrl());
            }
        }else if (Event.EventType.CREATE_PWD_SUCCESS == event.type){
             refreshData();
        }else if (Event.EventType.SYN_PWD_INFO_SUCCESS == event.type){
            refreshData();
        }
    }

    public void showShareBTKey(final String data) {
        OnekeyShare oks = new OnekeyShare();

        // 自定义分享平台
        oks.setCustomerLogo(BitmapFactory.decodeResource(getResources(), R.drawable.ic_share_zalo),
                "Zalo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent vIt = new Intent(Intent.ACTION_SEND);
//							vIt.setPackage("com.facebook.orca");
                            vIt.setType("text/plain");
                            vIt.putExtra(Intent.EXTRA_TEXT, data);
                            startActivity(vIt);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });

        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle(getString(R.string.app_name));
        oks.setText(data);
        oks.show(mActivity);
    }
}
