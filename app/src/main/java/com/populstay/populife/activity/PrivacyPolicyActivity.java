package com.populstay.populife.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.util.locale.LanguageUtil;

import static android.webkit.WebSettings.TextSize.SMALLER;

public class PrivacyPolicyActivity extends BaseActivity {

	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privacy_policy);

		initView();
	}

	private void initView() {
		findViewById(R.id.page_title).setVisibility(View.GONE);
		findViewById(R.id.page_action).setVisibility(View.GONE);

		mWebView = findViewById(R.id.wv_privacy_policy);
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(false);
		settings.setSupportZoom(false);
		settings.setTextSize(SMALLER);
		settings.setBuiltInZoomControls(false);
		settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		settings.setDefaultFontSize(18);

		String filePath = LanguageUtil.isChinese(this) ? "file:///android_asset/privacy_policy_cn.html"
				: "file:///android_asset/privacy_policy_en.html";
		mWebView.loadUrl(filePath);
	}

	@Override
	protected void queryLatestDeviceId() {

	}
}
