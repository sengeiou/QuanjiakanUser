package com.androidquanjiakan.util;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 作者：Administrator on 2017/4/24 16:56
 * <p>  用于过滤emoji 标点符号 等
 * 邮箱：liuzj@hi-board.com
 */


public class EditTextFilter {

    /**
     * 检测是否有emoji表情
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }


    /**
     * 判断是否是Emoji
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }



    /**
     * 禁止EditText输入空格
     * @param editText
     */
    public static void setEditTextInhibitInputSpace(EditText editText){
        InputFilter filter=new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if(source.equals(" "))return "";
                else return null;

            }
        };
        editText.setFilters(new InputFilter[]{filter});
    }

    /**
     * 检查是否输入了特殊字符，若后期需要屏蔽新字符可直接添加
     * @param tag
     * @return
     */
    public static boolean containsUnChar(String tag) {
        boolean isOk = false;
        String regex = "[-_——`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\s]";
        if (!TextUtils.isEmpty(tag)) {
            int length = tag.length();
            for (int i = 0; i < length; i++) {
                String ch = tag.substring(i, i + 1);
                if (ch.matches(regex)) {
                    isOk = true;
                    break;
                }
            }
        }
//        if (tag.matches(regex)) {
//            isOk = true;
//        }
        return isOk;
    }

    public static boolean containsSpace(String tag) {
        if (tag.contains(" ")) {
            return true;
        }else {
            return false;
        }
    }


    /**
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有：
     * 13+任意数
     * 15+除4的任意数
     * 18+任意数
     * 17+除9的任意数
     * 147
     */
    //判断是否是电话号码
    public static boolean isPhoneLegal(String str) throws PatternSyntaxException {
        String regExp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

}
