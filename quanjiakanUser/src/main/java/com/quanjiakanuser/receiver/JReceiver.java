package com.quanjiakanuser.receiver;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.androidquanjiakan.activity.main.MainActivity;
import com.androidquanjiakan.activity.video.VideoLivePlayActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.entity.VideoLiveListEntity;
import com.androidquanjiakan.util.LogUtil;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.helpers.IMReceiver;

public class JReceiver extends IMReceiver {

	@Override
	public void onReceive(Context arg0, Intent intent) {
		// TODO Auto-generated method stub
		dealData(arg0,intent);
	}

	public void dealData(Context context,Intent intent){
		Bundle bundle = intent.getExtras();
		LogUtil.w("[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			LogUtil.e("JPushInterface.ACTION_REGISTRATION_ID");
			printBundle(intent.getExtras());
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			// 自定义消息不会展示在通知栏，完全要开发者写代码去处理
			String content = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);

//			System.out.println("收到了自定义消息@@消息内容是:"+ content);
//			System.out.println("收到了自定义消息@@消息extra是:"+ extra);
			LogUtil.e("JPushInterface.ACTION_MESSAGE_RECEIVED");
			printBundle(intent.getExtras());
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			// 在这里可以做些统计，或者做些其他工作
			//TODO 跌倒的通知
			LogUtil.e("JPushInterface.ACTION_NOTIFICATION_RECEIVED");
//			showFallGuideDialog();
			printBundle(intent.getExtras());
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			// 在这里可以自己写代码去定义用户点击后的行为
			LogUtil.e("JPushInterface.ACTION_NOTIFICATION_OPENED");
			for (String key : bundle.keySet()) {
				if (key.equals(JPushInterface.EXTRA_EXTRA)) {
					if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
						LogUtil.w("This message has no Extra data");
						continue;
					}

					try {
						JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
						Iterator<String> it =  json.keys();
						if(json.has("start") && "14".equals(json.get("start").toString())){
							if(json.has("anchorId")){
								intoLiveRoom(context,json.get("anchorId").toString());
								return;
							}
						}
					} catch (JSONException e) {
						LogUtil.w("Get message extra JSON error!");
					}
				}
			}
		} else {
			LogUtil.e( "Unhandled intent - " + intent.getAction());
		}
	}

	public void showFallGuideDialog(final String name,final String fallDownId,final String memberId){
		final Dialog dialog = new Dialog(BaseApplication.getInstances().getMainActivity(), R.style.dialog_loading);
		View view = LayoutInflater.from(BaseApplication.getInstances().getMainActivity()).inflate(R.layout.dialog_watch_fall_hint, null);
		TextView content = (TextView) view.findViewById(R.id.tv_content);
		content.setText("全家康设备用户 \""+name+"\" 跌倒");
		TextView dismiss = (TextView) view.findViewById(R.id.btn_confirm);
		TextView goTo = (TextView) view.findViewById(R.id.btn_cancel);
		dismiss.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		goTo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO 点击前往时，调用
				MyHandler.putTask(BaseApplication.getInstances().getMainActivity(),new Task(new HttpResponseInterface() {
					@Override
					public void handMsg(String val) {
						//TODO 跳转到显示该设备当前位置的页面
					}
				},HttpUrls.getHandleFallDown(fallDownId,memberId),null,Task.TYPE_GET_STRING_NOCACHE,null));
			}
		});
	}

	public void intoLiveRoom(final Context context,String anchor){
		final VideoLiveListEntity entity = new VideoLiveListEntity();
		entity.setIsFollow(1);
		entity.setUserId(anchor+"");
		final String docID = anchor+"";
		MyHandler.putTask(BaseApplication.getInstances().getMainActivity(),new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				try{
					JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
					if(jsonObject.has("code") && !(jsonObject.get("code") instanceof JsonNull)){

						if(jsonObject.has("object") && !(jsonObject.get("object") instanceof JsonNull)
								&& jsonObject.get("object").getAsJsonObject()!=null  &&
								"1".equals(jsonObject.get("code").getAsString())){

							if(jsonObject.get("object").getAsJsonObject().has("isFollow") && !(jsonObject.get("object").getAsJsonObject().get("isFollow") instanceof JsonNull)){
								entity.setIsFollow(jsonObject.get("object").getAsJsonObject().get("isFollow").getAsLong());
							}else{
								entity.setIsFollow(0);
							}
							if(jsonObject.get("object").getAsJsonObject().has("state") &&
									!(jsonObject.get("object").getAsJsonObject().get("state") instanceof JsonNull) &&
									(1==jsonObject.get("object").getAsJsonObject().get("state").getAsInt())){
								Intent intent = new Intent(BaseApplication.getInstances().getMainActivity(),VideoLivePlayActivity.class);
								intent.putExtra(BaseConstants.PARAM_ENTITY,entity);
								intent.putExtra(BaseConstants.PARAM_URL,jsonObject.get("object").getAsJsonObject().get("flvUrl").getAsString());
								intent.putExtra(BaseConstants.PARAM_GROUP,jsonObject.get("object").getAsJsonObject().get("groupId").getAsString());
								intent.putExtra(BaseConstants.PARAM_NUMBER,jsonObject.get("object").getAsJsonObject().get("lookNum").getAsLong()+"");
								intent.putExtra(BaseConstants.PARAM_NAME,jsonObject.get("object").getAsJsonObject().get("name").getAsString());
								intent.putExtra(BaseConstants.PARAM_HEADIMG,jsonObject.get("object").getAsJsonObject().get("icon").getAsString());
								intent.putExtra(BaseConstants.PARAM_LIVERID,jsonObject.get("object").getAsJsonObject().get("userId").getAsString());
								BaseApplication.getInstances().getMainActivity().startActivity(intent);
							}else{
								BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(),"来晚了，直播已经结束!");
							}
						}else{
							if(jsonObject.has("message") && !(jsonObject.get("message") instanceof JsonNull)){
								BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(),jsonObject.get("message").getAsString());
							}else{
								BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(),"获取直播地址失败!");
							}
						}
					}else{
						BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(),"获取直播地址失败!");
					}
				}catch (Exception e){
					LogUtil.e(e.getMessage());
				}
			}
		}, HttpUrls.getIntoLiveRoom()+"&userId="+docID+"&lookId="+BaseApplication.getInstances().getUser_id()+"&userType=0",
				null,Task.TYPE_GET_STRING_NOCACHE, null));
	}

	@Override
	public IBinder peekService(Context myContext, Intent service) {
		// TODO Auto-generated method stub
//		Toast.makeText(myContext, "message", Toast.LENGTH_SHORT).show();
		LogUtil.w("JReceiver peekService message");
		return super.peekService(myContext, service);
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
					LogUtil.w("This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();

					while (it.hasNext()) {
						String myKey = it.next().toString();
						sb.append("\nkey:" + key + ", value: [" +
								myKey + " - " +json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					LogUtil.w("Get message extra JSON error!");
				}
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	

}
