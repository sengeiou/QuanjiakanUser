package com.androidquanjiakan.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.androidquanjiakan.activity.common.ImageViewerActivity;
import com.androidquanjiakan.activity.index.housekeeper.HouseKeeperDetailActivity;
import com.androidquanjiakan.activity.setting.order.HouseKeeperOrderHistoryListActivity;
import com.androidquanjiakan.activity.PaymentResultActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakan.main.wxapi.WXPayEntryActivity;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.AlipayHandler;
import com.quanjiakanuser.util.AlipayHandler.AlipayCallback;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanDialog.MyDialogCallback;
import com.quanjiakanuser.util.GsonParseUtil;
import com.androidquanjiakan.util.LogUtil;
import com.quanjiakanuser.util.WeixinPayHandler;
import com.wxapi.entity.HouseKeeperParamEntity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HouseKeeperOrderListAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<JsonObject> mList;
	private LayoutInflater inflater;
	
	public HouseKeeperOrderListAdapter(Context context,ArrayList<JsonObject> mList){
		this.context = context;
		this.mList = mList;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int p, View v, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(v == null){
			v = inflater.inflate(R.layout.item_housekeeper_order, null);
			holder = new ViewHolder();
			holder.image = (ImageView)v.findViewById(R.id.image);
			holder.tv_price = (TextView)v.findViewById(R.id.tv_price);
			holder.tv_name = (TextView)v.findViewById(R.id.tv_name);
			holder.tv_baomu = (TextView)v.findViewById(R.id.tv_baomu);
//			holder.tv_age = (TextView)v.findViewById(R.id.tv_age);
//			holder.tv_from_region = (TextView)v.findViewById(R.id.tv_from_region);
			holder.tv_house_company = (TextView)v.findViewById(R.id.tv_house_company);
			holder.tv_book_date_value = (TextView)v.findViewById(R.id.tv_book_date_value);
//			holder.tv_service_items_value = (TextView)v.findViewById(R.id.tv_service_item_value);
			holder.tv_status = (TextView)v.findViewById(R.id.tv_status);
			holder.tv_cancel = (TextView)v.findViewById(R.id.tv_cancel);
			holder.tv_order_time = (TextView)v.findViewById(R.id.tv_order_time);
			holder.tv_order_id = (TextView) v.findViewById(R.id.tv_order_id);
			v.setTag(holder);
		}else {
			holder = (ViewHolder)v.getTag();
		}
		bindData(holder, p);
		return v;
	}
	
	/**
	 * 绑定数据
	 * @param holder
	 * @param pos
	 */
	protected void bindData(ViewHolder holder,final int pos){
		final JsonObject object = mList.get(pos);
//		holder.tv_baomu.setText(object.get("address").getAsString());//
//		if(object.get("company_name")!=null && !(object.get("company_name") instanceof JsonNull) &&
//				object.get("company_name").getAsString().length()>0){
//			holder.tv_baomu.setText("\"广州市巨硅信息科技有限公司\"");
//		}else{
//			holder.tv_baomu.setText("广州市巨硅信息科技有限公司");
//		}
		holder.tv_baomu.setText("广州市巨硅信息科技有限公司");

		holder.tv_name.setText(object.get("housekeeper_name").getAsString());
		holder.tv_price.setText(object.get("price").getAsString()+"元/月");
		holder.tv_house_company.setText("从业"+object.get("experience").getAsString()+"年");
		ImageLoadUtil.loadImage(holder.image,object.get("image").getAsString(),ImageLoadUtil.optionsRoundCorner_Person);
		holder.image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(object.get("image").getAsString()!=null && object.get("image").getAsString().toLowerCase().startsWith("http")){
					Intent intent = new Intent(context, ImageViewerActivity.class);
					intent.putExtra(BaseConstants.PARAMS_URL,object.get("image").getAsString());
					context.startActivity(intent);
				}else{
					BaseApplication.getInstances().toast(context,"该家政人员无头像!");
				}
			}
		});
		holder.tv_book_date_value.setText(getDate(object.get("begindate").getAsString(), object.get("enddate").getAsString()));
