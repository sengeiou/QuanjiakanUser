package com.androidquanjiakan.activity.setting.other;

import java.util.HashMap;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.util.CheckUtil;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class FeedbackActivity extends BaseActivity implements OnClickListener{

	protected TextView tv_title,tv_submit;
	protected ImageButton ibtn_back;
	protected EditText et_mail,et_content;
	protected Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.layout_fankui);
		initView();
	}
	
	/**
	 * 初始化界面
	 */
	protected void initView(){
		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		ibtn_back.setVisibility(View.VISIBLE);
		ibtn_back.setOnClickListener(this);
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("建议反馈");
		tv_submit = (TextView)findViewById(R.id.tv_submit);
		tv_submit.setOnClickListener(this);
		et_mail = (EditText)findViewById(R.id.et_mail);
		et_content = (EditText)findViewById(R.id.et_content);
	}
	
	protected void submit(){
		if(et_mail.length() == 0 || et_content.length() == 0){
			Toast.makeText(context, "请填写必填信息!", Toast.LENGTH_SHORT).show();
			return;
		}

		if(!CheckUtil.isEmail(et_mail.getText().toString())){
			Toast.makeText(context, "请填写正确的邮箱地址!", Toast.LENGTH_SHORT).show();
			return;
		}

		HashMap<String, String> params = new HashMap<>();
		params.put("user_id", QuanjiakanSetting.getInstance().getUserId()+"");
		params.put("mail", et_mail.getText().toString());
		params.put("content", et_content.getText().toString());
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			
			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				if(val!=null && val.length()>0){
					HttpResponseResult result = new HttpResponseResult(val);
					if(result.isResultOk()){
						Toast.makeText(context, "建议反馈提交成功", Toast.LENGTH_SHORT).show();
						et_mail.setText("");
						et_content.setText("");
						finish();
					}else {
						Toast.makeText(context, "建议反馈提交失败，请重试", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}, HttpUrls.feedback(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(context)));
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
		int id = arg0.getId();		
		if(id == R.id.tv_submit){
			submit();
		}else if (id == R.id.ibtn_back) {
			finish();
		}
	}
	
}
