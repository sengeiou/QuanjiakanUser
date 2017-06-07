package com.quanjiakanuser.util;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.net.Uri;
import android.widget.Toast;

import com.androidquanjiakan.base.BaseConstants;

public class RecordUtil {

	private MediaRecorder mr;
	private String voiceURL;
	private String fileName;
	private MediaPlayer mp = new MediaPlayer();
	Context context;
	private String create_time;	
	private int duration;
	private long record_start_time = 0;
	private RecordUtilListener recordUtilListener;
	
	public RecordUtil(Context context){
		this.context = context;
		init();
	}
	
	 /**
	 * 
	 */
	@SuppressWarnings("deprecation")
	protected void init(){
		try {
			if(mr == null){
				mr=new MediaRecorder();
			}else{
				mr.reset();
			}
			mr.setAudioChannels(1);
			mr.setAudioSamplingRate(8000);
			mr.setAudioSource(MediaRecorder.AudioSource.MIC);
			mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mr.setMaxDuration(3*1000*60);
			String directory = BaseConstants.voiceDir;
			if(!new File(directory).exists()){
				new File(directory).mkdirs();
			}			
			String filepath= directory+"/"+System.currentTimeMillis()+".amr_";
			mr.setOutputFile(filepath);
			setVoiceName(new File(filepath).getName());
			setVoiceURL(filepath);			
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(context, "Recorder��ʼ��ʧ���ˣ�", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void startRec(){
		try {
			init();
			mr.prepare();
			mr.start();
			record_start_time = System.currentTimeMillis();
			mr.setOnInfoListener(new OnInfoListener() {
				
				@Override
				public void onInfo(MediaRecorder arg0, int what, int arg2) {
					// TODO Auto-generated method stub
					if(recordUtilListener != null){
						if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED){
							recordUtilListener.recordStatusChange(RecordStatus.RecordMaxDuration, null);
						}else {
							recordUtilListener.recordStatusChange(RecordStatus.UnknowInfo, null);
						}
					}
				}
			});
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(recordUtilListener !=null){
				recordUtilListener.recordStatusChange(RecordStatus.Record_Error, null);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(recordUtilListener !=null){
				recordUtilListener.recordStatusChange(RecordStatus.Record_IOException, null);
			}
		}
	}

	public void destroyRecord(){
		try {
			mr.stop();
			mr.release();
			mr=null;			
		} catch (Exception e) {
			// TODO: handle exception
			InfoPrinter.printLog(e.toString());
		}
	}
	
	public void stopRec(){
		try {			
			mr.stop();
			mr.release();
			mr = null;
			this.create_time = String.valueOf(System.currentTimeMillis());
			setDuration(record_start_time);
		} catch (Exception e) {
			// TODO: handle exception
			if(recordUtilListener !=null){
				recordUtilListener.recordStatusChange(RecordStatus.Record_Error, null);
			}
		}
	}
	
	public int getDurationByFilePath(String filepath){
		MediaPlayer mediaPlayer = MediaPlayer.create(context, Uri.parse(filepath));
		if(mediaPlayer != null){
			return mediaPlayer.getDuration();
		}else {
			return 0;
		}
	}
	
	public void onPause(){
		try {
			mp.pause();
		} catch (Exception e) {
			// TODO: handle exception
			if(recordUtilListener !=null){
				recordUtilListener.recordStatusChange(RecordStatus.Play_Error, null);
			}
		}
	}
	
	public void onContinue(){
		try {
			mp.start();
		} catch (Exception e) {
			// TODO: handle exception
			if(recordUtilListener !=null){
				recordUtilListener.recordStatusChange(RecordStatus.Play_Error, null);
			}
		}
	}
	
	public void play(){
		if(mp == null){
			mp=MediaPlayer.create(context, Uri.fromFile(new File(voiceURL)));
		}else{
//			mp=null;
//			mp=MediaPlayer.create(context, Uri.fromFile(new File(voiceURL)));
			mp.reset();		
//			mp.release();
		}		
		try {
			mp.setDataSource(voiceURL);
			mp.prepare();
//			mp.start();		
			mp.start();
			mp.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			mp.setOnErrorListener(new OnErrorListener() {
				
				@Override
				public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
					// TODO Auto-generated method stub
					if(recordUtilListener !=null){
						recordUtilListener.recordStatusChange(RecordStatus.Play_Error, null);
					}
					return false;
				}
			});
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			InfoPrinter.printLog(e.toString());
			if(recordUtilListener !=null){
				recordUtilListener.recordStatusChange(RecordStatus.Play_Error, null);
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			InfoPrinter.printLog(e.toString());
			if(recordUtilListener !=null){
				recordUtilListener.recordStatusChange(RecordStatus.Play_Error, null);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			InfoPrinter.printLog(e.toString());
			if(recordUtilListener !=null){
				recordUtilListener.recordStatusChange(RecordStatus.Play_IOException, null);
			}
		}
	}
	
	public void play(String url,final PlayerCompletedListener listener){
		if(mp == null){
			mp=new MediaPlayer();
		}else{
			mp.reset();
		}		
		try {		
			mp.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer arg0) {
					// TODO Auto-generated method stub
					if(listener != null){
						listener.complete();
					}
				}
			});	
			mp.setDataSource(url);
			mp.setOnErrorListener(new OnErrorListener() {
				
				@Override
				public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
					// TODO Auto-generated method stub
					if(recordUtilListener !=null){
						recordUtilListener.recordStatusChange(RecordStatus.Play_Error, null);
					}
					return false;
				}
			});
			mp.prepare();
			mp.start();
			InfoPrinter.printLog("voicePlay:"+url);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			InfoPrinter.printLog(e.toString());
			if(listener != null){
				listener.complete();
			}
			if(recordUtilListener !=null){
				recordUtilListener.recordStatusChange(RecordStatus.Play_Error, null);
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			InfoPrinter.printLog(e.toString());
			if(listener != null){
				listener.complete();
			}
			if(recordUtilListener !=null){
				recordUtilListener.recordStatusChange(RecordStatus.Play_Error, null);
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			InfoPrinter.printLog(e.toString());
			if(recordUtilListener !=null){
				recordUtilListener.recordStatusChange(RecordStatus.Play_Error, null);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			InfoPrinter.printLog(e.toString());
			if(recordUtilListener !=null){
				recordUtilListener.recordStatusChange(RecordStatus.Play_Error, null);
			}
		}
		mp.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
				// TODO Auto-generated method stub
				if(listener != null){
					listener.complete();
				}
				if(recordUtilListener !=null){
					recordUtilListener.recordStatusChange(RecordStatus.Play_Error, null);
				}
				return false;
			}
		});			
	}
	
