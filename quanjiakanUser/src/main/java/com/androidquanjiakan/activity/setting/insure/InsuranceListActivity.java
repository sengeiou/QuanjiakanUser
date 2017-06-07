package com.androidquanjiakan.activity.setting.insure;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquanjiakan.adapter.InsuranceListAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quanjiakan.main.R;
import com.androidquanjiakan.util.LogUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class InsuranceListActivity extends BaseActivity implements OnClickListener{

	private ImageButton ibtn_back;
	private TextView tv_title;
	private TextView menu_text;

	private PullToRefreshListView listview;
	private InsuranceListAdapter adapter;
	private RelativeLayout attention_to_line;

	private List<String> mList;

	private static final int REQUEST_INSURANCE_ADD = 1000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_insure_list);
		initView();
	}

	protected void initView(){
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("我的保单");
		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		ibtn_back.setVisibility(View.VISIBLE);
		ibtn_back.setOnClickListener(this);
		menu_text = (TextView) findViewById(R.id.menu_text);
		menu_text.setVisibility(View.VISIBLE);
		menu_text.setText("添加");
		menu_text.setOnClickListener(this);

		mList = new ArrayList<String>();
		adapter = new InsuranceListAdapter(this,mList);
		listview = (PullToRefreshListView) findViewById(R.id.listview);
		listview.setMode(PullToRefreshBase.Mode.BOTH);
		listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				adapter.getData().clear();
				initListViewData();
				listview.onRefreshComplete();
				LogUtil.e("--------onPullDownToRefresh");
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				loadMoreData();
				listview.onRefreshComplete();
				LogUtil.e("--------onPullUpToRefresh");
			}
		});
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Intent intent = new Intent(InsuranceListActivity.this,InsuranceDetailActivity.class);
				/**
				 * @TODO 本身List就是详细数据，还是有另外的接口获取详细数据
				 */
				startActivity(intent);
			}
		});
//		initListViewData();
	}

	public void initListViewData(){
		if(mList!=null){
			mList.clear();
		}else{
			mList = new ArrayList<String>();
		}
		for (int i = 0;i<10;i++){
			mList.add(i+"---***---");
		}
//		adapter.getData().addAll(mList);
		adapter.notifyDataSetChanged();
		listview.onRefreshComplete();
	}

	public void loadMoreData(){
		for (int i = 0;i<10;i++){
			mList.add(i+"*-*-*-*-*");
		}
		adapter.getData().addAll(mList);
		adapter.notifyDataSetChanged();
		listview.onRefreshComplete();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()){
			case R.id.ibtn_back:
				finish();
				break;
			case R.id.menu_text:
				jumpToAddInsurance();
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

	/**
	 * to Add Insurance
	 */
	public void jumpToAddInsurance(){
		Intent intent = new Intent(InsuranceListActivity.this,InsuranceAddActivity.class);
		startActivityForResult(intent,REQUEST_INSURANCE_ADD);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode){
			case REQUEST_INSURANCE_ADD:
				if(resultCode==RESULT_OK){
					initListViewData();
				}
				break;
			default:
				break;
		}
	}
}
