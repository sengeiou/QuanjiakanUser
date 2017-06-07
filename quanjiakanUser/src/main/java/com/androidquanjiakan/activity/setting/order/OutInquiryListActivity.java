package com.androidquanjiakan.activity.setting.order;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquanjiakan.activity.index.out.ConsultantActivity_GoOut_Order_Complete;
import com.androidquanjiakan.activity.index.out.ConsultantActivity_GoOut_Order_Publish;
import com.androidquanjiakan.activity.index.out.ConsultantActivity_GoOut_Order_Take;
import com.androidquanjiakan.adapter.InquiryListAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.entity.BaseHttpResultEntity;
import com.androidquanjiakan.entity.InquiryListEntity;
import com.androidquanjiakan.entity.PublishInfoEntity;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutInquiryListActivity extends BaseActivity implements OnClickListener{
	
	protected PullToRefreshListView listView;
	protected Context context;
	protected Message m = null;
	protected ImageButton ibtn_back;
	protected ArrayList<JsonObject> problemList = new ArrayList<>();
	public static final String KEY_PROBLEM_STRING = "problem_list_key_doctor";
	protected TextView tv_title;
	private TextView menu_text;

	private int pageIndex = 1;

	private final int REQUEST_COMPLETE = 100;
	private final int REQUEST_TAKE = 101;
	private final int REQUEST_PUBLISH = 102;
	/**
	 *
	 * @param savedInstanceState
     */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = OutInquiryListActivity.this;
		setContentView(R.layout.layout_orderlist);
		initView();

	}

	private ImageView nonedata;
	private TextView nonedatahint;
	public void showNoneData(boolean isShow){
		if(isShow){
			nonedatahint.setText("暂无订单");
			nonedata.setVisibility(View.VISIBLE);
			nonedatahint.setVisibility(View.VISIBLE);
		}else{
			nonedata.setVisibility(View.GONE);
			nonedatahint.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 初始化页面
	 */
	protected void initView(){

		nonedata = (ImageView) findViewById(R.id.nonedata);
		nonedatahint = (TextView) findViewById(R.id.nonedatahint);
		showNoneData(false);
		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		ibtn_back.setVisibility(View.VISIBLE);
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("上门问诊订单");
		ibtn_back.setOnClickListener(this);
		menu_text = (TextView) findViewById(R.id.menu_text);
		menu_text.setText("投诉");
		menu_text.setVisibility(View.VISIBLE);
		menu_text.setOnClickListener(this);

		listView = (PullToRefreshListView)findViewById(R.id.listview);
		listView.setMode(PullToRefreshBase.Mode.BOTH);
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				pageIndex = 1;
				loadData(pageIndex);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				pageIndex++;
				loadData(pageIndex);
			}
		});
		adapter = new InquiryListAdapter(this,new ArrayList<InquiryListEntity>());
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(itemClickListener);

		pageIndex = 1;
		loadData(pageIndex);

		loadPhone();
	}

	BaseHttpResultEntity<List<InquiryListEntity>> orginalData;
	List<InquiryListEntity> data;
	InquiryListAdapter adapter;
	public void loadData(final int page){
		Map<String,String> params = new HashMap<String,String>();
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				listView.onRefreshComplete();
				if(val!=null && val.length()>0 && !"null".equals(val.toLowerCase())){
					JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
					JsonArray datas = jsonObject.get("rows").getAsJsonArray();
 					data = (List<InquiryListEntity>) SerialUtil.jsonToObject(datas.toString(),new TypeToken<List<InquiryListEntity>>(){}.getType());
					if(adapter==null){
						if(data!=null && data.size()>0){
							adapter = new InquiryListAdapter(OutInquiryListActivity.this,data);
							listView.setMode(PullToRefreshBase.Mode.BOTH);
						}else{
							adapter = new InquiryListAdapter(OutInquiryListActivity.this,new ArrayList<InquiryListEntity>());
							listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
						}
					}else{
						if(data!=null && data.size()>0){
							if(page>1){
								adapter.getData().addAll(data);
							}else{
								adapter.setData(data);
							}
							listView.setMode(PullToRefreshBase.Mode.BOTH);
						}else{
							if(page>1){
								adapter.getData().addAll(new ArrayList<InquiryListEntity>());
							}else{
								adapter.setData(new ArrayList<InquiryListEntity>());
							}
							listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
						}

					}
					adapter.notifyDataSetChanged();
				}else{
					if(adapter==null){
						adapter = new InquiryListAdapter(OutInquiryListActivity.this,new ArrayList<InquiryListEntity>());
					}else{
						if(page>1){
							adapter.getData().addAll(new ArrayList<InquiryListEntity>());
						}else{
							adapter.setData(new ArrayList<InquiryListEntity>());
						}
					}
					listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
				}

				if(adapter!=null && adapter.getCount()>0){
					showNoneData(false);
				}else{
					showNoneData(true);
				}
			}
		}, HttpUrls.getPulishOrderList()+"&page="+page ,params,Task.TYPE_GET_STRING_NOCACHE, null));
	}

	public void loadPhone(){
		Map<String,String> params = new HashMap<String,String>();
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				if(val!=null && val.length()>0 && !"null".equals(val.toLowerCase())){
					JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
					dialogPhone = jsonObject.get("data").getAsString();
				}
			}
		}, HttpUrls.getComplainPhone(),params,Task.TYPE_GET_STRING_NOCACHE, null));
	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			InquiryListEntity item = adapter.getData().get((int)arg3);
			if("0".equals(item.getStatus())){
//				holder.status.setText("未付款");
			}else if("1".equals(item.getStatus())){
//				holder.status.setText("未被抢");
				PublishInfoEntity entity = new PublishInfoEntity();
				entity.setOrderid(item.getOrderid());
				if("1".equals(item.getPatient_type())){
					entity.setType(ConsultantActivity_GoOut_Order_Publish.TYPE_NORMAL);
				}else{
					entity.setType(ConsultantActivity_GoOut_Order_Publish.TYPE_HURRY);
				}

				entity.setName(item.getName());
				if("1".equals(item.getSex())){
					entity.setGander("男");
				}else{
					entity.setGander("女");
				}
				entity.setAge(item.getAge());
				entity.setAddr(item.getAddress());
				entity.setPhone(item.getPhone());
				entity.setPublish_time(item.getPublishtime());
				entity.setDescription(item.getDescribe());
				Intent intent = new Intent(OutInquiryListActivity.this,ConsultantActivity_GoOut_Order_Publish.class);
				intent.putExtra(ConsultantActivity_GoOut_Order_Publish.PARAMS_FROM,ConsultantActivity_GoOut_Order_Publish.FROM_PAY);
				intent.putExtra(ConsultantActivity_GoOut_Order_Publish.PARAMS_DATA,entity);
				startActivityForResult(intent,REQUEST_PUBLISH);
			}else if("2".equals(item.getStatus())){
//				holder.status.setText("已被抢");
				/**
				 *
				 */
				Intent intent = new Intent(OutInquiryListActivity.this, ConsultantActivity_GoOut_Order_Take.class);
				intent.putExtra(ConsultantActivity_GoOut_Order_Take.PARAMS_ORDERID,item.getOrderid());
				intent.putExtra(ConsultantActivity_GoOut_Order_Take.PARAMS_TYPE,item.getPatient_type());
				startActivityForResult(intent,REQUEST_TAKE);
			}else if("3".equals(item.getStatus())){
//				holder.status.setText("已取消");

			}else if("4".equals(item.getStatus())){
//				holder.status.setText("未被抢");
				PublishInfoEntity entity = new PublishInfoEntity();
				entity.setOrderid(item.getOrderid());
				if("1".equals(item.getPatient_type())){
					entity.setType(ConsultantActivity_GoOut_Order_Publish.TYPE_NORMAL);
				}else{
					entity.setType(ConsultantActivity_GoOut_Order_Publish.TYPE_HURRY);
				}

				entity.setName(item.getName());
				if("1".equals(item.getSex())){
					entity.setGander("男");
				}else{
					entity.setGander("女");
				}
				entity.setAge(item.getAge());
				entity.setAddr(item.getAddress());
				entity.setPhone(item.getPhone());
				entity.setPublish_time(item.getPublishtime());
				entity.setDescription(item.getDescribe());
				Intent intent = new Intent(OutInquiryListActivity.this,ConsultantActivity_GoOut_Order_Publish.class);
				intent.putExtra(ConsultantActivity_GoOut_Order_Publish.PARAMS_FROM,ConsultantActivity_GoOut_Order_Publish.FROM_LIST);
				intent.putExtra(ConsultantActivity_GoOut_Order_Publish.PARAMS_DATA,entity);
				startActivity(intent);
			}else if("5".equals(item.getStatus())){
//				holder.status.setText("已完成");
				Intent intent = new Intent(OutInquiryListActivity.this, ConsultantActivity_GoOut_Order_Complete.class);
				intent.putExtra(ConsultantActivity_GoOut_Order_Complete.PARAMS_TIME,item.getFinishtime());
				startActivityForResult(intent,REQUEST_COMPLETE);
			}
		}
	};

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
		switch (arg0.getId()){
			case R.id.ibtn_back:
				finish();
				break;
			case R.id.menu_text:
				complain();
				break;
		}
	}

	Dialog dialog;
	String dialogPhone;
	public void complain(){
		dialog = QuanjiakanDialog.getInstance().getDialog(this);
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_inquiry_complain,null);
		RelativeLayout rl_exit = (RelativeLayout) view.findViewById(R.id.rl_exit);
		rl_exit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		final TextView phone = (TextView) view.findViewById(R.id.phone);
		phone.setText(dialogPhone);
		final TextView call = (TextView) view.findViewById(R.id.call);
		call.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+phone.getText().toString().replace("-","")));
				startActivity(intent);
				dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode){
			case REQUEST_COMPLETE:
				if(resultCode==RESULT_OK){
					finish();
				}
				break;
			case REQUEST_TAKE:
				if(resultCode==RESULT_OK){
					loadData(1);
				}
				break;
			case REQUEST_PUBLISH:
				if(resultCode==RESULT_OK){
					loadData(1);
				}
				break;
			default:
				break;
		}
	}
}