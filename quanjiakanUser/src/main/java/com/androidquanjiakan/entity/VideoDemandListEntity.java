package com.androidquanjiakan.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/12/2 0002.
 */

public class VideoDemandListEntity implements Serializable {
    /**
     * "content": "俄罗斯美女投奔中国的。",
     * "createtime": "2016-12-02 11:33:32.0",
     * "headIcon": "http://picture.quanjiakan.com:9080/quanjiakan/resources/housekeeper/20161202113212_2h82a1.png",
     * "id": 19,
     * "imageUrl": "http://img1.gtimg.com/news/pics/hv1/217/14/2162/140587837.jpg",
     * "likeCount": 1,
     * "likes": 0,
     * "memberId": 0,
     * "name": "健康小秘书3",
     * "page": 1,
     * "playCount": 9,
     * "rows": 20,
     * "vedioUrl": "http://quanjiakan.oss-cn-hangzhou.aliyuncs.com/flvMultibitrateIn58/Clip_480_5sec_6mbps_h264.mp4"
     */
    private long appraiseCount;
    private String name;
    private String content;
    private long createtime;
    private String headIcon;
    private int id;
    private String imageUrl;
    private int likeCount;
    private int likes;//0 为 未点赞  其他为点赞了
    private int memberId;
    private int page;
    private int playCount;
    private int rows;

    private String vedioUrl;

    public long getAppraiseCount() {
        return appraiseCount;
    }

    public void setAppraiseCount(long appraiseCount) {
        this.appraiseCount = appraiseCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getHeadIcon() {
        return headIcon;
    }

    public void setHeadIcon(String headIcon) {
        this.headIcon = headIcon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getVedioUrl() {
        return vedioUrl;
    }

    public void setVedioUrl(String vedioUrl) {
        this.vedioUrl = vedioUrl;
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd  HH:mm");
    public String getFormatTimeString() {
        return sdf.format(new Date(createtime));
    }
}
