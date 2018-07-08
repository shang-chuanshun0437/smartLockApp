package mutong.com.mtaj.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import mutong.com.mtaj.R;
import mutong.com.mtaj.ble.BleHelper;
import mutong.com.mtaj.ble.BluetoothManagerService;
import mutong.com.mtaj.ble.util.BleConstant;
import mutong.com.mtaj.common.Constant;
import mutong.com.mtaj.utils.PermissionUtils;
import mutong.com.mtaj.utils.StatusBarUtil;

public class ReadyOpenDoorActivity extends AppCompatActivity implements View.OnClickListener
{
    private TextView readyOpendoor;
    private ImageView imageView;
    private AnimationDrawable animationDrawable;
    private String bluetoothMac;
    private BleHelper bleHelper;
    private ImageView back;
    private TextView progress;
    private TextView openHistory;
    private String deviceNum;
    //是否正在开锁
    private boolean isopening = false;
    /**
     * 自定义的打开 Bluetooth 的请求码，与 onActivityResult 中返回的 requestCode 匹配。
     */
    private static final int REQUEST_CODE_BLUETOOTH_ON = 1313;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ready_opendoor);

        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(this,R.color.title);
        //设置状态栏黑色文字
        StatusBarUtil.setBarTextLightMode(this);

        readyOpendoor = (TextView)findViewById(R.id.readyOpendoor);
        back = (ImageView)findViewById(R.id.back);
        imageView = (ImageView)findViewById(R.id.readyOpendoorImage);
        progress = (TextView)findViewById(R.id.progress);
        openHistory = (TextView)findViewById(R.id.open_history);

        readyOpendoor.setOnClickListener(this);
        back.setOnClickListener(this);
        imageView.setOnClickListener(this);
        openHistory.setOnClickListener(this);

        Bundle bundle = getIntent().getBundleExtra("device");

        if (bundle == null)
        {
            finish();
            return;

        }

        readyOpendoor.setText(bundle.getString("deviceName"));
        bluetoothMac = bundle.getString("bloothMac");
        deviceNum = bundle.getString("deviceNum");

        animationDrawable = (AnimationDrawable) imageView.getBackground();

        PermissionUtils.requestPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);
        PermissionUtils.location(this);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.readyOpendoorImage:
                System.out.println("readyOpendoorImage click,isopening:" + isopening);
                if (isopening)
                {
                    if (bleHelper != null)
                    {
                        bleHelper.close();
                        bleHelper = null;
                    }
                    //if(animationDrawable != null)
                    animationDrawable.selectDrawable(0);
                    animationDrawable.stop();
                    progress.setText("开锁进展：开锁失败，被手动停止。");
                    isopening = false;
                }
                else
                {
                    if (bleHelper == null)
                    {
                        bleHelper = new BleHelper(this,handler,bluetoothMac);
                    }

                    if( bleHelper.initialize() && !animationDrawable.isRunning())
                    {
                        System.out.println("start animationDrawable");
                        animationDrawable.start();
                        bleHelper.connectByMac(bluetoothMac);
                    }
                    isopening = true;
                }
                System.out.println("readyOpendoorImage click,isopening:" + isopening);
                break;

            case R.id.back:
            case R.id.readyOpendoor:
                finish();
                break;

            case R.id.open_history:
                Intent intent = new Intent(this, OpenDoorHistoryActivity.class);
                intent.putExtra("deviceNum",deviceNum);
                startActivity(intent);
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
            String temp = "开锁进展：";
            switch (msg.what)
            {
                case BleConstant.BLE_WRITE_NOTFOUND:
                    temp += "写特征值没找到：" + BleConstant.UUID_WRITE;
                    progress.setText(temp);
                    break;
                case BleConstant.BLE_WRITE_FOUND:
                    temp += "写特征值已找到：" + BleConstant.UUID_WRITE;
                    progress.setText(temp);
                    break;
                case BleConstant.BLE_NOTIFY_SUCCESS:
                    temp += "已收到通知：" + msg.obj;
                    temp += "\n";
                    progress.setText(temp);
                    break;
                case BleConstant.BLE_READ_NOTFOUND:
                    temp += "读特征值没找到：" + BleConstant.UUID_READ;
                    progress.setText(temp);
                    break;
                case BleConstant.BLE_READ_FOUND:
                    temp += "读特征值已找到：" + BleConstant.UUID_READ;
                    progress.setText(temp);
                    break;
                case BleConstant.BLE_READ_SUCCESS:
                    temp += "读UUID成功," + msg.obj ;
                    progress.setText(temp);
                    break;

                case BleConstant.HM_BLE_DISCONNECTED:
                    temp += "蓝牙断开连接";
                    progress.setText(temp);
                    break;
                case BleConstant.HM_BLE_CONNECTED:
                    temp += "蓝牙连接成功";
                    progress.setText(temp);
                    break;
                case BleConstant.BLE_WRITE_SUCCESS:
                    progress.setText(temp + "写数据成功,");
                    break;
                case BleConstant.BLE_WRITE_FAIL:
                    progress.setText(temp + "写数据失败,");
                    break;

                case BleConstant.BLE_NOTFOUND:
                    progress.setText(temp + "找不到指定的蓝牙：");
                    break;

                case BleConstant.BLE_SEARCH:
                    progress.setText(temp + "正在搜素蓝牙设备...");
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
