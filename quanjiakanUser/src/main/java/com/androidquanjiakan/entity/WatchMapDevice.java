package com.androidquanjiakan.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.List;


public class WatchMapDevice implements Serializable {

    private static final long serialVersionUID = 1614199093090437162L;


    /**
     * Results : {"Category":"WatchList","WatchList":[{"IMEI":"240207489224233264","Location":"113.2409402,23.1326885","Name":"干妈","Online":"0","Picture":"","Time":"2017-01-01 00:00:00","Type":"0"}]}
     */

    private ResultsBean Results;

    public ResultsBean getResults() {
        return Results;
    }

    public void setResults(ResultsBean Results) {
        this.Results = Results;
    }

    public class ResultsBean {
        /**
         * Category : WatchList
         * WatchList : [{"IMEI":"240207489224233264","Location":"113.2409402,23.1326885","Name":"干妈","Online":"0","Picture":"","Time":"2017-01-01 00:00:00","Type":"0"}]
         */

        private String Category;
        private List<WatchMapDevice_DeviceInfo> WatchList;

        public String getCategory() {
            return Category;
        }

        public void setCategory(String Category) {
            this.Category = Category;
        }

        public List<WatchMapDevice_DeviceInfo> getWatchList() {
            return WatchList;
        }

        public void setWatchList(List<WatchMapDevice_DeviceInfo> WatchList) {
            this.WatchList = WatchList;
        }

    }
}
