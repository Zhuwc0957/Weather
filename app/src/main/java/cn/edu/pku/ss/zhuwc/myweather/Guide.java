package cn.edu.pku.ss.zhuwc.myweather;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhuWC on 2017/12/3.
 */

public class Guide extends Activity implements ViewPager.OnPageChangeListener {
    private PagerAdapter viewPagerAdapter;
    private ViewPager vp;
    private List<View> views;
    private ImageView[] dots;
    private Button btn;
    private int[] ids={R.id.iv1,R.id.iv2,R.id.iv3,R.id.iv4};
    public void onCreate(Bundle saveInstanceState)
    {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.guide);
        initView();
        initDos();
        btn=(Button)views.get(3).findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Guide.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void initDos()
    {
        dots=new ImageView[views.size()];
        for(int i=0;i<views.size();i++)
        {
            dots[i]=(ImageView)findViewById(ids[i]);
        }
    }
    private void initView()
    {
        LayoutInflater inflater=LayoutInflater.from(this);
        views=new ArrayList<>();
        views.add(inflater.inflate(R.layout.page1,null));
        views.add(inflater.inflate(R.layout.page2,null));
        views.add(inflater.inflate(R.layout.page3,null));
        views.add(inflater.inflate(R.layout.page4,null));
        viewPagerAdapter=new myPagerAdapter(views,this);
        vp=(ViewPager)findViewById(R.id.viewpager);
        vp.setAdapter(viewPagerAdapter);
        vp.setOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for(int i=0;i<ids.length;i++)
        {
            if(i==position)
            {
                dots[i].setImageResource(R.drawable.page_indicator_focused);
            }
            else
            {
                dots[i].setImageResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
