package mutong.com.mtaj.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import mutong.com.mtaj.R;
import mutong.com.mtaj.common.ErrorCode;
import mutong.com.mtaj.common.UserCommonServiceSpi;
import mutong.com.mtaj.common.listview.StickyListHeadersListView;
import mutong.com.mtaj.common.listview.TestBaseAdapter;
import mutong.com.mtaj.repository.User;
import mutong.com.mtaj.utils.HttpUtil;
import mutong.com.mtaj.utils.StatusBarUtil;
import mutong.com.mtaj.utils.StringUtil;

public class OpenDoorHistoryActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener
{
    private String deviceNum;
    private UserCommonServiceSpi userCommonService;

    private TextView historytext;
    private ImageView back;
    private TestBaseAdapter baseAdapter;
    private View footerview;
    //listview
    private StickyListHeadersListView stickyList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_door_history);

        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(this,R.color.title);
        //设置状态栏黑色文字
        StatusBarUtil.setBarTextLightMode(this);

        back = (ImageView)findViewById(R.id.back);
        historytext = (TextView)findViewById(R.id.history_text);

        back.setOnClickListener(this);
        historytext.setOnClickListener(this);
        setListView();
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
            case R.id.back:
            case R.id.history_text:
                finish();
                break;
        }
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            JSONObject jsonObject = (JSONObject)msg.obj;
            switch (msg.what)
            {
                case 1:
                    try
                    {
                        JSONObject result = jsonObject.getJSONObject("result");
                        String retCode = result.getString("retcode");
                        switch (retCode)
                        {
                            case ErrorCode.SUCEESS:
                                JSONArray historys = jsonObject.getJSONArray("openDoorHistories");
                                if(historys != null)
                                {
                                    for (int i = 0;i < historys.length();i++)
                                    {
                                        JSONObject history = historys.getJSONObject(i);
                                        String userName = history.getString("userName");
                                        String openTime = history.getString("openTime");
                                        System.out.println(userName + "," + openTime);
                                    }
                                }
                                break;
                            case ErrorCode.NOT_LOGIN:
                                Toast.makeText(OpenDoorHistoryActivity.this,"您还没有登录，请登录后再进行操作",Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
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
        //httpUtil.post(map,url);
    }

    //设置listview
    private void setListView()
    {
        baseAdapter = new TestBaseAdapter(this);
        footerview = getLayoutInflater().inflate(R.layout.list_footer, null);

        stickyList = (StickyListHeadersListView)findViewById(R.id.history_list_view);
        stickyList.setOnItemClickListener(this);
        stickyList.setAdapter(baseAdapter);
        stickyList.addFooterView(footerview);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if(id == -1)
        {
            baseAdapter.loadDatas(new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.opendoorhistory2))));
            baseAdapter.notifyDataSetChanged();
            stickyList.setEnd(true);
        }
    }
}
