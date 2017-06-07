package com.androidquanjiakan.activity.index.volunteer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.androidquanjiakan.adapter.VolunteerEntryAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.entity.VolunteerStationEntity;
import com.androidquanjiakan.entity_util.SerialUtil;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VolunteerEntryActivity extends BaseActivity implements View.OnClickListener, AMapLocationListener {


    private ImageButton mBack;
    private TextView mTitle;
    private TextView mMenu;

    private TextView mHint;
    private TextView mPublish;
    private ImageView mBanner;
    private PullToRefreshListView mList;

    private VolunteerEntryAdapter mAdapter;
    private final int MSG_LOCATION_FINISH=1;

    //	private List<JsonObject> mData = new ArrayList<JsonObject>();
//	private List<VolunteerStationEntity> mData = new ArrayList<VolunteerStationEntity>();
    private int pageIndex = 1;
    private AMapLocationClient mlocationClient;
    private double latitude;
    private double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_volunteer_entry);
        initTitle();
        initMap();
        initContent();

    }

    private void initMap() {
        AMapLocationClientOption mLocationOption = null;
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        mlocationClient.setLocationListener(this);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        mLocationOption.setNeedAddress(true);
        //设置为是单次定位
        mLocationOption.setOnceLocation(true);
        mlocationClient.setLocationOption(mLocationOption);
        mlocationClient.startLocation();




    }

    public void initTitle() {
        mBack = (ImageButton) findViewById(R.id.ibtn_back);
        mBack.setVisibility(View.VISIBLE);
        mBack.setOnClickListener(this);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mTitle.setText("义工服务");
        mMenu = (TextView) findViewById(R.id.menu_text);
        mMenu.setText("发布记录");
        mMenu.setVisibility(View.VISIBLE);
        mMenu.setOnClickListener(this);
    }

    public void initContent() {
        mHint = (TextView) findViewById(R.id.hint);
        mPublish = (TextView) findViewById(R.id.publish);
        mPublish.setOnClickListener(this);
        mBanner = (ImageView) findViewById(R.id.banner);

        /**
         * Resize Image Banner
         */
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mBanner.getLayoutParams();
        int width = getResources().getDisplayMetrics().widthPixels;
        float real = 440 * width / 1079 * 1.0f;
        layoutParams.height = (int) real;
        layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        mBanner.setLayoutParams(layoutParams);


        mList = (PullToRefreshListView) findViewById(R.id.list);
        mList.setMode(PullToRefreshBase.Mode.BOTH);
        mList.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//				initContentData();
                pageIndex = 1;
                loadData(pageIndex);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageIndex++;
                loadData(pageIndex);
            }
        });

        mAdapter = new VolunteerEntryAdapter(this, mData);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(VolunteerEntryActivity.this, VolunteerStationDetailActivity.class);
                intent.putExtra("station_info", mAdapter.getData().get(position - 1));
                intent.putExtra("station_id", mAdapter.getData().get(position - 1).getId());
                startActivity(intent);
            }
        });
        loadData(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
        BaseApplication.getInstances().setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    List<VolunteerStationEntity> mData;

    public void loadData(final int page) {
        HashMap<String, String> params = new HashMap<>();
        params.put("member_id", QuanjiakanSetting.getInstance().getUserId()+"");
        params.put("lat", latitude+"");
        params.put("lng", longitude+"");
        MyHandler.putTask(this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
               //"rows":[{"id":"3","name":"全家康服务站-1","lat":"22.6325","lng":"113.5412","phone":"18503056325","contactor":"付凯","address":"荔湾区逢源路448号","province":"广东","city":"广州","dist":"荔湾区","photo":"http://familycareapi.oss-cn-shanghai.aliyuncs.com/3r1etmts71c4.jpg","dscpt":"服务站描述","duty_date":"周一到周五","duty_time":"09:00~17:00","services":null,"other_services":null,"status":"1","createtime":"2016-08-21 09:52:18.0","attended":"1","hasattend":"0"}],"total":1
                mList.onRefreshComplete();
                if (val != null && val.length() > 0 && !"null".equals(val.toLowerCase())) {
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                        JsonArray datas = jsonObject.get("rows").getAsJsonArray();
                        mData = (List<VolunteerStationEntity>) SerialUtil.jsonToObject(datas.toString(), new TypeToken<List<VolunteerStationEntity>>() {
                        }.getType());
                        if (mAdapter == null) {
                            if (mData != null && mData.size() > 0) {
                                mAdapter = new VolunteerEntryAdapter(VolunteerEntryActivity.this, mData);
                            } else {
                                mAdapter = new VolunteerEntryAdapter(VolunteerEntryActivity.this, new ArrayList<VolunteerStationEntity>());
                            }
                        } else {
                            if (mData != null && mData.size() > 0) {
                                if (page > 1) {
                                    mAdapter.getData().addAll(mData);
                                } else {
                                    mAdapter.setData(mData);
                                }
                            } else {
                                if (page > 1) {
                                    mAdapter.getData().addAll(new ArrayList<VolunteerStationEntity>());
                                } else {
                                    mAdapter.setData(new ArrayList<VolunteerStationEntity>());
                                }
                            }

                        }
                        mAdapter.notifyDataSetChanged();



                } else {
                    if (mAdapter == null) {
                        mAdapter = new VolunteerEntryAdapter(VolunteerEntryActivity.this, new ArrayList<VolunteerStationEntity>());
                    } else {
                        if (page > 1) {
                            mAdapter.getData().addAll(new ArrayList<VolunteerStationEntity>());
                        } else {
                            mAdapter.setData(new ArrayList<VolunteerStationEntity>());
                        }
                    }
                }
            }
        }, HttpUrls.getStationList() + "&page=" + page, params, Task.TYPE_POST_DATA_PARAMS, null));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.menu_text:
                Intent publish = new Intent(this, VolunteerPublishHistoryActivity.class);
                startActivity(publish);
                break;
            case R.id.publish:
                Intent create = new Intent(this, VolunteerCreateActivity.class);
                startActivity(create);
                break;
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what){
                case MSG_LOCATION_FINISH:
                    AMapLocation obj = (AMapLocation) msg.obj;
                    latitude =   obj.getLatitude();
                    longitude = obj.getLongitude();
                    //initContent();
                    break;
            }

            
        }
    };

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation!=null&&aMapLocation.getErrorCode() == 0) {
            Message message = handler.obtainMessage();
            message.what=MSG_LOCATION_FINISH;
            message.obj=aMapLocation;
            handler.sendMessage(message);

        }

    }
}
