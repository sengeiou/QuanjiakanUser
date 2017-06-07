package com.quanjiakanuser.widget;

import java.util.ArrayList;

import com.google.gson.JsonObject;
import com.quanjiakan.main.R;
import com.androidquanjiakan.base.QuanjiakanUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ProblemListAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<JsonObject> mList;
	private LayoutInflater inflater;
	
	public ProblemListAdapter(Context context,ArrayList<JsonObject> mList){
		this.context = context;
		this.mList = mList;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int p, View v, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(v == null){
			v = inflater.inflate(R.layout.item_problem, null);
			holder = new ViewHolder();
			holder.tv_title = (TextView)v.findViewById(R.id.tv_title);
			holder.tv_info = (TextView)v.findViewById(R.id.tv_info);
			holder.tv_status = (TextView)v.findViewById(R.id.tv_status);
			v.setTag(holder);
		}else {
			holder = (ViewHolder)v.getTag();
		}
		bindData(p, holder);
		return v;
	}
	
	protected void bindData(int pos,ViewHolder holder){
		JsonObject object = mList.get(pos);
		holder.tv_title.setText(object.get("title").getAsString());
		String date = QuanjiakanUtil.getDateTimePattern(Long.parseLong(object.get("date").getAsString()),"MM月dd日");
		String clinic = object.get("clinic").getAsString();
		String price = "免费";
		holder.tv_info.setText(date+" | "+clinic+" | "+price);
	}
	
	class ViewHolder{
		TextView tv_title;
		TextView tv_info;
		TextView tv_status;
	}
	
}
