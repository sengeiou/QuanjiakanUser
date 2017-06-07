package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/8 0008.
 */

public class WatchSos implements Serializable{

    /**
     * Results : {"IMEI":"355637052788650","Category":"SOSReport","SOSReport":{"Type":"WIFI","Radius":"550","Location":"113.2409402,23.1326885"}}
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
         * IMEI : 355637052788650
         * Category : SOSReport
         * SOSReport : {"Type":"WIFI","Radius":"550","Location":"113.2409402,23.1326885"}
         */

        private String IMEI;
        private String Category;
        private SOSReportBean SOSReport;

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

        public SOSReportBean getSOSReport() {
            return SOSReport;
        }

        public void setSOSReport(SOSReportBean SOSReport) {
            this.SOSReport = SOSReport;
        }

        public static class SOSReportBean {
            /**
             * Type : WIFI
             * Radius : 550
             * Location : 113.2409402,23.1326885
             */

            private String Type;
            private String Radius;
            private String Location;

            public String getType() {
                return Type;
            }

            public void setType(String Type) {
                this.Type = Type;
            }

            public String getRadius() {
                return Radius;
            }

            public void setRadius(String Radius) {
                this.Radius = Radius;
            }

            public String getLocation() {
                return Location;
            }

            public void setLocation(String Location) {
                this.Location = Location;
            }
        }
    }
}
