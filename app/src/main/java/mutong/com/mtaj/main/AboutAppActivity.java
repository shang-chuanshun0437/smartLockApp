package mutong.com.mtaj.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import mutong.com.mtaj.R;
import mutong.com.mtaj.utils.StatusBarUtil;

public class AboutAppActivity extends AppCompatActivity implements View.OnClickListener
{
    private ImageView back;
    private TextView about;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutapp);

        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(this,R.color.title);
        //设置状态栏黑色文字
        StatusBarUtil.setBarTextLightMode(this);

        back = (ImageView)findViewById(R.id.about_back);
        about = (TextView)findViewById(R.id.about);

        back.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.about:
            case R.id.about_back:
                finish();
                break;
        }
    }

}
