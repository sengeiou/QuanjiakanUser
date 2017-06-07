package com.androidquanjiakan.view;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanUtil;
import com.squareup.picasso.Transformation;

/**
 * Created by Administrator on 2016/12/14 0014.
 */

public class CircleTransformation  implements Transformation {
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
