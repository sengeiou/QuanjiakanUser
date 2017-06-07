package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/20 0020.
 */

public class VideoCommendSubItemEntity implements Serializable {

    /**
     "content": "看看",
     "createtime": 1482215827000,
     "id": 139,
     "ideoDemandId": 26,
     "isFloor": 1,

     "memberId": 11178,
     "name": "冯发强",
     "parentId": 60,
     "rootAppraiseId": 60,
     "toname": "冯发强"
     */

    private String content;
    private long createtime;
    private long id;
    private long ideoDemandId;
    /**
     * 0：楼主    1：不是楼主
     */
    private int isFloor;

    private long memberId;
    private String name;
    private long parentId;
    private long rootAppraiseId;
    private String toname;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdeoDemandId() {
        return ideoDemandId;
    }

    public void setIdeoDemandId(long ideoDemandId) {
        this.ideoDemandId = ideoDemandId;
    }

    public int getIsFloor() {
        return isFloor;
    }

    public void setIsFloor(int isFloor) {
        this.isFloor = isFloor;
    }

    public long getMemberId() {
        return memberId;
    }

    public void setMemberId(long memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public long getRootAppraiseId() {
        return rootAppraiseId;
    }

    public void setRootAppraiseId(long rootAppraiseId) {
        this.rootAppraiseId = rootAppraiseId;
    }

    public String getToname() {
        return toname;
    }

    public void setToname(String toname) {
        this.toname = toname;
    }
}
