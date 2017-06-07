package com.androidquanjiakan.activity.setting.other;

import java.util.HashMap;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class SceneModuleActivity extends BaseActivity implements OnClickListener {

	protected TextView tv_title;
	protected ImageButton ibtn_back;
	protected RadioGroup rgp;
	protected Context context;
	protected static final String KEY_DEFAULT_MODEL ="key_default_model";
	protected RadioButton rbtn_1,rbtn_2,rbtn_3,rbtn_4,rbtn_5;
	protected String current_model = "";
	private int model_position = -1;
	protected RadioButton[] rbtns = new RadioButton[5];

	private String device_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		/**
		 * 当前变更的设备的ID
		 */
		device_id = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_ID);
		LogUtil.e("PARAMS device_id:"+device_id);
		context = this;
		current_model = QuanjiakanSetting.getInstance().getValue(KEY_DEFAULT_MODEL);
		if(current_model.equals("")){
			current_model = "1";
			model_position = 0;
		}
		setContentView(R.layout.layout_qingjingmoshi);
		initView();

		/**
		 * TODO 设备列表----指定设备后再切换该指定设备的情景模式
		 */
	}

	/**
	 * 初始化界面
	 */
	protected void initView(){
		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		ibtn_back.setVisibility(View.VISIBLE);
		ibtn_back.setOnClickListener(this);
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("情景模式");
		rgp = (RadioGroup)findViewById(R.id.rgp);
		rgp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				RadioButton rbtn = (RadioButton)findViewById(arg1);
				switchWatchModel(rbtn.getTag().toString());
			}
		});
		rbtn_1 = (RadioButton)findViewById(R.id.rbtn_1);
		rbtn_2 = (RadioButton)findViewById(R.id.rbtn_2);
		rbtn_3 = (RadioButton)findViewById(R.id.rbtn_3);
		rbtn_4 = (RadioButton)findViewById(R.id.rbtn_4);
		rbtn_5 = (RadioButton)findViewById(R.id.rbtn_5);

		rbtn_1.setTag("1");
		rbtn_2.setTag("2");
		rbtn_3.setTag("3");
		rbtn_4.setTag("4");
		rbtn_5.setTag("5");

		rbtns[0] = rbtn_1;
		rbtns[1] = rbtn_2;
		rbtns[2] = rbtn_3;
		rbtns[3] = rbtn_4;
		rbtns[4] = rbtn_5;
		setRadioButtonSelected();
	}
	
	protected void setRadioButtonSelected(){
		for (int i = 0; i < rbtns.length; i++) {
			if(rbtns[i].getTag().equals(current_model)){
				rbtns[i].setChecked(true);
				model_position = i;
			}else {
				rbtns[i].setChecked(false);
			}
		}
	}
	
	/**
	 * 切换手表情景模式
	 * @param model
	 */
	protected void switchWatchModel(final String model){
		if(model.equals(current_model)){
			return;
		}
		LogUtil.e("UserID:"+BaseApplication.getInstances().getUser_id());
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user_id", BaseApplication.getInstances().getUser_id());
		params.put("model_id",model);
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			
			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				HttpResponseResult result = new HttpResponseResult(val);
				if(result.isResultOk()){
					Toast.makeText(context, "模式切换成功", Toast.LENGTH_SHORT).show();
					QuanjiakanSetting.getInstance().setValue(KEY_DEFAULT_MODEL, model);
					current_model = model;
				}else {
					Toast.makeText(context, "模式切换失败", Toast.LENGTH_SHORT).show();
					setRadioButtonSelected();
				}
			}
		}, HttpUrls.setWatchModel(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(context)));
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
		if(id == R.id.ibtn_back){
			finish();
		}
	}
	
}
