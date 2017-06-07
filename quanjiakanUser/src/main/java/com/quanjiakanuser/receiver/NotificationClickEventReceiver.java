package com.quanjiakanuser.receiver;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.androidquanjiakan.activity.main.MainActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.util.LogUtil;
import com.google.gson.JsonObject;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.jmessage.android.uikit.chatting.ChatActivity;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.NotificationClickEvent;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;

public class NotificationClickEventReceiver{
	
	private static HashMap<String, MessageReceiveCallBack> callbacks = new HashMap<>();

    public NotificationClickEventReceiver(Context context) {
        //注册接收消息事件
        JMessageClient.registerEventReceiver(this);
    }

    public static void registerMessageCallback(String key,MessageReceiveCallBack callBack){
    	callbacks.put(key, callBack);
    }
    
    public static void unRegisterMessageCallback(String key){
    	if(callbacks.containsKey(key)){
    		callbacks.remove(key);
    	}
    }
    
    public void onEventMainThread(MessageEvent event){
    	final String fromid = event.getMessage().getFromID();
		LogUtil.w("Message FromID:"+fromid);
		LogUtil.w("Message  -*- " + printMessage(event.getMessage())+"");
		if("user".equals(event.getMessage().getFromType
				()) && event.getMessage().getTargetType()==ConversationType.group &&
				"系统消息".equals(event.getMessage().getFromID())){
//			LogUtil.e("系统消息");
			return;
		}
    	if(!QuanjiakanSetting.getInstance().getUserInfos().has(fromid)){
    		//如果没有该用户的信息
//    		loadUserInfo(fromid);
    	}

    	if(event.getMessage().getFromID().equals("juguikeji")){

		}else{
			if(event.getMessage().getContent() instanceof TextContent){
				LogUtil.w("Message Text消息"+((TextContent)event.getMessage().getContent()).getText());
			}else if(event.getMessage().getContent() instanceof VoiceContent){
				LogUtil.w("Message Voice消息");
			}else if(event.getMessage().getContent() instanceof ImageContent){
				LogUtil.w("Message Image消息");
			}else{
				LogUtil.w("Message 其他类型消息");
			}
		}
		// 保存数据
//		if(BaseApplication.getInstances().isAppBackground()){
//			notifyCallbacks(event);
//		}
		notifyCallbacks(event);

//		if(event.getMessage().getFromID().equals("juguikeji") && event.getMessage().getTargetID().equals("juguikeji")){
//			/**
//			 *	跌倒通知
//			{_id=0, messageId=94936554, direct=receive, status=receive_success, content={"text":"【紧急通知】你的亲人跌倒了！","extras":{"location":"23.117055306224895|113.2759952545166","time":1.470909939277E12,"hascallhospital":true,"isfalldown":true}}, version=1, fromName='null', contentType=text, contentTypesString='text', createTimeInMillis=1470909943000, targetType=single, targetID='juguikeji', targetName='juguikeji', fromType='user', fromID=juguikeji, notification=null}
//			 */
//			TextContent textContent = (TextContent) event.getMessage().getContent();
//			String message = textContent.getText();
//			/**
//			 * 数据
//			 */
//			String location = textContent.getStringExtra("location");
//			double latitude = Double.parseDouble(location.split("\\|")[0]);
//			double longitude = Double.parseDouble(location.split("\\|")[1]);
//			Number time = textContent.getNumberExtra("time");
//			long timeValue = time.longValue();
//			boolean hascallhospital = textContent.getBooleanExtra("hascallhospital");
//			boolean isfalldown = textContent.getBooleanExtra("isfalldown");
//			LogUtil.w("跌倒数据--\nlat:"+latitude+"\nlongitude:"+longitude+"\ntime:"+timeValue+"\nhascallhospital:"+hascallhospital+"\nisfalldown"+isfalldown);
//
//			Intent intent = new Intent(BaseConstants.NDK_LOCATION);
//			intent.putExtra(BaseConstants.NDK_LOCATION_LAT,latitude);
//			intent.putExtra(BaseConstants.NDK_LOCATION_LON,longitude);
//			intent.putExtra(BaseConstants.NDK_LOCATION_TYPE,"Wifi");
//			intent.putExtra(BaseConstants.NDK_LOCATION_TIME,System.currentTimeMillis());
//			BaseApplication.getInstances().sendBroadcast(intent);
//
//		}
    }

