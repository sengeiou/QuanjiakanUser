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

import com.androidquanjiakan.entity.RecieveGiftEntity;
import com.androidquanjiakan.entity.RecieveGiftEntity_new;
import com.quanjiakan.main.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Gin on 2017/1/12.
 */

public class SendGiftAdapter_new extends BaseAdapter {

    private List<RecieveGiftEntity_new> datas;
    private Context context;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public SendGiftAdapter_new(Context context, List<RecieveGiftEntity_new> datas){
        this.context=context;
        this.datas=datas;

    }

    public List<RecieveGiftEntity_new> getData() {
        return datas;
    }

    public void setData(List<RecieveGiftEntity_new> datas) {
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas!=null?datas.size():0;
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder hodler;
        if(convertView==null) {
            hodler=new ViewHolder();
            convertView=LayoutInflater.from(context).inflate(R.layout.item_sendgift,null);
            hodler.icon= (ImageView) convertView.findViewById(R.id.icon);
            hodler.name= (TextView) convertView.findViewById(R.id.name);
            hodler.gifticon= (ImageView) convertView.findViewById(R.id.iv_gift);
            hodler.giftnumber= (TextView) convertView.findViewById(R.id.tv_gift_number);
            hodler.time= (TextView) convertView.findViewById(R.id.tv_time);

            convertView.setTag(hodler);

        }else {
            hodler= (ViewHolder) convertView.getTag();

        }

        Picasso.with(context).load(datas.get(position).getDocIcon()).transform(new CircleTransformation()).into(hodler.icon);
        hodler.name.setText(datas.get(position).getDocName());
        hodler.giftnumber.setText("一个"+datas.get(position).getPresentName());
        Picasso.with(context).load(datas.get(position).getPresentIcon()).into(hodler.gifticon);
        //送礼物时间
        hodler.time.setText(sdf.format(datas.get(position).getTradeTime()*1000));

        return convertView;
    }


    class ViewHolder{
        private ImageView gifticon;
        private TextView giftnumber;
        private TextView  time;
        private TextView name;
        private ImageView icon;

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
            canvas.drawCircle(r,r,r,paint);
            squaredBitmap.recycle();
            return bm;

        }

        @Override
        public String key() {
            return "circle";
        }
    }
}
