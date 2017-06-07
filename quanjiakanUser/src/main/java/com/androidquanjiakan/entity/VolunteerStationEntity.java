package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/14.
 */

public class VolunteerStationEntity implements Serializable{

    private String address;//地址
    private String city;
    private String contactor;
    private String createtime;
    private String dist;
    private String dscpt;
    private String id;
    private String lat;//纬度
    private String lng;//经度
    private String name;//服务站名
    private String phone;
    private String photo;//图片url
    private String province;
    private String status;
    private String duty_date;
    private String duty_time;
    private String services;
    private String other_services;

    public String getDuty_time() {
        return duty_time;
    }

    public void setDuty_time(String duty_time) {
        this.duty_time = duty_time;
    }

    public String getDuty_date() {
        return duty_date;
    }

    public void setDuty_date(String duty_date) {
        this.duty_date = duty_date;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public String getOther_services() {
        return other_services;
    }

    public void setOther_services(String other_services) {
        this.other_services = other_services;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getContactor() {
        return contactor;
    }

    public void setContactor(String contactor) {
        this.contactor = contactor;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getDist() {
        return dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    public String getDscpt() {
        return dscpt;
    }

    public void setDscpt(String dscpt) {
        this.dscpt = dscpt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "VolunteerStationEntity{" +
                "address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", contactor='" + contactor + '\'' +
                ", createtime='" + createtime + '\'' +
                ", dist='" + dist + '\'' +
                ", dscpt='" + dscpt + '\'' +
                ", id='" + id + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", photo='" + photo + '\'' +
                ", province='" + province + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
