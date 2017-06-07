package com.androidquanjiakan.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.androidquanjiakan.activity.index.watch_child.elder.EditRemindActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.entity.CareRemindBean;
import com.androidquanjiakan.entity.CareRemindEntity;
import com.androidquanjiakan.util.LogUtil;
import com.example.greendao.dao.CareRemindBeanDao;
import com.quanjiakan.main.R;

import java.util.List;

/**
 * 作者：Administrator on 2017/4/6 15:20
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


public class CareRemindAdapter extends BaseAdapter{



    private List<CareRemindEntity> mData;
    private Context mContext;
    private boolean isOpen = false;

    public CareRemindAdapter(Context mContext, List<CareRemindEntity> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public void setmData(List<CareRemindEntity> mData) {
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView==null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_care_remind, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.item = (LinearLayout) convertView.findViewById(R.id.item);
            holder.week = (TextView) convertView.findViewById(R.id.tv_week);
            holder.button = (ToggleButton) convertView.findViewById(R.id.btn_open_close);
            holder.container = (LinearLayout) convertView.findViewById(R.id.llt_container);

            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toEditRemind(position);
            }
        });

        holder.title.setText(mData.get(position).getTitle());
        holder.week.setText(mData.get(position).getWeek().substring(0,mData.get(position).getWeek().length()-1));

        if (mData.get(position).getButton().equals("0")) {
            holder.button.setBackgroundResource(R.drawable.edit_btn_close);
            isOpen = false;
        }else {
            holder.button.setBackgroundResource(R.drawable.edit_btn_open);
            isOpen = true;
        }

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpen) {
                    holder.button.setBackgroundResource(R.drawable.edit_btn_close);
                }else {
                    holder.button.setBackgroundResource(R.drawable.edit_btn_open);
                }
                isOpen = !isOpen;

                CareRemindBeanDao dao = BaseApplication.getInstances().getDaoInstant().getCareRemindBeanDao();
                CareRemindBean bean = dao.loadAll().get(position);
                if (isOpen) {
                    bean.setButton("1");
                }else {
                    bean.setButton("0");
                }
                dao.update(bean);

            }
        });

        String time = mData.get(position).getTime();
        LogUtil.e("---------changdu----"+mData.size());
        LogUtil.e("shijian------"+time);
        String[] split = time.split(",");
        if (holder.container.getChildCount()>0) {
            holder.container.removeAllViews();
        }

        for (int i=0;i<split.length;i++) {
            LogUtil.e("------danwei ------"+split[i]);
            TextView textView = new TextView(mContext);
            textView.setText(split[i]);
            textView.setTextColor(mContext.getResources().getColor(R.color.color_xun_ing));
            textView.setTextSize(QuanjiakanUtil.px2sp(mContext,40));
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundResource(R.drawable.selecter_btn_bac_green_small);
//            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(146,58);
            lp.setMargins(0,0,18,0);
//            textView.setPadding(10,0,10,0);
            textView.setLayoutParams(lp);
            holder.container.addView(textView);
        }



        return convertView;
    }

    //编辑提醒
    private void toEditRemind(int i) {

        Intent intent = new Intent(mContext, EditRemindActivity.class);
        intent.putExtra(EditRemindActivity.TYPE, "edit");
        intent.putExtra("position", i);
        mContext.startActivity(intent);
    }




    class ViewHolder {
        LinearLayout item;
        TextView title;
        TextView week;
        ToggleButton button;
        LinearLayout container;//动态添加textview的容器
    }
}
