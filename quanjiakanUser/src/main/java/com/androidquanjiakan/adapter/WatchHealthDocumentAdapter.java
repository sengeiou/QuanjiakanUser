package com.androidquanjiakan.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.activity.common.ImageViewerActivity;
import com.androidquanjiakan.adapterholder.HealthDocumentHolder;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.entity.WatchCaseHistoryEntity;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.quanjiakan.main.R;

import java.util.List;

/**
 * Created by Administrator on 2016/11/25 0025.
 */

public class WatchHealthDocumentAdapter extends BaseAdapter {

    private List<WatchCaseHistoryEntity> data;
    private Context context;

    public WatchHealthDocumentAdapter(Context context, List<WatchCaseHistoryEntity> data){
        this.context = context;
        this.data = data;
    }

    public List<WatchCaseHistoryEntity> getData() {
        return data;
    }

    public void setData(List<WatchCaseHistoryEntity> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        if(data!=null){
            return data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HealthDocumentHolder holder;
        if(convertView==null){
            holder = new HealthDocumentHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_health_document,null);
            holder.div = convertView.findViewById(R.id.div);
            holder.image = (ImageView) convertView.findViewById(R.id.image);
            holder.intro_content = (TextView) convertView.findViewById(R.id.intro_content);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        }else{
            holder= (HealthDocumentHolder) convertView.getTag();
        }
        final WatchCaseHistoryEntity entity = data.get(position);
        if(position==data.size()-1){
            holder.div.setVisibility(View.VISIBLE);
        }else{
            holder.div.setVisibility(View.VISIBLE);
        }
        if(entity!=null &&entity.getMedical_record()!=null && entity.getMedical_record().toLowerCase().startsWith("http")){
            ImageLoadUtil.loadImage(holder.image,entity.getMedical_record(),null);
            ImageLoadUtil.picassoLoad(holder.image,entity.getMedical_record(),ImageLoadUtil.TYPE_RECTANGLE);
            holder.image.setBackgroundResource(R.drawable.transparent_background);//transparent_background  case_pic_border
        }else{
            holder.image.setImageResource(R.drawable.ic_patient);
            holder.image.setBackgroundResource(R.drawable.transparent_background);
        }
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(entity!=null &&entity.getMedical_record()!=null && entity.getMedical_record().toLowerCase().startsWith("http")) {
                    Intent intent = new Intent(context, ImageViewerActivity.class);
                    intent.putExtra(BaseConstants.PARAMS_URL, entity.getMedical_record());
                    context.startActivity(intent);
                }else{
                    BaseApplication.getInstances().toast(context,"无效的网络图片!");
                }
            }
        });
        if(entity!=null && entity.getMedical_name()!=null && entity.getMedical_name().length()>0){
            holder.intro_content.setText(""+entity.getMedical_name());
        }else{
            holder.intro_content.setText("");
        }
        if(entity!=null && entity.getCreatetime()!=null){
            holder.time.setText("发布时间: "+((entity.getCreatetime()!=null && entity.getCreatetime().length()>19)? entity.getCreatetime().substring(0,19):entity.getCreatetime()));
        }else{
            holder.time.setText("");
        }
        return convertView;
    }
}
