package com.androidquanjiakan.entity;

import android.support.annotation.NonNull;

/**
 * Created by Gin on 2017/3/15.
 */

public class ScheduleListEntity implements Comparable<ScheduleListEntity>{
    private String Daily;
    private String Time;
    private String Details;
    private String Id;
    private String Status;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getDaily() {
        return Daily;
    }

    public void setDaily(String daily) {
        Daily = daily;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        this.Id = id;
    }

    @Override
    public int compareTo(@NonNull ScheduleListEntity scheduleListEntity) {
        int i = Integer.valueOf(this.getTime().substring(0, 2)) - Integer.valueOf(scheduleListEntity.getTime().substring(0, 2));
        if(i==0) {
            return Integer.valueOf(this.getTime().substring(3,5)) - Integer.valueOf(scheduleListEntity.getTime().substring(3, 5));
        }
        return i;
    }
}
