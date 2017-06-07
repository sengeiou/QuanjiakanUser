package com.androidquanjiakan.entity;

/**
 * Created by Administrator on 2016/11/22.
 */

public class LeavingMessageInfo {

    private String address;
    private String content;
    private long createTime;
    private long lat;
    private long lng;
    private long messageUserId;
    private long missingUserId;
    private long msgId;
    private String picture;
    private int total;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getVolName() {
        return volName;
    }

    public void setVolName(String volName) {
        this.volName = volName;
    }

    private String volName;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLat() {
        return lat;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public long getLng() {
        return lng;
    }

    public void setLng(long lng) {
        this.lng = lng;
    }

    public long getMessageUserId() {
        return messageUserId;
    }

    public void setMessageUserId(long messageUserId) {
        this.messageUserId = messageUserId;
    }

    public long getMissingUserId() {
        return missingUserId;
    }

    public void setMissingUserId(long missingUserId) {
        this.missingUserId = missingUserId;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public String toString() {
        return "LeavingMessageInfo{" +
                "address='" + address + '\'' +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", lat=" + lat +
                ", lng=" + lng +
                ", messageUserId=" + messageUserId +
                ", missingUserId=" + missingUserId +
                ", msgId=" + msgId +
                ", picture='" + picture + '\'' +
                '}';
    }
}
