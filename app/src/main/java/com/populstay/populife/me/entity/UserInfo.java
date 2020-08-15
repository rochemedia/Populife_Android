package com.populstay.populife.me.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class UserInfo implements Parcelable {


    // 用户id
    private String id;

    //手机号
    private String phone;

    //邮箱地址
    private String email;

    //昵称
    private String nickname;

    //用户头像
    private String avatar;

    //用户名
    private String username;

    //是否已删除，Y：是，N：否
    private String isDeleted;

    // 注册时间（毫秒级时间戳）
    private String registeredDate;

    // 注册账户类型，1：手机号，2：邮箱
    private int accountType;

    // sciener用户openid
    private int openid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public int getOpenid() {
        return openid;
    }

    public void setOpenid(int openid) {
        this.openid = openid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public UserInfo(){

    }

    protected UserInfo(Parcel in) {
        id = in.readString();
        phone = in.readString();
        email = in.readString();
        nickname = in.readString();
        avatar = in.readString();
        username = in.readString();
        isDeleted = in.readString();
        registeredDate = in.readString();
        accountType = in.readInt();
        openid = in.readInt();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(nickname);
        dest.writeString(avatar);
        dest.writeString(username);
        dest.writeString(isDeleted);
        dest.writeString(registeredDate);
        dest.writeInt(accountType);
        dest.writeInt(openid);
    }
}
