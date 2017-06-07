package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * 作者：Administrator on 2017/4/6 15:17
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class CareRemindEntity implements Serializable{

    private String title;
    private String week;
    private String time;
    private String button;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }
}
