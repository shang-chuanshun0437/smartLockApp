package mutong.com.mtaj.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import mutong.com.mtaj.R;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.StringUtil;

public class ModifyNickNameActivity extends AppCompatActivity implements View.OnClickListener
{
    private EditText modifyNickName;
    private TextView modifySave;
    private ImageView back;

    private User user;
    private UserCommonServiceSpi userCommonService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_nickname);

        modifyNickName = (EditText)findViewById(R.id.modify_nick_name);
        modifySave = (TextView)findViewById(R.id.modify_save);
        back = (ImageView)findViewById(R.id.modify_back);

        modifySave.setOnClickListener(this);
        back.setOnClickListener(this);

        userCommonService = new UserCommonServiceSpi(this);
        user = userCommonService.getLoginUser();
        if (user != null && !StringUtil.isEmpty(user.getNickName()))
        {
            modifyNickName.setText(user.getNickName());
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

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.modify_back:
                finish();
                break;

            case R.id.modify_save:
                String nickName = modifyNickName.getText().toString();
                if (user != null )
                {
                    user.setNickName(nickName);
                    userCommonService.insertUser(user);
                    Toast.makeText(this,"恭喜，更改成功！", Toast.LENGTH_LONG).show();
                    finish();
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
}
