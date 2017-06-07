package com.androidquanjiakan.activity.index.housekeeper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.androidquanjiakan.activity.PaymentResultActivity;
import com.androidquanjiakan.activity.common.ImageViewerActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanjiakan.main.R;
import com.quanjiakan.main.wxapi.WXPayEntryActivity;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.AlipayHandler;
import com.androidquanjiakan.util.CheckUtil;
import com.quanjiakanuser.util.GsonParseUtil;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.quanjiakanuser.util.AlipayHandler.AlipayCallback;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.quanjiakanuser.util.WeixinPayHandler;
import com.quanjiakanuser.widget.ChangeBirthDialog;
import com.quanjiakanuser.widget.ChangeBirthDialog.OnBirthListener;
import com.umeng.analytics.MobclickAgent;
import com.wxapi.entity.HouseKeeperParamEntity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class HouseKeeperOrderActivity extends BaseActivity implements OnClickListener {

	private TextView tv_title,tv_name,tv_level,tv_age,tv_price,tv_order;
	private TextView menu_text;

	private Context context;//payment_channel
	private RatingBar rBar;
	private ImageView image;
	private TextView tv_begin_date_value,tv_end_date_value,tv_code;
	private EditText et_contact_name,et_mobile,et_code,et_address;
	private Context mContext;
	private RadioButton rbtn_1,rbtn_2,rbtn_3;
	private ImageButton ibtn_back;
	private TextView tv_detail;
	private JsonObject object;
	private JsonObject paidinfo;

	private int total = 90;
	private String lastSMSPhone;
	private Dialog detailDialog;
	private JsonObject housekeeper;
	private boolean flag = false;
	public static JsonObject orderDetail = new JsonObject();
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			int arg = msg.arg1;
			if(msg.what == 1){
				if(arg < 1){
					tv_code.setEnabled(true);
					tv_code.setText("获取验证码");
				}else {
					tv_code.setEnabled(false);
					tv_code.setText("剩余"+arg + "s");
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.layout_housekeeper_order);
		initTitleBar();
		initView();
	}

	public void initTitleBar(){
		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		ibtn_back.setOnClickListener(this);
		ibtn_back.setVisibility(View.VISIBLE);
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("预约");

		menu_text = (TextView) findViewById(R.id.menu_text);
		menu_text.setText("用户须知");
		menu_text.setVisibility(View.VISIBLE);
		menu_text.setOnClickListener(this);
	}

	/**
	 * 初始化界面
	 */
	protected void initView(){
//		orderDetail = new JsonObject();

		image = (ImageView)findViewById(R.id.image);

		tv_name = (TextView)findViewById(R.id.tv_name);
		tv_level = (TextView)findViewById(R.id.tv_level);
		tv_age = (TextView)findViewById(R.id.tv_age);
		tv_price = (TextView)findViewById(R.id.tv_price);
		tv_order = (TextView)findViewById(R.id.tv_order);
		tv_order.setOnClickListener(this);
		rBar = (RatingBar)findViewById(R.id.rbar);
		tv_begin_date_value = (TextView)findViewById(R.id.tv_begin_date_value);
		tv_begin_date_value.setOnClickListener(this);
		tv_end_date_value = (TextView)findViewById(R.id.tv_end_date_value);
		tv_end_date_value.setOnClickListener(this);
		tv_code = (TextView)findViewById(R.id.tv_code);
		tv_code.setOnClickListener(this);
		et_contact_name = (EditText)findViewById(R.id.et_contact_name);
		et_code = (EditText)findViewById(R.id.et_code);
		et_mobile = (EditText)findViewById(R.id.et_mobile);
		et_address = (EditText)findViewById(R.id.et_address);

		rbtn_1 = (RadioButton)findViewById(R.id.rbtn_1);
		rbtn_2 = (RadioButton)findViewById(R.id.rbtn_2);
		rbtn_3 = (RadioButton)findViewById(R.id.rbtn_3);
		tv_detail = (TextView) findViewById(R.id.tv_detail);
		tv_detail.setVisibility(View.GONE);
		tv_detail.setOnClickListener(this);
		setValue();
	}

	/**
	 * 设置参数
	 */
	protected void setValue(){
		housekeeper = new GsonParseUtil(getIntent().getStringExtra("housekeeper")).getJsonObject();
		tv_name.setText(housekeeper.get("name").getAsString());
		tv_price.setText("价格:"+housekeeper.get("price").getAsString()+"/月");
		tv_price.setTag(housekeeper.get("price").getAsString());
		tv_age.setText("年龄:"+housekeeper.get("age").getAsInt()+"岁");
		rBar.setRating(housekeeper.get("star").getAsInt());
		ImageLoader.getInstance().displayImage(housekeeper.get("image").getAsString(), image,
				ImageLoadUtil.optionsRoundCorner_Person);
		image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(housekeeper!=null &&
						housekeeper.has("image") &&
						housekeeper.get("image").getAsString()!=null &&
						housekeeper.get("image").getAsString().toLowerCase().startsWith("http")){
					Intent intent = new Intent(context, ImageViewerActivity.class);
					intent.putExtra(BaseConstants.PARAMS_URL,housekeeper.get("image").getAsString());
					context.startActivity(intent);
				}else{
					BaseApplication.getInstances().toast(HouseKeeperOrderActivity.this,"该医护人员未设置头像!");
				}
			}
		});
		/**
		 * 赋值订单下信息
		 */
		orderDetail.addProperty("housekeeper_id",housekeeper.get("id").getAsString());
		orderDetail.addProperty("company_name",housekeeper.get("company_name").getAsString());
		orderDetail.addProperty("housekeeper_name",housekeeper.get("name").getAsString());
		orderDetail.addProperty("image",housekeeper.get("image").getAsString());
		orderDetail.addProperty("price",housekeeper.get("price").getAsString());
		orderDetail.addProperty("age",housekeeper.get("age").getAsString());
		orderDetail.addProperty("service_item",housekeeper.get("service_item").getAsString());
		orderDetail.addProperty("experience",housekeeper.get("experience").getAsString());
		orderDetail.addProperty("from_region",housekeeper.get("from_region").getAsString());
		orderDetail.addProperty("evaluation",(String)null);
	}

	/**
	 * 获取短信验证码
	 */
	protected void getSmsCode(){
		if(et_mobile.getText().toString()==null || et_mobile.getText().toString().length()<1){
			BaseApplication.getInstances().toast(HouseKeeperOrderActivity.this,"请填写手机号码!");
			return;
		}

		if(!CheckUtil.isPhoneNumber(et_mobile.getText().toString())){
			BaseApplication.getInstances().toast(HouseKeeperOrderActivity.this,"请填写有效的手机号码!");
			return;
		}
		HashMap<String, String> params = new HashMap<>();
		params.put("mobile", et_mobile.getText().toString());
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				HttpResponseResult result = new HttpResponseResult(val);
				if(result.getCode().equals("200")){
					lastSMSPhone = et_mobile.getText().toString();
					showSmsCodeTime();
					et_code.setTag(result.getMessage());
				}else {
					if(result.getMessage()!=null && result.getMessage().length()>0 && (result.getMessage().contains("发送上限") || result.getMessage().contains("验证码超出"))){
						Toast.makeText(context, "超出验证码当天发送上限"/*"获取验证码失败，请重试！"*/, Toast.LENGTH_SHORT).show();
					}else if(result.getMessage()!=null && result.getMessage().length()>0){
						Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(context, "获取验证码失败，请重试！", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}, HttpUrls.getSMSCode(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(context)));
	}

	protected void showSmsCodeTime(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					do {
						Thread.sleep(1000);
						total--;
						Message msg = new Message();
						msg.what = 1;
						msg.arg1 = total;
						mHandler.sendMessage(msg);
					} while (total > 0);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 下订单
	 */
	String pay_channel = "1";
	public static int wxPayStatus = 0;
	public static boolean wxPayJumpFlag = false;
	private String wxOrderID;
	protected void orderHouseKeeper(){
		/**
		 * 订单详情中的开始结束日期
		 */
		orderDetail.addProperty("begindate",tv_begin_date_value.getTag()+"");
		orderDetail.addProperty("enddate",tv_end_date_value.getTag()+"");
		orderDetail.addProperty("order_user_name",et_contact_name.getText().toString());
		orderDetail.addProperty("mobile",et_mobile.getText().toString());
		orderDetail.addProperty("address",et_address.getText().toString());

		if(tv_begin_date_value.getTag() == null || tv_end_date_value.getTag() == null){
			Toast.makeText(context, "请选择开始或结束日期", Toast.LENGTH_SHORT).show();
			return;
		}

        //*******************************************
        //校验开始结束日期的先后 1
//		try {
//			if(simpleDateFormat.parse(tv_end_date_value.getTag().toString()).getTime()<
//                    simpleDateFormat.parse(tv_begin_date_value.getTag().toString()).getTime()){
//				Toast.makeText(context, "结束日期应该在开始日期之后", Toast.LENGTH_SHORT).show();
//				return;
//            }
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
        //校验开始结束日期的先后 2
        if (Long.parseLong(tv_end_date_value.getTag().toString().replace("-","")) <
                Long.parseLong(tv_begin_date_value.getTag().toString().replace("-",""))
                ) {
            Toast.makeText(context, "结束日期应该在开始日期之后", Toast.LENGTH_SHORT).show();
            return;
        }
        //*******************************************


		if(et_mobile.length() == 0 || et_contact_name.length() == 0 || et_address.length() == 0){
			Toast.makeText(context, "请填写联系人姓名，电话，地址等", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!CheckUtil.isPhoneNumber(et_mobile.getText().toString())){
			Toast.makeText(context, "请填写正确的联系人手机号码!", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!CheckUtil.isAllChineseChar(et_contact_name.getText().toString())){
			Toast.makeText(context, "请填写正确的联系人姓名!", Toast.LENGTH_SHORT).show();
			return;
		}
//		if(lastSMSPhone==null){
//			Toast.makeText(HouseKeeperOrderActivity.this, "请获取并填写验证码!", Toast.LENGTH_SHORT).show();
//			return;
//		}
//		if(!lastSMSPhone.equals(et_mobile.getText().toString())){
//			Toast.makeText(HouseKeeperOrderActivity.this, "手机号码与获取验证码的手机号码不一致!", Toast.LENGTH_SHORT).show();
//			return;
//		}
//		if (CheckUtil.isEmpty(et_code.getText().toString()) || !et_code.getText().toString().equals(et_code.getTag().toString())) {
//			Toast.makeText(context, getResources().getString(R.string.setting_password_check9), Toast.LENGTH_SHORT).show();
//			return;
//		}

		if(rbtn_1.isChecked()){
			pay_channel = "1";
		}else if(rbtn_2.isChecked()){
			pay_channel = "2";
		}else if(rbtn_3.isChecked()){
			pay_channel = "0";
		}
		HashMap<String, String> params = new HashMap<>();
//		params.put("housekeeper_id", getIntent().getStringExtra("id"));
//		params.put("begindate", tv_begin_date_value.getTag()+"");
//		params.put("enddate", tv_end_date_value.getTag()+"");
//		params.put("order_user_name", et_contact_name.getText().toString());
//		params.put("mobile", et_mobile.getText().toString());
//		params.put("address", et_address.getText().toString());
//		params.put("pay_channel", pay_channel);
		//添加支付信息值
		int serviceMonth = QuanjiakanUtil.getMonthSpace(tv_begin_date_value.getTag()+"", tv_end_date_value.getTag()+"");
		final double total_fee =
//				0.01;
				200;
		String subject = "家政服务预付款";
		String body = "家政服务预付款200元(服务周期"+serviceMonth+"个月)";
//		params.put("subject", subject);
//		params.put("body", "Book a housekeeper");
//		params.put("total_fee", total_fee+"");
		totel_fee = total_fee;
//		{"note":"","housekeeperId":1,"begindate":"2016-01-01 00:00:00","enddate":"2016-02-02 00:00:00","orderUserName":"asdf","mobile":"18675513625","address":"","orderUserId":1,"userId":10041,"paymentChannel":1}
		String jsonString = "{\"note\":"+ "\"\"" +",\"housekeeperId\":"+getIntent().getStringExtra("id")+ ",\"companyId\":"+ getIntent().getStringExtra("company_id")+
				",\"begindate\":"+"\""+tv_begin_date_value.getTag()+"\""+",\"enddate\":"+"\""+tv_end_date_value.getTag()+"\""+",\"orderUserName\":"+"\""+et_contact_name.getText().toString()+"\""+
				",\"mobile\":"+"\""+et_mobile.getText().toString()+"\""+",\"address\":"+"\""+et_address.getText().toString()+"\""+",\"orderUserId\":"+QuanjiakanSetting.getInstance().getUserId()+",\"userId\":"+
				QuanjiakanSetting.getInstance().getUserId()+",\"paymentChannel\":"+pay_channel+"}";

		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				if(val!=null && val.length()>0){
					HttpResponseResult result = new HttpResponseResult(val);
					if(result.isResultOk()){
//						JsonObject object = null;
//						JsonObject paidinfo = null;
						JsonObject object1 = result.getObject();
						String message = object1.get("message").getAsString();
						object = new GsonParseUtil(message).getJsonObject();
//						if(!"0".equals(pay_channel)){
//							object = result.getObject();
//							paidinfo = object.get("paidinfo").getAsJsonObject();
//
//							/**
//							 * 赋值订单信息
//							 */
//							orderDetail.addProperty("id",object.get("message").getAsString());
//							orderDetail.addProperty("orderid",object.get("orderId").getAsString());
//							orderDetail.addProperty("createtime",sdf.format(new Date()));
//						}

//						object = result.getObject();
						if(!"0".equals(pay_channel)){
							paidinfo = object.get("paidinfo").getAsJsonObject();
						}


						/**
						 * 赋值订单信息
						 */
//						orderDetail.addProperty("id",object.get("message").getAsString());
						orderDetail.addProperty("id",message);
						orderDetail.addProperty("orderid",object.get("orderid").getAsString());
						orderDetail.addProperty("createtime",sdf.format(new Date()));

						if("1".equals(pay_channel)){
							//支付宝支付
							aliPay(object.get("orderid").getAsString(), total_fee, paidinfo, "1");
						}else if ("2".equals(pay_channel)) {
							//微信支付
							wxPayJumpFlag = true;
							wxOrderID = object.get("orderid").getAsString();
							HouseKeeperParamEntity entity = new HouseKeeperParamEntity();
							entity.setType("微信支付");
							entity.setOrderid(object.get("orderid").getAsString());
							entity.setTotal_fee(total_fee);
							new WeixinPayHandler(context, WXPayEntryActivity.FROM_HOUSE_KEEPER_PAY,entity).pay(paidinfo);
						}else if("0".equals(pay_channel)){
							getRemainSum(object.get("orderid").getAsString());
//							vertifyWalletPayment(object.get("orderId").getAsString());
//							BaseApplication.getInstances().toast("进入钱包支付,后续步骤需要确定后完善!");
						}
					}else {
						Toast.makeText(context, "预约失败", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}, HttpUrls.orderHouseKeeper() + "&data="+jsonString, params, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(context)));
	}

	private double totel_fee;
	/**
	 * 支付宝支付
	 * @param orderid
	 * @param total_fee
	 * @param paidinfo
	 * @param pay_channel
	 */
	protected void aliPay(final String orderid,final double total_fee,JsonObject paidinfo,final String pay_channel){
		//支付总额
		new AlipayHandler(context, new AlipayCallback() {

			@Override
			public void paidComplete(int type, String msg, String result) {
				// TODO Auto-generated method stub
				if(type == AlipayHandler.PAID_SUCCESS){
					//支付成功，并将支付提交提交到后台
					JsonObject object = new GsonParseUtil(result).getJsonObject();
					vertifyAliPayment(object.get("orderid").getAsString(), object.get("result").getAsString(),total_fee,pay_channel);
				}else if (type == AlipayHandler.PAID_FAILED) {
					//支付失败
					orderDetail.addProperty("status","1");
					Intent intent = new Intent(context,PaymentResultActivity.class);
					intent.putExtra(PaymentResultActivity.PARAMS_DATA,orderDetail.toString());
					intent.putExtra("type", "1".equals(pay_channel)?"支付宝":"微信支付");
					intent.putExtra("flag", -1);
					intent.putExtra("total_fee", total_fee);
					intent.putExtra("orderid", orderid);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivityForResult(intent, CommonRequestCode.REQUEST_PAY);
				}
			}
		}).pay(paidinfo);
	}


	/**
	 * 提交支付结果到客户端，并验证
	 * @param orderid
	 * @param info
	 * @param total
	 */
	protected void vertifyAliPayment(final String orderid, final String info, final double total, final String pay_channel){
		HashMap<String, String> params = new HashMap<>();
//		params.put("orderId", orderid);
//		params.put("user_id", BaseApplication.getInstances().getUser_id());
		String jsonString = "{\"userId\":"+ QuanjiakanSetting.getInstance().getUserId()+",\"orderid\":"+"\""+orderid+"\""+"}";
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {

			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				if(val!=null && val.length()>0){
					HttpResponseResult result = new HttpResponseResult(val);
					Intent intent = new Intent(context,PaymentResultActivity.class);

					intent.putExtra("type", /*"1".equals(pay_channel)?"支付宝":"微信支付"*/"支付宝");
					intent.putExtra("orderid", orderid);
					intent.putExtra("total_fee", total);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					if(result.getCode().equals("200")){
						orderDetail.addProperty("status","2");
						flag = true;
						intent.putExtra("flag", 1);//支付成功
					}else {
						orderDetail.addProperty("status","1");
						flag = false;
						intent.putExtra("flag", 0);
					}
					intent.putExtra(PaymentResultActivity.PARAMS_DATA,orderDetail.toString());
					startActivityForResult(intent,CommonRequestCode.REQUEST_PAY);
				}

			}
		}, HttpUrls.getVetifyHousePayment()+"&data="+jsonString, params, Task.TYPE_GET_STRING_NOCACHE, null));
	}

	public void vertifyWechatPayment(final String orderid){
		HashMap<String, String> params = new HashMap<>();
//		params.put("orderId", orderid);
//		params.put("user_id", BaseApplication.getInstances().getUser_id());
		String jsonString = "{\"userId\":"+ QuanjiakanSetting.getInstance().getUserId()+",\"orderid\":"+"\""+orderid+"\""+"}";
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {

			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				if(val!=null && val.length()>0){
					HttpResponseResult result = new HttpResponseResult(val);
					Intent intent = new Intent(context,PaymentResultActivity.class);

					intent.putExtra("type", /*"1".equals(pay_channel)?"支付宝":*/"微信支付");
					intent.putExtra("orderid", orderid);
					intent.putExtra("total_fee", totel_fee);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					if(result.getCode().equals("200")){
						orderDetail.addProperty("status","2");
						flag = true;
						intent.putExtra("flag", 1);//支付成功
					}else {
						orderDetail.addProperty("status","1");
						flag = false;
						intent.putExtra("flag", 0);
					}
					intent.putExtra(PaymentResultActivity.PARAMS_DATA,orderDetail.toString());
					startActivityForResult(intent,CommonRequestCode.REQUEST_PAY);
				}

			}
		}, HttpUrls.getVetifyHousePayment() + "&data="+jsonString, params, Task.TYPE_GET_STRING_NOCACHE, null));
	}

	public void getRemainSum(final String orderid){

		HashMap<String, String> params = new HashMap<>();
		MyHandler.putTask(HouseKeeperOrderActivity.this,new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				try {
					if(val!=null && val.length()>0 && !"null".equals(val.toLowerCase())){
						JSONObject jsonObject = new JSONObject(val);
						showMyWalletDialog(jsonObject.getString("member_wallet"),orderid);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		},HttpUrls.getWalletNumber()+"&member_id="+BaseApplication.getInstances().getUser_id(),params,Task.TYPE_GET_STRING_NOCACHE,null));
	}

	/**
	 * 显示自己的钱包的对话框
	 */
	Dialog myWalletDialog;
	public void showMyWalletDialog(final String payNumber,final String orderid) {
		myWalletDialog =  new Dialog(this, R.style.dialog_loading);
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_doctor_mywallet_pay, null);
		RelativeLayout rl_exit = (RelativeLayout) view.findViewById(R.id.rl_exit);
		rl_exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (myWalletDialog != null) {
					myWalletDialog.dismiss();
				}
				BaseApplication.getInstances().toast(HouseKeeperOrderActivity.this,"已取消支付!");
			}
		});
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setVisibility(View.VISIBLE);
		title.setText("支付明细");
		//real_pay_value
		TextView remaining_sum_value = (TextView) view.findViewById(R.id.remaining_sum_value);
		remaining_sum_value.setText(""+payNumber);

		TextView real_pay_value = (TextView) view.findViewById(R.id.real_pay_value);
		final TextView hint = (TextView) view.findViewById(R.id.hint);
		final String memory = 200+"";

		hint.setText("账户余额不足，请更换微信或支付宝支付!");
		if (Double.parseDouble(memory) <= Double.parseDouble(payNumber)) {
			hint.setVisibility(View.GONE);
		} else {
			hint.setVisibility(View.VISIBLE);
		}
