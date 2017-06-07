package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Gin on 2017/1/12.
 */

public class RecieveGiftEntity_new implements Serializable {

    /*
    "docIcon": "http://picture.quanjiakan.com:9080/quanjiakan/resources/doctor/20170105135004_08l0hkf2xqek5uonorr9.png",
      "docName": "谢文东",
      "giverId": 11303,
      "presentIcon": "http://picture.quanjiakan.com:9080/quanjiakan/resources/activity/20170116144552_diq06d.png",
      "presentName": "轮船",
      "recipientId": 11986,
      "tradeTime": 1484811109
     */
    private String docIcon;
    private String docName;
    private long tradeTime;
    //礼物类型
    private String presentName;
    private String presentIcon;
    private long giverId;

    public String getDocIcon() {
        return docIcon;
    }

    public void setDocIcon(String docIcon) {
        this.docIcon = docIcon;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public long getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(long tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getPresentName() {
        return presentName;
    }

    public void setPresentName(String presentName) {
        this.presentName = presentName;
    }

    public String getPresentIcon() {
        return presentIcon;
    }

    public void setPresentIcon(String presentIcon) {
        this.presentIcon = presentIcon;
    }

    public long getGiverId() {
        return giverId;
    }

    public void setGiverId(long giverId) {
        this.giverId = giverId;
    }
}
