package mutong.com.mtaj.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mutong.com.mtaj.R;
import mutong.com.mtaj.common.ErrorCode;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.Preference;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.HttpUtil;
import mutong.com.mtaj.utils.StringUtil;

public class ModifyNickNameActivity extends AppCompatActivity implements View.OnClickListener
{
    private EditText modifyNickName;
    private TextView modifySave;
    private ImageView back;
    private TextView modifyBackText;
    private UserCommonServiceSpi userCommonService;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_nickname);

        modifyNickName = (EditText)findViewById(R.id.modify_nick_name);
        modifySave = (TextView)findViewById(R.id.modify_save);
        back = (ImageView)findViewById(R.id.modify_back);
        modifyBackText = (TextView)findViewById(R.id.modify_back_text);

        modifySave.setOnClickListener(this);
        back.setOnClickListener(this);
        modifyBackText.setOnClickListener(this);

        userCommonService = new UserCommonServiceSpi(this);
        user = userCommonService.getLoginUser();
        if (user != null && !StringUtil.isEmpty(user.getUserName()))
        {
            modifyNickName.setText(user.getUserName());
        }

        //EditText获取焦点并显示软键盘
        modifyNickName.setFocusable(true);
        modifyNickName.setFocusableInTouchMode(true);
        modifyNickName.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        user = userCommonService.getLoginUser();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.modify_back:
            case R.id.modify_back_text:
                finish();
                break;

            case R.id.modify_save:
                String nickName = modifyNickName.getText().toString();
                if (user != null )
                {
                    Map<String,String> map = new HashMap<String,String>();
                    map.put("phoneNum",user.getPhoneNum());
                    map.put("token",user.getUserToken());
                    map.put("newUserName",nickName);

                    String url = "/user/modifyUserName";
                    HttpUtil httpUtil = new HttpUtil(handler,this);

                    httpUtil.post(map,url);
                }
                else
                {
                    Toast.makeText(this,"更改失败，请重新登录！", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }
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
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    try
                    {
                        JSONObject result = jsonObject.getJSONObject("result");
                        String retCode = result.getString("retcode");
                        switch (retCode)
                        {
                            case ErrorCode.SUCEESS:
                                user.setUserName(modifyNickName.getText().toString());
                                userCommonService.insertUser(user);

                                Toast.makeText(ModifyNickNameActivity.this,"恭喜，修改成功",Toast.LENGTH_LONG).show();
                                finish();
                                break;
                            case ErrorCode.NOT_LOGIN:
                                Intent intent = new Intent(ModifyNickNameActivity.this,LoginActivity.class);
                                startActivity(intent);
                                break;
                            case ErrorCode.DEFAULT_ERROR:
                                Toast.makeText(ModifyNickNameActivity.this,"抱歉服务器正在升级中，请稍后重试",Toast.LENGTH_LONG).show();
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
