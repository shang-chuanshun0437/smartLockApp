package mutong.com.mtaj.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mutong.com.mtaj.R;
import mutong.com.mtaj.adapter.DeviceItem;
import mutong.com.mtaj.adapter.DeviceItemAdapter;
import mutong.com.mtaj.adapter.SettingAdapter;
import mutong.com.mtaj.adapter.SettingItem;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.Device;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.HttpUtil;
import mutong.com.mtaj.utils.StatusBarUtil;
import mutong.com.mtaj.utils.StringUtil;

public class DeviceActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener
{
    //初始化设备数据
    private List<SettingItem> deviceItems = new ArrayList<SettingItem>();

    private ListView deviceListView;

    private TextView backText;
    private ImageView backImage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device);

        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(this,R.color.title);
        //设置状态栏黑色文字
        StatusBarUtil.setBarTextLightMode(this);

        initItems();

        deviceListView = (ListView)findViewById(R.id.device_listView);
        backImage = (ImageView)findViewById(R.id.device_back);
        backText = (TextView)findViewById(R.id.device_text_back);

        backText.setOnClickListener(this);
        backImage.setOnClickListener(this);
        deviceListView.setOnItemClickListener(this);

        SettingAdapter adapter = new SettingAdapter(this,R.layout.settings_item,deviceItems);
        deviceListView.setAdapter(adapter);
    }

    private void initItems()
    {
        deviceItems.clear();
        SettingItem []tempItems = new SettingItem[]{new SettingItem("管理的设备",R.mipmap.forward),
                new SettingItem("普通设备",R.mipmap.forward)};
        for (SettingItem settingItem : tempItems)
        {
            deviceItems.add(settingItem);
        }
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
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        switch (position)
        {
            case 0:
                Intent intent = new Intent(this,MangerDeviceActivity.class);
                startActivity(intent);
                break;
        }
    }
}
