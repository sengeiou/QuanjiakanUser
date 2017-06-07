package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.entity.MissingPersonInfo;
import com.androidquanjiakan.entity_util.RoundedTransformationBuilder;
import com.androidquanjiakan.view.RoundImageView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.quanjiakan.main.R;
import com.quanjiakanuser.util.DateUtils;
import com.quanjiakanuser.util.TimeFormat;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/11/19.
 */
public class SearchPublishAdapter extends BaseAdapter {

    private List<MissingPersonInfo> data;
    private Context context;

    public SearchPublishAdapter(List<MissingPersonInfo> data, Context context) {
        this.data = data;
        this.context = context;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_search_record, parent, false);
            holder = new ViewHolder(convertView);
            holder.ivIconMissing = (RoundedImageView) convertView.findViewById(R.id.iv_icon_missing);
            holder.ivIconMissing.setScaleType(ImageView.ScaleType.CENTER_CROP);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvNameMissing.setText(data.get(position).getName());
        if ("0".equals(data.get(position).getGender())) {
            holder.tvGenderMissing.setText("男");
        } else if ("1".equals(data.get(position).getGender())) {
            holder.tvGenderMissing.setText("女");
        }
        holder.tvAgeMissing.setText(data.get(position).getAge() + "岁");
        holder.tvHeightMissing.setText(data.get(position).getHeight() + "cm");
        holder.tvWeightMissing.setText(data.get(position).getWeight() + "Kg");
        holder.tvTimeMissing.setText(DateUtils.getStrTime(data.get(position).getMissingTime().toString()));
        holder.tvTimePublish.setText(DateUtils.getStrTime(data.get(position).getPublishTime().toString()));
        if (data.get(position).getStatus() == 3) {
            holder.status.setText("已找到");
            holder.status.setTextColor(context.getResources().getColor(R.color.colorflag));
        } else if (data.get(position).getStatus() == 1) {
            holder.status.setText("审核中");
            holder.status.setTextColor(context.getResources().getColor(R.color.color_status));
        } else if (data.get(position).getStatus() == 2) {
            holder.status.setText("寻找中");
            holder.status.setTextColor(context.getResources().getColor(R.color.color_xun_ing));
        } else if (data.get(position).getStatus() == 4) {
            holder.status.setText("放弃");
            holder.status.setTextColor(context.getResources().getColor(R.color.color_status));

        } else if (data.get(position).getStatus() == 5) {
            holder.status.setText("未通过");
            holder.status.setTextColor(context.getResources().getColor(R.color.color_status));
        }

        Transformation transformation = new RoundedTransformationBuilder()
                .cornerRadius(30)//设置角半径
                .oval(false)
                .build();
        if (null != data.get(position).getImag() && data.get(position).getImag().length() > 0) {
            Picasso.with(context).load(data.get(position).getImag()).error(R.drawable.record_pic_portrait).fit().into(holder.ivIconMissing);
        } else {
            Picasso.with(context).load(R.drawable.record_pic_portrait).into(holder.ivIconMissing);
        }
        holder.ivIconMissing.setScaleType(ImageView.ScaleType.CENTER);

        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.tv_name_missing)
        TextView tvNameMissing;
        @BindView(R.id.tv_time_publish)
        TextView tvTimePublish;
        @BindView(R.id.status)
        TextView status;
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
        //        @BindView(R.id.iv_icon_missing)
        RoundedImageView ivIconMissing;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
