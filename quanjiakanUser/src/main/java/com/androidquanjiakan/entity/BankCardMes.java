package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Gin on 2016/8/5.
 */
public class BankCardMes implements Serializable {



    private String card_no;
    private String bank;
    private String card_type;

    public String getCard_no() {
        return card_no;
    }

    public void setCard_no(String card_no) {
        this.card_no = card_no;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    @Override
    public String toString() {
        return "BankCardMes{" +
                "card_no='" + card_no + '\'' +
                ", bank='" + bank + '\'' +
                ", card_type='" + card_type + '\'' +
                '}';
    }
}
