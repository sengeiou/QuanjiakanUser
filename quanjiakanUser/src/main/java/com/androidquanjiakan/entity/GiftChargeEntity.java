package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/19 0019.
 */

public class GiftChargeEntity implements Serializable {
    /*
      "ebeans": 60,
      "orderid": "QJKRHG20170117020024637306",
      "payment": 0,
      "realPrice": 6,
      "rechargeTime": 1484632824830,
      "status": 0 未成功
      1 成功

            "ebeans": 60,
      "orderid": "QJKRHG20170119091504779162",
      "payment": 0,
      "realPrice": 0.01,
      "rechargeTime": 1484831704197,
      "status": 0

    private final int PAY_TYPE_WALLET = 0;
    private final int PAY_TYPE_ALI = 1;
    private final int PAY_TYPE_WECHAT = 2;
     */
    private long ebeans;
    private String orderid;
    private long payment;
    private double realPrice;
    private long rechargeTime;
    private long status;


    public long getEbeans() {
        return ebeans;
    }

    public void setEbeans(long ebeans) {
        this.ebeans = ebeans;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public long getPayment() {
        return payment;
    }

    public void setPayment(long payment) {
        this.payment = payment;
    }

    public double getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(double realPrice) {
        this.realPrice = realPrice;
    }

    public long getRechargeTime() {
        return rechargeTime;
    }

    public void setRechargeTime(long rechargeTime) {
        this.rechargeTime = rechargeTime;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }
}
