package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.entity.ChangeAuthorityEntity;
import com.quanjiakan.main.R;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * Created by 2017/2/23.
 */

public class ChangeAuthorityAdapter extends BaseAdapter {

    private Context context;
    private List<ChangeAuthorityEntity> list;

    public void setList(List<ChangeAuthorityEntity> list) {
        this.list = list;
    }

    public ChangeAuthorityAdapter(Context context, List<ChangeAuthorityEntity> list){
        this.context=context;
        this.list=list;

    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view==null) {
            holder=new ViewHolder();
            view= LayoutInflater.from(context).inflate(R.layout.item_change_authority,null);
            holder.iv_select= (ImageView) view.findViewById(R.id.iv_selected);
            holder.tv_name= (TextView) view.findViewById(R.id.tv_name);
            holder.tv_number= (TextView) view.findViewById(R.id.tv_number);
            holder.iv_app= (ImageView) view.findViewById(R.id.iv_app);
            view.setTag(holder);
        }else {
            holder= (ViewHolder) view.getTag();
        }
        ChangeAuthorityEntity entity = list.get(i);
        if(entity.isSelect()) {
            Picasso.with(context).load(R.drawable.jurisdiction_rb_sel).into(holder.iv_select);
        }else {
            Picasso.with(context).load(R.drawable.jurisdiction_rb_nor).into(holder.iv_select);
        }
        
        if(entity.isApp()) {
            Picasso.with(context).load(R.drawable.list_iocn_app).into(holder.iv_app);
        }else {
            Picasso.with(context).load(R.drawable.list_iocn_phone).into(holder.iv_app);
        }
        if (entity.getName().contains("%")) {
            try {

                holder.tv_name.setText(URLDecoder.decode(entity.getName(),"utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }else {
            holder.tv_name.setText(entity.getName());
        }

        holder.tv_number.setText(entity.getNumber());
        return view;
    }
    
    
    class ViewHolder{
        private ImageView iv_select;
        private TextView tv_name;
        private TextView tv_number;
        private ImageView iv_app;
    }
}
