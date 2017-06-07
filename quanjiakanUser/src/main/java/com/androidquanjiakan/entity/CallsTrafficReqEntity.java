package com.androidquanjiakan.entity;

/**
 * Created by Gin on 2017/3/15.
 */

public class CallsTrafficReqEntity {
    private String IMEI;
    private String Action;
    private String Category;

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}
