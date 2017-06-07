package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/3/15 0015.
 */

public class WatchEfenceResult implements Serializable {

    /**
     * Id : 148
     * Result : {"Code":"200","Message":"success"}
     */

    private String Id;
    private ResultBean Results;

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public ResultBean getResults() {
        return Results;
    }

    public void setResults(ResultBean Result) {
        this.Results = Result;
    }

    public static class ResultBean {
        /**
         * Code : 200
         * Message : success
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
