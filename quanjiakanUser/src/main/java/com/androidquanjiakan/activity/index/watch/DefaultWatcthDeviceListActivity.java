package com.androidquanjiakan.activity.index.watch;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquanjiakan.activity.index.MainEntryMapActivity;
import com.androidquanjiakan.adapter.DefaultWatchDeviceAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.constant.ConstantClassFunction;
import com.androidquanjiakan.datahandler.BindDeviceHandler;
import com.androidquanjiakan.entity.BindDeviceEntity;
import com.androidquanjiakan.entity.DefaultWatchEntity;
import com.androidquanjiakan.entity.WatchMapDevice;
import com.androidquanjiakan.entity.WatchMapDevice_DeviceInfo;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.util.LogUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gin on 2017/2/23.
 */

public class DefaultWatcthDeviceListActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton ibtn_back;
    private TextView tv_title;
    private ListView listview;
    private List<DefaultWatchEntity> list;
    private Context context;
    private DefaultWatchDeviceAdapter defaultWatchDeviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_watch_device);
        context=DefaultWatcthDeviceListActivity.this;
        initTitle();
        initView();
        initData();
    }

    private void initData() {
        if (list!=null) {
            list.clear();
        }

        MyHandler.putTask(DefaultWatcthDeviceListActivity.this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                LogUtil.e("val---------"+val);
                if (val!=null&&val.length()>0) {
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if (jsonObject.has(ConstantClassFunction.getRESULTS())) {
                        JsonObject results = jsonObject.getAsJsonObject(ConstantClassFunction.getRESULTS());
                        if (results.has(ConstantClassFunction.getCATEGORY())&&"WatchList".equals(results.get(ConstantClassFunction.getCATEGORY()).getAsString())) {
                            JsonArray watchList = results.getAsJsonArray("WatchList");
                            for (int i=watchList.size()-1;i>=0;i--) {
                                JsonObject watch = watchList.get(i).getAsJsonObject();
                                if (watch.has("IMEI")&&watch.has("Name")&&watch.has("Picture")) {
                                    DefaultWatchEntity entity = new DefaultWatchEntity();
                                    entity.setName(watch.get("Name").getAsString());
                                    entity.setIcon(watch.get("Picture").getAsString());
                                    entity.setDeviceNumber(watch.get("IMEI").getAsString());
                                    LogUtil.e("-----default-----"+BaseApplication.getInstances().getDefaultDevice());
                                    if (BaseApplication.getInstances().getDefaultDevice()!=null&&BaseApplication.getInstances().getDefaultDevice().length()>1) {
                                        if (BaseApplication.getInstances().getDefaultDevice().equals(watch.get("IMEI").getAsString())){
                                            entity.setSelect(true);
                                        }else {
                                            entity.setSelect(false);
                                        }

                                    }else {
                                        if (i==watchList.size()-1){
                                            entity.setSelect(true);
                                            //保存imei到sp
                                            BaseApplication.getInstances().setDefaultDevice(watch.get("IMEI").getAsString());
                                        }else {
                                            entity.setSelect(false);
                                        }

                                    }

                                    list.add(entity);
                                }
                            }
                            defaultWatchDeviceAdapter.setList(list);
                            defaultWatchDeviceAdapter.notifyDataSetChanged();
                        }
                    }
                }


            }
        }, HttpUrls.getBindDevices_new(), null, Task.TYPE_GET_STRING_NOCACHE, null));
    }

    private void initView() {
        listview = (ListView) findViewById(R.id.listview);
        list = new ArrayList<>();
        defaultWatchDeviceAdapter = new DefaultWatchDeviceAdapter(context, list);
        listview.setAdapter(defaultWatchDeviceAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (int t=0;t<list.size();t++){
                    if(t==(int)l) {
                        DefaultWatchEntity defaultWatchEntity = list.get(t);
                        // TODO: 2017/3/25 改变默认设备
                        defaultWatchEntity.setSelect(true);
                        //保存imei---->sp
                        BaseApplication.getInstances().setDefaultDevice(defaultWatchEntity.getDeviceNumber());
                    }else{
                        list.get(t).setSelect(false);
                    }
                    defaultWatchDeviceAdapter.notifyDataSetChanged();
                }


            }
        });


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

    private void initTitle() {
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setOnClickListener(this);
        ibtn_back.setVisibility(View.VISIBLE);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("默认手表设备");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibtn_back:
                finish();
                break;
        }




    }
}
