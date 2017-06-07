package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/1/17 0017.
 */

public class EBeanOption implements Serializable {
    /*
      "discount": 1,
      "ebeans": 60,
      "id": 1,
      "identifier": "EdouPriceone",
      "price": 6
     */
    private double discount;
    private long ebeans;
    private int id;
    private String identifier;
    private double price;

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public long getEbeans() {
        return ebeans;
    }

    public void setEbeans(long ebeans) {
        this.ebeans = ebeans;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
