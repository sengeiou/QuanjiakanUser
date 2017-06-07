package com.androidquanjiakan.entity;

import com.google.gson.JsonObject;

import cn.jpush.im.android.api.model.Conversation;

/**
 * Created by Administrator on 2016/8/5 0005.
 */
public class ProblemEntity {
    private Conversation mConversation;
    private JsonObject mJsonObject;
    private FreeInquiryProblemEntity problemInfoEntity;

    private String info;

    public Conversation getmConversation() {
        return mConversation;
    }

    public void setmConversation(Conversation mConversation) {
        this.mConversation = mConversation;
    }

    public JsonObject getmJsonObject() {
        return mJsonObject;
    }

    public void setmJsonObject(JsonObject mJsonObject) {
        this.mJsonObject = mJsonObject;
    }

    public FreeInquiryProblemEntity getProblemInfoEntity() {
        return problemInfoEntity;
    }

    public void setProblemInfoEntity(FreeInquiryProblemEntity problemInfoEntity) {
        this.problemInfoEntity = problemInfoEntity;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
