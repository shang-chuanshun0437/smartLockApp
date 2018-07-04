package mutong.com.mtaj.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.view.View;
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
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.Device;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.HttpUtil;
import mutong.com.mtaj.utils.StringUtil;

public class DeviceActivity extends AppCompatActivity implements View.OnClickListener
{
    //初始化设备数据
    private List<DeviceItem> deviceItems = new ArrayList<DeviceItem>();

    private ListView deviceListView;

    private ImageView deviceAdd;

    private ImageView deviceUser;

    private ImageView backImage;

    private JSONObject jsonObject = null;

    private  UserCommonServiceSpi userCommonService = new UserCommonServiceSpi(this);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device);

        deviceListView = (ListView)findViewById(R.id.device_listView);
        deviceAdd = (ImageView) findViewById(R.id.add_divice);
        deviceUser = (ImageView) findViewById(R.id.add_user);
        backImage = (ImageView)findViewById(R.id.deviceback);

        deviceAdd.setOnClickListener(this);
        deviceUser.setOnClickListener(this);
        backImage.setOnClickListener(this);

        //Todo 先从服务器中获取设备列表
        User user = userCommonService.getLoginUser();
        if(user == null)
        {
            //还没有登录
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            HttpUtil httpUtil = new HttpUtil(handler,this);

            Map<String,String> map = new ArrayMap<String,String>();
            map.put("userName",user.getUserName());
            map.put("token",user.getUserToken());

            String url = "/query/userDevices";

            httpUtil.post(map,url);
        }
        //将设备显示在界面上
        initDeviceItems(userCommonService.queryDevice());

        DeviceItemAdapter adapter = new DeviceItemAdapter(this,R.layout.device_item,deviceItems);
        deviceListView.setAdapter(adapter);
    }

    private void initDeviceItems(Device[] devices)
    {
        for(Device device : devices)
        {
            DeviceItem deviceItem = new DeviceItem();

            deviceItem.setImgId(R.mipmap.device_small);
            deviceItem.setDeviceName(device.getDeviceName());
            deviceItem.setDeviceNum(device.getDeviceNum());
            deviceItem.setUserName(device.getUserName());
            deviceItem.setAdminName(device.getAdminName());

            deviceItems.add(deviceItem);
        }
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        switch (id)
        {
            case R.id.deviceback:
                finish();
                break;
            //绑定设备
            case R.id.add_divice:
                Intent intent = new Intent(this, BindDeviceActivity.class);
                startActivity(intent);
                break;
            //绑定用户
            case R.id.add_user:
                break;
        }
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    jsonObject = (JSONObject)msg.obj;
                    try
                    {
                        JSONArray devices = jsonObject.getJSONArray("userAttachedDevices");
                        //先把sqlite device_user表清空
                        userCommonService.deleteDataFromSqlite("device_user",null);
                        //将请求中的数据写入sqlite device_user表
                        if(devices != null)
                        {
                            for(int i = 0;i < devices.length();i++)
                            {
                                JSONObject device = devices.getJSONObject(i);

                                Device dbDevice = new Device();
                                dbDevice.setUserName(device.getString("userName"));
                                dbDevice.setAdminName(device.getString("mainName"));
                                dbDevice.setDeviceNum(device.getString("deviceNum"));
                                dbDevice.setDeviceName(device.getString("deviceName"));
                                dbDevice.setBloothMac(device.getString("bloothMac"));
                                dbDevice.setDeviceVersion(device.getString("version"));
                                dbDevice.setRole(Integer.valueOf(device.getString("userType")));
                                userCommonService.insertDevice(dbDevice);
                            }
                        }
                    }
                    catch (JSONException e)
                    {

                    }

                    break;
            }
        }
    };
}
