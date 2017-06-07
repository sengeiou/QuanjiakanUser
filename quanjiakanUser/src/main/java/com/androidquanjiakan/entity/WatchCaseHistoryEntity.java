package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/24 0024.
 */

public class WatchCaseHistoryEntity implements Serializable {

    private static final long serialVersionUID = -4620588944878371850L;
    /**
     * id : 121
     * type : 1
     * deviceid : 239307951255536007
     * medical_name : 葫芦
     * medical_record : http://picture.quanjiakan.com:9080/quanjiakan/resources/medical/20170221171006_4aeqx7.png
     * user_id : 11178
     * createtime : 2017-02-21 17:10:02.0
     */

    private String id;
    private String type;
    private String deviceid;
    private String medical_name;
    private String medical_record;
    private String user_id;
    private String createtime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getMedical_name() {
        return medical_name;
    }

    public void setMedical_name(String medical_name) {
        this.medical_name = medical_name;
    }

    public String getMedical_record() {
        return medical_record;
    }

    public void setMedical_record(String medical_record) {
        this.medical_record = medical_record;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
}
