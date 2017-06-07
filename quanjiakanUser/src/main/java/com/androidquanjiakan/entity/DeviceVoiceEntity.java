package com.androidquanjiakan.entity;

import com.androidquanjiakan.base.BaseApplication;

/**
 * Created by Administrator on 2016/11/10 0010.
 */

public class DeviceVoiceEntity {
    private String userid;
    private String user_name;
    private String device_id;
    private String direction;
    private String voice_path;
    private String time;

    private String voiceTime;

    private String readFlag;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getVoice_path() {
        return voice_path;
    }

    public void setVoice_path(String voice_path) {
        this.voice_path = voice_path;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVoiceTime() {
        return voiceTime;
    }

    public void setVoiceTime(String voiceTime) {
        this.voiceTime = voiceTime;
    }

    public String getReadFlag() {
        return readFlag;
    }

    public void setReadFlag(String readFlag) {
        this.readFlag = readFlag;
    }

    public void setUnReadFlag(){
        if(readFlag==null){
            readFlag = BaseApplication.getInstances().getUser_id()+"_0;";
        }else{
            if(readFlag.contains(BaseApplication.getInstances().getUser_id()+"_0")){
                //Do no thing
            }else if(readFlag.contains(BaseApplication.getInstances().getUser_id()+"_1")){
                String read = BaseApplication.getInstances().getUser_id()+"_1";
                String unread = BaseApplication.getInstances().getUser_id()+"_0";
                readFlag = readFlag.replace(read,unread);//TODO 将已读替换为未读
            }else{
                readFlag +=BaseApplication.getInstances().getUser_id()+"_0;";
            }
        }
    }

    public void setReadFlag(){
        if(readFlag==null){
            readFlag = BaseApplication.getInstances().getUser_id()+"_1;";
        }else{
            if(readFlag.contains(BaseApplication.getInstances().getUser_id()+"_1")){
                //Do no thing
            }else if(readFlag.contains(BaseApplication.getInstances().getUser_id()+"_0")){
                String read = BaseApplication.getInstances().getUser_id()+"_1";
                String unread = BaseApplication.getInstances().getUser_id()+"_0";
                readFlag = readFlag.replace(unread,read);//TODO 将未读标识替换为已读
            }else{
                readFlag +=BaseApplication.getInstances().getUser_id()+"_1;";
            }
        }
    }

    public String getReadFlagString(){
        return BaseApplication.getInstances().getUser_id()+"_1";
    }

    public String getUnReadFlagString(){
        return BaseApplication.getInstances().getUser_id()+"_0";
    }

    public boolean isReadMessage(){
        return readFlag.contains(BaseApplication.getInstances().getUser_id()+"_1");
    }


    @Override
    public String toString() {
        return "DeviceVoiceEntity{" +
                "userid='" + userid + '\'' +
                ", direction='" + direction + '\'' +
                ", voice_path='" + voice_path + '\'' +
                ", time='" + time + '\'' +
                ", voiceTime='" + voiceTime + '\'' +
                ", readFlag='" + readFlag + '\'' +
                '}';
    }
}
