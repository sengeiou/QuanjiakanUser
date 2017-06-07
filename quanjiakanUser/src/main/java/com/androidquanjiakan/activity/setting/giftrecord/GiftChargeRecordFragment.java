package com.androidquanjiakan.activity.setting.giftrecord;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.activity.setting.ebean.EBeanChargeActivity;
import com.androidquanjiakan.adapter.GiftChargeRecordAdapter;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.entity.GiftChargeEntity;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.util.LogUtil;
import com.google.gson.JsonNull;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/11/4 0004.
 */

public class GiftChargeRecordFragment extends Fragment {

    private PullToRefreshListView listview;
    private ImageView nonedata;
    private TextView nonedatahint;


    private GiftChargeRecordAdapter mAdapter;
    private List<GiftChargeEntity> mData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_gift_send, container, false);
        initView(view);
        return view;
    }

    public void initView(View parent){
        listview = (PullToRefreshListView) parent.findViewById(R.id.listview);
        nonedata = (ImageView) parent.findViewById(R.id.nonedata);
        nonedatahint = (TextView) parent.findViewById(R.id.nonedatahint);

        /**
         * 使用初始的Adapter加载数据
         */
        mData = new ArrayList<>();
        mAdapter = new GiftChargeRecordAdapter(getActivity(),mData);
        listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listview.setAdapter(mAdapter);
        listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageIndex = 1;
                getHistoryOrder(pageIndex);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageIndex ++;
                getHistoryOrder(pageIndex);
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TOOD 似乎只是查看
            }
        });

        pageIndex = 1;
        getHistoryOrder(pageIndex);
    }


    /**
     * 需要发布一个订单后再调试数据
     */
    private final int ROWS = 20;
    private int pageIndex;

    public void getHistoryOrder(final int page) {
        LogUtil.e("加載充值訂單:Page --  "+page);
        try {
            if(mData==null){
                mData = new ArrayList<>();
            }
            if(page==1){
                mData.clear();
            }

            HashMap<String, String> map = new HashMap<>();
            map.put("memberId", BaseApplication.getInstances().getUser_id());
            //1 ---
            JSONObject json = new JSONObject();
            json.put("memberId", Long.parseLong(BaseApplication.getInstances().getUser_id()));
            json.put("page", page);
            json.put("memberId", ROWS);
//            String data = "&data=" + URLEncoder.encode(json.toString(), "utf-8");
            //2 ---
            String paramsData = "{\"memberId\":"+BaseApplication.getInstances().getUser_id()+",\"page\":"+page+",\"rows\":"+ROWS+"}";
            String data = "&data=" + URLEncoder.encode(paramsData, "utf-8");
            MyHandler.putTask(getActivity(), new Task(new HttpResponseInterface() {
                @Override
                public void handMsg(String val) {
                /*
                 */
                    listview.onRefreshComplete();
                    if (val != null && val.length() > 0) {
                        JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                        if (jsonObject.has("code") && !(jsonObject.get("code") instanceof JsonNull) &&
                                "200".equals(jsonObject.get("code").getAsString())) {

                            if (jsonObject.has("rows") && !(jsonObject.get("rows") instanceof JsonNull)) {
                                List<GiftChargeEntity> temp = (List<GiftChargeEntity>) SerialUtil.jsonToObject(jsonObject.get("rows").getAsJsonArray().toString(),
                                        new TypeToken<List<GiftChargeEntity>>(){}.getType());

                                if(temp!=null && temp.size()==ROWS){
                                    LogUtil.e("請求獲取的數量:"+temp.size());
                                    showNoneDataImage(false);
                                    listview.setMode(PullToRefreshBase.Mode.BOTH);
                                }else{
                                    if(temp!=null && temp.size()>0){
                                        LogUtil.e("請求獲取的數量:"+temp.size());
                                        showNoneDataImage(false);
                                    }else{
                                        if(page==1){
                                            showNoneDataImage(true);
                                        }else{
                                            showNoneDataImage(false);
                                        }
                                    }
                                    listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                }
                                mData.addAll(temp);
                                mAdapter.setData(mData);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                if(page==1){
                                    showNoneDataImage(true);
                                }else{
                                    showNoneDataImage(false);
                                }
                                BaseApplication.getInstances().toast(getActivity(),"获取订单历史失败!");
                            }
                        } else {
                            if(page==1){
                                showNoneDataImage(true);
                            }else{
                                showNoneDataImage(false);
                            }
                            BaseApplication.getInstances().toast(getActivity(),"获取订单历史失败!");
                        }
                    } else {
                        if(page==1){
                            showNoneDataImage(true);
                        }else{
                            showNoneDataImage(false);
                        }
                        BaseApplication.getInstances().toast(getActivity(),"获取订单历史失败!");
                    }

                }
            }, HttpUrls.getEBeanHistoryOrder_get() + data, map, Task.TYPE_GET_STRING_NOCACHE, null));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void showNoneDataImage(boolean isShow){
        if(isShow){
            nonedatahint.setText("暂无记录");
            nonedata.setVisibility(View.VISIBLE);
            nonedatahint.setVisibility(View.VISIBLE);
        }else{
            nonedata.setVisibility(View.GONE);
            nonedatahint.setVisibility(View.GONE);
        }
    }

    public void loadData(){

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

}
