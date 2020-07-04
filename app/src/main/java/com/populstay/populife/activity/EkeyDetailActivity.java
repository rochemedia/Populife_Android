package com.populstay.populife.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.populstay.populife.R;
import com.populstay.populife.app.MyApplication;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.Key;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.dialog.DialogUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.string.StringUtil;

public class EkeyDetailActivity extends BaseActivity implements View.OnClickListener {

	private static final String KEY_KEY_ID = "key_key_id";
	private static final String KEY_KEY_RIGHT = "key_key_right";
	private static final String KEY_KEY_ALIAS = "key_key_alias";
	private static final String KEY_KEY_TYPE = "key_key_type";
	private static final String KEY_START_TIME = "key_start_time";
	private static final String KEY_END_TIME = "key_end_time";
	private static final String KEY_RECEIVER = "key_receiver";
	private static final String KEY_SENDER = "key_sender";
	private static final String KEY_SENDING_TIME = "key_sending_time";
	private static final String KEY_KEY_STATUS = "key_key_status";
	private static final int REQUEST_CODE_MODIFY_EKEY_PERIOD = 1;

	private AlertDialog DIALOG;
	private TextView mTvMenu, mTvName, mTvValidPeriod, mTvStartTime,
			mTvEndTime, mTvReceiver, mTvSender, mTvSendingTime, mTvDelete;
	private LinearLayout mLlName, mLlValidPeriod, mLlRecord;
	private ImageView mIvNameMore, mIvValidPeriodMore;
	private CheckBox mCbDeleteKeys;

	private String mName, mReceiver, mSender, mKeyStatus;
	private int mKeyId, mKeyRight, mKeyType;
	private long mStartTime, mEndTime, mSendingTime;
	private boolean mIsAuth;
	private Key mKey = MyApplication.CURRENT_KEY;//当前用户的钥匙
	private EditText mEtDialogInput;

	/**
	 * 启动当前 activity
	 *
	 * @param context 上下文
	 * @param keyId   钥匙 id
	 */
	public static void actionStart(Context context, int keyId, int keyRight, String keyAlias, int keyType,
								   long startTime, long endTime, String receiver, String sender,
								   long sendingTime, String keyStatus) {
		Intent intent = new Intent(context, EkeyDetailActivity.class);
		intent.putExtra(KEY_KEY_ID, keyId);
		intent.putExtra(KEY_KEY_RIGHT, keyRight);
		intent.putExtra(KEY_KEY_ALIAS, keyAlias);
		intent.putExtra(KEY_KEY_TYPE, keyType);
		intent.putExtra(KEY_START_TIME, startTime);
		intent.putExtra(KEY_END_TIME, endTime);
		intent.putExtra(KEY_RECEIVER, receiver);
		intent.putExtra(KEY_SENDER, sender);
		intent.putExtra(KEY_SENDING_TIME, sendingTime);
		intent.putExtra(KEY_KEY_STATUS, keyStatus);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ekey_detail);

