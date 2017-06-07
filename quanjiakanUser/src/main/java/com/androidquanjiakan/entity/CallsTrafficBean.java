package com.androidquanjiakan.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Gin on 2017/3/14.
 */
@Entity
public class CallsTrafficBean {
    @Id(autoincrement = true)
    private Long id;

    private String json;

    @Generated(hash = 1597432213)
    public CallsTrafficBean(Long id, String json) {
        this.id = id;
        this.json = json;
    }

    @Generated(hash = 1286995305)
    public CallsTrafficBean() {
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
}
