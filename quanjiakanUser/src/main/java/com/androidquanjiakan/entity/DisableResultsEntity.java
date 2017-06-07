package com.androidquanjiakan.entity;

import java.util.List;

/**
 * Created by Gin on 2017/3/9.
 */

public class DisableResultsEntity {
    private results Results;
    public static class results{
        private String IMEI;
        private String Category;
        private List<DisableEntity>TimeTables;

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

        public List<DisableEntity> getTimeTables() {
            return TimeTables;
        }

        public void setTimeTables(List<DisableEntity> timeTables) {
            TimeTables = timeTables;
        }

        public results(String IMEI, String category, List<DisableEntity> timeTables) {
            this.IMEI = IMEI;
            Category = category;
            TimeTables = timeTables;
        }
    }

    public DisableResultsEntity.results getResults() {
        return Results;
    }

    public void setResults(DisableResultsEntity.results Results) {
        this.Results = Results;
    }
}
