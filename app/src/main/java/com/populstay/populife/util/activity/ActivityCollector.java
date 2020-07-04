package com.populstay.populife.util.activity;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jerry
 * 活动收集器,对所创建的 activity 活动类进行添加和移除,同时可实现随时退出程序
 */

public class ActivityCollector {

	private static List<Activity> mActivities = new ArrayList<>();

	/**
	 * 获取当前栈顶 activity
	 *
	 * @return 当前栈顶的 activity
	 */
	public static Activity getTopActivity() {
		if (mActivities.isEmpty()) {
			return null;
		} else {
			return mActivities.get(mActivities.size() - 1);
		}
	}

	/**
	 * 向活动收集器中添加某个 activity
	 *
	 * @param activity 当前 activity
	 */
	public static void addActivity(Activity activity) {
		mActivities.add(activity);
	}

	/**
	 * 从活动收集器中移除某个 activity
	 *
	 * @param activity 当前 activity
	 */
	public static void removeActivity(Activity activity) {
		mActivities.remove(activity);
	}

	/**
	 * 遍历活动收集器,将所有活动都关闭,最终实现退出程序
	 */
	public static void finishAll() {
		for (Activity activity : mActivities) {
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}
	}
}
