package mutong.com.mtaj.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import mutong.com.mtaj.common.NetworkService;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.CacheActivity;
import mutong.com.mtaj.utils.HttpUtil;
import mutong.com.mtaj.utils.SpaceTextWatcher;
import mutong.com.mtaj.utils.StatusBarUtil;
import mutong.com.mtaj.utils.StringUtil;

/**
 * Created by Administrator on 2018/5/28.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener
{
    private EditText phoneNumInput;
    private Button nextButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(this,R.color.title);
        //设置状态栏黑色文字
        StatusBarUtil.setBarTextLightMode(this);

        //添加到activity的cache中，目的是注册完成后，结束该activity
        CacheActivity.addActivity(this);

        phoneNumInput = (EditText)findViewById(R.id.phone_num_input);
        nextButton = (Button)findViewById(R.id.next_button);

        phoneNumInput.addTextChangedListener(new SpaceTextWatcher(phoneNumInput));
        nextButton.setOnClickListener(this);
        //EditText获取焦点并显示软键盘
        phoneNumInput.setFocusable(true);
        phoneNumInput.setFocusableInTouchMode(true);
        phoneNumInput.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.next_button:
                String phoneNum = checkPhoneNum();
                if (!StringUtil.isEmpty(phoneNum))
                {
                    //获取验证码
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("phoneNum",phoneNum);

                    HttpUtil httpUtil = new HttpUtil(handler,this);
                    httpUtil.post(map,"/verifyCode/getVerifyCode");
                }
                break;
        }
    }

    private String checkPhoneNum()
    {
        //去空格
        String phoneNum = phoneNumInput.getText().toString().replace(" ","");
        if(StringUtil.isEmpty(phoneNum) || phoneNum.length() != Constant.PHONUM_COUNT)
        {
            Toast.makeText(RegisterActivity.this,
                    "手机号输入有误，请重新输入11位的手机号...", Toast.LENGTH_LONG).show();
            return null;
        }
        return phoneNum;
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    JSONObject jsonObject = (JSONObject)msg.obj;
                    try
                    {
                        JSONObject result = jsonObject.getJSONObject("result");
                        String retCode = result.getString("retcode");
                        switch (retCode)
                        {
                            case ErrorCode.SUCEESS:
                                Intent intent = new Intent(RegisterActivity.this,PhoneNumVerifyCodeActivity.class);
                                intent.putExtra("phoneNum",phoneNumInput.getText().toString());
                                startActivity(intent);
                                break;

                            case ErrorCode.USERNAME_EXIST:
                                Toast.makeText(RegisterActivity.this,"该手机号已注册", Toast.LENGTH_LONG).show();
                                break;

                            case ErrorCode.DEFAULT_ERROR:
                                Toast.makeText(RegisterActivity.this,"服务器正在升级中，请稍后重试", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
            }
        }
    };
}
