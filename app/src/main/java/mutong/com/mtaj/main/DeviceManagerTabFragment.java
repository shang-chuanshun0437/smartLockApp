package mutong.com.mtaj.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import mutong.com.mtaj.common.Constant;
import mutong.com.mtaj.common.ErrorCode;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.Device;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.HttpUtil;
import mutong.com.mtaj.utils.StringUtil;

public class DeviceManagerTabFragment extends Fragment implements View.OnClickListener,AdapterView.OnItemClickListener
{
    //初始化设备数据
    private List<DeviceItem> deviceItems = new ArrayList<DeviceItem>();

    private ListView deviceListView;
    private UserCommonServiceSpi userCommonService;
    private User user;

    public static DeviceManagerTabFragment newInstance()
    {
        DeviceManagerTabFragment fragment = new DeviceManagerTabFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.device_manager_fragment, container, false);
        userCommonService = new UserCommonServiceSpi(this.getContext());
        user = userCommonService.getLoginUser();

        if(user == null)
        {
            //还没有登录
            Intent intent = new Intent(this.getContext(), LoginActivity.class);
            startActivity(intent);
            return view;
        }

        queryDevices();

        deviceListView = (ListView) view.findViewById(R.id.manger_listView);
        deviceListView.setOnItemClickListener(this);

        initDeviceItems(userCommonService.queryDevice());

        return view;
    }

    private void queryDevices()
    {
        HttpUtil httpUtil = new HttpUtil(handler,this.getContext());

        Map<String,String> map = new ArrayMap<String,String>();
        map.put("phoneNum",user.getPhoneNum());
        map.put("token",user.getUserToken());

        String url = "/query/userDevices";

        httpUtil.post(map,url);
    }

    private void initDeviceItems(Device[] devices)
    {
        deviceItems.clear();

        if(devices != null)
        {
            for(Device device : devices)
            {
                //设备的管理者
                if(user.getPhoneNum().equals(device.getPhoneNum()) && !StringUtil.isEmpty(device.getRole()) && device.getRole().equals(Constant.MAIN))
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
        DeviceItemAdapter adapter = new DeviceItemAdapter(this.getContext(),R.layout.device_item,deviceItems);
        deviceListView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        switch (id)
        {
            case R.id.add_device:
                Intent intent = new Intent(this.getContext(),BindDeviceActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        System.out.println("onResume");
        initDeviceItems(userCommonService.queryDevice());
    }

    @Override
    public void onStart()
    {
        super.onStart();
        System.out.println("onStart");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        System.out.println("onPause");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        TextView deviceNumText = view.findViewById(R.id.device_item_numinput);

        Intent intent = new Intent(this.getContext(),DeviceInfoActivity.class);
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
                                        dbDevice.setPhoneNum(device.getString("phoneNum"));
                                        dbDevice.setAdminName(device.getString("mainName"));
                                        dbDevice.setDeviceNum(device.getString("deviceNum"));
                                        dbDevice.setDeviceName(device.getString("deviceName"));
                                        dbDevice.setBloothMac(device.getString("bluetoothMac"));
                                        dbDevice.setDeviceVersion(device.getString("version"));
                                        dbDevice.setRole(device.getString("userType"));
                                        dbDevice.setAttachedTime(device.getString("associateTime"));
                                        dbDevice.setValidDate(device.getString("validDate"));

                                        userCommonService.insertDevice(dbDevice);
                                    }
                                }
                                initDeviceItems(userCommonService.queryDevice());
                                break;

                            case ErrorCode.DEFAULT_ERROR:
                                Toast.makeText(DeviceManagerTabFragment.this.getContext(),"抱歉，服务器正在升级中，请稍后重试",Toast.LENGTH_LONG).show();
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

