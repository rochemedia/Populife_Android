package com.populstay.populife.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.multidex.MultiDex;

import com.populstay.populife.R;
import com.populstay.populife.activity.AddDeviceSuccessActivity;
import com.populstay.populife.activity.LockNameAddActivity;
import com.populstay.populife.base.BaseApplication;
import com.populstay.populife.constant.BleConstant;
import com.populstay.populife.entity.BleSession;
import com.populstay.populife.entity.Key;
import com.populstay.populife.enumtype.Operation;
import com.populstay.populife.eventbus.Event;
import com.populstay.populife.home.entity.HomeDeviceInfo;
import com.populstay.populife.ui.loader.PeachLoader;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.ttlock.bl.sdk.api.TTLockAPI;
import com.ttlock.bl.sdk.callback.TTLockCallback;
import com.ttlock.bl.sdk.entity.DeviceInfo;
import com.ttlock.bl.sdk.entity.Error;
import com.ttlock.bl.sdk.entity.LockData;
import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice;

import org.greenrobot.eventbus.EventBus;

import java.util.TimeZone;

import cn.ittiger.player.Config;
import cn.ittiger.player.PlayerManager;
import cn.ittiger.player.factory.ExoPlayerFactory;
import cn.ittiger.player.factory.MediaPlayerFactory;

/**
 * Created by Jerry
 */

public class MyApplication extends BaseApplication {

