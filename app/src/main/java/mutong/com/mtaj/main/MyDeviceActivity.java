package mutong.com.mtaj.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import mutong.com.mtaj.R;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.StatusBarUtil;

public class MyDeviceActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener
{
    private UserCommonServiceSpi userCommonService;

    private TextView backText;
    private ImageView backImage;
    private TextView addDevice;

    private TabLayout topTab;
    private ViewPager devicePager;
    String []tabTitles = new String[]{"所有设备","我管理的设备"};
    private Fragment[] fragmentArrays = new Fragment[2];


    private ConstraintLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_device);

        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(this,R.color.mePage);
        //设置状态栏黑色文字
        StatusBarUtil.setBarTextLightMode(this);

        layout = (ConstraintLayout)findViewById(R.id.mydevice);
        layout.setBackgroundResource(R.mipmap.no_device);

        userCommonService = new UserCommonServiceSpi(this);
        User user = userCommonService.getLoginUser();
        if(user == null)
        {
            //还没有登录
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        backImage = (ImageView)findViewById(R.id.device_back);
        backText = (TextView)findViewById(R.id.device_text_back);
        addDevice = (TextView)findViewById(R.id.add_device);
        topTab = (TabLayout)findViewById(R.id.tabLayout);
        devicePager = (ViewPager)findViewById(R.id.viewPager);

        backText.setOnClickListener(this);
        backImage.setOnClickListener(this);
        addDevice.setOnClickListener(this);

        initView();
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        switch (id)
        {
            case R.id.device_back:
            case R.id.device_text_back:
                finish();
                break;

            case R.id.add_device:
                Intent intent = new Intent(this,BindDeviceActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        System.out.println("mydeviceActivity:onResume");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        TextView deviceNumText = view.findViewById(R.id.device_item_numinput);

        Intent intent = new Intent(this,DeviceInfoActivity.class);
        intent.putExtra("deviceNum",deviceNumText.getText().toString());
        startActivity(intent);
    }

    private void initView()
    {
        fragmentArrays[0] = DeviceUserTabFragment.newInstance();
        fragmentArrays[1] = DeviceManagerTabFragment.newInstance();
        //设置tablayout距离上下左右的距离
        //tab_title.setPadding(20,20,20,20);
        PagerAdapter pagerAdapter = new DevicePagerAdapter(getSupportFragmentManager());
        devicePager.setAdapter(pagerAdapter);
        //将ViewPager和TabLayout绑定
        topTab.setupWithViewPager(devicePager);
    }

    final class DevicePagerAdapter extends FragmentPagerAdapter {
        public DevicePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentArrays[position];
        }


        @Override
        public int getCount() {
            return fragmentArrays.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

}
