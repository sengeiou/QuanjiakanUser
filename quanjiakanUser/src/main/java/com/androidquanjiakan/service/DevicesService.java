package com.androidquanjiakan.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.util.LogUtil;

public class DevicesService extends Service {

    private Thread threadHolder;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void initBindDevice(){
        BaseApplication.getInstances().setSDKServerStatus(-1+"");
        threadHolder = new Thread(){
            @Override
            public void run() {
                /**
                 * SDK 独立于用户的设备运行
                 *
                 * TODO 在子线程中启动，避免在主线程中影响交互 2017-04-22 改
                 */
//                if(BaseApplication.getInstances().getNattyClient()!=null){
//                    BaseApplication.getInstances().showdownNatty();
//                }
                BaseApplication.getInstances().initNattyClient();
                int res = BaseApplication.getInstances().getNattyClient().ntyStartupClient();
                BaseApplication.getInstances().setSDKServerStatus(res+"");
            }
        };
        threadHolder.start();

    }

    public static final String PARAMS_POSITION = "position";
    private int position = 0;


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initBindDevice();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
