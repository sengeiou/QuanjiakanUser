package com.androidquanjiakan.entity;

/**
 * Created by Administrator on 2016/6/15 0015.
 */
public class FamilyPhoneNumberEntity {

    private String phoneNumber;
    private String name;
    private boolean isAdd;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    @Override
    public String toString() {
        return "FamilyPhoneNumberEntity{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", name='" + name + '\'' +
                ", isAdd=" + isAdd +
                '}';
    }
}
