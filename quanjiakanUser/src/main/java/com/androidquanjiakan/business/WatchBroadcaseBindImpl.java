package com.androidquanjiakan.business;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.util.NotificationUtil;
import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.quanjiakanuser.util.GsonParseUtil;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/3/16 0016.
 */

public class WatchBroadcaseBindImpl implements IWatchBroadcaseSaver {
    private Context context;

    public void doBusiness(String data, Context context) {
        this.context = context;

        LogUtil.e("----------bindData-------------" + data);
//        {"Results":{"IMEI":"355637052203973","Proposer":"15218293347","UserName":"%E5%87%A0%E7%82%B9","MsgId":"540"}}
        if (data != null) {
            String json = data.substring(0, data.lastIndexOf("}") + 1);
            String fromid = data.substring(data.lastIndexOf("}") + 1, data.length());
            LogUtil.e("-----------data----------" + json + "---------fromid---------" + fromid);
            JsonObject jsonObject = new GsonParseUtil(json).getJsonObject();
            if (jsonObject.has("Results")) {
                JsonObject results = jsonObject.getAsJsonObject("Results");
                if (results.has("Proposer") && results.has("UserName") && results.has("IMEI") && results.has("MsgId")) {
                    NotificationUtil.sendBindNotification(context, Long.parseLong(fromid),
                            results.get("IMEI").getAsString(),
                            results.get("Proposer").getAsString(),
                            results.get("UserName").getAsString(),
                            results.get("MsgId").getAsString());
                    LogUtil.e("----------bind1-----------");
                    showComfirmBindDialog(Long.parseLong(fromid),
                            results.get("IMEI").getAsString(),
                            results.get("Proposer").getAsString(),
                            results.get("UserName").getAsString(),
                            results.get("MsgId").getAsString()
                    );
                }
            }
        }
    }


    private Dialog bindComfirmDialog;
    private View view;

    public void showComfirmBindDialog(final long fromid,
                                      final String imei,
                                      final String phoneNumber,
                                      final String name,
                                      final String msgID) {
        //TODO 控制Dialog的弹出
        if (bindComfirmDialog != null && bindComfirmDialog.isShowing()) {
//            return;
        } else {
            LogUtil.e("------------bind2---------------------");
            bindComfirmDialog = new Dialog(BaseApplication.getInstances().getCurrentActivity(), R.style.dialog_loading);
            bindComfirmDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                        return true;
                    }
                    return false;
                }
            });
            view = LayoutInflater.from(context).inflate(R.layout.dialog_bind_confirm, null);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_title.setText("绑定申请");

            TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
            tv_content.setText(phoneNumber + "用户申请绑定" + imei + "设备");

            TextView btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);
            btn_cancel.setText("拒绝");
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bindComfirmDialog.dismiss();
                    //TODO 删除对应的这条通知消息
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.cancel(Integer.parseInt(msgID));

                    try {//拒绝同样需要走一次该消息
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("IMEI", imei);
                        jsonObject.put("Category", "BindConfirmReq");
                        jsonObject.put("MsgId", msgID);
                        jsonObject.put("Answer", "Reject");
                        long devid = Long.parseLong(imei, 16);
                        BaseApplication.getInstances().getNattyClient().ntyBindConfirmReqClient(fromid,
                                devid, Integer.parseInt(msgID), jsonObject.toString().getBytes(), jsonObject.toString().getBytes().length);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            TextView btn_confirm = (TextView) view.findViewById(R.id.btn_confirm);
            btn_confirm.setText("同意");
            btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bindComfirmDialog.dismiss();
                    //TODO 删除对应的这条通知消息
                    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.cancel(Integer.parseInt(msgID));

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("IMEI", imei);
                        jsonObject.put("Category", "BindConfirmReq");
                        jsonObject.put("MsgId", msgID);
                        jsonObject.put("Answer", "Agree");
                        long devid = Long.parseLong(imei, 16);
                        BaseApplication.getInstances().getNattyClient().ntyBindConfirmReqClient(fromid,
                                devid, Integer.parseInt(msgID), jsonObject.toString().getBytes(), jsonObject.toString().getBytes().length);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            WindowManager.LayoutParams lp = bindComfirmDialog.getWindow().getAttributes();
            lp.width = QuanjiakanUtil.dip2px(BaseApplication.getInstances(), 280);
            lp.height = lp.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            bindComfirmDialog.setContentView(view, lp);
            bindComfirmDialog.setCanceledOnTouchOutside(false);
            bindComfirmDialog.show();
        }

    }


    public void showComfirmBindDialog2(final long fromid,
                                       final String imei,
                                       final String phoneNumber,
                                       final String name,
                                       final String msgID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (bindComfirmDialog != null && view!=null && view.isAttachedToWindow()) {
                bindComfirmDialog.dismiss();
            }
        }else{
            if (bindComfirmDialog != null) {
                bindComfirmDialog.dismiss();
            }
        }
        //TODO 控制Dialog的弹出
        bindComfirmDialog = new Dialog(BaseApplication.getInstances().getCurrentActivity(), R.style.dialog_loading);
        bindComfirmDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        view = LayoutInflater.from(context).inflate(R.layout.dialog_bind_confirm, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText("绑定申请");

        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        tv_content.setText(phoneNumber + "用户申请绑定" + imei + "设备");

        TextView btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);
        btn_cancel.setText("拒绝");
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bindComfirmDialog.dismiss();
                //TODO 删除对应的这条通知消息
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(Integer.parseInt(msgID));

                try {//拒绝同样需要走一次该消息
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("IMEI", imei);
                    jsonObject.put("Category", "BindConfirmReq");
                    jsonObject.put("MsgId", msgID);
                    jsonObject.put("Answer", "Reject");
                    long devid = Long.parseLong(imei, 16);
                    BaseApplication.getInstances().getNattyClient().ntyBindConfirmReqClient(fromid,
                            devid, Integer.parseInt(msgID), jsonObject.toString().getBytes(), jsonObject.toString().getBytes().length);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        TextView btn_confirm = (TextView) view.findViewById(R.id.btn_confirm);
        btn_confirm.setText("同意");
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindComfirmDialog.dismiss();
                //TODO 删除对应的这条通知消息
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(Integer.parseInt(msgID));

                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("IMEI", imei);
                    jsonObject.put("Category", "BindConfirmReq");
                    jsonObject.put("MsgId", msgID);
                    jsonObject.put("Answer", "Agree");
                    long devid = Long.parseLong(imei, 16);
                    BaseApplication.getInstances().getNattyClient().ntyBindConfirmReqClient(fromid,
                            devid, Integer.parseInt(msgID), jsonObject.toString().getBytes(), jsonObject.toString().getBytes().length);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        WindowManager.LayoutParams lp = bindComfirmDialog.getWindow().getAttributes();
        lp.width = QuanjiakanUtil.dip2px(BaseApplication.getInstances(), 280);
        lp.height = lp.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        bindComfirmDialog.setContentView(view, lp);
        bindComfirmDialog.setCanceledOnTouchOutside(false);
        bindComfirmDialog.show();

    }

}
