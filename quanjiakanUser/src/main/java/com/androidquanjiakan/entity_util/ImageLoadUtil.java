package com.androidquanjiakan.entity_util;

import android.graphics.Color;
import android.widget.ImageView;

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.view.CircleTransformation;
import com.androidquanjiakan.view.RoundTransform;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.quanjiakan.main.R;
import com.androidquanjiakan.util.CheckUtil;
import com.squareup.picasso.Picasso;

/**
 * Created by Administrator on 2016/6/17 0017.
 */
public class ImageLoadUtil {
    public static final DisplayImageOptions
    optionsRoundCorner = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_stub)
    .showImageForEmptyUri(R.drawable.ic_empty)
    .showImageOnFail(R.drawable.ic_empty)
    .cacheInMemory(true)
    .cacheOnDisk(true)
    .considerExifParams(true)
//                    .displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
    /**
     * 圆角图片
     */
    .displayer(new RoundedBitmapDisplayer(20))
            .build();

    public static final DisplayImageOptions
            optionsRoundCorner_Person = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_stub)
            .showImageForEmptyUri(R.drawable.touxiang_person)
            .showImageOnFail(R.drawable.touxiang_person)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
//                    .displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
            /**
             * 圆角图片
             */
            .displayer(new RoundedBitmapDisplayer(20))
            .build();

    public static final DisplayImageOptions
            optionsRoundCorner_5 = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_stub)
            .showImageForEmptyUri(R.drawable.ic_empty)
            .showImageOnFail(R.drawable.ic_empty)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
//                    .displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
            /**
             * 圆角图片
             */
            .displayer(new RoundedBitmapDisplayer(5))
            .build();

    public static final DisplayImageOptions
            optionsCircle = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_stub)
            .showImageForEmptyUri(R.drawable.ic_empty)
            .showImageOnFail(R.drawable.ic_empty)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
                    .displayer(new CircleBitmapDisplayer(Color.WHITE, 5)).build();
//            /**
//             * 圆角图片
//             */
//            .displayer(new RoundedBitmapDisplayer(20))
//            .build();

    public static final DisplayImageOptions
            optionsNormal = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_stub)
            .showImageForEmptyUri(R.drawable.ic_empty)
            .showImageOnFail(R.drawable.ic_empty)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true).build();

    private static final int IMG_NULL = 0;
    private static final int IMG_NET = 1;
    private static final int IMG_LOCAL = 2;

    public static void loadLocalImage(ImageView imageView,String imagePath,DisplayImageOptions options){
        if(imagePath.startsWith("file://")){
            ImageLoader.getInstance().displayImage(imagePath,imageView,options);
        }else{
            ImageLoader.getInstance().displayImage("file://" +imagePath,imageView,options);
        }
    }

    public static void loadNetImage(ImageView imageView,String imagePath,DisplayImageOptions options){
        ImageLoader.getInstance().displayImage(imagePath,imageView,options);
    }

    public static void loadNullImage(ImageView imageView,String imagePath,DisplayImageOptions options){
        ImageLoader.getInstance().displayImage(imagePath,imageView,options);
    }

    public static int getImageType(String imagePath){
        if(CheckUtil.isEmpty(imagePath)){
            return IMG_NULL;
        }else if(imagePath.startsWith("http") || imagePath.startsWith("HTTP")){
            return IMG_NET;
        }else{
            return IMG_LOCAL;
        }
    }

    public static void loadImage(ImageView imageView,String imagePath,DisplayImageOptions options){
        if(IMG_LOCAL==getImageType(imagePath)){
            loadLocalImage(imageView,imagePath,options);
            return ;
        }else if(IMG_NET==getImageType(imagePath)){
            loadNetImage(imageView,imagePath,options);
            return ;
        }else{
            loadNullImage(imageView,imagePath,options);
            return ;
        }
    }

    public static final int TYPE_CYCLE = 1;
    public static final int TYPE_RECTANGLE = 2;
    public static final int TYPE_ROUND_RECTANGLE = 3;
    public static void picassoLoad(ImageView imageView,String imagePath,int type){
        switch (type){
            case TYPE_CYCLE:
                Picasso.with(BaseApplication.getInstances()).load(imagePath).transform(new CircleTransformation()).into(imageView);
                break;
            case TYPE_RECTANGLE:
                Picasso.with(BaseApplication.getInstances()).load(imagePath).into(imageView);
                break;
            case TYPE_ROUND_RECTANGLE:
                Picasso.with(BaseApplication.getInstances()).load(imagePath).transform(new RoundTransform()).into(imageView);
                break;
        }
    }

    /**
     * 铺满整个组件【图像会被拉伸出现形变】
     * @param imageView
     * @param imagePath
     * @param type
     */
    public static void picassoLoadFit(ImageView imageView,String imagePath,int type){
        switch (type){
            case TYPE_CYCLE:
                Picasso.with(BaseApplication.getInstances()).load(imagePath).fit().transform(new CircleTransformation()).into(imageView);
                break;
            case TYPE_RECTANGLE:
                Picasso.with(BaseApplication.getInstances()).load(imagePath).fit().into(imageView);
                break;
            case TYPE_ROUND_RECTANGLE:
                Picasso.with(BaseApplication.getInstances()).load(imagePath).fit().transform(new RoundTransform()).into(imageView);
                break;
        }
    }

}
