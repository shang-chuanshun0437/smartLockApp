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

public class ModifyDeviceNameDialogActivity extends Activity implements View.OnClickListener
{
    private EditText deviceNameEdit;
    private TextView confirmView;
    private TextView cancelView;
    private String deviceName;
    private String deviceNum;

    private UserCommonServiceSpi userCommonService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_modify_devicename);

        //设置对话框大小
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtil.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);

        deviceName = getIntent().getStringExtra("deviceName");
        deviceNum = getIntent().getStringExtra("deviceNum");
        if(StringUtil.isEmpty(deviceNum))
        {
            finish();
            return;
        }

        userCommonService = new UserCommonServiceSpi(this);

        deviceNameEdit = (EditText)findViewById(R.id.device_name);
        confirmView = (TextView)findViewById(R.id.ok);
        cancelView = (TextView)findViewById(R.id.cancel);

        deviceNameEdit.setText(deviceName);
        confirmView.setOnClickListener(this);
        cancelView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.ok:

                String deviceName = deviceNameEdit.getText().toString();
                User user = userCommonService.getLoginUser();

                Map<String, String> map = new HashMap<String, String>();
                map.put("phoneNum",user.getPhoneNum());
                map.put("token",user.getUserToken());
                map.put("deviceNum",deviceNum);
                map.put("deviceName",deviceName);

                HttpUtil httpUtil = new HttpUtil(handler,this);
                httpUtil.post(map,"/modify/modifyDeviceName");
                break;

            case R.id.cancel:
                finish();
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
                case Constant.CONSLE_FAIL:
                    Toast.makeText(ModifyDeviceNameDialogActivity.this,"抱歉，服务器正在升级中，请稍后重试",Toast.LENGTH_LONG).show();
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
                                String deviceName = jsonObject.getString("deviceName");
                                //写入sqlite
                                Device [] devices = userCommonService.queryByDeviceNum(deviceNum);
                                if (devices != null && devices.length > 0)
                                {
                                    devices[0].setDeviceName(deviceName);
                                    userCommonService.updateDevice(devices[0]);
                                }
                                Toast.makeText(ModifyDeviceNameDialogActivity.this,"恭喜，设备名称修改成功",Toast.LENGTH_LONG).show();
                                finish();
                                break;

                            case ErrorCode.DEFAULT_ERROR:
                                Toast.makeText(ModifyDeviceNameDialogActivity.this,"抱歉，服务器正在升级中，请稍后重试",Toast.LENGTH_LONG).show();
                                finish();
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
