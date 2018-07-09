package mutong.com.mtaj.common.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mutong.com.mtaj.R;
import mutong.com.mtaj.utils.DateUtil;

public class TestBaseAdapter extends BaseAdapter implements
        StickyListHeadersAdapter, SectionIndexer {

    private final Context mContext;
    private ArrayList<String> datas;
    private int[] mSectionIndices;//每个分段有多少项
    private String[] mSectionLetters;//每个分段的首字母
    private LayoutInflater mInflater;

    public TestBaseAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        datas = new ArrayList<String>(Arrays.asList(context.getResources().getStringArray(R.array.opendoorhistory)));
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
    }

    private int[] getSectionIndices() {
        ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
        String lastFirstChar = datas.get(0).substring(0,8);
        sectionIndices.add(0);
        for (int i = 1; i < datas.size(); i++) {
            if (!datas.get(i).substring(0,8).equals(lastFirstChar)) {
                lastFirstChar = datas.get(i).substring(0,8);
                sectionIndices.add(i);
            }
        }
        int[] sections = new int[sectionIndices.size()];
        for (int i = 0; i < sectionIndices.size(); i++) {
            sections[i] = sectionIndices.get(i);
        }
        return sections;
    }

    private String[] getSectionLetters() {
        String[] letters = new String[mSectionIndices.length];
        for (int i = 0; i < mSectionIndices.length; i++) {
            letters[i] = datas.get(mSectionIndices[i]).substring(0,8);
        }
        return letters;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(datas.get(position));

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
        String date = DateUtil.convert2String(datas.get(position),0);
        String week = DateUtil.dateToWeek(datas.get(position).substring(0,8));
        holder.text.setText(date);
        holder.weekDay.setText(week);

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
        return datas.get(position).substring(0,8).charAt(7);
    }

    @Override
    public int getPositionForSection(int section) {
        if (mSectionIndices.length == 0) {
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
    public int getSectionForPosition(int position) {
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
    }

    //加载数据，当数据加载完毕时，end为true
    public void loadDatas(ArrayList<String> extral)
    {
        datas.addAll(extral);
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
    }
}