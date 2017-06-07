package com.quanjiakanuser.http;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.entity_util.NetUtil;
import com.quanjiakan.main.R;

public class MyHandler {
	
	@SuppressLint("NewApi")
	public static void putTask(Task task){
//		InfoPrinter.printLog(task.getURL());
		if(!NetUtil.isNetworkAvailable(BaseApplication.getInstances())){
//			BaseApplication.getInstances().toast("网络连接不可用!");
			if(task!=null && task.getDialog()!=null){
				task.getDialog().dismiss();
			}
			if(task!=null && task.getInterface()!=null){
				task.getInterface().handMsg(null);
			}

			final Dialog dialog = new Dialog(BaseApplication.getInstances());
			View view = LayoutInflater.from(BaseApplication.getInstances()).inflate(R.layout.dialog_common_confirm,null);

			TextView title = (TextView) view.findViewById(R.id.tv_title);
			title.setText("提示");
			TextView content = (TextView) view.findViewById(R.id.tv_content);
			content.setText("网络连接不可用，是否前去设置?");

			TextView btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);
			TextView btn_confirm = (TextView) view.findViewById(R.id.btn_confirm);
			btn_cancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			btn_confirm.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					/**
					 * 跳转到设置页面
					 */
					dialog.dismiss();
					Intent intent =  new Intent(Settings.ACTION_WIFI_SETTINGS);
					BaseApplication.getInstances().startActivity(intent);
				}
			});
			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
			lp.width = QuanjiakanUtil.dip2px(BaseApplication.getInstances(), 300);
			lp.height = lp.WRAP_CONTENT;
			lp.gravity = Gravity.CENTER;
			dialog.setContentView(view, lp);
			dialog.show();

		}else{
			MyAsyncTask myAsync = new MyAsyncTask(task);
			myAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, task);
		}
	}


	@SuppressLint("NewApi")
	public static synchronized void putTask(final Activity activity,Task task){
//		InfoPrinter.printLog(task.getURL());
		if(!NetUtil.isNetworkAvailable(BaseApplication.getInstances())){
			BaseApplication.getInstances().toast(activity,"网络连接不可用!");
			if(task!=null && task.getDialog()!=null){
				task.getDialog().dismiss();
			}
			if(task!=null && task.getInterface()!=null){
				task.getInterface().handMsg(null);
			}
			/**
			 * 可跳转到WIFI设置的Dialog
			 */
//			final Dialog dialog = QuanjiakanDialog.getInstance().getDialog(activity);
//			View view = LayoutInflater.from(activity).inflate(R.layout.dialog_common_confirm,null);
//
//			TextView title = (TextView) view.findViewById(R.id.tv_title);
//			title.setText("提示");
//			TextView content = (TextView) view.findViewById(R.id.tv_content);
//			content.setText("网络连接不可用，是否前去设置?");
//
//			TextView btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);
//			TextView btn_confirm = (TextView) view.findViewById(R.id.btn_confirm);
//			btn_cancel.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					dialog.dismiss();
//				}
//			});
//			btn_confirm.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					/**
//					 * 跳转到设置页面
//					 */
//					dialog.dismiss();
//					Intent intent =  new Intent(Settings.ACTION_WIFI_SETTINGS);
//					activity.startActivity(intent);
//				}
//			});
//			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//			lp.width = QuanjiakanUtil.dip2px(BaseApplication.getInstances(), 300);
//			lp.height = lp.WRAP_CONTENT;
//			lp.gravity = Gravity.CENTER;
//			dialog.setContentView(view, lp);
//			dialog.show();

		}else{
			MyAsyncTask myAsync = new MyAsyncTask(task);
			myAsync.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, task);
		}
	}
}
