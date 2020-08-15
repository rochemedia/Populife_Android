package com.populstay.populife.util.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.populstay.populife.R;
import com.populstay.populife.activity.MainActivity;
import com.populstay.populife.activity.SignActivity;
import com.populstay.populife.sign.ISignListener;
import com.populstay.populife.sign.SignHandler;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Jerry
 */
public class NotificationUtil {

	private static final String CHANNEL_ID = "populife_notification_channel_id";

	public static void createNotification(Context context, int eventCode, String contentText) {

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 8.0 以上系统适配
			String channelName = context.getString(R.string.notification);
			int importance = NotificationManager.IMPORTANCE_HIGH;
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
			channel.setShowBadge(true);

			if (notificationManager != null) {
				notificationManager.createNotificationChannel(channel);
			}
		}

		try {
			// 点击通知后执行的操作（跳转页面）
			Intent intent = new Intent();
			if (eventCode == 1) { // 异地登录，跳转至登录页面
				intent.setClass(context, SignActivity.class);
				intent.putExtra(SignActivity.KEY_ACCOUNT_SIGN_ACTION_TYPE, SignActivity.VAL_ACCOUNT_SIGN_IN);
				SignHandler.onSignOut(new ISignListener() {
					@Override
					public void onSignInSuccess() {

					}

					@Override
					public void onSignUpSuccess() {

					}

					@Override
					public void onSignOutSuccess() {

					}
				});
			} else { // 钥匙状态改变，跳转至首页钥匙页面
				intent.setClass(context, MainActivity.class);
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

			Notification.Builder builder;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				builder = new Notification.Builder(context, CHANNEL_ID);
			} else {
				builder = new Notification.Builder(context);
			}
			// 对通知栏本身的设置
			Notification notification = builder
					.setContentTitle(context.getString(R.string.app_name))
					.setContentText(contentText)
					.setWhen(System.currentTimeMillis())
					.setSmallIcon(R.mipmap.ic_logo)
					.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_logo))
					.setStyle(new Notification.BigTextStyle())
					.setPriority(Notification.PRIORITY_HIGH)
					.setContentIntent(pendingIntent)
					.setAutoCancel(true) // 当点击通知消息进行跳转后，取消这条通知
					.build();

			if (notificationManager != null) {
				notificationManager.notify(1, notification);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
