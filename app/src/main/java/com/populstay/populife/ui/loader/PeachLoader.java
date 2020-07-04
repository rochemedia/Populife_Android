package com.populstay.populife.ui.loader;

import android.content.Context;
import android.support.v7.app.AppCompatDialog;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.populstay.populife.R;
import com.populstay.populife.util.dimen.DimenUtil;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

/**
 * 加载动画
 * Created by Jerry
 */

public class PeachLoader {

	private static final int LOADER_SIZE_SCALE = 8;
	private static final int LOADER_OFFSET_SCALE = 10;

	private static final ArrayList<AppCompatDialog> LOADERS = new ArrayList<>();

	private static final String DEFAULT_LOADER = LoaderStyle.BallSpinFadeLoaderIndicator.name();

	public static void showLoading(Context context, Enum<LoaderStyle> type) {
		showLoading(context, type.name());
	}

	public static void showLoading(Context context, String type) {

		final AppCompatDialog dialog = new AppCompatDialog(context, R.style.dialog_theme);

		final AVLoadingIndicatorView avLoadingIndicatorView = LoaderCreator.create(type, context);
		dialog.setContentView(avLoadingIndicatorView);
		dialog.setCanceledOnTouchOutside(false);

		int deviceWidth = DimenUtil.getScreenWidth();
		int deviceHeight = DimenUtil.getScreenHeight();

		final Window dialogWindow = dialog.getWindow();

		if (dialogWindow != null) {
			final WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			lp.width = deviceWidth / LOADER_SIZE_SCALE;
			lp.height = deviceHeight / LOADER_SIZE_SCALE;
			lp.height = lp.height + deviceHeight / LOADER_OFFSET_SCALE;
			lp.gravity = Gravity.CENTER;
		}
		LOADERS.add(dialog);
		dialog.show();
	}

	public static void showLoading(Context context) {
		showLoading(context, DEFAULT_LOADER);
	}

	public static void stopLoading() {
		for (AppCompatDialog dialog : LOADERS) {
			if (dialog != null) {
				if (dialog.isShowing()) {
					dialog.cancel();
				}
			}
		}
	}

}
