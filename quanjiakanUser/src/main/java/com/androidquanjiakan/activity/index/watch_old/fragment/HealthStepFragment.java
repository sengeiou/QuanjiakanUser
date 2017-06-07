package com.androidquanjiakan.activity.index.watch_old.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquanjiakan.view.StepArcOldView;
import com.quanjiakan.main.R;

/**
 * Created by Gin on 2017/4/6.
 */

public class HealthStepFragment extends Fragment {

    private View view;
    private StepArcOldView stepArcOldView;
    private LinearLayout ll_oneday;
    private TextView tv_oneday;
    private Context context;
    private View oneday_div;
    private TextView tv_sevenday;
    private View sevenday_div;
    private LinearLayout ll_sevenday;
    private final int TYPE_ONEDAY=1;
    private final int TYPE_SEVENDAY=2;
    private StepOneDayFragment stepOneDayFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private StepSevenDayFragment stepSevenDayFragment;
    private String deviceId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_health_step, container, false);
        initView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();

    }

    private void initView() {
        Bundle bundle = getArguments();
        deviceId = bundle.getString("deviceId");
        stepArcOldView = (StepArcOldView) view.findViewById(R.id.stepoldview);
        stepArcOldView.setCurrentCount(200000,120000);
        stepArcOldView.setOnClickListener(new StepArcOldView.OnClickListener() {
            @Override
            public void click() {
                Toast.makeText(context, "弹出dialog修改步数", Toast.LENGTH_SHORT).show();
            }
        });


        tv_oneday = (TextView) view.findViewById(R.id.tv_oneday);
        oneday_div = view.findViewById(R.id.oneday_div);
        ll_oneday = (LinearLayout) view.findViewById(R.id.ll_oneday);
        ll_oneday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClickType(TYPE_ONEDAY);

            }
        });

        tv_sevenday = (TextView) view.findViewById(R.id.tv_sevenday);
        sevenday_div = view.findViewById(R.id.sevenday_div);
        ll_sevenday = (LinearLayout) view.findViewById(R.id.ll_sevenday);
        ll_sevenday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClickType(TYPE_SEVENDAY);

            }
        });


        /**
         * 设置初始选择值
         */
        setClickType(TYPE_ONEDAY);

    }

    private void setClickType(int type) {
        switch (type){
            case TYPE_ONEDAY:
                tv_oneday.setTextColor(getResources().getColor(R.color.step_text_color));
                oneday_div.setVisibility(View.VISIBLE);
                tv_sevenday.setTextColor(getResources().getColor(R.color.color_countTextColor));
                sevenday_div.setVisibility(View.INVISIBLE);

                setFragment(TYPE_ONEDAY);
                break;

            case TYPE_SEVENDAY:
                tv_sevenday.setTextColor(getResources().getColor(R.color.step_text_color));
                sevenday_div.setVisibility(View.VISIBLE);
                tv_oneday.setTextColor(getResources().getColor(R.color.color_countTextColor));
                oneday_div.setVisibility(View.INVISIBLE);

                setFragment(TYPE_SEVENDAY);
                break;

        }
    }

    private void setFragment(int type) {
        if(fragmentManager==null) {
            fragmentManager = getFragmentManager();
        }
        Bundle bundle = new Bundle();
        bundle.putString("deviceId",deviceId);
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (type){
            case TYPE_ONEDAY:
                if(stepOneDayFragment==null) {
                    stepOneDayFragment = new StepOneDayFragment();
                    stepOneDayFragment.setArguments(bundle);
                }
                if(!stepOneDayFragment.isAdded()) {
                    fragmentTransaction.replace(R.id.fl_step,stepOneDayFragment,"one");
                    fragmentTransaction.commit();
                }else {
                    Fragment fragmentByTag = fragmentManager.findFragmentByTag("one");
                    fragmentTransaction.remove(fragmentByTag);
                    StepOneDayFragment stepOneDayFragment = new StepOneDayFragment();
                    fragmentTransaction.replace(R.id.fl_step,stepOneDayFragment);
                    stepOneDayFragment.setArguments(bundle);
                    fragmentTransaction.commit();
                }
                break;

            case TYPE_SEVENDAY:
                if(stepSevenDayFragment==null) {
                    stepSevenDayFragment = new StepSevenDayFragment();
                    stepSevenDayFragment.setArguments(bundle);
                }

                if(!stepSevenDayFragment.isAdded()) {
                    fragmentTransaction.replace(R.id.fl_step,stepSevenDayFragment);
                    fragmentTransaction.commit();
                }else {
                    if(stepOneDayFragment!=null) {
                        fragmentTransaction.hide(stepOneDayFragment).show(stepOneDayFragment);
                    }else {
                        fragmentTransaction.show(stepSevenDayFragment);
                    }

                }
                break;
        }

    }

}
