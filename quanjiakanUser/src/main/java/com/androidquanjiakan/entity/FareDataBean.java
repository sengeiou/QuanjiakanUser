package com.androidquanjiakan.entity;

/**
 * 作者：Administrator on 2017/3/18 18:38
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class FareDataBean {


    /**
     * Results : {"Category":"Fare","Details":"您的账户本月可用余额38.80元，实时话费20.40元，当前可用余额18.40元，如您需查询余额详细情况，请发送短信YEXX至10010。推荐您使用\u201c手机营业厅\u201d一键快速查询，页面展示更清晰，充值交费线上快捷支付，使用免流量，马上下载请点击 http://u.10010.cn/dxyyt 。【玩4G就上 m.10010.com】������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������","IMEI":"352315052834187"}
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
         * Category : Fare
         * Details : 您的账户本月可用余额38.80元，实时话费20.40元，当前可用余额18.40元，如您需查询余额详细情况，请发送短信YEXX至10010。推荐您使用“手机营业厅”一键快速查询，页面展示更清晰，充值交费线上快捷支付，使用免流量，马上下载请点击 http://u.10010.cn/dxyyt 。【玩4G就上 m.10010.com】������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������������
         * IMEI : 352315052834187
         */

        private String Category;
        private String Details;
        private String IMEI;

        public String getCategory() {
            return Category;
        }

        public void setCategory(String Category) {
            this.Category = Category;
        }

        public String getDetails() {
            return Details;
        }

        public void setDetails(String Details) {
            this.Details = Details;
        }

        public String getIMEI() {
            return IMEI;
        }

        public void setIMEI(String IMEI) {
            this.IMEI = IMEI;
        }
    }
}
