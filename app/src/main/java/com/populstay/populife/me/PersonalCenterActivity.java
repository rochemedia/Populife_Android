package com.populstay.populife.me;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.populstay.populife.BuildConfig;
import com.populstay.populife.R;
import com.populstay.populife.activity.AccountBindActivity;
import com.populstay.populife.activity.ModifyNicknameActivity;
import com.populstay.populife.activity.ModifyPwdActivity;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.constant.Constant;
import com.populstay.populife.eventbus.Event;
import com.populstay.populife.me.entity.UserInfo;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.permission.PermissionListener;
import com.populstay.populife.ui.loader.PeachLoader;
import com.populstay.populife.util.GsonUtil;
import com.populstay.populife.util.Utils;
import com.populstay.populife.util.activity.ActivityCollector;
import com.populstay.populife.util.file.FileUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.string.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

public class PersonalCenterActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_PERMISSION = 20;
    private static final int REQUEST_CODE_CARMERA = 4;
    private static final int REQUEST_CODE_PICK = 5;
    private static final int REQUEST_CODE_NICKNAME = 1;
    private static final int REQUEST_CODE_BIND = 2;

    private LinearLayout mLlMeUserAvatar, mLlMeNickName, mLlMePhone, mLlMeEmail, mLlMePwd;
    private CircleImageView mCircleImageView;
    private TextView mTvNickName, mPhone, mEmail;


    private PermissionListener mPermissionListener;
    private Uri mUri;
    private String mPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";
    private AlertDialog mDialogChoosePhoto;

    //////////////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_NICKNAME:
                    String nickname = data.getStringExtra(ModifyNicknameActivity.KEY_USER_NICKNAME);
                    mTvNickName.setText(nickname);
                    EventBus.getDefault().post(new Event(Event.EventType.USER_NIKE_NAME_MODIFY,nickname));
                    //cachePersonalInfo();
                    break;

                case REQUEST_CODE_BIND:
                    int bindType = data.getIntExtra(AccountBindActivity.KEY_BIND_TYPE, 0);
                    String bindVal = data.getStringExtra(AccountBindActivity.KEY_BIND_RESULT);
                    if (bindType == Constant.ACCOUNT_TYPE_EMAIL) {
                        mEmail.setText(bindVal);
                    } else if (bindType == Constant.ACCOUNT_TYPE_PHONE) {
                        mPhone.setText(bindVal);
                    }
                    //cachePersonalInfo();
                    break;

                case REQUEST_CODE_CARMERA:
                    try {
                        Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(mUri));
                        mPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";
                        upload(bit);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case REQUEST_CODE_PICK:
                    try {
                        // 该 uri 是上一个 Activity 返回的
                        mUri = data.getData();
                        if (mUri != null) {
                            Bitmap bit = BitmapFactory.decodeStream(getContentResolver().openInputStream(mUri));
                            mPath = FileUtil.getRealFilePath(this, mUri);
                            upload(bit);
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
       /* Glide.with(this)
                .load(mUri)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .centerCrop()
                .placeholder(R.drawable.ic_user_avatar)
                .error(R.drawable.ic_user_avatar)
                .into(mCircleImageView);*/
        //mCircleImageView.setImageBitmap(bitmap);
        //mCircleImageView.setImageURI(mUri);
        PeachLoader.showLoading(this,PeachLoader.DEFAULT_LOADER);
        uploadAvatar(bitmap);
    }

    private void uploadAvatar(final Bitmap bitmap) {
        OkHttpClient client = new OkHttpClient();

        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        File tempFile = new File(mPath);
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
                            PeachLoader.stopLoading();
                            PeachLogger.d("Http", e.getMessage());
                            toast(R.string.avatar_upload_failed);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            PeachLoader.stopLoading();
                            String str = response.body().string();
                            PeachLogger.d("Http", str);
                            try {
                                JSONObject jsonObject = new JSONObject(str);
                                if (jsonObject.getBoolean("success")) {
                                    final String avatar = jsonObject.getString("data");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setAvatar(avatar,bitmap);
                                        }
                                    });
                                    EventBus.getDefault().post(new Event(Event.EventType.USER_AVATAR_MODIFY,avatar));
                                }else {
                                    toast(R.string.avatar_upload_failed);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                toast(R.string.avatar_upload_failed);
                            }

                        }
                    });
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

    private void showTypeSelDialog() {
        mDialogChoosePhoto = new AlertDialog.Builder(this).create();
        mDialogChoosePhoto.show();
        final Window window = mDialogChoosePhoto.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_choose_photo);
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.anim_panel_up_from_bottom);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.getDecorView().setPadding(0, 0, 0, 0);
            //设置属性
            final WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            params.dimAmount = 0.5f;
            window.setAttributes(params);

            TextView takePhotoBtn = window.findViewById(R.id.btn_dialog_send_ekey_permanent);
            takePhotoBtn.setText(R.string.take_a_photo);
            takePhotoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mDialogChoosePhoto){
                        mDialogChoosePhoto.dismiss();
                    }
                    requestRuntimePermissions(new String[]{Manifest.permission.CAMERA}, new PermissionListener() {
                        @Override
                        public void onGranted() {
                            // 调用拍照
                            Utils.takePhoto(PersonalCenterActivity.this, mPath, REQUEST_CODE_CARMERA, mUri);
                        }

                        @Override
                        public void onDenied(List<String> deniedPermissions) {
                            toast(R.string.start_camera_fail);
                        }
                    });

                }
            });
            TextView choosePhotoBtn = window.findViewById(R.id.btn_dialog_send_ekey_one_time);
            choosePhotoBtn.setText(R.string.choose_from_album);
            choosePhotoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mDialogChoosePhoto){
                        mDialogChoosePhoto.dismiss();
                    }
                    requestRuntimePermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionListener() {
                        @Override
                        public void onGranted() {
                            // 调用相册
                            Utils.choosePhoto(PersonalCenterActivity.this, REQUEST_CODE_PICK);
                        }

                        @Override
                        public void onDenied(List<String> deniedPermissions) {
                            toast(R.string.note_permission_avatar);
                        }
                    });

                }
            });
            window.findViewById(R.id.btn_dialog_send_ekey_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mDialogChoosePhoto){
                        mDialogChoosePhoto.dismiss();
                    }
                }
            });
        }
    }
    //////////////////

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        initView();
        setListener();
        requestUserPersonalInfo();
    }

    private void initTitleBar() {
        findViewById(R.id.page_action).setVisibility(View.GONE);
        TextView pageTitle = findViewById(R.id.page_title);
        pageTitle.setText(R.string.personal_center);
    }

    private void initView() {
        initTitleBar();
        mLlMeUserAvatar = findViewById(R.id.ll_me_user_avatar);
        mLlMeNickName = findViewById(R.id.ll_me_nick_name);
        mLlMePhone = findViewById(R.id.ll_me_phone);
        mLlMeEmail = findViewById(R.id.ll_me_email);
        mLlMePwd = findViewById(R.id.ll_me_pwd);

        mCircleImageView = findViewById(R.id.civ_user_avatar);
        mUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", new File(mPath));
        mTvNickName = findViewById(R.id.tv_nick_name);
        mPhone = findViewById(R.id.tv_phone);
        mEmail = findViewById(R.id.tv_email);
    }

    private void setListener() {
        mLlMeUserAvatar.setOnClickListener(this);
        mCircleImageView.setOnClickListener(this);
        mLlMeNickName.setOnClickListener(this);
        mLlMePhone.setOnClickListener(this);
        mLlMeEmail.setOnClickListener(this);
        mLlMePwd.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_me_user_avatar:
            case R.id.civ_user_avatar:
                showTypeSelDialog();
                break;
            case R.id.ll_me_nick_name:
                Intent intentNickname = new Intent(this, ModifyNicknameActivity.class);
                intentNickname.putExtra(ModifyNicknameActivity.KEY_USER_NICKNAME, mTvNickName.getText().toString());
                startActivityForResult(intentNickname, REQUEST_CODE_NICKNAME);
                break;
            case R.id.ll_me_phone:
                Intent intentBind = new Intent(this, AccountBindActivity.class);
                intentBind.putExtra(AccountBindActivity.KEY_BIND_TYPE, Constant.ACCOUNT_TYPE_PHONE);
                startActivityForResult(intentBind, REQUEST_CODE_BIND);
                break;
            case R.id.ll_me_email:
                intentBind = new Intent(this, AccountBindActivity.class);
                intentBind.putExtra(AccountBindActivity.KEY_BIND_TYPE, Constant.ACCOUNT_TYPE_EMAIL);
                startActivityForResult(intentBind, REQUEST_CODE_BIND);
                break;
            case R.id.ll_me_pwd:
                goToNewActivity(ModifyPwdActivity.class);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mDialogChoosePhoto){
            mDialogChoosePhoto.dismiss();
        }
        if (null != mHeadIconBitmap && !mHeadIconBitmap.isRecycled()){
            mHeadIconBitmap.recycle();
        }
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
                        com.alibaba.fastjson.JSONObject result = JSON.parseObject(response);
                        if (result != null) {
                            int code = result.getInteger("code");
                            if (code == 200) {
                                UserInfo userInfo = GsonUtil.fromJson(result.getString("data"), UserInfo.class);
                                refreshUserInfoUI(userInfo);
                                cachePersonalInfo(userInfo);
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
    private void cachePersonalInfo(UserInfo userInfo) {
        if (null == userInfo){
            return;
        }
        String phone = userInfo.getPhone();
        String email = userInfo.getEmail();
        String nickname = userInfo.getNickname();
        int openid = userInfo.getOpenid();

        PeachPreference.setAccountRegisterType(userInfo.getAccountType());
        PeachPreference.putStr(PeachPreference.ACCOUNT_PHONE, StringUtil.isBlank(phone) ? "" : phone);
        PeachPreference.putStr(PeachPreference.ACCOUNT_EMAIL, StringUtil.isBlank(email) ? "" : email);
        PeachPreference.putStr(PeachPreference.ACCOUNT_NICKNAME, StringUtil.isBlank(nickname) ? "" : nickname);
        PeachPreference.putStr(PeachPreference.OPEN_ID, String.valueOf(openid));
    }

    /**
     * 刷新用户信息（UI显示）
     */
    @SuppressLint("SetTextI18n")
    private void refreshUserInfoUI(UserInfo userInfo) {
        if (null == userInfo){
            return;
        }
        setAvatar(userInfo.getAvatar(),null);
        String phone = userInfo.getPhone();
        String email = userInfo.getEmail();
        String nickname = userInfo.getNickname();

        mPhone.setText(phone);
        mEmail.setText(email);
        if (!TextUtils.isEmpty(nickname)){
            mTvNickName.setText(nickname);
        }else {
            // 默认显示名：populife_手机号/邮箱
            String defaultShowName = "populife_";
            if (!TextUtils.isEmpty(phone)) {
                defaultShowName += phone;
            }else {
                if (!TextUtils.isEmpty(email)){
                    defaultShowName += email;
                }
            }
            mTvNickName.setText(defaultShowName);
        }
    }

    private Bitmap mHeadIconBitmap;
    private void setAvatar(String avatarUrl,Bitmap bitmap) {
        if (isFinishing()){
            return;
        }

        if (null != mHeadIconBitmap && !mHeadIconBitmap.isRecycled()){
            mHeadIconBitmap.recycle();
        }

        if (!TextUtils.isEmpty(avatarUrl)) {
            if ((null != bitmap && !bitmap.isRecycled())) {
                mHeadIconBitmap = bitmap;
                Glide.with(this)
                        .load(avatarUrl)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .dontAnimate()
                        .centerCrop()
                        .placeholder(new BitmapDrawable(this.getResources(), bitmap))
                        .error(R.drawable.ic_user_avatar)
                        .into(mCircleImageView);
            }else {
                Glide.with(this)
                        .load(avatarUrl)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .dontAnimate()
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_avatar)
                        .error(R.drawable.ic_user_avatar)
                        .into(mCircleImageView);
            }
        }
    }
}
