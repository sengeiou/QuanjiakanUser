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

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.androidquanjiakan.entity.VolunteerMesEntity;
import com.quanjiakan.main.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;


/**
 * Created by Gin on 2016/11/23.
 */

public class VolunteerMesAdapter extends BaseAdapter{
    private Context context;
    private List<VolunteerMesEntity>datas;

    public VolunteerMesAdapter(Context context, List<VolunteerMesEntity>datas){
        this.context=context;
        this.datas=datas;
    }

    public void setDatas(List<VolunteerMesEntity>datas){
        this.datas=datas;
    }
    public List<VolunteerMesEntity> getDatas() {
        return datas;
    }
    @Override
    public int getCount() {
        return datas!=null?datas.size():0;
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
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
            view = LayoutInflater.from(context).inflate(R.layout.item_volunteer_mes, null);
            holder.headIcon= (ImageView) view.findViewById(R.id.head_icon);
            holder.tv_name= (TextView) view.findViewById(R.id.name);
            holder.tv_time= (TextView) view.findViewById(R.id.time);
            holder.tv_read= (TextView) view.findViewById(R.id.status);

            view.setTag(holder);
        }else {
            holder= (ViewHolder) view.getTag();
        }
        VolunteerMesEntity volunteerMesEntity = datas.get(i);
        Picasso.with(context).load(volunteerMesEntity.getPicture()).error(R.drawable.ic_launcher).transform(new CircleTransformation()).into(holder.headIcon);
        holder.tv_name.setText("来自义工   "+volunteerMesEntity.getName()+"的消息");
        holder.tv_time.setText(volunteerMesEntity.getCreatetime());
        if(volunteerMesEntity.isRead()) {
            holder.tv_read.setText("已阅读");
            holder.tv_read.setTextColor(context.getResources().getColor(R.color.font_color_999999));
        }else {
            holder.tv_read.setText("未阅读");
            holder.tv_read.setTextColor(context.getResources().getColor(R.color.maincolor));
        }

        return view;
    }

    class ViewHolder{
        ImageView headIcon;
        TextView tv_name;
        TextView tv_time;
        TextView tv_read;
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
