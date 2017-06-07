package com.androidquanjiakan.activity.index.watch_child;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
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
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.entity.CommonNattyData;
import com.androidquanjiakan.util.LogUtil;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;
import com.wbj.ndk.natty.client.NattyProtocolFilter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.im.android.eventbus.ThreadMode;

public class WatchChildFreshLocationActivity extends BaseActivity implements OnClickListener, LocationSource,
        AMapLocationListener, AMap.OnMapClickListener, GeocodeSearch.OnGeocodeSearchListener, AMap.InfoWindowAdapter, AMap.OnMarkerClickListener {

    private String device_id;
    private String device_type;

    @BindView(R.id.ibtn_back)
    ImageButton ibtnBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.map)
    MapView mapView;
    @BindView(R.id.location)
    TextView location;

    private AMap aMap;
    private GeocodeSearch geocoderSearch;

    private double lat;
    private double lng;

    ArrayList<BitmapDescriptor> iconList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_watch_child_fresh_locate);
        ButterKnife.bind(this);
        device_id = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_ID);
        device_type = getIntent().getStringExtra(BaseConstants.PARAMS_DEVICE_TYPE);
        lat = getIntent().getDoubleExtra(BaseConstants.PARAMS_DEVICE_LAT,-200);
        lng = getIntent().getDoubleExtra(BaseConstants.PARAMS_DEVICE_LNG,-200);
        if (device_id == null || device_id.length() < 0 || device_id.length() != 15 || device_type == null || device_type.length() < 1) {
            Toast.makeText(this, "传入参数异常!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toast.makeText(WatchChildFreshLocationActivity.this, "进来了吧", Toast.LENGTH_SHORT).show();

        initTitleBar();
        initMap(savedInstanceState);

        if(lat!=-200 && lng!=-200){
            moveCamera(new LatLng(lat,lng));
            showMarker(new LatLng(lat,lng));
            getAddressByLatlng(new LatLng(lat,lng));
        }else{
            getLocation();
        }
    }

    public void getLocation() {
        try {
            if (!BaseApplication.getInstances().isSDKConnected()) {
                LogUtil.e("SDK 断联");
                BaseApplication.getInstances().toast(WatchChildFreshLocationActivity.this,"已与手表服务器断开连接");
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
    private LocationSource.OnLocationChangedListener mListener;
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
                    mlocationClient = new AMapLocationClient(WatchChildFreshLocationActivity.this);
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
        ibtnBack.setVisibility(View.VISIBLE);
        ibtnBack.setOnClickListener(this);

        tvTitle.setText("地图");

    }

    public void initMap(Bundle savedInstanceState) {
        long currentTime = System.currentTimeMillis();
        LogUtil.e("Time:0");
        mapView.onCreate(savedInstanceState);
        LogUtil.e("Time:"+(System.currentTimeMillis()-currentTime)/1000);
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setCompassEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.setInfoWindowAdapter(this);
        aMap.setOnMarkerClickListener(this);

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);
    }

    public void getAddressByLatlng(final LatLng latlng) {
        LatLonPoint latLonPoint = new LatLonPoint(latlng.latitude, latlng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 100,
                GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);
        mapView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
        mapView.onPause();
        deactivate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Subscribe(threadMode = org.greenrobot.eventbus.ThreadMode.MAIN)
    public void onCommonNattyData(CommonNattyData msg){
        switch (msg.getType()) {
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

                            && json.getJSONObject("results").has("Location")
                            && json.getJSONObject("results").getString("Location") != null
                            && json.getJSONObject("results").getString("Location").length() > 3
                            && json.getJSONObject("results").getString("Location").contains(",")
                            ) {
                        LogUtil.e("FreshLocation.GET_LOCATION:" + data);

                        if (json.getJSONObject("results").getString("IMEI").equals(device_id)
                                ) {
                            String location = json.getJSONObject("results").getString("Location");

                            if (location!=null && location.contains(",") && location.split(",").length==2) {
                                lat = Double.parseDouble(location.split(",")[1]);
                                lng = Double.parseDouble(location.split(",")[0]);
                                moveCamera(new LatLng(lat,lng));
                                showMarker(new LatLng(lat,lng));
                                getAddressByLatlng(new LatLng(lat,lng));
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

                                    && json.getJSONObject("Results").has("Location")
                                    && json.getJSONObject("Results").getString("Location") != null
                                    && json.getJSONObject("Results").getString("Location").length() > 3
                                    && json.getJSONObject("Results").getString("Location").contains(",")
                            ) {
                        LogUtil.e("FreshLocation.GET_LOCATION:" + data);
                        if (json.getJSONObject("Results").getString("IMEI").equals(device_id)
                                ) {
                            String location = json.getJSONObject("Results").getString("Location");
                            if (location!=null && location.contains(",") && location.split(",").length==2) {
                                lat = Double.parseDouble(location.split(",")[1]);
                                lng = Double.parseDouble(location.split(",")[0]);
                                moveCamera(new LatLng(lat,lng));
                                showMarker(new LatLng(lat,lng));
                                getAddressByLatlng(new LatLng(lat,lng));
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
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                Intent intent = new Intent();
                intent.putExtra(BaseConstants.PARAMS_DEVICE_LAT,lat);
                intent.putExtra(BaseConstants.PARAMS_DEVICE_LNG,lng);
                setResult(RESULT_OK,intent);
                finish();
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
        if(marker.isInfoWindowShown()){
            marker.hideInfoWindow();
        }else{
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
                location.setText("最后定位地址:"+addressName.replace(province, ""));
//                LogUtil.e("经纬度解析成功!");
            } else {
                LogUtil.e("对不起，没有搜索到相关数据！");
            }
        } else {
            BaseApplication.getInstances().showerror(this, rCode);
            LogUtil.e("解析失败：  " + rCode);
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}
