package com.androidquanjiakan.util;

import com.wbj.ndk.natty.client.WGSTOGCJ02;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/5/17 0017.
 */

public class GPSToAMap {
    /**
     * 判断是否是有效的点
     * @param position
     * @return
     */
    public static boolean isVaild(String position){
        if(position!=null
                && position.length()>2
                && position.contains(",")
                && position.split(",").length==2){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 将GPS点转换为高德地图点
     *
     * @param position
     * @return
     */
    public static HashMap<String,Double> gpsToAmapPoint(String position){
        double lat = Double.parseDouble(position.split(",")[1]);
        double lon = Double.parseDouble(position.split(",")[0]);
                                    WGSTOGCJ02 mGCJ02 = new WGSTOGCJ02();
                            HashMap<String, Double> latlngMap = (HashMap<String, Double>) mGCJ02.transform
                                    (lon,lat);
        return latlngMap;
    }
}
