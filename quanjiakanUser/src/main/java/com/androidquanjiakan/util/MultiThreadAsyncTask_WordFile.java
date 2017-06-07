package com.androidquanjiakan.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.FileDownloaderUtil;
import com.androidquanjiakan.base.SingleDownloadThread;
import com.androidquanjiakan.interfaces.IDownloadCallback;
import com.androidquanjiakan.interfaces.IDownloadErrorCallback;
import com.androidquanjiakan.interfaces.IDownloadProgressCallback;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/21 0021.
 */
public class MultiThreadAsyncTask_WordFile extends AsyncTask<String,Long,Void> {
    /**
     * 可定义得更大
     */
    private final int MAX_SUBTHREAD = 3;
    private String netUrl;
    private String savePath;
    private List<SingleDownloadThread> subThreadList;

    private int sub_thread = 0;

    private boolean stopFlag;

    private int fileSize;
    private long downloadedSize;
    private IDownloadCallback icallback;
    IDownloadErrorCallback iErrorcallback;

    private Dialog updateDialog;
    private Context activity;

    public MultiThreadAsyncTask_WordFile(Activity context, String netUrl, IDownloadCallback icallback, Dialog dialog){
        sub_thread = MAX_SUBTHREAD;
        this.netUrl = netUrl;
        subThreadList = new ArrayList<SingleDownloadThread>();
        this.icallback = icallback;
        this.updateDialog = dialog;
        activity = context;
    }

    public MultiThreadAsyncTask_WordFile(Service context, String netUrl, IDownloadCallback icallback, IDownloadErrorCallback iErrorcallback, Dialog dialog){
        sub_thread = MAX_SUBTHREAD;
        this.netUrl = netUrl;
        subThreadList = new ArrayList<SingleDownloadThread>();
        this.icallback = icallback;
        this.iErrorcallback = iErrorcallback;
        this.updateDialog = dialog;
        activity = context;
    }

    public void stopSubThread(){
        stopFlag = true;
        for(int i = 0;i<sub_thread;i++){
            subThreadList.get(i).setBreakPointFlag(true);
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        downloadedSize = 0;
        for(int i = 0;i<sub_thread;i++){
            downloadedSize += subThreadList.get(i).getDownloadLength();
        }
        /**
         * 更新UI显示的百分比
         */
        if(icallback!=null){
            icallback.updateProgress(((int)((downloadedSize*100)/fileSize)),((downloadedSize*100)/fileSize)+"%");
        }
    }

    @Override
    protected Void doInBackground(String... strings) {
//            publishProgress();
        try{
            URL url = new URL(netUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.connect();
            if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                fileSize = httpURLConnection.getContentLength();

                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+BaseApplication.getInstances().getPackageName()+File.separator+ FileDownloaderUtil.TEMP_DIR);
                if(!dir.exists()){
                    dir.mkdirs();
                }
                /**
                 *
                 */
                final String fileName = netUrl.substring(netUrl.lastIndexOf("/")+1);
                File apk = new File(dir, fileName);
                this.savePath = apk.getAbsolutePath();
                if(apk.exists()){
                    apk.renameTo(new File(dir,"temp_"+fileName));
                    apk.delete();
                    apk = new File(dir,fileName);
                    apk.createNewFile();
                    RandomAccessFile randomAccessFile = new RandomAccessFile(apk,"rwd");
                    randomAccessFile.setLength(fileSize);
                }

                int range = fileSize/sub_thread;
                for(int i = 0;i<sub_thread;i++){
                    if(i<sub_thread-1){
                        subThreadList.add(new SingleDownloadThread(netUrl, fileSize, sub_thread, i,
                                i * range, i * range + (range - 1), savePath, new IDownloadProgressCallback() {
                            @Override
                            public void threadProgress(int id,long progress) {
                                publishProgress(progress);
                            }
                        }));
                    }else{
                        subThreadList.add(new SingleDownloadThread(netUrl, fileSize, sub_thread, i,
                                i * range, fileSize, savePath, new IDownloadProgressCallback() {
                            @Override
                            public void threadProgress(int id,long progress) {
                                publishProgress(progress);
                            }
                        }));
                    }

                }
                for(int i = 0;i<sub_thread;i++){
                    subThreadList.get(i).start();
                }
                while (true) {
                    if(!subThreadList.get(0).isAlive() && !subThreadList.get(1).isAlive() && !subThreadList.get(2).isAlive()){
                        break;
                    }else{

                    }
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(subThreadList.size()>0){
            if(stopFlag){
                if(updateDialog!=null && updateDialog.isShowing()){
                    updateDialog.dismiss();
                }
//					btn_submit.setText("下载停止");
                BaseApplication.getInstances().toast(activity,"下载停止");
            }else{
//					btn_submit.setText("下载完成");
                if(updateDialog!=null && updateDialog.isShowing()){
                    updateDialog.dismiss();
                }
//                FileDownloaderUtil.updateAppVersion(activity);
//                BaseApplication.getInstances().updateCheckTime();
                /**
                 * 调用外部APP打开文件
                 */
                if(savePath.toLowerCase().endsWith("doc") || savePath.toLowerCase().endsWith("docx")){
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(Uri.fromFile(new File(savePath)), "application/msword");
                    activity.startActivity(intent);
                }else{
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setDataAndType(Uri.fromFile(new File(savePath)), "*/*");
                    activity.startActivity(intent);
                }

            }
        }else{
            BaseApplication.getInstances().toast(activity,"网络出错");
            if(updateDialog!=null && updateDialog.isShowing()){
                updateDialog.dismiss();
            }
            if(iErrorcallback!=null){
                iErrorcallback.onError();
            }
//				btn_submit.setText("网络出错");
        }
//        updateTask = null;
    }
}
