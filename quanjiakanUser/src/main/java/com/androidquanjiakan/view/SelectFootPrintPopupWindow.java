package com.androidquanjiakan.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.quanjiakan.main.R;


/**
 * Created by Administrator on 2016/12/14.
 */

public class SelectFootPrintPopupWindow extends PopupWindow {

    private View view;
    private Context context;
    private final TextView bt_sure;
//    private final EditText et_foot_date;
    private final TextView et_foot_date;
    private final TextView et_foot_begin_time;
    private final TextView et_foot_end_time;
    private final EditText et_space_time;


    public SelectFootPrintPopupWindow(final Activity context, View.OnClickListener itemsOnClick) {


        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.select_foot_print_window, null);
        bt_sure = (TextView) view.findViewById(R.id.bt_sure);

        et_foot_date = (TextView) view.findViewById(R.id.et_foot_date);
        et_foot_begin_time = (TextView) view.findViewById(R.id.et_foot_begin_time);
        et_foot_end_time = (TextView) view.findViewById(R.id.et_foot_end_time);
        et_space_time = (EditText) view.findViewById(R.id.et_space_time);


        et_foot_date.setInputType(InputType.TYPE_NULL);
        et_foot_begin_time.setInputType(InputType.TYPE_NULL);
        et_foot_end_time.setInputType(InputType.TYPE_NULL);


//
//        final InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);


        et_space_time.setInputType(InputType.TYPE_CLASS_NUMBER);
        //此方法只是关闭软键盘

        bt_sure.setOnClickListener(itemsOnClick);

        et_foot_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDateDialog();
            }
        });
        et_foot_begin_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showSelectTimeDialog(et_foot_begin_time, 0);
            }
        });
        et_foot_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showSelectTimeDialog(et_foot_end_time, 1);
            }
        });
        et_space_time.setOnClickListener(itemsOnClick);


        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);

        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setContentView(view);

        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0xb0000000);

        this.setBackgroundDrawable(dw);

        this.setAnimationStyle(R.style.mypopwindow_anim_style);


        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
//        view.setOnTouchListener(new View.OnTouchListener() {
//
//            public boolean onTouch(View v, MotionEvent event) {
//
//                int height = view.findViewById(R.id.llt_popup).getBottom();
//                int y = (int) event.getY();
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    if (y > height) {
//                        dismiss();
//                    }
//                }
//                return true;
//            }
//        });

    }

    private void showSelectTimeDialog(final TextView et, int type) {
        FootPrintTimeDialog footPrintTimeDialog = new FootPrintTimeDialog(context, type);
        footPrintTimeDialog.show();
        footPrintTimeDialog.setBirthdayListener(new FootPrintTimeDialog.OnBirthListener() {
            @Override
            public void onClick(String text) {
                et.setText(text);
            }
        });
    }

    private void showDateDialog() {
        WatchBirthDaySelecterDialog day_dialog = new WatchBirthDaySelecterDialog(context, true);
        day_dialog.show();
        day_dialog.setBirthdayListener(new WatchBirthDaySelecterDialog.OnBirthListener() {

            @Override
            public void onClick(String year, String month, String day) {
                // TODO Auto-generated method stub
                if (Integer.parseInt(month) < 10) {
                    month = "0" + month;
                }
                if (Integer.parseInt(day) < 10) {
                    day = "0" + day;
                }
                et_foot_date.setText(year + "-" + month + "-" + day);
            }
        });
    }


}
