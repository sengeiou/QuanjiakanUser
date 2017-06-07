package com.androidquanjiakan.base;

import cn.jpush.android.api.JPushInterface;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.androidquanjiakan.interfaces.IActivity;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends AppCompatActivity implements IActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		JPushInterface.onPause(this);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.onResume(this);
		if(BaseApplication.getInstances().getMainActivity()!=null &&
				BaseApplication.getInstances().getNattyClient()!=null &&
				!BaseApplication.getInstances().isSDKConnected()){//reconnection
			BaseApplication.getInstances().getMainActivity().startNDKService();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	public Object getParamter(int type){
		return null;
	}
	public void showMyDialog(int type){

	}
	public void dismissMyDialog(int type){

	}
	public void onSuccess(int type,int httpResponseCode,Object result){

	}
	public void onError(int type,int httpResponseCode,Object errorMsg){

	}
}
