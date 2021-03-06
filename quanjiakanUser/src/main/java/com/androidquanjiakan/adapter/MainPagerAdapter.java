package com.androidquanjiakan.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/1 0001.
 */
public class MainPagerAdapter extends PagerAdapter{

    private List<View> list;

    public MainPagerAdapter(List<View> init){
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
            return
                    list.size();
                    /**
                     * 循环必要部分
                     */
//                    Integer.MAX_VALUE;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(View view, int position, Object object)                       //销毁Item
    {
        ((ViewPager) view).removeView(list.get(position%list.size()));
    }

    @Override
    public Object instantiateItem(View view, int position)                                //实例化Item
    {
//        if(((ViewPager) view).getChildAt(0)!=null){
//            ((ViewPager) view).removeViewAt(0);
//        }
        ((ViewPager) view).addView(list.get(position%list.size()), 0);
        return list.get(position%list.size());
    }
}
