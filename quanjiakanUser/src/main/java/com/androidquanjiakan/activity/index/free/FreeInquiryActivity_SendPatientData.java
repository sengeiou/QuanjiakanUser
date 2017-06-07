package com.androidquanjiakan.activity.index.free;

import java.util.HashMap;
import java.util.List;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.datahandler.OnlineDoctorPatientInfoHandler;
import com.androidquanjiakan.datahandler.PatientProblemInfoHandler;
import com.androidquanjiakan.entity.PatientInfoEntity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.util.LogUtil;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FreeInquiryActivity_SendPatientData extends BaseActivity implements OnClickListener{

	private TextView tv_title,tv_submit;
	private EditText et_name,et_age;
	private RadioButton rbtn_1,rbtn_2;
	private LinearLayout layout_items;
	private View layout_datas;
	private ImageButton ibtn_back;
	private RelativeLayout layout_name;
	private RelativeLayout layout_gender;
	private RelativeLayout layout_birth;
	private TextView edit;

	private Context context;

	private List<PatientInfoEntity> patientInfoEntityList;

	private int positionP = 0;

	private static final int REQUEST_CREATE = 10086;

	OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			et_name.setText("");
			et_age.setText("");
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.layout_get_patient_data);
		initTitle();
		initView();
	}

	public void initTitle(){
		ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
		ibtn_back.setVisibility(View.VISIBLE);
		ibtn_back.setOnClickListener(this);
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("选择健康档案");
	}

	protected void initView(){


		edit = (TextView) findViewById(R.id.edit);
		edit.setOnClickListener(this);

		layout_name = (RelativeLayout) findViewById(R.id.layout_name);
		layout_name.setVisibility(View.GONE);

		layout_gender = (RelativeLayout) findViewById(R.id.layout_gender);
		layout_gender.setVisibility(View.GONE);

		layout_birth = (RelativeLayout) findViewById(R.id.layout_birth);
		layout_birth.setVisibility(View.GONE);

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
	 * 本地缓存数据---保存在应用的内部存储空间中
	 */
	protected void loadPatientData(){
		layout_items.removeAllViews();
		layout_datas.setVisibility(View.VISIBLE);
		/**
		 * 数据库
		 */
		patientInfoEntityList = OnlineDoctorPatientInfoHandler.getAllValue();
		if(patientInfoEntityList.size() > 0){
			for (int i = 0; i < patientInfoEntityList.size(); i++) {
				PatientInfoEntity patient = patientInfoEntityList.get(i);
				View view = LayoutInflater.from(FreeInquiryActivity_SendPatientData.this).inflate(R.layout.item_patient_data, null);
				CheckBox checkBox = (CheckBox)view.findViewById(R.id.chk);
				checkBox.setText(patient.getName()+"("+patient.getSex()+" , "+patient.getAge()+")");
				checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("type","patient_meta");
				jsonObject.addProperty("age",patient.getAge());
				jsonObject.addProperty("sex",patient.getSex());
				jsonObject.addProperty("name",patient.getName());
				checkBox.setTag(jsonObject);
				layout_items.addView(view);
			}
		}else {

		}

		View view = LayoutInflater.from(FreeInquiryActivity_SendPatientData.this).inflate(R.layout.item_patient_data_add, null);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(FreeInquiryActivity_SendPatientData.this,FreeInquiryActivity_CreatePatientData.class);
				startActivityForResult(intent,REQUEST_CREATE);
			}
		});
		layout_items.addView(view);
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
			case R.id.edit:
				if(layout_items.getChildCount()<2){
					BaseApplication.getInstances().toast(FreeInquiryActivity_SendPatientData.this,"无可编辑档案");
				}else{
					int checked_rows = 0;
					int editPosition = -1;
					for (int i = 0; i < layout_items.getChildCount()-1; i++) {
						CheckBox chk = (CheckBox)layout_items.getChildAt(i).findViewById(R.id.chk);
						if(chk.isChecked()){
							checked_rows += 1;
							editPosition = i;
						}
					}
					if(checked_rows==0 || editPosition == -1){
						BaseApplication.getInstances().toast(FreeInquiryActivity_SendPatientData.this,"请选择一个待编辑档案");
					}else if(checked_rows>1){
						BaseApplication.getInstances().toast(FreeInquiryActivity_SendPatientData.this,"只能选择一个待编辑档案");
					}else{
						Intent intent = new Intent(FreeInquiryActivity_SendPatientData.this,FreeInquiryActivity_EditPatientData.class);
						intent.putExtra(FreeInquiryActivity_EditPatientData.PARAMS_POSITION,editPosition);
						startActivityForResult(intent,REQUEST_CREATE);
					}
				}
				break;
			default:
				break;
		}
	}

	protected void postProblem(){
		int checked_rows = 0;

		final JsonArray array = new GsonParseUtil(getIntent().getStringExtra("content")).getJsonArray();
		for (int i = 0; i < layout_items.getChildCount()-1; i++) {
			CheckBox chk = (CheckBox)layout_items.getChildAt(i).findViewById(R.id.chk);
			if(chk.isChecked()){
				checked_rows += 1;
				positionP = i;
			}
		}		
		if(checked_rows == 0){
			Toast.makeText(context, "请填写创建病人档案或选择病人档案", Toast.LENGTH_SHORT).show();
			return;
		}else if(checked_rows == 1){
			for (int i = 0; i < layout_items.getChildCount()-1; i++) {
				CheckBox chk = (CheckBox)layout_items.getChildAt(i).findViewById(R.id.chk);
				if(chk.isChecked()){
					JsonObject item = (JsonObject)chk.getTag();
					array.add(item);
				}
			}
			LogUtil.e("---------"+array);
		}else if (checked_rows > 1) {
			Toast.makeText(context, "最多只能选择一个病人", Toast.LENGTH_SHORT).show();
			return;
		}		
		final String doctor_id = getIntent().getStringExtra("doctor_id");
		
		HashMap<String, String> params = new HashMap<>();
		params.put("content", array+"");
		params.put("totoken", doctor_id);
		params.put("fromtoken", QuanjiakanSetting.getInstance().getUserId()+"");
//		params.put("price", 12+"");
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			
			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				if(val!=null && val.length()>0){
					HttpResponseResult result = new HttpResponseResult(val);
					if(result.getCode().equals("200")){
//						BaseApplication.getInstances().toast("问题提交成功!");
						Toast.makeText(FreeInquiryActivity_SendPatientData.this, "问题提交成功!", Toast.LENGTH_LONG).show();
						/**
						 * 保存问题至数据库
						 */
						PatientInfoEntity patientData = patientInfoEntityList.get(positionP);
						PatientProblemInfoHandler.insertValue(result.getMessage(),patientData.getName(),patientData.getSex(),patientData.getAge(),
								doctor_id,getIntent().getStringExtra("text"),System.currentTimeMillis()+"",getIntent().getStringExtra("clinic"));
						/**
						 * 返回上一页
						 */
						setResult(RESULT_OK);
						finish();
					}else{
						BaseApplication.getInstances().toast(FreeInquiryActivity_SendPatientData.this,result.getMessage());
						setResult(RESULT_CANCELED);
						finish();
					}
				}else{

				}


			}
		}, /*doctor_id.equals("") ? HttpUrls.postNewProblemToChunyu(): HttpUrls.postNewProblem()*/HttpUrls.postNewProblemToChunyu(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(FreeInquiryActivity_SendPatientData.this)));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode){
			case REQUEST_CREATE:
				if(resultCode==RESULT_OK){
					loadPatientData();
				}
				break;
			default:
				break;
		}
	}
}
