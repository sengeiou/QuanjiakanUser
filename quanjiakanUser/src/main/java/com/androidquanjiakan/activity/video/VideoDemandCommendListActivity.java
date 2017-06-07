package com.androidquanjiakan.activity.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquanjiakan.adapter.VideoDemandCommendListAdapter;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.entity.VideoCommendListEntity;
import com.androidquanjiakan.entity.VideoDemandListEntity;
import com.androidquanjiakan.entity_util.SerialUtil;
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
import com.quanjiakanuser.util.KeyBoardUtils;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 点播评论
 */
public class VideoDemandCommendListActivity extends BaseActivity implements View.OnClickListener {

    //
    private TextView tv_title;
    private ImageButton ibtn_back;
    private ImageButton ibtn_menu;
    private TextView menu_text;


    public static final String PARAMS_VIDEO_ENTITY = "demand_id";
    public static final String PARAMS_VIDEO_ID = "demand_entity";
    private VideoDemandListEntity entity;
    private String id;

    public static final int REQUEST_DETAIL = 1000;

    //
    private PullToRefreshListView listview;

    private EditText mInput;
    private LinearLayout apprise_line;
    private ImageView apprise_img;
    private TextView apprise_number;
    private TextView tv_sendtext;

    //Info
    private RelativeLayout rl_video;
    private ImageView iv_vedio_photo;
    private ImageView iv_vedio_play;

    private TextView tv_videotitle;

    public VideoDemandListEntity getEntity() {
        return entity;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.layout_video_demand_commend_list);
        id = getIntent().getStringExtra(PARAMS_VIDEO_ID);
        entity = (VideoDemandListEntity) getIntent().getSerializableExtra(PARAMS_VIDEO_ENTITY);
        if (id == null || entity == null) {
            BaseApplication.getInstances().toast(VideoDemandCommendListActivity.this,"传入参数异常!");
            finish();
            return;
        }
        initTitleBar();
        initView();

