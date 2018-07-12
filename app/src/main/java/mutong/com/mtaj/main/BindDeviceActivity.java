package mutong.com.mtaj.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import mutong.com.mtaj.R;
import java.util.Map;

import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.Device;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.HttpUtil;

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

        bindDeviceBtn = (Button)findViewById(R.id.bindDeviceButton);
        bindDeviceNum = (EditText) findViewById(R.id.bindDeviceNum);
        bindDeviceName = (EditText)findViewById(R.id.bindDeviceName);

        bindDeviceBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        switch (id)
        {
            case R.id.bindDeviceButton:
                String deviceNum = bindDeviceNum.getText().toString();
                String deviceName = bindDeviceName.getText().toString();

                UserCommonServiceSpi userCommonService = new UserCommonServiceSpi(this);

                user = userCommonService.getLoginUser();

                Map<String, String> map = new ArrayMap<String, String>();
                map.put("userName",user.getPhoneNum());
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
                    JSONObject response = (JSONObject) msg.obj;
                    try
                    {
                        String bloothMac = response.getString("bloothMac");
                        String attachedTime = response.getString("attachedTime");

                        Device device = new Device();

                        device.setAdminName(user.getPhoneNum());
                        device.setAttachedTime(attachedTime);
                        device.setDeviceName(bindDeviceName.getText().toString());
                        device.setDeviceNum(bindDeviceNum.getText().toString());
                        device.setUserName(user.getPhoneNum());
                        device.setDeviceVersion("0");
                        device.setRole(0);
                        device.setBloothMac(bloothMac);

                        UserCommonServiceSpi userCommonService = new UserCommonServiceSpi(BindDeviceActivity.this);
                        //先删除原有的数据
                        userCommonService.deleteDevice(device.getUserName(),device.getDeviceNum());
                        //再插入新数据
                        userCommonService.insertDevice(device);

                        Toast.makeText(BindDeviceActivity.this,"绑定成功", Toast.LENGTH_LONG).show();
                        finish();
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
