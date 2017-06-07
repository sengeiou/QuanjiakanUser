package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/26 0026.
 */

public class ConcernEntity implements Serializable{
    /**
     "anchorId": 11780,
     "doctorName": "俊杰",
     "followNum": 4,
     "icon": "http://picture.quanjiakan.com:9080/quanjiakan/resources/housekeeper/20161201103922_qpqjao.png",
     "id": 156,
     "lookNum": 793,
     "page": 1,
     "rows": 20,
     "state": 0,
     "type": 0,
     "userId": 11303
     */

    private long anchorId;
    private String doctorName;
    private long followNum;
    private String icon;
    private long id;
    private long lookNum;
    private long page;
    private long rows;
    private long state;
    private long type;
    private long userId;


    public long getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(long anchorId) {
        this.anchorId = anchorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public long getFollowNum() {
        return followNum;
    }

    public void setFollowNum(long followNum) {
        this.followNum = followNum;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLookNum() {
        return lookNum;
    }

    public void setLookNum(long lookNum) {
        this.lookNum = lookNum;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getRows() {
        return rows;
    }

    public void setRows(long rows) {
        this.rows = rows;
    }

    public long getState() {
        return state;
    }

    public void setState(long state) {
        this.state = state;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
