package com.androidquanjiakan.activity.video;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquanjiakan.adapter.VideoEntryDemandAdapter;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.entity.VideoDemandListEntity;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.util.LogUtil;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
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
 * Created by Administrator on 2016/11/29 0029.
 */

public class VideoDemandFragment extends Fragment {

    public static final int REQUEST_COMMEND = 1026;

    private VideoEntryDemandAdapter mAdapter;
    private List<VideoDemandListEntity> mData;

    private int pageIndex;

    private com.handmark.pulltorefresh.library.PullToRefreshListView mGridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_video_demand,container,false);
        initView(view);
        return view;
    }

    public void initView(View parent){
        nonedata = (ImageView) parent.findViewById(R.id.nonedata);
        nonedatahint = (TextView) parent.findViewById(R.id.nonedatahint);
        showNoneData(false);
        mGridView = (PullToRefreshListView) parent.findViewById(R.id.grid);
        initData();
    }

    public void initData(){
        mData = new ArrayList<>();
        mAdapter = new VideoEntryDemandAdapter(mData,getActivity(), VideoEntryDemandAdapter.DEMAND);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageIndex = 1;
                initViewData(pageIndex);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageIndex++;
                initViewData(pageIndex);
            }
        });
//        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getActivity(),VideoDemandPlayActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("TITLE", "");
//                //TODO 这里传入接口获取的视频地址
//                bundle.putString("URI", /*inputServer.getText().toString()*/
//                        mAdapter.getData().get((int)id).getVedioUrl()
//                );
//                bundle.putInt("decode_type", 0);//3. 设置缺省编码类型：0表示硬解；1表示软解；
//                intent.putExtras(bundle);
//                getActivity().startActivity(intent);
//            }
//        });



        pageIndex = 1;
        initViewData(pageIndex);
    }

    public void reload(){
        pageIndex = 1;
        initViewData(pageIndex);
    }
    public void initViewData(final int page){
        MyHandler.putTask(getActivity(),new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                try{
                    mGridView.onRefreshComplete();
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if(jsonObject.has("code") && !(jsonObject.get("code") instanceof JsonNull) && "200".equals(jsonObject.get("code").getAsString())){
                        if(jsonObject.has("rows") && !(jsonObject.get("rows") instanceof JsonNull) &&
                                jsonObject.get("rows").getAsJsonArray()!=null &&
                                jsonObject.get("rows").getAsJsonArray().toString().length()>2){
                            List<VideoDemandListEntity> temp = (List<VideoDemandListEntity>)
                                    SerialUtil.jsonToObject(jsonObject.
                                                    get("rows").getAsJsonArray().toString(),
                                            new TypeToken<List<VideoDemandListEntity>>(){}.getType());
                            if(temp!=null && temp.size()==20){
                                mGridView.setMode(PullToRefreshBase.Mode.BOTH);
                                if(page==1){
                                    mData.clear();
                                }
                                mData.addAll(temp);
                                mAdapter.setData(mData);
                            }else if(temp!=null && temp.size()>0){
                                mGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                if(page==1){
                                    mData.clear();
                                }
                                mData.addAll(temp);
                                mAdapter.setData(mData);
                            }
                        }else{
                            mGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        }
                        mAdapter.notifyDataSetChanged();

                        if(mData!=null && mData.size()>0){
                            showNoneData(false);
                        }else{
                            showNoneData(true);
                        }
                    }else{

                        if(mData!=null && mData.size()>0){
                            showNoneData(false);
                        }else{
                            showNoneData(true);
                        }

                        BaseApplication.getInstances().toast(getActivity(),"接口访问异常");
                    }
                }catch (Exception e){
                    LogUtil.e(e.getMessage());
                }
            }
        }, HttpUrls.getDemandVideoList()+"&page="+page+
                "&rows=20",null,Task.TYPE_GET_STRING_NOCACHE,
                QuanjiakanDialog.getInstance().getDialog(getActivity())));

    }

    private ImageView nonedata;
    private TextView nonedatahint;
    public void showNoneData(boolean isShow){
        if(isShow){
            nonedatahint.setText("暂无点播");
            nonedata.setVisibility(View.VISIBLE);
            nonedatahint.setVisibility(View.VISIBLE);
        }else{
            nonedata.setVisibility(View.GONE);
            nonedatahint.setVisibility(View.GONE);
        }
    }

    public void resizeHeight(){

    }

    public void resetWholeHeight() {
        // calculate height of all items.
        ListAdapter la = mGridView.getRefreshableView().getAdapter();
        if (null == la) {
            return;
        }
        // calculate height of all items.
        int h = 0;
        final int cnt = la.getCount();
        for (int i = 0; i < cnt; i++) {
            View item = la.getView(i, null, mGridView);
            item.measure(0, 0);
            h += item.getMeasuredHeight();
        }
        // reset ListView height
        ViewGroup.LayoutParams lp = mGridView.getLayoutParams();
        lp.height = h + (mGridView.getRefreshableView().getDividerHeight() * (cnt - 1)) +
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.3f * cnt, getResources().getDisplayMetrics());
        mGridView.setLayoutParams(lp);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onStart() {
        super.onStart();
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

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
