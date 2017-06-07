package com.androidquanjiakan.activity.setting.message;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.androidquanjiakan.adapter.ConversionListAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.entity.BaseHttpResultEntity_List;
import com.androidquanjiakan.entity.FreeInquiryProblemEntity;
import com.androidquanjiakan.entity.ProblemEntity;
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
import com.quanjiakanuser.receiver.NotificationClickEventReceiver;
import com.quanjiakanuser.util.GsonParseUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import cn.jmessage.android.uikit.chatting.ChatActivity;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

import static com.quanjiakanuser.receiver.NotificationClickEventReceiver.registerMessageCallback;

public class ProblemListActivity extends BaseActivity implements OnClickListener{
	
	protected PullToRefreshListView listView;
	protected Context context;
	protected Message m = null;
	protected ImageButton ibtn_back;
	protected ArrayList<JsonObject> problemList = new ArrayList<>();
	public static final String KEY_PROBLEM_STRING = "problem_list_key_doctor";
	protected TextView tv_title;

	private ConversionListAdapter adapter;
	private List<ProblemEntity> list;

//	private SwipeRefreshLayout fresh;

	public static final int REQUEST_IM = 1028;

	public static final int REQUEST_APPAND = 1030;

	private int entryType = 0;
	public static final String PARAMS_ENTRY_TYPE = "entryType";

	private JsonObject price;
	private JsonObject doctor;
	private JsonObject status;
	private String isPay;
	private String type;
	private Long millisecond;
	private JsonObject paidinfo;
	private float servicePrice;
	private float platformPrice;
	private float doctorPrice;
	private JsonObject object;


