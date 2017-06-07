package com.androidquanjiakan.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.androidquanjiakan.activity.index.watch_child.ChildVoiceChatActivity;
import com.androidquanjiakan.activity.index.watch_child.DigitalFanceActivity;
import com.androidquanjiakan.activity.index.watch_child.DigitalFanceActivity_new;
import com.androidquanjiakan.activity.index.watch_old.DigitalFanceOldActivity;
import com.androidquanjiakan.activity.main.MainActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.quanjiakan.main.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/21 0021.
 */

public class NotificationUtil {

    private Context mContext;
    // NotificationManager ： 是状态栏通知的管理类，负责发通知、清楚通知等。
    private NotificationManager manager;
    // 定义Map来保存Notification对象
    private Map<Integer, Notification> map = null;

    private static final int VOICE_NOTI = 50001;
    private static final int VOICE_UNWEAR = 50002;



    public NotificationUtil(Context context) {
        this.mContext = context;
        // NotificationManager 是一个系统Service，必须通过 getSystemService()方法来获取。
        manager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        map = new HashMap<Integer, Notification>();
    }

    public void showNotification(int notificationId, boolean isForce) {
        // 判断对应id的Notification是否已经显示， 以免同一个Notification出现多次
        if (!map.containsKey(notificationId)) {
            // 创建通知对象
            Notification notification = new Notification();
            // 设置通知栏滚动显示文字
            notification.tickerText = "开始下载更新文件";
            // 设置显示时间
            notification.when = System.currentTimeMillis();
            // 设置通知显示的图标
            notification.icon = R.drawable.ic_launcher;
            // 设置通知的特性: 通知被点击后，自动消失
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            // 设置点击通知栏操作
            Intent in = new Intent(mContext, MainActivity.class);// 点击跳转到指定页面
            PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, in,
                    0);
            notification.contentIntent = pIntent;
            // 设置通知的显示视图
            RemoteViews remoteViews = new RemoteViews(
                    mContext.getPackageName(),
                    R.layout.notification_contentview);
            // 设置暂停按钮的点击事件
            Intent pause = new Intent(mContext, MainActivity.class);// 设置跳转到对应界面
            PendingIntent pauseIn = PendingIntent.getActivity(mContext, 0, in,
                    0);
            // 这里可以通过Bundle等传递参数
            remoteViews.setOnClickPendingIntent(R.id.pause, pauseIn);
            /********** 简单分隔 **************************/
            // 设置取消按钮的点击事件
            Intent stop = new Intent(mContext, MainActivity.class);// 设置跳转到对应界面
            stop.putExtra(MainActivity.PARAMS_STOP, 0);
            stop.putExtra(MainActivity.PARAMS_FORCE, isForce);
            PendingIntent stopIn = PendingIntent
                    .getActivity(mContext, 0, in, 0);
            // 这里可以通过Bundle等传递参数
            remoteViews.setOnClickPendingIntent(R.id.cancel, stopIn);
            // 设置通知的显示视图
            notification.contentView = remoteViews;
            // 发出通知
            manager.notify(notificationId, notification);
            map.put(notificationId, notification);// 存入Map中
        }
    }

    /**
     * 取消通知操作
     *
     * @description：
     * @author ldm
     * @date 2016-5-3 上午9:32:47
     */
    public void cancel(int notificationId) {
        manager.cancel(notificationId);
        map.remove(notificationId);
    }

    public void updateProgress(int notificationId, int progress) {
        Notification notify = map.get(notificationId);
        if (null != notify) {
            // 修改进度条
            notify.contentView.setProgressBar(R.id.pBar, 100, progress, false);
            manager.notify(notificationId, notify);
        }
    }


    public static int sendSosNotification(Context context, String name, String imei, double mLat, double mLng) {
        try {
            Intent jump = new Intent(BaseApplication.getInstances().getMainActivity(), MainActivity.class);
            jump.putExtra(BaseConstants.PARAMS_DEVICE_ID, imei);
            jump.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, mLat);
            jump.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, mLng);
            int id = commonNotification(context, URLDecoder.decode(name, "utf-8") + "手表发出SOS报警",
                    URLDecoder.decode(name, "utf-8") + "手表发出SOS报警，请及时联系确认当前状况是否安全", jump);
            return id;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return -1;
        }

    }

    public static int sendOutBoundNotification(Context context, String name, String imei,
                                                double mLat, double mLng, int index, String type, String phone, String time) {
        try {
            Intent intent;
            if ("1".equals(type)) {//儿童
                intent = new Intent(context, DigitalFanceActivity_new.class);
                intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, imei);
                intent.putExtra(BaseConstants.PARAMS_DEVICE_TYPE, "1");
                intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, mLat);
                intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, mLng);
                intent.putExtra(BaseConstants.PARAMS_DEVICE_PHONE, phone);
                if (time != null) {
                    intent.putExtra(BaseConstants.PARAMS_DEVICE_PAGE_STATE, DigitalFanceActivity.STATUS_EDIT);
                    intent.putExtra(BaseConstants.PARAMS_DEVICE_PAGE_WHICH, index);
                }
            } else {//老人
                intent = new Intent(context, DigitalFanceActivity_new.class);
                intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, imei);
                intent.putExtra(BaseConstants.PARAMS_DEVICE_TYPE, "0");
                intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, mLat);
                intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, mLng);
                intent.putExtra(BaseConstants.PARAMS_DEVICE_PHONE, phone);
                if (time != null) {
                    intent.putExtra(BaseConstants.PARAMS_DEVICE_PAGE_STATE, DigitalFanceActivity.STATUS_EDIT);
                    intent.putExtra(BaseConstants.PARAMS_DEVICE_PAGE_WHICH, index);
                }
            }
            int id = commonNotification(context, URLDecoder.decode(name, "utf-8") + "手表离开 " + index + " 号安全区域",
                    URLDecoder.decode(name, "utf-8") + "手表离开 " + index + " 号安全区，请及时联系确认当前状况是否安全", intent);
            return id;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return -1;
        }
    }


    public static int sendInBoundNotification(Context context, String name, String imei,
                                               double mLat, double mLng, int index, String type, String phone, String time) {
        try {
            Intent intent;
            //将该通知显示为默认View
            if ("1".equals(type)) {//儿童
                intent = new Intent(context, DigitalFanceActivity_new.class);
                intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, imei);
                intent.putExtra(BaseConstants.PARAMS_DEVICE_TYPE, "1");
                intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, mLat);
                intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, mLng);
                intent.putExtra(BaseConstants.PARAMS_DEVICE_PHONE, phone);
                if (time != null) {
                    intent.putExtra(BaseConstants.PARAMS_DEVICE_PAGE_STATE, DigitalFanceActivity.STATUS_EDIT);
                    intent.putExtra(BaseConstants.PARAMS_DEVICE_PAGE_WHICH, index);
                }
            } else {//老人
                intent = new Intent(context, DigitalFanceOldActivity.class);
                intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, imei);
                intent.putExtra(BaseConstants.PARAMS_DEVICE_TYPE, "0");
                intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, mLat);
                intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, mLng);
                intent.putExtra(BaseConstants.PARAMS_DEVICE_PHONE, phone);
                if (time != null) {
                    intent.putExtra(BaseConstants.PARAMS_DEVICE_PAGE_STATE, DigitalFanceActivity.STATUS_EDIT);
                    intent.putExtra(BaseConstants.PARAMS_DEVICE_PAGE_WHICH, index);
                }
            }
            int id = commonNotification(context, URLDecoder.decode(name, "utf-8") + "手表进入 " + index + " 号安全区域",
                    URLDecoder.decode(name, "utf-8") + "手表已回到 " + index + " 号安全区,请放心!", intent);
            return id;

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            return -1;
        }
    }

    public static void sendVoiceHintNotification(Context context, String name, String imei,int unreadNumber) {
        try {
            Intent intent = new Intent(context, ChildVoiceChatActivity.class);
            intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, imei);//TODO 这里需要进行区分：是儿童手表还是老人手表---跳转的时候才会正确

            commonNotification(context, URLDecoder.decode(name, "utf-8") + "收到语音消息",
                    URLDecoder.decode(name, "utf-8") + "收到"+unreadNumber+"条未读消息语音消息", intent,VOICE_NOTI);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public static void sendUnWearWarnNotification(Context context, String name) {
        try {
            Intent jump = new Intent(BaseApplication.getInstances().getMainActivity(), MainActivity.class);
            commonNotification(context, URLDecoder.decode(name, "utf-8") + "手表脱落", "请及检查手表脱落原因，确保手表正常运行!", jump,VOICE_UNWEAR);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void sendBindNotification(Context context, final long fromid,
                                            final String imei,
                                            final String phone,
                                            final String name,
                                            final String msgID) {
        try {
            Intent jump = new Intent(BaseApplication.getInstances().getMainActivity(), MainActivity.class);
            //TODO 设置跳转参数
            jump.putExtra(BaseConstants.PARAMS_JUMP_TYPE,BaseConstants.PARAMS_JUMP_TYPE_BIND_REQUEST);
            jump.putExtra(BaseConstants.PARAMS_JUMP_TYPE_BIND_REQUEST_FROMID,fromid);
            jump.putExtra(BaseConstants.PARAMS_JUMP_TYPE_BIND_REQUEST_IMEI,imei);
            jump.putExtra(BaseConstants.PARAMS_JUMP_TYPE_BIND_REQUEST_PROPOSER,phone);
            jump.putExtra(BaseConstants.PARAMS_JUMP_TYPE_BIND_REQUEST_USERNAME,name);
            jump.putExtra(BaseConstants.PARAMS_JUMP_TYPE_BIND_REQUEST_MSGID,msgID);


            bindNotification(context, URLDecoder.decode(name, "utf-8") + "申请绑定手表",
                    URLDecoder.decode(name, "utf-8") + "(" + phone + ")" + "申请绑定手表", jump,msgID);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public static int commonNotification(Context context, String title, String text, Intent jump) {
        // 创建一个通知
        Notification mNotification = new Notification();

        // 设置属性值
        mNotification.icon = R.drawable.logo_user;

        mNotification.tickerText = text;

        mNotification.when = System.currentTimeMillis(); // 立即发生此通知

        // 添加声音效果
        mNotification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.beep);
        mNotification.sound = sound;
        mNotification.flags = Notification.FLAG_INSISTENT;

        PendingIntent contentIntent = PendingIntent.getActivity
                (context, 0, jump, 0);
        mNotification.setLatestEventInfo(context, title,
                text, contentIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int time = (int) System.currentTimeMillis();
        mNotificationManager.notify(time, mNotification);
        return time;
    }

    public static void commonNotification(Context context, String title, String text, Intent jump,int notifyID) {
        // 创建一个通知
        Notification mNotification = new Notification();

        // 设置属性值
        mNotification.icon = R.drawable.logo_user;

        mNotification.tickerText = text;

        mNotification.when = System.currentTimeMillis(); // 立即发生此通知

        // 添加声音效果
        mNotification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.beep);
        mNotification.sound = sound;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;

        PendingIntent contentIntent = PendingIntent.getActivity
                (context, 0, jump, 0);
        mNotification.setLatestEventInfo(context, title,
                text, contentIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notifyID, mNotification);
    }


    public static void bindNotification(Context context, String title, String text, Intent jump,String msgID) {
        // 创建一个通知
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.logo_user)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL);


//封装一个Intent
        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                context, 0, jump,
                PendingIntent.FLAG_UPDATE_CURRENT);
// 设置通知主题的意图
        builder.setContentIntent(resultPendingIntent);
//获取通知管理器对象
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Integer.parseInt(msgID), builder.build());
    }
}
