package com.androidquanjiakan.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/29 0029.
 */
public class ServicePriceEntity implements Serializable{
    private ServicePriceDetailEntity expert;
    private ServicePriceDetailEntity director;
    private ServicePriceDetailEntity vicedirector;
    private ServicePriceDetailEntity phonedoctor;

    public ServicePriceDetailEntity getExpert() {
        return expert;
    }

    public void setExpert(ServicePriceDetailEntity expert) {
        this.expert = expert;
    }

    public ServicePriceDetailEntity getDirector() {
        return director;
    }

    public void setDirector(ServicePriceDetailEntity director) {
        this.director = director;
    }

    public ServicePriceDetailEntity getVicedirector() {
        return vicedirector;
    }

    public void setVicedirector(ServicePriceDetailEntity vicedirector) {
        this.vicedirector = vicedirector;
    }

    public ServicePriceDetailEntity getPhonedoctor() {
        return phonedoctor;
    }

    public void setPhonedoctor(ServicePriceDetailEntity phonedoctor) {
        this.phonedoctor = phonedoctor;
    }
}
