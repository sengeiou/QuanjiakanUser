package com.androidquanjiakan.entity_util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * 解析json
 * Created by Administrator on 2016/6/14 0014.
 */
public class SerialUtil {
    private static final Gson gson = new Gson();

    /**
     *
     * @param jsonData
     * @param classes new TypeToken<T>(){}.getType();
     * @return T需要进行强制转换
     */
    public static Object jsonToObject(String jsonData, Type classes){
        return gson.fromJson(jsonData,classes);
    }

}