        initInput();
        initInfo();
    }

    public void initInfo() {
        rl_video = (RelativeLayout) findViewById(R.id.rl_video);
        rl_video.setOnClickListener(this);
        iv_vedio_photo = (ImageView) findViewById(R.id.iv_vedio_photo);
        iv_vedio_play = (ImageView) findViewById(R.id.iv_vedio_play);

        if (entity.getImageUrl() != null && entity.getImageUrl().toLowerCase().startsWith("http")) {
            Picasso.with(this).
                    load(entity.getImageUrl()).
                    error(R.drawable.ic_empty).
                    into(iv_vedio_photo);
        } else {
            iv_vedio_photo.setImageResource(R.drawable.ic_empty);
        }

        if (entity.getVedioUrl() != null) {
            iv_vedio_play.setVisibility(View.VISIBLE);
        } else {
            iv_vedio_play.setVisibility(View.GONE);
        }

        //
        tv_videotitle = (TextView) findViewById(R.id.tv_videotitle);
        if (entity.getContent() != null) {
            tv_videotitle.setVisibility(View.VISIBLE);
            tv_videotitle.setText(entity.getContent());
        } else {
            tv_videotitle.setVisibility(View.VISIBLE);
        }
        tv_videotitle.setOnClickListener(this);
    }

    public void initInput() {
        mInput = (EditText) findViewById(R.id.et_comment);
        /**
         * TODO 看是否需要处理
         */
        apprise_line = (LinearLayout) findViewById(R.id.apprise_line);
        apprise_line.setOnClickListener(this);
        apprise_img = (ImageView) findViewById(R.id.apprise_img);
        apprise_number = (TextView) findViewById(R.id.apprise_number);
        apprise_number.setText(entity.getLikeCount() + "");
        if (entity.getLikes() == 0) {
            apprise_img.setImageResource(R.drawable.icon_streaming_details_like_nor);
        } else {
            apprise_img.setImageResource(R.drawable.icon_streaming_details_like_sel_c);
        }

        tv_sendtext = (TextView) findViewById(R.id.tv_sendtext);
        tv_sendtext.setOnClickListener(this);
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() < 1) {
                    tv_sendtext.setVisibility(View.GONE);
                    apprise_line.setVisibility(View.VISIBLE);
                } else {
                    tv_sendtext.setVisibility(View.VISIBLE);
                    apprise_line.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        initIDs();
    }

    public void initTitleBar() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("评论");

        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);

        ibtn_menu = (ImageButton) findViewById(R.id.ibtn_menu);
        ibtn_menu.setVisibility(View.GONE);
        ibtn_menu.setOnClickListener(this);

        menu_text = (TextView) findViewById(R.id.menu_text);
        menu_text.setVisibility(View.GONE);
        menu_text.setOnClickListener(this);
    }

    private String root_id;
    private String parent_id;
    private String isFloor;

    public void initIDs() {
        root_id = "0";
        parent_id = "0";
        isFloor = "0";
        mInput.setText("");
        mInput.setHint("我也说一句...");
    }

    public void initView() {
        mData = new ArrayList<>();
        listview = (PullToRefreshListView) findViewById(R.id.listview);
        listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mAdapter = new VideoDemandCommendListAdapter(mData, this, new CommendCallBack() {
            @Override
            public void callCommend(String name, String rootID, String parentID, String floor) {
                mInput.setHint("@" + name);
                root_id = rootID;
                parent_id = parentID;
                isFloor = floor;
                KeyBoardUtils.openKeybord(mInput,VideoDemandCommendListActivity.this);
            }
        });
        listview.setAdapter(mAdapter);
        listview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                pageIndex = 1;
                getNetData(pageIndex);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//				pageIndex++;
                pageIndex = 1;
                getNetData(pageIndex);
            }
        });


        pageIndex = 1;
        getNetData(pageIndex);
    }

    private int pageIndex;
    private List<VideoCommendListEntity> mData;
    private VideoDemandCommendListAdapter mAdapter;

    public void getNetData(final int pageIndex) {
        HashMap<String, String> map = new HashMap<>();
        MyHandler.putTask(VideoDemandCommendListActivity.this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if (val != null && !"".equals(val) &&
                        val.startsWith("{") && val.endsWith("}")) {
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if (jsonObject.has("object") && !(jsonObject.get("object") instanceof JsonNull)) {
                        List<VideoCommendListEntity> dataList = (List<VideoCommendListEntity>) SerialUtil.jsonToObject(jsonObject.get("object").
                                getAsJsonArray().toString(), new TypeToken<List<VideoCommendListEntity>>() {
                        }.getType());
                        if (pageIndex == 1) {
                            mData.clear();
                        }
                        mData.addAll(dataList);

                        if (dataList.size() == 20) {
                            listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        }
                        mAdapter.setData(mData);
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    listview.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }

                listview.onRefreshComplete();
            }
        }, HttpUrls.getVideoCommendList() + "&ideoDemandId=" + id + "&page=" + pageIndex + "&rows=20", map, Task.TYPE_GET_STRING_NOCACHE, null));
    }

    private InputMethodManager imm;

    public void showSoftInput(View view) {
//        isOpenSoftInput();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (imm == null) {
            imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public void hideSoftInput(View view) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (imm == null) {
            imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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


    public void sendCommend() {
//        hideSoftInput(mInput);
        KeyBoardUtils.closeKeybord(mInput,VideoDemandCommendListActivity.this);
        if (mInput.getText().length() < 1) {
            BaseApplication.getInstances().toast(VideoDemandCommendListActivity.this,"请输入评论!");
            return;
        }
        if (root_id == null) {
            root_id = "0";
        }
        if (isFloor == null) {
            isFloor = "0";
        }
        if (parent_id == null) {
            parent_id = "0";
        }

        String toname = "";
        if (mInput.getHint() == null || !mInput.getHint().toString().contains("@")) {
            toname = BaseApplication.getInstances().getKeyValue("nickname").replace("'", "");
        } else {
            toname = mInput.getHint().toString().replace("@", "");
        }

        String content = " ";
        try {
            content = URLEncoder.encode(mInput.getText().toString(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String, String> map = new HashMap<>();
        MyHandler.putTask(VideoDemandCommendListActivity.this, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                //TODO 成功后刷新数据
                if (val != null && !"".equals(val) &&
                        val.startsWith("{") && val.endsWith("}")) {
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if (jsonObject != null && jsonObject.has("code") && !(jsonObject.get("code") instanceof JsonNull)) {
                        if ("200".equals(jsonObject.get("code").getAsString())) {
                            pageIndex = 1;
                            getNetData(pageIndex);

                            //重置所有信息
                            initIDs();
                        }
                    }
                }
                tv_sendtext.setEnabled(true);
            }
        }, HttpUrls.sendVideoCommend() + "&ideoDemandId=" + id
                + "&rootAppraiseId=" + root_id
                + "&content=" + content
                + "&isFloor=" + isFloor
                + "&parentId=" + parent_id
//                + "&tomemberId="+ ""
                + "&toname=" + toname, map, Task.TYPE_GET_STRING_NOCACHE, null));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.apprise_line: {

                apprise_line.setEnabled(false);
                if (entity.getLikes() == 0) {
                    MyHandler.putTask(this, new Task(new HttpResponseInterface() {
                        @Override
                        public void handMsg(String val) {
                            apprise_line.setEnabled(true);
                            if (val != null && val.length() > 0) {
                                JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                                if (jsonObject.has("code") && !(jsonObject.get("code") instanceof JsonNull)
                                        && "200".equals(jsonObject.get("code").getAsString())) {
                                    apprise_img.setImageResource(R.drawable.icon_streaming_details_like_sel_c);
                                    entity.setLikes(1);
                                    entity.setMemberId(Integer.parseInt(BaseApplication.getInstances().getUser_id()));
                                    /**
                                     * 重置显示数量
                                     */
                                    if (jsonObject.has("object") && !(jsonObject.get("object") instanceof JsonNull) &&
                                            jsonObject.get("object").getAsJsonObject().has("likeCount")) {
                                        entity.setLikeCount(jsonObject.get("object").getAsJsonObject().get("likeCount").getAsInt());
                                    } else {
                                        entity.setLikeCount(entity.getLikeCount() + 1);
                                    }
                                    apprise_number.setText("" + entity.getLikeCount());
                                }
                            }
                        }
                    }, HttpUrls.getDemandVideoAddPraiseNumber() + "&memberId=" +
                            BaseApplication.getInstances().getUser_id() +
                            "&id=" + entity.getId() + "&likes=1", null, Task.TYPE_GET_STRING_NOCACHE, null));
                } else {
                    MyHandler.putTask(this, new Task(new HttpResponseInterface() {
                        @Override
                        public void handMsg(String val) {
                            apprise_line.setEnabled(true);
                            if (val != null && val.length() > 0) {
                                JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                                if (jsonObject.has("code") && !(jsonObject.get("code") instanceof JsonNull)
                                        && "200".equals(jsonObject.get("code").getAsString())) {
                                    apprise_img.setImageResource(R.drawable.icon_streaming_details_like_nor);
                                    entity.setLikes(0);
                                    entity.setMemberId(Integer.parseInt(BaseApplication.getInstances().getUser_id()));
                                    /**
                                     * 重置显示数量
                                     */
                                    if (jsonObject.has("object") && !(jsonObject.get("object") instanceof JsonNull) &&
                                            jsonObject.get("object").getAsJsonObject().has("likeCount")) {
                                        entity.setLikeCount(jsonObject.get("object").getAsJsonObject().get("likeCount").getAsInt());
                                    } else {
                                        entity.setLikeCount(entity.getLikeCount() - 1);
                                    }
                                    apprise_number.setText("" + entity.getLikeCount());
                                }
                            }
                        }
                    }, HttpUrls.getDemandVideoAddPraiseNumber() + "&memberId=" +
                            BaseApplication.getInstances().getUser_id() +
                            "&id=" + entity.getId() + "&likes=0", null, Task.TYPE_GET_STRING_NOCACHE, null));
                }
                break;
            }
            case R.id.tv_videotitle: {
                root_id = "0";
                parent_id = "0";
                isFloor = "0";
                mInput.setHint("我也说一句...");
                break;
            }
            case R.id.tv_sendtext: {
                //TODO 调用发送
                tv_sendtext.setEnabled(false);
                sendCommend();
                break;
            }
            case R.id.ibtn_back: {
                finish();
                break;
            }
            case R.id.ibtn_menu: {
                //图片菜单
                break;
            }
            case R.id.menu_text: {
                //文字菜单
                break;
            }
            case R.id.rl_video: {
                Intent intent = new Intent(this, VideoDemandPlayActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", "");
                //TODO 这里传入接口获取的视频地址
                bundle.putString("URI",
                        entity.getVedioUrl()
                );
                bundle.putInt("decode_type", 0);//3. 设置缺省编码类型：0表示硬解；1表示软解；
                bundle.putString("id", entity.getId() + "");
                intent.putExtras(bundle);
                startActivityForResult(intent, VideoEntryActivity.REQUEST_DEMAND);
                break;
            }
        }
    }

    public interface CommendCallBack {
        void callCommend(String name, String rootID, String parentID, String floor);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DETAIL:
                VideoDemandListEntity tempEntity = null;
                tempEntity = (VideoDemandListEntity) data.getSerializableExtra(PARAMS_VIDEO_ENTITY);
                if (tempEntity != null) {
                    entity = tempEntity;
                }
                if (entity.getLikes() == 0) {
                    apprise_img.setImageResource(R.drawable.icon_streaming_details_like_nor);
                } else {
                    apprise_img.setImageResource(R.drawable.icon_streaming_details_like_sel_c);
                }
                apprise_number.setText(entity.getLikeCount() + "");

                pageIndex = 1;
                getNetData(pageIndex);
                break;
        }
    }
}
