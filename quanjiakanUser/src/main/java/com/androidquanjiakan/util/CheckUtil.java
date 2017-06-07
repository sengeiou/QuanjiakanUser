package com.androidquanjiakan.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/6/14 0014.
 */
public class CheckUtil {

    public static final String mobileRegex = "^1\\d{10}$";
    public static final String char_ChineseRegex = "[\\u4e00-\\u9fa5]+";
    public static final String charRegex = "[\\u4e00-\\u9fa5a-zA-Z0-9]+";
    public static final String numberRegex = "^[0-9]+$";
    public static final String emailRegex = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    public static final boolean isEmpty(String data){
        if(data==null || data.length()<1){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 校验手机号码
     *
     * @param phoneNumber
     * @return
     */
    public static final boolean isPhoneNumber(String phoneNumber){
        try{
            if(isEmpty(phoneNumber)){
                return false;
            }
            Pattern pattern = Pattern.compile(mobileRegex);
            Matcher matcher = pattern.matcher(phoneNumber);
            return matcher.matches();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 完全由中文字符组成
     *
     * @param phoneNumber
     * @return
     */
    public static final boolean isAllChineseChar(String phoneNumber){
        try{
            if(isEmpty(phoneNumber)){
                return false;
            }
            Pattern pattern = Pattern.compile(char_ChineseRegex);
            Matcher matcher = pattern.matcher(phoneNumber);
            return matcher.matches();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static final boolean isNumberChar(String number){
        try{
            if(isEmpty(number)){
                return false;
            }
            Pattern pattern = Pattern.compile(numberRegex);
            Matcher matcher = pattern.matcher(number);
            return matcher.matches();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 一个或多个中文字符、英文、数字【不含空格】
     *
     * @param phoneNumber
     * @return
     */
    public static final boolean isChar(String phoneNumber){
        try{
            if(isEmpty(phoneNumber)){
                return false;
            }
            Pattern pattern = Pattern.compile(charRegex);
            Matcher matcher = pattern.matcher(phoneNumber);
            return matcher.matches();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static final boolean isEmail(String email){
        try{
            if(isEmpty(email)){
                return false;
            }
            Pattern pattern = Pattern.compile(emailRegex);
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private static Pattern patternW;
    private static Matcher matcherW;
    private static Pattern patternN;
    private static Matcher matcherN;
    private static Pattern patternC;
    private static Matcher matcherC;
    private static int typeNumberW = 0;
    private static int typeNumberN = 0;
    private static int typeNumberC = 0;
    private static final String word = "[a-zA-Z]+";
    private static final String number = "[0-9]+";
    private static final String specificCharacter = "((?=[\\x21-\\x7e]+)[^A-Za-z0-9])";

    /**
     * 至少含有字母、数字、英文特殊字符中的两种，才能通过校验
     *
     * @param string
     * @return
     */
    public static boolean checkStringType(String string){
        if(string==null || string.length()<1){
            return false;
        }
        typeNumberW = 0;
        typeNumberN = 0;
        typeNumberC = 0;
        patternW = Pattern.compile(word);
        patternN = Pattern.compile(number);
        patternC = Pattern.compile(specificCharacter);
        for(char charact : string.toCharArray()){
            matcherW = patternW.matcher(charact+"");
            matcherN = patternN.matcher(charact+"");
            matcherC = patternC.matcher(charact+"");
            if(matcherW.matches() || matcherN.matches() || matcherC.matches()){
                if(matcherW.matches() && typeNumberW<1){
                    typeNumberW = 1;
                }
                if(matcherN.matches() && typeNumberN<1){
                    typeNumberN = 1;
                }
                if(matcherC.matches() && typeNumberC<1){
                    typeNumberC = 1;
                }
                continue;
            }else{
                typeNumberW = 0;
                typeNumberN = 0;
                typeNumberC = 0;
                break;
            }
        }

        if((typeNumberW+typeNumberN+typeNumberC)>1){
            return true;
        }
        return false;
    }
}
