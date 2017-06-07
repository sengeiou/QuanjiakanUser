package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
public class ServicePriceDetailEntity implements Serializable{
    private int week;
    private int month;
    private int quarter;
    private int halfyear;
    private int year;

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getQuarter() {
        return quarter;
    }

    public void setQuarter(int quarter) {
        this.quarter = quarter;
    }

    public int getHalfyear() {
        return halfyear;
    }

    public void setHalfyear(int halfyear) {
        this.halfyear = halfyear;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
