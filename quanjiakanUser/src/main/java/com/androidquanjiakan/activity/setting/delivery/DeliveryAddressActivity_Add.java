package com.androidquanjiakan.activity.setting.delivery;

import java.util.HashMap;
import java.util.List;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
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
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.quanjiakanuser.widget.ChangeAddressDialog;
import com.quanjiakanuser.widget.ChangeAddressDialog.OnAddressCListener;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class DeliveryAddressActivity_Add extends BaseActivity implements OnClickListener {

	protected ImageButton ibtn_menu,ibtn_back;
	protected TextView tv_title,tv_submit,tv_diqu,tv_diqu_key;
	protected EditText et_name,et_address,et_mobile;	
	protected CheckBox chk;
	public static final String key_delivery_address = "key_delivery_address"; 
	protected Context context;
	protected ChangeAddressDialog mChangeAddressDialog;

	private String provins;
	private String city;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.layout_add_delivery_address);
		initView();
	}
	
	protected void initView(){
		tv_title = (TextView)findViewById(R.id.tv_title);
		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		ibtn_menu = (ImageButton)findViewById(R.id.ibtn_menu);
		tv_submit = (TextView)findViewById(R.id.tv_submit);
		tv_diqu = (TextView)findViewById(R.id.tv_diqu);
		et_name = (EditText)findViewById(R.id.et_name);
		et_address = (EditText)findViewById(R.id.et_address);
		et_mobile = (EditText)findViewById(R.id.et_mobile);
		chk = (CheckBox)findViewById(R.id.chk);
		tv_diqu_key = (TextView)findViewById(R.id.tv_diqu_key);
		ibtn_back.setVisibility(View.VISIBLE);
		tv_title.setText("新建收货地址");
		tv_diqu.setOnClickListener(this);
		tv_submit.setOnClickListener(this);
		ibtn_back.setOnClickListener(this);
	}
	
	protected void add(){
		if(et_name.length() == 0 || et_mobile.length() == 0 || et_address.length() == 0){
			Toast.makeText(context, "请填写完善的资料", Toast.LENGTH_SHORT).show();return;
		}
		HashMap<String, String> params = new HashMap<>();
		params.put("user_id", QuanjiakanSetting.getInstance().getUserId()+"");
		params.put("name", et_name.getText().toString());
		params.put("mobile", et_mobile.getText().toString());
		params.put("address", tv_diqu.getTag().toString()+et_address.getText().toString());
		params.put("default", chk.isChecked()==true?"2":"1");
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			
			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				HttpResponseResult result = new HttpResponseResult(val);
				if(result.getCode().equals("200")){
					setResult(RESULT_OK,new Intent());
					finish();
				}else {
					Toast.makeText(context, "提交收货地址信息失败", Toast.LENGTH_SHORT).show();
				}
			}
		}, HttpUrls.addDeliveryAddress(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(context)));
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
			setResult(RESULT_CANCELED,new Intent());
			finish();
		}else if (id == R.id.tv_submit) {
			add();
		}else if (id == R.id.tv_diqu) {
			showDiquDialog();
		}
	}
	
	protected void showDiquDialog(){
		mChangeAddressDialog = new ChangeAddressDialog(context);
		mChangeAddressDialog.setAddress("广东", "广州");
		mChangeAddressDialog.setAddresskListener(new OnAddressCListener() {

			@Override
			public void onClick(String province, String city) {
				// TODO Auto-generated method stub
				// Toast.makeText(PersonalDataActivity.this, province + "-" +
				// city, Toast.LENGTH_LONG).show();
				tv_diqu.setText(province+" "+city);
				tv_diqu.setTag(province+";"+city+";");
			}
		});
		mChangeAddressDialog.show();
	}
	
	
	protected void saveDeliveryAddress(){
		JsonObject object = new JsonObject();
		object.addProperty("name", et_name.getText().toString());
		object.addProperty("mobile", et_mobile.getText().toString());
		object.addProperty("default", chk.isChecked());
		object.addProperty("address", tv_diqu.getTag()+et_address.getText().toString());
		JsonArray addresses = new GsonParseUtil(QuanjiakanSetting.getInstance().getValue(key_delivery_address)).getJsonArray();
		List<JsonObject> objects = QuanjiakanUtil.array2list(addresses);
		if(chk.isChecked()){
			//如果是默认的
			for (int i = 0; i < objects.size(); i++) {
				JsonObject obj = objects.get(i);
				obj.addProperty("default", false);
				objects.set(i, obj);
			}
			objects.add(0, object);
			QuanjiakanSetting.getInstance().setValue(key_delivery_address, QuanjiakanUtil.list2array(objects)+"");
		}else {
			objects.add(0, object);
			QuanjiakanSetting.getInstance().setValue(key_delivery_address, QuanjiakanUtil.list2array(objects)+"");
		}
	}
	
}
