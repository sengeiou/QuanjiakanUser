package com.androidquanjiakan.base;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.androidquanjiakan.interfaces.IDownloadProgressCallback;
import com.androidquanjiakan.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/4 0004.
 */
public class FileDownloaderUtil {

    public static final String TEMP_DIR = "tempfiledir";
    public static final String VERSION_APK = "quanjiakan.apk";
    public static List<SingleDownloadThread> list = new ArrayList<SingleDownloadThread>();
    
    public static final void versionFileUpdate(Context context,String urlPath,final Handler handler){
        try {
            if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//                BaseApplication.getInstances().toast("无可用外存!");
            }else{
                list.clear();

                URL url = new URL(urlPath);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                if(httpURLConnection.getResponseCode()== HttpURLConnection.HTTP_OK || httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_PARTIAL){
                    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+context.getPackageName()+File.separator+TEMP_DIR);
                    LogUtil.e("目录:"+dir.getAbsolutePath());
                    if(!dir.exists()){
                        dir.mkdirs();
                    }
                    File apk = new File(dir,VERSION_APK);
                    LogUtil.e("文件:"+apk.getAbsolutePath());
                    if(apk.exists()){
                        apk.renameTo(new File(dir,"temp"+VERSION_APK));
                        apk.delete();
                        apk = new File(dir,VERSION_APK);
                        apk.createNewFile();
                        RandomAccessFile randomAccessFile = new RandomAccessFile(apk,"rwd");
                        randomAccessFile.setLength(httpURLConnection.getContentLength());
                    }
                    final int sub_thread = 3;
                    final int fileSize = httpURLConnection.getContentLength();
                    int range = fileSize/sub_thread;
                    for(int i = 0;i<sub_thread;i++){
                        if(i<sub_thread-1){
                            list.add(new SingleDownloadThread(urlPath, fileSize, sub_thread, i,
                                    i * range, i * range + (range - 1), apk.getAbsolutePath(), new IDownloadProgressCallback() {
                                @Override
                                public void threadProgress(int id,long progress) {

                                    int downloadedSize = 0;
                                    for(int i = 0;i<sub_thread;i++){
                                        downloadedSize += list.get(i).getDownloadLength();
                                    }

                                    Message msg = new Message();
                                    msg.what = CommonRequestCode.MSG_VERSION_UPDATE_PROGRESHH;
                                    msg.obj = (downloadedSize*100)/fileSize;
                                    handler.sendMessage(msg);
                                }
                            }));
                        }else{
                            list.add(new SingleDownloadThread(urlPath, fileSize, sub_thread, i,
                                    i * range, fileSize, apk.getAbsolutePath(), new IDownloadProgressCallback() {
                                @Override
                                public void threadProgress(int id,long progress) {
                                    int downloadedSize = 0;
                                    for(int i = 0;i<sub_thread;i++){
                                        downloadedSize += list.get(i).getDownloadLength();
                                    }

                                    Message msg = new Message();
                                    msg.what = CommonRequestCode.MSG_VERSION_UPDATE_PROGRESHH;
                                    msg.obj = (downloadedSize*100)/fileSize;
                                    handler.sendMessage(msg);
                                }
                            }));
                        }

                    }
                    for(int i = 0;i<sub_thread;i++){
                        list.get(i).start();
                    }
                    while (true) {
                        if(!list.get(0).isAlive() && !list.get(1).isAlive() && !list.get(2).isAlive()){
                            break;
                        }else{
//                        Log.e("LOGUTIL","list.get(0)"+list.get(0).isAlive());
//                        Log.e("LOGUTIL","list.get(1)"+list.get(1).isAlive());
//                        Log.e("LOGUTIL","list.get(2)"+list.get(2).isAlive());
                        }
                    }
                    /**
                     * 发送Message 开始更新版本
                     */
                    handler.sendEmptyMessage(CommonRequestCode.MSG_VERSION_UPDATE);
                }else{
//                    BaseApplication.getInstances().toast("请求出错，响应码:"+httpURLConnection.getResponseCode());
                    return;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void updateProgress(Handler handler,int messageID){
        handler.sendEmptyMessage(messageID);
    }

    public static void updateAppVersion(Context context){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+
                File.separator+context.getPackageName()+File.separator+TEMP_DIR+File.separator
        +VERSION_APK))
                , "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
}
