package com.androidquanjiakan.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/3/15 0015.
 */

public class WatchEfenceBroadcastAdd implements Serializable {

    /**
     * Id : 2
     * Results : {"IMEI":"355637053995130","Category":"Efence","Action":"Add","Index":"2","Efence":{"Num":"4","Points":["113.242554,23.139700","113.242584,23.132236","113.252510,23.131338","113.252510,23.139931"]}}
     */

    private String Id;
    private ResultsBean Results;

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public ResultsBean getResults() {
        return Results;
    }

    public void setResults(ResultsBean Results) {
        this.Results = Results;
    }

    public static class ResultsBean {
        /**
         * IMEI : 355637053995130
         * Category : Efence
         * Action : Add
         * Index : 2
         * Efence : {"Num":"4","Points":["113.242554,23.139700","113.242584,23.132236","113.252510,23.131338","113.252510,23.139931"]}
         */

        private String IMEI;
        private String Category;
        private String Action;
        private String Index;
        private EfenceBean Efence;

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

        public String getAction() {
            return Action;
        }

        public void setAction(String Action) {
            this.Action = Action;
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
             * Num : 4
             * Points : ["113.242554,23.139700","113.242584,23.132236","113.252510,23.131338","113.252510,23.139931"]
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
