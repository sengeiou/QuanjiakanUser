package com.androidquanjiakan.activity.setting.message;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

public class FreeInquiryDetailActivity extends BaseActivity implements OnClickListener{

	private TextView tv_title;
	private ImageButton ibtn_back;
	private TextView menu_text;

	private PullToRefreshListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_free_inquiry_detail);

		initTitleBar();



	}

	public void initTitleBar(){
		tv_title = (TextView) findViewById(R.id.tv_title);
		ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
		menu_text = (TextView) findViewById(R.id.menu_text);

		tv_title.setText("免费问诊详情");
		ibtn_back.setVisibility(View.VISIBLE);
		ibtn_back.setOnClickListener(this);
		menu_text.setVisibility(View.VISIBLE);
		menu_text.setText("追问");
		menu_text.setOnClickListener(this);

	}

	public void initContent(){
		listView = (PullToRefreshListView) findViewById(R.id.listView);
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
			case R.id.menu_text:
				/**
				 * 跳转到追问界面
				 */
				break;
			default:

				break;
		}
	}
	
}
