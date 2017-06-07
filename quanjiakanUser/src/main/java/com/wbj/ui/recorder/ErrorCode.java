package com.wbj.ui.recorder;

import android.content.Context;
import android.content.res.Resources.NotFoundException;

public class ErrorCode {
	public final static int SUCCESS = 1000;
	public final static int E_NOSDCARD = 1001;
	public final static int E_STATE_RECODING = 1002;
	public final static int E_UNKOWN = 1003;
	
	public static String getErrorInfo(Context ctx, int vType) throws NotFoundException {
		switch (vType) {
		case SUCCESS:
			return "Success";
		case E_NOSDCARD:
			return "NoCard";
		case E_STATE_RECODING:
			return "Recording";
		case E_UNKOWN:
		default:
			return "Error";
		}
	}
}
