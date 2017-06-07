package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/5 0005.
 */
public class InquiryListEntity implements Serializable {
    private String id;
    private String name;
    //性别   1、男   2、女
    private String sex;
    private String age;
    private String phone;
    private String address;
    //病情类型    1. 缓      2. 急
    private String patient_type;
    private String describe;
    private String longitude;
    private String latitude;
    private String orderid;
    private String user_id;
    private String doctor_id;
    //订单状态: 分为  0.生成   1.发布  2.已抢单, 3.取消订单, 4.重新发布, 5.完成
    private String status;
    private String payment;
    private String total_fee;
    private String publishtime;
    private String grabtime;
    private String canceltime;
    private String finishtime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPatient_type() {
        return patient_type;
    }

    public void setPatient_type(String patient_type) {
        this.patient_type = patient_type;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public String getPublishtime() {
        return publishtime;
    }

    public void setPublishtime(String publishtime) {
        this.publishtime = publishtime;
    }

    public String getGrabtime() {
        return grabtime;
    }

    public void setGrabtime(String grabtime) {
        this.grabtime = grabtime;
    }

    public String getCanceltime() {
        return canceltime;
    }

    public void setCanceltime(String canceltime) {
        this.canceltime = canceltime;
    }

    public String getFinishtime() {
        return finishtime;
    }

    public void setFinishtime(String finishtime) {
        this.finishtime = finishtime;
    }
}
