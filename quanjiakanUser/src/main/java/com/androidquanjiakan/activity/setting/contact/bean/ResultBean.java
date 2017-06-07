package com.androidquanjiakan.activity.setting.contact.bean;

/**
 * Created by Gin on 2017/3/14.
 */

public class ResultBean {
    private String Id;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    private Result Results;

    public static class Result{
        private String Code;
        private String Message;

        public String getCode() {
            return Code;
        }

        public void setCode(String code) {
            Code = code;
        }

        public String getMessage() {
            return Message;
        }

        public void setMessage(String message) {
            Message = message;
        }

    }

    public ResultBean.Result getResult() {
        return Results;
    }

    public void setResult(ResultBean.Result Results) {
        this.Results = Results;
    }
}
