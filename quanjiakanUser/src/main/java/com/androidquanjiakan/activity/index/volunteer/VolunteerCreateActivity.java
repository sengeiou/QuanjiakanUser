package com.androidquanjiakan.activity.index.volunteer;

import android.app.Dialog;

import android.content.Intent;
import android.os.Bundle;

import android.view.Gravity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import android.widget.TextView;


import com.androidquanjiakan.activity.index.out.ConsultantActivity_GoOut_Locate_backup;

import com.androidquanjiakan.base.BaseActivity;

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanDialog;
import com.androidquanjiakan.base.QuanjiakanSetting;

import com.androidquanjiakan.dialog.SelectVolunteerTimeDialog;
import com.androidquanjiakan.entity_util.NetUtil;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;




public class VolunteerCreateActivity extends BaseActivity implements View.OnClickListener{


	private ImageButton mBack;
	private TextView mTitle;
	private TextView mMenu;

	private LinearLayout mLinearUrgency;
	private TextView mTextUrgency;

	private LinearLayout mLinearType;
	private TextView mTypeValue;

	private LinearLayout mLinearLocation;
	private TextView mLocationValue;

	private LinearLayout mLinearTime;
	private TextView mTimeValue;


	private Button mSubmit;

	public static final int VOLUNTEER_REQUEST_MAP=100;

	private final String VOLUNTEER_ADDR="volunteeraddr";
	private final String VOLUNTEER_LAT="volunteerlat";
	private final String VOLUNTEER_LON="volunteerlon";
	private String address;
	private Double lantitude;
	private Double longitude;
	private TextView tv_wash;
	private TextView tv_cooking;
	private TextView tv_cleanup;
	private TextView tv_accompany;
	private TextView tv_chat;
	private TextView tv_look_after;
	private TextView tv_other;

	private boolean isWashSel=false;
	private boolean isCookSel=false;
	private boolean isCleanSel=false;
	private boolean isAccompSel=false;
	private boolean isChatSel=false;
	private boolean isLookAfterSel=false;
	private boolean isOtherSel=false;
	private SelectVolunteerTimeDialog selectVolunteerTimeDialog;
	private EditText et_addtion;


