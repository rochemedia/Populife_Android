package com.populstay.populife.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.util.dialog.DialogUtil;

public class LockAddGuideKeyboxOpenActivity extends BaseActivity implements View.OnClickListener {

	//private ImageView mIvGuide;
	private TextView mTvQuestion, mTvNext/*, mTvGuide*/;
	//private CheckBox mCheckBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_add_guide);

		initView();
		initListener();
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.lock_add);
		mTvQuestion = findViewById(R.id.page_action);
		mTvQuestion.setText("");
		mTvQuestion.setCompoundDrawablesWithIntrinsicBounds(
				getResources().getDrawable(R.drawable.help_icon), null, null, null);

		mTvNext = findViewById(R.id.tv_lock_add_guide_next);
		/*mIvGuide = findViewById(R.id.iv_lock_add_guide_img);
		mTvGuide = findViewById(R.id.tv_lock_add_guide_note);
		mCheckBox = findViewById(R.id.cb_lock_add_guide);*/

	/*	mIvGuide.setImageResource(R.drawable.img_lock_add_guide_keybox_open);
		mTvGuide.setText(R.string.note_lock_add_guide_keybox_open);
		mCheckBox.setText(R.string.note_confirm_keybox_open);*/
	}

	private void initListener() {
		mTvQuestion.setOnClickListener(this);
		mTvNext.setOnClickListener(this);
		//mCheckBox.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.page_action:
				DialogUtil.showCommonDialog(this, null,
						getString(R.string.note_keybox_default_password), getString(R.string.ok),
						null, null, null);
				break;

			case R.id.tv_lock_add_guide_next:
				if (isBleNetEnable()) {
					LockAddGuideActivity.actionStart(LockAddGuideKeyboxOpenActivity.this, "");
				}
				break;

			/*case R.id.cb_lock_add_guide:
				mTvNext.setEnabled(mCheckBox.isChecked());
				break;*/

			default:
				break;
		}
	}
}
