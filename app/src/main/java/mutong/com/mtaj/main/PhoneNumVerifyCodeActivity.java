package mutong.com.mtaj.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import mutong.com.mtaj.R;
import mutong.com.mtaj.common.VerificationCodeInput;
import mutong.com.mtaj.utils.CountDownTimerUtil;
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
                //Todo 去校验验证码是否正确

                //如果短信验证码正确,则进行注册
                Intent intent = new Intent(this,StartRegisterActivity.class);
                intent.putExtra("phoneNum",phoneNum);
                startActivity(intent);
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
}