	/**
	 * bluetooth operation
	 */
	public static BleSession bleSession = BleSession.getInstance(Operation.UNLOCK, null);
	/**
	 * TTLockAPI
	 */
	@SuppressLint("StaticFieldLeak")
	public static TTLockAPI mTTLockAPI;
	/**
	 * current used key
	 */
	public static Key CURRENT_KEY = new Key(); // 全局当前正在使用的 key
	private Activity curActivity;
	/**
	 * Instantiate TTLockCallback Object
	 */
	private TTLockCallback mTTLockCallback = new TTLockCallback() {
		@Override
		public void onFoundDevice(ExtendedBluetoothDevice extendedBluetoothDevice) {
			//found device and broadcast
			broadcastUpdate(BleConstant.ACTION_BLE_DEVICE, BleConstant.DEVICE, extendedBluetoothDevice);
			//todo 读取本地数据
//			Key localKey = DbService.getKeyByLockmac(extendedBluetoothDevice.getAddress());
//			if (extendedBluetoothDevice.getAddress().equals(bleSession.getLockmac())) {
//				mTTLockAPI.connect(extendedBluetoothDevice);
//			}
		}

		@Override
		public void onDeviceConnected(ExtendedBluetoothDevice extendedBluetoothDevice) {
			//获取本地锁信息

			//uid equal to openid
			int uid = PeachPreference.getOpenid();
			switch (bleSession.getOperation()) {
				case ADD_ADMIN:
					//todo 判断要添加的锁是否已存在?
					mTTLockAPI.lockInitialize(extendedBluetoothDevice);
					break;

				case UNLOCK:
				case CLICK_UNLOCK:
					if (CURRENT_KEY != null) {
						if (bleSession.isAdmin())
							mTTLockAPI.unlockByAdministrator(extendedBluetoothDevice, uid, CURRENT_KEY.getLockVersion(), CURRENT_KEY.getAdminPwd(), CURRENT_KEY.getLockKey(), CURRENT_KEY.getLockFlagPos(), DateUtil.getCurTimeMillis(), CURRENT_KEY.getAesKeyStr(), CURRENT_KEY.getTimezoneRawOffset());
						else
							mTTLockAPI.unlockByUser(extendedBluetoothDevice, uid, CURRENT_KEY.getLockVersion(), CURRENT_KEY.getStartDate(), CURRENT_KEY.getEndDate(), CURRENT_KEY.getLockKey(), CURRENT_KEY.getLockFlagPos(), CURRENT_KEY.getAesKeyStr(), CURRENT_KEY.getTimezoneRawOffset());
					}
					break;

				case LOCK:
					if (CURRENT_KEY != null) {
						mTTLockAPI.lock(extendedBluetoothDevice, uid, CURRENT_KEY.getLockVersion(), CURRENT_KEY.getStartDate(), CURRENT_KEY.getEndDate(), CURRENT_KEY.getLockKey(), CURRENT_KEY.getLockFlagPos(), DateUtil.getCurTimeMillis(), CURRENT_KEY.getAesKeyStr(), CURRENT_KEY.getTimezoneRawOffset());
					}
					break;

				case SET_ADMIN_KEYBOARD_PASSWORD:
					if (CURRENT_KEY != null) {
						mTTLockAPI.setAdminKeyboardPassword(extendedBluetoothDevice, uid, CURRENT_KEY.getLockVersion(), CURRENT_KEY.getAdminPwd(), CURRENT_KEY.getLockKey(), CURRENT_KEY.getLockFlagPos(), CURRENT_KEY.getAesKeyStr(), bleSession.getPassword());
					}
					break;

				case GET_LOCK_TIME:
					if (CURRENT_KEY != null) {
						mTTLockAPI.getLockTime(extendedBluetoothDevice, CURRENT_KEY.getLockVersion(), CURRENT_KEY.getAesKeyStr(), CURRENT_KEY.getTimezoneRawOffset());
					}
					break;

				case SET_LOCK_TIME:
					if (CURRENT_KEY != null) {
						mTTLockAPI.setLockTime(extendedBluetoothDevice, PeachPreference.getOpenid(), CURRENT_KEY.getLockVersion(), CURRENT_KEY.getLockKey(), DateUtil.getCurTimeMillis(), CURRENT_KEY.getLockFlagPos(), CURRENT_KEY.getAesKeyStr(), CURRENT_KEY.getTimezoneRawOffset());
					}
					break;

				case SEARCH_AUTO_LOCK_TIME:
					if (CURRENT_KEY != null) {
						mTTLockAPI.searchAutoLockTime(extendedBluetoothDevice, PeachPreference.getOpenid(),
								CURRENT_KEY.getLockVersion(), CURRENT_KEY.getAdminPwd(), CURRENT_KEY.getLockKey(),
								CURRENT_KEY.getLockFlagPos(), CURRENT_KEY.getAesKeyStr());
					}
					break;

				case MODIFY_AUTO_LOCK_TIME:
					if (CURRENT_KEY != null) {
						mTTLockAPI.modifyAutoLockTime(extendedBluetoothDevice, PeachPreference.getOpenid(),
								CURRENT_KEY.getLockVersion(), CURRENT_KEY.getAdminPwd(), CURRENT_KEY.getLockKey(),
								CURRENT_KEY.getLockFlagPos(), bleSession.getAutoLockTime(), CURRENT_KEY.getAesKeyStr());
					}
					break;

				case RESET_LOCK:
					if (CURRENT_KEY != null) {
						mTTLockAPI.resetLock(extendedBluetoothDevice, uid, CURRENT_KEY.getLockVersion(),
								CURRENT_KEY.getAdminPwd(), CURRENT_KEY.getLockKey(), CURRENT_KEY.getLockFlagPos(), CURRENT_KEY.getAesKeyStr());
					}
					break;

				case RESET_KEYBOARD_PASSWORD:
					if (CURRENT_KEY != null) {
						mTTLockAPI.resetKeyboardPassword(extendedBluetoothDevice, PeachPreference.getOpenid(),
								CURRENT_KEY.getLockVersion(), CURRENT_KEY.getAdminPwd(), CURRENT_KEY.getLockKey(), CURRENT_KEY.getLockFlagPos(), CURRENT_KEY.getAesKeyStr());
					}
					break;

				case RESET_EKEY:
					if (CURRENT_KEY != null) {
						mTTLockAPI.resetEKey(extendedBluetoothDevice, PeachPreference.getOpenid(),
								CURRENT_KEY.getLockVersion(), CURRENT_KEY.getAdminPwd(), CURRENT_KEY.getLockFlagPos(), CURRENT_KEY.getAesKeyStr());
					}
					break;

				case ADD_PASSCODE:
					if (CURRENT_KEY != null) {
						mTTLockAPI.addPeriodKeyboardPassword(extendedBluetoothDevice,
								PeachPreference.getOpenid(), CURRENT_KEY.getLockVersion(),
								CURRENT_KEY.getAdminPwd(), CURRENT_KEY.getLockKey(), CURRENT_KEY.getLockFlagPos(), bleSession.getPassword(),
								bleSession.getStartDate(), bleSession.getEndDate(), CURRENT_KEY.getAesKeyStr(),
								(long) TimeZone.getDefault().getOffset(DateUtil.getCurTimeMillis()));
					}
					break;

				case MODIFY_KEYBOARD_PASSWORD:
					if (CURRENT_KEY != null) {
						mTTLockAPI.modifyKeyboardPassword(extendedBluetoothDevice, PeachPreference.getOpenid(),
								CURRENT_KEY.getLockVersion(), CURRENT_KEY.getAdminPwd(), CURRENT_KEY.getLockKey(),
								CURRENT_KEY.getLockFlagPos(), bleSession.getKeyboardPwdType(),
								bleSession.getKeyboardPwdOriginal(), bleSession.getKeyboardPwdNew(),
								bleSession.getStartDate(), bleSession.getEndDate(),
								CURRENT_KEY.getAesKeyStr(), DateUtil.getTimeZoneOffset());
					}
					break;

				case DELETE_ONE_KEYBOARDPASSWORD:
					if (CURRENT_KEY != null) {
						mTTLockAPI.deleteOneKeyboardPassword(extendedBluetoothDevice, PeachPreference.getOpenid(),
								CURRENT_KEY.getLockVersion(), CURRENT_KEY.getAdminPwd(), CURRENT_KEY.getLockKey(),
								CURRENT_KEY.getLockFlagPos(), bleSession.getKeyboardPwdType(),
								bleSession.getKeyboardPwdOriginal(), CURRENT_KEY.getAesKeyStr());
					}
					break;

				case REMOTE_UNLOCK_SWITCH:
					if (CURRENT_KEY != null) {
						mTTLockAPI.operateRemoteUnlockSwitch(extendedBluetoothDevice, 2, bleSession.getRemoteUnlockState(),
								PeachPreference.getOpenid(), CURRENT_KEY.getLockVersion(), CURRENT_KEY.getAdminPwd(),
								CURRENT_KEY.getLockKey(), CURRENT_KEY.getLockFlagPos(), CURRENT_KEY.getAesKeyStr());
					}
					break;

				case ADD_IC_CARD:
					if (CURRENT_KEY != null) {
						mTTLockAPI.addICCard(extendedBluetoothDevice, PeachPreference.getOpenid(),
								CURRENT_KEY.getLockVersion(), CURRENT_KEY.getAdminPwd(),
								CURRENT_KEY.getLockKey(), CURRENT_KEY.getLockFlagPos(),
								CURRENT_KEY.getAesKeyStr());
					}
					break;

				case MODIFY_IC_CARD_PERIOD:
					if (CURRENT_KEY != null) {
						mTTLockAPI.modifyICPeriod(extendedBluetoothDevice, PeachPreference.getOpenid(),
								CURRENT_KEY.getLockVersion(), CURRENT_KEY.getAdminPwd(), CURRENT_KEY.getLockKey(),
								CURRENT_KEY.getLockFlagPos(), bleSession.getIcCardNumber(), bleSession.getStartDate(),
								bleSession.getEndDate(), CURRENT_KEY.getAesKeyStr(), DateUtil.getTimeZoneOffset());
					}
					break;

				case DELETE_IC_CARD:
					if (CURRENT_KEY != null) {
						mTTLockAPI.deleteICCard(extendedBluetoothDevice, PeachPreference.getOpenid(),
								CURRENT_KEY.getLockVersion(), CURRENT_KEY.getAdminPwd(), CURRENT_KEY.getLockKey(),
								CURRENT_KEY.getLockFlagPos(), bleSession.getIcCardNumber(), CURRENT_KEY.getAesKeyStr());
					}
					break;

				case CLEAR_IC_CARDS:
					if (CURRENT_KEY != null) {
						mTTLockAPI.clearICCard(extendedBluetoothDevice, PeachPreference.getOpenid(), CURRENT_KEY.getLockVersion(),
								CURRENT_KEY.getAdminPwd(), CURRENT_KEY.getLockKey(), CURRENT_KEY.getLockFlagPos(),
								CURRENT_KEY.getAesKeyStr());
					}
					break;

				case SEARCH_IC_CARDS:
					if (CURRENT_KEY != null) {
						mTTLockAPI.searchICCard(extendedBluetoothDevice, PeachPreference.getOpenid(),
								CURRENT_KEY.getLockVersion(), CURRENT_KEY.getAdminPwd(), CURRENT_KEY.getLockKey(),
								CURRENT_KEY.getLockFlagPos(), CURRENT_KEY.getAesKeyStr(), DateUtil.getTimeZoneOffset());
					}
					break;

				case GET_LOCK_BATTERY:
					if (CURRENT_KEY != null) {
						mTTLockAPI.getElectricQuantity(null, CURRENT_KEY.getLockVersion(), CURRENT_KEY.getAesKeyStr());
					}
					break;

				case QUERY_KEYPAD_VOLUME:
					if (CURRENT_KEY != null) {
						mTTLockAPI.operateAudioSwitch(extendedBluetoothDevice, 1, 0,
								PeachPreference.getOpenid(), CURRENT_KEY.getLockVersion(), CURRENT_KEY.getAdminPwd(),
								CURRENT_KEY.getLockKey(), CURRENT_KEY.getLockFlagPos(), CURRENT_KEY.getAesKeyStr());
					}
					break;

				case MODIFY_KEYPAD_VOLUME:
					if (CURRENT_KEY != null) {
						mTTLockAPI.operateAudioSwitch(extendedBluetoothDevice, 2, bleSession.getKeypadVolumeState(),
								PeachPreference.getOpenid(), CURRENT_KEY.getLockVersion(), CURRENT_KEY.getAdminPwd(),
								CURRENT_KEY.getLockKey(), CURRENT_KEY.getLockFlagPos(), CURRENT_KEY.getAesKeyStr());
					}
					break;

				case GET_OPERATE_LOG:
					if (CURRENT_KEY != null) {
						mTTLockAPI.getOperateLog(extendedBluetoothDevice, CURRENT_KEY.getLockVersion(),
								CURRENT_KEY.getAesKeyStr(), DateUtil.getTimeZoneOffset());
					}
					break;

				default:
					break;
			}
		}

		@Override
		public void onDeviceDisconnected(ExtendedBluetoothDevice extendedBluetoothDevice) {
			PeachLoader.stopLoading();
			switch (bleSession.getOperation()) {
				case UNLOCK:
				case CLICK_UNLOCK:
					bleSession.getILockUnlock().onUnlockFinish();
					break;

				case LOCK:
					bleSession.getILockLock().onLockFinish();
					break;

				case RESET_LOCK:
					bleSession.getILockResetLock().onFinish();
					break;
				case ADD_PASSCODE:
					bleSession.getILockAddPasscode().onTimeOut();
					break;

				default:
					break;
			}
		}

		@Override
		public void onGetLockVersion(ExtendedBluetoothDevice extendedBluetoothDevice, int protocolType, int protocolVersion, int scene, int groupId, int orgId, Error error) {

		}

		@Override
		public void onLockInitialize(ExtendedBluetoothDevice extendedBluetoothDevice, LockData lockData, Error error) {


			PeachLogger.d("LOCK_INIT", "extendedBluetoothDevice="+",extendedBluetoothDevice="+extendedBluetoothDevice.toString());
			PeachLogger.d("LOCK_INIT", "onLockInitialize="+",lockData="+(lockData != null ? lockData.toString() : "null"));

			PeachLoader.stopLoading();
			if (error == Error.INVALID_VENDOR) {
				myToast(R.string.lock_not_supported);
			} else if (error == Error.SUCCESS) {
				mTTLockAPI.stopBTDeviceScan();
				String lockDataJson = lockData.toJson();
				/*Intent intent = new Intent(getApplication(), LockNameAddActivity.class);
				intent.putExtra(LockNameAddActivity.KEY_LOCK_INIT_DATA, lockDataJson);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);*/

				//AddDeviceSuccessActivity.actionStart(getApplication(), HomeDeviceInfo.IDeviceModel.MODEL_LOCK_DEADBOLT);
				EventBus.getDefault().post(new Event(Event.EventType.LOCK_LOCAL_INITIALIZE_SUCCEED,lockDataJson));


			} else {//failure
				myToast(R.string.note_lock_init_fail);
			}
		}

		@Override
		public void onResetEKey(ExtendedBluetoothDevice extendedBluetoothDevice, int lockFlagPos, Error error) {
			if (error == Error.SUCCESS) {
				bleSession.getILockResetEkey().onSuccess();
			} else {
				bleSession.getILockResetEkey().onFail();
			}
		}

		@Override
		public void onSetLockName(ExtendedBluetoothDevice extendedBluetoothDevice, String lockname, Error error) {

		}

		@Override
		public void onSetAdminKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, String adminCode, Error error) {
			if (error == Error.SUCCESS) {
				bleSession.getILockSetAdminKeyboardPwd().onSetPwdSuccess();
			} else {
				bleSession.getILockSetAdminKeyboardPwd().onSetPwdFail();
			}
		}

