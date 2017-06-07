package com.androidquanjiakan.util;

/**
 * 作者：Administrator on 2017/3/18 15:51
 * <p>  unicode转码工具类
 * 邮箱：liuzj@hi-board.com
 */


public class UnicodeUtil {

    /**
     * <p>转为unicode 编码<p>
     * @param
     * @return unicodeString
     */
    public static String encode(String str) {
        String prifix = "\\u";
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            String code = prifix + format(Integer.toHexString(c));
            unicode.append(code);
        }
        return unicode.toString();
    }

    /**
     * <p>unicode 解码<p>
     * @param unicode
     * @return originalString
     */
    public static String decode(String unicode) {
        StringBuffer str = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            int data = Integer.parseInt(hex[i], 16);
            str.append((char) data);
        }
        return str.length() > 0 ? str.toString() : unicode;
    }

    /**
     * <p>转为数据库查询编码<p>
     * 向数据库查询时,\\u需要转为%
     * @param str
     * @return
     */
    public static String encodeDBSearchParam(String str) {
        str = encode(str);
        str = str.replace("\\", "%");
        return str;
    }

    /**
     * <p>解码数据库查询参数<p>
     * 数据库查询后,回传参数% 转回\\u
     * @param str
     * @return
     */
    public static String decodeDBSearchParam(String str) {
        str = str.replace("%", "\\");
        str = decode(str);
        return str;
    }

    /**
     * 为长度不足4位的unicode 值补零
     * @param str
     * @return
     */
    private static String format(String str) {
        for ( int i=0, l=4-str.length(); i<l; i++ )
            str = "0" + str;
        return str;
    }

}
