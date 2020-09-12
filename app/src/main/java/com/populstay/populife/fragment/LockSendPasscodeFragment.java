package com.populstay.populife.fragment;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.populstay.populife.R;
import com.populstay.populife.app.MyApplication;
import com.populstay.populife.base.BaseFragment;
import com.populstay.populife.common.Urls;
import com.populstay.populife.entity.Key;
import com.populstay.populife.enumtype.Operation;
import com.populstay.populife.eventbus.Event;
import com.populstay.populife.lock.ILockAddPasscode;
import com.populstay.populife.net.RestClient;
import com.populstay.populife.net.callback.IFailure;
import com.populstay.populife.net.callback.ISuccess;
import com.populstay.populife.ui.loader.PeachLoader;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.device.KeyboardUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.populstay.populife.util.string.StringUtil;
import com.ttlock.bl.sdk.util.DigitUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.WeakHashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;

import static com.populstay.populife.app.MyApplication.mTTLockAPI;

/**
 * 发送“永久密码” Fragment
 * Created by Jerry
 */

public class LockSendPasscodeFragment extends BaseFragment implements View.OnClickListener {

	public static final String VAL_TAB_TYPE_CUSTOMIZE = " VAL_TAB_TYPE_CUSTOMIZE";
	public static final String VAL_TAB_TYPE_PERMANENT = " VAL_TAB_TYPE_PERMANENT";
	public static final String VAL_TAB_TYPE_PERIOD = " VAL_TAB_TYPE_PERIOD";
	public static final String VAL_TAB_TYPE_ONE_TIME = " VAL_TAB_TYPE_ONE_TIME";
	public static final String VAL_TAB_TYPE_CLEAR = " VAL_TAB_TYPE_CLEAR";
	public static final String VAL_TAB_TYPE_CYCLIC = " VAL_TAB_TYPE_CYCLIC";

	private static final String KEY_TAB_TYPE = "KEY_TAB_TYPE";
	private static final String KEY_LOCK_ID = "key_lock_id";
	private static final String KEY_KEY_ID = "key_key_id";
	private static final String KEY_LOCK_NAME = "key_lock_name";
	private static final String KEY_LOCK_MAC = "key_lock_mac";
	private static final String KEY_PASSWORD_LIST = "key_password_list";

	private LinearLayout mLlCyclicMode, mLlTime,mLlCustomPwd;
	private TextView mTvCyclicMode, mTvStartTime, mTvEndTime, mTvPasscode, mTvNote, mTvGenerate;
	private EditText mEtName,mEtCustomPwd;
	private TimePickerView mTimePicker;
	private OptionsPickerView mPickerCyclic;

	private Key mKey = MyApplication.CURRENT_KEY;
	/**
	 * passcodeType		Value(int)
	 * One-time				1
	 * Permanent			2
	 * Period				3
	 * Clear				4
	 * Weekend Cyclic		5
	 * Daily Cyclic			6
	 * Workday Cyclic		7
	 * Monday Cyclic		8
	 * Tuesday Cyclic		9
	 * Wednesday Cyclic		10
	 * Thursday Cyclic		11
	 * Friday Cyclic		12
	 * Saturday Cyclic		13
	 * Sunday Cyclic		14
	 */
	private int mPasscodeType;
	private String mCurTabType = VAL_TAB_TYPE_ONE_TIME; // 默认单次密码
	private int mLockId;
	private int mKeyId;
	private String mLockMac;
	private String mLockName;
	private Date mCreateTime, mStartTime, mEndTime;
	private List<String> mCyclicModeList;
	private AlertDialog DIALOG;
	private EditText mEtDialogInput;
	private String mInputPwd;
	private ArrayList<String> mPasswordList = new ArrayList<>();

