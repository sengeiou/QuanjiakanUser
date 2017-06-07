package com.androidquanjiakan.result;

import java.util.List;

/**
 * 作者：Administrator on 2017/3/10 14:05
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class ContactsResultBean {

    /**
     * Results : {"Category":"Contacts","Contacts":[{"Admin":"1","App":"1","Id":"3","Image":"0","Name":"爸爸","Tel":"13802735616","Userid":"11931"},{"Admin":"0","App":"1","Id":"5","Image":"0","Name":"哥哥","Tel":"13650703987","Userid":"11178"},{"Admin":"0","App":"1","Id":"6","Image":"0","Name":"姐姐","Tel":"15820233639","Userid":"10833"},{"Admin":"0","App":"1","Id":"7","Image":"0","Name":"干爹","Tel":"18773282552","Userid":"11780"},{"Admin":"0","App":"1","Id":"73","Name":"Father","Tel":"13681710674","Userid":"13189"},{"Admin":"0","App":"1","Id":"105","Name":"Father","Tel":"13650854428","Userid":"11913"},{"Admin":"0","App":"0","Id":"106","Image":"(null)","Name":"(null)","Tel":"(null)","Userid":"11780"},{"Admin":"0","App":"0","Id":"108","Image":"(null)","Name":"(null)","Tel":"(null)","Userid":"11780"},{"Admin":"0","App":"1","Id":"109","Name":"Father","Tel":"13666668888","Userid":"11837"},{"Admin":"0","App":"1","Id":"112","Name":"Father","Tel":"18011935659","Userid":"11303"},{"Admin":"0","App":"1","Id":"113","Name":"Father","Tel":"15218293347","Userid":"13469"}],"IMEI":"355637053995130","Num":11}
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
         * Category : Contacts
         * Contacts : [{"Admin":"1","App":"1","Id":"3","Image":"0","Name":"爸爸","Tel":"13802735616","Userid":"11931"},{"Admin":"0","App":"1","Id":"5","Image":"0","Name":"哥哥","Tel":"13650703987","Userid":"11178"},{"Admin":"0","App":"1","Id":"6","Image":"0","Name":"姐姐","Tel":"15820233639","Userid":"10833"},{"Admin":"0","App":"1","Id":"7","Image":"0","Name":"干爹","Tel":"18773282552","Userid":"11780"},{"Admin":"0","App":"1","Id":"73","Name":"Father","Tel":"13681710674","Userid":"13189"},{"Admin":"0","App":"1","Id":"105","Name":"Father","Tel":"13650854428","Userid":"11913"},{"Admin":"0","App":"0","Id":"106","Image":"(null)","Name":"(null)","Tel":"(null)","Userid":"11780"},{"Admin":"0","App":"0","Id":"108","Image":"(null)","Name":"(null)","Tel":"(null)","Userid":"11780"},{"Admin":"0","App":"1","Id":"109","Name":"Father","Tel":"13666668888","Userid":"11837"},{"Admin":"0","App":"1","Id":"112","Name":"Father","Tel":"18011935659","Userid":"11303"},{"Admin":"0","App":"1","Id":"113","Name":"Father","Tel":"15218293347","Userid":"13469"}]
         * IMEI : 355637053995130
         * Num : 11
         */

        private String Category;
        private String IMEI;
        private int Num;
        private List<ContactsBean> Contacts;

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

        public int getNum() {
            return Num;
        }

        public void setNum(int Num) {
            this.Num = Num;
        }

        public List<ContactsBean> getContacts() {
            return Contacts;
        }

        public void setContacts(List<ContactsBean> Contacts) {
            this.Contacts = Contacts;
        }

        public static class ContactsBean {
            /**
             * Admin : 1
             * App : 1
             * Id : 3
             * Image : 0
             * Name : 爸爸
             * Tel : 13802735616
             * Userid : 11931
             */

            private String Admin;
            private String App;
            private String Id;
            private String Image;
            private String Name;
            private String Tel;
            private String Userid;

            public String getAdmin() {
                return Admin;
            }

            public void setAdmin(String Admin) {
                this.Admin = Admin;
            }

            public String getApp() {
                return App;
            }

            public void setApp(String App) {
                this.App = App;
            }

            public String getId() {
                return Id;
            }

            public void setId(String Id) {
                this.Id = Id;
            }

            public String getImage() {
                return Image;
            }

            public void setImage(String Image) {
                this.Image = Image;
            }

            public String getName() {
                return Name;
            }

            public void setName(String Name) {
                this.Name = Name;
            }

            public String getTel() {
                return Tel;
            }

            public void setTel(String Tel) {
                this.Tel = Tel;
            }

            public String getUserid() {
                return Userid;
            }

            public void setUserid(String Userid) {
                this.Userid = Userid;
            }
        }
    }
}
