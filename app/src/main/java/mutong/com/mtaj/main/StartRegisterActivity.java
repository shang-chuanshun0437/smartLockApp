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
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.CacheActivity;
import mutong.com.mtaj.utils.HttpUtil;
import mutong.com.mtaj.utils.StringUtil;

/**
 * Created by Administrator on 2018/5/28.
 */

public class StartRegisterActivity extends AppCompatActivity implements View.OnClickListener
{
    private EditText pwdInput;
    private Button registerBtn;
    private String phoneNum;
    private String voucher;
    private TextView phoneNumText;
    private EditText confirmPwd;
    private EditText nickName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_register);

        phoneNum = getIntent().getStringExtra("phoneNum");
        voucher = getIntent().getStringExtra("voucher");

        pwdInput = (EditText)findViewById(R.id.pwdEditText);
        confirmPwd = (EditText)findViewById(R.id.confir_pwd);
        registerBtn = (Button)findViewById(R.id.register_button);
        phoneNumText = (TextView)findViewById(R.id.phoneNum);
        nickName = (EditText)findViewById(R.id.nickName);

        phoneNumText.setText(phoneNum);

        registerBtn.setOnClickListener(this);
        //EditText获取焦点并显示软键盘
        pwdInput.setFocusable(true);
        pwdInput.setFocusableInTouchMode(true);
        pwdInput.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.register_button:
                //校验密码和昵称
                if(checkPwdNickname())
                {
                    //发起注册请求
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("phoneNum",phoneNum.replace(" ",""));
                    map.put("userName",nickName.getText().toString());
                    map.put("password",pwdInput.getText().toString());
                    map.put("voucher",voucher);

                    HttpUtil httpUtil = new HttpUtil(handler,this);
                    httpUtil.post(map,"/user/addUser");
                }
                break;
        }
    }

    private boolean checkPwdNickname()
    {
        String pwdStr = pwdInput.getText().toString();
        String confirmStr = confirmPwd.getText().toString();
        String nickNameStr = nickName.getText().toString();
        if(StringUtil.isEmpty(pwdStr) || StringUtil.isEmpty(confirmStr) || StringUtil.isEmpty(nickNameStr))
        {
            Toast.makeText(this,"输入有误，不能为空",Toast.LENGTH_LONG).show();
            return false;
        }

        if(pwdStr.length() > 48 || pwdStr.length() < 8)
        {
            Toast.makeText(this,"密码长度输入有误，请输入8~48位的密码",Toast.LENGTH_LONG).show();
            return false;
        }

        if (!pwdStr.equals(confirmStr))
        {
            Toast.makeText(this,"确认密码有误，请重新输入",Toast.LENGTH_LONG).show();
            return false;
        }

        if(nickNameStr.length() > 10)
        {
            Toast.makeText(this,"昵称不要起的太长哦，请输入1~10位的昵称",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    try
                    {
                        JSONObject result = jsonObject.getJSONObject("result");
                        String retCode = result.getString("retcode");
                        switch (retCode)
                        {
                            case ErrorCode.SUCEESS:
                                Toast.makeText(StartRegisterActivity.this,"恭喜，您已注册成功", Toast.LENGTH_LONG).show();
                                CacheActivity.finishActivity();
                                finish();
                                break;

                            case ErrorCode.VERIFY_VOUCHER_ERROR:
                                Toast.makeText(StartRegisterActivity.this,"短信验证码已失效，请重新注册", Toast.LENGTH_LONG).show();
                                finish();
                                break;

                            case ErrorCode.DEFAULT_ERROR:
                                Toast.makeText(StartRegisterActivity.this,"服务器正在升级中，请稍后重试", Toast.LENGTH_LONG).show();
                                finish();
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
