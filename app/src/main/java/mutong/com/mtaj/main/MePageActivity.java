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
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.StringUtil;

public class MePageActivity extends Fragment implements View.OnClickListener
{
    private UserCommonServiceSpi userCommonService;

    private View view;

    private ImageView meLogin;
    private TextView meLoginText;

    private TextView meDeviceText;
    private ImageView meDeviceImage;
    private ImageView meForwardFirst;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.me_page, container, false);

        userCommonService = new UserCommonServiceSpi(getContext());

        meLogin = (ImageView)view.findViewById(R.id.me_login);
        meLoginText = (TextView)view.findViewById(R.id.me_loginText);

        meDeviceText = (TextView)view.findViewById(R.id.me_devicetext);
        meDeviceImage = (ImageView)view.findViewById(R.id.me_deviceimage);
        meForwardFirst = (ImageView)view.findViewById(R.id.me_forwardfirst);

        meLogin.setOnClickListener(this);
        meLoginText.setOnClickListener(this);

        meDeviceText.setOnClickListener(this);
        meDeviceImage.setOnClickListener(this);
        meForwardFirst.setOnClickListener(this);

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
            meLoginText.setText(user.getUserName());
        }
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        switch (id)
        {
            case R.id.me_login:
            case R.id.me_loginText:
                if(meLoginText.getText().toString().equals("登录/注册"))
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
            case R.id.me_devicetext:
            case R.id.me_deviceimage:
            case R.id.me_forwardfirst:
                Intent intent = new Intent(this.getContext(), DeviceActivity.class);
                startActivity(intent);
                break;
        }
    }
}
