package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/13 0013.
 */
public class GroupMemberEntity implements Serializable{
    private String ctime;
    private String username;
    /**
     * 1、群主
     * 0、成员
     */
    private int flag;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAddFlag(){
        return "ADD".endsWith(username);
    }
}
