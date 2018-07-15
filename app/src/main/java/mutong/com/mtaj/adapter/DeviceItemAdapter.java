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
import mutong.com.mtaj.common.CoustomRatingBar;

public class DeviceItemAdapter extends ArrayAdapter
{
    private int layoutId;
    private Context context;

    public DeviceItemAdapter(Context context, int layoutId, List<DeviceItem> list) {
        super(context, layoutId, list);
        this.layoutId = layoutId;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        DeviceItem item = (DeviceItem)getItem(position);

        View view = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.device_image);
        TextView deviceItemNumInput = (TextView) view.findViewById(R.id.device_item_numinput);
        TextView deviceNameInput = (TextView) view.findViewById(R.id.device_item_nameinput);
        TextView userNum = (TextView) view.findViewById(R.id.user_num);
        CoustomRatingBar ratingBar = (CoustomRatingBar)view.findViewById(R.id.ratingbar);

        imageView.setImageResource(item.getImgId());
        deviceItemNumInput.setText(item.getDeviceNum());
        deviceNameInput.setText(item.getDeviceName());
        deviceNameInput.getPaint().setFakeBoldText(true);//加粗
        userNum.setText(item.getUserNum());

        ratingBar.setStarEmptyDrawable(context.getResources().getDrawable(R.mipmap.ic_star_empty));
        ratingBar.setStarFillDrawable(context.getResources().getDrawable(R.mipmap.ic_star_fill));
        ratingBar.setStar(Float.parseFloat(item.getUserNum()));

        return view;
    }
}
