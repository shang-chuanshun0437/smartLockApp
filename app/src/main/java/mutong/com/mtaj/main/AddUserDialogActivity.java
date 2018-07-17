package mutong.com.mtaj.main;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mutong.com.mtaj.R;
import mutong.com.mtaj.common.Constant;
import mutong.com.mtaj.common.ErrorCode;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.Device;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.DateUtil;
import mutong.com.mtaj.utils.HttpUtil;
import mutong.com.mtaj.utils.ScreenSizeUtil;
import mutong.com.mtaj.utils.SpaceTextWatcher;
import mutong.com.mtaj.utils.StringUtil;

public class AddUserDialogActivity extends Activity implements View.OnClickListener
{
    private EditText bindPhoneNumText;
    private EditText validDateText;
    private TextView confirmView;
    private TextView cancelView;
    private String deviceNum;

    private UserCommonServiceSpi userCommonService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_adduser);

        //设置对话框大小
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtil.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);

        deviceNum = getIntent().getStringExtra("deviceNum");
        if (StringUtil.isEmpty(deviceNum))
        {
            finish();
            return;
        }
        userCommonService = new UserCommonServiceSpi(this);

        bindPhoneNumText = (EditText)findViewById(R.id.phone);
        validDateText = (EditText)findViewById(R.id.valid_date);
        confirmView = (TextView)findViewById(R.id.ok);
        cancelView = (TextView)findViewById(R.id.cancel);

        bindPhoneNumText.addTextChangedListener(new SpaceTextWatcher(bindPhoneNumText));
        confirmView.setOnClickListener(this);
        cancelView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.ok:
                String phoneStr = bindPhoneNumText.getText().toString().replace(" ","");
                String validDateStr = validDateText.getText().toString();

                //校验输入的日期是否正确
                if(!StringUtil.isEmpty(validDateStr) && !DateUtil.isDataFormat(validDateStr))
                {
                    Toast.makeText(this,"有效期格式输入错误，正确的格式:2020-12-12",Toast.LENGTH_LONG).show();
                    return;
                }
                User user = userCommonService.getLoginUser();

                Map<String, String> map = new HashMap<String, String>();
                map.put("phoneNum",user.getPhoneNum());
                map.put("token",user.getUserToken());
                map.put("deviceNum",deviceNum);
                map.put("bindPhoneNum",phoneStr);
                map.put("validDate",validDateStr);

                HttpUtil httpUtil = new HttpUtil(handlerAddUser,this);
                httpUtil.post(map,"/device/bindDevice4User");
                break;

            case R.id.cancel:
                finish();
                break;
        }

    }

    private Handler handlerAddUser = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case Constant.CONSLE_FAIL:
                    Toast.makeText(AddUserDialogActivity.this,"抱歉，服务器正在升级中，请稍后重试",Toast.LENGTH_LONG).show();
                    finish();
                    break;

                case Constant.CONSLE_SUCCESS:
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
                                Toast.makeText(AddUserDialogActivity.this,"恭喜，添加用户成功",Toast.LENGTH_LONG).show();
                                finish();
                                break;

                            case ErrorCode.MAIN_USER_MISSMATCH:
                                Toast.makeText(AddUserDialogActivity.this,"您的账号不具备管理员权限",Toast.LENGTH_LONG).show();
                                break;

                            case ErrorCode.USERPHONE_NOT_EXIST:
                                Toast.makeText(AddUserDialogActivity.this,"您添加的用户还没有注册",Toast.LENGTH_LONG).show();
                                break;

                            case ErrorCode.DEFAULT_ERROR:
                                Toast.makeText(AddUserDialogActivity.this,"抱歉，服务器正在升级中，请稍后重试",Toast.LENGTH_LONG).show();
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
