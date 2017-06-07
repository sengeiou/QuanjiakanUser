package com.androidquanjiakan.util;

import java.text.DecimalFormat;

import static android.R.attr.format;

/**
 * Created by Administrator on 2016/11/23 0023.
 */

public class StringUtil {

    private static final String twoBitDecimalFormat = "####.##";

    public static String getTwoBitDecimalString(float number){
        DecimalFormat decimalFormat = new DecimalFormat(twoBitDecimalFormat);
        String result = decimalFormat.format(number);
        if(result.indexOf(".")>=0){
            if(1==(result.length()-result.indexOf("."))){
                return result+"00";
            }else if(2==(result.length()-result.indexOf("."))){
                return result+"0";
            }else{
                return result;
            }
        }else{
            return result+".00";
        }
    }

    public static String getDecimalString(float number,int bitNumber){
        String format = "######";
        for (int i = 0; i < bitNumber; i++) {
            if (i == 0) {
                format += ".#";
            } else {
                format += "#";
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat(format);
        return decimalFormat.format(number);
    }

    public static String getTwoBitDecimalString(double number){
        DecimalFormat decimalFormat = new DecimalFormat(twoBitDecimalFormat);
        String result = decimalFormat.format(number);
        if(result.indexOf(".")>=0){
            if(1==(result.length()-result.indexOf("."))){
                return result+"00";
            }else if(2==(result.length()-result.indexOf("."))){
                return result+"0";
            }else{
                return result;
            }
        }else{
            return result+".00";
        }
    }

    public static String getDecimalString(double number,int bitNumber){
        String format = "######";
        for (int i = 0; i < bitNumber; i++) {
            if (i == 0) {
                format += ".#";
            } else {
                format += "#";
            }
        }
        DecimalFormat decimalFormat = new DecimalFormat(format);
        return decimalFormat.format(number);
    }
}
