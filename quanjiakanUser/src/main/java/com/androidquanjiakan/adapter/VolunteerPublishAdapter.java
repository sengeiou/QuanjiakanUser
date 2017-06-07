package com.androidquanjiakan.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.androidquanjiakan.adapterholder.VolunteerPublishHistoryHolder;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.entity.PublishVolunteerEntity;

import com.quanjiakan.main.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;


import java.util.List;

/**
 * Created by Administrator on 2016/10/18 0018.
 */

public class VolunteerPublishAdapter extends BaseAdapter {

    private List<PublishVolunteerEntity> data;
    private Context context;

    public VolunteerPublishAdapter(Context context, List<PublishVolunteerEntity> data){
        this.context = context;
        this.data = data;
    }

    public List<PublishVolunteerEntity> getData() {
        return data;
    }

    public void setData(List<PublishVolunteerEntity> data) {
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
        final VolunteerPublishHistoryHolder holder;
        if(convertView==null){
            holder = new VolunteerPublishHistoryHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_volunteer_publish_history,null);
            holder.mIcon = (ImageView) convertView.findViewById(R.id.head_icon);
            holder.mType = (ImageView) convertView.findViewById(R.id.help_type);
            holder.mName = (TextView) convertView.findViewById(R.id.name);
            holder.mPublishTime = (TextView) convertView.findViewById(R.id.publish_time);
            holder.mServiceTime = (TextView) convertView.findViewById(R.id.service_time);
            holder.mStatus = (TextView) convertView.findViewById(R.id.status);
            convertView.setTag(holder);
        }else{
            holder = (VolunteerPublishHistoryHolder) convertView.getTag();
        }
        PublishVolunteerEntity entity = data.get(position);
        holder.mName.setText(entity.getId());
        if(entity.getLevel().equals("1")) {
            Picasso.with(context).load(R.drawable.record_icon_help).into(holder.mType);
        }else if(entity.getLevel().equals("2")) {
            Picasso.with(context).load(R.drawable.record_icon_urgent).into(holder.mType);
        }
        holder.mServiceTime.setText(entity.getTitle()+"  "+entity.getBegintime());
        holder.mPublishTime.setText("发布时间: "+entity.getCreatetime());
        
        if(entity.getStatus().equals("1")) {
            holder.mStatus.setText("未完成");
        }else if(entity.getStatus().equals("2")) {
            holder.mStatus.setText("已完成");
        }


        Picasso.with(BaseApplication.getInstances()).load(R.drawable.ic_launcher).transform(new CircleTransformation()).into(holder.mIcon);
        //holder.mName.setText(data.get(position));
        return convertView;
    }


    public class CircleTransformation implements Transformation {
        @Override
        public Bitmap transform(Bitmap bitmap) {
            int size = Math.min(bitmap.getWidth(),bitmap.getHeight());
            int x= (bitmap.getWidth()-size)/2;
            int y= (bitmap.getHeight()-size)/2;

            Bitmap squaredBitmap  = Bitmap.createBitmap(bitmap, x, y, size, size);
            if (squaredBitmap!=bitmap){
                bitmap.recycle();
            }

            Bitmap bm = Bitmap.createBitmap(size, size, bitmap.getConfig());
            Canvas canvas = new Canvas(bm);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);//设置去锯齿
            float r = size/2f;
            float r1 = (size- QuanjiakanUtil.dip2px(BaseApplication.getInstances(),5))/2f;
            canvas.drawCircle(r,r,r1,paint);
            squaredBitmap.recycle();
            return bm;

        }

        @Override
        public String key() {
            return "circle";
        }
    }
}