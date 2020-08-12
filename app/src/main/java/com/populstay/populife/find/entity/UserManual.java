package com.populstay.populife.find.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class UserManual implements Parcelable {

    private String name;
    private String type;

    public UserManual(String name, String type) {
        this.name = name;
        this.type = type;
    }

    protected UserManual(Parcel in) {
        name = in.readString();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserManual> CREATOR = new Creator<UserManual>() {
        @Override
        public UserManual createFromParcel(Parcel in) {
            return new UserManual(in);
        }

        @Override
        public UserManual[] newArray(int size) {
            return new UserManual[size];
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
}
