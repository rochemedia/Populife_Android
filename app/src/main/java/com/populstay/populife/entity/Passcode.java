package com.populstay.populife.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 锁的键盘密码
 * Created by Jerry
 */
public class Passcode implements Parcelable {

	public static final Creator<Passcode> CREATOR = new Creator<Passcode>() {
		@Override
		public Passcode createFromParcel(Parcel in) {
			return new Passcode(in);
		}

		@Override
		public Passcode[] newArray(int size) {
			return new Passcode[size];
		}
	};
	/**
	 * 键盘密码id
	 */
	int keyboardPwdId;
	/**
	 * 键盘密码
	 */
	String keyboardPwd;
	/**
	 * 发送密码帐号
	 */
	String sendUser;
	/**
	 * 别名
	 */
	String alias;
	/**
	 * 键盘密码类型（参考科技侠平台）
	 */
	int keyboardPwdType;
	/**
	 * 生效时间(时间戳)
	 */
	long startDate;
	/**
	 * 失效时间(时间戳)
	 */
	long endDate;
	/**
	 * 创建时间(时间戳)
	 */
	long createDate;
	/**
	 * 密码状态(0删除，1未激活，2失效， 3正常)
	 */
	int status;

	public Passcode() {
	}

	public Passcode(int keyboardPwdId, String keyboardPwd, String sendUser, String alias,
					int keyboardPwdType, long startDate, long endDate, long createDate, int status) {
		this.keyboardPwdId = keyboardPwdId;
		this.keyboardPwd = keyboardPwd;
		this.sendUser = sendUser;
		this.alias = alias;
		this.keyboardPwdType = keyboardPwdType;
		this.startDate = startDate;
		this.endDate = endDate;
		this.createDate = createDate;
		this.status = status;
	}

	protected Passcode(Parcel in) {
		keyboardPwdId = in.readInt();
		keyboardPwd = in.readString();
		sendUser = in.readString();
		alias = in.readString();
		keyboardPwdType = in.readInt();
		startDate = in.readLong();
		endDate = in.readLong();
		createDate = in.readLong();
		status = in.readInt();
	}

	public int getKeyboardPwdId() {
		return keyboardPwdId;
	}

	public void setKeyboardPwdId(int keyboardPwdId) {
		this.keyboardPwdId = keyboardPwdId;
	}

	public String getKeyboardPwd() {
		return keyboardPwd;
	}

	public void setKeyboardPwd(String keyboardPwd) {
		this.keyboardPwd = keyboardPwd;
	}

	public String getSendUser() {
		return sendUser;
	}

	public void setSendUser(String sendUser) {
		this.sendUser = sendUser;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getKeyboardPwdType() {
		return keyboardPwdType;
	}

	public void setKeyboardPwdType(int keyboardPwdType) {
		this.keyboardPwdType = keyboardPwdType;
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

	public long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Passcode{" +
				"keyboardPwdId=" + keyboardPwdId +
				", keyboardPwd='" + keyboardPwd + '\'' +
				", sendUser='" + sendUser + '\'' +
				", alias='" + alias + '\'' +
				", keyboardPwdType=" + keyboardPwdType +
				", startDate=" + startDate +
				", endDate=" + endDate +
				", createDate=" + createDate +
				", status=" + status +
				'}';
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(keyboardPwdId);
		parcel.writeString(keyboardPwd);
		parcel.writeString(sendUser);
		parcel.writeString(alias);
		parcel.writeInt(keyboardPwdType);
		parcel.writeLong(startDate);
		parcel.writeLong(endDate);
		parcel.writeLong(createDate);
		parcel.writeInt(status);
	}
}
