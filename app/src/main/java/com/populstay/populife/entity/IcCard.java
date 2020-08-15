package com.populstay.populife.entity;

/**
 * Created by Jerry
 */
public class IcCard {

	/**
	 * 锁id
	 */
	private Integer lockId;

	/**
	 * ic卡号
	 */
	private String cardNumber;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 有效期开始时间
	 */
	private Long startDate;

	/**
	 * 有效期结束时间
	 */
	private Long endDate;

	/**
	 * ic卡ID
	 */
	private Integer cardId;

	/**
	 * 创建时间
	 */
	private Long createDate;

	/**
	 * 类型，1：永久，2：限时
	 */
	private Integer type;

	/**
	 * 过期状态，Y过期， N未过期
	 */
	private String expire;

	public IcCard() {
	}

	public IcCard(Integer lockId, String cardNumber, String remark, Long startDate, Long endDate,
				  Integer cardId, Long createDate, Integer type, String expire) {
		this.lockId = lockId;
		this.cardNumber = cardNumber;
		this.remark = remark;
		this.startDate = startDate;
		this.endDate = endDate;
		this.cardId = cardId;
		this.createDate = createDate;
		this.type = type;
		this.expire = expire;
	}

	public Integer getLockId() {
		return lockId;
	}

	public void setLockId(Integer lockId) {
		this.lockId = lockId;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

	public Integer getCardId() {
		return cardId;
	}

	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}

	public Long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Long createDate) {
		this.createDate = createDate;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getExpire() {
		return expire;
	}

	public void setExpire(String expire) {
		this.expire = expire;
	}

	@Override
	public String toString() {
		return "IcCard{" +
				"lockId=" + lockId +
				", cardNumber='" + cardNumber + '\'' +
				", remark='" + remark + '\'' +
				", startDate=" + startDate +
				", endDate=" + endDate +
				", cardId=" + cardId +
				", createDate=" + createDate +
				", type=" + type +
				", expire='" + expire + '\'' +
				'}';
	}
}
