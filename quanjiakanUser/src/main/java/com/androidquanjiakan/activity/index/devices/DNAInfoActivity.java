package com.androidquanjiakan.activity.index.devices;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.FileDownloaderUtil;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.entity.BaseHttpResultEntity_List;
import com.androidquanjiakan.entity.DNAInfoEntity;
import com.androidquanjiakan.entity.DNAInfoEntity_new;
import com.androidquanjiakan.entity.VersionInfoEntity;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.interfaces.IDownloadCallback;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.util.MultiThreadAsyncTask_WordFile;
import com.google.gson.reflect.TypeToken;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class DNAInfoActivity extends BaseActivity implements View.OnClickListener{

	private TextView tv_submit;
	private String device_id;
	private ImageButton ibtn_back;
	private TextView tv_title;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_device_dna_info);
		BaseApplication.getInstances().setCurrentActivity(this);
		device_id = getIntent().getStringExtra("device_id");
		if(device_id==null){
			BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(),"传入参数异常!");
			finish();
			return;
		}
		ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
		ibtn_back.setVisibility(View.VISIBLE);
		ibtn_back.setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("下载");

		tv_submit = (TextView) findViewById(R.id.tv_submit);
		tv_submit.setOnClickListener(this);
		getDNA_FilePath();
	}

	/**
	 * http://192.168.0.140:8080/quanjiakan/api?m=DNA&a=findbymemberid&memberId=11119
	 *
	 * TODO 获取指定设备的文档连接地址
     */
	private List<DNAInfoEntity> dataList;
	private DNAInfoEntity_new data;
	public void getDNA_FilePath(){
		HashMap<String,String> params = new HashMap<>();
//		params.put("memberId",BaseApplication.getInstances().getUser_id());//TODO Old Net Interface parameter
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				/**
				 * "id": "0",
				 "title": "123",
				 "url": "http://dna.quanjiakan.com:6080/quanjiakan/resources/dna/20161116163527_dujx37.docx",
				 "createTime": "1479285231369",
				 "IMEI": "240207489164313140"
				 */
//				if(val!=null && val.length()>0 && !"null".equals(val)){
//					BaseHttpResultEntity_List<DNAInfoEntity> result = (BaseHttpResultEntity_List<DNAInfoEntity>) SerialUtil.jsonToObject(val,new TypeToken<BaseHttpResultEntity_List<DNAInfoEntity>>(){}.getType());
//					dataList = result.getRows();
//					Collections.reverse(dataList);
//					for(DNAInfoEntity temp:dataList){
//						if(/*temp.getIMEI().equals(device_id)*/Long.parseLong(device_id,16)==temp.getIMEI() || (device_id.equals(Long.toHexString(temp.getIMEI())))){
//							data = temp;
//							break;
//						}
//					}
//					//For test
////					if(data==null){
////						data = new DNAInfoEntity();
////						data.setTitle("123");
////						data.setCreateTime("1479285231369");
////						data.setUrl("http://dna.quanjiakan.com:6080/quanjiakan/resources/dna/20161116163527_dujx37.docx");
////						data.setIMEI(device_id);
////						data.setId("0");
////					}
//				}

				//TODO New Handle
				if(val != null && val.length() > 0 && !"null".equals(val)){
					HttpResponseResult result = new HttpResponseResult(val);
					if("200".equals(result.getCode())){
						if(result.getObject()!=null  && result.getObject().toString().length()>0){
							DNAInfoEntity_new temp = (DNAInfoEntity_new) SerialUtil.jsonToObject(result.getObject().toString(),new TypeToken<DNAInfoEntity_new>(){}.getType());
							if(temp!=null && temp.getIMEI()!=null && ( Long.parseLong(device_id,16)==Long.parseLong(temp.getIMEI()) || device_id.equals(Long.toHexString(Long.parseLong(temp.getIMEI()))) )){
								data = temp;
								//TODO
								File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+BaseApplication.getInstances().getPackageName()+File.separator+ FileDownloaderUtil.TEMP_DIR);
								if(!dir.exists()){
									dir.mkdirs();
								}
								final String fileName = data.getUrl().substring(data.getUrl().lastIndexOf("/")+1);
								File apk = new File(dir, fileName);
								if(apk.exists()){
									tv_submit.setText("打开");
								}else{
									tv_submit.setText("下载");
								}
							}else{
								tv_submit.setText("暂无");
							}
						}else{
							tv_submit.setText("暂无");
						}
					}else{
						tv_submit.setText("暂无");
					}
				}else{
					tv_submit.setText("暂无");
				}
			}
		},HttpUrls.getDNA(device_id),//TODO 这里最好再次校验一次，对接接口虽然变更了，但由于没有数据，无法判断返回数据的格式是否与原来一致
				params,Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(this)));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.tv_submit:
				if(data==null){
					BaseApplication.getInstances().toast(DNAInfoActivity.this,"暂无DNA文档!");
				}else{

					File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+BaseApplication.getInstances().getPackageName()+File.separator+ FileDownloaderUtil.TEMP_DIR);
					if(!dir.exists()){
						dir.mkdirs();
					}
					final String fileName = data.getUrl().substring(data.getUrl().lastIndexOf("/")+1);
					File apk = new File(dir, fileName);

					if(apk.exists()){
						final String savePath = apk.getAbsolutePath();
						if(savePath.toLowerCase().endsWith("doc") || savePath.toLowerCase().endsWith("docx")){
							Intent intent = new Intent("android.intent.action.VIEW");
							intent.addCategory("android.intent.category.DEFAULT");
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.setDataAndType(Uri.fromFile(new File(savePath)), "application/msword");
							startActivity(intent);
						}else{
							Intent intent = new Intent("android.intent.action.VIEW");
							intent.setDataAndType(Uri.fromFile(new File(savePath)), "*/*");
							startActivity(intent);
						}
					}else{
						showUpdateDialog(false);
					}
				}
				break;
			case R.id.ibtn_back:
				finish();
				break;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(this.getClass().getSimpleName());
		JPushInterface.onPause(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
		BaseApplication.getInstances().setCurrentActivity(this);
		JPushInterface.onResume(this);
	}

	private Dialog updateDialog;
	private TextView title;
	private TextView content;
	private ProgressBar progressBar;
	private TextView rate;
	private TextView cancel;
	private TextView confirm;
	//TODO 下载DNA文档
	public void showUpdateDialog(final boolean isForce){
		updateDialog = new Dialog(this, R.style.dialog_loading);
		updateDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(updateDialog!=null && updateDialog.isShowing()){
					return true;
				}else{
					return false;
				}
			}
		});
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_new,null);
		view.setBackgroundResource(R.drawable.selecter_hollow_white);
		title = (TextView) view.findViewById(R.id.tv_title);
		title.setText("文件下载");

		content = (TextView) view.findViewById(R.id.tv_content);
		content.setVisibility(View.VISIBLE);
		if(isForce){
			content.setText("是否下载DNA信息文件");
		}else{
			content.setText("是否下载DNA信息文件");
		}

		progressBar = (ProgressBar) view.findViewById(R.id.progress);
		progressBar.setVisibility(View.GONE);
		progressBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,15,0));
		progressBar.setMax(100);

		rate = (TextView) view.findViewById(R.id.rate);
		rate.setVisibility(View.GONE);

		cancel = (TextView) view.findViewById(R.id.btn_cancel);
		cancel.setText(getString(R.string.cancel));
		cancel.setVisibility(View.VISIBLE);

		confirm = (TextView) view.findViewById(R.id.btn_confirm);
		confirm.setText("下载");
		confirm.setVisibility(View.VISIBLE);

		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(updateTask!=null){
					updateTask.stopSubThread();
					updateTask = null;
				}

				if(isForce){
					updateDialog.dismiss();
//					System.exit(0);
				}else{
					updateDialog.dismiss();
					BaseApplication.getInstances().updateCheckTime();
				}
			}
		});
		confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				confirm.setVisibility(View.GONE);

				if(updateTask!=null){
					updateTask.stopSubThread();
					updateTask = null;
				}
				updateTask = new MultiThreadAsyncTask_WordFile(DNAInfoActivity.this,data.getUrl(),callback,updateDialog);
				content.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
				rate.setVisibility(View.VISIBLE);
				updateTask.execute("");

			}
		});
		//
		WindowManager.LayoutParams lp = updateDialog.getWindow().getAttributes();
		lp.width = QuanjiakanUtil.dip2px(BaseApplication.getInstances(), 240);
		lp.height = lp.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;

		updateDialog.setContentView(view,lp);
		updateDialog.setCanceledOnTouchOutside(false);

		updateDialog.show();
	}

	/**
	 * APP更新
	 *
	 * 弹出对话框进行提示，根据是否是强制更新，
	 *
	 */
	MultiThreadAsyncTask_WordFile updateTask;
	IDownloadCallback callback = new IDownloadCallback() {
		@Override
		public void updateProgress(int progress, String rate) {
			if(progressBar!=null && DNAInfoActivity.this.rate!=null){
				progressBar.setProgress(progress);
				DNAInfoActivity.this.rate.setText(rate);
			}
		}
	};
}
