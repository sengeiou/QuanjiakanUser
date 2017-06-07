package com.androidquanjiakan.activity.index.free;

import android.content.Context;
import android.os.Bundle;
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
import com.androidquanjiakan.entity.PatientInfoEntity;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.androidquanjiakan.util.LogUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class FreeInquiryActivity_EditPatientData extends BaseActivity implements OnClickListener{

	private TextView tv_title,tv_submit;
	private EditText et_name,et_age;
	private ImageButton ibtn_back;
	private RadioButton rbtn_1,rbtn_2;
	private LinearLayout layout_items;
	private View layout_datas;

	private Context context;

	private String gender;
	private int editPosition = -1;
	public static final String PARAMS_POSITION = "edit_position";
	private List<JsonObject> patients;
	private PatientInfoEntity entity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.layout_create_patient_data);
		editPosition = getIntent().getIntExtra(PARAMS_POSITION,-1);
		if(editPosition==-1){
			BaseApplication.getInstances().toast(FreeInquiryActivity_EditPatientData.this,"传入参数异常!");
			finish();
			return;
		}
		initTitle();
		initView();
	}

	public void initTitle(){
		ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
		ibtn_back.setVisibility(View.VISIBLE);
		ibtn_back.setOnClickListener(this);
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("编辑档案");
	}

	protected void initView(){
		tv_submit = (TextView)findViewById(R.id.tv_submit);
		tv_submit.setOnClickListener(this);

		rbtn_1 = (RadioButton)findViewById(R.id.rbtn_1);
		rbtn_2 = (RadioButton)findViewById(R.id.rbtn_2);
		et_age = (EditText)findViewById(R.id.et_birth_value);
		et_name = (EditText)findViewById(R.id.et_name_value);
		layout_items = (LinearLayout)findViewById(R.id.layout_items);
		layout_datas = (View)findViewById(R.id.layout_datas);
		loadPatientData();
	}

	/**
	 * TODO 后期需要通过接口保存到服务器中
	 */
	protected void loadPatientData(){
		layout_datas.setVisibility(View.GONE);
		List<PatientInfoEntity> data = OnlineDoctorPatientInfoHandler.getAllValue();
		entity = data.get(editPosition);
		et_name.setText(entity.getName());
		et_age.setText(entity.getAge());
		if(TableInfo_ValueStub.ONLINE_DOCTOR_PATIENT_INFO_MALE.equals(entity.getSex())){
			rbtn_1.setChecked(true);
			rbtn_2.setChecked(false);
		}else{
			rbtn_1.setChecked(false);
			rbtn_2.setChecked(true);
		}
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
	
	protected void postProblem(){
		if(et_name.length() == 0 || et_age.length() == 0){
			Toast.makeText(context, "请填写或修改病人资料", Toast.LENGTH_SHORT).show();
			return;
		}else {
			entity.setName(et_name.getText().toString());
			entity.setAge(et_age.getText().toString());
			entity.setSex(rbtn_1.isChecked()?TableInfo_ValueStub.ONLINE_DOCTOR_PATIENT_INFO_MALE:TableInfo_ValueStub.ONLINE_DOCTOR_PATIENT_INFO_FEMALE);
			LogUtil.e("ID（待更新）:"+entity.getId());
			OnlineDoctorPatientInfoHandler.updateValue(entity);

			BaseApplication.getInstances().toast(FreeInquiryActivity_EditPatientData.this,"档案保存成功!");
			setResult(RESULT_OK);
			finish();
		}
	}

}
