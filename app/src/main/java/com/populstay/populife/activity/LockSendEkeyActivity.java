package com.populstay.populife.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.eventbus.Event;
import com.populstay.populife.keypwdmanage.entity.CreateBluetoothActionInfo;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.permission.PermissionListener;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.device.KeyboardUtil;
import com.populstay.populife.util.locale.LanguageUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.string.StringUtil;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.WeakHashMap;

public class LockSendEkeyActivity extends BaseActivity implements View.OnClickListener {

	private static final String KEY_LOCK_ID = "key_lock_id";
	private static final String KEY_IS_ADMIN = "key_is_admin";
	private final int REQUEST_CONTACT = 3;

	private AlertDialog DIALOG;
	private LinearLayout mLlTime, mLlAuth;
	private TextView mTvKeyType, mTvStartTime, mTvEndTime, mTvOneTimeNote, mTvSend;
	private CountryCodePicker mCountryCodePicker;
	private ImageView mIvContact;
	private EditText mEtReceiver, mEtKeyName;
	private Switch mSwitchAuth, mSwitchRemoteUnlock;
	//时间选择器
	private TimePickerView mTimePicker;

	private int mLockId;
	private boolean mIsAdmin;
	private int mKeyType = 1;//钥匙类型（1限时，2永久，3单次）
	private Date mStartTime;
	private Date mEndTime;

	private CreateBluetoothActionInfo mCreateBluetoothActionInfo;

	/**
	 * 启动当前 activity
	 *
	 * @param context 上下文
	 * @param lockId  锁 id
	 * @param isAdmin 是否为管理员
	 */
	public static void actionStart(Context context, int lockId, boolean isAdmin) {
		Intent intent = new Intent(context, LockSendEkeyActivity.class);
		intent.putExtra(KEY_LOCK_ID, lockId);
		intent.putExtra(KEY_IS_ADMIN, isAdmin);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_send_ekey);