		getIntentData();
		initView();
		initListener();
	}

	private void getIntentData() {
		Intent data = getIntent();
		mKeyId = data.getIntExtra(KEY_KEY_ID, 0);
		mKeyRight = data.getIntExtra(KEY_KEY_RIGHT, 0);
		mKeyType = data.getIntExtra(KEY_KEY_TYPE, 1);
		mName = data.getStringExtra(KEY_KEY_ALIAS);
		mReceiver = data.getStringExtra(KEY_RECEIVER);
		mSender = data.getStringExtra(KEY_SENDER);
		mKeyStatus = data.getStringExtra(KEY_KEY_STATUS);
		mStartTime = data.getLongExtra(KEY_START_TIME, 0);
		mEndTime = data.getLongExtra(KEY_END_TIME, 0);
		mSendingTime = data.getLongExtra(KEY_SENDING_TIME, 0);

		mIsAuth = mKeyRight == 1;
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.ekey_detail);
		mTvMenu = findViewById(R.id.page_action);
		mTvMenu.setText("");
		mTvMenu.setCompoundDrawablesWithIntrinsicBounds(
				getResources().getDrawable(R.drawable.ic_menu_more), null, null, null);

		mTvName = findViewById(R.id.tv_ekey_detail_name);
		mTvValidPeriod = findViewById(R.id.tv_ekey_detail_valid_period);
		mTvStartTime = findViewById(R.id.tv_ekey_detail_start_time);
		mTvEndTime = findViewById(R.id.tv_ekey_detail_end_time);
		mTvReceiver = findViewById(R.id.tv_ekey_detail_receiver);
		mTvSender = findViewById(R.id.tv_ekey_detail_sender);
		mTvSendingTime = findViewById(R.id.tv_ekey_detail_sending_time);
		mTvDelete = findViewById(R.id.tv_ekey_detail_delete);
		mLlName = findViewById(R.id.ll_ekey_detail_name);
		mLlValidPeriod = findViewById(R.id.ll_ekey_detail_valid_period);
		mLlRecord = findViewById(R.id.ll_ekey_detail_records);
		mIvNameMore = findViewById(R.id.iv_ekey_detail_name_more);
		mIvValidPeriodMore = findViewById(R.id.iv_ekey_detail_valid_period_more);

		refreshUI();
	}

	private void refreshUI() {
		mTvName.setText(mName);
		mTvReceiver.setText(mReceiver);
		mTvSender.setText(mSender);
		mTvSendingTime.setText(DateUtil.getDateToString(mSendingTime, "yyyy-MM-dd HH:mm"));
		switch (mKeyType) {//钥匙类型（1限时，2永久，3单次，4循环）
			case 1:
				mLlName.setEnabled(true);
				mLlValidPeriod.setEnabled(true);
				mIvNameMore.setVisibility(View.VISIBLE);
				mIvValidPeriodMore.setVisibility(View.VISIBLE);
				mTvValidPeriod.setVisibility(View.GONE);
				mTvStartTime.setVisibility(View.VISIBLE);
				mTvEndTime.setVisibility(View.VISIBLE);
				mTvStartTime.setText(DateUtil.getDateToString(mStartTime, "yyyy-MM-dd HH:mm"));
				mTvEndTime.setText(DateUtil.getDateToString(mEndTime, "yyyy-MM-dd HH:mm"));
				break;

			case 2:
				mLlName.setEnabled(true);
				mLlValidPeriod.setEnabled(true);
				mIvNameMore.setVisibility(View.VISIBLE);
				mIvValidPeriodMore.setVisibility(View.VISIBLE);
				mTvValidPeriod.setVisibility(View.VISIBLE);
				mTvStartTime.setVisibility(View.GONE);
				mTvEndTime.setVisibility(View.GONE);
				mTvValidPeriod.setText(R.string.permanent);
				break;

			case 3:
				mLlName.setEnabled(false);
				mLlValidPeriod.setEnabled(false);
				mTvMenu.setVisibility(View.GONE);
				mIvNameMore.setVisibility(View.GONE);
				mIvValidPeriodMore.setVisibility(View.GONE);
				mTvStartTime.setVisibility(View.GONE);
				mTvEndTime.setVisibility(View.GONE);
				mTvValidPeriod.setText(R.string.one_time);
				break;

			default:
				break;
		}
	}

	private void initListener() {
		mTvMenu.setOnClickListener(this);
		mLlName.setOnClickListener(this);
		mLlValidPeriod.setOnClickListener(this);
		mLlRecord.setOnClickListener(this);
		mTvDelete.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.page_action:
				showActionDialog();
				break;

			case R.id.ll_ekey_detail_name:
				showInputDialog();
				break;

			case R.id.ll_ekey_detail_valid_period://修改有效期
				Intent intentNickname = new Intent(this, EkeyPeriodModifyActivity.class);
				intentNickname.putExtra(EkeyPeriodModifyActivity.KEY_KEY_ID, mKeyId);
				intentNickname.putExtra(EkeyPeriodModifyActivity.KEY_KEY_TYPE, mKeyType);
				intentNickname.putExtra(EkeyPeriodModifyActivity.KEY_START_TIME, mStartTime);
				intentNickname.putExtra(EkeyPeriodModifyActivity.KEY_END_TIME, mEndTime);
				startActivityForResult(intentNickname, REQUEST_CODE_MODIFY_EKEY_PERIOD);
				break;

			case R.id.ll_ekey_detail_records:
				EkeyRecordActivity.actionStart(EkeyDetailActivity.this, mKeyId, mName);
				break;

			case R.id.tv_ekey_detail_delete:
				Resources res = getResources();
				if (mIsAuth) {
					showChooseDialog();
				} else {
					DialogUtil.showCommonDialog(EkeyDetailActivity.this, null,
							res.getString(R.string.note_delete_ekey), res.getString(R.string.delete),
							res.getString(R.string.cancel),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialogInterface, int i) {
									deleteEkey("N");
								}
							}, null);
				}
				break;

			case R.id.btn_dialog_send_ekey_period://冻结、解冻
