package com.populstay.populife.find.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {

    private String name;
    private String type;
    private float price;
    private int desc;
    private int photoDesId;
    private String detailUrl;

    public Product(String name, String type) {
        this.name = name;
        this.type = type;
    }

    protected Product(Parcel in) {
        name = in.readString();
        type = in.readString();
        price = in.readFloat();
        desc = in.readInt();
        photoDesId = in.readInt();
        detailUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
        dest.writeFloat(price);
        dest.writeInt(desc);
        dest.writeInt(photoDesId);
        dest.writeString(detailUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getDesc() {
        return desc;
    }

    public void setDesc(int desc) {
        this.desc = desc;
    }

    public int getPhotoDesId() {
        return photoDesId;
    }

    public void setPhotoDesId(int photoDesId) {
        this.photoDesId = photoDesId;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }
}
