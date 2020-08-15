package com.populstay.populife.entity;

/**
 * 内容消息（用户消息、反馈消息）
 */
public class ContentInfo {
	private String id;
	private String userId;
	private String title;
	private String content;
	private boolean hasRead;
	//todo 原始类型 long（考虑时区转换）
	private String createTime;

	public ContentInfo() {
	}

	public ContentInfo(String id, String userId, String title, String content, boolean hasRead, String createTime) {
		this.id = id;
		this.userId = userId;
		this.title = title;
		this.content = content;
		this.hasRead = hasRead;
		this.createTime = createTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isHasRead() {
		return hasRead;
	}

	public void setHasRead(boolean hasRead) {
		this.hasRead = hasRead;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "ContentInfo{" +
				"id='" + id + '\'' +
				", userId='" + userId + '\'' +
				", title='" + title + '\'' +
				", content='" + content + '\'' +
				", hasRead=" + hasRead +
				", createTime=" + createTime +
				'}';
	}
}
