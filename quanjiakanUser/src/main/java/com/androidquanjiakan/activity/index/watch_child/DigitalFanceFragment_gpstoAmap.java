package com.androidquanjiakan.activity.index.watch_child;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.PolygonOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.androidquanjiakan.activity.index.MainEntryMapActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.business.EfenceUtil;
import com.androidquanjiakan.database.TableInfo_ValueStub;
import com.androidquanjiakan.datahandler.DeviceRailHandler;
import com.androidquanjiakan.entity.CommonNattyData;
import com.androidquanjiakan.entity.DeviceRailEntity;
import com.androidquanjiakan.entity.WatchEfenceBroadcastAdd;
import com.androidquanjiakan.entity.WatchEfenceDelete;
import com.androidquanjiakan.entity.WatchEfenceNet;
import com.androidquanjiakan.entity.WatchEfenceResult;
import com.androidquanjiakan.entity.WatchLocationBroadcase;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.entity_util.UnitUtil;
import com.androidquanjiakan.util.CheckUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.util.MapUtil;
import com.androidquanjiakan.util.NaviMapUtil;
import com.google.gson.reflect.TypeToken;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.umeng.analytics.MobclickAgent;
import com.wbj.ndk.natty.client.NattyProtocolFilter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.androidquanjiakan.activity.index.MainEntryMapActivity.DEL_EFENCE;
import static com.androidquanjiakan.activity.index.MainEntryMapActivity.GET_LOCATION;
import static com.androidquanjiakan.activity.index.MainEntryMapActivity.SET_EFENCE;

