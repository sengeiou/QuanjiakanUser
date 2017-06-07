package com.androidquanjiakan.activity.setting.contact.bean;

/**
 * Created by Gin on 2017/3/14.
 */

public class ClassDisableEvent {
    private long fromId;
    private String json;
    private int status;

    public ClassDisableEvent(String json) {
        this.json = json;
    }

    public ClassDisableEvent(long fromId, String json, int status) {
        this.fromId = fromId;
        this.json = json;
        this.status = status;
    }

    public long getFromId() {
        return fromId;
    }

    public void setFromId(long fromId) {
        this.fromId = fromId;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
