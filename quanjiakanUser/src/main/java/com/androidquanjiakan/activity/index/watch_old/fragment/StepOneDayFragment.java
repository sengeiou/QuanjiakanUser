package com.androidquanjiakan.activity.index.watch_old.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquanjiakan.util.LogUtil;
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
 * Created by Gin on 2017/4/7.
 */

public class StepOneDayFragment extends Fragment {
    private LineChartView lineChart;
    String[] time = {"00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","22:00","23:00","24:00"};//X轴的标注
    int[] steps= {2000,6000,1234,1500,8000,1233,2344,1242,500,100,123,800,620,780,3020,1203,111,415,7200,1444,125,79,20,1000};//图表的数据
    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    private LineChartData lineData;
    private View view;
    private String deviceId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_step_oneday, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initView(View view) {
        Bundle bundle = getArguments();
        deviceId = bundle.getString("deviceId");
        LogUtil.e("oneday:"+deviceId);
        lineChart = (LineChartView) view.findViewById(R.id.line_chart);
        generateInitialLineData();

    }

    private void generateInitialLineData() {
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<PointValue> values = new ArrayList<PointValue>();

        for (int i = 0; i < steps.length; ++i) {
            values.add(new PointValue(i, steps[i]));
            axisValues.add(new AxisValue(i).setLabel(time[i]));
        }

        Line line = new Line(values);
        line.setColor(Color.parseColor("#2ebbbb")).setCubic(false);

        List<Line> lines = new ArrayList<Line>();
        lines.add(line);


        lineData = new LineChartData(lines);
        lineData.setAxisXBottom(new Axis(axisValues).setHasLines(false));
        lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3).setName(" "));

        lineChart.setLineChartData(lineData);

        lineChart.setViewportCalculationEnabled(true);

    }

}
