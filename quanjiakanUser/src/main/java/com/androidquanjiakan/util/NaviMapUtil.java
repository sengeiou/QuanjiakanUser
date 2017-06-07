package com.androidquanjiakan.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gin on 2016/9/2.
 */
public class NaviMapUtil {

    //判断哪个应用是否已安装
    public static boolean isAvilible(Context context, String packageName){
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if(packageInfos != null){
            for(int i = 0; i < packageInfos.size(); i++){
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }


    //调用高德地图路径规划
    public static void GotoGaoDeNaviMap(Context context,String sourceApplication,String slat,String slon,String sname,String dlat,String dlot,String dname,String dev,String m,String t){
        StringBuffer stringBuffer  = new StringBuffer("androidamap://route?sourceApplication=")
                .append(sourceApplication);

        stringBuffer.append("&slat=").append(slat)
                    .append("&slon=").append(slon)
                    .append("&sname=").append(sname)
                    .append("&dlat=").append(dlat)
                    .append("&dlon=").append(dlot)
                    .append("&dname=").append(dname)
                    .append("&dev=").append(dev)
                    .append("&m=").append(m)
                    .append("&t=").append(t);


        Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse(stringBuffer.toString()));
        intent.setPackage("com.autonavi.minimap");
        context.startActivity(intent);

    }

    //调用百度地图导航(路径规划)
    public static  void GotoBaiDuNaviMap(Context context,String origin , String destination  ,String mode ,String region ,String origin_region ,String destination_region
            ,String coord_type , String zoom , String src){
        StringBuffer stringBuffer  = new StringBuffer("intent://map/direction?origin=");
        stringBuffer.append(origin)
                .append("&destination=").append(destination)
                .append("&mode=").append(mode);
        if (!TextUtils.isEmpty(region)){
            stringBuffer.append("&region=").append(region);
        }
        if (!TextUtils.isEmpty(origin_region)){
            stringBuffer.append("&origin_region=").append(origin_region);
        }
        if (!TextUtils.isEmpty(destination_region)){
            stringBuffer.append("&destination_region=").append(destination_region);
        }
        if (!TextUtils.isEmpty(coord_type)){
            stringBuffer.append("&coord_type=").append(coord_type);
        }


        if (!TextUtils.isEmpty(zoom)){
            stringBuffer.append("&zoom=").append(zoom);
        }
        stringBuffer.append("&src=").append(src).append("#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
        String intentString = stringBuffer.toString();
        try {
            Intent intent  = Intent.getIntent(intentString);
            context.startActivity(intent);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }
}
