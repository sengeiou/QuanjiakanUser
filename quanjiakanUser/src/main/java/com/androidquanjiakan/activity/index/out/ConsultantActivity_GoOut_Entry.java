package com.androidquanjiakan.activity.index.out;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.entity.BaseHttpResultEntity;
import com.androidquanjiakan.entity.BaseHttpResultEntity_List;
import com.androidquanjiakan.entity.GoOutEntryEntity;
import com.androidquanjiakan.entity.GrabNumberEntity;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.google.gson.reflect.TypeToken;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class ConsultantActivity_GoOut_Entry extends BaseActivity implements OnClickListener {

    private TextView tv_title;
    private ImageButton ibtn_back;

    private ImageView image;

    private TextView info_line_type_hurry_go_out_memory;
    private TextView info_line_type_normal_go_out_memory;

    private TextView info_total;

    private LinearLayout layout_introduce;
    private TextView layout_introduce_name;
    private View layout_introduce_line;

    private LinearLayout layout_order;
    private TextView layout_order_name;
    private View layout_order_line;

    private TextView infomation;

    private TextView order;

    private BaseHttpResultEntity<List<GoOutEntryEntity>> result;
    private String hurry;
    private String normal;

    private String hurry_real;
    private String normal_real;

    private String hurry_platform;
    private String normal_platform;

    private BaseHttpResultEntity_List<GrabNumberEntity> resultNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_doctor_go_out_entry);

        initTitleBar();
        initInfoLine();

        initSelectPart();
        initOrder();

        loadData();
        loadGrabNumber();
    }

    public void initTitleBar(){
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("上门问诊");
        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
    }

    public void initInfoLine(){

        /**
         * 设置图片宽高
         */
        image = (ImageView) findViewById(R.id.image);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) image.getLayoutParams();
        int width = getResources().getDisplayMetrics().widthPixels;
        float real = 440*width/1079*1.0f;
        layoutParams.height = (int)real;
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        image.setLayoutParams(layoutParams);

        /**
         *
         */
        info_total = (TextView) findViewById(R.id.info_total);
        info_line_type_hurry_go_out_memory = (TextView) findViewById(R.id.info_line_type_hurry_go_out_memory);
        info_line_type_normal_go_out_memory = (TextView) findViewById(R.id.info_line_type_normal_go_out_memory);
    }

    public void loadData(){
        /**
         * @TODO 获取金额---购买人总数
         */
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if(val!=null && val.length()>0 && !"null".equals(val.toLowerCase())){
                    result = (BaseHttpResultEntity<List<GoOutEntryEntity>>) SerialUtil.jsonToObject(val,new TypeToken<BaseHttpResultEntity<List<GoOutEntryEntity>>>(){}.getType());
                    if(result.getRows()!=null && result.getRows().size()>1){
                        for(int i=0;i<2;i++){
                            if("1".equals(result.getRows().get(i).getPatient_type().trim())){
                                //normal
                                normal = result.getRows().get(i).getTotal_price();
                                normal_platform = result.getRows().get(i).getPlatform_price();
                                normal_real = result.getRows().get(i).getReal_price();
                                info_line_type_normal_go_out_memory.setText(result.getRows().get(i).getTotal_price()+"元/次"/*+" (原价:"+result.getRows().get(i).getPatient_money()+"元/次)"*/);
                            }else if("2".equals(result.getRows().get(i).getPatient_type().trim())){
                                //hurry
                                hurry = result.getRows().get(i).getTotal_price();
                                hurry_platform = result.getRows().get(i).getPlatform_price();
                                hurry_real = result.getRows().get(i).getReal_price();
                                info_line_type_hurry_go_out_memory.setText(result.getRows().get(i).getTotal_price()+"元/次"/*+" (原价:"+result.getRows().get(i).getPatient_money()+"元/次)"*/);
                            }
                        }
                    }else{
                        hurry = "150";
                        normal = "100";
                        info_line_type_normal_go_out_memory.setText("100元/次"/*+" (原价:400元/次)"*/);
                        info_line_type_hurry_go_out_memory.setText("150元/次"/*+" (原价:500元/次)"*/);
                    }
                }else{
                    hurry = "150";
                    normal = "100";
                    info_line_type_normal_go_out_memory.setText("100元/次"/*+" (原价:400元/次)"*/);
                    info_line_type_hurry_go_out_memory.setText("150元/次"/*+" (原价:500元/次)"*/);
                }
            }
        },HttpUrls.outGoDoctor(),null,Task.TYPE_GET_STRING_NOCACHE,QuanjiakanDialog.getInstance().getDialog(this)));
    }


    public void loadGrabNumber(){
        /**
         * @TODO 获取金额---购买人总数
         */
        MyHandler.putTask(this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                if(val!=null && val.length()>0 && !"null".equals(val.toLowerCase())){
                    resultNumber = (BaseHttpResultEntity_List<GrabNumberEntity>) SerialUtil.jsonToObject(val,new TypeToken<BaseHttpResultEntity_List<GrabNumberEntity>>(){}.getType());
                    if(resultNumber!=null && resultNumber.getRows()!=null && resultNumber.getRows().size()>0){
                        GrabNumberEntity entity = resultNumber.getRows().get(0);
                        info_total.setText(entity.getGrab_service_count()+"人已购买");
                    }else{
                        info_total.setText(200+"人已购买");
                    }
                }else{

                }
            }
        },HttpUrls.grabNumber(),null,Task.TYPE_GET_STRING_NOCACHE,QuanjiakanDialog.getInstance().getDialog(this)));
    }

    public void initSelectPart(){
        layout_introduce = (LinearLayout) findViewById(R.id.layout_introduce);
        layout_order = (LinearLayout) findViewById(R.id.layout_order);

        layout_introduce_name = (TextView) findViewById(R.id.layout_introduce_name);
        layout_order_name = (TextView) findViewById(R.id.layout_order_name);

        layout_introduce_line = findViewById(R.id.layout_introduce_line);
        layout_order_line = findViewById(R.id.layout_order_line);

        layout_introduce.setOnClickListener(this);
        layout_order.setOnClickListener(this);

        infomation = (TextView) findViewById(R.id.infomation);

        selectPart(R.id.layout_introduce);
    }

    public void selectPart(int partid){
        switch (partid){
            case R.id.layout_introduce:
                layout_introduce_name.setTextColor(getResources().getColor(R.color.color_14a9a8));
                layout_introduce_line.setVisibility(View.VISIBLE);

                layout_order_name.setTextColor(getResources().getColor(R.color.font_color_333333));
                layout_order_line.setVisibility(View.INVISIBLE);

                infomation.setText(getResources().getString(R.string.doctor_out_go_service_intro_desc));
                break;
            case R.id.layout_order:
                layout_introduce_name.setTextColor(getResources().getColor(R.color.font_color_333333));
                layout_introduce_line.setVisibility(View.INVISIBLE);

                layout_order_name.setTextColor(getResources().getColor(R.color.color_14a9a8));
                layout_order_line.setVisibility(View.VISIBLE);

                infomation.setText(getResources().getString(R.string.doctor_out_go_service_order_desc));
                break;
        }
    }

    public void initOrder(){
        order = (TextView) findViewById(R.id.order);
        order.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getSimpleName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.layout_introduce:
                selectPart(R.id.layout_introduce);
                break;
            case R.id.layout_order:
                selectPart(R.id.layout_order);
                break;
            case R.id.order:
                /**
                 * 跳转到填写订单页面
                 */
                if(hurry!=null && hurry.length()>0 && normal!=null && normal.length()>0){
                    Intent intent = new Intent(ConsultantActivity_GoOut_Entry.this,ConsultantActivity_GoOut_Order.class);
                    intent.putExtra(ConsultantActivity_GoOut_Order.PARAMS_HURRY_MENORY,hurry);
                    intent.putExtra(ConsultantActivity_GoOut_Order.PARAMS_NORMAL_MENORY,normal);

                    intent.putExtra(ConsultantActivity_GoOut_Order.PARAMS_HURRY_MENORY_REAL,hurry_real);
                    intent.putExtra(ConsultantActivity_GoOut_Order.PARAMS_NORMAL_MENORY_REAL,normal_real);

                    intent.putExtra(ConsultantActivity_GoOut_Order.PARAMS_HURRY_MENORY_PLATFORM,hurry_platform);
                    intent.putExtra(ConsultantActivity_GoOut_Order.PARAMS_NORMAL_MENORY_PLATFORM,normal_platform);
                    startActivityForResult(intent,REQUEST_ORDER);
                }else{
                    /**
                     * 参数异常，需要重试
                     */
                }
                break;
            default:
                break;
        }
    }

    private final int REQUEST_ORDER = 100;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_ORDER:
                if(resultCode==RESULT_OK){
                    finish();
                }
                break;
            default:
                break;
        }
    }
}