	public static LockSendPasscodeFragment newInstance(String tabType, int lockId, int keyId, String lockName, String lockMac, ArrayList<String> passwordList) {

		Bundle args = new Bundle();
		args.putString(KEY_TAB_TYPE, tabType);
		args.putInt(KEY_LOCK_ID, lockId);
		args.putInt(KEY_KEY_ID, keyId);
		args.putString(KEY_LOCK_NAME, lockName);
		args.putString(KEY_LOCK_MAC, lockMac);
		args.putStringArrayList(KEY_PASSWORD_LIST, passwordList);

		LockSendPasscodeFragment fragment = new LockSendPasscodeFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_lock_send_passcode, null);

		getIntentData();
		initView(view);
		initListener();
		return view;
	}

	private void getIntentData() {
		Bundle bundle = getArguments();
		if (bundle != null) {
			mCurTabType = bundle.getString(KEY_TAB_TYPE);
			mLockId = bundle.getInt(KEY_LOCK_ID, 0);
			mKeyId = bundle.getInt(KEY_KEY_ID, 0);
			mLockName = bundle.getString(KEY_LOCK_NAME);
			mLockMac = bundle.getString(KEY_LOCK_MAC);
			mPasswordList = bundle.getStringArrayList(KEY_PASSWORD_LIST);
		}

//		mKey = DbService.getKeyByLockmac(mLockMac);
	}

	private void initView(View view) {
		mLlCyclicMode = view.findViewById(R.id.ll_lock_send_passcode_cyclic_mode);
		mLlTime = view.findViewById(R.id.ll_lock_send_passcode_time);
		mLlCustomPwd = view.findViewById(R.id.ll_custom_pwd);
		mTvCyclicMode = view.findViewById(R.id.tv_lock_send_passcode_cyclic_mode);
		mTvStartTime = view.findViewById(R.id.tv_lock_send_passcode_start_time);
		mTvEndTime = view.findViewById(R.id.tv_lock_send_passcode_end_time);
		mTvPasscode = view.findViewById(R.id.tv_lock_send_passcode_pwd);
		mTvNote = view.findViewById(R.id.tv_lock_send_passcode_note);
		mTvGenerate = view.findViewById(R.id.tv_lock_send_passcode_generate);
		mEtName = view.findViewById(R.id.et_lock_send_passcode_name);
		mEtCustomPwd = view.findViewById(R.id.et_lock_send_passcode_password);

		initTimePicker();
		refreshUI();
	}

	private void initTimePicker() {
		//获取当前时间
		mStartTime = mEndTime = new Date();

		Calendar selectedDate = Calendar.getInstance();
		selectedDate.set(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH),
				selectedDate.get(Calendar.DAY_OF_MONTH), selectedDate.get(Calendar.HOUR_OF_DAY),
				selectedDate.get(Calendar.MINUTE));
		switch (mCurTabType) {
			case VAL_TAB_TYPE_PERIOD://限时密码
			case VAL_TAB_TYPE_CUSTOMIZE://自定义密码
				mTvStartTime.setText(DateUtil.getDateToString(mStartTime, "yyyy-MM-dd HH:00"));
				mTvEndTime.setText(DateUtil.getDateToString(mEndTime, "yyyy-MM-dd HH:00"));

				mTimePicker = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
					@Override
					public void onTimeSelect(Date date, View v) {
						((TextView) v).setText(DateUtil.getDateToString(date, "yyyy-MM-dd HH:00"));
						switch (v.getId()) {
							case R.id.tv_lock_send_passcode_start_time:
								mStartTime = date;
								break;

							case R.id.tv_lock_send_passcode_end_time:
								mEndTime = date;
								break;

							default:
								break;
						}
					}
				})
						.setType(new boolean[]{true, true, true, true, false, false})
						.setLabel(getString(R.string.unit_year), getString(R.string.unit_month), getString(R.string.unit_day),
								getString(R.string.unit_hour), getString(R.string.unit_minute), getString(R.string.unit_second))
						.setSubmitText(getResources().getString(R.string.ok))
						.setCancelText(getResources().getString(R.string.cancel))
						.setDate(selectedDate)
						.setRangDate(selectedDate, null)
						.build();
				break;

			case VAL_TAB_TYPE_CYCLIC://循环密码（默认周末）
				initCustomOptionPicker();

				mTvStartTime.setText(DateUtil.getDateToString(mStartTime, "HH:00"));
				mTvEndTime.setText(DateUtil.getDateToString(mEndTime, "HH:00"));

				mTimePicker = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
					@Override
					public void onTimeSelect(Date date, View v) {
						((TextView) v).setText(DateUtil.getDateToString(date, "HH:00"));
						switch (v.getId()) {
							case R.id.tv_lock_send_passcode_start_time:
								mStartTime = date;
								break;

							case R.id.tv_lock_send_passcode_end_time:
								mEndTime = date;
								break;

							default:
								break;
						}
					}
				})
						.setType(new boolean[]{false, false, false, true, false, false})
						.setLabel(getString(R.string.unit_year), getString(R.string.unit_month), getString(R.string.unit_day),
								getString(R.string.unit_hour), getString(R.string.unit_minute), getString(R.string.unit_second))
						.setSubmitText(getResources().getString(R.string.ok))
						.setCancelText(getResources().getString(R.string.cancel))
						.setDate(selectedDate)
						.setRangDate(selectedDate, null)
						.build();
				break;

			default:
				break;
		}
	}

	private void initCustomOptionPicker() {//条件选择器初始化，自定义布局
		mCyclicModeList = new ArrayList<>();
		mCyclicModeList.add(getString(R.string.weekend));
		mCyclicModeList.add(getString(R.string.daily));
		mCyclicModeList.add(getString(R.string.workday));
		mCyclicModeList.add(getString(R.string.monday));
		mCyclicModeList.add(getString(R.string.tuesday));
		mCyclicModeList.add(getString(R.string.wednesday));
		mCyclicModeList.add(getString(R.string.thursday));
		mCyclicModeList.add(getString(R.string.friday));
		mCyclicModeList.add(getString(R.string.saturday));
		mCyclicModeList.add(getString(R.string.sunday));
		/**
		 * @description
		 *
		 * 注意事项：
		 * 自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针。
		 * 具体可参考demo 里面的两个自定义layout布局。
		 */
		mPickerCyclic = new OptionsPickerBuilder(getActivity(), new OnOptionsSelectListener() {
			@Override
			public void onOptionsSelect(int options1, int option2, int options3, View v) {
				//返回的分别是三个级别的选中位置
				String tx = mCyclicModeList.get(options1);
				mTvCyclicMode.setText(tx);
				mPasscodeType = options1 + 5;
			}
		})
				.setLayoutRes(R.layout.pickerview_custom_cyclic_mode, new CustomListener() {
					@Override
					public void customLayout(View v) {
						final TextView tvSubmit = v.findViewById(R.id.tv_finish);
						TextView tvCancel = v.findViewById(R.id.iv_cancel);
						tvSubmit.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								mPickerCyclic.returnData();
								mPickerCyclic.dismiss();
							}
						});

						tvCancel.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								mPickerCyclic.dismiss();
							}
						});
					}
				})
				.isDialog(true)
				.build();

		mPickerCyclic.setPicker(mCyclicModeList);//添加数据
	}

	private void refreshUI() {
		mLlCustomPwd.setVisibility(View.GONE);
		switch (mCurTabType) {
			case VAL_TAB_TYPE_PERMANENT://永久密码
				mPasscodeType = 2;
				mLlCyclicMode.setVisibility(View.GONE);
				mLlTime.setVisibility(View.GONE);
				mTvNote.setText(R.string.note_password_permanent);
				break;

			case VAL_TAB_TYPE_PERIOD://限时密码
				mPasscodeType = 3;
				mLlCyclicMode.setVisibility(View.GONE);
				mTvNote.setText(R.string.note_password_period);
				break;

			case VAL_TAB_TYPE_ONE_TIME://单次密码
				mPasscodeType = 1;
				mLlCyclicMode.setVisibility(View.GONE);
				mLlTime.setVisibility(View.GONE);
				mTvNote.setText(R.string.note_password_one_time);
				break;

			case VAL_TAB_TYPE_CLEAR://清空密码
				mPasscodeType = 4;
				mLlCyclicMode.setVisibility(View.GONE);
				mLlTime.setVisibility(View.GONE);
				mTvNote.setText(R.string.note_password_clear);
				break;

			case VAL_TAB_TYPE_CUSTOMIZE://自定义密码
				mPasscodeType = 3;
				mLlCyclicMode.setVisibility(View.GONE);
				mLlCustomPwd.setVisibility(View.VISIBLE);
				mTvNote.setText(R.string.note_password_customize);
				break;

			case VAL_TAB_TYPE_CYCLIC://循环密码
				mPasscodeType = 5;
				mTvNote.setText(R.string.note_password_period);
				break;

			default:
				break;
		}
	}

	private void initListener() {
		mTvCyclicMode.setOnClickListener(this);
		mTvStartTime.setOnClickListener(this);
		mTvEndTime.setOnClickListener(this);
		mTvGenerate.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.tv_lock_send_passcode_cyclic_mode:
				mPickerCyclic.show();
				break;

			case R.id.tv_lock_send_passcode_start_time:
			case R.id.tv_lock_send_passcode_end_time:
				KeyboardUtil.hideSoftInput(view);
				mTimePicker.show(view);
				break;

			case R.id.tv_lock_send_passcode_generate:
				if (checkForm()) {
					if (VAL_TAB_TYPE_CUSTOMIZE.equals(mCurTabType)) {//自定义密码，先和锁通信
						//showInputDialog();
						mInputPwd = mEtCustomPwd.getText().toString();
						if (!StringUtil.isBlank(mInputPwd) && StringUtil.isNum(mInputPwd)
								&& mInputPwd.length() >= 6 && mInputPwd.length() <= 9) {

							if (isNetEnable()){
								if (isBleEnableNotHint()) {
									checkPasswordExist(mInputPwd);
								}else {
									if (DigitUtil.isSupportRemoteUnlock(mKey.getSpecialValue())){
										requestAddPasscode(mInputPwd,"2");
									}else {
										toast(R.string.enable_bluetooth);
									}
								}
							}
						} else {
							toast(R.string.note_passcode_invalid);
						}

					} else {//直接和后台获取密码
						generatePasscode();
					}
				}
				break;

			case R.id.btn_dialog_input_cancel:
				DIALOG.cancel();
				break;

			case R.id.btn_dialog_input_ok:
				mInputPwd = mEtDialogInput.getText().toString();
				if (!StringUtil.isBlank(mInputPwd) && StringUtil.isNum(mInputPwd)
						&& mInputPwd.length() >= 6 && mInputPwd.length() <= 9) {
					if (isBleNetEnable()) {
						checkPasswordExist(mInputPwd);
					}
				} else {
					toast(R.string.note_passcode_invalid);
				}
				break;

			default:
				break;
		}
	}

	/**
	 * 检查密码是否已存在
	 */
	public void checkPasswordExist(String pwd) {
		boolean isExist = false;
		for (String password : mPasswordList) {
			if (pwd.equals(password)) {
				isExist = true;
				break;
			}
		}
		if (!isExist) {
			addKeyboardPasscode(mInputPwd);
			//DIALOG.cancel();
		} else {
			toast(R.string.note_password_exist);
		}
	}

	private void showInputDialog() {
		DIALOG = new AlertDialog.Builder(getActivity()).create();
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

			title.setText(R.string.enter_customized_passcode);
			mEtDialogInput.setHint(R.string.passcode_format_6_9_digits);
			mEtDialogInput.setInputType(InputType.TYPE_CLASS_NUMBER);
			mEtDialogInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});
			mEtDialogInput.setMaxLines(1);

			cancel.setOnClickListener(this);
			ok.setOnClickListener(this);
		}
	}

	/**
	 * 添加自定义键盘密码
	 */
	private void addKeyboardPasscode(String pwd) {
		PeachLoader.showLoading(getActivity());
		long startDate = DateUtil.getStringToDate(mTvStartTime.getText().toString(), "yyyy-MM-dd HH:mm");
		long endDate = DateUtil.getStringToDate(mTvEndTime.getText().toString(), "yyyy-MM-dd HH:mm");
		if (mTTLockAPI.isConnected(mKey.getLockMac())) {
			setAddPwdLockCallback(startDate, endDate, pwd);
			mTTLockAPI.addPeriodKeyboardPassword(null,
					PeachPreference.getOpenid(), mKey.getLockVersion(),
					mKey.getAdminPwd(), mKey.getLockKey(), mKey.getLockFlagPos(), pwd,
					startDate, endDate, mKey.getAesKeyStr(),
					(long) TimeZone.getDefault().getOffset(System.currentTimeMillis()));
		} else {
			setAddPwdLockCallback(startDate, endDate, pwd);
			mTTLockAPI.connect(mKey.getLockMac());
		}
	}

	private void setAddPwdLockCallback(long startDate, long endDate, final String pwd) {
		MyApplication.bleSession.setOperation(Operation.ADD_PASSCODE);
		MyApplication.bleSession.setLockmac(mKey.getLockMac());
		MyApplication.bleSession.setPassword(pwd);
		MyApplication.bleSession.setStartDate(startDate);
		MyApplication.bleSession.setEndDate(endDate);

		MyApplication.bleSession.setILockAddPasscode(new ILockAddPasscode() {
			@Override
			public void onSuccess() {
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							PeachLoader.stopLoading();
							requestAddPasscode(pwd,"1");
						}
					});
				}
			}

			@Override
			public void onFail() {
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							PeachLoader.stopLoading();
							toastFail();
						}
					});
				}
			}

			@Override
			public void onTimeOut() {
				// 连接超时说明不在锁附近，用网关设置自定义吗
				if (DigitUtil.isSupportRemoteUnlock(mKey.getSpecialValue())){
					requestAddPasscode(mInputPwd,"2");
				}else {
					toastFail();
				}
			}
		});
	}

	/**
	 * 请求服务器，添加键盘密码
	 *
	 * @param keyboardPwd 键盘密码
	 * @param mediumType 通讯介质（1：蓝牙，2：网关，默认是1）
	 */
	private void requestAddPasscode(final String keyboardPwd, final String mediumType) {
		RestClient.builder()
				.url(Urls.LOCK_PASSCODE_ADD)
				.loader(getActivity())
				.params("userId", PeachPreference.readUserId())
				.params("lockId", mKey.getLockId())
				.params("keyboardPwd", keyboardPwd)
				.params("startDate", mTvStartTime.getText().toString())
				.params("endDate", mTvEndTime.getText().toString())
				.params("timeZone", DateUtil.getTimeZone())
				.params("keyId", mKey.getKeyId())
				.params("alias", mEtName.getText().toString().trim())
				.params("mediumType", mediumType)
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_PASSCODE_ADD", response);
						EventBus.getDefault().post(new Event(Event.EventType.CREATE_PWD_SUCCESS));

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							mTvPasscode.setText(keyboardPwd);
						} else {
							toast(R.string.note_passcode_customize_fail);
						}
					}
				})
				.build()
				.post();

	}

	public boolean isPasscodeGenerated() {
		return !StringUtil.isBlank(mTvPasscode.getText().toString());
	}

	public boolean checkForm() {
		boolean isPass = true;
		if (VAL_TAB_TYPE_PERIOD.equals(mCurTabType) || VAL_TAB_TYPE_CUSTOMIZE.equals(mCurTabType)
				|| VAL_TAB_TYPE_CYCLIC.equals(mCurTabType)) {//密码类型：限时、自定义、循环
			if (!mStartTime.before(mEndTime)) {
				isPass = false;
				toast(R.string.note_time_start_greater_than_end);
			}
		}
		return isPass;
	}

	/**
	 * OnekeyShare 自带分享弹窗 UI
	 */
	public void showShare() {
		OnekeyShare oks = new OnekeyShare();

		// 自定义分享平台
		oks.setCustomerLogo(BitmapFactory.decodeResource(getResources(), R.drawable.ic_share_zalo),
				"Zalo", new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						try {
							Intent vIt = new Intent(Intent.ACTION_SEND);
//							vIt.setPackage("com.facebook.orca");
							vIt.setType("text/plain");
							vIt.putExtra(Intent.EXTRA_TEXT, getShareContent());
							startActivity(vIt);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		//关闭sso授权
		oks.disableSSOWhenAuthorize();
		oks.setCallback(new PlatformActionListener() {
			@Override
			public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

			}

			@Override
			public void onError(Platform platform, int i, Throwable throwable) {

			}

			@Override
			public void onCancel(Platform platform, int i) {

			}
		});

		// title标题，微信、QQ和QQ空间等平台使用
		oks.setTitle(getString(R.string.app_name));
		// titleUrl QQ和QQ空间跳转链接
//		oks.setTitleUrl("http://sharesdk.cn");
//		oks.setAddress("13201812820");
		// text是分享文本，所有平台都需要这个字段
		oks.setText(getShareContent());
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//		oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url在微信、微博，Facebook等平台中使用
//		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网使用
//		oks.setComment("我是测试评论文本");
		// 启动分享GUI
		oks.show(getActivity());
	}

	private String getShareContent() {
		String content = "";
		String type = "";
		/**
		 * passcodeType		Value(int)
		 * One-time				1
		 * Permanent			2
		 * Period				3
		 * Clear				4
		 * Weekend Cyclic		5
		 * Daily Cyclic			6
		 * Workday Cyclic		7
		 * Monday Cyclic		8
		 * Tuesday Cyclic		9
		 * Wednesday Cyclic		10
		 * Thursday Cyclic		11
		 * Friday Cyclic		12
		 * Saturday Cyclic		13
		 * Sunday Cyclic		14
		 */
		switch (mPasscodeType) {
			case 1:
				type = getString(R.string.one_time);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) +
						DateUtil.getDateToString(mCreateTime, "yyyy-MM-dd HH:mm") + getString(R.string.use_it_within_6_hours) + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.one_time) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mLockName + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 2:
				type = getString(R.string.permanent);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mCreateTime, "yyyy-MM-dd HH:mm") + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.permanent) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mLockName + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_use_passcode_once_before) + DateUtil.getDateToString(DateUtil.getStringToDate(DateUtil.getDateToString(mCreateTime, "yyyy-MM-dd HH:mm"), "yyyy-MM-dd HH:mm") + 1000 * 3600 * 24, "yyyy-MM-dd HH:mm") + getString(R.string.no_key_bottom_right_dont_share_passcode);
				break;

			case 3:
				type = getString(R.string.period);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.period) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mLockName + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_use_passcode_once_before) + DateUtil.getDateToString(DateUtil.getStringToDate(mTvStartTime.getText().toString(), "yyyy-MM-dd HH:mm") + 1000 * 3600 * 24, "yyyy-MM-dd HH:mm") + getString(R.string.no_key_bottom_right_dont_share_passcode);

				break;

			case 4:
				type = getString(R.string.clear);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + DateUtil.getDateToString(mCreateTime, "yyyy-MM-dd HH:mm") + getString(R.string.use_it_within_24_hours) + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.clear) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mLockName + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 5:
				type = getString(R.string.weekend_cyclic);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.weekend_cyclic) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mLockName + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 6:
				type = getString(R.string.daily_cyclic);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.daily_cyclic) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mLockName + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 7:
				type = getString(R.string.workday_cyclic);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.workday_cyclic) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mLockName + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 8:
				type = getString(R.string.monday_cyclic);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.monday_cyclic) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mLockName + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 9:
				type = getString(R.string.tuesday_cyclic);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.tuesday_cyclic) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mLockName + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 10:
				type = getString(R.string.wednesday_cyclic);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.wednesday_cyclic) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mLockName + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 11:
				type = getString(R.string.thursday_cyclic);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.thursday_cyclic) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mLockName + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 12:
				type = getString(R.string.friday_cyclic);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.friday_cyclic) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mLockName + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 13:
				type = getString(R.string.saturday_cyclic);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.saturday_cyclic) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mLockName + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			case 14:
				type = getString(R.string.sunday_cyclic);
				content = getString(R.string.hello_here_is_your_passcode) + mTvPasscode.getText().toString() + "\n" +
						getString(R.string.start_time) + getString(R.string.symbol_colon) + mTvStartTime.getText().toString() + "\n" +
						getString(R.string.end_time) + getString(R.string.symbol_colon) + mTvEndTime.getText().toString() + "\n" +
						getString(R.string.type) + getString(R.string.symbol_colon) + getString(R.string.sunday_cyclic) + "\n" +
						getString(R.string.lock_name) + getString(R.string.symbol_colon) + mLockName + "\n" +
						"\n" +
						getString(R.string.to_unlock_press_no_passcode_no) + "\n" +
						"\n" +
						getString(R.string.note_no_key_bottom_right_dont_share_passcode);
				break;

			default:
				break;
		}

		return content;
	}

	/**
	 * 获取键盘密码
	 */
	public void generatePasscode() {
		mCreateTime = new Date(); //获取当前时间

		RestClient.builder()
				.url(Urls.LOCK_PASSCODE_GENERATE)
				.loader(getActivity())
				.params(getParams())
				.success(new ISuccess() {
					@Override
					public void onSuccess(String response) {
						PeachLogger.d("LOCK_PASSCODE_GENERATE", response);

						JSONObject result = JSON.parseObject(response);
						int code = result.getInteger("code");
						if (code == 200) {
							EventBus.getDefault().post(new Event(Event.EventType.CREATE_PWD_SUCCESS));
							JSONObject passcodeInfo = result.getJSONObject("data");
							mTvPasscode.setText(passcodeInfo.getString("keyboardPwd"));
						} else {
							toast(R.string.create_password_fail);
						}
					}
				})
				.failure(new IFailure() {
					@Override
					public void onFailure() {
						toast(R.string.create_password_fail);
					}
				})
				.build()
				.post();
	}

	private WeakHashMap<String, Object> getParams() {
		WeakHashMap<String, Object> params = new WeakHashMap<>();

		params.put("userId", PeachPreference.readUserId());
		params.put("lockId", mLockId);
		params.put("keyboardPwdVersion", 4);//键盘密码版本, 三代锁的密码版本为4
		params.put("keyboardPwdType", mPasscodeType);
		params.put("keyId", mKeyId);
		params.put("alias", mEtName.getText().toString().trim());

		if (VAL_TAB_TYPE_PERIOD.equals(mCurTabType) || VAL_TAB_TYPE_CUSTOMIZE.equals(mCurTabType)
				|| VAL_TAB_TYPE_CYCLIC.equals(mCurTabType)) {//密码类型：限时、自定义、循环
			params.put("startDate", mTvStartTime.getText().toString());
			params.put("endDate", mTvEndTime.getText().toString());
		}

		params.put("timeZone", DateUtil.getTimeZone());

		return params;
	}
}
