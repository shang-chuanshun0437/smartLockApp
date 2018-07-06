package mutong.com.mtaj.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
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
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.User;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener
{
    private User user;
    private UserCommonServiceSpi userCommonService;

    //列表数据
    private List<SettingItem> settingItems = new ArrayList<SettingItem>();
    private ListView settingList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        settingList = (ListView)findViewById(R.id.settings_list);

        initSettingItems();
        SettingAdapter adapter = new SettingAdapter(this,R.layout.settings_item,settingItems);

        settingList.setAdapter(adapter);
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

        }
    }

    private void initSettingItems()
    {

        SettingItem []tempItems = new SettingItem[]{new SettingItem("修改密码",R.mipmap.forward),
                                                        new SettingItem("关于",R.mipmap.forward)};
        for (SettingItem settingItem : tempItems)
        {
            settingItems.add(settingItem);
        }
    }
}
