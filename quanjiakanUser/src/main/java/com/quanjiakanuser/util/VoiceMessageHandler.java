package com.quanjiakanuser.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.util.SignatureUtil;
import com.quanjiakanuser.util.RecordUtil.PlayerCompletedListener;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class VoiceMessageHandler {

	Context context;
	File outputFile;
	RecordUtil voicePlayer;
	VoiceMessageListener listener;
	private int position = -1;
	private AnimationDrawable mAnimationDrawable;
	private ImageView imageView_playing;
	
	public static final int STATUS_LOADING = 1;
	public static final int STATUS_LOADED_TRUE = 2;
	public static final int STATUS_LOADED_FALSE = 3;
	public static final int STATUS_PLAYING = 4;
	public static final int STATUS_PLAYING_FINISHED = 5;
	private int height = 0;
	
	public VoiceMessageHandler(Context context){
		this.context = context;
	}
	
	public VoiceMessageHandler(Context context,VoiceMessageListener listener){
		this.context = context;
		voicePlayer = new RecordUtil(context);
		this.listener = listener;
		height = QuanjiakanUtil.dip2px(context,40);
	}
	
	public VoiceMessageHandler(Context context,VoiceMessageListener listener,RecordUtil _recordutil){
		this.context = context;
		voicePlayer = _recordutil;
		this.listener = listener;
		height = QuanjiakanUtil.dip2px(context,40);
	}
		
	public void loadingVoice(String url,int pos){
		if(pos == position){
			togglePlay();
		}else {
			stopPlay();
			position = pos;
			if(url.startsWith("http")){
				loadVoiceFile(url);
			}else {
				outputFile = new File(url);
				listener.voiceMessageStatusChange(position, STATUS_PLAYING);
				playVoice(url);
			}
		}
	}
	
	public void togglePlay(){
		if(voicePlayer.isPlaying()){
			listener.voiceMessageStatusChange(position, STATUS_PLAYING_FINISHED);
			voicePlayer.onPause();
		}else {
			listener.voiceMessageStatusChange(position, STATUS_PLAYING);
			voicePlayer.onContinue();
		}
	}
	
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
//			super.handleMessage(msg);
			int percent = msg.what;
			if(percent == 1){
				listener.voiceMessageStatusChange(position, STATUS_LOADING);
			}else if (percent == -1) {
				listener.voiceMessageStatusChange(position, STATUS_PLAYING);
				playVoice();
			}else if (percent == -2) {
				//����ʧ��
				listener.voiceMessageStatusChange(position, STATUS_LOADED_FALSE);
			}			
		}		
	};	
	
	public void playVoice(final String url){
		voicePlayer.play(url, new PlayerCompletedListener() {
			
			@Override
			public void complete() {
				// TODO Auto-generated method stub
				if(listener != null){
					listener.voiceMessageStatusChange(position, STATUS_PLAYING_FINISHED);
				}
			}
		});		
	}
	
	public void playVoice(){
		voicePlayer.play(outputFile.getPath(), new PlayerCompletedListener() {
			
			@Override
			public void complete() {
				// TODO Auto-generated method stub
				
				if(listener != null){
					listener.voiceMessageStatusChange(position, STATUS_PLAYING_FINISHED);
				}
			}
		});		
	}
	
	public String getVoicePathByURL(String url){
		File outputFile = new File(BaseConstants.voiceDir, SignatureUtil.getMD5String(url)+".amr_");
		if(outputFile.exists() && outputFile.length() > 300){
			return outputFile.getPath();
		}else {
			return "";
		}
	}
	
	protected void loadVoiceFile(final String url){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
					InputStream inputStream = null;
					FileOutputStream fOutputStream = null;
					outputFile = new File(BaseConstants.voiceDir, SignatureUtil.getMD5String(url)+".amr_");
					if(outputFile.exists()){
						mHandler.sendEmptyMessage(-1);
						return;
					}
					int total = 0;
					try {
						URL httpUrl = new URL(url);
						HttpURLConnection conn = (HttpURLConnection)httpUrl.openConnection();
						conn .setRequestProperty("Accept-Encoding", "identity");
						total = conn.getContentLength();
						int code = conn.getResponseCode();
						if(code == 200){
							inputStream = conn.getInputStream();				
							fOutputStream = new FileOutputStream(outputFile);
							int contentLength = conn.getContentLength();
							byte[] temp = new byte[1024*5];
							mHandler.sendEmptyMessage(1);
							int num = 0;
							while((num = inputStream.read(temp)) != -1){
								fOutputStream.write(temp, 0, num);
							}
							int fileLength = (int)outputFile.length();
							if(contentLength == fileLength){
								mHandler.sendEmptyMessage(-1);
							}else {
								mHandler.sendEmptyMessage(-2);	
							}
						}else {
							mHandler.sendEmptyMessage(-2);
						}
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						InfoPrinter.printLog(e.toString());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						InfoPrinter.printLog(e.toString());
					}				
			}
		}).start();
	}
	
	public void stopPlay(){
		position = -1;
		voicePlayer.stopPlay();
		stopAnimation();
	}	
	
	public void playAnimation(ImageView imageView){
//		imageView_playing = imageView;
//		imageView_playing.setBackgroundResource(R.anim.anim_voice_player);
//		mAnimationDrawable = (AnimationDrawable)imageView_playing.getBackground();
//		mAnimationDrawable.start();
	}
	
	public void playAnimation(ImageView imageView,int res){
//		imageView_playing = imageView;
//		imageView_playing.setBackgroundResource(res);
//		mAnimationDrawable = (AnimationDrawable)imageView_playing.getBackground();
//		mAnimationDrawable.start();
	}
	
	public void stopAnimation(){
		if(mAnimationDrawable != null){
//			mAnimationDrawable.selectDrawable(0);
			mAnimationDrawable.stop();			
		}
	}
	
	public void stopAnimation(ImageView imageView){
//		if(imageView.getBackground() != null && imageView.getBackground() instanceof AnimationDrawable){
//			AnimationDrawable animationDrawable = (AnimationDrawable)imageView.getBackground();
//			if(animationDrawable.isRunning()){
//				animationDrawable.stop();
//				imageView.setBackgroundResource(R.drawable.voice_player_03);
//			}
//		}
	}
	
	public interface VoiceMessageListener{
		public void voiceMessageStatusChange(int position,int status);
	}
	
	public void setVoiceTextLength(TextView tv,int duration,final int minSecond){
		int len = duration;
		int width = 0;
		if(len >=0 && len < minSecond){
			width = QuanjiakanUtil.dip2px(context,120);
		}else if (len >=minSecond && len <=60) {
			width = QuanjiakanUtil.dip2px(context,120) + QuanjiakanUtil.dip2px(context,len-minSecond);
		}else {
			width = QuanjiakanUtil.dip2px(context,165);
		}
		LayoutParams lp = new LayoutParams(width, height);
		tv.setLayoutParams(lp);
	}
	
	
	public void setVoiceTextLengthRequestLayout(TextView tv,int duration,final int minSecond){
		int len = duration;
		int width = 0;
		if(len >=0 && len < minSecond){
			width = QuanjiakanUtil.dip2px(context,120);
		}else if (len >=minSecond && len <=60) {
			width = QuanjiakanUtil.dip2px(context,120) + QuanjiakanUtil.dip2px(context,len-minSecond);
		}else {
			width = QuanjiakanUtil.dip2px(context,165);
		}
		tv.getLayoutParams().width = width;
		tv.requestLayout();
	}
	
}
