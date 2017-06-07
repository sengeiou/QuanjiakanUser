package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/14 0014.
 */

public class WatchGetConfig_results implements Serializable{

    /**
     * Results : {"IMEI":"352315052834187","Category":"Config","Config":{"Power":"3","Signal":"0","Steps":"8000","PhoneNum":"48915552","Location":",,255"}}
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
         * IMEI : 352315052834187
         * Category : Config
         * Config : {"Power":"3","Signal":"0","Steps":"8000","PhoneNum":"48915552","Location":",,255"}
         */

        private String IMEI;
        private String Category;
        private ConfigBean Config;

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

        public ConfigBean getConfig() {
            return Config;
        }

        public void setConfig(ConfigBean Config) {
            this.Config = Config;
        }

        public static class ConfigBean {
            /**
             * Power : 3
             * Signal : 0
             * Steps : 8000
             * WearStatus: "On",
             * PhoneNum : 48915552
             * Location : ,,255
             */

            private String Power;
            private String Signal;
            private String Steps;
            private String WearStatus;
            private String PhoneNum;
            private String Location;

            public String getPower() {
                return Power;
            }

            public void setPower(String Power) {
                this.Power = Power;
            }

            public String getSignal() {
                return Signal;
            }

            public void setSignal(String Signal) {
                this.Signal = Signal;
            }

            public String getSteps() {
                return Steps;
            }

            public void setSteps(String Steps) {
                this.Steps = Steps;
            }

            public String getWearStatus() {
                return WearStatus;
            }

            public void setWearStatus(String wearStatus) {
                WearStatus = wearStatus;
            }

            public String getPhoneNum() {
                return PhoneNum;
            }

            public void setPhoneNum(String PhoneNum) {
                this.PhoneNum = PhoneNum;
            }

            public String getLocation() {
                return Location;
            }

            public void setLocation(String Location) {
                this.Location = Location;
            }
        }
    }

    @Override
    public String toString() {
        return "WatchGetConfig_results{" +
                "results=" + Results +
                '}';
    }
}
