package com.quanjiakanuser.widget;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanjiakan.main.R;
import com.androidquanjiakan.base.QuanjiakanUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LeftVoiceView extends RelativeLayout{
	private ImageView image,image_anim;
	private TextView tv;
	private DisplayImageOptions options;
	
	public LeftVoiceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		View view = LayoutInflater.from(context).inflate(R.layout.item_voice_detail_left, null);
		image = (ImageView)view.findViewById(R.id.image_icon);
		tv = (TextView)view.findViewById(R.id.tv_title);
		image_anim = (ImageView)view.findViewById(R.id.image_anim);
		addView(view);
	}
	
	/**
	 * 设置显示的options
	 * @param option
	 */
	public void setDisplayOptions(DisplayImageOptions option){
		this.options = option;
	}
	
	public void setImageIcon(String icon,DisplayImageOptions options){
		ImageLoader.getInstance().displayImage(icon, image, options);
	}
	
	public void setVoiceDuration(int length){
		int max = QuanjiakanUtil.dip2px(getContext(), 150);
		if(length<=60){
			tv.setWidth((length*max)/60);
		}else {
			tv.setWidth(max);
		}
		tv.setText((length/1000)+"'");
	}
	
	public void setVoiceFilePath(String filepath){
		tv.setTag(filepath);
	}
	
	public TextView getVoiceTextView(){
		return this.tv;
	}

}