//				Resources res = getResources();
//				DialogUtil.showCommonDialog(EkeyDetailActivity.this, null,
//						res.getString(R.string.unit_percent), , , , );
				if ("110405".equals(mKeyStatus)) {//已冻结
					unfreezeEkey();
				} else {
					freezeEkey();
				}

				DIALOG.cancel();
				break;

			case R.id.btn_dialog_send_ekey_one_time://授权、反授权
				if (mIsAuth) {//已授权
					unauthEkey();
				} else {
					authEkey();
				}
				DIALOG.cancel();
				break;

			case R.id.btn_dialog_send_ekey_cancel:
				DIALOG.cancel();
				break;

			case R.id.btn_dialog_choose_cancel:
				DIALOG.cancel();
				break;

			case R.id.btn_dialog_choose_ok:
				String delType = mCbDeleteKeys.isChecked() ? "Y" : "N";
				deleteEkey(delType);
				DIALOG.cancel();
				break;

			case R.id.btn_dialog_input_cancel:
				DIALOG.cancel();
				break;

			case R.id.btn_dialog_input_ok:
				String ekeyAlias = mEtDialogInput.getText().toString();
				if (!StringUtil.isBlank(ekeyAlias)) {
					modifyEkeyAlias(ekeyAlias);
					DIALOG.cancel();
				} else {
					toast(R.string.enter_account_passwprd);
				}
				break;

			default:
				break;
		}
	}

	/**
	 * 修改钥匙别名
	 */
	private void modifyEkeyAlias(final String ekeyAlias) {
		RestClient.builder()
				.url(Urls.LOCK_EKEY_MODIFY_ALIAS)
				.loader(this)
				.params("alias", ekeyAlias)
				.params("keyId", mKeyId)
				.params("userId", PeachPreference.readUserId())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_EKEY_MODIFY_ALIAS", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							toast(R.string.note_modify_name_success);
							mName = ekeyAlias;
							refreshUI();
						} else {
							toast(R.string.note_modify_name_fail);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.note_modify_name_fail);
					}
				})
				.build()
				.post();
	}

	private void showInputDialog() {
		DIALOG = new AlertDialog.Builder(this).create();
		DIALOG.setCanceledOnTouchOutside(false);
		DIALOG.show();
		final Window window = DIALOG.getWindow();
		if (window != null) {
			window.setContentView(R.layout.dialog_input);
			window.setGravity(Gravity.CENTER);
//			window.setWindowAnimations(R.style.anim_panel_up_from_bottom);
			window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			//设置属性
			final WindowManager.LayoutParams params = window.getAttributes();
			params.width = WindowManager.LayoutParams.MATCH_PARENT;
			params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			params.dimAmount = 0.5f;
			window.setAttributes(params);

			TextView title = window.findViewById(R.id.tv_dialog_input_title);
			mEtDialogInput = window.findViewById(R.id.et_dialog_input_content);
			AppCompatButton cancel = window.findViewById(R.id.btn_dialog_input_cancel);
			AppCompatButton ok = window.findViewById(R.id.btn_dialog_input_ok);

			title.setText(R.string.modify_name);
			mEtDialogInput.setHint(mName);

			cancel.setOnClickListener(this);
			ok.setOnClickListener(this);
		}
	}

	private void showChooseDialog() {
		DIALOG = new AlertDialog.Builder(this).create();
		DIALOG.setCanceledOnTouchOutside(false);
		DIALOG.show();
		final Window window = DIALOG.getWindow();
		if (window != null) {
			window.setContentView(R.layout.dialog_choose);
			window.setGravity(Gravity.CENTER);
			window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			//设置属性
			final WindowManager.LayoutParams params = window.getAttributes();
			params.width = WindowManager.LayoutParams.MATCH_PARENT;
			params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			params.dimAmount = 0.5f;
			window.setAttributes(params);

			TextView title = window.findViewById(R.id.tv_dialog_choose_title);
			mCbDeleteKeys = window.findViewById(R.id.cb_dialog_choose_content);
			AppCompatButton cancel = window.findViewById(R.id.btn_dialog_choose_cancel);
			AppCompatButton ok = window.findViewById(R.id.btn_dialog_choose_ok);
			ok.setText(R.string.delete);

			title.setText(R.string.note_delete_autu_key);
			mCbDeleteKeys.setText(R.string.note_delete_sent_key);

			cancel.setOnClickListener(this);
			ok.setOnClickListener(this);
		}
	}

	private void showActionDialog() {
		DIALOG = new AlertDialog.Builder(this).create();
		DIALOG.show();
		final Window window = DIALOG.getWindow();
		if (window != null) {
			window.setContentView(R.layout.dialog_send_ekey_type);
			window.setGravity(Gravity.BOTTOM);
			window.setWindowAnimations(R.style.anim_panel_up_from_bottom);
			window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			//设置属性
			final WindowManager.LayoutParams params = window.getAttributes();
			params.width = WindowManager.LayoutParams.MATCH_PARENT;
			params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			params.dimAmount = 0.5f;
			window.setAttributes(params);

			AppCompatButton clearKeys = window.findViewById(R.id.btn_dialog_send_ekey_period);
			window.findViewById(R.id.btn_dialog_send_ekey_permanent).setVisibility(View.GONE);
			AppCompatButton sendKey = window.findViewById(R.id.btn_dialog_send_ekey_one_time);
			window.findViewById(R.id.btn_dialog_send_ekey_cancel).setOnClickListener(this);
			if ("110405".equals(mKeyStatus)) {//已冻结
				clearKeys.setText(R.string.unfreeze);
			} else {
				clearKeys.setText(R.string.freeze);
			}
			if (mIsAuth) {
				sendKey.setText(R.string.deauthorize);
			} else {
				sendKey.setText(R.string.authorize);
			}
			if (!mKey.isAdmin()) {
				clearKeys.setBackgroundResource(R.drawable.border_round_all);
				sendKey.setVisibility(View.GONE);
			}
			clearKeys.setOnClickListener(this);
			sendKey.setOnClickListener(this);
		}
	}

	/**
	 * 冻结用户钥匙
	 */
	private void freezeEkey() {
		RestClient.builder()
				.url(Urls.LOCK_EKEY_FREEZE)
				.loader(EkeyDetailActivity.this)
				.params("keyId", mKeyId)
				.params("userId", PeachPreference.readUserId())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_EKEY_FREEZE", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							toast(R.string.ekey_freeze_success);
							finish();
						} else if (code == 951) {
							toast(R.string.note_pending_ekey_cannot_freeze);
						} else {
							toast(R.string.ekey_freeze_fail);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.ekey_freeze_fail);
					}
				})
				.build()
				.post();
	}

	/**
	 * 解冻用户钥匙
	 */
	private void unfreezeEkey() {
		RestClient.builder()
				.url(Urls.LOCK_EKEY_UN_FREEZE)
				.loader(EkeyDetailActivity.this)
				.params("keyId", mKeyId)
				.params("userId", PeachPreference.readUserId())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_EKEY_UN_FREEZE", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							toast(R.string.ekey_un_freeze_success);
							finish();
						} else {
							toast(R.string.ekey_un_freeze_fail);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.ekey_un_freeze_fail);
					}
				})
				.build()
				.post();
	}

	/**
	 * 授权用户钥匙
	 */
	private void authEkey() {
		RestClient.builder()
				.url(Urls.LOCK_EKEY_AUTHORIZE)
				.loader(EkeyDetailActivity.this)
				.params("keyId", mKeyId)
				.params("userId", PeachPreference.readUserId())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_EKEY_AUTHORIZE", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							toast(R.string.ekey_authorize_success);
							finish();
						} else {
							toast(R.string.ekey_authorize_fail);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.ekey_authorize_fail);
					}
				})
				.build()
				.post();
	}

	/**
	 * 反授权用户钥匙
	 */
	private void unauthEkey() {
		RestClient.builder()
				.url(Urls.LOCK_EKEY_UN_AUTHORIZE)
				.loader(EkeyDetailActivity.this)
				.params("keyId", mKeyId)
				.params("userId", PeachPreference.readUserId())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_EKEY_UN_AUTHORIZE", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							toast(R.string.ekey_un_authorize_success);
							finish();
						} else {
							toast(R.string.ekey_un_authorize_fail);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.ekey_un_authorize_fail);
					}
				})
				.build()
				.post();
	}

	/**
	 * 删除钥匙（钥匙详情页面）
	 */
	private void deleteEkey(String delType) {
		RestClient.builder()
				.url(Urls.LOCK_EKEY_DELETE)
				.loader(EkeyDetailActivity.this)
				.params("keyId", mKeyId)
				.params("userId", PeachPreference.readUserId())
				.params("delType", delType)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_EKEY_DELETE", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							toast(R.string.ekey_delete_success);
							finish();
						} else {
							toast(R.string.ekey_delete_fail);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.ekey_delete_fail);
					}
				})
				.build()
				.post();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_MODIFY_EKEY_PERIOD) {
			mKeyType = 1;

			long startTime = data.getLongExtra(EkeyPeriodModifyActivity.KEY_START_TIME, mStartTime);
			long endTime = data.getLongExtra(EkeyPeriodModifyActivity.KEY_END_TIME, mEndTime);
			mStartTime = startTime;
			mEndTime = endTime;
			refreshUI();
		}
	}
}
