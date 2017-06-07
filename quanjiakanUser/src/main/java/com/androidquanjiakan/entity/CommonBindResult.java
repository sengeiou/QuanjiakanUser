package com.androidquanjiakan.entity;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public class CommonBindResult {
    private int type;
    private long fromid;
    private String json;

    public CommonBindResult(int type, long fromid, String json) {
        this.type = type;
        this.fromid = fromid;
        this.json = json;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getFromid() {
        return fromid;
    }

    public void setFromid(long fromid) {
        this.fromid = fromid;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
