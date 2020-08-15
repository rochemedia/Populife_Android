package com.populstay.populife.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.app.MyApplication;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.Key;
import com.populstay.populife.enumtype.Operation;
import com.populstay.populife.lock.ILockIcCardSearch;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.ui.loader.PeachLoader;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.ttlock.bl.sdk.entity.Error;

import static com.populstay.populife.app.MyApplication.mTTLockAPI;

public class IcCardUploadActivity extends BaseActivity {

	private TextView mTvUpload;

	private Key mKey = MyApplication.CURRENT_KEY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ic_card_upload);

		initView();
		initListener();
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.ic_card_upload);
		findViewById(R.id.page_action).setVisibility(View.GONE);

		mTvUpload = findViewById(R.id.tv_ic_card_upload);
	}

	private void initListener() {
		mTvUpload.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				searchLockIcCards();
			}
		});
	}

	/**
	 * 读取锁的 IC 卡信息
	 */
	private void searchLockIcCards() {
		PeachLoader.showLoading(this);

		if (mTTLockAPI.isConnected(mKey.getLockMac())) {
			setSearchIcCardCallback();

			mTTLockAPI.searchICCard(null, PeachPreference.getOpenid(),
					mKey.getLockVersion(), mKey.getAdminPwd(), mKey.getLockKey(),
					mKey.getLockFlagPos(), mKey.getAesKeyStr(), DateUtil.getTimeZoneOffset());
		} else {
			setSearchIcCardCallback();
			mTTLockAPI.connect(mKey.getLockMac());
		}
	}

	private void setSearchIcCardCallback() {
		MyApplication.bleSession.setOperation(Operation.SEARCH_IC_CARDS);
		MyApplication.bleSession.setLockmac(mKey.getLockMac());

		MyApplication.bleSession.setILockIcCardSearch(new ILockIcCardSearch() {
			@Override
			public void onSuccess(final String icCardInfo) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						PeachLoader.stopLoading();
						requestUploadIcCard(icCardInfo);
					}
				});
			}

			@Override
			public void onFail(Error error) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						PeachLoader.stopLoading();
						toast(R.string.operation_fail);
					}
				});
			}
		});
	}

	/**
	 * 请求服务器，上传 IC 卡
	 */
	private void requestUploadIcCard(String icCardInfo) {
		RestClient.builder()
				.url(Urls.IC_CARD_UPLOAD)
				.params("userId", PeachPreference.readUserId())
				.params("lockId", mKey.getLockId())
				.params("records", icCardInfo)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("IC_CARD_UPLOAD", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							toast(R.string.operation_success);
						} else {
							toast(R.string.operation_fail);
						}
					}
				})
				.build()
				.post();
	}
}
