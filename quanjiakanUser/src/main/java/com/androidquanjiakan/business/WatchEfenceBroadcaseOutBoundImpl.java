package com.androidquanjiakan.business;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps2d.model.LatLng;
import com.androidquanjiakan.activity.index.watch_child.DigitalFanceActivity;
import com.androidquanjiakan.activity.index.watch_child.DigitalFanceActivity_new;
import com.androidquanjiakan.activity.index.watch_old.DigitalFanceOldActivity;
import com.androidquanjiakan.activity.index.watch_old.WatchOldEntryActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.database.TableInfo_ValueStub;
import com.androidquanjiakan.datahandler.BindDeviceHandler;
import com.androidquanjiakan.datahandler.DeviceRailHandler;
import com.androidquanjiakan.entity.BindDeviceEntity;
import com.androidquanjiakan.entity.DeviceRailEntity;
import com.androidquanjiakan.entity.WatchEfenceOutBound;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.util.NotificationUtil;
import com.google.gson.reflect.TypeToken;
import com.quanjiakan.main.R;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/16 0016.
 */

public class WatchEfenceBroadcaseOutBoundImpl implements IWatchBroadcaseSaver {
    private Context context;
    private int id;

    public void doBusiness(String data, Context context) {
        try {
            this.context = context;
            JSONObject jsonObject = new JSONObject(data);
            /**
             *{
             "Results": {
             "IMEI": "355637052788650",
             "Category": "EfenceReport",
             "EfenceReport": {
             "Type": "WIFI",
             "Radius": "550",
             "Location": "113.2409402,23.1326885",
             "Bounds": "In",
             "TimeStamp": "2017-04-06 20:51:45",
             "Index": "2",
             "Efence": {
             "Num": "3",
             "Points": [
             "113.2409402,23.1326885",
             "113.2409412,23.1326895",
             "113.2409408,23.1326890"
             ]
             }
             }
             }
             }
             */
            if (data != null && data.contains("\"Category\":\"EfenceReport\"".replace("\\", ""))
                    && data.contains("\"Bounds\":\"Out\"".replace("\\", ""))
                    && data.contains("Location")
                    && data.contains("Index")
                    && data.contains("TimeStamp")) {

                WatchEfenceOutBound result = (WatchEfenceOutBound) SerialUtil.jsonToObject(data, new TypeToken<WatchEfenceOutBound>() {
                }.getType());
                if (result != null && result.getResults() != null && result.getResults().getEfenceReport()!=null) {
                    String location = result.getResults().getEfenceReport().getLocation();
                    double mLat = Double.parseDouble(location.split(",")[1]);
                    double mLng = Double.parseDouble(location.split(",")[0]);
                    int index = Integer.parseInt(result.getResults().getEfenceReport().getIndex());
                    BindDeviceEntity entity = BindDeviceHandler.getValue(result.getResults().getIMEI());
                    if (entity != null) {//确保这个人是我绑定过的人，并且在WatchList中有记录


                        //********************************************************************
                        DeviceRailEntity locaData = new DeviceRailEntity();
                        if ("1".equals(result.getResults().getEfenceReport().getIndex())
                                && result.getResults().getEfenceReport().getEfence().getPoints().size() > 1) {
                            List<LatLng> first = new ArrayList<>();
                            for (String temp : result.getResults().getEfenceReport().getEfence().getPoints()) {
                                if (temp != null && temp.contains(",") && temp.trim().split(",").length == 2) {
                                    String[] points = temp.split(",");
                                    first.add(new LatLng(Double.parseDouble(points[1]), Double.parseDouble(points[0])));
                                }
                            }
                            //TODO 保存该数据
                            int size = first.size();
                            if (size > 1) {
//                                if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
//                                    locaData.setUserid(BaseApplication.getInstances().getUser_id());
//                                    locaData.setDeviceid(result.getResults().getIMEI());
//                                    locaData.setNumber(size + "");
//                                    locaData.setPath(EfenceUtil.ListPointToString(first));
//                                    locaData.setTime(System.currentTimeMillis() + "");
//                                    locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
//                                } else {
//                                    locaData = new DeviceRailEntity();
//                                    locaData.setUserid(BaseApplication.getInstances().getUser_id());
//                                    locaData.setDeviceid(result.getResults().getIMEI());
//                                    locaData.setNumber(size + "");
//                                    locaData.setPath(EfenceUtil.ListPointToString(first));
//                                    locaData.setTime(System.currentTimeMillis() + "");
//                                    locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
//                                }
//                                DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);

                                if("1".equals(entity.getW_TYPE())){
                                    id = NotificationUtil.sendOutBoundNotification(context, entity.getName(), result.getResults().getIMEI(), mLat, mLng, index,
                                            entity.getW_TYPE(), entity.getPhoneNumber(), result.getResults().getEfenceReport().getTimeStamp());
                                }else{
                                    id = NotificationUtil.sendOutBoundNotification(context,entity.getName(),result.getResults().getIMEI(), mLat, mLng,index,
                                            entity.getW_TYPE(),entity.getPhoneNumber(),result.getResults().getEfenceReport().getTimeStamp());
                                }

                                showWarmDialog(entity.getW_TYPE(),TYPE_OUT, entity, result.getResults().getIMEI(), mLat, mLng, index, result.getResults().getEfenceReport().getTimeStamp(),id);

                            }

                        } else if ("2".equals(result.getResults().getEfenceReport().getIndex())
                                && result.getResults().getEfenceReport().getEfence().getPoints().size() > 1) {
                            List<LatLng> second = new ArrayList<>();
                            for (String temp : result.getResults().getEfenceReport().getEfence().getPoints()) {
                                if (temp != null && temp.contains(",") && temp.trim().split(",").length == 2) {
                                    String[] points = temp.split(",");
                                    second.add(new LatLng(Double.parseDouble(points[1]), Double.parseDouble(points[0])));
                                }
                            }
                            //TODO 保存该数据
                            int size = second.size();
                            if (size > 1) {

//                                if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
//                                    locaData.setUserid(BaseApplication.getInstances().getUser_id());
//                                    locaData.setDeviceid(result.getResults().getIMEI());
//                                    locaData.setNumber(size + "");
//                                    locaData.setPath(EfenceUtil.ListPointToString(second));
//                                    locaData.setTime(System.currentTimeMillis() + "");
//                                    locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
//                                } else {
//                                    locaData = new DeviceRailEntity();
//                                    locaData.setUserid(BaseApplication.getInstances().getUser_id());
//                                    locaData.setDeviceid(result.getResults().getIMEI());
//                                    locaData.setNumber(size + "");
//                                    locaData.setPath(EfenceUtil.ListPointToString(second));
//                                    locaData.setTime(System.currentTimeMillis() + "");
//                                    locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
//                                }
//                                DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);

                                if("1".equals(entity.getW_TYPE())){
                                    id = NotificationUtil.sendOutBoundNotification(context,entity.getName(),result.getResults().getIMEI(), mLat, mLng,index,
                                            entity.getW_TYPE(),entity.getPhoneNumber(),result.getResults().getEfenceReport().getTimeStamp());
                                }else{
                                    id = NotificationUtil.sendOutBoundNotification(context,entity.getName(),result.getResults().getIMEI(), mLat, mLng,index,
                                            entity.getW_TYPE(),entity.getPhoneNumber(),result.getResults().getEfenceReport().getTimeStamp());
                                }
                                showWarmDialog(entity.getW_TYPE(),TYPE_OUT, entity, result.getResults().getIMEI(), mLat, mLng, index, result.getResults().getEfenceReport().getTimeStamp(),id);
                            }
                        } else if ("3".equals(result.getResults().getEfenceReport().getIndex())
                                && result.getResults().getEfenceReport().getEfence().getPoints().size() > 1) {
                            List<LatLng> third = new ArrayList<>();
                            for (String temp : result.getResults().getEfenceReport().getEfence().getPoints()) {
                                if (temp != null && temp.contains(",") && temp.trim().split(",").length == 2) {
                                    String[] points = temp.split(",");
                                    third.add(new LatLng(Double.parseDouble(points[1]), Double.parseDouble(points[0])));
                                }
                            }
                            //TODO 保存该数据
                            int size = third.size();
                            if(size>1) {

//                                if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
//                                    locaData.setUserid(BaseApplication.getInstances().getUser_id());
//                                    locaData.setDeviceid(result.getResults().getIMEI());
//                                    locaData.setNumber(size + "");
//                                    locaData.setPath(EfenceUtil.ListPointToString(third));
//                                    locaData.setTime(System.currentTimeMillis() + "");
//                                    locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
//                                } else {
//                                    locaData = new DeviceRailEntity();
//                                    locaData.setUserid(BaseApplication.getInstances().getUser_id());
//                                    locaData.setDeviceid(result.getResults().getIMEI());
//                                    locaData.setNumber(size + "");
//                                    locaData.setPath(EfenceUtil.ListPointToString(third));
//                                    locaData.setTime(System.currentTimeMillis() + "");
//                                    locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
//                                }
//                                DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);

                                if("1".equals(entity.getW_TYPE())){
                                    id = NotificationUtil.sendOutBoundNotification(context,entity.getName(),result.getResults().getIMEI(), mLat, mLng,index,
                                            entity.getW_TYPE(),entity.getPhoneNumber(),result.getResults().getEfenceReport().getTimeStamp());
                                }else{
                                    id = NotificationUtil.sendOutBoundNotification(context,entity.getName(),result.getResults().getIMEI(), mLat, mLng,index,
                                            entity.getW_TYPE(),entity.getPhoneNumber(),result.getResults().getEfenceReport().getTimeStamp());
                                }
                                showWarmDialog(entity.getW_TYPE(),TYPE_OUT, entity, result.getResults().getIMEI(), mLat, mLng, index, result.getResults().getEfenceReport().getTimeStamp(),id);
                            }
                        } else {
                            if("1".equals(entity.getW_TYPE())){
                                id = NotificationUtil.sendOutBoundNotification(context,entity.getName(),result.getResults().getIMEI(), mLat, mLng,index,
                                        entity.getW_TYPE(),entity.getPhoneNumber(),result.getResults().getEfenceReport().getTimeStamp());
                            }else{
                                id = NotificationUtil.sendOutBoundNotification(context,entity.getName(),result.getResults().getIMEI(), mLat, mLng,index,
                                        entity.getW_TYPE(),entity.getPhoneNumber(),result.getResults().getEfenceReport().getTimeStamp());
                            }
                            showWarmDialog(entity.getW_TYPE(),TYPE_OUT, entity, result.getResults().getIMEI(), mLat, mLng, index, null,id);
                        }

                        //********************************************************************

                    }
                }
            } else if (data != null && data.contains("\"Category\":\"EfenceReport\"".replace("\\", ""))
                    && data.contains("\"Bounds\":\"In\"".replace("\\", ""))
                    && data.contains("Location")
                    && data.contains("Index")
                    && data.contains("TimeStamp")) {

                WatchEfenceOutBound result = (WatchEfenceOutBound) SerialUtil.jsonToObject(data, new TypeToken<WatchEfenceOutBound>() {
                }.getType());
                if (result != null && result.getResults() != null) {
                    String location = result.getResults().getEfenceReport().getLocation();
                    double mLat = Double.parseDouble(location.split(",")[1]);
                    double mLng = Double.parseDouble(location.split(",")[0]);
                    int index = Integer.parseInt(result.getResults().getEfenceReport().getIndex());
                    BindDeviceEntity entity = BindDeviceHandler.getValue(result.getResults().getIMEI());
                    if (entity != null) {//确保这个人是我绑定过的人，并且在WatchList中有记录


                        //********************************************************************
                        DeviceRailEntity locaData = new DeviceRailEntity();
                        if ("1".equals(result.getResults().getEfenceReport().getIndex())
                                && result.getResults().getEfenceReport().getEfence().getPoints().size() > 1) {
                            List<LatLng> first = new ArrayList<>();
                            for (String temp : result.getResults().getEfenceReport().getEfence().getPoints()) {
                                if (temp != null && temp.contains(",") && temp.trim().split(",").length == 2) {
                                    String[] points = temp.split(",");
                                    first.add(new LatLng(Double.parseDouble(points[1]), Double.parseDouble(points[0])));
                                }
                            }
                            //TODO 保存该数据
                            int size = first.size();
                            if(size>1) {

//                                if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
//                                    locaData.setUserid(BaseApplication.getInstances().getUser_id());
//                                    locaData.setDeviceid(result.getResults().getIMEI());
//                                    locaData.setNumber(size + "");
//                                    locaData.setPath(EfenceUtil.ListPointToString(first));
//                                    locaData.setTime(System.currentTimeMillis() + "");
//                                    locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
//                                } else {
//                                    locaData = new DeviceRailEntity();
//                                    locaData.setUserid(BaseApplication.getInstances().getUser_id());
//                                    locaData.setDeviceid(result.getResults().getIMEI());
//                                    locaData.setNumber(size + "");
//                                    locaData.setPath(EfenceUtil.ListPointToString(first));
//                                    locaData.setTime(System.currentTimeMillis() + "");
//                                    locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
//                                }
//                                DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
                                if("1".equals(entity.getW_TYPE())){
                                    id = NotificationUtil.sendInBoundNotification(context, entity.getName(), result.getResults().getIMEI(), mLat, mLng, index,
                                            entity.getW_TYPE(), entity.getPhoneNumber(), result.getResults().getEfenceReport().getTimeStamp());
                                }else{
                                    id = NotificationUtil.sendInBoundNotification(context,entity.getName(),result.getResults().getIMEI(), mLat, mLng,index,
                                            entity.getW_TYPE(),entity.getPhoneNumber(),result.getResults().getEfenceReport().getTimeStamp());
                                }

                                showWarmDialog(entity.getW_TYPE(),TYPE_IN, entity, result.getResults().getIMEI(), mLat, mLng, index, result.getResults().getEfenceReport().getTimeStamp(),id);
                            }
                        } else if ("2".equals(result.getResults().getEfenceReport().getIndex())
                                && result.getResults().getEfenceReport().getEfence().getPoints().size() > 1) {
                            List<LatLng> second = new ArrayList<>();
                            for (String temp : result.getResults().getEfenceReport().getEfence().getPoints()) {
                                if (temp != null && temp.contains(",") && temp.trim().split(",").length == 2) {
                                    String[] points = temp.split(",");
                                    second.add(new LatLng(Double.parseDouble(points[1]), Double.parseDouble(points[0])));
                                }
                            }
                            //TODO 保存该数据
                            int size = second.size();
                            if(size>1) {

//                                if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
//                                    locaData.setUserid(BaseApplication.getInstances().getUser_id());
//                                    locaData.setDeviceid(result.getResults().getIMEI());
//                                    locaData.setNumber(size + "");
//                                    locaData.setPath(EfenceUtil.ListPointToString(second));
//                                    locaData.setTime(System.currentTimeMillis() + "");
//                                    locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
//                                } else {
//                                    locaData = new DeviceRailEntity();
//                                    locaData.setUserid(BaseApplication.getInstances().getUser_id());
//                                    locaData.setDeviceid(result.getResults().getIMEI());
//                                    locaData.setNumber(size + "");
//                                    locaData.setPath(EfenceUtil.ListPointToString(second));
//                                    locaData.setTime(System.currentTimeMillis() + "");
//                                    locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
//                                }
//                                DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);

                                if("1".equals(entity.getW_TYPE())){
                                    id = NotificationUtil.sendInBoundNotification(context,entity.getName(),result.getResults().getIMEI(), mLat, mLng,index,
                                            entity.getW_TYPE(),entity.getPhoneNumber(),result.getResults().getEfenceReport().getTimeStamp());
                                }else{
                                    id = NotificationUtil.sendInBoundNotification(context,entity.getName(),result.getResults().getIMEI(), mLat, mLng,index,
                                            entity.getW_TYPE(),entity.getPhoneNumber(),result.getResults().getEfenceReport().getTimeStamp());
                                }
                                showWarmDialog(entity.getW_TYPE(),TYPE_IN, entity, result.getResults().getIMEI(), mLat, mLng, index, result.getResults().getEfenceReport().getTimeStamp(),id);
                            }
                        } else if ("3".equals(result.getResults().getEfenceReport().getIndex())
                                && result.getResults().getEfenceReport().getEfence().getPoints().size() > 1) {
                            List<LatLng> third = new ArrayList<>();
                            for (String temp : result.getResults().getEfenceReport().getEfence().getPoints()) {
                                if (temp != null && temp.contains(",") && temp.trim().split(",").length == 2) {
                                    String[] points = temp.split(",");
                                    third.add(new LatLng(Double.parseDouble(points[1]), Double.parseDouble(points[0])));
                                }
                            }
                            //TODO 保存该数据
                            int size = third.size();
                            if(size>1) {

//                                if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
//                                    locaData.setUserid(BaseApplication.getInstances().getUser_id());
//                                    locaData.setDeviceid(result.getResults().getIMEI());
//                                    locaData.setNumber(size + "");
//                                    locaData.setPath(EfenceUtil.ListPointToString(third));
//                                    locaData.setTime(System.currentTimeMillis() + "");
//                                    locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
//                                } else {
//                                    locaData = new DeviceRailEntity();
//                                    locaData.setUserid(BaseApplication.getInstances().getUser_id());
//                                    locaData.setDeviceid(result.getResults().getIMEI());
//                                    locaData.setNumber(size + "");
//                                    locaData.setPath(EfenceUtil.ListPointToString(third));
//                                    locaData.setTime(System.currentTimeMillis() + "");
//                                    locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
//                                }
//                                DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);

                                if("1".equals(entity.getW_TYPE())){
                                    id = NotificationUtil.sendInBoundNotification(context,entity.getName(),result.getResults().getIMEI(), mLat, mLng,index,
                                            entity.getW_TYPE(),entity.getPhoneNumber(),result.getResults().getEfenceReport().getTimeStamp());
                                }else{
                                    id =  NotificationUtil.sendInBoundNotification(context,entity.getName(),result.getResults().getIMEI(), mLat, mLng,index,
                                            entity.getW_TYPE(),entity.getPhoneNumber(),result.getResults().getEfenceReport().getTimeStamp());
                                }

                                showWarmDialog(entity.getW_TYPE(),TYPE_IN, entity, result.getResults().getIMEI(), mLat, mLng, index, result.getResults().getEfenceReport().getTimeStamp(),id);
                            }
                        } else {
                            if("1".equals(entity.getW_TYPE())){
                                id = NotificationUtil.sendInBoundNotification(context,entity.getName(),result.getResults().getIMEI(), mLat, mLng,index,
                                        entity.getW_TYPE(),entity.getPhoneNumber(),result.getResults().getEfenceReport().getTimeStamp());
                            }else{
                                id = NotificationUtil.sendInBoundNotification(context,entity.getName(),result.getResults().getIMEI(), mLat, mLng,index,
                                        entity.getW_TYPE(),entity.getPhoneNumber(),result.getResults().getEfenceReport().getTimeStamp());
                            }

                            showWarmDialog(entity.getW_TYPE(),TYPE_IN, entity, result.getResults().getIMEI(), mLat, mLng, index, null,id);
                        }

                        //********************************************************************

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Dialog confirmDialog;
    private final int TYPE_IN = 1;
    private final int TYPE_OUT = 2;

    public void showWarmDialog(String W_type,int typeInOut, final BindDeviceEntity entity, final String imei, final double mLat, final double mLng, final int index, final String time,final int id) {
        if (confirmDialog != null && confirmDialog.isShowing()) {
//            confirmDialog.dismiss();
        } else {
            confirmDialog = new Dialog(context, R.style.dialog_loading);
            confirmDialog.setCanceledOnTouchOutside(false);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_efence_out_bound_warn, null);
            final TextView watch_name = (TextView) view.findViewById(R.id.watch_name);
            final TextView hint_msg = (TextView) view.findViewById(R.id.hint_msg);
            final ImageView img = (ImageView) view.findViewById(R.id.img);
            try {
                if (typeInOut == TYPE_IN) {
                    watch_name.setText(URLDecoder.decode(entity.getName(), "utf-8") + "手表进入 "+index+" 号安全区域");
                    watch_name.setTextColor(context.getResources().getColor(R.color.font_color_333333));
                    hint_msg.setText(URLDecoder.decode(entity.getName(), "utf-8") + "手表已回到 "+index+" 号安全区,请放心!");
                    hint_msg.setTextColor(context.getResources().getColor(R.color.color_6f6f6f));
                    if("1".equals(W_type)){
                        img.setImageResource(R.drawable.crawl_icon_in_children);
                    }else{
                        img.setImageResource(R.drawable.crawl_icon_in_old);
                    }
                } else {
                    watch_name.setText(URLDecoder.decode(entity.getName(), "utf-8") + "手表离开 "+index+" 号安全区域");
                    watch_name.setTextColor(context.getResources().getColor(R.color.color_e32b2b));
                    hint_msg.setText(URLDecoder.decode(entity.getName(), "utf-8") + "手表离开 "+index+" 号安全区，请及时联系确认当前状况是否安全");
                    hint_msg.setTextColor(context.getResources().getColor(R.color.color_6f6f6f));
                    if("1".equals(W_type)){
                        img.setImageResource(R.drawable.crawl_icon_out_children);
                    }else{
                        img.setImageResource(R.drawable.crawl_icon_out_old);
                    }
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            TextView confirm = (TextView) view.findViewById(R.id.confirm);
            TextView cancel = (TextView) view.findViewById(R.id.cancel);

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDialog.dismiss();
                    //TODO 跳转到电子围栏页面，并显示对应的电子围栏
                    if ("1".equals(entity.getW_TYPE())) {//儿童
                        Intent intent = new Intent(context, DigitalFanceActivity_new.class);
                        intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, imei);
                        intent.putExtra(BaseConstants.PARAMS_DEVICE_TYPE, "1");
                        intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, mLat);
                        intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, mLng);
                        intent.putExtra(BaseConstants.PARAMS_DEVICE_PHONE, entity.getPhoneNumber());
                        if (time != null) {
                            intent.putExtra(BaseConstants.PARAMS_DEVICE_PAGE_STATE, DigitalFanceActivity.STATUS_EDIT);
                            intent.putExtra(BaseConstants.PARAMS_DEVICE_PAGE_WHICH, index);
                        }
                        context.startActivity(intent);
                    } else {//老人
                        Intent intent = new Intent(context, DigitalFanceOldActivity.class);
                        intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, imei);
                        intent.putExtra(BaseConstants.PARAMS_DEVICE_TYPE, "0");
                        intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, mLat);
                        intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, mLng);
                        intent.putExtra(BaseConstants.PARAMS_DEVICE_PHONE, entity.getPhoneNumber());
                        if (time != null) {
                            intent.putExtra(BaseConstants.PARAMS_DEVICE_PAGE_STATE, DigitalFanceActivity.STATUS_EDIT);
                            intent.putExtra(BaseConstants.PARAMS_DEVICE_PAGE_WHICH, index);
                        }
                        context.startActivity(intent);
                    }
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



    public void sendNotification(){

    }
}
