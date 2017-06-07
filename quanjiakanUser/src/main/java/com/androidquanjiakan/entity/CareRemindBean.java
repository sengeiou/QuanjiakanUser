package com.androidquanjiakan.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.io.Serializable;

/**
 * 作者：Administrator on 2017/4/6 16:00
 * <p>
 * 邮箱：liuzj@hi-board.com
 */

@Entity
public class CareRemindBean {
    @Id(autoincrement = true)
    private Long id;
    private String title;
    private String week;
    private String time;
    private String button;
    @Generated(hash = 1729674027)
    public CareRemindBean(Long id, String title, String week, String time,
            String button) {
        this.id = id;
        this.title = title;
        this.week = week;
        this.time = time;
        this.button = button;
    }
    @Generated(hash = 407519405)
    public CareRemindBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getWeek() {
        return this.week;
    }
    public void setWeek(String week) {
        this.week = week;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getButton() {
        return this.button;
    }
    public void setButton(String button) {
        this.button = button;
    }
}
