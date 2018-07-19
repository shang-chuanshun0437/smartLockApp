package mutong.com.mtaj.main;

import android.content.Context;
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
import mutong.com.mtaj.adapter.DeviceUsersAdapter;
import mutong.com.mtaj.adapter.DeviceUsersItem;
import mutong.com.mtaj.common.Constant;
import mutong.com.mtaj.common.ErrorCode;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.Device;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.DateUtil;
import mutong.com.mtaj.utils.HttpUtil;
import mutong.com.mtaj.utils.StringUtil;

public class DeviceUserTabFragment extends Fragment
{
    //初始化设备数据
    private List<DeviceUsersItem> list = new ArrayList<DeviceUsersItem>();
    private Context context;

    private ListView deviceListView;
    private UserCommonServiceSpi userCommonService;
    private User user;
    private Device device;

    public static DeviceUserTabFragment newInstance()
    {
        DeviceUserTabFragment fragment = new DeviceUserTabFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.device_manager_fragment, container, false);
        userCommonService = new UserCommonServiceSpi(this.getContext());
        user = userCommonService.getLoginUser();
        context = this.getContext();

        if(user == null)
        {
            //还没有登录
            Intent intent = new Intent(this.getContext(), LoginActivity.class);
            startActivity(intent);
            return view;
        }

        queryDevices();

        deviceListView = (ListView) view.findViewById(R.id.manger_listView);

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

    @Override
    public void onResume()
    {
        super.onResume();
        initDeviceItems(userCommonService.queryDevice());
    }

    private void initDeviceItems(Device [] devices)
    {
        list.clear();
        if (devices != null && devices.length > 0)
        {
            deviceListView.setBackgroundResource(R.color.white);
            for (Device device : devices)
            {
                if(user.getPhoneNum().equals(device.getPhoneNum()))
                {
                    DeviceUsersItem deviceUsersItem = new DeviceUsersItem();
                    int imgId;

                    if(!StringUtil.isEmpty(device.getRole()) && device.getRole().equals(Constant.OTHER))
                    {
                        imgId = StringUtil.isEmpty(device.getValidDate()) ? R.mipmap.userever : R.mipmap.uservalid;
                    }
                    else
                    {
                        imgId = R.mipmap.evcer;
                    }

                    String validate = StringUtil.isEmpty(device.getValidDate()) || device.getValidDate().equals("null")
                            ? "永久有效" : device.getValidDate();

                    deviceUsersItem.setImgId(imgId);
                    deviceUsersItem.setPhoneNum(device.getDeviceName());
                    deviceUsersItem.setNickName(DateUtil.dateTodate(device.getAttachedTime().substring(0,12)));
                    deviceUsersItem.setValidDate(validate);
                    list.add(deviceUsersItem);
                }
            }
        }
        else
        {
            deviceListView.setBackgroundResource(R.mipmap.no_device);
        }

        DeviceUsersAdapter adapter = new DeviceUsersAdapter(this.getContext(),R.layout.device_all_item,list,null);
        deviceListView.setAdapter(adapter);
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
                                Toast.makeText(DeviceUserTabFragment.this.getContext(),"抱歉，服务器正在升级中，请稍后重试",Toast.LENGTH_LONG).show();
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

