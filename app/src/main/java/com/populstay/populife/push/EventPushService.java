package com.populstay.populife.push;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.populstay.populife.app.AccountManager;
import com.populstay.populife.base.BaseApplication;
import com.populstay.populife.util.device.DeviceUtil;
import com.populstay.populife.util.net.NetworkUtil;
import com.populstay.populife.util.notification.NotificationUtil;
import com.populstay.populife.util.storage.PeachPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import redis.clients.jedis.Jedis;

/**
 * 事件推送服务类
 */
public class EventPushService extends Service {

	public static final String ACTION_NEW_DEVICE_LOGIN = "populife_action_new_device_login";
	public static final String ACTION_KEY_STATUS_CHANGE = "populife_action_key_status_change";

//	public static final String LOG_TAG = EventPushService.class.getSimpleName();
	/**
	 * 主机地址
	 */
	public static final String JEDIS_HOST_ADDR = "server.hafele.yigululock.com";
//	/**
//	 * 端口号为默认，不用设置
//	 */
//	public static final int JEDIS_PORT = 6379;
	/**
	 * 验证密码
	 */
	public static final String JEDIS_AUTH_PWD = "c49871320";
	private static final String DEVICE_MSG_KEY = DeviceUtil.getDeviceId(BaseApplication.getApplication());
	/**
	 * 心跳间隔时间 60s
	 */
	private static final int HEART_SPACE_TIME = 1000 * 60;
	/**
	 * 是否需要查询推送服务
	 */
	private boolean isNeedQueryJedis;
	/**
	 * 心跳定时器（用来检测网络连接情况）
	 */
	private Timer mHeartTimer;
	/**
	 * 当前网络是否连接
	 */
	private boolean netDisconnetion = false;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 发送心跳包
		sendHeartbeatInfo();
		// 启动Jedis
		launchJedisSerivce();
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 发送心跳包（每隔 1 分钟检测一次网络连接情况）
	 * 间隔 60s 发一次
	 */
	private void sendHeartbeatInfo() {

		if (mHeartTimer != null) {
			mHeartTimer.cancel();
		}
		mHeartTimer = new Timer();

		mHeartTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {

//				Log.e(LOG_TAG, LOG_TAG + "----------发送心跳包----------");

				// 为了不频繁检测，就把网络检测放在Jedis不在线程
				// 检测网络状态
				if (NetworkUtil.isNetConnected()) {
					// 由无网络变成有网络(断网时，Jedis会失联)
					if (!netDisconnetion) {
						Jedis jedis = new Jedis(JEDIS_HOST_ADDR);
						if (!jedis.isConnected()) {
							// 启动Jedis
							launchJedisSerivce();
						}
					}
					// 标识联网
					netDisconnetion = true;
				} else {
					// 断网需要结束Jedis线程
					isNeedQueryJedis = false;
					// 标识断网
					netDisconnetion = false;
					// 打开网络
//					NetworkUtils.setWifiEnabled(true);
					// 启动Jedis
//					launchJedisSerivce();
				}
			}
		}, 0, HEART_SPACE_TIME);
	}

	/**
	 * 启动Jedis
	 */
	private void launchJedisSerivce() {

		new Thread(new Runnable() {
			@Override
			public void run() {

				// 连接本地的 Redis 服务
				Jedis jedis = new Jedis(JEDIS_HOST_ADDR);
//				Jedis jedis = new Jedis(JEDIS_HOST_ADDR, JEDIS_PORT);

				try {
					jedis.auth(JEDIS_AUTH_PWD);
					jedis.select(1);
				} catch (Exception e) {
					e.printStackTrace();
					isNeedQueryJedis = false;
					return;
				}

				// 连接成功
				if (jedis.isConnected()) {
//					Log.e(LOG_TAG, LOG_TAG + "--Jedis连接成功 ");
					// 查看服务是否运行
//					Log.e(LOG_TAG, LOG_TAG + "-->Jedis服务正在运行: " + jedis.ping());
					isNeedQueryJedis = true;
				}
				// 连接失败
				else {
//					Log.e(LOG_TAG, LOG_TAG + "-->Jedis连接失败 ");
					isNeedQueryJedis = false;
					return;
				}

				// 取出数据
				while (isNeedQueryJedis) {

					// 从推送通道获取服务端发过来的数据（List<String>）
					// 数据格式：[9611d6a201fbb298, {"event":2, "msg":"推送内容"}]
					List<String> strings = null;
					try {
						strings = jedis.brpop(30000, DEVICE_MSG_KEY);
					} catch (Exception e) {
						e.printStackTrace();
					}

					int eventCode = 0;// 推送事件代号
					String eventMsg = null;// 推送事件消息内容（在手机上显示 Notification 时的内容）
					String deviceId = null;// 接收推送的设备 id
					String result = null;
					// 这里与后台约定strings集合里面只放一条记录（json格式）
					if (strings != null && strings.size() > 0) {
						deviceId = strings.get(0);
						result = strings.get(1);
					}
//					Log.e(LOG_TAG, LOG_TAG + "-->jedis data result= " + result);
//					Log.e(LOG_TAG, LOG_TAG + "-->jedis data end " + strings);

					// 解析数据（具体格式与后台约定）
					JSONObject jsonObject;
					try {
						if (result != null) {
							jsonObject = new JSONObject(result);
							eventCode = jsonObject.optInt("event");
							eventMsg = jsonObject.optString("msg");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

					// 使用接收到的推送数据，执行对应逻辑
					if (DEVICE_MSG_KEY != null && DEVICE_MSG_KEY.equals(deviceId)) {// 确定是当前设备接收到的推送

						// 检查用户的登录状态：登录则提示推送，下线则不提示
						if (AccountManager.isSignIn()) {
							// 在通知栏显示系统通知
							NotificationUtil.createNotification(EventPushService.this, eventCode, eventMsg);
							// 有新的系统消息，更新状态（显示 MainGeneralFragment 中的小红点）
							PeachPreference.setBoolean(PeachPreference.HAVE_NEW_MESSAGE, true);

							/*
							 * 1 - 异地登录（账号在另一台设备上登录，当前设备被迫下线）
							 * 2 - 收到电子钥匙（eKey）
							 * 3 - 电子钥匙被冻结
							 * 4 - 电子钥匙被解冻
							 * 5 - 电子钥匙被授权
							 * 6 - 电子钥匙被取消授权
							 * 7 - 删除锁/删除钥匙/清空钥匙
							 * 8 - 从主界面切换到锁列表
							 * 9 - 网关冻结锁
							 * 10 - 网关解冻锁
							 * 100 - 美洽新消息推送
							 */
							switch (eventCode) {
								case 1: // 当前账号异地登录，给 BaseActivity 发送广播，强制下线
									final Intent newDeviceLoginIntent = new Intent(ACTION_NEW_DEVICE_LOGIN);
									sendBroadcast(newDeviceLoginIntent);
									break;

								case 2:
								case 3:
								case 4:
								case 5:
								case 6:
								case 7:
								case 8:
								case 9:
								case 10:
									// 钥匙状态发生变化，给 LockDetailFragment 和 LockListFragment 发送广播，刷新对应的页面
									final Intent keyStatusChangeIntent = new Intent(ACTION_KEY_STATUS_CHANGE);
									sendBroadcast(keyStatusChangeIntent);
									break;

								default:
									break;
							}
						}
					}
				}
			}
		}).start();
	}

	@Override
	public void onDestroy() {
//		Log.e(LOG_TAG, LOG_TAG + "EventPushService is onDestroy");
		isNeedQueryJedis = false;
		if (mHeartTimer != null) {
			mHeartTimer.cancel();
		}
		stopSelf();
		super.onDestroy();
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
