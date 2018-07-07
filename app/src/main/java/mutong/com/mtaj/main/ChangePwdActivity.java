package mutong.com.mtaj.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;

import mutong.com.mtaj.R;
import mutong.com.mtaj.adapter.SettingAdapter;
import mutong.com.mtaj.adapter.SettingItem;
import mutong.com.mtaj.common.ErrorCode;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.HttpUtil;

public class ChangePwdActivity extends AppCompatActivity implements View.OnClickListener
{
    private User user;
    private UserCommonServiceSpi userCommonService;

    private TextView oldPwd;
    private TextView newPwd;
    private TextView confirmNewPwd;
    private TextView changPwd;
    private TextView modifyPwdText;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changepwd);

        userCommonService = new UserCommonServiceSpi(this);

        oldPwd = (TextView)findViewById(R.id.old_pwd_edit);
        newPwd = (TextView)findViewById(R.id.new_pwd_edit);
        confirmNewPwd = (TextView)findViewById(R.id.confirm_pwd_edit);
        changPwd = (TextView)findViewById(R.id.confirm);
        back = (ImageView)findViewById(R.id.back);
        modifyPwdText = (TextView)findViewById(R.id.modify_pwd_text);

        back.setOnClickListener(this);
        modifyPwdText.setOnClickListener(this);
        changPwd.setOnClickListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.confirm:
                if(!checkInput())
                {
                    return;
                }
                //去数据库校验当前密码是否正确，如果当前密码正确，则修改为新密码
                User user = userCommonService.getLoginUser();
                if(user == null)
                {
                    Intent intent = new Intent(this,LoginActivity.class);
                    startActivity(intent);
                    return;
                }

                Map<String,String> map = new HashMap<String,String>();
                map.put("userName",user.getUserName());
                map.put("oldpassword",oldPwd.getText().toString());
                map.put("newpassword",newPwd.getText().toString());

                String url = "/user/modifyPwd";
                HttpUtil httpUtil = new HttpUtil(handler,this);
                httpUtil.post(map,url);
                break;

            case R.id.back:
            case R.id.modify_pwd_text:
                finish();
                break;
        }
    }

    private boolean checkInput()
    {
        String pwd = newPwd.getText().toString();
        String confirmPwd = confirmNewPwd.getText().toString();
        String old = oldPwd.getText().toString();

        if(old.length() > 48 || old.length() < 6)
        {
            Toast.makeText(this,"当前密码输入有误，请重新输入",Toast.LENGTH_LONG).show();
            return false;
        }

        if (pwd.length() < 6)
        {
            Toast.makeText(this,"新密码长度小于6位，请重新输入新密码",Toast.LENGTH_LONG).show();
            return false;
        }

        if (pwd.length() > 48)
        {
            Toast.makeText(this,"新密码长度大于48位，请重新输入新密码",Toast.LENGTH_LONG).show();
            return false;
        }

        if(!pwd.equals(confirmPwd))
        {
            Toast.makeText(this,"两次输入的新密码不同，请重新输入确认密码",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    try
                    {
                        JSONObject response = (JSONObject)msg.obj;
                        JSONObject resultObject = response.getJSONObject("result");
                        String retCode = resultObject.getString("retcode");
                        String token = response.getString("token");
                        switch (retCode)
                        {
                            case ErrorCode.SUCEESS:
                                Toast.makeText(ChangePwdActivity.this,"密码修改成功",Toast.LENGTH_LONG).show();

                                //将服务器返回的密码和token存入sqlite
                                User user = userCommonService.getLoginUser();
                                user.setPassword(newPwd.getText().toString());
                                user.setUserToken(token);
                                userCommonService.insertUser(user);
                                finish();
                                break;

                            case ErrorCode.USERNAME_NOT_EXIST:
                                Intent intent = new Intent(ChangePwdActivity.this,LoginActivity.class);
                                startActivity(intent);
                                break;

                            case ErrorCode.PASSWORD_ERROR:
                                Toast.makeText(ChangePwdActivity.this,"当前密码输入错误，请重新输入",Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };
}
