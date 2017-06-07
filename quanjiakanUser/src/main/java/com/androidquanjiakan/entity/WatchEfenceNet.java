package com.androidquanjiakan.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/3/16 0016.
 */

public class WatchEfenceNet implements Serializable {

    /**
     * Results : {"Category":"EfenceList","EfenceList":[{"Efence":{"Num":4,"Points":["113.242554,23.139700","113.242584,23.132236","113.252510,23.131338","113.252510,23.139931"]},"Index":"-1132383440"},{"Efence":{"Num":4,"Points":["113.242554,23.139700","113.242584,23.132236","113.252510,23.131338","113.252510,23.139931"]},"Index":"-1134181592"},{"Efence":{"Num":4,"Points":["113.240631,23.140368","113.241577,23.131927","113.252037,23.132467","113.250366,23.142883"]},"Index":"-1140243664"},{"Efence":{"Num":4,"Points":["113.240128,23.142803","113.241043,23.131004","113.252876,23.129951","113.253128,23.141367"]},"Index":"1"},{"Efence":{"Num":4,"Points":["113.240402,23.142906","113.241325,23.131083","113.251595,23.130056","113.253464,23.143112"]},"Index":"1"}],"IMEI":"240207489224233264"}
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
         * Category : EfenceList
         * EfenceList : [{"Efence":{"Num":4,"Points":["113.242554,23.139700","113.242584,23.132236","113.252510,23.131338","113.252510,23.139931"]},"Index":"-1132383440"},{"Efence":{"Num":4,"Points":["113.242554,23.139700","113.242584,23.132236","113.252510,23.131338","113.252510,23.139931"]},"Index":"-1134181592"},{"Efence":{"Num":4,"Points":["113.240631,23.140368","113.241577,23.131927","113.252037,23.132467","113.250366,23.142883"]},"Index":"-1140243664"},{"Efence":{"Num":4,"Points":["113.240128,23.142803","113.241043,23.131004","113.252876,23.129951","113.253128,23.141367"]},"Index":"1"},{"Efence":{"Num":4,"Points":["113.240402,23.142906","113.241325,23.131083","113.251595,23.130056","113.253464,23.143112"]},"Index":"1"}]
         * IMEI : 240207489224233264
         */

        private String Category;
        private String IMEI;
        private List<EfenceListBean> EfenceList;

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

        public List<EfenceListBean> getEfenceList() {
            return EfenceList;
        }

        public void setEfenceList(List<EfenceListBean> EfenceList) {
            this.EfenceList = EfenceList;
        }

        public static class EfenceListBean {
            /**
             * Efence : {"Num":4,"Points":["113.242554,23.139700","113.242584,23.132236","113.252510,23.131338","113.252510,23.139931"]}
             * Index : -1132383440
             */

            private EfenceBean Efence;
            private String Index;

            public EfenceBean getEfence() {
                return Efence;
            }

            public void setEfence(EfenceBean Efence) {
                this.Efence = Efence;
            }

            public String getIndex() {
                return Index;
            }

            public void setIndex(String Index) {
                this.Index = Index;
            }

            public static class EfenceBean {
                /**
                 * Num : 4
                 * Points : ["113.242554,23.139700","113.242584,23.132236","113.252510,23.131338","113.252510,23.139931"]
                 */

                private int Num;
                private List<String> Points;

                public int getNum() {
                    return Num;
                }

                public void setNum(int Num) {
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
