package com.populstay.populife.keypwdmanage.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class KeyPwd implements Parcelable {

    // 密钥id
    private int id;
    // 密钥名称
    private String alias;
    //有效开始时间(时间戳)
    private long startDate;
    // 失效时间，0是永久有效(时间戳)
    private long endDate;
    // String接收帐号
    private String recUser;
    // String发送帐号
    private String sendUser;
    // Long发送时间(时间戳)
    private long sendDate;
    // String钥匙的状态（110401：正常使用，110402：待接收，110405：已冻结，110408：已删除，110410：已重置,110500:已过期）;;; 密码状态(0删除，1未激活，2失效， 3正常)
    private String status;
    //Integer 1限时钥匙
    private int type;
    //String用户头像
    private String avatar;
    // Integer钥匙是否被授权：0-否，1-是
    private int keyRight;
    // String键盘密码
    private String keyboardPwd;
    // Integer键盘密码类型（15为自定义密码,其他参考科技侠平台）
    private int keyboardPwdType;
    // Long创建时间(时间戳)
    private long createDate;
    // Integer1：蓝牙钥匙，2：键盘密码
    private int keyType;

    protected KeyPwd(Parcel in) {
        id = in.readInt();
        alias = in.readString();
        startDate = in.readLong();
        endDate = in.readLong();
        recUser = in.readString();
        sendUser = in.readString();
        sendDate = in.readLong();
        status = in.readString();
        type = in.readInt();
        avatar = in.readString();
        keyRight = in.readInt();
        keyboardPwd = in.readString();
        keyboardPwdType = in.readInt();
        createDate = in.readLong();
        keyType = in.readInt();
    }

    public static final Creator<KeyPwd> CREATOR = new Creator<KeyPwd>() {
        @Override
        public KeyPwd createFromParcel(Parcel in) {
            return new KeyPwd(in);
        }

        @Override
        public KeyPwd[] newArray(int size) {
            return new KeyPwd[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getRecUser() {
        return recUser;
    }

    public void setRecUser(String recUser) {
        this.recUser = recUser;
    }

    public String getSendUser() {
        return sendUser;
    }

    public void setSendUser(String sendUser) {
        this.sendUser = sendUser;
    }

    public long getSendDate() {
        return sendDate;
    }

    public void setSendDate(long sendDate) {
        this.sendDate = sendDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getKeyRight() {
        return keyRight;
    }

    public void setKeyRight(int keyRight) {
        this.keyRight = keyRight;
    }

    public String getKeyboardPwd() {
        return keyboardPwd;
    }

    public void setKeyboardPwd(String keyboardPwd) {
        this.keyboardPwd = keyboardPwd;
    }

    public int getKeyboardPwdType() {
        return keyboardPwdType;
    }

    public void setKeyboardPwdType(int keyboardPwdType) {
        this.keyboardPwdType = keyboardPwdType;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public int getKeyType() {
        return keyType;
    }

    public void setKeyType(int keyType) {
        this.keyType = keyType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(alias);
        dest.writeLong(startDate);
        dest.writeLong(endDate);
        dest.writeString(recUser);
        dest.writeString(sendUser);
        dest.writeLong(sendDate);
        dest.writeString(status);
        dest.writeInt(type);
        dest.writeString(avatar);
        dest.writeInt(keyRight);
        dest.writeString(keyboardPwd);
        dest.writeInt(keyboardPwdType);
        dest.writeLong(createDate);
        dest.writeInt(keyType);
    }
}
