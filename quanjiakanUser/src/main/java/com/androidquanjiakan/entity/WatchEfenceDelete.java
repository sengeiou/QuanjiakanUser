package com.androidquanjiakan.entity;

/**
 * Created by Administrator on 2017/3/15 0015.
 */

public class WatchEfenceDelete {

    /**
     * Results : {"IMEI":"355637053995130","Category":"Efence","Action":"Delete","Index":"1"}
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
         * IMEI : 355637053995130
         * Category : Efence
         * Action : Delete
         * Index : 1
         */

        private String IMEI;
        private String Category;
        private String Action;
        private String Index;

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
    }
}
