package com.androidquanjiakan.activity.setting.order;

import java.util.ArrayList;
import java.util.HashMap;

import com.androidquanjiakan.activity.PaymentResultActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.adapter.HouseKeeperOrderListAdapter;
import com.androidquanjiakan.util.LogUtil;
import com.umeng.analytics.MobclickAgent;
import com.wxapi.entity.HouseKeeperParamEntity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HouseKeeperOrderHistoryListActivity extends BaseActivity implements OnClickListener ,AdapterView.OnItemClickListener{

	protected HouseKeeperOrderListAdapter mAdapter;
	protected PullToRefreshListView listView;
	protected Context context;
	protected ArrayList<JsonObject> mList = new ArrayList<>();
	protected TextView tv_title;
	protected ImageButton ibtn_back;
	private LinearLayout order_condition_line;


	private int pageIndex = 1;
	/*

	 */
	public static boolean wxPayJumpFlag = false;

	public static int wxPayStatus = 0;

	public static final int WX_PAY_INIT = 0;
	public static final int WX_PAY_OK = 1;
	public static final int WX_PAY_COMM = -1;
	public static final int WX_PAY_CANCEL = -2;
	public static final int WX_PAY_OTHER = -3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		wxPayStatus = 0;
		context = this;
		setContentView(R.layout.layout_housekeeperlist);
		initView();

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
	 * 初始化界面控件
	 */
	protected void initView(){

		nonedata = (ImageView) findViewById(R.id.nonedata);
		nonedatahint = (TextView) findViewById(R.id.nonedatahint);
		showNoneData(false);

		order_condition_line = (LinearLayout) findViewById(R.id.order_condition_line);
		order_condition_line.setVisibility(View.GONE);

		tv_title = (TextView)findViewById(R.id.tv_title);
		tv_title.setText("家政护理订单");

		ibtn_back = (ImageButton)findViewById(R.id.ibtn_back);
		ibtn_back.setOnClickListener(this);
		ibtn_back.setVisibility(View.VISIBLE);

		listView = (PullToRefreshListView)findViewById(R.id.listview);
		listView.setMode(PullToRefreshBase.Mode.BOTH);
		listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				pageIndex = 1;
				load(pageIndex);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				if(mAdapter.getCount()<((pageIndex)*20)){
					listView.onRefreshComplete();
				}else{
					pageIndex++;
					load(pageIndex);
				}
			}
		});

		mAdapter = new HouseKeeperOrderListAdapter(context, mList);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(this);

		listView.onRefreshComplete();
		load(pageIndex);
	}
	
	/**
	 * 加载家政订单
	 */
	protected void load(final int page){
		HashMap<String, String> params = new HashMap<>();
		params.put("user_id", QuanjiakanSetting.getInstance().getUserId()+"");
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				listView.onRefreshComplete();
				if(val!=null && val.length()>0){
					JsonArray array = new GsonParseUtil(val).getJsonArray();
					if(page == 1){
						mList.clear();
					}
					mList.addAll(QuanjiakanUtil.array2list(array));
					if(mList.size()<((page)*20)){
						listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
					}else{
						listView.setMode(PullToRefreshBase.Mode.BOTH);
					}
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
		}, HttpUrls.getHouseKeeperOrderList(page), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(context)));
	}


	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(this.getClass().getSimpleName());
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
		BaseApplication.getInstances().setCurrentActivity(this);

		if(wxPayJumpFlag){//从微信支付返回
			LogUtil.e("从微信支付返回!");
			wxPayJumpFlag = false;

			if(wxPayStatus == WX_PAY_OK){
				/**
				 * 刷新数据状态
				 */
				LogUtil.e("微信支付状态---WX_PAY_OK ");
//				vertifyAliPayment(entity.getOrderid(),entity.get);
				/**
				 * 下面几种状态处理都相同，仅支付成功后需要刷新当前页面数据，以获得最新数据状态
				 */
//			}else if(wxPayStatus == WX_PAY_COMM){
//				LogUtil.e("微信支付状态---WX_PAY_COMM ");
//			}else if(wxPayStatus == WX_PAY_CANCEL){
//				LogUtil.e("微信支付状态---WX_PAY_CANCEL ");
//			}else if(wxPayStatus == WX_PAY_OTHER){
//				LogUtil.e("微信支付状态---WX_PAY_OTHER ");
//			}else if(wxPayStatus == WX_PAY_INIT){
//				LogUtil.e("微信支付状态---WX_PAY_INIT ");
			}else {
				LogUtil.e("微信支付状态---WX_PAY_FAIL ");
			}


			wxPayStatus = WX_PAY_INIT;
		}
	}

	public static HouseKeeperParamEntity entity;
	protected void vertifyAliPayment(final String orderid,String info,final double total){
		HashMap<String, String> params = new HashMap<>();
		params.put("orderid", orderid);
		params.put("result", info);
		params.put("type", "housekeeper");
		MyHandler.putTask(this,new Task(new HttpResponseInterface() {

			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				HttpResponseResult result = new HttpResponseResult(val);
				Intent intent = new Intent(context,PaymentResultActivity.class);
				intent.putExtra("type", "微信支付");
				intent.putExtra("orderid", orderid);
				intent.putExtra("total_fee", total);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				if(result.getCode().equals("200")){
					intent.putExtra("flag", 1);
					LogUtil.e("支付宝，校验支付成功");
				}else {
					intent.putExtra("flag", 0);
					LogUtil.e("支付宝，校验支付失败");
				}
				((HouseKeeperOrderHistoryListActivity)context).startActivityForResult(intent,0);
			}
		}, HttpUrls.getVetifyPayment(), params, Task.TYPE_POST_DATA_PARAMS, null));
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int id = arg0.getId();
		if(id == R.id.ibtn_back){
			finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		/**
		 * 需要跳转到订单详情列表去
		 */
		Intent intent = new Intent(HouseKeeperOrderHistoryListActivity.this,HouseKeeperOrderDetailActivity.class);
		intent.putExtra(HouseKeeperOrderDetailActivity.PARAMS_ENTITY,mList.get((int)l).toString());
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtil.e("requestCode:"+requestCode+"      ----   resultCode:"+resultCode);
		pageIndex = 1;
		load(pageIndex);
	}
}
