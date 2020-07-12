package com.populstay.populife.ui.widget.exedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.populstay.populife.R;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;


public class ExEditText extends FrameLayout implements IExEdit {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_PWD = 1;
    public static final int TYPE_VERIFICTION_CODE = 2;
    public static final int TYPE_ACCOUNT = 3;
    private int inputType = TYPE_NORMAL;

    private View rootView;
    private View editContainer;
    private TextView labelTv;
    private ClearEditText contentEt;
    private ImageView rightIcon;
    private TextView rightTv;
    private View bottomLineView;
    private TextView editStatusHintTv;
    private CountryCodePicker cCPicker;
    private View cCPickerLine;

    private boolean isVisiblePwd = false;

    public ExEditText(Context context) {
        super(context);
        initView(context, null);
    }

    public ExEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public ExEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.common_edit_text_layout, null);
        editContainer = rootView.findViewById(R.id.rl_edit_container);
        labelTv = rootView.findViewById(R.id.label_tv);
        contentEt = rootView.findViewById(R.id.content_et);
        rightIcon = rootView.findViewById(R.id.right_icon);
        rightTv = rootView.findViewById(R.id.right_tv);
        bottomLineView = rootView.findViewById(R.id.bottom_line_view);
        editStatusHintTv = rootView.findViewById(R.id.edit_status_hint_tv);
        cCPicker = rootView.findViewById(R.id.cc_picker);
        cCPickerLine = rootView.findViewById(R.id.cc_picker_line);
        addView(rootView);
        if (null != attrs) {
            initParams(context, attrs);
        }
        contentEt.setOnFocusChangeListenerEx(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus){
                    bottomLineView.setBackgroundColor(getContext().getResources().getColor(R.color.edit_focus_line));
                }else {
                    bottomLineView.setBackgroundColor(getContext().getResources().getColor(R.color.edit_un_focus_line));
                }
            }
        });
    }

    private void initParams(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExEditText);
        if (typedArray != null) {

            setText(typedArray.getString(R.styleable.ExEditText_text));
            setHint(typedArray.getString(R.styleable.ExEditText_hint));
            setLabel(typedArray.getString(R.styleable.ExEditText_label));
            setMaxLength(typedArray.getInteger(R.styleable.ExEditText_maxLength,0));

            isVisiblePwd = typedArray.getBoolean(R.styleable.ExEditText_isVisiblePwd,isVisiblePwd);
            inputType = typedArray.getInteger(R.styleable.ExEditText_inputType, TYPE_NORMAL);
            setType(inputType);
            setPaddingInner((int)typedArray.getDimension(R.styleable.ExEditText_paddingLeftInner,0), 0, (int)typedArray.getDimension(R.styleable.ExEditText_paddingRightInner,0), 0);
            typedArray.recycle();
        }

    }

    private void setPaddingInner(int left, int top, int right, int bottom) {
        if (null != editContainer){
            editContainer.setPadding(left, top, right, bottom);
        }
    }

    public void setType(int type) {
        this.inputType = type;
        switch (inputType){
            case TYPE_NORMAL:
                contentEt.setInputType(InputType.TYPE_CLASS_TEXT);
                showCcPicker(false);
                break;
            case TYPE_ACCOUNT:
                contentEt.setInputType(InputType.TYPE_CLASS_TEXT);
                showCcPicker(true);
                if (null != labelTv){
                    labelTv.setVisibility(GONE);
                }
                break;
            case TYPE_PWD:
                contentEt.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                isShowRightIcon(true);
                isVisiblePwd(isVisiblePwd);
                break;
            case TYPE_VERIFICTION_CODE:
                contentEt.setInputType(InputType.TYPE_CLASS_NUMBER);
                rightTv.setVisibility(VISIBLE);
                break;
        }
    }

    public int getType() {
        return inputType;
    }

    @Override
    public Editable getText() {
        if (null == contentEt) {
            return null;
        }
        return contentEt.getText();
    }

    public String getTextStr() {
        if (null == contentEt) {
            return "";
        }
        return contentEt.getText().toString().trim();
    }

    @Override
    public void setText(String text) {
        if (null != contentEt) {
            contentEt.setText(text);
        }
    }

    @Override
    public void setHint(String text) {
        if (null != contentEt) {
            contentEt.setHint(text);
        }
    }

    @Override
    public void setLabel(String text) {
        if (TextUtils.isEmpty(text)){
            labelTv.setVisibility(GONE);
            return;
        }
        if (null != labelTv) {
            showCcPicker(false);
            labelTv.setVisibility(VISIBLE);
            labelTv.setText(text);
        }
    }

    private void  showCcPicker(boolean isShow){
        cCPicker.setVisibility(isShow ? VISIBLE : GONE);
        cCPickerLine.setVisibility(isShow ? VISIBLE : GONE);
    }

    @Override
    public void isShowRightIcon(boolean isShow) {
        if (null != rightIcon){
            rightIcon.setVisibility(isShow ? VISIBLE : GONE);
            rightIcon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    isVisiblePwd(!isVisiblePwd);
                }
            });
        }
    }

    @Override
    public void isVisiblePwd(boolean isVisiblePwd) {
        this.isVisiblePwd = isVisiblePwd;
        if (null != rightIcon){
            rightIcon.setImageResource(isVisiblePwd ? R.drawable.ic_edit_content_view_icon : R.drawable.ic_edit_content_hide_icon);
        }
        if (null != contentEt) {
            contentEt.setTransformationMethod(isVisiblePwd ? HideReturnsTransformationMethod.getInstance() : PasswordTransformationMethod.getInstance());
            contentEt.setSelection(contentEt.getText().length());
        }
    }

    @Override
    public void setMaxLength(int maxLength) {
        if (null != contentEt) {
            contentEt.setMaxEms(maxLength);
        }
    }

    public TextView getVerifictionCodeView(){
        return rightTv;
    }

    public void addTextChangedListener(TextWatcher textWatcher){
        if (null != contentEt){
            contentEt.addTextChangedListenerEx(textWatcher);
        }
    }

    public void showEditCheckHint(boolean isShow,String hint){
        isShow = isShow && !TextUtils.isEmpty(hint);
        if (null != editStatusHintTv){
            editStatusHintTv.setVisibility(isShow ? VISIBLE : GONE);
            editStatusHintTv.setText(hint);
        }
    }
}
