package com.androidquanjiakan.result;

/**
 * 作者：Administrator on 2017/3/22 16:31
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class FlowDataBean {

    /**
     * Results : {"Category":"Flow","Details":"尊敬的用户：截止2017年03月22日16时，您手机尾数0029的号码本月产生的总流量（不含国际及港澳台)15.35MB。主要流量包使用情况如下：\u201c【主套餐国内流量】\u201d100MB，已用0MB，剩余100MB；\u201c上月主套餐结转流量\u201d,已用15.35MB，剩余84.65MB；套餐外收费流量（不含国际及港澳台）0MB。实际以出帐为准。安装【手机营业厅】客户端，流量包详情随时查，使用便捷、展示清晰，点击下载 http://u.10010.cn/dxyyt 。【玩4G就上 m.10010.com】��","IMEI":"352315052834187"}
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
         * Category : Flow
         * Details : 尊敬的用户：截止2017年03月22日16时，您手机尾数0029的号码本月产生的总流量（不含国际及港澳台)15.35MB。主要流量包使用情况如下：“【主套餐国内流量】”100MB，已用0MB，剩余100MB；“上月主套餐结转流量”,已用15.35MB，剩余84.65MB；套餐外收费流量（不含国际及港澳台）0MB。实际以出帐为准。安装【手机营业厅】客户端，流量包详情随时查，使用便捷、展示清晰，点击下载 http://u.10010.cn/dxyyt 。【玩4G就上 m.10010.com】��
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
