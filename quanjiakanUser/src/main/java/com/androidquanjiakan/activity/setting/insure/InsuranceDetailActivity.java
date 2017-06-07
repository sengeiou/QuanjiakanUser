package com.androidquanjiakan.activity.setting.insure;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

public class InsuranceDetailActivity extends BaseActivity implements OnClickListener{

	protected ImageButton ibtn_back;
	protected TextView tv_title;


	private TextView device_id_value;
	private TextView insurance_name_value;
	private TextView identify_id_value;
	private TextView insure_report_id_value;
	private TextView insurance_company_value;
	private TextView insure_classification_value;
	private TextView insure_payment_value;

	private TextView gender_value;
	private TextView effective_value;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_insure_detail);
		initView();
	}

	protected void initView(){
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("保单详情");
		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		ibtn_back.setVisibility(View.VISIBLE);
		ibtn_back.setOnClickListener(this);

		device_id_value = (TextView) findViewById(R.id.device_id_value);
		insurance_name_value = (TextView) findViewById(R.id.insurance_name_value);
		identify_id_value = (TextView) findViewById(R.id.identify_id_value);
		insure_report_id_value = (TextView) findViewById(R.id.insure_report_id_value);
		insurance_company_value = (TextView) findViewById(R.id.insurance_company_value);
		insure_classification_value = (TextView) findViewById(R.id.insure_classification_value);
		insure_payment_value= (TextView) findViewById(R.id.insure_payment_value);

		/**
		 * 数据赋值
		 */

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
		}
	}

}
