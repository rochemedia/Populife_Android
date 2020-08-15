package com.populstay.populife.keypwdmanage.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class CreateBluetoothActionInfo implements Parcelable {
    
    private String shareUrl;
    private boolean isShare;

    public CreateBluetoothActionInfo() {
    }

    protected CreateBluetoothActionInfo(Parcel in) {
        shareUrl = in.readString();
        isShare = in.readByte() != 0;
    }

    public static final Creator<CreateBluetoothActionInfo> CREATOR = new Creator<CreateBluetoothActionInfo>() {
        @Override
        public CreateBluetoothActionInfo createFromParcel(Parcel in) {
            return new CreateBluetoothActionInfo(in);
        }

        @Override
        public CreateBluetoothActionInfo[] newArray(int size) {
            return new CreateBluetoothActionInfo[size];
        }
    };

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public boolean isShare() {
        return isShare;
    }

    public void setShare(boolean share) {
        isShare = share;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(shareUrl);
        dest.writeByte((byte) (isShare ? 1 : 0));
    }
}
