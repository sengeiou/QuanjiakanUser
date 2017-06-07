package com.androidquanjiakan.activity.setting.delivery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.datahandler.DefaultReceiveAddressHandler;
import com.androidquanjiakan.entity.DeliverAddressEntity;
import com.androidquanjiakan.adapterholder.DeliverAddressHolder;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class DeliveryAddressActivity_Entry extends BaseActivity implements OnClickListener {

	protected ListView listView;
	protected JsonArray delivery_address_list = new JsonArray();
	protected List<DeliverAddressEntity> addresses = new ArrayList<DeliverAddressEntity>();
	protected TextView tv_add,tv_title;
	protected DeliveryAddressAdapter mAdapter;
	protected Context context;
	protected ImageButton ibtn_back;

	private boolean flag = true;

	private static final int requestCode_add = 1010;

	private static final int REQUESTCODE_MODIFY = 1011;

	public static final String PARAM_GET_RETURN = "get_return";
	private boolean getReturn = false;

	/**
	 *
	 *
	 [
	 {
	 "id": "26",
	 "user_id": "10084",
	 "name": "张工",
	 "address": "荔湾区周门北路28号B座5楼",
	 "mobile": "13312345678",
	 "status": "1"
	 },
	 {
	 "id": "27",
	 "user_id": "10084",
	 "name": "詹黑",
	 "address": "犹犹豫豫饿大概要非常放假了付出",
	 "mobile": "13387654321",
	 "status": "1"
	 }
	 ]
	 * @param savedInstanceState
     */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = DeliveryAddressActivity_Entry.this;
		setContentView(R.layout.layout_delivery_address_list);
		getReturn = this.getIntent().getBooleanExtra(PARAM_GET_RETURN,false);
		initView();
	}
	
	protected void initView(){
		tv_title = (TextView)findViewById(R.id.tv_title);
		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		tv_title.setText(getResources().getString(R.string.receive_address));
		ibtn_back.setVisibility(View.VISIBLE);
		ibtn_back.setOnClickListener(this);
		listView = (ListView)findViewById(R.id.listview);
		listView.setOnItemClickListener(itemClickListener);
		tv_add = (TextView)findViewById(R.id.tv_add);
		mAdapter = new DeliveryAddressAdapter();
		listView.setAdapter(mAdapter);
		tv_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(DeliveryAddressActivity_Entry.this,DeliveryAddressActivity_Add.class);
				startActivityForResult(intent, requestCode_add);
			}
		});
		loadAddress();
	}
	
	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
//			JsonObject object = addresses.get(arg2);
//			Intent intent = new Intent();
//			intent.putExtra("address", object+"");
//			setResult(1, intent);
//			finish();
			/**
			 *
			 */
			if(getReturn){
				/**
				 * 设为默认地址，并返回
				 */
				setDefaultAddress(addresses.get(arg2).getId(),true);

			}
		}
	};
	
	protected void loadAddress(){
		HashMap<String, String> params = new HashMap<>();
		params.put("user_id", QuanjiakanSetting.getInstance().getUserId()+"");
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			
			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				if(val!=null && val.length()>0){
					List<DeliverAddressEntity> temp = (List<DeliverAddressEntity>) SerialUtil.jsonToObject(val,new TypeToken<List<DeliverAddressEntity>>(){}.getType());
					addresses.clear();
					addresses.addAll(temp);
					boolean hasDefault = false;
					for(DeliverAddressEntity entity:temp){
						if(entity.isDefault()){
							DefaultReceiveAddressHandler.insertValue(entity);
							hasDefault = true;
							break;
						}
					}
					if(!hasDefault){
						DefaultReceiveAddressHandler.remove();
					}
					mAdapter.notifyDataSetChanged();
				}else{
//					BaseApplication.getInstances().toast(getResources().getString(R.string.error_server_interface));
				}
			}
		}, HttpUrls.getDeliveryAddress(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(context)));
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode){
			case requestCode_add:
				if(resultCode==RESULT_OK){
					loadAddress();
				}
				break;
			case REQUESTCODE_MODIFY:
				if(resultCode==RESULT_OK){
					loadAddress();
				}
				break;
			default:
				break;
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
	
	class DeliveryAddressAdapter extends BaseAdapter{

		protected LayoutInflater inflater;		
		public DeliveryAddressAdapter(){
			inflater = LayoutInflater.from(DeliveryAddressActivity_Entry.this);
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return addresses.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int pos, View view, ViewGroup arg2) {
			// TODO Auto-generated method stub
			final DeliverAddressHolder holder;
			if(view==null){
				holder = new DeliverAddressHolder();
				view = inflater.inflate(R.layout.item_delivery_address, null);
				holder.tv_name_mobile = (TextView)view.findViewById(R.id.tv_name_mobile);
				holder.chk = (CheckBox)view.findViewById(R.id.chk);
				holder.tv_address = (TextView)view.findViewById(R.id.tv_address);
				holder.tv_delete = (TextView)view.findViewById(R.id.tv_delete);
				holder.tv_edit = (TextView) view.findViewById(R.id.tv_edit);
				view.setTag(holder);
			}else{
				holder = (DeliverAddressHolder) view.getTag();
			}
			final DeliverAddressEntity address = addresses.get(pos);
			holder.tv_name_mobile.setText(address.getName() + "  "+address.getMobile());
			holder.tv_address.setText(address.getAddress().replace(";",""));
			holder.chk.setChecked(DeliverAddressEntity.DEFAULT_ADDR.endsWith(address.getStatus()));

			holder.tv_edit.setEnabled(true);
			holder.tv_edit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
//					Toast.makeText(DeliveryAddressActivity_Entry.this, "编辑:暂时无接口支持", Toast.LENGTH_SHORT).show();
//					holder.tv_edit.setEnabled(false);
					/**
					 * 使用删除，后添加的方式折中处理
					 */
					Intent intent = new Intent(DeliveryAddressActivity_Entry.this,DeliveryAddressActivity_Modify.class);
					intent.putExtra(DeliveryAddressActivity_Modify.KEY_OBJECT,address);
					startActivityForResult(intent,REQUESTCODE_MODIFY);
				}
			});
			holder.tv_delete.setEnabled(true);
			holder.tv_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
