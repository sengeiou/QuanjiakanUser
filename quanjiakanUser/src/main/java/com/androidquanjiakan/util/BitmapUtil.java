package com.androidquanjiakan.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.quanjiakanuser.util.ImageUtils;

/**
 * Created by Administrator on 2017/4/18 0018.
 */

public class BitmapUtil {

    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = ImageUtils.calculateInSampleSize(options, 160, 160);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static Bitmap getSmallBitmap(String filePath,int width,int height) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = ImageUtils.calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }
}
