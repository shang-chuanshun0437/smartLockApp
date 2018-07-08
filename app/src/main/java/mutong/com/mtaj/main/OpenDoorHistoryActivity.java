package mutong.com.mtaj.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.view.View;
import android.widget.Toast;

import java.util.Map;

import mutong.com.mtaj.R;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.HttpUtil;
import mutong.com.mtaj.utils.StatusBarUtil;
import mutong.com.mtaj.utils.StringUtil;

public class OpenDoorHistoryActivity extends AppCompatActivity implements View.OnClickListener
{
    private String deviceNum;
    private UserCommonServiceSpi userCommonService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_door_history);

        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(this,R.color.title);
        //设置状态栏黑色文字
        StatusBarUtil.setBarTextLightMode(this);

        //获取设备编号
        deviceNum = getIntent().getStringExtra("deviceNum");

        if(StringUtil.isEmpty(deviceNum))
        {
            Toast.makeText(this,"设备编号不正确",Toast.LENGTH_LONG).show();
            finish();
        }

        userCommonService = new UserCommonServiceSpi(this);

        //去后台获取登录历史
        getHistory();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {

        }
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:

                    break;
            }
        }
    };

    private void getHistory()
    {
        User user = userCommonService.getLoginUser();

        HttpUtil httpUtil = new HttpUtil(handler,this);

        Map<String,String> map = new ArrayMap<String,String>();

        map.put("userName",user.getUserName());
        map.put("token",user.getUserToken());
        map.put("deviceNum",deviceNum);

        String url = "/query/openDoorHistory";
        httpUtil.post(map,url);
    }
}
