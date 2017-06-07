package com.androidquanjiakan.activity.setting.contact.bean;

/**
 * 作者：Administrator on 2017/3/14 17:55
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class AddContactBean {

    /**
     * Action : Add
     * Category : Contacts
     * Contacts : {"Image":"1","Name":"爷爷","Tel":"13838384383"}
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
         * Image : 1
         * Name : 爷爷
         * Tel : 13838384383
         */

        private String Image;
        private String Name;
        private String Tel;

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
    }
}
