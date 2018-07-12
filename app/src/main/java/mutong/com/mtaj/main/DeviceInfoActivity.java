package mutong.com.mtaj.main;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mutong.com.mtaj.R;
import mutong.com.mtaj.adapter.DeviceInfoAdapter;
import mutong.com.mtaj.adapter.DeviceInfoItem;
import mutong.com.mtaj.common.Constant;
import mutong.com.mtaj.common.ErrorCode;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.Device;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.HttpUtil;
import mutong.com.mtaj.utils.ScreenSizeUtil;
import mutong.com.mtaj.utils.SpaceTextWatcher;
import mutong.com.mtaj.utils.StatusBarUtil;

public class DeviceInfoActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener
{
    private ListView deviceInfoList;
    private List<DeviceInfoItem> list = new ArrayList<DeviceInfoItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_info);

        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(this,R.color.title);
        //设置状态栏黑色文字
        StatusBarUtil.setBarTextLightMode(this);

        initItems();

        deviceInfoList = (ListView)findViewById(R.id.deviceinfo_list);
        DeviceInfoAdapter adapter = new DeviceInfoAdapter(this,R.layout.deviceinfo_item,list);
        deviceInfoList.setAdapter(adapter);
        deviceInfoList.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        switch (id)
        {

        }
    }

    private void initItems()
    {
        list.clear();
        String deviceNum = getIntent().getStringExtra("deviceNum");

        UserCommonServiceSpi userCommonService = new UserCommonServiceSpi(this);
        Device[] devices = userCommonService.queryByDeviceNum(deviceNum);

        DeviceInfoItem []deviceInfoItems = new DeviceInfoItem[]{
                new DeviceInfoItem("设备名称",devices[0].getDeviceName()),
                new DeviceInfoItem("设备编号",deviceNum),
                new DeviceInfoItem("硬件版本",devices[0].getDeviceVersion()),
                new DeviceInfoItem("蓝牙MAC",devices[0].getBloothMac())};

        for (DeviceInfoItem deviceInfoItem : deviceInfoItems)
        {
            list.add(deviceInfoItem);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        switch (position)
        {
            case 0:
                customDialog();
                break;
        }
    }

    /**
     * 自定义对话框
     */
    private void customDialog() {
        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.dialog_normal, null);
        TextView cancel = (TextView) view.findViewById(R.id.cancel);
        TextView confirm = (TextView) view.findViewById(R.id.ok);
        final EditText dialogDeviceName = (EditText) view.findViewById(R.id.device_name);
        dialog.setContentView(view);
        //使得点击对话框外部不消失对话框
        dialog.setCanceledOnTouchOutside(false);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtil.getInstance(this).getScreenHeight() * 0.30f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtil.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(dialogDeviceName.getText().toString());
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
