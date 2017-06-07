package com.wbj.ui.recorder;

import java.io.File;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;


public class AudioFileFunc {
	public final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
	
	public final static int AUDIO_SAMPLE_RATE = 44100;
	
	private static Context ctx = null;
	
	public static synchronized void setContext(Context context) {
		if (ctx == null)
			ctx = context;
	}
	
	public static Context getContext() {
		return ctx;
	}
	
	private static String AUDIO_AMR_FILENAME = "FinalAudio.amr";
	
	public static void setAudioName(String name) {
		AUDIO_AMR_FILENAME = name;
	}
	
	public static String getAudioName() {
		return AUDIO_AMR_FILENAME;
	}
	
	public static boolean isSdcardExit() {
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}	
	
	private static String filePath;
	private static long currentTime;
	public static String getRecordFilePath(){
		return filePath;
	}
	public static String getTime(){
		return currentTime+"";
	}
	
	public static String getAMRFilePath(Context context) {
		String mAudioAMRPath = "";
		
		if (isSdcardExit()) {
			String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
			//String fileBasePath = ctx.getCacheDir().getAbsolutePath();
			currentTime = System.currentTimeMillis();
			File dir = new File(fileBasePath + File.separator+context.getPackageName()+File.separator+"devices"+File.separator +"voice");
			if(!dir.exists()){
				dir.mkdirs();
			}
			mAudioAMRPath = dir.getAbsolutePath()+File.separator+ "SED_"+currentTime+".amr";
			filePath = mAudioAMRPath;
		}
		
		return mAudioAMRPath;
	}
	
	public static long getFileSize(String path) {
		File mFile = new File(path);
		if (!mFile.exists()) {
			return -1;
		}
		return mFile.length();
	}
}
