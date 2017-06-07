package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Gin on 2017/3/8.
 */

public class DisableEntity implements Serializable{


    private String Daily;
    private Morning Morning;
    private Afternoon Afternoon;

    public static class Morning{
        private String Status;
        private String StartTime;
        private String EndTime;

        public  String getStatus() {
            return Status;
        }

        public void setStatus(String status) {
            Status = status;
        }

        public String getStartTime() {
            return StartTime;
        }

        public void setStartTime(String startTime) {
            StartTime = startTime;
        }

        public String getEndTime() {
            return EndTime;
        }

        public void setEndTime(String endTime) {
            EndTime = endTime;
        }

        public Morning(String status, String startTime, String endTime) {
            Status = status;
            StartTime = startTime;
            EndTime = endTime;
        }

        public Morning() {
        }
    }

    public static class Afternoon{
        private String Status;
        private String StartTime;
        private String EndTime;

        public String getStatus() {
            return Status;
        }

        public void setStatus(String status) {
            Status = status;
        }

        public String getStartTime() {
            return StartTime;
        }

        public void setStartTime(String startTime) {
            StartTime = startTime;
        }

        public String getEndTime() {
            return EndTime;
        }

        public void setEndTime(String endTime) {
            EndTime = endTime;
        }

        public Afternoon(String status, String startTime, String endTime) {
            Status = status;
            StartTime = startTime;
            EndTime = endTime;
        }

        public Afternoon() {
        }
    }


    public String getDaily() {
        return Daily;
    }

    public void setDaily(String daily) {
        Daily = daily;
    }

    public DisableEntity.Morning getMorning() {
        return Morning;
    }

    public void setMorning(DisableEntity.Morning morning) {
        Morning = morning;
    }

    public DisableEntity.Afternoon getAfternoon() {
        return Afternoon;
    }

    public void setAfternoon(DisableEntity.Afternoon afternoon) {
        Afternoon = afternoon;
    }
}
