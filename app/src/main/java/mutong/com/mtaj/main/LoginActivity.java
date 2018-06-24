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
import mutong.com.mtaj.common.TerminalInfo;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.HttpUtil;
import mutong.com.mtaj.utils.StringUtil;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    private TextView rigisterView;
    private TextView userNameView;
    private TextView loginPwd;
    private Button loginBtn;
    private UserCommonServiceSpi userCommonService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        rigisterView = (TextView)findViewById(R.id.newUserRegist);
        userNameView = (TextView)findViewById(R.id.login_username) ;
        loginPwd = (TextView)findViewById(R.id.login_pwd);
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
                System.out.println("login_btnLogin:" + userNameView.getText().toString());
                String userName = userNameView.getText().toString();
                String pwd = loginPwd.getText().toString();

                //为了减少与服务器的交互次数，先从本地校验密码
                User user = userCommonService.getLoginUser();
                if(user != null && !StringUtil.isEmpty(user.getPassword()) && !StringUtil.isEmpty(user.getUserName()))
                {
                    //用户名相等，密码不相等
                    if(user.getUserName().equals(userName) && !user.getPassword().equals(pwd))
                    {
                        Toast.makeText(LoginActivity.this,"密码错误，请重新输入密码...", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                TerminalInfo terminalInfo = new TerminalInfo(this);
                String terminalId = terminalInfo.getTerminalId();
                Map<String,String> map = new HashMap<String,String>();
                map.put("userName",userName);
                map.put("password",pwd);
                map.put("terminalId",terminalId);

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
                        String token = jsonObject.getString("token");
                        String refreshToken = jsonObject.getString("refreshToken");

                        //先删除user_login表中的所有数据，再插入新用户信息
                        userCommonService.deleteDataFromSqlite(Constant.LOGIN_USER_TABLE,null);

                        User user = new User();
                        user.setUserName(userNameView.getText().toString());
                        user.setPassword(loginPwd.getText().toString());
                        user.setUserToken(token);
                        user.setRefreshToken(refreshToken);

                        userCommonService.insertUser(user);
                        Toast.makeText(LoginActivity.this,"恭喜，您已登录成功", Toast.LENGTH_LONG).show();
                        finish();
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
