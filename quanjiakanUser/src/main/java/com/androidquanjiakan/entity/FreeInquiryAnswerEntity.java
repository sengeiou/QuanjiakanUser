package com.androidquanjiakan.entity;

import com.google.gson.JsonArray;
import com.quanjiakanuser.util.GsonParseUtil;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/10 0010.
 */
public class FreeInquiryAnswerEntity implements Serializable {
    /**
     * {
     "id": "2",
     "price": "0",
     "doctor_id": "clinic_web_a3f76b1b6c3c50d7",
     "created_time_ms": "1473479795111",
     "patient": "{\"type\":\"patient_meta\",\"age\":\"30\",\"sex\":\"男\"}",

     "creator": "10691",
     "createtime": "2016-09-10 11:57:21.0",
     "chunyu_id": "412625718",
     "status": "1",
     "title": "你好！肚子疼了一天，吃不下饭",

     "user_id": "10691",
     "content": "[{\"text\":\"你好，肚子疼之前有吃了什么东西吗？\",\"type\":\"text\"}]",
     "fromtoken": "",
     "totoken": "10691",
     "problem_content_status": "1"
     }

     */

    private String id;
    private String price;
    private String doctor_id;
    private String created_time_ms;
    private String patient;

    private String creator;
    private String createtime;
    private String chunyu_id;
    private String status;
    private String title;

    private String is_reply;
    private String user_id;
    private String sponsor;//0 医生  1 用户
    private String content;
    private String fromtoken;

    private String totoken;
    private String problem_content_status;


    private String voiceTime;

    //

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getCreated_time_ms() {
        return created_time_ms;
    }

    public void setCreated_time_ms(String created_time_ms) {
        this.created_time_ms = created_time_ms;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getChunyu_id() {
        return chunyu_id;
    }

    public void setChunyu_id(String chunyu_id) {
        this.chunyu_id = chunyu_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFromtoken() {
        return fromtoken;
    }

    public void setFromtoken(String fromtoken) {
        this.fromtoken = fromtoken;
    }

    public String getTotoken() {
        return totoken;
    }

    public void setTotoken(String totoken) {
        this.totoken = totoken;
    }

    public String getProblem_content_status() {
        return problem_content_status;
    }

    public void setProblem_content_status(String problem_content_status) {
        this.problem_content_status = problem_content_status;
    }

    public String getIs_reply() {
        return is_reply;
    }

    public void setIs_reply(String is_reply) {
        this.is_reply = is_reply;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public String getVoiceTime() {
        return voiceTime;
    }

    public void setVoiceTime(String voiceTime) {
        this.voiceTime = voiceTime;
    }

    public JsonArray getContentString(){
        JsonArray jsonArray = new GsonParseUtil(content.replace("\\","")).getJsonArray();
        return jsonArray;
    }

    @Override
    public String toString() {
        return "FreeInquiryAnswerEntity{" +
                "id='" + id + '\'' +
                ", doctor_id='" + doctor_id + '\'' +
                ", created_time_ms='" + created_time_ms + '\'' +
                ", sponsor='" + ("0".endsWith(sponsor)? "医生":"用户") + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

}
