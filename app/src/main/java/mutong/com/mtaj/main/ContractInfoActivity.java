package mutong.com.mtaj.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mutong.com.mtaj.R;
import mutong.com.mtaj.adapter.DeviceInfoAdapter;
import mutong.com.mtaj.adapter.DeviceInfoItem;
import mutong.com.mtaj.adapter.DeviceInfoPicAdapter;
import mutong.com.mtaj.adapter.DeviceInfoPicItem;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.repository.Device;
import mutong.com.mtaj.utils.StatusBarUtil;

public class ContractInfoActivity extends AppCompatActivity implements View.OnClickListener
{
    private ListView deviceInfoList;

    private List<DeviceInfoItem> list = new ArrayList<DeviceInfoItem>();

    private ImageView back;
    private TextView backText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contract_info);

        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(this,R.color.title);
        //设置状态栏黑色文字
        StatusBarUtil.setBarTextLightMode(this);

        deviceInfoList = (ListView)findViewById(R.id.contract_list);

        back = (ImageView)findViewById(R.id.back);
        backText = (TextView)findViewById(R.id.textView15);

        back.setOnClickListener(this);
        backText.setOnClickListener(this);

        initItems();

    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();
        switch (id)
        {
            case R.id.back:
            case R.id.textView15:
                finish();
                break;
        }
    }

    private void initItems()
    {
        list.clear();

        DeviceInfoItem []contractInfoItems = new DeviceInfoItem[]{
                new DeviceInfoItem("签约日期","无"),
                new DeviceInfoItem("截止日期","无"),
                new DeviceInfoItem("房租(月)","0.00元"),
                new DeviceInfoItem("押金","0.00元"),
                new DeviceInfoItem("小区名称","无"),
                new DeviceInfoItem("房间号","无"),
                new DeviceInfoItem("电费(度)","0.00元/度"),
                new DeviceInfoItem("水费(吨)","0.00元/吨"),
                new DeviceInfoItem("燃气费(立方)","0.00元/立方"),};

        for (DeviceInfoItem contractInfoItem : contractInfoItems)
        {
            list.add(contractInfoItem);
        }

        DeviceInfoAdapter adapter = new DeviceInfoAdapter(this,R.layout.contractinfo_item,list);
        deviceInfoList.setAdapter(adapter);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

}
