package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Gin on 2017/2/23.
 */

public class QueryCallsEntity implements Serializable {
    private String name;
    private String time;
    private String message;
    private int type;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
