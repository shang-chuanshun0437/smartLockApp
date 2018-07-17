package mutong.com.mtaj.main;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.ArrayMap;
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
import mutong.com.mtaj.utils.HttpUtil;
import mutong.com.mtaj.utils.ScreenSizeUtil;
import mutong.com.mtaj.utils.StringUtil;

public class DeleteUserKeyDialogActivity extends Activity implements View.OnClickListener
{
    private TextView confirmView;
    private TextView cancelView;

    private String deletePhoneNum;
    private String deviceNum;

    private UserCommonServiceSpi userCommonService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delete_userkey);

        //设置对话框大小
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtil.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);

        deviceNum = getIntent().getStringExtra("deviceNum");
        deletePhoneNum = getIntent().getStringExtra("deletePhoneNum");

        if(StringUtil.isEmpty(deviceNum))
        {
            finish();
            return;
        }

        userCommonService = new UserCommonServiceSpi(this);

        confirmView = (TextView)findViewById(R.id.ok);
        cancelView = (TextView)findViewById(R.id.cancel);

        confirmView.setOnClickListener(this);
        cancelView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.ok:
                User user = userCommonService.getLoginUser();

                Map<String,String> map = new ArrayMap<String,String>();
                map.put("phoneNum",user.getPhoneNum());
                map.put("token",user.getUserToken());
                map.put("deletePhoneNum",deletePhoneNum);
                map.put("deviceNum",deviceNum);

                HttpUtil httpUtil = new HttpUtil(handler,this);
                httpUtil.post(map,"/device/deleteDevice");
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
                case 1:
                    try
                    {
                        JSONObject jsonObject = (JSONObject) msg.obj;
                        JSONObject resultObject = jsonObject.getJSONObject("result");
                        String retCode = resultObject.getString("retcode");
                        switch (retCode)
                        {
                            case ErrorCode.SUCEESS:
                                userCommonService.deleteDevice(deletePhoneNum,deviceNum);
                                finish();
                                break;

                            case ErrorCode.OTHER_USERS_EXIST:
                                Toast.makeText(DeleteUserKeyDialogActivity.this,"删除失败：您是设备管理员，该设备下还有其他用户",Toast.LENGTH_LONG).show();
                                finish();
                                break;

                            case ErrorCode.DEFAULT_ERROR:
                                Toast.makeText(DeleteUserKeyDialogActivity.this,"抱歉，服务器正在升级中，请稍后重试",Toast.LENGTH_LONG).show();
                                finish();
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
