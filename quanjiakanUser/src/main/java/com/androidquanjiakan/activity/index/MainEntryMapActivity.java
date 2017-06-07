package com.androidquanjiakan.activity.index;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.androidquanjiakan.activity.common.CommonWebEntryActivity;
import com.androidquanjiakan.activity.common.CommonWebEntryActivity_kitkat;
import com.androidquanjiakan.activity.index.bind.DevicesBindActivity;
import com.androidquanjiakan.activity.index.watch_child.WatchChildEntryActivity;
import com.androidquanjiakan.activity.index.watch_old.WatchOldEntryActivity;
import com.androidquanjiakan.adapter.DeviceContainerAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.datahandler.BindDeviceHandler;
import com.androidquanjiakan.entity.BindDeviceEntity;
import com.androidquanjiakan.entity.CommonNattyData;
import com.androidquanjiakan.entity.CommonVoiceData;
import com.androidquanjiakan.entity.DeviceContainerEntity;
import com.androidquanjiakan.entity.WatchCommonResult;
import com.androidquanjiakan.entity.WatchLocationBroadcase;
import com.androidquanjiakan.entity.WatchMapDevice;
import com.androidquanjiakan.entity.WatchMapDevice_DeviceInfo;
import com.androidquanjiakan.entity_util.NetUtil;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.entity_util.UnitUtil;
import com.androidquanjiakan.util.GPSToAMap;
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
import com.wbj.ndk.natty.client.WGSTOGCJ02;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainEntryMapActivity extends BaseActivity implements AMap.OnMarkerClickListener,
        AMap.OnInfoWindowClickListener, AMap.OnMarkerDragListener, AMap.OnMapLoadedListener,
        View.OnClickListener, AMap.InfoWindowAdapter,
        GeocodeSearch.OnGeocodeSearchListener {

    private ListView watch_container;
    private int currentCommand = 0;
    public static final int GET_CONFIG = 1;
    public static final int GET_LOCATION = 2;
    public static final int GET_SIGNAL = 3;
    public static final int GET_BATTERY = 4;
    public static final int SET_EFENCE = 5;
    public static final int DEL_EFENCE = 6;
    public static final int FRESH_MARK_LIST = 16;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case FRESH_MARK_LIST: {
                    showMarkerList(markerList, clickPosition);
                    break;
                }
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_entry_map);
        initTitleBar();

        initView(savedInstanceState);

        initWatchContainer();

        locateSelf(LOCATION_TYPE_GET_POSITION);
        //TODO 需要考虑绑定成功后返回刷新数据的逻辑
        //放到onResume里面做
        /*mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                getNetFamilyBindData();
            }
        }, 1000);*/
    }


    private ImageButton ibtn_back;
    private TextView tv_title;
    private TextView menu_text;
    private ImageButton ibtn_menu;

    public void initTitleBar() {
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        menu_text = (TextView) findViewById(R.id.menu_text);
        ibtn_menu = (ImageButton) findViewById(R.id.ibtn_menu);

        ibtn_back.setVisibility(View.VISIBLE);
        tv_title.setVisibility(View.VISIBLE);
        menu_text.setVisibility(View.VISIBLE);
        ibtn_menu.setVisibility(View.GONE);

        tv_title.setText("首页");
        ibtn_back.setImageResource(R.drawable.index_icon_function);//返回
        ibtn_menu.setImageResource(R.drawable.ic_navigation_drawer);//菜单
        menu_text.setText("绑定");

        ibtn_back.setOnClickListener(this);
        ibtn_menu.setOnClickListener(this);
        menu_text.setOnClickListener(this);
        tv_title.setOnClickListener(this);
    }


    private AMap aMap;
    private MapView mapView;
    private GeocodeSearch geocoderSearch;

    protected void initView(Bundle savedInstanceState) {
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = mapView.getMap();
        }

        aMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
        aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
        aMap.setInfoWindowAdapter(this);

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
    }

    private double selfLatitude = -1000;
    private double selfLongitude = -1000;
    private String selfAdress = "";
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;


    private final int LOCATION_TYPE_GET_POSITION = 1;//仅仅获取定位，不进行展示
    private final int LOCATION_TYPE_SHOW_POSITION = 2;//展示位置，但不移动焦点
    private final int LOCATION_TYPE_SHOW_AND_MOVE_POSITION = 3;//展示位置，并移动焦点
    //根据类型进行定位
    public void locateSelf(final int locateType) {
        stopLocateSelf();
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.poi_marker_pressed));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.TRANSPARENT);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细

        aMap.setMyLocationStyle(myLocationStyle);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        aMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                mListener = onLocationChangedListener;
                if (mlocationClient == null) {
                    mlocationClient = new AMapLocationClient(MainEntryMapActivity.this);
                    mLocationOption = new AMapLocationClientOption();
                    //设置定位监听
                    mlocationClient.setLocationListener(new AMapLocationListener() {
                        @Override
                        public void onLocationChanged(AMapLocation aMapLocation) {
                            if (mListener != null && aMapLocation != null) {
                                if (aMapLocation != null
                                        && aMapLocation.getErrorCode() == 0) {
                                    if(locateType!=LOCATION_TYPE_GET_POSITION){
                                        mListener.onLocationChanged(aMapLocation);
                                    }
                                    selfLatitude = aMapLocation.getLatitude();
                                    selfLongitude = aMapLocation.getLongitude();
                                    selfAdress = aMapLocation.getAddress();
                                    LogUtil.e("自己的位置\nselfLatitude:" + selfLatitude +
                                            "\nselfLongitude:" + selfLongitude +
                                            "\nselfAdress:" + selfAdress+
                                            "\n TYPE:"+locateType+
                                            "\n("+"LOCATION_TYPE_GET_POSITION=="+LOCATION_TYPE_GET_POSITION+"  ;  "
                                            +"LOCATION_TYPE_SHOW_POSITION=="+LOCATION_TYPE_SHOW_POSITION+"   ;  "
                                            +"LOCATION_TYPE_SHOW_AND_MOVE_POSITION=="+LOCATION_TYPE_SHOW_AND_MOVE_POSITION+"  "+")"
                                    );
                                    stopLocateSelf();
                                    if(locateType==LOCATION_TYPE_SHOW_AND_MOVE_POSITION){
                                        moveCamera(new LatLng(selfLatitude, selfLongitude));
                                    }
                                }else{
                                }
                            }else{
                            }
                        }
                    });
                    mLocationOption.setInterval(30000);
                    //设置为高精度定位模式
                    mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
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
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setCompassEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);
    }


    //终止定位过程
    public void stopLocateSelf() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    //WatchList 对应的数据，Adapter
    private List<DeviceContainerEntity> watchData = new ArrayList<>();
    private DeviceContainerAdapter mAdapter;

    private int clickPosition = 0;
    //初始化ListView以及Adapter容器
    public void initWatchContainer() {
        if (deviceEntityList == null) {
            deviceEntityList = new ArrayList<>();
        }
        watch_container = (ListView) findViewById(R.id.device_container);
//        deviceEntityList = BaseApplication.getInstances().getDaoInstant().getWatchMapDevice_DeviceInfoDao().loadAll();
        mAdapter = new DeviceContainerAdapter(MainEntryMapActivity.this, deviceEntityList);
        watch_container.setAdapter(mAdapter);
        watch_container.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * 变更InfoWindow对应的点
                 */
                clickPosition = (int) id;
                if (deviceEntityList.size() > clickPosition) {
                    //TODO 测试用定位点非真实定位点
                    //检查该设备在近30s内是否发送的SOS报警信号，有则展示报警的图标
                    if(30000>(System.currentTimeMillis()-deviceEntityList.get(clickPosition).getAlarmTime())){
                        showMarkerAlarmList(markerList, (clickPosition));
                    }else{
                        showMarkerList(markerList, (clickPosition));
                    }

                    //TODO 最好是将最近的定位地址也返回过来，否则需要针对各个设备循环获取定位地址、电话
                    //TODO 拉指定设备的定位地址
                    //TODO To Test indicate which one has been Selected
                    mAdapter.setSelectedPosition(clickPosition);//TODO 这个用于显示点击时的切换效果
                    mAdapter.notifyDataSetChanged();
                    if (deviceEntityList.get(clickPosition).getLocation() == null ||
                            deviceEntityList.get(clickPosition).getLocation().length() < 1 ||
                            !deviceEntityList.get(clickPosition).getLocation().contains(",") ||
                            deviceEntityList.get(clickPosition).getLocation().split(",").length != 2 ||
                            deviceEntityList.get(clickPosition).getLocation().split(",")[0] == null ||
                            deviceEntityList.get(clickPosition).getLocation().split(",")[0].length() < 1
                            ) {
                        //当点击的设备没有定位点，或者定位点不符合协议规范时，重新拉取定位
                        getLocation(deviceEntityList.get(clickPosition).getDeviceid());//TODO 收到16进制

                        //同时展示自己的当前位置【没有时重新拉取自己的定位】 TODO 该需求为王博靖在会上说的
                        if (selfLatitude == -1000 || selfLongitude == -1000) {
                            locateSelf(LOCATION_TYPE_SHOW_AND_MOVE_POSITION);
                        } else {
                            moveCamera(new LatLng(selfLatitude, selfLongitude));
                        }
                    } else {
                        getLocation(deviceEntityList.get(clickPosition).getDeviceid());//TODO 收到16进制
                        moveCamera(watchData.get((clickPosition)).getLatLng());
                    }
                }

            }
        });
    }

    //TODO 在变更了默认设备后重置默认的显示
    public void resetDefaultDevice() {
        int click = 0;
        if (BaseApplication.getInstances().getDefaultDevice() != null && BaseApplication.getInstances().getDefaultDevice().length() > 0) {
            for (int i = 0; i < mAdapter.getCount(); i++) {
                if (BaseApplication.getInstances().getDefaultDevice().equals(mAdapter.getDevices().get(i).getDeviceid())) {
                    click = i;
                    break;
                }
            }
        }
        mAdapter.setSelectedPosition(click);
        mAdapter.notifyDataSetChanged();
    }

    //Maker对应的经纬度列表
    private List<LatLng> markerList;

    //TODO 重置markerList
    public void resetMarkerList() {
        if (aMap != null) {
            aMap.clear();
        }

        if (markerList == null) {
            markerList = new ArrayList<>();
        } else {
            markerList.clear();
        }
        if (watchData == null) {
            watchData = new ArrayList<>();
        } else {
            watchData.clear();
        }
        //获取设备的定位经纬度数组
        if (deviceEntityList.size() > 0) {
            for (BindDeviceEntity dataTemp : deviceEntityList) {
                DeviceContainerEntity temp = new DeviceContainerEntity();
                if (dataTemp != null) {
                    temp.setName(dataTemp.getName());

                    if (dataTemp.getLocation() != null && dataTemp.getLocation().split(",").length == 2) {
                        String[] location = dataTemp.getLocation().split(",");
                        LatLng locTemp = new LatLng(Double.parseDouble(location[1]), Double.parseDouble(location[0]));
                        markerList.add(locTemp);
                        temp.setLatLng(locTemp);
                    } else {
                        markerList.add(null);
                        temp.setLatLng(null);
                    }
                    watchData.add(temp);
                }

            }
        }else{
            locateSelf(LOCATION_TYPE_SHOW_AND_MOVE_POSITION);
        }
    }

    BindDeviceEntity testOldAdd;
    //TODO 从网络上获取最新数据
    public void getNetFamilyBindData() {


        if(!NetUtil.isNetworkAvailable(this)) {
            if(BindDeviceHandler.getAllValue().size()!=0) {
                deviceEntityList = BindDeviceHandler.getAllValue();
                //加载本地缓存的数据
                mAdapter.setDevices(deviceEntityList);
                mAdapter.notifyDataSetChanged();
                for (BindDeviceEntity dataTemp : deviceEntityList) {
                    if (dataTemp == null) {
                        deviceEntityList.remove(dataTemp);//TODO 删除空数据
                    }
                }
                resetMarkerList();
                //TODO 需要在Adapter数据添加完成后展示
                initContainerData(deviceEntityList);
                //做个绑定显示
                BaseApplication.getInstances().setKeyNumberValue("bind_flag",1);
            }else{
                showTipsDialog();
            }
            return;
        }
        if (markerList == null) {
            markerList = new ArrayList<>();
        } else {
            markerList.clear();
        }
        if (LogUtil.BINDDEVICE_OFF) {
            //不做处理
            showTipsDialog();
        } else {
            if (BaseApplication.getInstances().getDaoInstant().getBindWatchEntityDao().count() > 0) {
                BaseApplication.getInstances().getDaoInstant().getBindWatchEntityDao().deleteAll();
            }
            MyHandler.putTask(MainEntryMapActivity.this, new Task(new HttpResponseInterface() {
                @Override
                public void handMsg(String val) {

//                    {"Result":{"Category":"WatchList","WatchList":[{"IMEI":"240207489224233264","Location":"113.2409402,23.1326885","Name":"爸爸","Online":"0","Picture":"image","Time":"2017-01-01 00:00:00"}]}}
                    if (val != null && val.contains("Result")) {
                        deviceEntityList.clear();
                        WatchMapDevice list = (WatchMapDevice) SerialUtil.jsonToObject(val, new TypeToken<WatchMapDevice>() {
                        }.getType());
                        if (list != null && list.getResults() != null && list.getResults().getWatchList() != null &&
                                list.getResults().getWatchList().size() > 0) {
                            //关闭提醒
                            if (tipsDialog != null) {
                                tipsDialog.dismiss();
                            }
                            //需要存的
                            List<BindDeviceEntity> insertList = new ArrayList<BindDeviceEntity>();
                            //需要更新进数据库的
                            List<BindDeviceEntity> saveList = new ArrayList<BindDeviceEntity>();
                            boolean testAddOld = false;
                            for (WatchMapDevice_DeviceInfo tempInfo : list.getResults().getWatchList()) {
                                BindDeviceEntity temp = new BindDeviceEntity();
                                temp.setW_TYPE(tempInfo.getType());
                                temp.setCreatetime(System.currentTimeMillis() + "");
                                temp.setUser_id(BaseApplication.getInstances().getUser_id());
                                temp.setIcon(tempInfo.getPicture());
                                temp.setName(tempInfo.getName());
                                temp.setDeviceid(tempInfo.getIMEI());
                                temp.setPhoneNumber(tempInfo.getPhoneNum());
                                temp.setLocation(tempInfo.getLocation());
                                temp.setAlarmTime(0);
                                saveList.add(temp);
                            }


                            //现有的数据
                            deviceEntityList = BindDeviceHandler.getAllValue();
                            //清空数据库该账户的数据
                            BindDeviceHandler.removeAll();
                            //过滤当前需要保存的数据
                            for (int j = saveList.size() - 1; j > -1; j--) {
                                BindDeviceEntity save = saveList.get(j);
                                if (deviceEntityList.size() > 0) {
                                    for (int i = deviceEntityList.size() - 1; i > -1; i--) {
                                        BindDeviceEntity temp = deviceEntityList.get(i);
                                        if (save.getDeviceid().equals(temp.getDeviceid())) {
                                            if (save.getLocation() == null || !save.getLocation().contains(",")) {
                                                save.setLocation(temp.getLocation());
                                            }else if(temp.getAlarmTime()>0){//TODO 使用最近的报警地址更新地址信息
                                                save.setLocation(temp.getLocation());
                                            }
                                            if(temp.getAlarmTime()>0 && (temp.getAlarmTime()>save.getAlarmTime())){
                                                save.setAlarmTime(temp.getAlarmTime());
                                            }
                                            insertList.add(save);
                                            break;
                                        } else if (i == 0) {
                                            insertList.add(save);
                                        }
                                    }
                                } else {
                                    insertList.add(save);
                                }
                            }
                            //上面先更新地址与报警时间


                            BindDeviceHandler.insertValue(insertList);

                            deviceEntityList = BindDeviceHandler.getAllValue();
                            mAdapter.setDevices(deviceEntityList);
                            mAdapter.notifyDataSetChanged();
                            BaseApplication.getInstances().setKeyNumberValue("bind_flag",1);
                            for (BindDeviceEntity dataTemp : deviceEntityList) {
                                if (dataTemp == null) {
                                    deviceEntityList.remove(dataTemp);
                                }
                            }
                            resetMarkerList();
                            //TODO 需要在Adapter数据添加完成后展示
                            initContainerData(deviceEntityList);


                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getLocation(deviceEntityList.get(clickPosition).getDeviceid());
                                }
                            },1000);

                        } else {
                            if (BaseApplication.getInstances().getDaoInstant().getBindWatchEntityDao().count() > 0) {
                                BaseApplication.getInstances().getDaoInstant().getBindWatchEntityDao().deleteAll();
                            }
                            showTipsDialog();
                            locateSelf(LOCATION_TYPE_SHOW_AND_MOVE_POSITION);
                        }
                    }
                    //{"code":202,"message":"结果集为空"}
                    else if (val != null && val.contains("code") && val.contains("202")) {
                        locateSelf(LOCATION_TYPE_SHOW_AND_MOVE_POSITION);
                        showTipsDialog();
                        BaseApplication.getInstances().toast(MainEntryMapActivity.this,"暂无绑定设备");
                        BindDeviceHandler.removeAll();//TODO 删除绑定记录
                        deviceEntityList.clear();
                        mAdapter.setDevices(deviceEntityList);
                        mAdapter.notifyDataSetChanged();
                        resetMarkerList();

                    } else {
                        locateSelf(LOCATION_TYPE_SHOW_AND_MOVE_POSITION);
                        showTipsDialog();
                        BaseApplication.getInstances().toast(MainEntryMapActivity.this,"暂无绑定设备");
                        BindDeviceHandler.removeAll();//TODO 删除绑定记录
                        deviceEntityList.clear();
                        mAdapter.setDevices(deviceEntityList);
                        mAdapter.notifyDataSetChanged();
                        resetMarkerList();
                    }
                    //TODO new netlink

                }
            }, HttpUrls.getBindDevices_new(), null, Task.TYPE_GET_STRING_NOCACHE, null));
        }
    }


    //    private List<BindWatchEntity> deviceEntityList = new ArrayList<>();//Old
    private List<BindDeviceEntity> deviceEntityList = new ArrayList<>();//New

    //TODO 初始化容器数据【Marker经纬度，ListView展示的名字与头像】
    private void initContainerData(List<BindDeviceEntity> size) {

        if (watchData == null) {
            watchData = new ArrayList<>();
        } else {
            watchData.clear();
        }
        markerList = new ArrayList<>();

        DeviceContainerEntity temp;
        for (BindDeviceEntity dataTemp : deviceEntityList) {
            temp = new DeviceContainerEntity();
            temp.setName(dataTemp.getName());
            if (dataTemp.getLocation() != null) {
                String[] location = dataTemp.getLocation().split(",");
                if (location.length == 2) {
                    LatLng locTemp = new LatLng(Double.parseDouble(location[1]), Double.parseDouble(location[0]));
                    markerList.add(locTemp);
                    temp.setLatLng(locTemp);
                } else {
                    markerList.add(null);
                    temp.setLatLng(null);
                }
            } else {
                markerList.add(null);
                temp.setLatLng(null);
            }
            watchData.add(temp);
        }

        if (markerList != null && markerList.size() > 0) {
            if(size.size()>clickPosition && (30000>(System.currentTimeMillis()-size.get(clickPosition).getAlarmTime()))){
                showMarkerAlarmList(markerList, clickPosition);
            }else{
                showMarkerList(markerList, clickPosition);
            }
        } else {
            locateSelf(LOCATION_TYPE_SHOW_AND_MOVE_POSITION);
        }
    }

    private final int SOLID_ZOOM = 16;

    //移动地图焦点
    protected void moveCamera(LatLng point) {
        if (point == null) {
            return;
        }
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, SOLID_ZOOM));
    }

    private Marker clickedMarker;
    ArrayList<BitmapDescriptor> iconChildList = new ArrayList<>();
    ArrayList<BitmapDescriptor> iconOldList = new ArrayList<>();

    //展示全部Marker
    public void showMarkerList(List<LatLng> list, int infoWindowPosition) {
        if (list == null || list.size() < 1) {
            locateSelf(LOCATION_TYPE_SHOW_AND_MOVE_POSITION);
            return;
        }
        if (aMap != null) {
            if (iconChildList == null || iconChildList.size() < 1) {
                iconChildList = new ArrayList<>();
            } else {
                iconChildList.clear();
            }
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_1));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_2));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_3));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_4));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_5));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_6));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_7));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_8));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_9));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_10));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_11));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_12));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_13));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_14));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_15));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_16));

            if (iconOldList == null || iconOldList.size() < 1) {
                iconOldList = new ArrayList<>();
            } else {
                iconOldList.clear();
            }
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_1));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_2));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_3));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_4));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_5));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_6));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_7));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_8));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_9));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_10));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_11));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_12));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_13));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_14));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_15));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_16));

            if (aMap != null) {
                aMap.clear();
            }

            if (clickedMarker != null && clickedMarker.isInfoWindowShown()) {
                clickedMarker.hideInfoWindow();
            }

            int size = deviceEntityList.size();
            for (BindDeviceEntity dataTemp : deviceEntityList) {
                if (dataTemp == null) {
                    --size;
                }
            }
            Marker marker;
            for (int i = 0; i < size; i++) {
                final LatLng temp = list.get(i);
                if (temp != null) {
                    if (i == infoWindowPosition) {
                        //TODO 需要区分下是老人还是儿童
                        if ("0".equals(deviceEntityList.get(i).getW_TYPE())) {
                            marker = aMap.addMarker(new MarkerOptions()
                                    .position(temp)
                                    .title("" + i)
                                    .icons(iconOldList)
                                    .period(1)
                                    .draggable(true));
                        } else if ("1".equals(deviceEntityList.get(i).getW_TYPE())) {
                            marker = aMap.addMarker(new MarkerOptions()
                                    .position(temp)
                                    .title("" + i)
                                    .icons(iconChildList)
                                    .period(1)
                                    .draggable(true));
                        } else {
                            marker = aMap.addMarker(new MarkerOptions()
                                    .position(temp)
                                    .title("" + i)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pay_my_addres))
                                    .draggable(true));
                        }
                    } else {

                        if ("0".equals(deviceEntityList.get(i).getW_TYPE())) {
                            marker = aMap.addMarker(new MarkerOptions()
                                    .position(temp)
                                    .title("" + i)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon))
                                    .draggable(true));
                        } else if ("1".equals(deviceEntityList.get(i).getW_TYPE())) {
                            marker = aMap.addMarker(new MarkerOptions()
                                    .position(temp)
                                    .title("" + i)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.crawl_ico_portrait))
                                    .draggable(true));
                        } else {
                            marker = aMap.addMarker(new MarkerOptions()
                                    .position(temp)
                                    .title("" + i)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pay_my_addres))
                                    .draggable(true));
                        }
                    }
                    if (infoWindowPosition == i) {
                        marker.showInfoWindow();
                        clickedMarker = marker;
                        moveCamera(temp);
                    }
                } else {
                    if (infoWindowPosition == i) {
                        locateSelf(LOCATION_TYPE_SHOW_AND_MOVE_POSITION);
                    }
                }
            }

            if(size==0){
                locateSelf(LOCATION_TYPE_SHOW_AND_MOVE_POSITION);
            }
        }
    }

    //展示全部Marker，但动态Marker为SOS信号的Maker
    public void showMarkerAlarmList(List<LatLng> list, int infoWindowPosition) {
        if (list == null || list.size() < 1) {
            locateSelf(LOCATION_TYPE_SHOW_AND_MOVE_POSITION);
            return;
        }
        if (aMap != null) {
            if (iconChildList == null || iconChildList.size() < 1) {
                iconChildList = new ArrayList<>();
            } else {
                iconChildList.clear();
            }
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_child_point_1));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_child_point_2));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_child_point_3));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_child_point_4));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_child_point_5));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_child_point_6));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_child_point_7));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_child_point_8));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_child_point_9));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_child_point_10));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_child_point_11));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_child_point_12));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_child_point_13));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_child_point_14));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_child_point_15));
            iconChildList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_child_point_16));

            if (iconOldList == null || iconOldList.size() < 1) {
                iconOldList = new ArrayList<>();
            } else {
                iconOldList.clear();
            }
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_old_point_1));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_old_point_2));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_old_point_3));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_old_point_4));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_old_point_5));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_old_point_6));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_old_point_7));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_old_point_8));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_old_point_9));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_old_point_10));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_old_point_11));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_old_point_12));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_old_point_13));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_old_point_14));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_old_point_15));
            iconOldList.add(BitmapDescriptorFactory.fromResource(R.drawable.alarm_old_point_16));

            if (aMap != null) {
                aMap.clear();
            }

            if (clickedMarker != null && clickedMarker.isInfoWindowShown()) {
                clickedMarker.hideInfoWindow();
            }

            int size = deviceEntityList.size();
            for (BindDeviceEntity dataTemp : deviceEntityList) {
                if (dataTemp == null) {
                    --size;
                }
            }
            Marker marker;
            for (int i = 0; i < size; i++) {
                final LatLng temp = list.get(i);
                if (temp != null) {
                    if (i == infoWindowPosition) {
                        //TODO 需要区分下是老人还是儿童
                        if ("0".equals(deviceEntityList.get(i).getW_TYPE())) {
                            marker = aMap.addMarker(new MarkerOptions()
                                    .position(temp)
                                    .title("" + i)
                                    .icons(iconOldList)
                                    .period(1)
                                    .draggable(true));
                        } else if ("1".equals(deviceEntityList.get(i).getW_TYPE())) {
                            marker = aMap.addMarker(new MarkerOptions()
                                    .position(temp)
                                    .title("" + i)
                                    .icons(iconChildList)
                                    .period(1)
                                    .draggable(true));
                        } else {
                            marker = aMap.addMarker(new MarkerOptions()
                                    .position(temp)
                                    .title("" + i)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pay_my_addres))
                                    .draggable(true));
                        }
                    } else {

                        if ("0".equals(deviceEntityList.get(i).getW_TYPE())) {
                            marker = aMap.addMarker(new MarkerOptions()
                                    .position(temp)
                                    .title("" + i)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon))
                                    .draggable(true));
                        } else if ("1".equals(deviceEntityList.get(i).getW_TYPE())) {
                            marker = aMap.addMarker(new MarkerOptions()
                                    .position(temp)
                                    .title("" + i)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.crawl_ico_portrait))
                                    .draggable(true));
                        } else {
                            marker = aMap.addMarker(new MarkerOptions()
                                    .position(temp)
                                    .title("" + i)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pay_my_addres))
                                    .draggable(true));
                        }
                    }
                    if (infoWindowPosition == i) {
                        marker.showInfoWindow();
                        clickedMarker = marker;
                        moveCamera(temp);
                    }
                } else {
                }
            }

            if(size==0){
                locateSelf(LOCATION_TYPE_SHOW_AND_MOVE_POSITION);
            }
        }
    }

    public void getLocation(String deviceId) {
        try {
            if (!BaseApplication.getInstances().isSDKConnected()) {
                BaseApplication.getInstances().toast(MainEntryMapActivity.this,"已与手表服务器断开连接");
                return;
            }
            currentCommand = GET_LOCATION;
            JSONObject jsonObject = new JSONObject();
            long devid = Long.parseLong(deviceId, 16);
            jsonObject.put("IMEI", deviceId);
            jsonObject.put("Action", "Get");
            jsonObject.put("Category", "Location");
            BaseApplication.getInstances().getNattyClient().ntyDataRouteClient(devid, jsonObject.toString().getBytes(), jsonObject.toString().length());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //TODO 接收来自广播的数据，刷新并展示相应的位置？由于Map在MainActivity内，所以不知到这么做，西方的导航栏是否会显示，需要确认下
        String deviceId = intent.getStringExtra(BaseConstants.PARAMS_DEVICE_ID);
        double mlat = intent.getDoubleExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, -200);
        double mlng = intent.getDoubleExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, -200);
        if (deviceId != null && deviceId.length() == 15 && mlat != -200 && mlng != -200) {
            int size = deviceEntityList.size();
            for (int i = 0; i < size; i++) {
                if (deviceId.equals(deviceEntityList.get(i).getDeviceid())
                        ) {
                    deviceEntityList.get(i).setLocation(mlng + "," + mlat);
                    clickPosition = i;
                    break;
                }
            }
            mAdapter.setDevices(deviceEntityList);
            mAdapter.notifyDataSetChanged();
            resetMarkerList();
            showMarkerAlarmList(markerList, clickPosition);
            moveCamera(new LatLng(mlat, mlng));
        }
    }


    public void SpecificPoint(Intent intent){
        String deviceId = intent.getStringExtra(BaseConstants.PARAMS_DEVICE_ID);
        double mlat = intent.getDoubleExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, -200);
        double mlng = intent.getDoubleExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, -200);
        if (deviceId != null && deviceId.length() == 15 && mlat != -200 && mlng != -200) {
            int size = deviceEntityList.size();
            for (int i = 0; i < size; i++) {
                if (deviceId.equals(deviceEntityList.get(i).getDeviceid())
                        ) {
                    deviceEntityList.get(i).setLocation(mlng + "," + mlat);
                    deviceEntityList.get(i).setAlarmTime(System.currentTimeMillis());
                    clickPosition = i;
                    BindDeviceHandler.insertValue(deviceEntityList.get(i));
                    break;
                }
            }
            mAdapter.setDevices(deviceEntityList);
            mAdapter.notifyDataSetChanged();
            resetMarkerList();
            showMarkerAlarmList(markerList, clickPosition);
            moveCamera(new LatLng(mlat, mlng));
        }
        startSOSTimer();
    }

    private Timer sosTimer;
    private TimerTask timerTask;
    public void startSOSTimer(){
        if(sosTimer!=null){
            sosTimer.cancel();
            sosTimer = null;
        }
        sosTimer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(FRESH_MARK_LIST);
                sosTimer.cancel();
                sosTimer = null;
            }
        };
        sosTimer.schedule(timerTask,30000);
    }


    @Override
    protected void onResume() {
        super.onResume();
        /*BindDeviceEntity bindDeviceEntity = BindDeviceHandler.getAllValue().get(0);
        */
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);
        BaseApplication.getInstances().setMainEntryMapActivity(this);
        mapView.onResume();
        getNetFamilyBindData();
        /*if(BindDeviceHandler.getAllValue().size()==0) {
            //TODO 需要考虑绑定成功后返回刷新数据的逻辑
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, 1000);
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
        mapView.onPause();
        stopLocateSelf();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    public void testUnwear(){
        try{
            //{"Results":{"IMEI":"355637052238805","Category":"WearStatus","WearStatus":"Off"}}
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObject1 = new JSONObject();

            jsonObject1.put("IMEI","355637052238805");
            jsonObject1.put("Category","WearStatus");
            jsonObject1.put("WearStatus","Off");

            jsonObject.put("Results",jsonObject1);

            NattyProtocolFilter.ntyCommonBroadcastResult(1,jsonObject.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void testSos(){
        try{
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObject1 = new JSONObject();
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("Type","LAB");
            jsonObject2.put("Radius","550");
            jsonObject2.put("Location","113.2451085,23.1325716");

            jsonObject1.put("IMEI","355637052238805");
            jsonObject1.put("Category","SOSReport");
            jsonObject1.put("SOSReport",jsonObject2);

            jsonObject.put("Results",jsonObject1);

            NattyProtocolFilter.ntyCommonBroadcastResult(1,jsonObject.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void testIn(){
        try{
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObject1 = new JSONObject();
            JSONObject jsonObject2 = new JSONObject();
            JSONObject jsonObject3 = new JSONObject();

            jsonObject3.put("Num","0");
            jsonObject3.put("Points",new JSONArray());

            jsonObject2.put("Type","LAB");
            jsonObject2.put("Radius","550");
            jsonObject2.put("Location","113.2451085,23.1325716");
            jsonObject2.put("Bounds","In");
            jsonObject2.put("TimeStamp","2017-04-12 10:47:59");
            jsonObject2.put("Index","3");
            jsonObject2.put("Efence",jsonObject3);

            jsonObject1.put("IMEI","355637052238805");
            jsonObject1.put("Category","EfenceReport");
            jsonObject1.put("EfenceReport",jsonObject2);

            jsonObject.put("Results",jsonObject1);

            NattyProtocolFilter.ntyCommonBroadcastResult(1,jsonObject.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void testOut(){
        try{

            //{"Results":{"IMEI":"355637051751824","Category":"EfenceReport","EfenceReport":{"Type":"WIFI","Radius":"35","Location":"113.24126,23.1322868","Bounds":"Out","TimeStamp":"2017-04-27 11:21:59","Index":"1","Efence":{"Num":"2","Points":["113.242843,23.134359","113.243141,23.129531"]}}}}
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObject1 = new JSONObject();
            JSONObject jsonObject2 = new JSONObject();
            JSONObject jsonObject3 = new JSONObject();

            jsonObject3.put("Num","2");
            JSONArray array = new JSONArray();
            array.put("113.242843,23.134359");
            array.put("113.243141,23.129531");
            jsonObject3.put("Points",array);


            jsonObject2.put("Type","WIFI");
            jsonObject2.put("Radius","35");
            jsonObject2.put("Location","113.24126,23.1322868");
            jsonObject2.put("Bounds","Out");
            jsonObject2.put("TimeStamp","2017-04-27 11:21:59");
            jsonObject2.put("Index","1");
            jsonObject2.put("Efence",jsonObject3);

            jsonObject1.put("IMEI","355637051751824");
            jsonObject1.put("Category","EfenceReport");
            jsonObject1.put("EfenceReport",jsonObject2);

            jsonObject.put("Results",jsonObject1);

            NattyProtocolFilter.ntyCommonBroadcastResult(1,jsonObject.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void toActive(){
        Intent intent = new Intent(MainEntryMapActivity.this, CommonWebEntryActivity_kitkat.class);
        intent.putExtra(BaseConstants.PARAMS_URL, "http://192.168.0.119:8080//familycare/activate?IMEI=355637052203973&version=1");
                    startActivityForResult(intent, CommonRequestCode.REQUEST_STATE);
//        startActivity(intent);
    }

    public void newLocation(){
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


            Common广播定位
         {"Results":{"IMEI":"355637052203973","Category": "LocationReport","LocationReport": {"Type":"WIFI","Radius":"35","Location":"113.2417318,23.1321294"}}}

         {
         "Results": {
         "IMEI": "355637052203973",
         "Category": "LocationReport",
         "LocationReport": {
         "Type": "WIFI",
         "Radius": "35",
         "Location": "113.2417318,23.1321294"
         }
         }
         }
         */
        try{
            //TODO 专门的定位广播
//            JSONObject parent = new JSONObject();
//            JSONObject child = new JSONObject();
//            child.put("Type","WIFI");
//            child.put("Radius","35");
//            child.put("Location","113.1415614,23.2322708");
//            child.put("Category","WIFI");
//            child.put("IMEI","355637052203973");
//
//            parent.put("Results",child);
//
//            NattyProtocolFilter.ntyProtocolLocationResult(0,parent.toString());

            //TODO 公共广播 定位
            /**
             {
             "Results": {
             "IMEI": "355637052203973",
             "Category": "LocationReport",
             "LocationReport": {
             "Type": "WIFI",
             "Radius": "35",
             "Location": "113.2417318,23.1321294"
             }
             }
             }
             */
            JSONObject parent = new JSONObject();
            JSONObject child = new JSONObject();
            JSONObject sub = new JSONObject();

            sub.put("Type","WIFI");
            sub.put("Radius","35");
            sub.put("Location","113.1415614,23.2322708");

            child.put("Type","WIFI");
            child.put("Category","LocationReport");
            child.put("IMEI","355637052203973");
            child.put("LocationReport",sub);

            parent.put("Results",child);

            NattyProtocolFilter.ntyCommonBroadcastResult(0,parent.toString());


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title: {

//                testUnwear();
//                testSos();
//                testOut();
//                testIn();
//                toActive();

                newLocation();
                break;
            }
            case R.id.ibtn_back: {
                if (BaseApplication.getInstances().getMainActivity() != null) {
                    BaseApplication.getInstances().getMainActivity().toggleMenu();
                }
                break;
            }
            case R.id.ibtn_menu: {

                break;
            }
            case R.id.menu_text: {
                if (watchData != null && watchData.size() < 8) {
                    Intent intent = new Intent(this, DevicesBindActivity.class);
                    intent.putExtra(DevicesBindActivity.TYPE,"child");
                    startActivityForResult(intent, REQUEST_BIND);
                } else {
                    Toast.makeText(this, "绑定数量已达上限!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    //**************************
    public void getAddressByLatlng(final LatLng latlng) {
        LatLonPoint latLonPoint = new LatLonPoint(latlng.latitude, latlng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 100,
                GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    public void getLatLngByAddress(final String province, final String city, final String section) {

    }

    //**************************
    //InfoWindow

    private TextView info_location;
    private TextView phone;
    private ImageView call_phone;
    private LinearLayout guide_line;//导航

    @Override
    public View getInfoWindow(final Marker marker) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_watch_child_infowindow, null);
        info_location = (TextView) view.findViewById(R.id.info_location);
        info_location.setTag(null);
        info_location.setText("");
        phone = (TextView) view.findViewById(R.id.phone);
        call_phone = (ImageView) view.findViewById(R.id.call_phone);
        guide_line = (LinearLayout) view.findViewById(R.id.guide_line);
        //TODO 临时对位置进行查询
        getAddressByLatlng(marker.getPosition());
        phone.setText("电话:" + deviceEntityList.get(Integer.parseInt(marker.getTitle())).getPhoneNumber());
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
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_watch_child_infowindow, null);
        info_location = (TextView) view.findViewById(R.id.info_location);
        info_location.setTag(null);
        info_location.setText("");
        phone = (TextView) view.findViewById(R.id.phone);
        call_phone = (ImageView) view.findViewById(R.id.call_phone);
        guide_line = (LinearLayout) view.findViewById(R.id.guide_line);
        //TODO 临时对位置进行查询
        getAddressByLatlng(marker.getPosition());
        phone.setText("电话:" + deviceEntityList.get(Integer.parseInt(marker.getTitle())).getPhoneNumber());
        call_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deviceEntityList.get(Integer.parseInt(marker.getTitle())).getPhoneNumber()!=null&&deviceEntityList.get(Integer.parseInt(marker.getTitle())).getPhoneNumber().length()>0){
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone.getText().toString().substring(phone.getText().toString().indexOf(":") + 1)));
                    startActivity(intent);
                }else {
                    showSetNumDialog();
                }
                
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

    private void showSetNumDialog() {
        
    }

    public void openGudieDialog(final LatLng latLng) {

        final Dialog selectNaviDialog = QuanjiakanDialog.getInstance().getCardDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_select_navimap, null);

        //判断是否安装高德地图,处理点击事件
        if (NaviMapUtil.isAvilible(this, "com.autonavi.minimap")) {
            TextView gaodeNavi = (TextView) view.findViewById(R.id.tv_gaodenavi);
            View view1 = view.findViewById(R.id.line1);
            view1.setVisibility(View.VISIBLE);
            gaodeNavi.setVisibility(View.VISIBLE);
            gaodeNavi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (selfLatitude != -1000 && selfLongitude != -1000) {
                        NaviMapUtil.GotoGaoDeNaviMap(MainEntryMapActivity.this, "全家康用户端", selfLatitude + "", selfLongitude + "", selfAdress, latLng.latitude + "", latLng.longitude + "",
                                (info_location.getTag() != null ? info_location.getTag().toString() : ""), "1", "0", "2");
                    } else {
                        BaseApplication.getInstances().toast(MainEntryMapActivity.this,"暂未获取到自己的定位信息,请稍后重试!");
                        locateSelf(LOCATION_TYPE_GET_POSITION);
                    }
                    selectNaviDialog.dismiss();
                }

            });
        }

        //判断是否安装百度地图,并处理点击事件
        if (NaviMapUtil.isAvilible(this, "com.baidu.BaiduMap")) {
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
                        NaviMapUtil.GotoBaiDuNaviMap(MainEntryMapActivity.this, selfBdlat + "," + selfBdlon, patitentBdLat + "," + patitentBdLon, "driving", null, null, null, null, null, "thirdapp.navi." + "巨硅科技" + R.string.app_name);
                    } else {
                        BaseApplication.getInstances().toast(MainEntryMapActivity.this,"暂未获取到自己的定位信息,请稍后重试!");
                    }
                    selectNaviDialog.dismiss();
                }
            });

        }

        //没有百度和高德地图
        if (!(NaviMapUtil.isAvilible(this, "com.baidu.BaiduMap") || NaviMapUtil.isAvilible(this, "com.autonavi.minimap"))) {
            TextView no_map = (TextView) view.findViewById(R.id.tv_no_map);
            View view3 = view.findViewById(R.id.line3);
            view3.setVisibility(View.VISIBLE);
            no_map.setVisibility(View.VISIBLE);
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
        params.width = UnitUtil.dp_To_px(this, 300);
        params.height = params.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;

        selectNaviDialog.setContentView(view, params);
        selectNaviDialog.setCanceledOnTouchOutside(false);
        selectNaviDialog.show();

    }

    //**************************
    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    //**************************
    @Override
    public void onMapLoaded() {

    }

    //**************************
    //点击标记

    private static final int REQUEST_ENTRY = 10020;

    private static final int REQUEST_BIND = 10021;

    @Override
    public boolean onMarkerClick(Marker marker) {
//        Toast.makeText(this, "marker click:"+marker.getTitle(), Toast.LENGTH_SHORT).show();

        if ("1".equals(deviceEntityList.get(Integer.parseInt(marker.getTitle())).getW_TYPE())) {
            Intent intent = new Intent(this, WatchChildEntryActivity.class);
//        intent.putExtra(WatchChildEntryActivity.PARAMS_DEVICE_ID, Long.toHexString(Long.parseLong(deviceEntityList.get(Integer.parseInt(marker.getTitle())).getIMEI())));//TODO 收到10进制，转16进制
            intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, deviceEntityList.get(Integer.parseInt(marker.getTitle())).getDeviceid());//TODO 收到16进制
            intent.putExtra(BaseConstants.PARAMS_DEVICE_PHONE, deviceEntityList.get(Integer.parseInt(marker.getTitle())).getPhoneNumber());
            intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, marker.getPosition().latitude);
            intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, marker.getPosition().longitude);
            intent.putExtra(BaseConstants.PARAMS_DEVICE_TYPE, deviceEntityList.get(Integer.parseInt(marker.getTitle())).getW_TYPE());
            intent.putExtra(BaseConstants.PARAMS_DEVICE_IMAGEPATH, deviceEntityList.get(Integer.parseInt(marker.getTitle())).getIcon());
            intent.putExtra(BaseConstants.PARAMS_DEVICE_NAME, deviceEntityList.get(Integer.parseInt(marker.getTitle())).getName());
            startActivityForResult(intent, REQUEST_ENTRY);
        } else {
            Intent intent = new Intent(this, WatchOldEntryActivity.class);
//        intent.putExtra(WatchOldEntryActivity.PARAMS_DEVICE_ID, Long.toHexString(Long.parseLong(deviceEntityList.get(Integer.parseInt(marker.getTitle())).getIMEI())));//TODO 收到10进制，转16进制
            intent.putExtra(BaseConstants.PARAMS_DEVICE_ID, deviceEntityList.get(Integer.parseInt(marker.getTitle())).getDeviceid());//TODO 收到16进制
            intent.putExtra(BaseConstants.PARAMS_DEVICE_PHONE, deviceEntityList.get(Integer.parseInt(marker.getTitle())).getPhoneNumber());
            intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, marker.getPosition().latitude);
            intent.putExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, marker.getPosition().longitude);
            intent.putExtra(BaseConstants.PARAMS_DEVICE_TYPE, deviceEntityList.get(Integer.parseInt(marker.getTitle())).getW_TYPE());
            intent.putExtra(BaseConstants.PARAMS_DEVICE_IMAGEPATH, deviceEntityList.get(Integer.parseInt(marker.getTitle())).getIcon());
            intent.putExtra(BaseConstants.PARAMS_DEVICE_NAME, deviceEntityList.get(Integer.parseInt(marker.getTitle())).getName());
            startActivityForResult(intent, REQUEST_ENTRY);
        }
        return true;
    }

    //**************************
    //拖动标记
    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    //**************************
    // 地理位置查询
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
//                mAddress = addressName.replace(province,"").replace(district,"").replace(city,"");
                info_location.setText("地址:" + addressName.replace(province, ""));
                info_location.setTag(addressName.replace(province, ""));
            } else {
            }
        } else {
            BaseApplication.getInstances().showerror(this, rCode);
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
    //**************************


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_BIND:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        resetDefaultDevice();
                        int type = data.getIntExtra(WatchChildEntryActivity.RESULT_TYPE, -1);
                        if (type != WatchChildEntryActivity.REQUEST_UNBIND) {
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //TODO 更新
                                    String deviceID = data.getStringExtra(BaseConstants.PARAMS_DEVICE_ID);
                                    String point = data.getStringExtra(BaseConstants.PARAMS_DEVICE_LOCATION_POINT);
                                    double lat = data.getDoubleExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, -200);
                                    double lon = data.getDoubleExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, -200);
                                    if (deviceID == null || (lat == -200 || lon == -200)) {
                                        //TODO 由于传入的参数出现问题，此处不进行处理
                                    } else {
                                        //TODO 传入正常的参数，所以根据返回的ID 变更对应的信息
                                        //TODO 需要将地图部分流程规划完整了，才好配合更新
                                        if (point != null && point.length() > 0 && point.contains(",")) {
                                            int size = deviceEntityList.size();
                                            for (int i = 0; i < size; i++) {
                                                if (deviceID.equals(deviceEntityList.get(i).getDeviceid())) {
                                                    deviceEntityList.get(i).setLocation(point);

                                                    mAdapter.setDevices(deviceEntityList);
                                                    mAdapter.notifyDataSetChanged();

                                                    resetMarkerList();

                                                    if (deviceEntityList.size() > 0) {
                                                        moveCamera(markerList.get(clickPosition));
                                                    } else {
                                                        locateSelf(LOCATION_TYPE_SHOW_AND_MOVE_POSITION);
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }, 4000);
                        }
                    }
                }
                break;
            case REQUEST_ENTRY:
                resetDefaultDevice();
                if (resultCode == RESULT_OK) {
                    //TODO 更新该点的位置信息
                    int type = data.getIntExtra(WatchChildEntryActivity.RESULT_TYPE, -1);
                    if (type == WatchChildEntryActivity.REQUEST_UNBIND) {
                        //TODO 重新获取用户的绑定信息  在onResume里面做啦
                    } else {
                        //TODO 更新
                        String deviceID = data.getStringExtra(BaseConstants.PARAMS_DEVICE_ID);
                        String point = data.getStringExtra(BaseConstants.PARAMS_DEVICE_LOCATION_POINT);
                        double lat = data.getDoubleExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LAT, -200);
                        double lon = data.getDoubleExtra(BaseConstants.PARAMS_DEVICE_LOCATION_LNG, -200);
                        if (deviceID == null || (lat == -200 || lon == -200)) {
                            //TODO 由于传入的参数出现问题，此处不进行处理
                        } else {
                            //TODO 传入正常的参数，所以根据返回的ID 变更对应的信息
                            //TODO 需要将地图部分流程规划完整了，才好配合更新

                            if (point != null && point.length() > 0 && point.contains(",")) {
                                int size = deviceEntityList.size();
                                for (int i = 0; i < size; i++) {
                                    if (deviceID.equals(deviceEntityList.get(i).getDeviceid())) {
                                        deviceEntityList.get(i).setLocation(point);

                                        mAdapter.setDevices(deviceEntityList);
                                        mAdapter.notifyDataSetChanged();

                                        resetMarkerList();

                                        if (deviceEntityList.size() > 0) {
                                            moveCamera(markerList.get(clickPosition));
                                        } else {
                                            locateSelf(LOCATION_TYPE_SHOW_AND_MOVE_POSITION);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocateSelf();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    private Dialog tipsDialog;
    private final String keyTips = BaseApplication.getInstances().getUser_id() + "EntryMapTips";

    public void showTipsDialog() {

        BaseApplication.getInstances().setKeyNumberValue("bind_flag",0);
        if (BaseApplication.getInstances().getKeyValue(keyTips) != null &&
                BaseApplication.getInstances().getKeyValue(keyTips).length() > 0
                ) {

        } else {
            tipsDialog = new Dialog(this, R.style.dialog_loading);
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_main_map_entry_tips, null);
            ImageView exit = (ImageView) view.findViewById(R.id.exit);
            final CheckBox checkBox = (CheckBox) view.findViewById(R.id.check_status);
            TextView btn_confirm = (TextView) view.findViewById(R.id.btn_confirm);
            btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tipsDialog != null) {
                        Intent intent = new Intent(MainEntryMapActivity.this, DevicesBindActivity.class);
                        intent.putExtra(DevicesBindActivity.TYPE,"child");
                        startActivityForResult(intent, REQUEST_BIND);
                        tipsDialog.dismiss();
                    }
                    if (checkBox.isChecked()) {
                        BaseApplication.getInstances().setKeyValue(keyTips, keyTips);
                    }
                }
            });
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tipsDialog != null) {
                        tipsDialog.dismiss();
                    }
                    if (checkBox.isChecked()) {
                        BaseApplication.getInstances().setKeyValue(keyTips, keyTips);
                    }
                }
            });

            WindowManager.LayoutParams lp = tipsDialog.getWindow().getAttributes();
            lp.width = QuanjiakanUtil.dip2px(this, 300);
            lp.height = lp.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            tipsDialog.setContentView(view, lp);
            tipsDialog.setCanceledOnTouchOutside(false);
            tipsDialog.show();
        }
    }


    @Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onCommonNattyData(CommonNattyData msg){
        switch (msg.getType()) {//
            case NattyProtocolFilter.DISPLAY_UPDATE_DATA_COMMON_BROADCAST:
                //{"IMEI":"355637053995130","Category": "BindConfirmReq","Proposer":"","Answer":"Agree"}}
                //TODO 同意绑定的请求
                String stringData = msg.getData();
                try {
                    if (stringData != null && (stringData.contains("BindConfirmReq") || stringData.contains("BindConfirm")) && stringData.contains(BaseApplication.getInstances().getUsername())) {
                        if ((stringData.contains("Agree") || stringData.toLowerCase().contains("agree"))) {
                            getNetFamilyBindData();//同意绑定则更新信息
                        } else if ((stringData.contains("Reject") || stringData.toLowerCase().contains("reject"))) {

                        }
                    }
                    /**
                     {
                         "Results": {
                             "IMEI": "355637052788650",
                             "Category": "LocationReport",
                             "LocationReport": {
                                 "Type": "WIFI",
                                 "Radius": "550",
                                 "Location": "113.2409402,23.1326885",
                             }
                         }
                     }
                     */
                    //TODO 收到定位广播
                    else if(stringData != null && stringData.contains("Results")
                            && stringData.contains("IMEI")
                            && stringData.contains("LocationReport")){
                        JSONObject jsonObject = new JSONObject(stringData);
                        //TODO 判断含有经纬度字算，并且，该字段是有效字段【由于给的数据实际中是存在无效字段的，所有需要判断有效性】
                        if(jsonObject.has("Results")
                                && jsonObject.getJSONObject("Results")!=null

                                && jsonObject.getJSONObject("Results").has("IMEI")
                                && jsonObject.getJSONObject("Results").getString("IMEI")!=null

                                && jsonObject.getJSONObject("Results").has("LocationReport")
                                && jsonObject.getJSONObject("Results").getJSONObject("LocationReport")!=null

                                && jsonObject.getJSONObject("Results").getJSONObject("LocationReport").has("Type")
                                && jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Type")!=null

                                && jsonObject.getJSONObject("Results").getJSONObject("LocationReport").has("Location")
                                && jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Location")!=null
                                && jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Location").length()>3
                                && jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Location").contains(",")){
                            int size = deviceEntityList.size();
                            for (int i = 0; i < size; i++) {
                                if (jsonObject.getJSONObject("Results").getString("IMEI").equals(deviceEntityList.get(i).getDeviceid())
                                        ) {
                                    //针对GPS进行转换
//                                    if("gps".equals(jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Type").toLowerCase())){
//                                        HashMap<String, Double> latlngMap = GPSToAMap.gpsToAmapPoint(jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Location"));
//                                        double tempmLon = latlngMap.get("lon");
//                                        double tempmLat = latlngMap.get("lat");
//                                        deviceEntityList.get(i).setLocation(tempmLon+","+tempmLat);
//                                    }else{
//                                        deviceEntityList.get(i).setLocation(jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Location"));
//                                    }
                                    //未针对GPS进行转换
                                    deviceEntityList.get(i).setLocation(jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Location"));
                                    break;
                                }
                            }

                            mAdapter.setDevices(deviceEntityList);
                            mAdapter.notifyDataSetChanged();

                            resetMarkerList();
                            if (markerList != null && markerList.size() > 0) {
                                showMarkerList(markerList, clickPosition);
                            }

                            if (deviceEntityList.size() > 0) {
                                moveCamera(markerList.get(clickPosition));
                            } else {
                                locateSelf(LOCATION_TYPE_SHOW_AND_MOVE_POSITION);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
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

                        && result.getResults().getType()!=null

                        && result.getResults().getLocation() != null
                        && result.getResults().getLocation().length() > 3
                        && result.getResults().getLocation().contains(",")
                        ) {
                    int size = deviceEntityList.size();
                    for (int i = 0; i < size; i++) {
                        if (result.getResults().getIMEI().equals(deviceEntityList.get(i).getDeviceid())
                                ) {
//                            if("gps".equals(result.getResults().getType().toLowerCase()) ){
//                                HashMap<String, Double> latlngMap = GPSToAMap.gpsToAmapPoint(result.getResults().getLocation());
//                                double tempmLon = latlngMap.get("lon");
//                                double tempmLat = latlngMap.get("lat");
//                                deviceEntityList.get(i).setLocation(tempmLon+","+tempmLat);
//                            }else{
//                                deviceEntityList.get(i).setLocation(result.getResults().getLocation());
//                            }

                            deviceEntityList.get(i).setLocation(result.getResults().getLocation());
                            //GPS 定位需要转一下
//                            WGSTOGCJ02 mGCJ02 = new WGSTOGCJ02();
//                            HashMap<String, Double> latlngMap = (HashMap<String, Double>) mGCJ02.transform
//                                    (((StateActivity_Handler_Child) getActivity()).mLon,
//                                            ((StateActivity_Handler_Child) getActivity()).mLat);
//                            ((StateActivity_Handler_Child) getActivity()).mLon = latlngMap.get("lon");
//                            ((StateActivity_Handler_Child) getActivity()).mLat = latlngMap.get("lat");
                            break;
                        }
                    }
                    mAdapter.setDevices(deviceEntityList);
                    mAdapter.notifyDataSetChanged();

                    resetMarkerList();
                    if (markerList != null && markerList.size() > 0) {
                        showMarkerList(markerList, clickPosition);
                    }

//                    if (currentCommand == MainEntryMapActivity.GET_LOCATION) {
                        if (deviceEntityList.size() > 0) {
                            moveCamera(markerList.get(clickPosition));
                            getAddressByLatlng(markerList.get(clickPosition));
                        } else {
                            locateSelf(LOCATION_TYPE_SHOW_AND_MOVE_POSITION);
                        }
//                    }
                }
                break;
            }
            case NattyProtocolFilter.DISPLAY_UPDATE_DATA_RESULT: {
                //需要自己根据发送的命令去判断
                String data = msg.getData();
                switch (currentCommand) {
                    case MainEntryMapActivity.GET_LOCATION: {
                        WatchCommonResult result = (WatchCommonResult) SerialUtil.jsonToObject(data, new TypeToken<WatchCommonResult>() {
                        }.getType());
                        if (result != null && "200".equals(result.getResult().getCode())) {
//                                BaseApplication.getInstances().toast(MainEntryMapActivity.this,"获取地址成功");
                        } else if (result != null && "10001".equals(result.getResult().getCode())) {
                            BaseApplication.getInstances().toast(MainEntryMapActivity.this,"设备不在线");
//                            locateSelf(LOCATION_TYPE_SHOW_AND_MOVE_POSITION);
                        } else {
//                                BaseApplication.getInstances().toast(MainEntryMapActivity.this,"获取地址失败");
                        }
                        break;
                    }
                }
                break;
            }
            case NattyProtocolFilter.DISPLAY_UPDATE_DATA_ROUTE: {
                //需要自己根据发送的命令去判断
                String data = msg.getData();
                switch (currentCommand) {
                    case MainEntryMapActivity.GET_LOCATION: {
                        //{"results":{"IMEI":"352315052834187","Category": "Location","Type":"WIFI","Radius":"35","Location":"113.241652,23.1320961"}}}
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

                                int size = deviceEntityList.size();
                                for (int i = 0; i < size; i++) {
                                    if (json.getJSONObject("results").getString("IMEI").equals(deviceEntityList.get(i).getDeviceid())
                                            ) {
//                                        if("gps".equals(json.getJSONObject("results").getString("Type").toLowerCase()) ){
//                                            HashMap<String, Double> latlngMap = GPSToAMap.gpsToAmapPoint(json.getJSONObject("results").getString("Location"));
//                                            double tempmLon = latlngMap.get("lon");
//                                            double tempmLat = latlngMap.get("lat");
//                                            deviceEntityList.get(i).setLocation(tempmLon+","+tempmLat);
//                                        }else{
//                                            deviceEntityList.get(i).setLocation(json.getJSONObject("results").getString("Location"));
//                                        }

                                        deviceEntityList.get(i).setLocation(json.getJSONObject("results").getString("Location"));
                                        break;
                                    }
                                }

                                mAdapter.setDevices(deviceEntityList);
                                mAdapter.notifyDataSetChanged();

                                resetMarkerList();
                                if (markerList != null && markerList.size() > 0) {
                                    showMarkerList(markerList, clickPosition);
                                }

                                if (deviceEntityList.size() > 0) {
                                    moveCamera(markerList.get(clickPosition));
                                } else {
                                    locateSelf(LOCATION_TYPE_SHOW_AND_MOVE_POSITION);
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
                                int size = deviceEntityList.size();
                                for (int i = 0; i < size; i++) {
                                    if (json.getJSONObject("Results").getString("IMEI").equals(deviceEntityList.get(i).getDeviceid())) {


//                                        if("gps".equals(json.getJSONObject("Results").getString("Type").toLowerCase()) ){
//                                            HashMap<String, Double> latlngMap = GPSToAMap.gpsToAmapPoint(json.getJSONObject("Results").getString("Location"));
//                                            double tempmLon = latlngMap.get("lon");
//                                            double tempmLat = latlngMap.get("lat");
//                                            deviceEntityList.get(i).setLocation(tempmLon+","+tempmLat);
//                                        }else{
//                                            deviceEntityList.get(i).setLocation(json.getJSONObject("Results").getString("Location"));
//                                        }


                                        deviceEntityList.get(i).setLocation(json.getJSONObject("Results").getString("Location"));
                                        BindDeviceEntity temp = deviceEntityList.get(i);
                                        BindDeviceHandler.insertValue(temp);
                                        break;
                                    }
                                }
                                mAdapter.setDevices(deviceEntityList);
                                mAdapter.notifyDataSetChanged();

//                                    String[] posi = json.getJSONObject("Results").getString("Location").split(",");
//                                    double lat = Double.parseDouble(posi[1]);
//                                    double lon = Double.parseDouble(posi[0]);
//                                    moveCamera(new LatLng(lat,lon));

                                resetMarkerList();

                                if (markerList != null && markerList.size() > 0) {
                                    showMarkerList(markerList, clickPosition);
                                }

                                if (deviceEntityList.size() > 0) {
                                    moveCamera(markerList.get(clickPosition));
                                    getAddressByLatlng(markerList.get(clickPosition));
                                } else {
                                    locateSelf(LOCATION_TYPE_SHOW_AND_MOVE_POSITION);
                                }
                            } else {
                                locateSelf(LOCATION_TYPE_SHOW_AND_MOVE_POSITION);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
                break;
            }
        }
    }

    @Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onCommonVoiceData(CommonVoiceData msg) {
        switch (msg.getType()) {
            case NattyProtocolFilter.DISPLAY_VOICE_PLAY: {
                mAdapter.notifyDataSetChanged();
            }
            break;
        }


    }
}
