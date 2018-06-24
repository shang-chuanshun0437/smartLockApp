package mutong.com.mtaj.listener;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.ArrayMap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import mutong.com.mtaj.common.Constant;
import mutong.com.mtaj.common.ErrorCode;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.main.LoginActivity;
import mutong.com.mtaj.main.MainActivity;
import mutong.com.mtaj.main.OpenDoorActivity;
import mutong.com.mtaj.repository.Device;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.HttpUtil;

public class GridViewItemClickListener implements AdapterView.OnItemClickListener
{
    private Context context;

    private UserCommonServiceSpi userCommonService;

    private HttpUtil httpUtil;

    public GridViewItemClickListener(Context context)
    {
        this.context = context;
        userCommonService = new UserCommonServiceSpi(context);
        httpUtil = new HttpUtil(handler,context);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        switch (i)
        {
            case 0://芝麻开门
                //获取用户下的设备，如果用户还没登录，则跳转到登录界面
                User user = userCommonService.getLoginUser();
                if(user == null)
                {
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                }
                else
                {
                    //向后台发送获取用户设备的请求
                    Map<String,String> map = new ArrayMap<String,String>();
                    map.put("userName",user.getUserName());
                    map.put("token",user.getUserToken());

                    String url = "/query/userDevices";

                    httpUtil.post(map,url);

                }
                break;

            case 1://客服
                break;
            case 2://智能锁
                break;
            case 3://租房
                break;
            case 4://生活服务
                break;
            case 5://社区服务
                break;
            default:
                break;
        }
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == Constant.CONSLE_SUCCESS)
            {
                JSONObject jsonObject = (JSONObject)msg.obj;
                try
                {
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
                                    dbDevice.setRole(Integer.valueOf(device.getString("userType")));
                                    userCommonService.insertDevice(dbDevice);
                                }
                            }

                            Intent intent = new Intent(context, OpenDoorActivity.class);
                            context.startActivity(intent);
                            break;

                        case ErrorCode.NOT_LOGIN:
                            Intent intentLogin = new Intent(context, LoginActivity.class);
                            context.startActivity(intentLogin);
                            break;
                    }
                }
                catch (JSONException e)
                {

                }
            }
            else if(msg.what == Constant.CONSLE_FAIL)
            {
                Intent intent = new Intent(context, OpenDoorActivity.class);
                context.startActivity(intent);
            }
        }
    };
}