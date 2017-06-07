package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/14 0014.
 */

public class WatchCommonResult implements Serializable{

    /**
     * Result : {"Code":"10001","Message":"10001"}
     */

    private ResultBean Results;

    public ResultBean getResult() {
        return Results;
    }

    public void setResult(ResultBean Result) {
        this.Results = Result;
    }

    public static class ResultBean {
        /**
         * Code : 10001
         * Message : 10001
         */

        private String Code;
        private String Message;

        public String getCode() {
            return Code;
        }

        public void setCode(String Code) {
            this.Code = Code;
        }

        public String getMessage() {
            return Message;
        }

        public void setMessage(String Message) {
            this.Message = Message;
        }
    }
}
