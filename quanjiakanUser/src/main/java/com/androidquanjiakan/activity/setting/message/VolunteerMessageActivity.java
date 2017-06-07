package com.androidquanjiakan.activity.setting.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import com.androidquanjiakan.adapter.VolunteerMessageAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;

import com.androidquanjiakan.entity.VolunteerMessageInfo;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class VolunteerMessageActivity extends BaseActivity implements View.OnClickListener{


	private ImageButton mBack;
	private TextView mTitle;
	private TextView mMenu;

	private PullToRefreshListView mListView;
	private VolunteerMessageAdapter mAdapter;
	private List<VolunteerMessageInfo> mData = new ArrayList<VolunteerMessageInfo>();//全部义工信息
	private List<VolunteerMessageInfo> readList = new ArrayList<VolunteerMessageInfo>();//已读义工信息
	private List<VolunteerMessageInfo> unReadList = new ArrayList<VolunteerMessageInfo>();//未读义工信息

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_volunteer_message);

		initTitle();
		initContent();

	}

	public void initTitle(){
		mBack = (ImageButton) findViewById(R.id.ibtn_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(this);
		mTitle = (TextView) findViewById(R.id.tv_title);
		mTitle.setText("我的消息");
		mMenu = (TextView) findViewById(R.id.menu_text);
		mMenu.setVisibility(View.VISIBLE);
		mMenu.setText("管理");
		mMenu.setOnClickListener(this);
	}

	public void initContent(){

		mListView = (PullToRefreshListView) findViewById(R.id.list);
		mListView.setMode(PullToRefreshBase.Mode.BOTH);
		mAdapter = new VolunteerMessageAdapter(this,mData);
		mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				pageIndex = 1;
				loadPage(pageIndex);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				pageIndex++;
				loadPage(pageIndex);
			}
		});
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mData.get(position-1).setRead(true);
				readList.add(mData.get(position-1));
				unReadList.remove(position-1);
				mAdapter.notifyDataSetChanged();
			}
		});


		mListView.setAdapter(mAdapter);

		pageIndex = 1;
		loadPage(pageIndex);
	}

	private int pageIndex = 1;
	public void loadPage(final int page){
		if(page==1){
			mData.clear();
		}

		final int size = mData.size();
		final ArrayList<VolunteerMessageInfo> temp = new ArrayList<>();
		/**
		 * 模拟数据加载
		 */
		for(int i = size;i<size+20;i++){
			VolunteerMessageInfo info = new VolunteerMessageInfo();
			info.setName(i+"");
			info.setRead(false);//默认未读
			temp.add(info);
		}

		if(page==1){
			mData.addAll(temp);
			unReadList.addAll(temp);
			mListView.onRefreshComplete();
			mAdapter.notifyDataSetChanged();
		}else{
			mListView.onRefreshComplete();
			mListView.postDelayed(new Runnable() {
				@Override
				public void run() {
					mData.addAll(temp);
					mAdapter.notifyDataSetChanged();
				}
			},500);
		}


		/**
		 * 当一页数据不足时，判断为无更多数据
		 */
		if(temp.size()>=20){
			mListView.setMode(PullToRefreshBase.Mode.BOTH);
		}else{
			mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.ibtn_back:
				finish();
				break;
			case R.id.menu_text:
				if(popupWindow!=null && popupWindow.isShowing()){
					popupWindow.dismiss();
				}else{
					showDevicesInfoWindow(mMenu);
				}
				break;
		}
	}

	private PopupWindow popupWindow;
	//显示已读未读的弹出框
	public void showDevicesInfoWindow(View root){
		View view = LayoutInflater.from(this).inflate(R.layout.dialog__volunteer_menu,null);
		final TextView unread = (TextView) view.findViewById(R.id.unread);
		final TextView read = (TextView) view.findViewById(R.id.read);
		unread.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				Toast.makeText(VolunteerMessageActivity.this, "未读", Toast.LENGTH_SHORT).show();
				mData.clear();
				mData.addAll(unReadList);
				mListView.onRefreshComplete();
				mListView.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (mAdapter==null){
							mAdapter = new VolunteerMessageAdapter(BaseApplication.getInstances(),mData);
						}
						mAdapter.notifyDataSetChanged();
					}
				},500);
			}
		});
		read.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
				Toast.makeText(VolunteerMessageActivity.this, "已读", Toast.LENGTH_SHORT).show();
				mData.clear();
				mListView.onRefreshComplete();
				mListView.postDelayed(new Runnable() {
					@Override
					public void run() {
						mData.addAll(readList);
						if (mAdapter==null){
							mAdapter = new VolunteerMessageAdapter(BaseApplication.getInstances(),mData);
						}
						mAdapter.notifyDataSetChanged();
					}
				},500);
			}
		});
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupWindow.dismiss();
			}
		});

//		ViewGroup.LayoutParams lp = view.getLayoutParams();
//		lp.width = lp.WRAP_CONTENT;
//		lp.height = lp.WRAP_CONTENT;
//		view.setLayoutParams(lp);

		popupWindow = new PopupWindow(view,
				ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
		popupWindow.setTouchable(true);
		popupWindow.setOutsideTouchable(true);
//		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.selecter_common_dialog_bg));
		popupWindow.showAsDropDown(root);//设置在某个指定View的下方

	}


}
