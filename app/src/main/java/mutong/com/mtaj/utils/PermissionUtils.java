package mutong.com.mtaj.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.widget.Toast;

import mutong.com.mtaj.permission.Action;
import mutong.com.mtaj.permission.AndPermission;
import mutong.com.mtaj.permission.Permission;
import mutong.com.mtaj.permission.RuntimeRationale;
import mutong.com.mtaj.permission.Setting;

import java.util.List;

import mutong.com.mtaj.R;
import mutong.com.mtaj.common.Constant;

public class PermissionUtils
{
    //申请位置权限
    public static void coarseLocation(final Context context)
    {
        Activity activity = (Activity)context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //判断是否具有权限
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                //判断是否需要向用户解释为什么需要申请该权限
                if (activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION))
                {
                    System.out.println("自Android 6.0开始需要打开位置权限才可以搜索到Ble设备");
                }
                //请求权限
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        Constant.REQUEST_COARSE_LOCATION);
            }
        }
    }
    //申请定位权限
    public static void location(final Context context)
    {
        Activity activity = (Activity)context;

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean networkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if( !(networkProvider || gpsProvider) && Build.VERSION.SDK_INT >= 23)
        {
            Intent enableLocate = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivityForResult(enableLocate, Constant.LOCATION_PERMISSION);
        }
    }

    public static void requestPermission(final Context context,String... permissions) {
        AndPermission.with(context)
                .runtime()
                .permission(permissions)
                .rationale(new RuntimeRationale())
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        //toast(R.string.successfully);
                        System.out.println(permissions + "onGranted");
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        //toast(R.string.failure);
                        System.out.println(permissions + "onDenied");
                        if (AndPermission.hasAlwaysDeniedPermission(context, permissions))
                        {
                            showSettingDialog(context, permissions);
                        }
                    }
                })
                .start();
    }

    /**
     * Display setting dialog.
     */
    private static void showSettingDialog(final Context context, final List<String> permissions)
    {
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = context.getString(R.string.message_permission_always_failed, TextUtils.join("\n", permissionNames));

        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(R.string.title_dialog)
                .setMessage(message)
                .setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPermission(context);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    /**
     * Set permissions.
     */
    private static void setPermission(final Context context) {
        AndPermission.with(context)
                .runtime()
                .setting()
                .onComeback(new Setting.Action()
                {
                    @Override
                    public void onAction()
                    {
                        Toast.makeText(context, R.string.message_setting_comeback, Toast.LENGTH_SHORT).show();
                    }
                })
                .start();
    }

}
