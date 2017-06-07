package com.quanjiakanuser.http;

import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.QuanjiakanSetting;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class HttpUrls {
	/**
	 * client
	 * 1: 用户端    2: 医生端   3:义工端    4: 专车端
	 *
	 * devicetype 指定设备类型
	 * Android 0  IOS 1
	 *
	 * platform TODO 暂时未说明该参数是啥作用
	 *
	 * For Test
	 */
//	public static final String base_url =
//			"http://app.quanjiakan.com/familycare/api?"+"devicetype=0&platform=2&client=1";//联通域名  http://app.quanjiakan.com/quanjiakan/
//	"http://112.93.116.191:8080/familycare/api?"+"&devicetype=0&platform=2&client=1";//联通
//	"http://192.168.0.125:8080/familycare/api?"+"&devicetype=0&platform=2&client=1";//测试服
//		"http://192.168.0.125:8080/familycare/api?"+"&devicetype=0&platform=2&client=1";//测试服
//	"http://192.168.0.138:8080/familycare/api?"+"&devicetype=0&platform=2&client=1";//测试服
//	"http://192.168.0.107:8080/familycare/api?"+"&devicetype=0&platform=2&client=1";//测试服
//	"http://192.168.0.100:8080/familycare/api?"+"&devicetype=0&platform=2&client=1";//测试服

	public static final String url = "http://pay.quanjiakan.com:7080/familycore-pay/core/api_get?"+"devicetype=0&platform=2&client=1";
//	public static final String url = "http://192.168.0.140:8080/familycore-pay/core/api_get?"+"&devicetype=0&platform=2";
	public static final String urlPay = "http://pay.quanjiakan.com:7080";
	/**
	 * release
	 */
	public static final String dnaBaseUrl = "http://app.quanjiakan.com";
	public static final String base_url = "http://app.quanjiakan.com/familycare/api?"+"devicetype=0&platform=2&client=1";
	public static final String baseUrlGet = "http://app.quanjiakan.com/familycare/v2/health/api_get?";
	public static final String baseUrlPost = "http://app.quanjiakan.com/familycare/v2/health/api?";

	public static final String baseUrlGet1 = "http://app.quanjiakan.com/familycare/health/api_get?";
	public static final String baseUrlPost1 = "http://app.quanjiakan.com/familycare/health/api?";


	public static final String dnaBaseUrls = "https://app.quanjiakan.com";
	public static final String base_urls = "https://app.quanjiakan.com/familycare/api?"+"devicetype=0&platform=2&client=1";
	public static final String baseUrlGets = "https://app.quanjiakan.com/familycare/v2/health/api_get?";
	public static final String baseUrlPosts = "https://app.quanjiakan.com/familycare/v2/health/api?";
	public static final String device_url = "http://app.quanjiakan.com/device/service?";

	/**
	 * release debug
	 */
//	public static final String dnaBaseUrl = "http://app.quanjiakan.com:8080";
//	public static final String base_url = "http://app.quanjiakan.com:8080/familycare/api?"+"devicetype=0&platform=2&client=1";
//	public static final String baseUrlGet = "http://app.quanjiakan.com:8080/familycare/v2/health/api_get?";
//	public static final String baseUrlPost = "http://app.quanjiakan.com:8080/familycare/v2/health/api?";
	/**
	 * release debug DB
	 */
//	public static final String dnaBaseUrl = "http://120.76.41.223";
//	public static final String base_url = "http://120.76.41.223/familycare/api?"+"devicetype=0&platform=2&client=1";
//	public static final String baseUrlGet = "http://120.76.41.223/familycare/v2/health/api_get?";
//	public static final String baseUrlPost = "http://120.76.41.223/familycare/v2/health/api?";
	/**
	 * http://app.quanjiakan.com:8081
	 * debug
	 */
//	public static final String dnaBaseUrl = "http://192.168.0.122:8080";
//	public static final String base_url = "http://192.168.0.122:8080/familycare/api?"+"devicetype=0&platform=2&client=1";
//	public static final String baseUrlGet = "http://192.168.0.122:8080/familycare/v2/health/api_get?";
//	public static final String baseUrlPost = "http://192.168.0.122:8080/familycare/v2/health/api?";
	/**
	 * @return
	 */
	public static String getSignin(){
		return base_url + "&token="+ 0 + "&user_id="+ 0 + "&m=member&a=signin";
	}

	public static String getHttpsSignin(){
		return base_urls + "&token="+ 0 + "&user_id="+ 0 + "&m=member&a=signin";
	}
	
	public static String getDoctorSignin(){
		return   base_url + "&m=doctor&a=signin";
	}
	
	/**
	 * ע
	 * @return
	 */
	/**
	 *
	 * @param anchorId  主播ID
	 * @param state 是否正在直播  0 取消 1 关注
     * @return
     */
	public static String postConcern(String anchorId,String state){
		return baseUrlGet+"action=updateFollow&code=liveProgram"+
				"&action=updateFollow&code=liveProgram"+
				"&devicetype=0&platform=2&client=1"+
				"&anchorId="+anchorId+"&userId="+BaseApplication.getInstances().getUser_id()+"&state="+state+"&type=0";
	}


	public static String getConcernList(){
		return baseUrlGet+"action=selectFollowUser&code=liveProgram" +
				"&devicetype=0&platform=2&client=1"+
				"&userId="+
				BaseApplication.getInstances().getUser_id()+"&type=0&devicetype=0&platform=2";
	}
	/**
	 *
	 * m=member&a=signup&mobile=lp&password=7CCFA0856955C1499924FA7665B74EF9&client=1
	 *
	 * 注册会员
	 * @return
	 */
	public static String getSignup(){
		return   base_url + "&m=member&a=signup";
	}	
	
	/**
	 * 注册设备
	 * @return
	 */
	public static String registerDevice(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=member&a=registerdevice";
	}

	public static String getDNA(){
		return   dnaBaseUrl+"/familycare/archives/findbymemberid?" +
				"devicetype=0&platform=2&client=1"+
				"&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=DNA&a=findbymemberid";
	}



	public static String getDNA(String imei){
		return
				dnaBaseUrl+//TODO formal
//				"http://192.168.0.119:8080"+//TODO informal  for Test

				"/familycare/archives/findByIMEI?token="+
				BaseApplication.getInstances().getSessionID()+"&IMEI=" + imei +
				"&devicetype=0&platform=2&client=1"+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId();
	}
	 
	/**
	 * 上传文件接口
	 * storage=13:	//春雨图片
	 storage=14:	//春雨声音
	 storage=15:	//病例图片
	 storage=16:	//设备图片
	 &storage=19    //寻人启事
	 &storage=20    //用户健康档案
	  storage=2 头像
	 * @return
	 */

	public static String postFile(){
		return   "http://picture.quanjiakan.com:9080/familycare-binary/upload?"+"devicetype=0&platform=2&client=1" +
				"&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() +
				"&m=file&a=postfile&sign="+BaseApplication.getInstances().getSessionID();
//		return "http://1.shangshousoft.applinzi.com/api?&m=file&a=postfile";
	}


	public static String addUserHealthDocument(){
		//http://app.quanjiakan.com
		return   dnaBaseUrl +
				"/familycare/health2/api?code=archives&action=add_archives";
//				"/familycare/api?platform=2&m=watch&a=addMedicalRecord" +
//				"&devicetype=0&platform=2&client=1"+
//				"&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId();
//		return "http://1.shangshousoft.applinzi.com/api?&m=file&a=postfile";
	}

	public static String addWatchHealthDocument(){
		return   dnaBaseUrl +"/familycare/api?devicetype=0&platform=2&client=1&m=watch&a=addMedicalRecord" +
				"&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId();
//		return "http://1.shangshousoft.applinzi.com/api?&m=file&a=postfile";
	}

	public static String updataUserInfo(String nickName,String picPath){
		//http://app.quanjiakan.com
		String nick="";
		String path = "";
		try {
			nick = "&nickname="+URLEncoder.encode(nickName,"utf-8")+"";
			path = "&picture="+URLEncoder.encode(picPath,"utf-8")+"";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			nick= "";
			path = "";
		}
		return   dnaBaseUrl +"/familycare/api?m=member&a=addmemberinfo" +
				"&devicetype=0&platform=2&client=1"+
				"&member_id="+ BaseApplication.getInstances().getUser_id()+
				nick+
				path+
				"&token="+ BaseApplication.getInstances().getSessionID() +
				"&user_id="+QuanjiakanSetting.getInstance().getUserId();
//		return "http://1.shangshousoft.applinzi.com/api?&m=file&a=postfile";
	}

	public static String updataUserNickName(String nickName){
		String nick="";
		try {
			nick = "&nickname="+URLEncoder.encode(nickName,"utf-8")+"";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			nick= "";
		}
		return   dnaBaseUrl +"/familycare/api?m=member&a=updatemembernickname" +
				"&devicetype=0&platform=2&client=1"+
				"&member_id="+ BaseApplication.getInstances().getUser_id()+
				nick+
				"&token="+ BaseApplication.getInstances().getSessionID() +
				"&user_id="+QuanjiakanSetting.getInstance().getUserId();
//		return "http://1.shangshousoft.applinzi.com/api?&m=file&a=postfile";
	}

	public static String getNameAndHeadIcon(){
		return   dnaBaseUrl+"/familycare/api?m=member&a=getmemberinfo" +
				"&devicetype=0&platform=2&client=1"+
				"&token="+ BaseApplication.getInstances().getSessionID() +
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() +
				"&member_id="+QuanjiakanSetting.getInstance().getUserId();
	}

	public static String getUserLevelInfo(){
		return   dnaBaseUrl+"/familycare/v2/health/api_get?code=livepresentgrade&action=getExperienceAndGrade" +
				"&devicetype=0&platform=2&client=1"+
				"&token="+ BaseApplication.getInstances().getSessionID() +
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() +
				"&memberId="+QuanjiakanSetting.getInstance().getUserId();
	}

	/**
	 user_id  用户id
	 did  设备id
	 type 类型  0 = 解绑  1 = 绑定
	 * @return
     */
	public static String bindedWatch(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=group&a=updategroupmember";
	}
	
	/**
	 * 追问问题
	 * @return
	 */
	public static String persistProblem(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=chunyu&a=persistproblem&type=jg";
	}
	
	/**
	 * 获取会员列表
	 * @return
	 */
	public static String getMemberList(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=member&a=getmemberlist";
	}
	
	/**
	 * 获取医生列表
	 * @return
	 */
	public static String getDoctorList(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() +
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=doctor&a=getdoctorlist";
	}
	
	/**
	 * 获取医生详情
	 * @return
	 */
	public static String getDoctorDetail(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=doctor&a=getdoctordetail";
	}

	/**
	 * 获取医生服务
	 * @return
	 */
//	public static String payForDoctorService(){
//		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=doctor&a=buydoctorservice";
//	}

	/**
	 * 获取医生服务(修改)
	 * @return
	 */
	public static String payForDoctorService(){
		return   url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&code=pay&action=phone_produceorder";
	}

	/**
	 * 获取科室分类
	 * @return
	 */
	public static String getClinicClassifyList(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=doctor&a=getcliniclist";
	}

	/**
	 * 评价医生
	 * @return
	 */
	public static String doctorEvaluate(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=doctor&a=evaluate&type=jg";
	}

	/**
	 * 已经支付的医生
	 * @return
	 */
	public static String getPaidDoctorList_origin(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=appuser&a=getmydoctorservice";
	}

	/**
	 * 已经支付的医生
	 * @return
	 */
	public static String getPaidDoctorList_new(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=doctor&a=getreferringphysician";
	}

	/**
	 * 更新订单
	 * @return
	 */
//	public static String updateDocterServicePayment(){
//		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=doctor&a=updatedoctorservicepayment";
//	}

	/**
	 * 发布电话订单（修改）
	 * @return
	 */
	public static String updateDocterServicePayment(){
		return   url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&code=pay&action=phone_publishorder";
	}
	
	/**
	 * 定向咨询时发布问题
	 * @return
	 */
	public static String postNewProblem(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=consultant&a=createproblem&type=jg";
	}
	
	/**
	 * 不定向咨询时，发布问题
	 * @return
	 */
	public static String postNewProblemToChunyu(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=consultant&a=createproblem&type=cy";
	}
	
	/**获取药品列表
	 * @param page
	 * @return
	 */
	public static String getMedcineList(int page){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=medcine&a=getmedcinelist&page="+page;
	}

	/**获取药品订单列表
	 * @param page
	 * @return
	 */
	public static String getMedcineOrderList(int page){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=medcine&a=getorderlist&user_id="+ BaseApplication.getInstances().getUser_id();
	}
	
	/**获取药品详情
	 * @param id
	 * @return
	 */
	public static String getMedcineDetail(String id){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=medcine&a=getmedcinedetail&id="+id;
	}
	
	/**
	 * 获取家政服务列表
	 * @param page
	 * @return
	 */
	public static String getHouseKeeperList(int page){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=housekeeper&a=appgethousekeeperlist&page="+page;
	}

	/**
	 * 获取义工站列表
	 * @return
	 */
	public static String getStationList(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=volunt&a=getstationlist";
	}

	public static String getHousekeeperTypeList(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=housekeepertype&a=gethousekeepertypedroplist";
	}


	public static String getHouseKeeperCompanyList(String province,String city,String area){
		String provinceS = "";
		String cityS = "";
		String areaS = "";

		if(province!=null){
			try {
				provinceS = "&province="+URLEncoder.encode(province,"utf-8");
//				provinceS = "&province="+province;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			provinceS = "";
		}

		if(city!=null){
			try {
				cityS = "&city="+URLEncoder.encode(city,"utf-8");
//				cityS = "&city="+city;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			cityS = "";
		}

		if(area!=null){
			try {
				areaS = "&area="+URLEncoder.encode(area,"utf-8");
//				areaS = "&area="+area;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			areaS = "";
		}
		/**
		 通过地区查询家政公司
		 http://192.168.0.117:8080/quanjiakan/api?m=housekeeper&a=getaerahousecompanylist&province=广东&city=广州&area=天河区
		 */
//		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=housekeeper&a=getaerahousecompanylist"+provinceS+cityS+areaS;//old   有分页
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=housekeeper&a=getapphousecompanydroplist"+provinceS+cityS+areaS;//  无分页
	}

	public static String getHouseKeeperList(int page,String province,String city,String area,String serviceType,String companyName){
		/**
		 * http://192.168.0.115:8080/quanjiakan/api?devicetype=0&platform=2&token=81e87748712c95a4d11d8c56db840d47&user_id=10678&m=housekeeper&a=gethousekeeperlist&page=1&housekeeper_type=1&serviceprovince=%E5%B9%BF%E4%B8%9C&servicecity=%E5%B9%BF%E5%B7%9E
		 */
		String provinceS = "";
		String cityS = "";
		String areaS = "";
		String type = "";
		String companyS = "";

		if(province!=null){
			try {
				provinceS = "&serviceprovince="+URLEncoder.encode(province,"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else{
			provinceS = "";
		}
		if(city!=null){
			try {
				cityS = "&servicecity="+URLEncoder.encode(city,"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else{
			cityS = "";
		}
		if(area!=null){
			try {
				areaS = "&servicearea="+URLEncoder.encode(area,"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else{
			areaS = "";
		}
		if(companyName!=null && !"公司".equals(companyName)){
			try {
				companyS = /*"&servicearea="+URLEncoder.encode(area,"utf-8")*/"&company_id="+companyName;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			companyS = "";
		}

		if(serviceType!=null && "保姆".equals(serviceType)){
			type = "&housekeeper_type=1";
		}else if(serviceType!=null && "保洁".equals(serviceType)){
			type = "&housekeeper_type=2";
		}else if(serviceType!=null && "全部".equals(serviceType)){
			type = "";
		}
		//new type value get from net
		else if(serviceType!=null && serviceType.length()>0){
			type = "&housekeeper_type="+serviceType;
		}
		//************************
		else{
			type = "";
		}
		return base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=housekeeper&a=gethousekeeperlist&page="+page+type+cityS+provinceS+areaS+companyS;
	}

	public static String updatepassword(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=member&a=updatepassword";
	}

	public static String carouselPictureList(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=other&a=getcarouselimages";
	}

	public static String outGoDoctor(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=graborder&a=getpatientprice";//getpatientprice  getpatienttype
	}

	public static String grabNumber(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=graborder&a=getgrabservicecount";
	}

//	public static String outGoCreateOrder(){
//		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=graborder&a=produceorder";
//	}

	/**
	 * 生成就近寻医订单
	 * @return
     */
	public static String outGoCreateOrder(){
		return   url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&code=pay&action=nearby_produceorder";
	}

//	public static String outGoPublishOrder(){
//		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=graborder&a=publishorder";
//	}

	/**
	 * 发布就近寻医订单
	 * @return
     */
	public static String outGoPublishOrder(){
		return   url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&code=pay&action=nearby_publishorder";
	}
	//m=graborder&a=getuserpublishorderlist
	public static String getPulishOrderList(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=graborder&a=getuserpublishorderlist";
	}

	public static String getDoctorOrderList(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=doctor&a=getpaidrecordbyuser";
	}

	/**
	 * 免费问诊，追问问题
	 *
     */
	public static String getFreeInquiryAppand(){
		/**
		 * POST
		 *
		 * 参数:
		 * content jsonArray格式
		 * fromtoken自己的userid
		 * totoken医生的id
		 * problem_id第一次发送的问题的ID
		 *
		 */
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=consultant&a=persist&type=cy";
	}

	public static String getGetChunyuProblemDetail(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=consultant&a=queryproblem&type=cy";
	}

	public static String getGetChunyuProblemReply(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=consultant&a=queryproblemanswer&type=cy";
	}


	/**
	 * 根据传递参数的不同获取相应的数据
	 * @return
     */
	public static String getFunctionInfo(){
		/**
		 * alias	String	是

		 complaint_phone : 投诉电话
		 address:地址
		 android_version:获取android版本
		 ios_version:iOS版本

		 */
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=jugui&a=getfunction";
	}

	/**
	 * 获取投诉点话  GET
	 * @return
     */
	public static String getComplainPhone(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=jugui&a=getfunction&alias=complaint_phone";
	}

	public static String getAddress(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=jugui&a=getfunction&alias=address";
	}


	public static String getHandleFallDown(String fallDownId,String memberId){
		//
//		return   dnaBaseUrl + "/familycare/cpt_get?m=health&a=familyHandling&name=vatis&fallDownId="+fallDownId+"&memberId="+memberId;
		//For test
		return   dnaBaseUrl + "/familycare/cpt_get?m=health&a=familyHandling&name=vatis&fallDownId="+fallDownId+"&memberId="+memberId;
	}

	public static String getEfenceData(String imei){
		//
		return   dnaBaseUrl + "/device/service?code=childWatch&type=efencelist&imei="+imei;
		//For test
//		return   "http://192.168.0.104:8080" + "/device/service?code=childWatch&type=efencelist&imei="+imei;
	}

	public static String getPublicKey(){
		return   dnaBaseUrl+"/familycore-pay/core/api?code=key&action=public_key" +
				"&devicetype=0&platform=2&client=1"+
				"&token="+ BaseApplication.getInstances().getSessionID()+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() +
				"&memberId="+QuanjiakanSetting.getInstance().getUserId()
				;
	}

	//********************************************  E豆  获取公钥
	public static String getPublicKey_get(){
		return   urlPay+"/familycore-pay/core/api_get?code=key&action=public_key" +
				"&devicetype=0&platform=2&client=1"+
				"&token="+ BaseApplication.getInstances().getSessionID()+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() +
				"&memberId="+QuanjiakanSetting.getInstance().getUserId()
				;
	}

	//157.122.57.165   -----   192.168.1.178     192.168.0.122:8080
	public static String getPublicKey_get_test(){
		return   "http://192.168.0.122:8080"+"/familycore-pay/core/api_get?code=key&action=public_key" +
				"&devicetype=0&platform=2&client=1"+
				"&token="+ BaseApplication.getInstances().getSessionID()+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() +
				"&memberId="+QuanjiakanSetting.getInstance().getUserId()
				;
	}

	//********************************************  E豆  我的信息
	public static String getEBeanInfo_get(){
		return   dnaBaseUrl+"/familycare/v2/health/api_get?code=profile&action=getmemberprofile" +
				"&devicetype=0&platform=2&client=1"+
				"&token="+ BaseApplication.getInstances().getSessionID()+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() +
				"&memberId="+QuanjiakanSetting.getInstance().getUserId()
				;
	}

	public static String getEBeanInfo_get_test(){
		return   "http://192.168.0.122:8080"+"/familycare/v2/health/api_get?code=profile&action=getmemberprofile" +
				"&devicetype=0&platform=2&client=1"+
				"&token="+ BaseApplication.getInstances().getSessionID()+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() +
				"&memberId="+QuanjiakanSetting.getInstance().getUserId()
				;
	}
	//********************************************  E豆充值历史
	public static String getEBeanHistoryOrder_get(){
		return   urlPay+"/familycore-pay/core/api_get?code=recharge&action=recharge_order_list" +
				"&devicetype=0&platform=2&client=1"+
				"&token="+ BaseApplication.getInstances().getSessionID()+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() +
				"&memberId="+QuanjiakanSetting.getInstance().getUserId()
				;
	}

	public static String getEBeanHistoryOrder_get_test(){
		return   "http://192.168.0.122:8080"+"/familycore-pay/core/api_get?code=recharge&action=recharge_order_list" +
				"&devicetype=0&platform=2&client=1"+
				"&token="+ BaseApplication.getInstances().getSessionID()+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() +
				"&memberId="+QuanjiakanSetting.getInstance().getUserId()
				;
	}
	//********************************************

	public static String getEBeanChargeOption_get(){
		return   urlPay+"/familycore-pay/core/api_get?code=recharge&action=recharge_list" +
				"&devicetype=0&platform=2&client=1"+
				"&token="+ BaseApplication.getInstances().getSessionID()+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() +
				"&memberId="+QuanjiakanSetting.getInstance().getUserId()
				;
	}

	public static String getEBeanChargeOption_get_test(){
		return   "http://192.168.0.122:8080"+"/familycore-pay/core/api_get?code=recharge&action=recharge_list" +
				"&devicetype=0&platform=2&client=1"+
				"&token="+ BaseApplication.getInstances().getSessionID()+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() +
				"&memberId="+QuanjiakanSetting.getInstance().getUserId()
				;
	}
	//********************************************

	public static String getEBeanOrder(){
		return   urlPay+"/familycore-pay/core/api_get?code=recharge&action=recharge_produceorder" +
				"&devicetype=0&platform=2&client=1" +
				"&token="+ BaseApplication.getInstances().getSessionID()+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() +
				"&memberId="+QuanjiakanSetting.getInstance().getUserId()
				;
	}

	public static String getEBeanOrder_test(){
		return   "http://192.168.0.122:8080"+"/familycore-pay/core/api_get?code=recharge&action=recharge_produceorder" +
				"&devicetype=0&platform=2&client=1" +
				"&token="+ BaseApplication.getInstances().getSessionID()+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() +
				"&memberId="+QuanjiakanSetting.getInstance().getUserId()
				;
	}

	//********************************************  EBean 发布订单

	public static String getEBeanOrderPublish(){
		return   urlPay+"/familycore-pay/core/api_get?code=recharge&action=recharge_publishorder" +
				"&devicetype=0&platform=2&client=1" +
				"&token="+ BaseApplication.getInstances().getSessionID()+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() +
				"&memberId="+QuanjiakanSetting.getInstance().getUserId()
				;
	}

	public static String getEBeanOrderPublish_test(){
		return   "http://192.168.0.122:8080"+"/familycore-pay/core/api_get?code=recharge&action=recharge_publishorder" +
				"&devicetype=0&platform=2&client=1" +
				"&token="+ BaseApplication.getInstances().getSessionID()+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() +
				"&memberId="+QuanjiakanSetting.getInstance().getUserId()
				;
	}
	//********************************************

	public static String getVideoCommendList(){
		return   baseUrlGet +"devicetype=0&platform=2&client=1"+
				"&token="+ BaseApplication.getInstances().getSessionID()+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() +
				"&memberId="+QuanjiakanSetting.getInstance().getUserId() +
				"&code=ideodemand&action=getappraiselist";
	}

	public static String getVideoCommendDetail(){
		return   baseUrlGet +"devicetype=0&platform=2&client=1"+
				"&token="+ BaseApplication.getInstances().getSessionID()+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() +
				"&memberId="+QuanjiakanSetting.getInstance().getUserId() +
				"&code=ideodemand&action=getappraiseonelist";
	}

	public static String sendVideoCommend(){
		return   baseUrlGet +"devicetype=0&platform=2&client=1"+ "&token="+ BaseApplication.getInstances().getSessionID()+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() +
				"&memberId="+QuanjiakanSetting.getInstance().getUserId() +
				"&code=ideodemand&action=addappraise";
	}

	public static String getFreeInquiryDoctorInfo(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=consultant&type=cy&a=doctordetail";
	}

	public static String getFreeInquiryEvaluateProblem(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=consultant&type=cy&a=evaluate";
	}

	public static String getAndroid_Version(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=jugui&a=getfunction&alias=android_version";
	}

	public static String getPulishOrderDetail(String orderid){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=graborder&a=getmedicalorderdetail&orderid="+orderid;
	}

	public static String getCancelOrder(String orderid){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=graborder&a=usercancelorder";
	}

	public static String getGetNews(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=news&a=getnews&type=1";
	}

	/**
	 * 获取视频点播列表
	 * @return
     */
	public static String getDemandVideoList(){
		return   baseUrlGet + "token="+ BaseApplication.getInstances().getSessionID() +
				"&devicetype=0&platform=2&client=1"+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() +
				"&memberId="+QuanjiakanSetting.getInstance().getUserId() + "&code=ideodemand&action=list";
	}

	/**
	 * 获取视频直播列表
	 * @return
	 */
	public static String getLiveVideoList(){
		return
				baseUrlGet
//						"http://112.93.116.191:8081/familycare/v2/health/api_get?"
				+ "token="+ BaseApplication.getInstances().getSessionID() +
						"&devicetype=0&platform=2&client=1"+
						"&user_id="+QuanjiakanSetting.getInstance().getUserId() +
						"&userId="+QuanjiakanSetting.getInstance().getUserId() +
				"&action=SEARCH&code=liveProgram&state=1&type=1";
	}

	public static String getIntoLiveRoom(){
		return
				baseUrlGet
//					"http://112.93.116.191:8081/familycare/v2/health/api_get?"
						+ "token="+ BaseApplication.getInstances().getSessionID() +
						"&devicetype=0&platform=2&client=1"+
						"&user_id="+QuanjiakanSetting.getInstance().getUserId() +
						"&action=GETLIVEURL&code=liveProgram";
	}

	public static String getDemandVideoPlayNumber(){
		return   baseUrlGet + "token="+ BaseApplication.getInstances().getSessionID() +
				"&devicetype=0&platform=2&client=1"+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&code=ideodemand&action=addplay";
	}

	public static String getDemandVideoAddPraiseNumber(){
		return   baseUrlGet + "token="+ BaseApplication.getInstances().getSessionID() +
				"&devicetype=0&platform=2&client=1"+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&code=ideodemand&action=videolikes";
	}
	/**
	 * 获取家政订单列表
	 * @param page
	 * @return
	 */
	public static String getHouseKeeperOrderList(int page){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=housekeeper&a=getorderlist&page="+page;
	}

	/**
	 * 获取家政详情
	 * @param id
	 * @return
	 */
	public static String getHouseKeeperDetail(String id){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=housekeeper&a=gethousekeeperdetail&id="+id;
	}
	
	/**
	 * 添加药品到购物车
	 * @return
	 */
	public static String postMedcineCart(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=medcine&a=addmedcinecart";
	}

	/**
	 * 从购物车删除药品
	 * @return
	 */
	public static String postDeleteMedcineCart(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=medcine&a=deleteshopcart";
	}
	
	/**
	 * 获取首页健康数据
	 * @return
	 */
	public static String getHealthData(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=health&a=gethealthdata";
	}

    /**
     * 获取首页健康数据
     * @return
     */
    public static String getMyInfo(){
        return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=appuser&a=getuserinfo";
    }

	/**
	 * 获取手环病历设备
	 * @return
	 */
	public static String getHealthData_CaseHistory(String deviceid){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=watch&a=getwatchuserinfo&deviceid="+deviceid;
	}

	/**
	 * 获取手环病历设备
	 * @return
	 */
	public static String getHealthData_UploadCaseHistory(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=watch&a=updatemedicalrecord";
	}

	public static String getHealthData_User(){
		return   dnaBaseUrl + "/familycare/health2/api_get?code=archives&action=user_archives" +
				"&devicetype=0&platform=2&client=1"+
				"&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId();
	}


	public static String getWatchHealthData_User(){
		return   dnaBaseUrl + "/familycare/api?m=watch&a=getwatchuserinfo" +
				"&devicetype=0&platform=2&client=1"+
				"&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId();
	}
	
	/**
	 * 获取短信验证码
	 * @return
	 */
	public static String getSMSCode(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() +
//				"&devicetype=0&platform=2&client=1"+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=member&a=getsmscode";
	}

	public static String getSMSCode2(){
		return   baseUrlPost1 + "token="/*+ BaseApplication.getInstances().getSessionID()*/ +
//				"&devicetype=0&platform=2&client=1"+
				"&code=sms&action=getsms";
	}
	
	/**
	 * 获取订单支付信息
	 * @return
	 */
	public static String getAliPaidInfo(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() +
				"&devicetype=0&platform=2&client=1"+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=alipay&a=getpaidinfo";
	}
	
	/**
	 * 验证阿里支付结果信息
	 * @return
	 */
	public static String getVetifyPayment(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() +
				"&devicetype=0&platform=2&client=1"+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=alipay&a=vertifypayment";
	}

	/**
	 * 验证阿里支付结果信息
	 * @return---
	 */
//	public static String getVetifyHousePayment(){
//		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=housekeeper&a=certifypayment";
//	}

	/**
	 *生成家政服务订单
	 * @return
     */
	public static String getVetifyHousePayment(){
		return   url + "&token="+ BaseApplication.getInstances().getSessionID() +
				"&devicetype=0&platform=2&client=1"+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&code=pay&action=housekeeper_publishorder";
	}
	
	
	/**
	 * 获取medcine购物车信息
	 * @return
	 */
	public static String getMedcineShopCartList(int page){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() +
				"&devicetype=0&platform=2&client=1"+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=medcine&a=getmedcinecart&page="+page;
	}
		
	/**
	 * 生成订单
	 * @return
	 */
	public static String addMedcineOrder(String pay_channel){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() +
				"&devicetype=0&platform=2&client=1"+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=medcine&a=ordermedcine&pay_channel="+pay_channel;
	}

	/**
	 * 取消药品订单
	 * @return
	 */
	public static String cancelMedicineOrder(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() +
				"&devicetype=0&platform=2&client=1"+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=medcine&a=cancelorder";
	}
		
	/**
	 * 编辑收货地址
	 * @return
	 */
	public static String editDeliveryAddress(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() +
				"&devicetype=0&platform=2&client=1"+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=medcine&a=updateaddress";
	}

	/**
	 * 添加收货地址
	 * @return
	 */
	public static String addDeliveryAddress(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() +
				"&devicetype=0&platform=2&client=1"+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=medcine&a=adddeliveraddress";
	}
	
	/**
	 * 获取收货地址列表
	 * @return
	 */
	public static String getDeliveryAddress(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() +
				"&devicetype=0&platform=2&client=1"+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=medcine&a=getdeliveraddress";
	}
	
	/**
	 * 获取默认收货地址
	 * @return
	 */
	public static String getDefaultDeliveryAddress(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() +
				"&devicetype=0&platform=2&client=1"+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=medcine&a=getdefaultaddress";
	}
	
	/**
	 * 设置默认的收货地址
	 * @return
	 */
	public static String setDefaultDeliveryAddress(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() +
				"&devicetype=0&platform=2&client=1"+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=medcine&a=setdefaultaddress";
	}
	
	/**
	 * 删除收货地址
	 * @return
	 */
	public static String deleteDeliveryAddress(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() +
				"&devicetype=0&platform=2&client=1"+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=medcine&a=deleteaddress";
	}
	
	/**
	 * 添加家政服务订单
	 * @return
	 */
//	public static String orderHouseKeeper(){
//		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=housekeeper&a=addhousekeeperorder";
//	}

	/**
	 * 生成家政预约订单
	 * @return
     */
	public static String orderHouseKeeper(){
		return   url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&code=pay&action=housekeeper_produceorder";
	}
	/**
	 * 绑定手表设备
	 * @return
	 */
	public static String bindWatch(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=watch&a=connect_watch";
	}
	//m=group&a=updategroupmember

	/**
	 * user_id:用户id
	 * did:deviceiid
	 * type:1表示添加群成员   0,表示删除
	 *
	 * @return
     */
	public static String joinGroupAfterBindWatch(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=group&a=updategroupmember";
	}
	/**
	 * 更新绑定设备信息
	 * @return
	 */
	public static String updateWatchInfo(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=watch&a=updatewatchkeeper";
	}

	public static String updateWatchInfo_new(String imei,String icon,String name,String relation){
		return   dnaBaseUrl + "/device/service?code=childWatch&type=updateappgroup&imei="+ imei+//16进制
				"&userid="+BaseApplication.getInstances().getUser_id()+
				"&icon="+icon+
				"&name="+name+
				"&relation="+relation;
	}
	
	/**
	 * 接触设备绑定
	 * @return
	 */
	public static String unbindWatch(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=watch&a=disconnect_watch";
	}
	
	/**
	 * 找回密码
	 * @return
	 */
	public static String findpassword(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=member&a=findpassword";
	}
	
	/**
	 * 反馈建议
	 * @return
	 */
	public static String feedback(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=other&a=feedback";
	}
	
	/**
	 * 添加亲情号码
	 * @return
	 */
	public static String addPhone(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=other&a=addphone";
	}
	
	/**
	 * 移除亲情号码
	 * @return
	 */
	public static String removePhone(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=other&a=removephone";
	}
	
	/**
	 * 设置亲情模式
	 * @return
	 */
	public static String setWatchModel(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=other&a=setwatchmodel";
	}
	
	/**
	 * 取消家政服务订单
	 * @return
	 */
	public static String cancelHouseKeeperOrder(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=housekeeper&a=cancelorder";
	}
	
	/**
	 * 获取家政服务支付信息
	 * @return
	 */
	public static String getHousekeeperPaidinfo(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=housekeeper&a=getpaidinfo";
	}
	
	/**
	 * 添加医生资料
	 * @return
	 */
	public static String postDoctorProfile(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=doctor&a=addprofile";
	}


	/**
	 * 文件上传
	 * @return
	 */
	public static String postFileUpload(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=file&a=postfile";
	}

	/**
	 * 获取医生资料
	 * @return
	 */
	public static String getPhoneDoctorDetail(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=doctor&a=getdoctorphone";
	}

	/**
	 * 获取医生资料
	 * @return
	 */
	public static String getDoctorPrice(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=doctor&a=getdoctorserviceprice";
	}

	/**
	 * 名医聊天开始时间（未改）
	 * @return
     */
	public static  String getStartTime(){
		return base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=doctor&a=doctorsstarttime";
	}

	/**
	 * 生成名医订单(修改)
	 * @return
     */
	public static String getConsultOrderInfo(){
		return url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&code=pay&action=famous_produceorder";
	}

	/**
	 * 发布名医订单（修改）
	 * @return
     */
	public static String publishConsultOrderInfo(){
		return url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&code=pay&action=famous_publishorder";
	}

	/**
	 * 检查名医聊天状态
	 * @return
     */
	public static String checkChatStatus(){
		return base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=doctor&a=checkifisnormal";
	}



	/**
	 * ************************************************************************************************
	 *
	 * 获取绑定
	 *
	 *
	 */

	public static String getBindDevices(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId()+"&m=watch&a=getbindeduser";
	}

	public static String getBindDevices_new(){
		return   dnaBaseUrl + "/device/service?code=childWatch&type=watchlist&userid="+QuanjiakanSetting.getInstance().getUserId();
//		return   "http://192.168.0.104:8080"+"/device/service?code=childWatch&type=watchlist&userid="+BaseApplication.getInstances().getUser_id();
	}

	public static String getBankcardList(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId()+"&m=member&a=getbankcardlist";
	}

	public static String getFamousDoctorMoney(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId()+"&m=wallet&a=getfamousdoctorwallet";
	}

	public static String getWalletNumber(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId()+"&m=wallet&a=getmemberwallet";
	}

	public static String getWalletDoposit(String uid,String deposit){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId()+"&m=wallet&a=memberdeposit&member_id="+uid+"&deposit_money="+deposit;
	}

	public static String getDiDiDoctorMoney(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId()+"&m=wallet&a=getdididoctorwallet";
	}

	public static String addBankcard(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId()+"&m=doctor&a=connectbankcard";
	}

	public static String removeBancard(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId()+"&m=doctor&a=disconnectbankcard";
	}

	public static String getVersion(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId()+"&m=jugui&a=getversion&alias=user_android_version";
	}

	public static String getHouseKeeperPhone(){
		return   base_url + "&token="+ BaseApplication.getInstances().getSessionID() + "&user_id="+QuanjiakanSetting.getInstance().getUserId() + "&m=jugui&a=getfunction&alias=housekeeper_phone";
	}

	public static String getPaymentPwd(){
		return base_url + "token="+ BaseApplication.getInstances().getSessionID() +"&user_id="+QuanjiakanSetting.getInstance().getUserId()+"&m=member&a=getpaymentpwd";
	}

	public static String updatePaymentPwd(){
		return base_url +"token="+BaseApplication.getInstances().getSessionID()+"&user_id="+QuanjiakanSetting.getInstance().getUserId()+"&m=member&a=updatepaymentpwd";
	}

	public static String getGroupChatList(){
		return base_url+"token="+BaseApplication.getInstances().getSessionID()+"&user_id="+QuanjiakanSetting.getInstance().getUserId()+"&m=other&a=getallgid";
	}


	public static String publishVoluteer(){
		return "http://shangshousoftware.applinzi.com/api?&m=volunt&a=postbangbangdainfo"+
		"&devicetype=0&platform=2&client=1"
				;
	}


	public static String getPublishVoluteer(String member_id){
		return "http://shangshousoftware.applinzi.com/api?m=volunt&a=getbangbangdalist" +
				"&devicetype=0&platform=2&client=1"+
				"&member_id="+member_id;
	}

	public static String finishVoluteerOrder(String user_id,String id){
		return "http://shangshousoftware.applinzi.com/api?m=volunt&a=finishbangbangda" +
				"&devicetype=0&platform=2&client=1"+
				"&user_id="+user_id+"&id="+id;
	}

	public static String deleteVoluteerOrder(String user_id,String id){
		return "http://shangshousoftware.applinzi.com/api?m=volunt&a=cancelbangbangda" +
				"&devicetype=0&platform=2&client=1"+
				"&user_id="+user_id+"&id="+id;
	}
	public static String getLeavingMessage(){
		return "http://app.quanjiakan.com/familycare/health/api_get?" + "token="+BaseApplication.getInstances().getSessionID()+
				"&devicetype=0&platform=2&client=1"+
				"&user_id="+QuanjiakanSetting.getInstance().getUserId()+ "&code=missing&action=findbymissinguserid";
	}
	public static String getMisiingPersonInfo(){
		return "http://app.quanjiakan.com/familycare/health/api_get?" + "devicetype=0&platform=2&client=1"+"&token="+BaseApplication.getInstances().getSessionID()+"&user_id="+QuanjiakanSetting.getInstance().getUserId()+ "&code=missing&action=getmissingpages";
	}

	public static String getVolunteerMes(String member_id){
		return "http://shangshousoftware.applinzi.com/api?m=volunt&a=getvolunteerinfo" +
				"&devicetype=0&platform=2&client=1"+
				"&member_id="+member_id;
	}
	public static String CancelSearchStatus(){
		return "http://app.quanjiakan.com/familycare/health/api_get?" + "devicetype=0&platform=2&client=1"+"&token="+BaseApplication.getInstances().getSessionID()+"&user_id="+QuanjiakanSetting.getInstance().getUserId()+ "&code=missing&action=abandonseek";
	}
	public static String ChangeSearchStatus(){
		return "http://app.quanjiakan.com/familycare/health/api_get?" + "devicetype=0&platform=2&client=1"+"&token="+BaseApplication.getInstances().getSessionID()+"&user_id="+QuanjiakanSetting.getInstance().getUserId()+ "&code=missing&action=findpeople";
	}
	public static String PublishSearchInfo(){
		return "http://app.quanjiakan.com/familycare/health/api_get?" + "devicetype=0&platform=2&client=1"+"&token="+BaseApplication.getInstances().getSessionID()+"&user_id="+QuanjiakanSetting.getInstance().getUserId()+ "&code=missing&action=addmissing";
	}
	public static String getPublishInfo(){
		return "http://app.quanjiakan.com/familycare/health/api_get?" + "devicetype=0&platform=2&client=1"+"&token="+BaseApplication.getInstances().getSessionID()+"&user_id="+QuanjiakanSetting.getInstance().getUserId()+ "&code=missing&action=getviamissingpages";
	}
	//送礼物
	public static final String base_url1 = "http://192.168.0.144:8080/familycare/v2/health/api_get?&platform=2&devicetype=0";
	public static String SendGift(){
		return baseUrlGet + "&action=givePresent&code=livepresenttrade&platform=2&devicetype=0";
	}
	//礼物列表
	public static String getGiftList() {
		return baseUrlGet + "&action=select&code=livepresent"+"&rows="+100+"&platform=2&devicetype=0";
	}
	//E豆信息
	public static String getMyLevelInfo(){
		//罗工id
		return
//				"http://192.168.1.178"+
				dnaBaseUrl+
				"/familycare/v2/health/api_get?code=profile&action=getmemberprofile" +
				"&memberId="+QuanjiakanSetting.getInstance().getUserId();
	}

	public static String getSpecificInfo(String id){
		//罗工id
		return
//				"http://192.168.1.178"+
				dnaBaseUrl+
						"/familycare/v2/health/api_get?code=profile&action=getmemberprofile" +
						"&memberId="+id;
	}

	public static String getLiverLevelInfo(String liverId){

		return baseUrlGet+"code=profile&action=getmemberprofile" +
				"&memberId="+liverId;
	}


	public static String getSendGift() {
		return dnaBaseUrl+"/familycare/v2/health/api_get?devicetype=0&platform=2&client=1"+"&action=selectGiverPresent&code=livepresenttrade";
	}

	//获取是否被禁言
	public static String isGag(){
		return
				baseUrlGet+
//						"http://192.168.0.118:8080/familycare/v2/health/api_get?"+
						"code=liveProgram&action=SELECTLIVEPROGRAMGAD";
	}

	//Turn操作
	public static String getTurnData() {
		//http://192.168.0.104:8080/device/service?code=childWatch&type=turn&imei=355637053995130
		return device_url + "code=childWatch&type=turn";
	}
	//RunTime操作
	public static String getRunTimeData() {
		return device_url + "code=childWatch&type=runtime";
	}





	//作息时间计划
	public static String getScheduleData(){
		return device_url+"code=childWatch&type=schedule";
	}

	//上课禁用
	public static String getTimeTablesData(){
		return device_url+"code=childWatch&type=timetable";
	}


//	手表联系人
//	http://192.168.0.104:8080/device/service?code=childWatch&type=contracts&imei=355637053995130
	public static String getContactsList(){
		return device_url + "code=childWatch&type=contracts";
	}

//	宝贝名片
//	http://192.168.0.104:8080/device/service?code=childWatch&type=childrencard&imei=352315052834187&userid=11913
	public static String getBabyCard() {
		return device_url + "code=childWatch&type=childrencard";
	}

//	添加或修改宝贝名片
//	http://192.168.0.104:8080/device/service?code=childWatch&type=addchildcard&imei=355637053995130&userid=11931&name=%E8%B4%9D%E8%B4%9D&gender=%E5%A5%B3&birthday=2014&grade=3&school=%E6%B1%9F%E5%8D%97&icon=image
	public static String saveBabyCard() {
		return device_url + "code=childWatch&type=addchildcard";
	}

//	移交管理员权限
//	http://192.168.0.104:8080/device/service?code=childWatch&type=transferadmin&userid=11107&adminuserid=11029&imei=352315052834187
	public static String MoveAdmin() {
		return device_url + "code=childWatch&type=transferadmin";
	}

	/* 优惠券*/
	/*  http://pay.quanjiakan.com:7080/familycore-pay/core/api_get?code=pay&action=promocode&data={%22memberId%22:10834,%22promocode%22:%2212312312%22}*/
	public static String checkCoupon() {
		return "http://pay.quanjiakan.com:7080/familycore-pay/core/api_get?code=pay&action=promocode";
	}

	/* 修改绑定头像和名字*/
	/* http://app.quanjiakan.com/device/service?code=childWatch&type=updateappbind&imei=355637053837001&userid=11824&icon=111&nickname=%E5%93%A5%E5%93%A5*/
	public static String updateBindInfo() {
		return "http://app.quanjiakan.com/device/service?code=childWatch&type=updateappbind";
	}

}
