package com.androidquanjiakan.activity.setting.giftrecord;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.adapter.SendGiftAdapter;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.entity.RecieveGiftEntity;
import com.androidquanjiakan.entity_util.NetUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gin on 2017/1/12.
 */

public class SendGiftFragment extends Fragment {

    private PullToRefreshListView pull_to_refresh_listview;
    private List<RecieveGiftEntity> datas;
    private int index=1;
    private SendGiftAdapter sendGiftAdapter;
    private SimpleDateFormat sdf;

    private ImageView nonedata;
    private TextView nonedatahint;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gift_send, container,false);
        pull_to_refresh_listview = (PullToRefreshListView) view.findViewById(R.id.listview);
        pull_to_refresh_listview.setMode(PullToRefreshBase.Mode.BOTH);

        nonedata = (ImageView) view.findViewById(R.id.nonedata);
        nonedatahint = (TextView) view.findViewById(R.id.nonedatahint);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        super.onActivityCreated(savedInstanceState);
        datas=new ArrayList<>();


        pull_to_refresh_listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            //下拉刷新
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                refreshView.getLoadingLayoutProxy().setRefreshingLabel("正在刷新");
                refreshView.getLoadingLayoutProxy().setPullLabel("下拉刷新更多");
                refreshView.getLoadingLayoutProxy().setReleaseLabel("释放开始刷新");
                datas.clear();
                index = 1;
                loadSendGiftData(index);

            }

            //上拉加载
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                refreshView.getLoadingLayoutProxy().setRefreshingLabel("正在加载");
                refreshView.getLoadingLayoutProxy().setPullLabel("上拉加载更多");
                refreshView.getLoadingLayoutProxy().setReleaseLabel("释放开始加载");
                if(sendGiftAdapter.getCount()<((index)*20)){
                    pull_to_refresh_listview.onRefreshComplete();
                    Toast.makeText(getActivity(), "数据已加载完", Toast.LENGTH_SHORT).show();
                }else{
                    ++index;
                    loadSendGiftData(index);
                }

            }
        });

        sendGiftAdapter = new SendGiftAdapter(getActivity(), datas);
        pull_to_refresh_listview.setAdapter( sendGiftAdapter);
        loadSendGiftData(index);
    }

    private void loadSendGiftData(final int page) {
        final List<RecieveGiftEntity> temp = new ArrayList<RecieveGiftEntity>();

        if(!NetUtil.isNetworkAvailable(getActivity())) {
            android.widget.Toast.makeText(getActivity(), "请检查网络连接", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }
        String str="&giverId="+ BaseApplication.getInstances().getUser_id()+"&rows="+20+"&page="+page;
        MyHandler.putTask(new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                pull_to_refresh_listview.onRefreshComplete();
                HttpResponseResult result = new HttpResponseResult(val);
                if(result!=null && "200".equals(result.getCode())/*&&result.getMessage().equals("返回成功")*/) {
                    try {
                        JSONObject jsonObject = new JSONObject(val);
                        String total = jsonObject.getString("total");
                        if(total.equals("0")) {
                            showNoneDataImage(true);
                            Toast.makeText(getActivity(), "没有送出礼物", Toast.LENGTH_SHORT).show();
                            datas.clear();
                            sendGiftAdapter.notifyDataSetChanged();
                            return;
                        }else {
                            showNoneDataImage(false);
                            JSONArray jsonArray = jsonObject.getJSONArray("rows");
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject rowsObj = jsonArray.getJSONObject(i);
                                RecieveGiftEntity recieveGiftEntity = new RecieveGiftEntity();
                                recieveGiftEntity.setGiftName(rowsObj.getString("presentName"));
                                recieveGiftEntity.setGiftIcon(rowsObj.getString("presentIcon"));
                                recieveGiftEntity.setIcon(rowsObj.getString("docIcon"));
                                recieveGiftEntity.setName(rowsObj.getString("docName"));

                                double tradeTime = Double.parseDouble(rowsObj.getString("tradeTime"))*1000;
                                DecimalFormat format    = new DecimalFormat("#0");
                                String time = format.format(tradeTime);
                                recieveGiftEntity.setTime(sdf.format(Long.parseLong(time)));

                                temp.add(recieveGiftEntity);

                            }

                            if (index==1){
                                datas.clear();
                            }

                            if(page==1 && (temp==null || temp.size() < 1)){
                                showNoneDataImage(true);
                            }else{
                                showNoneDataImage(false);
                            }

                            datas.addAll(temp);
                            if (datas.size()<((index)*20)){
                                pull_to_refresh_listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            }else {
                                pull_to_refresh_listview.setMode(PullToRefreshBase.Mode.BOTH);
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    pull_to_refresh_listview.onRefreshComplete();
                }else{
                    if(page==1){
                        showNoneDataImage(true);
                    }else{
                        showNoneDataImage(false);
                    }
                }
                sendGiftAdapter.notifyDataSetChanged();
            }
        }, HttpUrls.getSendGift()+str,null,Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(getActivity())));

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
}
