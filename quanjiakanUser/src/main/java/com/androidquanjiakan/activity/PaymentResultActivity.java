package com.androidquanjiakan.activity;

import com.androidquanjiakan.activity.setting.order.HouseKeeperOrderDetailActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.CommonRequestCode;
import com.quanjiakan.main.R;
import com.androidquanjiakan.util.LogUtil;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class PaymentResultActivity extends BaseActivity {

	private ImageView image;
	private TextView tv_pay_channel,tv_total_fee,tv_title;
	private ImageButton ibtn_back;
	private TextView tv_homepage;
	private TextView tv_orderlist;

	private String orderString;
	public static final String PARAMS_DATA = "data";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_payresult);
		BaseApplication.getInstances().setCurrentActivity(this);
		initView();
	}

	protected void initView(){
		final int flag = getIntent().getIntExtra("flag", -2);
		orderString = getIntent().getStringExtra(PARAMS_DATA);
		image = (ImageView)findViewById(R.id.image);
		tv_pay_channel = (TextView)findViewById(R.id.tv_pay_channel);
		ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
		ibtn_back.setVisibility(View.VISIBLE);
		ibtn_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(flag==1){
					setResult(RESULT_OK);
				}
				finish();
			}
		});
		tv_homepage = (TextView) findViewById(R.id.tv_homepage);
		tv_homepage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				/**
				 * 返回首页
				 */
				setResult(CommonRequestCode.RESULT_BACK_TO_MAIN);
				finish();
			}
		});
		tv_orderlist = (TextView) findViewById(R.id.tv_orderlist);
		tv_orderlist.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				/**
				 * 查看订单
				 */
				LogUtil.e("orderString:"+orderString);
				Intent intent = new Intent(PaymentResultActivity.this,HouseKeeperOrderDetailActivity.class);
				intent.putExtra(HouseKeeperOrderDetailActivity.PARAMS_ENTITY,orderString);
				startActivity(intent);
			}
		});


		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("支付结果");
		tv_total_fee = (TextView)findViewById(R.id.tv_total_fee);
		String pay_type = getIntent().getStringExtra("type");
		double total_fee = getIntent().getDoubleExtra("total_fee", 0.0);

		String orderid = getIntent().getStringExtra("orderid");
//		tv_total_fee.setText("￥"+total_fee+"元");
		tv_total_fee.setVisibility(View.GONE);
		if(flag == 0){
			//提交后台验证有故障
			image.setImageResource(R.drawable.ic_question);
			tv_pay_channel.setText("订单:\n"+orderid+"\n通过"+pay_type+"支付已提交，正在等待后台验证!"+"￥"+total_fee+"元");
		}else if (flag == 1) {
			//支付成功
			image.setImageResource(R.drawable.payment_success);
			tv_pay_channel.setText("订单:\n"+orderid+"\n通过"+pay_type+"支付成功\n"+"￥"+total_fee+"元");
		}else if (flag == -1) {
			//支付失败
			image.setImageResource(R.drawable.ic_error);
			tv_pay_channel.setText("订单:\n"+orderid+"\n通过"+pay_type+"支付失败\n"+"￥"+total_fee+"元");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(this.getClass().getSimpleName());
	}
	
}
