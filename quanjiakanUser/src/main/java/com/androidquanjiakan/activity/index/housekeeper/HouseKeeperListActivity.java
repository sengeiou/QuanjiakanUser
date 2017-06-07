package com.androidquanjiakan.activity.index.housekeeper;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.androidquanjiakan.adapter.HouseKeeperListAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.dialog.ChangeAddressDetailDialog;
import com.androidquanjiakan.entity.CompanyListEntity;
import com.androidquanjiakan.entity.HousekeeperTypeEntity;
import com.androidquanjiakan.entity_util.NetUtil;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.entity_util.UnitUtil;
import com.androidquanjiakan.util.LogUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HouseKeeperListActivity extends BaseActivity implements OnClickListener, GeocodeSearch.OnGeocodeSearchListener {

    protected PullToRefreshListView listView;
    protected HouseKeeperListAdapter mAdapter;
    protected ArrayList<JsonObject> mList = new ArrayList<>();
    protected Context context;
    protected TextView tv_title;
    protected ImageButton ibtn_back;
    private LinearLayout condition_line_left;
    private TextView select_condition_left;

    private LinearLayout condition_line_middleleft;
    private TextView select_condition_middleleft;

    private LinearLayout condition_line_right;
    private TextView select_condition_right;

    private LinearLayout select_condition_list;
    private ListView select_condition_list_left;
    private PullToRefreshListView select_condition_list_middleleft;
    private ListView select_condition_list_right;

    private LinearLayout order_condition_line;

    public AMapLocationClientOption mLocationOption = null;
    private AMapLocationClient mlocationClient;
    private GeocodeSearch geocoderSearch;


    public static final int PROVINCE = 1;
    public static final int CITY = 2;
    public static final int AREA = 3;
    public static final int TYPE = 4;

    private Dialog dialogLocation;
    private ArrayList<String> provinces;
    private ArrayList<String> cities;
    private ArrayList<String> sections;
    private ArrayList<String> types;

    private ArrayList<String> arrProvinces = new ArrayList<String>();
    private ArrayList<String> arrTypes = new ArrayList<String>();

    private Map<String, ArrayList<String>> mCitisDatasMap = new HashMap<String, ArrayList<String>>();
    private Map<String, ArrayList<String>> mAreaDatasMap = new HashMap<String, ArrayList<String>>();

    private int solidHeight;
    private int pageIndex = 1;
    private int currentTypeValue = 0;
    private JSONObject mJsonObj;

    private Handler mHandler = new Handler();

    OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            // TODO Auto-generated method stub
            Intent intent = new Intent(context, HouseKeeperDetailActivity.class);
            intent.putExtra("id", mList.get(arg2 - 1).get("id").getAsString());
            startActivityForResult(intent, CommonRequestCode.REQUEST_PAY);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        context = HouseKeeperListActivity.this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_housekeeperlist_newselect);
        initTitleBar();
        initCondition();
        initConditionData();
        initView();
        showNoneData(false);
        locateSelf();
    }

    public void initTitleBar() {
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setOnClickListener(this);
        ibtn_back.setVisibility(View.VISIBLE);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("家政护理");
    }

    public void locateSelf() {
        solidHeight = UnitUtil.dp_To_px(this, 40);

        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation == null || aMapLocation.getLatitude() == 0.0) {
                    //****** 查询无定位的保姆信息
                    pageIndex = 1;
                    mList.clear();
                    loadHouseKeeperListNotPosition(pageIndex);
                    mlocationClient.stopLocation();
                    return;
                }
                LogUtil.e(aMapLocation.getLatitude() + "-----" + aMapLocation.getLongitude() + "");
                LogUtil.e(aMapLocation.getErrorInfo());
                mlocationClient.stopLocation();
                LatLonPoint latLonPoint = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 100,
                        GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                geocoderSearch.getFromLocationAsyn(query);
            }
        });

        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(2000);
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();

        if (NetUtil.isNetworkAvailable(this)) {
            dialogLocation = QuanjiakanDialog.getInstance().getDialog(this);
        } else {
            BaseApplication.getInstances().toast(HouseKeeperListActivity.this,"网络连接不可用!");
        }
    }

    private void initCondition() {
        order_condition_line = (LinearLayout) findViewById(R.id.order_condition_line);

        condition_line_left = (LinearLayout) findViewById(R.id.condition_line_left);
        select_condition_left = (TextView) findViewById(R.id.select_condition_left);
        select_condition_left.setText("地区");

        condition_line_middleleft = (LinearLayout) findViewById(R.id.condition_line_middleleft);
        select_condition_middleleft = (TextView) findViewById(R.id.select_condition_middleleft);

        select_condition_middleleft.setText("公司");
//        condition_line_middleright = (LinearLayout) findViewById(R.id.condition_line_middleright);
//        select_condition_middleright = (TextView) findViewById(R.id.select_condition_middleright);
//        select_condition_middleright.setText("全部");

        condition_line_right = (LinearLayout) findViewById(R.id.condition_line_right);
        select_condition_right = (TextView) findViewById(R.id.select_condition_right);
        select_condition_right.setText("服务");

        select_condition_list = (LinearLayout) findViewById(R.id.select_condition_list);
        select_condition_list.setVisibility(View.GONE);
        select_condition_list.setOnClickListener(this);

        select_condition_list_left = (ListView) findViewById(R.id.select_condition_list_left);
        select_condition_list_middleleft = (PullToRefreshListView) findViewById(R.id.select_condition_list_middleleft);
        select_condition_list_middleleft.setVisibility(View.GONE);
        select_condition_list_middleleft.setMode(PullToRefreshBase.Mode.DISABLED);
        select_condition_list_middleleft.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getCompanyList();
//                select_condition_list_middleleft.onRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getMoreCompanyData();
            }
        });
