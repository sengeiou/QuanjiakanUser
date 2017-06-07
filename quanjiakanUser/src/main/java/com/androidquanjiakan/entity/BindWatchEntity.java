package com.androidquanjiakan.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Keep;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/9 0009.
 */
@Entity
public class BindWatchEntity implements Serializable{

    private static final long serialVersionUID = 1893700308389407164L;
    @Id(autoincrement = true)
    private Long id;
    /**
     * deviceid : 352315052834187
     * W_TYPE : 1
     * user_id : 11303
     * name : 儿童
     * icon : http://picture.quanjiakan.com:9080/quanjiakan/resources/device/20170307095449_1uje3bx9uns5r8wdit09.png
     * createtime : 2017-03-07 09:54:50.0
     */
    private String deviceid;
    /**
     * 0 老人
     * 1 儿童
     */
    private String W_TYPE;
    private String user_id;
    private String name;
    private String icon;
    private String createtime;

    @Generated(hash = 405553668)
    public BindWatchEntity(Long id, String deviceid, String W_TYPE, String user_id, String name, String icon,
            String createtime) {
        this.id = id;
        this.deviceid = deviceid;
        this.W_TYPE = W_TYPE;
        this.user_id = user_id;
        this.name = name;
        this.icon = icon;
        this.createtime = createtime;
    }

    @Generated(hash = 1870508069)
    public BindWatchEntity() {
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getW_TYPE() {
        return W_TYPE;
    }

    public void setW_TYPE(String W_TYPE) {
        this.W_TYPE = W_TYPE;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
