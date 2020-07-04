package com.populstay.populife.entity;

/**
 * Created by Jerry
 */
public class LockAction {
	private LockActionType actionType;
	private int iconResInt;
	private int titleResInt;
	private boolean isEnable;

	public LockAction() {
	}

	public LockAction(LockActionType actionType, int iconResInt, int titleResInt, boolean isEnable) {
		this.actionType = actionType;
		this.iconResInt = iconResInt;
		this.titleResInt = titleResInt;
		this.isEnable = isEnable;
	}

	public LockActionType getActionType() {
		return actionType;
	}

	public void setActionType(LockActionType actionType) {
		this.actionType = actionType;
	}

	public int getIconResInt() {
		return iconResInt;
	}

	public void setIconResInt(int iconResInt) {
		this.iconResInt = iconResInt;
	}

	public int getTitleResInt() {
		return titleResInt;
	}

	public void setTitleResInt(int titleResInt) {
		this.titleResInt = titleResInt;
	}

	public boolean isEnable() {
		return isEnable;
	}

	public void setEnable(boolean enable) {
		isEnable = enable;
	}

	@Override
	public String toString() {
		return "LockAction{" +
				"actionType=" + actionType +
				", iconResInt=" + iconResInt +
				", titleResInt=" + titleResInt +
				", isEnable=" + isEnable +
				'}';
	}

	public enum LockActionType {
		/**
		 * 发送钥匙
		 */
		SEND_EKEY,
		/**
		 * 发送密码
		 */
		SEND_PASSCODE,
		/**
		 * 钥匙管理
		 */
		EKEY_MANAGE,
		/**
		 * 密码管理
		 */
		PASSCODE_MANAGE,
		/**
		 * 操作记录
		 */
		OPERATE_RECORD,
		/**
		 * 锁设置
		 */
		SETTINGS,
		/**
		 * IC 卡相关
		 */
		IC_CARDS
	}
}
