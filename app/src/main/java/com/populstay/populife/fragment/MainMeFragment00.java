package com.populstay.populife.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.populstay.populife.R;
import com.populstay.populife.activity.AccountBindActivity;
import com.populstay.populife.activity.ChangeLanguageActivity;
import com.populstay.populife.activity.DeleteAccountActivity;
import com.populstay.populife.activity.ModifyNicknameActivity;
import com.populstay.populife.activity.ModifyPwdActivity;
import com.populstay.populife.activity.SignActivity;
import com.populstay.populife.base.BaseVisibilityFragment;
import com.populstay.populife.common.Urls;
import com.populstay.populife.constant.Constant;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.permission.PermissionListener;
import com.populstay.populife.sign.ISignListener;
import com.populstay.populife.sign.SignHandler;
import com.populstay.populife.util.activity.ActivityCollector;
import com.populstay.populife.util.device.FingerprintUtil;
import com.populstay.populife.util.dialog.DialogUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.string.StringUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;


/**
 * 底部导航栏“Me”Fragment
 * Created by Jerry
 */

public class MainMeFragment00 extends BaseVisibilityFragment implements View.OnClickListener {

	private static final int REQUEST_CODE_PERMISSION = 20;
	private static final int REQUEST_CODE_CARMERA = 4;
	private static final int REQUEST_CODE_PICK = 5;
	private static final int REQUEST_CODE_CUT = 6;
	private static final int REQUEST_CODE_NICKNAME = 1;
	private static final int REQUEST_CODE_BIND = 2;
	private PermissionListener mPermissionListener;
	// 创建一个以当前系统时间为名称的文件，防止重复
	private File tempFile = new File(Environment.getExternalStorageDirectory(), getPhotoFileName());
	private CircleImageView mCivAvatar;
	private ImageView mIvMail;
	private TextView mTvAccount, mTvNickname, mTvMailTitle, mTvMailContent, mTvExit, mTvDeleteAccount;
	private LinearLayout mLlNickName, mLlMail, mLlChangePwd, mLlTouchIdLogin, mLlChangeLanguage;
	private Switch mSwitchTouchIdLogin;
	private int mAccountType = Constant.ACCOUNT_TYPE_PHONE; // 注册账号的类型（1 手机，2 邮箱）
	private boolean mIsAccountDeleted;
	private String mPhone = "";
	private String mEmail = "";
	private String mAvatarUrl = "";
	private String mNickname = "";
	private DialogInterface.OnClickListener mChooseAvatarDialogListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(final DialogInterface dialog, final int which) {
			switch (which) {
//				case 0:
//					requestRuntimePermissions(new String[]{Manifest.permission.CAMERA}, new PermissionListener() {
//						@Override
//						public void onGranted() {
//							// 调用拍照
//							startCamera(dialog);
//						}
//
//						@Override
//						public void onDenied(List<String> deniedPermissions) {
//							toast(getString(R.string.start_camera_fail));
//						}
//					});
//					break;

				case 0:
					requestRuntimePermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
						@Override
						public void onGranted() {
							// 调用相册
							startPick(dialog);
						}

						@Override
						public void onDenied(List<String> deniedPermissions) {
							toast(getString(R.string.note_permission_avatar));
						}
					});
					break;

