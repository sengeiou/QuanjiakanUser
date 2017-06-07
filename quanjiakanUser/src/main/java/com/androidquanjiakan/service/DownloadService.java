package com.androidquanjiakan.service;

import android.app.Dialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.entity.VersionInfoEntity;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.interfaces.IDownloadCallback;
import com.androidquanjiakan.interfaces.IDownloadErrorCallback;
import com.androidquanjiakan.util.MultiThreadAsyncTask;
import com.androidquanjiakan.util.NotificationUtil;
import com.google.gson.reflect.TypeToken;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;

/**
 * Created by Administrator on 2016/10/21 0021.
 */

public class DownloadService extends Service {

    NotificationUtil mNotificationUtil;

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationUtil = new NotificationUtil(this);
        update();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(updateTask!=null){
            updateTask.stopSubThread();
            updateTask = null;
        }
    }

    private VersionInfoEntity versionInfoEntity;
    PackageManager packageManager = null;
    PackageInfo packageInfo = null;
    protected void update(){
        packageManager = getPackageManager();
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                //
                if(val!=null && val.length()>0 && !"null".equals(val)){
                    versionInfoEntity = (VersionInfoEntity) SerialUtil.jsonToObject(val,new TypeToken<VersionInfoEntity>(){}.getType());
                    if(versionInfoEntity!=null){
                        if(Integer.parseInt(versionInfoEntity.getCode())>packageInfo.versionCode){
                            /**
                             * 弹出更新对话框
                             */
                            if(versionInfoEntity.getForced_update()!=null && "1".equals(versionInfoEntity.getForced_update())){
                                //强制更新
//                                showUpdateDialog(true);
                                updateTask = new MultiThreadAsyncTask(DownloadService.this,versionInfoEntity.getUrl(),callback,errorCallback,updateDialog);
                                updateTask.execute("");
                            }else{
                                //非强制更新
//                                showUpdateDialog(false);
                                updateTask = new MultiThreadAsyncTask(DownloadService.this,versionInfoEntity.getUrl(),callback,errorCallback,updateDialog);
                                updateTask.execute("");
                            }
                            mNotificationUtil.showNotification(NOTIFICATIONI_DOWNLOAD,"1".equals(versionInfoEntity.getForced_update()));
                            /**
                             * 发送通知，在通知栏更新
                             */
                        }else{
                            BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(),"已是最新版!");
                        }
                    }else{
                        BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(),"获取版本信息失败!");
                    }
                }else{
                    BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(),"获取版本信息失败!");
                }
            }
        }, HttpUrls.getVersion(),null,Task.TYPE_GET_STRING_NOCACHE,null));
    }

    private Dialog updateDialog;
    private TextView title;
    private TextView content;
    private ProgressBar progressBar;
    private TextView rate;
    private TextView cancel;
    private TextView confirm;
    public void showUpdateDialog(final boolean isForce){
        updateDialog = new Dialog(this, R.style.dialog_loading);
        updateDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(updateDialog!=null && updateDialog.isShowing()){
                    return true;
                }else{
                    return false;
                }
            }
        });
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update,null);
        view.setBackgroundResource(R.drawable.selecter_hollow_white);
        title = (TextView) view.findViewById(R.id.tv_title);
        title.setText("版本更新");

        content = (TextView) view.findViewById(R.id.tv_content);
        content.setVisibility(View.VISIBLE);
        if(isForce){
            content.setText("当前版本:"+packageInfo.versionName+"\n\n更新版本:"+versionInfoEntity.getVersion()+ "\n\t(该版本为重要更新版本,若不更新将无法使用)");
        }else{
            content.setText("当前版本:"+packageInfo.versionName+"\n\n更新版本:"+versionInfoEntity.getVersion());
        }

        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,5,0));
        progressBar.setMax(100);

        rate = (TextView) view.findViewById(R.id.rate);
        rate.setVisibility(View.GONE);

        cancel = (TextView) view.findViewById(R.id.btn_cancel);
        cancel.setText(getString(R.string.cancel));
        cancel.setVisibility(View.VISIBLE);

        confirm = (TextView) view.findViewById(R.id.btn_confirm);
        confirm.setText("更新");
        confirm.setVisibility(View.VISIBLE);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(updateTask!=null){
                    updateTask.stopSubThread();
                    updateTask = null;
                }

                if(isForce){
                    updateDialog.dismiss();
                    System.exit(0);
                }else{
                    updateDialog.dismiss();
                    BaseApplication.getInstances().updateCheckTime();
                }
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                confirm.setVisibility(View.GONE);

                if(updateTask!=null){
                    updateTask.stopSubThread();
                    updateTask = null;
                }

            }
        });
        //
        WindowManager.LayoutParams lp = updateDialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(BaseApplication.getInstances(), 280);
        lp.height = lp.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        updateDialog.setContentView(view,lp);
        updateDialog.setCanceledOnTouchOutside(false);

        updateDialog.show();
    }

    MultiThreadAsyncTask updateTask;
    final int NOTIFICATIONI_DOWNLOAD = 10000;
    IDownloadCallback callback = new IDownloadCallback() {
        @Override
        public void updateProgress(int progress, String rate) {
//            progressBar.setProgress(progress);
            mNotificationUtil.updateProgress(NOTIFICATIONI_DOWNLOAD,progress);
        }
    };
    IDownloadErrorCallback errorCallback = new IDownloadErrorCallback() {
        @Override
        public void onError() {
            mNotificationUtil.cancel(NOTIFICATIONI_DOWNLOAD);
        }
    };
}
