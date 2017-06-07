package com.androidquanjiakan.base;

import android.os.Environment;

public class BaseConstants {
	
    private static final String STORAGEPATH=Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DIRECTORY=STORAGEPATH+"/facetalk";
	public static final String imageDir = DIRECTORY + "/images/";
	public static final String voiceDir = DIRECTORY + "/voice/";
	
    public static final String WEIXIN_APP_ID = "";
    public static final String XINGE_ACCESS_ID_STRING = "2100190646";
    public static final String XINGE_ACCESS_KEY_STRING = "AA465PU47VVF";

    public static final String jpush_patient_appkey = "57bc71929aa01ebc90f3ff5d";//3441cfbff7e24a2da2090605---原始key【不带secret key】
    public static final String jpush_doctor_appkey =  "cfb4b174eb75826de18ea851";
    public static final String jpush_volunteer_appkey="83f0eacab249d8002376c492";
    public static final String jpush_patient_masterSecret = "d09e78e8b064257e87874381";
    //***************************************************
    public static final String HTTP_HEADER_LOCATION = "Location";

    public static final String HTTP_HEADER_ENCODE_GZIP = "gzip";
    //************************************************************************
    public static final String EMPTY = "";
    public static final String FILE_PATH_PREFIX1 = "file:///";
    public static final String FILE_PATH_PREFIX2 = "file://";
    public static final String FILE_PATH_PREFIX3 = "file:/";
    //***************************************************
    public static final String PICTURE_COMMON_PREFIX = "http";
    public static final String ENCODE_ALLOW = "@#&=*+-_.,:!?()/~\'%";


    public static final String IMAGE_DATA_TYPE = "image/*";
    //*********************************************
    public static final String PARAMS_DEVICE_ID = "device_id";
    public static final String PARAMS_DEVICE_LOCATION_POINT = "location_point";
    public static final String PARAMS_DEVICE_LOCATION_LAT = "location_lat";
    public static final String PARAMS_DEVICE_LOCATION_LNG = "location_lng";
    public static final String PARAMS_DEVICE_PAGE_STATE = "page_state";
    public static final String PARAMS_DEVICE_PAGE_WHICH = "page_which";

    public static final String PARAMS_DEVICE_PHONE = "phone";
    public static final String PARAMS_DEVICE_TYPE = "type";
    public static final String PARAMS_DEVICE_IMAGEPATH = "imagepath";
    public static final String PARAMS_DEVICE_NAME = "name";
    public static final String PARAMS_DEVICE_LAT = "devicelat";
    public static final String PARAMS_DEVICE_LNG = "devicelng";

    //**************************************************
    public static final String PARAMS_JUMP_TYPE = "jump_type";
    public static final String PARAMS_JUMP_TYPE_BIND_REQUEST = "jump_type_bind_request";
    public static final String PARAMS_JUMP_TYPE_BIND_REQUEST_FROMID = "jump_type_bind_request_fromid";
    public static final String PARAMS_JUMP_TYPE_BIND_REQUEST_IMEI = "jump_type_bind_request_imei";
    public static final String PARAMS_JUMP_TYPE_BIND_REQUEST_PROPOSER = "jump_type_bind_request_proposer";
    public static final String PARAMS_JUMP_TYPE_BIND_REQUEST_USERNAME = "jump_type_bind_request_username";
    public static final String PARAMS_JUMP_TYPE_BIND_REQUEST_MSGID = "jump_type_bind_request_msgid";



    //**************************************************
    //Web
    public static final String PARAMS_URL = "params_url";
    public static final String PARAMS_TITLE = "params_title";
    public static final String PARAMS_FROM = "imageFrom";
    public static final String PARAMS_POSITION = "position";

    public static final String PARAM_ENTITY = "entity";
    public static final String PARAM_URL = "url";
    public static final String PARAM_HEADIMG = "headimg_url";
    public static final String PARAM_GROUP = "group_id";
    public static final String PARAM_NUMBER = "number";
    public static final String PARAM_NAME = "name";
    public static final String PARAM_LIVERID = "liverId";


    //*********************************************************************
    public static final int ENTRY_NORMAL = 0;
    public static final int ENTRY_VIDEO_LIVE = 1;
    public static final int NORMAL = 0;
    public static final int JIM = 1;



    //*********************************************************************
    public static final String PARAMS_ENTRY ="entry";
    public static final String PARAMS_STATE = "state";
    public static final String PARAMS_ID = "id";
    public static final String PARAMS_SHOW_HINT = "isShowHint";
    public static final String PARAMS_BIND_RESULT = "bind_result";


    public static final String PARAMS_GROUPID = "groupid";
    public static final String PARAMS_ENTITY = "entity";
    public static final String PARAMS_DATA = "data";
}
