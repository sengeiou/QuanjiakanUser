package com.androidquanjiakan.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.quanjiakan.main.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

/**
 * Created by Gin on 2016/12/1.
 */

public class AudienceHeadAdapter extends RecyclerView.Adapter<AudienceHeadAdapter.ViewHodler> {

    private LayoutInflater mInflater;
    private List<Integer> mDatas;
    private Context context;

    public AudienceHeadAdapter(Context context, List<Integer> datas){
        this.context=context;
        mInflater= LayoutInflater.from(context);
        this.mDatas=datas;

    }
    @Override
    public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_audience_head_adapter, parent,false);
        ViewHodler hodler = new ViewHodler(view);
        hodler.img=(ImageView)view.findViewById(R.id.iv_head);

        return hodler;
    }

    @Override
    public void onBindViewHolder(ViewHodler holder, int position) {
        Picasso.with(context).load(mDatas.get(position)).transform(new CircleTransformation()).into(holder.img);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }


    public class ViewHodler extends RecyclerView.ViewHolder{

        public ViewHodler(View itemView) {
            super(itemView);
        }

        ImageView img;
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
