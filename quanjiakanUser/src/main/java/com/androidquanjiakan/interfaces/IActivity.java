package com.androidquanjiakan.interfaces;

/**
 * Created by Administrator on 2017/4/6 0006.
 */

public interface IActivity {
    Object getParamter(int type);
    void showMyDialog(int type);
    void dismissMyDialog(int type);
    void onSuccess(int type,int httpResponseCode,Object result);
    void onError(int type,int httpResponseCode,Object errorMsg);
}

