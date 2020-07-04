package com.populstay.populife.entity;

/**
 * 锁的家庭分组信息
 * Created by Jerry
 */
public class LockGroup {
	//[{"id":null,"name":"other","country":null,"timeZone":null,"userId":null,"createDate":null,"lockCount":0}]
	private String id; //家庭（分组）id，为空时表示未分组
	private String name; //名称，为空时表示未分组
	private Long createTime; //创建时间（毫秒时间戳）
	private int lockCount; //家庭（分组）下的锁数量
	private boolean isSelected;

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		isSelected = selected;
	}

	public LockGroup() {
	}

	public LockGroup(String id, String name, Long createTime, int lockCount) {
		this.id = id;
		this.name = name;
		this.createTime = createTime;
		this.lockCount = lockCount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public int getLockCount() {
		return lockCount;
	}

	public void setLockCount(int lockCount) {
		this.lockCount = lockCount;
	}

	@Override
	public String toString() {
		return "LockGroup{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", createTime=" + createTime +
				", lockCount=" + lockCount +
				'}';
	}
}
