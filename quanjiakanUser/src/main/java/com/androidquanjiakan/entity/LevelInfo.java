package com.androidquanjiakan.entity;

/**
 * 作者：Administrator on 2017/1/18 15:34
 * <p>
 * 邮箱：liuzj@hi-board.com
 */

public class LevelInfo {

    /**
     * code : 200
     * message : 返回成功
     * object : {"currentGradeExperience":2,"ebeans":999955757,"ebeansConvertWallet":8.9996016E7,"experience":2,"grade":1,"money":0,"nextGradeExperience":998,"recharge":0,"totalGive":44252,"totalRecipient":10}
     */

    private String code;
    private String message;
    private ObjectBean object;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ObjectBean getObject() {
        return object;
    }

    public void setObject(ObjectBean object) {
        this.object = object;
    }

    public static class ObjectBean {
        /**
         * currentGradeExperience : 2
         * ebeans : 999955757
         * ebeansConvertWallet : 8.9996016E7
         * experience : 2
         * grade : 1
         * money : 0
         * nextGradeExperience : 998
         * recharge : 0
         * totalGive : 44252
         * totalRecipient : 10
         */

        private int currentGradeExperience;
        private int ebeans;
        private double ebeansConvertWallet;
        private int experience;
        private int grade;
        private int money;
        private int nextGradeExperience;
        private int recharge;
        private int totalGive;
        private int totalRecipient;

        public int getCurrentGradeExperience() {
            return currentGradeExperience;
        }

        public void setCurrentGradeExperience(int currentGradeExperience) {
            this.currentGradeExperience = currentGradeExperience;
        }

        public int getEbeans() {
            return ebeans;
        }

        public void setEbeans(int ebeans) {
            this.ebeans = ebeans;
        }

        public double getEbeansConvertWallet() {
            return ebeansConvertWallet;
        }

        public void setEbeansConvertWallet(double ebeansConvertWallet) {
            this.ebeansConvertWallet = ebeansConvertWallet;
        }

        public int getExperience() {
            return experience;
        }

        public void setExperience(int experience) {
            this.experience = experience;
        }

        public int getGrade() {
            return grade;
        }

        public void setGrade(int grade) {
            this.grade = grade;
        }

        public int getMoney() {
            return money;
        }

        public void setMoney(int money) {
            this.money = money;
        }

        public int getNextGradeExperience() {
            return nextGradeExperience;
        }

        public void setNextGradeExperience(int nextGradeExperience) {
            this.nextGradeExperience = nextGradeExperience;
        }

        public int getRecharge() {
            return recharge;
        }

        public void setRecharge(int recharge) {
            this.recharge = recharge;
        }

        public int getTotalGive() {
            return totalGive;
        }

        public void setTotalGive(int totalGive) {
            this.totalGive = totalGive;
        }

        public int getTotalRecipient() {
            return totalRecipient;
        }

        public void setTotalRecipient(int totalRecipient) {
            this.totalRecipient = totalRecipient;
        }
    }

    @Override
    public String toString() {
        return "LevelInfo{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", object=" + object +
                '}';
    }
}
