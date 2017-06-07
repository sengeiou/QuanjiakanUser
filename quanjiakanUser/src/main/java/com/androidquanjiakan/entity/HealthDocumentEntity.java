package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/11/26 0026.
 */

public class HealthDocumentEntity implements Serializable{
    /**
     *
     "createtime": "2016-11-26 16:26:02.0",
     "deviceid": "0",
     "id": 30,
     "medical_name": "皇家宝贝哈",
     "medical_record": "http://picture.quanjiakan.com:9080/quanjiakan/resources/useractivate/20161126162558_zj35gu.png",
     "user_id": 11108

     "createtime": "2016-12-08 11:31:36.0",
     "deviceid": "0",
     "id": 46,
     "medical_name": "风景",
     "medical_record": "http://picture.quanjiakan.com:9080/quanjiakan/resources/useractivate/20161208113128_ti147x.png",
     "page": 1,
     "rows": 20,
     "user_id": 11303
     */
    private String createtime;
    private String deviceid;
    private long id;
    private String medical_name;
    private String medical_record;
    private long user_id;

    private int rows;

    public String getCreatetime() {
        return createtime;
    }

    public String getCreatetimeFormat() {
        if(createtime!=null && createtime.length()>19){
            return createtime.substring(0,19);
        }
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }
}