//        select_condition_list_middleright = (ListView) findViewById(R.id.select_condition_list_middleright);
        select_condition_list_right = (ListView) findViewById(R.id.select_condition_list_right);

        condition_line_left.setOnClickListener(this);
        condition_line_middleleft.setOnClickListener(this);
//        condition_line_middleright.setOnClickListener(this);
        condition_line_right.setOnClickListener(this);
    }

    private void initConditionData() {
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
            arrProvinces.add("全部");
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
            arrTypes.add("全部");
            arrTypes.add("保姆");
            arrTypes.add("保洁");
            LogUtil.e("load local info end!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ImageView nonedata;
    private TextView nonedatahint;
    public void showNoneData(boolean isShow){
        if(isShow){
            nonedatahint.setText("暂无数据");
            nonedata.setVisibility(View.VISIBLE);
            nonedatahint.setVisibility(View.VISIBLE);
        }else{
            nonedata.setVisibility(View.GONE);
            nonedatahint.setVisibility(View.GONE);
        }
    }

    protected void initView() {

        nonedata = (ImageView) findViewById(R.id.nonedata);
        nonedatahint = (TextView) findViewById(R.id.nonedatahint);

        listView = (PullToRefreshListView) findViewById(R.id.listview);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        mAdapter = new HouseKeeperListAdapter(context, mList);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(onItemClickListener);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (mProvince != null) {
                    listView.setMode(PullToRefreshBase.Mode.BOTH);
                    pageIndex = 1;
                    mList.clear();
                    loadHouseKeeperList(pageIndex);

//                    loadHouseKeeperListNotPosition(pageIndex);
                } else {
                    locateSelf();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageIndex += 1;

                loadHouseKeeperList(pageIndex);

                //getHouseKeeperList
//                loadHouseKeeperListNotPosition(pageIndex);
            }
        });
        if (mList == null) {
            mList = new ArrayList<JsonObject>();
        } else {
            mList.clear();
        }
    }

    protected void loadHouseKeeperList(final int page) {
        LogUtil.e("typeID:"+typeId);
        MyHandler.putTask(this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                listView.onRefreshComplete();
                if(page==1){
                    mList.clear();
                    mAdapter = new HouseKeeperListAdapter(context, mList);
                    listView.setAdapter(mAdapter);
                }
                JSONObject jsonObject = null;
                List<JsonObject> list = null;
                try {
                    if (val != null && val.length() > 0) {
                        jsonObject = new JSONObject(val);
                        if (jsonObject != null && jsonObject.has("rows")) {

                            list = new GsonParseUtil(jsonObject.getString("rows")).getJsonList();
                            if (list != null && list.size() == 15) {
                                listView.setMode(PullToRefreshBase.Mode.BOTH);
                            } else {
                                listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            }
                            mList.addAll(list);
                        }else{
                            listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        }

                        if(mList!=null && mList.size()>0){
                            showNoneData(false);
                        }else{
                            showNoneData(true);
                        }
                    } else {
                        listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

                        if(mList!=null && mList.size()>0){
                            showNoneData(false);
                        }else{
                            showNoneData(true);
                        }
                    }

//                    listView.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            listView.onRefreshComplete();
//                        }
//                    }, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mAdapter.notifyDataSetChanged();
            }
        }, HttpUrls.getHouseKeeperList(page,(mProvince!=null?  mProvince.replace("省", ""):null),
                (mCity!=null ? mCity.replace("市", ""):null),
                (mSection!=null ? mSection:null),
                typeId,//new
                companyid)+"&rows=15",//里面还应该进行处理
                null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(context)));
    }

    protected void loadHouseKeeperListNotPosition(int page) {
        MyHandler.putTask(this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                // TODO Auto-generated method stub
                try {
                    if (val != null && val.length() > 0) {
                        JSONObject jsonObject = new JSONObject(val);
                        if (jsonObject != null && jsonObject.has("rows")) {

                            List<JsonObject> list = new GsonParseUtil(jsonObject.getString("rows")).getJsonList();
                            mList.addAll(list);
                            mAdapter.notifyDataSetChanged();
                            if (list != null && list.size() ==20 ) {
                                listView.setMode(PullToRefreshBase.Mode.BOTH);
                            } else {
                                listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            }
                        }else{
                            listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        }
                    } else {

                    }
                    listView.onRefreshComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, HttpUrls.getHouseKeeperList(page, null,
                null,
                null,
                typeId,
                companyid),//里面还应该进行处理
                null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(context)));
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        switch (arg0.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.condition_line_left:
                hideCondition();
                showAddressDialog();
                break;
            case R.id.condition_line_middleleft:
                /**
                 * @TODO 根据当前选择的地址，获取公司列表，然后根据选择的公司列表再次过滤数据并刷新
                 */
                if (currentTypeValue == CITY && isConditionShow()) {
                    hideCondition();
                } else {
                    getCompanyList();
                }
                currentTypeValue = CITY;
                break;
            case R.id.condition_line_middleright:
                break;
            case R.id.condition_line_right:
                if (currentTypeValue == TYPE && isConditionShow()) {
                    hideCondition();
                } else {
                    //Old *****
//                    showSelectCondition(TYPE, arrTypes);
                    //New *****
                    getTypeList();
                }
                currentTypeValue = TYPE;
                break;
        }
    }

    private String typeId = null;

    public void getTypeList() {
        if (typeListEntities != null && typeListEntities.size() > 0) {
            showTypeList(typeListEntities);
        } else {
            MyHandler.putTask(this, new Task(new HttpResponseInterface() {
                @Override
                public void handMsg(String val) {
                    if (val != null && val.length() > 0 && !"null".equals(val)) {
                        JsonArray jsonArray = new GsonParseUtil(val).getJsonArray();
                        typeListEntities = (List<HousekeeperTypeEntity>) SerialUtil.jsonToObject(jsonArray.toString(), new TypeToken<List<HousekeeperTypeEntity>>() {
                        }.getType());
                        if (jsonArray.size() > 0) {
                            showTypeList(typeListEntities);
                        } else {
                            BaseApplication.getInstances().toast(HouseKeeperListActivity.this,"无服务类型数据!");
                        }
                    } else {
                        BaseApplication.getInstances().toast(HouseKeeperListActivity.this,"未获取到服务类型数据!");
                    }
                }
            }, HttpUrls.getHousekeeperTypeList(),
                    null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(this)));
        }
    }

    private List<CompanyListEntity> companyListEntities;
    private int companyPage;

    public void getCompanyList() {
        if (select_condition_left.getText().length() < 1 || "位置".equals(select_condition_left.getText().toString().trim())) {
//            BaseApplication.getInstances().toast("请先选择位置!");
            if (companyid != null) {
                showCompanyList(companyListEntities);
            } else {
                companyPage = 1;
                MyHandler.putTask(this, new Task(new HttpResponseInterface() {
                    @Override
                    public void handMsg(String val) {
                        if (val != null && val.length() > 0 && !"null".equals(val)) {
                            //Old
//                            JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
//                            JsonArray jsonArray = jsonObject.get("rows").getAsJsonArray();
                            companyListEntities = (List<CompanyListEntity>) SerialUtil.jsonToObject(val, new TypeToken<List<CompanyListEntity>>() {
                            }.getType());
                            if (companyListEntities!=null && companyListEntities.size() > 0) {
                                showCompanyList(companyListEntities);
                            } else {
                                BaseApplication.getInstances().toast(HouseKeeperListActivity.this,"该地区无公司!");
                            }
                        } else {
                            BaseApplication.getInstances().toast(HouseKeeperListActivity.this,"该地区未查找到相关公司");
                        }
                    }
                }, HttpUrls.getHouseKeeperCompanyList(null, null, null)/* + "&page=" + companyPage + "&rows=20"*/,
                        null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(this)));
            }
        } else {
            /**
             *
             通过地区查询家政公司
             http://192.168.0.117:8080/quanjiakan/api?m=housekeeper&a=getaerahousecompanylist&province=广东&city=广州&area=天河区
             */
//            BaseApplication.getInstances().toast("开始获取公司列表");
            if (companyid != null) {
                showCompanyList(companyListEntities);
            } else {
                companyPage = 1;
                MyHandler.putTask(this, new Task(new HttpResponseInterface() {
                    @Override
                    public void handMsg(String val) {
                        if (val != null && val.length() > 0 && !"null".equals(val)) {
                            //Old
//                            JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
//                            JsonArray jsonArray = jsonObject.get("rows").getAsJsonArray();
                            companyListEntities = (List<CompanyListEntity>) SerialUtil.jsonToObject(val, new TypeToken<List<CompanyListEntity>>() {
                            }.getType());
                            if (companyListEntities!=null && companyListEntities.size() > 0) {
                                showCompanyList(companyListEntities);
                            } else {
                                BaseApplication.getInstances().toast(HouseKeeperListActivity.this,"该地区无公司!");
                            }
                        } else {
                            BaseApplication.getInstances().toast(HouseKeeperListActivity.this,"该地区未查找到相关公司");
                        }
                    }
                }, HttpUrls.getHouseKeeperCompanyList(mProvince.replace("省", ""), mCity.replace("市", ""), mSection) + "&page=" + companyPage + "&rows=20",
                        null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(this)));
            }

        }
    }

    public void getMoreCompanyData() {
        companyPage++;
        MyHandler.putTask(this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if (val != null && val.length() > 0 && !"null".equals(val)) {
//                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
//                    JsonArray jsonArray = jsonObject.get("rows").getAsJsonArray();
                    List<CompanyListEntity> companyListEntities_temp = (List<CompanyListEntity>) SerialUtil.jsonToObject(val, new TypeToken<List<CompanyListEntity>>() {
                    }.getType());
                    for (CompanyListEntity temp : companyListEntities_temp) {
                        nameList.add(temp.getName());
                    }
                    companyListEntities.addAll(companyListEntities_temp);
                    if (companyListEntities_temp!=null && companyListEntities_temp.size() > 0) {
                        if (companyAdapter != null) {
                            companyAdapter.notifyDataSetChanged();
                        }
                    } else {
                        BaseApplication.getInstances().toast(HouseKeeperListActivity.this,"无更多公司!");
                    }
                } else {
                    BaseApplication.getInstances().toast(HouseKeeperListActivity.this,"无更多公司!");
                }
                select_condition_list_middleleft.onRefreshComplete();
            }
        }, HttpUrls.getHouseKeeperCompanyList(mProvince.replace("省", ""), mCity.replace("市", ""), mSection) + "&page=" + companyPage + "&rows=20",
                null, Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(this)));
    }

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

                select_condition_list_left.setVisibility(View.VISIBLE);
