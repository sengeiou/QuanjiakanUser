package com.androidquanjiakan.activity.index.doctor;

import java.util.ArrayList;
import java.util.List;

import com.androidquanjiakan.activity.index.free.FreeInquiryActivity_CreatePatientProblem;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.CommonRequestCode;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.adapter.DoctorListAdapter;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * TODO 特定科室下的医生列表
 */
public class ConsultantEnterActivity extends BaseActivity implements OnClickListener{

	private PullToRefreshListView listView;
	private DoctorListAdapter mAdapter;
	private List<JsonObject> mList = new ArrayList<JsonObject>();
	private ImageButton ibtn_back;
	private TextView tv_title;

	private String classify_id;
	private String classify;
	public static final String PARAMS_CLASS = "params_classify";

	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long position) {

			/**
			 * 处理方式之一：已经付费的用户不再进入详情页面，直接进入聊天
			 */
			final JsonObject jsonObject = mAdapter.getmList().get((int)position);
			int paycount = 0;
			if(jsonObject.has("paid") && !(jsonObject.get("paid") instanceof JsonNull)){
				paycount = jsonObject.get("paid").getAsInt();
			}
			LogUtil.e("支付次数："+paycount);
			if(paycount>0){
				Intent intent = new Intent(ConsultantEnterActivity.this,DoctorDetailActivity.class);
				intent.putExtra("doctor_id", mList.get((int)position).get("id").getAsString());
				intent.putExtra(DoctorDetailActivity.PARAMS_PAYNUMBER,paycount);
				startActivity(intent);
			}else{
				Intent intent = new Intent(ConsultantEnterActivity.this,DoctorDetailActivity.class);
				intent.putExtra("doctor_id", mList.get((int)position).get("id").getAsString());
				startActivity(intent);
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_consultant_enter);
		classify_id = this.getIntent().getStringExtra(PARAMS_CLASS);
		if(classify_id==null){
			classify_id = "1";
		}
		initTitleBar();
		initView();
	}

	private ImageView nonedata;
	private TextView nonedatahint;
	public void showNoneData(boolean isShow){
		if(isShow){
			nonedatahint.setText("暂无医生");
			nonedata.setVisibility(View.VISIBLE);
			nonedatahint.setVisibility(View.VISIBLE);
		}else{
			nonedata.setVisibility(View.GONE);
			nonedatahint.setVisibility(View.GONE);
		}
	}

	public void initTitleBar(){
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("医护列表");
		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		ibtn_back.setOnClickListener(this);
		ibtn_back.setVisibility(View.VISIBLE);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void initView(){
		nonedata = (ImageView) findViewById(R.id.nonedata);
		nonedatahint = (TextView) findViewById(R.id.nonedatahint);
		showNoneData(false);
		listView = (PullToRefreshListView)findViewById(R.id.listview);
		mAdapter = new DoctorListAdapter(ConsultantEnterActivity.this, mList);
		listView.setAdapter(mAdapter);
//		listView.setOnItemClickListener(onItemClickListener);
		listView.setOnRefreshListener(new OnRefreshListener2() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				mList.clear();
				loadDoctorList();
			}
			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				loadMoreDoctorList();
			}
		});
		loadDoctorList();
	}

	/**
	 * TODO 需要根据分类获取医生数据
	 */
	private int pageIndex = 1;
	protected void loadDoctorList(){
		pageIndex = 1;
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			
			@Override
			public void handMsg(String val) {
				if(val!=null && val.length()>0){
					JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
					JsonArray docList = jsonObject.get("rows").getAsJsonArray();
					List<JsonObject> dataList = new ArrayList<JsonObject>();
					for(int i=0;i<docList.size();i++){
						JsonObject temp = docList.get(i).getAsJsonObject();
						/**
                         * 过滤无职称医生
						 */
						if(!"无职称".equals(temp.get("title").getAsString())){
							dataList.add(docList.get(i).getAsJsonObject() /*new GsonParseUtil(val).getJsonObject()*/);
						}else{

						}
					}
					LogUtil.w("---docList:"+docList.size());
					if(docList!=null && docList.size()==20){
						listView.setMode(PullToRefreshBase.Mode.BOTH);
					}else{
						listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
					}
					mList.clear();
					mList.addAll(dataList);
					if (mAdapter==null){
						mAdapter = new DoctorListAdapter(ConsultantEnterActivity.this, mList);
//						listView.setOnItemClickListener(onItemClickListener);
						listView.setAdapter(mAdapter);
					}else {
						mAdapter.setmList(mList);
						mAdapter.notifyDataSetChanged();
					}
					if(mList!=null && mList.size()>0){
						showNoneData(false);
					}else{
						showNoneData(true);
					}
				}else{
					if(mList!=null && mList.size()>0){
						showNoneData(false);
					}else{
						showNoneData(true);
					}
				}
				listView.onRefreshComplete();

			}
		}, HttpUrls.getDoctorList()+"&clinic="+classify_id+"&doctor_type=2&rows=20&page="+pageIndex, null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(ConsultantEnterActivity.this)));
	}

	protected void loadMoreDoctorList(){
		pageIndex++;
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {

			@Override
			public void handMsg(String val) {
				listView.onRefreshComplete();
				if(val!=null && val.length()>0){
					JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
					JsonArray docList = jsonObject.get("rows").getAsJsonArray();
					List<JsonObject> dataList = new ArrayList<JsonObject>();
					for(int i=0;i<docList.size();i++){
						dataList.add(docList.get(i).getAsJsonObject());
					}
					LogUtil.w("***docList:"+docList.size());
					if(docList!=null && docList.size()==20){
						listView.setMode(PullToRefreshBase.Mode.BOTH);
					}else{
						listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
					}
					mList.addAll(dataList);
					mAdapter.setmList(mList);
					mAdapter.notifyDataSetChanged();
				}else{
					BaseApplication.getInstances().toast(ConsultantEnterActivity.this,"服务器无响应或返回数据异常!");
				}
			}
		}, HttpUrls.getDoctorList()+"&clinic="+classify_id+"&doctor_type=2" +
				"&rows=20&page="+pageIndex, null, Task.TYPE_GET_STRING_NOCACHE,
				QuanjiakanDialog.getInstance().getDialog(ConsultantEnterActivity.this)));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(this.getClass().getSimpleName());
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
		BaseApplication.getInstances().setCurrentActivity(this);

		if(JMessageClient.getMyInfo()==null){
			JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX+BaseApplication.getInstances().getUser_id(), CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
				@Override
				public void gotResult(int i, String s) {
					if(i==0){
					}else{
					}
				}
			});
		}else{
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()){
			case R.id.tv:
				Intent intent = new Intent(ConsultantEnterActivity.this,FreeInquiryActivity_CreatePatientProblem.class);
				intent.putExtra("doctor_id", "");
				startActivity(intent);
				break;
			case R.id.ibtn_back:
				finish();
				break;
			default:
				break;
		}
	}
	
}