				default:
					break;
			}
		}
	};

	/**
	 * 把 bitmap 转换成 String
	 */
	public static String bitmapToStringByBase64(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 1.5M 的压缩后在 100Kb 以内，测试得值,压缩后的大小 =94486 字节,压缩后的大小 =74473 字节
		// 这里的 JPEG 如果换成 PNG，那么压缩的就有 600kB 这样
		bitmap.compress(Bitmap.CompressFormat.PNG, 10, baos); // 参数 40 为压缩率（100表示不压缩）
		byte[] b = baos.toByteArray();
		return Base64.encodeToString(b, Base64.DEFAULT);
	}

	// 使用系统当前日期加以调整作为照片的名称
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(date) + ".png";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_me, null);

		initView(view);
		initListener();

		return view;
	}

	@Override
	protected void onVisibilityChanged(boolean visible) {
		super.onVisibilityChanged(visible);
		if (visible) {
			// 刷新页面数据
			initData();
		}
	}

	private void initData() {
		loadCacheAvatar();
		loadCachePersonalInfo();
		requestUserPersonalInfo();
	}

	private void loadCachePersonalInfo() {
		mAccountType = PeachPreference.getAccountRegisterType();
		mPhone = PeachPreference.getStr(PeachPreference.ACCOUNT_PHONE);
		mEmail = PeachPreference.getStr(PeachPreference.ACCOUNT_EMAIL);
		mNickname = PeachPreference.getStr(PeachPreference.ACCOUNT_NICKNAME);

		refreshUserInfoUI();
	}

	private void loadCacheAvatar() {
		String photoBese64 = PeachPreference.getStr(PeachPreference.ACCOUNT_AVATAR);
		byte[] decodedString = Base64.decode(photoBese64, Base64.DEFAULT);
//		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//		mCivAvatar.setImageBitmap(decodedByte);

		BitmapTypeRequest bitmapTypeRequest = Glide.with(getActivity()).load(decodedString).asBitmap();
		bitmapTypeRequest.placeholder(R.drawable.ic_user_avatar);
		bitmapTypeRequest.placeholder(R.drawable.ic_user_avatar);
		bitmapTypeRequest.diskCacheStrategy(DiskCacheStrategy.RESULT);
		bitmapTypeRequest.dontAnimate();
		bitmapTypeRequest.into(mCivAvatar);
	}

	/**
	 * 获取用户个人信息
	 */
	private void requestUserPersonalInfo() {
		RestClient.builder()
				.url(Urls.USER_INFO)
				.params("userId", PeachPreference.readUserId())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("USER_INFO", response);
						JSONObject result = JSON.parseObject(response);
						if (result != null) {
							int code = result.getInteger("code");
							if (code == 200) {
								JSONObject userInfo = result.getJSONObject("data");
								int openid = userInfo.getInteger("openid");
								PeachPreference.putStr(PeachPreference.OPEN_ID, String.valueOf(openid));
								mAccountType = userInfo.getInteger("accountType");
								mIsAccountDeleted = "Y".equals(userInfo.getString("isDeleted"));
								mPhone = userInfo.getString("phone");
								mEmail = userInfo.getString("email");
								String avatar = userInfo.getString("avatar");
								if (!StringUtil.isBlank(avatar)) {
									mAvatarUrl = avatar;
								}
								mNickname = userInfo.getString("nickname");

								refreshUserInfoUI();
								cachePersonalInfo();
							}
						}
					}
				})
				.build()
				.get();
	}

	/**
	 * 本地缓存用户个人信息
	 */
	private void cachePersonalInfo() {
		PeachPreference.setAccountRegisterType(mAccountType);
		PeachPreference.putStr(PeachPreference.ACCOUNT_PHONE, StringUtil.isBlank(mPhone) ? "" : mPhone);
		PeachPreference.putStr(PeachPreference.ACCOUNT_EMAIL, StringUtil.isBlank(mEmail) ? "" : mEmail);
		PeachPreference.putStr(PeachPreference.ACCOUNT_NICKNAME, StringUtil.isBlank(mNickname) ? "" : mNickname);
	}

	/**
	 * 刷新用户信息（UI显示）
	 */
	@SuppressLint("SetTextI18n")
	private void refreshUserInfoUI() {
		//todo 头像设置
		Glide.with(this)
				.load(mAvatarUrl)
				.asBitmap()
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.dontAnimate()
				.centerCrop()
				.placeholder(R.drawable.ic_user_avatar)
				.error(R.drawable.ic_user_avatar)
				.into(mCivAvatar);
		mTvNickname.setText(mNickname);
		switch (mAccountType) {
			case Constant.ACCOUNT_TYPE_PHONE:
				if (!StringUtil.isBlank(mPhone)) {
					mTvAccount.setText(getResources().getString(R.string.account) + " " + mPhone);
				}
				mIvMail.setImageResource(R.drawable.ic_login_email);
				mTvMailTitle.setText(R.string.email);
				mTvMailContent.setText(mEmail);
				break;

			case Constant.ACCOUNT_TYPE_EMAIL:
				if (!StringUtil.isBlank(mEmail)) {
					mTvAccount.setText(getResources().getString(R.string.account) + " " + mEmail);
				}
				mIvMail.setImageResource(R.drawable.ic_login_phone);
				mTvMailTitle.setText(R.string.phone);
				mTvMailContent.setText(mPhone);
				break;

			default:
				break;
		}
	}

	private void initView(View view) {
		view.findViewById(R.id.page_back).setVisibility(View.GONE);
		((TextView) view.findViewById(R.id.page_title)).setText(R.string.nav_tab_me);
		view.findViewById(R.id.page_action).setVisibility(View.GONE);

		mCivAvatar = view.findViewById(R.id.civ_user_avatar);
		mTvAccount = view.findViewById(R.id.tv_me_account);
		mIvMail = view.findViewById(R.id.iv_me_mail);
		mTvNickname = view.findViewById(R.id.tv_me_nick_name);
		mTvMailTitle = view.findViewById(R.id.tv_me_mail_title);
		mTvMailContent = view.findViewById(R.id.tv_me_mail_content);
		mLlNickName = view.findViewById(R.id.ll_me_nick_name);
		mLlMail = view.findViewById(R.id.ll_me_mail);
		mLlChangePwd = view.findViewById(R.id.ll_me_change_pwd);
		mTvExit = view.findViewById(R.id.tv_settings_exit);
		mTvDeleteAccount = view.findViewById(R.id.tv_settings_delete_account);
		mLlTouchIdLogin = view.findViewById(R.id.ll_me_touch_id_login);
		mSwitchTouchIdLogin = view.findViewById(R.id.switch_touch_id_login);
		mLlChangeLanguage = view.findViewById(R.id.ll_me_change_language);

		mLlTouchIdLogin.setVisibility(FingerprintUtil.isSupportFingerprint(getActivity()) ? View.VISIBLE : View.GONE);
		mSwitchTouchIdLogin.setChecked(PeachPreference.isTouchIdLogin());
	}

	private void initListener() {
		mCivAvatar.setOnClickListener(this);
		mLlNickName.setOnClickListener(this);
		mLlMail.setOnClickListener(this);
		mLlChangePwd.setOnClickListener(this);
		mTvExit.setOnClickListener(this);
		mTvDeleteAccount.setOnClickListener(this);
		mSwitchTouchIdLogin.setOnClickListener(this);
		mLlChangeLanguage.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.civ_user_avatar:
				DialogUtil.showListDialog(getActivity(), getString(R.string.choose_avatar),
						new String[]{getString(R.string.choose_from_album)},
						mChooseAvatarDialogListener);
//				DialogUtil.showListDialog(getActivity(), getString(R.string.choose_avatar),
//						new String[]{getString(R.string.take_a_photo), getString(R.string.choose_from_album)},
//						mChooseAvatarDialogListener);
				break;

			case R.id.ll_me_nick_name:
				Intent intentNickname = new Intent(getActivity(), ModifyNicknameActivity.class);
				intentNickname.putExtra(ModifyNicknameActivity.KEY_USER_NICKNAME, mNickname);
				startActivityForResult(intentNickname, REQUEST_CODE_NICKNAME);
				break;

			case R.id.ll_me_mail:
				Intent intentBind = new Intent(getActivity(), AccountBindActivity.class);
				if (mAccountType == Constant.ACCOUNT_TYPE_PHONE) {
					intentBind.putExtra(AccountBindActivity.KEY_BIND_TYPE, Constant.ACCOUNT_TYPE_EMAIL);
				} else if (mAccountType == Constant.ACCOUNT_TYPE_EMAIL) {
					intentBind.putExtra(AccountBindActivity.KEY_BIND_TYPE, Constant.ACCOUNT_TYPE_PHONE);
				}
				startActivityForResult(intentBind, REQUEST_CODE_BIND);
				break;

			case R.id.ll_me_change_pwd:
				goToNewActivity(ModifyPwdActivity.class);
				break;

			case R.id.tv_settings_exit:
				DialogUtil.showCommonDialog(getActivity(), null,
						getString(R.string.note_exit), getString(R.string.ok), getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								exit();
							}
						}, null);
				break;

			case R.id.tv_settings_delete_account:
				DialogUtil.showCommonDialog(getActivity(), null, getString(R.string.note_delete_account),
						getString(R.string.delete), getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								//删除账号前，要判断是否有已绑定的锁
								//先解绑多有的锁，才能删除账号
								//只有锁在手机附近，才能解绑锁
								if (mAccountType == Constant.ACCOUNT_TYPE_PHONE) {
									DeleteAccountActivity.actionStart(getActivity(), mPhone);
								} else if (mAccountType == Constant.ACCOUNT_TYPE_EMAIL) {
									DeleteAccountActivity.actionStart(getActivity(), mEmail);
								}
							}
						}, null);
				break;

			case R.id.switch_touch_id_login:
				PeachPreference.setTouchIdLogin(mSwitchTouchIdLogin.isChecked());
				break;

			case R.id.ll_me_change_language:
				goToNewActivity(ChangeLanguageActivity.class);
				break;

			default:
				break;
		}
	}

	/**
	 * 调用系统相机
	 */
	protected void startCamera(DialogInterface dialog) {
		dialog.dismiss();
		// 调用系统的拍照功能
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra("camerasensortype", 2); // 调用前置摄像头
		intent.putExtra("autofocus", true); // 自动对焦
		intent.putExtra("fullScreen", false); // 全屏
		intent.putExtra("showActionIcons", false);
		// 指定调用相机拍照后照片的存储路径
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
		startActivityForResult(intent, REQUEST_CODE_CARMERA); // 启动相机程序
	}

	/**
	 * 调用系统相册
	 */
	protected void startPick(DialogInterface dialog) {
		dialog.dismiss();
		try {
			if (tempFile.exists()) {
				tempFile.delete();
			}
			tempFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		startActivityForResult(intent, REQUEST_CODE_PICK);
	}

	// 调用系统裁剪
	private void startPhotoZoom(Uri uri, int size) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以裁剪
		intent.putExtra("crop", true);
		intent.putExtra("scale", true);
//		// aspectX,aspectY是宽高的比例
//		intent.putExtra("aspectX", 1);
//		intent.putExtra("aspectY", 1);
		// outputX,outputY是裁剪图片的宽高
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		// 设置是否返回数据
		intent.putExtra("return-data", true);
		startActivityForResult(intent, REQUEST_CODE_CUT);
	}

	/**
	 * 将裁剪后的图片显示在ImageView上
	 */
	private void setPicToView(Intent data) {
		Bundle bundle = data.getExtras();
		if (null != bundle) {
			final Bitmap bmp = bundle.getParcelable("data");
			mCivAvatar.setImageBitmap(bmp);

//			saveCropPic(bmp);
			PeachLogger.d("AvatarFilePath", tempFile.getAbsolutePath());
		}
	}

	/**
	 * 上传头像
	 */
	private void uploadAvatar() {
		OkHttpClient client = new OkHttpClient();

		MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
		if (tempFile != null) {
			RequestBody body = RequestBody.create(MediaType.parse("image/*"), tempFile);
			String fileName = tempFile.getName();

			requestBody.addFormDataPart("file", fileName, body);
			requestBody.addFormDataPart("userId", PeachPreference.readUserId());

			Request request = new Request.Builder()
					.url(Urls.BASE_URL + Urls.AVATAR_UPLOAD)
					.post(requestBody.build())
					.tag(this)
					.build();

			client.newBuilder()
					.readTimeout(5000, TimeUnit.MILLISECONDS)
					.build()
					.newCall(request)
					.enqueue(new Callback() {
						@Override
						public void onFailure(Call call, IOException e) {
//							toast("fail");
						}

						@Override
						public void onResponse(Call call, Response response) throws IOException {
							String str = response.body().string();
							PeachLogger.d("reponse", response);
							PeachLogger.d("body", str);
						}
					});
		}
	}

	/**
	 * 退出登录
	 */
	private void exit() {
		SignHandler.onSignOut(new ISignListener() {
			@Override
			public void onSignInSuccess() {
			}

			@Override
			public void onSignUpSuccess() {
			}

			@Override
			public void onSignOutSuccess() {
				SignActivity.actionStart(getActivity(), SignActivity.VAL_ACCOUNT_SIGN_IN);
				ActivityCollector.finishAll();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case REQUEST_CODE_NICKNAME:
					mNickname = data.getStringExtra(ModifyNicknameActivity.KEY_USER_NICKNAME);
					mTvNickname.setText(mNickname);
					cachePersonalInfo();
					break;

				case REQUEST_CODE_BIND:
					if (mAccountType == Constant.ACCOUNT_TYPE_PHONE) {
						mEmail = data.getStringExtra(AccountBindActivity.KEY_BIND_RESULT);
						mTvMailContent.setText(mEmail);
					} else if (mAccountType == Constant.ACCOUNT_TYPE_EMAIL) {
						mPhone = data.getStringExtra(AccountBindActivity.KEY_BIND_RESULT);
						mTvMailContent.setText(mPhone);
					}
					cachePersonalInfo();
					break;

				case REQUEST_CODE_CARMERA:
					startPhotoZoom(Uri.fromFile(tempFile), 300);
					break;

				case REQUEST_CODE_PICK:
					if (null != data) {
						startPhotoZoom(data.getData(), 300);
					}
					break;

				case REQUEST_CODE_CUT:
					if (null != data) {
//						setPicToView(data);
						uploadAvatar();
						Bundle bundle = data.getExtras();
						if (null != bundle) {
							Bitmap bmp = bundle.getParcelable("data");
							String photoBese64 = bitmapToStringByBase64(bmp);
							PeachPreference.putStr(PeachPreference.ACCOUNT_AVATAR, photoBese64);
							// 上传头像 logo
//							uploadAvatar(photoBese64);

							byte[] decodedString = Base64.decode(photoBese64, Base64.DEFAULT);

							BitmapTypeRequest bitmapTypeRequest = Glide.with(getActivity()).load(decodedString).asBitmap();
							bitmapTypeRequest.placeholder(R.drawable.ic_user_avatar);
							bitmapTypeRequest.placeholder(R.drawable.ic_user_avatar);
							bitmapTypeRequest.diskCacheStrategy(DiskCacheStrategy.RESULT);
							bitmapTypeRequest.dontAnimate();
							bitmapTypeRequest.into(mCivAvatar);
						}
					}
					break;

				default:
					break;
			}
		}
	}

	/**
	 * 运行时权限处理
	 */
	public void requestRuntimePermissions(String[] permissions, PermissionListener permissionListener) {
		Activity topActivity = ActivityCollector.getTopActivity();
		if (null == topActivity) {
			return;
		}
		mPermissionListener = permissionListener;
		List<String> permissionList = new ArrayList<>();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			for (String permission : permissions) {
				if (ContextCompat.checkSelfPermission(topActivity, permission) != PackageManager.PERMISSION_GRANTED) {
					permissionList.add(permission);
				}
				if (!permissionList.isEmpty()) {
					ActivityCompat.requestPermissions(topActivity, permissionList.toArray(new String[permissionList.size()]), REQUEST_CODE_PERMISSION);
				} else {
					mPermissionListener.onGranted();
				}
			}
		} else {
			mPermissionListener.onGranted();
		}
	}

	/**
	 * 用户对运行时权限授权/拒绝后，进行后续的回调操作
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		if (requestCode == REQUEST_CODE_PERMISSION) {
			if (grantResults.length > 0) {
				List<String> deniedPermissions = new ArrayList<>();
				for (int i = 0; i < grantResults.length; i++) {
					int grantResult = grantResults[i];
					String permission = permissions[i];
					if (grantResult != PackageManager.PERMISSION_GRANTED) {
						deniedPermissions.add(permission);
					}
				}
				if (deniedPermissions.isEmpty()) {
					mPermissionListener.onGranted();
				} else {
					mPermissionListener.onDenied(deniedPermissions);
				}
			}
		}
	}
}
