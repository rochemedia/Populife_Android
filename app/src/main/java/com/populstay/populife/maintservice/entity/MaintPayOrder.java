package com.populstay.populife.maintservice.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 预支付订单信息
 */
public class MaintPayOrder implements Parcelable {

    // 订单号（作为custom参数传给sdk）
    private String preOrderNo;
    // 金额单位(货币类型)
    private String currencyCode;
    // 支付金额
    private double amount;

    public MaintPayOrder(){

    }

    protected MaintPayOrder(Parcel in) {
        preOrderNo = in.readString();
        currencyCode = in.readString();
        amount = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(preOrderNo);
        dest.writeString(currencyCode);
        dest.writeDouble(amount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MaintPayOrder> CREATOR = new Creator<MaintPayOrder>() {
        @Override
        public MaintPayOrder createFromParcel(Parcel in) {
            return new MaintPayOrder(in);
        }

        @Override
        public MaintPayOrder[] newArray(int size) {
            return new MaintPayOrder[size];
        }
    };

    public String getPreOrderNo() {
        return preOrderNo;
    }

    public void setPreOrderNo(String preOrderNo) {
        this.preOrderNo = preOrderNo;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