//					if (flag){
//						deleteAddress(address.getId());
//					}
					holder.tv_delete.setEnabled(false);
					deleteAddress(address.getId());

				}
			});
			holder.chk.setEnabled(true);
			/**
			 * 在有默认地址时变更默认地址
			 * 会导致多两次接口访问-----前提:没有单独的修改接口【暂时修改接口也没有提供】
			 */
//			holder.chk.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//				@Override
//				public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
//					// TODO Auto-generated method stub
////					if(flag){
////						holder.chk.setEnabled(false);
////						(address.getId(),arg1);
////					}
//					holder.chk.setEnabled(false);
//					setDefaultAddress(address.getId(),arg1);
//
//				}
//			});
			holder.chk.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					setDefaultAddress(address.getId(),holder.chk.isChecked());
				}
			});
			return view;
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int id = arg0.getId();
		if(id == R.id.ibtn_back){
			if(DefaultReceiveAddressHandler.getAllValue().size()<1){
				if(addresses==null || addresses.size()<1){
					setResult(RESULT_OK);
					finish();
				}else{
					boolean hasDefault = false;
					for(DeliverAddressEntity entity:addresses){
						if(entity.isDefault()){
							hasDefault = true;
							break;
						}
					}
					if(hasDefault){
						setResult(RESULT_OK);
						finish();
					}
					BaseApplication.getInstances().toast(DeliveryAddressActivity_Entry.this,"请选择一个默认地址!");
				}
			}else{
				setResult(RESULT_OK);
				finish();
			}
		}
	}
	
	/**
	 * 删除地址
	 * @param id
	 */
	public void deleteAddress(String id){
		HashMap<String, String> params = new HashMap<>();
		params.put("id", id);
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			
			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				HttpResponseResult result = new HttpResponseResult(val);
				if(result.getCode().equals("200")){
					loadAddress();
				}
			}
		}, HttpUrls.deleteDeliveryAddress(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(context)));
	}
	
	/**
	 * --------
	 * <b>前提条件，默认地址最多只有一个，可以没有</b>
	 *
	 *
	 * 设置默认收货地址
	 *
	 *
	 *
	 * @param id
	 * @param isDefault
	 */
	public void setDefaultAddress(final String id,final boolean isDefault){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("id", id);
		params.put("default", isDefault? DeliverAddressEntity.DEFAULT_ADDR:DeliverAddressEntity.NOT_DEFAULT_ADDR);
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				HttpResponseResult result = new HttpResponseResult(val);
				if(result.getCode().equals("200")){
					loadAddress();
					if(isDefault){
						for(DeliverAddressEntity entity : addresses){
							if(id.equals(entity.getId())){
								DefaultReceiveAddressHandler.insertValue(entity);
								break;
							}
						}
					}else{
						DefaultReceiveAddressHandler.remove();
					}
				}
			}
		}, HttpUrls.setDefaultDeliveryAddress(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(context)));
	}
	
	public void setFlagTrue(){
		flag = true;
	}
	public void setFlagFalse(){
		flag = false;
	}
}
