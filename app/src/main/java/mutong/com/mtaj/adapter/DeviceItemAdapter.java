package mutong.com.mtaj.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mutong.com.mtaj.R;

public class DeviceItemAdapter extends ArrayAdapter
{
    private int layoutId;

    public DeviceItemAdapter(Context context, int layoutId, List<DeviceItem> list) {
        super(context, layoutId, list);
        this.layoutId = layoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        DeviceItem item = (DeviceItem)getItem(position);

        View view = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.device_image);
        TextView deviceItemNumInput = (TextView) view.findViewById(R.id.device_item_numinput);
        TextView deviceNameInput = (TextView) view.findViewById(R.id.device_item_nameinput);
        TextView deviceUserNameInput = (TextView) view.findViewById(R.id.device_item_usernameinput);
        TextView deviceAdminInput = (TextView) view.findViewById(R.id.device_item_admininput);

        imageView.setImageResource(item.getImgId());
        deviceItemNumInput.setText(item.getDeviceNum());
        deviceNameInput.setText(item.getDeviceName());
        deviceUserNameInput.setText(item.getUserName());
        deviceAdminInput.setText(item.getAdminName());

        return view;
    }
}
