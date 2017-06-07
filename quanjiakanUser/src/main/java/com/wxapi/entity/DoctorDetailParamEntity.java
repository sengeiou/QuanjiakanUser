package com.wxapi.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/7/25 0025.
 */
public class DoctorDetailParamEntity implements Serializable{
    private String doctorID;

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }
}
