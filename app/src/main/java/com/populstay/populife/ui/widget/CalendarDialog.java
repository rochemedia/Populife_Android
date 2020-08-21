package com.populstay.populife.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import com.populstay.populife.R;
import com.populstay.populife.util.date.DateUtil;

import java.util.Date;

public class CalendarDialog extends Dialog {

    private CalendarView mCalendarView;
    private TextView tvCancelBtn, tvOKBtn;
    private CalendarView.OnDateChangeListener mOnDateChangeListener;
    private int mYear,  mMonth,  mDayOfMonth;

    public CalendarDialog(@NonNull Context context) {
        super(context);
    }

    public CalendarDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CalendarDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_dalog_layout);
        init();
        setListener();
    }


    private void init() {
        mCalendarView = findViewById(R.id.calendarView);
        tvCancelBtn = findViewById(R.id.tvCancelBtn);
        tvOKBtn = findViewById(R.id.tvOKBtn);
    }

    private void setListener() {
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                mYear = year;
                mMonth = month;
                mDayOfMonth = dayOfMonth;
            }
        });
        tvCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnDateChangeListener){
                    mOnDateChangeListener.onSelectedDayChange(mCalendarView, mYear, mMonth, mDayOfMonth);
                }
                dismiss();
            }
        });
    }

    public void setOnDateChangeListener(CalendarView.OnDateChangeListener mOnDateChangeListener) {
        this.mOnDateChangeListener = mOnDateChangeListener;
    }
}
