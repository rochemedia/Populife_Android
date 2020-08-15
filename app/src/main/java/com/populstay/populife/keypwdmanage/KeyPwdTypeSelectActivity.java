package com.populstay.populife.keypwdmanage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.ui.widget.extextview.ExTextView;
import com.populstay.populife.util.DensityUtils;

public class KeyPwdTypeSelectActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout mLlKeyPwdTypePermanent, mLlKeyPwdTypePeriod, mLlKeyPwdTypeOneTime, mLlKeyPwdTypeCustom, mLlKeyPwdTypeKeyBtKey;
    public static final String KEY_PWD_TYPE_SELECTED = "KEY_PWD_TYPE_SELECTED";
    public static final int KEY_PWD_TYPE_REQUEST_CODE = 0x01;
    private String currentKeyPwdType = KeyPwdConstant.IKeyPwdType.KEY_PWD_TYPE_KEY_BT_KEY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_pwd_type_select);
        initView();
        setItemNameAndIcon();

    }

    private void initView() {

        TextView mPageTitle  = findViewById(R.id.page_title);
        mPageTitle.setText(getResources().getString(R.string.select_key_pwd_type));
        findViewById(R.id.page_action).setVisibility(View.GONE);

        mLlKeyPwdTypePermanent = findViewById(R.id.ll_key_pwd_type_permanent);
        mLlKeyPwdTypePeriod = findViewById(R.id.ll_key_pwd_type_period);
        mLlKeyPwdTypeOneTime = findViewById(R.id.ll_key_pwd_type_one_time);
        mLlKeyPwdTypeCustom = findViewById(R.id.ll_key_pwd_type_custom);
        mLlKeyPwdTypeKeyBtKey = findViewById(R.id.ll_key_pwd_type_key_bt_key);

        mLlKeyPwdTypePermanent.setOnClickListener(this);
        mLlKeyPwdTypePeriod.setOnClickListener(this);
        mLlKeyPwdTypeOneTime.setOnClickListener(this);
        mLlKeyPwdTypeCustom.setOnClickListener(this);
        mLlKeyPwdTypeKeyBtKey.setOnClickListener(this);
    }
    private void setItemNameAndIcon() {

        int iconPadding = DensityUtils.dp2px(this,12);

        ExTextView tvPwdPermanent = mLlKeyPwdTypePermanent.findViewById(R.id.tv_item_name);
        tvPwdPermanent.setText(R.string.key_pwd_permanent);
        tvPwdPermanent.setRawIcon(R.drawable.pwd_permanent_icon, iconPadding);

        ExTextView tvPwdPeriod = mLlKeyPwdTypePeriod.findViewById(R.id.tv_item_name);
        tvPwdPeriod.setText(R.string.key_pwd_period);
        tvPwdPeriod.setRawIcon(R.drawable.pwd_period_icon, iconPadding);

        ExTextView tvPwdOneTime = mLlKeyPwdTypeOneTime.findViewById(R.id.tv_item_name);
        tvPwdOneTime.setText(R.string.key_pwd_one_time);
        tvPwdOneTime.setRawIcon(R.drawable.pwd_one_time_icon, iconPadding);

        ExTextView tvPwdCustom = mLlKeyPwdTypeCustom.findViewById(R.id.tv_item_name);
        tvPwdCustom.setText(R.string.key_pwd_custom);
        tvPwdCustom.setRawIcon(R.drawable.pwd_custom_icon, iconPadding);

        ExTextView tvPwdBtKey = mLlKeyPwdTypeKeyBtKey.findViewById(R.id.tv_item_name);
        tvPwdBtKey.setText(R.string.key_pwd_bt_key);
        tvPwdBtKey.setRawIcon(R.drawable.bt_key_icon, iconPadding);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_key_pwd_type_permanent:
                currentKeyPwdType = KeyPwdConstant.IKeyPwdType.KEY_PWD_TYPE_PERMANENT;
                setResult();
                break;
            case R.id.ll_key_pwd_type_period:
                currentKeyPwdType = KeyPwdConstant.IKeyPwdType.KEY_PWD_TYPE_PERIOD;
                setResult();
                break;
            case R.id.ll_key_pwd_type_one_time:
                currentKeyPwdType = KeyPwdConstant.IKeyPwdType.KEY_PWD_TYPE_ONE_TIME;
                setResult();
                break;
            case R.id.ll_key_pwd_type_custom:
                currentKeyPwdType = KeyPwdConstant.IKeyPwdType.KEY_PWD_TYPE_CUSTOM;
                setResult();
                break;
            case R.id.ll_key_pwd_type_key_bt_key:
                currentKeyPwdType = KeyPwdConstant.IKeyPwdType.KEY_PWD_TYPE_KEY_BT_KEY;
                setResult();
                break;
        }
    }

    private void setResult(){
        Intent data = new Intent();
        data.putExtra(KEY_PWD_TYPE_SELECTED, currentKeyPwdType);
        setResult(RESULT_OK,data);
        finish();
    }

    public static void actionStartForResult(Activity context) {
        Intent intent = new Intent(context, KeyPwdTypeSelectActivity.class);
        context.startActivityForResult(intent, KEY_PWD_TYPE_REQUEST_CODE);
    }

}