public class DigitalFanceFragment_gpstoAmap extends Fragment implements AMap.OnMarkerClickListener,
        AMap.OnInfoWindowClickListener, AMap.OnMarkerDragListener, AMap.OnMapLoadedListener,
        View.OnClickListener, AMap.InfoWindowAdapter,
        GeocodeSearch.OnGeocodeSearchListener, AMap.OnMapClickListener {

    private final int SOLID_ZOOM = 16;

    private AMap aMap;
    private MapView mapView;
    private GeocodeSearch geocoderSearch;

    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    //
    private double selfLatitude = -1000;
    private double selfLongitude = -1000;
    private String selfAdress = "";
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //***********************************************
    private PolylineOptions mPolyoptions;


    private List<LatLng> first;
    private List<LatLng> second;
    private List<LatLng> third;

    public static final int FIRST = 1;
    public static final int SECOND = 2;
    public static final int THIRD = 3;
    public static final int STATUS_SHOW = 1;
    public static final int STATUS_EDIT = 2;
    public static final int WHICH_DEFAULT = 0;

    private int pageStatus = STATUS_SHOW;
    private int pageWhich = FIRST;

    private int lineStatus;


    private String device_id;
    private String phoneNumberString;
    public String mAddress = "";
    private int currentCommand;
    private int mPosition;
    private Dialog noticeDialog;

    public int getPageStatus() {
        return pageStatus;
    }

    public void setPageStatus(int pageStatus) {
        this.pageStatus = pageStatus;
    }

    public int getPageWhich() {
        return pageWhich;
    }

    public void setPageWhich(int pageWhich) {
        this.pageWhich = pageWhich;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getPhoneNumberString() {
        return phoneNumberString;
    }

    public void setPhoneNumberString(String phoneNumberString) {
        this.phoneNumberString = phoneNumberString;
    }

    public double getmLat() {
        return mLat;
    }

    public void setmLat(double mLat) {
        this.mLat = mLat;
    }

    public double getmLon() {
        return mLon;
    }

    public void setmLon(double mLon) {
        this.mLon = mLon;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            closePopupWindow();
            switch (msg.what) {
                case NattyProtocolFilter.DISPLAY_UPDATE_CONTROL_LOCATION_NEW: {

                    break;
                }
                case NattyProtocolFilter.DISPLAY_UPDATE_DATA_COMMON_BROADCAST: {
                    //{"Id":"2","Results":{"IMEI":"355637053995130","Category":"Efence","Action":"Add","Index":"2","Efence":{"Num":"4","Points":["113.242554,23.139700","113.242584,23.132236","113.252510,23.131338","113.252510,23.139931"]}}}
                    String data = msg.getData().getString("Data");
                    if (data != null && data.contains("Efence") && data.contains("Add")) {
                        WatchEfenceBroadcastAdd result = (WatchEfenceBroadcastAdd) SerialUtil.jsonToObject(data, new TypeToken<WatchEfenceBroadcastAdd>() {
                        }.getType());
                        if (result != null && device_id.equals(result.getResults().getIMEI())) {
                            DeviceRailEntity locaData = new DeviceRailEntity();
                            if ("1".equals(result.getResults().getIndex())) {
                                first.clear();
                                for (String temp : result.getResults().getEfence().getPoints()) {
                                    String[] points = temp.split(",");
                                    first.add(new LatLng(Double.parseDouble(points[1]), Double.parseDouble(points[0])));
                                }
                                //TODO 保存该数据
                                int size = first.size();
                                if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
                                    locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                    locaData.setDeviceid(device_id);
                                    locaData.setNumber(size + "");
                                    locaData.setPath(EfenceUtil.ListPointToString(first));
                                    locaData.setTime(System.currentTimeMillis() + "");
                                    locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
                                } else {
                                    locaData = new DeviceRailEntity();
                                    locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                    locaData.setDeviceid(device_id);
                                    locaData.setNumber(size + "");
                                    locaData.setPath(EfenceUtil.ListPointToString(first));
                                    locaData.setTime(System.currentTimeMillis() + "");
                                    locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
                                }
                                DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
                                getRailNumber();

                            } else if ("2".equals(result.getResults().getIndex())) {
                                second.clear();
                                for (String temp : result.getResults().getEfence().getPoints()) {
                                    String[] points = temp.split(",");
                                    second.add(new LatLng(Double.parseDouble(points[1]), Double.parseDouble(points[0])));
                                }
                                //TODO 保存该数据
                                int size = second.size();
                                if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
                                    locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                    locaData.setDeviceid(device_id);
                                    locaData.setNumber(size + "");
                                    locaData.setPath(EfenceUtil.ListPointToString(second));
                                    locaData.setTime(System.currentTimeMillis() + "");
                                    locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
                                } else {
                                    locaData = new DeviceRailEntity();
                                    locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                    locaData.setDeviceid(device_id);
                                    locaData.setNumber(size + "");
                                    locaData.setPath(EfenceUtil.ListPointToString(second));
                                    locaData.setTime(System.currentTimeMillis() + "");
                                    locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
                                }
                                DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
                                getRailNumber();
                            } else if ("3".equals(result.getResults().getIndex())) {
                                third.clear();
                                for (String temp : result.getResults().getEfence().getPoints()) {
                                    String[] points = temp.split(",");
                                    third.add(new LatLng(Double.parseDouble(points[1]), Double.parseDouble(points[0])));
                                }
                                //TODO 保存该数据
                                int size = third.size();
                                if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
                                    locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                    locaData.setDeviceid(device_id);
                                    locaData.setNumber(size + "");
                                    locaData.setPath(EfenceUtil.ListPointToString(third));
                                    locaData.setTime(System.currentTimeMillis() + "");
                                    locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
                                } else {
                                    locaData = new DeviceRailEntity();
                                    locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                    locaData.setDeviceid(device_id);
                                    locaData.setNumber(size + "");
                                    locaData.setPath(EfenceUtil.ListPointToString(third));
                                    locaData.setTime(System.currentTimeMillis() + "");
                                    locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
                                }
                                DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
                                getRailNumber();
                            } else {

                            }
                        }
                    } else if (data != null && data.contains("Efence") && data.contains("Delete")) {
                        //TODO 收到删除
                        pageStatus = STATUS_SHOW;
                        getAddress(new LatLonPoint(mLat, mLon));
                        //{"Results":{"IMEI":"355637053995130","Category":"Efence","Action":"Delete","Index":"1"}}
                        WatchEfenceDelete result = (WatchEfenceDelete) SerialUtil.jsonToObject(data, new TypeToken<WatchEfenceDelete>() {
                        }.getType());
                        if (result != null && device_id.equals(result.getResults().getIMEI())) {
                            if ("1".equals(result.getResults().getIndex())) {
                                DeviceRailHandler.remove(device_id, TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST, "");
                                getRailNumber();
                            } else if ("2".equals(result.getResults().getIndex())) {
                                DeviceRailHandler.remove(device_id, TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND, "");
                                getRailNumber();
                            } else if ("3".equals(result.getResults().getIndex())) {
                                DeviceRailHandler.remove(device_id, TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD, "");
                                getRailNumber();
                            } else {

                            }
                        }
                    }
                    break;
                }
                case NattyProtocolFilter.DISPLAY_UPDATE_DATA_RESULT: {
                    //需要自己根据发送的命令去判断
                    String data = msg.getData().getString("Data");
                    switch (currentCommand) {
                        case MainEntryMapActivity.SET_EFENCE: {
                            LogUtil.e("DATA_RESULT  SET_EFENCE：" + data);
                            WatchEfenceResult result = (WatchEfenceResult) SerialUtil.jsonToObject(data, new TypeToken<WatchEfenceResult>() {
                            }.getType());
                            if (result != null && "200".equals(result.getResults().getCode())) {
                                DeviceRailEntity locaData = new DeviceRailEntity();
                                switch (pageWhich) {
                                    case FIRST: {
                                        int size = first.size();
                                        if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
                                            locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                            locaData.setDeviceid(device_id);
                                            locaData.setNumber(size + "");
                                            locaData.setPath(EfenceUtil.ListPointToString(first));
                                            locaData.setTime(System.currentTimeMillis() + "");
                                            locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
                                        } else {
                                            locaData = new DeviceRailEntity();
                                            locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                            locaData.setDeviceid(device_id);
                                            locaData.setNumber(size + "");
                                            locaData.setPath(EfenceUtil.ListPointToString(first));
                                            locaData.setTime(System.currentTimeMillis() + "");
                                            locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
                                        }
                                        DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
                                        getRailNumber();
                                        break;
                                    }
                                    case SECOND: {
                                        int size = second.size();
                                        if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
                                            locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                            locaData.setDeviceid(device_id);
                                            locaData.setNumber(size + "");
                                            locaData.setPath(EfenceUtil.ListPointToString(second));
                                            locaData.setTime(System.currentTimeMillis() + "");
                                            locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
                                        } else {
                                            locaData = new DeviceRailEntity();
                                            locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                            locaData.setDeviceid(device_id);
                                            locaData.setNumber(size + "");
                                            locaData.setPath(EfenceUtil.ListPointToString(second));
                                            locaData.setTime(System.currentTimeMillis() + "");
                                            locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
                                        }
                                        DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
                                        getRailNumber();
                                        break;
                                    }
                                    case THIRD: {
                                        int size = third.size();
                                        if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
                                            locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                            locaData.setDeviceid(device_id);
                                            locaData.setNumber(size + "");
                                            locaData.setPath(EfenceUtil.ListPointToString(third));
                                            locaData.setTime(System.currentTimeMillis() + "");
                                            locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
                                        } else {
                                            locaData = new DeviceRailEntity();
                                            locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                            locaData.setDeviceid(device_id);
                                            locaData.setNumber(size + "");
                                            locaData.setPath(EfenceUtil.ListPointToString(third));
                                            locaData.setTime(System.currentTimeMillis() + "");
                                            locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
                                        }
                                        DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
                                        getRailNumber();
                                        break;
                                    }
                                }
                            } else if (result != null && "10001".equals(result.getResults().getCode())) {
                                BaseApplication.getInstances().toast(getActivity(), "设备不在线");
                            } else if (result != null && "10009".equals(result.getResults().getCode())) {
                                BaseApplication.getInstances().toast(getActivity(), "已存在围栏，请删除后再设置!");
                            } else {
                                BaseApplication.getInstances().toast(getActivity(), "设置围栏失败 ");
                            }
                            break;
                        }
                        case MainEntryMapActivity.DEL_EFENCE: {
                            LogUtil.e("DATA_RESULT  DEL_EFENCE：" + data);
                            WatchEfenceResult result = (WatchEfenceResult) SerialUtil.jsonToObject(data, new TypeToken<WatchEfenceResult>() {
                            }.getType());
                            if (result != null && "200".equals(result.getResults().getCode())) {
                                switch (pageWhich) {
                                    case FIRST: {
                                        //需要判断成功了才进行这个操作
                                        DeviceRailHandler.remove(device_id, TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST, "");
                                        getRailNumber();
                                        break;
                                    }
                                    case SECOND: {
                                        DeviceRailHandler.remove(device_id, TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND, "");
                                        getRailNumber();
                                        break;
                                    }
                                    case THIRD: {
                                        DeviceRailHandler.remove(device_id, TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD, "");
                                        getRailNumber();
                                        break;
                                    }
                                }
                            } else if (result != null && "10001".equals(result.getResults().getCode())) {
                                BaseApplication.getInstances().toast(getActivity(), "设备不在线");
                            } else {
                                BaseApplication.getInstances().toast(getActivity(), "设置围栏失败");
                            }
                            break;
                        }
                    }
                    break;
                }
                case NattyProtocolFilter.DISPLAY_UPDATE_DATA_ROUTE: {
                    //需要自己根据发送的命令去判断
                    String data = msg.getData().getString("Data");
                    switch (currentCommand) {
                        case MainEntryMapActivity.SET_EFENCE: {
                            LogUtil.e("DATA_ROUTE  SET_EFENCE：" + data);
//                            WatchCommonResult result = (WatchCommonResult) SerialUtil.jsonToObject(data,new TypeToken<WatchCommonResult>(){}.getType());
//                            if(result!=null && "200".equals(result.getResult().getCode())){
//                                BaseApplication.getInstances().toast("DigitalFanceActivity.this,设置围栏成功  ROUTE");
//                            }else if(result!=null && "10001".equals(result.getResult().getCode())){
//                                BaseApplication.getInstances().toast("DigitalFanceActivity.this,设备不在线  ROUTE");
//                            }else{
//                                BaseApplication.getInstances().toast(DigitalFanceActivity.this,"设置围栏失败  ROUTE");
//                            }
                            break;
                        }
                        case MainEntryMapActivity.DEL_EFENCE: {
                            LogUtil.e("DATA_ROUTE  SET_EFENCE：" + data);
//                            WatchCommonResult result = (WatchCommonResult) SerialUtil.jsonToObject(data,new TypeToken<WatchCommonResult>(){}.getType());
//                            if(result!=null && "200".equals(result.getResult().getCode())){
//                                BaseApplication.getInstances().toast(DigitalFanceActivity.this,"设置围栏成功  ROUTE");
//                            }else if(result!=null && "10001".equals(result.getResult().getCode())){
//                                BaseApplication.getInstances().toast(DigitalFanceActivity.this,"设备不在线  ROUTE");
//                            }else{
//                                BaseApplication.getInstances().toast(DigitalFanceActivity.this,"设置围栏失败  ROUTE");
//                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }
    };


    //弹出提示设置围栏的dialog
    private void showNoticeDialog() {

        noticeDialog = new Dialog(getActivity(), R.style.ShareDialog);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_fence_notice, null);
        RelativeLayout rl_ok = (RelativeLayout) view.findViewById(R.id.rl_ok);
        rl_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noticeDialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = noticeDialog.getWindow().getAttributes();

        lp.width = QuanjiakanUtil.dip2px(getActivity(), 300);
        lp.height = lp.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        noticeDialog.setContentView(view);
        noticeDialog.getWindow().setLayout(lp.width, lp.height);
        noticeDialog.show();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_child_digital_fance, null);
        initStore();
        initTitleBar(view);
        initMap(view, savedInstanceState);
//        JSON.
        startMap();
        getSynchronizedData();
        LogUtil.e("Fragment onCreateView ");


        getLocation();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.e("Fragment onCreate ");
    }


    private final long divTime = 24 * 60 * 60 * 1000;

    public void getSynchronizedData() {
        //TODO 需要增加判断，当存在一个数据

        MyHandler.putTask(getActivity(), new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String data) {
                long deviceid = Long.parseLong(device_id, 16);
                if (data != null && data.contains("\"Category\":\"EfenceList\"".replace("\\", ""))
                        && (data.contains(device_id) || data.contains(deviceid + ""))) {
                    DeviceRailHandler.removeAll();
                    WatchEfenceNet netData = (WatchEfenceNet) SerialUtil.jsonToObject(data, new TypeToken<WatchEfenceNet>() {
                    }.getType());
                    if (netData != null && netData.getResults() != null && netData.getResults().getEfenceList() != null
                            && netData.getResults().getEfenceList().size() > 0) {
                        for (WatchEfenceNet.ResultsBean.EfenceListBean temp : netData.getResults().getEfenceList()) {
                            if (temp != null && temp.getEfence() != null) {
                                if ("1".equals(temp.getIndex())) {
                                    List<LatLng> points = new ArrayList<LatLng>();
                                    for (String string : temp.getEfence().getPoints()) {
                                        if (string != null && string.contains(",") && string.trim().split(",").length == 2) {
                                            String[] onePoint = string.split(",");
                                            points.add(new LatLng(Double.parseDouble(onePoint[1]), Double.parseDouble(onePoint[0])));
                                        }
                                    }
                                    locaData = new DeviceRailEntity();
                                    int size = points.size();
                                    if (size > 1) {
                                        if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
                                            locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                            locaData.setDeviceid(device_id);
                                            locaData.setNumber(size + "");
                                            locaData.setPath(EfenceUtil.ListPointToString(points));
                                            locaData.setTime(System.currentTimeMillis() + "");
                                            locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
                                        } else {
                                            locaData = new DeviceRailEntity();
                                            locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                            locaData.setDeviceid(device_id);
                                            locaData.setNumber(size + "");
                                            locaData.setPath(EfenceUtil.ListPointToString(points));
                                            locaData.setTime(System.currentTimeMillis() + "");
                                            locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
                                        }
                                        DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
                                    }
                                } else if ("2".equals(temp.getIndex())) {
                                    List<LatLng> points = new ArrayList<LatLng>();
                                    for (String string : temp.getEfence().getPoints()) {
                                        if (string != null && string.contains(",") && string.trim().split(",").length == 2) {
                                            String[] onePoint = string.split(",");
                                            points.add(new LatLng(Double.parseDouble(onePoint[1]), Double.parseDouble(onePoint[0])));
                                        }
                                    }
                                    locaData = new DeviceRailEntity();
                                    int size = points.size();
                                    if (size > 1) {

                                        if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
                                            locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                            locaData.setDeviceid(device_id);
                                            locaData.setNumber(size + "");
                                            locaData.setPath(EfenceUtil.ListPointToString(points));
                                            locaData.setTime(System.currentTimeMillis() + "");
                                            locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
                                        } else {
                                            locaData = new DeviceRailEntity();
                                            locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                            locaData.setDeviceid(device_id);
                                            locaData.setNumber(size + "");
                                            locaData.setPath(EfenceUtil.ListPointToString(points));
                                            locaData.setTime(System.currentTimeMillis() + "");
                                            locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
                                        }
                                        DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
                                    }
                                } else if ("3".equals(temp.getIndex())) {
                                    List<LatLng> points = new ArrayList<LatLng>();
                                    for (String string : temp.getEfence().getPoints()) {
                                        if (string != null && string.contains(",") && string.trim().split(",").length == 2) {
                                            String[] onePoint = string.split(",");
                                            points.add(new LatLng(Double.parseDouble(onePoint[1]), Double.parseDouble(onePoint[0])));
                                        }
                                    }
                                    locaData = new DeviceRailEntity();
                                    int size = points.size();
                                    if (size > 1) {
                                        if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
                                            locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                            locaData.setDeviceid(device_id);
                                            locaData.setNumber(size + "");
                                            locaData.setPath(EfenceUtil.ListPointToString(points));
                                            locaData.setTime(System.currentTimeMillis() + "");
                                            locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
                                        } else {
                                            locaData = new DeviceRailEntity();
                                            locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                            locaData.setDeviceid(device_id);
                                            locaData.setNumber(size + "");
                                            locaData.setPath(EfenceUtil.ListPointToString(points));
                                            locaData.setTime(System.currentTimeMillis() + "");
                                            locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
                                        }
                                        DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
                                    }
                                }

                            }
                        }
                        getRailNumber();

                        //
                        if (pageStatus == STATUS_EDIT) {//TODO 当前为编辑界面，则展示----------正常不会为编辑，当传入参数为编辑时，则为越界提醒跳转
                            aMap.clear();//清除图标----越界通知
                            digitalRailList = DeviceRailHandler.getAllValue(device_id);
                            if (digitalRailList.size() > 0) {
                                //TODO 初始化围栏数据
                                DeviceRailEntity firsts = null;
                                DeviceRailEntity seconds = null;
                                DeviceRailEntity thirds = null;
                                for (DeviceRailEntity temp : digitalRailList) {
                                    if (temp.getPosition() != null && TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST.equals(temp.getPosition())) {
                                        firsts = temp;
                                    } else if (temp.getPosition() != null && TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND.equals(temp.getPosition())) {
                                        seconds = temp;
                                    } else if (temp.getPosition() != null && TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD.equals(temp.getPosition())) {
                                        thirds = temp;
                                    }
                                }
                                if (firsts != null) {
                                    first = StringToListPoint(firsts);
                                }
                                if (seconds != null) {
                                    second = StringToListPoint(seconds);
                                }
                                if (thirds != null) {
                                    third = StringToListPoint(thirds);
                                }
                                lineStatus = POLYLINE_STATUS_FINISH;
                                reDrawPolyline(pageWhich, lineStatus);
                            }
                        }
                    }
                } else {
                    LogUtil.e("未获取到信息,不进行更新!");
                }
            }
        }, HttpUrls.getEfenceData(device_id), null, Task.TYPE_GET_STRING_NOCACHE, null));
    }

    public void initStore() {
        first = new ArrayList<>();
        second = new ArrayList<>();
        third = new ArrayList<>();
        //TODO 网络获取已有的围栏数据以及围栏各个点的数据
    }

    //设置电子围栏
    public void setEfence(String deviceId, List<LatLng> list, int position) {
        if (!BaseApplication.getInstances().isSDKConnected()) {
            Toast.makeText(getActivity(), "已与手表服务器断开连接", Toast.LENGTH_SHORT).show();
            return;
        }
        currentCommand = SET_EFENCE;
        mPosition = position;
        long devid = Long.parseLong(deviceId, 16);
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObjectSub = new JSONObject();
        JSONArray jsonObjectBeSub = new JSONArray();
        try {
            jsonObject.put("IMEI", deviceId);
            jsonObject.put("Category", "Efence");
            jsonObject.put("Action", "Add");
            jsonObject.put("Index", "" + position);
            jsonObjectSub.put("Num", "" + list.size());// 2017-04-08 说要String类型的数据
            for (LatLng temp : list) {
                jsonObjectBeSub.put(temp.longitude + "," + temp.latitude);
            }
            jsonObjectSub.put("Points", jsonObjectBeSub);
            jsonObject.put("Efence", jsonObjectSub);
            BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(devid, jsonObject.toString().getBytes(), jsonObject.toString().length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getLocation() {
        if (!BaseApplication.getInstances().isSDKConnected()) {
            BaseApplication.getInstances().toast(getActivity(), "已与手表服务器断开连接");
            return;
        }
        long devid = Long.parseLong(device_id, 16);
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObjectSub = new JSONObject();
        JSONObject jsonObjectBeSub = new JSONObject();
        try {
            //TODO 获取指定设备的定位信息
            currentCommand = GET_LOCATION;
            jsonObject = new JSONObject();
            jsonObject.put("IMEI", device_id);
            jsonObject.put("Action", "Get");
            jsonObject.put("Category", "Location");
            BaseApplication.getInstances().getNattyClient().ntyDataRouteClient(devid, jsonObject.toString().getBytes(), jsonObject.toString().length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private ImageButton ibtn_back;
    private TextView tv_title;
    private TextView menu_text;
    private ImageButton ibtn_menu;

    public void initTitleBar(View view) {
        ibtn_back = (ImageButton) view.findViewById(R.id.ibtn_back);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        menu_text = (TextView) view.findViewById(R.id.menu_text);
        ibtn_menu = (ImageButton) view.findViewById(R.id.ibtn_menu);

        ibtn_back.setVisibility(View.VISIBLE);
        tv_title.setVisibility(View.VISIBLE);
        menu_text.setVisibility(View.GONE);
        ibtn_menu.setVisibility(View.VISIBLE);

        tv_title.setText("电子围栏");
        tv_title.setOnClickListener(this);
        ibtn_back.setImageResource(R.drawable.back);//返回
        menu_text.setText("文字菜单");

        ibtn_back.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);

    }

    public void initMap(View view, Bundle savedInstanceState) {
        lineStatus = POLYLINE_STATUS_UNFINISH;
        mapView = (MapView) view.findViewById(R.id.map);
        long timeRecord = System.currentTimeMillis();
        mapView.onCreate(savedInstanceState);
        LogUtil.w("DigitaklFance mapView.onCreate time Use:" + (System.currentTimeMillis() - timeRecord) / 1000 + "    Second");
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
        aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
        aMap.setInfoWindowAdapter(this);
        aMap.setOnMapClickListener(this);

    }

    public void startMap() {
        locate();//拉自己的经纬度
        initPolylineOption();//初始化线条设置
        initLcationSearch();
        getAddress(new LatLonPoint(mLat, mLon));
        getRailNumber();
//        getRailRecordFromNet();
    }

    public void getRailRecordFromNet() {
        //TODO 根据pageWhich 判断将获取的数据保存到哪个数组中


        //TODO 在获取玩后，展示该电子围栏
        if (pageStatus == STATUS_EDIT) {
            getLocateData();
        }
    }

    DeviceRailEntity locaData;

    public void getLocateData() {
        switch (pageWhich) {
            case FIRST: {
                List<DeviceRailEntity> entityList = DeviceRailHandler.getAllValue(device_id);
                if (entityList != null && entityList.size() > 0) {
                    for (DeviceRailEntity temp : entityList) {
                        if (temp != null && TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST.equals(temp.getPosition())) {
                            locaData = temp;
                            first = StringToListPoint(locaData);
                            break;
                        }
                    }
                    if (aMap != null) {
                        aMap.clear();
                    }
                    reDrawPolyline(pageWhich, lineStatus);
                } else {
                    first = new ArrayList<>();
                }
                break;
            }
            case SECOND: {
                List<DeviceRailEntity> entityList = DeviceRailHandler.getAllValue(device_id);
                if (entityList != null && entityList.size() > 0) {
                    for (DeviceRailEntity temp : entityList) {
                        if (temp != null && TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND.equals(temp.getPosition())) {
                            locaData = temp;
                            second = StringToListPoint(locaData);
                            break;
                        }
                    }
                    if (aMap != null) {
                        aMap.clear();
                    }
                    reDrawPolyline(pageWhich, lineStatus);
                } else {
                    second = new ArrayList<>();
                }
                break;
            }
            case THIRD: {
                List<DeviceRailEntity> entityList = DeviceRailHandler.getAllValue(device_id);
                if (entityList != null && entityList.size() > 0) {
                    for (DeviceRailEntity temp : entityList) {
                        if (temp != null && TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD.equals(temp.getPosition())) {
                            locaData = temp;
                            third = StringToListPoint(locaData);
                            break;
                        }
                    }
                    if (aMap != null) {
                        aMap.clear();
                    }
                    reDrawPolyline(pageWhich, lineStatus);
                } else {
                    third = new ArrayList<>();
                }
                break;
            }
        }
    }

    public List<LatLng> StringToListPoint(DeviceRailEntity entity) {
        List<LatLng> list = new ArrayList<>();
        String path = entity.getPath();
        if (path != null && path.contains(";") && path.trim().length() > 7) {
            String[] splitData = path.split(";");
            for (String temp : splitData) {
                if (temp != null && temp.contains(",") && temp.split(",").length == 2) {
                    String[] tempSplit = temp.split(",");
                    LatLng latLng = new LatLng(Double.parseDouble(tempSplit[0]), Double.parseDouble(tempSplit[1]));
                    list.add(latLng);
                }
            }
            if (list.size() > 1) {
                return list;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void initPolylineOption() {
        mPolyoptions = new PolylineOptions();
        mPolyoptions.width(2f);
        mPolyoptions.color(Color.argb(255, 22, 169, 168));
    }

    public void initLcationSearch() {
        geocoderSearch = new GeocodeSearch(getActivity());
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                if (rCode == 1000) {
                    if (result != null && result.getRegeocodeAddress() != null
                            && result.getRegeocodeAddress().getFormatAddress() != null) {
                        String district = result.getRegeocodeAddress().getDistrict();
                        String province = result.getRegeocodeAddress().getProvince();
                        String city = result.getRegeocodeAddress().getCity();
                        String addressName = result.getRegeocodeAddress().getFormatAddress()
                        /*+ "附近"*/;
                        mAddress = addressName.replace(province, "")/*.replace(district, "").replace(city, "")*/;

                        moveCamera(new LatLng(mLat, mLon));
                        selfMarker(new LatLng(mLat, mLon), mAddress);
                        if (info_location != null) {
                            info_location.setText("地址:" + addressName.replace(province, ""));
                            info_location.setTag(addressName.replace(province, ""));
                        }
                        LogUtil.e("经纬度解析成功!");
                    } else {
                        LogUtil.e("对不起，没有搜索到相关数据！");
                    }
                } else {
                    BaseApplication.getInstances().showerror(getActivity(), rCode);
                    LogUtil.e("解析失败：  " + rCode);
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });
    }


    /**
     * 定位自己
     */
    public void locate() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.marker_other_highlight));// 设置小蓝点的图标***不设置则使用默认
//                .defaultMarker());// 设置小蓝点的图标***不设置则使用默认
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(0f);// 设置圆形的边框粗细

        aMap.getUiSettings().setCompassEnabled(false);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.moveCamera(CameraUpdateFactory.zoomTo(SOLID_ZOOM));
        aMap.setMyLocationStyle(myLocationStyle);

        aMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                mListener = onLocationChangedListener;
                if (mlocationClient == null) {
                    mlocationClient = new AMapLocationClient(getActivity().getApplicationContext());
                    mLocationOption = new AMapLocationClientOption();
                    //设置定位监听
                    mlocationClient.setLocationListener(new AMapLocationListener() {
                        @Override
                        public void onLocationChanged(AMapLocation aMapLocation) {
                            selfLatitude = aMapLocation.getLatitude();
                            selfLongitude = aMapLocation.getLongitude();
                            selfAdress = aMapLocation.getAddress();
                            LogUtil.e("自己的位置\nSelfLatitude:" + selfLatitude +
                                    "\nSelfLongitude:" + selfLongitude +
                                    "\nSelfAdress:" + selfAdress +
                                    "\nCurrentTime:" + sdf.format(new Date()));
                            final LatLng latLng = new LatLng(selfLatitude, selfLongitude);
//                            if((first!=null&&first.size()>0)||
//                                    (second!=null&&second.size()>0) ||
//                                    (third!=null&&third.size()>0)){
//
//                            }else{
//                                moveCamera(latLng);
//                            }
//                            moveCamera(new LatLng(selfLatitude,selfLongitude));//TODO 定位自己不需要移动焦点
//                            selfMarker(latLng,selfAdress);
                            stopLocation();
                        }
                    });
                    mLocationOption.setInterval(30000);
                    //设置为高精度定位模式
                    mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                    mLocationOption.setLocationCacheEnable(true);
                    //设置定位参数
                    mlocationClient.setLocationOption(mLocationOption);

                    mlocationClient.startLocation();
                }
            }

            @Override
            public void deactivate() {
                mListener = null;
                if (mlocationClient != null) {
                    mlocationClient.stopLocation();
                    mlocationClient.onDestroy();
                }
                mlocationClient = null;
            }
        });// 设置定位监听
        aMap.setMyLocationEnabled(true);
    }

    public void stopLocation() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
        }
    }

    public void selfMarker(LatLng pLng, String address) {
        if (pLng == null) {
            return;
        }
        if (marker != null && marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
        }
        if (pageStatus == STATUS_SHOW) {
            marker = aMap.addMarker(new MarkerOptions()
                    .position(pLng)
                    .title(address)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.crawl_bg_position))
                    .draggable(false));
            marker.showInfoWindow();
        } else if (pageStatus == STATUS_EDIT) {
            marker = aMap.addMarker(new MarkerOptions()
                    .position(pLng)
                    .title(address)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.crawl_ico_portrait))
                    .draggable(false));
            marker.hideInfoWindow();
        }
    }

    public void reDrawPolyline(int which, int polylineStatus) {


        if (mPolyoptions.getPoints() != null &&
                mPolyoptions.getPoints().size() > 0) {
            mPolyoptions.getPoints().clear();
        }
        switch (which) {
            case FIRST: {
                if (first == null || first.size() < 2) {

                } else {
                    if (first != null && first.size() > 0) {
                        mPolyoptions.addAll(first);
                    }
                }
                showPolyline(polylineStatus, first);
                break;
            }
            case SECOND: {
                if (second == null || second.size() < 2) {

                } else {
                    if (second != null && second.size() > 0) {
                        mPolyoptions.addAll(second);
                    }
                }
                showPolyline(polylineStatus, second);
                break;
            }
            case THIRD: {
                if (third == null || third.size() < 2) {

                } else {
                    if (third != null && third.size() > 0) {
                        mPolyoptions.addAll(third);
                    }
                }
                showPolyline(polylineStatus, third);
                break;
            }
            default: {
                if (first == null || first.size() < 2) {

                } else {
                    if (first != null && first.size() > 0) {
                        mPolyoptions.addAll(first);
                    }
                }
                showPolyline(polylineStatus, first);
                break;
            }
        }
        showPolyMarker(which);
        //*****
        final LatLng latLng = new LatLng(mLat, mLon);
        selfMarker(latLng);
    }

    public void selfMarker(LatLng pLng) {
        if (pLng == null) {
            return;
        }
        if (pageStatus == STATUS_SHOW) {
            Marker marker = aMap.addMarker(new MarkerOptions()
                    .position(pLng)
                    .title(selfAdress)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.crawl_bg_position))
                    .draggable(false));
            marker.showInfoWindow();
        } else if (pageStatus == STATUS_EDIT) {
            Marker marker = aMap.addMarker(new MarkerOptions()
                    .position(pLng)
                    .title("手机当前位置")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.crawl_ico_portrait))
                    .draggable(false));
            marker.hideInfoWindow();
        }
    }

    private Dialog confirmDialog;

    public void openConfirmDialog() {
        if (confirmDialog != null && confirmDialog.isShowing()) {
            confirmDialog.dismiss();
        } else {
            confirmDialog = new Dialog(getActivity(), R.style.dialog_loading);
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_draw_digital_rail_confirm, null);
            TextView confirm = (TextView) view.findViewById(R.id.confirm);
            TextView cancel = (TextView) view.findViewById(R.id.cancel);

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDialog.dismiss();

                    submitData();
                    /**
                     * 根据围栏数判断菜单栏显示的样式
                     */
//                    reloadRailData();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmDialog.dismiss();
//                    BaseApplication.getInstances().toast(DigitalFanceActivity.this,"取消操作");
                    switch (pageWhich) {
                        case FIRST:
                            first.clear();
                            break;
                        case SECOND:
                            second.clear();
                            break;
                        case THIRD:
                            third.clear();
                            break;
                    }
                    lineStatus = POLYLINE_STATUS_UNFINISH;
                    moveCamera(new LatLng(mLat, mLon));
                    selfMarker(new LatLng(mLat, mLon), mAddress);
                }
            });

            WindowManager.LayoutParams lp = confirmDialog.getWindow().getAttributes();
            lp.width = lp.WRAP_CONTENT;
            lp.height = lp.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;

            confirmDialog.setContentView(view, lp);

            confirmDialog.show();
        }
    }

    /**
     * 根据围栏数判断菜单栏显示的样式
     * <p>
     * 完成后刷新dialog样式
     */
    public void submitData() {
        switch (pageWhich) {
            case FIRST: {
                setEfence(device_id, first, FIRST);
                break;
            }
            case SECOND: {
                setEfence(device_id, second, SECOND);
                break;
            }
            case THIRD: {
                setEfence(device_id, third, THIRD);
                break;
            }
        }
    }

    public String ListPointToString_Net(List<LatLng> list) {
        StringBuilder stringBuilder = new StringBuilder();
        if (list != null && list.size() > 0) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                LatLng temp = list.get(i);
                if (i < size - 1) {
                    stringBuilder.append(temp.longitude + "|" + temp.latitude + ";");
                } else {
                    stringBuilder.append(temp.longitude + "|" + temp.latitude);
                }
            }
        }
        return stringBuilder.toString();
    }

    public static String ListPointToString(List<LatLng> list) {
        StringBuilder stringBuilder = new StringBuilder();
        if (list != null && list.size() > 0) {
            int size = list.size();
            for (int i = 0; i < size; i++) {
                LatLng temp = list.get(i);
                if (i < size - 1) {
                    stringBuilder.append(temp.latitude + "," + temp.longitude + ";");
                } else {
                    stringBuilder.append(temp.latitude + "," + temp.longitude);
                }
            }
        }
        return stringBuilder.toString();
    }

    private final int POLYLINE_STATUS_UNFINISH = 2;
    private final int POLYLINE_STATUS_FINISH = 1;

    public void showPolyline(int status, List<LatLng> list) {
        if (list != null && list.size() > 0) {
            rechageCameraFocus(list);
            if (status == POLYLINE_STATUS_FINISH) {
                lineStatus = POLYLINE_STATUS_FINISH;
                if (list.size() > 2) {
                    aMap.addPolygon(new PolygonOptions().addAll(list).fillColor(Color.argb(50, 198, 246, 246)).strokeColor(Color.argb(255, 22, 169, 168)).strokeWidth(2f));
                } else if (list.size() == 2) {
                    double dist = AMapUtils.calculateLineDistance(list.get(0), list.get(1));
                    aMap.addCircle(new CircleOptions().center(list.get(0))
                            .radius(dist).strokeColor(Color.argb(255, 22, 169, 168))
                            .fillColor(Color.argb(50, 198, 246, 246)).strokeWidth(2f));
                }
            } else {
//                aMap.addPolyline(mPolyoptions);
                if (list.size() > 2) {
                    aMap.addPolygon(new PolygonOptions().addAll(list).fillColor(Color.argb(50, 198, 246, 246)).strokeColor(Color.argb(255, 22, 169, 168)).strokeWidth(2f));
                } else if (list.size() == 2) {
                    double dist = AMapUtils.calculateLineDistance(list.get(0), list.get(1));
                    aMap.addCircle(new CircleOptions().center(list.get(0))
                            .radius(dist).strokeColor(Color.argb(255, 22, 169, 168))
                            .fillColor(Color.argb(50, 198, 246, 246)).strokeWidth(2f));
                }
            }
        }
    }

    public void rechageCameraFocus(List<LatLng> list) {
        final int size = list.size();
        double lat = 0;
        double lng = 0;
        for (LatLng temp : list) {
            lat += temp.latitude;
            lng += temp.longitude;
        }
        lat = lat / size;
        lng = lng / size;
        moveCamera(new LatLng(lat, lng));
    }

    protected void moveCamera(LatLng point) {
        if (point == null) {
            return;
        }
        LogUtil.e("移动焦点");
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, SOLID_ZOOM));
    }

    public void showPolyMarker(int which) {
        switch (which) {
            case FIRST: {
                showMarkerList(first);
                break;
            }
            case SECOND: {
                showMarkerList(second);
                break;
            }
            case THIRD: {
                showMarkerList(third);
                break;
            }
            default: {
                showMarkerList(first);
                break;
            }
        }
    }

    public void showMarkerList(List<LatLng> list) {
        if (list == null || list.size() < 1) {
            return;
        }
        final int size = list.size();
        for (int i = 0; i < size; i++) {
            final LatLng temp = list.get(i);
            if (i == 0) {
                aMap.addMarker(new MarkerOptions()
                        .position(temp)
                        .title("" + i)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.crawl_ico_click_small))
                        .draggable(false));
            } else {
                aMap.addMarker(new MarkerOptions()
                        .position(temp)
                        .title("" + i)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.crawl_ico_click_big))
                        .draggable(false));
            }
        }
    }


    private PopupWindow popupWindow;
    private List<DeviceRailEntity> digitalRailList = new ArrayList<>();

    public void getRailNumber() {
        int size = 0;
        digitalRailList.clear();
        digitalRailList = DeviceRailHandler.getAllValue(device_id);
        if (digitalRailList == null || digitalRailList.size() < 1) {
            menu_text.setVisibility(View.GONE);
            ibtn_menu.setVisibility(View.VISIBLE);
            ibtn_menu.setImageResource(R.drawable.crawl_ico_add_nor);
            ibtn_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //弹出提示dialog
                    showNoticeDialog();
                    //编辑 第一条数据
                    aMap.clear();
                    if (pageStatus == STATUS_SHOW) {
                        pageStatus = STATUS_EDIT;
                    }
                    pageWhich = FIRST;
                    if (first != null) {//避免逻辑与显示上的错误
                        first.clear();
                    }//去除原来的所有点
                    moveCamera(new LatLng(mLat, mLon));
                    selfMarker(new LatLng(mLat, mLon), mAddress);
                }
            });
        } else {
            menu_text.setVisibility(View.GONE);
            ibtn_menu.setVisibility(View.VISIBLE);
            ibtn_menu.setImageResource(R.drawable.crawl_ico_pull_nor);
            ibtn_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDigitalRailPopWindow();
                }
            });
        }
    }

    public void showDigitalRailPopWindow() {
        /**
         * 区分顺序
         */
        if (digitalRailList.size() > 0) {
            DeviceRailEntity first = null;
            DeviceRailEntity second = null;
            DeviceRailEntity third = null;
            for (DeviceRailEntity temp : digitalRailList) {
                if (temp.getPosition() != null && TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST.equals(temp.getPosition())) {
                    first = temp;
                } else if (temp.getPosition() != null && TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND.equals(temp.getPosition())) {
                    second = temp;
                } else if (temp.getPosition() != null && TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD.equals(temp.getPosition())) {
                    third = temp;
                }
            }
            if (first != null) {
                this.first = StringToListPoint(first);
            }
            if (second != null) {
                this.second = StringToListPoint(second);
            }
            if (third != null) {
                this.third = StringToListPoint(third);
            }

            View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_watch_child, null);
            RelativeLayout root = (RelativeLayout) view.findViewById(R.id.parent);
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (popupWindow != null) {
                        popupWindow.dismiss();
                    }
                }
            });
            LinearLayout parent = (LinearLayout) view.findViewById(R.id.real_container);
            if (first != null) {
                View subItem1 = LayoutInflater.from(getActivity()).inflate(R.layout.item_watch_digitalrail, null);
                final TextView subItemName1 = (TextView) subItem1.findViewById(R.id.name);
                ImageView subItemDelete1 = (ImageView) subItem1.findViewById(R.id.delete);
                View div1 = subItem1.findViewById(R.id.div);
                subItemName1.setText("围栏 1");
                if (pageWhich == FIRST) {
                    subItemName1.setTextColor(getResources().getColor(R.color.color_title_green));
                }
                subItemDelete1.setVisibility(View.VISIBLE);
                subItemDelete1.setImageResource(R.drawable.crawl_ico_delete);
                div1.setVisibility(View.VISIBLE);
                subItemName1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        subItemName1.setTextColor(getResources().getColor(R.color.color_title_green));
                        //切换成当前围栏的编辑模式

                        aMap.clear();//清除图标----展示围栏

                        pageStatus = STATUS_EDIT;
                        pageWhich = FIRST;
                        lineStatus = POLYLINE_STATUS_FINISH;
//                        showPolyline(POLYLINE_STATUS_FINISH,DigitalFanceActivity.this.first);
                        reDrawPolyline(pageWhich, lineStatus);
                    }
                });
                subItemDelete1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //删除该围栏
                        popupWindow.dismiss();
                        aMap.clear();//清除图标----展示地址
                        pageWhich = FIRST;