		getIntentData();
		initView();
		initListener();
	}

	private void getIntentData() {
		Intent data = getIntent();
		mLockId = data.getIntExtra(KEY_LOCK_ID, 0);
		// 没有授权管理员
		//mIsAdmin = data.getBooleanExtra(KEY_IS_ADMIN, false);
	}

	private void initView() {
		((TextView) findViewById(R.id.page_title)).setText(R.string.create_bluetooth_key);
		findViewById(R.id.page_action).setVisibility(View.GONE);

		mLlTime = findViewById(R.id.ll_lock_send_ekey_time);
		mLlAuth = findViewById(R.id.ll_lock_send_ekey_auth);
		mTvKeyType = findViewById(R.id.tv_lock_send_ekey_type);
		mTvStartTime = findViewById(R.id.tv_lock_send_ekey_start_time);
		mTvEndTime = findViewById(R.id.tv_lock_send_ekey_end_time);
		mTvOneTimeNote = findViewById(R.id.tv_lock_send_ekey_note);
		mCountryCodePicker = findViewById(R.id.cpp_lock_send_ekey);
		mIvContact = findViewById(R.id.iv_lock_send_ekey_receiver);
		mEtReceiver = findViewById(R.id.et_lock_send_ekey_receiver);
		mEtKeyName = findViewById(R.id.et_lock_send_ekey_name);
		mSwitchAuth = findViewById(R.id.switch_lock_send_ekey_auth);
		mSwitchRemoteUnlock = findViewById(R.id.switch_lock_send_ekey_remote_unlock);
		mTvSend = findViewById(R.id.tv_lock_send_ekey_send);

		if (mIsAdmin) {
			mLlAuth.setVisibility(View.VISIBLE);
		} else {
			mLlAuth.setVisibility(View.GONE);
		}

		setCountryInfo();

		initTimePicker();
	}

	/**
	 * 设置国家信息（国家简称 + 国家码）
	 */
	private void setCountryInfo() {

		requestRuntimePermissions(new String[]{Manifest.permission.READ_PHONE_STATE,
				Manifest.permission.ACCESS_COARSE_LOCATION}, new PermissionListener() {
			@Override
			public void onGranted() {
				// 获取当前国家的国家码
				String countryNameCode = LanguageUtil.getCountryNameCode(LockSendEkeyActivity.this);
				mCountryCodePicker.setCountryForNameCode(countryNameCode);
			}

			@Override
			public void onDenied(List<String> deniedPermissions) {
				toast(R.string.note_permission);
			}
		});
	}

	private void initTimePicker() {
		//获取当前时间
		mStartTime = mEndTime = new Date();

		mTvStartTime.setText(DateUtil.getDateToString(mStartTime, "yyyy-MM-dd HH:mm"));
		mTvEndTime.setText(DateUtil.getDateToString(mEndTime, "yyyy-MM-dd HH:mm"));

		Calendar selectedDate = Calendar.getInstance();
		selectedDate.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH),
				selectedDate.get(Calendar.DAY_OF_MONTH), selectedDate.get(Calendar.HOUR_OF_DAY),
				selectedDate.get(Calendar.MINUTE));


		mTimePicker = new TimePickerBuilder(this, new OnTimeSelectListener() {
			@Override
			public void onTimeSelect(Date date, View v) {
				((TextView) v).setText(DateUtil.getDateToString(date, "yyyy-MM-dd HH:mm"));
				switch (v.getId()) {
					case R.id.tv_lock_send_ekey_start_time:
						mStartTime = date;
						break;

					case R.id.tv_lock_send_ekey_end_time:
						mEndTime = date;
						break;

					default:
						break;
				}
			}
		})
				.setType(new boolean[]{true, true, true, true, true, false})
				.setLabel(getString(R.string.unit_year), getString(R.string.unit_month), getString(R.string.unit_day),
						getString(R.string.unit_hour), getString(R.string.unit_minute), getString(R.string.unit_second))
				.setSubmitText(getResources().getString(R.string.ok))
				.setCancelText(getResources().getString(R.string.cancel))
				.setDate(selectedDate)
				.setCancelColor(0Xff212322)
				.setSubmitColor(0xff212322)
				.setRangDate(selectedDate, null)
				.build();
	}

	private void initListener() {
		mTvKeyType.setOnClickListener(this);
		mIvContact.setOnClickListener(this);
		mTvStartTime.setOnClickListener(this);
		mTvEndTime.setOnClickListener(this);
		mTvSend.setOnClickListener(this);

		/*mEtReceiver.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				*//*mTvSend.setEnabled(!StringUtil.isBlank(editable.toString().trim()));
				mCountryCodePicker.setVisibility(editable.length() == 0 ||
						StringUtil.isNum(editable.toString()) ? View.VISIBLE : View.GONE);*//*
			}
		});*/
		mEtKeyName.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				mTvSend.setEnabled(!StringUtil.isBlank(editable.toString().trim()));
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.tv_lock_send_ekey_type:
				showTypeSelDialog();
				break;

			case R.id.iv_lock_send_ekey_receiver:
				requestRuntimePermissions(new String[]{Manifest.permission.READ_CONTACTS}, new PermissionListener() {
					@Override
					public void onGranted() {
						Intent intent = new Intent();
						intent.setAction(Intent.ACTION_PICK);
						intent.setData(ContactsContract.Contacts.CONTENT_URI);
						startActivityForResult(intent, REQUEST_CONTACT);
					}

					@Override
					public void onDenied(List<String> deniedPermissions) {
						toast(getString(R.string.note_permission_contact));
					}
				});
				break;

			case R.id.tv_lock_send_ekey_start_time:
			case R.id.tv_lock_send_ekey_end_time:
				KeyboardUtil.hideSoftInput(view);
				mTimePicker.show(view);
				break;

			case R.id.tv_lock_send_ekey_send:
				if (checkForm()) {
					sendEkey();
				}
				break;

			case R.id.btn_dialog_send_ekey_period:
				mLlTime.setVisibility(View.VISIBLE);
				if (mIsAdmin) {
					mLlAuth.setVisibility(View.VISIBLE);
				} else {
					mLlAuth.setVisibility(View.GONE);
				}
				mTvOneTimeNote.setVisibility(View.GONE);
				mTvKeyType.setText(R.string.period);
				mKeyType = 1;
				DIALOG.cancel();
				break;

			case R.id.btn_dialog_send_ekey_permanent:
				mLlTime.setVisibility(View.GONE);
				if (mIsAdmin) {
					mLlAuth.setVisibility(View.VISIBLE);
				} else {
					mLlAuth.setVisibility(View.GONE);
				}
				mTvOneTimeNote.setVisibility(View.GONE);
				mTvKeyType.setText(R.string.permanent);
				mKeyType = 2;
				DIALOG.cancel();
				break;

			case R.id.btn_dialog_send_ekey_one_time:
				mLlTime.setVisibility(View.GONE);
				mLlAuth.setVisibility(View.GONE);
				mTvOneTimeNote.setVisibility(View.VISIBLE);
				mTvKeyType.setText(R.string.one_time);
				mKeyType = 3;
				DIALOG.cancel();
				break;

			case R.id.btn_dialog_send_ekey_cancel:
				DIALOG.cancel();
				break;

			default:
				break;
		}
	}

	private boolean checkForm() {
		final String receiver = mEtReceiver.getText().toString().trim();

		boolean isPass = true;
		/*if (mKeyType != 1) {//非限时钥匙
			if (!StringUtil.isNum(receiver) && !StringUtil.isEmail(receiver)) {
				isPass = false;
				toast(R.string.note_receiver_format);
			}
		} else {//限时钥匙
			if (!StringUtil.isNum(receiver) && !StringUtil.isEmail(receiver)) {
				isPass = false;
				toast(R.string.note_receiver_format);
			} else if (!mStartTime.before(mEndTime)) {
				isPass = false;
				toast(R.string.note_time_start_greater_than_end);
			}
		}*/
		if (!mStartTime.before(mEndTime)) {
			isPass = false;
			toast(R.string.note_time_start_greater_than_end);
		}
		return isPass;
	}

	private void showTypeSelDialog() {
		DIALOG = new AlertDialog.Builder(this).create();
		DIALOG.show();
		final Window window = DIALOG.getWindow();
		if (window != null) {
			window.setContentView(R.layout.dialog_send_ekey_type);
			window.setGravity(Gravity.CENTER);
			//window.setWindowAnimations(R.style.anim_panel_up_from_bottom);
			window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			//设置属性
			final WindowManager.LayoutParams params = window.getAttributes();
			params.width = WindowManager.LayoutParams.WRAP_CONTENT;
			params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			params.dimAmount = 0.5f;
			window.setAttributes(params);

			window.findViewById(R.id.btn_dialog_send_ekey_period).setOnClickListener(this);
			window.findViewById(R.id.btn_dialog_send_ekey_permanent).setOnClickListener(this);
			window.findViewById(R.id.btn_dialog_send_ekey_one_time).setOnClickListener(this);
			window.findViewById(R.id.btn_dialog_send_ekey_cancel).setOnClickListener(this);
		}
	}

	private void showShareKeyDialog() {
		DIALOG = new AlertDialog.Builder(this).create();
		DIALOG.show();
		final Window window = DIALOG.getWindow();
		if (window != null) {
			window.setContentView(R.layout.dialog_share_key);
			window.setGravity(Gravity.CENTER);
			window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			//设置属性
			final WindowManager.LayoutParams params = window.getAttributes();
			params.width = WindowManager.LayoutParams.WRAP_CONTENT;
			params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			params.dimAmount = 0.5f;
			window.setAttributes(params);

			window.findViewById(R.id.tv_share_btn).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (null != mCreateBluetoothActionInfo){
						mCreateBluetoothActionInfo.setShare(true);
					}
					EventBus.getDefault().post(new Event(Event.EventType.CREATE_BT_KEY_SUCCESS, mCreateBluetoothActionInfo));
					finish();
				}
			});
			window.findViewById(R.id.tv_skip_btn).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (null != mCreateBluetoothActionInfo){
						mCreateBluetoothActionInfo.setShare(false);
					}
					EventBus.getDefault().post(new Event(Event.EventType.CREATE_BT_KEY_SUCCESS, mCreateBluetoothActionInfo));
					finish();
				}
			});
		}
	}

	/**
	 * 发送 ekey
	 */
	private void sendEkey() {
		RestClient.builder()
				.url(Urls.LOCK_EKEY_V2_SEND)
				.loader(LockSendEkeyActivity.this)
				.params(getParams())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						switch (code) {
							case 200:
								toast(R.string.send_ekey_success);
								String  data = result.getString("data");
								mCreateBluetoothActionInfo = new CreateBluetoothActionInfo();
								mCreateBluetoothActionInfo.setShareUrl(data);
								showShareKeyDialog();
								//LockManageBluetoothKeyActivity.actionStart(LockSendEkeyActivity.this, mLockId, mIsAdmin);
								break;

							case 920:
								toast(R.string.note_receive_user_not_found);
								break;

							case 951:
								toast(R.string.note_send_ekey_to_registered_user);
								break;

							case 952:
								toast(R.string.note_cannot_send_ekey_to_yourself);
								break;

							case 953:
								toast(R.string.note_no_auth_send_ekey);
								break;

							case 954:
								toast(R.string.note_cannot_exceed_expiration_send_ekey);
								break;

							default:
								toast(R.string.send_ekey_fail);
								break;
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.send_ekey_fail);
					}
				})
				.build()
				.post();
	}

	private WeakHashMap<String, Object> getParams() {
		WeakHashMap<String, Object> params = new WeakHashMap<>();

		params.put("userId", PeachPreference.readUserId());
		String receiver = mEtReceiver.getText().toString();
		//params.put("recUser", StringUtil.isNum(receiver) ? mCountryCodePicker.getSelectedCountryCodeWithPlus() + receiver : receiver);
		params.put("lockId", mLockId);
		params.put("type", mKeyType);
		params.put("keyAlias", mEtKeyName.getText().toString());

		if (mKeyType == 1) {//限时钥匙
			params.put("startDate", mTvStartTime.getText().toString());
			params.put("endDate", mTvEndTime.getText().toString());
			params.put("timeZone", DateUtil.getTimeZone());
		}

		if (mIsAdmin) {
			params.put("auAdmin", mSwitchAuth.isChecked());
//			params.put("arUnlock", mSwitchRemoteUnlock.isChecked());
		}
		return params;
	}

	private String[] getPhoneContacts(Uri uri) {
		String[] contact = new String[2];
		//得到ContentResolver对象
		ContentResolver cr = getContentResolver();
		//取得电话本中开始一项的光标
		Cursor cursor = cr.query(uri, null, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
			//取得联系人名字
			int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
			contact[0] = cursor.getString(nameFieldColumnIndex);

			//取得电话号码
			String ContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			Cursor phone = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + ContactId, null, null);

			if (phone != null) {
				phone.moveToFirst();
				contact[1] = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

				phone.close();
			}

			cursor.close();
		}

		return contact;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CONTACT) {
			if (data == null) {
				return;
			}
			Uri result = data.getData();
			String[] contact = getPhoneContacts(result);

			String phoneNum = contact[1].replaceAll(" ", "")
					.replaceFirst("^0*", "");

			mEtReceiver.setText(phoneNum);
			mEtKeyName.setText(contact[0]);
		}
	}
}
