package com.populstay.populife.maintservice.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class MaintDevice implements Parcelable {

    //  设备id
    private String id;
    // 设备name
    private String name;
    // 设备alias
    private String alias;
    // 维修服务费用
    private float fee;
    // 类设备类型 1：keybox锁，2:deadbolt锁，, 3：网关
    private int type;
    //true：已购买，false尚未购买
    private boolean isBuyed;


    protected MaintDevice(Parcel in) {
        id = in.readString();
        name = in.readString();
        alias = in.readString();
        fee = in.readFloat();
        type = in.readInt();
        isBuyed = in.readByte() != 0;
    }

    public static final Creator<MaintDevice> CREATOR = new Creator<MaintDevice>() {
        @Override
        public MaintDevice createFromParcel(Parcel in) {
            return new MaintDevice(in);
        }

        @Override
        public MaintDevice[] newArray(int size) {
            return new MaintDevice[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(alias);
        dest.writeFloat(fee);
        dest.writeInt(type);
        dest.writeByte((byte) (isBuyed ? 1 : 0));
    }

    public interface DeviceType {
        // 类设备类型 1：keybox锁，2:deadbolt锁，, 3：网关
        int KEY_BOX = 1;
        int DEADBOLT = 2;
        int GATEWAY = 3;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isBuyed() {
        return isBuyed;
    }

    public void setBuyed(boolean buyed) {
        isBuyed = buyed;
    }
}
