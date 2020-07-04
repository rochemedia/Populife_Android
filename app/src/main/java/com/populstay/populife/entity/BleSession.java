package com.populstay.populife.entity;


import com.populstay.populife.enumtype.Operation;
import com.populstay.populife.lock.ILockAddPasscode;
import com.populstay.populife.lock.ILockDeletePasscode;
import com.populstay.populife.lock.ILockGetBattery;
import com.populstay.populife.lock.ILockGetFirmware;
import com.populstay.populife.lock.ILockGetOperateLog;
import com.populstay.populife.lock.ILockGetTime;
import com.populstay.populife.lock.ILockIcCardAdd;
import com.populstay.populife.lock.ILockIcCardClear;
import com.populstay.populife.lock.ILockIcCardDelete;
import com.populstay.populife.lock.ILockIcCardModifyPeriod;
import com.populstay.populife.lock.ILockIcCardSearch;
import com.populstay.populife.lock.ILockLock;
import com.populstay.populife.lock.ILockModifyAutoLockTime;
import com.populstay.populife.lock.ILockModifyKeypadVolume;
import com.populstay.populife.lock.ILockModifyPasscode;
import com.populstay.populife.lock.ILockModifyRemoteUnlockState;
import com.populstay.populife.lock.ILockQueryKeypadVolume;
import com.populstay.populife.lock.ILockResetEkey;
import com.populstay.populife.lock.ILockResetKeyboardPwd;
import com.populstay.populife.lock.ILockResetLock;
import com.populstay.populife.lock.ILockSearchAutoLockTime;
import com.populstay.populife.lock.ILockSetAdminKeyboardPwd;
import com.populstay.populife.lock.ILockSetTime;
import com.populstay.populife.lock.ILockUnlock;

/**
 * Created by Administrator on 2016/7/15 0015.
 */
public class BleSession {

	/**
	 * operation
	 */
	private Operation operation;

	/**
	 * lock mac
	 */
	private String lockmac;

	/**
	 * passcode
	 */
	private String password;

	private long startDate;

	private long endDate;

	private int autoLockTime;
	/**
	 * 键盘按键音状态（0 开启，1 关闭）
	 */
	private int keypadVolumeState;
	private boolean isAdmin;
	private int keyboardPwdType;
	/**
	 * 远程开锁状态（1 开启、2 关闭）
	 */
	private int remoteUnlockState;
	/**
	 * IC card number
	 */
	private long icCardNumber;
	private String keyboardPwdOriginal;
	private String keyboardPwdNew;
	private ILockAddPasscode mILockAddPasscode;
	private ILockLock mILockLock;
	private ILockUnlock mILockUnlock;
	private ILockResetEkey mILockResetEkey;
	private ILockResetKeyboardPwd mILockResetKeyboardPwd;
	private ILockSetAdminKeyboardPwd mILockSetAdminKeyboardPwd;
	private ILockGetTime mILockGetTime;
	private ILockSetTime mILockSetTime;
	private ILockGetFirmware mILockGetFirmware;
	private ILockSearchAutoLockTime mILockSearchAutoLockTime;
	private ILockModifyAutoLockTime mILockModifyAutoLockTime;
	private ILockResetLock mILockResetLock;
	private ILockModifyPasscode mILockModifyPasscode;
	private ILockDeletePasscode mILockDeletePasscode;
	private ILockModifyRemoteUnlockState mILockModifyRemoteUnlockState;
	private ILockIcCardAdd mILockIcCardAdd;
	private ILockIcCardModifyPeriod mILockIcCardModifyPeriod;
	private ILockIcCardDelete mILockIcCardDelete;
	private ILockIcCardClear mILockIcCardClear;
	private ILockIcCardSearch mILockIcCardSearch;
	private ILockGetBattery mILockGetBattery;

	public ILockQueryKeypadVolume getILockQueryKeypadVolume() {
		return mILockQueryKeypadVolume;
	}

	public void setILockQueryKeypadVolume(ILockQueryKeypadVolume ILockQueryKeypadVolume) {
		mILockQueryKeypadVolume = ILockQueryKeypadVolume;
	}

