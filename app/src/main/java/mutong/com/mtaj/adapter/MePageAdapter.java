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

public class MePageAdapter extends ArrayAdapter
{
    private int layoutId;

    public MePageAdapter(Context context, int layoutId, List<MePageItem> list) {
        super(context, layoutId, list);
        this.layoutId = layoutId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        MePageItem item = (MePageItem)getItem(position);

        View view = LayoutInflater.from(getContext()).inflate(layoutId, parent, false);

        ImageView headImage = (ImageView) view.findViewById(R.id.head_image);
        TextView itemName = (TextView) view.findViewById(R.id.item_name);
        ImageView forwardImage = (ImageView) view.findViewById(R.id.forward_image);

        headImage.setImageResource(item.getHeadId());
        itemName.setText(item.getItemName());
        forwardImage.setImageResource(item.getForwardId());

        return view;
    }
}
