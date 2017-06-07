package com.androidquanjiakan.entity;

import com.amap.api.maps2d.model.LatLng;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/2/22 0022.
 */

public class DeviceContainerEntity implements Serializable {
    private String name;
    private LatLng latLng;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
