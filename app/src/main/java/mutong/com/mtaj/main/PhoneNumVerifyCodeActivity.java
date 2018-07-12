package mutong.com.mtaj.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mutong.com.mtaj.R;
import mutong.com.mtaj.common.ErrorCode;
import mutong.com.mtaj.common.VerificationCodeInput;
import mutong.com.mtaj.utils.CacheActivity;
import mutong.com.mtaj.utils.CountDownTimerUtil;
import mutong.com.mtaj.utils.HttpUtil;
import mutong.com.mtaj.utils.StatusBarUtil;
import mutong.com.mtaj.utils.StringUtil;

public class PhoneNumVerifyCodeActivity extends AppCompatActivity implements View.OnClickListener
{
    private String phoneNum;
    private TextView phoneNumText;
    private TextView nextSend;
    private Button nextBtn;
    private VerificationCodeInput verificationCodeInput;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_num_verify_code);

        //添加到activity的cache中，目的是注册完成后，结束该activity
        CacheActivity.addActivity(this);

        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(this,R.color.title);
        //设置状态栏黑色文字
        StatusBarUtil.setBarTextLightMode(this);

        //获取手机号
        phoneNum = getIntent().getStringExtra("phoneNum");

        if(StringUtil.isEmpty(phoneNum))
        {
            Toast.makeText(this,"手机号不正确",Toast.LENGTH_LONG).show();
            finish();
        }

        phoneNumText = (TextView)findViewById(R.id.phone_num);
        phoneNumText.setText("+86 " + phoneNum);
        nextBtn = (Button)findViewById(R.id.next_button);
        verificationCodeInput = (VerificationCodeInput)findViewById(R.id.verificationCodeInput);

        nextBtn.setOnClickListener(this);

        //启动短信验证码倒计时,90s之后，可重新发送
        nextSend = (TextView)findViewById(R.id.next_send);
        nextSend.setOnClickListener(this);
        CountDownTimerUtil countDownTimerUtil = new CountDownTimerUtil(nextSend,90 * 1000,1000);
        countDownTimerUtil.start();

        //EditText获取焦点并显示软键盘
        EditText focus = verificationCodeInput.getmEditTextList().get(0);
        focus.setFocusable(true);
        focus.setFocusableInTouchMode(true);
        focus.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.back:
            case R.id.history_text:
                finish();
                break;

            case R.id.next_button:
                //获取输入的验证码
                String verifyCode = getVerifyCode();
                if (verifyCode.length() != 6)
                {
                    Toast.makeText(this,"请输入6位的验证码",Toast.LENGTH_LONG).show();
                    break;
                }
                //校验验证码
                Map<String, String> map = new HashMap<String, String>();
                map.put("phoneNum",phoneNum.replace(" ",""));
                map.put("verifyCode",verifyCode);

                HttpUtil httpUtil = new HttpUtil(handler,this);
                httpUtil.post(map,"/verifyCode/checkVerifyCode");

                break;

            case R.id.next_send:
                //重新获取验证码
                Map<String, String> resendMap = new HashMap<String, String>();
                resendMap.put("phoneNum",phoneNum.replace(" ",""));

                HttpUtil resendHttpUtil = new HttpUtil(reSendHandler,this);
                resendHttpUtil.post(resendMap,"/verifyCode/getVerifyCode");

                CountDownTimerUtil countDownTimerUtil = new CountDownTimerUtil(nextSend,90 * 1000,1000);
                countDownTimerUtil.start();
                break;
        }
    }

    private String getVerifyCode()
    {
        StringBuffer buffer = new StringBuffer();

        List<EditText> list = verificationCodeInput.getmEditTextList();
        for (EditText editText : list)
        {
            buffer.append(editText.getText().toString());
        }
        return buffer.toString();
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
                                //获取后台返回的凭据
                                String voucher = jsonObject.getString("voucher");
                                //如果短信验证码正确,则进行注册
                                Intent intent = new Intent(PhoneNumVerifyCodeActivity.this,StartRegisterActivity.class);
                                intent.putExtra("phoneNum",phoneNum);
                                intent.putExtra("voucher",voucher);
                                startActivity(intent);
                                break;

                            case ErrorCode.VERIFY_CODE_NULL:
                                Toast.makeText(PhoneNumVerifyCodeActivity.this,"请重新获取验证码", Toast.LENGTH_LONG).show();
                                break;

                            case ErrorCode.VERIFY_CODE_ERROR:
                                Toast.makeText(PhoneNumVerifyCodeActivity.this,"验证码错误，请重新输入验证码", Toast.LENGTH_LONG).show();
                                break;

                            case ErrorCode.DEFAULT_ERROR:
                                Toast.makeText(PhoneNumVerifyCodeActivity.this,"服务器正在升级中，请稍后重试", Toast.LENGTH_LONG).show();
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

    private Handler reSendHandler = new Handler()
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
                                Toast.makeText(PhoneNumVerifyCodeActivity.this,"已将验证码发送到您的手机上，请查收", Toast.LENGTH_LONG).show();
                                break;

                            case ErrorCode.DEFAULT_ERROR:
                                Toast.makeText(PhoneNumVerifyCodeActivity.this,"服务器正在升级中，请稍后重试", Toast.LENGTH_LONG).show();
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
