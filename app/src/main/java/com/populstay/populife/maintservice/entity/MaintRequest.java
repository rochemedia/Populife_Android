package com.populstay.populife.maintservice.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 维修申请
 */
public class MaintRequest implements Parcelable {

    private String applyNo;
    private String userId;
    private String description;
    private String modelNum;
    // 附件链接
    private String purchasedTicket;
    private long purchasedDate;
    private long createDate;
    private boolean cancelled;
    private int status;
    private String lockName;
    private boolean empty;

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModelNum() {
        return modelNum;
    }

    public void setModelNum(String modelNum) {
        this.modelNum = modelNum;
    }

    public String getPurchasedTicket() {
        return purchasedTicket;
    }

    public void setPurchasedTicket(String purchasedTicket) {
        this.purchasedTicket = purchasedTicket;
    }

    public long getPurchasedDate() {
        return purchasedDate;
    }

    public void setPurchasedDate(long purchasedDate) {
        this.purchasedDate = purchasedDate;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public static Creator<MaintRequest> getCREATOR() {
        return CREATOR;
    }

    public MaintRequest() {
    }

    protected MaintRequest(Parcel in) {
        applyNo = in.readString();
        userId = in.readString();
        description = in.readString();
        modelNum = in.readString();
        purchasedTicket = in.readString();
        purchasedDate = in.readLong();
        createDate = in.readLong();
        cancelled = in.readByte() != 0;
        status = in.readInt();
        lockName = in.readString();
        empty = in.readByte() != 0;
    }

    public static final Creator<MaintRequest> CREATOR = new Creator<MaintRequest>() {
        @Override
        public MaintRequest createFromParcel(Parcel in) {
            return new MaintRequest(in);
        }

        @Override
        public MaintRequest[] newArray(int size) {
            return new MaintRequest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(applyNo);
        dest.writeString(userId);
        dest.writeString(description);
        dest.writeString(modelNum);
        dest.writeString(purchasedTicket);
        dest.writeLong(purchasedDate);
        dest.writeLong(createDate);
        dest.writeByte((byte) (cancelled ? 1 : 0));
        dest.writeInt(status);
        dest.writeString(lockName);
        dest.writeByte((byte) (empty ? 1 : 0));
    }
}
