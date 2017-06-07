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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

public class StepSevenDayFragment extends Fragment {

    private LineChartView lineChart;
    String[] time = new String[7];//X轴的标注
    int[] steps= {20,60,100,120,100,12,22};//图表的数据
    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
    private LineChartData lineData;
    private String deviceId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_sevenday, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        Bundle bundle = getArguments();
        deviceId = bundle.getString("deviceId");
        LogUtil.e("sevenday:"+deviceId);
        lineChart = (LineChartView) view.findViewById(R.id.line_chart);
        initDate();
        generateInitialLineData();


    }

    private void generateInitialLineData() {

        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<PointValue> values = new ArrayList<PointValue>();

        for (int i = 0; i < time.length; ++i) {
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
        lineChart.setZoomEnabled(false);



    }

    private void initDate() {
        SimpleDateFormat sdf=new SimpleDateFormat("M.d");
        Calendar seven = Calendar.getInstance();
        seven.roll(Calendar.DATE, -7);//日期回滚7天
        time[0]=sdf.format(seven.getTime())+"";

        Calendar six = Calendar.getInstance();
        six.roll(Calendar.DATE, -6);//日期回滚6天
        time[1]=sdf.format(six.getTime())+"";

        Calendar five = Calendar.getInstance();
        five.roll(Calendar.DATE, -5);//日期回滚5天
        time[2]=sdf.format(five.getTime())+"";

        Calendar four = Calendar.getInstance();
        four.roll(Calendar.DATE, -4);//日期回滚4天
        time[3]=sdf.format(four.getTime())+"";

        Calendar three = Calendar.getInstance();
        three.roll(Calendar.DATE, -3);//日期回滚3天
        time[4]=sdf.format(three.getTime())+"";

        Calendar two = Calendar.getInstance();
        two.roll(Calendar.DATE, -2);//日期回滚2天
        time[5]=sdf.format(two.getTime())+"";

        Calendar one = Calendar.getInstance();
        one.roll(Calendar.DATE, -1);//日期回滚1天
        time[6]=sdf.format(one.getTime())+"";


    }




}
