package mutong.com.mtaj.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import mutong.com.mtaj.R;
import mutong.com.mtaj.common.CircleImageView;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.StringUtil;

public class MePageActivity extends Fragment implements View.OnClickListener
{
    private UserCommonServiceSpi userCommonService;

    private View view;

    private CircleImageView headPortrait;
    private TextView nickname;

    private TextView accountNumberEdit;
    private ImageView meForward;

    private TextView settings;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.me_page, container, false);

        userCommonService = new UserCommonServiceSpi(getContext());

        headPortrait = (CircleImageView) view.findViewById(R.id.head_portrait);
        nickname = (TextView)view.findViewById(R.id.nickname);
        accountNumberEdit = (TextView)view.findViewById(R.id.account_number_edit);
        meForward = (ImageView)view.findViewById(R.id.me_forward);
        settings = (TextView) view.findViewById(R.id.me_page_settings);

        headPortrait.setOnClickListener(this);
        nickname.setOnClickListener(this);
        accountNumberEdit.setOnClickListener(this);
        meForward.setOnClickListener(this);
        settings.setOnClickListener(this);
        //meForwardFirst.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        User user = userCommonService.getLoginUser();
        if (user != null && !StringUtil.isEmpty(user.getUserName()) && !StringUtil.isEmpty(user.getUserToken()))
        {
            //meLogin.setBackground();
            accountNumberEdit.setText(user.getUserName());

            if (!StringUtil.isEmpty(user.getNickName()))
            {
                nickname.setText(user.getNickName());
            }
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
}
