package mutong.com.mtaj.main;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import mutong.com.mtaj.R;

public class MainActivity extends FragmentActivity {
    // 定义FragmentTabHost对象
    private FragmentTabHost mTabHost;
    // 定义一个布局
    private LayoutInflater layoutInflater;
    // 定义一个数组来存放Fragment界面
    private Class fragmentArray[] = { HomePageActivity.class, SelectedPageActivity.class,
            MePageActivity.class };
    // 定义数组来存放按钮图片
    private int mImageViewArray[] = { R.drawable.home,
            R.drawable.tab_recording, R.drawable.tab_me };
    // Tab选项卡的文字
    private String mTextViewArray[] = { "首页", "精选", "我的" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    /****** 初始化组件 *******/
    private void initView() {
        // 实例化布局对象
        layoutInflater = LayoutInflater.from(this);
        // 实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        // 得到fragment的个数
        int count = fragmentArray.length;
        for (int i = 0; i < count; i++) {
            // 为每一个Tab按钮设置图标、文字和内容
            TabSpec tabSpec = mTabHost.newTabSpec(mTextViewArray[i])
                    .setIndicator(getTabItemView(i));
            // 将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            // 设置Tab按钮的背景;
            //mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.selector_tab_background);
        }
    }

    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);
        ImageView imageview = (ImageView) view.findViewById(R.id.tabimage);
        imageview.setImageResource(mImageViewArray[index]);
        TextView textview = (TextView) view.findViewById(R.id.textview);
        textview.setText(mTextViewArray[index]);
        return view;
    }

}