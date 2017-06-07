package com.androidquanjiakan.activity.index.watch_old.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.quanjiakan.main.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Gin on 2017/4/6.
 */

public class HealthHeartRateFragment extends Fragment implements View.OnClickListener {

    private LineChartView lineChart;
    String[] time = {"00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00","24:00"};//X轴的标注
    int[] rates= {20,50,55,69,80,69,67,39,40,86,92,80,70,83,50,100,90,93,72,44,55,79,20,100};//
    private LineChartData lineData;
    private String deviceId;
    private ImageView iv_background;
    private Button btn_begincheck;
    private Animation animation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_health_heartrate, container, false);
        initView(view);

        return view;
    }

    private void initView(View view) {
        Bundle bundle = getArguments();
        deviceId = bundle.getString("deviceId");
        iv_background = (ImageView) view.findViewById(R.id.iv_background);
        btn_begincheck = (Button) view.findViewById(R.id.btn_begincheck);

        btn_begincheck.setOnClickListener(this);
        iv_background.setOnClickListener(this);

        lineChart = (LineChartView) view.findViewById(R.id.line_chart);
        generateInitialLineData();


    }

    private void generateInitialLineData() {
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<PointValue> values = new ArrayList<PointValue>();

        for (int i = 0; i < rates.length; ++i) {
            values.add(new PointValue(i, rates[i]));
            axisValues.add(new AxisValue(i).setLabel(time[i]));
        }

        Line line = new Line(values);
        line.setColor(Color.parseColor("#2ebbbb")).setCubic(false);

        List<Line> lines = new ArrayList<Line>();
        lines.add(line);


        lineData = new LineChartData(lines);
        lineData.setAxisXBottom(new Axis(axisValues).setHasLines(false));
        lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3).setName(""));

        lineChart.setLineChartData(lineData);

        lineChart.setViewportCalculationEnabled(true);





    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_begincheck:
                iv_background.setImageResource(R.drawable.health_ring_rotate);
                animation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
                iv_background.startAnimation(animation);
                break;

            case R.id.iv_background:
                iv_background.clearAnimation();
                iv_background.setImageResource(R.drawable.health_ring_nor);
                break;
        }



    }
}
