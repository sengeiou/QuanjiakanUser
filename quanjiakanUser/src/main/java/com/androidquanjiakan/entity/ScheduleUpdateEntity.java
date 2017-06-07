package com.androidquanjiakan.entity;


/**
 * Created by Gin on 2017/3/10.
 */

public class ScheduleUpdateEntity {
    private String IMEI;
    private String Category;
    private String Action;
    private String Id;

    private ScheduleEntity Schedule;

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public ScheduleEntity getSchedule() {
        return Schedule;
    }

    public void setSchedule(ScheduleEntity schedule) {
        Schedule = schedule;
    }
}
