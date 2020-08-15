package com.populstay.populife.util.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * AlertDialog 对话框工具类
 * Created by Jerry
 */

public class DialogUtil {

	private static AlertDialog DIALOG;

	/**
	 * 返回一个列表对话框
	 *
	 * @param context               上下文
	 * @param title                 对话框标题
	 * @param message               对话框列表内容
	 * @param positiveContent       "确定"按钮要显示的内容
	 * @param negativeContent       "取消"按钮要显示的内容
	 * @param positiveClickListener "确认"按钮的监听事件
	 * @param negativeClickListener "取消"按钮的监听事件
	 */
	public static void showCommonDialog(Context context, String title, String message,
										String positiveContent, String negativeContent,
										DialogInterface.OnClickListener positiveClickListener,
										DialogInterface.OnClickListener negativeClickListener) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title)
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton(positiveContent, positiveClickListener)
				.setNegativeButton(negativeContent, negativeClickListener);

		DIALOG = builder.create();
		DIALOG.setCanceledOnTouchOutside(false);
		DIALOG.show();
	}

	/**
	 * 新建一个含有多个 item 的 dialog 对话框，并显示
	 *
	 * @param context       上下文
	 * @param title         对话框标题
	 * @param items         对话框列表内容
	 * @param clickListener 列表内容的点击事件
	 */
	public static void showListDialog(Context context, String title, String[] items,
									  DialogInterface.OnClickListener clickListener) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(title)
				.setItems(items, clickListener)
				.show();
	}

	public interface DialogButtonInterface {
		void positiveClick(String positiveContent, String positiveClickListener);

		void negativeClick(String negativeContent, String negativeClickListener);
	}

}
