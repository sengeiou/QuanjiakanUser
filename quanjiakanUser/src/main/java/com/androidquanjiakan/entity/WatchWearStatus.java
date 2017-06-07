package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/12 0012.
 */

public class WatchWearStatus implements Serializable {

    /**
     * Results : {"IMEI":"355637052238805","Category":"WearStatus","WearStatus":"Off"}
     */

    private ResultsBean Results;

    public ResultsBean getResults() {
        return Results;
    }

    public void setResults(ResultsBean Results) {
        this.Results = Results;
    }

    public static class ResultsBean {
        /**
         * IMEI : 355637052238805
         * Category : WearStatus
         * WearStatus : Off
         */

        private String IMEI;
        private String Category;
        private String WearStatus;

        public String getIMEI() {
            return IMEI;
        }

        public void setIMEI(String IMEI) {
            this.IMEI = IMEI;
        }

        public String getCategory() {
            return Category;
        }

        public void setCategory(String Category) {
            this.Category = Category;
        }

        public String getWearStatus() {
            return WearStatus;
        }

        public void setWearStatus(String WearStatus) {
            this.WearStatus = WearStatus;
        }
    }
}
