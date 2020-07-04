package com.populstay.populife.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.util.locale.LanguageUtil;

import static android.webkit.WebSettings.TextSize.SMALLER;

public class CommonQuestionDetailActivity extends BaseActivity {

	private static final String KEY_QUESTION_SECTION = "key_question_section";
	private static final String KEY_QUESTION_ROW = "key_question_row";

	private WebView mWebView;

	private String mFilePath;

	/**
	 * 启动当前 activity
	 *
	 * @param context 上下文
	 * @param section 消息id
	 */
	public static void actionStart(Context context, String section, String row) {
		Intent intent = new Intent(context, CommonQuestionDetailActivity.class);
		intent.putExtra(KEY_QUESTION_SECTION, section);
		intent.putExtra(KEY_QUESTION_ROW, row);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_question_detail);

		getIntentData();
		initView();
	}

	private void getIntentData() {
		Intent data = getIntent();
		String section = data.getStringExtra(KEY_QUESTION_SECTION);
		String row = data.getStringExtra(KEY_QUESTION_ROW);

		int languageType = LanguageUtil.getLanguageType(this);
		switch (languageType) {
			case 1: // 英语
				mFilePath = "file:///android_asset/question/en_question_section" + section + "row" + row + ".html";
				break;

			case 2: // 简体中文
				mFilePath = "file:///android_asset/question/zh_question_section" + section + "row" + row + ".html";
				break;

			case 3: // 日语
				mFilePath = "file:///android_asset/question/ja_question_section" + section + "row" + row + ".html";
				break;

			case 4: // 法语
				mFilePath = "file:///android_asset/question/fr_question_section" + section + "row" + row + ".html";
				break;

			case 5: // 德语
				mFilePath = "file:///android_asset/question/de_question_section" + section + "row" + row + ".html";
				break;

			default: // 默认英语
				mFilePath = "file:///android_asset/question/en_question_section" + section + "row" + row + ".html";
				break;
		}
		if (LanguageUtil.isChinese(this)) {
			mFilePath = "file:///android_asset/question/zh_question_section" + section + "row" + row + ".html";
		}
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.common_questions);
		findViewById(R.id.page_action).setVisibility(View.GONE);

		mWebView = findViewById(R.id.wv_common_question_detail);
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(false);
		settings.setSupportZoom(false);
		settings.setTextSize(SMALLER);
		settings.setBuiltInZoomControls(false);
		settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		settings.setDefaultFontSize(18);

		mWebView.loadUrl(mFilePath);
	}
}
