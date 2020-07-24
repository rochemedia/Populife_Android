package com.populstay.populife.home.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.ttlock.gateway.sdk.model.DeviceInfo;

public class HomeDevice implements Parcelable {

    private String deviceId;
    private String name;
    // G2表示网关，其他的都是锁
    private String modelNum;
    // 设备是否被冻结
    private boolean freezed;
    private String alias;
    private int type;




    public HomeDevice() {
    }

    public HomeDevice(String name) {
        this.name = name;
    }

    protected HomeDevice(Parcel in) {
        deviceId = in.readString();
        name = in.readString();
        modelNum = in.readString();
        freezed = in.readByte() != 0;
        alias = in.readString();
        type = in.readInt();
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
        parcel.writeString(alias);
        parcel.writeInt(type);
    }


}
