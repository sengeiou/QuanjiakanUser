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

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Administrator on 2016/11/4 0004.
 */

public class GiftSendRecordFragment extends Fragment {

    private PullToRefreshListView listview;
    private ImageView nonedata;
    private TextView nonedatahint;

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
//        listview.setAdapter(null);
//        listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//
//            }
//        });
//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });
//        loadData();
        showNoneDataImage(true);
        Toast.makeText(getActivity(), "Send", Toast.LENGTH_SHORT).show();
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
