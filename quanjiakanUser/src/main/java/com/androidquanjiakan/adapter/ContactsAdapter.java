package com.androidquanjiakan.adapter;

/**
 * 作者：Administrator on 2017/2/20 09:42
 * <p>
 * 邮箱：liuzj@hi-board.com
 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.entity.ContactsBean;
import com.androidquanjiakan.result.ContactsResultBean;
import com.androidquanjiakan.view.CircleTransformation;
import com.quanjiakan.main.R;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;


public class ContactsAdapter extends BaseAdapter{



    private List<ContactsResultBean.ResultsBean.ContactsBean> mData;
    private Context mContext;
    private int[] arr_image = {R.drawable.contact_pic_portrait,R.drawable.contacts_pic_mom,R.drawable.contacts_pic_grandpa,
            R.drawable.contacts_pic_grandma,R.drawable.contacts_pic_grandpa2,R.drawable.contacts_pic_grandma2
            ,R.drawable.contacts_pic_sister,R.drawable.contacts_pic_borther,R.drawable.contacts_pic_custom};


    public ContactsAdapter(List<ContactsResultBean.ResultsBean.ContactsBean> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    public void setmData(List<ContactsResultBean.ResultsBean.ContactsBean> mData) {
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_menu_listview, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.num = (TextView) convertView.findViewById(R.id.tv_number);
            holder.flag = (TextView) convertView.findViewById(R.id.tv_flag);
            holder.icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.flag_icon = (ImageView) convertView.findViewById(R.id.flag_icon);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
// TODO: 2017/2/23 这里还要做处理
        if ("1".equals(mData.get(position).getAdmin())){
            holder.flag.setVisibility(View.VISIBLE);
        }else {
            holder.flag.setVisibility(View.GONE);
        }
        if (!"(null)".equals(mData.get(position).getName())){
            if (mData.get(position).getName().contains("%")) {
                try {
                    holder.name.setText(URLDecoder.decode(mData.get(position).getName(),"utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }else {
                holder.name.setText(mData.get(position).getName());
            }
//            holder.name.setText(mData.get(position).getName());

        }
        if (!"(null)".equals(mData.get(position).getTel())) {
            holder.num.setText(mData.get(position).getTel());
        }


        if (mData.get(position).getImage()!=null&&!"(null)".equals(mData.get(position).getImage())) {
            if (mData.get(position).getImage().contains("http")){
                Picasso.with(mContext).load(mData.get(position).getImage()).transform(new CircleTransformation()).into(holder.icon);
            }else {
                Picasso.with(mContext).load(arr_image[Integer.parseInt(mData.get(position).getImage())]).into(holder.icon);
            }
        }

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemOnClickListener.itemOnClickListener(v,position);
            }
        });
        if (mData.get(position).getApp().equals("1")){
            holder.flag_icon.setImageResource(R.drawable.list_iocn_app);
        }else if (mData.get(position).getApp().equals("0")){
            holder.flag_icon.setImageResource(R.drawable.list_iocn_phone);
        }else {
            holder.flag_icon.setImageResource(R.drawable.list_iocn_phone);
        }

        return convertView;
    }


    private ItemOnClickListener mItemOnClickListener;

    public void setmItemOnClickListener(ItemOnClickListener listener){
        this.mItemOnClickListener = listener;
    }

    public interface ItemOnClickListener{
        /**
         * 传递点击的view
         * @param view
         */
        public void itemOnClickListener(View view,int pos);
    }


    class ViewHolder {
        TextView name;
        TextView num;
        TextView flag;
        ImageView icon;
        ImageView flag_icon;
    }

    @Override
    public int getItemViewType(int position) {
//        return super.getItemViewType(position);
        if (mData.get(position).getAdmin().equals("1")) {
            return 0;//管理员
        }else if (mData.get(position).getApp().equals("1")){
            return 1;//审核
        }else {
            return 2;//普通添加
        }
    }

    @Override
    public int getViewTypeCount() {
//        return super.getViewTypeCount();
        return 3;
    }
}
