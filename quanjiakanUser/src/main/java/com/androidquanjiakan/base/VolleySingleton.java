package com.androidquanjiakan.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by Administrator on 2016/9/27 0027.
 *
 */
public class VolleySingleton {
    /*
    StringRequest stringRequest = new StringRequest(Request.Method.GET,"https://www.baidu.com",new Listener<String>(){

            @Override
            public void onResponse(String s) {
                //打印请求返回结果
                Log.e("volley",s);
            }
        },new ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("volleyerror","erro2");
            }
        });
//将StringRequest对象添加进RequestQueue请求队列中
   VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(stringRequest);
   -------------------------------------------
   StringRequest stringRequest = new StringRequest(Method.POST, url,  listener, errorListener) {
 @Override
 protected Map<String, String> getParams() throws AuthFailureError {
 Map<String, String> map = new HashMap<String, String>();
 map.put("params1", "value1");
 map.put("params2", "value2");
 return map;
 }
 };
 -----------------------------------------------
 JsonObjectRequest jr = new JsonObjectRequest(Request.Method.GET,url,null,new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("volley",jsonObject.toString());
            }
        },new ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("volleyerror","erro");
            }
        });
 VolleySingleton.getVolleySingleton(this.getApplicationContext()).addToRequestQueue(jr);
     */
    private static VolleySingleton volleySingleton;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private Context mContext;
    public VolleySingleton(Context context) {
        this.mContext = context;
        mRequestQueue = getRequestQueue();
        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache(){
                    private final LruCache<String,Bitmap> cache = new LruCache<String ,Bitmap>(20);
                    @Override
                    public Bitmap getBitmap(String url){
                        return cache.get(url);
                    }
                    @Override
                    public void putBitmap(String url,Bitmap bitmap){
                        cache.put(url,bitmap);
                    }
                });
    }
    public static synchronized VolleySingleton getVolleySingleton(Context context){
        if(volleySingleton == null){
            volleySingleton = new VolleySingleton(context);
        }
        return volleySingleton;
    }
    public RequestQueue getRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}