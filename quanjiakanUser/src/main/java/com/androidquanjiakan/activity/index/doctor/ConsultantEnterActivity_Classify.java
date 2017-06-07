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

import com.androidquanjiakan.activity.index.free.FreeInquiryActivity_CreatePatientProblem;
import com.androidquanjiakan.adapter.ClinicClassifyAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.entity.ClinicClassifyEntity;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.view.FloatingView;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.androidquanjiakan.util.LogUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * TODO 科室分类
 */
public class ConsultantEnterActivity_Classify extends BaseActivity implements OnClickListener{

	private PullToRefreshListView listView;
	private ImageButton ibtn_back;
	private TextView tv_title;
	private View viewTitle;

	private FloatingView floating;

	private ClinicClassifyAdapter mAdapter;
	private List<ClinicClassifyEntity> mListEntity = new ArrayList<ClinicClassifyEntity>();

	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {
			Intent intent = new Intent(ConsultantEnterActivity_Classify.this,ConsultantEnterActivity.class);
			intent.putExtra(ConsultantEnterActivity.PARAMS_CLASS, mListEntity.get((int)arg3).getId());//接口数据
			startActivity(intent);

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_consultant_enter);
		initTitleBar();
		initView();
		floating = (FloatingView) findViewById(R.id.floating);
		floating.setVisibility(View.VISIBLE);
		floating.setOnClickListener(this);

	}

	private ImageView nonedata;
	private TextView nonedatahint;
	public void showNoneData(boolean isShow){
		if(isShow){
			nonedatahint.setText("暂无科室");
			nonedata.setVisibility(View.VISIBLE);
			nonedatahint.setVisibility(View.VISIBLE);
		}else{
			nonedata.setVisibility(View.GONE);
			nonedatahint.setVisibility(View.GONE);
		}
	}

	public void initTitleBar(){
		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("医护咨询");
		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		ibtn_back.setOnClickListener(this);
		ibtn_back.setVisibility(View.VISIBLE);
	}

	/**
	 1、妇科                 2、儿科                   3、皮肤性病科
	 4、泌尿外科             5、内科                   6、外科
	 7、男科                 8、产科                   9、中医科

	 10、骨伤科             11、眼科                  12、精神心理科
	 13、口腔颌面科         14、耳鼻喉科              15、肿瘤及防治科
	 16、整形美容科         17、报告解读科            18、营养科、

	 19、基因检测科
	 */

	public void initClassifyData_Net(){
		HashMap<String,String> params = new HashMap<String,String>();
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				/**
				 * TODO 获取网络分类，为后续的获取指定分类下医生列表提供参数
				 */
//				mList.add("");
				if(val!=null && val.length()>0){
					mListEntity = (List<ClinicClassifyEntity>) SerialUtil.jsonToObject(val,new TypeToken<List<ClinicClassifyEntity>>(){}.getType());
					for(int i = mListEntity.size()-1;i>-1;i--){
						ClinicClassifyEntity temp = mListEntity.get(i);
						if("电话医生".equals(temp.getName())){
							mListEntity.remove(i);
							break;
						}else{
							continue;
						}
					}
					mAdapter.setData(mListEntity);
					mAdapter.notifyDataSetChanged();

					if(mListEntity!=null && mListEntity.size()>0){
						showNoneData(false);
					}else{
						showNoneData(true);
					}
				}else{
					if(mListEntity!=null && mListEntity.size()>0){
						showNoneData(false);
					}else{
						showNoneData(true);
					}
				}
				listView.onRefreshComplete();
			}
		}, HttpUrls.getClinicClassifyList(),params, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(ConsultantEnterActivity_Classify.this)));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void initView(){

		listView = (PullToRefreshListView)findViewById(R.id.listview);
		/**
		 * TODO 获取网络数据，需要接口联通了并开放了，才能使用
		 */
		if(mListEntity==null){
			mListEntity = new ArrayList<ClinicClassifyEntity>();
		}else{
			mListEntity.clear();
		}

		nonedata = (ImageView) findViewById(R.id.nonedata);
		nonedatahint = (TextView) findViewById(R.id.nonedatahint);
		showNoneData(false);
		initClassifyData_Net();

		mAdapter = new ClinicClassifyAdapter(ConsultantEnterActivity_Classify.this,mListEntity);
		listView.setAdapter(mAdapter);
		listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		listView.setOnItemClickListener(onItemClickListener);
		listView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				initClassifyData_Net();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				initClassifyData_Net();
			}
		});
		/**
		 * 获取网络分类
		 */
		loadDoctorList();
	}

	protected void loadDoctorList(){
		/**
		 * 获取分类
		 */
//		MyHandler.putTask(new Task(new HttpResponseInterface() {
//
//			@Override
//			public void handMsg(String val) {
//				mList.clear();
//				List<String> list = (ArrayList<String>) SerialUtil.jsonToObject(val,new TypeToken<ArrayList<String>>(){}.getType());
//				mList.addAll(list);
//				mAdapter.notifyDataSetChanged();
//				listView.onRefreshComplete();
//			}
//		}, HttpUrls.getDoctorList(), null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(ConsultantEnterActivity_Classify.this)));
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
				//进入到创建问题的页面
				Intent tv = new Intent(ConsultantEnterActivity_Classify.this,FreeInquiryActivity_CreatePatientProblem.class);
				tv.putExtra("doctor_id", "");
				startActivity(tv);
				break;
			case R.id.ibtn_back:
				finish();
				break;
			case R.id.floating:
				LogUtil.e(" 点击事件");
				Intent floating = new Intent(ConsultantEnterActivity_Classify.this,PersonalDoctorListActivity.class);
				startActivity(floating);
				break;
		}
	}

}
