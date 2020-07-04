package com.populstay.populife.util.device;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Jerry
 */
public class KeyboardUtil {

	/**
	 * 显示软键盘
	 */
	public static void showSoftInput(final Activity activity) {
		InputMethodManager imm =
				(InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (imm == null) return;
		View view = activity.getCurrentFocus();
		if (view == null) {
			view = new View(activity);
			view.setFocusable(true);
			view.setFocusableInTouchMode(true);
			view.requestFocus();
		}
		imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
	}

	/**
	 * 显示软键盘
	 */
	public static void showSoftInput(final View view) {
		InputMethodManager imm = (InputMethodManager) view.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			view.setFocusable(true);
			view.setFocusableInTouchMode(true);
			view.requestFocus();
			imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
		}
	}

	/**
	 * 隐藏软键盘
	 */
	public static void hideSoftInput(final Activity activity) {
		InputMethodManager imm =
				(InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		if (imm == null) return;
		View view = activity.getCurrentFocus();
		if (view == null) view = new View(activity);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/**
	 * 隐藏软键盘
	 */
	public static void hideSoftInput(final View view) {
		InputMethodManager imm = (InputMethodManager) view.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	/**
	 * 根据软键盘的当前状态，显示/隐藏软键盘
	 */
	public static void toggleSoftInput(final View view) {
		InputMethodManager imm = (InputMethodManager) view.getContext()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null) {
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		}
	}
}
