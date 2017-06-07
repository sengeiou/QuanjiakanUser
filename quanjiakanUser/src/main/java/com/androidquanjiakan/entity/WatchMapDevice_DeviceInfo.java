package com.androidquanjiakan.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2017/3/17 0017.
 */
@Entity
public class WatchMapDevice_DeviceInfo implements Serializable {
    private static final long serialVersionUID = -8213808095500103716L;
    /**
     * IMEI : 240207489224233264
     * Location : 113.2409402,23.1326885
     * Name : 干妈
     * Online : 0
     * Picture :
     * Time : 2017-01-01 00:00:00
     * Type : 0
     */
    @Id(autoincrement = true)
    private Long id;
    private String IMEI;
    private String Location;
    private String Name;
    private String Online;
    private String PhoneNum;
    private String Picture;
    private String Time;
    private String Type;


    @Generated(hash = 1700897258)
    public WatchMapDevice_DeviceInfo(Long id, String IMEI, String Location,
            String Name, String Online, String PhoneNum, String Picture,
            String Time, String Type) {
        this.id = id;
        this.IMEI = IMEI;
        this.Location = Location;
        this.Name = Name;
        this.Online = Online;
        this.PhoneNum = PhoneNum;
        this.Picture = Picture;
        this.Time = Time;
        this.Type = Type;
    }

    @Generated(hash = 194760378)
    public WatchMapDevice_DeviceInfo() {
    }


    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String Location) {
        this.Location = Location;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getOnline() {
        return Online;
    }

    public void setOnline(String Online) {
        this.Online = Online;
    }

    public String getPhoneNum() {
        return PhoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        PhoneNum = phoneNum;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String Picture) {
        this.Picture = Picture;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String Time) {
        this.Time = Time;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