	public ILockModifyKeypadVolume getILockModifyKeypadVolume() {
		return mILockModifyKeypadVolume;
	}

	public void setILockModifyKeypadVolume(ILockModifyKeypadVolume ILockModifyKeypadVolume) {
		mILockModifyKeypadVolume = ILockModifyKeypadVolume;
	}

	private ILockQueryKeypadVolume mILockQueryKeypadVolume;
	private ILockModifyKeypadVolume mILockModifyKeypadVolume;
	private ILockGetOperateLog mILockGetOperateLog;

	public static BleSession getInstance(Operation operation, String lockmac) {
		BleSession bleSession = new BleSession();
		bleSession.setOperation(operation);
		bleSession.setLockmac(lockmac);
		return bleSession;
	}

	public int getKeypadVolumeState() {
		return keypadVolumeState;
	}

	public void setKeypadVolumeState(int keypadVolumeState) {
		this.keypadVolumeState = keypadVolumeState;
	}

	public ILockGetOperateLog getILockGetOperateLog() {
		return mILockGetOperateLog;
	}

	public void setILockGetOperateLog(ILockGetOperateLog ILockGetOperateLog) {
		mILockGetOperateLog = ILockGetOperateLog;
	}

	public ILockGetBattery getILockGetBattery() {
		return mILockGetBattery;
	}

	public void setILockGetBattery(ILockGetBattery ILockGetBattery) {
		mILockGetBattery = ILockGetBattery;
	}

	public ILockIcCardSearch getILockIcCardSearch() {
		return mILockIcCardSearch;
	}

	public void setILockIcCardSearch(ILockIcCardSearch ILockIcCardSearch) {
		mILockIcCardSearch = ILockIcCardSearch;
	}

	public ILockIcCardClear getILockIcCardClear() {
		return mILockIcCardClear;
	}

	public void setILockIcCardClear(ILockIcCardClear ILockIcCardClear) {
		mILockIcCardClear = ILockIcCardClear;
	}

	public ILockIcCardDelete getILockIcCardDelete() {
		return mILockIcCardDelete;
	}

	public void setILockIcCardDelete(ILockIcCardDelete ILockIcCardDelete) {
		mILockIcCardDelete = ILockIcCardDelete;
	}

	public ILockIcCardModifyPeriod getILockIcCardModifyPeriod() {
		return mILockIcCardModifyPeriod;
	}

	public void setILockIcCardModifyPeriod(ILockIcCardModifyPeriod ILockIcCardModifyPeriod) {
		mILockIcCardModifyPeriod = ILockIcCardModifyPeriod;
	}

	public long getIcCardNumber() {
		return icCardNumber;
	}

	public void setIcCardNumber(long icCardNumber) {
		this.icCardNumber = icCardNumber;
	}

	public ILockIcCardAdd getILockIcCardAdd() {
		return mILockIcCardAdd;
	}

	public void setILockIcCardAdd(ILockIcCardAdd ILockIcCardAdd) {
		mILockIcCardAdd = ILockIcCardAdd;
	}

	public ILockModifyRemoteUnlockState getILockModifyRemoteUnlockState() {
		return mILockModifyRemoteUnlockState;
	}

	public void setILockModifyRemoteUnlockState(ILockModifyRemoteUnlockState ILockModifyRemoteUnlockState) {
		mILockModifyRemoteUnlockState = ILockModifyRemoteUnlockState;
	}

	public int getRemoteUnlockState() {
		return remoteUnlockState;
	}

	public void setRemoteUnlockState(int remoteUnlockState) {
		this.remoteUnlockState = remoteUnlockState;
	}

	public ILockDeletePasscode getILockDeletePasscode() {
		return mILockDeletePasscode;
	}

	public void setILockDeletePasscode(ILockDeletePasscode ILockDeletePasscode) {
		mILockDeletePasscode = ILockDeletePasscode;
	}

	public ILockModifyPasscode getILockModifyPasscode() {
		return mILockModifyPasscode;
	}

	public void setILockModifyPasscode(ILockModifyPasscode ILockModifyPasscode) {
		mILockModifyPasscode = ILockModifyPasscode;
	}

