package com.androidquanjiakan.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.androidquanjiakan.entity.GiftBean;
import com.androidquanjiakan.entity.ProdctBean;
import com.quanjiakan.main.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * GridView加载数据的适配器
 * @author Administrator
 *
 */
public class GiftGridViewAdpter extends BaseAdapter {

	private Context context;
	private List<GiftBean> lists;//数据源
	private int mIndex; // 页数下标，标示第几页，从0开始
	private int mPargerSize;// 每页显示的最大的数量



	public GiftGridViewAdpter(Context context, List<GiftBean> lists,
							  int mIndex, int mPargerSize) {
		this.context = context;
		this.lists = lists;
		this.mIndex = mIndex;
		this.mPargerSize = mPargerSize;
	}

	/**
	 * 先判断数据及的大小是否显示满本页lists.size() > (mIndex + 1)*mPagerSize
	 * 如果满足，则此页就显示最大数量lists的个数
	 * 如果不够显示每页的最大数量，那么剩下几个就显示几个
	 */
	@Override
	public int getCount() {
		return lists.size() > (mIndex + 1) * mPargerSize ?
				mPargerSize : (lists.size() - mIndex*mPargerSize);
	}

	@Override
	public GiftBean getItem(int arg0) {
		return lists.get(arg0 + mIndex * mPargerSize);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0 + mIndex * mPargerSize;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_gift_view, null);
			holder.tv_name = (TextView)convertView.findViewById(R.id.item_name);
			holder.iv_nul = (ImageView)convertView.findViewById(R.id.item_image);
			holder.iv_lian = (ImageView)convertView.findViewById(R.id.iv_lian);
			holder.item_edou = (TextView)convertView.findViewById(R.id.item_edou);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder)convertView.getTag();
		}

		final int pos = position + mIndex * mPargerSize;//假设mPageSiez

		holder.tv_name.setText(lists.get(pos).getName());
		Picasso.with(context).load(lists.get(pos).getIcon()).into(holder.iv_nul);
		if (lists.get(pos).getPrice()>0&&lists.get(pos).getPrice()<=10){
			holder.iv_lian.setVisibility(View.VISIBLE);
		}else {
			holder.iv_lian.setVisibility(View.INVISIBLE);
		}

		holder.item_edou.setText(lists.get(pos).getPrice()+"E豆");

		return convertView;
	}
	static class ViewHolder{
		private TextView tv_name;
		private ImageView iv_nul;
		private ImageView iv_lian;
		private TextView item_edou;
	}
}
