package com.androidquanjiakan.result;

/**
 * 作者：Administrator on 2017/3/10 11:22
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class RuntimeResultBean {

    /**
     * Results : {"IMEI":"355637052788650","Category":" RunTime","RunTime":{"AutoConnection":"1","LossReport":"1","LightPanel":"30","WatchBell":"11,11","TagetStep":"5000"}}
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
         * Category :  RunTime
         * RunTime : {"AutoConnection":"1","LossReport":"1","LightPanel":"30","WatchBell":"11,11","TagetStep":"5000"}
         */

        private String IMEI;
        private String Category;
        private RunTimeBean RunTime;

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

        public RunTimeBean getRunTime() {
            return RunTime;
        }

        public void setRunTime(RunTimeBean RunTime) {
            this.RunTime = RunTime;
        }

        public static class RunTimeBean {
            /**
             * AutoConnection : 1
             * LossReport : 1
             * LightPanel : 30
             * WatchBell : 11,11
             * TagetStep : 5000
             * Model: 1
             */

            private String AutoConnection;
            private String LossReport;
            private String LightPanel;
            private String WatchBell;
            private String TagetStep;
            private String Model;

            public String getModel() {
                return Model;
            }

            public void setModel(String model) {
                Model = model;
            }

            public String getAutoConnection() {
                return AutoConnection;
            }

            public void setAutoConnection(String AutoConnection) {
                this.AutoConnection = AutoConnection;
            }

            public String getLossReport() {
                return LossReport;
            }

            public void setLossReport(String LossReport) {
                this.LossReport = LossReport;
            }

            public String getLightPanel() {
                return LightPanel;
            }

            public void setLightPanel(String LightPanel) {
                this.LightPanel = LightPanel;
            }

            public String getWatchBell() {
                return WatchBell;
            }

            public void setWatchBell(String WatchBell) {
                this.WatchBell = WatchBell;
            }

            public String getTagetStep() {
                return TagetStep;
            }

            public void setTagetStep(String TagetStep) {
                this.TagetStep = TagetStep;
            }
        }
    }
}
