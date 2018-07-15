package mutong.com.mtaj.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mutong.com.mtaj.R;
import mutong.com.mtaj.common.GridViewRowDivide;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.listener.GridViewItemClickListener;
import mutong.com.mtaj.listener.OpenDoorGrideViewListener;
import mutong.com.mtaj.repository.Device;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.StatusBarUtil;

public class OpenDoorActivity extends AppCompatActivity implements View.OnClickListener
{
    private GridView openDoorGridView;
    private ImageView backView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_door);

        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(this,R.color.title);
        //设置状态栏黑色文字
        StatusBarUtil.setBarTextLightMode(this);

        openDoorGridView = (GridView) findViewById(R.id.openDoorGridView);
        backView = (ImageView)findViewById(R.id.back);

        backView.setOnClickListener(this);

        setOpenDoorGridView();
    }

    private void setOpenDoorGridView()
    {
        String []from = {"icon","title"};
        int []to = {R.id.mainViewIcon,R.id.mainViewTitle};

        SimpleAdapter simpleAdapter = new SimpleAdapter(this,getData(), R.layout.door_gridview_item, from,to);

        openDoorGridView.setAdapter(simpleAdapter);
        openDoorGridView.setOnItemClickListener(new OpenDoorGrideViewListener(this));
    }

    private List<Map<String,Object>> getData()
    {
        List<Map<String,Object>> dataMap = new ArrayList<Map<String,Object>>();
        List<String> titlelist = new ArrayList<String>();

        UserCommonServiceSpi userCommonService = new UserCommonServiceSpi(this);
        Device[] devices = userCommonService.queryDevice();
        User user = userCommonService.getLoginUser();

        if (devices != null && user != null)
        {
            for(Device device : devices)
            {
                if(device.getPhoneNum().equals(user.getPhoneNum()))
                {
                    titlelist.add(device.getDeviceName());
                }
            }
        }

        String []title = titlelist.toArray(new String[titlelist.size()]);

        for (int i = 0;i < title.length;i++)
        {
            Map<String,Object> map = new HashMap<>();

            //map.put("icon",R.mipmap.device_mid);
            map.put("icon",R.mipmap.device_small);
            map.put("title",title[i]);
            dataMap.add(map);
        }
        return dataMap;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.back:
                finish();
                break;
        }
    }
}
