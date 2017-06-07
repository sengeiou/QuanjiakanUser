package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/14 0014.
 */
public class VersionInfoEntity implements Serializable{
    //{"function":"用户端Android版本","version":"1.0.0.5","url":"http://192.168.0.117:8080/quanjiakan/quanjiakanUser-debug_code5.apk","forced_update":"0","code":"5"}
    private String function;
    private String version;
    private String url;
    private String forced_update;
    private String code;

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getForced_update() {
        return forced_update;
    }

    public void setForced_update(String forced_update) {
        this.forced_update = forced_update;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
