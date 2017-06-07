package com.androidquanjiakan.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.activity.index.SigninActivity_mvp;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.util.EditTextFilter;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.SignatureUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

public class Setting_PasswordModifyActivity extends BaseActivity implements OnClickListener {
	
	protected EditText et_mobile,/*et_code,*/et_password,et_confirm;
	protected Button btn_submit;
	protected TextView /*tv_yanzhengma,*/tv_title;
	protected Context context;
	protected ImageButton ibtn_back;
	private String flag;
	private TextView tv_notice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = Setting_PasswordModifyActivity.this;
		setContentView(R.layout.layout_password_modify);
		initView();
	}
	
	protected void initView(){
		et_mobile = (EditText)findViewById(R.id.et_username);
//		et_code = (EditText)findViewById(R.id.et_code);
//		et_code.setTag("");
//		tv_yanzhengma = (TextView)findViewById(R.id.btn_yanzhengma);
//		tv_yanzhengma.setOnClickListener(this);
		et_password = (EditText)findViewById(R.id.et_newpassword);
		et_confirm = (EditText)findViewById(R.id.et_confirmnewpassword);
		btn_submit = (Button)findViewById(R.id.btn_submit);
		btn_submit.setOnClickListener(this);
		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		ibtn_back.setOnClickListener(this);
		ibtn_back.setVisibility(View.VISIBLE);
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText(R.string.setting_password_modify);

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
			case R.id.btn_yanzhengma:
//				getSMSCode();
				break;
			case R.id.btn_submit:
				findPassword();
				break;
			case R.id.ibtn_back:
				finish();
				break;
			default:
				break;
		}
	}
	
	protected void findPassword(){
		if(et_mobile.length() == 0 || et_password.length() == 0 || et_confirm.length() == 0){
			Toast.makeText(context, getResources().getString(R.string.setting_password_check1), Toast.LENGTH_SHORT).show();
			return;
		}



		if(!QuanjiakanSetting.getInstance().getValue(QuanjiakanSetting.KEY_SIGNAL).equals(SignatureUtil.getSHA1String(et_mobile.getText().toString()))){
			Toast.makeText(context, getResources().getString(R.string.setting_password_origin_error), Toast.LENGTH_SHORT).show();
			return;
		}


		if(et_password.getText().toString().trim().length()<6||et_password.getText().toString().trim().length()>15) {
			Toast.makeText(context, "新密码位数不对", Toast.LENGTH_SHORT).show();
			return;
		}

		if(et_confirm.getText().toString().trim().length()<6||et_confirm.getText().toString().trim().length()>15) {
			Toast.makeText(context, "确认密码位数不对", Toast.LENGTH_SHORT).show();
			return;
		}


		if(!et_password.getText().toString().equals(et_confirm.getText().toString())){
			Toast.makeText(context, getResources().getString(R.string.setting_password_check2), Toast.LENGTH_SHORT).show();
			return;
		}

		if(et_password.getText().toString().equals(et_mobile.getText().toString())){
			Toast.makeText(context, getResources().getString(R.string.setting_password_check13), Toast.LENGTH_SHORT).show();
			return;
		}



		/**
		 * 提升账号安全措施
		 */
		if(!CheckUtil.checkStringType(et_password.getText().toString())){
			Toast.makeText(context, getResources().getString(R.string.setting_password_check5), Toast.LENGTH_SHORT).show();
			return;
		}


		
		HashMap<String, String> params = new HashMap<>();//et_mobile.getText().toString()
		params.put("oldpassword", BaseApplication.getInstances().getFormatPWString(et_mobile.getText().toString()));
		params.put("password", BaseApplication.getInstances().getFormatPWString(et_password.getText().toString()));
		params.put("confirmpassword", BaseApplication.getInstances().getFormatPWString(et_password.getText().toString()));
		params.put("user_id", BaseApplication.getInstances().getUser_id());
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				if(val!=null && val.length()>0){
					HttpResponseResult result = new HttpResponseResult(val);
					if(result.getCode().equals("200")){
						/**
						 * 不退出
						 */
//						Toast.makeText(context, getResources().getString(R.string.setting_password_check3), Toast.LENGTH_SHORT).show();
//						QuanjiakanSetting.getInstance().setValue(QuanjiakanSetting.KEY_SIGNAL, SignatureUtil.getSHA1String(et_password.getText().toString()));
//						finish();
						/**
						 * 退出
						 */
						Toast.makeText(context, getResources().getString(R.string.setting_password_check31), Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(Setting_PasswordModifyActivity.this,SigninActivity_mvp.class);
						startActivity(intent);
						QuanjiakanSetting.getInstance().logout();
						finish();

					}else {
						Toast.makeText(context, getResources().getString(R.string.setting_password_check4), Toast.LENGTH_SHORT).show();
					}
				}
			}
		}, HttpUrls.updatepassword(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(context)));
	}
	
//	/**
//	 * 获取短信验证码
//	 */
//	protected void getSMSCode(){
//		if (!CheckUtil.isPhoneNumber(et_mobile.getText().toString())) {
//			Toast.makeText(context, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
//			return;
//		}
//
//		HashMap<String, String> params = new HashMap<>();
//		params.put("mobile", et_mobile.getText().toString());
//		MyHandler.putTask(new Task(new HttpResponseInterface() {
//
//			@Override
//			public void handMsg(String val) {
//				// TODO Auto-generated method stub
//				HttpResponseResult result = new HttpResponseResult(val);
//				if(result.getCode().equals("200")){
//					//获取验证码
//					showSmsCodeTime();
//					et_code.setTag(result.getMessage());
//				}else {
//					Toast.makeText(context, "获取验证码失败，请重试！", Toast.LENGTH_SHORT).show();
//					et_code.setTag("2222");
//				}
//			}
//		}, HttpUrls.getSMSCode(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(this)));
//	}
//
//	Handler mHandler = new Handler(){
//
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			super.handleMessage(msg);
//			int arg = msg.arg1;
//			if(msg.what == 1){
//				if(arg == 0){
//					tv_yanzhengma.setText("获取验证码");
//				}else {
//					tv_yanzhengma.setText("剩余"+arg + "s");
//				}
//			}
//		}
//
//	};
//
//	int total = 120;
//	protected void showSmsCodeTime(){
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				try {
//					do {
//						Thread.sleep(1000);
//						total--;
//						Message msg = new Message();
//						msg.what = 1;
//						msg.arg1 = total;
////						mHandler.sendMessage(msg);
//					} while (total > 0);
//
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}).start();
//	}
	
}
