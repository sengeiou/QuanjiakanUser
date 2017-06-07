package com.androidquanjiakan.adapter;

import java.util.ArrayList;

import com.androidquanjiakan.activity.common.ImageViewerActivity;
import com.androidquanjiakan.activity.index.volunteer.VoluteerOrderDetailActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.androidquanjiakan.view.CircleTransformation;
import com.androidquanjiakan.view.RoundTransform;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.quanjiakan.main.R;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class HouseKeeperListAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<JsonObject> mList;
	private LayoutInflater inflater;

	public HouseKeeperListAdapter(Context context,ArrayList<JsonObject> mList){
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
		return mList.get(arg0).get("id").getAsInt();
	}

	@Override
	public View getView(int p, View v, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(v == null){
			v = inflater.inflate(R.layout.item_housekeeper, null);
			holder = new ViewHolder();
			holder.image = (ImageView)v.findViewById(R.id.image);
			holder.tv_price = (TextView)v.findViewById(R.id.tv_price);
			holder.tv_name = (TextView)v.findViewById(R.id.tv_name);
			holder.tv_name_recommond = (TextView) v.findViewById(R.id.tv_name_recommond);
			holder.tv_baomu = (TextView)v.findViewById(R.id.tv_baomu);
			holder.tv_age = (TextView)v.findViewById(R.id.tv_age);
			holder.tv_from_region = (TextView)v.findViewById(R.id.tv_from_region);
			holder.tv_house_company = (TextView)v.findViewById(R.id.tv_house_company);
			v.setTag(holder);
		}else {
			holder = (ViewHolder)v.getTag();
		}
		bindData(holder, p);
		return v;
	}
	
	void bindData(ViewHolder holder,int pos){

		try {
			final JSONObject object = new JSONObject(mList.get(pos).toString());

			holder.tv_name.setText(object.get("name").toString());
			/**
			 * 设置是否为系统推荐
			 */
			if(object.has("language") && object.get("language")!=null && object.get("language").toString().length()>0 && !"null".equalsIgnoreCase(object.get("language").toString())){
				holder.tv_baomu.setText(object.get("language").toString());
			}else{
				holder.tv_baomu.setText("粤语");
			}
			if(object.get("price").toString()!=null && object.get("price").toString().length()>0){
				holder.tv_price.setText(object.get("price").toString()+"元/月");
			}else{
				holder.tv_price.setVisibility(View.GONE);
			}
			holder.tv_age.setText(object.get("age").toString()+"岁");
			/**
			 * 身高
			 */
			if(object.has("height") && object.get("height")!=null && object.get("height").toString().length()>0 && !"null".equalsIgnoreCase(object.get("height").toString())){
				holder.tv_from_region.setText(object.get("height").toString()+"CM");
			}else{
				holder.tv_from_region.setText("");
				holder.tv_from_region.setVisibility(View.GONE);
			}
			if(object.has("experience") && !(object.get("experience") instanceof JsonNull) && object.get("experience").toString().length()>0){
				holder.tv_house_company.setText("广州市巨硅信息科技有限公司" + " 从业"+object.get("experience").toString()+"年");
			}else{
				holder.tv_house_company.setText("广州市巨硅信息科技有限公司");
			}
//			ImageLoader.getInstance().displayImage(object.get("image").toString(), holder.image);//无形变----截取了图片的部分
//			ImageLoader.getInstance().displayImage(object.get("image").toString(), holder.image,
//					ImageLoadUtil.optionsRoundCorner_Person);//整张图进行压缩【长宽比例越大则形变越厉害】
			if(object.has("image") && !(object.get("image") instanceof JsonNull) &&
					object.get("image").toString().length()>0 &&
					object.get("image").toString().toLowerCase().startsWith("http")){
				Picasso.with(context).load(object.get("image").toString()).error(R.drawable.ic_patient).fit().
						transform(new RoundTransform()).into(holder.image);;
			}else{
				Picasso.with(context).load(R.drawable.ic_patient).into(holder.image);
			}

			holder.image.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					try {
						if(object.get("image").toString()!=null && object.get("image").toString().toLowerCase().startsWith("http")){
							Intent intent = new Intent(context, ImageViewerActivity.class);
							intent.putExtra(BaseConstants.PARAMS_URL,object.get("image").toString());
							context.startActivity(intent);
						}else{
							BaseApplication.getInstances().toast(context,"该家政人员无头像!");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	class ViewHolder{
		TextView tv_name;
		TextView tv_name_recommond;
		TextView tv_price;
		TextView tv_baomu;
		TextView tv_age;
		TextView tv_from_region;
		TextView tv_house_company;
		ImageView image;
	}
	
}
