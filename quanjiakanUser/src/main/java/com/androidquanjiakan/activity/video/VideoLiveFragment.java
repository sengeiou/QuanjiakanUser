package com.androidquanjiakan.activity.video;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.adapter.VideoEntryLiveAdapter;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.entity.ConcernEntity;
import com.androidquanjiakan.entity.VideoDemandListEntity;
import com.androidquanjiakan.entity.VideoLiveListEntity;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.util.LogUtil;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by Administrator on 2016/11/29 0029.
 */

public class VideoLiveFragment extends Fragment {


    private VideoEntryLiveAdapter mAdapter;
    private List<VideoLiveListEntity> mData;


    private int pageIndex = 0;

    private com.handmark.pulltorefresh.library.PullToRefreshGridView mGridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_video_live,container,false);
        initView(view);
        return view;
    }

    private ImageView nonedata;
    private TextView nonedatahint;
    public void showNoneData(boolean isShow){
        if(isShow){
            nonedatahint.setText("暂无直播");
            nonedata.setVisibility(View.VISIBLE);
            nonedatahint.setVisibility(View.VISIBLE);
        }else{
            nonedata.setVisibility(View.GONE);
            nonedatahint.setVisibility(View.GONE);
        }
    }

    public void initView(View parent){
        nonedata = (ImageView) parent.findViewById(R.id.nonedata);
        nonedatahint = (TextView) parent.findViewById(R.id.nonedatahint);
        showNoneData(false);
        mGridView = (PullToRefreshGridView)parent.findViewById(R.id.grid);
        initData();
    }

    public void reload(){
        pageIndex = 1;
        initViewData(pageIndex);
    }

    public void initData(){
        mData = new ArrayList<>();
        mAdapter = new VideoEntryLiveAdapter(mData,getActivity(), VideoEntryLiveAdapter.LIVE);
        mGridView.getRefreshableView().setNumColumns(2);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                pageIndex = 1;
                initViewData(pageIndex);
                mGridView.onRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                pageIndex++;
                initViewData(pageIndex);
                mGridView.onRefreshComplete();
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                final VideoLiveListEntity entity = mAdapter.getData().get((int)id);

                intoLiveRoom(entity,entity.getUserId(),entity.getName(),entity.getIcon());
                /**
                 * 点播的播放器无法使用
                 */
//                Intent intent = new Intent(getActivity(),VideoDemandPlayActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("TITLE", "");
//                //TODO 这里传入接口获取的视频地址
//                bundle.putString("URI", /*inputServer.getText().toString()*/
////                        "http://200048939.vod.myqcloud.com/200048939_2cdbd156b6cc11e6ad39991f76a4df69.f20.mp4"
//                        entity.getFlvUrl()+"?"+entity.getUsrargs()
//                );
//                bundle.putInt("decode_type", 0);//3. 设置缺省编码类型：0表示硬解；1表示软解；
//                bundle.putString("id",entity.getId()+"");
//                intent.putExtras(bundle);
//                ((VideoEntryActivity)getActivity()).startActivityForResult(intent, VideoEntryActivity.REQUEST_DEMAND);
            }
        });
    }

    public void initViewData(final int page){
        MyHandler.putTask(getActivity(),new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                try{
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if(jsonObject.has("code") && !(jsonObject.get("code") instanceof JsonNull) &&
                            "1".equals(jsonObject.get("code").getAsString())){
                        if(jsonObject.has("rows") && !(jsonObject.get("rows") instanceof JsonNull) &&
                                jsonObject.get("rows").getAsJsonArray()!=null &&
                                jsonObject.get("rows").getAsJsonArray().toString().length()>2){
                            List<VideoLiveListEntity> temp = (List<VideoLiveListEntity>)
                                    SerialUtil.jsonToObject(jsonObject.
                                                    get("rows").getAsJsonArray().toString(),
                                            new TypeToken<List<VideoLiveListEntity>>(){}.getType());
                            if(temp!=null && temp.size()==20){
                                mGridView.setMode(PullToRefreshBase.Mode.BOTH);
                                if(page==1){
                                    mData.clear();
                                }
                                temp = distinct(temp);
                                mData.addAll(temp);
                                mAdapter.setData(mData);
                            }else if(temp!=null && temp.size()>0){
                                mGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                if(page==1){
                                    mData.clear();
                                }
                                temp = distinct(temp);
                                mData.addAll(temp);
                                mAdapter.setData(mData);
                            }
                        }else{
                            mData.clear();
                            mAdapter.setData(mData);
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
        }, HttpUrls.getLiveVideoList()+"&page="+page+"&rows=20",null,Task.TYPE_GET_STRING_NOCACHE, QuanjiakanDialog.getInstance().getDialog(getActivity())));
    }

    public List<VideoLiveListEntity> distinct(List<VideoLiveListEntity> data){
        if(data==null || data.size()<2){
            return data;
        }else{
            for(int i=data.size()-2;i>=0;i--){
                for(int j = data.size()-1;j>i;j--){
                    if(data.get(j).getStartTime()==data.get(i).getStartTime()){
                        data.remove(j);
                    }
                }
            }
            return data;
        }
    }


    public void intoLiveRoom(final VideoLiveListEntity entity, final String docID,final String name,final String img){
        MyHandler.putTask(getActivity(),new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                try{
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if(jsonObject.has("code") && !(jsonObject.get("code") instanceof JsonNull)){

                        if(jsonObject.has("object") && !(jsonObject.get("object") instanceof JsonNull)
                                && jsonObject.get("object").getAsJsonObject()!=null  &&
                                "1".equals(jsonObject.get("code").getAsString())){

                            if(jsonObject.get("object").getAsJsonObject().has("isFollow") && !(jsonObject.get("object").getAsJsonObject().get("isFollow") instanceof JsonNull)){
                                entity.setIsFollow(jsonObject.get("object").getAsJsonObject().get("isFollow").getAsLong());
                            }else{
                                entity.setIsFollow(0);
                            }

                            if(jsonObject.get("object").getAsJsonObject().has("state") &&
                                    !(jsonObject.get("object").getAsJsonObject().get("state") instanceof JsonNull) &&
                                    (1==jsonObject.get("object").getAsJsonObject().get("state").getAsInt())){
                                loginCount = 0;
                                intoLive(entity,jsonObject,name,img);
                            }else{
                                BaseApplication.getInstances().toast(getActivity(),"来晚了，直播已经结束!");
                            }

                        }else{
                            if(jsonObject.has("message") && !(jsonObject.get("message") instanceof JsonNull)){
                                BaseApplication.getInstances().toast(getActivity(),jsonObject.get("message").getAsString());
                            }else{
                                BaseApplication.getInstances().toast(getActivity(),"获取直播地址失败!");
                            }
                        }
                    }else{
                        BaseApplication.getInstances().toast(getActivity(),"获取直播地址失败!");
                    }
                }catch (Exception e){
                    LogUtil.e(e.getMessage());
                }
            }
        }, HttpUrls.getIntoLiveRoom()+"&userId="+docID+"&lookId="+BaseApplication.getInstances().getUser_id()+"&userType=0",
                null,Task.TYPE_GET_STRING_NOCACHE, /*QuanjiakanDialog.getInstance().getDialog(getActivity())*/null));
    }

    private int loginCount = 0;
    public void intoLive(final VideoLiveListEntity entity, final JsonObject jsonObject, final String name, final String img){
        if(loginCount>5){
            Toast.makeText(getActivity(), "进入直播聊天群失败!", Toast.LENGTH_SHORT).show();
            return;
        }
        loginCount++;
        if(JMessageClient.getMyInfo()!=null) {
            Intent intent = new Intent(getActivity(), VideoLivePlayActivity.class);
            intent.putExtra(BaseConstants.PARAM_ENTITY, entity);
            intent.putExtra(BaseConstants.PARAM_URL, jsonObject.get("object").getAsJsonObject().get("flvUrl").getAsString());
            intent.putExtra(BaseConstants.PARAM_GROUP, jsonObject.get("object").getAsJsonObject().get("groupId").getAsString());
            intent.putExtra(BaseConstants.PARAM_NUMBER, jsonObject.get("object").getAsJsonObject().get("lookNum").getAsLong() + "");
            intent.putExtra(BaseConstants.PARAM_NAME, name);
            intent.putExtra(BaseConstants.PARAM_HEADIMG, img);
            intent.putExtra(BaseConstants.PARAM_LIVERID, jsonObject.get("object").getAsJsonObject().get("userId").getAsString());
            ((VideoEntryActivity) getActivity()).startActivityForResult(intent, VideoEntryActivity.REQUEST_LIVE);
        }else{
            JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX + BaseApplication.getInstances().getUser_id(), CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
                @Override
                public void gotResult(int result, String s) {
                    if(result==0){
                        Intent intent = new Intent(getActivity(), VideoLivePlayActivity.class);
                        intent.putExtra(BaseConstants.PARAM_ENTITY, entity);
                        intent.putExtra(BaseConstants.PARAM_URL, jsonObject.get("object").getAsJsonObject().get("flvUrl").getAsString());
                        intent.putExtra(BaseConstants.PARAM_GROUP, jsonObject.get("object").getAsJsonObject().get("groupId").getAsString());
                        intent.putExtra(BaseConstants.PARAM_NUMBER, jsonObject.get("object").getAsJsonObject().get("lookNum").getAsLong() + "");
                        intent.putExtra(BaseConstants.PARAM_NAME, name);
                        intent.putExtra(BaseConstants.PARAM_HEADIMG, img);
                        intent.putExtra(BaseConstants.PARAM_LIVERID, jsonObject.get("object").getAsJsonObject().get("userId").getAsString());

                        ((VideoEntryActivity) getActivity()).startActivityForResult(intent, VideoEntryActivity.REQUEST_LIVE);
                    }else{
                        intoLive(entity,jsonObject,name,img);
                    }
                }
            });
        }
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

        pageIndex = 1;
        initViewData(pageIndex);
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
}
