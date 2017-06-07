package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Gin on 2017/2/23.
 */

public class ChangeAuthorityEntity implements Serializable {
    private String name;
    private String number;
    private String userId;



    private boolean isApp;
    private boolean isSelect;

    public ChangeAuthorityEntity() {
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isApp() {
        return isApp;
    }

    public void setApp(boolean app) {
        isApp = app;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
