package mutong.com.mtaj.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import mutong.com.mtaj.common.Constant;
import mutong.com.mtaj.common.ErrorCode;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.Device;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.HttpUtil;
import mutong.com.mtaj.utils.StatusBarUtil;
import mutong.com.mtaj.utils.StringUtil;

public class MangerDeviceActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener
{
    //初始化设备数据
    private List<DeviceItem> deviceItems = new ArrayList<DeviceItem>();

    private ListView deviceListView;
    private UserCommonServiceSpi userCommonService;
    private User user;

    private TextView backText;
    private ImageView backImage;
    private TextView addDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manger_device);

        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(this,R.color.title);
        //设置状态栏黑色文字
        StatusBarUtil.setBarTextLightMode(this);

        userCommonService = new UserCommonServiceSpi(this);
        user = userCommonService.getLoginUser();
        if(user == null)
        {
            //还没有登录
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        queryDevices();
        initDeviceItems(userCommonService.queryDevice());
        deviceListView = (ListView)findViewById(R.id.manger_listView);
        backImage = (ImageView)findViewById(R.id.device_back);
        backText = (TextView)findViewById(R.id.device_text_back);
        addDevice = (TextView)findViewById(R.id.add_device);

        backText.setOnClickListener(this);
        backImage.setOnClickListener(this);
        deviceListView.setOnItemClickListener(this);
        addDevice.setOnClickListener(this);

        DeviceItemAdapter adapter = new DeviceItemAdapter(this,R.layout.device_item,deviceItems);
        deviceListView.setAdapter(adapter);
    }

    private void queryDevices()
    {
        HttpUtil httpUtil = new HttpUtil(handler,this);

        Map<String,String> map = new ArrayMap<String,String>();
        map.put("phoneNum",user.getPhoneNum());
        map.put("token",user.getUserToken());

        String url = "/query/userDevices";

        httpUtil.post(map,url);
    }

    private void initDeviceItems(Device[] devices)
    {
        for(Device device : devices)
        {
            //设备的管理者
            if(user.getPhoneNum().equals(device.getUserName()) && !StringUtil.isEmpty(device.getRole()) && device.getRole().equals(Constant.MAIN))
            {
                DeviceItem deviceItem = new DeviceItem();

                deviceItem.setImgId(R.mipmap.device_small);
                deviceItem.setDeviceName(device.getDeviceName());
                deviceItem.setDeviceNum(device.getDeviceNum());
                deviceItem.setUserNum(String.valueOf(userCommonService.queryByDeviceNum(device.getDeviceNum()).length));
                deviceItems.add(deviceItem);
            }
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

            case R.id.add_device:
                Intent intent = new Intent(this,BindDeviceActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        TextView deviceNumText = view.findViewById(R.id.device_item_numinput);

        Intent intent = new Intent(this,DeviceInfoActivity.class);
        intent.putExtra("deviceNum",deviceNumText.getText().toString());
        startActivity(intent);
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    try
                    {
                        JSONObject jsonObject = (JSONObject) msg.obj;
                        JSONObject resultObject = jsonObject.getJSONObject("result");
                        String retCode = resultObject.getString("retcode");
                        switch (retCode)
                        {
                            case ErrorCode.SUCEESS:
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
                                        dbDevice.setRole(device.getString("userType"));
                                        userCommonService.insertDevice(dbDevice);
                                    }
                                }
                                break;

                            case ErrorCode.DEFAULT_ERROR:
                                Toast.makeText(MangerDeviceActivity.this,"抱歉，服务器正在升级中，请稍后重试",Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                    catch (JSONException e)
                    {

                    }

                    break;

                case 0 :
                    break;
            }
        }
    };
}
