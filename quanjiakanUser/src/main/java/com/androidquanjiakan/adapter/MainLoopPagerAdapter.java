package com.androidquanjiakan.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/1 0001.
 */
public class MainLoopPagerAdapter extends PagerAdapter{

    private List<View> list;

    public MainLoopPagerAdapter(List<View> init){
        this.list = init;
    }

    public void initValue(List<View> init){
        list = init;
    }

    public void addValue(List<View> add){
        if(list==null){
            list = new ArrayList<View>();
        }
        list.addAll(add);
    }

    @Override
    public int getCount() {
        if(list==null){
            return 0;
        }else{
            return list.size();
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object)                       //销毁Item
    {
        view.removeView(list.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position)                                //实例化Item
    {
        view.addView(list.get(position));
        return list.get(position%list.size());
    }
}
