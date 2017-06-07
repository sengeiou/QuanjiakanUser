package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/18 0018.
 */

public class WatchLocationBroadcase implements Serializable{

    /**
     * Results : {"Type":"WIFI","Radius":"35","Location":"113.2416873,23.1320855","Category":"WIFI","IMEI":"352315052834187"}
     * {"Results":{"Type":"WIFI","Radius":"35","Location":"113.2415614,23.1322708","Category":"WIFI","IMEI":"355637050066828"}}
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
         * Type : WIFI
         * Radius : 35
         * Location : 113.2416873,23.1320855
         * Category : WIFI
         * IMEI : 352315052834187
         */

        private String Type;
        private String Radius;
        private String Location;
        private String Category;
        private String IMEI;

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

        public String getCategory() {
            return Category;
        }

        public void setCategory(String Category) {
            this.Category = Category;
        }

        public String getIMEI() {
            return IMEI;
        }

        public void setIMEI(String IMEI) {
            this.IMEI = IMEI;
        }
    }
}
