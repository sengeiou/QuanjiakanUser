package com.androidquanjiakan.activity.index;

import com.androidquanjiakan.activity.main.MainActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.quanjiakan.main.R;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.util.LogUtil;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import cn.jpush.android.api.JPushInterface;

public class WelcomeActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_loading);
//		addAlertWindow();

		JPushInterface.clearAllNotifications(BaseApplication.getInstances());
//		JPushInterface.stopPush(BaseApplication.getInstances());
	}
	
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			initView();
		}		
	};
	

	protected void initView(){
		if(QuanjiakanSetting.getInstance().getValue("isyindaoye").equals("")){
			Intent intent = new Intent(WelcomeActivity.this,GuidePageActivity.class);
			startActivity(intent);
		}else {
			if(QuanjiakanSetting.getInstance().getUserId() == 0 || "0".equals(BaseApplication.getInstances().getUser_id())){
				Intent intent = new Intent(WelcomeActivity.this,SigninActivity_mvp.class);
				startActivity(intent);
			}else {
				Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
				startActivity(intent);
			}
		}
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(this.getClass().getSimpleName());

		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mHandler.sendEmptyMessage(1);
			}
		}, 500);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(this.getClass().getSimpleName());
	}

}
