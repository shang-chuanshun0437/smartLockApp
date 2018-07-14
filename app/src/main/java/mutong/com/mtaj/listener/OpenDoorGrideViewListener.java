package mutong.com.mtaj.listener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mutong.com.mtaj.R;
import mutong.com.mtaj.common.GridViewRowDivide;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.main.LoginActivity;
import mutong.com.mtaj.main.ReadyOpenDoorActivity;
import mutong.com.mtaj.repository.Device;
import mutong.com.mtaj.repository.User;

public class OpenDoorGrideViewListener implements AdapterView.OnItemClickListener
{
    private Context context;

    public OpenDoorGrideViewListener(Context context)
    {
        this.context = context;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        TextView item = view.findViewById(R.id.mainViewTitle);
        String deviceName = item.getText().toString();

        //根据设备名称来获取设备详情
        UserCommonServiceSpi userCommonService = new UserCommonServiceSpi(context);
        Device device = userCommonService.queryByDeviceName(deviceName);

        Bundle bundle = new Bundle();
        bundle.putString("deviceNum",device.getDeviceNum());
        bundle.putString("deviceName",device.getDeviceName());
        bundle.putString("deviceVersion",device.getDeviceVersion());
        bundle.putString("bloothMac",device.getBloothMac());

        Intent intent = new Intent(context, ReadyOpenDoorActivity.class);
        intent.putExtra("device",bundle);

        context.startActivity(intent);
    }
}
