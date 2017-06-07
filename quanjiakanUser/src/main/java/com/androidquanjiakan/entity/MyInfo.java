package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/25 0025.
 */
public class MyInfo implements Serializable {
    /**
     * {"id":"5","member_id":"10678","nickname":"小小叶","photo":null,"status":"1","createtime":"2016-08-23 10:36:58.0"}
     */
    private String id;
    private String member_id;
    private String nickname;
    private String photo;
    private String status;
    private String createtime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }
}
