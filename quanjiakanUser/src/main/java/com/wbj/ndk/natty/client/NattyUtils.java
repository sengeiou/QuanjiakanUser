package com.wbj.ndk.natty.client;


public class NattyUtils {
	public final static String NTY_MSG_NODE_SIGNAL_NAME = "Signal"; 
	public final static String NTY_MSG_NODE_POWER_NAME = "Power"; 
	
	public final static String NTY_MSG_NODE_PHONEBOOK_NAME = "PhoneBook";
	public final static String NTY_MSG_NODE_FAMILYNUMBER_NAME = "FamilyNumber"; 
	
	public final static String NTY_MSG_NODE_FALLEN_NAME = "Fall";
	public final static String NTY_MSG_NODE_GPS_NAME = "GPS";
	
	public final static String NTY_MSG_NODE_WIFI_NAME = "Wifi";
	public final static String NTY_MSG_NODE_LAB_NAME = "Lab";
	
	public final static String NTY_MSG_NODE_LOCATION_NAME = "Location";
	public final static String NTY_MSG_NODE_STEP_NAME = "Step";
	public final static String NTY_MSG_NODE_HEARTRATE_NAME = "HeartRate";
	public final static String NTY_MSG_NODE_STATUS_NAME = "Status";
	public final static String NTY_MSG_NODE_CONFIG_NAME = "Config";
	public final static String NTY_MSG_NODE_EFENCELIST_NAME = "EfenceList";
	public final static String NTY_MSG_NODE_EFENCE_NAME = "Efence";
	
	public final static String NTY_MSG_OPERA_GET_NAME = "Get";
	public final static String NTY_MSG_OPERA_SET_NAME = "Set";
	public final static String NTY_MSG_OPERA_START_NAME = "Start";
	
	
	
	public final static int NTY_MSG_NODE_SIGNAL = 0x01;
	public final static int NTY_MSG_NODE_POWER = 0x02;
	
	public final static int NTY_MSG_NODE_PHONEBOOK = 0x03;
	public final static int NTY_MSG_NODE_FAMILYNUMBER = 0x04;
	
	public final static int NTY_MSG_NODE_FALLEN = 0x05;
	public final static int NTY_MSG_NODE_GPS = 0x06;
	
	public final static int NTY_MSG_NODE_WIFI = 0x07;
	public final static int NTY_MSG_NODE_LAB = 0x08;
	
	public final static int NTY_MSG_NODE_LOCATION = 0x09;
	public final static int NTY_MSG_NODE_STEP = 0x0A;
	public final static int NTY_MSG_NODE_HEARTRATE = 0x0B;
	public final static int NTY_MSG_NODE_STATUS = 0x0C;
	public final static int NTY_MSG_NODE_CONFIG = 0x0D;
	public final static int NTY_MSG_NODE_EFENCE = 0x0E;
	public final static int NTY_MSG_NODE_EFENCELIST = 0x0F;
	
	public final static int NTY_MSG_OPERA_GET = 0xA1;
	public final static int NTY_MSG_OPERA_SET = 0xA2;
	public final static int NTY_MSG_OPERA_START = 0xA3;
	
	public static int ntyCompareNode(String taken) {
		if(taken.equals(NTY_MSG_NODE_SIGNAL_NAME)) {
			return NTY_MSG_NODE_SIGNAL;
		} else if (taken.equals(NTY_MSG_NODE_POWER_NAME)) {
			return NTY_MSG_NODE_POWER;
		} else if (taken.equals(NTY_MSG_NODE_PHONEBOOK_NAME)) {
			return NTY_MSG_NODE_PHONEBOOK;
		} else if (taken.equals(NTY_MSG_NODE_FAMILYNUMBER_NAME)) {
			return NTY_MSG_NODE_FAMILYNUMBER;
		} else if (taken.equals(NTY_MSG_NODE_FALLEN_NAME)) {
			return NTY_MSG_NODE_FALLEN;
		} else if (taken.equals(NTY_MSG_NODE_GPS_NAME)) {
			return NTY_MSG_NODE_GPS;
		} else if (taken.equals(NTY_MSG_NODE_WIFI_NAME)) {
			return NTY_MSG_NODE_WIFI;
		} else if (taken.equals(NTY_MSG_NODE_LAB_NAME)) {
			return NTY_MSG_NODE_LAB;
		} else if (taken.equals(NTY_MSG_NODE_LOCATION_NAME)) {
			return NTY_MSG_NODE_LOCATION;
		} else if (taken.equals(NTY_MSG_NODE_STEP_NAME)) {
			return NTY_MSG_NODE_STEP;
		} else if (taken.equals(NTY_MSG_NODE_HEARTRATE_NAME)) {
			return NTY_MSG_NODE_HEARTRATE;
		} else if (taken.equals(NTY_MSG_NODE_STATUS_NAME)) {
			return NTY_MSG_NODE_STATUS;
		} else if (taken.equals(NTY_MSG_NODE_CONFIG_NAME)) {
			return NTY_MSG_NODE_CONFIG;
		} else if (taken.equals(NTY_MSG_NODE_EFENCE_NAME)) {
			return NTY_MSG_NODE_EFENCE;
		} else if (taken.equals(NTY_MSG_NODE_EFENCELIST_NAME)) {
			return NTY_MSG_NODE_EFENCELIST;
		}
		return -1;
	}
	
	public static int ntyCompareOpera(String taken) {
		if (taken.equals(NTY_MSG_OPERA_GET_NAME)) {
			return NTY_MSG_OPERA_GET;
		} else if (taken.equals(NTY_MSG_OPERA_SET_NAME)) {
			return NTY_MSG_OPERA_SET;
		} else if (taken.equals(NTY_MSG_OPERA_START_NAME)) {
			return NTY_MSG_OPERA_START;
		} 
		return -1;
	}
}
