package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.adapterholder.FriendHolder;
import com.androidquanjiakan.entity.FriendsBean;
import com.quanjiakan.main.R;

import java.util.List;

/**
 * Created by Administrator on 2016/7/22 0022.
 */
public class Friend_FriendsAdapter extends BaseAdapter {

    private List<FriendsBean> data;
    private Context context;
    private String grouperId;
    private List<String> itemId;


    public Friend_FriendsAdapter(Context context, List<FriendsBean> list){
        data = list;
        this.context = context;

    }

    public  void setData(List<String> itemId,String grouperId){
        this.grouperId=grouperId;
        this.itemId=itemId;
    }

    public void resetData(List<FriendsBean> list){
        this.data = list;
    }

    public List<FriendsBean> getData(){
        return data;
    }

    @Override
    public int getCount() {
        if(data!=null){
            return data.size();
        }
        return 0;
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
        final FriendHolder holder;
        if(view==null){
            holder = new FriendHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_friend_friend,null);
            holder.header = (ImageView) view.findViewById(R.id.header);
            holder.info = (TextView) view.findViewById(R.id.info);
            view.setTag(holder);
        }else {
            holder = (FriendHolder) view.getTag();
        }
        holder.info.setText(data.get(i).getFriendName());

        return view;
    }


}
