package com.androidquanjiakan.entity;

/**
 * Created by Administrator on 2016/11/17 0017.
 *
 * "id": "1",
 "name": "保姆",
 "createtime": "1479364796",
 "status": "0"
 */

public class HousekeeperTypeEntity {
    private String id;
    private String name;
    private String createtime;
    private String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
