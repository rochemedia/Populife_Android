package com.populstay.populife.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;

public class PDFActivity extends BaseActivity {

	private static final String PAGE_TITLE = "page_title";
	private static final String PDF_ASSET_NAME = "pdf_asset_name";
	private static final String IS_BACK_FINISH_PAGE = "is_back_finish_page";

	private RelativeLayout mRlBack;
	private TextView mTvClose;

	public static void actionStart(Context context, String pageTitle, String pdfAssetName, boolean isBack) {
		Intent intent = new Intent(context, PDFActivity.class);
		intent.putExtra(PAGE_TITLE, pageTitle);
		intent.putExtra(PDF_ASSET_NAME, pdfAssetName);
		intent.putExtra(IS_BACK_FINISH_PAGE, isBack);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pdf);

		initView();
	}

	private void initView() {
		mRlBack = findViewById(R.id.page_back);
		mTvClose = findViewById(R.id.page_action);

		Intent data = getIntent();
		String pageTitle = data.getStringExtra(PAGE_TITLE);
		String pdfAssetName = data.getStringExtra(PDF_ASSET_NAME);
		boolean isBack = data.getBooleanExtra(IS_BACK_FINISH_PAGE, true);

		((TextView) findViewById(R.id.page_title)).setText(pageTitle);

		if (isBack) {
			mTvClose.setVisibility(View.GONE);
		} else {
			mRlBack.setVisibility(View.GONE);
			mTvClose.setText("");
			mTvClose.setCompoundDrawablesWithIntrinsicBounds(
					getResources().getDrawable(R.drawable.ic_close), null, null, null);
			mTvClose.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
		}

		PDFView pdfView = findViewById(R.id.pdfView);
		pdfView.fromAsset(pdfAssetName)
				.enableAntialiasing(true)
				.load();
	}
}
