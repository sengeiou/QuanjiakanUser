package com.androidquanjiakan.activity.index.watch_child;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.entity.CommonNattyData;
import com.androidquanjiakan.entity.WatchLocationBroadcase;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.util.LogUtil;
import com.google.gson.reflect.TypeToken;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;
import com.wbj.ndk.natty.client.NattyProtocolFilter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.ArrayList;

public class WatchChildFreshLocationFragment_gpstoAmap extends Fragment implements OnClickListener, LocationSource,
        AMapLocationListener, AMap.OnMapClickListener, GeocodeSearch.OnGeocodeSearchListener, AMap.InfoWindowAdapter, AMap.OnMarkerClickListener {

    public static String PARAMS_DEVICE_TYPE = "deviceType";
    public static String PARAMS_DEVICE_LAT = "devicelat";
    public static String PARAMS_DEVICE_LNG = "devicelng";

    private String device_id;
    private String device_type;

    MapView mapView;
    TextView location;
    Bundle savedInstanceStates;

    private AMap aMap;
    private GeocodeSearch geocoderSearch;

    private double lat;
    private double lng;

    ArrayList<BitmapDescriptor> iconList = new ArrayList<>();


    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watch_child_fresh_locate_new, null);
        initView(view);
        initMap(savedInstanceState);
        afterCreate();
        return view;
    }

    public void initView(View parent) {
        /*
    @BindView(R.id.ibtn_back)
    ImageButton ibtnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.location)
    TextView location;
         */
        mapView = (MapView) parent.findViewById(R.id.map);
        location = (TextView) parent.findViewById(R.id.location);
        location.setOnClickListener(this);
    }

    protected void afterCreate() {
        initTitleBar();

        if (lat != -200 && lng != -200) {
            moveCamera(new LatLng(lat, lng));
            showMarker(new LatLng(lat, lng));
            getAddressByLatlng(new LatLng(lat, lng));
        } else {

        }
        getLocation();
    }

    public void getLocation() {
        try {
            if (!BaseApplication.getInstances().isSDKConnected()) {
                LogUtil.e("SDK 断联");
                BaseApplication.getInstances().toast(getActivity(), "已与手表服务器断开连接");
                return;
            }
            JSONObject jsonObject = new JSONObject();
            long devid = Long.parseLong(device_id, 16);
            jsonObject.put("IMEI", device_id);
            jsonObject.put("Action", "Get");
            jsonObject.put("Category", "Location");
            LogUtil.e("Location:" + jsonObject.toString());
            BaseApplication.getInstances().getNattyClient().ntyDataRouteClient(devid, jsonObject.toString().getBytes(), jsonObject.toString().length());
            LogUtil.e("SDK 配置");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showMarker(LatLng latlng) {
        if (aMap != null) {
            if ("1".equals(device_type)) {
                if (iconList == null || iconList.size() < 1) {
                    iconList = new ArrayList<>();
                } else {
                    iconList.clear();
                }
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_1));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_2));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_3));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_4));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_5));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_6));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_7));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_8));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_9));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_10));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_11));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_12));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_13));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_14));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_15));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_child_16));
            } else {
                if (iconList == null || iconList.size() < 1) {
                    iconList = new ArrayList<>();
                } else {
                    iconList.clear();
                }
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_1));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_2));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_3));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_4));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_5));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_6));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_7));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_8));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_9));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_10));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_11));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_12));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_13));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_14));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_15));
                iconList.add(BitmapDescriptorFactory.fromResource(R.drawable.map_location_old_16));
            }
            aMap.clear();

            Marker marker = aMap.addMarker(new MarkerOptions()
                    .position(latlng)
                    .title("1")
                    .period(1)
                    .icons(iconList)
                    .draggable(false));
            moveCamera(latlng);
        }
    }

    private double selfLatitude = -1000;
    private double selfLongitude = -1000;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    public void locateSelfPosition() {
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
                    mlocationClient = new AMapLocationClient(getActivity());
                    mLocationOption = new AMapLocationClientOption();
                    //设置定位监听
                    mlocationClient.setLocationListener(new AMapLocationListener() {
                        @Override
                        public void onLocationChanged(AMapLocation aMapLocation) {

                            if (mListener != null && aMapLocation != null) {

                                if (aMapLocation != null
                                        && aMapLocation.getErrorCode() == 0) {
                                    mListener.onLocationChanged(aMapLocation);

                                    selfLatitude = aMapLocation.getLatitude();
                                    selfLongitude = aMapLocation.getLongitude();
                                    stopLocateSelf();
                                    moveCamera(new LatLng(selfLatitude, selfLongitude));
                                }
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

    public void stopLocateSelf() {
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    private final int SOLID_ZOOM = 16;

    protected void moveCamera(LatLng point) {
        if (point == null) {
            return;
        }
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, SOLID_ZOOM));
    }

    public void initTitleBar() {
//        ibtnBack.setVisibility(View.VISIBLE);
//        ibtnBack.setOnClickListener(this);
//
//        tvTitle.setText("地图");

    }

    public void initMap(Bundle savedInstanceState) {
        long timeRecord = System.currentTimeMillis();
        mapView.onCreate(savedInstanceState);
        LogUtil.w("ChildLocation mapView.onCreate time Use:" + (System.currentTimeMillis() - timeRecord) / 1000 + "Second");

//        if (aMap == null) {
//
//        }
        aMap = mapView.getMap();

        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setCompassEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.setInfoWindowAdapter(this);
        aMap.setOnMarkerClickListener(this);

        geocoderSearch = new GeocodeSearch(getActivity());
        geocoderSearch.setOnGeocodeSearchListener(this);
    }

    public void getAddressByLatlng(final LatLng latlng) {
        LatLonPoint latLonPoint = new LatLonPoint(latlng.latitude, latlng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 100,
                GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(getActivity());
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(getActivity());
        mapView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(getActivity());
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
        mapView.onPause();
        deactivate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
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
//                Intent intent = new Intent();
//                intent.putExtra(PARAMS_DEVICE_LAT,lat);
//                intent.putExtra(PARAMS_DEVICE_LNG,lng);
//                setResult(RESULT_OK,intent);
//                finish();
                break;
            case R.id.location:
//                newLocation();
                break;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

    }

    @Override
    public View getInfoWindow(Marker marker) {
        marker.hideInfoWindow();
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        marker.hideInfoWindow();
        return null;
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
        } else {
            marker.hideInfoWindow();
        }
        return true;
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

    }

    @Override
    public void deactivate() {

    }

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
                location.setText("最后定位地址:" + addressName.replace(province, ""));
//                LogUtil.e("经纬度解析成功!");
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

    @Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onCommonNattyData(CommonNattyData msg) {
        switch (msg.getType()) {
            case NattyProtocolFilter.DISPLAY_UPDATE_DATA_COMMON_BROADCAST:
                //{"IMEI":"355637053995130","Category": "BindConfirmReq","Proposer":"","Answer":"Agree"}}
                //TODO 同意绑定的请求
                String stringData = msg.getData();
                try {
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
                    if (stringData != null && stringData.contains("Results")
                            && stringData.contains("IMEI")
                            && stringData.contains("LocationReport")) {
                        JSONObject jsonObject = new JSONObject(stringData);
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
//                                if("gps".equals(jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Type").toLowerCase())){
//                                    HashMap<String, Double> latlngMap = GPSToAMap.gpsToAmapPoint(jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Location"));
//                                    lng = latlngMap.get("lon");
//                                    lat = latlngMap.get("lat");
//
//                                    moveCamera(new LatLng(lat, lng));
//                                    showMarker(new LatLng(lat, lng));
//                                    getAddressByLatlng(new LatLng(lat, lng));
//                                }else{
//                                    String[] strings = jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Location").split(",");
//                                    lng = Double.parseDouble(strings[1]);
//                                    lat = Double.parseDouble(strings[0]);
//
//                                    moveCamera(new LatLng(lat, lng));
//                                    showMarker(new LatLng(lat, lng));
//                                    getAddressByLatlng(new LatLng(lat, lng));
//                                }

                                String[] strings = jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Location").split(",");
                                lng = Double.parseDouble(strings[1]);
                                lat = Double.parseDouble(strings[0]);

                                moveCamera(new LatLng(lat, lng));
                                showMarker(new LatLng(lat, lng));
                                getAddressByLatlng(new LatLng(lat, lng));
                                break;
                            }
                        }
                    } else if (stringData != null && stringData.contains("Results")
                            && stringData.contains("IMEI")
                            && stringData.contains("LocationReport")) {
                        JSONObject jsonObject = new JSONObject(stringData);
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
//                                        lng = latlngMap.get("lon");
//                                        lat = latlngMap.get("lat");
//
//                                        moveCamera(new LatLng(lat, lng));
//                                        showMarker(new LatLng(lat, lng));
//                                        getAddressByLatlng(new LatLng(lat, lng));
//                                    } else {
//                                        String[] strings = jsonObject.getJSONObject("Results").getJSONObject("LocationReport").getString("Location").split(",");
//                                        lng = Double.parseDouble(strings[1]);
//                                        lat = Double.parseDouble(strings[0]);
//
//                                        moveCamera(new LatLng(lat, lng));
//                                        showMarker(new LatLng(lat, lng));
//                                        getAddressByLatlng(new LatLng(lat, lng));
//                                    }


                                    lat = Double.parseDouble(location.split(",")[1]);
                                    lng = Double.parseDouble(location.split(",")[0]);
                                    moveCamera(new LatLng(lat, lng));
                                    showMarker(new LatLng(lat, lng));
                                    getAddressByLatlng(new LatLng(lat, lng));
                                } else {
                                    locateSelfPosition();
                                }

                                break;
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case NattyProtocolFilter.DISPLAY_UPDATE_DATA_RESULT: {

                break;
            }
            case NattyProtocolFilter.DISPLAY_UPDATE_DATA_ROUTE: {
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

//                                if ("gps".equals(json.getJSONObject("results").getString("Type").toLowerCase())) {
//                                    HashMap<String, Double> latlngMap = GPSToAMap.gpsToAmapPoint(location);
//                                    lng = latlngMap.get("lon");
//                                    lat = latlngMap.get("lat");
//
//                                    moveCamera(new LatLng(lat, lng));
//                                    showMarker(new LatLng(lat, lng));
//                                    getAddressByLatlng(new LatLng(lat, lng));
//                                } else {
//                                    lat = Double.parseDouble(location.split(",")[1]);
//                                    lng = Double.parseDouble(location.split(",")[0]);
//
//                                    moveCamera(new LatLng(lat, lng));
//                                    showMarker(new LatLng(lat, lng));
//                                    getAddressByLatlng(new LatLng(lat, lng));
//                                }


                                lat = Double.parseDouble(location.split(",")[1]);
                                lng = Double.parseDouble(location.split(",")[0]);
                                moveCamera(new LatLng(lat, lng));
                                showMarker(new LatLng(lat, lng));
                                getAddressByLatlng(new LatLng(lat, lng));
                            } else {
                                locateSelfPosition();
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

//                                if ("gps".equals(json.getJSONObject("Results").getString("Type").toLowerCase())) {
//                                    HashMap<String, Double> latlngMap = GPSToAMap.gpsToAmapPoint(location);
//                                    lng = latlngMap.get("lon");
//                                    lat = latlngMap.get("lat");
//
//                                    moveCamera(new LatLng(lat, lng));
//                                    showMarker(new LatLng(lat, lng));
//                                    getAddressByLatlng(new LatLng(lat, lng));
//                                } else {
//                                    lat = Double.parseDouble(location.split(",")[1]);
//                                    lng = Double.parseDouble(location.split(",")[0]);
//
//                                    moveCamera(new LatLng(lat, lng));
//                                    showMarker(new LatLng(lat, lng));
//                                    getAddressByLatlng(new LatLng(lat, lng));
//                                }

                                lat = Double.parseDouble(location.split(",")[1]);
                                lng = Double.parseDouble(location.split(",")[0]);
                                moveCamera(new LatLng(lat, lng));
                                showMarker(new LatLng(lat, lng));
                                getAddressByLatlng(new LatLng(lat, lng));
                            } else {
                                locateSelfPosition();
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
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
//                                lng = latlngMap.get("lon");
//                                lat = latlngMap.get("lat");
//
//                                moveCamera(new LatLng(lat, lng));
//                                showMarker(new LatLng(lat, lng));
//                                getAddressByLatlng(new LatLng(lat, lng));
//                            } else {
//                                lat = Double.parseDouble(location.split(",")[1]);
//                                lng = Double.parseDouble(location.split(",")[0]);
//                                moveCamera(new LatLng(lat, lng));
//                                showMarker(new LatLng(lat, lng));
//                                getAddressByLatlng(new LatLng(lat, lng));
//                            }


                            lat = Double.parseDouble(location.split(",")[1]);
                            lng = Double.parseDouble(location.split(",")[0]);
                            moveCamera(new LatLng(lat, lng));
                            showMarker(new LatLng(lat, lng));
                            getAddressByLatlng(new LatLng(lat, lng));
                        } else {
                            locateSelfPosition();
                        }
                        break;
                    }
                }
                break;
            }
        }
    }
}
