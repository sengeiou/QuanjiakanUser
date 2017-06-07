package com.androidquanjiakan.entity;

/**
 * Created by Administrator on 2016/11/8.
 */

public class VolunteerMessageInfo {
    private String name;//义工名字
    private boolean isRead;//义工消息是否已读

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}
