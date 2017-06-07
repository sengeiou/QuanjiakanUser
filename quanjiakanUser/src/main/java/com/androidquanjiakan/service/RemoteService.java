package com.androidquanjiakan.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.androidquanjiakan.aidl.IRemoteInterface;
import com.androidquanjiakan.util.LogUtil;

import java.util.List;

/**
 * 用于监视APP的远程服务，
 */
public class RemoteService extends Service {
    /**
     * 守护进程，监控活动进程是否存在主APP进程，若不存在时，则将主APP进程启动
     */
    public static final String MAIN_APP = "com.quanjiakan.main";

    public static final String GUARD_SERVICE = "com.quanjiakan.main:remote";

    public static final int MSG_START_APP = 1000;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_START_APP:
                    startMainAPP();
                    break;
            }
        }
    };

    public RemoteService() {
    }

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        String stringToSend = "I'm the test String";
        public RemoteService getService() {
            return RemoteService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        checkThreadStart();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    public void checkThreadStart(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while (true){

                        if(getAPP()){
                            Thread.sleep(5000);
                        }else{
                            /**
                             * 启动主APP
                             */
                            mHandler.sendEmptyMessage(MSG_START_APP);
                            break;
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    class ServiceImpl extends IRemoteInterface.Stub{

        @Override
        public boolean checkAPP(String appPackageName) throws RemoteException {
            boolean flag = false;
            ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            //获取正在运行的应用
            List<ActivityManager.RunningAppProcessInfo> run = am.getRunningAppProcesses();
            //获取包管理器，在这里主要通过包名获取程序的图标和程序名
            PackageManager pm =getApplicationContext().getPackageManager();
            for(ActivityManager.RunningAppProcessInfo ra : run){
                //这里主要是过滤系统的应用和电话应用，当然你也可以把它注释掉。
                if(ra.processName.equals("system") || ra.processName.equals("com.android.phone")){
                    continue;
                }
                if(MAIN_APP.equals(ra.processName)){
                    flag = true;
                }
            }
            if(!flag){
                startMainAPP();
            }
            return flag;
        }
    }

    public boolean getAPP(){
        boolean flag = false;
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //获取正在运行的应用
        List<ActivityManager.RunningAppProcessInfo> run = am.getRunningAppProcesses();
        //获取包管理器，在这里主要通过包名获取程序的图标和程序名
        PackageManager pm =this.getPackageManager();
        for(ActivityManager.RunningAppProcessInfo ra : run){
            //这里主要是过滤系统的应用和电话应用，当然你也可以把它注释掉。
            if(ra.processName.equals("system") || ra.processName.equals("com.android.phone")){
                continue;
            }
            if(MAIN_APP.equals(ra.processName)){
                flag = true;
            }
        }
        if(!flag){
            LogUtil.e("主APP未启动");
        }else{
            LogUtil.e("主APP已经启动");
        }
        return flag;
    }

    public boolean getGuard(){
        boolean flag = false;
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //获取正在运行的应用
        List<ActivityManager.RunningAppProcessInfo> run = am.getRunningAppProcesses();
        //获取包管理器，在这里主要通过包名获取程序的图标和程序名
        PackageManager pm =this.getPackageManager();
        for(ActivityManager.RunningAppProcessInfo ra : run){
            //这里主要是过滤系统的应用和电话应用，当然你也可以把它注释掉。
            if(GUARD_SERVICE.equals(ra.processName)){
                flag = true;
            }
        }
        return flag;
    }

    public void startMainAPP(){
        PackageInfo packageInfo = null;
        try{
            packageInfo = getPackageManager().getPackageInfo(MAIN_APP,0);
        }catch (PackageManager.NameNotFoundException name){
            name.printStackTrace();
        }
        if(packageInfo == null){
            /**
             * 用户端未安装
             */

        }else{
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN,null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(MAIN_APP);

            List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(resolveIntent,0);
            ResolveInfo resolveInfo = resolveInfoList.iterator().next();

            if(resolveInfo !=null){
                String packageName = resolveInfo.activityInfo.packageName;
                String className = resolveInfo.activityInfo.name;

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                ComponentName componentName = new ComponentName(packageName,className);
                intent.setComponent(componentName);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }else{
                //未找到应用信息----理论上不会走这里了
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent();
        intent.setClass(this,RemoteService.class);
        startService(intent);
    }
}
