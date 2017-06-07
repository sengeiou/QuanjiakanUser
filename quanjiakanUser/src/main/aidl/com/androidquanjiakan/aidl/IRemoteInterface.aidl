// IRemoteInterface.aidl
package com.androidquanjiakan.aidl;

// Declare any non-default types here with import statements

interface IRemoteInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.    ----- include Integer,Long,Char、List、Map、Parcelable
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);
    boolean checkAPP( String appPackageName);
}
