package com.androidquanjiakan.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 作者：Administrator on 2017/3/8 15:46
 * <p>wifi定位
 * 邮箱：liuzj@hi-board.com
 */

@Entity
public class WIFIlocationBean {

    @Id(autoincrement = true)
    private Long id;

    private String json;

    @Generated(hash = 377554626)
    public WIFIlocationBean(Long id, String json) {
        this.id = id;
        this.json = json;
    }

    @Generated(hash = 2132314851)
    public WIFIlocationBean() {
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
