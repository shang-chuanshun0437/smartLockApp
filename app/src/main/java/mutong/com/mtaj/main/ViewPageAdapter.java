package mutong.com.mtaj.main;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2018/4/23.
 */

public class ViewPageAdapter extends PagerAdapter
{
    List<ImageView> viewList;

    public ViewPageAdapter(List<ImageView> viewList)
    {
        this.viewList = viewList;
    }

    //viewPager中显示子视图的个数
    @Override
    public int getCount()
    {
        return viewList.size();
    }

    //判断是否重新生成新的视图
    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    //生成新的视图
    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    //从viewPager中移除视图
    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView(viewList.get(position));
    }
}
