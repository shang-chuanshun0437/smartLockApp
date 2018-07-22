package mutong.com.mtaj.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mutong.com.mtaj.R;
import mutong.com.mtaj.adapter.SettingAdapter;
import mutong.com.mtaj.adapter.SettingItem;
import mutong.com.mtaj.common.Constant;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.StatusBarUtil;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener
{
    private UserCommonServiceSpi userCommonService;

    private ImageView settingBack;
    private TextView setting;
    private TextView exit;

    private TextView modifyPwdText;
    private ImageView modifyPwdImage;

    private TextView suggessionText;
    private ImageView suggessionImage;
    private TextView better;

    private TextView aboutText;
    private ImageView aboutImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(this,R.color.title);
        //设置状态栏黑色文字
        StatusBarUtil.setBarTextLightMode(this);

        userCommonService = new UserCommonServiceSpi(this);
        User user = userCommonService.getLoginUser();
        if (user == null)
        {
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }

        settingBack = (ImageView)findViewById(R.id.back);
        setting = (TextView)findViewById(R.id.back_text);
        exit = (TextView)findViewById(R.id.exit);

        modifyPwdImage = (ImageView)findViewById(R.id.pwd_image);
        modifyPwdText = (TextView)findViewById(R.id.modify_pwd);

        suggessionImage = (ImageView)findViewById(R.id.sugesstion_image);
        suggessionText = (TextView)findViewById(R.id.sugesstion);
        better = (TextView)findViewById(R.id.better);

        aboutImage = (ImageView)findViewById(R.id.about_image);
        aboutText = (TextView)findViewById(R.id.about);

        setting.setOnClickListener(this);
        settingBack.setOnClickListener(this);

        modifyPwdText.setOnClickListener(this);
        modifyPwdImage.setOnClickListener(this);

        suggessionText.setOnClickListener(this);
        suggessionImage.setOnClickListener(this);
        better.setOnClickListener(this);

        aboutText.setOnClickListener(this);
        aboutImage.setOnClickListener(this);

        exit.setOnClickListener(this);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(userCommonService.getLoginUser() == null)
        {
            finish();
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.back:
            case R.id.back_text:
                finish();
                break;
            case R.id.exit:
                userCommonService.deleteDataFromSqlite(Constant.LOGIN_USER_TABLE,null);
                finish();
                break;

            case R.id.pwd_image: //修改密码
            case R.id.modify_pwd:
                Intent intent = new Intent(this, ChangePwdActivity.class);
                startActivity(intent);
                break;

            case R.id.sugesstion_image: //意见反馈
            case R.id.sugesstion:
            case R.id.better:

                break;

            case R.id.about_image: //关于我们
            case R.id.about:
                Intent about = new Intent(SettingActivity.this,AboutAppActivity.class);
                startActivity(about);
                break;

        }
    }
}
