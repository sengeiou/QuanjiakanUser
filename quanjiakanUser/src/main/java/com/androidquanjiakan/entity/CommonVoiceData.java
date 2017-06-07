package com.androidquanjiakan.entity;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public class CommonVoiceData {
    private int type;
    private byte[] data;
    private String len;
    private String fromId;

    public CommonVoiceData(int type, byte[] data, String len, String fromId) {
        this.type = type;
        this.data = data;
        this.len = len;
        this.fromId = fromId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getLen() {
        return len;
    }

    public void setLen(String len) {
        this.len = len;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }
}
