package mutong.com.mtaj.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import mutong.com.mtaj.R;
import java.util.Map;

import mutong.com.mtaj.common.Constant;
import mutong.com.mtaj.common.ErrorCode;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.Device;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.HttpUtil;
import mutong.com.mtaj.utils.SpaceTextWatcher;
import mutong.com.mtaj.utils.StatusBarUtil;

public class BindDeviceActivity extends AppCompatActivity implements View.OnClickListener
{
    private Button bindDeviceBtn;
    private EditText bindDeviceNum;
    private EditText bindDeviceName;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bind_device);

        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(this,R.color.title);
        //设置状态栏黑色文字
        StatusBarUtil.setBarTextLightMode(this);


        bindDeviceBtn = (Button)findViewById(R.id.bindDeviceButton);
        bindDeviceNum = (EditText) findViewById(R.id.bindDeviceNum);
        bindDeviceName = (EditText)findViewById(R.id.bindDeviceName);

        bindDeviceNum.addTextChangedListener(new SpaceTextWatcher(bindDeviceNum));
        //EditText获取焦点并显示软键盘
        bindDeviceNum.setFocusable(true);
        bindDeviceNum.setFocusableInTouchMode(true);
        bindDeviceNum.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        bindDeviceBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        switch (id)
        {
            case R.id.bindDeviceButton:
                String deviceNum = bindDeviceNum.getText().toString().replace(" ","");
                String deviceName = bindDeviceName.getText().toString();

                UserCommonServiceSpi userCommonService = new UserCommonServiceSpi(this);

                user = userCommonService.getLoginUser();

                Map<String, String> map = new ArrayMap<String, String>();
                map.put("phoneNum",user.getPhoneNum());
                map.put("token",user.getUserToken());
                map.put("deviceNum",deviceNum);
                map.put("deviceName",deviceName);

                String bindDeviceURL = "/device/bindDevice";

                HttpUtil httpUtil = new HttpUtil(handler,this);
                httpUtil.post(map,bindDeviceURL);

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
                    try
                    {
                        JSONObject jsonObject = (JSONObject) msg.obj;
                        JSONObject resultObject = jsonObject.getJSONObject("result");
                        String retCode = resultObject.getString("retcode");
                        switch (retCode)
                        {
                            case ErrorCode.SUCEESS:
                                String bluetoothMac = jsonObject.getString("bluetoothMac");
                                String attachedTime = jsonObject.getString("attachedTime");
                                String deviceVersion = jsonObject.getString("deviceVersion");

                                Device device = new Device();

                                device.setAdminName(user.getPhoneNum());
                                device.setAttachedTime(attachedTime);
                                device.setDeviceName(bindDeviceName.getText().toString());
                                device.setDeviceNum(bindDeviceNum.getText().toString().replace(" ",""));
                                device.setUserName(user.getPhoneNum());
                                device.setDeviceVersion(deviceVersion);
                                device.setRole(Constant.MAIN);
                                device.setBloothMac(bluetoothMac);

                                UserCommonServiceSpi userCommonService = new UserCommonServiceSpi(BindDeviceActivity.this);
                                //先删除原有的数据
                                userCommonService.deleteDevice(device.getUserName(),device.getDeviceNum());
                                //再插入新数据
                                userCommonService.insertDevice(device);

                                Toast.makeText(BindDeviceActivity.this,"绑定成功", Toast.LENGTH_LONG).show();
                                finish();
                                break;

                            case ErrorCode.DEFAULT_ERROR:
                                Toast.makeText(BindDeviceActivity.this,"抱歉服务器正在升级中，请稍后重试", Toast.LENGTH_LONG).show();
                                break;

                            case ErrorCode.DEVICE_NOT_EXIT:
                            case ErrorCode.MAIN_USER_MISSMATCH:
                                Toast.makeText(BindDeviceActivity.this,"该设备不存在或已被其他用户绑定，请重新输入要绑定的设备编号", Toast.LENGTH_LONG).show();
                                break;

                            case ErrorCode.NOT_LOGIN:
                                Intent intent = new Intent(BindDeviceActivity.this,LoginActivity.class);
                                startActivity(intent);
                                break;
                        }

                    }
                    catch (JSONException e)
                    {
                        Toast.makeText(BindDeviceActivity.this,"绑定失败", Toast.LENGTH_LONG).show();
                    }

                    break;
            }
        }
    };
}
