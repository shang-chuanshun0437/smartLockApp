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

public class DeviceUsersAdapter extends ArrayAdapter
{
    private int layoutId;

    public DeviceUsersAdapter(Context context, int layoutId, List<DeviceUsersItem> list) {
        super(context, layoutId, list);
        this.layoutId = layoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        DeviceUsersItem item = (DeviceUsersItem)getItem(position);

        View view = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.admin);
        TextView phoneNum = (TextView) view.findViewById(R.id.phone);
        TextView validDate = (TextView) view.findViewById(R.id.ok);
        TextView nickName = (TextView) view.findViewById(R.id.nick_name);

        imageView.setImageResource(item.getImgId());
        phoneNum.setText(item.getPhoneNum());
        validDate.setText(item.getValidDate());
        nickName.setText(item.getNickName());

        return view;
    }
}
