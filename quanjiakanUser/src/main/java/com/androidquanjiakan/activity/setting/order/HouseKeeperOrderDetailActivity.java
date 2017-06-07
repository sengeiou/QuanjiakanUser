package com.androidquanjiakan.activity.setting.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidquanjiakan.activity.common.ImageViewerActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.util.LogUtil;
import com.umeng.analytics.MobclickAgent;

public class HouseKeeperOrderDetailActivity extends BaseActivity implements OnClickListener{

	protected TextView tv_name,tv_level,
			tv_age,tv_price,
			tv_company,tv_region,tv_experience,
			tv_order_name,tv_order_address;
	protected ImageView image;
	protected Context context;
	protected RatingBar rBar;
	private TextView tv_rate_key;

	private TextView tv_title;
	protected ImageButton ibtn_back;

	private String jsonObject;
	private JsonObject json;
	public static final String PARAMS_ENTITY = "params_entity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.layout_housekeeper_order_detail);
		jsonObject = getIntent().getStringExtra(PARAMS_ENTITY);
		LogUtil.e("订单详情信息:"+jsonObject.toString());
		json = new GsonParseUtil(jsonObject).getJsonObject();
		initView();
	}

	/**
	 * 初始化页面
	 */
	protected void initView(){
		ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
		ibtn_back.setVisibility(View.VISIBLE);
		ibtn_back.setOnClickListener(this);
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("预约详情");

		image = (ImageView)findViewById(R.id.image);
		tv_name = (TextView)findViewById(R.id.tv_name);
		tv_level = (TextView)findViewById(R.id.tv_level);
		tv_level.setVisibility(View.GONE);//这个字段暂时没有
		tv_age = (TextView)findViewById(R.id.tv_age);
		tv_price = (TextView)findViewById(R.id.tv_price);

		tv_company = (TextView)findViewById(R.id.tv_company);
		tv_region = (TextView)findViewById(R.id.tv_region);
		tv_experience = (TextView)findViewById(R.id.tv_experience);


		tv_order_name = (TextView)findViewById(R.id.tv_order_name);
		tv_order_address = (TextView)findViewById(R.id.tv_order_address);
		rBar = (RatingBar)findViewById(R.id.rbar);
		tv_rate_key = (TextView) findViewById(R.id.tv_rate_key);

//		loadInfo();
		bind(json);
	}
	
	protected void loadInfo(){
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			
			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				JsonObject object = new GsonParseUtil(val).getJsonObject();
				if(object.has("id")){
					bind(object);
				}
			}
		}, HttpUrls.getHouseKeeperDetail(getIntent().getStringExtra("id")), null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(context)));
	}
	
	/**
	 * @param housekeeper
	 */
	protected void bind(final JsonObject housekeeper){
		tv_name.setText(housekeeper.get("housekeeper_name").getAsString());
		tv_name.setTag(housekeeper.get("id").getAsString());

//		int level = housekeeper.get("level").getAsInt();
//		switch (level){
//			case 1:
//				tv_level.setText("初级保姆");
//				break;
//			case 2:
//				tv_level.setText("中级保姆");
//				break;
//			case 3:
//				tv_level.setText("高级保姆");
//				break;
//			default:
//				tv_level.setText("初级保姆");
//				break;
//		}
		if(!housekeeper.has("price") || housekeeper.get("price").getAsString()==null || housekeeper.get("price").getAsString().length()<1){
			tv_price.setVisibility(View.GONE);
		}else{
			tv_price.setText("价格:"+housekeeper.get("price").getAsString()+"元/月");
		}
		tv_age.setText("年龄:"+housekeeper.get("age").getAsInt()+"岁");

		if(housekeeper.has("star") && housekeeper.get("star").getAsString()!=null &&  housekeeper.get("star").getAsString().length()>1){
			int star = housekeeper.get("star").getAsInt();
			rBar.setRating(star);
			rBar.setProgress(housekeeper.get("star").getAsInt());
		}else{
			rBar.setVisibility(View.GONE);
			tv_rate_key.setVisibility(View.GONE);
		}

		if(housekeeper.get("begindate").getAsString().length()>10 && housekeeper.get("enddate").getAsString().length()>10 ){
			tv_company.setText("预约时间:"+housekeeper.get("begindate").getAsString().substring(0,10)+" 到 "+housekeeper.get("enddate").getAsString().substring(0,10));
		}else{
			tv_company.setText("预约时间:"+housekeeper.get("begindate").getAsString()+" 到 "+housekeeper.get("enddate").getAsString());
		}



		int status = housekeeper.get("status").getAsInt();
		if(status==2){//已付款
			/**
			 * 需要给出费用的算式
			 *
			 * housekeeper.get("service_region").getAsString()
			 */
			tv_region.setText("费用合计:"+"");

			/**
			 * housekeeper.get("experience").getAsString()+"年"
			 * 实付定金
			 *
			 * 是否付款
			 */
			tv_experience.setText("是否付款:"+"已付定金");
		}else{
			/**
			 * 需要给出费用的算式
			 *
			 * housekeeper.get("service_region").getAsString()
			 */
			tv_region.setText("费用合计:"+"");

			/**
			 * housekeeper.get("experience").getAsString()+"年"
			 */
			tv_experience.setText("是否付款:"+" 未付款");
		}


		/**
		 * 预约人名称    电话号码
		 */
		tv_order_name.setText(housekeeper.get("order_user_name").getAsString()+"    "+housekeeper.get("mobile").getAsString());

		/**
		 * 预约人地址
		 */
		tv_order_address.setText(housekeeper.get("address").getAsString());


//		ImageLoader.getInstance().displayImage(housekeeper.get("image").getAsString(), image);
		ImageLoadUtil.loadImage(image,housekeeper.get("image").getAsString(),ImageLoadUtil.optionsRoundCorner_Person);
		image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(HouseKeeperOrderDetailActivity.this, ImageViewerActivity.class);
				intent.putExtra(BaseConstants.PARAMS_URL,housekeeper.get("image").getAsString());
				startActivity(intent);
			}
		});
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
		if (id == R.id.ibtn_back) {
			finish();
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
