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
    private EditText phoneNumInput;
    private Button nextButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        phoneNumInput = (EditText)findViewById(R.id.phone_num_input);
        nextButton = (Button)findViewById(R.id.next_button);

        phoneNumInput.addTextChangedListener(textWatcher);
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
                    Intent intent = new Intent(this,PhoneNumVerifyCodeActivity.class);
                    intent.putExtra("phoneNum",phoneNumInput.getText().toString());
                    startActivity(intent);
                }
                break;
        }
    }
    //和后台服务器数据交互完成注册
    private void regist()
    {
        //Todo 对二维码的处理稍后再做

        //校验用户名和密码是否合法
        //请求服务器进行注册
        /*Map<String, String> map = new HashMap<String, String>();
        map.put("userName",phoneNum.getText().toString());
        map.put("password",pwd.getText().toString());

        HttpUtil httpUtil = new HttpUtil(handler,this);
        httpUtil.post(map,"/user/addUser");*/

        /*RequestQueue mQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,Constant.URL_PREFIX + "user/addUser",
                new JSONObject(map),
                new URLResponseListener(this,handler), new URLErrorResponseListener(this));

        mQueue.add(jsonObjectRequest);*/

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
                    Toast.makeText(RegisterActivity.this,"恭喜，您已注册成功", Toast.LENGTH_LONG).show();
                    //将用户数据存入数据库
                    UserCommonServiceSpi userCommonService = new UserCommonServiceSpi(RegisterActivity.this);
                    User user = new User();

                    //user.setUserName(phoneNum.getText().toString());
                    //user.setPassword(pwd.getText().toString());

                    userCommonService.insertUser(user);
                    finish();
                    break;
            }
        }
    };

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            if (s == null || s.length() == 0)
                return;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                    continue;
                } else {
                    sb.append(s.charAt(i));
                    if ((sb.length() == 4 || sb.length() == 9)
                            && sb.charAt(sb.length() - 1) != ' ') {
                        sb.insert(sb.length() - 1, ' ');
                    }
                }
            }
            if (!sb.toString().equals(s.toString())) {
                int index = start + 1;
                if (sb.charAt(start) == ' ') {
                    if (before == 0) {
                        index++;
                    } else {
                        index--;
                    }
                } else {
                    if (before == 1) {
                        index--;
                    }
                }
                phoneNumInput.setText(sb.toString());
                phoneNumInput.setSelection(index);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
