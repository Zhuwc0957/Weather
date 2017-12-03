package cn.edu.pku.ss.zhuwc.myweather;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Objects;

/**
 * Created by ZhuWC on 2017/12/3.
 */

public class myPagerAdapter extends PagerAdapter {
    private List<View> views;
    private Context context;

    public myPagerAdapter(List<View> views,Context context)
    {
        this.views=views;
        this.context=context;
    }
    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container,int position)
    {
        container.addView(views.get(position));
        return views.get(position);
    }

    public void destroyItem(ViewGroup container,int position,Object object)
    {
        container.removeView(views.get(position));
    }
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view==object);
    }
}
