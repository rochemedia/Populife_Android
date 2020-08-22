package com.populstay.populife.pay.paypal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalItem;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.populstay.populife.pay.paypal.entity.ProductPayInfo;
import com.populstay.populife.pay.paypal.entity.ProductPayInfoItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;

public class PayPalHelper {

    private static final String TAG = "PayPalHelper";
    //配置何种支付环境，一般沙盒，正式
    // 测试 PayPalConfiguration.ENVIRONMENT_NO_NETWORK
    // 正式 PayPalConfiguration.ENVIRONMENT_PRODUCTION
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;

    // note that these credentials will differ between live & sandbox environments.
    //你所注册的APP Id
    private static final String CONFIG_CLIENT_ID = "AQaN46hZ5LzGV5wNVKJCQdQ2tzIzBibTOYZCBHCE8bocEdhY_XiOt9EWkJvQ_6JB2m_hp1L7EuVvz5Jh";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;


    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID);

    private static PayPalHelper payPalHelper;

    private PayPalHelper() {
    }

    public static PayPalHelper getInstance() {
        if (payPalHelper == null) {
            synchronized (PayPalHelper.class) {
                payPalHelper = new PayPalHelper();
            }
        }
        return payPalHelper;
    }

    /**
     * 启动PayPal服务
     *
     * @param context
     */
    public void startPayPalService(Context context) {
        Intent intent = new Intent(context, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        context.startService(intent);
    }

    /**
     * 停止PayPal服务  sdfsdfsdssaaass
     *
     * @param context
     */
    public void stopPayPalService(Context context) {
        context.stopService(new Intent(context, PayPalService.class));
    }

    /**
     * 开始执行支付操作
     *
     * @param context
     */
    public void doPayPalPay(Context context, ProductPayInfo productPayInfo) {
        /*
         * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
         * Change PAYMENT_INTENT_SALE to
         *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
         *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
         *     later via calls from your server.
         *
         * Also, to include additional payment details and an item list, see getStuffToBuy() below.
         */
        PayPalPayment thingToBuy = getStuffToBuy(PayPalPayment.PAYMENT_INTENT_AUTHORIZE,productPayInfo);
        /*
         * See getStuffToBuy(..) for examples of some available payment options.
         */
        Intent intent = new Intent(context, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        ((Activity) context).startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    /*
     * This method shows use of optional payment details and item list.
     *
     * 直接给PP创建支付的信息，支付对象实体信息
     */
    private PayPalPayment getStuffToBuy(String paymentIntent,ProductPayInfo productPayInfo) {

        List<ProductPayInfoItem> infoItems = productPayInfo.getItems();

        //--- include an item list, payment amount details
        //具体的产品信息列表
        PayPalItem[] items = new PayPalItem[infoItems.size()];
        for (int i = 0, len = infoItems.size(); i < len; i++) {
            ProductPayInfoItem infoItem = infoItems.get(i);
            // PayPalItem的四个参数. 1.商品名称 2.商品数量 3.商品单价 4.货币 5.商品描述
            items[i] = new PayPalItem(infoItem.getName(), infoItem.getNumber(), new BigDecimal(infoItem.getPrice()), infoItem.getMoneyType(), infoItem.getDescription());
        }
        BigDecimal subtotal = PayPalItem.getItemTotal(items);

        // 如果订单有运费跟税,在这里添加
        BigDecimal shipping = new BigDecimal(0);
        BigDecimal tax = new BigDecimal(0);

        // 整合总价
        PayPalPaymentDetails paymentDetails = new PayPalPaymentDetails(shipping, subtotal, tax);
        BigDecimal amount = subtotal.add(shipping).add(tax);
        PayPalPayment payment = new PayPalPayment(amount, productPayInfo.getMoneyType(), productPayInfo.getDescription(), paymentIntent);
        payment.items(items).paymentDetails(paymentDetails);
        //--- set other optional fields like invoice_number, custom field, and soft_descriptor
        payment.custom(productPayInfo.getPreOrderNo());
        return payment;
    }

    /**
     * 处理支付之后的结果
     *
     * @param context
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void confirmPayResult(final Context context, int requestCode, int resultCode, Intent data, final DoResult doResult) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));

                    /**
                     *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                     * or consent completion.
                     * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                     * for more details.
                     *
                     * For sample mobile backend interactions, see
                     * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                     */
                    //displayResultText("PaymentConfirmation info received from PayPal");
                    // 这里直接跟服务器确认支付结果，支付结果确认后回调处理结果
                    JSONObject jsonObject = confirm.toJSONObject();
                    if (jsonObject != null) {
                        JSONObject response = jsonObject.optJSONObject("response");
                        if (response != null) {
                            String paymentId = response.optString("id");
                            Log.i(TAG, "response="+response.toString());
                            //根据Id从自己的服务器判断相应的查询逻辑
                            doResult.confirmSuccess(paymentId);
                        }
                    }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.");
                doResult.customerCanceled();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                doResult.invalidPaymentConfiguration();
                Log.i(
                        TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        doResult.confirmFuturePayment();
                        Log.i("FuturePaymentExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("FuturePaymentExample", authorization_code);

                        //sendAuthorizationToServer(auth);
                        //displayResultText("Future Payment code received from PayPal");

                    } catch (JSONException e) {
                        doResult.confirmNetWorkError();
                        Log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");
                doResult.customerCanceled();
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                doResult.invalidPaymentConfiguration();
                Log.i(
                        "FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("ProfileSharingExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("ProfileSharingExample", authorization_code);

                        //sendAuthorizationToServer(auth);
                        //displayResultText("Profile Sharing code received from PayPal");

                    } catch (JSONException e) {
                        Log.e("ProfileSharingExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("ProfileSharingExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        "ProfileSharingExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }

    /**
     * c处理完结果之后回调
     */
    public interface DoResult {
        //与服务确认支付成功
        void confirmSuccess(String paymentId);

        //网络异常或者json返回有问题
        void confirmNetWorkError();

        //用户取消支付
        void customerCanceled();

        //授权支付(目前用不上)
        void confirmFuturePayment();

        //订单支付验证无效(目前用不上)
        void invalidPaymentConfiguration();
    }

}
