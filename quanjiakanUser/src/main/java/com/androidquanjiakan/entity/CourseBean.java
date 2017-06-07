package com.androidquanjiakan.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 作者：Administrator on 2017/3/8 15:28
 * <p>
 * 邮箱：liuzj@hi-board.com
 */

@Entity
public class CourseBean {

//    "Daily": "Monday| Tuesday | Wednesday | Thursday | Friday | Saturday| Sunday ",
//            "Morning": {
//        "Status": "On",
//                "StartTime":"08:00:00",
//                "EndTime":"12:00:00"
//    },
//            "Afternoon": {
//        "Status": "On",
//                "StartTime":"13:00:00",
//                "EndTime":"18:00:00"
//    },
    @Id(autoincrement = true)
    private Long id;
    private String imei;

    private String json;//课程表的json

    @Generated(hash = 713591196)
    public CourseBean(Long id, String imei, String json) {
        this.id = id;
        this.imei = imei;
        this.json = json;
    }

    @Generated(hash = 858107730)
    public CourseBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImei() {
        return this.imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getJson() {
        return this.json;
    }

    public void setJson(String json) {
        this.json = json;
    }

   

}
