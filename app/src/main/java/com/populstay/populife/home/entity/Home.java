package com.populstay.populife.home.entity;

import android.os.Parcel;
import android.os.Parcelable;


public class Home implements Parcelable {
    // 家庭（分组）id，为空时表示未分组
    private String id;
    // 名称，为空时表示未分组
    private String name;
    // 创建时间（毫秒时间戳）
    private long createDate;
    // 家庭（分组）下的锁数量
    private int lockCount;

    public Home(){

    }

    protected Home(Parcel in) {
        id = in.readString();
        name = in.readString();
        createDate = in.readLong();
        lockCount = in.readInt();
    }

    public static final Creator<Home> CREATOR = new Creator<Home>() {
        @Override
        public Home createFromParcel(Parcel in) {
            return new Home(in);
        }

        @Override
        public Home[] newArray(int size) {
            return new Home[size];
        }
    };

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

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public int getLockCount() {
        return lockCount;
    }

    public void setLockCount(int lockCount) {
        this.lockCount = lockCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeLong(createDate);
        parcel.writeInt(lockCount);
    }
}
