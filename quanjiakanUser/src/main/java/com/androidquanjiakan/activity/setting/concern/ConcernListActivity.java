package com.androidquanjiakan.activity.setting.concern;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquanjiakan.activity.video.VideoEntryActivity;
import com.androidquanjiakan.activity.video.VideoLivePlayActivity;
import com.androidquanjiakan.adapter.ConcernListAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.entity.ConcernEntity;
import com.androidquanjiakan.entity.VideoLiveListEntity;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.util.LogUtil;
import com.google.gson.JsonNull;
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

public class ConcernListActivity extends BaseActivity implements OnClickListener{

	private ImageButton ibtn_back;
	private TextView tv_title;

	private PullToRefreshListView listview;
	private ConcernListAdapter mAdapter;
	private List<ConcernEntity> mData;

	private int pageIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_concern_list);
		initTitleBar();
		initView();
	}

	public void initTitleBar(){
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("关注");
		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		ibtn_back.setVisibility(View.VISIBLE);
		ibtn_back.setOnClickListener(this);
	}

	protected void initView(){
		mData = new ArrayList<>();
		mAdapter = new ConcernListAdapter(this,mData);
		listview = (PullToRefreshListView) findViewById(R.id.listview);
		listview.setAdapter(mAdapter);

//		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//			}
//		});

		listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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

		pageIndex = 1;
		loadData(pageIndex);
	}

	public void initLoadInfo(){
		pageIndex = 1;
		loadData(pageIndex);
	}

	public void loadData(final int pageIndex){
		//TODO 若有接口获取信息，则变更该信息
		if(mData==null){
			mData=new ArrayList<>();
		}
		if(pageIndex<=1){
			mData.clear();
		}

		MyHandler.putTask(ConcernListActivity.this,new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				if(val!=null && val.toLowerCase().startsWith("{") && val.toLowerCase().endsWith("}")){
					JsonObject jsonObject =  new GsonParseUtil(val).getJsonObject();
					if(jsonObject.has("code") && !(jsonObject.get("code") instanceof JsonNull) && "1".equals(jsonObject.get("code").getAsString())){
						if(jsonObject.has("rows") && !(jsonObject.get("rows") instanceof JsonNull)){
							List<ConcernEntity> tempList = (List<ConcernEntity>) SerialUtil.
                                    jsonToObject(jsonObject.get("rows").getAsJsonArray().toString(),
                                    new TypeToken<List<ConcernEntity>>(){}.getType());
                            if(tempList!=null && tempList.size()>0){
                                if(tempList.size()==20){
                                    listview.setMode(PullToRefreshBase.Mode.BOTH);
                                }else{
                                    listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                }
								/**
                                 * 去重
								 */
								tempList = distinct(tempList);
                                mData.addAll(tempList);
                                mAdapter.setData(mData);
                            }else{
                                listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            }
						}else{
                            listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
						}
					}else{
                        listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                    }
					mAdapter.notifyDataSetChanged();

				}else{

				}
				listview.onRefreshComplete();
			}
		}, HttpUrls.getConcernList()+"&page="+pageIndex+"&rows=20",null,Task.TYPE_GET_STRING_NOCACHE,null));
	}

	public List<ConcernEntity> distinct(List<ConcernEntity> data){
		if(data==null || data.size()<2){
			return data;
		}else{
			for(int i=data.size()-2;i>=0;i--){
				for(int j = data.size()-1;j>i;j--){
					if(data.get(j).getAnchorId()==data.get(i).getAnchorId()){
						data.remove(j);
					}
				}
			}
			return data;
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
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0.getId() == R.id.ibtn_back){
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode){
			case VideoEntryActivity.REQUEST_LIVE:
				initLoadInfo();
				break;
		}
	}
}
