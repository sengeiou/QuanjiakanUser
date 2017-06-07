package com.androidquanjiakan.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 作者：Administrator on 2017/4/12 14:12
 * <p>
 * 邮箱：liuzj@hi-board.com
 */

@Entity
public class WatchCardBean {

    @Id(autoincrement = true)
    private Long id;
    private String json;
    private String imei;
    @Generated(hash = 1340860213)
    public WatchCardBean(Long id, String json, String imei) {
        this.id = id;
        this.json = json;
        this.imei = imei;
    }
    @Generated(hash = 119750283)
    public WatchCardBean() {
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
