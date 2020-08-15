package com.populstay.populife.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class Utils {
	private static final String hexDigits[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d",
			"e", "f"};
	private static long lastClickTime;

	private Utils() {

	}

	public static boolean isServiceRunning(Context context, String serviceName) {
		boolean isRunning = false;
		if (context == null || serviceName == null) return isRunning;
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> arrayList = activityManager.getRunningServices(100);
		if (arrayList == null || arrayList.size() == 0) return isRunning;
		for (int i = 0, n = arrayList.size(); i < n; i++) {
			if (serviceName.equals(arrayList.get(i).service.getClassName().toString())) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	public static String getClientVersion(@NonNull Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getApplicationInfo().packageName, 0).versionName;
		} catch (PackageManager.NameNotFoundException ex) {
			return "";
		}
	}

	public static void closeInputKeyboard(Activity context) {
		if (context == null || context.getCurrentFocus() == null || context.getCurrentFocus().getWindowToken() == null)
			return;
		try {
			((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
					(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (NullPointerException nullPoint) {
			nullPoint.printStackTrace();
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	/* 日期格式化类 SimpleDateFormat
	 * 作用1： 可以把日期转换转指定格式的字符串 format()
	 * 作用2： 可以把一个 字符转换成对应的日期。 parse()
	 */
	public static String getDate(long t, String format) {
		Date date = new Date(t);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
		return simpleDateFormat.format(date);
	}

	public static boolean AndroidM() {
		return Build.VERSION.SDK_INT >= 23;
	}

	private static String byteArrayToHexString(byte b[]) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++)
			resultSb.append(byteToHexString(b[i]));

		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0) n += 256;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	/**
	 * get App versionCode
	 *
	 * @param context
	 * @return
	 */
	public static String getVersionCode(Context context) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packageInfo;
		String versionCode = "";
		try {
			packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			versionCode = packageInfo.versionCode + "";
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * get App versionName
	 *
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		PackageManager packageManager = context.getPackageManager();
		PackageInfo packageInfo;
		String versionName = "";
		try {
			packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			versionName = packageInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	public static String MD5(String origin) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString.getBytes("UTF-8")));
		} catch (Exception exception) {
		}
		return resultString;
	}

	public static int getStatusBarHeight(Context context) {
		int status_bar_height = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			status_bar_height = context.getResources().getDimensionPixelSize(resourceId);
		}
		return status_bar_height;
	}

	public static void takePhoto(Activity context, String path, int reqCode, Uri imageUri) {
		Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		context.startActivityForResult(intentToTakePhoto, reqCode);
	}

	public static int getNavigationBarHeight(Activity activity) {
		Resources resources = activity.getResources();
		int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
		return resources.getDimensionPixelSize(resourceId);
	}

	public static void takePhoto(Fragment context, String path, int reqCode, Uri imageUri) {
		Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		context.startActivityForResult(intentToTakePhoto, reqCode);
	}

	public static void choosePhoto(Activity context, int reqCode) {
		Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
		// 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型" 所有类型则写 "image/*"
		intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		context.startActivityForResult(intentToPickPic, reqCode);
	}

	public static void choosePhoto(Fragment context, int reqCode) {
		Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
		// 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型" 所有类型则写 "image/*"
		intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		context.startActivityForResult(intentToPickPic, reqCode);
	}

	public static String bitmapToBase64(Bitmap bitmap) {
		if (bitmap == null) return null;
		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static Bitmap getBitmapFromImageView(ImageView imageView) {
		imageView.setDrawingCacheEnabled(true);
		Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
		imageView.setDrawingCacheEnabled(false);
		return bitmap;
	}

	/**
	 * @param fragment    当前activity
	 * @param orgUri      剪裁原图的Uri
	 * @param desUri      剪裁后的图片的Uri
	 * @param aspectX     X方向的比例
	 * @param aspectY     Y方向的比例
	 * @param width       剪裁图片的宽度
	 * @param height      剪裁图片高度
	 * @param requestCode 剪裁图片的请求码
	 */
	public static void cropImageUri(Activity fragment, Uri orgUri, Uri desUri, int aspectX, int aspectY, int width,
									int height, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}
		if (Build.MANUFACTURER.toUpperCase().contains("HUAWEI")) {
			intent.putExtra("aspectX", 9998 * aspectX);
			intent.putExtra("aspectY", 9999 * aspectY);
		} else {
			intent.putExtra("aspectX", aspectX);
			intent.putExtra("aspectY", aspectY);
		}
		intent.putExtra("crop", "true");
		Rect rounded = new Rect();
		rounded.left = 0;
		rounded.top = 0;
		rounded.right = width;
		rounded.bottom = width * aspectY / aspectX;
		intent.putExtra("cropped-rect", rounded);
		intent.setDataAndType(orgUri, "image/*");
		intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", aspectX);
//        intent.putExtra("aspectY", aspectY);
		intent.putExtra("outputX", width);
		intent.putExtra("outputY", height);
		intent.putExtra("scale", true);
		//将剪切的图片保存到目标Uri中
		intent.putExtra(MediaStore.EXTRA_OUTPUT, desUri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		fragment.startActivityForResult(intent, requestCode);
	}

	/**
	 * @return 防止快速点击事件
	 */
	public synchronized static boolean isFastClick(final int milliSecond) {
		long time = System.currentTimeMillis();
		if (time - lastClickTime < milliSecond) {
			return false;
		}
		lastClickTime = time;
		return true;
	}
}
