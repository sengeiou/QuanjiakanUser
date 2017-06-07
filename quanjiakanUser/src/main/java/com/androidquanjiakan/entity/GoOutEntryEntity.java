package com.androidquanjiakan.entity;

/**
 * Created by Administrator on 2016/9/2 0002.
 */
public class GoOutEntryEntity {
    private String patient_type;
    private String patient_money;
    private String discount_money;
    private String total_price;
    private String real_price;
    private String platform_price;
    private String describe;

    public String getPatient_type() {
        return patient_type;
    }

    public void setPatient_type(String patient_type) {
        this.patient_type = patient_type;
    }

    public String getPatient_money() {
        return patient_money;
    }

    public void setPatient_money(String patient_money) {
        this.patient_money = patient_money;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getDiscount_money() {
        return discount_money;
    }

    public void setDiscount_money(String discount_money) {
        this.discount_money = discount_money;
    }

    public String getTotal_price() {
        return total_price;
    }

    public void setTotal_price(String total_price) {
        this.total_price = total_price;
    }

    public String getReal_price() {
        return real_price;
    }

    public void setReal_price(String real_price) {
        this.real_price = real_price;
    }

    public String getPlatform_price() {
        return platform_price;
    }

    public void setPlatform_price(String platform_price) {
        this.platform_price = platform_price;
    }
}
