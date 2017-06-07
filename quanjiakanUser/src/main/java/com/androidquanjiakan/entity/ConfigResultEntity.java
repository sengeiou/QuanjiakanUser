package com.androidquanjiakan.entity;

/**
 * Created by Gin on 2017/3/16.
 */

public class ConfigResultEntity {


    private Results Results;

    public static class Results{
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

        public void setCategory(String category) {
            Category = category;
        }

        public ConfigBean getConfig() {
            return Config;
        }

        public void setConfig(ConfigBean config) {
            Config = config;
        }
    }


    public Results getResult() {
        return Results;
    }

    public void setResult(Results Results) {
        Results = Results;
    }
}
