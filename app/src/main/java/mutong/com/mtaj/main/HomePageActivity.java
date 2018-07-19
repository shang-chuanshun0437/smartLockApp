package mutong.com.mtaj.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import mutong.com.mtaj.R;
import mutong.com.mtaj.common.AppDownloadManager;
import mutong.com.mtaj.common.Constant;
import mutong.com.mtaj.common.ErrorCode;
import mutong.com.mtaj.common.GridViewRowDivide;
import mutong.com.mtaj.listener.GridViewItemClickListener;
import mutong.com.mtaj.utils.APKVersionUtil;
import mutong.com.mtaj.utils.CacheActivity;
import mutong.com.mtaj.utils.CustomDialog;
import mutong.com.mtaj.utils.HttpUtil;
import mutong.com.mtaj.utils.PermissionUtils;
import mutong.com.mtaj.utils.StatusBarUtil;

public class HomePageActivity extends Fragment
{
    private GridViewRowDivide mainGridView;
    private ViewPager viewPager;
    private View view;
    private AppDownloadManager appDownloadManager;
    private String downloadUrl;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        appDownloadManager = new AppDownloadManager(this.getContext());
        //校验版本号是否需要更新
        checkVersion();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_page, container, false);

        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(this.getActivity(), R.color.white);
        //设置状态栏黑色文字
        StatusBarUtil.setBarTextLightMode(this.getActivity());

        mainGridView = (GridViewRowDivide) view.findViewById(R.id.mainGridView);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);

        //设置主界面
        this.setMainView();
        //设置ViewPager
        this.setViewPager();

        broadcast();

        return view;
    }

    private void setMainView() {
        String[] from = {"icon", "title"};
        int[] to = {R.id.mainViewIcon, R.id.mainViewTitle};

        SimpleAdapter simpleAdapter = new SimpleAdapter(this.getContext(), getData(),
                R.layout.gridview_item, from, to);

        mainGridView.setAdapter(simpleAdapter);
        mainGridView.setOnItemClickListener(new GridViewItemClickListener(this.getContext()));
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> dataMap = new ArrayList<Map<String, Object>>();

        int[] icon = {R.mipmap.opendevice, R.mipmap.talk, R.mipmap.smartlockshow,
                R.mipmap.zufang, R.mipmap.liveservice, R.mipmap.community,};
        String[] title = {"芝麻开门", "客服", "智能锁", "租房", "生活服务", "社区服务",};

        for (int i = 0; i < title.length; i++) {
            Map<String, Object> map = new HashMap<>();

            map.put("icon", icon[i]);
            map.put("title", title[i]);
            dataMap.add(map);
        }
        return dataMap;
    }

    //设置主界面的viewPager
    private void setViewPager() {
        List<ImageView> list = new ArrayList<ImageView>();
        int[] images = new int[]{R.mipmap.banner01, R.mipmap.banner02, R.mipmap.banner03};

        for (int i = 0; i < images.length; i++) {
            ImageView imageView = new ImageView(this.getContext());
            imageView.setImageResource(images[i]);
            list.add(imageView);
        }

        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(list);

        viewPager.setAdapter(viewPageAdapter);

        //viewPager 上的小圆点
        final List<View> dots = new ArrayList<View>();
        dots.add(view.findViewById(R.id.dot_0));
        dots.add(view.findViewById(R.id.dot_1));
        dots.add(view.findViewById(R.id.dot_2));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int oldPostion = 0;

            @Override
            public void onPageSelected(int position) {
                dots.get(position).setBackgroundResource(R.drawable.dot_focused);
                dots.get(oldPostion).setBackgroundResource(R.drawable.dot_normal);
                oldPostion = position;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 利用线程池定时执行viewPager轮播
     */
    private void broadcast() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(
                new Runnable() {
                    @Override
                    public void run() {
                        int nextItem = (viewPager.getCurrentItem() + 1) % 3;
                        handler.sendEmptyMessage(nextItem);
                    }
                },
                2,
                5,
                TimeUnit.SECONDS);
    }

    /**
     * 接收子线程传递过来的数据
     */
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            viewPager.setCurrentItem(msg.what);
        }
    };

    private void checkVersion() {

        String url = "/upgrade";
        HttpUtil httpUtil = new HttpUtil(versionHandler, this.getContext());
        httpUtil.post(new ArrayMap<String, String>(), url);
    }

    private Handler versionHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                //开始下载apk
                case Constant.DOWNLOAD_APK:
                    try
                    {
                        appDownloadManager.downloadApk(downloadUrl,"大管家","wedwd");
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(HomePageActivity.this.getContext(),"更新失败:需要获取存储权限",Toast.LENGTH_LONG).show();
                        //申请存储权限
                        PermissionUtils.requestPermission(HomePageActivity.this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                    break;
                case 1:
                    try {
                        JSONObject response = (JSONObject) msg.obj;
                        JSONObject resultObject = response.getJSONObject("result");
                        String retCode = resultObject.getString("retcode");
                        switch (retCode) {
                            case ErrorCode.SUCEESS:
                                JSONObject upgrade = response.getJSONObject("upgrade");
                                downloadUrl = upgrade.getString("downloadUrl");
                                int versionCode = upgrade.getInt("versionCode");
                                String versionName = upgrade.getString("versionName");
                                String versionDesc = upgrade.getString("versionDesc");

                                if (versionCode > APKVersionUtil.getVersionCode(HomePageActivity.this.getContext()))
                                {
                                    /*Intent intent = new Intent(HomePageActivity.this.getContext(),UpgradDialogActivity.class);
                                    intent.putExtra("downloadUrl",downloadUrl);
                                    intent.putExtra("versionName",versionName);
                                    intent.putExtra("versionDesc",versionDesc);
                                    //目的是，获取存储权限时用到
                                    CacheActivity.addActivity(HomePageActivity.this.getActivity());
                                    startActivity(intent);*/
                                    CustomDialog customDialog = new CustomDialog(HomePageActivity.this.getContext(),
                                            R.layout.dialog_normal,versionHandler,null);
                                    customDialog.showDialog();

                                }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        appDownloadManager.resume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        appDownloadManager.onPause();
    }
}