//                        DeviceRailHandler.remove(device_id, TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST, "");
//                        getRailNumber();
                        // TODO 展示模式
                        pageStatus = STATUS_SHOW;
                        moveCamera(new LatLng(mLat, mLon));
                        selfMarker(new LatLng(mLat, mLon), mAddress);

                        deleteEfence("1");
                    }
                });
                parent.addView(subItem1);
            } else {
                View subItem2 = LayoutInflater.from(getActivity()).inflate(R.layout.item_watch_digitalrail, null);
                TextView subItemName2 = (TextView) subItem2.findViewById(R.id.name);
                ImageView subItemDelete2 = (ImageView) subItem2.findViewById(R.id.delete);
                View div2 = subItem2.findViewById(R.id.div);
                subItemName2.setText("添   加");
                if (pageWhich == FIRST) {
                    subItemName2.setTextColor(getResources().getColor(R.color.color_title_green));
                }
                subItemDelete2.setVisibility(View.VISIBLE);
                subItemDelete2.setImageResource(R.drawable.crawl_ico_add);
                div2.setVisibility(View.VISIBLE);
                subItem2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        //切换成当前围栏的编辑模式
                        //展示当前的编辑模式
                        aMap.clear();//清除图标----展示地址
                        pageStatus = STATUS_EDIT;
                        pageWhich = FIRST;
                        lineStatus = POLYLINE_STATUS_UNFINISH;
                        DigitalFanceFragment_gpstoAmap.this.first.clear();
                        moveCamera(new LatLng(mLat, mLon));
                        selfMarker(new LatLng(mLat, mLon), mAddress);

                    }
                });
                parent.addView(subItem2);
            }

            if (second != null) {
                View subItem1 = LayoutInflater.from(getActivity()).inflate(R.layout.item_watch_digitalrail, null);
                final TextView subItemName1 = (TextView) subItem1.findViewById(R.id.name);
                ImageView subItemDelete1 = (ImageView) subItem1.findViewById(R.id.delete);
                View div1 = subItem1.findViewById(R.id.div);
                subItemName1.setText("围栏 2");
                if (pageWhich == SECOND) {
                    subItemName1.setTextColor(getResources().getColor(R.color.color_title_green));
                }
                subItemDelete1.setVisibility(View.VISIBLE);
                subItemDelete1.setImageResource(R.drawable.crawl_ico_delete);
                div1.setVisibility(View.VISIBLE);
                subItemName1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        subItemName1.setTextColor(getResources().getColor(R.color.color_title_green));
                        //切换成当前围栏的编辑模式
                        //TODO 展示围栏，并可以进行编辑
                        aMap.clear();//清除图标---
                        pageStatus = STATUS_EDIT;
                        pageWhich = SECOND;
                        lineStatus = POLYLINE_STATUS_FINISH;
//                        showPolyline(POLYLINE_STATUS_FINISH,DigitalFanceActivity.this.second);
                        reDrawPolyline(pageWhich, lineStatus);
                    }
                });
                subItemDelete1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //删除该围栏
                        popupWindow.dismiss();
                        pageWhich = SECOND;
                        LogUtil.e("删除围栏2");
