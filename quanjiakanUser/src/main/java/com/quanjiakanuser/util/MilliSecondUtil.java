package com.quanjiakanuser.util;

/**
 * Created by Administrator on 2016/11/15.
 */

public class MilliSecondUtil {


    private static final MilliSecondUtil instance = new MilliSecondUtil();
    private MilliSecondUtil(){}
    public static MilliSecondUtil getInstance(){
        return instance;
    }

    public static final String formatTime_For_Millisecond(long ms){

        long minute = ms/(1000*60);
        long second = (ms % (1000 * 60)) / 1000;

        StringBuilder sb = new StringBuilder();
        if (minute>=0){
            sb.append(minute+"_");
        }
        if (second>=0){
            sb.append(second+"_");
        }

        return sb.toString();

    }
}
