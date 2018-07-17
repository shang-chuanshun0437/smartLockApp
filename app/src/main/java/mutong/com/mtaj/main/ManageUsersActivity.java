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

public class ManageUsersActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener
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
        usersList.setOnItemClickListener(this);

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

        DeviceUsersAdapter adapter = new DeviceUsersAdapter(this,R.layout.device_manager_item,list);
        usersList.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        final TextView delete = view.findViewById(R.id.cancel);
        final TextView deletePhoneNum = view.findViewById(R.id.phone);
        System.out.println("onItemClick ; " + position);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                User user = userCommonService.getLoginUser();

                deletePhoneStr = deletePhoneNum.getText().toString().replace(" ","");

                Map<String,String> map = new ArrayMap<String,String>();
                map.put("phoneNum",user.getPhoneNum());
                map.put("token",user.getUserToken());
                map.put("deletePhoneNum",deletePhoneStr);
                map.put("deviceNum",deviceNum.replace(" ",""));

                CustomDialog customDialog = new CustomDialog(ManageUsersActivity.this,R.layout.dialog_delete_device,deleteHandler,null,map);
                customDialog.showDialog();
            }
        });
    }

    private Handler deleteHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    try
                    {
                        JSONObject jsonObject = (JSONObject) msg.obj;
                        JSONObject resultObject = jsonObject.getJSONObject("result");
                        String retCode = resultObject.getString("retcode");
                        switch (retCode)
                        {
                            case ErrorCode.SUCEESS:
                                User user = userCommonService.getLoginUser();
                                userCommonService.deleteDevice(deletePhoneStr,deviceNum);
                                System.out.println("deletePhoneStr:" + deletePhoneStr);
                                initItems();
                                break;

                            case ErrorCode.OTHER_USERS_EXIST:
                                Toast.makeText(ManageUsersActivity.this,"删除失败：您是设备管理员，该设备下还有其他用户",Toast.LENGTH_LONG).show();
                                break;

                            case ErrorCode.DEFAULT_ERROR:
                                Toast.makeText(ManageUsersActivity.this,"抱歉，服务器正在升级中，请稍后重试",Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                    catch (JSONException e)
                    {

                    }

                    break;

                case 0 :
                    break;
            }
        }
    };
}
