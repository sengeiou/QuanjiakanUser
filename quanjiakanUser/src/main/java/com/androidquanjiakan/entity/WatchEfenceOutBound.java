package com.androidquanjiakan.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/4/8 0008.
 */

public class WatchEfenceOutBound implements Serializable {

    /**
     * Results : {"IMEI":"355637052788650","Category":"EfenceReport","EfenceReport":{"Type":"WIFI","Radius":"550","Location":"113.2409402,23.1326885","Bounds":"In","TimeStamp":"2017-04-06 20:51:45","Index":"2","Efence":{"Num":"3","Points":["113.2409402,23.1326885","113.2409412,23.1326895","113.2409408,23.1326890"]}}}
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
         * Category : EfenceReport
         * EfenceReport : {"Type":"WIFI","Radius":"550","Location":"113.2409402,23.1326885","Bounds":"In","TimeStamp":"2017-04-06 20:51:45","Index":"2","Efence":{"Num":"3","Points":["113.2409402,23.1326885","113.2409412,23.1326895","113.2409408,23.1326890"]}}
         */

        private String IMEI;
        private String Category;
        private EfenceReportBean EfenceReport;

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

        public EfenceReportBean getEfenceReport() {
            return EfenceReport;
        }

        public void setEfenceReport(EfenceReportBean EfenceReport) {
            this.EfenceReport = EfenceReport;
        }

        public static class EfenceReportBean {
            /**
             * Type : WIFI
             * Radius : 550
             * Location : 113.2409402,23.1326885
             * Bounds : In
             * TimeStamp : 2017-04-06 20:51:45
             * Index : 2
             * Efence : {"Num":"3","Points":["113.2409402,23.1326885","113.2409412,23.1326895","113.2409408,23.1326890"]}
             */

            private String Type;
            private String Radius;
            private String Location;
            private String Bounds;
            private String TimeStamp;
            private String Index;
            private EfenceBean Efence;

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

            public String getBounds() {
                return Bounds;
            }

            public void setBounds(String Bounds) {
                this.Bounds = Bounds;
            }

            public String getTimeStamp() {
                return TimeStamp;
            }

            public void setTimeStamp(String TimeStamp) {
                this.TimeStamp = TimeStamp;
            }

            public String getIndex() {
                return Index;
            }

            public void setIndex(String Index) {
                this.Index = Index;
            }

            public EfenceBean getEfence() {
                return Efence;
            }

            public void setEfence(EfenceBean Efence) {
                this.Efence = Efence;
            }

            public static class EfenceBean {
                /**
                 * Num : 3
                 * Points : ["113.2409402,23.1326885","113.2409412,23.1326895","113.2409408,23.1326890"]
                 */

                private String Num;
                private List<String> Points;

                public String getNum() {
                    return Num;
                }

                public void setNum(String Num) {
                    this.Num = Num;
                }

                public List<String> getPoints() {
                    return Points;
                }

                public void setPoints(List<String> Points) {
                    this.Points = Points;
                }
            }
        }
    }
}
