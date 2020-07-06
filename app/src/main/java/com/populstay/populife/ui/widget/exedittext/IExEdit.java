package com.populstay.populife.ui.widget.exedittext;

import android.text.Editable;

public interface IExEdit {

    Editable getText();
    void setText(String text);
    void setHint(String text);
    void setLabel(String text);
    void isShowRightIcon(boolean isShow);
    void isVisiblePwd(boolean isVisiblePwd);
    void setMaxLength(int maxLength);
}
