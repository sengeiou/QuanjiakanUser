package com.androidquanjiakan.activity.index.volunteer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ImageButton;

import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;


import com.androidquanjiakan.activity.setting.message.*;
import com.androidquanjiakan.activity.setting.message.VolunteerMessageActivity;
import com.androidquanjiakan.adapter.VolunteerMessageAdapter;
import com.androidquanjiakan.adapter.VolunteerPublishAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;

import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.entity.PublishVolunteerEntity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.swipemenulistview.SwipeMenu;
import com.quanjiakanuser.swipemenulistview.SwipeMenuCreator;
import com.quanjiakanuser.swipemenulistview.SwipeMenuItem;
import com.quanjiakanuser.swipemenulistview.SwipeMenuListView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;




public class VolunteerPublishHistoryActivity extends BaseActivity{


	private ImageButton mBack;
	private TextView mTitle;
	private TextView mMenu;

	private SwipeMenuListView mListView;
	private VolunteerPublishAdapter mAdapter;
	private List<PublishVolunteerEntity>volunteerEntities;
	private int COMPLETED_ORDER=2;
	private SwipeRefreshLayout fresh;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=VolunteerPublishHistoryActivity.this;
		setContentView(R.layout.layout_volunteer_pulish_history);
		initTitle();
		initContent();

	}

	public void initTitle(){
		mBack = (ImageButton) findViewById(R.id.ibtn_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		mTitle = (TextView) findViewById(R.id.tv_title);
		mTitle.setText("发布记录");
		mMenu = (TextView) findViewById(R.id.menu_text);
		mMenu.setText("我的消息");
		mMenu.setVisibility(View.VISIBLE);
		mMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(context, VolunteerMesageActivity.class);
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

	public void initContent(){

		fresh = (SwipeRefreshLayout) findViewById(R.id.fresh);
		fresh.setColorSchemeResources(R.color.holo_blue_light,R.color.holo_green_light,R.color.holo_orange_light,R.color.holo_red_light);
		mListView = (SwipeMenuListView) findViewById(R.id.list);
		SwipeMenuCreator creator=new SwipeMenuCreator(){

			@Override
			public void create(SwipeMenu menu) {
              /*  // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(getActivity().getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("Open");
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);*/


				// create "delete" item
				SwipeMenuItem deleteItem = new SwipeMenuItem(context);
				// set item background
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				// set item width
				deleteItem.setWidth(QuanjiakanUtil.dip2px(context,60));
				// set a icon
				deleteItem.setIcon(R.drawable.ic_delete);
				// add to menu
				menu.addMenuItem(deleteItem);
			}
		};
		mListView.setMenuCreator(creator);
		mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index){
					case 0:
					    //删除该单
						HashMap<String,String> params = new HashMap<>();
						MyHandler.putTask(VolunteerPublishHistoryActivity.this,new Task(new HttpResponseInterface() {
							@Override
							public void handMsg(String val) {
								HttpResponseResult result = new HttpResponseResult(val);
								if(result.isResultOk()) {
								    reloadData();
								}
							}
						},HttpUrls.deleteVoluteerOrder(QuanjiakanSetting.getInstance().getUserId()+"",mAdapter.getData().get(position).getId()),params,Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(context)));
						break;
				}

			}
		});


		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(VolunteerPublishHistoryActivity.this,VoluteerOrderDetailActivity.class);
				intent.putExtra("itemdata",mAdapter.getData().get((int)id));
				startActivityForResult(intent,COMPLETED_ORDER);
			}
		});
		volunteerEntities=new ArrayList<>();
		mAdapter = new VolunteerPublishAdapter(this,volunteerEntities);
		mListView.setAdapter(mAdapter);

		fresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadPage(1);
			}
		});

		pageIndex = 1;
		loadPage(pageIndex);
	}

	private void reloadData() {

		HashMap<String, String> params = new HashMap<>();
		final List<PublishVolunteerEntity> publishVolunteerEntities = new ArrayList<>();
		MyHandler.putTask(VolunteerPublishHistoryActivity.this,new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {

				//fresh.setRefreshing(false);
				//{"rows":[{"id":"36","member_id":"11150","title":"打扫,其他","info":"贤","lat":"39.922019","lng":"116.400535","duration":"0","begintime":"2016-11-18 17:14:00.0","level":"2","address":"北京市东城区东华门街道故宫博物院","target":"2","status":"1","createtime":"2016-11-18 17:15:06.0","hasattend":"0","attended":"0"},{"id":"37","member_id":"11150","title":"打扫","info":"业","lat":"23.138076","lng":"113.244461","duration":"0","begintime":"2016-11-18 17:15:00.0","level":"2","address":"广东省广州市荔湾区站前街道安易发物流提货处(站前路营业部)广州西站","target":"2","status":"1","createtime":"2016-11-18 17:15:49.0","hasattend":"0","attended":"0"}],"total":2}
				try {
					JSONObject jsonObject = new JSONObject(val);
					if(jsonObject.getString("total").equals("0")) {
						publishVolunteerEntities.clear();
						mAdapter.setData(publishVolunteerEntities);
						mAdapter.notifyDataSetChanged();
						return;
					}
					JSONArray jsonArray = jsonObject.getJSONArray("rows");
					for (int i=0;i<jsonArray.length();i++){
						PublishVolunteerEntity volunteerEntity = new PublishVolunteerEntity();
						JSONObject jsonObject1 = jsonArray.getJSONObject(i);
						String id = jsonObject1.getString("id");
						String title = jsonObject1.getString("title");
						String begintime = jsonObject1.getString("begintime");
						String createtime = jsonObject1.getString("createtime");
						String hasattend = jsonObject1.getString("hasattend");
						String level = jsonObject1.getString("level");
						String target = jsonObject1.getString("target");
						String status = jsonObject1.getString("status");
						String info = jsonObject1.getString("info");
						String address = jsonObject1.getString("address");

						volunteerEntity.setId(id);
						volunteerEntity.setTitle(title);
						volunteerEntity.setBegintime(begintime);
						volunteerEntity.setCreatetime(createtime);
						volunteerEntity.setHasattend(hasattend);
						volunteerEntity.setLevel(level);
						volunteerEntity.setTarget(target);
						volunteerEntity.setStatus(status);
						volunteerEntity.setInfo(info);
						volunteerEntity.setAddress(address);

						publishVolunteerEntities.add(volunteerEntity);
						mAdapter.setData(publishVolunteerEntities);
						mAdapter.notifyDataSetChanged();


					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}, HttpUrls.getPublishVoluteer(QuanjiakanSetting.getInstance().getUserId()+""),params,Task.TYPE_GET_STRING_NOCACHE,null));

	}

	private int pageIndex = 1;
	public void loadPage(final int page){
		if(page==1){
			volunteerEntities.clear();
		}

		//final int size = volunteerEntities.size();
		HashMap<String, String> params = new HashMap<>();
		MyHandler.putTask(VolunteerPublishHistoryActivity.this,new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				fresh.setRefreshing(false);
				//{"rows":[{"id":"36","member_id":"11150","title":"打扫,其他","info":"贤","lat":"39.922019","lng":"116.400535","duration":"0","begintime":"2016-11-18 17:14:00.0","level":"2","address":"北京市东城区东华门街道故宫博物院","target":"2","status":"1","createtime":"2016-11-18 17:15:06.0","hasattend":"0","attended":"0"},{"id":"37","member_id":"11150","title":"打扫","info":"业","lat":"23.138076","lng":"113.244461","duration":"0","begintime":"2016-11-18 17:15:00.0","level":"2","address":"广东省广州市荔湾区站前街道安易发物流提货处(站前路营业部)广州西站","target":"2","status":"1","createtime":"2016-11-18 17:15:49.0","hasattend":"0","attended":"0"}],"total":2}
				try {
					JSONObject jsonObject = new JSONObject(val);
					JSONArray jsonArray = jsonObject.getJSONArray("rows");
					for (int i=0;i<jsonArray.length();i++){
						PublishVolunteerEntity volunteerEntity = new PublishVolunteerEntity();
						JSONObject jsonObject1 = jsonArray.getJSONObject(i);
						String id = jsonObject1.getString("id");
						String title = jsonObject1.getString("title");
						String begintime = jsonObject1.getString("begintime");
						String createtime = jsonObject1.getString("createtime");
						String hasattend = jsonObject1.getString("hasattend");
						String level = jsonObject1.getString("level");
						String target = jsonObject1.getString("target");
						String status = jsonObject1.getString("status");
						String info = jsonObject1.getString("info");
						String address = jsonObject1.getString("address");

						volunteerEntity.setId(id);
						volunteerEntity.setTitle(title);
						volunteerEntity.setBegintime(begintime);
						volunteerEntity.setCreatetime(createtime);
						volunteerEntity.setHasattend(hasattend);
						volunteerEntity.setLevel(level);
						volunteerEntity.setTarget(target);
						volunteerEntity.setStatus(status);
						volunteerEntity.setInfo(info);
						volunteerEntity.setAddress(address);

						volunteerEntities.add(volunteerEntity);
						mAdapter.notifyDataSetChanged();


					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}, HttpUrls.getPublishVoluteer(QuanjiakanSetting.getInstance().getUserId()+""),params,Task.TYPE_GET_STRING_NOCACHE,null));
		/*for(int i = size;i<size+20;i++){
			temp.add(i+"");
		}*/

		/*if(page==1){
			//volunteerEntities.addAll(temp);
			mListView.onRefreshComplete();
			mAdapter.notifyDataSetChanged();
		}else{
			mListView.onRefreshComplete();
			mListView.postDelayed(new Runnable() {
				@Override
				public void run() {
					//volunteerEntities.addAll(temp);
					mAdapter.notifyDataSetChanged();
				}
			},500);
		}*/


		/**
		 * 当一页数据不足时，判断为无更多数据
		/* *//*
		if(temp.size()>=20){
			mListView.setMode(PullToRefreshBase.Mode.BOTH);
		}else{
			mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		}*/
	}



	/*@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.ibtn_back:
				finish();
				break;
			case R.id.menu_text:
				Toast.makeText(this, "发布记录", Toast.LENGTH_SHORT).show();
				break;
			case R.id.publish:
				Toast.makeText(this, "发布", Toast.LENGTH_SHORT).show();
				break;
		}
	}*/

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==COMPLETED_ORDER&&resultCode==RESULT_OK) {
		    //刷新界面
			loadPage(1);
		}
	}
}
