package com.populstay.populife.eventbus;

public class Event {

    public int type;
    public Object obj;

    public Event(int type) {
        this.type = type;
    }

    public Event(int type, Object obj) {
        this.type = type;
        this.obj = obj;
    }

    public interface EventType {
        // 新建空间
        int ADD_SPACE = 1;
        // 删除空间
        int DELETE_SPACE = 2;
        // 修改空间名称
        int RENAME_SPACE = 3;
    }
}
