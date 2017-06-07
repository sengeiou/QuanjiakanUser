package com.androidquanjiakan.business;

import com.amap.api.maps2d.model.LatLng;

import java.util.List;

/**
 * Created by Administrator on 2017/3/16 0016.
 */

public class EfenceUtil {
    public static String ListPointToString(List<LatLng> list) {
        StringBuilder stringBuilder = new StringBuilder();
        if (list != null && list.size() > 0) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                LatLng temp = list.get(i);
                if (i < size - 1) {
                    stringBuilder.append(temp.latitude + "," + temp.longitude + ";");
                } else {
                    stringBuilder.append(temp.latitude + "," + temp.longitude);
                }
            }
        }
        return stringBuilder.toString();
    }
}
