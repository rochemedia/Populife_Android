package com.populstay.populife.entity;

/**
 * Created by Jerry
 */
public class LockOperateRecord {
	//日志id
	String id;

	//操作者昵称（优先取用户昵称|手机号|邮箱地址）
	String nickname;

	//操作者头像url
	String avatar;

	//日志内容
	String content;

	//操作时间（毫秒时间戳）
	String createDate;

	//事件类型，1：App开锁，2：App闭锁, 3密码开锁
	String event;

	public LockOperateRecord() {
	}

	public LockOperateRecord(String id, String nickname, String avatar, String content, String createDate, String event) {
		this.id = id;
		this.nickname = nickname;
		this.avatar = avatar;
		this.content = content;
		this.createDate = createDate;
		this.event = event;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}
}