	public void stopPlay(){
		if(mp != null && mp.isPlaying()){
			mp.stop();
		}
	}
	
	/**
	 * @return ��ȡ¼�����ŵ�ǰλ��
	 */
	public int getCurrentPlayingPosition(){
		if(mp != null && mp.isPlaying()){
			return mp.getCurrentPosition();
		}else {
			return -1;
		}
	}
	
	public String getVoiceURL(){
		return this.voiceURL;
	}
	public void setVoiceURL(String url){
		this.voiceURL=url;
	}
	
	public String getVoiceName(){
		return this.fileName;
	}
	public void setVoiceName(String filename){
		this.fileName=filename;
	}
	
	public int getDuration(){
		return this.duration;
	}
	
	public void setDuration(long startTime){
//		MediaPlayer player = MediaPlayer.create(context, Uri.fromFile(new File(filepath)));
//		if(player != null){
//			duration = player.getDuration();
//		}else {
//			duration = 0;
//		}
		long span = System.currentTimeMillis()-startTime;
		this.duration = (int)span;
		InfoPrinter.printLog("duration:"+duration);
	}
	
	public int getCurrentPosition(){
		return mp.getCurrentPosition();
	}
	
	public boolean isPlaying(){
		if(mp.isPlaying()){
			return true;
		}else{
			return false;
		}
	}
	
	public String getCreateTime(){
		return this.create_time;
	}
	
	public interface PlayerCompletedListener{
		public void complete();
	}
	
	public void destroy(){
		stopRec();
		stopPlay();
	}
	
	public void destroyPlayer(){
		try {
			mp.release();
			mp.stop();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}	
	
	public static enum RecordStatus{
		StartRecord,
		StopRecord,
		PlayCompleted,
		RecordMaxDuration,
		UnknowInfo,
		Play_Error,
		Record_Error,
		Record_IOException,
		Play_IOException
	} 
	
	public void addRecordUtilListener(RecordUtilListener listener){
		this.recordUtilListener = listener;
	}
	
	public interface RecordUtilListener{
		public void recordStatusChange(RecordStatus status,Object obj);
	}
	
}
