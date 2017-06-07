package com.androidquanjiakan.activity.index.watch_old.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquanjiakan.util.LogUtil;
import com.quanjiakan.main.R;

/**
 * Created by Gin on 2017/4/6.
 */

public class HealthSleepFragment extends Fragment {
    private String deviceId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_sleep, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        Bundle bundle = getArguments();
        deviceId = bundle.getString("deviceId");
        LogUtil.e("");
    }
}
