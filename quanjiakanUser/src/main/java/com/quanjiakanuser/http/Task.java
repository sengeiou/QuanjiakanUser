package com.quanjiakanuser.http;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quanjiakanuser.http.HttpManager.ProgressChangeListener;
import android.app.Dialog;

public class Task {
	
	public static final int TYPE_GET_STRING_NOCACHE = 0;
	public static final int TYPE_GET_SPK_NOCACHE = 4;
	public static final int TYPE_POST_DATA_PARAMS = 7;
	public static final int TYPE_POST_FILE=8;
	public static final int TYPE_MULTIPART_DATA_PARAMS=9;	
	public static final int TYPE_GET_VOICE_FILE=10;	
	
	private HttpResponseInterface itfc;
	private String url;
	private Object params;
	private int tasktype;
	private Dialog dialog;
	private ProgressChangeListener progressChangeListener;
	
	
	/**
	 * @param aty 
	 * @param url
	 * @param params
	 * @param type 0 get string|nocache ,1 get snd nocache,2 get string|cache , 
	 * 3 get snd|cache; 4��get spk nocache ,5 get spk cache ,6 post seriable obj,7 post data with params,
	 * @param dialog
	 */
	public Task(HttpResponseInterface aty,String url,Object params,int type,Dialog dialog){

		this.itfc = aty;
		this.url = url;
		this.params = params;
		this.tasktype = type;
		this.dialog = dialog;
		if(params != null){
			if(params instanceof String){
				this.params = string2Map(params.toString());
			}else if (params instanceof Map) {
				this.params = params;
			}else if (params instanceof JsonObject) {
				this.params = Json2Map((JsonObject)params);
			}else {
				this.params = string2Map(new Gson().toJson(params));
			}
		}
	}
	
//	public Task(HttpResponseInterface aty,String url,JsonObject param,int type,Dialog dialog){
//		this.itfc = aty;
//		this.url = url;
//		this.params = Json2Map(param);
//		this.tasktype = type;
//		this.dialog = dialog;
//	}
//	
//	public Task(HttpResponseInterface aty,String url,String param,int type,Dialog dialog){
//		this.itfc = aty;
//		this.url = url;
//		this.params = string2Map(param);
//		this.tasktype = type;
//		this.dialog = dialog;
//	}
	
	protected HashMap<String, String> Json2Map(JsonObject object){
		HashMap<String, String> map = new HashMap<>();
		if(object != null){
			map.put("param", object.toString());
		}
		return map;
	}
	
	protected HashMap<String, String> string2Map(String object){
		HashMap<String, String> map = new HashMap<>();
		if(object != null){
			map.put("param", object);
		}
		return map;
	}
	
	public HttpResponseInterface getInterface(){
		return this.itfc;
	}
	
	public void setInterface(HttpResponseInterface itfc){
		this.itfc = itfc;
	}
	
	public String getURL(){
		return this.url;
	}
	
	public void setURL(String URL){
		this.url = URL;
	}
	
	public Object getParams(){
		return this.params;
	}
	public void setParams(HashMap<String,Object> params){
		this.params = params;
	}
	
	public int getTaskType(){
		return this.tasktype;
	}
	
	public void setTaskType(int type){
		this.tasktype = type;
	}
	
	public Dialog getDialog(){
		return this.dialog ;
	}
	public void setDialog(Dialog dialog){
		this.dialog = dialog;
	}

	public ProgressChangeListener getProgressChangeListener() {
		return progressChangeListener;
	}

	public void setProgressChangeListener(
			ProgressChangeListener progressChangeListener) {
		this.progressChangeListener = progressChangeListener;
	}
	
	

}
