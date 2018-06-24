package mutong.com.mtaj.animation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import mutong.com.mtaj.R;
import mutong.com.mtaj.main.MainActivity;

public class animation extends AppCompatActivity {

    //启动页面的时长
    private int count;

    //显示剩余时长的view，单位：s
    private TextView lodingTimeView;

    //启动动画
    private Animation animation;

    //消息处理
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == 0)
            {
                lodingTimeView.setText(getCount() + "");
                handler.sendEmptyMessageDelayed(0,1000);
                animation.reset();
                lodingTimeView.startAnimation(animation);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //去除标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_animation);

        lodingTimeView = (TextView) findViewById(R.id.textView);
        count = Integer.valueOf(lodingTimeView.getText().toString());

        animation = AnimationUtils.loadAnimation(this,R.anim.animation_context);
        handler.sendEmptyMessageDelayed(0,1000);
    }

    private int getCount()
    {
        count--;
        if (count == 0)
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return count;
    }
}
