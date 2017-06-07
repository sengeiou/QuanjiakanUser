package com.androidquanjiakan.business;

import android.content.Context;

import com.amap.api.maps2d.model.LatLng;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.database.TableInfo_ValueStub;
import com.androidquanjiakan.datahandler.DeviceRailHandler;
import com.androidquanjiakan.entity.DeviceRailEntity;
import com.androidquanjiakan.entity.WatchEfenceBroadcastAdd;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/16 0016.
 */

public class WatchEfenceBroadcaseSaverImpl implements IWatchBroadcaseSaver {
    @Override
    public void doBusiness(String data, Context context) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            //{"Id":"2","Results":{"IMEI":"355637053995130","Category":"Efence","Action":"Add","Index":"2","Efence":{"Num":"4","Points":["113.242554,23.139700","113.242584,23.132236","113.252510,23.131338","113.252510,23.139931"]}}}
            String str = "{\"Id\":\"2\",\"Results\":{\"IMEI\":\"355637053995130\",\"Category\":\"Efence\",\"Action\":\"Add\",\"Index\":\"2\",\"Efence\":{\"Num\":\"4\",\"Points\":[\"113.242554,23.139700\",\"113.242584,23.132236\",\"113.252510,23.131338\",\"113.252510,23.139931\"]}}}";
            if (data != null && data.contains("\"Category\":\"Efence\"".replace("\\", "")) && data.contains("\"Action\":\"Add\"".replace("\\", ""))) {
                WatchEfenceBroadcastAdd result = (WatchEfenceBroadcastAdd) SerialUtil.jsonToObject(data, new TypeToken<WatchEfenceBroadcastAdd>() {
                }.getType());
                if (result != null) {

                    DeviceRailEntity locaData = new DeviceRailEntity();
                    if ("1".equals(result.getResults().getIndex())) {
                        List<LatLng> first = new ArrayList<>();
                        for (String temp : result.getResults().getEfence().getPoints()) {
                            if (temp != null && temp.contains(",") && temp.trim().split(",").length == 2) {
                                String[] points = temp.split(",");
                                first.add(new LatLng(Double.parseDouble(points[1]), Double.parseDouble(points[0])));
                            }
                        }
                        //TODO 保存该数据
                        int size = first.size();
                        if(size>1) {

                            if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
                                locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                locaData.setDeviceid(result.getResults().getIMEI());
                                locaData.setNumber(size + "");
                                locaData.setPath(EfenceUtil.ListPointToString(first));
                                locaData.setTime(System.currentTimeMillis() + "");
                                locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
                            } else {
                                locaData = new DeviceRailEntity();
                                locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                locaData.setDeviceid(result.getResults().getIMEI());
                                locaData.setNumber(size + "");
                                locaData.setPath(EfenceUtil.ListPointToString(first));
                                locaData.setTime(System.currentTimeMillis() + "");
                                locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
                            }
                            DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_FIRST);
                        }

                    } else if ("2".equals(result.getResults().getIndex())) {
                        List<LatLng> second = new ArrayList<>();
                        for (String temp : result.getResults().getEfence().getPoints()) {
                            if (temp != null && temp.contains(",") && temp.trim().split(",").length == 2) {
                                String[] points = temp.split(",");
                                second.add(new LatLng(Double.parseDouble(points[1]), Double.parseDouble(points[0])));
                            }
                        }
                        //TODO 保存该数据
                        int size = second.size();
                        if(size>1) {

                            if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
                                locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                locaData.setDeviceid(result.getResults().getIMEI());
                                locaData.setNumber(size + "");
                                locaData.setPath(EfenceUtil.ListPointToString(second));
                                locaData.setTime(System.currentTimeMillis() + "");
                                locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
                            } else {
                                locaData = new DeviceRailEntity();
                                locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                locaData.setDeviceid(result.getResults().getIMEI());
                                locaData.setNumber(size + "");
                                locaData.setPath(EfenceUtil.ListPointToString(second));
                                locaData.setTime(System.currentTimeMillis() + "");
                                locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
                            }
                            DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_SECOND);
                        }
                    } else if ("3".equals(result.getResults().getIndex())) {
                        List<LatLng> third = new ArrayList<>();
                        for (String temp : result.getResults().getEfence().getPoints()) {
                            if (temp != null && temp.contains(",") && temp.trim().split(",").length == 2) {
                                String[] points = temp.split(",");
                                third.add(new LatLng(Double.parseDouble(points[1]), Double.parseDouble(points[0])));
                            }
                        }
                        //TODO 保存该数据
                        int size = third.size();
                        if(size>1) {

                            if (locaData != null && locaData.getTime() != null && locaData.getTime().length() > 0) {
                                locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                locaData.setDeviceid(result.getResults().getIMEI());
                                locaData.setNumber(size + "");
                                locaData.setPath(EfenceUtil.ListPointToString(third));
                                locaData.setTime(System.currentTimeMillis() + "");
                                locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
                            } else {
                                locaData = new DeviceRailEntity();
                                locaData.setUserid(BaseApplication.getInstances().getUser_id());
                                locaData.setDeviceid(result.getResults().getIMEI());
                                locaData.setNumber(size + "");
                                locaData.setPath(EfenceUtil.ListPointToString(third));
                                locaData.setTime(System.currentTimeMillis() + "");
                                locaData.setPosition(TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
                            }
                            DeviceRailHandler.insertValue(locaData, TableInfo_ValueStub.DEVICE_RAIL_INFO_THIRD);
                        }
                    } else {

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
