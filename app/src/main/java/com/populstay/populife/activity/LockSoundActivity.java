package com.populstay.populife.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.app.MyApplication;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.entity.Key;
import com.populstay.populife.enumtype.Operation;
import com.populstay.populife.lock.ILockModifyKeypadVolume;
import com.populstay.populife.util.storage.PeachPreference;

import static com.populstay.populife.app.MyApplication.mTTLockAPI;

public class LockSoundActivity extends BaseActivity {

	public static final String KEY_LOCK_SOUND = "key_keypad_volume";

	private TextView mTvCurMode, mTvSwitch;

	private Key mKey = MyApplication.CURRENT_KEY;
	private int mLockSoundState; // 0 off, 1 on

	/**
	 * 启动当前 activity
	 *
	 * @param context   上下文
	 * @param lockSound 键盘按键音（0 off, 1 on）
	 */
	public static void actionStart(Context context, int lockSound) {
		Intent intent = new Intent(context, LockSoundActivity.class);
		intent.putExtra(KEY_LOCK_SOUND, lockSound);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_sound);

		getIntentData();
		initView();
		initListener();
	}

	private void getIntentData() {
		mLockSoundState = getIntent().getIntExtra(KEY_LOCK_SOUND, 1);
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.lock_sound);
		findViewById(R.id.page_action).setVisibility(View.GONE);

		mTvCurMode = findViewById(R.id.tv_lock_sound_mode);
		mTvSwitch = findViewById(R.id.tv_lock_sound);

		refreshKeypadVolume();
	}

	private void refreshKeypadVolume() {
		if (mLockSoundState == 1) { // 键盘按键音已关闭
			mTvCurMode.setText(R.string.on);
			mTvSwitch.setText(R.string.turn_off);
		} else { // 键盘按键音已开启
			mTvCurMode.setText(R.string.off);
			mTvSwitch.setText(R.string.turn_on);
		}
	}

	private void initListener() {
		mTvSwitch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isBleEnable()) {
					switchKeypadVolume();
				}
			}
		});
	}

	private void switchKeypadVolume() {
		showLoading();
		if (mTTLockAPI.isConnected(mKey.getLockMac())) {
			setModifyKeypadVolumeCallback();
			mTTLockAPI.operateAudioSwitch(null, 2, mLockSoundState == 1 ? 0 : 1,
					PeachPreference.getOpenid(), mKey.getLockVersion(), mKey.getAdminPwd(),
					mKey.getLockKey(), mKey.getLockFlagPos(), mKey.getAesKeyStr());
		} else {
			setModifyKeypadVolumeCallback();
			mTTLockAPI.connect(mKey.getLockMac());
		}
	}

	private void setModifyKeypadVolumeCallback() {
		MyApplication.bleSession.setOperation(Operation.MODIFY_KEYPAD_VOLUME);
		MyApplication.bleSession.setKeypadVolumeState(mLockSoundState == 1 ? 0 : 1);

		MyApplication.bleSession.setILockModifyKeypadVolume(new ILockModifyKeypadVolume() {
			@Override
			public void onSuccess(final int state) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						stopLoading();
						toastSuccess();
						mLockSoundState = state;
						refreshKeypadVolume();
					}
				});
			}

			@Override
			public void onFail() {
				stopLoading();
				toastFail();
			}
		});
	}
}
