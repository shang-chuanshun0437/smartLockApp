package mutong.com.mtaj.common.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mutong.com.mtaj.R;
import mutong.com.mtaj.utils.DateUtil;
import mutong.com.mtaj.utils.JSONArrayUtil;

public class TestBaseAdapter extends BaseAdapter implements
        StickyListHeadersAdapter, SectionIndexer {

    private final Context mContext;
    private JSONArray datas;
    private int[] mSectionIndices;//每个分段有多少项
    private String[] mSectionLetters;//每个分段的首字母
    private LayoutInflater mInflater;

    public TestBaseAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    private int[] getSectionIndices() {
        ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
        try
        {
            //String userName = datas.getJSONObject(0).getString("userName");
            if(datas != null)
            {
                String openTime = datas.getJSONObject(0).getString("openTime");
                String lastFirstChar = openTime.substring(0,8);
                sectionIndices.add(0);
                for (int i = 1; i < datas.length(); i++)
                {
                    JSONObject history = datas.getJSONObject(i);

                    if (!history.getString("openTime").substring(0,8).equals(lastFirstChar))
                    {
                        lastFirstChar = history.getString("openTime").substring(0,8);
                        sectionIndices.add(i);
                    }
                }
                int[] sections = new int[sectionIndices.size()];
                for (int i = 0; i < sectionIndices.size(); i++) {
                    sections[i] = sectionIndices.get(i);
                }
                return sections;
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return new int[0];
    }

    private String[] getSectionLetters()
    {
        String[] letters = new String[mSectionIndices.length];
        for (int i = 0; i < mSectionIndices.length; i++)
        {
            try
            {
                letters[i] =  datas.getJSONObject(i).getString("openTime").substring(0,8);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return letters;
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.length();
    }

    @Override
    public Object getItem(int position)
    {
        try
        {
            return datas == null ? null : datas.getJSONObject(position).getString("openTime");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.test_list_item_layout, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            holder.phoneType = (TextView)convertView.findViewById(R.id.phoen_type);
            holder.time = (TextView)convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try
        {
            if(datas != null)
            {
                holder.text.setText(datas.getJSONObject(position).getString("userName"));
                holder.phoneType.setText(datas.getJSONObject(position).getString("phoneType"));
                holder.time.setText(DateUtil.convert2String(datas.getJSONObject(position).getString("openTime"),1));
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return convertView;
    }

    //用来获取并显示分段的索引
    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.header, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text1);
            holder.weekDay = (TextView)convertView.findViewById(R.id.week_day);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        // set header text as first char in name
        //CharSequence headerChar = mCountries[position].substring(0,8);
        try
        {
            String date = DateUtil.convert2String(datas.getJSONObject(position).getString("openTime"),0);
            String week = DateUtil.dateToWeek(datas.getJSONObject(position).getString("openTime").substring(0,8));
            holder.text.setText(date);
            holder.weekDay.setText(week);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return convertView;
    }

    /**
     * Remember that these have to be static, postion=1 should always return
     * the same Id that is.
     * 决定着分组
     */
    @Override
    public long getHeaderId(int position) {
        // return the first character of the country as ID because this is what
        // headers are based upon
        //第7个字符表示哪一天
        try
        {
            return datas.getJSONObject(position).getString("openTime").charAt(7);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getPositionForSection(int section) {
        if (mSectionIndices == null || mSectionIndices.length == 0) {
            return 0;
        }

        if (section >= mSectionIndices.length) {
            section = mSectionIndices.length - 1;
        } else if (section < 0) {
            section = 0;
        }
        return mSectionIndices[section];
    }

    @Override
    public int getSectionForPosition(int position)
    {
        if (mSectionIndices == null)
        {
            return 0;
        }
        for (int i = 0; i < mSectionIndices.length; i++) {
            if (position < mSectionIndices[i]) {
                return i - 1;
            }
        }
        return mSectionIndices.length - 1;
    }

    @Override
    public Object[] getSections() {
        return mSectionLetters;
    }

    class HeaderViewHolder {
        TextView text;
        TextView weekDay;
    }

    class ViewHolder {
        TextView text;
        TextView phoneType;
        TextView time;
    }

    //加载数据
    public void loadDatas(JSONArray jsonArray)
    {
        if (jsonArray == null)
        {
            return;
        }
        datas = JSONArrayUtil.joinJSONArray(datas,jsonArray);
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
    }
}