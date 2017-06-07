package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.entity.MessageRecordBean;
import com.androidquanjiakan.entity.WatchMsgRecordEntity;
import com.androidquanjiakan.view.CircleTransformation;
import com.quanjiakan.main.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * 作者：Administrator on 2017/2/20 17:09
 * <p>
 * 邮箱：liuzj@hi-board.com
 */
public class MsgRecordAdapter extends BaseAdapter{



    private List<WatchMsgRecordEntity> mDatas;
    private Context mContext;

    public MsgRecordAdapter(List<WatchMsgRecordEntity> mDatas, Context mContext) {
        this.mDatas = mDatas;
        this.mContext = mContext;
    }

    public void setmDatas(List<WatchMsgRecordEntity> mDatas) {
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_msg_record_watch,parent,false);
            holder = new ViewHolder();
            holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tv_describe = (TextView) convertView.findViewById(R.id.tv_describe);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

//        holder.tv_describe.setText(mDatas.get(position));
//        holder.tv_content.setText(mDatas.get(position));
        Picasso.with(mContext).load(mDatas.get(position).getImage()).transform(new CircleTransformation()).into(holder.iv_icon);

        holder.tv_describe.setText(mDatas.get(position).getTitle());
        holder.tv_time.setText(mDatas.get(position).getTime());
        holder.tv_content.setText(mDatas.get(position).getMsg());
        return convertView;
    }


    class ViewHolder {
        ImageView iv_icon;
        TextView tv_describe;
        TextView tv_content;
        TextView tv_time;

    }
}
