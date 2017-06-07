package com.androidquanjiakan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.entity.MissingPersonInfo;
import com.androidquanjiakan.util.LogUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.quanjiakan.main.R;
import com.quanjiakanuser.util.DateUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/17.
 */

public class NoticeMissingAdapter extends BaseAdapter {

    private List<MissingPersonInfo> data;
    private Context context;

    public NoticeMissingAdapter(Context context, List<MissingPersonInfo> data) {
        this.context = context;
        this.data = data;
    }

    public List<MissingPersonInfo> getData() {
        return data;
    }

    public void setData(List<MissingPersonInfo> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        if (data != null) {
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_notice_missing, parent, false);

            holder = new ViewHolder(convertView);
            holder.ivIconMissing = (RoundedImageView) convertView.findViewById(R.id.iv_icon_missing);
//            holder.ivIconMissing.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            holder.ivIconMissing.mutateBackground(true);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvNameMissing.setText(data.get(position).getName());
        if (data.get(position).getName().equals("李洁")) {
            LogUtil.e("==============="+data.get(position).getImag());
        }
        if ("0".equals(data.get(position).getGender())) {
            holder.tvGenderMissing.setText("男");
        } else if ("1".equals(data.get(position).getGender())) {
            holder.tvGenderMissing.setText("女");
        }


        holder.tvAgeMissing.setText(data.get(position).getAge()+" 岁");
        holder.tvHeightMissing.setText(data.get(position).getHeight()+" cm");
        holder.tvWeightMissing.setText(data.get(position).getWeight()+" Kg");
        holder.tvTimeMissing.setText(DateUtils.getStrTime(data.get(position).getMissingTime().toString()));
        holder.tvTimePublish.setText(DateUtils.getStrTime(data.get(position).getPublishTime().toString()));
        if (data.get(position).getStatus()==3){
            holder.ivStatus.setImageResource(R.drawable.notice_state_find);
            holder.ivStatus.setScaleType(ImageView.ScaleType.FIT_XY);
        }else {
            holder.ivStatus.setImageResource(R.drawable.notice_state_look);
        }
//        Transformation transformation = new RoundedTransformationBuilder()
//                .cornerRadius(30)//设置角半径
//                .oval(false)
//                .build();
        if (null != data.get(position).getImag() && data.get(position).getImag().length() > 0) {
            Picasso.with(context).load(data.get(position).getImag()).error(R.drawable.record_pic_portrait)
//                    .transform(transformation)
//                    .transform(new RoundTransformDesign(5))
                    .into(holder.ivIconMissing);
        } else {
            Picasso.with(context).load(R.drawable.record_pic_portrait).into(holder.ivIconMissing);
        }
//        holder.ivIconMissing.setScaleType(ImageView.ScaleType.FIT_XY);
        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.tv_name_missing)
        TextView tvNameMissing;
        @BindView(R.id.tv_gender_missing)
        TextView tvGenderMissing;
        @BindView(R.id.tv_age_missing)
        TextView tvAgeMissing;
        @BindView(R.id.tv_height_missing)
        TextView tvHeightMissing;
        @BindView(R.id.tv_weight_missing)
        TextView tvWeightMissing;
        @BindView(R.id.tv_time_missing)
        TextView tvTimeMissing;
        @BindView(R.id.tv_time_publish)
        TextView tvTimePublish;
//        @BindView(R.id.iv_icon_missing)
        RoundedImageView ivIconMissing;
        @BindView(R.id.iv_status)
        ImageView ivStatus;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}

