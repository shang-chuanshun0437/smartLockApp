package mutong.com.mtaj.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mutong.com.mtaj.R;
import mutong.com.mtaj.adapter.DeviceUsersAdapter;
import mutong.com.mtaj.adapter.DeviceUsersItem;
import mutong.com.mtaj.common.Constant;
import mutong.com.mtaj.common.ErrorCode;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.Device;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.CustomDialog;
import mutong.com.mtaj.utils.StatusBarUtil;
import mutong.com.mtaj.utils.StringUtil;

public class ManageUsersActivity extends AppCompatActivity implements View.OnClickListener
{
    private ListView usersList;
    private ImageView backView;
    private String deviceNum;
    private UserCommonServiceSpi userCommonService;
    private TextView headName;
    private String deletePhoneStr;

    private List<DeviceUsersItem> list = new ArrayList<DeviceUsersItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_users);

        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(this,R.color.title);
        //设置状态栏黑色文字
        StatusBarUtil.setBarTextLightMode(this);

        deviceNum = getIntent().getStringExtra("deviceNum");

        userCommonService = new UserCommonServiceSpi(this);

        usersList = (ListView) findViewById(R.id.users_list);
        backView = (ImageView)findViewById(R.id.back);
        headName = (TextView)findViewById(R.id.back_text);

        backView.setOnClickListener(this);
        initItems();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.back:
                finish();
                break;
        }
    }

    private void initItems()
    {
        list.clear();
        Device [] devices = userCommonService.queryByDeviceNum(deviceNum);
        if (devices != null && devices.length > 0)
        {
            headName.setText(devices[0].getDeviceName());
            for (Device device : devices)
            {
                DeviceUsersItem deviceUsersItem = new DeviceUsersItem();
                if(!StringUtil.isEmpty(device.getRole()) && device.getRole().equals(Constant.OTHER))
                {
                    deviceUsersItem.setImgId(R.mipmap.normal_user);
                }
                else
                {
                    deviceUsersItem.setImgId(R.mipmap.admin);
                }

                deviceUsersItem.setPhoneNum(device.getPhoneNum());
                deviceUsersItem.setNickName(device.getUserName());
                if (StringUtil.isEmpty(device.getValidDate()) || device.getValidDate().equals("null"))
                {
                    deviceUsersItem.setValidDate("永久有效");
                }
                else
                {
                    deviceUsersItem.setValidDate(device.getValidDate());
                }
                list.add(deviceUsersItem);
            }
        }

        DeviceUsersAdapter adapter = new DeviceUsersAdapter(this,R.layout.device_manager_item,list,deviceNum);
        usersList.setAdapter(adapter);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        initItems();
    }
}
