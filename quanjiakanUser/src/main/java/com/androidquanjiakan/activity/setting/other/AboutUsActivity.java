package com.androidquanjiakan.activity.setting.other;

import com.androidquanjiakan.activity.common.CommonWebEntryActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class AboutUsActivity extends BaseActivity implements OnClickListener{

	protected ImageButton ibtn_back;
	protected TextView tv_title;

	protected TextView tv_version;
	protected TextView tv_version_code;


	protected TextView tv_wechat_value;
	protected TextView tv_service_value;
	protected TextView tv_mail_value;
	protected TextView tv_web_value;

	private RelativeLayout attention_to_line;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_aboutus);
		initTitleBar();
		initView();
	}

	public void initTitleBar(){
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("关于我们");
		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		ibtn_back.setVisibility(View.VISIBLE);
		ibtn_back.setOnClickListener(this);
	}

	protected void initView(){
		tv_version = (TextView) findViewById(R.id.tv_version);

		tv_version_code = (TextView) findViewById(R.id.tv_version_code);
		tv_version_code.setText(BaseApplication.getInstances().getVersion());

		tv_wechat_value = (TextView) findViewById(R.id.tv_wechat_value);
		tv_service_value = (TextView) findViewById(R.id.tv_service_value);
		tv_service_value.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+getResources().getString(R.string.about_communicate)));
				startActivity(intent);
			}
		});
		tv_mail_value = (TextView) findViewById(R.id.tv_mail_value);
		tv_web_value = (TextView) findViewById(R.id.tv_web_value);
		tv_web_value.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AboutUsActivity.this, CommonWebEntryActivity.class);
				intent.putExtra(BaseConstants.PARAMS_URL, "http://www.hi-board.com/");
				startActivity(intent);
			}
		});
//		loadPhone();//是否需要使用这个接口，现实的格式是没有横杠的
	}

	public void loadData(){
		//TODO 若有接口获取信息，则变更该信息
	}

	String dialogPhone;
	public void loadPhone(){
		Map<String,String> params = new HashMap<String,String>();
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				if(val!=null && val.length()>0 && !"null".equals(val.toLowerCase())){
					JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
					dialogPhone = jsonObject.get("data").getAsString();
//					tv_service_value.setText(dialogPhone);
				}
			}
		}, HttpUrls.getComplainPhone(),params,Task.TYPE_GET_STRING_NOCACHE, null));
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
		if(arg0.getId() == R.id.ibtn_back){
			finish();
		}
	}
	
}
