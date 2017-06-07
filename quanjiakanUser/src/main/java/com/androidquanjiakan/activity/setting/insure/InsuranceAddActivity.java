package com.androidquanjiakan.activity.setting.insure;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.adapter.InsureDeviceAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.datahandler.BindDeviceHandler;
import com.androidquanjiakan.entity.BindDeviceEntity;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class InsuranceAddActivity extends BaseActivity implements OnClickListener{

	protected ImageButton ibtn_back;
	protected TextView tv_title;

	private TextView confirm;

	private ImageView device_pulldown;
	private ImageView insurance_company_pulldown;

	private ImageView gender_male;
	private ImageView gender_female;
	private boolean gender_male_flag = true;

	private ImageView effective_yes;
	private ImageView effective_no;
	private boolean effective_flag = true;

	private EditText device_id_value;
	private EditText insurance_name_value;
	private EditText identify_id_value;
	private EditText insure_report_id_value;
	private EditText insurance_company_value;
	private EditText insure_classification_value;
	private EditText insure_payment_value;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_insure_add);
		initView();
	}

	protected void initView(){
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("新增保单");
		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		ibtn_back.setVisibility(View.VISIBLE);
		ibtn_back.setOnClickListener(this);

		confirm = (TextView) findViewById(R.id.confirm);
		confirm.setOnClickListener(this);

		gender_male = (ImageView) findViewById(R.id.gender_male);
		gender_female = (ImageView) findViewById(R.id.gender_female);
		gender_male.setOnClickListener(this);
		gender_female.setOnClickListener(this);
		effective_yes = (ImageView) findViewById(R.id.effective_yes);
		effective_no = (ImageView) findViewById(R.id.effective_no);
		effective_yes.setOnClickListener(this);
		effective_no.setOnClickListener(this);

		device_id_value = (EditText) findViewById(R.id.device_id_value);
		insurance_name_value = (EditText) findViewById(R.id.insurance_name_value);
		identify_id_value = (EditText) findViewById(R.id.identify_id_value);
		insure_report_id_value = (EditText) findViewById(R.id.insure_report_id_value);
		insurance_company_value = (EditText) findViewById(R.id.insurance_company_value);
		insure_classification_value = (EditText) findViewById(R.id.insure_classification_value);
		insure_payment_value= (EditText) findViewById(R.id.insure_payment_value);

		device_pulldown = (ImageView) findViewById(R.id.device_pulldown);
		insurance_company_pulldown = (ImageView) findViewById(R.id.insurance_company_pulldown);
		device_pulldown.setOnClickListener(this);
		insurance_company_pulldown.setOnClickListener(this);
	}

	public void changeGender(boolean flag){
		if(flag){
			gender_male_flag = true;
			gender_male.setImageResource(R.drawable.choice_light);
			gender_female.setImageResource(R.drawable.choice);
		}else{
			gender_male_flag = false;
			gender_male.setImageResource(R.drawable.choice);
			gender_female.setImageResource(R.drawable.choice_light);
		}
	}

	public void changeEffection(boolean flag){
		if(flag){
			effective_flag = true;
			effective_yes.setImageResource(R.drawable.choice_light);
			effective_no.setImageResource(R.drawable.choice);
		}else{
			effective_flag = false;
			effective_yes.setImageResource(R.drawable.choice);
			effective_no.setImageResource(R.drawable.choice_light);
		}
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
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.ibtn_back:
				finish();
				break;
			case R.id.device_pulldown:
				if(popupWindow==null || !popupWindow.isShowing()){
					showDevicesInfoWindow(view);
				}else{
					popupWindow.dismiss();
				}
				break;
			case R.id.insurance_company_pulldown:
				if(insurePopupWindow==null || !insurePopupWindow.isShowing()){
					showInsuranceCompanyWindow(view);
				}else{
					insurePopupWindow.dismiss();
				}
				break;
			case R.id.gender_male:
				changeGender(true);
				break;
			case R.id.gender_female:
				changeGender(false);
				break;
			case R.id.effective_yes:
				changeEffection(true);
				break;
			case R.id.effective_no:
				changeEffection(false);
				break;
			case R.id.confirm:

				submit();
				break;
		}
	}
	private PopupWindow popupWindow;
	public void showDevicesInfoWindow(View root){
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_popup_list,null);
		ListView listview = (ListView) view.findViewById(R.id.listview);
		final List<BindDeviceEntity> data = BindDeviceHandler.getAllValue();
		InsureDeviceAdapter adapter = new InsureDeviceAdapter(this,data);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				popupWindow.dismiss();
				device_id_value.setText(data.get((int)l).getDeviceid());
				insurance_name_value.setText(data.get((int)l).getName());
			}
		});
		popupWindow = new PopupWindow(view,
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
		popupWindow.setTouchable(true);
		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.selecter_common_dialog_bg));
		popupWindow.showAsDropDown(root);//设置在某个指定View的下方

	}
	private PopupWindow insurePopupWindow;
	public void showInsuranceCompanyWindow(View root){
		/**
		 * 需要获取保险公司名称信息----或者使用本地数据
		 */
		/**
		 * @TODO 接口获取数据?
		 */
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_popup_list,null);
		ListView listview = (ListView) view.findViewById(R.id.listview);
		final List<BindDeviceEntity> data = new ArrayList<BindDeviceEntity>();
		BindDeviceEntity
				entity = new BindDeviceEntity();
		entity.setName("中国平安");
		data.add(entity);

		entity = new BindDeviceEntity();
		entity.setName("中国人寿");
		data.add(entity);

		entity = new BindDeviceEntity();
		entity.setName("太平洋保险");
		data.add(entity);




		InsureDeviceAdapter adapter = new InsureDeviceAdapter(this,data);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				insurePopupWindow.dismiss();
				insurance_company_value.setText(data.get((int)l).getName());
			}
		});
		insurePopupWindow = new PopupWindow(view,
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
		insurePopupWindow.setTouchable(true);
		insurePopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.selecter_common_dialog_bg));
		insurePopupWindow.showAsDropDown(root);
	}

	/**
	 * @TODO 提交接口---提交成功后返回，失败则留在本页，并提示原因
	 */
	public void submit(){
		StringBuilder sb = new StringBuilder();
		sb.append("device_id_value:"+device_id_value.getText().toString());
		if(gender_male_flag){
			sb.append("\n性别:男!");
		}else{
			sb.append("\n性别:女!");
		}
		sb.append("\ninsurance_name_value:"+insurance_name_value.getText().toString());
		sb.append("\nidentify_id_value:"+identify_id_value.getText().toString());
		sb.append("\ninsure_report_id_value:"+insure_report_id_value.getText().toString());
		sb.append("\ninsurance_company_value:"+insurance_company_value.getText().toString());
		sb.append("\ninsure_classification_value:"+insure_classification_value.getText().toString());
		sb.append("\ninsure_payment_value:"+insure_payment_value.getText().toString());//effective_flag
		if(effective_flag){
			sb.append("\n有效:true");
		}else{
			sb.append("\n性别:false");
		}
		Toast.makeText(InsuranceAddActivity.this, ""+sb.toString(), Toast.LENGTH_SHORT).show();
	}
	
}
