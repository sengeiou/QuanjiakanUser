package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.adapterholder.GroupChatListHolder;
import com.androidquanjiakan.entity.GroupChatListEntity;
import com.quanjiakan.main.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/29 0029.
 */
public class GroupChatListAdapter extends BaseAdapter{

    private List<GroupChatListEntity> data;
    private Context context;

    public GroupChatListAdapter(Context context,List<GroupChatListEntity> data){
        this.context = context;
        if(data == null){
            this.data = new ArrayList<GroupChatListEntity>();
        }else{
            this.data = data;
        }
    }

    public void resetData(List<GroupChatListEntity> list){
        this.data = list;
    }

    public List<GroupChatListEntity> getData(){
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
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final GroupChatListHolder holder;
        if(view==null){
            holder = new GroupChatListHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_groupchat_list,null);
            holder.item_header = (ImageView) view.findViewById(R.id.item_header);
            holder.item_time = (TextView) view.findViewById(R.id.item_time);
            holder.item_name = (TextView) view.findViewById(R.id.item_name);
            holder.item_number = (TextView) view.findViewById(R.id.item_number);
            view.setTag(holder);
        }else{
            holder = (GroupChatListHolder) view.getTag();
        }
        GroupChatListEntity entity = data.get(i);
        holder.item_number.setText(entity.getNumber()+"人");
        holder.item_time.setText("");
        holder.item_name.setText(entity.getName());
        return view;
    }
}
