package com.populstay.populife.maintservice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.pay.paypal.PayPalHelper;
import com.populstay.populife.pay.paypal.entity.ProductPayInfo;
import com.populstay.populife.pay.paypal.entity.ProductPayInfoItem;

import java.util.ArrayList;
import java.util.List;

public class MaintWorkOrderActivity extends BaseActivity {


    private TextView tvPaymentConfirmBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maint_work_order);
        PayPalHelper.getInstance().startPayPalService(MaintWorkOrderActivity.this);
        initTitleBar();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PayPalHelper.getInstance().stopPayPalService(MaintWorkOrderActivity.this);
    }

    private void initTitleBar() {
        ((TextView) findViewById(R.id.page_title)).setText(R.string.maintenance_detials_2);
        findViewById(R.id.page_action).setVisibility(View.GONE);
    }

    private void initView() {
        tvPaymentConfirmBtn = findViewById(R.id.tv_payment_confirm_btn);
        tvPaymentConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPay();
            }
        });
    }

    private void startPay(){
        ProductPayInfo productPayInfo = new ProductPayInfo();
        productPayInfo.setDescription("维修智能设备");
        // 关键信息 货币单位
        productPayInfo.setMoneyType("USD");
        // 关键信息 订单号
        productPayInfo.setPreOrderNo("123456789");

        List<ProductPayInfoItem> infoItems = new ArrayList<>();
        ProductPayInfoItem infoItem = new ProductPayInfoItem();
        infoItem.setDescription("维修智能设备");
        // 关键信息 货币单位
        infoItem.setMoneyType("USD");
        infoItem.setName("维修智能设备");
        infoItem.setNumber(1);
        // 关键信息 支付金额
        infoItem.setPrice(100);
        infoItems.add(infoItem);
        productPayInfo.setItems(infoItems);

        PayPalHelper.getInstance().doPayPalPay(MaintWorkOrderActivity.this, productPayInfo);
    }
}
