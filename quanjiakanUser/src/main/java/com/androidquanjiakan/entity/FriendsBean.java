package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Gin on 2016/8/17.
 */
public class FriendsBean implements Serializable {

    public String friendName;
    public String zhicheng;
    public String icon;
    public String id;//对方id
    public String mobile;
    public String clinic;
    public String hospital_name;
    public String title;
    public String selfId;//自己id

    public String getSelfId() {
        return selfId;
    }

    public void setSelfId(String selfId) {
        this.selfId = selfId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getClinic() {
        return clinic;
    }

    public void setClinic(String clinic) {
        this.clinic = clinic;
    }

    public String getHospital_name() {
        return hospital_name;
    }

    public void setHospital_name(String hospital_name) {
        this.hospital_name = hospital_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getZhicheng() {
        return zhicheng;
    }

    public void setZhicheng(String zhicheng) {
        this.zhicheng = zhicheng;
    }


    @Override
    public String toString() {
        return "FriendsBean{" +
                "icon='" + icon + '\'' +
                ", zhicheng='" + zhicheng + '\'' +
                ", friendName='" + friendName + '\'' +
                ", clinic='" + clinic + '\'' +
                '}';
    }
}
