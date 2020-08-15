package com.populstay.populife.enumtype;

public enum Operation {
	/**
	 * add admin
	 */
	ADD_ADMIN,

	/**
	 * unlock
	 */
	UNLOCK,

	/**
	 * lock
	 */
	LOCK,

	/**
	 * reset keyboard passcode
	 */
	RESET_KEYBOARD_PASSWORD,

	/**
	 * reset ekey
	 */
	RESET_EKEY,

	/**
	 * set lock time
	 */
	SET_LOCK_TIME,
	/**
	 * get lock time
	 */
	GET_LOCK_TIME,

	/**
	 * get operation log
	 */
	GET_OPERATE_LOG,

	/**
	 * parking lock up
	 */
	LOCKCAR_UP,

	/**
	 * parking lock down
	 */
	LOCKCAR_DOWN,

	/**
	 * click to unlock
	 */
	CLICK_UNLOCK,

	/**
	 * set admin passcode
	 */
	SET_ADMIN_KEYBOARD_PASSWORD,

	/**
	 * set delete passcode
	 */
	SET_DELETE_PASSWORD,

	/**
	 * Restore factory settings
	 */
	RESET_LOCK,

	/**
	 * set lock time
	 */
	CHECK_LOCKTIME,

	/**
	 * modify key name
	 */
	MODIFY_KEYNAME,

	/**
	 * delete keyboard passcode
	 */
	DELETE_ONE_KEYBOARDPASSWORD,
	/**
	 * get lock version information
	 */
	GET_LOCK_VERSION_INFO,
	/**
	 * add customized passcode
	 */
	ADD_PASSCODE,
	/**
	 * search auto lock time
	 */
	SEARCH_AUTO_LOCK_TIME,
	/**
	 * modify auto lock time
	 */
	MODIFY_AUTO_LOCK_TIME,
	/**
	 * modify keyboard password
	 */
	MODIFY_KEYBOARD_PASSWORD,
	/**
	 * modify remote unlock status
	 */
	REMOTE_UNLOCK_SWITCH,
	/**
	 * add IC card
	 */
	ADD_IC_CARD,
	/**
	 * modify IC card period
	 */
	MODIFY_IC_CARD_PERIOD,
	/**
	 * delete an IC card
	 */
	DELETE_IC_CARD,
	/**
	 * clear all IC cards
	 */
	CLEAR_IC_CARDS,
	/**
	 * search Ic cards and upload to sever
	 */
	SEARCH_IC_CARDS,
	/**
	 * get lock battery
	 */
	GET_LOCK_BATTERY,
	/**
	 * query keypad volume
	 */
	QUERY_KEYPAD_VOLUME,
	/**
	 * modify keypad volume
	 */
	MODIFY_KEYPAD_VOLUME,
}