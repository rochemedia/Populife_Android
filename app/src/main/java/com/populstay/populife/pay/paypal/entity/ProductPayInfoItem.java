package com.populstay.populife.pay.paypal.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ProductPayInfoItem implements Parcelable {

    // 商品描述
    private String description;
    // 货币
    private String moneyType;
    // 单价
    private double price;
    // 数量
    private int number;
    // 名称
    private String name;

    public ProductPayInfoItem(){

    }

    protected ProductPayInfoItem(Parcel in) {
        description = in.readString();
        moneyType = in.readString();
        price = in.readDouble();
        number = in.readInt();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(moneyType);
        dest.writeDouble(price);
        dest.writeInt(number);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ProductPayInfoItem> CREATOR = new Creator<ProductPayInfoItem>() {
        @Override
        public ProductPayInfoItem createFromParcel(Parcel in) {
            return new ProductPayInfoItem(in);
        }

        @Override
        public ProductPayInfoItem[] newArray(int size) {
            return new ProductPayInfoItem[size];
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
