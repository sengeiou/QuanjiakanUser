package com.androidquanjiakan.activity.index.watch_child.elder;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquanjiakan.activity.setting.contact.AddContactActivity;
import com.androidquanjiakan.activity.setting.contact.AddContactNumberActivity;
import com.androidquanjiakan.base.BaseActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.quanjiakan.main.R;
import com.quanjiakanuser.util.KeyBoardUtils;
import com.umeng.analytics.MobclickAgent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：Administrator on 2017/4/10 18:05
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class OldAddContactActivity extends BaseActivity implements View.OnClickListener {

    private Button next;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;
    private ImageView image5;

    private LinearLayout llt_1;
    private LinearLayout llt_2;
    private LinearLayout llt_3;
    private LinearLayout llt_4;
    private LinearLayout llt_5;

    private TextView text1;
    private TextView text2;
    private TextView text3;
    private TextView text4;
    private TextView text5;

    private String name;
    private EditText et_diy;
    private RelativeLayout temp;
    private TextView tv_sure;

    private String image;
    private String device_id;


    private List<ImageView> imageViews = new ArrayList<>();


    private int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_old_add_contact);


        initTitle();

        initView();
    }



    private ImageButton ibtn_back;
    private TextView tv_title;

    private void initTitle() {

        ibtn_back = (ImageButton) findViewById(R.id.ibtn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        ibtn_back.setVisibility(View.VISIBLE);
        ibtn_back.setOnClickListener(this);
        tv_title.setText("添加手表联系人");

    }


    private void initView() {
        next = (Button) findViewById(R.id.btn_next);
        next.setOnClickListener(this);
//        if (flag==1){
//            next.setVisibility(View.GONE);
//        }else {
//            next.setVisibility(View.VISIBLE);
//        }


        /**
         * 图标
         */
        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        image3 = (ImageView) findViewById(R.id.image3);
        image4 = (ImageView) findViewById(R.id.image4);
        image5 = (ImageView) findViewById(R.id.image5);
        imageViews.add(image1);
        imageViews.add(image2);
        imageViews.add(image3);
        imageViews.add(image4);
        imageViews.add(image5);
        for (int i=0;i<imageViews.size();i++){
            imageViews.get(i).setBackgroundResource(R.drawable.contacts_pic_custom);
        }


        llt_1 = (LinearLayout) findViewById(R.id.llt_1);
        llt_2 = (LinearLayout) findViewById(R.id.llt_2);
        llt_3 = (LinearLayout) findViewById(R.id.llt_3);
        llt_4 = (LinearLayout) findViewById(R.id.llt_4);
        llt_5 = (LinearLayout) findViewById(R.id.llt_5);


        llt_1.setOnClickListener(this);
        llt_2.setOnClickListener(this);
        llt_3.setOnClickListener(this);
        llt_4.setOnClickListener(this);
        llt_5.setOnClickListener(this);

//        if (flag==1){
//            llt_9.setEnabled(false);
//        }else {
//            llt_9.setEnabled(true);
//        }

        text1 = (TextView) findViewById(R.id.text1);
        text2 = (TextView) findViewById(R.id.text2);
        text3 = (TextView) findViewById(R.id.text3);
        text4 = (TextView) findViewById(R.id.text4);
        text5 = (TextView) findViewById(R.id.text5);


        et_diy = (EditText) findViewById(R.id.et_diy);
        temp = (RelativeLayout) findViewById(R.id.llt_pinglun);
        tv_sure = (TextView) findViewById(R.id.tv_sure);

        temp.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_back:
                finish();
                break;
            case R.id.btn_next:
                toAddContactNumberActivity();
                break;

            case R.id.llt_1:
                KeyBoardUtils.closeKeybord(et_diy, OldAddContactActivity.this);
                temp.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                for (int i = 0; i < imageViews.size(); i++) {
                    if (i == 0) {
                        imageViews.get(i).setImageResource(R.drawable.add_icon_selected);
                        if (flag == 1) {

                            changeRelation(text1.getText().toString(), "0");

                        }
                    } else {
                        imageViews.get(i).setImageResource(0);
                    }
                }
                name = text1.getText().toString();
                image = "0";
                break;

            case R.id.llt_2:
                KeyBoardUtils.closeKeybord(et_diy, OldAddContactActivity.this);
                temp.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                for (int i = 0; i < imageViews.size(); i++) {
                    if (i == 1) {
                        imageViews.get(i).setImageResource(R.drawable.add_icon_selected);
                        if (flag == 1) {
                            changeRelation(text2.getText().toString(), "1");
                        }
                    } else {
                        imageViews.get(i).setImageResource(0);
                    }
                }
                name = text2.getText().toString();
                image = "1";
                break;

            case R.id.llt_3:
                KeyBoardUtils.closeKeybord(et_diy, OldAddContactActivity.this);
                temp.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                for (int i = 0; i < imageViews.size(); i++) {
                    if (i == 2) {
                        imageViews.get(i).setImageResource(R.drawable.add_icon_selected);
                        if (flag == 1) {
                            changeRelation(text3.getText().toString(), "2");
                        }
                    } else {
                        imageViews.get(i).setImageResource(0);
                    }
                }
                name = text3.getText().toString();
                image = "2";
                break;

            case R.id.llt_4:
                KeyBoardUtils.closeKeybord(et_diy, OldAddContactActivity.this);
                temp.setVisibility(View.GONE);
                next.setVisibility(View.VISIBLE);
                for (int i = 0; i < imageViews.size(); i++) {
                    if (i == 3) {
                        imageViews.get(i).setImageResource(R.drawable.add_icon_selected);
                        if (flag == 1) {
                            changeRelation(text4.getText().toString(), "3");
                        }
                    } else {
                        imageViews.get(i).setImageResource(0);
                    }
                }
                name = text4.getText().toString();
                image = "3";
                break;

            case R.id.llt_5:
                for (int i = 0; i < imageViews.size(); i++) {
                    if (i == 4) {
                        imageViews.get(i).setImageResource(R.drawable.add_icon_selected);
                    } else {
                        imageViews.get(i).setImageResource(0);
                    }
                }
                temp.setVisibility(View.VISIBLE);
                next.setVisibility(View.GONE);
                KeyBoardUtils.openKeybord(et_diy, OldAddContactActivity.this);
                et_diy.setHint(text5.getText().toString());

                tv_sure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(et_diy.getText().toString().trim())) {
                            BaseApplication.getInstances().toast(OldAddContactActivity.this,"请输入名称");

                        } else if (containsEmoji(et_diy.getText().toString().trim())) {
                            BaseApplication.getInstances().toast(OldAddContactActivity.this,"含有非法字符！");
                            et_diy.setText("");
                        } else if (et_diy.getText().toString().getBytes().length > 12) {
                            BaseApplication.getInstances().toast(OldAddContactActivity.this,"超过限定字符长度！");
                            et_diy.setText("");
                        } else {
                            name = et_diy.getText().toString().trim().replaceAll(" ", "");
                            temp.setVisibility(View.GONE);
//                            next.setVisibility(View.VISIBLE);
                            KeyBoardUtils.closeKeybord(et_diy, OldAddContactActivity.this);
                            toAddContactNumberActivity();
                        }


                    }
                });
                // TODO: 2017/2/23 自定义
                name = text5.getText().toString();
                image = "8";
                break;

        }
    }


    private void changeRelation(String name,String icon) {

        try {
            String change_name = URLEncoder.encode(name, "utf-8");
            Intent intent = new Intent();
            intent.putExtra("changename",change_name);
            intent.putExtra("changeicon",icon);
            setResult(RESULT_OK,intent);
            finish();
//            for (int j =0;j<contacts.size();j++) {
//                if (contacts.get(j).getName().equals(change_name)) {
//                    BaseApplication.getInstances().toast(OldAddContactActivity.this,"该联系人名字已存在！");
//                    break;
//                }else if (j==contacts.size()-1) {
//                    Intent intent = new Intent();
//                    intent.putExtra("changename",change_name);
//                    intent.putExtra("changeicon",icon);
//                    setResult(RESULT_OK,intent);
//                    finish();
//                }
//            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private void toAddContactNumberActivity() {

        if (name!=null&&!TextUtils.isEmpty(name)){
            Intent intent = new Intent(OldAddContactActivity.this, AddContactNumberActivity.class);

            intent.putExtra(AddContactNumberActivity.PARAMS_NAME,name);
            intent.putExtra(AddContactNumberActivity.PARAMS_ICON,image);
            intent.putExtra(AddContactNumberActivity.ID,device_id);
            intent.putExtra(AddContactNumberActivity.TYPE,"old");
            startActivity(intent);
            finish();
        }

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

    /*****************************       输入过滤       *****************************/
    /** * 检测是否有emoji表情 * @param source * @return */
    public static boolean containsEmoji(String source) {                          //两种方法限制emoji
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }


    /** * 判断是否是Emoji * @param codePoint 比较的单个字符 * @return */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }
}
