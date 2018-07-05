package mutong.com.mtaj.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import mutong.com.mtaj.R;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.StringUtil;

public class PersonalInfoActivity extends AppCompatActivity implements View.OnClickListener
{
    private TextView personalAccountEdit;
    private TextView personalAccount;
    private TextView personalNickNameEdit;
    private TextView personalNickName;
    private UserCommonServiceSpi userCommonService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_info);

        userCommonService = new UserCommonServiceSpi(this);

        personalAccountEdit = (TextView)findViewById(R.id.personal_account_edit);
        personalNickNameEdit = (TextView)findViewById(R.id.personal_nickname_edit);
        personalNickName = (TextView)findViewById(R.id.personal_nickname);

        personalNickNameEdit.setOnClickListener(this);
        personalNickName.setOnClickListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        User user = userCommonService.getLoginUser();
        if (user != null && !StringUtil.isEmpty(user.getUserName()) && !StringUtil.isEmpty(user.getUserToken()))
        {
            if (!StringUtil.isEmpty(user.getUserName()))
            {
                personalAccountEdit.setText(user.getUserName());
            }

            if (!StringUtil.isEmpty(user.getNickName()))
            {
                personalNickNameEdit.setText(user.getNickName());
            }
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.personal_nickname_edit:
            case R.id.personal_nickname:
                Intent intent = new Intent(this, ModifyNickNameActivity.class);
                startActivity(intent);
                break;
        }
    }
}
