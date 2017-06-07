package com.wxapi.entity;

/**
 * Created by Administrator on 2016/7/25 0025.
 */
public class HouseKeeperParamEntity {
    private String type;
    private String orderid;
    private double total_fee;
    /*
    1、通过
    2、未通过
     */
    private int flag;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public double getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(double total_fee) {
        this.total_fee = total_fee;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
