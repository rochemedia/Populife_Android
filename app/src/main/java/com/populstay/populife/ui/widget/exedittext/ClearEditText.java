package com.populstay.populife.ui.widget.exedittext;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.content.Context;
import android.util.AttributeSet;

import com.populstay.populife.R;

@SuppressLint("AppCompatCustomView")
public class ClearEditText extends EditText implements
        View.OnFocusChangeListener, TextWatcher {

    private Drawable mClearDrawable;
    private boolean mHasFocus;
    private OnFocusChangeListener mExOnFocusChangeListener;
    private TextWatcher mExTextWatcher;

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
            mClearDrawable = getResources().getDrawable(R.drawable.ic_edit_clear_btn);
        }

        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
        setClearIconVisible(false);
        setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    protected void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawablePadding(20);
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    public void setOnFocusChangeListenerEx(OnFocusChangeListener exOnFocusChangeListener) {
        mExOnFocusChangeListener = exOnFocusChangeListener;
    }

    public void addTextChangedListenerEx(TextWatcher exTextWatcher) {
        mExTextWatcher = exTextWatcher;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 清除按钮点击区域
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                boolean xTouchable = event.getX() > (getWidth() - getTotalPaddingRight() - 10)
                        && (event.getX() < (getWidth() - getPaddingRight() + 10));

                boolean yTouchable = event.getY() > (getHeight() - mClearDrawable.getIntrinsicHeight()) / 2
                        && event.getY() < (getHeight() + mClearDrawable.getIntrinsicHeight()) / 2;

                if (xTouchable && yTouchable) {
                    setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.mHasFocus = hasFocus;
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
        if (null != mExOnFocusChangeListener) {
            mExOnFocusChangeListener.onFocusChange(v, hasFocus);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int count,
                              int after) {
        if (mHasFocus) {
            setClearIconVisible(s.length() > 0);
        }
        if (null != mExTextWatcher) {
            mExTextWatcher.onTextChanged(s, start, count, after);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

        if (null != mExTextWatcher) {
            mExTextWatcher.beforeTextChanged(s, start, count, after);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (null != mExTextWatcher) {
            mExTextWatcher.afterTextChanged(s);
        }
    }

}