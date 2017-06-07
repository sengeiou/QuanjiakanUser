package com.quanjiakan.main.wxapi;

import com.androidquanjiakan.activity.index.out.ConsultantActivity_GoOut_Order;
import com.androidquanjiakan.activity.index.phonedocter.ConsultantActivity_PhoneDoctor;
import com.androidquanjiakan.activity.index.housekeeper.HouseKeeperOrderActivity;
import com.androidquanjiakan.activity.setting.ebean.EBeanChargeActivity;
import com.androidquanjiakan.activity.setting.order.HouseKeeperOrderHistoryListActivity;
import com.androidquanjiakan.activity.PaymentResultActivity;
import com.androidquanjiakan.activity.index.doctor.DoctorDetailActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.CommonRequestCode;
import com.quanjiakan.main.R;
import com.androidquanjiakan.util.LogUtil;
import com.quanjiakanuser.util.WeixinPayHandler;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wxapi.entity.HouseKeeperParamEntity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "LOGUTIL WXPayEntry";
    private IWXAPI api;

	private TextView tv_title;


	public static final int FROM_DOCTOR_PAY = 1;
	public static final int FROM_HOUSE_KEEPER_PAY = 2;
	public static final int FROM_HOUSE_KEEPER_ORDER_PAY = 3;
	public static final int FROM_DOCTOR_PHONE_PAY = 4;
	public static final int FROM_DOCTOR_OUT_GO_PAY = 5;
	public static final int FROM_EBEAN_PAY = 6;


	public static int FROM_TYPE = -1;
	public static Object PARAMS;

	private Intent intent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_payresult);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("WXPayEntry");
        api = WXAPIFactory.createWXAPI(this, WeixinPayHandler.app_id);
        api.handleIntent(getIntent(), this);
    }

	public static void initPARAMS(){
		FROM_TYPE = -1;
		PARAMS = null;
	}

	public static void setFromType(int fromType){
		FROM_TYPE = fromType;
//		switch (FROM_TYPE){
//			case FROM_DOCTOR_PAY:
//				LogUtil.e("set Type: FROM_DOCTOR_PAY");
//				break;
//			case FROM_HOUSE_KEEPER_PAY:
//				LogUtil.e("set Type: FROM_HOUSE_KEEPER_PAY");
//				break;
//			case FROM_HOUSE_KEEPER_ORDER_PAY:
//				LogUtil.e("set Type: FROM_HOUSE_KEEPER_ORDER_PAY");
//				break;
//			case FROM_DOCTOR_PHONE_PAY:
//				LogUtil.e("set Type: FROM_DOCTOR_PHONE_PAY");
//				break;
//			default:
//				LogUtil.e("set Type: default");
//				break;
//		}
	}

	public static  void setParams(Object param){
		PARAMS = param;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
//		BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(),"req");
	}

	@Override
	public void onResp(BaseResp resp) {
		switch (FROM_TYPE){
			case FROM_DOCTOR_PAY:
				LogUtil.e("onResp Type: FROM_DOCTOR_PAY");
				break;
			case FROM_HOUSE_KEEPER_PAY:
				LogUtil.e("onResp Type: FROM_HOUSE_KEEPER_PAY");
				break;
			case FROM_HOUSE_KEEPER_ORDER_PAY:
				LogUtil.e("onResp Type: FROM_HOUSE_KEEPER_ORDER_PAY");
				break;
			case FROM_DOCTOR_PHONE_PAY:
				LogUtil.e("onResp Type: FROM_DOCTOR_PHONE_PAY");
				break;
			case FROM_EBEAN_PAY:
				LogUtil.e("onResp Type: FROM_DOCTOR_PHONE_PAY");
				break;
			default:
				LogUtil.e("onResp Type: default");
				break;
		}
		LogUtil.e( "onPayFinish, errCode = " + resp.errCode+"errorString:"+resp.errStr);
		if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
//			BaseApplication.getInstances().toast("成功   WXPayEntryActivity IWXAPIEventHandler 回调");
			switch (FROM_TYPE){
				case FROM_DOCTOR_PAY:
					/**
					 * 名医
					 */
					DoctorDetailActivity.wxPayStatus = DoctorDetailActivity.WX_PAY_OK;
					initPARAMS();
					finish();
					break;
				case FROM_HOUSE_KEEPER_PAY:
					if(PARAMS!=null){
						intent = new Intent(this,PaymentResultActivity.class);
						HouseKeeperOrderActivity.orderDetail.addProperty("status","2");//支付成功
						intent.putExtra(PaymentResultActivity.PARAMS_DATA, HouseKeeperOrderActivity.orderDetail.toString());
						intent.putExtra("type", ((HouseKeeperParamEntity)PARAMS).getType());
						intent.putExtra("flag", 1);
						intent.putExtra("total_fee", ((HouseKeeperParamEntity)PARAMS).getTotal_fee());
						intent.putExtra("orderid", ((HouseKeeperParamEntity)PARAMS).getOrderid());
						intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						startActivityForResult(intent, CommonRequestCode.REQUEST_PAY);
					}else{
						BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(),"传入参数异常!");
						initPARAMS();
						finish();
					}
					break;
				case FROM_HOUSE_KEEPER_ORDER_PAY:
					HouseKeeperOrderHistoryListActivity.wxPayStatus = HouseKeeperOrderHistoryListActivity.WX_PAY_OK;
					if(PARAMS!=null){
						intent = new Intent(this,PaymentResultActivity.class);
						intent.putExtra("type", ((HouseKeeperParamEntity)PARAMS).getType());
						intent.putExtra("flag", 1);
						intent.putExtra("total_fee", ((HouseKeeperParamEntity)PARAMS).getTotal_fee());
						intent.putExtra("orderid", ((HouseKeeperParamEntity)PARAMS).getOrderid());
						intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						startActivityForResult(intent, CommonRequestCode.REQUEST_PAY);
					}else{
						BaseApplication.getInstances().toast(BaseApplication.getInstances().getCurrentActivity(),"传入参数异常!");
						initPARAMS();
						finish();
					}
					break;
				case FROM_DOCTOR_PHONE_PAY:
					ConsultantActivity_PhoneDoctor.wxPayStatus = ConsultantActivity_PhoneDoctor.WX_PAY_OK;
					initPARAMS();
					finish();
					break;
				case FROM_DOCTOR_OUT_GO_PAY:
					ConsultantActivity_GoOut_Order.wxPayStatus = ConsultantActivity_GoOut_Order.WX_PAY_OK;
					initPARAMS();
					finish();
					break;
				case FROM_EBEAN_PAY:
					EBeanChargeActivity.wxPayStatus = ConsultantActivity_GoOut_Order.WX_PAY_OK;
					initPARAMS();
					finish();
					break;
				default:
					try {
						throw new Exception("WXPayEntryActivity unknow from source!");
					} catch (Exception e) {
						e.printStackTrace();
						LogUtil.e(""+e.getMessage());
					}
					initPARAMS();
					finish();
					break;
			}
			initPARAMS();
		}else if (resp.errCode == BaseResp.ErrCode.ERR_COMM) {
//			BaseApplication.getInstances().toast("支付失败      WXPayEntryActivity IWXAPIEventHandler 回调");
			switch (FROM_TYPE){
				case FROM_DOCTOR_PAY:
					LogUtil.e("ERR_COMM FROM_DOCTOR_PAY WXPayEntity");
					DoctorDetailActivity.wxPayStatus = DoctorDetailActivity.WX_PAY_COMM;
					break;
				case FROM_HOUSE_KEEPER_PAY:
					LogUtil.e("ERR_COMM FROM_HOUSE_KEEPER_PAY WXPayEntity");
					intent = new Intent(this,PaymentResultActivity.class);
					HouseKeeperOrderActivity.orderDetail.addProperty("status","1");//支付失败
					intent.putExtra(PaymentResultActivity.PARAMS_DATA, HouseKeeperOrderActivity.orderDetail.toString());
					intent.putExtra("type", ((HouseKeeperParamEntity)PARAMS).getType());
					intent.putExtra("flag", -1);
					intent.putExtra("total_fee", ((HouseKeeperParamEntity)PARAMS).getTotal_fee());
					intent.putExtra("orderid", ((HouseKeeperParamEntity)PARAMS).getOrderid());
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivityForResult(intent, CommonRequestCode.REQUEST_PAY);
					break;
				case FROM_HOUSE_KEEPER_ORDER_PAY:
					LogUtil.e("ERR_COMM FROM_HOUSE_KEEPER_ORDER_PAY WXPayEntity");
					HouseKeeperOrderHistoryListActivity.wxPayStatus = HouseKeeperOrderHistoryListActivity.WX_PAY_COMM;
					break;
				default:
					try {
						throw new Exception("WXPayEntryActivity unknow from source!");
					} catch (Exception e) {
						e.printStackTrace();
						LogUtil.e(""+e.getMessage());
					}
					break;
			}
			initPARAMS();
			finish();
		}else if(resp.errCode == BaseResp.ErrCode.ERR_USER_CANCEL){
			switch (FROM_TYPE){
				case FROM_DOCTOR_PAY:
					DoctorDetailActivity.wxPayStatus = DoctorDetailActivity.WX_PAY_CANCEL;
					break;
				case FROM_HOUSE_KEEPER_PAY:
					intent = new Intent(this,PaymentResultActivity.class);
					HouseKeeperOrderActivity.orderDetail.addProperty("status","1");//支付失败
					intent.putExtra(PaymentResultActivity.PARAMS_DATA, HouseKeeperOrderActivity.orderDetail.toString());
					intent.putExtra("type", ((HouseKeeperParamEntity)PARAMS).getType());
					intent.putExtra("flag", -1);
					intent.putExtra("total_fee", ((HouseKeeperParamEntity)PARAMS).getTotal_fee());
					intent.putExtra("orderid", ((HouseKeeperParamEntity)PARAMS).getOrderid());
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivityForResult(intent, CommonRequestCode.REQUEST_PAY);
					break;
				case FROM_HOUSE_KEEPER_ORDER_PAY:
					HouseKeeperOrderHistoryListActivity.wxPayStatus = HouseKeeperOrderHistoryListActivity.WX_PAY_CANCEL;
					break;
				default:
					try {
						throw new Exception("WXPayEntryActivity unknow from source!");
					} catch (Exception e) {
						e.printStackTrace();
						LogUtil.e(""+e.getMessage());
					}
					break;
			}
			initPARAMS();
			setResult(RESULT_OK);
			finish();
		}else{
			switch (FROM_TYPE){
				case FROM_DOCTOR_PAY:
					DoctorDetailActivity.wxPayStatus = DoctorDetailActivity.WX_PAY_OTHER;
					LogUtil.e("ERR_USER_CANCEL FROM_DOCTOR_PAY WXPayEntity");
					break;
				case FROM_HOUSE_KEEPER_PAY:
					LogUtil.e("ERR_USER_CANCEL FROM_HOUSE_KEEPER_PAY WXPayEntity");
					intent = new Intent(this,PaymentResultActivity.class);
					HouseKeeperOrderActivity.orderDetail.addProperty("status","1");//支付失败
					intent.putExtra(PaymentResultActivity.PARAMS_DATA, HouseKeeperOrderActivity.orderDetail.toString());
					intent.putExtra("type", ((HouseKeeperParamEntity)PARAMS).getType());
					intent.putExtra("flag", -1);
					intent.putExtra("total_fee", ((HouseKeeperParamEntity)PARAMS).getTotal_fee());
					intent.putExtra("orderid", ((HouseKeeperParamEntity)PARAMS).getOrderid());
					intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					startActivityForResult(intent, CommonRequestCode.REQUEST_PAY);
					break;
				case FROM_HOUSE_KEEPER_ORDER_PAY:
					HouseKeeperOrderHistoryListActivity.wxPayStatus = HouseKeeperOrderHistoryListActivity.WX_PAY_OTHER;
					LogUtil.e("ERR_USER_CANCEL FROM_HOUSE_KEEPER_ORDER_PAY WXPayEntity");
					break;
				default:
					try {
						throw new Exception("WXPayEntryActivity unknow from source!");
					} catch (Exception e) {
						e.printStackTrace();
						LogUtil.e(""+e.getMessage());
					}
					break;
			}
			initPARAMS();
			setResult(RESULT_OK);
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode){
//			case FROM_DOCTOR_PAY:
//				finish();
//				break;
//			case FROM_HOUSE_KEEPER_PAY:
//				finish();
//				break;
			default:
				finish();
				break;
		}
	}
}