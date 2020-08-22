package com.populstay.populife.pay.paypal.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ProductPayInfo implements Parcelable {

    // 商品描述
    private String description;
    // 货币
    private String moneyType;
    // 商品明细
    private List<ProductPayInfoItem> items;
    // 订单号（作为custom参数传给sdk）
    private String preOrderNo;

    public ProductPayInfo(){

    }

    protected ProductPayInfo(Parcel in) {
        description = in.readString();
        moneyType = in.readString();
        items = in.createTypedArrayList(ProductPayInfoItem.CREATOR);
        preOrderNo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(moneyType);
        dest.writeTypedList(items);
        dest.writeString(preOrderNo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductPayInfo> CREATOR = new Creator<ProductPayInfo>() {
        @Override
        public ProductPayInfo createFromParcel(Parcel in) {
            return new ProductPayInfo(in);
        }

        @Override
        public ProductPayInfo[] newArray(int size) {
            return new ProductPayInfo[size];
        }
    };

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMoneyType() {
        return moneyType;
    }

    public void setMoneyType(String moneyType) {
        this.moneyType = moneyType;
    }

    public List<ProductPayInfoItem> getItems() {
        return items;
    }

    public void setItems(List<ProductPayInfoItem> items) {
        this.items = items;
    }

    public String getPreOrderNo() {
        return preOrderNo;
    }

    public void setPreOrderNo(String preOrderNo) {
        this.preOrderNo = preOrderNo;
    }
}
