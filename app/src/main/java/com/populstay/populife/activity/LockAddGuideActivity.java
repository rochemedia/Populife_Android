package com.populstay.populife.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;

public class LockAddGuideActivity extends BaseActivity implements View.OnClickListener {

	private static final String KEY_LOCK_TYPE = "KEY_LOCK_TYPE";

	private ImageView mIvGuide;
	private TextView mTvQuestion, mTvNext;
	private int mLockType;
	private CheckBox mCheckBox;

	/**
	 * @param lockType 锁类型
	 *                 0
	 */
	public static void actionStart(Context context, int lockType) {
		Intent intent = new Intent(context, LockAddGuideActivity.class);
		intent.putExtra(KEY_LOCK_TYPE, lockType);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_add_guide);

		mLockType = getIntent().getIntExtra(KEY_LOCK_TYPE, 0);
		initView();
		initListener();
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.lock_add);
		mTvQuestion = findViewById(R.id.page_action);
		mTvQuestion.setText("");
		mTvQuestion.setCompoundDrawablesWithIntrinsicBounds(
				getResources().getDrawable(R.drawable.ic_question_mark), null, null, null);

		mTvNext = findViewById(R.id.tv_lock_add_guide_next);
		mIvGuide = findViewById(R.id.iv_lock_add_guide_img);
		mCheckBox = findViewById(R.id.cb_lock_add_guide);

		if (mLockType == 0) {
			mIvGuide.setImageResource(R.drawable.img_lock_add_guide_deadbolt);
		} else if (mLockType == 1) {
			mIvGuide.setImageResource(R.drawable.img_lock_add_guide_keybox_touch);
		}
	}

	private void initListener() {
		mTvQuestion.setOnClickListener(this);
		mTvNext.setOnClickListener(this);
		mCheckBox.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.page_action:
				CommonQuestionDetailActivity.actionStart(LockAddGuideActivity.this, "0", "1");
				break;

			case R.id.tv_lock_add_guide_next:
				if (isBleNetEnable()) {
					goToNewActivity(FoundDeviceActivity.class);
				}
				break;

			case R.id.cb_lock_add_guide:
				mTvNext.setEnabled(mCheckBox.isChecked());
				break;

			default:
				break;
		}
	}
}