//                        DeviceRailHandler.remove(device_id, TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND, "");
                        aMap.clear();//清除图标---
                        pageStatus = STATUS_SHOW;
//                        getRailNumber();

                        moveCamera(new LatLng(mLat, mLon));
                        selfMarker(new LatLng(mLat, mLon), mAddress);

                        deleteEfence("2");
                    }
                });
                parent.addView(subItem1);
            } else {
                View subItem2 = LayoutInflater.from(getActivity()).inflate(R.layout.item_watch_digitalrail, null);
                TextView subItemName2 = (TextView) subItem2.findViewById(R.id.name);
                ImageView subItemDelete2 = (ImageView) subItem2.findViewById(R.id.delete);
                View div2 = subItem2.findViewById(R.id.div);
                subItemName2.setText("添   加");
                if (pageWhich == SECOND) {
                    subItemName2.setTextColor(getResources().getColor(R.color.color_title_green));
                }
                subItemDelete2.setVisibility(View.VISIBLE);
                subItemDelete2.setImageResource(R.drawable.crawl_ico_add);
                div2.setVisibility(View.VISIBLE);
                subItem2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        showNoticeDialog();
                        //切换成当前围栏的编辑模式
                        aMap.clear();//清除图标---
                        pageStatus = STATUS_EDIT;
                        pageWhich = SECOND;
                        lineStatus = POLYLINE_STATUS_UNFINISH;
                        DigitalFanceFragment_gpstoAmap.this.second.clear();
                        moveCamera(new LatLng(mLat, mLon));
                        selfMarker(new LatLng(mLat, mLon), mAddress);
                    }
                });
                parent.addView(subItem2);
            }

            if (third != null) {
                View subItem1 = LayoutInflater.from(getActivity()).inflate(R.layout.item_watch_digitalrail, null);
                final TextView subItemName1 = (TextView) subItem1.findViewById(R.id.name);
                ImageView subItemDelete1 = (ImageView) subItem1.findViewById(R.id.delete);
                View div1 = subItem1.findViewById(R.id.div);
                subItemName1.setText("围栏 3");
                if (pageWhich == THIRD) {
                    subItemName1.setTextColor(getResources().getColor(R.color.color_title_green));
                }
                subItemDelete1.setVisibility(View.VISIBLE);
                subItemDelete1.setImageResource(R.drawable.crawl_ico_delete);
                div1.setVisibility(View.INVISIBLE);
                subItemName1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        //切换成当前围栏的编辑模式
                        aMap.clear();//清除图标---
                        pageStatus = STATUS_EDIT;
                        pageWhich = THIRD;
                        lineStatus = POLYLINE_STATUS_FINISH;
//                        showPolyline(POLYLINE_STATUS_FINISH,DigitalFanceActivity.this.second);
                        reDrawPolyline(pageWhich, lineStatus);
                    }
                });
                subItemDelete1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //删除该围栏
                        popupWindow.dismiss();
                        LogUtil.e("删除围栏3");
//                        DeviceRailHandler.remove(device_id, TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD, "");
                        aMap.clear();//清除图标---
                        pageWhich = THIRD;
                        pageStatus = STATUS_SHOW;
