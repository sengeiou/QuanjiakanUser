package com.quanjiakanuser.util;

import com.androidquanjiakan.util.SignatureUtil;
import com.google.gson.JsonObject;
import com.quanjiakan.main.wxapi.WXPayEntryActivity;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import android.content.Context;

import org.apache.http.NameValuePair;

import java.util.List;

public class WeixinPayHandler {

	private Context context;
	private IWXAPI api;
	public static final String app_id = "wxaf61aa460f10f734";
	
	public WeixinPayHandler(Context context,int from,Object param){
		this.context = context;
		init();
		WXPayEntryActivity.setFromType(from);
		WXPayEntryActivity.setParams(param);
	}
	
	protected void init(){
		api = WXAPIFactory.createWXAPI(context, app_id);
		api.registerApp(app_id);
	}
	
	public void pay(JsonObject json){
		PayReq req = new PayReq();
		req.appId			= json.get("appid").getAsString();
		req.partnerId		= json.get("mch_id").getAsString();
		req.prepayId		= json.get("prepay_id").getAsString();
		req.nonceStr		= json.get("nonce_str").getAsString();
		req.packageValue	= "Sign=WXPay";
		/**
		 * ************************************************
		 * 升级微信后出现无法支付情况
		 */
		req.timeStamp		= json.get("timestamp").getAsString();
		req.sign			= json.get("sign").getAsString();
		/**
		 * ************************************************
		 * 测试这么写是否能够进行支付
		 */
//		req.timeStamp		= (System.currentTimeMillis()/1000)+"";
//		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
//		signParams.add(new BasicNameValuePair("appid", req.appId));
//		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
//		signParams.add(new BasicNameValuePair("package", req.packageValue));
//		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
//		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
//		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
//		req.sign           = genAppSign(signParams);
		/**
		 * ************************************************
		 *
		 */
		req.extData			= "APP"; // optional  app data

//		req.appId			= json.getString("appid");
//		req.partnerId		= json.getString("partnerid");
//		req.prepayId		= json.getString("prepayid");
//		req.nonceStr		= json.getString("noncestr");
//		req.timeStamp		= json.getString("timestamp");
//		req.packageValue	= json.getString("package");
//		req.sign			= json.getString("sign");
//		req.extData			= "app data"; // optional
//		Toast.makeText(context, "正常调起支付", Toast.LENGTH_SHORT).show();
		// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
		api.sendReq(req);
	}

	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(app_id);
		String appSign = SignatureUtil.getMD5String(sb.toString().getBytes()).toUpperCase();
		return appSign;
	}

}
