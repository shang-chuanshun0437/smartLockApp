package mutong.com.mtaj.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mutong.com.mtaj.R;
import mutong.com.mtaj.adapter.SettingAdapter;
import mutong.com.mtaj.adapter.SettingItem;
import mutong.com.mtaj.common.CircleImageView;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.Preference;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.StatusBarUtil;

public class MePageActivity extends Fragment implements View.OnClickListener,AdapterView.OnItemClickListener
{
    private UserCommonServiceSpi userCommonService;

    private View view;

    private CircleImageView headPortrait;
    private TextView nickname;

    private TextView accountNumberEdit;
    private ImageView meForward;

    private TextView settings;

    private ListView mePage;

    //列表数据
    private List<SettingItem> listItems = new ArrayList<SettingItem>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.me_page, container, false);

        userCommonService = new UserCommonServiceSpi(getContext());

        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(getActivity(),R.color.mePage);

        headPortrait = (CircleImageView) view.findViewById(R.id.head_portrait);
        nickname = (TextView)view.findViewById(R.id.nickname);
        accountNumberEdit = (TextView)view.findViewById(R.id.account_number_edit);
        meForward = (ImageView)view.findViewById(R.id.me_forward);
        settings = (TextView) view.findViewById(R.id.me_page_settings);
        mePage = (ListView)view.findViewById(R.id.me_list);

        headPortrait.setOnClickListener(this);
        nickname.setOnClickListener(this);
        accountNumberEdit.setOnClickListener(this);
        meForward.setOnClickListener(this);
        settings.setOnClickListener(this);

        initItems();
        SettingAdapter adapter = new SettingAdapter(getContext(),R.layout.settings_item,listItems);
        mePage.setAdapter(adapter);
        mePage.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        User user = userCommonService.getLoginUser();
        if (user != null )
        {
            Preference preference = userCommonService.getPreference(user.getPhoneNum());

            accountNumberEdit.setText(user.getPhoneNum());
            nickname.setText(user.getUserName());

            if(preference != null)
            {
                if(preference.getHeadPortraitPath() != null)
                {
                    Bitmap bitmap = BitmapFactory.decodeFile(preference.getHeadPortraitPath());
                    headPortrait.setImageBitmap(bitmap);
                }
            }

        }
        else
        {
            accountNumberEdit.setText("未登录");
            nickname.setText("未设置昵称");
            headPortrait.setImageResource(R.mipmap.xiaoxiong);
        }
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        switch (id)
        {
            case R.id.head_portrait:
            case R.id.account_number_edit:
            case R.id.nickname:
            case R.id.me_forward:
                if(accountNumberEdit.getText().toString().equals("未登录"))
                {
                    Intent intent = new Intent(this.getContext(), LoginActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(this.getContext(), PersonalInfoActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.me_page_settings:
                Intent intent = new Intent(this.getContext(), SettingActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void initItems()
    {
        listItems.clear();
        SettingItem []tempItems = new SettingItem[]{new SettingItem("我的设备",R.mipmap.forward),
                new SettingItem("关于",R.mipmap.forward)};
        for (SettingItem settingItem : tempItems)
        {
            listItems.add(settingItem);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

        switch (position)
        {
            case 0:
                Intent intent = new Intent(this.getContext(),MyDeviceActivity.class);
                startActivity(intent);
                break;
        }
        System.out.println(view.getId() + "," + id);
    }
}
