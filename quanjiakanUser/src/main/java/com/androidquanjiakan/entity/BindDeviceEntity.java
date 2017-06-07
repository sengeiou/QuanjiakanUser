package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/16 0016.
 */
public class BindDeviceEntity implements Serializable{
    private String id;
    private String deviceid;
    private String user_id;
    private String createtime;
    private String name;
    private String icon;
    private String scene;
    private String phoneNumber;
    private String location;

    private long alarmTime;

    @Override
    public String toString() {
        return "BindDeviceEntity{" +
                "id='" + id + '\'' +
                ", deviceid='" + deviceid + '\'' +
                ", user_id='" + user_id + '\'' +
                ", createtime='" + createtime + '\'' +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", scene='" + scene + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", location='" + location + '\'' +
                ", W_TYPE='" + W_TYPE + '\'' +
                '}';
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * 0 老人
     * 1 儿童
     * W_TYPE: 0.老人 1. 儿童
     */
    private String W_TYPE;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getW_TYPE() {
        return W_TYPE;
    }

    public void setW_TYPE(String w_TYPE) {
        W_TYPE = w_TYPE;
    }

    public long getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(long alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getTypeString(){
        switch (W_TYPE){
            case "0":
                return "老人";
            case "1":
                return "儿童";
            default:
                return "未知类型";
        }
    }
}