//		holder.tv_service_items_value.setText(object.get("service_item").getAsString());
		holder.tv_order_id.setText(object.get("orderid").getAsString());
		int status = object.get("status").getAsInt();
		if(status == 1){
			//未付款
			holder.tv_status.setText("未付款");
			holder.tv_status.setTextColor(context.getResources().getColor(R.color.red));
			holder.tv_cancel.setVisibility(View.GONE);
		}else if (status == 2) {
			//已付款
			holder.tv_status.setText("已付款");
			holder.tv_status.setTextColor(context.getResources().getColor(R.color.maincolor));
			holder.tv_cancel.setVisibility(View.GONE);
		}
		String datetime = object.get("createtime").getAsString();
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(datetime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		holder.tv_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String orderid = object.get("id").getAsString();
				cancelOrder(orderid, pos);
			}
		});
	}
	
	protected String getDate(String begin, String end){
		String begin_date = begin.substring(5, 10);
		String end_date = end.substring(5, 10);
		return begin_date + " 到 " + end_date;
	}
	
	/**
	 * 取消订单
	 */
	protected void cancelOrder(String orderid,final int pos){
		HashMap<String, String> params = new HashMap<>();
		params.put("id", orderid);
		MyHandler.putTask((Activity) context,new Task(new HttpResponseInterface() {
			
			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				HttpResponseResult result = new HttpResponseResult(val);
				if(result.isResultOk()){
					//删除该记录并notifydatasetchanged
					mList.remove(pos);
					notifyDataSetChanged();
				}else {
					Toast.makeText(context, "取消该订单失败", Toast.LENGTH_SHORT).show();
				}
			}
		}, HttpUrls.cancelHouseKeeperOrder(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(context)));
	}
	
	protected void showPayChannelDialog(final JsonObject json,final int pos){
		List<String> strings = new ArrayList<>();
		strings.add("微信支付");
		strings.add("支付宝支付");
		QuanjiakanDialog.getInstance().getListDialogDefineHeight(context, strings, "选择支付方式", new MyDialogCallback() {
			
			@Override
			public void onItemClick(int position, Object object) {
				// TODO Auto-generated method stub
				String pay_channel = "";
				if(position == 0){
					pay_channel = "2";
				}else {
					pay_channel = "1";
				}
				payOrder(json, pos,pay_channel);				
			}
		}, null);
	}	
	
	/**
	 * 支付订单
	 */
	protected void payOrder(final JsonObject object,int pos,final String pay_channel){
		HashMap<String, String> params = new HashMap<>();
		params.put("orderid", object.get("orderid").getAsString());
		params.put("total_fee", "0.01"/*object.get("price").getAsString()*/);
		params.put("subject", "家政服务预约");
		params.put("body", "deposit");
		params.put("pay_channel", pay_channel);
		MyHandler.putTask((Activity)context, new Task(new HttpResponseInterface() {
			
			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				HttpResponseResult result = new HttpResponseResult(val);
				if(result.isResultOk()){
					if(pay_channel.equals("1")){
						new AlipayHandler(context, new AlipayCallback() {
							
							@Override
							public void paidComplete(int type, String msg, String result) {
								// TODO Auto-generated method stub
								LogUtil.e("支付宝支付信息--- TYPE:"+type+"; MSG:"+msg+";  TradeID:"+result);
								if(result!=null && result.length()>0){
									JsonObject json = new GsonParseUtil(result).getJsonObject();
									vertifyAliPayment(json.get("orderid").getAsString(), json.get("result").getAsString(),0.01);
								}else{
									BaseApplication.getInstances().toast(context,"支付失败!");
								}
							}
						}).pay(result.getObject());
					}else if (pay_channel.equals("2")) {
						HouseKeeperParamEntity entity = new HouseKeeperParamEntity();
						entity.setType("微信支付");
						entity.setOrderid(object.get("orderid").getAsString());
						entity.setTotal_fee(0.01/* object.get("price").getAsDouble() */);
						HouseKeeperOrderHistoryListActivity.wxPayJumpFlag = true;
						HouseKeeperOrderHistoryListActivity.entity = entity;
						new WeixinPayHandler(context, WXPayEntryActivity.FROM_HOUSE_KEEPER_ORDER_PAY,entity).pay(result.getObject());
					}
				}
			}
		}, HttpUrls.getHousekeeperPaidinfo(), params, Task.TYPE_POST_DATA_PARAMS, QuanjiakanDialog.getInstance().getDialog(context)));
	}	
	
	/**
	 * 提交支付结果到客户端，并验证
	 * @param orderid
	 * @param info
	 * @param total
	 */
	protected void vertifyAliPayment(final String orderid,String info,final double total){
		HashMap<String, String> params = new HashMap<>();
		params.put("orderid", orderid);
		params.put("result", info);
		params.put("type", "housekeeper");
		MyHandler.putTask((Activity)context, new Task(new HttpResponseInterface() {
			
			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				HttpResponseResult result = new HttpResponseResult(val);
				Intent intent = new Intent(context,PaymentResultActivity.class);
				intent.putExtra("type", "支付宝");
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
	
	class ViewHolder{
		
		TextView tv_name;
		TextView tv_price;
		TextView tv_baomu;
//		TextView tv_age;
//		TextView tv_from_region;
		TextView tv_house_company;
		
		TextView tv_book_date_value;
//		TextView tv_service_items_value;
		TextView tv_status;
		ImageView image;
		TextView tv_cancel;
		TextView tv_order_time;
//		TextView tv_pay;

		TextView tv_order_id;
		
	}


}
