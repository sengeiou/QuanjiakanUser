package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Gin on 2016/11/23.
 */

public class VolunteerMesEntity implements Serializable {

    private String name;
    private String picture;
    private String targetId;

    public String getTargetAppKey() {
        return targetAppKey;
    }

    public void setTargetAppKey(String targetAppKey) {
        this.targetAppKey = targetAppKey;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    private String targetAppKey;

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    private boolean isRead;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    private String createtime;


}
