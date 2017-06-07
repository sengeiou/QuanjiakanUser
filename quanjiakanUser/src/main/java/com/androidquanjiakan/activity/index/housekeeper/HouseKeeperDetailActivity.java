package com.androidquanjiakan.activity.index.housekeeper;

import com.androidquanjiakan.activity.common.ImageViewerActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.LogUtil;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class HouseKeeperDetailActivity extends BaseActivity implements OnClickListener{

	protected TextView tv_title,tv_name,tv_level,tv_age,tv_price,tv_order,tv_company,tv_region,
			tv_experience,tv_items,tv_company_name,tv_address,tv_service_items;
	protected ImageView image_call,image;
	protected Context context;
	protected RatingBar rBar;
	protected TextView tv_buy;
	protected ImageButton ibtn_back;
	private JsonObject object;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.layout_housekeeper_detail);
		initTitleBar();
		initView();
	}

	public void initTitleBar(){
		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		ibtn_back.setVisibility(View.VISIBLE);
		ibtn_back.setOnClickListener(this);
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("人员详情");
	}

	/**
	 * 初始化页面
	 */
	protected void initView(){
		image = (ImageView)findViewById(R.id.image);
		tv_name = (TextView)findViewById(R.id.tv_name);
		tv_level = (TextView)findViewById(R.id.tv_level);
		tv_age = (TextView)findViewById(R.id.tv_age);
		tv_price = (TextView)findViewById(R.id.tv_price);
		tv_order = (TextView)findViewById(R.id.tv_order);
		tv_order.setVisibility(View.GONE);
		tv_company = (TextView)findViewById(R.id.tv_company);
		tv_region = (TextView)findViewById(R.id.tv_region);
		tv_experience = (TextView)findViewById(R.id.tv_experience);
		tv_items = (TextView)findViewById(R.id.tv_items);
		tv_company_name = (TextView)findViewById(R.id.tv_name_company);
		tv_address = (TextView)findViewById(R.id.tv_address_company);
		tv_service_items = (TextView)findViewById(R.id.tv_service_items);
		rBar = (RatingBar)findViewById(R.id.rbar);	
		tv_buy = (TextView)findViewById(R.id.tv_buy);
		tv_buy.setOnClickListener(this);
		image_call = (ImageView)findViewById(R.id.image_call);
		loadInfo();
		getCompanyPhoneNumber();
	}
	
	protected void loadInfo(){
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			
			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				object = new GsonParseUtil(val).getJsonObject();
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
		tv_name.setText(housekeeper.get("name").getAsString());
		tv_name.setTag(housekeeper.get("id").getAsString());
		tv_buy.setTag(housekeeper);

		if(housekeeper.has("price") && housekeeper.get("price").getAsString()!=null && housekeeper.get("price").getAsString().length()>0){
			tv_price.setText("价格:"+housekeeper.get("price").getAsString()+"元/月");
		}else{
			tv_price.setText("");
		}
		tv_age.setText("年龄:"+housekeeper.get("age").getAsInt()+"岁");
		if(housekeeper.has("mobile") && housekeeper.get("mobile").getAsString()!=null && housekeeper.get("mobile").getAsString().length()>0){
			tv_order.setText("电话  "+housekeeper.get("mobile").getAsString());
			tv_order.setTag(housekeeper.get("mobile").getAsString());
			tv_order.setOnClickListener(this);
			tv_order.setVisibility(View.GONE);
		}else{
			tv_order.setVisibility(View.GONE);
		}
		rBar.setProgress(housekeeper.get("star").getAsInt());
		tv_company.setText("公司:"+"广州市巨硅信息科技有限公司");
		tv_region.setText("区域:"+housekeeper.get("service_region").getAsString());
		tv_experience.setText("工龄:"+housekeeper.get("experience").getAsString()+"年");
		tv_service_items.setText(housekeeper.get("service_item").getAsString());
		tv_company_name.setText(/*housekeeper.get("company_name").getAsString()*/"广州市巨硅信息科技有限公司");
//		tv_items.setText("服务范围:"+housekeeper.get("service_item").getAsString());
		int star = housekeeper.get("star").getAsInt();
		image_call.setTag(housekeeper.get("phone").getAsString());
//		tv_address.setText(housekeeper.get("address").getAsString());
		tv_address.setText("广州市荔湾区周门北路28号荔天大厦5楼全层");
		image_call.setOnClickListener(this);
		rBar.setRating(star);
		int level = housekeeper.get("level").getAsInt();
		switch (level){
			case 1:
				tv_level.setText("初级保姆");
				break;
			case 2:
				tv_level.setText("中级保姆");
				break;
			case 3:
				tv_level.setText("高级保姆");
				break;
			default:
				break;
		}
		int model = housekeeper.get("model").getAsInt();
		switch (model){
			case 1:
				tv_items.setText("是否住家:不住家");
				break;
			default:// model  0  是住家
				tv_items.setText("是否住家:住家");
				break;
		}
		ImageLoader.getInstance().displayImage(housekeeper.get("image").getAsString(), image,
				ImageLoadUtil.optionsRoundCorner_Person);
		image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(housekeeper.get("image").getAsString()!=null && housekeeper.get("image").getAsString().toLowerCase().startsWith("http")){
					Intent intent = new Intent(HouseKeeperDetailActivity.this, ImageViewerActivity.class);
					intent.putExtra(BaseConstants.PARAMS_URL,housekeeper.get("image").getAsString());
					startActivity(intent);
				}else{
					BaseApplication.getInstances().toast(HouseKeeperDetailActivity.this,"该家政人员无头像!");
				}
			}
		});
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
		int id = arg0.getId();
		if(id == R.id.tv_buy){
			Intent intent = new Intent(this, HouseKeeperOrderActivity.class);
			intent.putExtra("id", tv_name.getTag()+"");
			intent.putExtra("housekeeper", arg0.getTag()+"");
			intent.putExtra("company_id",object.get("company_id").getAsString());
			startActivityForResult(intent, CommonRequestCode.REQUEST_PAY);
		}else if (id == R.id.ibtn_back) {
			finish();
		}else if (id == R.id.tv_order) {//修正后，该组件不再显示
			if(arg0.getTag()!=null && arg0.getTag().toString().length()>0){
				Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+arg0.getTag()));
				startActivity(intent);
			}else{
				BaseApplication.getInstances().toast(HouseKeeperDetailActivity.this,"商家未提供电话号码!");
			}
		}else if(id == R.id.image_call){
//			if(arg0.getTag()!=null && arg0.getTag().toString().length()>0){
//				Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+arg0.getTag()));
//				startActivity(intent);
//			}else{
//				BaseApplication.getInstances().toast("商家未提供电话号码!");
//			}
			if(compangPhone!=null && isCalled){
				Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+compangPhone));
				startActivity(intent);
			}else{
				getCompanyPhoneNumber();
			}
		}
	}

	private boolean isCalled = false;
	private String compangPhone;
	public void getCompanyPhoneNumber(){
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				JsonObject object = new GsonParseUtil(val).getJsonObject();
				if(object.has("data")){
					compangPhone = object.get("data").getAsString();
					if(isCalled){
						Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+compangPhone));
						startActivity(intent);
					}
					isCalled = true;
				}
			}
		}, HttpUrls.getHouseKeeperPhone(), null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(context)));
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
