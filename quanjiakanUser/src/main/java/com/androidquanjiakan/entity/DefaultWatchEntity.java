package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Gin on 2017/2/23.
 */

public class DefaultWatchEntity implements Serializable {
    private String icon;
    private String name;
    private String deviceNumber;
    private boolean isSelect;

    public DefaultWatchEntity() {
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceNumber() {
        return deviceNumber;
    }

    public void setDeviceNumber(String deviceNumber) {
        this.deviceNumber = deviceNumber;
    }
}
