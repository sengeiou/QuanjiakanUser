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

import com.androidquanjiakan.activity.index.phonedocter.ConsultantActivity_PhoneDoctor;
import com.androidquanjiakan.adapter.PhoneDoctorOrderListAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.entity.BaseHttpResultEntity_List;
import com.androidquanjiakan.entity.PhoneDoctorRecordEntity;
import com.androidquanjiakan.entity_util.SerialUtil;
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

public class PhoneDoctorOrderListActivity extends BaseActivity implements OnClickListener{
	
	protected PullToRefreshListView listView;
	protected Context context;
	protected Message m = null;
	protected ImageButton ibtn_back;
	protected ArrayList<JsonObject> problemList = new ArrayList<>();
	public static final String KEY_PROBLEM_STRING = "problem_list_key_doctor";
	protected TextView tv_title;
	private TextView menu_text;

	private int pageIndex = 1;

	private final int PAGE_SIZE = 10;

	private final int REQUEST_COMPLETE = 100;
	private final int REQUEST_TAKE = 101;
	/**
	 *
	 * @param savedInstanceState
     */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = PhoneDoctorOrderListActivity.this;
		setContentView(R.layout.layout_orderlist);
		initView();
		showNoneData(false);
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

		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		ibtn_back.setVisibility(View.VISIBLE);
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("儿童医生订单");
		ibtn_back.setOnClickListener(this);
		menu_text = (TextView) findViewById(R.id.menu_text);
		menu_text.setText("投诉");
		menu_text.setVisibility(View.VISIBLE);
		menu_text.setOnClickListener(this);

		listView = (PullToRefreshListView)findViewById(R.id.listview);
		listView.setMode(PullToRefreshBase.Mode.BOTH);
		data = new ArrayList<>();
		adapter = new PhoneDoctorOrderListAdapter(this,data,null);
		listView.setAdapter(adapter);
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				listView.setMode(PullToRefreshBase.Mode.BOTH);
				pageIndex = 1;
				getPhoneDoctorOrder(pageIndex);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				pageIndex++;
				getPhoneDoctorOrder(pageIndex);
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(PhoneDoctorOrderListActivity.this,ConsultantActivity_PhoneDoctor.class);
				intent.putExtra(ConsultantActivity_PhoneDoctor.PARAMS_CLASS, "0");
				startActivity(intent);
			}
		});

		getPhoneDoctorOrder(1);
//		getPhoneDoctorMemony();
		loadPhone();

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

	public void getPhoneDoctorMemony(){
		HashMap<String, String> params = new HashMap<>();
		/**
		 * 获取价格列表
		 *
		 * {"expert":{"week":200,"month":500,"quarter":1500,"halfyear":2800,"year":5000},"director":{"week":150,"month":400,"quarter":1200,"halfyear":1800,"year":3200},"vicedirector":{"week":100,"month":300,"quarter":1000,"halfyear":1500,"year":3000},"phonedoctor":{"year":1000}}
		 */
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {

			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				if(val!=null && val.length()>0){
					/**
					 * 原接口
					 */
					JsonObject temp;
					price = new GsonParseUtil(val).getJsonObject();
					temp = new GsonParseUtil(price.get("phonedoctor").getAsJsonObject().get("year").getAsString()).getJsonObject();
					adapter.setChargeValue(temp.get("total_price").getAsString());
					adapter.notifyDataSetChanged();
				}
			}
		}, HttpUrls.getDoctorPrice(), params, Task.TYPE_POST_DATA_PARAMS, null));
	}


	JsonObject price;
	BaseHttpResultEntity_List<PhoneDoctorRecordEntity> result;
	List<PhoneDoctorRecordEntity> data;
	PhoneDoctorOrderListAdapter adapter;
	public void getPhoneDoctorOrder(final int page){
		Map<String,String> params = new HashMap<String,String>();
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				listView.onRefreshComplete();
				if(val!=null && val.length()>0 && !"null".equals(val.toLowerCase())){
					result = (BaseHttpResultEntity_List<PhoneDoctorRecordEntity>) SerialUtil.jsonToObject(val,
							new TypeToken<BaseHttpResultEntity_List<PhoneDoctorRecordEntity>>(){}.getType());
					data = result.getRows();
					if(page==1){
						adapter.getData().clear();
					}
					if(data!=null && data.size()>0){
						if(data.size()>=PAGE_SIZE){
							listView.setMode(PullToRefreshBase.Mode.BOTH);
						}else{
							listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
						}
						if(adapter!=null){
							adapter.getData().addAll(data);
							adapter.notifyDataSetChanged();
						}else{
							adapter = new PhoneDoctorOrderListAdapter(PhoneDoctorOrderListActivity.this,data,null);
							listView.setAdapter(adapter);
						}
					}else{
						listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
					}

					if(data!=null && data.size()>0){
						showNoneData(false);
					}else{
						showNoneData(true);
					}
				}else{
					if(data!=null && data.size()>0){
						showNoneData(false);
					}else{
						showNoneData(true);
					}
					BaseApplication.getInstances().toast(PhoneDoctorOrderListActivity.this,"接口访问异常!");
				}
				//filter =1 名医    2 电话医生
			}
		}, HttpUrls.getDoctorOrderList()+"&page="+page+"&filter=2&rows="+PAGE_SIZE ,params,Task.TYPE_GET_STRING_NOCACHE, null));
	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
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
			default:
				break;
		}
	}
}