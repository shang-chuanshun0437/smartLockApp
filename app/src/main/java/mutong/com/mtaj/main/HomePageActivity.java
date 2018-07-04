package mutong.com.mtaj.main;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import mutong.com.mtaj.R;
import mutong.com.mtaj.common.GridViewRowDivide;
import mutong.com.mtaj.listener.GridViewItemClickListener;

public class HomePageActivity extends Fragment
{
    private GridViewRowDivide mainGridView;
    private ViewPager viewPager;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_page, container, false);
        mainGridView = (GridViewRowDivide) view.findViewById(R.id.mainGridView);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        //设置主界面
        this.setMainView();
        //设置ViewPager
        this.setViewPager();

        broadcast();
        return view;
    }

    private void setMainView()
    {
        String []from = {"icon","title"};
        int []to = {R.id.mainViewIcon,R.id.mainViewTitle};

        SimpleAdapter simpleAdapter = new SimpleAdapter(this.getContext(),getData(),
                R.layout.gridview_item, from,to);

        mainGridView.setAdapter(simpleAdapter);
        mainGridView.setOnItemClickListener(new GridViewItemClickListener(this.getContext()));
    }

    private List<Map<String,Object>>  getData()
    {
        List<Map<String,Object>> dataMap = new ArrayList<Map<String,Object>>();

        int []icon = {R.mipmap.opendevice,R.mipmap.talk,R.mipmap.smartlockshow,
                       R.mipmap.zufang,R.mipmap.liveservice, R.mipmap.community,};
        String []title = {"芝麻开门","客服","智能锁", "租房","生活服务","社区服务",};

        for (int i = 0;i < title.length;i++)
        {
            Map<String,Object> map = new HashMap<>();

            map.put("icon",icon[i]);
            map.put("title",title[i]);
            dataMap.add(map);
        }
        return dataMap;
    }

    //设置主界面的viewPager
    private void setViewPager()
    {
        List<ImageView> list = new ArrayList<ImageView>();
        int [] images = new int[]{R.mipmap.banner01,R.mipmap.banner02,R.mipmap.banner03};

        for (int i = 0;i < images.length;i++)
        {
            ImageView imageView = new ImageView(this.getContext());
            imageView.setBackgroundResource(images[i]);
            list.add(imageView);
        }

        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(list);

        viewPager.setAdapter(viewPageAdapter);

        //viewPager 上的小圆点
        final List<View> dots = new ArrayList<View>();
        dots.add(view.findViewById(R.id.dot_0));
        dots.add(view.findViewById(R.id.dot_1));
        dots.add(view.findViewById(R.id.dot_2));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            int oldPostion = 0;
            @Override
            public void onPageSelected(int position)
            {
                dots.get(position).setBackgroundResource(R.drawable.dot_focused);
                dots.get(oldPostion).setBackgroundResource(R.drawable.dot_normal);
                oldPostion = position;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
    }

    /**
     * 利用线程池定时执行viewPager轮播
     */
    private void broadcast()
    {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(
                new Runnable() {
                    @Override
                    public void run()
                    {
                        int nextItem = (viewPager.getCurrentItem() + 1) % 3;
                        handler.sendEmptyMessage(nextItem);
                    }
                },
                2,
                5,
                TimeUnit.SECONDS);
    }

    /*@Override
    public void onStart()
    {
        super.onStart();
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(
                new Runnable() {
                    @Override
                    public void run()
                    {
                        int nextItem = (viewPager.getCurrentItem() + 1) % 3;
                        handler.sendEmptyMessage(nextItem);
                    }
                },
                2,
                5,
                TimeUnit.SECONDS);
    }*/

    /**
     * 接收子线程传递过来的数据
     */
    private Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            viewPager.setCurrentItem(msg.what);
        }
    };
}