//		final String memory = 0.01+"";
		//------------------------
		real_pay_value.setText(memory+"");

		TextView confirm = (TextView) view.findViewById(R.id.confirm);
		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (myWalletDialog != null) {
					myWalletDialog.dismiss();
				}
				/**
				 * 开始支付
				 */
				if(Double.parseDouble(200+"")<=Double.parseDouble(payNumber)){
					vertifyWalletPayment(orderid);
				}else{
					hint.setVisibility(View.VISIBLE);
					BaseApplication.getInstances().toast(HouseKeeperOrderActivity.this,"余额不足，请换微信或支付宝支付!");
				}
				/**
				 * 跳过支付，
				 */
			}
		});
		WindowManager.LayoutParams lp = myWalletDialog.getWindow().getAttributes();
		lp.width = QuanjiakanUtil.dip2px(this, 300);
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.BOTTOM;

		myWalletDialog.setContentView(view, lp);
		myWalletDialog.show();
	}

	public void vertifyWalletPayment(final String orderid){
		HashMap<String, String> params = new HashMap<>();
//		params.put("orderId", orderid);
//		params.put("user_id", BaseApplication.getInstances().getUser_id());
		String jsonString = "{\"userId\":"+ QuanjiakanSetting.getInstance().getUserId()+",\"orderid\":"+"\""+orderid+"\""+"}";
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {

			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				if(val!=null && val.length()>0 && !"null".equals(val.toLowerCase())){
					HttpResponseResult result = new HttpResponseResult(val);
					Intent intent = new Intent(context,PaymentResultActivity.class);

					intent.putExtra("type", /*"0".equals(pay_channel)?"支付宝":"微信支付"*/"钱包支付");
					intent.putExtra("orderid", orderid);
					intent.putExtra("total_fee", totel_fee);
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					if(result.getCode().equals("200")){
						orderDetail.addProperty("status","2");
						flag = true;
						intent.putExtra("flag", 1);//支付成功
					}else {
						BaseApplication.getInstances().toastLong(HouseKeeperOrderActivity.this,""+result.getMessage());
						flag = false;
						orderDetail.addProperty("status","1");
						intent.putExtra("flag", 0);
					}
					intent.putExtra(PaymentResultActivity.PARAMS_DATA,orderDetail.toString());
					startActivityForResult(intent,CommonRequestCode.REQUEST_PAY);
				}

			}
		}, HttpUrls.getVetifyHousePayment() + "&data="+jsonString, params, Task.TYPE_GET_STRING_NOCACHE, null));
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(this.getClass().getSimpleName());
	}

	public static final int WX_PAY_INIT = 0;
	public static final int WX_PAY_OK = 1;
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
		BaseApplication.getInstances().setCurrentActivity(this);
		if(wxPayJumpFlag){//从微信支付返回
			wxPayJumpFlag = false;
			if(wxPayStatus == WX_PAY_OK){
				vertifyWechatPayment(wxOrderID);
			}else {
				BaseApplication.getInstances().toast(HouseKeeperOrderActivity.this,"微信支付失败!");
			}
			wxPayStatus = WX_PAY_INIT;
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int id = arg0.getId();
		if(id == R.id.tv_order){
			if (flag){
				BaseApplication.getInstances().toast(HouseKeeperOrderActivity.this,"您已完成预约！");
			}else {
				orderHouseKeeper();
			}

		}else if (id == R.id.tv_code) {
			getSmsCode();
		}else if (id == R.id.tv_begin_date_value) {
			//显示开始时间
			showDay(tv_begin_date_value);
		}else if (id == R.id.tv_end_date_value) {
			//显示结束时间
			showDay(tv_end_date_value);
		}else if (id == R.id.ibtn_back) {
			finish();
		}else if(id == R.id.tv_detail){
			showDetailDiaolog();
		}else if(id == R.id.menu_text){
			showNoticeDialog();
		}
	}

	public void showDetailDiaolog(){
		/**
		 * 展示对话框
		 */
		detailDialog = QuanjiakanDialog.getInstance().getDialog(HouseKeeperOrderActivity.this);
		View view = LayoutInflater.from(HouseKeeperOrderActivity.this).inflate(R.layout.dialog_housekeeper_detail,null);
		/**
		 * 数据赋值
		 */
		ImageView exit = (ImageView) view.findViewById(R.id.exit);
		exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				detailDialog.dismiss();
			}
		});
		//总价
		TextView expense = (TextView) view.findViewById(R.id.expense);
		//单位
		TextView unit = (TextView) view.findViewById(R.id.unit);
		//价格组成
		TextView count = (TextView) view.findViewById(R.id.count);

