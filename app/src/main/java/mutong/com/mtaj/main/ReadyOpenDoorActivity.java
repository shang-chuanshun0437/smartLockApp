package mutong.com.mtaj.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import mutong.com.mtaj.R;
import mutong.com.mtaj.ble.BleHelper;
import mutong.com.mtaj.ble.BluetoothManagerService;
import mutong.com.mtaj.ble.util.BleConstant;
import mutong.com.mtaj.common.Constant;
import mutong.com.mtaj.utils.PermissionUtils;

public class ReadyOpenDoorActivity extends AppCompatActivity implements View.OnClickListener
{
    private TextView readyOpendoor;
    private ImageView imageView;
    private AnimationDrawable animationDrawable;
    private EditText editText;
    private String bluetoothMac;
    private BleHelper bleHelper;
    /**
     * 自定义的打开 Bluetooth 的请求码，与 onActivityResult 中返回的 requestCode 匹配。
     */
    private static final int REQUEST_CODE_BLUETOOTH_ON = 1313;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ready_opendoor);

        readyOpendoor = (TextView)findViewById(R.id.readyOpendoor);
        imageView = (ImageView)findViewById(R.id.readyOpendoorImage);
        editText = (EditText)findViewById(R.id.editText);

        editText.setFocusable(false);

        animationDrawable = (AnimationDrawable) imageView.getBackground();
        imageView.setOnClickListener(this);

        Bundle bundle = getIntent().getBundleExtra("device");

        if (bundle == null)
        {
            finish();
            return;

        }

        System.out.println("ReadyOpenDoorActivity thread:" + Thread.currentThread().getName());
        readyOpendoor.setText(bundle.getString("deviceName"));
        bluetoothMac = bundle.getString("bloothMac");
        PermissionUtils.requestPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);
        //PermissionUtils.requestPermission(this,Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        //PermissionUtils.coarseLocation(this);
        PermissionUtils.location(this);

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.readyOpendoorImage:
                System.out.println("readyOpendoorImage click");
                bleHelper = new BleHelper(this,handler,bluetoothMac);
                if( bleHelper.initialize() && !animationDrawable.isRunning())
                {
                    System.out.println("start animationDrawable");
                    animationDrawable.start();
                    bleHelper.connectByMac(bluetoothMac);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // 开启蓝牙
        if (requestCode == REQUEST_CODE_BLUETOOTH_ON)
        {
            switch (resultCode)
            {
                // 点击确认按钮
                case Activity.RESULT_OK:
                {
                    animationDrawable.start();
                }
                break;
                // 点击取消按钮或点击返回键
                case Activity.RESULT_CANCELED:
                {
                    finish();
                }
                break;
            }
        }

        // 开启定位
        if (requestCode == Constant.LOCATION_PERMISSION)
        {
            switch (resultCode)
            {
                // 点击确认按钮
                case Activity.RESULT_OK:
                {
                    System.out.println("开启定位");
                }
                break;
                // 点击取消按钮或点击返回键
                case Activity.RESULT_CANCELED:
                {
                    finish();
                }
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        //开启位置权限
        if (requestCode == Constant.REQUEST_COARSE_LOCATION)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //这里进行授权被允许的处理
                System.out.println("用户授权");
            }
            else
            {
                System.out.println("用户拒绝授权");
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                // 根据包名打开对应的设置界面
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
        else
        {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            String temp = editText.getText().toString();
            switch (msg.what)
            {
                case BleConstant.BLE_WRITE_NOTFOUND:
                    temp += "写特征值没找到：" + BleConstant.UUID_WRITE;
                    temp += "\n";
                    editText.setText(temp);
                    break;
                case BleConstant.BLE_WRITE_FOUND:
                    temp += "写特征值已找到：" + BleConstant.UUID_WRITE;
                    temp += "\n";
                    editText.setText(temp);
                    break;
                case BleConstant.BLE_NOTIFY_SUCCESS:
                    temp += "已收到通知：" + msg.obj;
                    temp += "\n";
                    editText.setText(temp);
                    break;
                case BleConstant.BLE_READ_NOTFOUND:
                    temp += "读特征值没找到：" + BleConstant.UUID_READ;
                    temp += "\n";
                    editText.setText(temp);
                    break;
                case BleConstant.BLE_READ_FOUND:
                    temp += "读特征值已找到：" + BleConstant.UUID_READ;
                    temp += "\n";
                    editText.setText(temp);
                    break;
                case BleConstant.BLE_READ_SUCCESS:
                    temp += "读UUID成功," + msg.obj ;
                    temp += "\n";
                    editText.setText(temp);
                    break;

                case BleConstant.HM_BLE_DISCONNECTED:
                    temp += "蓝牙断开连接";
                    temp += "\n";
                    editText.setText(temp);
                    break;
                case BleConstant.HM_BLE_CONNECTED:
                    temp += "蓝牙连接成功";
                    temp += "\n";
                    editText.setText(temp);
                    break;
                case BleConstant.BLE_WRITE_SUCCESS:
                    editText.setText(temp + "写数据成功," + msg.obj + "\n");
                    break;
                case BleConstant.BLE_WRITE_FAIL:
                    editText.setText(temp + "写数据失败," + "\n");
                    break;

                case BleConstant.BLE_NOTFOUND:
                    editText.setText(temp + "找不到指定的蓝牙：" + msg.obj + "\n");
                    break;
            }
        }
    };

    @Override
    protected void onStop()
    {
        super.onStop();
        if (bleHelper != null)
        {
            bleHelper.close();
        }
    }


}
