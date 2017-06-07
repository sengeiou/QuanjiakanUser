package com.androidquanjiakan.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 作者：Administrator on 2017/3/31 09:58
 * <p>
 * 邮箱：liuzj@hi-board.com
 */

@Entity
public class BindingRequestBean {

    @Id(autoincrement = true)
    private Long id;
    private String json;
    private String imei;
    private String fromId;
    @Generated(hash = 1021619127)
    public BindingRequestBean(Long id, String json, String imei, String fromId) {
        this.id = id;
        this.json = json;
        this.imei = imei;
        this.fromId = fromId;
    }
    @Generated(hash = 118237541)
    public BindingRequestBean() {
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
    public String getFromId() {
        return this.fromId;
    }
    public void setFromId(String fromId) {
        this.fromId = fromId;
    }
}
