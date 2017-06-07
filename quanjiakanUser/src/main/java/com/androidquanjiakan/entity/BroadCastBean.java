package com.androidquanjiakan.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 保存广播数据
 */
@Entity
public class BroadCastBean {
    @Id(autoincrement = true)
    private Long id;
    private String json;
    private String fromId;
    private String time;
    private String imei;
    @Generated(hash = 1228889965)
    public BroadCastBean(Long id, String json, String fromId, String time,
            String imei) {
        this.id = id;
        this.json = json;
        this.fromId = fromId;
        this.time = time;
        this.imei = imei;
    }
    @Generated(hash = 346428806)
    public BroadCastBean() {
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
    public String getFromId() {
        return this.fromId;
    }
    public void setFromId(String fromId) {
        this.fromId = fromId;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getImei() {
        return this.imei;
    }
    public void setImei(String imei) {
        this.imei = imei;
    }


}
