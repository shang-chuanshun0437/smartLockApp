package mutong.com.mtaj.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import mutong.com.mtaj.R;

public class DeviceInfoPicAdapter extends ArrayAdapter
{
    private int layoutId;

    public DeviceInfoPicAdapter(Context context, int layoutId, List<DeviceInfoPicItem> list) {
        super(context, layoutId, list);
        this.layoutId = layoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        DeviceInfoPicItem item = (DeviceInfoPicItem)getItem(position);

        View view = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);

        TextView itemView = (TextView) view.findViewById(R.id.deviceinfo_item);
        TextView itemName = (TextView) view.findViewById(R.id.deviceinfo_name);
        ImageView image = (ImageView) view.findViewById(R.id.image);

        itemView.setText(item.getItem());
        itemName.setText(item.getItemName());
        image.setBackgroundResource(item.getImageId());

        return view;
    }
}
