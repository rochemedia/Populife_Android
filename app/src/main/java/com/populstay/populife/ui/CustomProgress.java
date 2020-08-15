package com.populstay.populife.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.populstay.populife.R;


public class CustomProgress extends Dialog {

	public CustomProgress(Context context) {
		super(context);
	}

	public CustomProgress(Context context, int theme) {
		super(context, theme);
	}

	/**
	 * 弹出默认形式的自定义 ProgressDialog
	 *
	 * @param context 上下文
	 * @return CustomProgress
	 */
	public static CustomProgress show(Context context) {
		CustomProgress dialog = new CustomProgress(context, R.style.custom_loading_progress);
		dialog.setTitle("");
		dialog.setContentView(R.layout.progress_custom);
		// 按返回键不可取消
		dialog.setCancelable(false);
		// 点击 dialog 外部不可取消
		dialog.setCanceledOnTouchOutside(false);
		// 设置居中
		dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		// 设置背景层透明度
		lp.dimAmount = 0.2f;
		dialog.getWindow().setAttributes(lp);
//		dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		dialog.show();

		return dialog;
	}

	/**
	 * 弹出自定义ProgressDialog
	 *
	 * @param context        上下文
	 * @param message        提示信息
	 * @param cancelable     是否按返回键取消
	 * @param cancelListener 按下返回键监听
	 * @return
	 */
	public static CustomProgress show(Context context, CharSequence message, boolean cancelable, OnCancelListener cancelListener) {
		CustomProgress dialog = new CustomProgress(context, R.style.custom_loading_progress);
		dialog.setTitle("");
		dialog.setContentView(R.layout.progress_custom);
		if (message == null || message.length() == 0) {
			dialog.findViewById(R.id.tv_loading_message).setVisibility(View.GONE);
		} else {
			TextView txt = dialog.findViewById(R.id.tv_loading_message);
			txt.setVisibility(View.VISIBLE);
			txt.setText(message);
		}
		// 点击返回取消 dialog
		dialog.setCancelable(cancelable);
		// 点击 dialog 外部是否取消
		dialog.setCanceledOnTouchOutside(false);
		// 监听返回键处理
		dialog.setOnCancelListener(cancelListener);
		// 设置居中
		dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		// 设置背景层透明度
		lp.dimAmount = 0.2f;
		dialog.getWindow().setAttributes(lp);
		// dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		dialog.show();
		return dialog;
	}

	/**
	 * 弹出自定义ProgressDialog
	 *
	 * @param context                上下文
	 * @param message                提示信息
	 * @param cancelable             是否按返回键取消
	 * @param canceledOnTouchOutside 是否点击 dialog 窗口外部按返回键取消
	 * @param cancelListener         按下返回键监听
	 * @return
	 */
	public static CustomProgress show(Context context, CharSequence message, boolean cancelable, boolean canceledOnTouchOutside, OnCancelListener cancelListener) {
		CustomProgress dialog = new CustomProgress(context, R.style.custom_loading_progress);
		dialog.setTitle("");
		dialog.setContentView(R.layout.progress_custom);
		if (message == null || message.length() == 0) {
			dialog.findViewById(R.id.tv_loading_message).setVisibility(View.GONE);
		} else {
			TextView txt = dialog.findViewById(R.id.tv_loading_message);
			txt.setText(message);
		}
		// 按返回键是否取消
//		dialog.setCancelable(cancelable);
		// 不可取消
		dialog.setCancelable(false);
		// 点击 dialog 外部是否取消
		dialog.setCanceledOnTouchOutside(canceledOnTouchOutside);
		// 监听返回键处理
		dialog.setOnCancelListener(cancelListener);
		// 设置居中
		dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		// 设置背景层透明度
		lp.dimAmount = 0.2f;
		dialog.getWindow().setAttributes(lp);
		// dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		dialog.show();
		return dialog;
	}

	/**
	 * 当窗口焦点改变时调用
	 */
	public void onWindowFocusChanged(boolean hasFocus) {
		ImageView imageView = findViewById(R.id.iv_loading_spinner);
		// 获取ImageView上的动画背景
		AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
		// 开始动画
		spinner.start();
	}

	/**
	 * 给Dialog设置提示信息
	 *
	 * @param message
	 */
	public void setMessage(CharSequence message) {
		if (message != null && message.length() > 0) {
			findViewById(R.id.tv_loading_message).setVisibility(View.VISIBLE);
			TextView txt = findViewById(R.id.tv_loading_message);
			txt.setText(message);
			txt.invalidate();
		}
	}
}
