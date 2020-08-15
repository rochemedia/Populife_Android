package com.populstay.populife.entity;

/**
 * Created by Jerry
 */
public class GatewayBindedLock {

	/**
	 * 锁ID
	 */
	private int lockId;

	/**
	 * 锁mac地址
	 */
	private String lockMac;

	/**
	 * 锁名称
	 */
	private String lockName;

	/**
	 * 网关与锁之间信号强度，参考标准：大于-75为强，大于-85小于-75为中，小于-85为弱。
	 */
	private int signal;

	/**
	 * RSSI信号强度更新时间
	 */
	private long updateDate;

	/**
	 * 锁别名
	 */
	private String alias;

	/**
	 * 锁特征值
	 */
	private int specialValue;

	public GatewayBindedLock() {

	}

	public GatewayBindedLock(int lockId, String lockMac, String lockName, int signal, long updateDate, String alias, int specialValue) {
		this.lockId = lockId;
		this.lockMac = lockMac;
		this.lockName = lockName;
		this.signal = signal;
		this.updateDate = updateDate;
		this.alias = alias;
		this.specialValue = specialValue;
	}

	public int getLockId() {
		return lockId;
	}

	public void setLockId(int lockId) {
		this.lockId = lockId;
	}

	public String getLockMac() {
		return lockMac;
	}

	public void setLockMac(String lockMac) {
		this.lockMac = lockMac;
	}

	public String getLockName() {
		return lockName;
	}

	public void setLockName(String lockName) {
		this.lockName = lockName;
	}

	public int getSignal() {
		return signal;
	}

	public void setSignal(int signal) {
		this.signal = signal;
	}

	public long getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(long updateDate) {
		this.updateDate = updateDate;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getSpecialValue() {
		return specialValue;
	}

	public void setSpecialValue(int specialValue) {
		this.specialValue = specialValue;
	}
}
