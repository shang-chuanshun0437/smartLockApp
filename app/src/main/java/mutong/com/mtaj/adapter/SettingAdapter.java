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

public class SettingAdapter extends ArrayAdapter
{
    private int layoutId;

    public SettingAdapter(Context context, int layoutId, List<SettingItem> list) {
        super(context, layoutId, list);
        this.layoutId = layoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        SettingItem item = (SettingItem)getItem(position);

        View view = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);

        ImageView imageView = (ImageView) view.findViewById(R.id.settings_image);
        TextView settingName = (TextView) view.findViewById(R.id.settings_name);


        imageView.setImageResource(item.getImgId());
        settingName.setText(item.getSettingsName());

        return view;
    }
}
