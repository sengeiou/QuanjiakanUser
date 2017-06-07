package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquanjiakan.adapterholder.GroupMemberHolder;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.entity.GroupMemberEntity;
import com.androidquanjiakan.interfaces.IRemoveGroupMemberInterface;
import com.quanjiakan.main.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/13 0013.
 */
public class GroupMemberGridAdapter extends BaseAdapter {

    private IRemoveGroupMemberInterface removeInterface;
    /**
     * TODO 需要更换为实体
     * 需要字段：
     * 头像
     * 名称
     * 用户ID
     */
    private List<GroupMemberEntity> mList;//
    private Context context;

    private boolean removeable = false;

    public GroupMemberGridAdapter(Context context, List<GroupMemberEntity> list, boolean removeable, IRemoveGroupMemberInterface call){
        this.context = context;
        if(list!=null){
            mList = list;
        }else{
            mList = new ArrayList<GroupMemberEntity>();
        }
        this.removeable = removeable;
        removeInterface = call;
    }

    public void resetRemoveTag(boolean flag){
        this.removeable = flag;
    }

    public List<GroupMemberEntity> getData(){
        return mList;
    }

    @Override
    public int getCount() {
        if(mList!=null){
            return mList.size();
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
    public View getView(final int position, View view, ViewGroup viewGroup) {
        GroupMemberHolder holder;
        if(view==null){
            holder = new GroupMemberHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_group_member,null);
            holder.image = (ImageView) view.findViewById(R.id.image);
            holder.remove = (ImageView) view.findViewById(R.id.remove);
            holder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        }else{
            holder = (GroupMemberHolder) view.getTag();
        }
        if(!mList.get(position).isAddFlag()){
            holder.image.setBackgroundResource(R.drawable.ic_stub);
            if(removeable && !BaseApplication.getInstances().getUser_id().endsWith(mList.get(position).getUsername())){
                holder.remove.setVisibility(View.VISIBLE);
                holder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /**
                         * TODO 是否需要弹出确认对话框
                         */
                        if(removeInterface!=null){
                            removeInterface.removeGroupMember(mList.get(position).getUsername());
                        }
                    }
                });
            }else{
                holder.remove.setVisibility(View.GONE);
            }
            holder.name.setText(mList.get(position).getUsername());
        }else{
            holder.remove.setVisibility(View.GONE);
            holder.image.setBackgroundResource(R.drawable.add_group_member);
            holder.name.setText("");
        }
        return view;
    }
}
