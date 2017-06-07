package com.androidquanjiakan.entity;

/**
 * Created by Gin on 2017/3/9.
 */

public class ScheduleEntity  {
    private String Daily;
    private String Time;
    private String Details;
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


}
