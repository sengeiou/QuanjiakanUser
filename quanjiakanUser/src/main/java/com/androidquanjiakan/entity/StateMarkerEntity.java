package com.androidquanjiakan.entity;

import com.amap.api.maps2d.model.LatLng;

/**
 * Created by Administrator on 2016/10/31 0031.
 */

public class StateMarkerEntity {
    private LatLng latLng;
    private String time;
    private String type;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
