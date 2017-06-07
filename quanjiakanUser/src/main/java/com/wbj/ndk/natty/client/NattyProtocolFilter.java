package com.wbj.ndk.natty.client;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.androidquanjiakan.activity.setting.contact.bean.AddContactEvent;
import com.androidquanjiakan.activity.setting.contact.bean.ChangeContactsEvent;
import com.androidquanjiakan.activity.setting.contact.bean.ClassDisableEvent;
import com.androidquanjiakan.activity.setting.contact.bean.ContactsChangeResultEvent;
import com.androidquanjiakan.activity.setting.contact.bean.DataResultEvent;
import com.androidquanjiakan.activity.setting.contact.bean.FareEvent;
import com.androidquanjiakan.activity.setting.contact.bean.RunTimeEvent;
import com.androidquanjiakan.activity.setting.contact.bean.RunTimeResultEvent;
import com.androidquanjiakan.activity.setting.contact.bean.TagetStepEvent;
import com.androidquanjiakan.activity.setting.contact.bean.WorKRestEvent;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.constant.ConstantClassFunction;
import com.androidquanjiakan.entity.BindingRequestBean;
import com.androidquanjiakan.entity.BroadCastBean;
import com.androidquanjiakan.entity.CommonBindResult;
import com.androidquanjiakan.entity.CommonNattyData;
import com.androidquanjiakan.entity.CommonVoiceData;
import com.androidquanjiakan.entity.ContactsBean;
import com.androidquanjiakan.entity.CourseBean;
import com.androidquanjiakan.entity.FareandFlowDataBean;
import com.androidquanjiakan.entity.RunTimeCategoryBean;
import com.androidquanjiakan.entity.ScheduleBean;
import com.androidquanjiakan.entity.ScheduleListEntity;
import com.androidquanjiakan.entity.ScheduleResultsEntity;
import com.androidquanjiakan.entity.rusumeBean;
import com.androidquanjiakan.result.ContactsResultBean;
import com.androidquanjiakan.result.RuntimeResultBean;
import com.androidquanjiakan.util.GsonUtil;
import com.androidquanjiakan.util.LogUtil;
import com.example.greendao.dao.BindingRequestBeanDao;
import com.example.greendao.dao.BroadCastBeanDao;
import com.example.greendao.dao.ContactsBeanDao;
import com.example.greendao.dao.FareandFlowDataBeanDao;
import com.example.greendao.dao.RunTimeCategoryBeanDao;
import com.example.greendao.dao.rusumeBeanDao;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quanjiakanuser.util.GsonParseUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NattyProtocolFilter {

    public final static int DISPLAY_UPDATE_CONTROL_POWER = 0x11;
    public final static int DISPLAY_UPDATE_CONTROL_SIGNAL = 0x12;
    public final static int DISPLAY_UPDATE_CONTROL_LOCATION = 0x13;
    public final static int DISPLAY_UPDATE_CONTROL_FALL = 0x14;
    public final static int DISPLAY_UPDATE_CONTROL_STEP = 0x15;
    public final static int DISPLAY_UPDATE_CONTROL_HEARTRATE = 0x16;
    public final static int DISPLAY_UPDATE_CONTROL_RECONNECTED = 0x17;
    public final static int DISPLAY_UPDATE_CONTROL_DISCONNECT = 0x18;
    public final static int DISPLAY_UPDATE_CONTROL_NOEXIST = 0x19;

    public final static int DISPLAY_UPDATE_CONTROL_BIND_RESULT = 0x1A;
    public final static int DISPLAY_UPDATE_CONTROL_UNBIND_RESULT = 0x1B;
    public final static int DISPLAY_UPDATE_STATUS = 0x1C;
    public final static int DISPLAY_UPDATE_CONFIG = 0x1D;

    public final static int VOICE_UPLOAD = 0x2A;
    public final static int VOICE_RECORD = 0x2B;
    public final static int VOICE_STOP = 0x2C;
    public final static int DISPLAY_VOICE_PLAY = 0x2D;

    public final static int DISPLAY_VOICE_SEND_RESULT = 0x2E;

    public final static int DISPLAY_EFENCE_RESULT = 0x30;
    public final static int DISPLAY_EFENCELIST_RESULT = 0x31;


    public final static int DISPLAY_UPDATE_CONTROL_LOCATION_NEW = 0x41;
    public final static int DISPLAY_UPDATE_DATA_RESULT = 0x42;
    public final static int DISPLAY_UPDATE_DATA_ROUTE = 0x43;
    public final static int DISPLAY_UPDATE_DATA_COMMON_BROADCAST = 0x44;
    public final static int DISPLAY_UPDATE_DATA_ADMIN_BIND_INFO = 0x45;

    public final static int DISPLAY_UPDATE_ERROR = 0xFF;
    public final static int DISPLAY_UPDATE_SUCCESS = 0xF0;


    private static List<Handler> handlerList = new ArrayList<Handler>();
    private static ContactsBean contactsBean;
    private static ContactsResultBean contactsResultBean;
    private static ContactsResultBean.ResultsBean resultBean;
    private static List<ContactsResultBean.ResultsBean.ContactsBean> contacts;


    public static void ntyProtocolFilter(String recv) {
        String[] takens = recv.split(" ");

        for (int i = 0; i < takens.length; i++) {
            LogUtil.i("taken " + i + ":" + takens[i]);
        }

        switch (NattyUtils.ntyCompareOpera(takens[0])) {
            case NattyUtils.NTY_MSG_OPERA_GET:
                break;
            case NattyUtils.NTY_MSG_OPERA_SET: {
                int node = NattyUtils.ntyCompareNode(takens[1]);

                LogUtil.i("NTY_MSG_OPERA_SET node " + node);
                switch (node) {
                    case NattyUtils.NTY_MSG_NODE_EFENCELIST: {
                        for (Handler handler : handlerList) {
                            if (handler != null) {
                                Message msg = new Message();
                                Bundle b = new Bundle();

                                b.putString("EFENCELIST", takens[2]);
                                msg.what = DISPLAY_EFENCELIST_RESULT;
                                msg.setData(b);
                                LogUtil.i("NTY_MSG_NODE_EFENCELIST");
                                handler.sendMessage(msg);
                            }
                        }
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_EFENCE: {
                        for (Handler handler : handlerList) {
                            if (handler != null) {
                                Message msg = new Message();
                                Bundle b = new Bundle();

                                b.putString("EFENCE", takens[2]);
                                msg.what = DISPLAY_EFENCE_RESULT;
                                msg.setData(b);
                                LogUtil.i("NTY_MSG_NODE_EFENCE");
                                handler.sendMessage(msg);
                            }
                        }
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_POWER: {
                        for (Handler handler : handlerList) {
                            if (handler != null) {
                                Message msg = new Message();
                                Bundle b = new Bundle();

                                b.putString("Power", takens[2]);
                                msg.what = DISPLAY_UPDATE_CONTROL_POWER;
                                msg.setData(b);
                                LogUtil.i("NTY_MSG_NODE_POWER");
                                handler.sendMessage(msg);
                            }
                        }
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_SIGNAL: {
                        for (Handler handler : handlerList) {
                            if (handler != null) {
                                Message msg = new Message();
                                Bundle b = new Bundle();

                                b.putString("Signal", takens[2]);
                                msg.what = DISPLAY_UPDATE_CONTROL_SIGNAL;
                                msg.setData(b);
                                LogUtil.i("NTY_MSG_NODE_SIGNAL");
                                handler.sendMessage(msg);
                            }
                        }
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_PHONEBOOK: {
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_FAMILYNUMBER: {
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_FALLEN: {
                        for (Handler handler : handlerList) {
                            if (handler != null) {
                                Message msg = new Message();
                                Bundle b = new Bundle();

                                b.putString("Fall", takens[2]);
                                msg.what = DISPLAY_UPDATE_CONTROL_FALL;
                                msg.setData(b);
                                LogUtil.i("DISPLAY_UPDATE_CONTROL_FALL");
                                handler.sendMessage(msg);
                            }
                        }
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_GPS: {
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_LAB: {
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_WIFI: {
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_LOCATION: {
                        for (Handler handler : handlerList) {
                            if (handler != null) {
                                Message msg = new Message();
                                Bundle b = new Bundle();

                                b.putString("Location", takens[2]);
                                msg.what = DISPLAY_UPDATE_CONTROL_LOCATION;
                                msg.setData(b);
                                LogUtil.i("NTY_MSG_NODE_POWER");
                                handler.sendMessage(msg);
                            }
                        }
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_STEP: {
                        for (Handler handler : handlerList) {
                            if (handler != null) {
                                Message msg = new Message();
                                Bundle b = new Bundle();

                                b.putString("Step", takens[2]);
                                msg.what = DISPLAY_UPDATE_CONTROL_STEP;
                                msg.setData(b);
                                LogUtil.i("NTY_MSG_NODE_STEP");
                                handler.sendMessage(msg);
                            }
                        }
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_HEARTRATE: {//
                        for (Handler handler : handlerList) {
                            if (handler != null) {
                                Message msg = new Message();
                                Bundle b = new Bundle();

                                b.putString("HeartRate", takens[2]);
                                msg.what = DISPLAY_UPDATE_CONTROL_HEARTRATE;
                                msg.setData(b);
                                LogUtil.i("NTY_MSG_NODE_HEARTRATE");
                                handler.sendMessage(msg);
                            }
                        }
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_CONFIG: { //
                        for (Handler handler : handlerList) {
                            if (handler != null) {
                                Message msg = new Message();
                                Bundle b = new Bundle();

                                b.putString("Config", takens[2]);
                                msg.what = DISPLAY_UPDATE_CONFIG;
                                msg.setData(b);
                                LogUtil.i("NTY_MSG_NODE_HEARTRATE");
                                handler.sendMessage(msg);
                            }
                        }
                        break;
                    }
                }

                break;
            }
            case NattyUtils.NTY_MSG_OPERA_START:
                break;
        }
    }

    public static void ntyProtocolFilter(String id, String recv) {
        String[] takens = recv.split(" ");
        LogUtil.e("----------------------SDK 收到的回调数据:ID---" + id + " ***  Data:" + recv);
        for (int i = 0; i < takens.length; i++) {
            LogUtil.i("taken " + i + ":" + takens[i]);
        }

        switch (NattyUtils.ntyCompareOpera(takens[0])) {
            case NattyUtils.NTY_MSG_OPERA_GET:
                break;
            case NattyUtils.NTY_MSG_OPERA_SET: {
                int node = NattyUtils.ntyCompareNode(takens[1]);

                LogUtil.i("NTY_MSG_OPERA_SET node " + node);
                switch (node) {
                    case NattyUtils.NTY_MSG_NODE_EFENCELIST: {
                        for (Handler handler : handlerList) {
                            if (handler != null) {
                                Message msg = new Message();
                                Bundle b = new Bundle();

                                b.putString("EFENCELIST", takens[2]);
                                b.putString("id", id + "");
                                msg.what = DISPLAY_EFENCELIST_RESULT;
                                msg.setData(b);
                                LogUtil.i("NTY_MSG_NODE_EFENCELIST");
                                handler.sendMessage(msg);
                            }
                        }
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_EFENCE: {
                        for (Handler handler : handlerList) {
                            if (handler != null) {
                                Message msg = new Message();
                                Bundle b = new Bundle();

                                b.putString("EFENCE", takens[2]);
                                b.putString("id", id + "");
                                msg.what = DISPLAY_EFENCE_RESULT;
                                msg.setData(b);
                                LogUtil.i("NTY_MSG_NODE_EFENCE");
                                handler.sendMessage(msg);
                            }
                        }
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_POWER: {
                        for (Handler handler : handlerList) {
                            if (handler != null) {
                                Message msg = new Message();
                                Bundle b = new Bundle();

                                b.putString("Power", takens[2]);
                                b.putString("id", id + "");
                                msg.what = DISPLAY_UPDATE_CONTROL_POWER;
                                msg.setData(b);
                                LogUtil.i("NTY_MSG_NODE_POWER");
                                handler.sendMessage(msg);
                            }
                        }
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_SIGNAL: {
                        for (Handler handler : handlerList) {
                            if (handler != null) {
                                Message msg = new Message();
                                Bundle b = new Bundle();

                                b.putString("Signal", takens[2]);
                                b.putString("id", id + "");
                                msg.what = DISPLAY_UPDATE_CONTROL_SIGNAL;
                                msg.setData(b);
                                LogUtil.i("NTY_MSG_NODE_SIGNAL");
                                handler.sendMessage(msg);
                            }
                        }
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_PHONEBOOK: {
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_FAMILYNUMBER: {
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_FALLEN: {
                        for (Handler handler : handlerList) {
                            if (handler != null) {
                                Message msg = new Message();
                                Bundle b = new Bundle();

                                b.putString("Fall", takens[2]);
                                b.putString("id", id + "");
                                msg.what = DISPLAY_UPDATE_CONTROL_FALL;
                                msg.setData(b);
                                LogUtil.i("DISPLAY_UPDATE_CONTROL_FALL");
                                handler.sendMessage(msg);
                            }
                        }
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_GPS: {
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_LAB: {
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_WIFI: {
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_LOCATION: {
                        for (Handler handler : handlerList) {
                            if (handler != null) {
                                Message msg = new Message();
                                Bundle b = new Bundle();

                                b.putString("Location", takens[2]);
                                b.putString("id", id + "");
                                msg.what = DISPLAY_UPDATE_CONTROL_LOCATION;
                                msg.setData(b);
                                LogUtil.i("NTY_MSG_NODE_POWER");
                                handler.sendMessage(msg);
                            }
                        }
                        break;
                    }
                    case NattyUtils.NTY_MSG_NODE_STEP:
                        for (Handler handler : handlerList) {
                            if (handler != null) {
                                Message msg = new Message();
                                Bundle b = new Bundle();

                                b.putString("Step", takens[2]);
                                b.putString("id", id + "");
                                msg.what = DISPLAY_UPDATE_CONTROL_STEP;
                                msg.setData(b);
                                LogUtil.i("NTY_MSG_NODE_STEP");
                                handler.sendMessage(msg);
                            }
                        }
                        break;
                    case NattyUtils.NTY_MSG_NODE_HEARTRATE: //
                        for (Handler handler : handlerList) {
                            if (handler != null) {
                                Message msg = new Message();
                                Bundle b = new Bundle();

                                b.putString("HeartRate", takens[2]);
                                b.putString("id", id + "");
                                msg.what = DISPLAY_UPDATE_CONTROL_HEARTRATE;
                                msg.setData(b);
                                LogUtil.i("NTY_MSG_NODE_HEARTRATE");
                                handler.sendMessage(msg);
                            }
                        }
                        break;
                    case NattyUtils.NTY_MSG_NODE_CONFIG: //
                        for (Handler handler : handlerList) {
                            if (handler != null) {
                                Message msg = new Message();
                                Bundle b = new Bundle();

                                b.putString("Config", takens[2]);
                                b.putString("id", id + "");
                                msg.what = DISPLAY_UPDATE_CONFIG;
                                msg.setData(b);
                                LogUtil.i("NTY_MSG_NODE_HEARTRATE");
                                handler.sendMessage(msg);
                            }
                        }
                        break;
                }

                break;
            }
            case NattyUtils.NTY_MSG_OPERA_START:
                break;
        }
    }

    /**
     * case 1:
     * [MBProgressHUD showError:@"该UserID不存在"];
     * break;
     * case 2:
     * [MBProgressHUD showError:@"该设备ID不存在"];
     * break;
     * case 3:
     * [MBProgressHUD showError:@"UserId与DeviceId已经绑定过了"];
     * break;
     * case 4:
     * [MBProgressHUD showError:@"该设备未激活，请激活"];
     * break;
     * <p>
     * case 0:  绑定成功
     *
     * @param len
     */
    public static void ntyProtocolBind(int len) {
//        for (Handler handler : handlerList) {
//            if (handler != null) {
//                Message msg = new Message();
//                Bundle b = new Bundle();
//
//                b.putString("Bind", len + "");
//                msg.what = DISPLAY_UPDATE_CONTROL_BIND_RESULT;
//                msg.setData(b);
//                LogUtil.i("DISPLAY_UPDATE_CONTROL_BIND_RESULT");
//                handler.sendMessage(msg);
//            }
//        }
        EventBus.getDefault().post(new CommonNattyData(DISPLAY_UPDATE_CONTROL_BIND_RESULT,len + ""));
    }

    public static void ntyProtocolUnBind(int len) {
//        for (Handler handler : handlerList) {
//            if (handler != null) {
//                Message msg = new Message();
//                Bundle b = new Bundle();
//
//                b.putString("UnBind", len + "");
//                msg.what = DISPLAY_UPDATE_CONTROL_UNBIND_RESULT;
//                msg.setData(b);
//                LogUtil.i("DISPLAY_UPDATE_CONTROL_UNBIND_RESULT");
//                handler.sendMessage(msg);
//            }
//        }
        EventBus.getDefault().post(new CommonNattyData(DISPLAY_UPDATE_CONTROL_UNBIND_RESULT,len + ""));

    }


    public static void ntyProtocolReconnect(int len) {
        BaseApplication.getInstances().setSDKServerStatus("1");
    }

    public static void ntyProtocolDisconnect(int len) {
        BaseApplication.getInstances().setSDKServerStatus("-1");
    }

    //{"Results":{"IMEI":"352315052834187","Proposer":"18011935659","UserName":"爸爸","MsgId":"40"}}
    public static void ntyProtocolAdminBindComfirmCallBack(long fromID,String info) {
//        for (Handler handler : handlerList) {
//            if (handler != null) {
//                Message msg = new Message();
//                Bundle b = new Bundle();
//
//                b.putLong("fromid", fromID);
//                b.putString("json", info);
//                msg.what = DISPLAY_UPDATE_DATA_ADMIN_BIND_INFO;
//                msg.setData(b);
//                LogUtil.i("DISPLAY_UPDATE_DATA_ADMIN_BIND_INFO");
//                handler.sendMessage(msg);
//            }
//        }
        EventBus.getDefault().post(new CommonBindResult(DISPLAY_UPDATE_DATA_ADMIN_BIND_INFO,fromID,info));
        LogUtil.e("绑定申请的数据-------------------------"+info);
        //{"Results":{"IMEI":"352315052834187","Proposer":"18011935659","UserName":"爸爸","MsgId":"40"}}
        JsonObject jsonObject = new GsonParseUtil(info).getJsonObject();
        JsonObject object = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());
        BindingRequestBeanDao bindingRequestBeanDao = BaseApplication.getInstances().getDaoInstant().getBindingRequestBeanDao();
        BindingRequestBean bindingRequestBean = new BindingRequestBean(null, info, object.get("IMEI").getAsString(),fromID+"");

        try {
            bindingRequestBeanDao.insert(bindingRequestBean);
        }catch (Exception e) {
//            BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(),"数据插入失败");
        }
    }

    public static void ntyProtocolNoExist(int len) {
//        for (Handler handler : handlerList) {
//            if (handler != null) {
//                Message msg = new Message();
//                Bundle b = new Bundle();
//
//                b.putString("NoExist", "1");
//                msg.what = DISPLAY_UPDATE_CONTROL_NOEXIST;
//                msg.setData(b);
//                LogUtil.i("DISPLAY_UPDATE_CONTROL_NOEXIST");
//                handler.sendMessage(msg);
//            }
//        }
    }

    public static void ntyProtocolPlayVoice(int len, byte[] buffer, long fromId) {
//        for (Handler handler : handlerList) {
//            if (handler != null) {
//                Message msg = new Message();
//                Bundle b = new Bundle();
//
//                b.putByteArray("Data", buffer);
//                b.putString("Len", len + "");
//                b.putString("FromId", fromId + "");
//
//                msg.what = DISPLAY_VOICE_PLAY;
//                msg.setData(b);
//                LogUtil.i("DISPLAY_VOICE_PLAY");
//                handler.sendMessage(msg);
//            }
//        }
        EventBus.getDefault().post(new CommonVoiceData(DISPLAY_VOICE_PLAY,buffer,len+"",fromId+""));
    }

    public static void ntyProtocolSendVoiceResult(int len, long fromId) {
//        for (Handler handler : handlerList) {
//            if (handler != null) {
//                Message msg = new Message();
//                Bundle b = new Bundle();
//
//                b.putString("Result", len + "");
//                b.putString("FromId", fromId + "");
//
//                msg.what = DISPLAY_VOICE_SEND_RESULT;
//                msg.setData(b);
//                LogUtil.i("DISPLAY_VOICE_PLAY");
//                handler.sendMessage(msg);
//            }
//        }
    }

    //*********************************************************
    public static void ntyProtocolSendSuccess(long fromId, int len) {
//        for (Handler handler : handlerList) {
//            if (handler != null) {
//                Message msg = new Message();
//                Bundle b = new Bundle();
//
//                b.putString("Success", len + "");
//                b.putString("FromId", fromId + "");
//
//                msg.what = DISPLAY_UPDATE_SUCCESS;
//                msg.setData(b);
//                LogUtil.i("DISPLAY_UPDATE_SUCCESS");
//                handler.sendMessage(msg);
//            }
//        }
    }

    public static void ntyProtocolSendFail(long fromId, int len) {
//        for (Handler handler : handlerList) {
//            if (handler != null) {
//                Message msg = new Message();
//                Bundle b = new Bundle();
//
//                b.putString("Error", len + "");
//                b.putString("FromId", fromId + "");
//
//                msg.what = DISPLAY_UPDATE_ERROR;
//                msg.setData(b);
//                LogUtil.i("DISPLAY_UPDATE_ERROR");
//                handler.sendMessage(msg);
//            }
//        }
    }

    public static void ntyProtocolLocationResult(long id, String data) {
//        for (Handler handler : handlerList) {
//            if (handler != null) {
//                Message msg = new Message();
//                Bundle b = new Bundle();
//
//                b.putString("Location", data);
//                b.putString("id", id + "");
//                msg.what = DISPLAY_UPDATE_CONTROL_LOCATION_NEW;
//                msg.setData(b);
//                LogUtil.i("DISPLAY_UPDATE_CONTROL_LOCATION_NEW");
//                handler.sendMessage(msg);
//            }
//        }
        EventBus.getDefault().post(new CommonNattyData(DISPLAY_UPDATE_CONTROL_LOCATION_NEW,data));
    }

    public static void ntyProtocolVoiceResult(long id, String data) {
//        for (Handler handler : handlerList) {
//            if (handler != null) {
//                Message msg = new Message();
//                Bundle b = new Bundle();
//
//                b.putString("Location", data);
//                b.putString("id", id + "");
//                msg.what = DISPLAY_VOICE_PLAY;
//                msg.setData(b);
//                LogUtil.i("DISPLAY_VOICE_PLAY");
//                handler.sendMessage(msg);
//            }
//        }
//        EventBus.getDefault().post(new CommonNattyData(DISPLAY_VOICE_PLAY,data));
    }

    public static void ntyDataResult(long id, String data) {
//        for (Handler handler : handlerList) {
//            if (handler != null) {
//                Message msg = new Message();
//                Bundle b = new Bundle();
//                b.putString("Data", data);
//                msg.what = DISPLAY_UPDATE_DATA_RESULT;
//                msg.setData(b);
//                LogUtil.i("DISPLAY_UPDATE_DATA_RESULT");
//                handler.sendMessage(msg);
//            }
//        }
        EventBus.getDefault().post(new CommonNattyData(DISPLAY_UPDATE_DATA_RESULT,data));
    }

    public static void ntyDataRoute(long id, String data) {
//        for (Handler handler : handlerList) {
//            if (handler != null) {
//                Message msg = new Message();
//                Bundle b = new Bundle();
//                b.putString("Data", data);
//                msg.what = DISPLAY_UPDATE_DATA_ROUTE;
//                msg.setData(b);
//                LogUtil.i("DISPLAY_UPDATE_DATA_ROUTE");
//                handler.sendMessage(msg);
//            }
//        }
        EventBus.getDefault().post(new CommonNattyData(DISPLAY_UPDATE_DATA_ROUTE,data));
    }

    public static void ntyCommonBroadcastResult(long id, String data) {
//        for (Handler handler : handlerList) {
//            if (handler != null) {
//                Message msg = new Message();
//                Bundle b = new Bundle();
//                b.putString("Data", data);
//                msg.what = DISPLAY_UPDATE_DATA_COMMON_BROADCAST;
//                msg.setData(b);
//                LogUtil.i("DISPLAY_UPDATE_DATA_COMMON_BROADCAST");
//                handler.sendMessage(msg);
//            }
//        }
        EventBus.getDefault().post(new CommonNattyData(DISPLAY_UPDATE_DATA_COMMON_BROADCAST,data));
    }

    /**
     * 保存广播数据
     *
     * @param id
     * @param data
     * @param status
     */
    public static void ntyCommonBroadcastResult(long id, String data, int status)  {
        LogUtil.e("广播:" + data);



        /**根据不同内容保存相应数据 **/

        Gson gson = new Gson();

        JsonObject jsonObject = new GsonParseUtil(data).getJsonObject();


        //有Results类型
        if (jsonObject.has(ConstantClassFunction.getRESULTS())) {
            JsonObject result = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());

            /**
             *
             * 根据imei保存广播
             */
            BroadCastBeanDao broadCastBeanDao = BaseApplication.getInstances().getDaoInstant().getBroadCastBeanDao();
            //把所有广播都存进去
            BroadCastBean broadCastBean = new BroadCastBean(null, data, id + "", System.currentTimeMillis() + "", result.get("IMEI").getAsString());
            try {
                broadCastBeanDao.insert(broadCastBean);
            } catch (Exception e) {
                LogUtil.e("数据库插入失败！");
            }

            LogUtil.e("广播数据-------------------"+data);

            if (result.has(ConstantClassFunction.getCATEGORY())) {
                String category = result.get(ConstantClassFunction.getCATEGORY()).getAsString();
                if (ConstantClassFunction.RUNTIME.equals(category)) {


                    //拿数据库数据
                    RunTimeCategoryBeanDao runTimeCategoryBeanDao = BaseApplication.getInstances().getDaoInstant().getRunTimeCategoryBeanDao();
//                    List<RunTimeCategoryBean> categoryBeanList = runTimeCategoryBeanDao.loadAll();

                    if (runTimeCategoryBeanDao.loadAll().size() > 0) {
                        for (int i = 0; i < runTimeCategoryBeanDao.loadAll().size(); i++) {
                            if (result.get("IMEI").getAsString().equals(runTimeCategoryBeanDao.loadAll().get(i).getImei())) {
                                RunTimeCategoryBean runTimeCategoryBean = runTimeCategoryBeanDao.loadAll().get(i);
                                String json = runTimeCategoryBean.getJson();
                                LogUtil.e("runtimeResultBean-----" + json);
                                RuntimeResultBean runtimeResultBean = gson.fromJson(json, RuntimeResultBean.class);
                                RuntimeResultBean.ResultsBean runtimeResultBeanResult = runtimeResultBean.getResults();

                                RuntimeResultBean.ResultsBean.RunTimeBean runTime = runtimeResultBeanResult.getRunTime();
                                JsonObject runTime2 = result.getAsJsonObject(ConstantClassFunction.RUNTIME);
                                if (runTime2.has("AutoConnection")) {
                                    runTime.setAutoConnection(runTime2.get("AutoConnection").getAsString());
                                }

                                if (runTime2.has("LightPanel")) {
                                    runTime.setLightPanel(runTime2.get("LightPanel").getAsString());
                                }


                                if (runTime2.has("LossReport")) {
                                    runTime.setLossReport(runTime2.get("LossReport").getAsString());
                                }

                                if (runTime2.has("TagetStep")) {
                                    runTime.setTagetStep(runTime2.get("TagetStep").getAsString());
                                }

                                if (runTime2.has("WatchBell")) {
                                    runTime.setWatchBell(runTime2.get("WatchBell").getAsString());
                                }

                                runtimeResultBeanResult.setRunTime(runTime);
                                //保存到数据库
                                runtimeResultBean.setResults(runtimeResultBeanResult);
                                String json1 = gson.toJson(runtimeResultBean);
                                runTimeCategoryBean.setJson(json1);
                                runTimeCategoryBeanDao.update(runTimeCategoryBean);

                            }

                        }

                    }

                } else if (ConstantClassFunction.TURN.equals(category)) {

                    rusumeBeanDao rusumeBeanDao = BaseApplication.getInstances().getDaoInstant().getRusumeBeanDao();
                    for (int k = 0; k < rusumeBeanDao.loadAll().size(); k++) {
                        if (result.get("IMEI").getAsString().equals(rusumeBeanDao.loadAll().get(k).getImei())) {
                            rusumeBean rusumeBean = rusumeBeanDao.loadAll().get(k);
                            rusumeBean.setJson(data);
                            rusumeBeanDao.update(rusumeBean);
                        }
                    }

                } else if (ConstantClassFunction.CONTACTS.equals(category)) {

                    LogUtil.e("broad----------联系人1---------"+data);
                    ContactsBeanDao contactsBeanDao = BaseApplication.getInstances().getDaoInstant().getContactsBeanDao();
                    if (contactsBeanDao.loadAll().size() > 0) {
//                        contactsBean = contactsBeanDao.loadAll().get(0);
                        for (int i = 0; i < contactsBeanDao.loadAll().size(); i++) {

                            if (result.get("IMEI").getAsString().equals(contactsBeanDao.loadAll().get(i).getImei())) {

                                contactsBean = contactsBeanDao.loadAll().get(i);
                                String json = contactsBeanDao.loadAll().get(i).getJson();
                                contactsResultBean = gson.fromJson(json, ContactsResultBean.class);
                                resultBean = contactsResultBean.getResults();
                                contacts = resultBean.getContacts();

                                JsonObject contact = result.getAsJsonObject(ConstantClassFunction.CONTACTS);
                                if (ConstantClassFunction.ADD.equals(result.get(ConstantClassFunction.ACTION).getAsString())&&(contact.has("Image")&&contact.has("Name")&&contact.has("Tel")&&contact.has("Id"))) {//添加

                                    LogUtil.e("broad----------联系人add2---------");
                                    ContactsResultBean.ResultsBean.ContactsBean bean = new ContactsResultBean.ResultsBean.ContactsBean();
                                    bean.setApp("0");
                                    bean.setAdmin("0");
                                    bean.setImage(contact.get("Image").getAsString());
                                    bean.setName(contact.get("Name").getAsString());
                                    bean.setTel(contact.get("Tel").getAsString());
                                    bean.setId(contact.get("Id").getAsString());

                                    //保存到数据库
                                    contacts.add(bean);
                                    resultBean.setContacts(contacts);
                                    contactsResultBean.setResults(resultBean);
                                    String contactsjson = gson.toJson(contactsResultBean);
                                    contactsBean.setJson(contactsjson);
                                    contactsBeanDao.update(contactsBean);


                                } else if (ConstantClassFunction.UPDATE.equals(result.get(ConstantClassFunction.ACTION).getAsString())) {
                                    LogUtil.e("broad----------联系人update2---------");
                                    /**
                                     *{
                                     "Result": {
                                     "IMEI": "355637052788650",
                                     "Category": "Contacts",
                                     "Action": "Update",
                                     "Contact": {
                                     "Image": "1",
                                     "Name": "爷爷",
                                     "Tel": "13838384383",
                                     "Id": "1234"
                                     }
                                     }
                                     }
                                     */
                                    for (ContactsResultBean.ResultsBean.ContactsBean c : contacts) {
                                        if (c.getId().equals(contact.get("Id").getAsString())) {
                                            c.setImage(contact.get("Image").getAsString());
                                            c.setName(contact.get("Name").getAsString());
                                            c.setTel(contact.get("Tel").getAsString());

                                            resultBean.setContacts(contacts);
                                            contactsResultBean.setResults(resultBean);
                                            String contactsjson = gson.toJson(contactsResultBean);
                                            contactsBean.setJson(contactsjson);
                                            contactsBeanDao.update(contactsBean);
                                        }
                                    }

                                }else if (ConstantClassFunction.DELETE.equals(result.get(ConstantClassFunction.ACTION).getAsString())) {
                                    //删除联系人
                                    LogUtil.e("broad----------联系人delete2---------");
                                    for (ContactsResultBean.ResultsBean.ContactsBean c : contacts) {
                                        if (c.getId().equals(result.get("Id").getAsString())) {
                                            contacts.remove(c);
                                            resultBean.setContacts(contacts);
                                            contactsResultBean.setResults(resultBean);
                                            String contactsjson = gson.toJson(contactsResultBean);
                                            contactsBean.setJson(contactsjson);
                                            contactsBeanDao.update(contactsBean);
                                        }
                                    }

                                }

                            }
                        }

                    }

                } else if(category.equals(ConstantClassFunction.SCHEDULE)) {
                    //{"Results":{"IMEI":"355637053995130","Category":"Schedule","Action":"Add","Id":"165","Schedule":{"Daily":"Tuesday|Thursday|Sunday","Time":"11:32:00","Details":"%E6%96%B9%E6%B3%95%E5%B9%B2%E6%B4%BB"}}}
                    //增加
                    if(result.has("Id")&&result.has("Action")&&result.get("Action").getAsString().equals("Add")) {
                        LogUtil.e("增加作息表进来吧");
                        String imei = result.get("IMEI").getAsString();
                        String category1 = result.get("Category").getAsString();
                        String action = result.get("Action").getAsString();
                        String id1 = result.get("Id").getAsString();
                        JsonObject schedule = result.get("Schedule").getAsJsonObject();
                        String daily = schedule.get("Daily").getAsString();
                        String time = schedule.get("Time").getAsString();
                        String details = schedule.get("Details").getAsString();
                        try {
                            String encode = URLEncoder.encode(details, "utf-8");
                        String status1 = schedule.get("Status").getAsString();

                            ScheduleListEntity scheduleListEntity = new ScheduleListEntity();
                            List<ScheduleListEntity> alist = new ArrayList<>();
                            scheduleListEntity.setId(id1);
                            scheduleListEntity.setTime(time);
                            scheduleListEntity.setDaily(daily);
                            scheduleListEntity.setDetails(details);
                            scheduleListEntity.setStatus(status1);

                            alist.add(scheduleListEntity);

                            ScheduleResultsEntity scheduleResultsEntity1 = new ScheduleResultsEntity();
                            scheduleResultsEntity1.setResults(new ScheduleResultsEntity.results(imei,category1,"",alist));
                            List<ScheduleBean> scheduList = BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().queryBuilder().list();
                            if(scheduList.size()>0) {
                                for (int i=0;i<scheduList.size();i++){
                                    if(imei.equals(scheduList.get(i).getImei())) {
                                        ScheduleBean scheduleBean = scheduList.get(i);
                                        ScheduleResultsEntity scheduleResultsEntity = GsonUtil.fromJson(scheduleBean.getJson(), ScheduleResultsEntity.class);
                                        List<ScheduleListEntity> list = scheduleResultsEntity.getResults().getSchedule();
                                        list.add(scheduleListEntity);
                                        Collections.sort(list);//排序

                                        scheduleResultsEntity.getResults().setSchedule(list);
                                        String json = GsonUtil.toJson(scheduleResultsEntity);
                                        scheduleBean.setJson(json);
                                        BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().update(scheduleBean);
                                        break;
                                    }else {
                                        if(i==scheduList.size()-1) {
                                            ScheduleBean scheduleBean = new ScheduleBean();
                                            scheduleBean.setId(null);
                                            scheduleBean.setImei(imei);
                                            scheduleBean.setJson(GsonUtil.toJson(scheduleResultsEntity1));
                                            BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().insert(scheduleBean);
                                        }
                                    }
                                }
                            }else {
                                ScheduleBean scheduleBean = new ScheduleBean();
                                scheduleBean.setId(null);
                                scheduleBean.setImei(imei);
                                scheduleBean.setJson(GsonUtil.toJson(scheduleResultsEntity1));
                                BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().insert(scheduleBean);
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        //{"Results":{"IMEI":"355637053995130","Category":"Schedule","Action":"Delete","Id":"165"}}
                        //删除
                    }else if(result.has("Id")&&result.has("Action")&&result.get("Action").getAsString().equals("Delete")) {
                        LogUtil.e("删除作息表进来了吧");
                        String imei = result.get("IMEI").getAsString();
                        String Id = result.get("Id").getAsString();
                        List<ScheduleBean> list = BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().queryBuilder().list();
                        if(list.size()>0) {
                            for (int i=0;i<list.size();i++){
                                if(imei.equals(list.get(i).getImei())) {
                                    ScheduleBean scheduleBean = list.get(i);
                                    String json1 = scheduleBean.getJson();
                                    ScheduleResultsEntity scheduleResultsEntity = GsonUtil.fromJson(json1, ScheduleResultsEntity.class);
                                    ScheduleResultsEntity.results results = scheduleResultsEntity.getResults();

                                    if(getDeleteList(i,Id)==null) {
                                        BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().delete(list.get(i));
                                        break;
                                    }else {
                                        results.setSchedule(getDeleteList(i,Id));
                                    }

                                    scheduleResultsEntity.setResults(results);
                                    String toJson = GsonUtil.toJson(scheduleResultsEntity);
                                    scheduleBean.setJson(toJson);
                                    BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().update(scheduleBean);

                                }
                            }
                        }
                        //{"Results":{"IMEI":"355637053995130","Category":"Schedule","Action":"Update","Id":"84","Schedule":{"Daily":"Wednesday|Saturday","Time":"15:42:00","Details":"%E4%BD%A0%E5%A5%BD"}}}
                        //更新
                    }else if(result.has("Id")&&result.has("Action")&&result.get("Action").getAsString().equals("Update")) {
                        String imei = result.get("IMEI").getAsString();
                        String category1 = result.get("Category").getAsString();
                        String action = result.get("Action").getAsString();
                        String id1 = result.get("Id").getAsString();
                        JsonObject schedule = result.get("Schedule").getAsJsonObject();
                        String daily = schedule.get("Daily").getAsString();
                        String time = schedule.get("Time").getAsString();
                        String details = schedule.get("Details").getAsString();
                        String status1 = schedule.get("Status").getAsString();
                        LogUtil.e("edit status:"+status1);
                        /*String decode = null;*/
                        try {
                            String  encode = URLEncoder.encode(details, "utf-8");
                            List<ScheduleBean> list = BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().loadAll();
                            if(list.size()>0) {
                                for (int i=0;i<list.size();i++){
                                    if(imei.equals(list.get(i).getImei())) {
                                        ScheduleBean scheduleBean = list.get(i);
                                        String s = scheduleBean.getJson();
                                        ScheduleResultsEntity scheduleResultsEntity = GsonUtil.fromJson(s, ScheduleResultsEntity.class);
                                        ScheduleResultsEntity.results results = scheduleResultsEntity.getResults();
                                        results.setSchedule(getUpdateList(i,id1,details,time,daily,status1));

                                        scheduleResultsEntity.setResults(results);
                                        String toJson = GsonUtil.toJson(scheduleResultsEntity);
                                        scheduleBean.setJson(toJson);
                                        BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().update(scheduleBean);

                                    }

                                }

                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                    }
                }else if(category.equals(ConstantClassFunction.TIMETABLES)) {
                    LogUtil.e("上课禁用进来了吧");
                    String imei = result.get("IMEI").getAsString();
                    List<CourseBean> list = BaseApplication.getInstances().getDaoInstant().getCourseBeanDao().queryBuilder().list();
                    if(list.size()>0) {
                        for (int i=0;i<list.size();i++){
                            if(imei.equals(list.get(i).getImei())) {
                                CourseBean courseBean = list.get(i);
                                courseBean.setJson(data);
                                BaseApplication.getInstances().getDaoInstant().update(courseBean);
                                break;
                            }else {
                                if(i==list.size()-1) {
                                    CourseBean courseBean = new CourseBean();
                                    courseBean.setId(null);
                                    courseBean.setJson(data);
                                    courseBean.setImei(imei);
                                }
                            }
                        }
                    }else {
                        CourseBean courseBean = new CourseBean();
                        courseBean.setId(null);
                        courseBean.setJson(data);
                        courseBean.setImei(imei);
                    }

                }

            }
        }

    }

    //获取要更新对应id和imel的列表
    private static List<ScheduleListEntity> getUpdateList(int t, String Id, String details, String time, String daily,String status) {
        ScheduleBean scheduleBean = BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().queryBuilder().list().get(t);
        String json = scheduleBean.getJson();
        ScheduleResultsEntity scheduleResultsEntity = GsonUtil.fromJson(json, ScheduleResultsEntity.class);

        List<ScheduleListEntity> schedule = scheduleResultsEntity.getResults().getSchedule();

        for (int i=0;i<schedule.size();i++){
            ScheduleListEntity scheduleListEntity = schedule.get(i);
            if(Id.equals(scheduleListEntity.getId())) {
                ScheduleListEntity scheduleListEntity1 = schedule.get(i);
                scheduleListEntity1.setDetails(details);
                scheduleListEntity1.setTime(time);
                scheduleListEntity1.setId(Id);
                scheduleListEntity1.setDaily(daily);
                scheduleListEntity1.setStatus(status);
                return schedule;
            }

        }
        return schedule;
    }


    //获取要删除对应imei和Id的ScheduleListEntity列表
    private static List<ScheduleListEntity> getDeleteList(int t,String Id) {

        ScheduleBean scheduleBean = BaseApplication.getInstances().getDaoInstant().getScheduleBeanDao().queryBuilder().list().get(t);
        String json = scheduleBean.getJson();
        ScheduleResultsEntity scheduleResultsEntity = GsonUtil.fromJson(json, ScheduleResultsEntity.class);

        List<ScheduleListEntity> schedule = scheduleResultsEntity.getResults().getSchedule();
        if (schedule.size() == 1) {
            return null;
        }
        for (int i = 0; i < schedule.size(); i++) {
            ScheduleListEntity scheduleListEntity = schedule.get(i);
            if (Id.equals(scheduleListEntity.getId())) {
                schedule.remove(i);
                return schedule;
            }

        }
        return new ArrayList<>();
    }

    /******
     * Turn and runtime
     *********/

    public static void ntyProtocolTurn(String json) {
        EventBus.getDefault().post(new RunTimeEvent(json));
        EventBus.getDefault().post(new TagetStepEvent(json));
        EventBus.getDefault().post(new ClassDisableEvent(json));
        EventBus.getDefault().post(new WorKRestEvent(json));
        EventBus.getDefault().post(new AddContactEvent(json));
        EventBus.getDefault().post(new ChangeContactsEvent(json));
        EventBus.getDefault().post(new DataResultEvent(json));

    }

    /******
     * Turn and runtime
     *********/

    public static void ntyProtocolRunTime(String json) {

        LogUtil.e("ntyProtocolRunTime--------------------");
        EventBus.getDefault().post(new RunTimeResultEvent(json));
        EventBus.getDefault().post(new TagetStepEvent(json));
        EventBus.getDefault().post(new ClassDisableEvent(json));
        EventBus.getDefault().post(new ContactsChangeResultEvent(json));
    }

    public static void ntyProtocolDateRoute(String json) {
        EventBus.getDefault().post(new FareEvent(json));
        EventBus.getDefault().post(new TagetStepEvent(json));
        //这里对数据进行过滤
        JsonObject jsonObject = new GsonParseUtil(json).getJsonObject();
        if (jsonObject.has(ConstantClassFunction.getRESULTS())) {
            JsonObject results = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());
            if ("Fare".equals(results.get(ConstantClassFunction.getCATEGORY()).getAsString()) || "Flow".equals(results.get(ConstantClassFunction.getCATEGORY()).getAsString())) {
                FareandFlowDataBeanDao fareandFlowDataBeanDao = BaseApplication.getInstances().getDaoInstant().getFareandFlowDataBeanDao();
                FareandFlowDataBean fareandFlowDataBean = new FareandFlowDataBean(null, json, results.get("IMEI").getAsString());

                try {
                    fareandFlowDataBeanDao.insert(fareandFlowDataBean);

                } catch (Exception e) {
//                    BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(),"数据插入失败");
                }

            }
        }



    }

    public static void ntyProtocolBindConfirmResult(long fromId,String json) {

    }

}
