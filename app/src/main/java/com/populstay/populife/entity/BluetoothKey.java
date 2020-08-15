package com.populstay.populife.entity;

/**
 * 蓝牙钥匙实体类
 * Created by Jerry
 */
public class BluetoothKey {
	/**
	 * 钥匙id
	 */
	private int keyId;

	/**
	 * 钥匙名称
	 */
	private String alias;

	/**
	 * 有效开始时间(时间戳)
	 */
	private long startDate;

	/**
	 * 失效时间，0是永久有效(时间戳)
	 */
	private long endDate;

	/**
	 * 接收帐号
	 */
	private String recUser;

	/**
	 * 发送帐号
	 */
	private String sendUser;

	/**
	 * 发送时间(时间戳)
	 */
	private long sendDate;

	/**
	 * 钥匙的状态（110401：正常使用，110402：待接收，110405：已冻结，110408：已删除，110410：已重置,110500:已过期）
	 */
	private String keyStatus;

	/**
	 * 有效类型（1限时，2永久，3单次，4循环）
	 */
	private int type;

	/**
	 * 用户头像
	 */
	private String avatar;

	/**
	 * 钥匙是否被授权：0-否，1-是
	 */
	private int keyRight;

	/**
	 * 锁别名
	 */
	private String lockAlias;

	/**
	 * 剩余天数
	 */
	private int dayNum;

	public BluetoothKey() {
	}

	public BluetoothKey(int keyId, String alias, long startDate, long endDate, String recUser, String sendUser,
						long sendDate, String keyStatus, int type, String avatar, int keyRight) {
		this.keyId = keyId;
		this.alias = alias;
		this.startDate = startDate;
		this.endDate = endDate;
		this.recUser = recUser;
		this.sendUser = sendUser;
		this.sendDate = sendDate;
		this.keyStatus = keyStatus;
		this.type = type;
		this.avatar = avatar;
		this.keyRight = keyRight;
	}

	public String getLockAlias() {
		return lockAlias;
	}

	public void setLockAlias(String lockAlias) {
		this.lockAlias = lockAlias;
	}

	public int getDayNum() {
		return dayNum;
	}

	public void setDayNum(int dayNum) {
		this.dayNum = dayNum;
	}

	public int getKeyId() {
		return keyId;
	}

	public void setKeyId(int keyId) {
		this.keyId = keyId;
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

	public String getKeyStatus() {
		return keyStatus;
	}

	public void setKeyStatus(String keyStatus) {
		this.keyStatus = keyStatus;
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

	@Override
	public String toString() {
		return "BluetoothKey{" +
				"keyId=" + keyId +
				", alias='" + alias + '\'' +
				", startDate=" + startDate +
				", endDate=" + endDate +
				", recUser='" + recUser + '\'' +
				", sendUser='" + sendUser + '\'' +
				", sendDate=" + sendDate +
				", keyStatus='" + keyStatus + '\'' +
				", type=" + type +
				", avatar='" + avatar + '\'' +
				", keyRight=" + keyRight +
				'}';
	}
}
