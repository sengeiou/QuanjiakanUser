package com.androidquanjiakan.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquanjiakan.adapterholder.MainListVIewHolder;
import com.androidquanjiakan.entity.HealthMessageEntity;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.quanjiakan.main.R;

import java.util.List;

/**
 * Created by Administrator on 2016/6/3 0003.
 */
public class MainListViewAdapter extends BaseAdapter {
    private Activity context;
    private List<HealthMessageEntity> list;

    public final DisplayImageOptions
            optionsRoundCorner = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_stub)
            .showImageForEmptyUri(R.drawable.ic_empty)
            .showImageOnFail(R.drawable.ic_empty)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .displayer(new RoundedBitmapDisplayer(5))
            .build();

    public MainListViewAdapter(Activity context,List<HealthMessageEntity> data){
        this.context = context;
        this.list = data;
    }

    public List<HealthMessageEntity> getData(){
        return list;
    }

    public void setData(List<HealthMessageEntity> data){
        this.list = data;
    }

    @Override
    public int getCount() {
        if(list!=null){
            return list.size();
        }else{
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        MainListVIewHolder holder;
        if(view==null){
            holder = new MainListVIewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_main_listview,null);
            holder.header = (LinearLayout) view.findViewById(R.id.item_main_listview_header);
            holder.header_title_name = (TextView) view.findViewById(R.id.item_main_listview_title_name);
            holder.content = (RelativeLayout) view.findViewById(R.id.item_main_listview_content);
            holder.content_image = (ImageView) view.findViewById(R.id.item_main_listview_content_image);
            holder.content_top = (TextView) view.findViewById(R.id.item_main_listview_content_text_top);
            holder.content_bottom = (TextView) view.findViewById(R.id.item_main_listview_content_text_bottom);
            holder.content_time = (TextView) view.findViewById(R.id.item_main_listview_content_text_time);
            view.setTag(holder);
        }else{
            holder = (MainListVIewHolder) view.getTag();
        }
        /**
         *  实例化，根据数据判断标题栏是否需要显示
         */
        if(i==0){
            holder.header.setVisibility(View.VISIBLE);
            holder.header_title_name.setText("养生课堂");
        }else if(i>0){
            holder.header.setVisibility(View.GONE);
        }
        /**
         *
         */
        holder.content_top.setText(list.get(i).getTitle());
        holder.content_bottom.setText(list.get(i).getContent());
//        holder.content_time.setText(list.get(i).getTime());
        holder.content_time.setVisibility(View.GONE);

        ImageLoadUtil.loadImage(holder.content_image,list.get(i).getImg(),optionsRoundCorner);
        return view;
    }
}
