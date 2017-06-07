package com.androidquanjiakan.activity.setting.other;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.entity.FamilyPhoneNumberHolder;
import com.androidquanjiakan.entity.FamilyPhoneNumberEntity;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.umeng.analytics.MobclickAgent;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FamilyPhoneNumberActivity extends BaseActivity {

	private SwipeRefreshLayout fresh;

	protected ListView listView;
	protected Context mContext;
	protected TextView tv_title;
	protected List<FamilyPhoneNumberEntity> contacts_unadd = new ArrayList<FamilyPhoneNumberEntity>();
	protected List<FamilyPhoneNumberEntity> contacts_added = new ArrayList<FamilyPhoneNumberEntity>();
	protected ContactAdapter mAdapter;
	protected ImageButton ibtn_back;
	protected static final String key_added_contact = "added_contact";
	protected static final String key_added_contact_phone = "added_contact_phone";
	protected String addedString = "";
	protected String addedPhoneString = "";

	private LinearLayout container;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = FamilyPhoneNumberActivity.this;
		setContentView(R.layout.layout_problemlist);
		initView();
	}

	protected void initView(){
		tv_title = (TextView)findViewById(R.id.tv_title);
		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		ibtn_back.setVisibility(View.VISIBLE);
		ibtn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		tv_title.setText("亲情号码");

		fresh = (SwipeRefreshLayout) findViewById(R.id.fresh);
		fresh.setColorSchemeResources(R.color.holo_blue_light,R.color.holo_green_light,R.color.holo_orange_light);
		fresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				/**
				 * TODO 刷新获取已经绑定的用户数据--目前没有这个接口
				 */
				fresh.setRefreshing(false);
			}
		});
		listView = (ListView)findViewById(R.id.listview);
		mAdapter = new ContactAdapter();
		listView.setAdapter(mAdapter);
		initAddedContact();
		getPhoneContacts();
	}
	
	View header;
	protected void initAddedContact(){
		addedString = QuanjiakanSetting.getInstance().getValue(key_added_contact);
		addedPhoneString = QuanjiakanSetting.getInstance().getValue(key_added_contact_phone);
		LogUtil.w("addedPhoneString:"+addedPhoneString);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		header = inflater.inflate(R.layout.item_qinqinghaoma_added, null);
		container = (LinearLayout)header.findViewById(R.id.head_list);
	}
	
	/**
	 * header
	 */
	protected void refreshHeaderView(){
		addedString = "";
		addedPhoneString = "";
		int size = contacts_added.size();
		container.removeAllViews();
		for (int i = 0; i < size; i++) {
			final FamilyPhoneNumberEntity object = contacts_added.get(i);
			final int position = i;
			final View view = LayoutInflater.from(FamilyPhoneNumberActivity.this).inflate(R.layout.item_contact,null);
			((TextView)view.findViewById(R.id.tv_name)).setText(object.getName());
			((TextView)view.findViewById(R.id.tv_phone)).setText(object.getPhoneNumber());
			((TextView)view.findViewById(R.id.tv_button_added)).setVisibility(View.VISIBLE);
			((TextView)view.findViewById(R.id.tv_button_add)).setVisibility(View.GONE);
			if(i<size-1){
				view.findViewById(R.id.tv_line).setVisibility(View.VISIBLE);
				view.findViewById(R.id.tv_line_bottom).setVisibility(View.GONE);
			}else{
				view.findViewById(R.id.tv_line).setVisibility(View.GONE);
				view.findViewById(R.id.tv_line_bottom).setVisibility(View.VISIBLE);
			}
			view.findViewById(R.id.tv_button_added).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					removePhone(contacts_added.get(position),position);
				}
			});
			view.findViewById(R.id.tv_line_bottom).setVisibility(View.GONE);
			addedString += object.getName()+",";
			addedPhoneString += object.getPhoneNumber()+","+object.getName()+";";
			container.addView(view);
		}
		if(!addedString.equals("")){
			header.setVisibility(View.VISIBLE);
			if(listView.getHeaderViewsCount()<1){
				listView.addHeaderView(header);
			}
		}else {
			header.setVisibility(View.GONE);
			if(listView.getHeaderViewsCount()>0){
				listView.removeHeaderView(header);
			}
		}
		QuanjiakanSetting.getInstance().setValue(key_added_contact, addedString);
		QuanjiakanSetting.getInstance().setValue(key_added_contact_phone, addedPhoneString);
		LogUtil.w("addedPhoneString（reset）:"+addedPhoneString);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(this.getClass().getSimpleName());
		BaseApplication.getInstances().setCurrentActivity(this);
	}

	class ContactAdapter extends BaseAdapter{

		LayoutInflater inflater;
		
		public ContactAdapter(){
			inflater = LayoutInflater.from(getApplicationContext());
		}		
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return contacts_unadd.size();
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
			final FamilyPhoneNumberHolder holder;
			if(view==null){
				holder = new FamilyPhoneNumberHolder();
				view = inflater.inflate(R.layout.item_contact, null);
				holder.tv_name = (TextView)view.findViewById(R.id.tv_name);
				holder.tv_phone = (TextView)view.findViewById(R.id.tv_phone);
				holder.tv_button_added = (TextView)view.findViewById(R.id.tv_button_added);
				holder.tv_button_add = (TextView)view.findViewById(R.id.tv_button_add);
				holder.tv_line = view.findViewById(R.id.tv_line);
				holder.tv_line_bottom = view.findViewById(R.id.tv_line_bottom);
				view.setTag(holder);
			}else{
				holder = (FamilyPhoneNumberHolder) view.getTag();
			}
			final FamilyPhoneNumberEntity object = contacts_unadd.get(pos);
			holder.tv_name.setText(object.getName());
			holder.tv_phone.setText(object.getPhoneNumber());
			if(!object.isAdd()){//未添加
				holder.tv_button_add.setVisibility(View.VISIBLE);
				holder.tv_button_added.setVisibility(View.GONE);
			}else {//--已添加
				holder.tv_button_add.setVisibility(View.GONE);
				holder.tv_button_added.setVisibility(View.VISIBLE);
			}
			holder.tv_button_add
					.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					addPhone(object,pos);
				}
			});
			if(pos<getCount()-1){
				holder.tv_line.setVisibility(View.VISIBLE);
				holder.tv_line_bottom.setVisibility(View.GONE);
			}else{
				holder.tv_line.setVisibility(View.GONE);
				holder.tv_line_bottom.setVisibility(View.VISIBLE);
			}
			return view;
		}		
	}

	protected void addPhone(final FamilyPhoneNumberEntity object,final int pos){
		final HashMap<String, String> params = new HashMap<>();
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			
			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				HttpResponseResult result = new HttpResponseResult(val);
				if(result.isResultOk()){
					FamilyPhoneNumberEntity entity = new FamilyPhoneNumberEntity();
					entity.setAdd(true);
					entity.setName(object.getName());
					entity.setPhoneNumber(object.getPhoneNumber());
					contacts_unadd.remove(pos);
					contacts_added.add(entity);
					mAdapter.notifyDataSetChanged();
					refreshHeaderView();
				}
			}
		}, HttpUrls.addPhone(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(mContext)));
	}
	
	protected void removePhone(final FamilyPhoneNumberEntity object, final int pos){
		HashMap<String, String> params = new HashMap<>();
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			
			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				HttpResponseResult result = new HttpResponseResult(val);
				if(result.isResultOk()){
					FamilyPhoneNumberEntity entity = new FamilyPhoneNumberEntity();
					entity.setAdd(false);
					entity.setName(object.getName());
					entity.setPhoneNumber(object.getPhoneNumber());
//					contacts_unadd.set(pos, entity);
					contacts_added.remove(pos);
					contacts_unadd.add(entity);
					mAdapter.notifyDataSetChanged();
					refreshHeaderView();
				}
			}
		}, HttpUrls.removePhone(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(mContext)));
	}
	
	private static final String[] PHONES_PROJECTION = new String[] {Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };
	/**联系人显示名称**/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;	 
	/**电话号码**/
    private static final int PHONES_NUMBER_INDEX = 1;	
    
	/**得到手机通讯录联系人信息**/
	/**
	 * TODO 需要有个接口判断号码是否被添加成了亲情号码  或直接返回添加的亲情号码信息
	 *
	 *
	 */
    private void getPhoneContacts() {
		ContentResolver resolver = mContext.getContentResolver();
		// 获取手机联系人
		LogUtil.w("校验是否为已添加用户:"+addedPhoneString);
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);
		if (phoneCursor != null) {
		    while (phoneCursor.moveToNext()) {		
				//得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				//当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
				    continue;		
				//得到联系人名称
				String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
				FamilyPhoneNumberEntity object = new FamilyPhoneNumberEntity();
				object.setPhoneNumber(phoneNumber);
				object.setName(contactName);
				if(addedPhoneString.contains(phoneNumber+","+contactName)){
					object.setAdd(true);
				}else {
					object.setAdd(false);
				}
				LogUtil.w(object.toString());
				if(addedPhoneString.contains(object.getPhoneNumber()+","+object.getName())){
//					(object.getPhoneNumber()+","+object.getName()).equals(addedPhoneString)
					contacts_added.add(object);
				}else{
					contacts_unadd.add(object);
				}
			}
		    phoneCursor.close();
		    mAdapter.notifyDataSetChanged();
			refreshHeaderView();
		}
    }	
	
}