	//stringBuilder.append("\nFromType:"+msg);
	public static String printMessage(Message msg){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\nFromAppKey:"+msg.getFromAppKey());
		stringBuilder.append("\nFromType:"+msg.getFromType().toString());
		stringBuilder.append("\nTargetAppKey:"+msg.getTargetAppKey());
		stringBuilder.append("\nContentType:"+msg.getContentType().toString());
		stringBuilder.append("\ngetContent().toJson():"+msg.getContent().toJson());

//		stringBuilder.append("\nFromType:"+msg);
		return stringBuilder.toString();
	}
    
    public static void loadUserInfo(final String fromid){
    	HashMap<String,String> params = new HashMap<>();
		params.put("id", fromid);
		MyHandler.putTask(new Task(new HttpResponseInterface() {
			
			@Override
			public void handMsg(String val) {
				// TODO Auto-generated method stub
				JsonObject object = new GsonParseUtil(val).getJsonObject();
				if(object.has("name")){
					QuanjiakanSetting.getInstance().setUserInfo(fromid, object);
				}
			}
		}, HttpUrls.getDoctorDetail(), params, Task.TYPE_POST_DATA_PARAMS, null));
    }
    
    protected void notifyCallbacks(MessageEvent event){
    	if(callbacks != null){
    		Iterator iter = callbacks.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				MessageReceiveCallBack callBack = (MessageReceiveCallBack)entry.getValue();
				callBack.messageReceived(event);
			}
    	}
    }
    
    protected void notifyCallbacks(String key,MessageEvent event){
    	if(callbacks != null && callbacks.containsKey(key)){
    		callbacks.get(key).messageReceived(event);
    	}
    }

	public static String printClickMessage(Message msg){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("通知栏IM消息点击事件");
		stringBuilder.append("\nFromAppKey:"+msg.getFromAppKey());
		stringBuilder.append("\nTargetType:"+msg.getTargetType());
		stringBuilder.append("\nFromType:"+msg.getFromType().toString());
		stringBuilder.append("\nTargetAppKey:"+msg.getTargetAppKey());
		stringBuilder.append("\nContentType:"+msg.getContentType().toString());
		stringBuilder.append("\ngetContent().toJson():"+msg.getContent().toJson());

//		stringBuilder.append("\nFromType:"+msg);
		return stringBuilder.toString();
	}

	private JsonObject status;
	private String isPay;
	private float servicePrice;
	private float platformPrice;
	private float doctorPrice;
	private String type;
	private long millisecond;
	public void intoChatRoom(final String doctorID){

		HashMap<String, String> params = new HashMap<>();
		String entity = "&user_id="+BaseApplication.getInstances().getUser_id()+"&doctor_id="+doctorID;
		MyHandler.putTask(BaseApplication.getInstances().getMainActivity(),new Task(new HttpResponseInterface() {
			@Override
			public void handMsg(String val) {
				if(null==val||"".equals(val)){
					BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(),"网络连接错误！");
					return;
				}else {
					status = new GsonParseUtil(val).getJsonObject();

					servicePrice = status.get("servicePrice").getAsFloat();
					platformPrice =status.get("platformPrice").getAsFloat();
					doctorPrice = status.get("doctorPrice").getAsFloat();
					type = status.get("type").getAsString();//状态 0未支付  1已支付  2付费未回复 3开始聊天
					millisecond = status.get("millisecond").getAsLong();//聊天剩余时间
					final String order_id = status.get("orderid").getAsString();

					if(JMessageClient.getMyInfo()!=null){
						Intent intent = new Intent(BaseApplication.getInstances().getMainActivity(), ChatActivity.class);

						intent.putExtra(ChatActivity.MILLISECOND,millisecond);
						intent.putExtra(ChatActivity.TYPE,type);
						intent.putExtra(ChatActivity.ORDER_ID,order_id);
						intent.putExtra(ChatActivity.TARGET_ID, "Doc"+ doctorID //暂时没有这个ID的用户，使用固定ID作为替代
			/*"10043" */   );//医生的ID
						intent.putExtra(ChatActivity.TARGET_APP_KEY, BaseConstants.jpush_doctor_appkey);
						BaseApplication.getInstances().getMainActivity().startActivity(intent);
					}else{
						JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX+BaseApplication.getInstances().getUser_id(), CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
							@Override
							public void gotResult(int i, String s) {
								LogUtil.e("私人医生跳转:Code:"+i+"\nMessage:"+s);
								if(i==0){
									Intent intent = new Intent(BaseApplication.getInstances().getMainActivity(), ChatActivity.class);
									intent.putExtra(ChatActivity.MILLISECOND,millisecond);
									intent.putExtra(ChatActivity.TYPE,type);
									intent.putExtra(ChatActivity.ORDER_ID,order_id);
									intent.putExtra(ChatActivity.TARGET_ID, "Doc"+ doctorID //暂时没有这个ID的用户，使用固定ID作为替代
			/*"10043" */   );//医生的ID

									intent.putExtra(ChatActivity.TARGET_APP_KEY, BaseConstants.jpush_doctor_appkey);
									BaseApplication.getInstances().getMainActivity().startActivity(intent);
								}else{
									Toast.makeText(BaseApplication.getInstances(), "极光IM错误码:"+i+"\n错误信息:"+s, Toast.LENGTH_SHORT).show();
								}
							}
						});
					}

				}
			}
		},HttpUrls.checkChatStatus()+entity,params,Task.TYPE_GET_STRING_NOCACHE,null));
	}
    
    /**
     * 收到消息处理
     * @param notificationClickEvent 通知点击事件
     */
    public void onEvent(NotificationClickEvent notificationClickEvent) {
		/**
		 * 恢复APP至前台
		 */
		final Message message = notificationClickEvent.getMessage();
		LogUtil.w(/*"NotificationClickEventReceiver onEvent [点击Notification的响应]  "+*/""+ printClickMessage(message)+"");
		if(BaseConstants.jpush_doctor_appkey.equals(message.getFromAppKey())){
			/**
			 * 点击跳转到聊天页面*****医生
			 */
			if(message.getTargetType()==ConversationType.single && message.getFromID().startsWith("Doc")){
				intoChatRoom(message.getFromID().replace("Doc","").replace(CommonRequestCode.JMESSAGE_PREFIX,""));
			}else{
				LogUtil.e("群聊会话");
				Intent intent = new Intent(BaseApplication.getInstances().getMainActivity(), MainActivity.class);
				BaseApplication.getInstances().getMainActivity().startActivity(intent);
			}

		}
		else{


			//义工  ****  条件   BaseConstants."义工的APPKey".equals(message.getFromAppKey())
//			Intent intent = new Intent(BaseApplication.getInstances(), ChatActivity.class);
//			intent.putExtra(ChatActivity.TARGET_ID,""+message.getFromID());//
//			intent.putExtra(ChatActivity.TARGET_APP_KEY, BaseConstants.“义工的APPKey”);
//			BaseApplication.getInstances().getMainActivity().startActivity(intent);


			//就近寻医 *** 跳转到 订单页面

			/**
			 * 跳转到首页
			 */
			Intent intent = new Intent(BaseApplication.getInstances().getMainActivity(), MainActivity.class);
			BaseApplication.getInstances().getMainActivity().startActivity(intent);
		}
    }
        
    public interface MessageReceiveCallBack{
    	public void messageReceived(MessageEvent event);
    }    

}
