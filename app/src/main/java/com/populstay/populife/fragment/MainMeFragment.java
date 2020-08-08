package com.populstay.populife.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.populstay.populife.BuildConfig;
import com.populstay.populife.R;
import com.populstay.populife.activity.AccountBindActivity;
import com.populstay.populife.activity.ChangeLanguageActivity;
import com.populstay.populife.activity.DeleteAccountActivity;
import com.populstay.populife.activity.GatewayListActivity;
import com.populstay.populife.activity.MessageListActivity;
import com.populstay.populife.activity.ModifyNicknameActivity;
import com.populstay.populife.activity.ModifyPwdActivity;
import com.populstay.populife.activity.SignActivity;
import com.populstay.populife.base.BaseVisibilityFragment;
import com.populstay.populife.common.Urls;
import com.populstay.populife.constant.Constant;
import com.populstay.populife.eventbus.Event;
import com.populstay.populife.home.HomeListActivity;
import com.populstay.populife.me.PersonalCenterActivity;
import com.populstay.populife.me.ServiceSupportActivity;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.permission.PermissionListener;
import com.populstay.populife.sign.ISignListener;
import com.populstay.populife.sign.SignHandler;
import com.populstay.populife.util.activity.ActivityCollector;
import com.populstay.populife.util.dialog.DialogUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.string.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


/**
 * 底部导航栏“Me”Fragment
 * Created by Jerry
 */

public class MainMeFragment extends BaseVisibilityFragment implements View.OnClickListener {

