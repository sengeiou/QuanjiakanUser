package com.androidquanjiakan.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import com.alivc.player.AccessKey;
import com.alivc.player.AccessKeyCallback;
import com.alivc.player.AliVcMediaPlayer;
import com.amap.api.services.core.AMapException;
import com.androidquanjiakan.activity.index.MainEntryMapActivity;
import com.androidquanjiakan.activity.main.MainActivity;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.util.SignatureUtil;
import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;
import com.duanqu.qupai.jni.ApplicationGlue;
import com.example.greendao.dao.DaoMaster;
import com.example.greendao.dao.DaoSession;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.quanjiakanuser.http.CacheManager;
import com.quanjiakanuser.receiver.NotificationClickEventReceiver;
import com.quanjiakanuser.util.SharePreferenceManager;
import com.umeng.analytics.MobclickAgent;
import com.wbj.ndk.natty.client.NattyClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;


public class BaseApplication extends MultiDexApplication {

    /**
     * 打包时需要变更
     */
    public static final String CURRENT_ACTIVITY_NAME = "current_activity_name";

    private MainActivity mainActivity;
    private MainEntryMapActivity mainEntryMapActivity;

    private Activity currentActivity;

    private ImageLoaderConfiguration.Builder config;

    private static final String JCHAT_CONFIGS = "JChat_configs";
    public static final String TARGET_APP_KEY = "targetAppKey";
    public static final String TARGET_ID = "targetId";
    private final static String TAG = "Quanjiakan";

    private String user_id;

    private String sdk_status;

    private String sessionID;

    private static BaseApplication instances;

    public static BaseApplication getInstances() {
        return instances;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instances = this;

        initJpush();

        initImageLoader(getApplicationContext());
        QuanjiakanSetting.init(this);
        CacheManager.init(this);
        initPlayer();
        loadLibrary();

        initUmeng();
//        stat();
        //配置数据库
        setupDatabase();

    }

    private void stat() {
        // 打开调试开关，正式版本请关闭，以免影响性能
        StatService.setDebugOn(false);
        // 设置APP_KEY，APP_KEY是从mtj官网获取，建议通过manifest.xml配置
        StatService.setAppKey("7d4b70265f");
        // 设置渠道，建议通过manifest.xml配置
        StatService.setAppChannel(this, "Baidu Market", true);
        // 打开异常收集开关，默认收集java层异常，如果有嵌入SDK提供的so库，则可以收集native crash异常
        StatService.setOn(this, StatService.EXCEPTION_LOG);
        // 如果没有页面和事件埋点，此代码必须设置，否则无法完成接入
        // 设置发送策略，建议使用 APP_START
        // 发送策略，取值 取值 APP_START、SET_TIME_INTERVAL、ONCE_A_DAY
        // 备注，SET_TIME_INTERVAL与ONCE_A_DAY，如果APP退出或者进程死亡，则不会发送
        // 建议此代码不要在Application中设置，否则可能因为进程重启等造成启动次数高，具体见web端sdk帮助中心
        StatService.setSendLogStrategy(this, SendStrategyEnum.APP_START, 1, false);
    }

    //直播
    public void loadLibrary(){
        System.loadLibrary("gnustl_shared");
//        System.loadLibrary("ijkffmpeg");//目前使用微博的ijkffmpeg会出现1K再换wifi不重连的情况
        System.loadLibrary("qupai-media-thirdparty");
//        System.loadLibrary("alivc-media-jni");
        System.loadLibrary("qupai-media-jni");
        ApplicationGlue.initialize(this);
    }

    public void initPlayer(){
        // 检查/mnt/sdcard/TAOBAOPLAYER 是否存在,不存在创建
        File rootPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+getPackageName()+File.separator+"aliyun");
        if (!rootPath.exists()) {
            rootPath.mkdir();
        }

