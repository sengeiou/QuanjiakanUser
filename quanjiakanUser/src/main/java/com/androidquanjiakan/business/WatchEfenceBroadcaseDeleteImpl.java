package com.androidquanjiakan.business;

import android.content.Context;

import com.androidquanjiakan.database.TableInfo_ValueStub;
import com.androidquanjiakan.datahandler.DeviceRailHandler;
import com.androidquanjiakan.entity.WatchEfenceDelete;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.google.gson.reflect.TypeToken;

/**
 * Created by Administrator on 2017/3/16 0016.
 */

public class WatchEfenceBroadcaseDeleteImpl implements IWatchBroadcaseSaver {
    @Override
    public void doBusiness(String data, Context context) {
        try{
            //data != null && data.contains("\"Category\":\"Efence\"".replace("\\", "")) && data.contains("\"Action\":\"Delete\"".replace("\\", ""))) {
            if(data!=null && data.contains("\"Category\":\"Efence\"".replace("\\", "")) && data.contains("\"Action\":\"Delete\"".replace("\\", ""))) {
                //TODO 收到删除
                WatchEfenceDelete result = (WatchEfenceDelete) SerialUtil.jsonToObject(data, new TypeToken<WatchEfenceDelete>() {}.getType());
                if(result!=null) {
                    if ("1".equals(result.getResults().getIndex())) {
                        DeviceRailHandler.remove(result.getResults().getIMEI(), TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST, "");
                    } else if ("2".equals(result.getResults().getIndex())) {
                        DeviceRailHandler.remove(result.getResults().getIMEI(), TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND, "");
                    } else if ("3".equals(result.getResults().getIndex())) {
                        DeviceRailHandler.remove(result.getResults().getIMEI(), TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD, "");
                    } else {

                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
