package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/29 0029.
 */
public class GroupChatListEntity implements Serializable{
    /**
     * 排序规则
     */
    private long gid;//gid
    private String mtime;
    private String ctime;
    private String desc;
    private int max_member_count;
    private int number;
    private String name;
    private String did;//did

    public void setDid(String did){
        this.did=did;
    }

    public String getDid(){
        return did;
    }

    public void setNumber(int number){
        this.number=number;
    }

    public int getNumber(){
        return number;
    }

    public long getGid() {
        return gid;
    }

    public void setGid(long gid) {
        this.gid = gid;
    }

    public String getMtime() {
        return mtime;
    }

    public void setMtime(String mtime) {
        this.mtime = mtime;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getMax_member_count() {
        return max_member_count;
    }

    public void setMax_member_count(int max_member_count) {
        this.max_member_count = max_member_count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
