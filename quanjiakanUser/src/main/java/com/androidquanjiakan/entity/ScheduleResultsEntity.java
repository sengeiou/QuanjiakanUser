package com.androidquanjiakan.entity;

import java.util.List;

/**
 * Created by Gin on 2017/3/9.
 */

public class ScheduleResultsEntity {
    private results Results;
    public static class results{
        private String IMEI;
        private String Category;
        private String  Num;
        private List<ScheduleListEntity>Schedule;


        public results(String IMEI, String category, String num, List<ScheduleListEntity> schedule) {
            this.IMEI = IMEI;
            Category = category;
            Num = num;
            Schedule = schedule;
        }

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

        public String getNum() {
            return Num;
        }

        public void setNum(String num) {
            Num = num;
        }

        public List<ScheduleListEntity> getSchedule() {
            return Schedule;
        }

        public void setSchedule(List<ScheduleListEntity> schedule) {
            Schedule = schedule;
        }
    }

    public ScheduleResultsEntity.results getResults() {
        return Results;
    }

    public void setResults(ScheduleResultsEntity.results Results) {
        this.Results = Results;
    }
}
