package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/15 0015.
 */
public class DeliverAddressEntity implements Serializable {
    public static final String DEFAULT_ADDR = "2";
    public static final String NOT_DEFAULT_ADDR = "1";
    /**
     * {
     * "id": "26",
     * "user_id": "10084",
     * "name": "张工",
     * "address": "荔湾区周门北路28号B座5楼",
     * "mobile": "13312345678",
     * "status": "1"
     * }
     */
    private String id;
    private String user_id;
    private String name;
    private String address;
    private String mobile;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * 1---非默认地址
     * 2---默认地址
     * @return
     */
    public String getStatus() {
        return status;
    }

    public int getStatusIntValue(){
        return Integer.parseInt(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDefault(){
        return DEFAULT_ADDR.equals(status);
    }

}
