package com.androidquanjiakan.entity;

/**
 * Created by Administrator on 2016/11/17 0017.
 */

public class DNAInfoEntity {
    /**
     "id": "11",
     "title": "123",
     "url": "http://dna.quanjiakan.com:6080/quanjiakan/resources/dna/20161116163527_dujx37.docx",
     "createTime": "1479285231369",
     "IMEI": "240207489164313140"
     */
    private long id;
    private String title;
    private String url;
    private long createTime;
    private long IMEI;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getIMEI() {
        return IMEI;
    }

    public void setIMEI(long IMEI) {
        this.IMEI = IMEI;
    }
}
