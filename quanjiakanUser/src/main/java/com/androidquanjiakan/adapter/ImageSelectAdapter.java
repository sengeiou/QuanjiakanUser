package com.androidquanjiakan.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.androidquanjiakan.adapterholder.ImageSelectHolder;
import com.androidquanjiakan.entity.ImageSelectEntity;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.quanjiakan.main.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/20 0020.
 */
public class ImageSelectAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private DisplayImageOptions options;

    private List<ImageSelectEntity> data;
    private boolean isSelectComponent;

    private boolean isShowProgress;

    public ImageSelectAdapter(Context context,List<ImageSelectEntity> list,boolean isSelectComponent,boolean isShowProgress){
        if(list==null || list.size()<1){
            data = new ArrayList<ImageSelectEntity>();
        }else{
            if(data!=null){
                data.clear();
            }else{
                data = new ArrayList<ImageSelectEntity>();
            }
            data.addAll(list);
        }
        this.isSelectComponent = isSelectComponent;
        this.isShowProgress = isShowProgress;
        inflater = LayoutInflater.from(context);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public List<ImageSelectEntity> getData(){
        return data;
    }

    @Override
    public int getCount() {
        return data.size();
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
        final ImageSelectHolder holder;
        if(view==null){
            holder = new ImageSelectHolder();
            view = inflater.inflate(R.layout.item_grid_image,null);
            holder.imageView = (ImageView) view.findViewById(R.id.image);
            holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
            holder.select_state = (ImageView) view.findViewById(R.id.select_state);
            view.setTag(holder);
        }else{
            holder = (ImageSelectHolder) view.getTag();
        }
        final ImageSelectEntity entity = data.get(i);
        if(isShowProgress){
            holder.progressBar.setVisibility(View.VISIBLE);
        }else{
            holder.progressBar.setVisibility(View.GONE);
        }
//        ImageLoader.getInstance()
//                .displayImage(entity.getImgUrl(), holder.imageView, options, new SimpleImageLoadingListener() {
//                    @Override
//                    public void onLoadingStarted(String imageUri, View view) {
//                        if(isShowProgress){
//                            holder.progressBar.setProgress(0);
//                            holder.progressBar.setVisibility(View.VISIBLE);
//                        }
//                    }
//
//                    @Override
//                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                        holder.progressBar.setVisibility(View.GONE);
//                    }
//
//                    @Override
//                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                        holder.progressBar.setVisibility(View.GONE);
//                    }
//                }, new ImageLoadingProgressListener() {
//                    @Override
//                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
//                        holder.progressBar.setProgress(Math.round(100.0f * current / total));
//                    }
//                });

        ImageLoadUtil.loadImage(holder.imageView,entity.getImgUrl(),ImageLoadUtil.optionsRoundCorner);

        if(isSelectComponent){
            holder.select_state.setVisibility(View.VISIBLE);
            if(entity.isSelected()){
                holder.select_state.setBackgroundResource(R.drawable.deliver_address_selected);
            }else{
                holder.select_state.setBackgroundResource(R.drawable.deliver_address_unselected);
            }

            holder.select_state.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(entity.isSelected()){
                        entity.setSelected(false);
                        holder.select_state.setBackgroundResource(R.drawable.deliver_address_unselected);
                    }else{
                        entity.setSelected(true);
                        holder.select_state.setBackgroundResource(R.drawable.deliver_address_selected);
                    }
                }
            });
        }else{
            holder.select_state.setVisibility(View.GONE);
        }

        return view;
    }
}
