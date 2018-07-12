package mutong.com.mtaj.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mutong.com.mtaj.R;
import mutong.com.mtaj.common.Constant;
import mutong.com.mtaj.common.ErrorCode;
import mutong.com.mtaj.common.TerminalInfo;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.HttpUtil;
import mutong.com.mtaj.utils.StatusBarUtil;
import mutong.com.mtaj.utils.StringUtil;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    private TextView rigisterView;
    private TextView phoneNum;
    private TextView loginPwd;
    private Button loginBtn;
    private UserCommonServiceSpi userCommonService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(this,R.color.title);
        //设置状态栏黑色文字
        StatusBarUtil.setBarTextLightMode(this);

        rigisterView = (TextView)findViewById(R.id.newUserRegist);
        phoneNum = (TextView)findViewById(R.id.phone_num) ;
        loginPwd = (TextView)findViewById(R.id.pwd);
        loginBtn = (Button) findViewById(R.id.login_btnLogin);

        rigisterView.setOnClickListener(this);
        loginBtn.setOnClickListener(this);

        userCommonService = new UserCommonServiceSpi(this);
    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            //新用户注册
            case R.id.newUserRegist:
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;

            //用户登录
            case R.id.login_btnLogin:
                String phoneNumStr = phoneNum.getText().toString();
                String pwd = loginPwd.getText().toString();

                //先从本地校验密码
                User user = userCommonService.getLoginUser();
                if(user != null && !StringUtil.isEmpty(user.getPassword()) && !StringUtil.isEmpty(user.getPhoneNum()))
                {
                    //用户名相等，密码不相等
                    if(user.getPhoneNum().equals(phoneNumStr) && !user.getPassword().equals(pwd))
                    {
                        Toast.makeText(LoginActivity.this,"密码错误，请重新输入密码...", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                Map<String,String> map = new HashMap<String,String>();
                map.put("phoneNum",phoneNumStr);
                map.put("password",pwd);

                String url = "/user/login";
                HttpUtil httpUtil = new HttpUtil(handler,this);

                httpUtil.post(map,url);
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
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    try
                    {
                        JSONObject result = jsonObject.getJSONObject("result");
                        String retCode = result.getString("retcode");
                        switch (retCode)
                        {
                            case ErrorCode.SUCEESS:
                                String token = jsonObject.getString("token");
                                String userName = jsonObject.getString("userName");
                                //先删除user_login表中的所有数据，再插入新用户信息
                                userCommonService.deleteDataFromSqlite(Constant.LOGIN_USER_TABLE,null);

                                User user = new User();
                                user.setPhoneNum(phoneNum.getText().toString());
                                user.setPassword(loginPwd.getText().toString());
                                user.setUserToken(token);
                                user.setUserName(userName);

                                userCommonService.insertUser(user);
                                Toast.makeText(LoginActivity.this,"恭喜，您已登录成功", Toast.LENGTH_LONG).show();
                                finish();
                                break;

                            case ErrorCode.USERPHONE_NOT_EXIST:
                                Toast.makeText(LoginActivity.this,"您输入的用户名未注册，请重新输入用户名", Toast.LENGTH_LONG).show();
                                break;

                            case ErrorCode.PASSWORD_ERROR:
                                Toast.makeText(LoginActivity.this,"用户名或密码错误", Toast.LENGTH_LONG).show();
                                break;

                            case ErrorCode.DEFAULT_ERROR:
                                Toast.makeText(LoginActivity.this,"系统正在升级中，请稍后重试", Toast.LENGTH_LONG).show();
                                break;
                        }

                    }
                    catch (JSONException e)
                    {
                        Toast.makeText(LoginActivity.this,"登录失败，请您重新登录", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };
}
