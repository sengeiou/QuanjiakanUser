package com.androidquanjiakan.activity.index.out;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.dialog.ChangeAddressDetailDialog;
import com.androidquanjiakan.util.AMapUtil;
import com.androidquanjiakan.util.ToastUtil;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConsultantActivity_GoOut_Locate_backup extends BaseActivity implements OnClickListener,LocationSource,
        AMapLocationListener, AMap.OnMapClickListener,GeocodeSearch.OnGeocodeSearchListener,AMap.InfoWindowAdapter,AMap.OnMarkerClickListener {

    private final String PARAMS_ADDR = "addr";
    private final String PARAMS_LAT = "lat";
    private final String PARAMS_LON = "lon";

    private final String VOLUNTEER_ADDR="volunteeraddr";
    private final String VOLUNTEER_LAT="volunteerlat";
    private final String VOLUNTEER_LON="volunteerlon";

    private TextView tv_title;
    private ImageButton ibtn_back;

    private AMap aMap;
    private MapView mapView;

    private GeocodeSearch geocoderSearch;

    double my_latitude;
    double my_longitude;


    private ArrayList<String> provinces;
    private ArrayList<String> cities;
    private ArrayList<String> sections;

    private ArrayList<String> arrProvinces = new ArrayList<String>();

    private Map<String, ArrayList<String>> mCitisDatasMap = new HashMap<String, ArrayList<String>>();
    private Map<String, ArrayList<String>> mAreaDatasMap = new HashMap<String, ArrayList<String>>();

    private JSONObject mJsonObj;

    /**
     * 省市区三列
     */
    private LinearLayout condition_line_left;
    private LinearLayout condition_line_middle;
    private LinearLayout condition_line_right;

    private TextView condition_left;
    private TextView condition_middle;
    private TextView condition_right;

    /**
     * 条件列
     */
    private LinearLayout select_condition_list;
    private ListView select_left;
    private ListView select_middle;
    private ListView select_right;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_doctor_go_out_locate);

        initTitleBar();

        initMap(savedInstanceState);
        initCondition();
        initConditionData();
    }

    public void initConditionData(){
        try {
            StringBuffer sb = new StringBuffer();
            InputStream is = getAssets().open("city.json");
            int len = -1;
            byte[] buf = new byte[1024];
            while ((len = is.read(buf)) != -1) {
                sb.append(new String(buf, 0, len, "gbk"));
            }
            is.close();
            mJsonObj = new JSONObject(sb.toString());
            /**
             * 解析省、市、区数据
             */
            JSONArray jsonArray = mJsonObj.getJSONArray("citylist");
            arrProvinces = new ArrayList<String>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonP = jsonArray.getJSONObject(i);
                String province = jsonP.getString("p");
                /**
                 * 省
                 */
                arrProvinces.add(province);
                /**
                 * 市
                 */
                if (jsonP.has("c")) {
                    JSONArray jsonCs = jsonP.getJSONArray("c");
                    ArrayList<String> mCitiesDatas = new ArrayList<String>();
                    for (int j = 0; j < jsonCs.length(); j++) {
                        JSONObject jsonCity = jsonCs.getJSONObject(j);
                        String city = jsonCity.getString("n");
                        mCitiesDatas.add(city);
                        /**
                         * 地区
                         */
                        if (jsonCity.has("a")) {
                            JSONArray jsonAreas = jsonCity.getJSONArray("a");
                            ArrayList<String> mAreasDatas = new ArrayList<String>();
                            for (int k = 0; k < jsonAreas.length(); k++) {
                                String area = jsonAreas.getJSONObject(k).getString("s");
                                mAreasDatas.add(area);
                            }
                            mAreaDatasMap.put(city, mAreasDatas);
                        } else {
                            mAreaDatasMap.put(city, null);
                            continue;
                        }
                    }
                    mCitisDatasMap.put(province, mCitiesDatas);
                } else {
                    mCitisDatasMap.put(province, null);
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void initCondition(){
        condition_line_left = (LinearLayout) findViewById(R.id.condition_line_left);
        condition_line_middle = (LinearLayout) findViewById(R.id.condition_line_middle);
        condition_line_right = (LinearLayout) findViewById(R.id.condition_line_right);

        condition_left = (TextView) findViewById(R.id.condition_left);
        condition_middle = (TextView) findViewById(R.id.condition_middle);
        condition_right = (TextView) findViewById(R.id.condition_right);

        select_condition_list = (LinearLayout) findViewById(R.id.select_condition_list);
        select_left = (ListView) findViewById(R.id.select_left);
        select_middle = (ListView) findViewById(R.id.select_middle);
        select_right = (ListView) findViewById(R.id.select_right);

        condition_line_left.setOnClickListener(this);
        condition_line_middle.setOnClickListener(this);
        condition_line_right.setOnClickListener(this);
    };

    private int currentTypeValue = 0;

    public static final int PROVINCE = 1;
    public static final int CITY = 2;
    public static final int AREA = 3;

    private void hideCondition() {
        select_condition_list.setVisibility(View.GONE);
    }

    private boolean isConditionShow() {
        return select_condition_list.getVisibility() == View.VISIBLE;
    }

    private void showCondition(int which) {
        select_condition_list.setVisibility(View.VISIBLE);
        switch (which) {
            case PROVINCE:
                select_left.setVisibility(View.VISIBLE);
                select_middle.setVisibility(View.INVISIBLE);
                select_right.setVisibility(View.INVISIBLE);
                break;
            case CITY:
                select_left.setVisibility(View.INVISIBLE);
                select_middle.setVisibility(View.VISIBLE);
                select_right.setVisibility(View.INVISIBLE);
                break;
            case AREA:
                select_left.setVisibility(View.INVISIBLE);
                select_middle.setVisibility(View.INVISIBLE);
                select_right.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    public void filter(){

        /**
         *
         */
        String address = condition_left.getText().toString()+condition_middle.getText().toString()+condition_right.getText().toString();
        address = address.replace("全部","");
        String city = "";
        if("全部".equals(condition_left.getText().toString()) && "全部".equals(condition_middle.getText().toString()) && "全部".equals(condition_right.getText().toString())){

        }else if(!"全部".equals(condition_left.getText().toString()) && "全部".equals(condition_middle.getText().toString()) && "全部".equals(condition_right.getText().toString())){
            city = condition_left.getText().toString();
            getLatLng(address,city);
        }else if(!"全部".equals(condition_left.getText().toString()) && !"全部".equals(condition_middle.getText().toString()) && "全部".equals(condition_right.getText().toString())){
            city = condition_left.getText().toString();
            getLatLng(address,city);
        }else if(!"全部".equals(condition_left.getText().toString()) && !"全部".equals(condition_middle.getText().toString()) && !"全部".equals(condition_right.getText().toString())){
            city = condition_middle.getText().toString();
            getLatLng(address,city);
        }

    }

    /**
     * @param which
     * @param data
     */
    public void showSelectCondition(int which, final ArrayList<String> data) {
        showCondition(which);
        ArrayAdapter<String> adapter;
        switch (which) {
            case PROVINCE://省
                adapter = new ArrayAdapter<String>(this, R.layout.item_select_condition_medicine, arrProvinces);
                select_left.setAdapter(adapter);
                select_left.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        if (arrProvinces.get(position).equals(condition_left.getText().toString())) {
                            hideCondition();
                        } else {

                            /**
                             *  默认第一个市，第一个区
                             */
                            if("全部".equals(arrProvinces.get(position))){
                                condition_left.setText("全部");
                                condition_middle.setText("全部");
                                condition_left.setText("全部");
                            }else{
                                condition_left.setText(arrProvinces.get(position));
                                ArrayList<String> city = mCitisDatasMap.get(arrProvinces.get(position));
                                if(city!=null && city.size()>0){
                                    if("全部".equals(city.get(0)) && city.size()>1){
                                        condition_middle.setText(city.get(1));
                                    }else{
                                        condition_middle.setText(city.get(0));
                                    }
                                }else{
                                    condition_middle.setText("全部");
                                }
                                ArrayList<String> area = mAreaDatasMap.get(condition_middle.getText().toString());
                                if(area!=null && area.size()>0){
//                                    select_condition_middleright.setText(area.get(0));

                                    if("全部".equals(area.get(0)) && area.size()>1){
                                        condition_right.setText(area.get(1));
                                    }else{
                                        condition_right.setText(area.get(0));
                                    }
                                }else{
                                    condition_right.setText("全部");
                                }
                            }

                            hideCondition();
                            /**
                             * @TODO 需要添加对选中条件后展示数据的过滤操作
                             */
                            filter();
                        }
                    }
                });

                break;
            case CITY://市
                if ("全部".equals(condition_left.getText().toString()) || !arrProvinces.contains(condition_left.getText().toString())) {
                    BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Locate_backup.this,"请选择一个确定的省份!");
                    hideCondition();
                } else if (data == null) {
                    BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Locate_backup.this,"该地区无更小行政区域");
                    hideCondition();
                } else {
                    adapter = new ArrayAdapter<String>(this, R.layout.item_select_condition_medicine, data);
                    select_middle.setAdapter(adapter);
                    select_middle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            if (data.get(position).equals(condition_middle.getText().toString())) {
                                hideCondition();
                            } else {
                                /**
                                 * 全部
                                 */
//                                    condition_middle.setText(data.get(position));
//                                    condition_right.setText("全部");

                                /**
                                 * 默认第一个区
                                 */
                                condition_middle.setText(data.get(position));
                                ArrayList<String> area = mAreaDatasMap.get(data.get(position));
                                if(area!=null && area.size()>0){
//                                        condition_right.setText(area.get(0));

                                    if("全部".equals(area.get(0)) && area.size()>1){
                                        condition_right.setText(area.get(1));
                                    }else{
                                        condition_right.setText(area.get(0));
                                    }
                                }else{
                                    condition_right.setText("全部");
                                }

                                hideCondition();
                                /**
                                 * @TODO 需要添加对选中条件后展示数据的过滤操作
                                 */
                                filter();
                            }
                        }
                    });
                }

                break;
            case AREA://区
                if ("全部".equals(condition_middle.getText().toString())) {
                    BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Locate_backup.this,"请选择一个确定的城市!");
                    hideCondition();
                } else if (data == null) {
                    BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Locate_backup.this,"该地区无更小行政区域");
                    hideCondition();
                } else {
                    adapter = new ArrayAdapter<String>(this, R.layout.item_select_condition_medicine, data);
                    select_right.setAdapter(adapter);
                    select_right.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                            if (data.get(position).equals(condition_right.getText().toString())) {
                                hideCondition();
                            } else {
                                if ("全部".equals(data.get(position))) {
                                    condition_right.setText("全部");
                                }
                                condition_right.setText(data.get(position));
                                hideCondition();
                                /**
                                 * @TODO 需要添加对选中条件后展示数据的过滤操作------由于接口只做两层过滤，所以该条件已不起作用
                                 */
                               filter();
                            }
                        }
                    });
                }
                break;
            default:
                break;
        }

    }

    public void initTitleBar(){
        tv_title = (TextView) findViewById(R.id.tv_title);
        if(getIntent().getStringExtra("flag")!=null&&getIntent().getStringExtra("flag").equals("fromVolunteer")) {
            tv_title.setText("发布");
        }else {
            tv_title.setText("位置");
        }
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);

    }

    public void initMap(Bundle savedInstanceState){

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);


        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
        }

        /**
         * ***** 初始化
         */
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.icon_gcoding));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        myLocationStyle.strokeWidth(0f);// 设置圆形的边框粗细

        aMap.getUiSettings().setCompassEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        aMap.setMyLocationStyle(myLocationStyle);

        aMap.setLocationSource(this);// 设置定位监听
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()

        aMap.setOnMapClickListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setInfoWindowAdapter(this);

        //进行定位获取当前位置
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
        deactivate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
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
    /**
     * 停止定位
     */
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.condition_line_left:
                showAddressDialog();
                break;
            case R.id.condition_line_middle:
                showAddressDialog();
                break;
            case R.id.condition_line_right:
                showAddressDialog();
                break;
            default:
                break;
        }
    }

    public void showAddressDialog(){
        ChangeAddressDetailDialog dialog = new ChangeAddressDetailDialog(ConsultantActivity_GoOut_Locate_backup.this);
        dialog.setAddresskListener(new ChangeAddressDetailDialog.OnAddressCListener() {
            @Override
            public void onClick(String province, String city, String section) {

                condition_left.setText(province );

                condition_middle.setText(city );

                condition_right.setText(section );

                filter();

            }
        });
        dialog.show();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {

                address = amapLocation.getAddress();
                latitude = amapLocation.getLatitude();
                longitude = amapLocation.getLongitude();
                /**
                 * 显示位置数据
                 */
                String province = amapLocation.getProvince();
                String city = amapLocation.getCity();
                String district = amapLocation.getDistrict();
                /**
                 * 显示省
                 */
                for(int i = province.length()-1;i>-1;i--){
                    String sub = province.substring(0,i);
                    if(arrProvinces.contains(sub)){
                        condition_left.setText(sub);
                        break;
                    }else{
                        condition_left.setText("");
                    }
                }

                if(city!=null){

                    ArrayList<String> citys = mCitisDatasMap.get(condition_left.getText().toString());
                    if(citys!=null && citys.size()>0){
                        for(int i = city.length()-1;i>-1;i--){
                            String sub = city.substring(0,i);
                            if(citys.contains(sub)){
                                condition_middle.setText(sub);
                                break;
                            }else{
                                condition_middle.setText("");
                            }
                        }
                    }else{
                        condition_middle.setText("");
                    }

                    if(district!=null){
                        condition_right.setText(district);
                    }else{
                        condition_right.setText("");
                    }

                }else{
                    if(district!=null){
                        ArrayList<String> citys = mCitisDatasMap.get(condition_left.getText().toString());
                        if(citys!=null && citys.size()>0){
                            for(int i = district.length()-1;i>-1;i--){
                                String sub = district.substring(0,i);
                                if(citys.contains(sub)){
                                    condition_middle.setText(sub);
                                    break;
                                }else{
                                    condition_middle.setText("");
                                }
                            }
                        }else{
                            condition_middle.setText("");
                        }
                    }else{
                        condition_middle.setText("");
                    }
                }

                /**
                 *
                 */
                mylatitude = latitude;
                mylongitude = longitude;

                deactivate();//不调用停止定位，会不断刷新当前位置

                addMarkerAndInfoWindow();

            } else {//定位失败
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Locate_backup.this,errText);
            }
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        movePoint(latLng);
    }

    public void movePoint(LatLng latLng){
        //---反地理编码，获取到位置信息同时显示InfoWindow上
        decodePoint(latLng);
    }

    public void decodePoint(LatLng latLng){
        //获取--坐标位置地址名称
        LatLonPoint point = new LatLonPoint(latLng.latitude,latLng.longitude);
        getAddress(point);
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 50,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    /**
     * 响应逆地理编码
     */
    public void getLatLng(final String address,final String city) {
        GeocodeQuery query = new GeocodeQuery(address, city);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
    }

    private Marker marker;
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {

                address = result.getRegeocodeAddress().getFormatAddress();
                LatLonPoint latLng = result.getRegeocodeQuery().getPoint();
                latitude = latLng.getLatitude();
                longitude = latLng.getLongitude();
						/*+ "附近"*/;

                String addr_provice = result.getRegeocodeAddress().getProvince();
                String addr_city = result.getRegeocodeAddress().getCity();
                String addr_district = result.getRegeocodeAddress().getDistrict();
                String addr_neighbor = result.getRegeocodeAddress().getNeighborhood();
                String addr_township = result.getRegeocodeAddress().getTownship();
                String addr_towncode = result.getRegeocodeAddress().getTowncode();
                String addr_adcode = result.getRegeocodeAddress().getAdCode();

//                BaseApplication.getInstances().toast("address:"+address
//                        +"\n"+"addr_provice:"+addr_provice
//                        +"\n"+"addr_city:"+addr_city
//                        +"\n"+"addr_district:"+addr_district
//                        +"\n"+"addr_neighbor:"+addr_neighbor
//                        +"\n"+"addr_township:"+addr_township
//                        +"\n"+"addr_towncode:"+addr_towncode
//                        +"\n"+"addr_adcode:"+addr_adcode);

                String province = addr_provice;
                String city = addr_city;
                String district = addr_district;
                /**
                 * 显示省
                 */
                for(int i = province.length()-1;i>-1;i--){
                    String sub = province.substring(0,i);
                    if(arrProvinces.contains(sub)){
                        condition_left.setText(sub);
                        break;
                    }else{
                        condition_left.setText("");
                    }
                }

                if(city!=null){

                    ArrayList<String> citys = mCitisDatasMap.get(condition_left.getText().toString());
                    if(citys!=null && citys.size()>0){
                        for(int i = city.length()-1;i>-1;i--){
                            String sub = city.substring(0,i);
                            if(citys.contains(sub)){
                                condition_middle.setText(sub);
                                break;
                            }else{
                                condition_middle.setText("");
                            }
                        }
                    }else{
                        condition_middle.setText("");
                    }

                    if(district!=null){
                        condition_right.setText(district);
                    }else{
                        condition_right.setText("");
                    }

                }else{
                    if(district!=null){
                        ArrayList<String> citys = mCitisDatasMap.get(condition_left.getText().toString());
                        if(citys!=null && citys.size()>0){
                            for(int i = district.length()-1;i>-1;i--){
                                String sub = district.substring(0,i);
                                if(citys.contains(sub)){
                                    condition_middle.setText(sub);
                                    break;
                                }else{
                                    condition_middle.setText("");
                                }
                            }
                        }else{
                            condition_middle.setText("");
                        }
                    }else{
                        condition_middle.setText("");
                    }
                }

                addMarkerAndInfoWindow();
            } else {
                BaseApplication.getInstances().toast(ConsultantActivity_GoOut_Locate_backup.this,"对不起，没有搜索到相关数据！");
            }
        } else {
            BaseApplication.getInstances().showerror(ConsultantActivity_GoOut_Locate_backup.this,rCode);
        }
    }

    public void addMarkerAndInfoWindow(){
        if(marker!=null){
            marker.hideInfoWindow();
        }
        LatLng latLng = new LatLng(latitude,longitude);
        aMap.clear();
        MarkerOptions otMarkerOptions = new MarkerOptions();
        otMarkerOptions.title(address);
        otMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon));
        otMarkerOptions.position(latLng);
        marker = aMap.addMarker(otMarkerOptions);
        aMap.moveCamera(CameraUpdateFactory.changeLatLng(latLng));

        marker.showInfoWindow();
    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {

                GeocodeAddress address = result.getGeocodeAddressList().get(0);
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));
                this.address = address.getFormatAddress();
                latitude = address.getLatLonPoint().getLatitude();
                longitude = address.getLatLonPoint().getLongitude();
                addMarkerAndInfoWindow();

            } else {
                ToastUtil.show(ConsultantActivity_GoOut_Locate_backup.this,"对不起，没有搜索到相关数据！");
            }

        } else {
            ToastUtil.showerror(ConsultantActivity_GoOut_Locate_backup.this, rCode);
        }
    }

    /**
     * 显示对话框，展示地址信息
     */
    private View dialog;
    private TextView loc_title;
    private TextView location;
    private TextView confirm;
    private String address;
    private double latitude = -181;
    private double longitude = -181;
    private double mylatitude = -181;
    private double mylongitude = -181;

    @Override
    public View getInfoWindow(Marker marker) {
        showLocationInfoDialog();
        return dialog;
    }

    @Override
    public View getInfoContents(Marker marker) {
        showLocationInfoDialog();
        return dialog;
    }

    public void showLocationInfoDialog(){
        dialog = LayoutInflater.from(this).inflate(R.layout.dialog_doctor_out_go_location,null);
        loc_title = (TextView) dialog.findViewById(R.id.loc_title);
        if(longitude==my_longitude && latitude == my_latitude){
            loc_title.setText("您当前的位置:");
        }else{
            loc_title.setText("您选择的位置:");
        }
        location = (TextView) dialog.findViewById(R.id.loc_addr);
        location.setText(address);
        confirm = (TextView) dialog.findViewById(R.id.loc_confirm);
        confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * 点击后返回相应的数据
                 */
                if(address!=null && address.length()>0 && latitude>-181 && longitude>-181){
                    if(getIntent().getStringExtra("flag")!=null&&getIntent().getStringExtra("flag").equals("fromVolunteer")) {
                        Intent intent = new Intent();
                        intent.putExtra(VOLUNTEER_ADDR,address);
                        intent.putExtra(VOLUNTEER_LAT,latitude);
                        intent.putExtra(VOLUNTEER_LON,longitude);
                        setResult(RESULT_OK,intent);
                        finish();
                    }else {
                        Intent intent = new Intent();
                        intent.putExtra(PARAMS_ADDR,address);
                        intent.putExtra(PARAMS_LAT,latitude);
                        intent.putExtra(PARAMS_LON,longitude);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                }else{

                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}
