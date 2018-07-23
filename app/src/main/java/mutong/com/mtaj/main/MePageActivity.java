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
import mutong.com.mtaj.adapter.MePageAdapter;
import mutong.com.mtaj.adapter.MePageItem;
import mutong.com.mtaj.common.CircleImageView;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.Preference;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.StatusBarUtil;

public class MePageActivity extends Fragment implements View.OnClickListener
{
    private UserCommonServiceSpi userCommonService;

    private View view;

    private CircleImageView headPortrait;
    private TextView nickname;

    private TextView accountNumberEdit;
    private ImageView meForward;

    private TextView settings;

    private ListView mePage;
    private ListView helpList;

    //列表数据
    private List<MePageItem> listItems = new ArrayList<MePageItem>();
    private List<MePageItem> helpDatas = new ArrayList<MePageItem>();

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
        helpList = (ListView)view.findViewById(R.id.help_list);

        headPortrait.setOnClickListener(this);
        nickname.setOnClickListener(this);
        accountNumberEdit.setOnClickListener(this);
        meForward.setOnClickListener(this);
        settings.setOnClickListener(this);

        initItems();

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
        MePageItem []tempItems = new MePageItem[]{new MePageItem("我的设备",R.mipmap.walk_anjian,R.mipmap.forward),
                new MePageItem("租房合约",R.mipmap.contract,R.mipmap.forward)};
        for (MePageItem settingItem : tempItems)
        {
            listItems.add(settingItem);
        }
        MePageAdapter adapter = new MePageAdapter(getContext(),R.layout.mepage_item,listItems);
        mePage.setAdapter(adapter);
        mePage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0://我的设备
                        Intent intent = new Intent(MePageActivity.this.getContext(),MyDeviceActivity.class);
                        startActivity(intent);
                        break;

                    case 1://租房合约
                        Intent contractIntent = new Intent(MePageActivity.this.getContext(),ContractInfoActivity.class);
                        startActivity(contractIntent);
                        break;
                }
            }
        });

        helpDatas.clear();
        MePageItem []helpItems = new MePageItem[]{new MePageItem("帮助信息",R.mipmap.help,R.mipmap.forward),
                new MePageItem("联系我们",R.mipmap.talk_service,R.mipmap.forward)};
        for (MePageItem settingItem : helpItems)
        {
            helpDatas.add(settingItem);
        }
        MePageAdapter adapterHelp = new MePageAdapter(getContext(),R.layout.mepage_item,helpDatas);
        helpList.setAdapter(adapterHelp);
        helpList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

}
