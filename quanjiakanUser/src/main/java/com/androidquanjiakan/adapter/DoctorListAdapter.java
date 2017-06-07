package com.androidquanjiakan.adapter;

import java.util.List;

import com.androidquanjiakan.activity.common.ImageViewerActivity;
import com.androidquanjiakan.activity.index.doctor.ConsultantEnterActivity;
import com.androidquanjiakan.activity.index.doctor.DoctorDetailActivity;
import com.androidquanjiakan.activity.index.volunteer.VoluteerOrderDetailActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.BaseConstants;
import com.androidquanjiakan.entity_util.ImageLoadUtil;
import com.androidquanjiakan.util.LogUtil;
import com.androidquanjiakan.view.CircleTransformation;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanjiakan.main.R;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class DoctorListAdapter extends BaseAdapter{

	private Context context;
	private List<JsonObject> mList;
	private LayoutInflater inflater;
	
	public DoctorListAdapter(Context context,List<JsonObject> mList){
		this.context = context;
		this.mList = mList;
		inflater = LayoutInflater.from(context);
	}

	public List<JsonObject> getmList() {
		return mList;
	}

	public void setmList(List<JsonObject> mList) {
		this.mList = mList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(mList!=null) {
			return mList.size();
		}else {
			return 0;
		}
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int p, View v, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if(v == null){
			v = inflater.inflate(R.layout.item_doctor, null);
			holder = new ViewHolder();
			holder.image = (ImageView)v.findViewById(R.id.image);
			holder.tv_name = (TextView)v.findViewById(R.id.tv_name);
			holder.tv_clinic_title = (TextView)v.findViewById(R.id.tv_clinic_title);
			holder.tv_rank_name = (TextView) v.findViewById(R.id.tv_rank_name);
			holder.rbar = (RatingBar) v.findViewById(R.id.rbar);
			holder.tv_hospital = (TextView)v.findViewById(R.id.tv_hospital);
			v.setTag(holder);
		}else {
			holder = (ViewHolder)v.getTag();
		}
		bindData(holder, p);
		v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final JsonObject jsonObject = mList.get(p);
				int paycount = 0;
				if(jsonObject.has("paid") && !(jsonObject.get("paid") instanceof JsonNull)){
					paycount = jsonObject.get("paid").getAsInt();
				}
				LogUtil.e("支付次数："+paycount);
				if(paycount>0){
					Intent intent = new Intent(context,DoctorDetailActivity.class);
					intent.putExtra("doctor_id", mList.get(p).get("id").getAsString());
					intent.putExtra(DoctorDetailActivity.PARAMS_PAYNUMBER,paycount);
					context.startActivity(intent);
				}else{
					Intent intent = new Intent(context,DoctorDetailActivity.class);
					intent.putExtra("doctor_id", mList.get(p).get("id").getAsString());
					context.startActivity(intent);
				}
			}
		});
		return v;
	}
	
	void bindData(final ViewHolder holder,final  int pos){
		/**
		 * 原付款版本
		 */
		final JsonObject object = mList.get(pos);
		if(object.has("name")  &&  object.get("name")!=null && !(object.get("name") instanceof JsonNull)){
			holder.tv_name.setText(object.get("name").getAsString());
		}else{
			holder.tv_name.setText("");
		}

		//科室名
		if(object.has("clinic_name") &&  object.get("clinic_name")!=null && !(object.get("clinic_name") instanceof JsonNull)  ){
			holder.tv_clinic_title.setText(object.get("clinic_name").getAsString());
		}else{
			holder.tv_clinic_title.setText("");
			holder.tv_clinic_title.setVisibility(View.GONE);
		}

		//职称
		if(object.has("title")  &&  object.get("title")!=null && !(object.get("title") instanceof JsonNull)  ){
			holder.tv_rank_name.setText(object.get("title").getAsString());
		}else{
			holder.tv_rank_name.setText("");
			holder.tv_rank_name.setVisibility(View.GONE);
		}

		if(object.has("recommend_rate")  &&  object.get("recommend_rate")!=null && !(object.get("recommend_rate") instanceof JsonNull)  ){
			holder.rbar.setRating(object.get("recommend_rate").getAsInt());
		}else{
			holder.rbar.setRating(5);
		}

		if(object.has("icon") && object.get("icon")!=null && !(object.get("icon") instanceof JsonNull) && object.get("icon").getAsString().toLowerCase().startsWith("http")){
//			ImageLoader.getInstance().displayImage(object.get("icon").getAsString(), holder.image,
//					ImageLoadUtil.optionsCircle);
			Picasso.with(BaseApplication.getInstances()).load(object.get("icon").getAsString()).transform(new CircleTransformation()).into(holder.image);
		}else{
			holder.image.setImageResource(R.drawable.ic_patient);
		}

		holder.image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(object!=null &&
						object.has("icon") &&
						object.get("icon")!=null &&
						object.get("icon").getAsString()!=null
						&& !(object.get("icon") instanceof JsonNull) &&
						object.get("icon").getAsString().toLowerCase().startsWith("http")){
					Intent intent = new Intent(context, ImageViewerActivity.class);
					intent.putExtra(BaseConstants.PARAMS_URL,object.get("icon").getAsString());
					context.startActivity(intent);
				}else{
					BaseApplication.getInstances().toast(context,"该医护人员未设置头像!");
				}
			}
		});
		if(object.has("hospitalName") && object.get("hospitalName")!=null  && !(object.get("hospitalName") instanceof JsonNull)){//  原Key:hospital_name   改后:hospitalName
			holder.tv_hospital.setText(object.get("hospitalName").getAsString());
		}else if(object.has("hospital_name") && object.get("hospital_name")!=null  && !(object.get("hospital_name") instanceof JsonNull)){
			holder.tv_hospital.setText(object.get("hospital_name").getAsString());
		}else{
			holder.tv_hospital.setText(" ");
		}

		/*if(object.has("description") && object.get("description")!=null && !(object.get("description") instanceof JsonNull)){
			holder.tv_good_at.setText(object.get("description").getAsString());
		}else if(object.has("good_at") && object.get("good_at")!=null && !(object.get("good_at") instanceof JsonNull)){
			holder.tv_good_at.setText(object.get("good_at").getAsString());
		}else{
			holder.tv_good_at.setText(" ");
		}*/
		/**
		 * 罗鹏版本
		 */
//		JsonObject object = mList.get(pos);
//		holder.tv_name.setText(object.get("name").getAsString());
//		holder.tv_clinic_title.setText(object.get("clinic_name").getAsString() + " "+object.get("title").getAsString());
//		ImageLoader.getInstance().displayImage(object.get("icon").getAsString(), holder.image, ((BaseApplication)((Activity)context).getApplication()).getNormalImageOptions(0, R.drawable.touxiang));
//		holder.tv_hospital.setText(object.get("hospital_name").getAsString());
//		holder.tv_good_at.setText(object.get("good_at").getAsString());
	}
	
	class ViewHolder{
		TextView tv_name;
		TextView tv_clinic_title;
		TextView tv_rank_name;
		TextView tv_hospital;
		RatingBar rbar;
		ImageView image;
	}
	
}
