package com.androidquanjiakan.activity.index.out;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquanjiakan.base.BaseActivity;
import com.quanjiakan.main.R;
import com.umeng.analytics.MobclickAgent;

public class ConsultantActivity_GoOut_HistoryOrder extends BaseActivity implements OnClickListener {

    private TextView tv_title;
    private ImageButton ibtn_back;

    private TextView info_line_type_hurry_go_out_memory;
    private TextView info_line_type_normal_go_out_memory;

    private LinearLayout layout_introduce;
    private TextView layout_introduce_name;
    private View layout_introduce_line;

    private LinearLayout layout_order;
    private TextView layout_order_name;
    private View layout_order_line;

    private TextView infomation;

    private TextView order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_doctor_go_out_entry);

        initTitleBar();
        initInfoLine();

        initSelectPart();
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
         *
         */
        info_line_type_hurry_go_out_memory = (TextView) findViewById(R.id.info_line_type_hurry_go_out_memory);
        info_line_type_normal_go_out_memory = (TextView) findViewById(R.id.info_line_type_normal_go_out_memory);
    }

    public void loadData(){
        /**
         * 接口获取金额
         */
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
                layout_introduce_name.setTextColor(getResources().getColor(R.color.maincolor));
                layout_introduce_line.setVisibility(View.VISIBLE);

                layout_order_name.setTextColor(getResources().getColor(R.color.colorAlphaBlack_88));
                layout_order_line.setVisibility(View.INVISIBLE);

                infomation.setText("服务介绍");
                break;
            case R.id.layout_order:
                layout_introduce_name.setTextColor(getResources().getColor(R.color.colorAlphaBlack_88));
                layout_introduce_line.setVisibility(View.INVISIBLE);

                layout_order_name.setTextColor(getResources().getColor(R.color.maincolor));
                layout_order_line.setVisibility(View.VISIBLE);
                infomation.setText("下单流程");
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
                break;
            default:
                break;
        }
    }
}
