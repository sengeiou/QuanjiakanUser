package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidquanjiakan.adapterholder.MedicineClassifyHolder;
import com.quanjiakan.main.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/29 0029.
 */
public class MedicineClassifyAdapter extends BaseAdapter {
    private List<String> data;
    private Context context;

    public MedicineClassifyAdapter(Context context,List<String> list){
        if(list==null){
            data = new ArrayList<String>();
        }
        data = list;
        this.context = context;
    }

    public List<String> getData(){
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
        MedicineClassifyHolder holder;
        if(view==null){
            holder = new MedicineClassifyHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_medicine_classify,null);
            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            view.setTag(holder);
        }else{
            holder = (MedicineClassifyHolder) view.getTag();
        }
        holder.tv_name.setText(data.get(i));
        return view;
    }
}
