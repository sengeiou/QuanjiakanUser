package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/13 0013.
 */
public class GroupInfoEntity implements Serializable{
    private String desc;
    private String name;
    private String mtime;
    private String appkey;
    private long gid;
    private String ctime;
    private int max_member_count;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMtime() {
        return mtime;
    }

    public void setMtime(String mtime) {
        this.mtime = mtime;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public long getGid() {
        return gid;
    }

    public void setGid(long gid) {
        this.gid = gid;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public int getMax_member_count() {
        return max_member_count;
    }

    public void setMax_member_count(int max_member_count) {
        this.max_member_count = max_member_count;
    }
}
