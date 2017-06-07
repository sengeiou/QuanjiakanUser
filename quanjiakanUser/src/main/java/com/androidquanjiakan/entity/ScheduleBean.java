package com.androidquanjiakan.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 作者：Administrator on 2017/3/8 15:19
 * <p>
 * 邮箱：liuzj@hi-board.com
 */

@Entity
public class ScheduleBean {

    @Id(autoincrement = true)
    private Long id;
    private String json;//作息计划json
    private String imei;
    @Generated(hash = 1853978510)
    public ScheduleBean(Long id, String json, String imei) {
        this.id = id;
        this.json = json;
        this.imei = imei;
    }
    @Generated(hash = 1005095379)
    public ScheduleBean() {
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
