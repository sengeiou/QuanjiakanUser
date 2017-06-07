package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/23 0023.
 */
public class HealthDataEntity implements Serializable{
    private int temp;
    private int beat;
    private int step;
    private int calorie;
    private String lat_lng;
    private String step_array;
    private String battery_watch_power;
    private int battery_device_power;

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getBeat() {
        return beat;
    }

    public void setBeat(int beat) {
        this.beat = beat;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }

    public String getLat_lng() {
        return lat_lng;
    }

    public void setLat_lng(String lat_lng) {
        this.lat_lng = lat_lng;
    }

    public String getStep_array() {
        return step_array;
    }

    public void setStep_array(String step_array) {
        this.step_array = step_array;
    }

    public String getBattery_watch_power() {
        return battery_watch_power;
    }

    public void setBattery_watch_power(String battery_watch_power) {
        this.battery_watch_power = battery_watch_power;
    }

    public int getBattery_device_power() {
        return battery_device_power;
    }

    public void setBattery_device_power(int battery_device_power) {
        this.battery_device_power = battery_device_power;
    }

    public int getStepArrayNumber(){
        return step_array.split(",").length;
    }

    public int[] getStepArrayIntValue(){
        String[] value = step_array.split(",");
        int[] intValue = new int[value.length];
        for(int i = 0;i<value.length;i++){
            intValue[i] = Integer.parseInt(value[i]);
        }
        return intValue;
    }

    public double getLatitude(){
        String latitude = lat_lng.split("\\|")[0];
        return Double.parseDouble(latitude);
    }

    public double getLongitude(){
        String latitude = lat_lng.split("\\|")[1];
        return Double.parseDouble(latitude);
    }

}
