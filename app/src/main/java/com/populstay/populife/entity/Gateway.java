package com.populstay.populife.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jerry
 */
public class Gateway implements Parcelable {

	public static final Creator<Gateway> CREATOR = new Creator<Gateway>() {
		@Override
		public Gateway createFromParcel(Parcel in) {
			return new Gateway(in);
		}

		@Override
		public Gateway[] newArray(int size) {
			return new Gateway[size];
		}
	};
	/**
	 * 网关ID
	 */
	private int gatewayId;
	/**
	 * 网关mac地址
	 */
	private String gatewayMac;
	/**
	 * 网关NET MAC地址
	 */
	private String gatewayName;
	/**
	 * 网关名称
	 */
	private String name;
	/**
	 * 网关管理的锁数量
	 */
	private int lockNum;
	/**
	 * 是否在线：0-否，1-是
	 */
	private int isOnline;

	/**
	 * 网关别称
	 */
	private String alias;

	public Gateway() {
	}

	public Gateway(int gatewayId, String gatewayMac, String gatewayName, String name, int lockNum, int isOnline) {
		this.gatewayId = gatewayId;
		this.gatewayMac = gatewayMac;
		this.gatewayName = gatewayName;
		this.name = name;
		this.lockNum = lockNum;
		this.isOnline = isOnline;
	}

	protected Gateway(Parcel in) {
		gatewayId = in.readInt();
		gatewayMac = in.readString();
		gatewayName = in.readString();
		name = in.readString();
		lockNum = in.readInt();
		isOnline = in.readInt();
		alias = in.readString();
	}

	public int getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(int gatewayId) {
		this.gatewayId = gatewayId;
	}

	public String getGatewayMac() {
		return gatewayMac;
	}

	public void setGatewayMac(String gatewayMac) {
		this.gatewayMac = gatewayMac;
	}

	public String getGatewayName() {
		return gatewayName;
	}

	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLockNum() {
		return lockNum;
	}

	public void setLockNum(int lockNum) {
		this.lockNum = lockNum;
	}

	public int getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(int isOnline) {
		this.isOnline = isOnline;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public String toString() {
		return "Gateway{" +
				"gatewayId=" + gatewayId +
				", gatewayMac='" + gatewayMac + '\'' +
				", gatewayName='" + gatewayName + '\'' +
				", name='" + name + '\'' +
				", lockNum=" + lockNum +
				", isOnline=" + isOnline +
				", alias=" + alias +
				'}';
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(gatewayId);
		parcel.writeString(gatewayMac);
		parcel.writeString(gatewayName);
		parcel.writeString(name);
		parcel.writeInt(lockNum);
		parcel.writeInt(isOnline);
		parcel.writeString(alias);
	}
}
