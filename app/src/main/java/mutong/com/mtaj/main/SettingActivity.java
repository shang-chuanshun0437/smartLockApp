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

public class SettingActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener
{
    private UserCommonServiceSpi userCommonService;

    private ImageView settingBack;
    private TextView setting;
    private TextView exit;

    //列表数据
    private List<SettingItem> settingItems = new ArrayList<SettingItem>();
    private ListView settingList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        userCommonService = new UserCommonServiceSpi(this);
        User user = userCommonService.getLoginUser();
        if (user == null)
        {
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        }

        settingBack = (ImageView)findViewById(R.id.setting_back);
        setting = (TextView)findViewById(R.id.setting);
        exit = (TextView)findViewById(R.id.exit);
        settingList = (ListView)findViewById(R.id.settings_list);

        initSettingItems();
        SettingAdapter adapter = new SettingAdapter(this,R.layout.settings_item,settingItems);

        setting.setOnClickListener(this);
        settingBack.setOnClickListener(this);
        exit.setOnClickListener(this);
        settingList.setAdapter(adapter);
        settingList.setOnItemClickListener(this);
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
            case R.id.setting:
            case R.id.setting_back:
                finish();
                break;
            case R.id.exit:
                userCommonService.deleteDataFromSqlite(Constant.LOGIN_USER_TABLE,null);
                finish();
                break;
        }
    }

    private void initSettingItems()
    {
        settingItems.clear();

        SettingItem []tempItems = new SettingItem[]{new SettingItem("修改密码",R.mipmap.forward),
                                                        new SettingItem("关于",R.mipmap.forward)};
        for (SettingItem settingItem : tempItems)
        {
            settingItems.add(settingItem);
        }
    }

    //ListView监听器
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        switch (position)
        {
            case 0:
                Intent intent = new Intent(this, ChangePwdActivity.class);
                startActivity(intent);
                break;
            case 1:
                Intent about = new Intent(this,AboutAppActivity.class);
                startActivity(about);
                break;
        }
    }
}
