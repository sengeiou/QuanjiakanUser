package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/17 0017.
 */
public class LoginInfoEntity implements Serializable{
    private String code;
    private String message;
    private String token;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "LoginInfoEntity{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
