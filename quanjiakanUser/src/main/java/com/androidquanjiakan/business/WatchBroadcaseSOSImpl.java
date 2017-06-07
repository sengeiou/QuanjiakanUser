package com.androidquanjiakan.business;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.androidquanjiakan.activity.index.watch_old.WatchOldEntryActivity;
import com.androidquanjiakan.activity.main.MainActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.datahandler.BindDeviceHandler;
import com.androidquanjiakan.entity.BindDeviceEntity;
import com.androidquanjiakan.entity.WatchSos;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.util.NotificationUtil;
import com.androidquanjiakan.view.RoundImageView;
import com.google.gson.reflect.TypeToken;
import com.quanjiakan.main.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import static android.R.string.no;

/**
 * Created by Administrator on 2017/3/16 0016.
 */

public class WatchBroadcaseSOSImpl implements IWatchBroadcaseSaver {
    private Context context;

    public void doBusiness(String data, Context context) {
        try {
            this.context = context;
            /**
             *{
             "Results": {
             "IMEI": "355637052788650",
             "Category": "SOSReport",
             "SOSReport": {
             "Type": "WIFI",
             "Radius": "550",
             "Location": "113.2409402,23.1326885",
             }
             }
             }

             {"Results":{"IMEI":"355637052238805","Category": "SOSReport","SOSReport": {"Type":"LAB","Radius":"550","Location":"113.2451085,23.1325716"}}}
             */
            if (data != null && (data.contains("SOSReport") /*|| data.toLowerCase().contains("sosreport")*/)
                    && data.contains("IMEI")
                    && data.contains("Location")) {

                WatchSos result = (WatchSos) SerialUtil.jsonToObject(data, new TypeToken<WatchSos>() {
                }.getType());
                if (result != null && result.getResults() != null
                        && result.getResults().getIMEI() !=null
                        && result.getResults().getSOSReport() !=null
                        && result.getResults().getSOSReport().getLocation() !=null
                        && result.getResults().getSOSReport().getLocation().length()>3
                        && result.getResults().getSOSReport().getLocation().contains(",")
                        && result.getResults().getSOSReport().getLocation().split(",").length==2
                        ) {
                    String location = result.getResults().getSOSReport().getLocation();
                    double mLat = Double.parseDouble(location.split(",")[1]);
                    double mLng = Double.parseDouble(location.split(",")[0]);
                    BindDeviceEntity entity = BindDeviceHandler.getValue(result.getResults().getIMEI());
                    if (entity != null) {//确保这个人是我绑定过的人，并且在WatchList中有记录
                        int id = NotificationUtil.sendSosNotification(context, entity.getName(), result.getResults().getIMEI(), mLat, mLng);
                        showWarmDialog(entity, result.getResults().getIMEI(), mLat, mLng,id);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Dialog confirmDialog;
    public void showWarmDialog(final BindDeviceEntity entity, final String imei, final double mLat, final double mLng, final int id) {
        if (confirmDialog != null && confirmDialog.isShowing()) {
//            confirmDialog.dismiss();
        } else {
            confirmDialog = new Dialog(BaseApplication.getInstances().getCurrentActivity(), R.style.dialog_loading);
            confirmDialog.setCanceledOnTouchOutside(false);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_efence_sos, null);
            final TextView watch_name = (TextView) view.findViewById(R.id.watch_name);
            final TextView hint_msg = (TextView) view.findViewById(R.id.hint_msg);
            final RoundImageView img = (RoundImageView) view.findViewById(R.id.img);
            watch_name.setText("SOS求救警报");
            try {
                hint_msg.setText(URLDecoder.decode(entity.getName(),"utf-8") + "手表发出SOS求救警报，请立即确认当前状况是否安全");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            TextView confirm = (TextView) view.findViewById(R.id.confirm);
            TextView cancel = (TextView) view.findViewById(R.id.cancel);
            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDialog.dismiss();
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, imei);
                    intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, mLat);
                    intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, mLng);
                    context.startActivity(intent);
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (id!=-1) {
                        mNotificationManager.cancel(id);
                    }

                    confirmDialog.dismiss();
                }
            });

            WindowManager.LayoutParams lp = confirmDialog.getWindow().getAttributes();
            lp.width = QuanjiakanUtil.dip2px(BaseApplication.getInstances(), 280);
            lp.height = lp.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            confirmDialog.setContentView(view, lp);
            confirmDialog.show();
        }
    }

}
