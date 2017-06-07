package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/2 0002.
 */

public class VideoLiveListEntity implements Serializable {
    /**
     "flvUrl": "http://live.quanjiakan.com/quanjiakan/11780.flv",
     "followNum": 0,
     "groupId": "15763713",
     "hlsUrl": "http://live.quanjiakan.com/quanjiakan/11780.m3u8",
     "icon": "http://picture.quanjiakan.com:9080/quanjiakan/resources/housekeeper/20161201103922_qpqjao.png",
     "id": 181,
     "imageUrl": "http://picture.quanjiakan.com:9080/quanjiakan/resources/video/livevideo/20161226154512_5kqxigjucvxyj3bfyabo.png",
     "isFollow": 0,
     "likeNum": 3,
     "lookNum": 776,
     "name": "俊杰",
     "page": 1,
     "rows": 20,
     "rtmpUrl": "rtmp://live.quanjiakan.com/quanjiakan/11780",
     "securityUrl": "rtmp://video-center.alivecdn.com/quanjiakan/11780?vhost=live.quanjiakan.com",
     "startTime": 1482200676,
     "state": 1,
     "title": "回家",
     "type": 1,
     "userId": 11780,
     "usrargs": "auth_key=1482738317-0-0-35f5755e4ee7b8f2c3847b69fa3eaed4"
     */

    private long followNum;
    private long isFollow;
    private String flvUrl;
    private String groupId;
    private String hlsUrl;
    private String icon;
    private long id;

    private String imageUrl;
    private long likeNum;
    private long lookNum;
    private String name;
    private long page;

    private long rows;
    private String rtmpUrl;
    private String securityUrl;
    private long startTime;
    private String state;

    private String title;
    private String type;
    private String userId;
    private String usrargs;

    public long getFollowNum() {
        return followNum;
    }

    public void setFollowNum(long followNum) {
        this.followNum = followNum;
    }

    public long getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(long isFollow) {
        this.isFollow = isFollow;
    }

    public String getFlvUrl() {
        return flvUrl;
    }

    public void setFlvUrl(String flvUrl) {
        this.flvUrl = flvUrl;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getHlsUrl() {
        return hlsUrl;
    }

    public void setHlsUrl(String hlsUrl) {
        this.hlsUrl = hlsUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(long likeNum) {
        this.likeNum = likeNum;
    }

    public long getLookNum() {
        return lookNum;
    }

    public void setLookNum(long lookNum) {
        this.lookNum = lookNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getRtmpUrl() {
        return rtmpUrl;
    }

    public void setRtmpUrl(String rtmpUrl) {
        this.rtmpUrl = rtmpUrl;
    }

    public String getSecurityUrl() {
        return securityUrl;
    }

    public void setSecurityUrl(String securityUrl) {
        this.securityUrl = securityUrl;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsrargs() {
        return usrargs;
    }

    public void setUsrargs(String usrargs) {
        this.usrargs = usrargs;
    }
}
