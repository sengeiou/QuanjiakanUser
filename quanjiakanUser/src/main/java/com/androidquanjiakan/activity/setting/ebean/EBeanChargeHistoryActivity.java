package com.androidquanjiakan.activity.setting.ebean;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 充值历史页
 */
public class EBeanChargeHistoryActivity extends BaseActivity implements OnClickListener{

	private ImageButton ibtn_back;
	private TextView tv_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_ebean_charge_history);
		initTitleBar();
		initView();
	}

	public void initTitleBar(){
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("充值历史");
		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		ibtn_back.setVisibility(View.VISIBLE);
		ibtn_back.setOnClickListener(this);
	}

	protected void initView(){

	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
		BaseApplication.getInstances().setCurrentActivity(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(this.getClass().getSimpleName());
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()){
			case R.id.ibtn_back:
				finish();
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode){

		}
	}
}
