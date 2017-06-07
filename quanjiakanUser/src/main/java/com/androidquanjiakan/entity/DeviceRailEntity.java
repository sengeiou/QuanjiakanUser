package com.androidquanjiakan.entity;

/**
 * Created by Administrator on 2016/11/10 0010.
 */

public class DeviceRailEntity {
    private String userid;
    private String deviceid;
    private String number;
    private String path;
    private String time;//用于标示 该条记录在服务器端代表的ID
    private String position;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "DeviceRailEntity{" +
                "userid='" + userid + '\'' +
                ", deviceid='" + deviceid + '\'' +
                ", number='" + number + '\'' +
                ", path='" + path + '\'' +
                ", time='" + time + '\'' +
                ", position='" + position + '\'' +
                '}';
    }
}
