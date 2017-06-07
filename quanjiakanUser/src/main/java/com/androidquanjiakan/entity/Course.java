package com.androidquanjiakan.entity;


import java.io.Serializable;

public class Course implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -9121734039844677432L;
    private int jieci;

    private int day;
    private String des;
    private String time;
    private int spanNum = 1;// Ĭ�Ͽ�Խ����
    private String weekStr;
    private boolean isClose;

    public boolean isClose() {
        return isClose;
    }

    public void setClose(boolean close) {
        isClose = close;
    }

    public String getWeekStr() {
        return weekStr;
    }

    public void setWeekStr(String weekStr) {
        this.weekStr = weekStr;
    }

    private String ClassRoomName;
    private String ClassTypeName;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Course(int jieci, int day, String des, String time) {
        this.jieci = jieci;
        this.day = day;
        this.des = des;
        this.time = time;
    }

    public Course() {
    }

    public int getJieci() {
        return jieci;
    }

    public void setJieci(int jieci) {
        this.jieci = jieci;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getSpanNum() {
        return spanNum;
    }

    public void setSpanNum(int spanNum) {
        this.spanNum = spanNum;
    }

    @Override
    public String toString() {
        return "Course [jieci=" + jieci + ", day=" + day + ", des=" + des
                + ", spanNun=" + spanNum + "]";
    }

    public String getClassRoomName() {
        return ClassRoomName;
    }

    public void setClassRoomName(String classRoomName) {
        ClassRoomName = classRoomName;
    }

    public String getClassTypeName() {
        return ClassTypeName;
    }

    public void setClassTypeName(String classTypeName) {
        ClassTypeName = classTypeName;
    }


}
