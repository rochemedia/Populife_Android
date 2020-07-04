package com.populstay.populife.activity;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.app.AccountManager;
import com.populstay.populife.app.IUserChecker;
import com.populstay.populife.base.BaseActivity;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					sleep(1000);
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
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
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
		SignActivity.actionStart(SplashActivity.this, SignActivity.VAL_ACCOUNT_SIGN_IN);
		finish();
	}

	@Override
	protected void queryLatestDeviceId() {

	}
}
