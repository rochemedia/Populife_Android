package com.populstay.populife.fragment.entry;

import com.flyco.tablayout.listener.CustomTabEntity;

public class FindTabEntity implements CustomTabEntity {

    private String tabTitle;
    private int tabSelectedIcon;
    private int tabUnselectedIcon;

    public FindTabEntity(String tabTitle, int tabSelectedIcon, int tabUnselectedIcon) {
        this.tabTitle = tabTitle;
        this.tabSelectedIcon = tabSelectedIcon;
        this.tabUnselectedIcon = tabUnselectedIcon;
    }

    public void setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
    }

    public void setTabSelectedIcon(int tabSelectedIcon) {
        this.tabSelectedIcon = tabSelectedIcon;
    }

    public void setTabUnselectedIcon(int tabUnselectedIcon) {
        this.tabUnselectedIcon = tabUnselectedIcon;
    }

    @Override
    public String getTabTitle() {
        return null;
    }

    @Override
    public int getTabSelectedIcon() {
        return 0;
    }

    @Override
    public int getTabUnselectedIcon() {
        return 0;
    }
}
