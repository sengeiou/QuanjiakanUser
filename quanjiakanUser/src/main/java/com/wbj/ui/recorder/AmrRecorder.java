package com.wbj.ui.recorder;

import java.io.File;
import java.io.IOException;
import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;

public class AmrRecorder {

	private boolean isRecord = false;
	private MediaRecorder mMediaRecorder;
	
	private AmrRecorder() {
		
	}
	
	private static AmrRecorder mInstance = null;
	public synchronized static AmrRecorder getInstance() {
		if (mInstance == null) {
			mInstance = new AmrRecorder();
		}
		return mInstance;
	}
	
	public int startRecordAndFile(Context ctx) {
		if (isRecord) {
			return ErrorCode.E_STATE_RECODING;
		} else {
			if (mMediaRecorder == null) {
				createMediaRecord(ctx);
				
				try {
					mMediaRecorder.prepare();
					mMediaRecorder.start();
					
					isRecord = true;
					return ErrorCode.SUCCESS;
				} catch (IOException ex) {
					ex.printStackTrace();
					return ErrorCode.E_UNKOWN;
				}
			}
		}
		return ErrorCode.E_NOSDCARD;
	}
	
	public void stopRecordAndFile() {
		close();
	}
	
	public long getRecordFileSize(Context ctx) {
		return AudioFileFunc.getFileSize(AudioFileFunc.getRecordFilePath());
	}

	public boolean isRecordNull(){
		if(mMediaRecorder!=null){
			return false;
		}else{
			return true;
		}
	}

	public int getMaxAmplitude(){
		if(mMediaRecorder!=null){
			return mMediaRecorder.getMaxAmplitude();
		}
		return 0;
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	private void createMediaRecord(Context ctx) {
		mMediaRecorder = new MediaRecorder();
		mMediaRecorder.setAudioSource(AudioFileFunc.AUDIO_INPUT);
		mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
		mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		
		File file = new File(AudioFileFunc.getAMRFilePath(ctx));
		if (file.exists()) {
			file.delete();
		}
		mMediaRecorder.setOutputFile(AudioFileFunc.getRecordFilePath());
	}
	
	private void close() {
		try{
			if (mMediaRecorder != null) {
				isRecord = false;
				mMediaRecorder.stop();
				mMediaRecorder.release();
				mMediaRecorder = null;
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
