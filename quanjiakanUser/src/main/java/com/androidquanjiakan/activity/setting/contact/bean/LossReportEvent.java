package com.androidquanjiakan.activity.setting.contact.bean;

/**
 * 作者：Administrator on 2017/3/14 11:16
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class LossReportEvent {

    private long fromId;
    private String json;
    private int status;

    public LossReportEvent(String json) {
        this.json = json;
    }

    public LossReportEvent(String json, int status) {
        this.json = json;
        this.status = status;

    }

    public LossReportEvent(long fromId, String json, int status) {
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