	public int getKeyboardPwdType() {
		return keyboardPwdType;
	}

	public void setKeyboardPwdType(int keyboardPwdType) {
		this.keyboardPwdType = keyboardPwdType;
	}

	public String getKeyboardPwdOriginal() {
		return keyboardPwdOriginal;
	}

	public void setKeyboardPwdOriginal(String keyboardPwdOriginal) {
		this.keyboardPwdOriginal = keyboardPwdOriginal;
	}

	public String getKeyboardPwdNew() {
		return keyboardPwdNew;
	}

	public void setKeyboardPwdNew(String keyboardPwdNew) {
		this.keyboardPwdNew = keyboardPwdNew;
	}

	public ILockAddPasscode getILockAddPasscode() {
		return mILockAddPasscode;
	}

	public void setILockAddPasscode(ILockAddPasscode ILockAddPasscode) {
		mILockAddPasscode = ILockAddPasscode;
	}

	public ILockLock getILockLock() {
		return mILockLock;
	}

	public void setILockLock(ILockLock ILockLock) {
		mILockLock = ILockLock;
	}

	public ILockUnlock getILockUnlock() {
		return mILockUnlock;
	}

	public void setILockUnlock(ILockUnlock ILockUnlock) {
		mILockUnlock = ILockUnlock;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean admin) {
		isAdmin = admin;
	}

	public ILockResetEkey getILockResetEkey() {
		return mILockResetEkey;
	}

	public void setILockResetEkey(ILockResetEkey lockResetEkey) {
		mILockResetEkey = lockResetEkey;
	}

	public ILockResetKeyboardPwd getILockResetKeyboardPwd() {
		return mILockResetKeyboardPwd;
	}

	public void setILockResetKeyboardPwd(ILockResetKeyboardPwd ILockResetKeyboardPwd) {
		mILockResetKeyboardPwd = ILockResetKeyboardPwd;
	}

	public ILockResetLock getILockResetLock() {
		return mILockResetLock;
	}

	public void setILockResetLock(ILockResetLock ILockResetLock) {
		mILockResetLock = ILockResetLock;
	}

	public int getAutoLockTime() {
		return autoLockTime;
	}

	public void setAutoLockTime(int autoLockTime) {
		this.autoLockTime = autoLockTime;
	}

	public ILockModifyAutoLockTime getILockModifyAutoLockTime() {
		return mILockModifyAutoLockTime;
	}

	public void setILockModifyAutoLockTime(ILockModifyAutoLockTime ILockModifyAutoLockTime) {
		mILockModifyAutoLockTime = ILockModifyAutoLockTime;
	}

	public ILockSearchAutoLockTime getILockSearchAutoLockTime() {
		return mILockSearchAutoLockTime;
	}

	public void setILockSearchAutoLockTime(ILockSearchAutoLockTime ILockSearchAutoLockTime) {
		mILockSearchAutoLockTime = ILockSearchAutoLockTime;
	}

	public ILockGetFirmware getILockGetFirmware() {
		return mILockGetFirmware;
	}

	public void setILockGetFirmware(ILockGetFirmware ILockGetFirmware) {
		mILockGetFirmware = ILockGetFirmware;
	}

	public ILockSetTime getILockSetTime() {
		return mILockSetTime;
	}

	public void setILockSetTime(ILockSetTime ILockSetTime) {
		mILockSetTime = ILockSetTime;
	}

	public ILockGetTime getILockGetTime() {
		return mILockGetTime;
	}

	public void setILockGetTime(ILockGetTime ILockGetTime) {
		mILockGetTime = ILockGetTime;
	}

	public ILockSetAdminKeyboardPwd getILockSetAdminKeyboardPwd() {
		return mILockSetAdminKeyboardPwd;
	}

	public void setILockSetAdminKeyboardPwd(ILockSetAdminKeyboardPwd ILockSetAdminKeyboardPwd) {
		mILockSetAdminKeyboardPwd = ILockSetAdminKeyboardPwd;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public String getLockmac() {
		return lockmac;
	}

	public void setLockmac(String lockmac) {
		this.lockmac = lockmac;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

}