		@Override
		public void onSetDeletePassword(ExtendedBluetoothDevice extendedBluetoothDevice, String deleteCode, Error error) {

		}

		@Override
		public void onUnlock(ExtendedBluetoothDevice extendedBluetoothDevice, int uid, int uniqueid, long lockTime, Error error) {
			if (error == Error.SUCCESS) {
				//开锁成功
				int batteryCapacity = extendedBluetoothDevice.getBatteryCapacity();
				bleSession.getILockUnlock().onUnlockSuccess(batteryCapacity);
			} else {
				bleSession.getILockUnlock().onUnlockFail();
			}
		}

		@Override
		public void onSetLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {
			if (error == Error.SUCCESS) {
				bleSession.getILockSetTime().onSetTimeSuccess();
			} else {
				bleSession.getILockSetTime().onSetTimeFail();
			}
		}

		@Override

		public void onGetLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, long lockTime, Error error) {
			if (error == Error.SUCCESS) {
				bleSession.getILockGetTime().onGetTimeSuccess(lockTime);
			} else {
				bleSession.getILockGetTime().onGetTimeFail();
			}
		}

		@Override
		public void onResetKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, String pwdInfo, long timestamp, Error error) {
			if (error == Error.SUCCESS) {
				bleSession.getILockResetKeyboardPwd().onSuccess(pwdInfo, timestamp);
			} else {
				bleSession.getILockResetKeyboardPwd().onFail();
			}
		}

		@Override
		public void onSetMaxNumberOfKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int validPwdNum, Error error) {

		}

		@Override
		public void onResetKeyboardPasswordProgress(ExtendedBluetoothDevice extendedBluetoothDevice, int progress, Error error) {

		}

		@Override
		public void onResetLock(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {
			if (error == Error.SUCCESS) {
				bleSession.getILockResetLock().onSuccess();
			} else {
				bleSession.getILockResetLock().onFail();
			}
		}

		@Override
		public void onAddKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int keyboardPwdType, String password, long startDate, long endDate, Error error) {
			if (error == Error.SUCCESS) {
				bleSession.getILockAddPasscode().onSuccess();
			} else {
				bleSession.getILockAddPasscode().onFail();
			}
		}

		@Override
		public void onModifyKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int keyboardPwdType, String originPwd, String newPwd, Error error) {
			if (error == Error.SUCCESS) {
				bleSession.getILockModifyPasscode().onSuccess();
			} else {
				bleSession.getILockModifyPasscode().onFail(error);
			}
		}

		@Override
		public void onDeleteOneKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int keyboardPwdType, String deletedPwd, Error error) {
			if (error == Error.SUCCESS) {
				bleSession.getILockDeletePasscode().onSuccess();
			} else {
				bleSession.getILockDeletePasscode().onFail();
			}
		}

		@Override
		public void onDeleteAllKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {

		}

		@Override
		public void onGetOperateLog(ExtendedBluetoothDevice extendedBluetoothDevice, String records, Error error) {
			if (error == Error.SUCCESS) {
				bleSession.getILockGetOperateLog().onSuccess(records);
			} else {
				bleSession.getILockGetOperateLog().onFail();
			}
		}

		@Override
		public void onSearchDeviceFeature(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int specialValue, Error error) {

		}

		@Override
		public void onAddICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int status, int battery, long cardNumber, Error error) {
			if (error == Error.SUCCESS) {
				if (status == 1) {
					bleSession.getILockIcCardAdd().onEnterAddMode();
				} else if (status == 2)
					if (cardNumber != 0) {
						bleSession.getILockIcCardAdd().onSuccess(cardNumber);
					} else {
						bleSession.getILockIcCardAdd().onFail();
					}
			} else {
				bleSession.getILockIcCardAdd().onFail();
			}
		}

		@Override
		public void onModifyICCardPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long cardNumber, long startDate, long endDate, Error error) {
			if (error == Error.SUCCESS) {
				bleSession.getILockIcCardModifyPeriod().onSuccess();
			} else {
				bleSession.getILockIcCardModifyPeriod().onFail();
			}
		}

		@Override
		public void onDeleteICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long cardNumber, Error error) {
			if (error == Error.SUCCESS) {
				bleSession.getILockIcCardDelete().onSuccess();
			} else {
				bleSession.getILockIcCardDelete().onFail(error);
			}
		}

		@Override
		public void onClearICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error) {
			if (error == Error.SUCCESS) {
				bleSession.getILockIcCardClear().onSuccess();
			} else {
				bleSession.getILockIcCardClear().onFail(error);
			}
		}

		@Override
		public void onSetWristbandKeyToLock(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error) {

		}

		@Override
		public void onSetWristbandKeyToDev(Error error) {

		}

		@Override
		public void onSetWristbandKeyRssi(Error error) {

		}

		@Override
		public void onAddFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int status, int battery, long fingerPrintNo, Error error) {

		}

		@Override
		public void onAddFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int status, int battery, long fingerPrintNo, int totalCount, Error error) {

		}

		@Override
		public void onFingerPrintCollection(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error) {

		}

		@Override
		public void onFingerPrintCollection(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int currentCount, int totalCount, Error error) {

		}

		@Override
		public void onModifyFingerPrintPeriod(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long fingerPrintNo, long startDate, long endDate, Error error) {

		}

		@Override
		public void onDeleteFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, long fingerPrintNo, Error error) {

		}

		@Override
		public void onClearFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error) {

		}

		/**
		 * @param extendedBluetoothDevice
		 * @param battery
		 * @param currentTime 当前自动闭锁时间（0为“不自动闭锁”）
		 * @param minTime 可设置的最短自动闭锁时间
		 * @param maxTime 可设置的最长自动闭锁时间
		 * @param error
		 */
		@Override
		public void onSearchAutoLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int currentTime, int minTime, int maxTime, Error error) {
			if (error == Error.SUCCESS) {
				bleSession.getILockSearchAutoLockTime().onSearchAutoLockTimeSuccess(currentTime);
			} else {
				bleSession.getILockSearchAutoLockTime().onSearchAutoLockTimeFail();
			}
		}

		@Override
		public void onModifyAutoLockTime(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int autoLockTime, Error error) {
			if (error == Error.SUCCESS) {
				bleSession.getILockModifyAutoLockTime().onModifyAutoLockTimeSuccess();
			} else {
				bleSession.getILockModifyAutoLockTime().onModifyAutoLockTimeFail();
			}
		}

		@Override
		public void onReadDeviceInfo(ExtendedBluetoothDevice extendedBluetoothDevice, DeviceInfo deviceInfo, Error error) {

		}

		@Override
		public void onEnterDFUMode(ExtendedBluetoothDevice extendedBluetoothDevice, Error error) {

		}

		@Override
		public void onGetLockSwitchState(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int status, Error error) {

		}

		@Override
		public void onLock(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int uid, int uniqueid, long lockTime, Error error) {
			if (error == Error.SUCCESS) {
				//闭锁成功
				int batteryCapacity = extendedBluetoothDevice.getBatteryCapacity();
				bleSession.getILockLock().onLockSuccess(batteryCapacity);
			} else {
				//闭锁失败
				bleSession.getILockLock().onLockFail();
			}
		}

		@Override
		public void onScreenPasscodeOperate(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int status, Error error) {

		}

		@Override
		public void onRecoveryData(ExtendedBluetoothDevice extendedBluetoothDevice, int op, Error error) {

		}

		@Override
		public void onSearchICCard(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, String json, Error error) {
			if (error == Error.SUCCESS) {
				bleSession.getILockIcCardSearch().onSuccess(json);
			} else {
				bleSession.getILockIcCardSearch().onFail(error);
			}
		}

		@Override
		public void onSearchFingerPrint(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, String json, Error error) {

		}

		@Override
		public void onSearchPasscode(ExtendedBluetoothDevice extendedBluetoothDevice, String json, Error error) {

		}

		@Override
		public void onSearchPasscodeParam(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, String pwdInfo, long timestamp, Error error) {

		}

		/**
		 * @param operateType 操作类型（1 get获取、2 modify修改）
		 * @param state  远程开锁开关状态（1 on 打开、0 off关闭）
		 * @param specialValue 设备特征值
		 */
		@Override
		public void onOperateRemoteUnlockSwitch(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int operateType, int state, int specialValue, Error error) {
			if (error == Error.SUCCESS) {
				//修改远程开锁状态成功
				bleSession.getILockModifyRemoteUnlockState().onSuccess(battery, operateType, state, specialValue);
			} else {
				//修改失败
				bleSession.getILockModifyRemoteUnlockState().onFail(error);
			}
		}

		@Override
		public void onGetElectricQuantity(ExtendedBluetoothDevice extendedBluetoothDevice, int electricQuantity, Error error) {
			if (error == Error.SUCCESS) {
				//读取锁电量成功
				bleSession.getILockGetBattery().onGetBatterySuccess(electricQuantity);
			} else {
				bleSession.getILockGetBattery().onGetBatteryFail();
			}
		}

		@Override
		public void onOperateAudioSwitch(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int operateType, int state, Error error) {
			if (error == Error.SUCCESS) {
				if (operateType == 1) // 查询
					bleSession.getILockQueryKeypadVolume().onSuccess(state);
				else if (operateType == 2) // 修改
					bleSession.getILockModifyKeypadVolume().onSuccess(state);
			} else {
				if (operateType == 1) // 查询
					bleSession.getILockQueryKeypadVolume().onFail();
				else if (operateType == 2) // 修改
					bleSession.getILockModifyKeypadVolume().onFail();
			}
		}

		@Override
		public void onOperateRemoteControl(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int operateType, int keyValue, Error error) {

		}

		@Override
		public void onOperateDoorSensorLocking(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int operationType, int operationValue, Error error) {

		}

		@Override
		public void onGetDoorSensorState(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, int state, Error error) {

		}

		@Override
		public void onSetNBServer(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, Error error) {

		}

		@Override
		public void onGetAdminKeyboardPassword(ExtendedBluetoothDevice extendedBluetoothDevice, int battery, String adminCode, Error error) {

		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		MultiDex.install(this);
		init();
	}

	private void init() {
		initLock();
		initVideoPlayerConfig();
	}

	private void initVideoPlayerConfig(){
		PlayerManager.loadConfig(
				new Config.Builder(this)
						.cache(false)
						.buildPlayerFactory(new MediaPlayerFactory())
						.build()
		);
	}


	/**
	 * Init TTLockAPI Object
	 */
	private void initLock() {
		mTTLockAPI = new TTLockAPI(getApplication(), mTTLockCallback);
	}

	//TODO:
	private <K, V extends Parcelable> void broadcastUpdate(String action, K key, V value) {
		final Intent intent = new Intent(action);
		if (key != null) {
			Bundle bundle = new Bundle();
			bundle.putParcelable((String) key, value);
			intent.putExtras(bundle);
		}
		sendBroadcast(intent);
	}
}
