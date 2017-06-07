package com.androidquanjiakan.business;

import android.app.Dialog;
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
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.datahandler.BindDeviceHandler;
import com.androidquanjiakan.entity.BindDeviceEntity;
import com.androidquanjiakan.entity.WatchSos;
import com.androidquanjiakan.entity.WatchWearStatus;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.util.NotificationUtil;
import com.androidquanjiakan.view.RoundImageView;
import com.google.gson.reflect.TypeToken;
import com.quanjiakan.main.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by Administrator on 2017/3/16 0016.
 */

public class WatchBroadcaseUnwearImpl implements IWatchBroadcaseSaver {
    private Context context;

    public void doBusiness(String data, Context context) {
        try {
            this.context = context;
            /**
             {"Results":{"IMEI":"355637052238805","Category":"WearStatus","WearStatus":"Off"}}
             */
            if (data!=null
                    && data.contains("WearStatus")
                    && (data.contains("Off") ||data.toLowerCase().contains("off"))) {

                WatchWearStatus result = (WatchWearStatus) SerialUtil.jsonToObject(data, new TypeToken<WatchWearStatus>() {
                }.getType());
                if (result != null && result.getResults() != null
                        && result.getResults().getIMEI() !=null
                        && result.getResults().getWearStatus() !=null
                        && ("off".equals(result.getResults().getWearStatus().toLowerCase()) ||
                        "Off".equals(result.getResults().getWearStatus()))
                        ) {
                    BindDeviceEntity entity = BindDeviceHandler.getValue(result.getResults().getIMEI());
                    if(entity!=null){
                        //绑定过该设备的手表
                        NotificationUtil.sendUnWearWarnNotification(context, entity.getName());
                        showWarmDialog(entity.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Dialog confirmDialog;
    public void showWarmDialog(String name) {
        if (confirmDialog != null && confirmDialog.isShowing()) {
//            confirmDialog.dismiss();
        } else {
            confirmDialog = new Dialog(context, R.style.dialog_loading);
            confirmDialog.setCanceledOnTouchOutside(false);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_watch_unwear, null);
            TextView tv_title = (TextView) view.findViewById(R.id.watch_name);
            TextView tv_content = (TextView) view.findViewById(R.id.hint_msg);
            try {
                tv_title.setText(URLDecoder.decode(name, "utf-8") + "手表脱落");//tv_content
                tv_content.setText("请及时检查手表脱落原因，确保手表正常运行!");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            TextView cancel = (TextView) view.findViewById(R.id.confirm);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                    if (id!=-1) {
//                        mNotificationManager.cancel(id);
//                    }
                    confirmDialog.dismiss();
                }
            });

            WindowManager.LayoutParams lp = confirmDialog.getWindow().getAttributes();
            lp.width = QuanjiakanUtil.dip2px(context,280);
            lp.height = lp.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            confirmDialog.setCanceledOnTouchOutside(false);
            confirmDialog.setContentView(view, lp);
            confirmDialog.show();
        }
    }

}
