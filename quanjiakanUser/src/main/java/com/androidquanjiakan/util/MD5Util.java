package com.androidquanjiakan.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Ivan
 * @version 1.0
 * @briefMD5�����࣬�ṩ�ַ�MD5���ܣ�У�飩���ļ�MD5ֵ��ȡ��У�飩���ܡ� 
 */


public class MD5Util 
{ 
    /** 
     * 16�����ַ� 
     */ 
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'}; 
 
    /** 
     * ָ���㷨ΪMD5��MessageDigest 
     */ 
    private static MessageDigest messageDigest = null; 
 
    /** 
     * ��ʼ��messageDigest�ļ����㷨ΪMD5 
     */ 
    static 
    { 
        try 
        { 
            messageDigest = MessageDigest.getInstance("MD5"); 
        } 
        catch(NoSuchAlgorithmException e) 
        { 
            e.printStackTrace(); 
        } 
    } 
 
    
    /** 
     * MD5�����ַ� 
     * @param str Ŀ���ַ� 
     * @return MD5���ܺ���ַ� 
     */ 
    public static String getMD5String(String str) 
    { 
        return getMD5String(str.getBytes()); 
    } 
    
    public static String MD5Encode(String origin) {
        String resultString = null;
        try {
            resultString = origin;
            MessageDigest md = MessageDigest.getInstance("MD5");
            //resultString = byteArrayToHexString(md.digest(resultString.getBytes())); ///深坑啊，赶紧注释掉，用UTF-8
            resultString = bytesToHex(md.digest(resultString.getBytes("utf-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }
 
    /** 
     * MD5������byte�����ʾ���ַ� 
     * @param bytes Ŀ��byte���� 
     * @return MD5���ܺ���ַ� 
     */ 
    public static String getMD5String(byte[] bytes) 
    { 
        messageDigest.update(bytes); 
        return bytesToHex(messageDigest.digest()); 
    } 
     
   
  
 
    /** 
     * ���ֽ�����ת����16�����ַ� 
     * @param bytes Ŀ���ֽ����� 
     * @return ת����� 
     */ 
    public static String bytesToHex(byte bytes[]) 
    { 
        return bytesToHex(bytes, 0, bytes.length); 
    } 
 
    /** 
     * ���ֽ�������ָ������������ת����16�����ַ� 
     * @param bytes Ŀ���ֽ����� 
     * @param start ��ʼλ�ã�������λ�ã� 
     * @param end ����λ�ã���������λ�ã� 
     * @return ת����� 
     */ 
    public static String bytesToHex(byte bytes[], int start, int end) 
    { 
        StringBuilder sb = new StringBuilder(); 
 
        for(int i = start; i < start + end; i++) 
        { 
            sb.append(byteToHex(bytes[i])); 
        } 
 
        return sb.toString(); 
    } 
 
    /** 
     * �������ֽ���ת����16�����ַ� 
     * @param bt Ŀ���ֽ� 
     * @return ת����� 
     */ 
    public static String byteToHex(byte bt) 
    { 
        return HEX_DIGITS[(bt & 0xf0) >> 4] + "" + HEX_DIGITS[bt & 0xf]; 
    } 
 
  
}  
