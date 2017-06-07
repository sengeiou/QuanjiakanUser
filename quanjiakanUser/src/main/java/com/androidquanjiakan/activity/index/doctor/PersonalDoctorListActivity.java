package com.androidquanjiakan.activity.index.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.adapter.DoctorPersonalListAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.util.LogUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.androidquanjiakan.adapter.DoctorListAdapter;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jmessage.android.uikit.chatting.ChatActivity;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;


public class PersonalDoctorListActivity extends BaseActivity implements OnClickListener{

	private PullToRefreshListView listView;
	private ImageButton ibtn_back;
	private TextView tv_title;
	private View viewTitle;

	private DoctorPersonalListAdapter mAdapter;
	private List<JsonObject> mList = new ArrayList<JsonObject>();

	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long position) {
			//指定医生咨询
//				Intent intent = new Intent(PersonalDoctorListActivity.this,ConsultantEnterActivity.class);
//				startActivity(intent);
//			if(arg2 == 1){
//				//春雨医生入口
//
//			}else{
//
//			}


			jsonObject = mAdapter.getmList().get((int)position);

			HashMap<String, String> params = new HashMap<>();
			String entity = "&user_id="+BaseApplication.getInstances().getUser_id()+"&doctor_id="+jsonObject.get("doctor_id").getAsString();
			MyHandler.putTask(PersonalDoctorListActivity.this,new Task(new HttpResponseInterface() {
				@Override
				public void handMsg(String val) {
					if(null==val||"".equals(val)){
						BaseApplication.getInstances().toast(PersonalDoctorListActivity.this,"网络连接错误！");
						return;
					}else {
						status = new GsonParseUtil(val).getJsonObject();

						servicePrice = status.get("servicePrice").getAsFloat();
						platformPrice =status.get("platformPrice").getAsFloat();
						doctorPrice = status.get("doctorPrice").getAsFloat();
						type = status.get("type").getAsString();//状态 0未支付  1已支付  2付费未回复 3开始聊天
						millisecond = status.get("millisecond").getAsLong();//聊天剩余时间
						final String order_id = status.get("orderid").getAsString();

						if(JMessageClient.getMyInfo()!=null){
							Intent intent = new Intent(PersonalDoctorListActivity.this, ChatActivity.class);

							intent.putExtra(ChatActivity.MILLISECOND,millisecond);
							intent.putExtra(ChatActivity.TYPE,type);
							intent.putExtra(ChatActivity.ORDER_ID,order_id);
							intent.putExtra(ChatActivity.TARGET_ID, "Doc"+ jsonObject.get("doctor_id").getAsString() //暂时没有这个ID的用户，使用固定ID作为替代
			/*"10043" */   );//医生的ID
							intent.putExtra(ChatActivity.TARGET_APP_KEY, BaseConstants.jpush_doctor_appkey);
							startActivity(intent);
						}else{
							JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX+BaseApplication.getInstances().getUser_id(), CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
								@Override
								public void gotResult(int i, String s) {
									LogUtil.e("私人医生跳转:Code:"+i+"\nMessage:"+s);
									if(i==0){
										Intent intent = new Intent(PersonalDoctorListActivity.this, ChatActivity.class);
										intent.putExtra(ChatActivity.MILLISECOND,millisecond);
										intent.putExtra(ChatActivity.TYPE,type);
										intent.putExtra(ChatActivity.ORDER_ID,order_id);
										intent.putExtra(ChatActivity.TARGET_ID, "Doc"+ jsonObject.get("doctor_id").getAsString() //暂时没有这个ID的用户，使用固定ID作为替代
			/*"10043" */   );//医生的ID

										intent.putExtra(ChatActivity.TARGET_APP_KEY, BaseConstants.jpush_doctor_appkey);
										startActivity(intent);
									}else{
										Toast.makeText(PersonalDoctorListActivity.this, "极光IM错误码:"+i+"\n错误信息:"+s, Toast.LENGTH_SHORT).show();
									}
								}
							});
						}

					}
				}
			},HttpUrls.checkChatStatus()+entity,params,Task.TYPE_GET_STRING_NOCACHE,null));
		}
	};
	private JsonObject jsonObject;
	private JsonObject status;
	private String isPay;
	private float servicePrice;
	private float platformPrice;
	private float doctorPrice;
	private String type;
	private long millisecond;


	/**
	 * 检查聊天状态
	 */
	protected void checkStatus() {
		if(jsonObject!=null){
			HashMap<String, String> params = new HashMap<>();
			String entity = "&user_id="+BaseApplication.getInstances().getUser_id()+"&doctor_id="+jsonObject.get("doctor_id").getAsString();
			MyHandler.putTask(this,new Task(new HttpResponseInterface() {
				@Override
				public void handMsg(String val) {
					if(null==val||"".equals(val)){
						BaseApplication.getInstances().toast(PersonalDoctorListActivity.this,"网络连接错误！");
						return;
					}else {
						status = new GsonParseUtil(val).getJsonObject();

						servicePrice = status.get("servicePrice").getAsFloat();
						platformPrice =status.get("platformPrice").getAsFloat();
						doctorPrice = status.get("doctorPrice").getAsFloat();
						type = status.get("type").getAsString();//状态 0未支付  1已支付  2付费未回复 3开始聊天
						millisecond = status.get("millisecond").getAsLong();//聊天剩余时间
					}
				}
			},HttpUrls.checkChatStatus()+entity,params,Task.TYPE_GET_STRING_NOCACHE,null));
		}
	}



	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_consultant_enter);
		initTitle();
		initView();
        /**
		 *
		 */
	}


	private int pageIndex = 1;
	public void initClassifyData_Net(int page){
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {

			@Override
			public void handMsg(String val) {
				listView.onRefreshComplete();
				if(val!=null && val.length()>0){
					mList.clear();
					JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
					JsonArray docList = jsonObject.get("rows").getAsJsonArray();
					List<JsonObject> dataList = new ArrayList<JsonObject>();
					/**
					 * 原去除电话医生数据
					 */
					for(int i=0;i<docList.size();i++){
						JsonObject jsonObject1 = docList.get(i).getAsJsonObject();
						dataList.add(jsonObject1);

					}

					if(dataList!=null && dataList.size()==20){
						listView.setMode(PullToRefreshBase.Mode.BOTH);
					}else{
						listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
					}
//					dataList = district(dataList);//去重
					mList.addAll(dataList);

//				mList.addAll(new GsonParseUtil(val).getJsonList());
					mAdapter.notifyDataSetChanged();

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
			}
		},
				/**
				 * TODO URL地址需要重新变更（不含科室参数则为全体医生数据）
				 */
//				HttpUrls.getPaidDoctorList(),//罗鹏
//				HttpUrls.getPaidDoctorList_origin(),//付凯
				HttpUrls.getPaidDoctorList_new()+"&page="+pageIndex+"&rows=20",//家碧
				null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(this)));
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

	public List<JsonObject> district(List<JsonObject> data){
		if(data!=null){
			int i = 0;
			int j = data.size()-1;
			for(i = 0;i<data.size();i++){
				JsonObject jsonObject_i = data.get(i);
				for(j = data.size()-1;j>i;j--){
					JsonObject jsonObject_j = data.get(j);
					/**
					 * 罗鹏
					 */
//					if(jsonObject_i.get("member_id").getAsString().equals(jsonObject_j.get("member_id").getAsString())){
//						data.remove(j);
//					}else{
//						continue;
//					}
					/**
					 * 付凯
					 */
					if(jsonObject_i.get("id").getAsString().equals(jsonObject_j.get("id").getAsString())){
						data.remove(j);
					}else{
						continue;
					}
				}
				if(i==j-1){
					break;
				}else{
					continue;
				}
			}
		}
		return data;
	}

	public void initTitle(){
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("私人医生");
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
		if(mList==null){
			mList = new ArrayList<>();
		}else{
			mList.clear();
		}
//		initClassifyData();

		/**
		 * TODO 获取网络数据，需要接口联通了并开放了，才能使用
		 */
		mAdapter = new DoctorPersonalListAdapter(PersonalDoctorListActivity.this,mList);

		listView.setAdapter(mAdapter);
		listView.setMode(PullToRefreshBase.Mode.BOTH);
//		listView.setOnItemClickListener(onItemClickListener);
		listView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				pageIndex = 1;
				initClassifyData_Net(pageIndex);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				pageIndex++;
				initClassifyData_Net(pageIndex);
			}
		});
		/**
		 * 获取网络分类
		 */
		pageIndex = 1;
		initClassifyData_Net(pageIndex);
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
		//checkStatus();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
		BaseApplication.getInstances().setCurrentActivity(this);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		checkStatus();
	}

	@Override
	public void onClick(View arg0) {
		int id = arg0.getId();
		if(id == R.id.tv){
			//进入到创建问题的页面
		}else if (id == R.id.ibtn_back) {
			finish();
		}else if(id == R.id.floating){

		}
	}

}