	private static final int REQUEST_CODE_PERMISSION = 20;
	private static final int REQUEST_CODE_CARMERA = 4;
	private static final int REQUEST_CODE_PICK = 5;
	private static final int REQUEST_CODE_NICKNAME = 1;
	private static final int REQUEST_CODE_BIND = 2;
	private PermissionListener mPermissionListener;
	private CircleImageView mCivAvatar;
	private TextView mTvNickname, mTvExit, mTvDeleteAccount;
	private LinearLayout mLlMePersonalCenter, mLlSpaceManagement, mLlMail, mLlChangePwd, mLlTouchIdLogin, mLlChangeLanguage;
	private int mAccountType = Constant.ACCOUNT_TYPE_PHONE; // 注册账号的类型（1 手机，2 邮箱）
	private String mPhone = "";
	private String mEmail = "";
	private String mAvatarUrl = "";
	private String mNickname = "";
	private Uri mUri;
	private String mPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_me, null);

		initView(view);
		initListener();
		initData();

		return view;
	}

	@Override
	protected void onVisibilityChanged(boolean visible) {
		super.onVisibilityChanged(visible);
		if (visible) {
			// 刷新页面数据
//			initData();
		}
	}

	private void initData() {
//		loadCacheAvatar();
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
								//mIsAccountDeleted = "Y".equals(userInfo.getString("isDeleted"));
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
	}

	private void initView(View view) {

		mUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", new File(mPath));

		mCivAvatar = view.findViewById(R.id.civ_user_avatar);
		registerForContextMenu(mCivAvatar);
		mTvNickname = view.findViewById(R.id.tv_me_account);
		mLlMePersonalCenter = view.findViewById(R.id.ll_me_personal_center);
		mLlSpaceManagement = view.findViewById(R.id.ll_me_space_management);
		mLlMail = view.findViewById(R.id.ll_me_gateway);
		mLlChangePwd = view.findViewById(R.id.ll_me_auditor);
		mTvExit = view.findViewById(R.id.tv_settings_exit);
		mTvDeleteAccount = view.findViewById(R.id.tv_settings_delete_account);
		mLlTouchIdLogin = view.findViewById(R.id.ll_me_service_and_support);
		mLlChangeLanguage = view.findViewById(R.id.ll_me_settings);
	}

	private void initListener() {
		mCivAvatar.setOnClickListener(this);
		mLlMePersonalCenter.setOnClickListener(this);
		mLlSpaceManagement.setOnClickListener(this);
		mLlMail.setOnClickListener(this);
		mLlChangePwd.setOnClickListener(this);
		mTvExit.setOnClickListener(this);
		mTvDeleteAccount.setOnClickListener(this);
		mLlChangeLanguage.setOnClickListener(this);
		mLlTouchIdLogin.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {

			// 个人中心
			case R.id.ll_me_personal_center:
				goToNewActivity(PersonalCenterActivity.class);
				break;

			// 空间管理
			case R.id.ll_me_space_management:
				HomeListActivity.actionStart(getActivity(), HomeListActivity.VAL_ACTION_TYPE_MANAGE_HOME);
				break;

			// 智能网关
			case R.id.ll_me_gateway:
				goToNewActivity(GatewayListActivity.class);
				break;

			// 审计追踪
			case R.id.ll_me_auditor:
				PeachPreference.setBoolean(PeachPreference.HAVE_NEW_MESSAGE, false);
				goToNewActivity(MessageListActivity.class);
				break;

			// 服务与支持
			case R.id.ll_me_service_and_support:
				goToNewActivity(ServiceSupportActivity.class);
				break;

			// 设置
			case R.id.ll_me_settings:
				goToNewActivity(ChangeLanguageActivity.class);
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
			default:
				break;
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
					} else if (mAccountType == Constant.ACCOUNT_TYPE_EMAIL) {
						mPhone = data.getStringExtra(AccountBindActivity.KEY_BIND_RESULT);
					}
					cachePersonalInfo();
					break;

				case REQUEST_CODE_CARMERA:
					try {
						Bitmap bit = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(mUri));
						upload(bit);
//						uploadAvatar();
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;

				case REQUEST_CODE_PICK:
					try {
						// 该 uri 是上一个 Activity 返回的
						mUri = data.getData();
						if (mUri != null) {
							Bitmap bit = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(mUri));
							upload(bit);
//							uploadAvatar();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;

				default:
					break;
			}
		}
	}

	private void upload(Bitmap bitmap) {
		Glide.with(this)
				.load(mUri)
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.dontAnimate()
				.centerCrop()
				.placeholder(R.drawable.ic_user_avatar)
				.error(R.drawable.ic_user_avatar)
				.into(mCivAvatar);
//		mCivAvatar.setImageBitmap(bitmap);
//		mCivAvatar.setImageURI(mUri);
//		executeInteractor(new UploadImageRequestBuilder(userInfoModel.getUser_id(), Utils.bitmapToBase64(bitmap)), new
//				JsonConverterCallback<Results>((PresenterActivity) getActivity(), Results.class, R.string.uploading) {
//
//					@Override
//					public void onResponse(Call call, Results result) {
//						if (result == null || result.code != 200) {
//							toast("上传头像失败,请重试");
//							return;
//						}
//						userInfoModel.setPic_prev((String) result.data.get("path"));
//						mUrl = (String) result.data.get("com_path");
//						if (!TextUtils.isEmpty(mUrl)) {
//							mSimpleDraweeView.setImageURI(Uri.parse(mUrl));
//						}
//					}
//				});
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

	private void setAvatar(String avatarUrl) {
		if (!TextUtils.isEmpty(avatarUrl)) {
			Glide.with(this)
					.load(avatarUrl)
					.asBitmap()
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.dontAnimate()
					.centerCrop()
					.placeholder(R.drawable.ic_user_avatar)
					.error(R.drawable.ic_user_avatar)
					.into(mCivAvatar);
		}
	}

	@Override
	public void onEventSub(Event event) {
		super.onEventSub(event);
		if (Event.EventType.USER_AVATAR_MODIFY ==  event.type){
			setAvatar((String) event.obj);
		}else if (Event.EventType.USER_NIKE_NAME_MODIFY ==  event.type){
			mTvNickname.setText((String) event.obj);
		}
	}
}