//                select_condition_list_middleleft.setVisibility(View.INVISIBLE);
                select_condition_list_middleleft.setVisibility(View.GONE);
//                select_condition_list_middleright.setVisibility(View.INVISIBLE);
                select_condition_list_right.setVisibility(View.INVISIBLE);
                break;
            case CITY:

                select_condition_list_left.setVisibility(View.GONE);
//                select_condition_list_middleleft.setVisibility(View.VISIBLE);
                select_condition_list_middleleft.setVisibility(View.GONE);
//                select_condition_list_middleright.setVisibility(View.INVISIBLE);
                select_condition_list_right.setVisibility(View.GONE);
                break;
            case AREA:

                select_condition_list_left.setVisibility(View.INVISIBLE);
//                select_condition_list_middleleft.setVisibility(View.INVISIBLE);
                select_condition_list_middleleft.setVisibility(View.GONE);
//                select_condition_list_middleright.setVisibility(View.VISIBLE);
                select_condition_list_right.setVisibility(View.INVISIBLE);
                break;
            case TYPE:

                select_condition_list_left.setVisibility(View.INVISIBLE);
//                select_condition_list_middleleft.setVisibility(View.INVISIBLE);
                select_condition_list_middleleft.setVisibility(View.GONE);
//                select_condition_list_middleright.setVisibility(View.INVISIBLE);
                select_condition_list_right.setVisibility(View.VISIBLE);
                break;
            default:
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
//            case PROVINCE://省
//                adapter = new ArrayAdapter<String>(this, R.layout.item_select_condition_medicine, arrProvinces);
//                select_condition_list_left.setAdapter(adapter);
//                select_condition_list_left.setOnItemClickListener(new OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                        if (arrProvinces.get(position).equals(select_condition_left.getText().toString())) {
//                            hideCondition();
//                        } else {
//                            /**
//                             * 从最大位置开始过滤
//                             */
////                            select_condition_left.setText(arrProvinces.get(position));
////                            select_condition_middleleft.setText("全部");
////                            select_condition_middleright.setText("全部");
//                            /**
//                             * 默认第一个市
//                             */
////                            ArrayList<String> city = mCitisDatasMap.get(select_condition_left.getText().toString());
////                            select_condition_middleleft.setText(city.get(1));
////                            select_condition_middleright.setText("全部");
//
//                            /**
//                             *  默认第一个市，第一个区
//                             */
//                            if("全部".equals(arrProvinces.get(position))){
//                                select_condition_left.setText("全部");
////                                select_condition_middleright.setText("全部");
//                                select_condition_middleleft.setText("全部");
//                            }else{
//                                select_condition_left.setText(arrProvinces.get(position));
//                                ArrayList<String> city = mCitisDatasMap.get(arrProvinces.get(position));
//                                if(city!=null && city.size()>0){
//                                    if("全部".equals(city.get(0)) && city.size()>1){
//                                        select_condition_middleleft.setText(city.get(1));
//                                    }else{
//                                        select_condition_middleleft.setText(city.get(0));
//                                    }
//                                }else{
//                                    select_condition_middleleft.setText("全部");
//                                }
//                                ArrayList<String> area = mAreaDatasMap.get(select_condition_middleleft.getText().toString());
//                                if(area!=null && area.size()>0){
////                                    select_condition_middleright.setText(area.get(0));
//
//                                    if("全部".equals(area.get(0)) && area.size()>1){
//                                        select_condition_middleright.setText(area.get(1));
//                                    }else{
//                                        select_condition_middleright.setText(area.get(0));
//                                    }
//                                }else{
//                                    select_condition_middleright.setText("全部");
//                                }
//                            }
//
//
//                            hideCondition();
//                            /**
//                             * @TODO 需要添加对选中条件后展示数据的过滤操作
//                             */
//                            filter();
//                        }
//                    }
//                });
//
//                break;
//            case CITY://市
//                if ("全部".equals(select_condition_left.getText().toString()) || !arrProvinces.contains(select_condition_left.getText().toString())) {
//                    BaseApplication.getInstances().toast("请选择一个确定的省份!");
//                    hideCondition();
//                } else if (data == null) {
//                    BaseApplication.getInstances().toast("该地区无更小行政区域");
//                    hideCondition();
//                } else {
//                    adapter = new ArrayAdapter<String>(this, R.layout.item_select_condition_medicine, data);
//                    select_condition_list_middleleft.setAdapter(adapter);
//                    select_condition_list_middleleft.setOnItemClickListener(new OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                                if (data.get(position).equals(select_condition_middleleft.getText().toString())) {
//                                    hideCondition();
//                                } else {
//                                    /**
//                                     * 全部
//                                     */
////                                    select_condition_middleleft.setText(data.get(position));
////                                    select_condition_middleright.setText("全部");
//
//                                    /**
//                                     * 默认第一个区
//                                     */
//                                    select_condition_middleleft.setText(data.get(position));
//                                    ArrayList<String> area = mAreaDatasMap.get(data.get(position));
//                                    if(area!=null && area.size()>0){
////                                        select_condition_middleright.setText(area.get(0));
//
//                                        if("全部".equals(area.get(0)) && area.size()>1){
//                                            select_condition_middleright.setText(area.get(1));
//                                        }else{
//                                            select_condition_middleright.setText(area.get(0));
//                                        }
//                                    }else{
//                                        select_condition_middleright.setText("全部");
//                                    }
//
//
//
//                                    hideCondition();
//                                    /**
//                                     * @TODO 需要添加对选中条件后展示数据的过滤操作
//                                     */
//                                    filter();
//                                }
//                        }
//                    });
//                }
//
//                break;
//            case AREA://区
//                if ("全部".equals(select_condition_middleleft.getText().toString())) {
//                    BaseApplication.getInstances().toast("请选择一个确定的城市!");
//                    hideCondition();
//                } else if (data == null) {
//                    BaseApplication.getInstances().toast("该地区无更小行政区域");
//                    hideCondition();
//                } else {
//                    adapter = new ArrayAdapter<String>(this, R.layout.item_select_condition_medicine, data);
//                    select_condition_list_middleright.setAdapter(adapter);
//                    select_condition_list_middleright.setOnItemClickListener(new OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                            if (data.get(position).equals(select_condition_middleright.getText().toString())) {
//                                hideCondition();
//                            } else {
//                                if ("全部".equals(data.get(position))) {
//                                    select_condition_middleright.setText("全部");
//                                }
//                                select_condition_middleright.setText(data.get(position));
//                                hideCondition();
//                                /**
//                                 * @TODO 需要添加对选中条件后展示数据的过滤操作------由于接口只做两层过滤，所以该条件已不起作用
//                                 */
////                                filter();
//                            }
//                        }
//                    });
//                }
//                break;
            case TYPE:
                adapter = new ArrayAdapter<String>(this, R.layout.item_select_condition_medicine, arrTypes);
                select_condition_list_right.setAdapter(adapter);
                select_condition_list_right.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        if (arrTypes.get(position).equals(select_condition_right.getText().toString())) {
                            hideCondition();
                        } else {
                            select_condition_right.setText(arrTypes.get(position));
                            hideCondition();
                            /**
                             * @TODO 需要添加对选中条件后展示数据的过滤操作
                             */
                            filter();
                        }
                    }
                });
                break;
            default:
        }

    }


    private String companyid;
    private ArrayAdapter<String> companyAdapter;
    private ArrayList<String> nameList;
    public void showCompanyList(final List<CompanyListEntity> data) {
        showCondition(CITY);
        nameList = new ArrayList<String>();
        for (CompanyListEntity temp : companyListEntities) {
            nameList.add(temp.getName());
        }
        companyAdapter = new ArrayAdapter<String>(this, R.layout.item_select_condition_medicine, nameList);
        select_condition_list_middleleft.setAdapter(companyAdapter);
        select_condition_list_middleleft.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (data.get(position-1).equals(select_condition_middleleft.getText().toString())) {
                    hideCondition();
                } else {
                    companyid = companyListEntities.get((int) l).getCompany_id();
                    hideCondition();
                    /**
                     * @TODO 需要添加对选中条件后展示数据的过滤操作
                     */
                    filter();
                }
            }
        });
        select_condition_list_middleleft.onRefreshComplete();
    }

    private List<HousekeeperTypeEntity> typeListEntities;

    public void showTypeList(final List<HousekeeperTypeEntity> data) {
        showCondition(TYPE);
        ArrayList<String> nameList = new ArrayList<String>();
        for (HousekeeperTypeEntity temp : data) {
            nameList.add(temp.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_select_condition_medicine, nameList);
        select_condition_list_right.setAdapter(adapter);
        select_condition_list_right.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (data.get(position).equals(select_condition_right.getText().toString())) {
                    hideCondition();
                } else {
                    typeId = typeListEntities.get((int) l).getId();
                    LogUtil.e("选择的服务类型ID typeId:"+typeId);
                    select_condition_right.setText(typeListEntities.get((int) l).getName());
                    hideCondition();
                    /**
                     * @TODO 需要添加对选中条件后展示数据的过滤操作
                     */
                    filter();
                }
            }
        });
    }

    /**
     * 过滤操作
     */
    public void filter() {
        /**
         * TODO 根据四个条件来过滤数据--省-市-区、公司、类型
         */
        pageIndex = 1;
        mList.clear();
        loadHouseKeeperList(pageIndex);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CommonRequestCode.REQUEST_PAY:
                if (resultCode == CommonRequestCode.RESULT_BACK_TO_MAIN) {
                    setResult(CommonRequestCode.RESULT_BACK_TO_MAIN);
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        dialogLocation.dismiss();
        if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null) {
            LogUtil.e("getProvince:" + regeocodeResult.getRegeocodeAddress().getProvince() + "\ngetCity:" + regeocodeResult.getRegeocodeAddress().getCity() + "\ngetDistrict:" + regeocodeResult.getRegeocodeAddress().getDistrict()
                    + "\n getAdCode:" + regeocodeResult.getRegeocodeAddress().getAdCode()
                    + "\n getBuilding:" + regeocodeResult.getRegeocodeAddress().getBuilding()
                    + "\n getCityCode:" + regeocodeResult.getRegeocodeAddress().getCityCode()
                    + "\n getFormatAddress:" + regeocodeResult.getRegeocodeAddress().getFormatAddress()
                    + "\n getNeighborhood:" + regeocodeResult.getRegeocodeAddress().getNeighborhood()
                    + "\n getTowncode:" + regeocodeResult.getRegeocodeAddress().getTowncode()
                    + "\n getTownship:" + regeocodeResult.getRegeocodeAddress().getTownship()
                    + "\n getStreetNumber().getStreet():" + regeocodeResult.getRegeocodeAddress().getStreetNumber().getStreet()
                    + "\n getStreetNumber().getNumber():" + regeocodeResult.getRegeocodeAddress().getStreetNumber().getNumber()
                    + "\n getStreetNumber().getDistance():" + regeocodeResult.getRegeocodeAddress().getStreetNumber().getDistance()
                    + "\n getStreetNumber().getDirection():" + regeocodeResult.getRegeocodeAddress().getStreetNumber().getDirection());
            /**
             *
             */
            mProvince = regeocodeResult.getRegeocodeAddress().getProvince();
            mCity = regeocodeResult.getRegeocodeAddress().getCity();
            mSection = regeocodeResult.getRegeocodeAddress().getDistrict();


            if (regeocodeResult.getRegeocodeAddress().getDistrict() != null && regeocodeResult.getRegeocodeAddress().getDistrict().length() > 0) {
                select_condition_left.setText(regeocodeResult.getRegeocodeAddress().getDistrict());
            } else if (regeocodeResult.getRegeocodeAddress().getCity() != null && regeocodeResult.getRegeocodeAddress().getCity().length() > 0) {
                select_condition_left.setText(regeocodeResult.getRegeocodeAddress().getCity());
            } else if (regeocodeResult.getRegeocodeAddress().getProvince() != null && regeocodeResult.getRegeocodeAddress().getProvince().length() > 0) {
                select_condition_left.setText(regeocodeResult.getRegeocodeAddress().getProvince().replace("省", ""));
            }


//            select_condition_middleleft.setText(regeocodeResult.getRegeocodeAddress().getCity().replace("市",""));

//            select_condition_middleright.setText("荔湾区");
            /**
             * 是否应该再次执行一遍获取数据的请求--------还是说等定位获取到了之后再请求数据
             */
            pageIndex = 1;
            mList.clear();
            loadHouseKeeperList(pageIndex);

//            loadHouseKeeperListNotPosition(pageIndex);


        } else {
            LogUtil.e("regeocodeResult == null");
//            BaseApplication.getInstances().toast("获取当前位置失败!");
            /**
             * 查询全部数据
             */
            //****** 查询无定位的保姆信息
            pageIndex = 1;
            mList.clear();
            loadHouseKeeperListNotPosition(pageIndex);
        }

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    private String mProvince;
    private String mCity;
    private String mSection;

    public void showAddressDialog() {
        ChangeAddressDetailDialog dialog = new ChangeAddressDetailDialog(HouseKeeperListActivity.this);
        dialog.setAddresskListener(new ChangeAddressDetailDialog.OnAddressCListener() {
            @Override
            public void onClick(String province, String city, String section) {
                if (mProvince != null && mProvince.equals(province)
                        && mCity != null && mCity.equals(city)
                        && mSection != null && mSection.equals(section)) {

                } else {
                    companyid = null;
                }

                mProvince = province;
                mCity = city;
                mSection = section;

                if (section != null && section.length() > 0) {
                    select_condition_left.setText(section);
                } else if (city != null && city.length() > 0) {
                    select_condition_left.setText(city);
                } else if (province != null && province.length() > 0) {
                    select_condition_left.setText(province);
                }

//                select_condition_middleleft.setText(city );

//                select_condition_middleright.setText(section );

                filter();

            }
        });
        dialog.show();
    }

}
