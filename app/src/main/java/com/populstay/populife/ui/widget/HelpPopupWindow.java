package com.populstay.populife.ui.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.populstay.populife.R;

public class HelpPopupWindow {

    private PopupWindow mPopupWindow;
    private Context mContext;


    public HelpPopupWindow(Context context) {
        this.mContext = context;
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.help_popup_window_layout, null);
        mPopupWindow = new PopupWindow();
        mPopupWindow.setWidth((int) mContext.getResources().getDimension(R.dimen.help_win_width));
        mPopupWindow.setHeight((int) mContext.getResources().getDimension(R.dimen.help_win_height));
        mPopupWindow.setContentView(contentView);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
    }

    public void show(View anchor){
        if (null == mPopupWindow){
            return;
        }
        if (mPopupWindow.isShowing()){
            mPopupWindow.dismiss();
        }else {
            mPopupWindow.showAsDropDown(anchor);
        }
    }

    public void dismiss(){
        if (null == mPopupWindow){
            return;
        }
        mPopupWindow.dismiss();
    }


}
