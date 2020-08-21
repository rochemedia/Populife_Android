package com.populstay.populife.maintservice;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import com.populstay.populife.BuildConfig;
import com.populstay.populife.R;
import com.populstay.populife.base.BaseActivity;
import com.populstay.populife.common.Urls;
import com.populstay.populife.home.entity.HomeDevice;
import com.populstay.populife.home.entity.HomeDeviceInfo;
import com.populstay.populife.maintservice.adapter.ProductListAdapter;
import com.populstay.populife.permission.PermissionListener;
import com.populstay.populife.ui.loader.LoaderStyle;
import com.populstay.populife.ui.loader.PeachLoader;
import com.populstay.populife.ui.widget.CalendarDialog;
import com.populstay.populife.ui.widget.exedittext.ExEditText;
import com.populstay.populife.util.Utils;
import com.populstay.populife.util.activity.ActivityCollector;
import com.populstay.populife.util.date.DateUtil;
import com.populstay.populife.util.file.FileUtil;
import com.populstay.populife.util.log.PeachLogger;
import com.populstay.populife.util.storage.PeachPreference;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.ex.CountryCodeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MaintenanceRequestActivity extends BaseActivity implements View.OnClickListener {

    public static final int SELECT_COUNTRY_INTENT_CODE = 001;
    private static final int REQUEST_CODE_PERMISSION = 002;
    private static final int REQUEST_CODE_CARMERA = 003;
    private static final int REQUEST_CODE_PICK = 004;

    private TextView mTvServiceProcessGuideHint;
    private RecyclerView mProductListView;
    private ProductListAdapter mProductListAdapter;
    private List<HomeDevice> mProductList;

    private View mContentView, mSubmitSuccessLayout;
    private TextView mPageTitle;

    private TextView mTvSelectCountryBtn,mTvSelectDateBtn,mTvSelectCertificateBtn;
    private TextView selectCountryInfo, selectPhoneAreaCode;

    private CalendarDialog mCalendarDialog;

    // 1选择所在国家操作，2选择物流信息所在国家操作,3选择物流号码所在国家操作
    private int curSelectCountryType = SELECT_COUNTRY_TYPE_YOUR_COUNTRY;
    public static final int SELECT_COUNTRY_TYPE_YOUR_COUNTRY = 1;
    public static final int SELECT_COUNTRY_TYPE_LOGISTICS_COUNTRY =2;
    public static final int SELECT_COUNTRY_TYPE_PHONE_COUNTRY =3;
    private ExEditText etName,etPostalCode, etAddrDetail, etCity, etProvince, etPhone, etEmail, etProductEquipmentNumber;
    private EditText etProblemDescription;

    private TextView tvMaintenanceRequestBtn;

    private Country mYourCountry,mSelectLogisticsCountry,mSelectPhoneCountry;
    private String selectDateStr;
    private TextView mTvWordsNum;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_request);
        initTitleBar();
        initData();
        initView();
        setListener();
    }

    private void initTitleBar() {
        mPageTitle = findViewById(R.id.page_title);
        mPageTitle.setText(R.string.maintenance_request);
        findViewById(R.id.page_action).setVisibility(View.GONE);
    }

    private void initView() {
        mContentView = findViewById(R.id.content_view);
        mSubmitSuccessLayout = findViewById(R.id.maintenance_request_success_layout);
        mTvServiceProcessGuideHint = findViewById(R.id.tv_after_sales_service_process_guide_hint);
        initProductListView();
        mTvSelectCountryBtn = findViewById(R.id.tv_select_country_btn);
        mTvSelectDateBtn = findViewById(R.id.tv_select_date_btn);
        mTvSelectCertificateBtn = findViewById(R.id.tv_select_certificate_btn);
        selectCountryInfo = findViewById(R.id.selectCountryInfo);
        selectPhoneAreaCode = findViewById(R.id.selectPhoneAreaCode);

        etName = findViewById(R.id.et_name);
        etPostalCode = findViewById(R.id.et_postal_code);
        etAddrDetail = findViewById(R.id.et_addr_detail);
        etCity = findViewById(R.id.et_city);
        etProvince = findViewById(R.id.et_province);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        etProblemDescription = findViewById(R.id.et_problem_description);
        etProductEquipmentNumber = findViewById(R.id.et_product_equipment_number);
        mTvWordsNum = findViewById(R.id.tv_feedback_submit_words_num);
        mTvWordsNum.setText(getString(R.string.num_zero) + getString(R.string.words_num_limit_200));

        tvMaintenanceRequestBtn = findViewById(R.id.tv_maintenance_request_btn);
    }



    private void showSubmitSuccessLayout(){
        mContentView.setVisibility(View.GONE);
        mPageTitle.setText(R.string.submit_success);
        mSubmitSuccessLayout.setVisibility(View.VISIBLE);
        findViewById(R.id.tv_maintenance_request_finish_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData() {
        mProductList = new ArrayList<>();

        // 横闩锁
        HomeDevice device = new HomeDevice();
        device.setName(HomeDeviceInfo.IDeviceName.NAME_LOCK_DEADBOLT);
        device.setModelNum(HomeDeviceInfo.IModelNum.NAME_LOCK_DEADBOLT);
        mProductList.add(device);

        // 密码锁
        device = new HomeDevice();
        device.setName(HomeDeviceInfo.IDeviceName.NAME_LOCK_KEY_BOX);
        device.setModelNum(HomeDeviceInfo.IModelNum.NAME_LOCK_KEY_BOX);
        mProductList.add(device);

        // 网关
        device = new HomeDevice();
        device.setName(HomeDeviceInfo.IDeviceName.NAEM_GATEWAY);
        device.setModelNum(HomeDeviceInfo.IModelNum.NAEM_GATEWAY);
        mProductList.add(device);

    }

    private void initProductListView() {
        mProductListView = findViewById(R.id.product_list_view);
        mProductListView.setLayoutManager(new GridLayoutManager(this,2));
        mProductListAdapter = new ProductListAdapter(mProductList,this);
        mProductListView.setAdapter(mProductListAdapter);
        mProductListAdapter.setOnItemClickListener(new ProductListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                mProductListAdapter.selectItem(position);
            }
        });

    }

    private void setListener() {
        mTvServiceProcessGuideHint.setOnClickListener(this);
        mTvSelectCountryBtn.setOnClickListener(this);
        mTvSelectDateBtn.setOnClickListener(this);
        mTvSelectCertificateBtn.setOnClickListener(this);
        selectCountryInfo.setOnClickListener(this);
        selectPhoneAreaCode.setOnClickListener(this);
        tvMaintenanceRequestBtn.setOnClickListener(this);

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setEnableSubmitBtn();
            }
        });
        etPostalCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setEnableSubmitBtn();
            }
        });
        etAddrDetail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setEnableSubmitBtn();
            }
        });
        etCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setEnableSubmitBtn();
            }
        });
        etProvince.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setEnableSubmitBtn();
            }
        });
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setEnableSubmitBtn();
            }
        });
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setEnableSubmitBtn();
            }
        });
        etProblemDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTvWordsNum.setText(String.valueOf(s.length()) + getString(R.string.words_num_limit_200));
                setEnableSubmitBtn();
            }
        });
        etProductEquipmentNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setEnableSubmitBtn();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_after_sales_service_process_guide_hint:
                goToNewActivity(MaintAfterSaleProcessActivity.class);
                break;
            case R.id.tv_select_country_btn:
                curSelectCountryType = SELECT_COUNTRY_TYPE_YOUR_COUNTRY;
                selectCountry();
                break;
            case R.id.tv_select_date_btn:
                selectDate();
                break;
            case R.id.tv_select_certificate_btn:
                selectCertificate();
                break;
            case R.id.selectCountryInfo:
                curSelectCountryType = SELECT_COUNTRY_TYPE_LOGISTICS_COUNTRY;
                selectCountryInfo();
                break;
            case R.id.selectPhoneAreaCode:
                curSelectCountryType = SELECT_COUNTRY_TYPE_PHONE_COUNTRY;
                selectPhoneAreaCode();
                break;
            case R.id.tv_maintenance_request_btn:
                submitData();
                break;
        }
    }

    private void selectPhoneAreaCode() {
        selectCountry();
    }

    private void selectCountryInfo() {
        selectCountry();
    }

    private void selectDate(){
        if (null == mCalendarDialog){
            mCalendarDialog = new CalendarDialog(this);
            mCalendarDialog.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                    String result = "";
                    if (year == 0 && month == 0 && dayOfMonth == 0){
                        result = DateUtil.getDateToString(view.getDate(),DateUtil.DATE_TIME_PATTERN_2);
                    }else {
                        month +=1;
                        result = year + "-" + (month < 10 ? "0" + month : month) + "-" + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth);
                    }
                    selectDateStr = result;
                    mTvSelectDateBtn.setText(result);
                    setEnableSubmitBtn();
                }
            });
        }
        mCalendarDialog.show();
    }

    private void setEnableSubmitBtn(){
        tvMaintenanceRequestBtn.setEnabled(checkEnableSubmitBtn());
    }

    private boolean checkEnableSubmitBtn(){
        if (null == mSelectLogisticsCountry){
            return false;
        }

        if (null == mYourCountry){
            return false;
        }

        if (null == mSelectPhoneCountry){
            return false;
        }

        if (TextUtils.isEmpty(selectDateStr)){
            return false;
        }

        if (TextUtils.isEmpty(etName.getTextStr())){
            return false;
        }
        if (TextUtils.isEmpty(etPostalCode.getTextStr())){
            return false;
        }
        if (TextUtils.isEmpty(etAddrDetail.getTextStr())){
            return false;
        }
        if (TextUtils.isEmpty(etCity.getTextStr())){
            return false;
        }
        if (TextUtils.isEmpty(etProvince.getTextStr())){
            return false;
        }
        if (TextUtils.isEmpty(etPhone.getTextStr())){
            return false;
        }
        if (TextUtils.isEmpty(etEmail.getTextStr())){
            return false;
        }
        if (TextUtils.isEmpty(etProductEquipmentNumber.getTextStr())){
            return false;
        }
        if (TextUtils.isEmpty(etProblemDescription.getText().toString().trim())){
            return false;
        }
        if (!isExistsCertificateFile()){
            return false;
        }
        return true;
    }

    private void selectCountry(){
        Intent intent = new Intent(MaintenanceRequestActivity.this, CountryCodeActivity.class);
        startActivityForResult(intent,SELECT_COUNTRY_INTENT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK != resultCode){
            return;
        }
        if (SELECT_COUNTRY_INTENT_CODE == requestCode){
            Country country = data.getParcelableExtra(CountryCodeActivity.SELECT_COUNTRY);
            // 所在国家
            if (SELECT_COUNTRY_TYPE_YOUR_COUNTRY == curSelectCountryType){
                mYourCountry = country;
                if (null == mSelectLogisticsCountry){
                    mSelectLogisticsCountry = country;
                }
                if (null == mSelectPhoneCountry){
                    mSelectPhoneCountry = country;
                }
            }
            // 物流信息国家
            else if (SELECT_COUNTRY_TYPE_LOGISTICS_COUNTRY == curSelectCountryType){

                mSelectLogisticsCountry = country;
                if (null == mSelectPhoneCountry){
                    mSelectPhoneCountry = country;
                }
            }
            // 手机区号
            else if (SELECT_COUNTRY_TYPE_PHONE_COUNTRY == curSelectCountryType){
                mSelectPhoneCountry = country;
            }

            if (null != mYourCountry){
                mTvSelectCountryBtn.setText(mYourCountry.getName());
            }
            if (null != mSelectLogisticsCountry){
                selectCountryInfo.setText(mSelectLogisticsCountry.getName());
            }
            if (null != mSelectPhoneCountry){
                selectPhoneAreaCode.setText(mSelectPhoneCountry.getPhoneCode());
            }
            setEnableSubmitBtn();
        } else if (REQUEST_CODE_CARMERA == requestCode) {
            try {
                selectCertificateFile = true;
                setSelectCertificateBtnStatus();
                setEnableSubmitBtn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (REQUEST_CODE_PICK == requestCode) {
            try {
                // 该 uri 是上一个 Activity 返回的
                mUri = data.getData();
                if (mUri != null) {
                    mPath = FileUtil.getRealFilePath(this, mUri);
                }
                selectCertificateFile = true;
                setSelectCertificateBtnStatus();
                setEnableSubmitBtn();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setSelectCertificateBtnStatus(){
        if (!isExistsCertificateFile()){
            mTvSelectCertificateBtn.setText(getString(R.string.select_buy_certificate));
        }else {
            mTvSelectCertificateBtn.setText(getString(R.string.select_buy_certificate) + getString(R.string.select_buy_certificate_tag));
        }
    }

    // 认证文件是否已经选择
    private boolean isExistsCertificateFile(){

       return selectCertificateFile && !TextUtils.isEmpty(mPath) && new File(mPath).exists();
    }

    private PermissionListener mPermissionListener;
    private Uri mUri;
    private String mPath;
    private AlertDialog mDialogChoosePhoto;
    private boolean selectCertificateFile;

    private void submitData() {
        OkHttpClient client = new OkHttpClient();

        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        File tempFile = new File(mPath);
        if (tempFile != null) {
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), tempFile);
            String fileName = tempFile.getName();

            // 用户id
            requestBody.addFormDataPart("userId", PeachPreference.readUserId());
            // 购买凭证电子图片
            requestBody.addFormDataPart("purchasedTicketImg", fileName, body);
            //  问题描述
            requestBody.addFormDataPart("description", etProblemDescription.getText().toString());
            // 型号
            requestBody.addFormDataPart("modelNum", etProductEquipmentNumber.getTextStr());
            // 购买日期
            requestBody.addFormDataPart("purchasedDate", selectDateStr);
            // 国家二字码
            requestBody.addFormDataPart("countryCode", null == mSelectPhoneCountry ? "" : "+" + mSelectPhoneCountry.getPhoneCode());
            // 姓名
            requestBody.addFormDataPart("name", etName.getTextStr());
            // 邮箱地址
            requestBody.addFormDataPart("emailAddress", etEmail.getTextStr());
            // 电话
            requestBody.addFormDataPart("telephone", etPhone.getTextStr());
            // 省份
            requestBody.addFormDataPart("province", etProvince.getTextStr());
            // 城市
            requestBody.addFormDataPart("city", etCity.getTextStr());
            // 街道门牌号
            requestBody.addFormDataPart("streetAddress", etAddrDetail.getTextStr());
            // 邮编
            requestBody.addFormDataPart("postcode", etPostalCode.getTextStr());
            // lockId 可选	Integer 锁id

            Request request = new Request.Builder()
                    .url(Urls.BASE_URL + Urls.REPAIR_APPLY_SUBMIT)
                    .post(requestBody.build())
                    .tag(this)
                    .build();


            PeachLoader.showLoading(this, LoaderStyle.BallSpinFadeLoaderIndicator);

            client.newBuilder()
                    .readTimeout(5000, TimeUnit.MILLISECONDS)
                    .build()
                    .newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            PeachLoader.stopLoading();
                            PeachLogger.d("Http", e.getMessage());
                            toast(R.string.submit_fail);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            PeachLoader.stopLoading();
                            String str = response.body().string();
                            PeachLogger.d("Http", str);
                            try {
                                JSONObject jsonObject = new JSONObject(str);
                                if (jsonObject.getBoolean("success")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 提交申请成功
                                            showSubmitSuccessLayout();
                                        }
                                    });

                                }else {
                                    toast(R.string.submit_fail);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                toast(R.string.submit_fail);
                            }

                        }
                    });
        }
    }

    /**
     * 运行时权限处理
     */
    public void requestRuntimePermissions(String[] permissions, PermissionListener permissionListener) {
        Activity topActivity = ActivityCollector.getTopActivity();
        if (null == topActivity) {
            return;
        }
        mPermissionListener = permissionListener;
        List<String> permissionList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(topActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionList.add(permission);
                }
                if (!permissionList.isEmpty()) {
                    ActivityCompat.requestPermissions(topActivity, permissionList.toArray(new String[permissionList.size()]), REQUEST_CODE_PERMISSION);
                } else {
                    mPermissionListener.onGranted();
                }
            }
        } else {
            mPermissionListener.onGranted();
        }
    }

    /**
     * 用户对运行时权限授权/拒绝后，进行后续的回调操作
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0) {
                List<String> deniedPermissions = new ArrayList<>();
                for (int i = 0; i < grantResults.length; i++) {
                    int grantResult = grantResults[i];
                    String permission = permissions[i];
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        deniedPermissions.add(permission);
                    }
                }
                if (deniedPermissions.isEmpty()) {
                    mPermissionListener.onGranted();
                } else {
                    mPermissionListener.onDenied(deniedPermissions);
                }
            }
        }
    }

    private void selectCertificate() {
        mDialogChoosePhoto = new AlertDialog.Builder(this).create();
        mDialogChoosePhoto.show();
        final Window window = mDialogChoosePhoto.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_choose_photo);
            window.setGravity(Gravity.BOTTOM);
            window.setWindowAnimations(R.style.anim_panel_up_from_bottom);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.getDecorView().setPadding(0, 0, 0, 0);
            //设置属性
            final WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            params.dimAmount = 0.5f;
            window.setAttributes(params);

            TextView title = window.findViewById(R.id.tv_title);
            title.setText(R.string.select_file);

            TextView takePhotoBtn = window.findViewById(R.id.btn_dialog_send_ekey_permanent);
            takePhotoBtn.setText(R.string.take_a_photo);
            takePhotoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mDialogChoosePhoto){
                        mDialogChoosePhoto.dismiss();
                    }
                    requestRuntimePermissions(new String[]{Manifest.permission.CAMERA}, new PermissionListener() {
                        @Override
                        public void onGranted() {
                            // 调用拍照
                            mPath = Environment.getExternalStorageDirectory() + File.separator + System.currentTimeMillis() + "photo.jpeg";
                            mUri = FileProvider.getUriForFile(MaintenanceRequestActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File(mPath));
                            Utils.takePhoto(MaintenanceRequestActivity.this, mPath, REQUEST_CODE_CARMERA, mUri);
                        }

                        @Override
                        public void onDenied(List<String> deniedPermissions) {
                            toast(R.string.start_camera_fail);
                        }
                    });

                }
            });
            TextView choosePhotoBtn = window.findViewById(R.id.btn_dialog_send_ekey_one_time);
            choosePhotoBtn.setText(R.string.choose_from_album);
            choosePhotoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mDialogChoosePhoto){
                        mDialogChoosePhoto.dismiss();
                    }
                    requestRuntimePermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, new PermissionListener() {
                        @Override
                        public void onGranted() {
                            // 调用相册
                            Utils.choosePhoto(MaintenanceRequestActivity.this, REQUEST_CODE_PICK);
                        }

                        @Override
                        public void onDenied(List<String> deniedPermissions) {
                            toast(R.string.note_permission_avatar);
                        }
                    });

                }
            });
            window.findViewById(R.id.btn_dialog_send_ekey_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mDialogChoosePhoto){
                        mDialogChoosePhoto.dismiss();
                    }
                }
            });
        }
    }
}
