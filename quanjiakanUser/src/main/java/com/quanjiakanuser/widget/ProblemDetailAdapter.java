package com.quanjiakanuser.widget;

import java.io.File;
import java.util.ArrayList;

import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.model.Message;

import com.androidquanjiakan.base.BaseApplication;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanjiakan.main.R;
import com.quanjiakanuser.receiver.NotificationClickEventReceiver;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.quanjiakanuser.util.RecordUtil;
import com.quanjiakanuser.util.VoiceMessageHandler;
import com.quanjiakanuser.util.VoiceMessageHandler.VoiceMessageListener;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class ProblemDetailAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<Message> mList;	
	private static final int left_text = 1;
	private static final int left_voice = 2;
	private static final int left_image = 3;
	private static final int right_text = 4;
	private static final int right_voice = 5;
	private static final int right_image = 6;
	private static final int time_stamp = 7;	
	private LayoutInflater inflater;
	private DisplayImageOptions options,options_touxiang;
	private JsonObject userInfo = new JsonObject();
	private VoiceMessageHandler voiceMessageHandler;
	private RecordUtil recordUtil;
	
	public ProblemDetailAdapter(Context context,ArrayList<Message> mList,final String fromid){
		this.context = context;
		this.mList = mList;
		this.inflater = LayoutInflater.from(context);
		options = ((BaseApplication)((Activity)context).getApplication()).getNormalImageOptions(0, R.drawable.ic_launcher);
		options_touxiang = ((BaseApplication)((Activity)context).getApplication()).getNormalImageOptions(360, R.drawable.ic_launcher);
		if(QuanjiakanSetting.getInstance().getUserInfos().has(fromid)){
			userInfo = QuanjiakanSetting.getInstance().getUserInfos().get(fromid).getAsJsonObject();
		}else {
			//如果没有该用户的信息
    		NotificationClickEventReceiver.loadUserInfo(fromid);
		}
		recordUtil = new RecordUtil(context);
		voiceMessageHandler = new VoiceMessageHandler(context, voiceMessageListener);
	}
	
	VoiceMessageListener voiceMessageListener = new VoiceMessageListener() {
		
		@Override
		public void voiceMessageStatusChange(int position, int status) {
			// TODO Auto-generated method stub
			Toast.makeText(context, position+":播放状态:"+status, Toast.LENGTH_SHORT).show();
		}
	};	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int pos, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		int type = getItemViewType(pos);
		if(view == null){
			viewHolder = new ViewHolder();
			if(type == left_image){
				view = inflater.inflate(R.layout.item_image_detail_left, null);
				viewHolder.img_left_image = (ImageView)view.findViewById(R.id.image);
				viewHolder.image_icon = (ImageView)view.findViewById(R.id.image_icon);
			}else if (type == left_text) {
				view = inflater.inflate(R.layout.item_text_detail_left, null);
				viewHolder.tv_left_text = (TextView)view.findViewById(R.id.tv_title);
				viewHolder.image_icon = (ImageView)view.findViewById(R.id.image_icon);
			}else if (type == left_voice) {
				view = new LeftVoiceView(context);
				viewHolder.leftVoiceView = (LeftVoiceView)view;
			}else if (type == right_image) {
				view = inflater.inflate(R.layout.item_image_detail_right, null);
				viewHolder.img_right_image = (ImageView)view.findViewById(R.id.image);
				viewHolder.image_icon = (ImageView)view.findViewById(R.id.image_icon);
			}else if (type == right_text) {
				view = inflater.inflate(R.layout.item_text_detail_right, null);
				viewHolder.tv_right_text = (TextView)view.findViewById(R.id.tv_title);
				viewHolder.image_icon = (ImageView)view.findViewById(R.id.image_icon);
			}else if (type == right_voice) {
				view = new RightVoiceView(context);
				viewHolder.rightVoiceView = (RightVoiceView)view;
			}else if (type == time_stamp) {
				view = inflater.inflate(R.layout.item_birth_year, null);
				viewHolder.tv_time_stamp = (TextView)view.findViewById(R.id.tempValue);
			}else{
				view = inflater.inflate(R.layout.item_image_detail_left, null);
				viewHolder.img_left_image = (ImageView)view.findViewById(R.id.image);
				viewHolder.image_icon = (ImageView)view.findViewById(R.id.image_icon);
			}
			view.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder)view.getTag();
		}		
		dataBind(pos, viewHolder, type);
		return view;
	}
	
	class ViewHolder{
		
		TextView tv_left_text;
		TextView tv_left_voice;
		ImageView img_left_image;
		
		TextView tv_time_stamp;
		
		TextView tv_right_text;
		TextView tv_right_voice;
		ImageView img_right_image;
		
		ImageView image_icon;
		
		LeftVoiceView leftVoiceView;
		RightVoiceView rightVoiceView;
		
	}
	
	protected void dataBind(final int pos,ViewHolder holder,int type){
		Message object = mList.get(pos);
		if(type == left_image){
			ImageContent content =  (ImageContent)object.getContent();
			String src = content.getLocalThumbnailPath();
			holder.img_left_image.setImageURI(Uri.fromFile(new File(src)));
			if(content.getWidth() != 0){
				int width = content.getWidth();
				int height = content.getHeight();
				RelativeLayout.LayoutParams lp = new LayoutParams(width, height);
				lp.addRule(RelativeLayout.RIGHT_OF, R.id.image_icon);
				lp.leftMargin = QuanjiakanUtil.dip2px(context, 8);
				holder.img_left_image.setLayoutParams(lp);
			}
			ImageLoader.getInstance().displayImage(userInfo.get("icon").getAsString(), holder.image_icon,options_touxiang);
		}else if (type == left_text) {
			holder.tv_left_text.setText(((TextContent)object.getContent()).getText());
			ImageLoader.getInstance().displayImage(userInfo.get("icon").getAsString(), holder.image_icon,options_touxiang);
		}else if (type == left_voice) {
			holder.leftVoiceView.setImageIcon(userInfo.get("icon").getAsString(), options_touxiang);
			VoiceContent content = (VoiceContent)object.getContent();
			holder.leftVoiceView.setVoiceDuration(content.getDuration());
			holder.leftVoiceView.setVoiceFilePath(content.getLocalPath());
			holder.leftVoiceView.getVoiceTextView().setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					voiceMessageHandler.loadingVoice(arg0.getTag()+"", pos);
				}
			});
		}else if (type == right_image) {
			ImageContent content =  (ImageContent)object.getContent();
			String src = content.getLocalThumbnailPath();
			holder.img_right_image.setImageURI(Uri.fromFile(new File(src)));
			if(content.getWidth() != 0){
				int width = content.getWidth();
				int height = content.getHeight();
				RelativeLayout.LayoutParams lp = new LayoutParams(width, height);
				lp.addRule(RelativeLayout.LEFT_OF, R.id.image_icon);
				lp.rightMargin = QuanjiakanUtil.dip2px(context, 8);
				holder.img_right_image.setLayoutParams(lp);
			}
		}else if (type == right_text) {
			holder.tv_right_text.setText(((TextContent)object.getContent()).getText());
		}else if (type == right_voice) {
			VoiceContent content = (VoiceContent)object.getContent();
			holder.rightVoiceView.setVoiceDuration(content.getDuration());
			holder.rightVoiceView.setVoiceFilePath(content.getLocalPath());
			holder.rightVoiceView.getVoiceTextView().setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					voiceMessageHandler.loadingVoice(arg0.getTag()+"", pos);
				}
			});
		}else if (type == time_stamp) {
			String problem = object.getContent().getStringExtra("title");
			holder.tv_time_stamp.setText("问题:"+problem);
		}
	}
	
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		Message object = mList.get(position);
		String fromid = object.getFromID();
		String type = object.getContentType().toString();
		if(object.getContent().getStringExtras().containsKey("title")){
			return time_stamp;
		}else {
			if(fromid.equals(QuanjiakanSetting.getInstance().getUserId()+"")){
				//自己
				if(type.equals(ContentType.text.toString())){
					return right_text;
				}else if (type.equals(ContentType.voice.toString())) {
					return right_voice;
				}else if (type.equals(ContentType.image.toString())) {
					return right_image;
				}
			}else{
				//
				if(type.equals(ContentType.text.toString())){
					return left_text;
				}else if (type.equals(ContentType.voice.toString())) {
					return left_voice;
				}else if (type.equals(ContentType.image.toString())) {
					return left_image;
				}
			}
		}
		return time_stamp;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 7;
	}
	
}
