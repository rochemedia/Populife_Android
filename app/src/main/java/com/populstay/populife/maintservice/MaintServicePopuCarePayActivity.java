package com.populstay.populife.maintservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.maintservice.entity.MaintDevice;
import com.populstay.populife.maintservice.entity.MaintPayOrder;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IError;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.pay.paypal.PayPalHelper;
import com.populstay.populife.pay.paypal.entity.ProductPayInfo;
import com.populstay.populife.pay.paypal.entity.ProductPayInfoItem;
import com.populstay.populife.util.GsonUtil;
import com.populstay.populife.util.storage.PeachPreference;

import java.util.ArrayList;
import java.util.List;

public class MaintServicePopuCarePayActivity extends BaseActivity {

    public static final String SELECT_MAINT_DEVICE_TAG  = "select_maint_device_tag";
    private MaintDevice mMaintDevice;
    private MaintPayOrder mPayOrder;
    private TextView tvProductPrice;
    private TextView tvPaymentConfirmBtn;
    private String mPaymentId;
    private View mContentView, mSubmitSuccessLayout;
    private TextView mPageTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maint_service_popucare_pay);
        PayPalHelper.getInstance().startPayPalService(MaintServicePopuCarePayActivity.this);
        getIntentData();
        initTitleBar();
        initView();
        showProductPrice();
        setListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PayPalHelper.getInstance().stopPayPalService(MaintServicePopuCarePayActivity.this);
    }

    private void getIntentData() {

        mMaintDevice = getIntent().getParcelableExtra(SELECT_MAINT_DEVICE_TAG);
    }

    private void setListener() {
        tvPaymentConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == mPayOrder){
                    getPayOrder();
                }else {
                    // 预支付订单已经创建，开始支付
                    startPay();
                }
            }
        });
    }

    private void initTitleBar() {
        mPageTitle = findViewById(R.id.page_title);
        mPageTitle.setText(R.string.buy_popucare_service);
        findViewById(R.id.page_action).setVisibility(View.GONE);
    }

    private void initView() {
        tvProductPrice = findViewById(R.id.tv_product_price);
        tvPaymentConfirmBtn = findViewById(R.id.tv_payment_confirm_btn);
        mSubmitSuccessLayout = findViewById(R.id.maint_service_popucare_pay_request_success_layout);
        mContentView = findViewById(R.id.content_view);
    }

    private void showProductPrice(){
        tvProductPrice.setText(String.format(getString(R.string.product_price), mMaintDevice.getFee()));
    }

    private void showSubmitSuccessLayout(){
        mContentView.setVisibility(View.GONE);
        mPageTitle.setText(R.string.submit_success);
        mSubmitSuccessLayout.setVisibility(View.VISIBLE);
        findViewById(R.id.tv_maintenance_request_finish_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PayPalHelper.getInstance().confirmPayResult(MaintServicePopuCarePayActivity.this, requestCode, resultCode, data, new PayPalHelper.DoResult() {
            @Override
            public void confirmSuccess(String id) {
                // 支付成功
                mPaymentId = id;
                // 提交订单
                submitPayOrder();
            }

            @Override
            public void confirmNetWorkError() {
                // 支付失败
                toast(R.string.failure_to_pay);
            }

            @Override
            public void customerCanceled() {
                // 支付取消(不用处理)
            }

            @Override
            public void confirmFuturePayment() {
                //授权支付(用不上)
            }

            @Override
            public void invalidPaymentConfiguration() {
                //订单支付验证无效(用不上)
            }
        });
    }


    /**
     * 获取预支付订单
     */
    private void getPayOrder() {
        RestClient.builder()
                .url(Urls.PAYPAL_REPAIR_SERVICE_PAY_ORDER)
                .loader(this)
                .params("userId", PeachPreference.readUserId())
                //  设备id
                .params("deviceId", mMaintDevice.getId())
                // type	int类设备类型 1：keybox锁，2:deadbolt锁，, 3：网关
                .params("type", mMaintDevice.getType())
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        JSONObject result = JSON.parseObject(response);
                        if (result.getBoolean("success")) {
                            mPayOrder = GsonUtil.fromJson(result.getJSONObject("data").toJSONString(),MaintPayOrder.class);
                            startPay();
                        }else {
                            toast(R.string.failed_to_obtain_advance_order_information);
                        }
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        toast(R.string.failed_to_obtain_advance_order_information);
                    }
                })
                .error(new IError() {
                    @Override
                    public void onError(int code, String msg) {
                        toast(R.string.failed_to_obtain_advance_order_information);
                    }
                })
                .build()
                .get();
    }


    private void startPay(){
        ProductPayInfo productPayInfo = new ProductPayInfo();
        productPayInfo.setDescription(getString(R.string.buy_popucare_service));
        // 关键信息 货币单位
        productPayInfo.setMoneyType(mPayOrder.getCurrencyCode());
        // 关键信息 订单号
        productPayInfo.setPreOrderNo(mPayOrder.getPreOrderNo());

        List<ProductPayInfoItem> infoItems = new ArrayList<>();
        ProductPayInfoItem infoItem = new ProductPayInfoItem();
        infoItem.setDescription(getString(R.string.buy_popucare_service));
        // 关键信息 货币单位
        infoItem.setMoneyType(mPayOrder.getCurrencyCode());
        infoItem.setName(getString(R.string.buy_popucare_service));
        infoItem.setNumber(1);
        // 关键信息 支付金额
        infoItem.setPrice(mPayOrder.getAmount());
        infoItems.add(infoItem);
        productPayInfo.setItems(infoItems);

        PayPalHelper.getInstance().doPayPalPay(MaintServicePopuCarePayActivity.this, productPayInfo);
    }


    /**
     * 支付成功，提交订单
     */
    private void submitPayOrder() {
        RestClient.builder()
                .url(Urls.PAYPAL_SUCCESS_REPAIR_SERVICE_PAY_ORDER)
                .loader(this)
                // 订单号
                .params("preOrderNo", mPayOrder.getPreOrderNo())
                // 交易号
                .params("paymentId", mPaymentId)
                //支付人(Android端PayPalSDK取不到不用传)
                //.params("payerId", mPayerId)
                //.params("payerId", "sb-errcc2671445@business.example.com")
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(final String response) {
                        JSONObject result = JSON.parseObject(response);
                        if (result.getBoolean("success")){
                            // 支付成功
                            showSubmitSuccessLayout();
                        }else {
                            toast(R.string.order_submission_failed);
                        }
                    }
                })
                .failure(new IFailure() {
                    @Override
                    public void onFailure() {
                        toast(R.string.order_submission_failed);
                    }
                })
                .build()
                .post();
    }
}
