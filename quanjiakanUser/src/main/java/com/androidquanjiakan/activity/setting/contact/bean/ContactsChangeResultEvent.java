package com.androidquanjiakan.activity.setting.contact.bean;

/**
 * 作者：Administrator on 2017/3/14 11:02
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class ContactsChangeResultEvent {

    private long fromId;
    private String json;
    private int status;

    public ContactsChangeResultEvent(String json) {
        this.json = json;
    }

    public ContactsChangeResultEvent(String json, int status) {
        this.json = json;
        this.status = status;

    }

    public ContactsChangeResultEvent(long fromId, String json, int status) {
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