//                        getRailNumber();

                        moveCamera(new LatLng(mLat, mLon));
                        selfMarker(new LatLng(mLat, mLon), mAddress);

                        deleteEfence("3");
                    }
                });
                parent.addView(subItem1);
            } else {
                View subItem2 = LayoutInflater.from(getActivity()).inflate(R.layout.item_watch_digitalrail, null);
                TextView subItemName2 = (TextView) subItem2.findViewById(R.id.name);
                ImageView subItemDelete2 = (ImageView) subItem2.findViewById(R.id.delete);
                View div2 = subItem2.findViewById(R.id.div);
                subItemName2.setText("添   加");
                if (pageWhich == THIRD) {
                    subItemName2.setTextColor(getResources().getColor(R.color.color_title_green));
                }
                subItemDelete2.setVisibility(View.VISIBLE);
                subItemDelete2.setImageResource(R.drawable.crawl_ico_add);
                div2.setVisibility(View.INVISIBLE);
                subItem2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        showNoticeDialog();
                        //切换成当前围栏的编辑模式
//                        DeviceRailHandler.remove(device_id, TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD, "");
                        aMap.clear();//清除图标---
                        pageStatus = STATUS_EDIT;
                        pageWhich = THIRD;
                        lineStatus = POLYLINE_STATUS_UNFINISH;
                        DigitalFanceFragment_gpstoAmap.this.third.clear();
                        moveCamera(new LatLng(mLat, mLon));
                        selfMarker(new LatLng(mLat, mLon), mAddress);
                    }
                });
                parent.addView(subItem2);
            }
            if (popupWindow == null) {
                popupWindow = new PopupWindow(view,
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            } else {
                popupWindow.setContentView(view);
            }
            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.selecter_common_dialog_bg_transparent));
            popupWindow.showAsDropDown(menu_text, -100, 100);//设置在某个指定View的下方
        }
    }

    public void closePopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    private Dialog dialog;

    public void showDialog() {
        dialog = QuanjiakanDialog.getInstance().getDialog(getActivity());
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void deleteEfence(final String efenceID) {
        if (!BaseApplication.getInstances().isSDKConnected()) {
            Toast.makeText(getActivity(), "已与手表服务器断开连接", Toast.LENGTH_SHORT).show();
            return;
        }
        //TODO 根据保存是返回的电子围栏ID 进行另外的方法调用删除该围栏  @efenceID
        currentCommand = DEL_EFENCE;
        long devid = Long.parseLong(device_id, 16);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("IMEI", device_id);
            jsonObject.put("Category", "Efence");
            jsonObject.put("Action", "Delete");
            jsonObject.put("Index", efenceID);
            BaseApplication.getInstances().getNattyClient().ntyCommonReqClient(devid, jsonObject.toString().getBytes(), jsonObject.toString().length());
            showDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initView() {
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        MobclickAgent.onResume(getActivity());
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(getActivity());

        //**********************
        LogUtil.e("Fragment onResume ");
        LogUtil.e("Fragment onResume mLat:" + mLat);
        LogUtil.e("Fragment onResume mLon:" + mLon);
        LogUtil.e("Fragment onResume device_id:" + device_id);
        LogUtil.e("Fragment onResume pageStatus:" + pageStatus);
        LogUtil.e("Fragment onResume pageWhich:" + pageWhich);
        LogUtil.e("Fragment onResume phoneNumberString:" + phoneNumberString);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        MobclickAgent.onPause(getActivity());
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
        LogUtil.e("Fragment onPause ");
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        LogUtil.e("Fragment onStop ");
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        LogUtil.e("Fragment onStart ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        LogUtil.e("Fragment onDestroy ");
    }

    public void newLocation() {
        //{"Results":{"Type":"WIFI","Radius":"35","Location":"113.2415614,23.1322708","Category":"WIFI","IMEI":"355637050066828"}}
        /**
         {
         "Results": {
         "Type": "WIFI",
         "Radius": "35",
         "Location": "113.2415614,23.1322708",
         "Category": "WIFI",
         "IMEI": "355637050066828"
         }
         }
         */
        try {
            JSONObject parent = new JSONObject();
            JSONObject child = new JSONObject();
            child.put("Type", "WIFI");
            child.put("Radius", "35");
            child.put("Location", "113.1415614,23.2322708");
            child.put("Category", "WIFI");
            child.put("IMEI", "355637052203973");

            parent.put("Results", child);

            NattyProtocolFilter.ntyProtocolLocationResult(0, parent.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                getActivity().finish();
                break;
            case R.id.tv_title:
//                newLocation();
                break;
        }
    }

    public void getAddressByLatlng(final LatLng latlng) {
        LatLonPoint latLonPoint = new LatLonPoint(latlng.latitude, latlng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 100,
                GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    private TextView info_location;
    private TextView phone;
    private ImageView call_phone;
    private LinearLayout guide_line;//导航
    private LinearLayout phone_line;

    @Override
    public View getInfoWindow(final Marker marker) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_watch_child_infowindow, null);
        info_location = (TextView) view.findViewById(R.id.info_location);
        info_location.setTag(null);
        if (mAddress == null) {
            getAddressByLatlng(marker.getPosition());
            info_location.setText("");
        } else {
            info_location.setText(mAddress);
        }
        phone_line = (LinearLayout) view.findViewById(R.id.phone_line);
        phone = (TextView) view.findViewById(R.id.phone);
        call_phone = (ImageView) view.findViewById(R.id.call_phone);
        guide_line = (LinearLayout) view.findViewById(R.id.guide_line);
        //TODO 临时对位置进行查询
        if (phoneNumberString != null && phoneNumberString.length() > 0 && CheckUtil.isPhoneNumber(phoneNumberString)) {
            phone.setText("电话:" + phoneNumberString);
            phone_line.setVisibility(View.GONE);
        } else {
            phone_line.setVisibility(View.GONE);
        }
        call_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone.getText().toString().substring(phone.getText().toString().indexOf(":") + 1)));
                startActivity(intent);
            }
        });
        guide_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //导航
                openGudieDialog(marker.getPosition());
            }
        });
        return view;
    }

    @Override
    public View getInfoContents(final Marker marker) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_watch_child_infowindow, null);
        info_location = (TextView) view.findViewById(R.id.info_location);
        info_location.setTag(null);
        if (mAddress == null) {
            getAddressByLatlng(marker.getPosition());
            info_location.setText("");
        } else {
            info_location.setText(mAddress);
        }
        phone_line = (LinearLayout) view.findViewById(R.id.phone_line);
        phone = (TextView) view.findViewById(R.id.phone);
        call_phone = (ImageView) view.findViewById(R.id.call_phone);
        guide_line = (LinearLayout) view.findViewById(R.id.guide_line);
        //TODO 临时对位置进行查询
        if (phoneNumberString != null && phoneNumberString.length() > 0 && CheckUtil.isPhoneNumber(phoneNumberString)) {
            phone.setText("电话:" + phoneNumberString);
            phone_line.setVisibility(View.GONE);
        } else {
            phone_line.setVisibility(View.GONE);
        }
        call_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone.getText().toString().substring(phone.getText().toString().indexOf(":") + 1)));
                startActivity(intent);
            }
        });
        guide_line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //导航
                openGudieDialog(marker.getPosition());
            }
        });
        return view;
    }

    public void openGudieDialog(final LatLng latLng) {
        final Dialog selectNaviDialog = QuanjiakanDialog.getInstance().getCardDialog(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_select_navimap, null);
        //判断是否安装高德地图,处理点击事件
        if (NaviMapUtil.isAvilible(getActivity(), "com.autonavi.minimap")) {
            TextView gaodeNavi = (TextView) view.findViewById(R.id.tv_gaodenavi);
            gaodeNavi.setVisibility(View.VISIBLE);
            View view1 = view.findViewById(R.id.line1);
            view1.setVisibility(View.VISIBLE);
            gaodeNavi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selfLatitude != -1000 && selfLongitude != -1000) {
                        NaviMapUtil.GotoGaoDeNaviMap(getActivity(), "全家康用户端", selfLatitude + "", selfLongitude + "", selfAdress, latLng.latitude + "", latLng.longitude + "",
                                (info_location.getTag() != null ? info_location.getTag().toString() : ""), "1", "0", "2");
                    } else {
                        BaseApplication.getInstances().toast(getActivity(), "暂未获取到自己的定位信息,请稍后重试!");
                    }
                    selectNaviDialog.dismiss();
                }

            });
        }

        //判断是否安装百度地图,并处理点击事件
        if (NaviMapUtil.isAvilible(getActivity(), "com.baidu.BaiduMap")) {
            TextView baiduNavi = (TextView) view.findViewById(R.id.tv_baidunavi);
            baiduNavi.setVisibility(View.VISIBLE);
            View view2 = view.findViewById(R.id.line2);
            view2.setVisibility(View.VISIBLE);
            //先将火星坐标转换为百度坐标

            baiduNavi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selfLatitude != -1000 && selfLongitude != -1000) {
                        Map<String, String> selfPoint = MapUtil.bd_encrypt(selfLatitude, selfLongitude);
                        Map<String, String> patitentPoint = MapUtil.bd_encrypt(latLng.latitude, latLng.longitude);
                        final String selfBdlat = selfPoint.get("mgLat");
                        final String selfBdlon = selfPoint.get("mgLon");
                        final String patitentBdLat = patitentPoint.get("mgLat");
                        final String patitentBdLon = patitentPoint.get("mgLon");
                        NaviMapUtil.GotoBaiDuNaviMap(getActivity(), selfBdlat + "," + selfBdlon, patitentBdLat + "," + patitentBdLon, "driving", null, null, null, null, null, "thirdapp.navi." + "巨硅科技" + R.string.app_name);
                    } else {
                        BaseApplication.getInstances().toast(getActivity(), "暂未获取到自己的定位信息,请稍后重试!");
                    }
                    selectNaviDialog.dismiss();
                }
            });

        }

        //没有百度和高德地图
        if (!(NaviMapUtil.isAvilible(getActivity(), "com.baidu.BaiduMap") || NaviMapUtil.isAvilible(getActivity(), "com.autonavi.minimap"))) {
            TextView no_map = (TextView) view.findViewById(R.id.tv_no_map);
            no_map.setVisibility(View.VISIBLE);
            View view3 = view.findViewById(R.id.line3);
            view3.setVisibility(View.VISIBLE);
            no_map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectNaviDialog.dismiss();
                }
            });
        }

        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNaviDialog.dismiss();
            }
        });

        WindowManager.LayoutParams params = selectNaviDialog.getWindow().getAttributes();
        params.width = UnitUtil.dp_To_px(getActivity(), 300);
        params.height = params.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;

        selectNaviDialog.setContentView(view, params);
        selectNaviDialog.setCanceledOnTouchOutside(false);
        selectNaviDialog.show();

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    private final int SIZE_LIMIT = 6;
    private Marker marker;

    @Override
    public void onMapClick(LatLng latLng) {
        if (pageStatus == STATUS_EDIT) {
            switch (pageWhich) {
                case WHICH_DEFAULT:
                    break;
                case FIRST:
                    if (first == null) {
                        first = new ArrayList<>();
                    }
                    //**控制展示页面的点数量
                    if (first.size() < SIZE_LIMIT) {
                        //***
                        if (first.size() >= 3 && isValidPoint(first, latLng)) {
                            first.add(latLng);
                        } else if (first.size() < 3) {
                            first.add(latLng);
                        } else {

                        }
                    } else {
                        BaseApplication.getInstances().toastLong(getActivity(), "超出可指定点的数量!");
                    }
                    //**展示所有点
                    //**连接所有线
                    aMap.clear();//清除图标---
                    reDrawPolyline(FIRST, lineStatus);
                    break;
                case SECOND:
                    if (second == null) {
                        second = new ArrayList<>();
                    }
                    if (second.size() < SIZE_LIMIT) {
                        if (second.size() < 3) {
                            second.add(latLng);
                        } else if (second.size() >= 3 && isValidPoint(second, latLng)) {
                            second.add(latLng);
                        } else {

                        }
                    } else {
                        BaseApplication.getInstances().toastLong(getActivity(), "超出可指定点的数量!");
                    }
                    //**展示所有点
                    //**连接所有线
                    aMap.clear();//清除图标---
                    reDrawPolyline(SECOND, lineStatus);
                    break;
                case THIRD:
                    if (third == null) {
                        third = new ArrayList<>();
                    }
                    if (third.size() < SIZE_LIMIT) {
                        if (third.size() < 3) {
                            third.add(latLng);
                        } else if (third.size() >= 3 && isValidPoint(third, latLng)) {
                            third.add(latLng);
                        } else {

                        }
                    } else {
                        BaseApplication.getInstances().toastLong(getActivity(), "超出可指定点的数量!");
                    }
                    //**展示所有点
                    //**连接所有线
                    aMap.clear();//清除图标---
                    reDrawPolyline(THIRD, lineStatus);
                    break;
                default:
                    break;
            }
        } else {
        }
    }

    public boolean isValidPoint(List<LatLng> list, LatLng point) {
        //判断当前点与已有的点连线是否存在交叉
        boolean bool = false;
        if (list.size() == 3) {//4个点
            bool = judgeIntersect(list.get(0), list.get(1), list.get(2), point);
            if (bool) {//存在交叉了
                return !bool;
            } else {
                bool = judgeIntersect(list.get(1), list.get(2), point, list.get(0));
            }
        } else if (list.size() == 4) {//5个点
            bool = judgeIntersect(list.get(0), list.get(1), list.get(2), list.get(3));
            if (bool) {
                return !bool;
            }
            bool = judgeIntersect(list.get(0), list.get(1), list.get(3), point);
            if (bool) {
                return !bool;
            }
            bool = judgeIntersect(list.get(1), list.get(2), list.get(3), point);
            if (bool) {
                return !bool;
            }
            bool = judgeIntersect(list.get(1), list.get(2), list.get(3), list.get(0));
            if (bool) {
                return !bool;
            }
            bool = judgeIntersect(list.get(2), list.get(3), point, list.get(0));
        } else if (list.size() == 5) {//6个点
            bool = judgeIntersect(list.get(0), list.get(1), list.get(2), list.get(3));
            if (bool) {
                return !bool;
            }
            bool = judgeIntersect(list.get(0), list.get(1), list.get(3), list.get(4));
            if (bool) {
                return !bool;
            }
            bool = judgeIntersect(list.get(0), list.get(1), list.get(4), point);
            if (bool) {
                return !bool;
            }
            //******************
            bool = judgeIntersect(list.get(1), list.get(2), list.get(3), list.get(4));
            if (bool) {
                return !bool;
            }
            bool = judgeIntersect(list.get(1), list.get(2), list.get(4), point);
            if (bool) {
                return !bool;
            }
            bool = judgeIntersect(list.get(1), list.get(2), point, list.get(0));
            if (bool) {
                return !bool;
            }
            //******************
            bool = judgeIntersect(list.get(2), list.get(3), list.get(4), point);
            if (bool) {
                return !bool;
            }
            bool = judgeIntersect(list.get(2), list.get(3), point, list.get(0));
            if (bool) {
                return !bool;
            }
            bool = judgeIntersect(list.get(3), list.get(4), point, list.get(0));
        }
        return !bool;
    }

    public boolean judgeIntersect(LatLng point1, LatLng point2, LatLng point3,
                                  LatLng point4) {
        // Line 1 ****** point1 point2
        // Line 2 ****** point3 point4
        // situation 1
        if (point1.latitude == point2.latitude) {//线1垂直坐标系
            if (point3.latitude == point4.latitude) {// 两线平行
                if (point1.latitude == point3.latitude) {// 在同一直线上
                    if (point1.longitude >= point2.longitude) {// 点1 在上方
                        if ((point3.longitude <= point1.longitude && point3.longitude >= point2.longitude)
                                || (point4.longitude <= point1.longitude && point4.longitude >= point2.longitude)) {
                            // 点3 或 点4 在 线段1 上
                            return true;
                        } else {
                            //不在线上
                            return false;
                        }
                    } else {
                        if ((point3.longitude >= point1.longitude && point3.longitude <= point2.longitude)
                                || (point4.longitude >= point1.longitude && point4.longitude <= point2.longitude)) {
                            // 点3 或 点4 在 线段1 上
                            return true;
                        } else {
                            //不在线上
                            return false;
                        }
                    }
                } else {// 两线平行，但不是重合的直线
                    return false;
                }
            } else {//線1 垂直  线2不垂直
                List<LatLng> list = pointOnLine(point1, point2, point3, point4);
                if (list != null && list.size() > 0) {
                    if (list.size() > 1) {
                        return true;
                    } else {
                        LatLng temp = list.get(0);
                        if (point1.longitude > point2.longitude) {
                            if (temp.longitude <= point1.longitude && temp.longitude >= point2.longitude &&
                                    ((temp.longitude <= point3.longitude && temp.longitude >= point4.longitude) ||
                                            (temp.longitude >= point3.longitude && temp.longitude <= point4.longitude))) {
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            if (temp.longitude >= point1.longitude && temp.longitude <= point2.longitude &&
                                    ((temp.longitude <= point3.longitude && temp.longitude >= point4.longitude) ||
                                            (temp.longitude >= point3.longitude && temp.longitude <= point4.longitude))) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }
                } else {
                    return false;
                }
            }
        } else {
            if (point3.latitude == point4.latitude) {//線1 不垂直，線2垂直
                List<LatLng> list = pointOnLine(point1, point2, point3, point4);
                if (list != null && list.size() > 0) {
                    if (list.size() > 1) {
                        return true;
                    } else {
                        LatLng temp = list.get(0);
                        if (point1.longitude > point2.longitude) {
                            if (temp.longitude <= point1.longitude && temp.longitude >= point2.longitude &&
                                    ((temp.longitude <= point3.longitude && temp.longitude >= point4.longitude) ||
                                            (temp.longitude >= point3.longitude && temp.longitude <= point4.longitude))) {
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            if (temp.longitude >= point1.longitude && temp.longitude <= point2.longitude &&
                                    ((temp.longitude <= point3.longitude && temp.longitude >= point4.longitude) ||
                                            (temp.longitude >= point3.longitude && temp.longitude <= point4.longitude))) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }
                } else {
                    return false;
                }
            } else {
                List<LatLng> list = pointOnLine(point1, point2, point3, point4);
                if (list != null && list.size() > 0) {
                    if (list.size() > 1) {
                        return true;
                    } else {
                        LatLng temp = list.get(0);
                        if (point1.latitude > point2.latitude) {
                            if (temp.latitude <= point1.latitude && temp.latitude >= point2.latitude &&
                                    ((temp.latitude <= point3.latitude && temp.latitude >= point4.latitude) ||
                                            (temp.latitude >= point3.latitude && temp.latitude <= point4.latitude))) {
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            if (temp.latitude >= point1.latitude && temp.latitude <= point2.latitude &&
                                    ((temp.latitude <= point3.latitude && temp.latitude >= point4.latitude) ||
                                            (temp.latitude >= point3.latitude && temp.latitude <= point4.latitude))) {
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }
                } else {
                    return false;
                }
            }
        }
    }

    public List<LatLng> pointOnLine(LatLng line1Point1, LatLng line1Point2, LatLng line2Point1, LatLng line2Point2) {
        List<LatLng> list = new ArrayList<LatLng>();
        if (line1Point1.latitude == line1Point2.latitude) {
            if (line2Point1.latitude == line2Point2.latitude) {//line1 line2 都垂直
                if (line1Point1.latitude == line2Point1.latitude) {//直线重合
                    if (line1Point1.longitude > line1Point2.longitude) {//线1 点1 在点2 上面
                        if ((line2Point1.longitude < line1Point1.longitude && line2Point1.longitude > line1Point2.longitude) ||
                                (line2Point2.longitude < line1Point1.longitude && line2Point2.longitude > line1Point2.longitude)) {
                            //多点重合
                            list.clear();
                            list.add(new LatLng(0, 0));
                            list.add(new LatLng(0, 0));
                            return list;
                        } else {
                            if (line2Point2.longitude == line1Point2.longitude) {
                                list.clear();
                                list.add(line1Point2);
                                return list;
                            } else if (line2Point2.longitude == line1Point1.longitude) {
                                list.clear();
                                list.add(line1Point1);
                                return list;
                            } else if (line2Point1.longitude == line1Point2.longitude) {
                                list.clear();
                                list.add(line1Point2);
                                return list;
                            } else if (line2Point1.longitude == line1Point1.longitude) {
                                list.clear();
                                list.add(line1Point1);
                                return list;
                            } else {
                                return null;
                            }
                        }
                    } else {//线1 点1 在点2 下面
                        if ((line2Point1.longitude > line1Point1.longitude && line2Point1.longitude < line1Point2.longitude) ||
                                (line2Point2.longitude > line1Point1.longitude && line2Point2.longitude < line1Point2.longitude)) {
                            //多点重合
                            list.clear();
                            list.add(new LatLng(0, 0));
                            list.add(new LatLng(0, 0));
                            return list;
                        } else {
                            if (line2Point2.longitude == line1Point2.longitude) {
                                list.clear();
                                list.add(line1Point2);
                                return list;
                            } else if (line2Point2.longitude == line1Point1.longitude) {
                                list.clear();
                                list.add(line1Point1);
                                return list;
                            } else if (line2Point1.longitude == line1Point2.longitude) {
                                list.clear();
                                list.add(line1Point2);
                                return list;
                            } else if (line2Point1.longitude == line1Point1.longitude) {
                                list.clear();
                                list.add(line1Point1);
                                return list;
                            } else {
                                return null;
                            }
                        }
                    }
                } else {//直线平行 不重合
                    return null;
                }
            } else {//line1 垂直，line2 不垂直
                double latitude = line1Point1.latitude;
                double longitude =
                        (line2Point1.longitude - line2Point2.longitude) /
                                (line2Point1.latitude - line2Point2.latitude) * line1Point1.latitude +
                                countLineValue(line2Point1, line2Point2);
                LatLng temp = new LatLng(latitude, longitude);
                list.add(temp);
                return list;
            }
        } else {
            if (line2Point1.latitude == line2Point2.latitude) {//line1 不垂直，line2 垂直
                double latitude = line2Point1.latitude;
                double longitude =
                        (line1Point1.longitude - line1Point2.longitude) /
                                (line1Point1.latitude - line1Point2.latitude) * line2Point1.latitude +
                                countLineValue(line1Point1, line1Point2);
                LatLng temp = new LatLng(latitude, longitude);
                list.add(temp);
                return list;
            } else {//line1  不垂直，line2 不垂直
                if (countLineDelta(line1Point1, line1Point2) == countLineDelta(line2Point1, line2Point2)) {
                    if (countLineValue(line1Point1, line1Point2) == countLineValue(line2Point1, line2Point2)) {
                        if (line1Point1.longitude > line1Point2.longitude) {//线1 点1 在点2 上面
                            if ((line2Point1.longitude < line1Point1.longitude && line2Point1.longitude > line1Point2.longitude) ||
                                    (line2Point2.longitude < line1Point1.longitude && line2Point2.longitude > line1Point2.longitude)) {
                                //多点重合
                                list.clear();
                                list.add(new LatLng(0, 0));
                                list.add(new LatLng(0, 0));
                                return list;
                            } else {
                                if (line2Point2.longitude == line1Point2.longitude) {
                                    list.clear();
                                    list.add(line1Point2);
                                    return list;
                                } else if (line2Point2.longitude == line1Point1.longitude) {
                                    list.clear();
                                    list.add(line1Point1);
                                    return list;
                                } else if (line2Point1.longitude == line1Point2.longitude) {
                                    list.clear();
                                    list.add(line1Point2);
                                    return list;
                                } else if (line2Point1.longitude == line1Point1.longitude) {
                                    list.clear();
                                    list.add(line1Point1);
                                    return list;
                                } else {
                                    return null;
                                }
                            }
                        } else {
                            if ((line2Point1.longitude > line1Point1.longitude && line2Point1.longitude < line1Point2.longitude) ||
                                    (line2Point2.longitude > line1Point1.longitude && line2Point2.longitude < line1Point2.longitude)) {
                                //多点重合
                                list.clear();
                                list.add(new LatLng(0, 0));
                                list.add(new LatLng(0, 0));
                                return list;
                            } else {
                                if (line2Point2.longitude == line1Point2.longitude) {
                                    list.clear();
                                    list.add(line1Point2);
                                    return list;
                                } else if (line2Point2.longitude == line1Point1.longitude) {
                                    list.clear();
                                    list.add(line1Point1);
                                    return list;
                                } else if (line2Point1.longitude == line1Point2.longitude) {
                                    list.clear();
                                    list.add(line1Point2);
                                    return list;
                                } else if (line2Point1.longitude == line1Point1.longitude) {
                                    list.clear();
                                    list.add(line1Point1);
                                    return list;
                                } else {
                                    return null;
                                }
                            }
                        }
                    } else {//平行，不重合
                        return null;
                    }
                } else {//两线相交
                    list.clear();
                    double latitude = (countLineValue(line2Point1, line2Point2) - countLineValue(line1Point1, line1Point2)) /
                            ((line1Point1.longitude - line1Point2.longitude) / (line1Point1.latitude - line1Point2.latitude) -
                                    (line2Point1.longitude - line2Point2.longitude) / (line2Point1.latitude - line2Point2.latitude));
                    double longitude = (line2Point1.longitude - line2Point2.longitude) / (line2Point1.latitude - line2Point2.latitude) * latitude + countLineValue(line2Point1, line2Point2);
                    LatLng temp = new LatLng(latitude, longitude);
                    list.add(temp);
                }
            }
        }
        return list;
    }

    /**
     * y = delta*x+value
     *
     * @return delta
     */
    public Double countLineDelta(LatLng point1, LatLng point2) {
        if (point1.latitude == point2.latitude) {
            return null;
        } else {
            return (point1.longitude - point2.longitude) / (point1.latitude - point2.latitude);
        }
    }

    /**
     * y = delta*x+value
     *
     * @return value
     */
    public Double countLineValue(LatLng point1, LatLng point2) {
        if (point1.latitude == point2.latitude) {
            return null;
        } else if (point1.longitude == point2.longitude) {
            return point1.longitude;
        } else {
            return point1.longitude - (point1.longitude - point2.longitude) / (point1.latitude - point2.latitude) * point1.latitude;
        }
    }

    @Override
    public void onMapLoaded() {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
        } else {
            marker.hideInfoWindow();
        }
        if ("0".equals(marker.getTitle())) {
            aMap.clear();//清除图标---

            reDrawPolyline(pageWhich, POLYLINE_STATUS_FINISH);//
            //弹出确定对话框，让用户确定是否
            if (pageStatus == STATUS_EDIT) {
                if (pageWhich == FIRST && first.size() < 2) {
                    QuanjiakanDialog.getInstance().getCommonConfirmDialog(getActivity(), "提示", "请至少选择两个点来确定围栏范围!", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    return true;
                } else if (pageWhich == SECOND && second.size() < 2) {
                    QuanjiakanDialog.getInstance().getCommonConfirmDialog(getActivity(), "提示", "请至少选择两个点来确定围栏范围!", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    return true;
                } else if (pageWhich == THIRD && third.size() < 2) {
                    QuanjiakanDialog.getInstance().getCommonConfirmDialog(getActivity(), "提示", "请至少选择两个点来确定围栏范围!", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    return true;
                }
                openConfirmDialog();
            }

        } else {

        }
        return true;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    List<LatLng> tempList;

    @Override
    public void onMarkerDrag(Marker marker) {
        tempList = new ArrayList<>();
        switch (pageWhich) {
            case FIRST: {
                final int dragIndex = Integer.parseInt(marker.getTitle());
                final LatLng temp = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                tempList.addAll(first);
                tempList.remove(dragIndex);
                tempList.add(dragIndex, temp);
                if (isValidPoint(tempList)) {
                    first.remove(dragIndex);
                    first.add(dragIndex, temp);
                }
                break;
            }
            case SECOND: {
                final int dragIndex = Integer.parseInt(marker.getTitle());
                final LatLng temp = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
//                second.remove(dragIndex);
//                second.add(dragIndex,temp);
                tempList.addAll(second);
                tempList.remove(dragIndex);
                tempList.add(dragIndex, temp);
                if (isValidPoint(tempList)) {
                    second.remove(dragIndex);
                    second.add(dragIndex, temp);
                }
                break;
            }
            case THIRD: {
                final int dragIndex = Integer.parseInt(marker.getTitle());
                final LatLng temp = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
//                third.remove(dragIndex);
//                third.add(dragIndex,temp);
                tempList.addAll(third);
                tempList.remove(dragIndex);
                tempList.add(dragIndex, temp);
                if (isValidPoint(tempList)) {
                    third.remove(dragIndex);
                    third.add(dragIndex, temp);
                }
                break;
            }
            default:
                break;
        }
        if (aMap != null) {
            aMap.clear();
        }
        reDrawPolyline(pageWhich, lineStatus);
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
//        reDrawPolyline(pageWhich,POLYLINE_STATUS_UNFINISH);
    }

    public boolean isValidPoint(List<LatLng> list) {
        //判断当前点与已有的点连线是否存在交叉
        boolean bool = false;
        if (list.size() == 4) {//4个点
            bool = judgeIntersect(list.get(0), list.get(1), list.get(2), list.get(3));
            if (bool) {//存在交叉了
                return !bool;
            } else {
                bool = judgeIntersect(list.get(1), list.get(2), list.get(3), list.get(0));
            }
        } else if (list.size() == 5) {//5个点
            bool = judgeIntersect(list.get(0), list.get(1), list.get(2), list.get(3));
            if (bool) {
                return !bool;
            }
            bool = judgeIntersect(list.get(0), list.get(1), list.get(3), list.get(4));
            if (bool) {
                return !bool;
            }
            bool = judgeIntersect(list.get(1), list.get(2), list.get(3), list.get(4));
            if (bool) {
                return !bool;
            }
            bool = judgeIntersect(list.get(1), list.get(2), list.get(3), list.get(0));
            if (bool) {
                return !bool;
            }
            bool = judgeIntersect(list.get(2), list.get(3), list.get(4), list.get(0));
        } else if (list.size() == 6) {//6个点
            bool = judgeIntersect(list.get(0), list.get(1), list.get(2), list.get(3));
            if (bool) {
                return !bool;
            }
            bool = judgeIntersect(list.get(0), list.get(1), list.get(3), list.get(4));
            if (bool) {
                return !bool;
            }
            bool = judgeIntersect(list.get(0), list.get(1), list.get(4), list.get(5));
            if (bool) {
                return !bool;
            }
            //******************
            bool = judgeIntersect(list.get(1), list.get(2), list.get(3), list.get(4));
            if (bool) {
                return !bool;
            }
            bool = judgeIntersect(list.get(1), list.get(2), list.get(4), list.get(5));
            if (bool) {
                return !bool;
            }
            bool = judgeIntersect(list.get(1), list.get(2), list.get(5), list.get(0));
            if (bool) {
                return !bool;
            }
            //******************
            bool = judgeIntersect(list.get(2), list.get(3), list.get(4), list.get(5));
            if (bool) {
                return !bool;
            }
            bool = judgeIntersect(list.get(2), list.get(3), list.get(5), list.get(0));
            if (bool) {
                return !bool;
            }
            bool = judgeIntersect(list.get(3), list.get(4), list.get(5), list.get(0));
        }
        return !bool;
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
//        if (rCode == 1000) {
//            if (result != null && result.getRegeocodeAddress() != null
//                    && result.getRegeocodeAddress().getFormatAddress() != null) {
//                String district = result.getRegeocodeAddress().getDistrict();
//                String province = result.getRegeocodeAddress().getProvince();
//                String city = result.getRegeocodeAddress().getCity();
//                String addressName = result.getRegeocodeAddress().getFormatAddress()
//						/*+ "附近"*/;
////                mAddress = addressName.replace(province,"").replace(district,"").replace(city,"");
//                info_location.setText("地址:"+addressName.replace(province,""));
//                info_location.setTag(addressName.replace(province,""));
//                LogUtil.e("经纬度解析成功!");
//            } else {
//                LogUtil.e("对不起，没有搜索到相关数据！");
//            }
//        } else {
//            BaseApplication.getInstances().showerror(this,rCode);
//            LogUtil.e("解析失败：  "+rCode);
//        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    public double mLat = 0x0;
    public double mLon = 0x0;

    public void getAddress(final LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 100,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }


    @Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onCommonNattyData(CommonNattyData msg) {
        closePopupWindow();
        switch (msg.getType()) {
            case NattyProtocolFilter.DISPLAY_UPDATE_CONTROL_LOCATION_NEW: {
                //{"Results":{"Type":"WIFI","Radius":"35","Location":"113.2416873,23.1320855","Category":"WIFI","IMEI":"352315052834187"}}
                //{"Results":{"Type":"WIFI","Radius":"35","Location":"113.2415614,23.1322708","Category":"WIFI","IMEI":"355637050066828"}}
                String data = msg.getData();
                WatchLocationBroadcase result = (WatchLocationBroadcase) SerialUtil.jsonToObject(data, new TypeToken<WatchLocationBroadcase>() {
                }.getType());
                if (result != null &&
                        result.getResults() != null
                        && result.getResults().getIMEI() != null
                        && result.getResults().getIMEI().length() > 0

                        && result.getResults().getType() != null

                        && result.getResults().getLocation() != null
                        && result.getResults().getLocation().length() > 3
                        && result.getResults().getLocation().contains(",")
                        ) {
                    if (result.getResults().getIMEI().equals(device_id)
                            ) {
                        String location = result.getResults().getLocation();
                        if (location != null && location.contains(",") && location.split(",").length == 2) {

//                            if ("gps".equals(result.getResults().getType().toLowerCase())) {
//                                HashMap<String, Double> latlngMap = GPSToAMap.gpsToAmapPoint(location);
//                                mLat = latlngMap.get("lon");
//                                mLon = latlngMap.get("lat");
//                            } else {
//                                mLat = Double.parseDouble(location.split(",")[1]);
//                                mLon = Double.parseDouble(location.split(",")[0]);
//                            }

                            mLat = Double.parseDouble(location.split(",")[1]);
                            mLon = Double.parseDouble(location.split(",")[0]);
                            moveCamera(new LatLng(mLat, mLon));
//                            showMarker(new LatLng(lat,lng));
//                            getAddressByLatlng(new LatLng(lat,lng));
                            if (pageStatus == STATUS_SHOW) {
                                aMap.clear();//清除图标---
                                moveCamera(new LatLng(mLat, mLon));
                                selfMarker(new LatLng(mLat, mLon));
                                getAddress(new LatLonPoint(mLat, mLon));

                            } else {
                                aMap.clear();//清除图标---
                                reDrawPolyline(pageWhich, lineStatus);
                            }
                        } else {

                        }
                        break;
                    }
                }
                break;
            }
            case NattyProtocolFilter.DISPLAY_UPDATE_DATA_COMMON_BROADCAST: {
                //{"Id":"2","Results":{"IMEI":"355637053995130","Category":"Efence","Action":"Add","Index":"2","Efence":{"Num":"4","Points":["113.242554,23.139700","113.242584,23.132236","113.252510,23.131338","113.252510,23.139931"]}}}
                String data = msg.getData();
                if (data != null && data.contains("Efence") && data.contains("Add")) {
                    WatchEfenceBroadcastAdd result = (WatchEfenceBroadcastAdd) SerialUtil.jsonToObject(data, new TypeToken<WatchEfenceBroadcastAdd>() {
                    }.getType());
                    if (result != null && device_id.equals(result.getResults().getIMEI())) {
                        DeviceRailEntity locaData = new DeviceRailEntity();
                        if ("1".equals(result.getResults().getIndex())) {
                            first.clear();
                            for (String temp : result.getResults().getEfence().getPoints()) {
                                String[] points = temp.split(",");
                                first.add(new LatLng(Double.parseDouble(points[1]), Double.parseDouble(points[0])));
                            }
                            //TODO 保存该数据
                            int size = first.size();
                            if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
                                locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                locaData.setDeviceid(device_id);
                                locaData.setNumber(size + "");
                                locaData.setPath(EfenceUtil.ListPointToString(first));
                                locaData.setTime(System.currentTimeMillis() + "");
                                locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
                            } else {
                                locaData = new DeviceRailEntity();
                                locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                locaData.setDeviceid(device_id);
                                locaData.setNumber(size + "");
                                locaData.setPath(EfenceUtil.ListPointToString(first));
                                locaData.setTime(System.currentTimeMillis() + "");
                                locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
                            }
                            DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
                            getRailNumber();

                        } else if ("2".equals(result.getResults().getIndex())) {
                            second.clear();
                            for (String temp : result.getResults().getEfence().getPoints()) {
                                String[] points = temp.split(",");
                                second.add(new LatLng(Double.parseDouble(points[1]), Double.parseDouble(points[0])));
                            }
                            //TODO 保存该数据
                            int size = second.size();
                            if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
                                locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                locaData.setDeviceid(device_id);
                                locaData.setNumber(size + "");
                                locaData.setPath(EfenceUtil.ListPointToString(second));
                                locaData.setTime(System.currentTimeMillis() + "");
                                locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
                            } else {
                                locaData = new DeviceRailEntity();
                                locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                locaData.setDeviceid(device_id);
                                locaData.setNumber(size + "");
                                locaData.setPath(EfenceUtil.ListPointToString(second));
                                locaData.setTime(System.currentTimeMillis() + "");
                                locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
                            }
                            DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
                            getRailNumber();
                        } else if ("3".equals(result.getResults().getIndex())) {
                            third.clear();
                            for (String temp : result.getResults().getEfence().getPoints()) {
                                String[] points = temp.split(",");
                                third.add(new LatLng(Double.parseDouble(points[1]), Double.parseDouble(points[0])));
                            }
                            //TODO 保存该数据
                            int size = third.size();
                            if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
                                locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                locaData.setDeviceid(device_id);
                                locaData.setNumber(size + "");
                                locaData.setPath(EfenceUtil.ListPointToString(third));
                                locaData.setTime(System.currentTimeMillis() + "");
                                locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
                            } else {
                                locaData = new DeviceRailEntity();
                                locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                locaData.setDeviceid(device_id);
                                locaData.setNumber(size + "");
                                locaData.setPath(EfenceUtil.ListPointToString(third));
                                locaData.setTime(System.currentTimeMillis() + "");
                                locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
                            }
                            DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
                            getRailNumber();
                        } else {

                        }
                    }
                } else if (data != null && data.contains("Efence") && data.contains("Delete")) {
                    //TODO 收到删除
                    dismissDialog();
                    pageStatus = STATUS_SHOW;
                    getAddress(new LatLonPoint(mLat, mLon));
                    //{"Results":{"IMEI":"355637053995130","Category":"Efence","Action":"Delete","Index":"1"}}
                    WatchEfenceDelete result = (WatchEfenceDelete) SerialUtil.jsonToObject(data, new TypeToken<WatchEfenceDelete>() {
                    }.getType());
                    if (result != null && device_id.equals(result.getResults().getIMEI())) {
                        if ("1".equals(result.getResults().getIndex())) {
                            DeviceRailHandler.remove(device_id, TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST, "");
                            getRailNumber();
                        } else if ("2".equals(result.getResults().getIndex())) {
                            DeviceRailHandler.remove(device_id, TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND, "");
                            getRailNumber();
                        } else if ("3".equals(result.getResults().getIndex())) {
                            DeviceRailHandler.remove(device_id, TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD, "");
                            getRailNumber();
                        } else {

                        }
                    }
                } else if (data != null && data.contains("Results")
                        && data.contains("IMEI")
                        && data.contains("LocationReport")) {
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        //TODO 判断含有经纬度字算，并且，该字段是有效字段【由于给的数据实际中是存在无效字段的，所有需要判断有效性】
                        if (jsonObject.has("Results")
                                && jsonObject.getJSONObject("Results") != null

                                && jsonObject.getJSONObject("Results").has("IMEI")
                                && jsonObject.getJSONObject("Results").getString("IMEI") != null

                                && jsonObject.getJSONObject("Results").has("LocationReport")
                                && jsonObject.getJSONObject("Results").getJSONObject("LocationReport") != null

                                && jsonObject.getJSONObject("Results").getJSONObject("LocationReport").has("Type")
                                && jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Type") != null

                                && jsonObject.getJSONObject("Results").getJSONObject("LocationReport").has("Location")
                                && jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Location") != null
                                && jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Location").length() > 3
                                && jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Location").contains(",")) {
                            if (jsonObject.getJSONObject("Results").getString("IMEI").equals(device_id)
                                    ) {
                                //TODO 变更定位地址 刷新当前位置

//                                if ("gps".equals(jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Type").toLowerCase())) {
//                                    HashMap<String, Double> latlngMap = GPSToAMap.gpsToAmapPoint(jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Location"));
//                                    mLon = latlngMap.get("lon");
//                                    mLat = latlngMap.get("lat");
//                                } else {
//                                    String[] strings = jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Location").split(",");
//                                    mLon = Double.parseDouble(strings[1]);
//                                    mLat = Double.parseDouble(strings[0]);
//                                }


                                String[] strings = jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Location").split(",");
                                mLon = Double.parseDouble(strings[1]);
                                mLat = Double.parseDouble(strings[0]);

//                                moveCamera(new LatLng(lat,lng));
//                                showMarker(new LatLng(lat,lng));
//                                getAddressByLatlng(new LatLng(lat,lng));

                                getAddress(new LatLonPoint(mLat, mLon));
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (data != null && data.contains("Results")
                        && data.contains("IMEI")
                        && data.contains("LocationReport")) {
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        //TODO 判断含有经纬度字算，并且，该字段是有效字段【由于给的数据实际中是存在无效字段的，所有需要判断有效性】
                        if (jsonObject.has("Results")
                                && jsonObject.getJSONObject("Results") != null

                                && jsonObject.getJSONObject("Results").has("IMEI")
                                && jsonObject.getJSONObject("Results").getString("IMEI") != null

                                && jsonObject.getJSONObject("Results").has("LocationReport")
                                && jsonObject.getJSONObject("Results").getJSONObject("LocationReport") != null

                                && jsonObject.getJSONObject("Results").getJSONObject("LocationReport").has("Type")
                                && jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Type") != null

                                && jsonObject.getJSONObject("Results").getJSONObject("LocationReport").has("Location")
                                && jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Location") != null
                                && jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Location").length() > 3
                                && jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Location").contains(",")) {
                            if (jsonObject.getJSONObject("Results").getString("IMEI").equals(device_id)
                                    ) {

                                String location = jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Location");
                                if (location != null && location.contains(",") && location.split(",").length == 2) {


//                                    if ("gps".equals(jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Type").toLowerCase())) {
//                                        HashMap<String, Double> latlngMap = GPSToAMap.gpsToAmapPoint(location);
//                                        mLon = latlngMap.get("lon");
//                                        mLat = latlngMap.get("lat");
//                                    } else {
//                                        mLat = Double.parseDouble(location.split(",")[1]);
//                                        mLon = Double.parseDouble(location.split(",")[0]);
//                                    }


                                    mLat = Double.parseDouble(location.split(",")[1]);
                                    mLon = Double.parseDouble(location.split(",")[0]);
                                    moveCamera(new LatLng(mLat, mLon));
//                            showMarker(new LatLng(lat,lng));
//                            getAddressByLatlng(new LatLng(lat,lng));
                                    if (pageStatus == STATUS_SHOW) {
                                        aMap.clear();//清除图标---
                                        moveCamera(new LatLng(mLat, mLon));
                                        selfMarker(new LatLng(mLat, mLon));
                                        getAddress(new LatLonPoint(mLat, mLon));

                                    } else {
                                        aMap.clear();//清除图标---
                                        reDrawPolyline(pageWhich, lineStatus);
                                    }
                                } else {

                                }

                                break;
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case NattyProtocolFilter.DISPLAY_UPDATE_DATA_RESULT: {
                //需要自己根据发送的命令去判断
                String data = msg.getData();
                switch (currentCommand) {
                    case MainEntryMapActivity.SET_EFENCE: {
                        LogUtil.e("DATA_RESULT  SET_EFENCE：" + data);
                        WatchEfenceResult result = (WatchEfenceResult) SerialUtil.jsonToObject(data, new TypeToken<WatchEfenceResult>() {
                        }.getType());
                        if (result != null && "200".equals(result.getResults().getCode())) {
                            DeviceRailEntity locaData = new DeviceRailEntity();
                            switch (pageWhich) {
                                case FIRST: {
                                    int size = first.size();
                                    if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
                                        locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                        locaData.setDeviceid(device_id);
                                        locaData.setNumber(size + "");
                                        locaData.setPath(EfenceUtil.ListPointToString(first));
                                        locaData.setTime(System.currentTimeMillis() + "");
                                        locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
                                    } else {
                                        locaData = new DeviceRailEntity();
                                        locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                        locaData.setDeviceid(device_id);
                                        locaData.setNumber(size + "");
                                        locaData.setPath(EfenceUtil.ListPointToString(first));
                                        locaData.setTime(System.currentTimeMillis() + "");
                                        locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
                                    }
                                    DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
                                    getRailNumber();
                                    break;
                                }
                                case SECOND: {
                                    int size = second.size();
                                    if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
                                        locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                        locaData.setDeviceid(device_id);
                                        locaData.setNumber(size + "");
                                        locaData.setPath(EfenceUtil.ListPointToString(second));
                                        locaData.setTime(System.currentTimeMillis() + "");
                                        locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
                                    } else {
                                        locaData = new DeviceRailEntity();
                                        locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                        locaData.setDeviceid(device_id);
                                        locaData.setNumber(size + "");
                                        locaData.setPath(EfenceUtil.ListPointToString(second));
                                        locaData.setTime(System.currentTimeMillis() + "");
                                        locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
                                    }
                                    DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
                                    getRailNumber();
                                    break;
                                }
                                case THIRD: {
                                    int size = third.size();
                                    if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
                                        locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                        locaData.setDeviceid(device_id);
                                        locaData.setNumber(size + "");
                                        locaData.setPath(EfenceUtil.ListPointToString(third));
                                        locaData.setTime(System.currentTimeMillis() + "");
                                        locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
                                    } else {
                                        locaData = new DeviceRailEntity();
                                        locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                        locaData.setDeviceid(device_id);
                                        locaData.setNumber(size + "");
                                        locaData.setPath(EfenceUtil.ListPointToString(third));
                                        locaData.setTime(System.currentTimeMillis() + "");
                                        locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
                                    }
                                    DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
                                    getRailNumber();
                                    break;
                                }
                            }
                        } else if (result != null && "10001".equals(result.getResults().getCode())) {
                            BaseApplication.getInstances().toast(getActivity(), "设备不在线");
                        } else if (result != null && "10009".equals(result.getResults().getCode())) {
                            BaseApplication.getInstances().toast(getActivity(), "已存在围栏，请删除后再设置!");
                        } else {
                            BaseApplication.getInstances().toast(getActivity(), "设置围栏失败 ");
                        }
                        break;
                    }
                    case MainEntryMapActivity.DEL_EFENCE: {
                        dismissDialog();
                        LogUtil.e("DATA_RESULT  DEL_EFENCE：" + data);
                        WatchEfenceResult result = (WatchEfenceResult) SerialUtil.jsonToObject(data, new TypeToken<WatchEfenceResult>() {
                        }.getType());
                        if (result != null && "200".equals(result.getResults().getCode())) {
                            switch (pageWhich) {
                                case FIRST: {
                                    //需要判断成功了才进行这个操作
                                    DeviceRailHandler.remove(device_id, TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST, "");
                                    getRailNumber();
                                    break;
                                }
                                case SECOND: {
                                    DeviceRailHandler.remove(device_id, TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND, "");
                                    getRailNumber();
                                    break;
                                }
                                case THIRD: {
                                    DeviceRailHandler.remove(device_id, TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD, "");
                                    getRailNumber();
                                    break;
                                }
                            }
                        } else if (result != null && "10001".equals(result.getResults().getCode())) {
                            BaseApplication.getInstances().toast(getActivity(), "设备不在线");
                        } else {
                            BaseApplication.getInstances().toast(getActivity(), "设置围栏失败");
                        }
                        break;
                    }
                }
                break;
            }
            case NattyProtocolFilter.DISPLAY_UPDATE_DATA_ROUTE: {
                //需要自己根据发送的命令去判断
                String data = msg.getData();
                try {
                    JSONObject json = new JSONObject(data);

                    if (json.has("results") && json.getJSONObject("results") != null

                            && json.getJSONObject("results").has("Category")
                            && json.getJSONObject("results").getString("Category") != null
                            && "Location".equals(json.getJSONObject("results").getString("Category"))

                            && json.getJSONObject("results").has("IMEI")
                            && json.getJSONObject("results").getString("IMEI") != null
                            && json.getJSONObject("results").getString("IMEI").length() > 0

                            && json.getJSONObject("results").has("Type")
                            && json.getJSONObject("results").getString("Type") != null

                            && json.getJSONObject("results").has("Location")
                            && json.getJSONObject("results").getString("Location") != null
                            && json.getJSONObject("results").getString("Location").length() > 3
                            && json.getJSONObject("results").getString("Location").contains(",")
                            ) {
                        LogUtil.e("FreshLocation.GET_LOCATION:" + data);

                        if (json.getJSONObject("results").getString("IMEI").equals(device_id)
                                ) {
                            String location = json.getJSONObject("results").getString("Location");

                            if (location != null && location.contains(",") && location.split(",").length == 2) {

//                                    if ("gps".equals(json.getJSONObject("results").getString("Type").toLowerCase())) {
//                                        HashMap<String, Double> latlngMap = GPSToAMap.gpsToAmapPoint(location);
//                                        mLon = latlngMap.get("lon");
//                                        mLat = latlngMap.get("lat");
//                                    } else {
//                                        mLat = Double.parseDouble(location.split(",")[1]);
//                                        mLon = Double.parseDouble(location.split(",")[0]);
//                                    }


                                mLat = Double.parseDouble(location.split(",")[1]);
                                mLon = Double.parseDouble(location.split(",")[0]);
                                moveCamera(new LatLng(mLat, mLon));
//                            showMarker(new LatLng(lat,lng));
//                            getAddressByLatlng(new LatLng(lat,lng));
                                if (pageStatus == STATUS_SHOW) {
                                    aMap.clear();//清除图标---
                                    moveCamera(new LatLng(mLat, mLon));
                                    selfMarker(new LatLng(mLat, mLon));
                                    getAddress(new LatLonPoint(mLat, mLon));

                                } else {
                                    aMap.clear();//清除图标---
                                    reDrawPolyline(pageWhich, lineStatus);
                                }
                            } else {
                            }
                            break;
                        }
                    } else if (
                            json.has("Results") && json.getJSONObject("Results") != null

                                    && json.getJSONObject("Results").has("Category")
                                    && json.getJSONObject("Results").getString("Category") != null
                                    && "Location".equals(json.getJSONObject("Results").getString("Category"))

                                    && json.getJSONObject("Results").has("IMEI")
                                    && json.getJSONObject("Results").getString("IMEI") != null
                                    && json.getJSONObject("Results").getString("IMEI").length() > 0

                                    && json.getJSONObject("Results").has("Type")
                                    && json.getJSONObject("Results").getString("Type") != null

                                    && json.getJSONObject("Results").has("Location")
                                    && json.getJSONObject("Results").getString("Location") != null
                                    && json.getJSONObject("Results").getString("Location").length() > 3
                                    && json.getJSONObject("Results").getString("Location").contains(",")
                            ) {
                        if (json.getJSONObject("Results").getString("IMEI").equals(device_id)
                                ) {
                            String location = json.getJSONObject("Results").getString("Location");
                            if (location != null && location.contains(",") && location.split(",").length == 2) {

//                                    if ("gps".equals(json.getJSONObject("Results").getString("Type").toLowerCase())) {
//                                        HashMap<String, Double> latlngMap = GPSToAMap.gpsToAmapPoint(location);
//                                        mLon = latlngMap.get("lon");
//                                        mLat = latlngMap.get("lat");
//                                    } else {
//                                        mLat = Double.parseDouble(location.split(",")[1]);
//                                        mLon = Double.parseDouble(location.split(",")[0]);
//                                    }


                                mLat = Double.parseDouble(location.split(",")[1]);
                                mLon = Double.parseDouble(location.split(",")[0]);
                                moveCamera(new LatLng(mLat, mLon));
//                            showMarker(new LatLng(lat,lng));
//                            getAddressByLatlng(new LatLng(lat,lng));
                                if (pageStatus == STATUS_SHOW) {
                                    aMap.clear();//清除图标---
                                    moveCamera(new LatLng(mLat, mLon));
                                    selfMarker(new LatLng(mLat, mLon));
                                    getAddress(new LatLonPoint(mLat, mLon));

                                } else {
                                    aMap.clear();//清除图标---
                                    reDrawPolyline(pageWhich, lineStatus);
                                }
                            } else {

                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
