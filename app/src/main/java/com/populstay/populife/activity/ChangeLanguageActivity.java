package com.populstay.populife.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.util.locale.LanguageUtil;

public class ChangeLanguageActivity extends BaseActivity implements View.OnClickListener {

	private TextView mTvSave;
	private LinearLayoutCompat mLlSystem, mLlEnglish, mLlChinese, mLlJapanese, mLlFrench,mLlGerman;
	private ImageView mIvSystem, mIvEnglish, mIvChinese, mIvJapanese, mIvFrench,mIvGerman;

	private int mLanguageType = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_language);

		initView();
		initListener();
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.switch_language);
		mTvSave = findViewById(R.id.page_action);
		mTvSave.setText(R.string.save);

		mLlSystem = findViewById(R.id.ll_change_language_system);
		mLlEnglish = findViewById(R.id.ll_change_language_english);
		mLlChinese = findViewById(R.id.ll_change_language_simplified_chinese);
		mLlJapanese = findViewById(R.id.ll_change_language_japanese);
		mLlFrench = findViewById(R.id.ll_change_language_french);
		mLlGerman = findViewById(R.id.ll_change_language_german);
		mIvSystem = findViewById(R.id.iv_change_language_system);
		mIvEnglish = findViewById(R.id.iv_change_language_english);
		mIvChinese = findViewById(R.id.iv_change_language_simplified_chinese);
		mIvJapanese = findViewById(R.id.iv_change_language_japanese);
		mIvFrench = findViewById(R.id.iv_change_language_french);
		mIvGerman = findViewById(R.id.iv_change_language_german);

		int type = LanguageUtil.getLanguageType(this);
		refreshLanguageUI(type);
	}

	private void refreshLanguageUI(int languageType) {
		switch (languageType) {
			case 0: // 跟随系统
				mLanguageType = 0;
				mIvSystem.setVisibility(View.VISIBLE);
				mIvEnglish.setVisibility(View.INVISIBLE);
				mIvChinese.setVisibility(View.INVISIBLE);
				mIvJapanese.setVisibility(View.INVISIBLE);
				mIvFrench.setVisibility(View.INVISIBLE);
				mIvGerman.setVisibility(View.INVISIBLE);
				break;

			case 1: // 英文
				mLanguageType = 1;
				mIvSystem.setVisibility(View.INVISIBLE);
				mIvEnglish.setVisibility(View.VISIBLE);
				mIvChinese.setVisibility(View.INVISIBLE);
				mIvJapanese.setVisibility(View.INVISIBLE);
				mIvFrench.setVisibility(View.INVISIBLE);
				mIvGerman.setVisibility(View.INVISIBLE);
				break;

			case 2: // 简体中文
				mLanguageType = 2;
				mIvSystem.setVisibility(View.INVISIBLE);
				mIvEnglish.setVisibility(View.INVISIBLE);
				mIvChinese.setVisibility(View.VISIBLE);
				mIvJapanese.setVisibility(View.INVISIBLE);
				mIvFrench.setVisibility(View.INVISIBLE);
				mIvGerman.setVisibility(View.INVISIBLE);
				break;

			case 3: // 日语
				mLanguageType = 3;
				mIvSystem.setVisibility(View.INVISIBLE);
				mIvEnglish.setVisibility(View.INVISIBLE);
				mIvChinese.setVisibility(View.INVISIBLE);
				mIvJapanese.setVisibility(View.VISIBLE);
				mIvFrench.setVisibility(View.INVISIBLE);
				mIvGerman.setVisibility(View.INVISIBLE);
				break;

			case 4: // 法语
				mLanguageType = 4;
				mIvSystem.setVisibility(View.INVISIBLE);
				mIvEnglish.setVisibility(View.INVISIBLE);
				mIvChinese.setVisibility(View.INVISIBLE);
				mIvJapanese.setVisibility(View.INVISIBLE);
				mIvFrench.setVisibility(View.VISIBLE);
				mIvGerman.setVisibility(View.INVISIBLE);
				break;

			case 5: // 德语
				mLanguageType = 5;
				mIvSystem.setVisibility(View.INVISIBLE);
				mIvEnglish.setVisibility(View.INVISIBLE);
				mIvChinese.setVisibility(View.INVISIBLE);
				mIvJapanese.setVisibility(View.INVISIBLE);
				mIvFrench.setVisibility(View.INVISIBLE);
				mIvGerman.setVisibility(View.VISIBLE);
				break;

			default: // 默认英语
				mLanguageType = 1;
				mIvSystem.setVisibility(View.INVISIBLE);
				mIvEnglish.setVisibility(View.VISIBLE);
				mIvChinese.setVisibility(View.INVISIBLE);
				mIvJapanese.setVisibility(View.INVISIBLE);
				mIvFrench.setVisibility(View.INVISIBLE);
				break;
		}
	}

	private void initListener() {
		mTvSave.setOnClickListener(this);
		mLlSystem.setOnClickListener(this);
		mLlEnglish.setOnClickListener(this);
		mLlChinese.setOnClickListener(this);
		mLlJapanese.setOnClickListener(this);
		mLlFrench.setOnClickListener(this);
		mLlGerman.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.page_action:
				changeLanguage();
				break;

			case R.id.ll_change_language_system:
				refreshLanguageUI(0);
				break;

			case R.id.ll_change_language_english:
				refreshLanguageUI(1);
				break;

			case R.id.ll_change_language_simplified_chinese:
				refreshLanguageUI(2);
				break;

			case R.id.ll_change_language_japanese:
				refreshLanguageUI(3);
				break;

			case R.id.ll_change_language_french:
				refreshLanguageUI(4);
				break;

			case R.id.ll_change_language_german:
				refreshLanguageUI(5);
				break;

			default:
				break;
		}
	}

	private void changeLanguage() {
		boolean sameLanguage = LanguageUtil.isSameLanguage(mLanguageType);
		if (!sameLanguage) {
			LanguageUtil.setLocale(mLanguageType);
			LanguageUtil.toRestartMainActvity(this);
		}
		// 设置完语言后缓存type
		LanguageUtil.putLanguageType(mLanguageType);
		finish();
	}
}
