package com.populstay.populife.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.app.AccountManager;
import com.populstay.populife.app.IUserChecker;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.constant.Constant;
import com.populstay.populife.util.device.FingerprintUtil;
import com.populstay.populife.util.storage.PeachPreference;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class SplashActivity extends BaseActivity implements View.OnClickListener {

	private static final String DEFAULT_KEY_NAME = "default_key";
	private AlertDialog DIALOG;
	private TextView mTvFingerprintMsg, mTvCancel;

	private Cipher mCipher;
	private KeyStore keyStore;
	private CancellationSignal mCancellationSignal;
	private FingerprintManager fingerprintManager;
	/**
	 * 标识是否是用户主动取消的认证。
	 */
	private boolean isSelfCancelled;
	private int mAuthFailNum; // 指纹验证失败次数
	private ImageView mIvImgSplash1, mIvImgSplash2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		mIvImgSplash1 = findViewById(R.id.iv_img_splash_1);
		mIvImgSplash2 = findViewById(R.id.iv_img_splash_2);
		getShareConfig();
		playSplashAnim();
	}

	private void getShareConfig(){
		String sharePreId = getIntent().getStringExtra(Constant.SHARE_KEY_PARAM_PRE_ID);
		if (TextUtils.isEmpty(sharePreId)) {
			// 通过h5网页调起app
			Intent intent = getIntent();
			String action = intent.getAction();
			if (Intent.ACTION_VIEW.equals(action)) {
				Uri uri = intent.getData();
				if (uri != null) {
					// 通过URL获取value
					sharePreId = uri.getQueryParameter(Constant.SHARE_KEY_PARAM_PRE_ID);
					PeachPreference.setShareKeyPreId(sharePreId);
				}
			}
		}else {
			PeachPreference.setShareKeyPreId("");
		}
	}

	private void playSplashAnim() {
		AnimatorSet set = new AnimatorSet().setDuration(1200);
		AnimatorSet subSet1 = new AnimatorSet();
		AnimatorSet subSet2 = new AnimatorSet();
		AnimatorSet subSet3 = new AnimatorSet();
		AnimatorSet subSet4 = new AnimatorSet();

		// time 280
		subSet1.play(ObjectAnimator.ofFloat(mIvImgSplash1,"translationY",300).setDuration(200))
				.after(ObjectAnimator.ofFloat(mIvImgSplash1, "alpha", 0f, 1f).setDuration(80));

		// time 280
		subSet2.play(ObjectAnimator.ofFloat(mIvImgSplash2,"translationY",-300).setDuration(200))
				.after(ObjectAnimator.ofFloat(mIvImgSplash2, "alpha", 0f, 1f).setDuration(80));

		// time 200
		subSet3.play(ObjectAnimator.ofFloat(mIvImgSplash1, "scaleY", 1.5f).setDuration(200))
				.with(ObjectAnimator.ofFloat(mIvImgSplash1, "scaleX", 1.5f).setDuration(200))
				.with(ObjectAnimator.ofFloat(mIvImgSplash2, "scaleY", 1.5f).setDuration(200))
				.with(ObjectAnimator.ofFloat(mIvImgSplash2, "scaleX", 1.5f).setDuration(200));

		// time 80
		subSet4.play(ObjectAnimator.ofFloat(mIvImgSplash1, "alpha", 1f, 0f).setDuration(80))
				.with(ObjectAnimator.ofFloat(mIvImgSplash2, "alpha", 1f, 0f).setDuration(80));

		// total time 840
		//1.按先后顺序执行动画
		set.playSequentially(subSet1,subSet2,subSet3,subSet4);
		set.start();

		set.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {

				if (isFinishing()) {
					return;
				}

				// 检查用户是否已经登录
				AccountManager.checkAccount(new IUserChecker() {
					@Override
					public void onSignIn() {
						// 指纹验证（设备支持指纹验证 且 开启指纹验证）
						if (PeachPreference.isTouchIdLogin() && FingerprintUtil.isSupportFingerprint(SplashActivity.this)) {
							initKey();
							initCipher();
						} else {
							onAuthSuccess();
						}
					}

					@Override
					public void onNotSignIn() {
						onAuthFail();
					}
				});

			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});

	}

	@TargetApi(23)
	private void initKey() {
		try {
			keyStore = KeyStore.getInstance("AndroidKeyStore");
			keyStore.load(null);
			KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
			KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(DEFAULT_KEY_NAME,
					KeyProperties.PURPOSE_ENCRYPT |
							KeyProperties.PURPOSE_DECRYPT)
					.setBlockModes(KeyProperties.BLOCK_MODE_CBC)
					.setUserAuthenticationRequired(true)
					.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
			keyGenerator.init(builder.build());
			keyGenerator.generateKey();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@TargetApi(23)
	private void initCipher() {
		try {
			SecretKey key = (SecretKey) keyStore.getKey(DEFAULT_KEY_NAME, null);
			mCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
					+ KeyProperties.BLOCK_MODE_CBC + "/"
					+ KeyProperties.ENCRYPTION_PADDING_PKCS7);
			mCipher.init(Cipher.ENCRYPT_MODE, key);

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					showFingerprintDialog();
					startListening(mCipher);
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@TargetApi(23)
	private void showFingerprintDialog() {
		DIALOG = new AlertDialog.Builder(this).create();
		DIALOG.setCanceledOnTouchOutside(false);
		DIALOG.setCancelable(false);
		DIALOG.show();
		final Window window = DIALOG.getWindow();
		if (window != null) {
			window.setContentView(R.layout.dialog_fingerprint);
			window.setGravity(Gravity.CENTER);
			window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			//设置属性
			final WindowManager.LayoutParams params = window.getAttributes();
			params.width = WindowManager.LayoutParams.MATCH_PARENT;
			params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			params.dimAmount = 0.5f;
			window.setAttributes(params);

			mTvFingerprintMsg = window.findViewById(R.id.tv_dialog_fingerprint_msg);
			mTvCancel = window.findViewById(R.id.tv_dialog_fingerprint_cancel);
			AppCompatButton btnCancel = window.findViewById(R.id.btn_dialog_fingerprint_cancel);
			AppCompatButton btnLogin = window.findViewById(R.id.btn_dialog_fingerprint_login);

			mTvCancel.setOnClickListener(this);
			btnCancel.setOnClickListener(this);
			btnLogin.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.tv_dialog_fingerprint_cancel:
			case R.id.btn_dialog_fingerprint_cancel:
			case R.id.btn_dialog_fingerprint_login:
				DIALOG.cancel();
				onAuthFail();
				break;

			default:
				break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// 开始指纹认证监听
		if (mCipher != null) {
			startListening(mCipher);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		// 停止指纹认证监听
		stopListening();
	}

	@TargetApi(23)
	private void startListening(Cipher cipher) {
		isSelfCancelled = false;
		mCancellationSignal = new CancellationSignal();
		fingerprintManager = getSystemService(FingerprintManager.class);

		fingerprintManager.authenticate(new FingerprintManager.CryptoObject(cipher), mCancellationSignal,
				0, new FingerprintManager.AuthenticationCallback() {
					@Override
					public void onAuthenticationError(int errorCode, CharSequence errString) {
						mTvFingerprintMsg.setText(errString.toString());
						if (!isSelfCancelled) {
							if (errorCode == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT) {
								DIALOG.cancel();
								onAuthFail();
							}
						}
					}

					@Override
					public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

					}

					@Override
					public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
						DIALOG.cancel();
						onAuthSuccess();
					}

					@Override
					public void onAuthenticationFailed() {
						mTvCancel.setVisibility(View.GONE);
						mAuthFailNum++;
						mTvFingerprintMsg.setText(R.string.try_again);
						// 开启抖动动画
						startShakeAnim(mTvFingerprintMsg);

						if (mAuthFailNum == 3) {
							DIALOG.cancel();
							onAuthFail();
						}
					}
				}, null);
	}

	/**
	 * 开启左右抖动动画
	 */
	private void startShakeAnim(View view) {
		if (view == null) {
			return;
		}

		TranslateAnimation animation = new TranslateAnimation(-20.0f, 20.0f,
				0.0f, 0.0f); // new TranslateAnimation(xFrom,xTo, yFrom,yTo)
		animation.setDuration(50); // animation duration
		animation.setRepeatCount(6); // animation repeat count
		animation.setRepeatMode(2); // repeat animation (left to right, right to left )

		view.startAnimation(animation); // start animation
	}

	@TargetApi(23)
	private void stopListening() {
		if (mCancellationSignal != null) {
			mCancellationSignal.cancel();
			mCancellationSignal = null;
			isSelfCancelled = true;
		}
	}

	/**
	 * 指纹验证成功
	 */
	public void onAuthSuccess() {
		goToNewActivity(MainActivity.class);
		finish();
	}

	/**
	 * 指纹验证失败
	 */
	private void onAuthFail() {
		SignActivity.actionStart(SplashActivity.this, SignActivity.VAL_ACCOUNT_SIGN_UP);
		finish();
	}

	@Override
	protected void queryLatestDeviceId() {

	}
}
