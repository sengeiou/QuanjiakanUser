package com.androidquanjiakan.entity;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/17.
 */

public class MissingPersonInfo implements Serializable{



    private String name;
    private String gender;
    private String age;
    private String height;
    private String weight;
    private Long missingTime;
    private Long publishTime;
    private String imag;

    private String contacts;
    private String contactsPhone;
    private String clothes;//描述
    private String missingAddress;
    private int status;

    private String missingUserId;
    private String orderid;
    private String findTime;
    private String findAddress;
    private String findLongitude;
    private String findLatitude;

    public String getFindTime() {
        return findTime;
    }

    public void setFindTime(String findTime) {
        this.findTime = findTime;
    }

    public String getFindAddress() {
        return findAddress;
    }

    public void setFindAddress(String findAddress) {
        this.findAddress = findAddress;
    }

    public String getFindLongitude() {
        return findLongitude;
    }

    public void setFindLongitude(String findLongitude) {
        this.findLongitude = findLongitude;
    }

    public String getFindLatitude() {
        return findLatitude;
    }

    public void setFindLatitude(String findLatitude) {
        this.findLatitude = findLatitude;
    }

    public String getMissingUserId() {
        return missingUserId;
    }

    public void setMissingUserId(String missingUserId) {
        this.missingUserId = missingUserId;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMissingAddress() {
        return missingAddress;
    }

    public void setMissingAddress(String missingAddress) {
        this.missingAddress = missingAddress;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getContactsPhone() {
        return contactsPhone;
    }

    public void setContactsPhone(String contactsPhone) {
        this.contactsPhone = contactsPhone;
    }

    public String getClothes() {
        return clothes;
    }

    public void setClothes(String clothes) {
        this.clothes = clothes;
    }

    public boolean isPub() {
        return isPub;
    }

    public void setPub(boolean pub) {
        isPub = pub;
    }

    private boolean isPub;//是否发布

    public String getImag() {
        return imag;
    }

    public void setImag(String imag) {
        this.imag = imag;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Long getMissingTime() {
        return missingTime;
    }

    public void setMissingTime(Long missingTime) {
        this.missingTime = missingTime;
    }

    public Long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
    }

    @Override
    public String toString() {
        return "MissingPersonInfo{" +
                "name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", age='" + age + '\'' +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", missingTime='" + missingTime + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", isPub=" + isPub +
                ", imag='" + imag + '\'' +
                '}';
    }
}
