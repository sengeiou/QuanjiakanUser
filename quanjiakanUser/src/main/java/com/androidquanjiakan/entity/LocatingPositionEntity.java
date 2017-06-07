package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/8 0008.
 */
public class LocatingPositionEntity implements Serializable {
    private double latitude;
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "LocatingPositionEntity{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