//			//费用包含---标题
//			TextView include_header = (TextView) view.findViewById(R.id.include_header);

		//费用包含---明细
		TextView include_value = (TextView) view.findViewById(R.id.include_value);
		include_value.setText(housekeeper.get("service_item").getAsString());

//			//费用不含---标题
//			TextView exclude_header = (TextView) view.findViewById(R.id.exclude_header);

		//费用不含---明细
		TextView exclude_value = (TextView) view.findViewById(R.id.exclude_value);
		exclude_value.setText("其他事项支出");

		WindowManager.LayoutParams lp = detailDialog.getWindow().getAttributes();
		lp.width = QuanjiakanUtil.dip2px(context, 300);
		lp.height = lp.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;
		detailDialog.setContentView(view, lp);
		detailDialog.setCanceledOnTouchOutside(false);
		detailDialog.show();
	}

	protected void showDay(final TextView tv){
		ChangeBirthDialog day_dialog = new ChangeBirthDialog(context);
		day_dialog.show();
		day_dialog.setBirthdayListener(new OnBirthListener() {

			@Override
			public void onClick(String year, String month, String day) {
				// TODO Auto-generated method stub
				if (Integer.parseInt(month) < 10) {
					month = "0" + month;
				}
				if (Integer.parseInt(day) < 10) {
					day = "0" + day;
				}
				tv.setText(year+"/"+month+"/"+day);
				tv.setTag(year+"-"+month+"-"+day);
			}
		});
	}

	private Dialog noticeDialog;
	public void showNoticeDialog(){
		if(noticeDialog!=null && noticeDialog.isShowing()){
			noticeDialog.dismiss();
		}else{
			noticeDialog = new Dialog(this,R.style.dialog_loading);
			View view = LayoutInflater.from(HouseKeeperOrderActivity.this).inflate(R.layout.dialog_medicine_introduce,null);
			/**
			 * 数据赋值
			 */
			RelativeLayout rl_exit = (RelativeLayout) view.findViewById(R.id.rl_exit);
			rl_exit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					noticeDialog.dismiss();
				}
			});
			TextView title = (TextView) view.findViewById(R.id.title);
			title.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
			title.setText("用户须知");//
			TextView include_value = (TextView) view.findViewById(R.id.indroduce);
			include_value.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
			include_value.setText(R.string.house_keeper_user_notice);

			WindowManager.LayoutParams lp = noticeDialog.getWindow().getAttributes();
			lp.width = QuanjiakanUtil.dip2px(context, 300);
			lp.height = lp.WRAP_CONTENT;
			lp.gravity = Gravity.CENTER;
			noticeDialog.setContentView(view, lp);
			noticeDialog.setCanceledOnTouchOutside(false);
			noticeDialog.show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode){
			case CommonRequestCode.REQUEST_PAY:
				if(resultCode==CommonRequestCode.RESULT_BACK_TO_MAIN){
					setResult(CommonRequestCode.RESULT_BACK_TO_MAIN);
					finish();
				}
				break;
			default:
				break;
		}
	}
}
