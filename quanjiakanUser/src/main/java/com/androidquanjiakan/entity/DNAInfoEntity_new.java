package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class DNAInfoEntity_new implements Serializable{

    /**
     * IMEI : 240207489215702897
     * createtime : 1.492758623424E12
     * id : 4
     * title : 恩
     * url : http://dna.quanjiakan.com:6080/quanjiakan/resources/dna/20170421151012_ii3f6o.doc
     * userid : 2
     */

    private String IMEI;
    private double createtime;
    private int id;
    private String title;
    private String url;
    private int userid;

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public double getCreatetime() {
        return createtime;
    }

    public void setCreatetime(double createtime) {
        this.createtime = createtime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
