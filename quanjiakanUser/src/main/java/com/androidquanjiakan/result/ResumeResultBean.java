package com.androidquanjiakan.result;

/**
 * 作者：Administrator on 2017/3/10 10:32
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class ResumeResultBean {

    /**
     * Results : {"IMEI":"355637052788650","Category":"Turn","Turn":{"Status":"1","On":{"Time":"08:00:00"},"Off":{"Time":"18:00:00"}}}
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
         * Category :  Turn
         * Turn : {"Status":"1","On":{"Time":"08:00:00"},"Off":{"Time":"18:00:00"}}
         */
        private String IMEI;
        private String Category;
        private TurnBean Turn;

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

        public TurnBean getTurn() {
            return Turn;
        }

        public void setTurn(TurnBean Turn) {
            this.Turn = Turn;
        }

        public static class TurnBean {

            /**
             * Status : 1
             * On : {"Time":"08:00:00"}
             * Off : {"Time":"18:00:00"}
             */

            private String Status;
            private OnBean On;
            private OffBean Off;

            public String getStatus() {
                return Status;
            }

            public void setStatus(String Status) {
                this.Status = Status;
            }

            public OnBean getOn() {
                return On;
            }

            public void setOn(OnBean On) {
                this.On = On;
            }

            public OffBean getOff() {
                return Off;
            }

            public void setOff(OffBean Off) {
                this.Off = Off;
            }

            public static class OnBean {

                /**
                 * Time : 08:00:00
                 */

                private String Time;

                public String getTime() {
                    return Time;
                }

                public void setTime(String Time) {
                    this.Time = Time;
                }
            }

            public static class OffBean {

                /**
                 * Time : 18:00:00
                 */

                private String Time;

                public String getTime() {
                    return Time;
                }

                public void setTime(String Time) {
                    this.Time = Time;
                }
            }
        }
    }
}
