package mutong.com.mtaj.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mutong.com.mtaj.R;
import mutong.com.mtaj.adapter.DeviceInfoAdapter;
import mutong.com.mtaj.adapter.DeviceInfoItem;
import mutong.com.mtaj.adapter.DeviceInfoPicAdapter;
import mutong.com.mtaj.adapter.DeviceInfoPicItem;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.Device;
import mutong.com.mtaj.utils.StatusBarUtil;

public class DeviceInfoActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener
{
    private ListView deviceInfoList;
    private ListView picList;

    private List<DeviceInfoItem> list = new ArrayList<DeviceInfoItem>();
    private List<DeviceInfoPicItem> listPic = new ArrayList<DeviceInfoPicItem>();

    private String deviceNum;
    private UserCommonServiceSpi userCommonService;

    private ImageView back;
    private TextView backText;
    private TextView addUser;
    private String deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_info);

        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(this,R.color.title);
        //设置状态栏黑色文字
        StatusBarUtil.setBarTextLightMode(this);

        deviceNum = getIntent().getStringExtra("deviceNum");
        userCommonService = new UserCommonServiceSpi(DeviceInfoActivity.this);

        deviceInfoList = (ListView)findViewById(R.id.deviceinfo_list);
        picList = (ListView)findViewById(R.id.listViewPic);

        picList.setOnItemClickListener(this);

        initPicItem();

        back = (ImageView)findViewById(R.id.back);
        backText = (TextView)findViewById(R.id.textView15);
        addUser = (TextView)findViewById(R.id.add_user);

        back.setOnClickListener(this);
        backText.setOnClickListener(this);
        addUser.setOnClickListener(this);

    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        switch (id)
        {
            case R.id.back:
            case R.id.textView15:
                finish();
                break;
            case R.id.add_user:
                //给设备添加用户
                Intent intent = new Intent(this,AddUserDialogActivity.class);
                intent.putExtra("deviceNum",deviceNum);
                startActivity(intent);
                break;
        }
    }

    private void initItems()
    {
        list.clear();

        Device[] devices = userCommonService.queryByDeviceNum(deviceNum);

        if (devices != null && devices.length > 0)
        {
            DeviceInfoItem []deviceInfoItems = new DeviceInfoItem[]{
                    new DeviceInfoItem("设备编号",deviceNum),
                    new DeviceInfoItem("硬件版本",devices[0].getDeviceVersion()),
                    new DeviceInfoItem("蓝牙MAC",devices[0].getBloothMac())};

            for (DeviceInfoItem deviceInfoItem : deviceInfoItems)
            {
                list.add(deviceInfoItem);
            }
        }
        DeviceInfoAdapter adapter = new DeviceInfoAdapter(this,R.layout.deviceinfo_item,list);
        deviceInfoList.setAdapter(adapter);
    }

    private void initPicItem()
    {
        initItems();
        listPic.clear();
        Device[] devices = userCommonService.queryByDeviceNum(deviceNum);

        if (devices != null && devices.length > 0)
        {
            DeviceInfoPicItem []deviceInfoItems = new DeviceInfoPicItem[]{
                    new DeviceInfoPicItem("设备名称",devices[0].getDeviceName(),R.mipmap.forward),
                    new DeviceInfoPicItem("用户数",String.valueOf(devices.length),R.mipmap.forward)};
            deviceName = devices[0].getDeviceName();

            for (DeviceInfoPicItem deviceInfoItem : deviceInfoItems)
            {
                listPic.add(deviceInfoItem);
            }
        }

        DeviceInfoPicAdapter adapter = new DeviceInfoPicAdapter(this,R.layout.deviceinfo_item_pic,listPic);
        picList.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        switch (position)
        {
            case 0:
                //修改设备名称
                Intent modifyNameIntent = new Intent(this,ModifyDeviceNameDialogActivity.class);
                modifyNameIntent.putExtra("deviceName",deviceName);
                modifyNameIntent.putExtra("deviceNum",deviceNum);
                startActivity(modifyNameIntent);
                break;
            case 1:
                //查看设备下的用户详情,用户数
                Intent intent = new Intent(this,ManageUsersActivity.class);
                intent.putExtra("deviceNum",deviceNum);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        initPicItem();
    }

}