	/**
	 *
	 * @param savedInstanceState
     */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = ProblemListActivity.this;
		setContentView(R.layout.layout_problemlist);
		entryType = getIntent().getIntExtra(PARAMS_ENTRY_TYPE,0);
		registerMessageCallback(ProblemListActivity.class.getName(), new NotificationClickEventReceiver.MessageReceiveCallBack() {
			@Override
			public void messageReceived(MessageEvent event) {
				final String fromid = event.getMessage().getFromID();
				if(event.getMessage().getTargetType()== ConversationType.single && "Doc".startsWith(fromid)){
					loadData();
				}else{
					//避免由於直播送禮造成的連續接口訪問
				}
			}
		});
		initView();
	}

	public static String printMessage(cn.jpush.im.android.api.model.Message msg){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\nFromAppKey:"+msg.getFromAppKey());
		stringBuilder.append("\nFromType:"+msg.getFromType().toString());
		stringBuilder.append("\nTargetAppKey:"+msg.getTargetAppKey());
		stringBuilder.append("\nContentType:"+msg.getContentType().toString());
		stringBuilder.append("\ngetContent().toJson():"+msg.getContent().toJson());

//		stringBuilder.append("\nFromType:"+msg);
		return stringBuilder.toString();
	}


	/**
	 * 初始化页面
	 */
	protected void initView(){

		nonedata = (ImageView) findViewById(R.id.nonedata);
		nonedatahint = (TextView) findViewById(R.id.nonedatahint);
		showNoneData(false);

		problemList.clear();
		problemList.addAll(QuanjiakanSetting.getInstance().getProblemList());
		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		if(entryType==0){
			ibtn_back.setVisibility(View.VISIBLE);
		}else{
			ibtn_back.setVisibility(View.GONE);
		}
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("消息");
		ibtn_back.setOnClickListener(this);

//		fresh = (SwipeRefreshLayout) findViewById(R.id.fresh);
//		fresh.setColorSchemeResources(R.color.holo_blue_light);
//		fresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//			@Override
//			public void onRefresh() {
//				loadData();
//			}
//		});
		listView = (PullToRefreshListView)findViewById(R.id.listview);
		listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		list  = new ArrayList<ProblemEntity>();
		adapter = new ConversionListAdapter(this,list);
		listView.setAdapter(adapter);
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadData();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			}
		});
		loadData();
	}



	BaseHttpResultEntity_List<FreeInquiryProblemEntity> inquiryList;
	public void loadFreeInquiryList(){
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				listView.onRefreshComplete();
				if(val!=null && val.length()>0 &&!"null".equals(val.toLowerCase())){
					inquiryList = (BaseHttpResultEntity_List<FreeInquiryProblemEntity>) SerialUtil.jsonToObject(val,new TypeToken<BaseHttpResultEntity_List<FreeInquiryProblemEntity>>(){}.getType());
					initClassifyData_Net();
//					sortInfo();
				}else{
					initClassifyData_Net();
//					sortInfo();
				}
			}
		}, HttpUrls.getGetChunyuProblemDetail(),null,Task.TYPE_GET_STRING_NOCACHE,QuanjiakanDialog.getInstance().getDialog(this)));
	}

	public void loadData(){
		if(entryType!=0){
			if(JMessageClient.getMyInfo()!=null){
				loadFreeInquiryList();
			}else{
				JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX+BaseApplication.getInstances().getUser_id(), CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
					@Override
					public void gotResult(int i, String s) {
						loadFreeInquiryList();
					}
				});
			}
		}
		/**
		 * 加载名字咨询的对话---存在的问题是，这里的会话没有该医生是否过期的状态
		 */
	}

	private List<JsonObject> dataList = new ArrayList<JsonObject>();
	public void initClassifyData_Net(){
		count = 0;
		sortInfo();
	}

	private int count = 0;
	public void sortInfo(){
		count++;
		list.clear();
		List<Conversation> list_conversation =  JMessageClient.getConversationList();
		if(list_conversation==null){
			list_conversation = new ArrayList<Conversation>();
			if(count<3){
				JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX+BaseApplication.getInstances().getUser_id(), CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
					@Override
					public void gotResult(int i, String s) {
						if(i==0){
							count=5;
						}
						sortInfo();
					}
				});
				return;//防止显示出现问题
			}
		}else{
			for(int i = list_conversation.size()-1;i>-1;i--){
				//去除非医生聊天数据
				if(list_conversation.get(i).getLatestMessage()==null || list_conversation.get(i).getTargetId().equals("juguikeji")){
					list_conversation.remove(i);
				}
				//去除群聊数据
				else if(!(list_conversation.get(i).getTargetInfo() instanceof UserInfo)){
					list_conversation.remove(i);
				}
			}
		}

		Collections.sort(list_conversation, new Comparator<Conversation>() {
			@Override
			public int compare(Conversation conversation, Conversation t1) {
				if(conversation.getLatestMessage().getCreateTime()<t1.getLatestMessage().getCreateTime()){
					return 1;
				}else if(conversation.getLatestMessage().getCreateTime()>t1.getLatestMessage().getCreateTime()){
					return -1;
				}else{
					return 0;
				}

			}
		});


		/**
		 * TODO 排序后的收费消息
		 */
		if(dataList!=null && dataList.size()>0){
			for(Conversation conversation:list_conversation){
				for(int i=dataList.size()-1;i>-1;i--){
					if(dataList.get(i).get("id").getAsString().equals(conversation.getTargetId())){
						ProblemEntity entity = new ProblemEntity();
						entity.setmConversation(conversation);
						this.list.add(entity);
						dataList.remove(i);
						break;
					}else{
						continue;
					}
				}
			}
		}else{
			for(Conversation conversation:list_conversation){
				ProblemEntity entity = new ProblemEntity();
				entity.setmConversation(conversation);
				this.list.add(entity);
			}
		}

		/**
		 * 春雨医生问题列表
		 */
		if(inquiryList!=null && inquiryList.getRows()!=null){
			List<FreeInquiryProblemEntity> data = inquiryList.getRows();
			Collections.sort(data, new Comparator<FreeInquiryProblemEntity>() {
				@Override
				public int compare(FreeInquiryProblemEntity patientProblemInfoEntity, FreeInquiryProblemEntity t1) {
					if(Long.parseLong(patientProblemInfoEntity.getCreated_time_ms())>Long.parseLong(t1.getCreated_time_ms())){
						return -1;
					}else{
						return 1;
					}
				}
			});

			for(FreeInquiryProblemEntity conversation:data){
				ProblemEntity entity = new ProblemEntity();
				entity.setProblemInfoEntity(conversation);
				this.list.add(entity);
			}
		}

		adapter.resetData(list);
		adapter.notifyDataSetChanged();

		if(list!=null && list.size()>0){
			showNoneData(false);
		}else{
			showNoneData(true);
		}

	}

	private ImageView nonedata;
	private TextView nonedatahint;
	public void showNoneData(boolean isShow){
		if(isShow){
			nonedatahint.setText("暂无消息");
			nonedata.setVisibility(View.VISIBLE);
			nonedatahint.setVisibility(View.VISIBLE);
		}else{
			nonedata.setVisibility(View.GONE);
			nonedatahint.setVisibility(View.GONE);
		}
	}

	private ProblemEntity mConv;
	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub

			mConv = adapter.getData().get((int)arg3);
			checkStatus(mConv.getmConversation().getTargetId());

			if ("1".equals(isPay)&&"3".equals(type)){
				if(mConv.getmConversation()!=null) {
					Intent intent = new Intent(ProblemListActivity.this, ChatActivity.class);
					intent.putExtra(ChatActivity.TARGET_ID, mConv.getmConversation().getTargetId());//10053;10043
					intent.putExtra(ChatActivity.TARGET_APP_KEY, mConv.getmConversation().getTargetAppKey());//10053;10043
					intent.putExtra(ChatActivity.MILLISECOND,millisecond);
					intent.putExtra(ChatActivity.TYPE,type);
					startActivityForResult(intent, REQUEST_IM);

				}
			}
		}
	};
	protected void checkStatus(String tempDoctor_id) {
		//http://192.168.0.140:8080/quanjiakan/api?m=doctor&a=checkifisnormal&userId=10749&doctorId=11016
		HashMap<String, String> params = new HashMap<>();
//        params.put("userId",BaseApplication.getInstances().getUser_id());
//        params.put("doctorId",tempDoctor_id);
		String entity = "&user_id="+BaseApplication.getInstances().getUser_id()+"&doctor_id="+tempDoctor_id.substring(3,tempDoctor_id.length());
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				if(null==val||"".equals(val)){
					BaseApplication.getInstances().toast(ProblemListActivity.this,"网络连接错误！");
					return;
				}else {
					status = new GsonParseUtil(val).getJsonObject();
					isPay = status.get("isPay").getAsString();
//                    {"servicePrice":"15.00","platformPrice":"6.00","doctorPrice":"9.00","isPay":"0"}
					servicePrice = status.get("servicePrice").getAsFloat();
					platformPrice = status.get("platformPrice").getAsFloat();
					doctorPrice = status.get("doctorPrice").getAsFloat();
					if("1".equals(isPay)){
						type = status.get("type").getAsString();//状态 0未支付  1已支付  2付费未回复 3开始聊天
						millisecond = status.get("millisecond").getAsLong();//聊天剩余时间
					}
				}
			}
		},HttpUrls.checkChatStatus()+entity,params,Task.TYPE_GET_STRING_NOCACHE,null));

	}

	private Dialog dialog;
	private TextView content_question;
	private TextView content_answer;
	public void showFreeInquiryDialog(final String question,final String answer){
		dialog = QuanjiakanDialog.getInstance().getDialog(this);
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_inquiry_detail,null);
		content_question = (TextView) view.findViewById(R.id.content_question);
		content_answer = (TextView) view.findViewById(R.id.content_answer);
		content_question.setText(question);
		content_answer.setText(answer);
		dialog.setContentView(view);
		dialog.setCanceledOnTouchOutside(true);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
		BaseApplication.getInstances().setCurrentActivity(this);
//		problemList.clear();
//		problemList.addAll(QuanjiakanSetting.getInstance().getProblemList());
//		mAdapter.notifyDataSetChanged();
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
		int id = R.id.ibtn_back;
		if(id == R.id.ibtn_back){
			finish();
		}		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode){
			case REQUEST_APPAND:
				if(resultCode==RESULT_OK){
					loadData();
				}
				break;
			case REQUEST_IM:
//				if(resultCode==RESULT_OK) {
//
//				}
//				initClassifyData_Net();
				loadData();
				break;
			default:
				break;
		}
	}
}