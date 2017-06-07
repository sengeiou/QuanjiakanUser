package com.androidquanjiakan.util;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;

import com.androidquanjiakan.base.BaseApplication;

/**
 * 用于获取经纬度，两点间距离的工具类
 * Created by Administrator on 2016/11/4.
 */

public class LocationInfoUtil {
    public double latitude;
    public double longitude;
    private final double EARTH_RADIUS = 6378137.0;
    public Context context;
    public LocationInfoUtil(Context context){
        this.context = context;
    }
    public String getDistance(double lat,double lng){
        LocationManager locationManager  = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        LocationProvider gpsProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);//1.通过GPS定位，较精确，也比较耗电
        LocationProvider netProvider = locationManager.getProvider(LocationManager.NETWORK_PROVIDER);//2.通过网络定位，对定位精度度不高或省点情况可考虑使用
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //得到纬度
                latitude = location.getLatitude();
                //得到经度
                longitude = location.getLongitude();
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (netProvider != null || gpsProvider != null) {
           /*
        * 进行定位
            * provider:用于定位的locationProvider字符串:LocationManager.NETWORK_PROVIDER/LocationManager.GPS_PROVIDER
        * minTime:时间更新间隔，单位：ms
            * minDistance:位置刷新距离，单位：m
        * listener:用于定位更新的监听者locationListener
        */
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,100,listener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,100,listener);

        } else {
            //无法定位：1、提示用户打开定位服务；2、跳转到设置界面
            BaseApplication.getInstances().toast(context,"无法定位，请打开定位服务");
            Intent i = new Intent();
            i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(i);
        }
        double Lat1 = rad(latitude);
        double Lat2 = rad(lat);
        double a = Lat1 - Lat2;
        double b = rad(longitude) - rad(lng);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        LogUtil.e("**************"+s);
        String str = String.valueOf(s);
        String dis = str.substring(0,str.indexOf(".")+2);
        LogUtil.e("**************"+dis);
        return dis;

    }
    public double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public double getLatitude(){
        return latitude;

    }
    public double getLongitude(){
        return longitude;
    }

}