        // videolist.txt是否存在,不存在复制
        File videolistFile = new File(rootPath, "videolist.txt");
        if (!videolistFile.exists()) {
            try {
                copyAssetsToSD("videolist.txt", videolistFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // accesstoken.txt 是否存在,不存在复制
        File assessKeyFile = new File(rootPath, "accesstoken.txt");
        if (!assessKeyFile.exists()) {
            try {
                copyAssetsToSD("accesstoken.txt", assessKeyFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File file = new File(rootPath, "accesstoken.txt");
        if (file.exists()) {
            try {
                BufferedReader fileReader = new BufferedReader(new FileReader(file));
                final String accessKeyIds = fileReader.readLine();
                final String accessKeySecrets = fileReader.readLine();
                if(accessKeyIds==null || accessKeySecrets==null){
                    final String accessKeyId = "q8BlKq5w8CSxCJWe";
                    final String accessKeySecret = "bXPgdXkAsm4mesB5BvTfrsX3nyendF";
                    final String businessId = "video_live";

                    AliVcMediaPlayer.init(getApplicationContext(), businessId, new AccessKeyCallback() {
                        public AccessKey getAccessToken() {
                            LogUtil.e("accessKeyId:"+accessKeyId+"\naccessKeySecret:"+accessKeySecret);
                            return new AccessKey(accessKeyId, accessKeySecret);
                        }
                    });
                }else{
                    final String accessKeyId = fileReader.readLine();
                    final String accessKeySecret = fileReader.readLine();
                    final String businessId = "video_live";

                    AliVcMediaPlayer.init(getApplicationContext(), businessId, new AccessKeyCallback() {
                        public AccessKey getAccessToken() {
                            LogUtil.e("accessKeyIds:"+accessKeyIds+"\naccessKeySecrets:"+accessKeySecrets);
                            return new AccessKey(accessKeyIds, accessKeySecrets);
                        }
                    });
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } else {
            final String accessKeyId = "q8BlKq5w8CSxCJWe";
            final String accessKeySecret = "bXPgdXkAsm4mesB5BvTfrsX3nyendF";
            final String businessId = "video_live";

            AliVcMediaPlayer.init(getApplicationContext(), businessId, new AccessKeyCallback() {
                public AccessKey getAccessToken() {
                    LogUtil.e("accessKeyId:"+accessKeyId+"\naccessKeySecret:"+accessKeySecret);
                    return new AccessKey(accessKeyId, accessKeySecret);
                }
            });
        }
    }

    public void initUmeng(){
        MobclickAgent.enableEncrypt(true);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setCatchUncaughtExceptions(true);
    }

    private void copyAssetsToSD(String assetsFile, String sdFile) throws IOException {
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(sdFile);
        myInput = this.getAssets().open(assetsFile);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }

        myOutput.flush();
        myInput.close();
        myOutput.close();
    }

    public void initJpush() {
        //初始化JMessage-sdk
//        JPushInterface.setDebugMode(true);
        JPushInterface.init(getApplicationContext());

//        JMessageClient.setDebugMode(true);
        JMessageClient.init(getApplicationContext());


        SharePreferenceManager.init(getApplicationContext(), JCHAT_CONFIGS);
        //设置Notification的模式
        JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_DEFAULT);//NOTI_MODE_DEFAULT NOTI_MODE_NO_NOTIFICATION
        //注册Notification点击的接收器
        new NotificationClickEventReceiver(getApplicationContext());

        JPushInterface.setPushNotificationBuilder(11,new BasicPushNotificationBuilder(this));
    }

    public void toast(Context context,String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void toastLong(Context context,String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public void showerror(Context context, int rCode) {
//        try {
//            switch (rCode) {
//                //服务错误码
//                case 1001:
//                    throw new AMapException(AMapException.AMAP_SIGNATURE_ERROR);
//                case 1002:
//                    throw new AMapException(AMapException.AMAP_INVALID_USER_KEY);
//                case 1003:
//                    throw new AMapException(AMapException.AMAP_SERVICE_NOT_AVAILBALE);
//                case 1004:
//                    throw new AMapException(AMapException.AMAP_DAILY_QUERY_OVER_LIMIT);
//                case 1005:
//                    throw new AMapException(AMapException.AMAP_ACCESS_TOO_FREQUENT);
//                case 1006:
//                    throw new AMapException(AMapException.AMAP_INVALID_USER_IP);
//                case 1007:
//                    throw new AMapException(AMapException.AMAP_INVALID_USER_DOMAIN);
//                case 1008:
//                    throw new AMapException(AMapException.AMAP_INVALID_USER_SCODE);
//                case 1009:
//                    throw new AMapException(AMapException.AMAP_USERKEY_PLAT_NOMATCH);
//                case 1010:
//                    throw new AMapException(AMapException.AMAP_IP_QUERY_OVER_LIMIT);
//                case 1011:
//                    throw new AMapException(AMapException.AMAP_NOT_SUPPORT_HTTPS);
//                case 1012:
//                    throw new AMapException(AMapException.AMAP_INSUFFICIENT_PRIVILEGES);
//                case 1013:
//                    throw new AMapException(AMapException.AMAP_USER_KEY_RECYCLED);
//                case 1100:
//                    throw new AMapException(AMapException.AMAP_ENGINE_RESPONSE_ERROR);
//                case 1101:
//                    throw new AMapException(AMapException.AMAP_ENGINE_RESPONSE_DATA_ERROR);
//                case 1102:
//                    throw new AMapException(AMapException.AMAP_ENGINE_CONNECT_TIMEOUT);
//                case 1103:
//                    throw new AMapException(AMapException.AMAP_ENGINE_RETURN_TIMEOUT);
//                case 1200:
//                    throw new AMapException(AMapException.AMAP_SERVICE_INVALID_PARAMS);
//                case 1201:
//                    throw new AMapException(AMapException.AMAP_SERVICE_MISSING_REQUIRED_PARAMS);
//                case 1202:
//                    throw new AMapException(AMapException.AMAP_SERVICE_ILLEGAL_REQUEST);
//                case 1203:
//                    throw new AMapException(AMapException.AMAP_SERVICE_UNKNOWN_ERROR);
//                    //sdk返回错误
//                case 1800:
//                    throw new AMapException(AMapException.AMAP_CLIENT_ERRORCODE_MISSSING);
//                case 1801:
//                    throw new AMapException(AMapException.AMAP_CLIENT_ERROR_PROTOCOL);
//                case 1802:
//                    throw new AMapException(AMapException.AMAP_CLIENT_SOCKET_TIMEOUT_EXCEPTION);
//                case 1803:
//                    throw new AMapException(AMapException.AMAP_CLIENT_URL_EXCEPTION);
//                case 1804:
//                    throw new AMapException(AMapException.AMAP_CLIENT_UNKNOWHOST_EXCEPTION);
//                case 1806:
//                    throw new AMapException(AMapException.AMAP_CLIENT_NETWORK_EXCEPTION);
//                case 1900:
//                    throw new AMapException(AMapException.AMAP_CLIENT_UNKNOWN_ERROR);
//                case 1901:
//                    throw new AMapException(AMapException.AMAP_CLIENT_INVALID_PARAMETER);
//                case 1902:
//                    throw new AMapException(AMapException.AMAP_CLIENT_IO_EXCEPTION);
//                case 1903:
//                    throw new AMapException(AMapException.AMAP_CLIENT_NULLPOINT_EXCEPTION);
//                    //云图和附近错误码
//                case 2000:
//                    throw new AMapException(AMapException.AMAP_SERVICE_TABLEID_NOT_EXIST);
//                case 2001:
//                    throw new AMapException(AMapException.AMAP_ID_NOT_EXIST);
//                case 2002:
//                    throw new AMapException(AMapException.AMAP_SERVICE_MAINTENANCE);
//                case 2003:
//                    throw new AMapException(AMapException.AMAP_ENGINE_TABLEID_NOT_EXIST);
//                case 2100:
//                    throw new AMapException(AMapException.AMAP_NEARBY_INVALID_USERID);
//                case 2101:
//                    throw new AMapException(AMapException.AMAP_NEARBY_KEY_NOT_BIND);
//                case 2200:
//                    throw new AMapException(AMapException.AMAP_CLIENT_UPLOADAUTO_STARTED_ERROR);
//                case 2201:
//                    throw new AMapException(AMapException.AMAP_CLIENT_USERID_ILLEGAL);
//                case 2202:
//                    throw new AMapException(AMapException.AMAP_CLIENT_NEARBY_NULL_RESULT);
//                case 2203:
//                    throw new AMapException(AMapException.AMAP_CLIENT_UPLOAD_TOO_FREQUENT);
//                case 2204:
//                    throw new AMapException(AMapException.AMAP_CLIENT_UPLOAD_LOCATION_ERROR);
//                    //路径规划
//                case 3000:
//                    throw new AMapException(AMapException.AMAP_ROUTE_OUT_OF_SERVICE);
//                case 3001:
//                    throw new AMapException(AMapException.AMAP_ROUTE_NO_ROADS_NEARBY);
//                case 3002:
//                    throw new AMapException(AMapException.AMAP_ROUTE_FAIL);
//                case 3003:
//                    throw new AMapException(AMapException.AMAP_OVER_DIRECTION_RANGE);
//                    //短传分享
//                case 4000:
//                    throw new AMapException(AMapException.AMAP_SHARE_LICENSE_IS_EXPIRED);
//                case 4001:
//                    throw new AMapException(AMapException.AMAP_SHARE_FAILURE);
//                default:
//                    Toast.makeText(context, "错误码：" + rCode, Toast.LENGTH_LONG).show();
//                    break;
//            }
//        } catch (Exception e) {
//            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
//        }
    }

    public String getFormatPWString(String formatString){
        return SignatureUtil.getMD5String("@@"+formatString+"@@hi-board@@");
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public MainEntryMapActivity getMainEntryMapActivity(){
        return mainEntryMapActivity;
    }

    public void setMainEntryMapActivity(MainEntryMapActivity mainEntryMapActivity1){
        this.mainEntryMapActivity = mainEntryMapActivity1;
    }

    public Activity getCurrentActivity(){
        return currentActivity;
    }

    public void setCurrentActivity(Activity activity){
        this.currentActivity = activity;
    }

    public void initImageLoader(Context context) {
        config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app
        ImageLoader.getInstance().init(config.build());
    }

    private NattyClient nattyClient;

    public void initNattyClient(){
        nattyClient = new NattyClient();
        long appid = Long.parseLong(getUser_id());
        nattyClient.ntyClientInitilize();
        nattyClient.ntySetSelfId(appid);
    };

    public void setCurrentActivityName(String name){
        setKeyValue(CURRENT_ACTIVITY_NAME,name);
    }

    public String getCurrentActivityName(){
        return getKeyValue(CURRENT_ACTIVITY_NAME);
    }

    public NattyClient getNattyClient(){
        if(nattyClient!=null){
            return nattyClient;
        }else{
            initNattyClient();
            return nattyClient;
        }
    }

    public void showdownNatty(){
        if(nattyClient!=null){
            nattyClient.ntyShutdownClient();
        }
        nattyClient = null;
    }

    public ImageLoaderConfiguration.Builder getConfig() {
        return config;
    }

    public String getVersion() {
        try {
            PackageManager pm = getPackageManager();
            return pm.getPackageInfo(getPackageName(), 0).versionName + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getVersionCode() {
        try {
            PackageManager pm = getPackageManager();
            return pm.getPackageInfo(getPackageName(), 0).versionCode + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public void setUsername(String username) {
        QuanjiakanSetting.getInstance().setUserName(username);
    }

    public String getUsername() {
        return QuanjiakanSetting.getInstance().getUserName();
    }

    public boolean isCheckedUpdateToday() {
        boolean bool = false;
        String time = QuanjiakanSetting.getInstance().getUpdateTime();
        if (time == null || "".equals(time)) {
            bool = false;
            LogUtil.e("today uncheck update");
        } else {
            long longTime = Long.parseLong(time);
            if ((System.currentTimeMillis() - longTime) < 24 * 60 * 60 * 1000) {
                //今天已经检查过更新了
                LogUtil.e("today checked update");
                bool = true;
            } else {
                //今天尚未检查更新
                LogUtil.e("today uncheck update");
//                QuanjiakanSetting.getInstance().setUpdateTime(System.currentTimeMillis()+"");
                bool = false;
            }
        }
        return bool;
    }


    public void updateCheckTime() {
        QuanjiakanSetting.getInstance().setUpdateTime(System.currentTimeMillis() + "");
    }

    public String getPw_signature() {
        return QuanjiakanSetting.getInstance().getValue(QuanjiakanSetting.KEY_SIGNAL);
    }

    public void setPw_signature(String pw_signature) {
        QuanjiakanSetting.getInstance().setValue(QuanjiakanSetting.KEY_SIGNAL, SignatureUtil.getSHA1String(pw_signature));
    }

    public String getUser_id() {
        user_id = QuanjiakanSetting.getInstance().getUserId() + "";
        return user_id;
    }

    public void setUser_id(String user_id) {
        QuanjiakanSetting.getInstance().setUserId(Integer.parseInt(user_id));
    }

    /**
     * ****************************************************
     */
    public String getSDKServerStatus() {
        sdk_status = QuanjiakanSetting.getInstance().getSDKServerStatus() + "";
        return sdk_status;
    }

    public boolean isSDKConnected() {
        return QuanjiakanSetting.getInstance().getSDKServerStatus()>=0;
    }

    public void setSDKServerStatus(String sdk_status) {
        LogUtil.e("连接状态值："+sdk_status);
        QuanjiakanSetting.getInstance().setSDKServerStatus(Integer.parseInt(sdk_status));
    }
    /**
     * ****************************************************
     */
    public String getDevice() {
        return QuanjiakanSetting.getInstance().getDevice();
    }

    public void setDevice(String Device) {
        QuanjiakanSetting.getInstance().setUserId(Integer.parseInt(Device));
    }

    public String getSessionID() {
        sessionID = QuanjiakanSetting.getInstance().getUserSig();
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        QuanjiakanSetting.getInstance().setUserSig(sessionID);
    }

    /**
     * clear all param when exit the app
     */
    public static void onAppExit() {

    }

    @Override
    public void onTerminate() {
        Log.e(TAG, ">>>>onTerminate<<<<");
        super.onTerminate();
    }

    public DisplayImageOptions getNormalImageOptions(float angle, int defaultImage) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(defaultImage)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .displayer(new RoundedBitmapDisplayer(QuanjiakanUtil.dip2px(getApplicationContext(), angle)))
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        return options;
    }

    public boolean isAppBackground(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    return true;
                }else{
                    return false;
                }
            }
        }
        return false;
    }


    public String getKeyValue(String key) {
        return QuanjiakanSetting.getInstance().getKeyValue(key);
    }

    public void setKeyValue(String key,String value) {
        QuanjiakanSetting.getInstance().setKeyValue(key,value);
    }

    public int getKeyNumberValue(String key) {
        return QuanjiakanSetting.getInstance().getKeyNumberValue(key);
    }

    public void setKeyNumberValue(String key,int value) {
        QuanjiakanSetting.getInstance().setKeyNumberValue(key,value);
    }

    public String getDefaultDevice() {
        return QuanjiakanSetting.getInstance().getKeyValue(getUser_id()+"DefaultDevice");
    }

    public void setDefaultDevice(String IMEI_16radix) {
        QuanjiakanSetting.getInstance().setKeyValue(getUser_id()+"DefaultDevice",IMEI_16radix);
    }

    public String getMode(){
        return QuanjiakanSetting.getInstance().getKeyValue(getUser_id()+"mode");
    }

    public void setMode(String mode) {
        QuanjiakanSetting.getInstance().setKeyValue(getUser_id()+"mode",mode);
    }

    private static DaoSession daoSession;
    private DaoMaster.DevOpenHelper helper;
    private SQLiteDatabase db;
    private DaoMaster daoMaster;

    /**
     * 配置数据库
     */
    private void setupDatabase() {
        //创建数据库

        helper = new DaoMaster.DevOpenHelper(this,getUser_id()+"watch.db", null);
        //获取可写数据库
        db = helper.getWritableDatabase();
//        helper.onUpgrade(db,DaoMaster.SCHEMA_VERSION-1,DaoMaster.SCHEMA_VERSION);
        //获取数据库对象
        daoMaster = new DaoMaster(db);
        //获取Dao对象管理者
        daoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoInstant() {
        return daoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

}
