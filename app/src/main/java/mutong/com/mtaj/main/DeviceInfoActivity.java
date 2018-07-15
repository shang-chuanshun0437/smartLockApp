package mutong.com.mtaj.main;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mutong.com.mtaj.R;
import mutong.com.mtaj.adapter.DeviceInfoAdapter;
import mutong.com.mtaj.adapter.DeviceInfoItem;
import mutong.com.mtaj.adapter.DeviceInfoPicAdapter;
import mutong.com.mtaj.adapter.DeviceInfoPicItem;
import mutong.com.mtaj.common.Constant;
import mutong.com.mtaj.common.ErrorCode;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.Device;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.CustomDialog;
import mutong.com.mtaj.utils.HttpUtil;
import mutong.com.mtaj.utils.ScreenSizeUtil;
import mutong.com.mtaj.utils.SpaceTextWatcher;
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

        initItems();
        DeviceInfoAdapter adapter = new DeviceInfoAdapter(this,R.layout.deviceinfo_item,list);
        deviceInfoList.setAdapter(adapter);

        initPicItem();

        back = (ImageView)findViewById(R.id.back);
        backText = (TextView)findViewById(R.id.textView15);
        addUser = (TextView)findViewById(R.id.help);

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
            case R.id.help:
                CustomDialog customDialog = new CustomDialog(this,R.layout.dialog_adduser,handlerAddUser,deviceNum);
                customDialog.showDialog();
                break;
        }
    }

    private void initItems()
    {
        list.clear();

        Device[] devices = userCommonService.queryByDeviceNum(deviceNum);

        DeviceInfoItem []deviceInfoItems = new DeviceInfoItem[]{
                new DeviceInfoItem("设备编号",deviceNum),
                new DeviceInfoItem("硬件版本",devices[0].getDeviceVersion()),
                new DeviceInfoItem("蓝牙MAC",devices[0].getBloothMac())};

        for (DeviceInfoItem deviceInfoItem : deviceInfoItems)
        {
            list.add(deviceInfoItem);
        }
    }

    private void initPicItem()
    {
        listPic.clear();

        Device[] devices = userCommonService.queryByDeviceNum(deviceNum);

        DeviceInfoPicItem []deviceInfoItems = new DeviceInfoPicItem[]{
                new DeviceInfoPicItem("设备名称",devices[0].getDeviceName(),R.mipmap.forward),
                new DeviceInfoPicItem("用户数",String.valueOf(devices.length),R.mipmap.forward)};

        for (DeviceInfoPicItem deviceInfoItem : deviceInfoItems)
        {
            listPic.add(deviceInfoItem);
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
                CustomDialog customDialog = new CustomDialog(this,R.layout.dialog_normal,handler,deviceNum);
                customDialog.showDialog();
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

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    try
                    {
                        JSONObject result = jsonObject.getJSONObject("result");
                        String retCode = result.getString("retcode");
                        switch (retCode)
                        {
                            case ErrorCode.SUCEESS:
                                String deviceName = jsonObject.getString("deviceName");
                                //写入sqlite
                                Device [] devices = userCommonService.queryByDeviceNum(deviceNum);
                                if (devices != null && devices.length > 0)
                                {
                                    devices[0].setDeviceName(deviceName);
                                    userCommonService.updateDevice(devices[0]);
                                }
                                initPicItem();
                                Toast.makeText(DeviceInfoActivity.this,"恭喜，设备名称修改成功",Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                    catch (JSONException e)
                    {

                    }
            }
        }
    };

    private Handler handlerAddUser = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    try
                    {
                        JSONObject result = jsonObject.getJSONObject("result");
                        String retCode = result.getString("retcode");
                        switch (retCode)
                        {
                            case ErrorCode.SUCEESS:
                                JSONObject userAttachedDevice = jsonObject.getJSONObject("userAttachedDevice");
                                //写入sqlite
                                Device device = new Device();

                                device.setUserName(userAttachedDevice.getString("userName"));
                                device.setPhoneNum(userAttachedDevice.getString("phoneNum"));
                                device.setAdminName(userAttachedDevice.getString("mainName"));
                                device.setDeviceNum(userAttachedDevice.getString("deviceNum"));
                                device.setDeviceName(userAttachedDevice.getString("deviceName"));
                                device.setBloothMac(userAttachedDevice.getString("bluetoothMac"));
                                device.setDeviceVersion(userAttachedDevice.getString("version"));
                                device.setRole(userAttachedDevice.getString("userType"));
                                device.setAttachedTime(userAttachedDevice.getString("associateTime"));
                                device.setValidDate(userAttachedDevice.getString("validDate"));

                                //先删再插入数据
                                userCommonService.deleteDevice(device.getPhoneNum(),device.getDeviceNum());
                                userCommonService.insertDevice(device);
                                initPicItem();
                                Toast.makeText(DeviceInfoActivity.this,"恭喜，添加用户成功",Toast.LENGTH_LONG).show();
                                break;

                            case ErrorCode.MAIN_USER_MISSMATCH:
                                Toast.makeText(DeviceInfoActivity.this,"您的账号不具备管理员权限",Toast.LENGTH_LONG).show();
                                break;

                            case ErrorCode.USERPHONE_NOT_EXIST:
                                Toast.makeText(DeviceInfoActivity.this,"您添加的用户还没有注册",Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                    catch (JSONException e)
                    {

                    }
            }
        }
    };
}
