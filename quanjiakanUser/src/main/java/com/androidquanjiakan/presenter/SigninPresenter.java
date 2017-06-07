package com.androidquanjiakan.presenter;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.androidquanjiakan.activity.index.SigninActivity_mvp;
import com.androidquanjiakan.activity.main.MainActivity;
import com.androidquanjiakan.base.BaseApplication;
import com.androidquanjiakan.base.CommonRequestCode;
import com.androidquanjiakan.base.QuanjiakanSetting;
import com.androidquanjiakan.entity.LoginInfoEntity;
import com.androidquanjiakan.entity_util.SerialUtil;
import com.androidquanjiakan.util.LogUtil;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.quanjiakan.main.R;
import com.quanjiakanuser.http.HttpResponseInterface;
import com.quanjiakanuser.http.HttpResponseResult;
import com.quanjiakanuser.http.HttpUrls;
import com.quanjiakanuser.http.MyHandler;
import com.quanjiakanuser.http.Task;
import com.quanjiakanuser.util.GsonParseUtil;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by Administrator on 2017/4/14 0014.
 */

public class SigninPresenter implements IBasePresenter {
    private LoginInfoEntity entity;
    public void doLogin(final SigninActivity_mvp activityMvp){
        Object params = activityMvp.getParamter(IPresenterBusinessCode.LOGIN);
        if(params==null){
            //TODO 控制无效的参数，需要针对不同的业务进行区分，当业务本身即为Get,不需要参数时，这个判断与回调也就不需要了
            activityMvp.onError(IPresenterBusinessCode.NONE,200,null);
            return ;
        }
        activityMvp.showMyDialog(IPresenterBusinessCode.LOGIN);
        MyHandler.putTask(activityMvp, new Task(new HttpResponseInterface() {
            @Override
            public void handMsg(String val) {
                LogUtil.e("---------login-----------"+val);
                if (val == null || val.length() < 1) {
                    activityMvp.dismissMyDialog(IPresenterBusinessCode.LOGIN);
                    activityMvp.onError(IPresenterBusinessCode.LOGIN,200,activityMvp.getResources().getString(R.string.common_hint_null_return_value));
                } else {
                    HttpResponseResult result = new HttpResponseResult(val);
                    JsonObject jsonObject = new GsonParseUtil(val).getJsonObject();
                    if (result.getCode().equals("200")) {
                        try {
                            entity = (LoginInfoEntity) SerialUtil.jsonToObject(val, new TypeToken<LoginInfoEntity>() {
                            }.getType());
                            QuanjiakanSetting.getInstance().setValue("session", val);
                            //做个登陆缓存
                            if (jsonObject != null && jsonObject.has("nickname") &&
                                    !(jsonObject.get("nickname") instanceof JsonNull)) {
                                BaseApplication.getInstances().setKeyValue("nickname", jsonObject.get("nickname").getAsString());
                            }
                            QuanjiakanSetting.getInstance().setUserId(Integer.parseInt(entity.getMessage()));
                            registerOnJpush(activityMvp,activityMvp.getUsername(), entity.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                            activityMvp.dismissMyDialog(IPresenterBusinessCode.LOGIN);
                            activityMvp.onError(IPresenterBusinessCode.LOGIN,200,activityMvp.getResources().getString(R.string.common_hint_serial_error));
                        }
                    } else {
                        activityMvp.dismissMyDialog(IPresenterBusinessCode.LOGIN);
                        activityMvp.onError(IPresenterBusinessCode.LOGIN,200,result.getMessage());
                    }
                }
            }
        }, HttpUrls.getSignin(), params, Task.TYPE_POST_DATA_PARAMS, null));
    }


    protected void registerOnJpush(final SigninActivity_mvp activityMvp,final String username, final String user_id) {
        JMessageClient.register(CommonRequestCode.JMESSAGE_PREFIX + user_id, CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
            @Override
            public void gotResult(final int status, final String desc) {
                LogUtil.e("JMessage register status:" + status + "    desc:" + desc);
                if (status == 0 || status == 898001) {
                    JMessageClient.login(CommonRequestCode.JMESSAGE_PREFIX + user_id, CommonRequestCode.JMESSAGE_PASSWORD, new BasicCallback() {
                        @Override
                        public void gotResult(final int status, final String desc) {
                            LogUtil.e("JMessage login status:" + status + "    desc:" + desc);
                            activityMvp.dismissMyDialog(IPresenterBusinessCode.LOGIN);
                            if (status == 0) {
                                //TODO 设置别名，为通过别名发送推送的通知建立条件
                                JPushInterface.setAliasAndTags(activityMvp, CommonRequestCode.JMESSAGE_PREFIX + user_id, null, new TagAliasCallback() {
                                    @Override
                                    public void gotResult(int resultCode, String resultMessage, Set<String> set) {
                                        switch (resultCode) {

                                        }
                                    }
                                });
                                //TODO 虽然说是设置了全局免打扰，但没觉得有什么效果
                                JMessageClient.setNoDisturbGlobal(0, new BasicCallback() {
                                    @Override
                                    public void gotResult(int result, String message) {
                                    }
                                });
                                BaseApplication.getInstances().setSessionID(entity.getToken());
                                BaseApplication.getInstances().setUser_id(entity.getMessage().trim());
                                QuanjiakanSetting.getInstance().setValue(QuanjiakanSetting.KEY_SIGNAL_P, username);
                                BaseApplication.getInstances().setUsername(username);
                                BaseApplication.getInstances().setPw_signature(activityMvp.getPassword());
                                activityMvp.onSuccess(IPresenterBusinessCode.LOGIN,200,null);
                            } else {
                                /**
                                 * 避免由于接口成功导致的重进时，进入APP页面
                                 */
                                BaseApplication.getInstances().setUser_id("0");
                                QuanjiakanSetting.getInstance().setUserId(0);
                                activityMvp.onError(IPresenterBusinessCode.LOGIN,200,activityMvp.getResources().getString(R.string.common_hint_login_im_server_error));

                            }
                        }
                    });
                } else {
                    /**
                     * 避免由于接口成功导致的重进时，进入APP页面
                     */
                    activityMvp.dismissMyDialog(IPresenterBusinessCode.LOGIN);
                    BaseApplication.getInstances().setUser_id("0");
                    QuanjiakanSetting.getInstance().setUserId(0);
                    activityMvp.onError(IPresenterBusinessCode.LOGIN,200,activityMvp.getResources().getString(R.string.common_hint_login_im_server_error));//common_hint_login_im_server_error
                }
            }
        });

    }
}
