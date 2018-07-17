package mutong.com.mtaj.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mutong.com.mtaj.R;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.main.DeleteUserKeyDialogActivity;
import mutong.com.mtaj.repository.Device;

public class DeviceUsersAdapter extends ArrayAdapter
{
    private int layoutId;
    private Context context;
    private String deviceNum;

    public DeviceUsersAdapter(Context context, int layoutId, List<DeviceUsersItem> list,String deviceNum) {
        super(context, layoutId, list);
        this.layoutId = layoutId;
        this.context = context;
        this.deviceNum = deviceNum;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent)
    {
        System.out.println("getView:");
        DeviceUsersItem item = (DeviceUsersItem)getItem(position);

        View view = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);

        if(layoutId == R.layout.device_all_item)
        {
            ImageView imageView = (ImageView) view.findViewById(R.id.admin);
            final TextView deviceName = (TextView) view.findViewById(R.id.device_name);
            TextView validDate = (TextView) view.findViewById(R.id.valid_date);
            TextView addTime = (TextView) view.findViewById(R.id.add_time);
            final TextView delete = (TextView)view.findViewById(R.id.delete);

            imageView.setImageResource(item.getImgId());
            deviceName.setText(item.getPhoneNum());
            validDate.setText(item.getValidDate());
            addTime.setText(item.getNickName());

            delete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    UserCommonServiceSpi userCommonService = new UserCommonServiceSpi(context);
                    Device device = userCommonService.queryByDeviceName(deviceName.getText().toString());
                    System.out.println("onclick:" + device.getDeviceNum());
                    Intent intent = new Intent(context,DeleteUserKeyDialogActivity.class);
                    intent.putExtra("deletePhoneNum",userCommonService.getLoginUser().getPhoneNum());
                    intent.putExtra("deviceNum",device.getDeviceNum());
                    context.startActivity(intent);
                }
            });
        }
        else if(layoutId == R.layout.device_manager_item)
        {
            ImageView imageView = (ImageView) view.findViewById(R.id.admin);
            final TextView phoneNum = (TextView) view.findViewById(R.id.phone);
            TextView nickName = (TextView) view.findViewById(R.id.nick_name);
            TextView validDate = (TextView) view.findViewById(R.id.valid_date);
            final TextView delete = (TextView)view.findViewById(R.id.delete);

            imageView.setImageResource(item.getImgId());
            phoneNum.setText(item.getPhoneNum());
            validDate.setText(item.getValidDate());
            nickName.setText(item.getNickName());

            delete.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    System.out.println("device_manager_item onclick:" + deviceNum);
                    Intent intent = new Intent(context,DeleteUserKeyDialogActivity.class);
                    intent.putExtra("deletePhoneNum",phoneNum.getText().toString());
                    intent.putExtra("deviceNum",deviceNum);
                    context.startActivity(intent);
                }
            });
        }

        return view;
    }
}
