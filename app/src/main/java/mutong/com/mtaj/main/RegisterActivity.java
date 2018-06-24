package mutong.com.mtaj.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import mutong.com.mtaj.R;
import mutong.com.mtaj.common.Constant;
import mutong.com.mtaj.common.NetworkService;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.HttpUtil;
import mutong.com.mtaj.utils.StringUtil;

/**
 * Created by Administrator on 2018/5/28.
 */

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener
{
    private EditText phoneNum;
    private EditText pwd;
    private EditText verify;
    private TextView query_verify;
    private Button registBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        phoneNum = (EditText)findViewById(R.id.regist_name);
        pwd = (EditText)findViewById(R.id.register_pwd);
        verify = (EditText)findViewById(R.id.verify);
        query_verify = (TextView) findViewById(R.id.query_verify);
        registBtn = (Button)findViewById(R.id.registBtn);

        query_verify.setOnClickListener(this);
        registBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.query_verify:
                System.out.println(R.id.query_verify);
                break;

            case R.id.registBtn:
                if( !NetworkService.isNetworkOpen(this))
                {
                    return;
                }
                regist();
                break;
        }
    }
    //和后台服务器数据交互完成注册
    private void regist()
    {
        //Todo 对二维码的处理稍后再做

        //校验用户名和密码是否合法
        if( !checkUserNameAndPwd())
        {
            return;
        }
        //请求服务器进行注册
        Map<String, String> map = new HashMap<String, String>();
        map.put("userName",phoneNum.getText().toString());
        map.put("password",pwd.getText().toString());

        HttpUtil httpUtil = new HttpUtil(handler,this);
        httpUtil.post(map,"user/addUser");

        /*RequestQueue mQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,Constant.URL_PREFIX + "user/addUser",
                new JSONObject(map),
                new URLResponseListener(this,handler), new URLErrorResponseListener(this));

        mQueue.add(jsonObjectRequest);*/

    }

    private boolean checkUserNameAndPwd()
    {
        String userName = phoneNum.getText().toString();
        String password = pwd.getText().toString();

        if(StringUtil.isEmpty(userName) || userName.length() != Constant.PHONUM_COUNT)
        {
            Toast.makeText(RegisterActivity.this,
                    "用户名不合法，请重新输入11位的手机号...", Toast.LENGTH_LONG).show();
            return false;
        }

        if(StringUtil.isEmpty(password) || password.length() < Constant.MIN_PWD)
        {
            Toast.makeText(RegisterActivity.this, "密码不合法，请输入6-12位的密码...", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(RegisterActivity.this,"恭喜，您已注册成功", Toast.LENGTH_LONG).show();
                    //将用户数据存入数据库
                    UserCommonServiceSpi userCommonService = new UserCommonServiceSpi(RegisterActivity.this);
                    User user = new User();

                    user.setUserName(phoneNum.getText().toString());
                    user.setPassword(pwd.getText().toString());

                    userCommonService.insertUser(user);
                    finish();
                    break;
            }
        }
    };

}
