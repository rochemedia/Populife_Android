package com.populstay.populife.ui.widget.extextview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.populstay.populife.R;

@SuppressLint("AppCompatCustomView")
public class ExTextView extends TextView {

    private boolean isSelected;
    private int defaultDrawablePadding = 4;

    public ExTextView(Context context) {
        super(context);
    }

    public ExTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ExTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setText(String textStr, int startIndex, int endIndex, final OnClickListener clickListener) {

        if (TextUtils.isEmpty(textStr)) {
            return;
        }
        if (startIndex >= textStr.length()) {
            startIndex = 0;
        }
        if (endIndex > textStr.length() || -1 == endIndex) {
            endIndex = textStr.length();
        }

        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(textStr);

        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#000019"));
        //spannableBuilder.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        ClickableSpan clickableSpanTwo = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                if (null != clickListener) {
                    clickListener.onClick(view);
                }
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.parseColor("#ee2737"));


            }
        };
        spannableBuilder.setSpan(clickableSpanTwo, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        setText(spannableBuilder);
        setMovementMethod(LinkMovementMethod.getInstance());
        //setSelected(false);
    }

    public void setIcon(int iconResId) {
        setIcon(iconResId, defaultDrawablePadding);
    }

    public void setIcon(int iconResId, int drawablePadding) {
        Drawable drawable = this.getResources().getDrawable(iconResId);
        drawable.setBounds(0, 0, getLineHeight(), getLineHeight());
        setCompoundDrawablePadding(drawablePadding);
        setCompoundDrawables(drawable, null, null, null);
    }

    public void setRawIcon(int iconResId, int drawablePadding) {
        Drawable drawable = this.getResources().getDrawable(iconResId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        setCompoundDrawablePadding(drawablePadding);
        setCompoundDrawables(drawable, null, null, null);
    }

    public void setSelected(boolean isSelected) {
        setSelected(isSelected,defaultDrawablePadding);
    }

    public void setSelected(boolean isSelected, int drawablePadding) {
        this.isSelected = isSelected;
        setIcon(isSelected ? R.drawable.ic_select : R.drawable.ic_unselect,drawablePadding);
    }

    public boolean isSelected() {
        return isSelected;
    }


}