	public void initContent(){
		mLinearUrgency = (LinearLayout) findViewById(R.id.ll_urgency_degree);
		mTextUrgency=(TextView)findViewById(R.id.tv_urgency_value);
		mLinearUrgency.setOnClickListener(this);

		mLinearTime = (LinearLayout) findViewById(R.id.ll_select_time);
		mTimeValue = (TextView) findViewById(R.id.tv_select_time);
		mLinearTime.setOnClickListener(this);

		mLinearLocation = (LinearLayout) findViewById(R.id.ll_location);
		mLocationValue = (TextView) findViewById(R.id.tv_location_value);
		mLinearLocation.setOnClickListener(this);



        mLinearType = (LinearLayout) findViewById(R.id.ll_select_type);
        mTypeValue = (TextView) findViewById(R.id.tv_type_value);
        mLinearType.setOnClickListener(this);

		tv_wash = (TextView) findViewById(R.id.tv_wash_clothes);
		tv_wash.setOnClickListener(this);
		tv_cooking = (TextView)findViewById(R.id.tv_cooking);
		tv_cooking.setOnClickListener(this);
		tv_cleanup = (TextView)findViewById(R.id.tv_cleanup);
		tv_cleanup.setOnClickListener(this);
		tv_accompany = (TextView)findViewById(R.id.tv_accompany);
		tv_accompany.setOnClickListener(this);
		tv_chat = (TextView)findViewById(R.id.tv_chat);
		tv_chat.setOnClickListener(this);
		tv_look_after = (TextView)findViewById(R.id.tv_look_after);
		tv_look_after.setOnClickListener(this);
		tv_other = (TextView)findViewById(R.id.tv_other);
		tv_other.setOnClickListener(this);

		et_addtion = (EditText) findViewById(R.id.et_addtion);


		mSubmit = (Button) findViewById(R.id.btn_submit);
		mSubmit.setOnClickListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_volunteer_create);
		initTitle();
		initContent();

	}

	public void initTitle(){
		mBack = (ImageButton) findViewById(R.id.ibtn_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(this);
		mTitle = (TextView) findViewById(R.id.tv_title);
		mTitle.setText("发布");
		mMenu = (TextView) findViewById(R.id.menu_text);
		mMenu.setVisibility(View.GONE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		MobclickAgent.onPageStart(this.getClass().getSimpleName());
		BaseApplication.getInstances().setCurrentActivity(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		MobclickAgent.onPageEnd(this.getClass().getSimpleName());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.ibtn_back:
				finish();
				break;
			case R.id.ll_urgency_degree:
				//弹出选择紧急对话框
				showSelectUrgencyDialog();
				break;
			
			case R.id.ll_select_time:
				showSelectTimeDialog();
				break;
			
			case R.id.ll_location:
				Intent intent = new Intent(this,ConsultantActivity_GoOut_Locate_backup.class);
				intent.putExtra("flag","fromVolunteer");
				startActivityForResult(intent,VOLUNTEER_REQUEST_MAP);
			    break;
			
            case R.id.ll_select_type:
                showSelectHelpType();
                break;
			
			case R.id.tv_wash_clothes:
			    if(isWashSel) {
			        tv_wash.setBackgroundResource(R.drawable.img_service_release_nor);
					tv_wash.setTextColor(getResources().getColor(R.color.font_color_999999));
			    }else {
					tv_wash.setBackgroundResource(R.drawable.img_service_release_sel);
					tv_wash.setTextColor(getResources().getColor(R.color.white));
				}
				isWashSel=!isWashSel;
			    break;
			
			case R.id.tv_cleanup:
				if(isCleanSel) {
					tv_cleanup.setBackgroundResource(R.drawable.img_service_release_nor);
					tv_cleanup.setTextColor(getResources().getColor(R.color.font_color_999999));
				}else {
					tv_cleanup.setBackgroundResource(R.drawable.img_service_release_sel);
					tv_cleanup.setTextColor(getResources().getColor(R.color.white));
				}
				isCleanSel=!isCleanSel;
			    break;
			
			case R.id.tv_cooking :
				if(isCookSel) {
					tv_cooking.setBackgroundResource(R.drawable.img_service_release_nor);
					tv_cooking.setTextColor(getResources().getColor(R.color.font_color_999999));
				}else {
					tv_cooking.setBackgroundResource(R.drawable.img_service_release_sel);
					tv_cooking.setTextColor(getResources().getColor(R.color.white));
				}
				isCookSel=!isCookSel;
			    break;
			
			case R.id.tv_look_after:
				if(isLookAfterSel) {
					tv_look_after.setBackgroundResource(R.drawable.img_service_release_nor);
					tv_look_after.setTextColor(getResources().getColor(R.color.font_color_999999));
				}else {
					tv_look_after.setBackgroundResource(R.drawable.img_service_release_sel);
					tv_look_after.setTextColor(getResources().getColor(R.color.white));
				}
				isLookAfterSel=!isLookAfterSel;
			    break;
			
			case R.id.tv_chat:
			    if(isChatSel) {
					tv_chat.setBackgroundResource(R.drawable.img_service_release_nor);
					tv_chat.setTextColor(getResources().getColor(R.color.font_color_999999));
				}else {
					tv_chat.setBackgroundResource(R.drawable.img_service_release_sel);
					tv_chat.setTextColor(getResources().getColor(R.color.white));
				}
				isChatSel=!isChatSel;
			    break;

			case R.id.tv_accompany:
				if(isAccompSel) {
					tv_accompany.setBackgroundResource(R.drawable.img_service_release_nor);
					tv_accompany.setTextColor(getResources().getColor(R.color.font_color_999999));
				}else {
					tv_accompany.setBackgroundResource(R.drawable.img_service_release_sel);
					tv_accompany.setTextColor(getResources().getColor(R.color.white));
				}
				isAccompSel=!isAccompSel;
			    break;

			case R.id.tv_other:
			    if(isOtherSel) {
					tv_other.setBackgroundResource(R.drawable.img_service_release_nor);
					tv_other.setTextColor(getResources().getColor(R.color.font_color_999999));
				}else {
					tv_other.setBackgroundResource(R.drawable.img_service_release_sel);
					tv_other.setTextColor(getResources().getColor(R.color.white));
				}
				isOtherSel=!isOtherSel;
			    break;
			case R.id.btn_submit:
				completedVolunteer();
			    break;
		}
	}



	private void completedVolunteer() {
		if(!NetUtil.isNetworkAvailable(this)) {
			BaseApplication.getInstances().toast(VolunteerCreateActivity.this,"网络连接不可用");
			return;
		}
		if(mTextUrgency.getText().toString().trim().length()==0) {
		    BaseApplication.getInstances().toast(VolunteerCreateActivity.this,"请选择紧急程度");
			return;
		}
		if(mTimeValue.getText().toString().trim().length()==0) {
			BaseApplication.getInstances().toast(VolunteerCreateActivity.this,"请选择时间");
			return;
		}
		if(mLocationValue.getText().toString().trim().length()==0) {
		    BaseApplication.getInstances().toast(VolunteerCreateActivity.this,"请选择地点");
			return;
		}
		if( mTypeValue.getText().toString().trim().length()==0) {
		    BaseApplication.getInstances().toast(VolunteerCreateActivity.this,"请选择求助对象");
			return;
		}

		if(!isWashSel&&!isCookSel&&!isCleanSel&&!isAccompSel&&!isChatSel&&!isLookAfterSel&&!isOtherSel) {
		    BaseApplication.getInstances().toast(VolunteerCreateActivity.this,"请选择求助事项");
			return;
		}

        HashMap<String, String> params = new HashMap<>();
        params.put("member_id",QuanjiakanSetting.getInstance().getUserId()+"");
        params.put("level",mTextUrgency.getText().toString().trim().equals("普通")?"1":"2");
        params.put("begintime",mTimeValue.getText().toString().trim());
        params.put("title",selectThing());
        params.put("info",et_addtion.getText().toString().trim()==null?"":et_addtion.getText().toString().trim());
        params.put("duration","0");
        params.put("lat",lantitude+"");
        params.put("lng",longitude+"");
        params.put("address",address);
        params.put("target",selectType(mTypeValue.getText().toString().trim()));
        MyHandler.putTask(VolunteerCreateActivity.this,new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                HttpResponseResult result = new HttpResponseResult(val);
                if(result.isResultOk()) {
                    BaseApplication.getInstances().toast(VolunteerCreateActivity.this,"发布成功");
                    finish();
                }else {
                    BaseApplication.getInstances().toast(VolunteerCreateActivity.this,result.getMessage());
                }

            }
        }, HttpUrls.publishVoluteer(),params,Task.TYPE_POST_DATA_PARAMS,QuanjiakanDialog.getInstance().getDialog(this)));


    }

	private void showSelectHelpType() {
        final Dialog dialog =new Dialog(this,R.style.LoadingDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_select_help_type, null);
        view.findViewById(R.id.tv_oldpeople).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                mTypeValue.setText("老人");
            }
        });
        view.findViewById(R.id.tv_children).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                mTypeValue.setText("儿童");
            }
        });
        view.findViewById(R.id.tv_young_people).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                mTypeValue.setText("少年");
            }
        });
        view.findViewById(R.id.tv_disabled_people).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                mTypeValue.setText("残障人士");
            }
        });
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.gravity= Gravity.BOTTOM;
        lp.width = this.getResources().getDisplayMetrics().widthPixels*3/4;
        lp.height = lp.WRAP_CONTENT;

        dialog.setContentView(view,lp);

        dialog.show();


    }

    //选择时间
    private void showSelectTimeDialog() {
		selectVolunteerTimeDialog = new SelectVolunteerTimeDialog(this);
		selectVolunteerTimeDialog.setOnTimeChangeCListener(new SelectVolunteerTimeDialog.OnTimeChangeCListener() {
			@Override
			public void onClick(String year, String month, String day, String hour, String minute) {
				if(year!=null&&month!=null&&day!=null&&hour!=null&&minute!=null) {
				    mTimeValue.setText(year+"-"+month+"-"+day+"-"+hour+"-"+minute);
				}
			}
		});
		selectVolunteerTimeDialog.show();
	}

    //选择是否紧急
	private void showSelectUrgencyDialog() {
		final Dialog dialog = new Dialog(this,R.style.LoadingDialog);
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_select_urgencydegree, null);
		view.findViewById(R.id.tv_urgency).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				mTextUrgency.setText("紧急");
			}
		});

		view.findViewById(R.id.tv_common).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				mTextUrgency.setText("普通");
			}
		});

		view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});

		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.dimAmount = 0.5f;
		lp.gravity= Gravity.BOTTOM;
		lp.width = this.getResources().getDisplayMetrics().widthPixels*3/4;
		lp.height = lp.WRAP_CONTENT;

		dialog.setContentView(view,lp);
		dialog.show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode){
			case VOLUNTEER_REQUEST_MAP:
			    if(resultCode==RESULT_OK) {
					address = data.getStringExtra(VOLUNTEER_ADDR);
					lantitude = data.getDoubleExtra(VOLUNTEER_LAT,-1);
					longitude = data.getDoubleExtra(VOLUNTEER_LON,-1);
					if(address!=null && address.length()>0 && lantitude!=null && lantitude!=-1  && longitude!=null && longitude!=-1){
						mLocationValue.setText(address);
					}else{
						BaseApplication.getInstances().toast(VolunteerCreateActivity.this,"定位数据异常!");
					}
				}
			    break;
		}
	}

	/*//使屏幕变暗
	private void setScreenBgDarken() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.5f;
		lp.dimAmount = 0.5f;
		getWindow().setAttributes(lp);
	}

	//使屏幕变亮
	private void setScreenBgLight() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 1.0f;
		lp.dimAmount = 1.0f;
		getWindow().setAttributes(lp);
	}*/

    //拼接事务
	public String selectThing(){
        StringBuilder thing = new StringBuilder();
        if(isWashSel) {
            thing.append(tv_wash.getText().toString()).append(",");
        }
        if(isCookSel) {
            thing.append(tv_cooking.getText().toString()).append(",");
        }
        if(isCleanSel) {
            thing.append(tv_cleanup.getText().toString()).append(",");
        }
        if(isAccompSel) {
            thing.append(tv_accompany.getText().toString()).append(",");
        }
        if(isChatSel) {
            thing.append(tv_chat.getText().toString()).append(",");
        }
        if(isLookAfterSel) {
            thing.append(tv_look_after.getText().toString()).append(",");
        }
        if(isOtherSel) {
            thing.append(tv_other.getText().toString()).append(",");
        }
        String selectedThing = thing.toString();
        String substring = selectedThing.substring(0, selectedThing.length() - 1);
        return substring;
    }

    //选择帮助对象
    public String selectType(String type){
        if(type.equals("老人")) {
            return "1";
        }else if(type.equals("儿童")) {
            return "2";
        }else if(type.equals("少年")) {
            return "3";
        }else if(type.equals("残障人士")) {
            return "4";
        }else {
            return "4";
        }

    }


}
