package com.androidquanjiakan.entity;

/**
 * 作者：Administrator on 2017/3/20 16:18
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class BabyCardEntity {

    /**
     * Results : {"Category":"Childrencard","Childrencard":{"Birthday":"2017-02-23 20:47","Gender":"男","Grade":"3","Icon":"icon","Name":"宝贝","Phonenumber":"15521410030","Relation":"妈妈","School":"清华"},"IMEI":"239307951255536007"}
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
         * Category : Childrencard
         * Childrencard : {"Birthday":"2017-02-23 20:47","Gender":"男","Grade":"3","Icon":"icon","Name":"宝贝","Phonenumber":"15521410030","Relation":"妈妈","School":"清华"}
         * IMEI : 239307951255536007
         */

        private String Category;
        private ChildrencardBean Childrencard;
        private String IMEI;

        public String getCategory() {
            return Category;
        }

        public void setCategory(String Category) {
            this.Category = Category;
        }

        public ChildrencardBean getChildrencard() {
            return Childrencard;
        }

        public void setChildrencard(ChildrencardBean Childrencard) {
            this.Childrencard = Childrencard;
        }

        public String getIMEI() {
            return IMEI;
        }

        public void setIMEI(String IMEI) {
            this.IMEI = IMEI;
        }

        public static class ChildrencardBean {
            /**
             * Birthday : 2017-02-23 20:47
             * Gender : 男
             * Grade : 3
             * Icon : icon
             * Name : 宝贝
             * Phonenumber : 15521410030
             * Relation : 妈妈
             * School : 清华
             */

            private String Birthday;
            private String Gender;
            private String Grade;
            private String Icon;
            private String Name;
            private String Phonenumber;
            private String Relation;
            private String School;

            public String getBirthday() {
                return Birthday;
            }

            public void setBirthday(String Birthday) {
                this.Birthday = Birthday;
            }

            public String getGender() {
                return Gender;
            }

            public void setGender(String Gender) {
                this.Gender = Gender;
            }

            public String getGrade() {
                return Grade;
            }

            public void setGrade(String Grade) {
                this.Grade = Grade;
            }

            public String getIcon() {
                return Icon;
            }

            public void setIcon(String Icon) {
                this.Icon = Icon;
            }

            public String getName() {
                return Name;
            }

            public void setName(String Name) {
                this.Name = Name;
            }

            public String getPhonenumber() {
                return Phonenumber;
            }

            public void setPhonenumber(String Phonenumber) {
                this.Phonenumber = Phonenumber;
            }

            public String getRelation() {
                return Relation;
            }

            public void setRelation(String Relation) {
                this.Relation = Relation;
            }

            public String getSchool() {
                return School;
            }

            public void setSchool(String School) {
                this.School = School;
            }
        }
    }
}
