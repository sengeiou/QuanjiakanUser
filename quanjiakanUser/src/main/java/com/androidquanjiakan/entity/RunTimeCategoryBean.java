package com.androidquanjiakan.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Gin on 2017/3/8.
 * 运行时参数
 */
@Entity
public class RunTimeCategoryBean {
    @Id(autoincrement = true)
    private Long id;
    private String json;
    private String imei;
    @Generated(hash = 456268867)
    public RunTimeCategoryBean(Long id, String json, String imei) {
        this.id = id;
        this.json = json;
        this.imei = imei;
    }
    @Generated(hash = 1910520592)
    public RunTimeCategoryBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getJson() {
        return this.json;
    }
    public void setJson(String json) {
        this.json = json;
    }
    public String getImei() {
        return this.imei;
    }
    public void setImei(String imei) {
        this.imei = imei;
    }




}
