package com.androidquanjiakan.activity.index.free;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.database.TableInfo_ValueStub;
import com.androidquanjiakan.datahandler.OnlineDoctorPatientInfoHandler;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

public class FreeInquiryActivity_CreatePatientData extends BaseActivity implements OnClickListener{

	private TextView tv_title,tv_submit;
	private EditText et_name,et_age;
	private ImageButton ibtn_back;
	private RadioButton rbtn_1,rbtn_2;
	private LinearLayout layout_items;
	private View layout_datas;
	private Context context;
	private String gender;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.layout_create_patient_data);
		initTitleBar();
		initView();
	}

	public void initTitleBar(){
		ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
		ibtn_back.setVisibility(View.VISIBLE);
		ibtn_back.setOnClickListener(this);
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("创建新档案");
	}

	protected void initView(){
		tv_submit = (TextView)findViewById(R.id.tv_submit);
		tv_submit.setOnClickListener(this);
		rbtn_1 = (RadioButton)findViewById(R.id.rbtn_1);
		rbtn_2 = (RadioButton)findViewById(R.id.rbtn_2);
		et_age = (EditText)findViewById(R.id.et_birth_value);
		et_name = (EditText)findViewById(R.id.et_name_value);
//		et_age.addTextChangedListener(new TextWatcher() {
//			@Override
//			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//			}
//
//			@Override
//			public void onTextChanged(CharSequence s, int start, int before, int count) {
//				if(s.length()>0){
//					long temp = Long.parseLong(s+"");
//					if(temp>120){
//						BaseApplication.getInstances().toast("年龄不能大于120岁!");
//						et_age.setText("120");
//					}
//				}
//			}
//
//			@Override
//			public void afterTextChanged(Editable s) {
//
//			}
//		});
		layout_items = (LinearLayout)findViewById(R.id.layout_items);
		layout_datas = (View)findViewById(R.id.layout_datas);
		loadPatientData();
	}

	/**
	 * 本地缓存数据---保存在应用的内部存储空间中
	 */
	protected void loadPatientData(){
		layout_datas.setVisibility(View.GONE);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(this.getClass().getSimpleName());
	}

	@Override                                                                                                                                                                                                                            
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
		BaseApplication.getInstances().setCurrentActivity(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()){
			case R.id.tv_submit:
				postProblem();
				break;
			case R.id.ibtn_back:
				finish();
				break;
			default:
				break;
		}
	}

	/**
	 * 后面需要进行修正，使用接口来保存相关的数据
	 */
	protected void postProblem(){
		if(et_name.length() == 0 || et_age.length() == 0){
			Toast.makeText(context, "请填写病人资料或者选择病人资料", Toast.LENGTH_SHORT).show();
			return;
		}else if(et_name.length()>0 && et_age.length()>0 && ((Long.parseLong(et_age.getText().toString()))>120)){
			Toast.makeText(context, "年龄不能大于120岁", Toast.LENGTH_SHORT).show();
			return;
		}else {//向cache中保存数据
			JsonObject patient = new JsonObject();
			patient.addProperty("type", "patient_meta");
			patient.addProperty("age", et_age.getText().toString());
			patient.addProperty("sex", rbtn_1.isChecked()?"男":"女");
			patient.addProperty("name", et_name.getText().toString());
			OnlineDoctorPatientInfoHandler.insertValue(et_name.getText().toString(),
					rbtn_1.isChecked()?TableInfo_ValueStub.ONLINE_DOCTOR_PATIENT_INFO_MALE:TableInfo_ValueStub.ONLINE_DOCTOR_PATIENT_INFO_FEMALE,
					et_age.getText().toString());
//			BaseApplication.getInstances().toast("档案保存成功!");//TODO 2017-02-08 需求统一，娅利确认去除
			setResult(RESULT_OK);
			finish();
		}
	}

}
