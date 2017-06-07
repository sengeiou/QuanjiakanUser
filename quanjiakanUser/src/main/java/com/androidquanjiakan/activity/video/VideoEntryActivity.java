package com.androidquanjiakan.activity.video;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.util.LogUtil;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

import cn.jpush.android.api.JPushInterface;

public class VideoEntryActivity extends BaseActivity implements View.OnClickListener{

	//title line
	private LinearLayout live_line;
	private View live_div;

	private LinearLayout demand_line;
	private View demand_div;
	private final int TYPE_LIVE = 1;
	private final int TYPE_DEMAND = 2;

	public static final int REQUEST_DEMAND = 10001;
	public static final int REQUEST_LIVE = 10000;

	public static final int REQUEST_DEMAND_COMMEND = 10002;
	//
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private VideoLiveFragment videoLiveFragment;
	private VideoDemandFragment videoDemandFragment;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_video_entry);
		initTitleLine();
	}

	private void initTitleLine(){
		live_line = (LinearLayout) findViewById(R.id.live_line);
		live_div = findViewById(R.id.live_div);

		demand_line = (LinearLayout) findViewById(R.id.demand_line);
		demand_div = findViewById(R.id.demand_div);

		live_line.setOnClickListener(this);
		demand_line.setOnClickListener(this);
		/**
		 * 设置初始选择值
		 */
		setClickType(TYPE_LIVE);
	}

	public void setClickType(int type){
		switch (type){
			case TYPE_LIVE: {
				live_div.setVisibility(View.VISIBLE);
				demand_div.setVisibility(View.INVISIBLE);
				setFragment(TYPE_LIVE);
				break;
			}
			case TYPE_DEMAND: {
				live_div.setVisibility(View.INVISIBLE);
				demand_div.setVisibility(View.VISIBLE);
				setFragment(TYPE_DEMAND);
				break;
			}
		}
	}

	public void setFragment(int type){
		if(fragmentManager==null){
			fragmentManager = getFragmentManager();
		}
		fragmentTransaction = fragmentManager.beginTransaction();
		switch (type){
			case TYPE_LIVE: {
				if(videoLiveFragment==null){
					videoLiveFragment = new VideoLiveFragment();
				}
				if(!videoLiveFragment.isAdded()){
					fragmentTransaction.replace(R.id.container,videoLiveFragment);
					fragmentTransaction.commit();
				}else{
					fragmentTransaction.show(videoLiveFragment);
				}
				break;
			}
			case TYPE_DEMAND: {
				if(videoDemandFragment==null){
					videoDemandFragment = new VideoDemandFragment();
				}

				if(!videoDemandFragment.isAdded()){
					fragmentTransaction.replace(R.id.container,videoDemandFragment);
					fragmentTransaction.commit();
				}else{
					fragmentTransaction.show(videoDemandFragment);
				}
				break;
			}
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		BaseApplication.getInstances().setCurrentActivity(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.live_line:
				setClickType(TYPE_LIVE);
				break;
			case R.id.demand_line:
				setClickType(TYPE_DEMAND);
				break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode){
			case REQUEST_DEMAND:
				if(videoDemandFragment!=null){
					videoDemandFragment.reload();
				}
				break;
			case REQUEST_DEMAND_COMMEND:{
				if(videoDemandFragment!=null){
					videoDemandFragment.reload();
				}
				break;
			}
			case REQUEST_LIVE:
				if(videoLiveFragment!=null){
					videoLiveFragment.reload();
				}
				break;
		}
	}
}
