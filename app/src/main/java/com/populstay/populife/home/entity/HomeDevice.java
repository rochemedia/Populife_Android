package com.populstay.populife.home.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class HomeDevice implements Parcelable {

    private String deviceId;
    private String name;
    // G2表示网关，其他的都是锁
    private String modelNum;
    // 设备是否被冻结
    private boolean freezed;

    protected HomeDevice(Parcel in) {
        deviceId = in.readString();
        name = in.readString();
        modelNum = in.readString();
        freezed = in.readByte() != 0;
    }

    public static final Creator<HomeDevice> CREATOR = new Creator<HomeDevice>() {
        @Override
        public HomeDevice createFromParcel(Parcel in) {
            return new HomeDevice(in);
        }

        @Override
        public HomeDevice[] newArray(int size) {
            return new HomeDevice[size];
        }
    };

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModelNum() {
        return modelNum;
    }

    public void setModelNum(String modelNum) {
        this.modelNum = modelNum;
    }

    public boolean isFreezed() {
        return freezed;
    }

    public void setFreezed(boolean freezed) {
        this.freezed = freezed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(deviceId);
        parcel.writeString(name);
        parcel.writeString(modelNum);
        parcel.writeByte((byte) (freezed ? 1 : 0));
    }
}
