package com.androidquanjiakan.activity.index;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.dialog.SelectTimeDialog;
import com.androidquanjiakan.util.ChString;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Gin on 2016/10/10.
 */

public class SpecialCar_Activity extends BaseActivity implements View.OnClickListener, LocationSource, AMapLocationListener, GeocodeSearch.OnGeocodeSearchListener, AMap.InfoWindowAdapter, RouteSearch.OnRouteSearchListener {
    private TextView tv_back;
    private TextView tv_love_specialcar;
    private TextView tv_ambulancecar;
    private ImageButton ibtn_xieyi;

    private OnLocationChangedListener mListener;

    private final int LOVEING=1;
    private final int AMBULANCE=2;
    private FrameLayout content_container;
    private ListView lv_address;
    private String address;
    private String address1;
    private MapView mapView;
    private TextView tv_begin;
    private TextView tv_finish;
    private RelativeLayout rl_finish;
    private RelativeLayout rl_begin;
    private RelativeLayout rl_begintime;
    private TextView tv_begintime;
    private ImageButton ibtn_choosetime;
    private RelativeLayout rl_money;
    private TextView tv_money;
    private TextView tv_moneyrule;
    private TextView tv_number;
    private TextView tv_submit;
    private AMap aMap;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private double latitude;
    private double longitude;
    private String city;
    private Marker marker;

