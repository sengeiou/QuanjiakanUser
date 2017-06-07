package com.androidquanjiakan.util;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * 基于高德地图获取当前位置经纬度，计算两点距离
 * Created by Administrator on 2016/11/14.
 */

public class AmapLocationUtil {
    //经纬度
    private static double latitude;//纬度

    public static double getLatitude() {
        return latitude;
    }

    public static double getLongitude() {
        return longitude;
    }

    private static double longitude;//经度
//    private static final double EARTH_RADIUS = 6378137.0;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {

            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    latitude = aMapLocation.getLatitude();
                    longitude = aMapLocation.getLongitude();


                }else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    LogUtil.e("AmapError","location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }


        }
    };
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;


    public AmapLocationUtil(Context context){
        //初始化定位
        mLocationClient = new AMapLocationClient(context);
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();

        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();


    }

    /*public static String getDistance(double lat,double lng){
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
    }*/

    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 根据两个位置的经纬度，来计算两地的距离（单位为KM）
     * 参数为String类型
     */
    public static String getDistance(String lat1Str, String lng1Str) {
        Double lat1 = Double.parseDouble(lat1Str);
        Double lng1 = Double.parseDouble(lng1Str);
        double radLat1 = rad(lat1);
        double radLat2 = rad(latitude);
        double difference = radLat1 - radLat2;
        double mdifference = rad(lng1) - rad(longitude);
        double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(difference / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(mdifference / 2), 2)));
        distance = distance * EARTH_RADIUS;
        distance = Math.round(distance * 10000) / 10000;
        String distanceStr = distance+"";
        distanceStr = distanceStr.
                substring(0, distanceStr.indexOf(".")+2);//保留小数点后一位

        return distanceStr;
    }

}
