package com.androidquanjiakan.activity.setting.contact.bean;

/**
 * 作者：Administrator on 2017/3/14 19:54
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class UpdateContactBean {


    /**
     * Action : Update
     * Category : Contacts
     * Contacts : {"Tel ":"13838384383","Admin":"1","App":"1","Id":"24137","Image":"http://picture.quanjiakan.com:9080/quanjiakan/resources/doctor/20170309150430_2esdw5lvzex7kqik30ts.png","Name":"妈妈"}
     * IMEI : 355637052788650
     */

    private String Action;
    private String Category;
    private ContactBean Contacts;
    private String IMEI;

    public String getAction() {
        return Action;
    }

    public void setAction(String Action) {
        this.Action = Action;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String Category) {
        this.Category = Category;
    }

    public ContactBean getContact() {
        return Contacts;
    }

    public void setContact(ContactBean Contact) {
        this.Contacts = Contact;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public static class ContactBean {
        /**
         * Tel  : 13838384383
         * Admin : 1
         * App : 1
         * Id : 24137
         * Image : http://picture.quanjiakan.com:9080/quanjiakan/resources/doctor/20170309150430_2esdw5lvzex7kqik30ts.png
         * Name : 妈妈
         */

        private String Tel;
        private String Admin;
        private String App;
        private String Id;
        private String Image;
        private String Name;

        public String getTel() {
            return Tel;
        }

        public void setTel(String Tel) {
            this.Tel = Tel;
        }

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
    }
}