    public static final int LOVING_BEGINADDRESS=1;
    public static final int LOVING_DESTINATION=2;
    private SelectTimeDialog selectTimeDialog;
    private TextView tv_cancelorder;
    private TextView tv_waitorder;
    private RelativeLayout rl_wait;
    private ImageView iv_touxiang;
    private TextView tv_drivername;
    private ImageView iv_star1;
    private ImageView iv_star2;
    private ImageView iv_star3;
    private ImageView iv_star4;
    private ImageView iv_star5;
    private TextView tv_ordered;
    private TextView tv_carname;
    private TextView tv_carcard;
    private ImageButton ibtn_telphone;
    private RelativeLayout rl_all;
    private String cityCode;
    private GeocodeSearch geocoderSearch;
    private double latitudeStart;
    private double longitudeStart;
    private double latitudeFinish;
    private double longitudeFinish;
    private RouteSearch mRouteSearch;
    private float distance;
    private long duration;
    private String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_special_car);
        BaseApplication.getInstances().setCurrentActivity(this);
        initView(savedInstanceState);
        init();

    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setCompassEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

        aMap.setInfoWindowAdapter(this);
    }

    private void initView(Bundle savedInstanceState) {
        tv_back = (TextView)findViewById(R.id.tv_back);
        tv_back.setOnClickListener(this);

        tv_love_specialcar = (TextView)findViewById(R.id.tv_love_specialcar);
        tv_love_specialcar.setOnClickListener(this);
        tv_ambulancecar = (TextView)findViewById(R.id.tv_ambulancecar);
        tv_ambulancecar.setOnClickListener(this);
        ibtn_xieyi = (ImageButton)findViewById(R.id.ibtn_xieyi);
        ibtn_xieyi.setOnClickListener(this);

        tv_waitorder = (TextView)findViewById(R.id.tv_waitordertitle);
        tv_cancelorder = (TextView)findViewById(R.id.tv_cancelorder);
        //content_container = (FrameLayout) findViewById(R.id.content_container);

        //默认选中爱心专车
        setType(LOVEING);
        
        
        //顶部等待出发--------------------------------------
        rl_wait = (RelativeLayout) findViewById(R.id.rl_wait);
        iv_touxiang = (ImageView)findViewById(R.id.iv_touxiang);
        tv_drivername = (TextView)findViewById(R.id.tv_drivername);
        iv_star1 = (ImageView)findViewById(R.id.iv_star1);
        iv_star2 = (ImageView)findViewById(R.id.iv_star2);
        iv_star3 = (ImageView)findViewById(R.id.iv_star3);
        iv_star4 = (ImageView)findViewById(R.id.iv_star4);
        iv_star5 = (ImageView)findViewById(R.id.iv_star5);
        tv_ordered = (TextView)findViewById(R.id.tv_ordered);
        tv_carname = (TextView)findViewById(R.id.tv_carname);
        tv_carcard = (TextView)findViewById(R.id.tv_carcard);
        ibtn_telphone = (ImageButton)findViewById(R.id.ibtn_telphone);
        


        mapView=(MapView)findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        

        //底部出发---------------------------------------
        rl_all = (RelativeLayout) findViewById(R.id.rl_all);
        tv_begin = (TextView)findViewById(R.id.tv_beginadress);
        tv_finish = (TextView)findViewById(R.id.tv_finishadress);

        rl_finish = (RelativeLayout)findViewById(R.id.rl_finish);
        rl_begin = (RelativeLayout) findViewById(R.id.rl_begin);

        rl_begintime = (RelativeLayout) findViewById(R.id.rl_begintime);
        tv_begintime = (TextView) findViewById(R.id.tv_begintime);
        ibtn_choosetime = (ImageButton) findViewById(R.id.ibtn_choosetime);
        ibtn_choosetime.setOnClickListener(this);


        rl_money = (RelativeLayout) findViewById(R.id.rl_money);
        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_moneyrule = (TextView) findViewById(R.id.tv_moneyrules);
        tv_moneyrule.setOnClickListener(this);
        tv_number = (TextView) findViewById(R.id.tv_number);
        tv_number.setOnClickListener(this);

        tv_submit = (TextView) findViewById(R.id.tv_submit);
        tv_submit.setText("去选座");
        tv_submit.setOnClickListener(this);

        rl_begin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), address, Toast.LENGTH_SHORT).show();
                if(address!=null) {
                    Intent intent = new Intent(SpecialCar_Activity.this, SelectDestination_Activity.class);
                    intent.putExtra("city",city);
                    intent.putExtra("flag",LOVING_BEGINADDRESS);
                    intent.putExtra("cityCode",cityCode);
                    intent.putExtra("longitude",longitude);
                    intent.putExtra("latitude",latitude);
                    startActivityForResult(intent,LOVING_BEGINADDRESS);
                }else {
                    Toast.makeText(SpecialCar_Activity.this, "定位失败,请重试!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        rl_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), address, Toast.LENGTH_SHORT).show();
                if(address!=null) {
                    Intent intent = new Intent(SpecialCar_Activity.this, SelectDestination_Activity.class);
                    intent.putExtra("city",city);
                    intent.putExtra("flag",LOVING_DESTINATION);
                    intent.putExtra("cityCode",cityCode);
                    intent.putExtra("longitude",longitude);
                    intent.putExtra("latitude",latitude);
                    startActivityForResult(intent,LOVING_DESTINATION);
                }else {
                    Toast.makeText(SpecialCar_Activity.this, "定位失败,请重试!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==LOVING_BEGINADDRESS&&resultCode== SelectDestination_Activity.LOVING_BEGINRESULT) {
            String address = data.getStringExtra("address");
            String addressdetail = data.getStringExtra("addressdetail");
            tv_begin.setText(address);
        }else if(requestCode==LOVING_DESTINATION&&resultCode==SelectDestination_Activity.LOVING_FINISHRESULT) {
            String address1 = data.getStringExtra("address");
            String addressdetail = data.getStringExtra("addressdetail");
            tv_finish.setText(address1);
            rl_begintime.setVisibility(View.VISIBLE);
            rl_money.setVisibility(View.VISIBLE);
            tv_number.setVisibility(View.GONE);
            //给其画线
            TextPaint paint = tv_moneyrule.getPaint();
            paint.setColor(getResources().getColor(R.color.colorbutton));
            paint.setFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

            tv_submit.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_love_specialcar:
                setType(LOVEING);
                break;
            case R.id.tv_ambulancecar:
                setType(AMBULANCE);
                break;
            case R.id.ibtn_xieyi:
                Toast.makeText(SpecialCar_Activity.this, "协议", Toast.LENGTH_SHORT).show();
                break;

            case R.id.ibtn_choosetime:
                //选择出发时间
                showTimeDialog();
                break;
            case R.id.tv_submit:
                if(tv_submit.getText().toString().equals("去选座")) {
                    //弹出选座的对话框
                    showSelectSeatDialog();
                }else if(tv_submit.getText().toString().equals("预约专车")) {
                  //呼叫专车
                    //rl_all.setVisibility(View.GONE);
                    tv_love_specialcar.setVisibility(View.GONE);
                    tv_ambulancecar.setVisibility(View.GONE);
                    ibtn_xieyi.setVisibility(View.GONE);

                    tv_waitorder.setVisibility(View.VISIBLE);
                    tv_waitorder.setText("等待司机接单");
                    tv_cancelorder.setVisibility(View.VISIBLE);


                    //通过地址拿到经纬度
                    List<GeocodeQuery> list =new ArrayList<>();
                    geocoderSearch = new GeocodeSearch(this);
                    geocoderSearch.setOnGeocodeSearchListener(this);

                    GeocodeQuery query = new GeocodeQuery(tv_begin.getText().toString().trim(), cityCode);
                    GeocodeQuery geocodeQuery = new GeocodeQuery(tv_finish.getText().toString().trim(), cityCode);
                    list.add(query);
                    list.add(geocodeQuery);

                    geocoderSearch.getFromLocationNameAsyn(list.get(0));// 设置出发同步地理编码请求
                    geocoderSearch.getFromLocationNameAsyn(list.get(1));// 设置终点同步地理编码请求

                    /*GeocodeSearch search = new GeocodeSearch(this);
                    search.setOnGeocodeSearchListener(this);
                    GeocodeQuery geocodeQuery = new GeocodeQuery(tv_finish.getText().toString(), cityCode);
                    search.getFromLocationNameAsyn(geocodeQuery);*/

                    //进行一系列网络请求


                   /* if(longitudeStart!=0&&latitudeStart!=0&&longitudeFinish!=0&&latitudeFinish!=0) {
                        Toast.makeText(SpecialCar_Activity.this, "进来了没", Toast.LENGTH_SHORT).show();


                    }*/

                    //marker.showInfoWindow();
                }
                break;
            case R.id.tv_number:
                showSelectSeatDialog();
                break;
            case R.id.tv_moneyrules:
                Toast.makeText(SpecialCar_Activity.this, "收费标准", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    //选择座位
    private void showSelectSeatDialog() {
        final Dialog dialog = QuanjiakanDialog.getInstance().getCardDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_selectseat, null);
        view.findViewById(R.id.tv_cancelselect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.tv_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_number.setVisibility(View.VISIBLE);
                tv_number.setText("1人乘车");
                tv_submit.setText("预约专车");
                dialog.dismiss();

            }
        });
        view.findViewById(R.id.tv_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_number.setVisibility(View.VISIBLE);
                tv_number.setText("2人乘车");
                tv_submit.setText("预约专车");
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.tv_3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_number.setVisibility(View.VISIBLE);
                tv_number.setText("3人乘车");
                tv_submit.setText("预约专车");
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.tv_4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_number.setVisibility(View.VISIBLE);
                tv_number.setText("4人乘车");
                tv_submit.setText("预约专车");
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = this.getResources().getDisplayMetrics().widthPixels;
        lp.height = lp.WRAP_CONTENT;

        dialog.setContentView(view,lp);
        dialog.show();

    }

    //选择出发时间
    private void showTimeDialog() {
        selectTimeDialog = new SelectTimeDialog(this);
        selectTimeDialog.setOnTimeChangeCListener(new SelectTimeDialog.OnTimeChangeCListener() {
            @Override
            public void onClick(String date, String clock, String minute) {
                tv_begintime.setText(date+"  "+clock+"  " +minute);
            }
        });
        selectTimeDialog.show();

    }

    public void setType(int type) {
        switch (type){
            case LOVEING:
                tv_love_specialcar.setBackgroundResource(R.drawable.shape_speciacar);
                tv_love_specialcar.setTextColor(getResources().getColor(R.color.colorSpecialCar));

                tv_ambulancecar.setBackgroundColor(getResources().getColor(R.color.colorHui));
                tv_ambulancecar.setTextColor(getResources().getColor(R.color.colortext));
                //爱心专车fragment
                //lovingCarFragment();
                break;
            case AMBULANCE:
                tv_ambulancecar.setBackgroundResource(R.drawable.shape_speciacar);
                tv_ambulancecar.setTextColor(getResources().getColor(R.color.colorSpecialCar));

                tv_love_specialcar.setBackgroundColor(getResources().getColor(R.color.colorHui));
                tv_love_specialcar.setTextColor(getResources().getColor(R.color.colortext));
                //救护车fragment
                //ambulanceCarFragment();
                break;
        }
    }

    private void ambulanceCarFragment() {
       /* AmbulanceCarFragment fragment = new AmbulanceCarFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_container,fragment);
        transaction.commitAllowingStateLoss();*/
    }

    private void lovingCarFragment() {
       /* LovingCarFragment fragment = new LovingCarFragment();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_container,fragment);
        transaction.commitAllowingStateLoss();*/
    }


    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());

        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());

        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation!=null&&aMapLocation.getErrorCode()==0) {
            latitude = aMapLocation.getLatitude();
            longitude = aMapLocation.getLongitude();
            address =  aMapLocation.getAddress();
            city = aMapLocation.getCity();
            cityCode = aMapLocation.getCityCode();
            //street = aMapLocation.getStreet();
            if(address.contains("市")) {
                address1 = address.substring(address.indexOf("市")+1, address.length() - 1);
            }else {
                address1=address;
            }

            tv_begin.setText(address1);

            deactivate();//不调用停止定位，会不断刷新当前位置

            addMarkerAndInfoWindow();

        }else {
            Toast.makeText(this, "定位失败", Toast.LENGTH_SHORT).show();
            deactivate();
        }


    }

    private void addMarkerAndInfoWindow() {
        if(marker!=null){
            marker.hideInfoWindow();
        }
        LatLng latLng = new LatLng(latitude,longitude);
        aMap.clear();
        MarkerOptions otMarkerOptions = new MarkerOptions();
        otMarkerOptions.title(address);
        otMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_location));
        otMarkerOptions.position(latLng);
        marker = aMap.addMarker(otMarkerOptions);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(18));

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

    }

    //地理编码查询回调
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        if(i==1000) {
            if(geocodeResult!=null&&geocodeResult.getGeocodeAddressList()!=null&&geocodeResult.getGeocodeAddressList().size()>0) {
                GeocodeQuery geocodeQuery = geocodeResult.getGeocodeQuery();
                String locationName = geocodeQuery.getLocationName();
                if(locationName.equals(tv_begin.getText().toString().trim())) {
                    GeocodeAddress geocodeAddress = geocodeResult.getGeocodeAddressList().get(0);
                    LatLonPoint latLonPoint = geocodeAddress.getLatLonPoint();//经纬度
                    //起点纬度
                    latitudeStart = latLonPoint.getLatitude();
                    //起点经度
                    longitudeStart = latLonPoint.getLongitude();
                }
                if(locationName.equals(tv_finish.getText().toString().trim())) {
                    GeocodeAddress geocodeAddress = geocodeResult.getGeocodeAddressList().get(0);
                    LatLonPoint latLonPoint = geocodeAddress.getLatLonPoint();//经纬度
                    //终点纬度
                    latitudeFinish = latLonPoint.getLatitude();
                    //终点经度
                    longitudeFinish = latLonPoint.getLongitude();
                }
            }

            //通过拿到两点经纬度,获取行车的数据
            LatLonPoint mStartPoint = new LatLonPoint(latitudeStart, longitudeStart);
            LatLonPoint mEndPoint = new LatLonPoint(latitudeFinish, longitudeFinish);
            mRouteSearch = new RouteSearch(this);
            mRouteSearch.setRouteSearchListener(this);
            RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                    mStartPoint, mEndPoint);
            RouteSearch.DriveRouteQuery dQuery = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null,
                    null, "");
            mRouteSearch.calculateDriveRouteAsyn(dQuery);
        }

    }


    //设置自定义弹窗,展示时间和距离
    private View dialog;
    @Override
    public View getInfoWindow(Marker marker) {
        showDistanceInfoDialog();
        return dialog;
    }

    @Override
    public View getInfoContents(Marker marker) {
        showDistanceInfoDialog();
        return dialog;
    }

    public void showDistanceInfoDialog(){
        dialog=LayoutInflater.from(this).inflate(R.layout.dialog_specialcar_distance,null);
        TextView tv_minute = (TextView) dialog.findViewById(R.id.tv_minute);
        TextView tv_distance = (TextView) dialog.findViewById(R.id.tv_distance);
        if(time!=null&&distance!=0) {
            tv_minute.setText(time);
            tv_distance.setText("大约"+getFriendlyLength((int)distance));
        }

    }


    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }


    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
        distance = driveRouteResult.getPaths().get(0).getDistance();
        duration = driveRouteResult.getPaths().get(0).getDuration();
        time = getFriendlyTime((int) duration);

        marker.showInfoWindow();
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    public static String getFriendlyTime(int second) {
        if (second >= 60) {
            int miniate = second / 60;
            return miniate + "";
        }
        return second/60+"";
    }


    public static String getFriendlyLength(int lenMeter) {
        if (lenMeter > 10000) // 10 km
        {
            int dis = lenMeter / 1000;
            return dis + ChString.Kilometer;
        }

        if (lenMeter > 1000) {
            float dis = (float) lenMeter / 1000;
            DecimalFormat fnum = new DecimalFormat("##0.0");
            String dstr = fnum.format(dis);
            return dstr + ChString.Kilometer;
        }

        if (lenMeter > 100) {
            int dis = lenMeter / 50 * 50;
            return dis + ChString.Meter;
        }

        int dis = lenMeter / 10 * 10;
        if (dis == 0) {
            dis = 10;
        }
        return dis + ChString.Meter;
    }
}
