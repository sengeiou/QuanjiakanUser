package com.androidquanjiakan.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/11/26 0026.
 *
 * {
 "code": "200",
 "list": [
 {
 "createtime": "2016-11-26 14:28:25.0",
 "deviceid": "0",
 "id": 18,
 "medical_name": " 221123213213",
 "medical_record": "http://picture.quanjiakan.com:9080/quanjiakan/resources/useractivate/20161126143317_l2xfey.png",
 "user_id": 11108
 },
 {
 "createtime": "2016-11-26 16:26:02.0",
 "deviceid": "0",
 "id": 30,
 "medical_name": "皇家宝贝哈",
 "medical_record": "http://picture.quanjiakan.com:9080/quanjiakan/resources/useractivate/20161126162558_zj35gu.png",
 "user_id": 11108
 }
 ],
 "message": "返回成功",
 "total": 2
 }
 */

public class HealthDocumentResult implements Serializable{
    private String code;
    private List<HealthDocumentEntity> rows;
    private String message;
    private int total;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<HealthDocumentEntity> getRows() {
        return rows;
    }

    public void setRows(List<HealthDocumentEntity> rows) {
        this.rows = rows;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
