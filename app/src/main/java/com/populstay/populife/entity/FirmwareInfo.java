package com.populstay.populife.entity;

/**
 * 固件信息实体类
 */
public class  FirmwareInfo {

	public int errcode;//error code
	public String errmsg;//error message

	public int needUpgrade;//is need upgrade：0-no，1-yes，2-unknown
	public int specialValue;//special value
	public String modelNum;//Product model
	public String hardwareRevision;
	public String firmwareRevision;
	public String version;//Latest version number

	public FirmwareInfo() {
	}

	public FirmwareInfo(int errcode, String errmsg, int needUpgrade, int specialValue, String modelNum,
						String hardwareRevision, String firmwareRevision, String version) {
		this.errcode = errcode;
		this.errmsg = errmsg;
		this.needUpgrade = needUpgrade;
		this.specialValue = specialValue;
		this.modelNum = modelNum;
		this.hardwareRevision = hardwareRevision;
		this.firmwareRevision = firmwareRevision;
		this.version = version;
	}

	public int getErrcode() {
		return errcode;
	}

	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public int getNeedUpgrade() {
		return needUpgrade;
	}

	public void setNeedUpgrade(int needUpgrade) {
		this.needUpgrade = needUpgrade;
	}

	public int getSpecialValue() {
		return specialValue;
	}

	public void setSpecialValue(int specialValue) {
		this.specialValue = specialValue;
	}

	public String getModelNum() {
		return modelNum;
	}

	public void setModelNum(String modelNum) {
		this.modelNum = modelNum;
	}

	public String getHardwareRevision() {
		return hardwareRevision;
	}

	public void setHardwareRevision(String hardwareRevision) {
		this.hardwareRevision = hardwareRevision;
	}

	public String getFirmwareRevision() {
		return firmwareRevision;
	}

	public void setFirmwareRevision(String firmwareRevision) {
		this.firmwareRevision = firmwareRevision;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
