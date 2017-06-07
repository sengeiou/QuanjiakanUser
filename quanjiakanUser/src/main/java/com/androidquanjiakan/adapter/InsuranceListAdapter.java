package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androidquanjiakan.adapterholder.InsuranceListHolder;
import com.quanjiakan.main.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/7/16 0016.
 */
public class InsuranceListAdapter extends BaseAdapter {

    private Context context;
    private List<String> dataList;

    public InsuranceListAdapter(Context context, List<String> data){
        this.context = context;
        this.dataList = data;
        if(this.dataList==null){
            this.dataList = new ArrayList<String>();
        }
    }

    public List<String> getData(){
        return this.dataList;
    };



    @Override
    public int getCount() {
        if(dataList!=null){
            return dataList.size();
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        InsuranceListHolder holder;
        if(view==null){
            holder = new InsuranceListHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_insurance_list,null);
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.classify = (TextView) view.findViewById(R.id.classify);
            holder.company = (TextView) view.findViewById(R.id.company);
            holder.insurance_number = (TextView) view.findViewById(R.id.insurance_number);

            view.setTag(holder);
        }else{
            holder = (InsuranceListHolder) view.getTag();
        }
        holder.insurance_number.setText(dataList.get(position));
        return view;
    }
}
