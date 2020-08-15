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
        // 获取家庭组数据完成
        int GET_HOME_DATA_COMPLETE = 4;
        // 选择家庭组变化
        int CHANGE_HOME = 5;
        // 锁头本地初始化
        int LOCK_LOCAL_INITIALIZE_SUCCEED = 6;
        // 用户头像更新
        int USER_AVATAR_MODIFY = 7;
        // 用户昵称更新
        int USER_NIKE_NAME_MODIFY = 8;
        // 创建蓝牙钥匙成功
        int CREATE_BT_KEY_SUCCESS = 9;
        // 创建键盘密码成功
        int CREATE_PWD_SUCCESS = 10;
        // 密码状态同步成功
        int SYN_PWD_INFO_SUCCESS = 11;
    }
}
