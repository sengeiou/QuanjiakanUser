package com.quanjiakanuser.http;

import android.app.Dialog;
import android.os.AsyncTask;

import com.androidquanjiakan.util.LogUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class MyAsyncTask extends AsyncTask<Task, Integer, String>{
	Dialog dialog;
	Task task ;
	int type ;
	Object params;
	String urlString;
	HttpResponseInterface aty;
	
	public MyAsyncTask(Task task){
		this.task = task;
		dialog = task.getDialog();
		params = task.getParams();
		type = task.getTaskType();
		aty = task.getInterface();
		urlString = task.getURL();
	}
	
	@Override
	protected String doInBackground(Task...arg0) {
		// TODO Auto-generated method stub		
		String rst = "";
		//panduan task type
		if (type == Task.TYPE_GET_STRING_NOCACHE) {
			rst = HttpManager.getRequest(urlString);
			LogUtil.e("---TYPE_GET_STRING_NOCACHE" +
					"\n"+urlString+"" +
					"\nRequest Result:"+rst);
		}else if(type == Task.TYPE_POST_DATA_PARAMS){
			// post data with params;
			rst = HttpManager.postData((HashMap<String,Object>)params, urlString);
			LogUtil.e("---TYPE_POST_DATA_PARAMS" +
							"\n"+urlString+"" +
							"\nParams:"+new Gson().toJson((HashMap<String,Object>)params)+"" +
							"\nRequest Result:"+rst);
		}else if(type == Task.TYPE_POST_FILE){
			HashMap<String, String> p = (HashMap<String, String>)params;
			/**
			 * 新
			 */
//			rst = HttpManager.uploadFile(urlString, p/*.get("file").toString()*/, task.getProgressChangeListener(),p.get("filename"));
			/**
			 * 旧
			 */
			rst = HttpManager.postFile(urlString, p/*.get("file").toString()*/, task.getProgressChangeListener(),p.get("filename"));
			LogUtil.e("---TYPE_POST_FILE" +
					"\n"+urlString+"" +
					"\nParams:"+new Gson().toJson(p)+"" +
					"\nRequest Result:"+rst);
		}else if (type == Task.TYPE_MULTIPART_DATA_PARAMS) {
			rst = HttpManager.postMultiPartParams((HashMap<String,Object>)params, urlString);
			LogUtil.e("---TYPE_MULTIPART_DATA_PARAMS" +
					"\n"+urlString+"" +
					"\nParams:"+new Gson().toJson((HashMap<String,Object>)params)+"" +
					"\nRequest Result:"+rst);
		}		
		return rst;
	}	

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
//		InfoPrinter.printLog("PostExecute:"+result);
		HttpResponseInterface activity = (HttpResponseInterface)task.getInterface();
		dismissDialog();
		activity.handMsg(result);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}
	
	protected void dismissDialog(){
		if(dialog != null && dialog.isShowing()){
			try {
				dialog.dismiss();				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}	
}
